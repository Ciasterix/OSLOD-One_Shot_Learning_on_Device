package com.example.oslod;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class AddPhotoActivity extends AppCompatActivity {
    ImageButton btnCapture;
    ImageButton btnSave;
    EditText txtLabel;
    private ImageView imgView;
    private Bitmap newImageBitmap;
    private static final int Image_Capture_Code = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        btnCapture = findViewById(R.id.btnTakePicture);;
        btnSave = findViewById(R.id.btnSaveSample);;
        txtLabel = findViewById(R.id.addLabel);
        imgView = findViewById(R.id.newImage);

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cInt,Image_Capture_Code);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOrDiscardRusult();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Capture_Code) {
            if (resultCode == RESULT_OK) {
                newImageBitmap = (Bitmap) data.getExtras().get("data");
                imgView.setImageBitmap(newImageBitmap);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Anulowano", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveOrDiscardRusult() {
        String label = txtLabel.getText().toString();
        if(label.equals("")) {
            Toast.makeText(this, "Błędna etykieta", Toast.LENGTH_LONG).show();
        }
        else if(imgView.getDrawable() == null) {
            Toast.makeText(this, "Zrób zdjęcie", Toast.LENGTH_LONG).show();
        }
        else {
//            Comparer.getInstance().addNewSample(new Sample(newImageBitmap, label));
            final String catalogName = getIntent().getStringExtra("CATALOG_NAME");
            Model.getInstance().saveSampleInMemory(new Sample(newImageBitmap, label), catalogName);
            Toast.makeText(this, "Dodano klasę", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
