package pcd.poool.vjavaexecutorframework.model;

public record P2d(double x, double y) {

    public P2d sum(V2d v) {
        return new P2d(x + v.x(), y + v.y());
    }

    public V2d sub(P2d v) {
        return new V2d(x - v.x(), y - v.y());
    }

    public String toString() {
        return "P2d(" + x + "," + y + ")";
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double abs() {
        return (double) Math.sqrt(x * x + y * y);
    }

    public V2d getNormalized() {
        double module = (double) Math.sqrt(x * x + y * y);
        return new V2d(x / module, y / module);
    }

    public V2d mul(double fact) {
        return new V2d(x * fact, y * fact);
    }

    public V2d getSwappedX() {
        return new V2d(-x, y);
    }

    public V2d getSwappedY() {
        return new V2d(x, -y);
    }

    public static V2d empty() {
        return new V2d(0, 0);
    }
}

