package models;

import java.util.Objects;

public class Vector3D {

    public long x;
    public long y;
    public long z;

    public Vector3D(long x, long y, long z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double calculateDistanceToPoint(Vector3D other) {
        return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2) + Math.pow(z - other.z, 2));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('(').append(x).append(',').append(y).append(',').append(z).append(')');
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vector3D) {
            Vector3D other = (Vector3D) obj;
            return x == other.x && y == other.y && z == other.z;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
