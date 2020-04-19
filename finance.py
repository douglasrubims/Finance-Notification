from alpha_vantage.timeseries import TimeSeries
import MySQLdb as SQL
import time

def getDatabase():
	db = SQL.connect(host='127.0.0.1', db='FinanceApp', port=3306, user='root', passwd='')
	cursor = db.cursor()
	cursor.execute('SELECT apiKey, stockExchange, expectedPrice FROM FinanceAppConfig')
	return cursor.fetchone()

apiKey, stockExchange, expectedPrice = getDatabase()

header = True
notification = False

print('\t\t' + stockExchange + '\n')
while True:
	data, _ = TimeSeries(key=apiKey, output_format='pandas').get_intraday(symbol=stockExchange, interval='1min', outputsize='full')
	data.index.name = None
	print(data['4. close'].head(1).to_string(header=header))
	if data['4. close'][0] <= expectedPrice:
		if not notification:
			notification = True
			print('[INFO] The quotation reached the desired value: ' + str(expectedPrice))
	else:
		notification = False
	if header:
		header = False
	time.sleep(30)