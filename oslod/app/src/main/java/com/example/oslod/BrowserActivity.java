package com.example.oslod;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class BrowserActivity extends AppCompatActivity {
    private ListView listView;
    private ListViewAdapter listAdapter;
    Button btnAddNewSample;
    Comparer comparer = Comparer.getInstance();
    Model model = Model.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        listView = (ListView) findViewById(R.id.customListView);

        final String catalogName = getIntent().getStringExtra("CATALOG_NAME");
        ArrayList<Sample> samples = model.loadSamplesFromCatalog(catalogName);

        final ListViewAdapter listAdapter = new ListViewAdapter(this, samples);
        listView.setAdapter(listAdapter);

        btnAddNewSample = (Button) findViewById(R.id.btnPlaceOrder);
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

//public class Score extends AppCompatActivity {
//
//
//    ListView listView;
//
//
//    @Override
//    protected void onCreate(final Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_score);
//        listView = (ListView) findViewById(lstRanking);
//
//
//        final DbHelper db = new DbHelper(this);
//        final List<Ranking> listRanking = db.getRanking();
//        if (listRanking.size() > 0) {
//            final CustomAdapter adapter = new CustomAdapter(this, listRanking);
//            listView.setAdapter(adapter);
//
//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    listRanking.remove(position);
//
//                    adapter.notifyDataSetChanged();
//
//                }
//            });
