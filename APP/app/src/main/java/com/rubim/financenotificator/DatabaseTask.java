package com.rubim.financenotificator;

import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DatabaseTask extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... strings) {
        JSONObject jsonObject = null;
        HttpURLConnection httpURLConnection = null;
        switch(strings[0]) {
            case "index":
                try {
                    JSONArray jsonArray = new JSONArray(Requests.get(strings[1] + "/data"));
                    MainActivity.arrayList = new ArrayList<Stock>();
                    if(jsonArray.length() == 0) {
                        MainActivity.arrayList = new ArrayList<String>();
                        MainActivity.arrayList.add("Insira uma bolsa para monitorar.");
                        MainActivity.listView.post(() -> MainActivity.listView.setAdapter(new ArrayAdapter<>(MainActivity.context, android.R.layout.simple_list_item_1, MainActivity.arrayList)));
                    } else for(int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        MainActivity.arrayList.add(new Stock(jsonObject.getString("stockExchange"), jsonObject.getString("objective"), jsonObject.getString("objective").equals("buy") ? "Comprar" : jsonObject.getString("objective").equals("sell") ? "Vender" : "Null", jsonObject.getString("last_trade"), jsonObject.getDouble("expectedValue"), jsonObject.getDouble("value")));
                    }
                } catch(JSONException ex) {
                    ex.printStackTrace();
                }
                MainActivity.stockAdapter = new StockAdapter(MainActivity.context , MainActivity.arrayList);
                MainActivity.listView.post(() -> MainActivity.listView.setAdapter(MainActivity.stockAdapter));
                break;
            case "create":
                jsonObject = new JSONObject();
                try {
                    jsonObject.put("stockExchange", strings[2]);
                    jsonObject.put("objective", strings[3]);
                    jsonObject.put("expectedValue", Double.parseDouble(strings[4]));
                    URL url = new URL(strings[1] + "/data");
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Content-type", "application/json");
                    httpURLConnection.setDoOutput(true);
                    PrintStream printStream = new PrintStream(httpURLConnection.getOutputStream());
                    printStream.println(jsonObject.toString());
                    httpURLConnection.connect();
                    httpURLConnection.getInputStream();
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    httpURLConnection.disconnect();
                }
                break;
            case "update":
                jsonObject = new JSONObject();
                try {
                    jsonObject.put("objective", strings[3]);
                    jsonObject.put("expectedValue", Double.parseDouble(EditStockExchange.expectedValueEditText.getText().toString().trim()));
                    URL url = new URL(strings[1] + "/data/" + strings[2] + "/edit");
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("PUT");
                    httpURLConnection.setRequestProperty("Content-type", "application/json");
                    httpURLConnection.setDoOutput(true);
                    PrintStream printStream = new PrintStream(httpURLConnection.getOutputStream());
                    printStream.println(jsonObject.toString());
                    httpURLConnection.connect();
                    httpURLConnection.getInputStream();
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    httpURLConnection.disconnect();
                }
                break;
            case "delete":
                try {
                    URL url = new URL(strings[1] + "/data/" + strings[2] + "/" + strings[3] + "/delete");
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("DELETE");
                    httpURLConnection.setRequestProperty("Content-type", "application/json");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.connect();
                    httpURLConnection.getInputStream();
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    httpURLConnection.disconnect();
                }
                break;
        }
        return null;
    }
}
