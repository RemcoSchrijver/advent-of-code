use std::collections::HashMap;
use std::collections::VecDeque;

use ec_stories_2024::Part;
use ec_stories_2024::load_input;
use ec_stories_2024::parse_part;

fn main() {
    let part = parse_part().unwrap();
    let day = "day01";
    match part {
        Part::Part1 => part1(load_input(day, part)),
        Part::Part2 => part2(load_input(day, part)),
        Part::Part3 => part3(load_input(day, part)),
    }
}

fn part1(input: Vec<String>) {
    let result = input
        .iter()
        .map(|line| parse_line(line))
        .map(|eni_formulas| {
            eni_formulas.iter().fold(0u128, |acc, eni_formula| {
                acc + calculate_eni_formula(eni_formula)
            })
        });

    println!("Found max value: {}", result.max().expect("No value found"));
}

fn part2(input: Vec<String>) {
    let result = input
        .iter()
        .map(|line| parse_line(line))
        .map(|eni_formulas| {
            eni_formulas.iter().fold(0u128, |acc, eni_formula| {
                acc + calcculate_eni_formula_last_five(eni_formula)
            })
        });

    println!("Found max value: {}", result.max().expect("No value found"));
}

fn part3(input: Vec<String>) {
    let result = input
        .iter()
        .map(|line| parse_line(line))
        .map(|eni_formulas| {
            eni_formulas.iter().fold(0u128, |acc, eni_formula| {
                acc + calcculate_eni_formula_sums(eni_formula)
            })
        });

    println!("Found max value: {}", result.max().expect("No value found"));
}

#[derive(Debug)]
struct EniFormula {
    base: u128,
    exponent: u128,
    modulo: u128,
}

fn calculate_eni_formula(eni_formula: &EniFormula) -> u128 {
    let mut accumalator: Vec<u128> = Vec::new();
    let mut score = 1;

    for _ in 0..eni_formula.exponent {
        score = (score * eni_formula.base) % eni_formula.modulo;
        accumalator.push(score);
    }
    accumalator.reverse();

    return accumalator
        .iter()
        .fold(String::new(), |acc, value| format!("{}{}", acc, value))
        .parse::<u128>()
        .expect("Parsing accumalator failed");
}

fn calcculate_eni_formula_last_five(eni_formula: &EniFormula) -> u128 {
    let mut accumalator: VecDeque<u128> = VecDeque::new();
    let mut score = 1;
    let mut skipping = false;

    let mut visited: HashMap<VecDeque<u128>, u128> = HashMap::new();

    let mut i = 0;

    while i < eni_formula.exponent {
        score = (score * eni_formula.base) % eni_formula.modulo;
        accumalator.push_front(score);
        if accumalator.len() > 5 {
            accumalator.pop_back();
        }
        if !skipping && visited.contains_key(&accumalator) {

            let loop_length = i - visited.get(&accumalator).unwrap();
            let skip_index = i + loop_length * ((eni_formula.exponent - i - 1) / loop_length);

            i = skip_index;
            skipping = true;
            if i == skip_index {
                i += 1;
            }
        } else {
            if !skipping {
                visited.insert(accumalator.clone(), i);
            }
            i += 1;
        }
    }
    let result = accumalator
        .into_iter()
        .fold(String::new(), |acc, value| format!("{}{}", acc, value))
        .parse::<u128>()
        .expect("Something went wrong grabbing 5 chars");
    return result;
}

fn calcculate_eni_formula_sums(eni_formula: &EniFormula) -> u128 {
    let mut sum = 0;
    let mut score = 1;
    let mut skipping = false;

    let mut visited: HashMap<u128, u128> = HashMap::new();
    let mut historic_sums: HashMap<u128, u128> = HashMap::new();

    let mut i = 0;

    // Adding: 4
    // Adding: 16
    // Adding: 64
    // Adding: 16
    // Existing sum: 100
    // jumping to: 13999
    // number of loops being skipped 6998, a single loop is worth: 84, loop length: 2
    // result for EniFormula { base: 4, exponent: 14000, modulo: 120 } = 587932
    //
    // So we wanna go to 14 000, we're at i = 3 when we discover loop, 

    while i < eni_formula.exponent {
        score = (score * eni_formula.base) % eni_formula.modulo;
        sum += score;
        historic_sums.insert(i, sum);
        if !skipping && visited.contains_key(&score) {
            let loop_length = i - visited.get(&score).unwrap();
            let skip_loops = (eni_formula.exponent - i - 1) / loop_length;
            let single_loop_sum = historic_sums.get(&(i)).unwrap() - historic_sums.get(&(i - loop_length)).unwrap();

            sum += single_loop_sum * skip_loops;
            let skip_index = i + loop_length * skip_loops;
            i = skip_index;
            skipping = true;
            if i == skip_index {
                i += 1;
            }
        } else {
            if !skipping {
                visited.insert(score, i);
            }
            i += 1;
        }
    }
    let result = sum;
    return result;
}

fn parse_line(line: &str) -> Vec<EniFormula> {
    let mut result: Vec<EniFormula> = vec![];

    let splits: Vec<&str> = line.split(" ").collect();
    let modulo = parse_num_from_field(splits.last().copied());
    for i in 0..3 {
        result.push(EniFormula {
            base: parse_num_from_field(splits.get(i).copied()),
            exponent: parse_num_from_field(splits.get(i + 3).copied()),
            modulo: modulo,
        });
    }
    return result;
}

fn parse_num_from_field(field: Option<&str>) -> u128 {
    field
        .and_then(|actual| actual.split("=").last())
        .and_then(|value| value.parse::<u128>().ok())
        .expect("Failed to parse value")
}
