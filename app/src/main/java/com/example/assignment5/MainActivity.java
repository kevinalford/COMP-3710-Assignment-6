package com.example.assignment5;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    SQL myDb;
    TextView balance;
    EditText editDate;
    EditText editPrice;
    EditText editItem;
    EditText dateStart;
    EditText dateEnd;
    EditText priceHigh;
    EditText priceLow;
    Button btnAdd;
    Button btnSub;
    Button btnSer;
    TableLayout history;
    DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new SQL(this);

        balance = (TextView) findViewById(R.id.balance);
        editDate = (EditText) findViewById(R.id.date);
        editPrice = (EditText) findViewById(R.id.price);
        editItem = (EditText) findViewById(R.id.item);
        dateStart = (EditText) findViewById(R.id.dateStart);
        dateEnd = (EditText) findViewById(R.id.dateEnd);
        priceHigh = (EditText) findViewById(R.id.priceHigh);
        priceLow = (EditText) findViewById(R.id.priceLow);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnSub = (Button) findViewById(R.id.btnSub);
        btnSer = (Button) findViewById(R.id.btnSer);
        history = (TableLayout) findViewById(R.id.tableHistory);

        AddTransaction();
        GetHistory();
    }

    public void AddTransaction(){
        btnAdd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        double price = Double.parseDouble(editPrice.getText().toString());

                        model model = new model();
                        model.mDate = editDate.getText().toString();
                        model.mItem = editItem.getText().toString();
                        model.mPrice = price;
                        boolean result = myDb.createTransaction(model);
                        if (result)
                            Toast.makeText(MainActivity.this, "Transaction Created", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainActivity.this, "Transaction Not Created", Toast.LENGTH_LONG).show();
                        GetHistory();
                        ClearText();
                    }
                }
        );

        btnSub.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        double price = -1 * Double.parseDouble(editPrice.getText().toString());
                        model model = new model();
                        model.mDate = editDate.getText().toString();
                        model.mItem = editItem.getText().toString();
                        model.mPrice = price;
                        boolean result = myDb.createTransaction(model);
                        if (result)
                            Toast.makeText(MainActivity.this, "Transaction Success", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainActivity.this, "Transaction Fail", Toast.LENGTH_LONG).show();
                        GetHistory();
                        ClearText();
                    }
                }
        );

        btnSer.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClearTable();
                        GetRequiredHistory();
                        Toast.makeText(MainActivity.this, "Filter success", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    public void GetHistory(){
        ClearTable();
        Cursor result = myDb.getAllData();
        StringBuffer buffer = new StringBuffer();
        Double balance = 0.0;
        while(result.moveToNext()){
            TableRow tr = new TableRow(this);
            TableRow.LayoutParams columnLayout = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            columnLayout.weight = 1;

            TextView date = new TextView(this);
            date.setLayoutParams(columnLayout);
            date.setText(result.getString(2));
            tr.addView(date);

            TextView priceView = new TextView(this);
            priceView.setLayoutParams(columnLayout);
            priceView.setText(result.getString(3));
            tr.addView(priceView);

            TextView item = new TextView(this);
            item.setLayoutParams(columnLayout);
            item.setText(result.getString(1));
            tr.addView(item);

            history.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

            // get price for balance
            double price = Double.parseDouble(result.getString(3));
            balance += price;
        }
        MainActivity.this.balance.setText("Current Balance: $" + df.format(balance));
    }

    public void GetRequiredHistory(){
        ClearTable();
        Cursor result = myDb.getAllData();
        StringBuffer buffer = new StringBuffer();
        Double balance = 0.0;

        while(result.moveToNext()) {
            int start, end, actual;
            String monthStart = "00";
            String monthEnd = "99";
            String actualMonthStart;

            if(dateStart.getText().toString().equals("")){
                start = 0;
            }else{
                String dateStartString = dateStart.getText().toString();
                monthStart = dateStartString.substring(5, 7);
                start = Integer.parseInt(monthStart);
            }

            if(dateEnd.getText().toString().equals("")){
                end = 13;
            }else{
                String dateEndString = dateEnd.getText().toString();
                monthEnd = dateEndString.substring(5, 7);
                end = Integer.parseInt(monthEnd);
            }

            String actualDateString = result.getString(2);
            actualMonthStart = actualDateString.substring(5, 7);
            actual = Integer.parseInt(actualMonthStart);

//            System.out.println("Month");
//            System.out.println(start);
//            System.out.println(end);
//            System.out.println(actual);
//            System.out.println("String");
//            System.out.println(monthStart);
//            System.out.println(monthEnd);
//            System.out.println(actualMonthStart);


            int lowPrice, highPrice, actualPrice;
            if(priceLow.getText().toString().equals("")){
                lowPrice = 0;
            } else{
                String priceLowString = priceLow.getText().toString();
                lowPrice = Integer.parseInt(priceLowString);
            }

            if(priceHigh.getText().toString().equals("")){
                highPrice = 99999999;
            }else{
                String priceHighString = priceHigh.getText().toString();
                highPrice = Integer.parseInt(priceHighString);
            }

            String actualPriceString = result.getString(3);
            actualPrice = Integer.parseInt(actualPriceString);

            if (actual > end || actual < start) {
//                result.moveToNext();
            }
            else if (actualPrice > highPrice || actualPrice < lowPrice) {
//                result.moveToNext();
            }
            else {
                TableRow tr = new TableRow(this);
                TableRow.LayoutParams columnLayout = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                columnLayout.weight = 1;

                TextView date = new TextView(this);
                date.setLayoutParams(columnLayout);
                date.setText(result.getString(2));
                tr.addView(date);

                TextView priceView = new TextView(this);
                priceView.setLayoutParams(columnLayout);
                priceView.setText(result.getString(3));
                tr.addView(priceView);

                TextView item = new TextView(this);
                item.setLayoutParams(columnLayout);
                item.setText(result.getString(1));
                tr.addView(item);

                history.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));


                double price = Double.parseDouble(result.getString(3));
                balance += price;
            }

        }
    }

    public void ClearText(){
        MainActivity.this.editDate.setText("");
        MainActivity.this.editPrice.setText("");
        MainActivity.this.editItem.setText("");
    }

    public void ClearTable(){
        int count = history.getChildCount();
        for (int i = 1; i < count; i++) {
            history.removeViewAt(1);
        }
    }
}