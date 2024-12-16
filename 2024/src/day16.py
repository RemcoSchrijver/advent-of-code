import sys
from inputs import load_input
import copy

sys.setrecursionlimit(5500)


def part1() -> int:
    input_strings = load_input("./input/day16")
    grid, start, end = load_map(input_strings)
    return depth_first_search(grid, start, end)[end]


def part2() -> int:
    input_strings = load_input("./input/day16")
    grid, start, end = load_map(input_strings)
    paths = dfs_recursive(grid, start, end, set(), 0, (0, 1))
    optimal_tiles = set()
    smallest_score = paths[0][1]
    for path, score in paths:
        if score < smallest_score:
            smallest_score = score
    for path, score in paths:
        if score == smallest_score:
            for tile in path:
                optimal_tiles.add(tile)
    return len(optimal_tiles)


def load_map(
    input_strings: list[str],
) -> tuple[list[list[str]], tuple[int, int], tuple[int, int]]:
    grid = []
    start = (0, 0)
    end = (0, 0)
    for y, input_string in enumerate(input_strings):
        row = []
        for x, char in enumerate(input_string.strip()):
            if char == "S":
                start = (y, x)
                row.append(".")
                continue
            if char == "E":
                end = (y, x)
                row.append(".")
                continue
            row.append(char)
        grid.append(row)
    return grid, start, end


def depth_first_search(
    grid: list[list[str]], start: tuple[int, int], end: tuple[int, int]
) -> dict[tuple[int, int], int]:
    queue = [(start, (0, 1))]
    score_map = {start: 0}
    while len(queue) > 0:
        position, direction = queue.pop(0)
        if position == end:
            continue
        score = score_map[position]
        potential_positions = [
            (direction, score + 1),
            (rotate(direction), score + 1001),
            (rotate(rotate(rotate(direction))), score + 1001),
        ]
        for new_direction, new_score in potential_positions:
            new_position = add_tuples(position, new_direction)
            grid_char = grid[new_position[0]][new_position[1]]
            if new_position not in score_map and grid_char == ".":
                score_map[new_position] = new_score
                queue.append((new_position, new_direction))
            else:
                if grid_char == ".":
                    old_score = score_map[new_position]
                    if old_score > new_score:
                        score_map[new_position] = new_score
                        queue.append((new_position, new_direction))
    return score_map


def dfs_recursive(
    grid: list[list[str]],
    position: tuple[int, int],
    end: tuple[int, int],
    visited: set[tuple[int, int]],
    score: int,
    direction: tuple[int, int],
) -> list[tuple[list[tuple[int, int]], int]]:
    if position == end:
        return [([position], score)]
    visited.add(position)
    potential_positions = [
        (direction, score + 1),
        (rotate(direction), score + 1001),
        (rotate(rotate(rotate(direction))), score + 1001),
    ]
    paths = []
    for new_direction, new_score in potential_positions:
        new_position = add_tuples(position, new_direction)

        grid_char = grid[new_position[0]][new_position[1]]
        if new_position not in visited and grid_char == ".":
            found_paths = dfs_recursive(
                grid,
                new_position,
                end,
                visited,
                new_score,
                new_direction,
            )
            for path, score in found_paths:
                paths.append(([position] + path, score))
    return paths


def rotate(direction: tuple[int, int]) -> tuple[int, int]:
    match direction:
        case (0, 1):
            return (1, 0)
        case (1, 0):
            return (0, -1)
        case (0, -1):
            return (-1, 0)
        case _:
            return (0, 1)


def add_tuples(tuple1: tuple[int, int], tuple2: tuple[int, int]) -> tuple[int, int]:
    return (tuple1[0] + tuple2[0], tuple1[1] + tuple2[1])


def print_score_grid(
    grid: list[list[str]], score_map: dict[tuple[int, int], int]
) -> None:
    score_grid = []
    for y, row in enumerate(grid):
        score_row = []
        for x in range(len(row)):
            if (y, x) in score_map:
                score = score_map[(y, x)]

                if score < 10:
                    score_row.append(f"0000{score}")
                elif score < 10000:
                    score_row.append(f"0{score}")
                else:
                    score_row.append(str(score))
            else:
                score_row.append("#####")
        score_grid.append(score_row)
    for each in score_grid:
        print(each)


if __name__ == "__main__":
    print(globals()[sys.argv[1]]())
