import sys
from inputs import load_input


def part1() -> int:
    input_strings = load_input("./input/day04")
    board = create_board(input_strings)
    sum = 0
    for y in range(len(board)):
        row = board[y]
        for x in range(len(row)):
            char = row[x]
            if char == "X":
                sum += find_xmas(board, x, y)
    return sum


def part2() -> int:
    input_strings = load_input("./input/day04")
    board = create_board(input_strings)
    sum = 0
    for y in range(len(board)):
        row = board[y]
        for x in range(len(row)):
            char = row[x]
            if char == "A":
                sum += find_cross_mas(board, x, y)
    return sum


def create_board(input_strings: list[str]) -> list[list[str]]:
    vertical = []
    for input_string in input_strings:
        horizontal = list(input_string.strip())
        vertical.append(horizontal)
    return vertical


def find_xmas(board: list[list[str]], x: int, y: int) -> int:
    max_y = len(board)
    max_x = len(board[y])
    row = board[y]
    sum = 0

    # left:
    if x - 3 >= 0 and row[x - 1] == "M" and row[x - 2] == "A" and row[x - 3] == "S":
        sum += 1
    # right
    if x + 3 < max_x and row[x + 1] == "M" and row[x + 2] == "A" and row[x + 3] == "S":
        sum += 1
    # top
    if (
        y - 3 >= 0
        and board[y - 1][x] == "M"
        and board[y - 2][x] == "A"
        and board[y - 3][x] == "S"
    ):
        sum += 1
    # bottom
    if (
        y + 3 < max_y
        and board[y + 1][x] == "M"
        and board[y + 2][x] == "A"
        and board[y + 3][x] == "S"
    ):
        sum += 1
    # left-top-diagonal
    if (
        y - 3 >= 0
        and x - 3 >= 0
        and board[y - 1][x - 1] == "M"
        and board[y - 2][x - 2] == "A"
        and board[y - 3][x - 3] == "S"
    ):
        sum += 1
    # right-top-diagonal
    if (
        y - 3 >= 0
        and x + 3 < max_x
        and board[y - 1][x + 1] == "M"
        and board[y - 2][x + 2] == "A"
        and board[y - 3][x + 3] == "S"
    ):
        sum += 1
    # left-bottom-diagonal
    if (
        y + 3 < max_y
        and x - 3 >= 0
        and board[y + 1][x - 1] == "M"
        and board[y + 2][x - 2] == "A"
        and board[y + 3][x - 3] == "S"
    ):
        sum += 1
    # right-bottom-diagonal
    if (
        y + 3 < max_y
        and x + 3 < max_x
        and board[y + 1][x + 1] == "M"
        and board[y + 2][x + 2] == "A"
        and board[y + 3][x + 3] == "S"
    ):
        sum += 1
    return sum


def find_cross_mas(board: list[list[str]], x: int, y: int) -> int:
    max_y = len(board)
    max_x = len(board[y])

    if x - 1 >= 0 and x + 1 < max_x and y - 1 >= 0 and y + 1 < max_y:
        left_diagonal = board[y - 1][x - 1] + "A" + board[y + 1][x + 1]
        right_diagonal = board[y + 1][x - 1] + "A" + board[y - 1][x + 1]
        if (left_diagonal == "MAS" or left_diagonal[::-1] == "MAS") and (
            right_diagonal == "MAS" or right_diagonal[::-1] == "MAS"
        ):
            return 1

    return 0


if __name__ == "__main__":
    print(globals()[sys.argv[1]]())
