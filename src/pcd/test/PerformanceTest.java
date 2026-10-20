package pcd.test;

import java.util.*;
import java.util.concurrent.Callable;

public final class PerformanceTest {
    private PerformanceTest() {
    }

    static void main(String[] args) throws Exception {
        BenchmarkSettings settings;
        try {
            settings = BenchmarkSettings.from(args);
        } catch (IllegalArgumentException ex) {
            System.err.println(ex.getMessage());
            return;
        }
        var benchmark = new PhysicsBenchmark(settings.warmupMillis(), settings.measurementMillis());
        var results = new ArrayList<BenchmarkResult>();

        printConfiguration(settings);
        var sequential = measure("Sequential", 1, settings.repetitions(), benchmark::sequential);
        results.add(sequential);

        for (int workers : workerCounts(settings.maxWorkers())) {
            results.add(measure("Native threads", workers, settings.repetitions(),
                    () -> benchmark.nativeThreads(workers)));
        }
        for (int workers : workerCounts(settings.maxWorkers())) {
            results.add(measure("Executor framework", workers, settings.repetitions(),
                    () -> benchmark.executorFramework(workers)));
        }

        printResults(results, sequential);
    }

    private static BenchmarkResult measure(String name, int workers, int repetitions, Callable<BenchmarkSample> trial)
            throws Exception {
        var samples = new ArrayList<BenchmarkSample>();
        for (int i = 0; i < repetitions; i++) {
            System.out.printf("Measuring %-18s with %2d worker(s), sample %d/%d...%n",
                    name, workers, i + 1, repetitions);
            samples.add(trial.call());
        }
        return new BenchmarkResult(name, workers, samples);
    }

    private static List<Integer> workerCounts(int maxWorkers) {
        var counts = new LinkedHashSet<Integer>();
        for (int workers = 1; workers > 0 && workers <= maxWorkers; workers *= 2) {
            counts.add(workers);
        }
        counts.add(maxWorkers);
        return List.copyOf(counts);
    }

    private static void printConfiguration(BenchmarkSettings settings) {
        System.out.println("Poool physics performance test");
        System.out.printf("Logical processors: %d%n", Runtime.getRuntime().availableProcessors());
        System.out.printf("Board: 4500 balls, warm-up: %d ms, measurement: %d ms, repetitions: %d%n",
                settings.warmupMillis(), settings.measurementMillis(), settings.repetitions());
        System.out.printf("Response probe interval: %d ms%n%n", PhysicsBenchmark.RESPONSE_INTERVAL_MILLIS);
    }

    private static void printResults(List<BenchmarkResult> results, BenchmarkResult sequential) {
        System.out.println();
        System.out.printf("%-21s %7s %12s %13s %11s %13s %11s%n",
                "Implementation", "Workers", "Throughput", "Response ms", "Speedup", "Amdahl max", "Efficiency");
        System.out.println("-----------------------------------------------------------------------------------------------");

        for (var result : results) {
            var oneWorker = oneWorkerResult(results, result);
            double speedup = result.meanThroughput() / sequential.meanThroughput();
            double coreSpeedup = result.meanThroughput() / oneWorker.meanThroughput();
            double efficiency = coreSpeedup / result.workers();
            double amdahlMaximum = amdahlMaximum(results, result);
            String formattedAmdahl = Double.isFinite(amdahlMaximum)
                    ? String.format(Locale.ROOT, "%.2f", amdahlMaximum)
                    : "n/a";
            System.out.printf(Locale.ROOT, "%-21s %7d %12.2f %13.2f %11.2f %13s %10.1f%%%n",
                    result.name(), result.workers(), result.meanThroughput(), result.meanResponseMillis(),
                    speedup, formattedAmdahl, efficiency * 100);
        }
    }

    private static BenchmarkResult oneWorkerResult(List<BenchmarkResult> results, BenchmarkResult result) {
        return results.stream()
                .filter(candidate -> candidate.name().equals(result.name()) && candidate.workers() == 1)
                .findFirst()
                .orElse(result);
    }

    private static double amdahlMaximum(List<BenchmarkResult> results, BenchmarkResult result) {
        var largestRun = results.stream()
                .filter(candidate -> candidate.name().equals(result.name()))
                .max(Comparator.comparingInt(BenchmarkResult::workers))
                .orElse(result);
        if (largestRun.workers() == 1) return 1;
        double speedup = largestRun.meanThroughput() / oneWorkerResult(results, result).meanThroughput();
        int workers = largestRun.workers();
        double parallelFraction = (1 - 1 / speedup) / (1 - 1.0 / workers);
        if (parallelFraction <= 0) return 1;
        if (parallelFraction >= 1) return Double.NaN;
        return 1 / (1 - parallelFraction);
    }

}
