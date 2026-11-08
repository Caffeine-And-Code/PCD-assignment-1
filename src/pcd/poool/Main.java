package pcd.poool;

import pcd.test.PerformanceTest;

public final class Main {
    private enum Program {
        SYNCHRONIZED,
        NATIVE_THREADS,
        EXECUTOR_FRAMEWORK,
        PERFORMANCE_TEST
    }

    private static final Program PROGRAM = Program.NATIVE_THREADS;

    private Main() {
    }

    static void main(String[] args) throws Exception {
        switch (PROGRAM) {
            case SYNCHRONIZED -> pcd.poool.vsynchronized.Main.main(args);
            case NATIVE_THREADS -> pcd.poool.vnativethreads.Main.main(args);
            case EXECUTOR_FRAMEWORK -> pcd.poool.vjavaexecutorframework.Main.main(args);
            case PERFORMANCE_TEST -> PerformanceTest.main(args);
        }
    }
}
