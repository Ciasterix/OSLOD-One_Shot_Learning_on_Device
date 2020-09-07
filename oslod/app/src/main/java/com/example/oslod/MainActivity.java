package com.example.oslod;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Button btnCapture;
    private Button btnBrowse;
    private Button btnFullResults;
    private ImageView imgCapture;
    private static final int Image_Capture_Code = 1;
    private String mainAppDir;
    private TextView txtPredLabel;
    Spinner dropdown;
    Comparer comparer;
    Model model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        mainAppDir = cw.getDir("oslodDataCatalogs", Context.MODE_PRIVATE).getAbsolutePath();
        Toast.makeText(this, mainAppDir, Toast.LENGTH_LONG).show();
        btnCapture = findViewById(R.id.btnTakePicture);
        btnBrowse = findViewById(R.id.btnBrowseImages);
        imgCapture = findViewById(R.id.newImage);
        btnFullResults = findViewById(R.id.btnFullResults);
        txtPredLabel = findViewById(R.id.textPredictedLabel);

        btnFullResults.setEnabled(false);

        model = Model.getInstance();
        model.setDirPath(mainAppDir);
        model.loadExistingCatalogs();
        model.initCurrentData();

        comparer = Comparer.getInstance();
        comparer.setDirPath(mainAppDir);

        dropdown = findViewById(R.id.spinnerCatalogs);
        ArrayAdapter<String> adapterSpinnersCatalogs = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, model.getCatalogsNames());
        dropdown.setAdapter(adapterSpinnersCatalogs);

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cInt,Image_Capture_Code);
            }
        });

        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inten = new Intent(getBaseContext(), CatalogsActivity.class);
                startActivity(inten);
            }
        });

        btnFullResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inten = new Intent(getBaseContext(), FullResultsActivity.class);
                startActivity(inten);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Capture_Code) {
            if (resultCode == RESULT_OK) {
                Bitmap bp = (Bitmap) data.getExtras().get("data");
                imgCapture.setImageBitmap(bp);
                comparer.loadNeuralNet(this, "model.pt");

                ArrayList<Sample> samples = model.loadSamplesFromCatalog(dropdown.getSelectedItem().toString());
                model.setCurrentSamples(samples);
                comparer.setSamples(samples);

                float[] similarities = comparer.predictScores(bp);
                String classLabel = comparer.getMostSimilarClass(similarities);
                comparer.storeFullResults(similarities);
                btnFullResults.setEnabled(true);

                txtPredLabel.setText(classLabel);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Anulowano", Toast.LENGTH_LONG).show();
            }
        }
    }
}