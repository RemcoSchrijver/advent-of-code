use ec_quest_2026::Part;
use ec_quest_2026::{load_input, parse_part};

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
    let total_score = solve_parts_one_and_two(input);

    println!(
        "Total combined vote score for ISO votes is: {}",
        total_score
    );
}

fn part2(input: Vec<String>) {
    let total_score = solve_parts_one_and_two(input);

    println!(
        "Total combined vote score for ISO votes is: {}",
        total_score
    );
}

fn part3(input: Vec<String>) {
    let total_scores = input
        .into_iter()
        .map(|line| IsoVote::parse_from_string(&line))
        .filter(|vote| !smaller_iso_vote_exists(vote))
        .map(|vote| calculate_iso_vote_value(&vote));

    println!(
        "Total combined vote score for ISO votes is: {}",
        total_scores.sum::<u128>()
    );
}

fn solve_parts_one_and_two(input: Vec<String>) -> u128 {
    let total_scores = input
        .into_iter()
        .map(|line| calculate_iso_vote_value(&IsoVote::parse_from_string(&line)));

    return total_scores.sum();
}

fn calculate_iso_vote_value(iso_vote: &IsoVote) -> u128 {
    let mut accumalator = 0;
    let vote_length = iso_vote.base.len();
    for i in 0..vote_length {
        accumalator += iso_vote.vote[i] * (iso_vote.base[i].pow((vote_length - 1 - i) as u32))
    }
    return accumalator;
}

fn smaller_iso_vote_exists(iso_vote: &IsoVote) -> bool {
    let existing_value = calculate_iso_vote_value(iso_vote);
    let mut shrinking_vote = convert_digit_vec_to_number(&iso_vote.vote);

    loop {
        // Consider the whole thing as a number and break it up again after
        shrinking_vote -= 1;
        if shrinking_vote == 0 {
            return false;
        } else {
            if calculate_iso_vote_value(&IsoVote::build_iso_vote(
                iso_vote.base.clone(),
                convert_string_to_digits(&shrinking_vote.to_string()),
            )) == existing_value
            {
                return true;
            }
        }
    }
}

#[derive(Debug)]
struct IsoVote {
    base: Vec<u128>,
    vote: Vec<u128>,
}

const PREFIX: &str = "ISO2-";
impl IsoVote {
    fn parse_from_string(value: &str) -> IsoVote {
        let base_vote_split: Vec<Vec<u128>> = value
            .strip_prefix(PREFIX)
            .expect("Couldn't strip prefix")
            .split(" ")
            .map(|digits| convert_string_to_digits(digits))
            .collect();

        return Self::build_iso_vote(base_vote_split[0].clone(), base_vote_split[1].clone());
    }

    fn build_iso_vote(base: Vec<u128>, vote: Vec<u128>) -> IsoVote {
        let base_length = base.len();
        let vote_length = vote.len();
        if base_length > vote_length {
            return IsoVote {
                base: base.into_iter().skip(base_length - vote_length).collect(),
                vote: vote,
            };
        }
        if base_length < vote_length {
            let difference = vote_length - base_length;
            let old_base = base;
            let mut new_base = old_base.clone();
            for i in 0..difference {
                let insert_val = old_base[base_length - 1 - (i % base_length)];
                new_base.insert(0, insert_val);
            }
            return IsoVote {
                base: new_base,
                vote: vote,
            };
        }
        return IsoVote {
            base: base,
            vote: vote,
        };
    }
}

fn convert_string_to_digits(digits: &str) -> Vec<u128> {
    digits
        .chars()
        .map(|c| c.to_digit(10).expect("Couldn't convert char to digit") as u128)
        .collect()
}

fn convert_digit_vec_to_number(digits: &Vec<u128>) -> u128 {
    digits
        .iter()
        .map(|digit| digit.to_string())
        .collect::<String>()
        .parse::<u128>()
        .expect("Couldn't convert vote to single number")
}
