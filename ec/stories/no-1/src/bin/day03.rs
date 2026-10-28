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

fn part2(input: Vec<String>) {
    let snails = parse_snails(input);

    let alignment_day = find_day_snails_align(snails);

    println!("first alignment day: {}", alignment_day);
}

fn part3(input: Vec<String>) {
    let snails = parse_snails(input);

    let alignment_day = find_day_snails_align_fast(snails);

    println!("first alignment day: {}", alignment_day);
}

#[derive(Debug)]
struct Snail {
    x: u128,
    y: u128,
}

impl Snail {
    fn position_snail_after_n_days(&self, n: u128) -> Snail {
        let length_of_path = self.path_length();
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

    fn path_length(&self) -> u128 {
        self.x - 1 + self.y
    }

    fn days_from_golden_line(&self) -> u128 {
        let length_of_path = self.path_length();

        length_of_path - self.x
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

fn find_day_snails_align(positions: Vec<Snail>) -> u128 {
    let longest_snail_and_cycle = positions
        .iter()
        .map(|snail| (snail, snail.path_length()))
        .max_by_key(|x| x.1)
        .unwrap();
    let initial_days_to_move = longest_snail_and_cycle.0.days_from_golden_line();

    let mut snail_positions: Vec<Snail> = positions
        .iter()
        .map(|snail| snail.position_snail_after_n_days(initial_days_to_move))
        .collect();

    let mut current_day = initial_days_to_move;
    loop {
        if snail_positions.iter().all(|snail| snail.y == 1) {
            break;
        } else {
            current_day += longest_snail_and_cycle.1;
            snail_positions = snail_positions
                .iter()
                .map(|snail| snail.position_snail_after_n_days(longest_snail_and_cycle.1))
                .collect();
        }
    }

    return current_day;
}

fn find_day_snails_align_fast(positions: Vec<Snail>) -> u128 {
    if positions.is_empty() {
        return 0;
    }

    let mut current_day = positions[0].y - 1;
    let mut increase = positions[0].path_length();
    
    for snail in positions.iter().skip(1) {
        let target_end = snail.y - 1;
        let modulo_value = snail.path_length();

        while current_day % modulo_value != target_end {
            current_day += increase;
        }
        increase = least_common_multiple(increase, modulo_value)
    }

    return current_day;
}

fn greatest_common_divisor(mut a: u128, mut b: u128) -> u128 {
  assert!(a != 0 && b != 0);
  while b != 0 {
    if b < a {
      std::mem::swap(&mut b, &mut a);
    }
    b %= a;
  }
  a
}


fn least_common_multiple(a: u128, b: u128) -> u128 {
    if a == 0 || b == 0 {
        return 0;
    } else {
        return (a * b) / greatest_common_divisor(a, b);
    }
}
