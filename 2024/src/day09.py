import sys
from inputs import load_input


def part1() -> int:
    input_strings = load_input("./input/day09")
    block_map = generate_block_map(input_strings[0])
    collapsed_block_map = collapse_block_map(block_map)
    return calculate_check_sum(collapsed_block_map)


def part2() -> int:
    input_strings = load_input("./input/day09")
    block_map = generate_block_map(input_strings[0])
    collapsed_block_map = collapse_block_map_files_naive(block_map)
    return calculate_check_sum(collapsed_block_map)


def generate_block_map(input: str) -> list[int]:
    input = input.strip()
    result = []
    for index, char in enumerate(input):
        if index % 2 == 0:
            for _ in range(int(char)):
                result.append(int(index / 2))
        else:
            for _ in range(int(char)):
                result.append(-1)

    return result


def collapse_block_map(block_map: list[int]) -> list[int]:
    last_index = len(block_map) - 1
    for i, each in enumerate(block_map):
        if i >= last_index:
            return block_map
        if each == -1:
            while block_map[last_index] == -1:
                last_index += -1
            if i >= last_index:
                return block_map
            block_map[i] = block_map[last_index]
            block_map[last_index] = -1
            last_index += -1
    return block_map


def calculate_check_sum(block_map: list[int]) -> int:
    check_sum = 0
    for i, each in enumerate(block_map):
        if each != -1:
            check_sum += i * each
    return check_sum


def collapse_block_map_files_naive(block_map: list[int]) -> list[int]:
    value = None
    start_index = -1
    end_index = -1
    shifted = set()
    for i in range(len(block_map) - 1, -1, -1):
        if block_map[i] != -1 and value == None:
            value = block_map[i]
            end_index = i
        if block_map[i] != value and value != None:
            if value not in shifted:
                start_index = i + 1
                length = end_index - start_index + 1
                find_free_block(block_map, length, start_index)
                shifted.add(value)
            if block_map[i] != -1:
                value = block_map[i]
                end_index = i
            else:
                value = None
    return block_map


def find_free_block(block_map: list[int], length, current_index) -> None:
    counter = 0
    for i in range(len(block_map)):
        if block_map[i] == -1:
            counter += 1
            if counter == length and i < current_index:
                for j in range(length):
                    block_map[i - j] = block_map[current_index + j]
                    block_map[current_index + j] = -1
                return
            if i > current_index:

                return
        else:
            counter = 0


if __name__ == "__main__":
    print(globals()[sys.argv[1]]())
