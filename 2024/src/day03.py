import sys
from inputs import load_input


def part1() -> int:
    input_strings = load_input("./input/day03")
    sum = 0
    for input_string in input_strings:
        sum += calculate_uncorrupted_mul(input_string)
    return sum


def part2() -> int:
    input_strings = load_input("./input/day03")
    input_string = "".join(input_strings)
    return calculate_uncorrupted_mul_with_conditionals(input_string)


def calculate_uncorrupted_mul(input_string: str) -> int:
    sum = 0
    mul_prefix_splits = input_string.split("mul(")
    for prefix_split in mul_prefix_splits:
        num_split = prefix_split.split(",")
        if len(num_split) > 1:
            first_num = num_split[0]
            if ")" in num_split[1]:
                second_num = num_split[1].split(")")[0]
                if first_num.isdecimal() and second_num.isdecimal():
                    if int(first_num) < 1000 and int(second_num) < 1000:
                        sum += int(first_num) * int(second_num)
    return sum


def calculate_uncorrupted_mul_with_conditionals(input_string) -> int:
    sum = 0
    dont_splits = input_string.split("don't()")
    sum += calculate_uncorrupted_mul(dont_splits.pop(0))
    for dont_split in dont_splits:
        index = dont_split.find("do()")
        if index >= 0:
            sum += calculate_uncorrupted_mul(dont_split[index:])
    return sum


if __name__ == "__main__":
    print(globals()[sys.argv[1]]())
