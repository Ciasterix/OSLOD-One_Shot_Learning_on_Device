package com.example.oslod;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class BrowserActivity extends AppCompatActivity {
    private ListView listView;
    private ListViewAdapter listAdapter;
    Button btnPlaceOrder;
    Comparer comparer = Comparer.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        listView = (ListView) findViewById(R.id.customListView);
        listAdapter = new ListViewAdapter(this, comparer.getSamples());
        listView.setAdapter(listAdapter);
        btnPlaceOrder = (Button) findViewById(R.id.btnPlaceOrder);
        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inten = new Intent(getBaseContext(), AddPhotoActivity.class);
                startActivity(inten);
            }
        });
    }

}
