package com.rubim.financenotificator;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;

public class CreateStockExchange extends AppCompatActivity {

    protected static EditText stockExchange, expectedValue;
    protected static RadioGroup radioGroup;
    protected static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        context = this.getApplicationContext();
        stockExchange = findViewById(R.id.stockExchange);
        expectedValue = findViewById(R.id.expectedValue);
        radioGroup = findViewById(R.id.radioGroup);
    }

    public void save(View v) {
        String objective = "";
        if(radioGroup.getCheckedRadioButtonId() == R.id.comprar) objective = "buy";
        else if(radioGroup.getCheckedRadioButtonId() == R.id.vender) objective = "sell";
        new DatabaseTask().execute("create", MainActivity.url, stockExchange.getText().toString().trim(), objective, expectedValue.getText().toString().trim());
        MainActivity.update(null);
        finish();
    }

    public void back(View v) { finish(); }
}
