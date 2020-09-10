package com.example.oslod;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class BrowserActivity extends AppCompatActivity {
    private ListView listView;
    private TextView txtView;
    ImageButton btnAddNewSample;
    Model model = Model.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        listView = (ListView) findViewById(R.id.customListView);
        txtView = findViewById(R.id.textView);

        final String catalogName = getIntent().getStringExtra("CATALOG_NAME");
        ArrayList<Sample> samples = model.loadSamplesFromCatalog(catalogName);

        txtView.setText(catalogName);

        final ListViewAdapter listAdapter = new ListViewAdapter(this, samples, false);
        listView.setAdapter(listAdapter);

        btnAddNewSample = (ImageButton) findViewById(R.id.btnPlaceOrder);
        btnAddNewSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addPhotoInten = new Intent(getBaseContext(), AddPhotoActivity.class);
                addPhotoInten.putExtra("CATALOG_NAME", catalogName);
                startActivity(addPhotoInten);
            }
        });
    }
}