package com.example.oslod;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button btnCapture;
    private Button btnBrowse;
    private ImageView imgCapture;
    private static final int Image_Capture_Code = 1;
    private String mainAppDir;
    private TextView txtPredLabel;
    Comparer comparer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        mainAppDir = cw.getDir("oslodData", Context.MODE_PRIVATE).getAbsolutePath();
        btnCapture = findViewById(R.id.btnTakePicture);
        btnBrowse = findViewById(R.id.btnBrowseImages);
        imgCapture = findViewById(R.id.newImage);
        txtPredLabel = findViewById(R.id.textPredictedLabel);

        comparer = Comparer.getInstance();
        comparer.setDirPath(mainAppDir);
        comparer.loadSamples();
        comparer.loadModel(this, "model.pt");

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
                Intent inten = new Intent(getBaseContext(), BrowserActivity.class);
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
                float[] similarities = comparer.predictScores(bp);
                String classLabel = comparer.getMostSimilarClass(similarities);
                txtPredLabel.setText(classLabel);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Anulowano", Toast.LENGTH_LONG).show();
            }
        }
    }
}