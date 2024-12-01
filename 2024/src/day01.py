import sys
import bisect
from inputs import load_input


def part1() -> int:
    input_strings = load_input("./input/day01")
    return get_distance(input_strings)


def part2() -> int:
    input_strings = load_input("./input/day01")
    return get_similarity_score(input_strings)


def parse_to_lists(input_strings: list[str]) -> tuple[list[int], list[int]]:
    list1 = []
    list2 = []
    for each in input_strings:
        split = each.split("   ")
        bisect.insort(list1, int(split[0]))
        bisect.insort(list2, int(split[1]))
    return (list1, list2)


def get_distance(input_strings: list[str]) -> int:
    (list1, list2) = parse_to_lists(input_strings)

    differences = map(lambda x, y: abs(x - y), list1, list2)

    return sum(differences)


def get_similarity_score(input_strings: list[str]) -> int:
    (list1, list2) = parse_to_lists(input_strings)

    similarity_mapping = {}
    for each in list1:
        similarity_mapping[each] = 0
    for each in list2:
        existing_count = similarity_mapping.get(each)
        count = 1
        if existing_count is not None:
            count += existing_count

        similarity_mapping[each] = count

    answer = 0
    for key in list1:
        answer += key * similarity_mapping[key]

    return answer


if __name__ == "__main__":
    print(globals()[sys.argv[1]]())
