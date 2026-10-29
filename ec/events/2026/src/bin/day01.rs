use ec_quest_2026::Part;
use ec_quest_2026::{load_input, parse_part};

const DAY: &str = "day01";

fn main() {
    let part = parse_part().unwrap();
    match part {
        Part::Part1 => part1(load_input(DAY, part)),
        Part::Part2 => part2(load_input(DAY, part)),
        Part::Part3 => part3(load_input(DAY, part)),
    }
}

fn part1(input: Vec<String>) {
    let pinecones_found = parse_input(input);
    
    let pinecones_needed: u128 = pinecones_found.into_iter().map(|pinecones| closest_upper_squared_number(pinecones) - pinecones).sum();
    println!("We need {} pinecones in total", pinecones_needed);
}

fn part2(input: Vec<String>) {
    let pinecones_found = parse_input(input);
    
    let pinecones_needed: u128 = pinecones_found.into_iter().map(|pinecones| closest_grid_number(pinecones) - pinecones).sum();
    println!("We need {} pinecones in total", pinecones_needed);
}

fn part3(input: Vec<String>) {
    let pinecones_found = parse_input(input);
    
    let pinecones_needed: u128 = pinecones_found.into_iter().map(|pinecones| closest_grid_with_empty_cell(pinecones) - pinecones).sum();
    println!("We need {} pinecones in total", pinecones_needed);
}

fn parse_input(input: Vec<String>) -> Vec<u128> {
    if input.is_empty() || input.len() > 1 {
        panic!("Invalid input")
    }
    return input
        .first()
        .expect("Somehow there is no first line")
        .split(",")
        .map(|value| value.parse::<u128>().expect("Not a number"))
        .collect();
}

fn closest_upper_squared_number(num: u128) -> u128 {
    let rounded_down_square_root = num.isqrt();
    if rounded_down_square_root * rounded_down_square_root == num {
        return num
    } else {
        return (rounded_down_square_root + 1).pow(2);
    }
}

const MIN_ROW_LENGTH: u128 = 3;
fn closest_grid_number(num: u128) -> u128 {
    let mut base_root = (num.isqrt() + 1).max(MIN_ROW_LENGTH);
    let mut total_pinecones_for_squared;
    loop {
        total_pinecones_for_squared = base_root.pow(2) - (base_root - 2).pow(2);
        if total_pinecones_for_squared < num {
            base_root = base_root + 1;
        } else {
            break;
        }
    }
    return total_pinecones_for_squared;
}

fn closest_grid_with_empty_cell(num: u128) -> u128 {
    let mut base_root = (num.isqrt() + 1).max(MIN_ROW_LENGTH);
    if base_root % 2 == 0 {
        base_root += 1;
    }

    let mut total_pinecones_for_squared;
    loop {
        total_pinecones_for_squared = base_root.pow(2) - 1;
        if total_pinecones_for_squared < num {
            base_root = base_root + 1;
        } else {
            break;
        }
    }
    return total_pinecones_for_squared;
}
