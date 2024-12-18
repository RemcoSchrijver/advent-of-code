import sys
from inputs import load_input


def part1() -> int:
    input_strings = load_input("./input/day18")
    obstacles = parse_bytes(input_strings, 1024)
    grid = create_maze((71, 71), obstacles)
    end = (70, 70)
    return find_shortest_path(grid, (0, 0), end)[end]


def test_part1() -> int:
    input_strings = load_input("./tests/day18_1")
    obstacles = parse_bytes(input_strings, 12)
    grid = create_maze((7, 7), obstacles)
    end = (6, 6)
    return find_shortest_path(grid, (0, 0), end)[end]


def part2() -> str:
    input_strings = load_input("./input/day18")
    return binary_search_for_cutoff((71, 71), input_strings)


def test_part2() -> str:
    input_strings = load_input("./tests/day18_1")
    return binary_search_for_cutoff((7, 7), input_strings)


def parse_bytes(input_strings: list[str], number: int) -> set[tuple[int, int]]:
    result = set()
    for i in range(number):
        coordinate_str = input_strings[i]
        coordinates = coordinate_str.strip().split(",")
        x, y = coordinates[0], coordinates[1]
        result.add((int(y), int(x)))
    return result


def create_maze(
    grid_size: tuple[int, int], obstacles: set[tuple[int, int]]
) -> list[list[str]]:
    grid = []
    for y in range(grid_size[0]):
        row = []
        for x in range(grid_size[1]):
            if (y, x) in obstacles:
                row.append("#")
            else:
                row.append(".")
        grid.append(row)
    return grid


def find_shortest_path(
    grid: list[list[str]], start: tuple[int, int], end: tuple[int, int]
) -> dict[tuple[int, int], int]:
    score_map = {}
    next_moves = [(start, 0)]
    while len(next_moves) > 0:
        position, score = next_moves.pop()
        if position in score_map:
            if score >= score_map[position]:
                continue
        score_map[position] = score
        if position == end:
            continue

        potential_vectors = [(-1, 0), (1, 0), (0, -1), (0, 1)]
        for potential_vector in potential_vectors:
            y, x = add_tuples(position, potential_vector)

            if is_in_bounds(grid, (y, x)) and grid[y][x] == ".":
                next_moves.append(((y, x), score + 1))
    return score_map


def binary_search_for_cutoff(size: tuple[int, int], input_strings: list[str]) -> str:
    search_space = (0, len(input_strings))
    while search_space[0] != search_space[1]:
        middle = search_space[0] + (search_space[1] - search_space[0]) // 2
        obstacles = parse_bytes(input_strings, middle)
        grid = create_maze(size, obstacles)
        end = add_tuples(size, (-1, -1))
        score_map = find_shortest_path(grid, (0, 0), end)
        if end not in score_map:
            search_space = (search_space[0], middle)
        else:
            search_space = (middle, search_space[1])

        if abs(search_space[0] - search_space[1]) == 1:
            obstacles = parse_bytes(input_strings, search_space[1])
            grid = create_maze(size, obstacles)
            end = add_tuples(size, (-1, -1))
            score_map = find_shortest_path(grid, (0, 0), end)
            if end not in find_shortest_path(grid, (0, 0), end):
                search_space = (search_space[0], search_space[0])
            else:
                search_space = (search_space[1], search_space[1])

    return input_strings[search_space[0]]


def add_tuples(t1: tuple[int, int], t2: tuple[int, int]) -> tuple[int, int]:
    return (t1[0] + t2[0], t1[1] + t2[1])


def is_in_bounds(grid: list[list[str]], position: tuple[int, int]) -> bool:
    y, x = position
    return y >= 0 and y < len(grid) and x >= 0 and x < len(grid[y])


if __name__ == "__main__":
    print(globals()[sys.argv[1]]())
