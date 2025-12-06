package models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Grid<T> implements Iterable<T> {

    public List<List<T>> grid;

    public Grid(int length, int width) {
        this.grid = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            grid.add(new ArrayList<>());
        }
    }

    public Grid(List<List<T>> inputGrid) {
        this.grid = inputGrid;
    }

    public T set(int x, int y, T value) {
        return grid.get(y).set(x, value);
    }

    public T get(int x, int y) {
        return grid.get(y).get(x);
    }

    public int length() {
        return grid.size();
    }

    public int width(int y) {
        return grid.get(y).size();
    }

    public List<T> returnNeighbors(int x, int y) {
        List<T> result = new ArrayList<>();
        for (int i = 1; i >= -1; i--) {
            for (int j = 1; j >= -1; j--) {
                if (i == 0 && j == 0) {
                    continue;
                }
                if (coordinatesOnGrid(x + i, y + j)) {
                    result.add(get(x + i, y + j));
                }
            }
        }
        return result;
    }

    public List<T> getRow(int y) {
        return grid.get(y);
    }

    public List<T> getColumn(int x) {
        List<T> result = new ArrayList<>();
        for (int y = 0; y < length(); y++) {
            result.add(get(x, y));
        }
        return result;
    }

    public boolean coordinatesOnGrid(Pair<Integer, Integer> coordinates) {
        return coordinatesOnGrid(coordinates.first, coordinates.second);
    }

    public boolean coordinatesOnGrid(int x, int y) {
        return x > -1 && y > -1 && grid.size() > y && grid.get(y).size() > x;
    }

    @Override
    public Iterator<T> iterator() {
        return grid.stream().flatMap(Collection::stream).collect(Collectors.toList()).iterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (List<T> list : grid) {
            sb.append(list.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
