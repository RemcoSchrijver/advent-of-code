use ec_stories_no_1::{Part, load_input, parse_num_from_field_with_equals_split, parse_part};

const DAY: &str = "day03";

fn main() {
    let part = parse_part().unwrap();
    match part {
        Part::Part1 => part1(load_input(DAY, part)),
        Part::Part2 => part2(load_input(DAY, part)),
        Part::Part3 => part3(load_input(DAY, part)),
    }
}

fn part1(input: Vec<String>) {
    let snails = parse_snails(input);

    let moved_snails: Vec<Snail> = snails
        .iter()
        .map(|snail| snail.position_snail_after_n_days(100))
        .collect();

    let mut final_score = 0;
    println!("Moved snails end: {:?}", moved_snails);
    moved_snails
        .iter()
        .for_each(|snail| final_score = final_score + snail.calculate_formula());

    println!("Final score {}", final_score);
}

fn part2(_input: Vec<String>) {
    println!("Not implemented yet");
}

fn part3(_input: Vec<String>) {
    println!("Not implemented yet");
}

#[derive(Debug)]
struct Snail {
    x: u128,
    y: u128,
}

impl Snail {
    fn position_snail_after_n_days(&self, n: u128) -> Snail {
        let length_of_path = self.x - 1 + self.y;
        let moves = n % length_of_path;

        let mut new_x = self.x;
        let mut new_y = self.y;

        for _ in 0..moves {
            if new_y == 1 {
                new_y = new_x;
                new_x = 1;
            } else {
                new_x += 1;
                new_y -= 1;
            }
        }
        return Snail { x: new_x, y: new_y };
    }

    fn calculate_formula(&self) -> u128 {
        self.x + self.y * 100
    }
}

fn parse_line(line: &str) -> Snail {
    let x_and_y: Vec<&str> = line.split(" ").collect();
    Snail {
        x: parse_num_from_field_with_equals_split(x_and_y.get(0).copied()),
        y: parse_num_from_field_with_equals_split(x_and_y.get(1).copied()),
    }
}

fn parse_snails(lines: Vec<String>) -> Vec<Snail> {
    lines.iter().map(|line| parse_line(line)).collect()
}
