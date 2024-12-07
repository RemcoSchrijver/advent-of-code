import copy
import sys
from inputs import load_input


def part1() -> int:
    input_strings = load_input("./input/day07")
    equations = get_equations(input_strings)
    valid_values = return_valid_equations(equations)
    return sum(valid_values)


def part2() -> int:
    input_strings = load_input("./input/day07")
    equations = get_equations(input_strings)
    valid_values = return_valid_equations(equations, use_concat=True)
    return sum(valid_values)


def get_equations(input_strings: list[str]) -> list[tuple[int, list[int]]]:
    result = []
    for equation in input_strings:
        value_input_split = equation.split(":")
        value = int(value_input_split[0])
        inputs = []
        for input in value_input_split[1].strip().split(" "):
            inputs.append(int(input))
        result.append((value, inputs))
    return result


def return_valid_equations(
    equations: list[tuple[int, list[int]]], use_concat: bool = False
) -> list[int]:
    valid_equations = []
    for expected, inputs in equations:
        collector = inputs.pop(0)
        potential_answers = brute_force_solutions(
            expected, collector, inputs, use_concat
        )
        if expected in potential_answers:
            valid_equations.append(expected)

    return valid_equations


def brute_force_solutions(
    expected: int, collector: int, inputs: list[int], use_concat: bool
) -> list[int]:
    if collector > expected:
        return []
    if len(inputs) == 0:
        return [collector]
    else:
        next_input = inputs.pop(0)
        plus_collector = collector + next_input
        mult_collector = collector * next_input
        result = brute_force_solutions(
            expected, plus_collector, copy.deepcopy(inputs), use_concat
        ) + brute_force_solutions(
            expected, mult_collector, copy.deepcopy(inputs), use_concat
        )
        if use_concat:
            concat_collector = int(f"{collector}{next_input}")
            result += brute_force_solutions(
                expected, concat_collector, copy.deepcopy(inputs), use_concat
            )
        return result


if __name__ == "__main__":
    print(globals()[sys.argv[1]]())
