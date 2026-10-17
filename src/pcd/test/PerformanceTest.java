package pcd.test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

/** Entry point for the performance tests requested by the assignment. */
public final class PerformanceTest {
    private PerformanceTest() {
    }

    public static void main(String[] args) throws Exception {
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

    private static BenchmarkResult measure(String name, int workers, int repetitions, Callable<Double> trial)
            throws Exception {
        var samples = new double[repetitions];
        for (int i = 0; i < repetitions; i++) {
            System.out.printf("Measuring %-18s with %2d worker(s), sample %d/%d...%n",
                    name, workers, i + 1, repetitions);
            samples[i] = trial.call();
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
        System.out.println("Sequential: pcd.poool.vsynchronized");
        System.out.println("Native threads: pcd.poool.vnativethreads");
        System.out.println("Executor framework: pcd.poool.vjavaexecutorframework");
        System.out.printf("Board: 4500 balls, warm-up: %d ms, measurement: %d ms, repetitions: %d%n%n",
                settings.warmupMillis(), settings.measurementMillis(), settings.repetitions());
    }

    private static void printResults(List<BenchmarkResult> results, BenchmarkResult sequential) {
        System.out.println();
        System.out.printf("%-21s %7s %12s %11s %11s %11s%n",
                "Implementation", "Workers", "Mean FPS", "S vs seq", "S vs 1", "Core eff.");
        System.out.println("-------------------------------------------------------------------------------");

        for (var result : results) {
            double oneWorkerFps = results.stream()
                    .filter(candidate -> candidate.name().equals(result.name()) && candidate.workers() == 1)
                    .findFirst().orElse(result).mean();
            double speedupVsSequential = result.mean() / sequential.mean();
            double scaling = result.mean() / oneWorkerFps;
            double efficiency = scaling / result.workers();
            System.out.printf(Locale.ROOT, "%-21s %7d %12.2f %11.2f %11.2f %10.1f%%%n",
                    result.name(), result.workers(), result.mean(),
                    speedupVsSequential, scaling, efficiency * 100);
        }
    }

}
