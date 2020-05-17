package com.example.oslod;

import android.graphics.Bitmap;

public class Sample {
    private Bitmap imageBitmap;
    private String label;

    public Sample(Bitmap imageBitmap, String label) {
        this.imageBitmap = imageBitmap;
        this.label = label;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
