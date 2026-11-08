package pcd.test.jpf;

public final class PhysicsStepJpfTest {
    private static final int WORKERS = 2;

    private PhysicsStepJpfTest() {
    }

    public static void main(String[] args) throws InterruptedException {
        var step = new PhysicsStepModel(WORKERS);
        var workers = new Thread[WORKERS];

        for (int id = 0; id < WORKERS; id++) {
            int workerId = id;
            workers[id] = new Thread(() -> execute(step, workerId));
            workers[id].start();
        }

        step.awaitFinished();
        for (Thread worker : workers) worker.join();

        assert step.allMoved();
        assert step.allCheckedCollisions();
        for (Thread worker : workers) assert !worker.isAlive();
    }

    private static void execute(PhysicsStepModel step, int workerId) {
        try {
            step.execute(workerId);
        } catch (InterruptedException ex) {
            throw new AssertionError(ex);
        }
    }
}
