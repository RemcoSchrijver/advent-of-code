import sys
from math import gcd
from inputs import load_input


def part1() -> int:
    input_strings = load_input("./input/day08")
    grid = create_grid(input_strings)
    antenna_positions = get_antenna_positions(grid)
    count = get_number_anti_nodes(len(grid), len(grid[0]), antenna_positions)
    return count


def part2() -> int:
    input_strings = load_input("./input/day08")
    grid = create_grid(input_strings)
    antenna_positions = get_antenna_positions(grid)
    count = get_number_anti_nodes_in_line(len(grid), len(grid[0]), antenna_positions)
    return count


def create_grid(input_strings: list[str]) -> list[list[str]]:
    grid = []
    for row in input_strings:
        grid_row = []
        for character in row:
            if character != "\n":
                grid_row.append(character)

        grid.append(grid_row)
    return grid


def get_antenna_positions(grid: list[list[str]]) -> dict[str, list[tuple[int, int]]]:
    antenna_positions: dict[str, list[tuple[int, int]]] = {}
    for y, row in enumerate(grid):
        for x, character in enumerate(row):
            if character != ".":
                if character in antenna_positions:
                    antenna_positions[character].append((y, x))
                else:
                    antenna_positions[character] = [(y, x)]
    return antenna_positions


def get_number_anti_nodes(
    max_y, max_x, antenna_position_map: dict[str, list[tuple[int, int]]]
) -> int:
    anti_node_pos = set()
    for antenna_positions in antenna_position_map.values():
        for i, antenna_pos in enumerate(antenna_positions):
            for j, other_antenna_pos in enumerate(antenna_positions):
                if i == j:
                    continue
                this_y, this_x = antenna_pos
                other_y, other_x = other_antenna_pos
                new_y = other_y - this_y + other_y
                new_x = other_x - this_x + other_x
                if new_y >= 0 and new_y < max_y and new_x >= 0 and new_x < max_x:
                    anti_node_pos.add((new_y, new_x))
    return len(anti_node_pos)


def get_number_anti_nodes_in_line(
    max_y, max_x, antenna_position_map: dict[str, list[tuple[int, int]]]
) -> int:
    anti_node_pos = set()
    for antenna_positions in antenna_position_map.values():
        if len(antenna_positions) > 1:
            for each in antenna_positions:
                anti_node_pos.add(each)
        for i, antenna_pos in enumerate(antenna_positions):
            for j, other_antenna_pos in enumerate(antenna_positions):
                if i == j:
                    continue
                this_y, this_x = antenna_pos
                other_y, other_x = other_antenna_pos
                new_y = other_y - this_y
                new_x = other_x - this_x
                greatest_divisor = gcd(new_y, new_x)
                vector = (new_y / greatest_divisor, new_x / greatest_divisor)
                new_position = (int(other_y + vector[0]), int(other_x + vector[1]))
                count = 1
                while (
                    new_position[0] >= 0
                    and new_position[0] < max_y
                    and new_position[1] >= 0
                    and new_position[1] < max_x
                ):
                    anti_node_pos.add(new_position)
                    new_position = (
                        int(new_position[0] + vector[0] * count),
                        int(new_position[1] + vector[1] * count),
                    )

    return len(anti_node_pos)


if __name__ == "__main__":
    print(globals()[sys.argv[1]]())
