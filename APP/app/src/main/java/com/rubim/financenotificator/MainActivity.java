package com.rubim.financenotificator;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    protected static ListView listView;
    protected static ArrayList arrayList;
    protected static StockAdapter stockAdapter;
    protected static Context context;
    protected static String url = "https://financenotificator.rj.r.appspot.com/api";
    // Android emulator: protected static String url = "http://10.0.2.2:5000/api";
    // Google Cloud App Engine: protected static String url = "https://financenotificator.rj.r.appspot.com/api";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this.getApplicationContext();
        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if(arrayList.get(position) instanceof Stock) {
                Intent intent = new Intent(context, EditStockExchange.class);
                intent.putExtra("stockExchange", ((Stock) arrayList.get(position)).getStockExchange());
                intent.putExtra("objective", ((Stock) arrayList.get(position)).getObjective());
                intent.putExtra("objectiveText", ((Stock) arrayList.get(position)).getObjectiveText());
                intent.putExtra("expectedValue", ((Stock) arrayList.get(position)).getExpectedValue());
                startActivity(intent);
            }
        });
        arrayList = new ArrayList<String>();
        arrayList.add("Carregando...");
        listView.setAdapter(new ArrayAdapter(context, android.R.layout.simple_list_item_1, arrayList));
        new DatabaseTask().execute("index", url);
    }

    public void add(View v) { startActivity(new Intent(context, CreateStockExchange.class)); }

    public static void update(View v) {
        Toast.makeText(context, "Atualizando...", Toast.LENGTH_SHORT).show();
        new DatabaseTask().execute("index", url);
    }
}
