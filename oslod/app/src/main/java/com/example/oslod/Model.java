package com.example.oslod;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Model {
    private static Model instance = new Model();
//    private final ArrayList<File> allCatalogs = new ArrayList<>();
//    private File[] allCatalogs;
    private ArrayList<Sample> currentSamples = new ArrayList<>();
    private ArrayList<Sample> catalogs = new ArrayList<>();
    //    private File currentCatalog;
    private String currentCatalog = null;
    private String appDirPath;

    public Model() {
    }

    public void initCurrentData() {
        if(catalogs.size() > 0){
            currentCatalog = catalogs.get(0).getLabel();
            loadCurrentSamples();
        }
    }

    public void loadExistingCatalogs() {
        File appDirFiles = new File(appDirPath);
//        allCatalogs = appDirFiles.listFiles();
        File[] catalogsFiles = appDirFiles.listFiles();
        for(File f: catalogsFiles) {
//                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
//            Bitmap b = null;
            String label = f.getName();
            catalogs.add(new Sample(null, label));
        }
    }

    public void loadCurrentSamples() {
        currentSamples = loadSamplesFromCatalog(currentCatalog);
    }

    public ArrayList<Sample> loadSamplesFromCatalog(String catalogName) {
        ArrayList<Sample> loadedSamples = new ArrayList<>();
        try {
//            File currentDirFile = new File(this.currentCatalog);
            File[] files = new File(appDirPath, catalogName).listFiles();
            for(File f: files) {
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                String label = f.getName().substring(0, f.getName().length()-4);
                loadedSamples.add(new Sample(b, label));
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return loadedSamples;
    }

    public boolean createCatalog(String name) {
        File newDirectory = new File(appDirPath, name);
        if (!newDirectory.exists()) {
            boolean res = newDirectory.mkdirs();
            if(currentCatalog == null && res) {
                currentCatalog = newDirectory.getName();
            }
            return res;
        }
        else {
            return false;
        }
    }

//    public void addNewSample(Sample s, ) {
//        saveSampleToInternalMemory(s);
//        samples.add(s);
//    }

    public void saveSampleInMemory(Sample s, String catalogName) {
        File catalogFile = new File(appDirPath, catalogName);
        File sampleFile = new File(catalogFile, s.getLabel()+".png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(sampleFile);
            s.getImageBitmap().compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Model getInstance(){
        return instance;
    }

    public ArrayList<Sample> getSamples() {
        return currentSamples;
    }

    public ArrayList<Sample> getCatalogs() {
        return catalogs;
    }

    public String[] getCatalogsNames() {
        ArrayList<String> names = new ArrayList<>();
        for (Sample c: catalogs) {
            names.add(c.getLabel());
        }
        return names.toArray(new String[0]);
    }

    public void setDirPath(String dirPath) {
        this.appDirPath = dirPath;
    }

    public String getDirPath() {
        return appDirPath;
    }

//    public void addNewSample(Sample s) {
//        saveSampleToInternalMemory(s);
//        samples.add(s);
//    }

}
