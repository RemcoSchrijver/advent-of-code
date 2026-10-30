use std::collections::HashSet;

use ec_quest_2026::Part;
use ec_quest_2026::{load_input, parse_part};

const DAY: &str = "day02";

fn main() {
    let part = parse_part().unwrap();
    match part {
        Part::Part1 => part1(load_input(DAY, part)),
        Part::Part2 => part2(load_input(DAY, part)),
        Part::Part3 => part3(load_input(DAY, part)),
    }
}

fn part1(input: Vec<String>) {
    let answer = solve_parts(input);
    println!("Total checksum of the alloy comparison is {}", answer);
}

fn part2(input: Vec<String>) {
    let answer = solve_parts(input);
    println!("Total checksum of the alloy comparison is {}", answer);
}

fn part3(input: Vec<String>) {
    let answer = solve_parts(input);
    println!("Total checksum of the alloy comparison is {}", answer);
}

fn solve_parts(input: Vec<String>) -> u128 {
    let checksums: Vec<u128> = input
        .into_iter()
        .map(|line| parse_line(line))
        .map(|comparisons| calculate_checksum(comparisons))
        .collect();

    return checksums.into_iter().sum();
}

fn parse_line(line: String) -> Vec<AlloyComparison> {
    return line
        .split(" ")
        .map(|value| AlloyComparison::construct_from_string(value))
        .collect();
}

fn calculate_checksum(alloy_comparisons: Vec<AlloyComparison>) -> u128 {
    let alloys = all_alloy_numbers(&alloy_comparisons);
    let sorted_alloys = sort_list_by_alloys(alloys, &alloy_comparisons, 0, false);
    return sorted_alloys
        .iter()
        .rev()
        .map(|value| value.to_string().chars().collect::<Vec<char>>())
        .flatten()
        .collect::<String>()
        .parse::<u128>()
        .expect("Failed to flatten the sorted alloys");
}

fn all_alloy_numbers(alloy_comparisons: &Vec<AlloyComparison>) -> Vec<u128> {
    let mut alloys = HashSet::new();
    alloy_comparisons.iter().for_each(|alloy_comparison| {
        alloys.insert(alloy_comparison.left);
        alloys.insert(alloy_comparison.right);
    });

    return alloys.into_iter().collect();
}

// This is really dumb but oh well
fn sort_list_by_alloys(
    mut alloys: Vec<u128>,
    alloy_comparisons: &Vec<AlloyComparison>,
    mut index: usize,
    mut mutated_last_cycle: bool,
) -> Vec<u128> {
    if index >= alloy_comparisons.len() {
        if mutated_last_cycle {
            index = 0;
            mutated_last_cycle = false;
        } else {
            return alloys;
        }
    }
    let alloy_comparison = &alloy_comparisons[index];
    let left_index = alloys
        .iter()
        .position(|n| *n == alloy_comparison.left)
        .expect("Couldn't find left value");
    let right_index = alloys
        .iter()
        .position(|n| *n == alloy_comparison.right)
        .expect("Couldn't find right value");

    match alloy_comparison.comperator {
        Comperator::LT => {
            if left_index > right_index {
                mutated_last_cycle = true;
                alloys.swap(left_index, right_index);
            }
        }
        Comperator::GT => {
            if left_index < right_index {
                mutated_last_cycle = true;
                alloys.swap(left_index, right_index);
            }
        }
    }

    return sort_list_by_alloys(alloys, alloy_comparisons, index + 1, mutated_last_cycle);
}

#[derive(Debug)]
enum Comperator {
    LT,
    GT,
}

#[derive(Debug)]
struct AlloyComparison {
    left: u128,
    comperator: Comperator,
    right: u128,
}

impl AlloyComparison {
    fn construct_from_string(value: &str) -> AlloyComparison {
        let comperator;
        let number_values: Vec<&str>;
        if value.contains("<") {
            comperator = Comperator::LT;
            number_values = value.split("<").collect();
        } else if value.contains(">") {
            comperator = Comperator::GT;
            number_values = value.split(">").collect();
        } else {
            panic!("No valid comperator found");
        }

        return AlloyComparison {
            left: number_values
                .first()
                .and_then(|value| value.parse::<u128>().ok())
                .expect("Failed to parse left number"),
            comperator: comperator,
            right: number_values
                .last()
                .and_then(|value| value.parse::<u128>().ok())
                .expect("Failed to parse left number"),
        };
    }
}
