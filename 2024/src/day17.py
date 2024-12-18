import sys
from inputs import load_input
from math import floor, pow


class Program:
    def __init__(self, a: int, b: int, c: int, instructions: list[int]) -> None:
        self.a = a
        self.b = b
        self.c = c
        self.instructions = instructions


def part1() -> str:
    input_strings = load_input("./input/day17")
    program = parse_program(input_strings)
    out = execute_program(program)
    print(f"Registers {program.a}, {program.b}, {program.c}")
    return out.replace(",", "")


def part2() -> int:
    input_strings = load_input("./input/day17")
    pass


def execute_program(program: Program) -> str:
    pointer = 0
    out = ""
    instructions = program.instructions
    while pointer < len(instructions):
        opcode, literal_operand = instructions[pointer], instructions[pointer + 1]
        if opcode not in {1, 3, 4}:
            combo_operand = translate_operand(literal_operand, program)
        else:
            combo_operand = -1
        match opcode:
            case 0:
                program.a = floor(program.a / pow(2, combo_operand))
                pointer += 2
            case 1:
                program.b = program.b ^ literal_operand
                pointer += 2
            case 2:
                program.b = combo_operand % 8
                pointer += 2
            case 3:
                if program.a != 0:
                    pointer = literal_operand
                else:
                    pointer += 2
            case 4:
                program.b = program.b ^ program.c
                pointer += 2
            case 5:
                print(combo_operand % 8)
                for each in str(combo_operand % 8):
                    out += each + ","
                pointer += 2
            case 6:
                program.b = floor(program.a / pow(2, combo_operand))
                pointer += 2
            case 7:
                program.c = floor(program.a / pow(2, combo_operand))
                pointer += 2
    return out


def translate_operand(operand: int, program: Program) -> int:
    if operand < 4:
        return operand
    match operand:
        case 4:
            return program.a
        case 5:
            return program.b
        case 6:
            return program.c
        case _:
            raise RuntimeError(f"Operand not supported {operand}")


def parse_program(input_strings: list[str]) -> Program:
    a = 0
    b = 0
    c = 0
    instructions = []
    for line in input_strings:
        if "A:" in line:
            a = int(line.replace("Register A: ", "").strip())
        elif "B:" in line:
            b = int(line.replace("Register B: ", "").strip())
        elif "C:" in line:
            c = int(line.replace("Register C: ", "").strip())
        elif "Program:" in line:
            instructions = list(
                map(int, line.replace("Program: ", "").strip().split(","))
            )

    return Program(a, b, c, instructions)


if __name__ == "__main__":
    print(globals()[sys.argv[1]]())
