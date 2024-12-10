import sys
from typing import Optional
from inputs import load_input


def part1() -> int:
    input_strings = load_input("./input/day10")
    grid = create_grid(input_strings)
    return count_routes_from_trailheads(grid)


def part2() -> int:
    input_strings = load_input("./input/day10")
    grid = create_grid(input_strings)
    return count_routes_from_trailheads(grid, with_distinct_paths=True)


def count_routes_from_trailheads(grid: list[list[int]], with_distinct_paths = False) -> int:
    answer = 0
    for y, row in enumerate(grid):
        for x, tile in enumerate(row):
            if tile == 0:
                visited = set()
                if with_distinct_paths:
                    visited = None
                result = count_routes_from_point((y, x), grid, visited)
                answer += result
    return answer


def count_routes_from_point(
    position: tuple[int, int], grid: list[list[int]], visited: Optional[set[tuple[int, int]]]
) -> int:

    y, x = position
    height = grid[y][x]
    if height == 9:
        if visited is not None:
            if position in visited:
                return 0
            visited.add(position)
            return 1
        return 1

    neighbors = get_valid_neighbors(position, grid)
    if len(neighbors) == 0:
        return 0
    else:
        answer = 0
        for each in neighbors:
            answer += count_routes_from_point(each, grid, visited)
        return answer


def get_valid_neighbors(
    position: tuple[int, int], grid: list[list[int]]
) -> list[tuple[int, int]]:
    y, x = position
    expected_height = grid[y][x] + 1
    neighbors = []
    if y - 1 >= 0:
        if grid[y - 1][x] == expected_height:
            neighbors.append((y - 1, x))
    if y + 1 < len(grid):
        if grid[y + 1][x] == expected_height:
            neighbors.append((y + 1, x))
    if x - 1 >= 0:
        if grid[y][x - 1] == expected_height:
            neighbors.append((y, x - 1))
    if x + 1 < len(grid[y]):
        if grid[y][x + 1] == expected_height:
            neighbors.append((y, x + 1))
    return neighbors


def create_grid(input_strings: list[str]) -> list[list[int]]:
    grid = []
    for input_string in input_strings:
        row = []
        for tile in input_string.strip():
            if tile == ".":
                row.append(-1)
            else:
                row.append(int(tile))
        grid.append(row)
    return grid


if __name__ == "__main__":
    print(globals()[sys.argv[1]]())
