package com.rubim.financenotificator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class StockAdapter extends ArrayAdapter<Stock> {

    private Context context;
    private ArrayList<Stock> list;

    public StockAdapter(Context context, ArrayList<Stock> list) {
        super(context, 0, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(this.context).inflate(R.layout.stock, null);
        ((TextView) convertView.findViewById(R.id.stockExchange)).setText(this.list.get(position).getStockExchange());
        ((TextView) convertView.findViewById(R.id.value)).setText("R$" + new DecimalFormat("0.00").format(this.list.get(position).getValue()).replace(".", ","));
        ((TextView) convertView.findViewById(R.id.last_trade)).setText(this.list.get(position).getLast_trade());
        ((TextView) convertView.findViewById(R.id.expectedValue)).setText("R$" + new DecimalFormat("0.00").format(this.list.get(position).getExpectedValue()).replace(".", ","));
        ((TextView) convertView.findViewById(R.id.objective)).setText(this.list.get(position).getObjectiveText());
        return convertView;
    }
}
