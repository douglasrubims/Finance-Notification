from flask import Flask, request, jsonify, abort
from datetime import datetime, timezone
from dotenv import load_dotenv
from wallstreet import Stock
import requests
import pymongo
import urllib
import pytz
import time
import os

load_dotenv()
app = Flask(__name__)
database = pymongo.MongoClient(os.getenv('DATABASE_URL') % (urllib.parse.quote(os.getenv('DB_USER')), urllib.parse.quote(os.getenv('DB_PASS'))))['FinanceApp']['data']

@app.route('/api/data', methods=['GET'])
def __index():
	out = []
	for item in sorted(database.find(), key=lambda quote: quote['objective'], reverse=True):
		item['_id'] = str(item['_id'])
		out.append(item)
	return jsonify(out)

@app.route('/api/data/<stockExchange>/<objective>', methods=['GET'])
def __show(stockExchange, objective):
	if not database.count_documents({ 'stockExchange': stockExchange, 'objective': objective }) > 0:
		abort(400)
	data = database.find_one({ 'stockExchange': stockExchange, 'objective': objective })
	data['_id'] = str(data['_id'])
	return data

@app.route('/api/data', methods=['POST'])
def __store():
	if not request.json or not 'objective' in request.json or not 'stockExchange' in request.json or not 'expectedValue' in request.json or database.count_documents({ 'stockExchange': request.json['stockExchange'], 'objective': request.json['objective'] }) > 0:
		abort(400)
	stock = Stock(request.json['stockExchange'])
	price = stock.price
	last_trade = pytz.timezone('UTC').localize(datetime.strptime(stock.last_trade, '%d %b %Y %H:%M:%S')).astimezone(pytz.timezone('Brazil/East')).strftime('%d/%m/%Y %H:%M:%S')
	database.insert_one({ 'objective': request.json['objective'], 'stockExchange': request.json['stockExchange'], 'expectedValue': request.json['expectedValue'], 'last_trade': last_trade, 'value': price, 'last_notification': last_trade })
	data = database.find_one({ 'stockExchange': request.json['stockExchange'] })
	data['_id'] = str(data['_id'])
	objective = { 'buy': 'comprar', 'sell': 'vender' }
	__sendNotification('Ativo adicionado.\nObjetivo: %s\nAtivo: %s\nValor esperado: R$%.2f' % (objective[data['objective']], data['stockExchange'], data['expectedValue']))
	return data

@app.route('/api/data/<stockExchange>/edit', methods=['PUT'])
def __update(stockExchange):
	if not request.json or not 'objective' in request.json or not 'expectedValue' in request.json or not database.count_documents({ 'stockExchange': stockExchange }) > 0:
		abort(400)
	database.update_one({ 'stockExchange': stockExchange, 'objective': request.json['objective'] }, { '$set': { 'expectedValue': request.json['expectedValue'] } })
	data = database.find_one({ 'stockExchange': stockExchange, 'objective': request.json['objective'] })
	data['_id'] = str(data['_id'])
	objective = { 'buy': 'comprar', 'sell': 'vender' }
	__sendNotification('Ativo atualizado.\nObjetivo: %s\nAtivo: %s\nValor esperado: R$%.2f' % (objective[data['objective']], data['stockExchange'], data['expectedValue']))
	return data

@app.route('/api/data/<stockExchange>/<objective>/delete', methods=['DELETE'])
def __destroy(stockExchange, objective):
	if not database.count_documents({ 'stockExchange': stockExchange, 'objective': objective }) > 0:
		abort(400)
	database.delete_one({ 'stockExchange': stockExchange, 'objective': objective })
	objectiveText = { 'buy': 'comprar', 'sell': 'vender' }
	__sendNotification('Ativo removido.\nObjetivo: %s\nAtivo: %s' % (objectiveText[objective], stockExchange))
	return jsonify({ 'result': 'success' })

@app.route('/api/notificate', methods=['GET'])
def __notificate():
	notificateText = ''
	out = []
	for data in database.find():
		stock = Stock(data['stockExchange'])
		price = stock.price
		last_trade = stock.last_trade
		objective = { 'buy': [ price <= data['expectedValue'], 'COMPRA' ], 'sell': [ price >= data['expectedValue'], 'VENDA'] }
		if data['last_trade'] != pytz.timezone('UTC').localize(datetime.strptime(last_trade, '%d %b %Y %H:%M:%S')).astimezone(pytz.timezone('Brazil/East')).strftime('%d/%m/%Y %H:%M:%S'):
			if objective[data['objective']][0] and (datetime.utcnow() - pytz.timezone('Brazil/East').localize(datetime.strptime(data['last_notification'], '%d/%m/%Y %H:%M:%S')).astimezone(pytz.timezone('UTC')).replace(tzinfo=None)).total_seconds() / 60 > 5:
				database.update_one({ '_id': data['_id'] }, { '$set': { 'last_notification': pytz.timezone('UTC').localize(datetime.utcnow()).astimezone(pytz.timezone('Brazil/East')).strftime('%d/%m/%Y %H:%M:%S') } })
				notificateText = notificateText + '[%s] %s: R$%.2f | %s\n' % (objective[data['objective']][1], data['stockExchange'], price, pytz.timezone('UTC').localize(datetime.strptime(last_trade, '%d %b %Y %H:%M:%S')).astimezone(pytz.timezone('Brazil/East')).strftime('%d/%m/%Y às %H:%M:%S'))
			database.update_one({ '_id': data['_id'] }, { '$set': { 'last_trade': pytz.timezone('UTC').localize(datetime.strptime(last_trade, '%d %b %Y %H:%M:%S')).astimezone(pytz.timezone('Brazil/East')).strftime('%d/%m/%Y %H:%M:%S'), 'value': price } })
		data = database.find_one({ '_id': data['_id'] })
		out.append({ '_id': str(data['_id']), 'objective': data['objective'], 'stockExchange': data['stockExchange'], 'expectedValue': data['expectedValue'], 'last_notification': data['last_notification'], 'value': price, 'last_trade': pytz.timezone('UTC').localize(datetime.strptime(last_trade, '%d %b %Y %H:%M:%S')).astimezone(pytz.timezone('Brazil/East')).strftime('%d/%m/%Y %H:%M:%S') })
	if notificateText != '':
		__sendNotification(notificateText)
	return jsonify(out)

def __sendNotification(message):
	requests.post('https://notify.run/5WniBV40gO4S7rU8', { 'message': 'Finance Notificator\n' + message })

if __name__ == '__main__':
	app.run(host='0.0.0.0', port=5000, debug=True)