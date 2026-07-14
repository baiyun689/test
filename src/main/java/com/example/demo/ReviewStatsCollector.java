package com.example.demo;

import java.util.ArrayList;
import java.util.List;

public class ReviewStatsCollector {

    private static List<Double> scores = new ArrayList<>();

    public void record(double score) {
        scores.add(score);
    }

    public double average() {
        double sum = 0;
        for (double s : scores) {
            sum += s;
        }
        return sum / scores.size();
    }
}
