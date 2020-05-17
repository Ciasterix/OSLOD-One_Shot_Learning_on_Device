package com.example.oslod;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    public ArrayList<Sample> listSamples;
    private Context context;

    public ListViewAdapter(Context context, ArrayList<Sample> listSamples) {
        this.context = context;
        this.listSamples = listSamples;
    }

    public void setListSamples(ArrayList<Sample> listSamples) {
        this.listSamples = listSamples;
    }

    @Override
    public int getCount() {
        return listSamples.size();
    }

    @Override
    public Sample getItem(int position) {
        return listSamples.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row;
        final ListViewItem listItem;
        if(convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.activity_custom_listview, parent,false);
            listItem = new ListViewItem();
            listItem.textLabel = row.findViewById(R.id.txtLabel);
            listItem.imgSample = row.findViewById(R.id.imgSample);
            listItem.btnEditLabel = row.findViewById(R.id.btnEditLabel);
            listItem.btnRemoveSample = row.findViewById(R.id.btnRemove);
            row.setTag(listItem);
        }
        else
        {
            row=convertView;
            listItem= (ListViewItem) row.getTag();
        }
        final Sample sample = getItem(position);

        listItem.textLabel.setText(sample.getLabel());
        listItem.imgSample.setImageBitmap(sample.getImageBitmap());

        listItem.btnEditLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Zmiana etykiety");
                alert.setMessage("Podaj nową etykietę klasy");
                final EditText input = new EditText(context);
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newLabel = input.getText().toString();
                        Comparer.getInstance().changeLabel(listItem.textLabel.getText().toString(), newLabel);
                        listItem.textLabel.setText(newLabel);
                        Toast toast = Toast.makeText(context, "etykieta zmieniona", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast toast = Toast.makeText(context, "edycja anulowana", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });

                alert.show();
            }
        });
        listItem.btnRemoveSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comparer.getInstance().deleteSample(sample);
                Toast toast = Toast.makeText(context, "klasa usunięta", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        return row;
    }
}