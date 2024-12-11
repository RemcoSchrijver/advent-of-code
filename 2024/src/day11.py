import sys
from inputs import load_input


def part1() -> int:
    input_strings = load_input("./input/day11")
    initial_stones = load_stones(input_strings[0])
    stones = simulate_stone_transformations(initial_stones, 25)
    return len(stones)


def part2() -> int:
    input_strings = load_input("./input/day11")
    initial_stones = load_stones(input_strings[0])
    stones = calculate_amount_of_stones(initial_stones, 75, {})
    return stones


def load_stones(input_string: str) -> list[int]:
    return list(map(int, input_string.split(" ")))


def simulate_stone_transformations(stones: list[int], count: int) -> list[int]:
    if count == 0:
        return stones
    else:
        count -= 1
        result = []
        for stone in stones:
            new_stones = transform_stone(stone)
            result += new_stones
        return simulate_stone_transformations(result, count)


def calculate_amount_of_stones(
    stones: list[int], count: int, cache: dict[tuple[int, int], int]
) -> int:
    if count == 0:
        return len(stones)
    result = 0
    for stone in stones:
        if (stone, count) in cache:
            result += cache[(stone, count)]
        else:
            new_stones = transform_stone(stone)
            answer = calculate_amount_of_stones(new_stones, count - 1, cache)
            cache[(stone, count)] = answer
            result += answer
    return result


def transform_stone(stone: int) -> list[int]:
    result = []
    if stone == 0:
        result.append(1)
    elif len(str(stone)) % 2 == 0:
        stone_str = str(stone)
        len_str = len(stone_str)
        stone1, stone2 = stone_str[: len_str // 2], stone_str[len_str // 2 :]
        result.append(int(stone1))
        result.append(int(stone2))
    else:
        result.append(stone * 2024)
    return result


if __name__ == "__main__":
    print(globals()[sys.argv[1]]())
