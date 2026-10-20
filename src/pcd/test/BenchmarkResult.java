package pcd.test;

import java.util.List;

record BenchmarkResult(String name, int workers, List<BenchmarkSample> samples) {
    BenchmarkResult {
        samples = List.copyOf(samples);
    }

    double meanThroughput() {
        return samples.stream().mapToDouble(BenchmarkSample::throughput).average().orElse(0);
    }

    double meanResponseMillis() {
        return samples.stream().mapToDouble(BenchmarkSample::responseMillis).average().orElse(0);
    }
}
