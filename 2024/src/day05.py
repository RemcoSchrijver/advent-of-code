import sys
from inputs import load_input


def part1() -> int:
    input_strings = load_input("./input/day05")
    rule_strings, update_strings = split_rules_and_updates(input_strings)
    rules = load_rules(rule_strings)
    return evaluate_updates(rules, update_strings)


def part2() -> int:
    input_strings = load_input("./input/day05")
    rule_strings, update_strings = split_rules_and_updates(input_strings)
    rules = load_rules(rule_strings)
    return fix_broken_updates(rules, update_strings)


def split_rules_and_updates(input_strings: list[str]) -> tuple[list[str], list[str]]:
    rules = []
    updates = []
    update_lines = False
    for each in input_strings:
        if each == "" or each == "\n":
            update_lines = True
            continue
        if update_lines:
            updates.append(each)
        else:
            rules.append(each)
    return (rules, updates)


def load_rules(rule_strings: list[str]) -> dict[int, set[int]]:
    rules = {}
    for rule_string in rule_strings:
        split = rule_string.split("|")
        num1 = int(split[0])
        num2 = int(split[1])

        if num2 in rules:
            rules[num2].add(num1)
        else:
            rules[num2] = {num1}

    return rules


def evaluate_updates(rules: dict[int, set[int]], update_strings: list[str]) -> int:
    sum = 0
    for update_string in update_strings:
        update = list(map(int, update_string.split(",")))
        if verify_update(rules, update):
            sum += update[int((len(update) - 1) / 2)]

    return sum


def verify_update(rules: dict[int, set[int]], update: list[int]) -> bool:
    previous_nums = []
    for num in update:
        if num not in rules:
            previous_nums.append(num)
        else:
            required_nums = rules[num]
            for required_num in required_nums:
                if required_num in previous_nums or required_num not in update:
                    previous_nums.append(num)
                else:
                    return False
    return True


def fix_broken_updates(rules: dict[int, set[int]], update_strings: list[str]) -> int:
    sum = 0
    for update_string in update_strings:
        update = list(map(int, update_string.split(",")))
        if not verify_update(rules, update):
            update = fix_broken_update(rules, update)
            while not verify_update(rules, update):
                update = fix_broken_update(rules, update)

            sum += update[int((len(update) - 1) / 2)]

    return sum


def fix_broken_update(rules: dict[int, set[int]], update: list[int]) -> list[int]:
    for i in range(len(update)):
        num = update[i]
        if num in rules:
            required_nums = rules[num]
            for required_num in required_nums:
                if required_num in update:
                    req_index = update.index(required_num)
                    if req_index > i:
                        update[i], update[req_index] = update[req_index], update[i]

    return update


if __name__ == "__main__":
    print(globals()[sys.argv[1]]())
