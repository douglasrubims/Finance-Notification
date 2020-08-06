package com.rubim.financenotificator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class EditStockExchange extends AppCompatActivity {

    protected static EditText expectedValueEditText;
    protected static String stock, objective, objectiveText;
    protected static double expectedValue;
    protected static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        context = this.getApplicationContext();
        expectedValueEditText = findViewById(R.id.expectedValue);
        Intent intent = getIntent();
        stock = intent.getStringExtra("stockExchange");
        objective = intent.getStringExtra("objective");
        objectiveText = intent.getStringExtra("objectiveText");
        expectedValue = intent.getDoubleExtra("expectedValue", 0);
        ((TextView) findViewById(R.id.stockExchange)).setText(stock);
        ((TextView) findViewById(R.id.objective)).setText(objectiveText);
        expectedValueEditText.setText(Double.toString(expectedValue));
    }

    public void save(View v) {
        new DatabaseTask().execute("update", MainActivity.url, stock, objective);
        MainActivity.update(null);
        finish();
    }

    public void delete(View v) {
        new DatabaseTask().execute("delete", MainActivity.url, stock, objective);
        MainActivity.update(null);
        finish();
    }

    public void back(View v) { finish(); }
}
