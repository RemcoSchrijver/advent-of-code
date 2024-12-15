import sys
from inputs import load_input


def part1() -> int:
    input_strings = load_input("./input/day15")
    grid, start_pos, movements = load_problem(input_strings)
    grid = simulate_movements(start_pos, grid, movements)
    return calculate_score(grid)


def part2() -> int:
    input_strings = load_input("./input/day15")
    grid, start_pos, movements = load_problem_2(input_strings)
    grid = simulate_movements_larger(start_pos, grid, movements)
    return calculate_score_larger(grid)


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
                    start_pos = (y, x * 2)
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
            if dy == 0:
                counter = 1
                while grid[y + dy * counter][x + dx * counter] in "[]":
                    counter += 2
                if grid[y + dy * (counter)][x + dx * (counter)] == "#":
                    return start_pos

                i = 1
                if dx > 0:
                    while i < counter:
                        grid[y + dy * (i + 1)][x + dx * (i + 1)] = "["
                        i += 1
                        grid[y + dy * (i + 1)][x + dx * (i + 1)] = "]"
                        i += 1
                else:
                    while i < counter:
                        grid[y + dy * (i + 1)][x + dx * (i + 1)] = "]"
                        i += 1
                        grid[y + dy * (i + 1)][x + dx * (i + 1)] = "["
                        i += 1

                grid[y][x] = "."
                grid[y + dy][x + dx] = "@"
                return (y + dy, x + dx)

            if grid_value == "[":
                start_box = (y + dy, x, y + dy, x + 1)
            else:
                start_box = (y + dy, x - 1, y + dy, x)
            connected_boxes = get_connected_boxes_y([start_box], grid, dy)
            for y1, x1, y2, x2 in connected_boxes:
                if grid[y1 + dy][x1] == "#" or grid[y2 + dy][x2] == "#":
                    return start_pos
            marked = set()
            for y1, x1, y2, x2 in connected_boxes:
                if (y1, x1) not in marked:
                    grid[y1][x1] = "."
                if (y2, x2) not in marked:
                    grid[y2][x2] = "."
                grid[y1 + dy][x1] = "["
                marked.add((y1 + dy, x1))
                grid[y2 + dy][x2] = "]"
                marked.add((y2 + dy, x2))

            grid[y][x] = "."
            grid[y + dy][x + dx] = "@"
            return (y + dy, x + dx)


def get_connected_boxes_y(
    connected_boxes: list[tuple[int, int, int, int]], grid: list[list[str]], dy: int
) -> list[tuple[int, int, int, int]]:
    if len(connected_boxes) < 1:
        return []
    new_connected_boxes = []
    for y1, x1, y2, x2 in connected_boxes:
        next_tiles = grid[y1 + dy][x1] + grid[y2 + dy][x2]
        if next_tiles == "[]":
            new_connected_boxes.append((y1 + dy, x1, y2 + dy, x2))
            continue
        if "]" in next_tiles:
            new_connected_boxes.append((y1 + dy, x1 - 1, y2 + dy, x1))
        if "[" in next_tiles:
            new_connected_boxes.append((y1 + dy, x2, y2 + dy, x2 + 1))
    return connected_boxes + get_connected_boxes_y(new_connected_boxes, grid, dy)


def calculate_score_larger(grid: list[list[str]]) -> int:
    result = 0
    for y, row in enumerate(grid):
        for x, char in enumerate(row):
            if char == "[":
                result += 100 * y + x
    return result


if __name__ == "__main__":
    print(globals()[sys.argv[1]]())
