import sys
from typing import Optional
from inputs import load_input


def part1() -> int:
    input_strings = load_input("./input/day02")
    return count_safe_reports(input_strings)


def part2() -> int:
    input_strings = load_input("./input/day02")
    return count_safe_reports(input_strings, with_dampener=True)


def parse_reports(input_strings: list[str]) -> list[list[int]]:
    result = []
    for each in input_strings:
        report = list(map(int, each.split(" ")))
        result.append(report)
    return result


def count_safe_reports(input_strings: list[str], with_dampener: bool = False) -> int:
    reports = parse_reports(input_strings)
    count = 0
    for report in reports:
        if is_safe(report):
            count += 1
        elif with_dampener:
            sublists = create_sublists(report)
            if any(map(is_safe, sublists)):
                count += 1

    return count


def create_sublists(report: list[int]) -> list[list[int]]:
    result = []
    for i in range(len(report)):
        result.append(report[:i] + report[i + 1 :])
    return result


def is_safe(report: list[int]) -> bool:
    return is_safe_increasing(report) or is_safe_decreasing(report)


def is_safe_increasing(report: list[int]) -> bool:
    report_length = len(report)
    for i in range(report_length):
        if i < report_length - 1:
            dif = report[i + 1] - report[i]
            if dif < 1 or dif > 3:
                return False
        else:
            return True
    return True


def is_safe_decreasing(report: list[int]) -> bool:
    report_length = len(report)
    for i in range(report_length):
        if i < report_length - 1:
            dif = report[i] - report[i + 1]
            if dif < 1 or dif > 3:
                return False
        else:
            return True
    return True


if __name__ == "__main__":
    print(globals()[sys.argv[1]]())
