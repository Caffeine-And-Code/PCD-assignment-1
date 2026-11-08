package pcd.test;

record BenchmarkResult(String name, int workers, double elapsedNanos) {
    double millisecondsPerStep(int steps) {
        return elapsedNanos / steps / 1_000_000;
    }

    double throughput(int steps) {
        return steps * 1_000_000_000.0 / elapsedNanos;
    }

    double speedup(BenchmarkResult sequential) {
        return sequential.elapsedNanos / elapsedNanos;
    }

    double efficiency(BenchmarkResult sequential) {
        return speedup(sequential) / workers;
    }
}
