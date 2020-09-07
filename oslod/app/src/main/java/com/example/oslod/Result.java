package com.example.oslod;

public class Result {
    private float score;
    private Sample sample;

    public Result(float score, Sample sample) {
        this.score = score;
        this.sample = sample;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }
}
