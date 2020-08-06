package com.rubim.financenotificator;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Requests {

    protected static String get(String target) {
        HttpURLConnection httpURLConnection = null;
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(target);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = reader.readLine()) != null) result.append(line);
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            httpURLConnection.disconnect();
        }
        return result.toString();
    }
}
