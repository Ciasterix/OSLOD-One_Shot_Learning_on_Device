package com.example.oslod;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CatalogsActivity extends AppCompatActivity {
    private ListView listView;
    Button btnAddNewCatalog;
    Model model = Model.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogs);
        listView = (ListView) findViewById(R.id.customCatalogsListView);
        final ListViewAdapter listAdapter = new ListViewAdapter(
                this, model.getCatalogs(), true);
        listView.setAdapter(listAdapter);
        final Context context = this;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                String catalogName = model.getCatalogs().get(position).getLabel();
                Toast.makeText(getBaseContext(), catalogName, Toast.LENGTH_SHORT).show();
                Intent browserInten = new Intent(getBaseContext(), BrowserActivity.class);
                browserInten.putExtra("CATALOG_NAME", catalogName);
                model.setCurrentCatalog(catalogName);
                startActivity(browserInten);
            }
        });

        btnAddNewCatalog = (Button) findViewById(R.id.btnAddNewCatalog);
        btnAddNewCatalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Nowa kolekcja");
                alert.setMessage("Podaj nazwę kolekcji");
                final EditText input = new EditText(context);
                input.setHint("nazwa");
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newCatalogName = input.getText().toString();
                        if (newCatalogName.equals("")) {
                            Toast toast = Toast.makeText(context, "Błąd: Podana etykieta jest pusta", Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            boolean success = model.createCatalog(newCatalogName);
                            String toastText;
                            if (success) {
                                toastText = "Utworzono nową kolekcję";
                            }
                            else {
                                toastText = "Nie udało się utworzyć kolekcji";
                            }
                            Toast toast = Toast.makeText(context, toastText, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast toast = Toast.makeText(context, "Dodawanie kolekcji anulowane", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
                alert.show();
            }
        });
    }
}
