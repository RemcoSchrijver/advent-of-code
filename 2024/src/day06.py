import copy
import sys
from inputs import load_input


def part1() -> int:
    input_strings = load_input("./input/day06")
    grid = create_grid(input_strings)
    starting_position = get_starting_position(input_strings)
    traversed_tiles = get_traversed_tiles(starting_position, grid)
    return len(set(traversed_tiles))


def part2() -> int:
    input_strings = load_input("./input/day06")
    grid = create_grid(input_strings)
    starting_position = get_starting_position(input_strings)
    sum = traverse_while_introducing_blockers(starting_position, (-1, 0), grid)
    return sum


def create_grid(input_strings: list[str]) -> list[list[bool]]:
    grid = []
    for y in range(len(input_strings)):
        row = []
        input_row = input_strings[y].strip()
        for x in range(len(input_row)):
            if input_strings[y][x] == "#":
                row.append(False)
            else:
                row.append(True)
        grid.append(row)
    return grid


def get_starting_position(input_strings: list[str]) -> tuple[int, int]:
    for y in range(len(input_strings)):
        for x in range(len(input_strings[y])):
            if input_strings[y][x] == "^":
                return (y, x)
    raise RuntimeError("Starting position not found")


def get_traversed_tiles(
    starting_position: tuple[int, int], grid: list[list[bool]]
) -> list[tuple[int, int]]:
    current_position = starting_position
    traversed_tiles = [starting_position]
    vector = (-1, 0)
    while not out_of_bounds(current_position, vector, grid):
        next_position = calculate_next_position(current_position, vector, grid)
        if current_position != next_position:
            current_position = next_position
            traversed_tiles.append(next_position)
        else:
            vector = rotate_vector(vector)
    return traversed_tiles


def calculate_next_position(
    current_position: tuple[int, int], vector: tuple[int, int], grid: list[list[bool]]
) -> tuple[int, int]:
    continued_vector = add_tuple(current_position, vector)
    if get_tile(continued_vector, grid):
        current_position = continued_vector
    return current_position


def traverse_while_introducing_blockers(
    starting_position: tuple[int, int],
    start_vector: tuple[int, int],
    grid: list[list[bool]],
) -> int:
    traversed_tiles = set(get_traversed_tiles(starting_position, grid))
    traversed_tiles.remove(starting_position)
    sum = 0
    for each in traversed_tiles:
        new_grid = introduce_blocker(each, grid)
        can_be_blocked = traverse_with_loop_check(
            starting_position, start_vector, new_grid
        )
        if can_be_blocked:
            sum += 1
    return sum


def introduce_blocker(
    blocker_position: tuple[int, int], grid: list[list[bool]]
) -> list[list[bool]]:
    new_grid = copy.deepcopy(grid)
    new_grid[blocker_position[0]][blocker_position[1]] = False
    return new_grid


def traverse_with_loop_check(
    starting_position: tuple[int, int],
    start_vector: tuple[int, int],
    grid: list[list[bool]],
) -> bool:
    current_position = starting_position
    vector = start_vector
    traversed: set[tuple[tuple[int, int], tuple[int, int]]] = set()
    while not out_of_bounds(current_position, vector, grid):
        if (current_position, vector) in traversed:
            return True
        next_position = calculate_next_position(current_position, vector, grid)
        if current_position != next_position:
            traversed.add((current_position, vector))
            current_position = next_position
        else:
            vector = rotate_vector(vector)
    return False


def out_of_bounds(
    position: tuple[int, int], vector: tuple[int, int], grid: list[list[bool]]
) -> bool:
    y_max = len(grid)
    (y, x) = add_tuple(position, vector)
    return y < 0 or y >= y_max or x < 0 or x >= len(grid[y])


def add_tuple(tuple1: tuple[int, int], tuple2: tuple[int, int]) -> tuple[int, int]:
    return (tuple1[0] + tuple2[0], tuple1[1] + tuple2[1])


def get_tile(position: tuple[int, int], grid: list[list[bool]]) -> bool:
    return grid[position[0]][position[1]]


def rotate_vector(vector: tuple[int, int]) -> tuple[int, int]:
    if vector == (-1, 0):
        return (0, 1)
    elif vector == (0, 1):
        return (1, 0)
    elif vector == (1, 0):
        return (0, -1)
    elif vector == (0, -1):
        return (-1, 0)
    raise RuntimeError("Invalid rotation")


if __name__ == "__main__":
    print(globals()[sys.argv[1]]())
