package pcd.poool.vsynchronized.model.base;

public record Vector(double x, double y) {
    public Vector sum(Vector v) {
        return new Vector(x + v.x(), y + v.y());
    }

    public Vector sub(Vector v) {
        return new Vector(x - v.x(), y - v.y());
    }

    public String toString() {
        return "Vector(" + x + "," + y + ")";
    }

    public double abs() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector getNormalized() {
        double module = Math.sqrt(x * x + y * y);
        return new Vector(x / module, y / module);
    }

    public Vector mul(double fact) {
        return new Vector(x * fact, y * fact);
    }

    public Vector getSwappedX() {
        return new Vector(-x, y);
    }

    public Vector getSwappedY() {
        return new Vector(x, -y);
    }

    public static Vector empty() {
        return new Vector(0, 0);
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }
}
