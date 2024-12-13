import sys
from inputs import load_input


def part1() -> int:
    input_strings = load_input("./input/day13")
    problems = parse_problems(input_strings)
    sum = 0
    for problem in problems:
        sum += try_solve_problem(problem)
    return sum


def part2() -> int:
    input_strings = load_input("./input/day13")
    problems = parse_problems(input_strings)
    problems = convert_problems_to_part_2(problems)
    sum = 0
    for problem in problems:
        sum += try_solve_problem(problem, bound_check=False)
    return sum


class Button:
    def __init__(self, x_move: int, y_move: int) -> None:
        self.x_move = x_move
        self.y_move = y_move

    @classmethod
    def parse_button(cls, input_string: str) -> "Button":
        numbers = input_string.strip().split(":")[1]
        number_split = numbers.split(",")
        x, y = number_split[0].strip().replace("X", ""), number_split[
            1
        ].strip().replace("Y", "")
        return Button(int(x), int(y))

    def __str__(self) -> str:
        return f"Button({self.x_move}, {self.y_move})"


class Problem:
    def __init__(self, a: Button, b: Button, x_target: int, y_target: int) -> None:
        self.a = a
        self.b = b
        self.x_target = x_target
        self.y_target = y_target

    @classmethod
    def parse_problem(cls, a: Button, b: Button, input_string: str) -> "Problem":
        numbers = input_string.strip().split(":")[1]
        number_split = numbers.split(",")
        x, y = number_split[0].strip().replace("X=", ""), number_split[
            1
        ].strip().replace("Y=", "")
        return Problem(a, b, int(x), int(y))

    def __str__(self) -> str:
        return (
            f"Problem({str(self.a)}, {str(self.b)}, {self.x_target}, {self.y_target})"
        )


def parse_problems(input_strings: list[str]) -> list[Problem]:
    result = []
    a: Button = Button(0, 0)
    b: Button = Button(0, 0)
    for each in input_strings:
        line = each.strip()
        if line == "":
            continue
        else:
            if "A" in line:
                a = Button.parse_button(line)
            elif "B" in line:
                b = Button.parse_button(line)
            else:
                result.append(Problem.parse_problem(a, b, line))

    return result


def convert_problems_to_part_2(problems: list[Problem]) -> list[Problem]:
    for problem in problems:
        problem.x_target += 10000000000000
        problem.y_target += 10000000000000
    return problems


def try_solve_problem(problem: Problem, bound_check: bool = True) -> int:
    # 1: n * x_a + m * x_b = x_target
    # 2: n * y_a + m * y_b = y_target
    a = problem.a
    b = problem.b
    factor_1 = b.y_move
    factor_2 = b.x_move
    if (b.x_move > 0 and b.y_move > 0) or (b.x_move < 0 and b.y_move < 0):
        factor_2 = -1 * factor_2

    # n = (problem.x_target * factor_1 + problem.y_target * factor_2) / (
    #     a.x_move * factor_1 + a.y_move * factor_2
    # )
    n1 = problem.x_target * factor_1 + problem.y_target * factor_2
    n2 = a.x_move * factor_1 + a.y_move * factor_2
    if n1 % n2 != 0:
        return 0
    n = n1 // n2

    # m = (problem.x_target - (n * a.x_move)) / b.x_move
    m1 = problem.x_target - n * a.x_move
    m2 = b.x_move
    if m1 % m2 != 0:
        return 0
    m = m1 // m2

    result = 0
    if bound_check:
        if n <= 100 and m <= 100:
            result = n * 3 + m
    else:
        result = n * 3 + m
    return result


if __name__ == "__main__":
    print(globals()[sys.argv[1]]())
