package models;

public class Pair<T, S> {
    public T first;
    public S second;

    public Pair(T first, S second) {
        this.first = first;
        this.second = second;
    }
    
    @Override
    public String toString() {
        return "Pair(" + first.toString() + "," + second.toString() +")";
    }
}
