package pcd.test;

import java.util.Arrays;

record BenchmarkResult(String name, int workers, double[] samples) {
    double mean() {
        return Arrays.stream(samples).average().orElse(0);
    }
}
