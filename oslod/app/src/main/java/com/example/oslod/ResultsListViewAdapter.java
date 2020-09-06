package com.example.oslod;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.text.DecimalFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class ResultsListViewAdapter extends BaseAdapter {

    public ArrayList<Result> listResults;
    private Context context;

    public ResultsListViewAdapter(Context context, ArrayList<Result> listResults) {
        this.context = context;
        this.listResults = listResults;
    }

    public void setListSamples(ArrayList<Result> listSamples) {
        this.listResults = listSamples;
    }

    @Override
    public int getCount() {
        return listResults.size();
    }

    @Override
    public Result getItem(int position) {
        return listResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row;
        final ResultListViewItem resultsItem;
        if(convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.activity_result_list_view_item, parent,false);
            resultsItem = new ResultListViewItem();
            resultsItem.textLabel = row.findViewById(R.id.txtLabel);
            resultsItem.imgSample = row.findViewById(R.id.imgSample);
            row.setTag(resultsItem);
        }
        else
        {
            row = convertView;
            resultsItem = (ResultListViewItem) row.getTag();
        }
        final Result result = getItem(position);

        DecimalFormat df = new DecimalFormat("0.00");

        String textWithScore = df.format(result.getScore()) + " : " + result.getSample().getLabel();
        resultsItem.textLabel.setText(textWithScore);
        resultsItem.imgSample.setImageBitmap(result.getSample().getImageBitmap());

        this.notifyDataSetChanged();

        return row;
    }
}