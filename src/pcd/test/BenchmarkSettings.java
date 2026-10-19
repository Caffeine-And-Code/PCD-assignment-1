package pcd.test;

record BenchmarkSettings(long warmupMillis, long measurementMillis, int repetitions, int maxWorkers) {
    private static final long DEFAULT_WARMUP_MILLIS = 1_000;
    private static final long DEFAULT_MEASUREMENT_MILLIS = 3_000;
    private static final int DEFAULT_REPETITIONS = 3;

    static BenchmarkSettings from(String[] args) {
        if (args.length > 4) throw new IllegalArgumentException(usage());
        try {
            var settings = new BenchmarkSettings(
                    longArgument(args, 0, DEFAULT_WARMUP_MILLIS),
                    longArgument(args, 1, DEFAULT_MEASUREMENT_MILLIS),
                    intArgument(args, 2, DEFAULT_REPETITIONS),
                    intArgument(args, 3, Runtime.getRuntime().availableProcessors()));
            settings.validate();
            return settings;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("All arguments must be integers.\n" + usage());
        }
    }

    private void validate() {
        if (warmupMillis < 0 || measurementMillis <= 0 || repetitions <= 0 || maxWorkers <= 0) {
            throw new IllegalArgumentException(usage());
        }
    }

    private static long longArgument(String[] args, int index, long fallback) {
        return args.length > index ? Long.parseLong(args[index]) : fallback;
    }

    private static int intArgument(String[] args, int index, int fallback) {
        return args.length > index ? Integer.parseInt(args[index]) : fallback;
    }

    private static String usage() {
        return "Usage: PerformanceTest [warmup-ms] [measurement-ms] [repetitions] [max-workers]";
    }
}
