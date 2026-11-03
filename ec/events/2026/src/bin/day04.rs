use std::collections::HashMap;

use ec_quest_2026::Part;
use ec_quest_2026::{load_input, parse_part};

const DAY: &str = "day04";

fn main() {
    let part = parse_part().unwrap();
    match part {
        Part::Part1 => part1(load_input(DAY, part)),
        Part::Part2 => part2(load_input(DAY, part)),
        Part::Part3 => part3(load_input(DAY, part)),
    }
}

fn part1(input: Vec<String>) {
    let quest_results: Vec<QuestResult> = input
        .iter()
        .map(|line| QuestResult::parse_quest_result(line))
        .collect();

    let mut time_per_contestant: HashMap<u128, u128> = HashMap::new();

    quest_results.iter().for_each(|quest_result| {
        let id = &quest_result.id;
        if time_per_contestant.contains_key(id) {
            time_per_contestant.insert(*id, time_per_contestant[id] + quest_result.time);
        } else {
            time_per_contestant.insert(*id, quest_result.time);
        }
    });

    let mut final_times_ordered = time_per_contestant.into_values().collect::<Vec<u128>>();
    final_times_ordered.sort_unstable();

    let checksum = final_times_ordered
        .iter()
        .enumerate()
        .fold(0, |acc, (index, val)| acc + (index + 1) as u128 * val);

    println!(
        "From all the Quests we got the total checksum: {}",
        checksum
    );
}

fn part2(input: Vec<String>) {
    let quest_results: Vec<QuestResult> = input
        .iter()
        .map(|line| QuestResult::parse_quest_result(line))
        .collect();

    let mut contestant_results = collect_contestant_results(&quest_results);

    let mut scores: Vec<(u128, u128)> = contestant_results
        .values_mut()
        .map(|contestant| contestant.get_parts_completed_and_time())
        .collect();
    scores.sort_by_key(|&(first, second)| (u128::MAX - first, second));

    let checksum = scores
        .iter()
        .enumerate()
        .fold(0, |acc, (index, (_parts_completed, time))| {
            acc + (index + 1) as u128 * time
        });

    println!(
        "From all the contestants ranked by parts and then time we got the total checksum: {}",
        checksum
    );
}

fn part3(input: Vec<String>) {
    let quest_results: Vec<QuestResult> = input
        .iter()
        .map(|line| QuestResult::parse_quest_result(line))
        .collect();

    let contestant_results = collect_contestant_results(&quest_results);
    let scores_per_contestant_and_quest = calculate_scores_per_quest(contestant_results);
    let scores_per_contestant = calculate_scores_per_contestant(scores_per_contestant_and_quest);

    let mut scores: Vec<&u128> = scores_per_contestant.values().collect();
    scores.sort();
    scores.reverse();

    let mut checksum = 0;
    let mut previous_score = 0;
    let mut previous_multiplier = 0;
    let mut multiplier = 1;

    for score in scores {
        let score_multiplier;
        if previous_score == *score {
            score_multiplier = previous_multiplier;
        } else {
            score_multiplier = multiplier;
        }

        checksum += score_multiplier * score;
        // Lower score if there is no tie
        multiplier += 1;
        previous_score = *score;
        previous_multiplier = score_multiplier;
    }

    println!(
        "From all the contestants ranked by points we got the total checksum: {}",
        checksum
    );
}

fn collect_contestant_results(results: &Vec<QuestResult>) -> HashMap<u128, Contestant> {
    let mut contestants: HashMap<u128, Contestant> = HashMap::new();

    results.iter().for_each(|quest_result| {
        let id = quest_result.id;
        if !contestants.contains_key(&id) {
            let contestant = Contestant {
                quests: HashMap::new(),
            };
            contestants.insert(id, contestant);
        }
        let contestant = contestants.get_mut(&quest_result.id).unwrap();
        contestant.add_quest_result(quest_result);
    });

    return contestants;
}

fn calculate_scores_per_contestant(
    scores_per_contestant_and_quest: HashMap<(u128, u128), u128>,
) -> HashMap<u128, u128> {
    let mut result = HashMap::new();

    for ((contestant_id, _quest), score) in scores_per_contestant_and_quest {
        result
            .entry(contestant_id)
            .and_modify(|existing_score| *existing_score += score)
            .or_insert_with(|| score);
    }

    return result;
}

// returns: <(contestant ID, quest ID), score>
fn calculate_scores_per_quest(
    contestant_results: HashMap<u128, Contestant>,
) -> HashMap<(u128, u128), u128> {
    // (quest, part) [(contestant_id ,time)]
    let mut quests_and_parts_with_times_and_contestants: HashMap<(u128, u8), Vec<(u128, u128)>> =
        HashMap::new();

    for (contestant_id, contestant) in contestant_results {
        for (quest_id, quest) in contestant.quests {
            let parts = quest.parts.clone();
            for (part, time) in parts {
                let final_time;
                if part == 1 {
                    final_time = time;
                } else if part == 2 {
                    final_time = quest.parts[&1] + time;
                } else {
                    final_time = quest.parts[&1] + quest.parts[&2] + time;
                }
                quests_and_parts_with_times_and_contestants
                    .entry((quest_id, part))
                    .or_insert_with(|| Vec::new());
                let contestant_vec = quests_and_parts_with_times_and_contestants
                    .get_mut(&(quest_id, part))
                    .unwrap();
                contestant_vec.push((contestant_id, final_time));
            }
        }
    }

    let mut quest_results = HashMap::new();

    for ((quest, part), contestant_ids_and_times) in quests_and_parts_with_times_and_contestants {
        quest_results.insert(
            (quest, part),
            calculate_score_for_quest_part(&contestant_ids_and_times, part),
        );
    }

    let mut result = HashMap::new();

    for ((quest, _part), scores_per_part) in quest_results {
        for (contestant, score) in scores_per_part {
            result
                .entry((contestant, quest))
                .and_modify(|existing_score| *existing_score += score)
                .or_insert_with(|| score);
        }
    }
    return result;
}

// returns: <contestant ID, score>
fn calculate_score_for_quest_part(
    contestant_ids_and_times: &Vec<(u128, u128)>,
    part: u8,
) -> HashMap<u128, u128> {
    let mut sorted_contestant_ids_and_times = contestant_ids_and_times.clone();
    sorted_contestant_ids_and_times.sort_by_key(|&(_contestant_id, time)| time);

    let mut score_count;
    match part {
        1 => score_count = 50,
        2 => score_count = 100,
        3 => score_count = 150,
        _ => panic!("Invalid part"),
    }
    let mut previous_time = 0;
    let mut previous_score = 0;
    let mut results = HashMap::new();
    for (contestant_id, time) in sorted_contestant_ids_and_times {
        let contestant_score;
        if previous_time == time {
            contestant_score = previous_score;
        } else {
            contestant_score = score_count;
        }

        results.insert(contestant_id, contestant_score);
        if score_count > 0 {
            score_count = score_count - 1;
        }
        previous_time = time;
        previous_score = contestant_score;
    }

    return results;
}

#[derive(Debug)]
struct QuestResult {
    id: u128,
    quest: u128,
    part: u8,
    time: u128,
}

impl QuestResult {
    fn parse_quest_result(line: &str) -> QuestResult {
        let splits: Vec<&str> = line.split(" ").collect();
        return QuestResult {
            id: splits[0].split("=").collect::<Vec<&str>>()[1]
                .parse::<u128>()
                .expect("Couldn't parse id"),
            quest: splits[1].split("=").collect::<Vec<&str>>()[1]
                .parse::<u128>()
                .expect("Couldn't parse quest"),
            part: splits[2].split("=").collect::<Vec<&str>>()[1]
                .parse::<u8>()
                .expect("Couldn't parse part"),
            time: splits[3].split("=").collect::<Vec<&str>>()[1]
                .parse::<u128>()
                .expect("Couldn't parse time"),
        };
    }
}

#[derive(Debug)]
struct Contestant {
    quests: HashMap<u128, Quest>,
}

impl Contestant {
    fn get_parts_completed_and_time(&self) -> (u128, u128) {
        let mut parts_completed = 0;
        let mut total_time = 0;
        self.quests.values().for_each(|quest| {
            parts_completed += quest.parts.len() as u128;
            total_time += quest.time;
        });
        return (parts_completed, total_time);
    }

    fn add_quest_result(&mut self, quest_result: &QuestResult) {
        let quest = self
            .quests
            .entry(quest_result.quest)
            .or_insert_with(|| Quest {
                parts: HashMap::new(),
                time: 0,
            });

        quest.parts.insert(quest_result.part, quest_result.time);

        quest.time += quest_result.time;
    }
}

#[derive(Debug)]
struct Quest {
    parts: HashMap<u8, u128>,
    time: u128,
}
