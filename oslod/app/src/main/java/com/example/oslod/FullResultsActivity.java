package com.example.oslod;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;

public class FullResultsActivity extends AppCompatActivity {
    private ListView listView;
    private ResultsListViewAdapter resultsAdapter;
    Comparer comparer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_results);
        listView = (ListView) findViewById(R.id.customResultsListView);
        final ResultsListViewAdapter resultsAdapter = new ResultsListViewAdapter(
                this, Comparer.getInstance().getFullResults());
        listView.setAdapter(resultsAdapter);
        final Context context = this;
    }
}
