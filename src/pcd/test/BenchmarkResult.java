package pcd.test;

import java.util.Arrays;

/** Measurements collected for one implementation and worker count. */
record BenchmarkResult(String name, int workers, double[] samples) {
    double mean() {
        return Arrays.stream(samples).average().orElse(0);
    }
}
