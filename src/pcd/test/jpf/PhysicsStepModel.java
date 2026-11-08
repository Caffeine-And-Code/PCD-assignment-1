package pcd.test.jpf;

final class PhysicsStepModel {
    private final boolean[] moved;
    private final boolean[] checkedCollisions;
    private int movements;
    private int completed;

    PhysicsStepModel(int workers) {
        moved = new boolean[workers];
        checkedCollisions = new boolean[workers];
    }

    synchronized void execute(int id) throws InterruptedException {
        moved[id] = true;
        movements++;
        notifyAll();
        while (movements < moved.length) wait();

        assert movements == moved.length;
        checkedCollisions[id] = true;
        completed++;
        notifyAll();
    }

    synchronized void awaitFinished() throws InterruptedException {
        while (completed < moved.length) wait();
    }

    synchronized boolean allMoved() {
        for (boolean value : moved) if (!value) return false;
        return true;
    }

    synchronized boolean allCheckedCollisions() {
        for (boolean value : checkedCollisions) if (!value) return false;
        return true;
    }
}
