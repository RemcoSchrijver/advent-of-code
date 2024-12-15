import sys
from inputs import load_input


def part1() -> int:
    input_strings = load_input("./input/day15")
    grid, start_pos, movements = load_problem(input_strings)
    grid = simulate_movements(start_pos, grid, movements)
    return calculate_score(grid)


def part2() -> int:
    input_strings = load_input("./input/day15")
    pass


def create_grid(input_strings: list[str]) -> tuple[list[list[str]], tuple[int, int]]:
    grid = []
    start_pos = (0, 0)
    for y, input_string in enumerate(input_strings):
        row = []
        for x, char in enumerate(input_string.strip()):
            if char == "@":
                start_pos = (y, x)
            row.append(char)
        grid.append(row)
    return grid, start_pos


def create_movement_order(input_strings: list[str]) -> list[tuple[int, int]]:
    movements = []
    for input_string in input_strings:
        for char in input_string.strip():
            match char:
                case "^":
                    movements.append((-1, 0))
                case ">":
                    movements.append((0, 1))
                case "v":
                    movements.append((1, 0))
                case "<":
                    movements.append((0, -1))
    return movements


def load_problem(
    input_strings: list[str],
) -> tuple[list[list[str]], tuple[int, int], list[tuple[int, int]]]:
    split_index = input_strings.index("\n")
    grid, start_pos = create_grid(input_strings[:split_index])
    movements = create_movement_order(input_strings[split_index:])
    return (grid, start_pos, movements)


def simulate_movements(
    start_pos: tuple[int, int], grid: list[list[str]], movements: list[tuple[int, int]]
) -> list[list[str]]:
    for y, x in movements:
        start_pos = move_robot(start_pos, grid, (y, x))

    return grid


def move_robot(
    start_pos: tuple[int, int], grid: list[list[str]], move: tuple[int, int]
) -> tuple[int, int]:
    y, x = start_pos
    dy, dx = move
    grid_value = grid[y + dy][x + dx]
    match grid_value:
        case ".":
            grid[y][x] = "."
            grid[y + dy][x + dx] = "@"
            return (y + dy, x + dx)
        case "O":
            counter = 1
            while grid[y + dy * counter][x + dx * counter] == "O":
                counter += 1
            if grid[y + dy * (counter)][x + dx * (counter)] == "#":
                return start_pos

            i = 1
            while i < counter:
                grid[y + dy * (i + 1)][x + dx * (i + 1)] = "O"
                i += 1
            grid[y][x] = "."
            grid[y + dy][x + dx] = "@"
            return (y + dy, x + dx)
    return start_pos


def calculate_score(grid: list[list[str]]) -> int:
    result = 0
    for y, row in enumerate(grid):
        for x, char in enumerate(row):
            if char == "O":
                result += 100 * y + x
    return result


def create_larger_grid(
    input_strings: list[str],
) -> tuple[list[list[str]], tuple[int, int]]:
    grid = []
    start_pos = (0, 0)
    for y, input_string in enumerate(input_strings):
        row = []
        for x, char in enumerate(input_string.strip()):
            match char:
                case "@":
                    start_pos = (y, x)
                    row.append(char)
                    row.append(".")
                case "O":
                    row.append("[")
                    row.append("]")
                case _:
                    row.append(char)
                    row.append(char)

        grid.append(row)
    return grid, start_pos



def load_problem_2(
    input_strings: list[str],
) -> tuple[list[list[str]], tuple[int, int], list[tuple[int, int]]]:
    split_index = input_strings.index("\n")
    grid, start_pos = create_larger_grid(input_strings[:split_index])
    movements = create_movement_order(input_strings[split_index:])
    return (grid, start_pos, movements)


def simulate_movements_larger(
    start_pos: tuple[int, int], grid: list[list[str]], movements: list[tuple[int, int]]
) -> list[list[str]]:
    for y, x in movements:
        start_pos = move_robot_larger(start_pos, grid, (y, x))

    return grid


def move_robot_larger(
    start_pos: tuple[int, int], grid: list[list[str]], move: tuple[int, int]
) -> tuple[int, int]:
    y, x = start_pos
    dy, dx = move
    grid_value = grid[y + dy][x + dx]
    match grid_value:
        case ".":
            grid[y][x] = "."
            grid[y + dy][x + dx] = "@"
            return (y + dy, x + dx)
        case "#":
            return start_pos
        case _:
            counter = 1
            while grid[y + dy * counter][x + dx * counter] == "O":
                counter += 1
            if grid[y + dy * (counter)][x + dx * (counter)] == "#":
                return start_pos

            i = 1
            while i < counter:
                grid[y + dy * (i + 1)][x + dx * (i + 1)] = "O"
                i += 1
            grid[y][x] = "."
            grid[y + dy][x + dx] = "@"
            return (y + dy, x + dx)
    return start_pos


def calculate_score(grid: list[list[str]]) -> int:
    result = 0
    for y, row in enumerate(grid):
        for x, char in enumerate(row):
            if char == "O":
                result += 100 * y + x
    return result


if __name__ == "__main__":
    print(globals()[sys.argv[1]]())
