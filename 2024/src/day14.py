from functools import reduce
from operator import mul
import sys
from inputs import load_input


class Robot:
    def __init__(self, x: int, y: int, dx: int, dy: int) -> None:
        self.x = x
        self.y = y
        self.dx = dx
        self.dy = dy


def part1() -> int:
    input_strings = load_input("./input/day14")
    robots = get_robots(input_strings)
    end_positions = []
    grid = (101, 103)
    for robot in robots:
        end_positions.append(move_robot(robot, 100, grid))
    quarters = calculate_quadrants(end_positions, grid)
    return reduce(mul, quarters)


def part2() -> int:
    input_strings = load_input("./input/day14")
    robots = get_robots(input_strings)
    grid = (101, 103)
    result_map = {}
    counter = 1
    result_string = ""
    while True:
        end_positions = []
        for robot in robots:
            end_positions.append(move_robot(robot, 1, grid))
        result_string = string_positions(end_positions, grid)
        if "2" not in result_string:
            print(result_string)
            return counter
        result_map[result_string] = counter
        counter += 1


def get_robots(input_strings: list[str]) -> list[Robot]:
    result = []
    for input_string in input_strings:
        coordinate_split = input_string.split("v=")
        position = coordinate_split[0].replace("p=", "").strip().split(",")
        vector = coordinate_split[1].strip().split(",")
        result.append(
            Robot(int(position[0]), int(position[1]), int(vector[0]), int(vector[1]))
        )

    return result


def move_robot(
    robot: Robot, remaining_count: int, grid_size: tuple[int, int]
) -> tuple[int, int]:
    if remaining_count == 0:
        return (robot.x, robot.y)
    robot.x = (robot.x + robot.dx) % grid_size[0]
    robot.y = (robot.y + robot.dy) % grid_size[1]
    return move_robot(robot, remaining_count - 1, grid_size)


def calculate_quadrants(
    positions: list[tuple[int, int]], grid_size: tuple[int, int]
) -> list[int]:
    q1, q2, q3, q4 = 0, 0, 0, 0
    limit_x, limit_y = grid_size
    for x, y in positions:
        if x < limit_x // 2:
            if y < limit_y // 2:
                q1 += 1
            if y > limit_y // 2:
                q2 += 1
        if x > limit_x // 2:
            if y < limit_y // 2:
                q3 += 1
            if y > limit_y // 2:
                q4 += 1
    return [q1, q2, q3, q4]


def string_positions(
    positions: list[tuple[int, int]], grid_size: tuple[int, int]
) -> str:
    grid = []
    for _ in range(grid_size[0]):
        grid.append([0] * grid_size[1])
    for each in positions:
        grid[each[0]][each[1]] += 1
    result_string = ""
    for row in grid:
        for num in row:
            if num is None or num < 1:
                result_string += "."
            else:
                result_string += str(num)

        result_string += "\n"
    return result_string


if __name__ == "__main__":
    print(globals()[sys.argv[1]]())
