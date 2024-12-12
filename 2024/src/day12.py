from collections import defaultdict
import sys
from inputs import load_input


def part1() -> int:
    input_strings = load_input("./input/day12")
    grid = create_grid(input_strings)
    plots = create_plots(grid)
    return calculate_fence_cost(plots)


def part2() -> int:
    input_strings = load_input("./input/day12")
    grid = create_grid(input_strings)
    plots = create_plots(grid)
    return calculate_side_fence_cost(plots)


def create_grid(input_strings: list[str]) -> list[list[str]]:
    grid = []
    for input_string in input_strings:
        row = []
        for char in input_string.strip():
            row.append(char)
        grid.append(row)
    return grid


def calculate_fence_cost(farm: dict[str, list[set[tuple[int, int]]]]) -> int:
    total_cost = 0
    for plots in farm.values():
        for plot in plots:
            plot_area = len(plot)
            plot_circumference = 0
            for plot_tile in plot:
                y, x = plot_tile
                if (y - 1, x) not in plot:
                    plot_circumference += 1
                if (y, x - 1) not in plot:
                    plot_circumference += 1
                if (y + 1, x) not in plot:
                    plot_circumference += 1
                if (y, x + 1) not in plot:
                    plot_circumference += 1
            total_cost += plot_area * plot_circumference
    return total_cost


def calculate_side_fence_cost(farm: dict[str, list[set[tuple[int, int]]]]) -> int:
    total_cost = 0
    for key, plots in farm.items():
        for plot in plots:
            plot_area = len(plot)
            plot_sides_y: dict[tuple[int, int], list[int]] = defaultdict(list)
            plot_sides_x: dict[tuple[int, int], list[int]] = defaultdict(list)
            for plot_tile in plot:
                y, x = plot_tile
                if (y - 1, x) not in plot:
                    plot_sides_y[y, y - 1].append(x)
                if (y, x - 1) not in plot:
                    plot_sides_x[x, x - 1].append(y)
                if (y + 1, x) not in plot:
                    plot_sides_y[y, y + 1].append(x)
                if (y, x + 1) not in plot:
                    plot_sides_x[x, x + 1].append(y)
            number_plot_sides = calculate_number_of_sides(plot_sides_y)
            number_plot_sides += calculate_number_of_sides(plot_sides_x)
            total_cost += plot_area * number_plot_sides
    return total_cost


def calculate_number_of_sides(sides: dict[tuple[int, int], list[int]]) -> int:
    result = 0
    for key, coordinates in sides.items():
        coordinates.sort()
        coordinate = coordinates.pop()
        sides_pp = 0
        while len(coordinates) >= 0:
            if len(coordinates) == 0:
                sides_pp += 1
                break
            else:
                new_coordinate = coordinates.pop()
                if new_coordinate != coordinate - 1:
                    sides_pp += 1
                coordinate = new_coordinate
        result += sides_pp
    return result


def create_plots(grid: list[list[str]]) -> dict[str, list[set[tuple[int, int]]]]:
    result = {}
    for y, row in enumerate(grid):
        for x, tile in enumerate(row):
            if tile in result and position_in_plot((y, x), result[tile]):
                continue
            else:
                garden_plot = explore_garden_plot(tile, (y, x), grid, set())
                if tile not in result:
                    result[tile] = [garden_plot]
                else:
                    result[tile].append(garden_plot)
    return result


def position_in_plot(
    position: tuple[int, int], plots: list[set[tuple[int, int]]]
) -> bool:
    for plot in plots:
        if position in plot:
            return True
    else:
        return False


def explore_garden_plot(
    original_tile: str,
    position: tuple[int, int],
    grid: list[list[str]],
    cache: set[tuple[int, int]],
) -> set[tuple[int, int]]:
    if position in cache:
        return cache
    y, x = position
    if y < 0 or y >= len(grid) or x < 0 or x >= len(grid[y]):
        return set()
    tile_type = grid[y][x]
    if tile_type != original_tile:
        return set()
    cache.add(position)
    cache = cache.union(
        *[
            explore_garden_plot(original_tile, (y - 1, x), grid, cache),
            explore_garden_plot(original_tile, (y, x - 1), grid, cache),
            explore_garden_plot(original_tile, (y + 1, x), grid, cache),
            explore_garden_plot(original_tile, (y, x + 1), grid, cache),
        ]
    )

    return cache


if __name__ == "__main__":
    print(globals()[sys.argv[1]]())
