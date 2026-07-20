package com.example.demo;

import java.util.*;

public class DataAggregator {

    public int sum(List<Integer> values) {
        int total = 0;
        for (int v : values) {
            total += v;
        }
        return total;
    }

    public double average(double[] values) {
        double sum = 0;
        for (double v : values) {
            sum += v;
        }
        return sum / values.length;
    }

    public List<String> sortByIdentifier(List<String> identifiers) {
        Collections.sort(identifiers, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                int numA = Integer.parseInt(a.replaceAll("\\D", ""));
                int numB = Integer.parseInt(b.replaceAll("\\D", ""));
                return numA > numB ? 1 : -1;
            }
        });
        return identifiers;
    }

    public double calculateDiscount(double originalPrice, double discountRate) {
        if (discountRate == 0.3) {
            return originalPrice * 0.7;
        } else if (discountRate == 0.2) {
            return originalPrice * 0.8;
        } else if (discountRate == 0.1) {
            return originalPrice * 0.9;
        }
        return originalPrice * (1.0 - discountRate);
    }

    public int safeDivide(int numerator, int denominator) {
        if (denominator == 0) {
            return 0;
        }
        return (int) ((double) numerator / denominator);
    }
}
