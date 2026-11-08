package pcd.test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

public final class PerformanceTest {
    private static final int WARMUP_STEPS = 20;
    private static final int MEASURED_STEPS = 30;
    private static final int REPETITIONS = 3;

    private PerformanceTest() {
    }

    public static void main(String[] args) throws Exception {
        int cores = Runtime.getRuntime().availableProcessors();
        var benchmark = new PhysicsBenchmark(WARMUP_STEPS, MEASURED_STEPS);
        var results = new ArrayList<BenchmarkResult>();

        System.out.printf("Logical processors: %d, warm-up: %d steps, measurement: %d steps, repetitions: %d%n%n",
                cores, WARMUP_STEPS, MEASURED_STEPS, REPETITIONS);

        var sequential = measure("Sequential", 1, benchmark::sequential);
        results.add(sequential);

        for (int workers : workerCounts(cores)) {
            results.add(measure("Native threads", workers, () -> benchmark.nativeThreads(workers)));
            results.add(measure("Executor", workers, () -> benchmark.executorFramework(workers)));
        }

        printResults(results, sequential, MEASURED_STEPS);
    }

    private static BenchmarkResult measure(String name, int workers, Callable<Long> trial) throws Exception {
        System.out.printf("Measuring %-14s with %2d worker(s)...%n", name, workers);
        long total = 0;
        for (int i = 0; i < REPETITIONS; i++) total += trial.call();
        return new BenchmarkResult(name, workers, total / (double) REPETITIONS);
    }

    private static List<Integer> workerCounts(int cores) {
        var counts = new LinkedHashSet<Integer>();
        for (int workers = 1; workers > 0 && workers <= cores; workers *= 2) counts.add(workers);
        counts.add(cores);
        counts.add(cores * 2);
        return List.copyOf(counts);
    }

    private static void printResults(List<BenchmarkResult> results, BenchmarkResult sequential, int steps) {
        System.out.println();
        System.out.printf("%-18s %7s %12s %12s %10s %11s%n",
                "Implementation", "Workers", "ms/step", "Steps/s", "Speedup", "Efficiency");
        System.out.println("------------------------------------------------------------------------");
        for (var result : results) {
            System.out.printf(Locale.ROOT, "%-18s %7d %12.2f %12.2f %10.2f %10.1f%%%n",
                    result.name(), result.workers(), result.millisecondsPerStep(steps), result.throughput(steps),
                    result.speedup(sequential), result.efficiency(sequential) * 100);
        }
    }
}
