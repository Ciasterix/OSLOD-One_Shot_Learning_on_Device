package com.example.oslod;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class Comparer {
    private static Comparer instance = new Comparer();
    private ArrayList<Sample> samples = new ArrayList<>();
    private String dirPath;
    private Module module;

    public Comparer() {
    }

    public void loadSamples() {
        try {
            File appDir = new File(dirPath);
            File[] files = appDir.listFiles();
            for(File f: files) {
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                String label = f.getName().substring(0, f.getName().length()-4);
                samples.add(new Sample(b, label));
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public void addNewSample(Sample s) {
        saveSampleToInternalMemory(s);
        samples.add(s);
    }

    private void saveSampleToInternalMemory(Sample s) {
        File sampleFile = new File(dirPath, s.getLabel()+".png");
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

    public void deleteSample(Sample s) {
        samples.remove(s);
        deleteSampleFromInternalMemory(s);
    }

    private void deleteSampleFromInternalMemory(Sample s) {
        File sampleFile = new File(dirPath, s.getLabel()+".png");
        try {
            sampleFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeLabel(String oldLabel, String newLabel) {
        for(Sample s: samples) {
            if (s.getLabel().equals(oldLabel)) s.setLabel(newLabel);
        }
        renameSampleInInternalMemory(oldLabel, newLabel);
    }

    public boolean renameSampleInInternalMemory(String oldLabel, String newLabel) {
        File oldSampleFile = new File(dirPath, oldLabel + ".png");
        File newSampleFile = new File(dirPath, newLabel + ".png");
        if (newSampleFile.exists())
            return false;
        else {
            boolean success = oldSampleFile.renameTo(newSampleFile);
            return success;
        }
    }

    public float[] predictScores(Bitmap unknownImage) {
        float[] unkownSampleScores = runPrediction(unknownImage);
        float[] similarities = new float[samples.size()];
        for(int i = 0; i < samples.size(); i++) {
            float[] sampleScores = runPrediction(samples.get(i).getImageBitmap());
            similarities[i] = (float)cosineSimilarity(unkownSampleScores, sampleScores);
        }
        return similarities;
    }

    public float[] runPrediction(Bitmap bitmap) {
        final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(bitmap,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB);
        final Tensor outputTensor = module.forward(IValue.from(inputTensor)).toTensor();
        final float[] scores = outputTensor.getDataAsFloatArray();
        return scores;
    }

    public double cosineSimilarity(float[] A, float[] B) {
        if (A == null || B == null || A.length == 0 || A.length != B.length) {
            return 2.0;
        }

        double sumProduct = 0;
        double sumASq = 0;
        double sumBSq = 0;
        for (int i = 0; i < A.length; i++) {
            sumProduct += A[i]*B[i];
            sumASq += A[i] * A[i];
            sumBSq += B[i] * B[i];
        }
        if (sumASq == 0 && sumBSq == 0) {
            return 2.0;
        }
        return sumProduct / (Math.sqrt(sumASq) * Math.sqrt(sumBSq));
    }

    public void loadNeuralNet(Context context, String filename) {
        try {
            module = Module.load(assetFilePath(context, filename));
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public String getMostSimilarClass(float[] scores) {
        float maxSim = -Float.MAX_VALUE;
        String maxSimLabel = "";
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] > maxSim) {
                maxSim = scores[i];
                maxSimLabel = samples.get(i).getLabel();
            }
        }
        return maxSimLabel;
    }

    public static String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }
        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }

    public static Comparer getInstance() {
        return instance;
    }

    public void setSamples(ArrayList<Sample> samples) {
        this.samples = samples;
    }

    public ArrayList<Sample> getSamples() {
        return samples;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public String getDirPath() {
        return dirPath;
    }
}
