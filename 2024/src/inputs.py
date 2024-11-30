def load_input(path: str) -> list[str]:
    with open(path, encoding="utf8") as input_file:
        return input_file.readlines()
