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
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class Comparer {
    private static Comparer instance = new Comparer();
    private ArrayList<Sample> samples = new ArrayList<>();
    private String dirPath;
    private ArrayList<Result> fullResults;
    private Module module;
    private static HashMap<String, String> neuralNetFiles = new HashMap<>();
    private String currentNeuralNetName;

    static {
        neuralNetFiles.put("przedmioty", "model.pt");
        neuralNetFiles.put("twarze", "face_model.pt");
        neuralNetFiles.put("rośliny", "flowers");
    }

    public Comparer() {
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
        if(currentNeuralNetName == "twarze") {
            bitmap = Bitmap.createScaledBitmap(bitmap, 112, 112, false);
        }
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

    public void loadNeuralNet(Context context, String netName) {
        currentNeuralNetName = netName;
        String netFile = neuralNetFiles.get(netName);
        try {
            module = Module.load(assetFilePath(context, netFile));
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

    public void storeFullResults(float[] scores) {
        fullResults = new ArrayList<Result>();
        TreeMap<Float, Sample> resultsMap = new TreeMap();
        for (int i = 0; i < scores.length; i++) {
            resultsMap.put(scores[i], samples.get(i));
        }
        for(Map.Entry<Float, Sample> entry : resultsMap.entrySet()) {
            Float score = entry.getKey();
            Sample sample = entry.getValue();
            fullResults.add(0, new Result(score, sample));
        }
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

    public ArrayList<Result> getFullResults() {
        return fullResults;
    }
}
