use std::collections::HashMap;

use ec_quest_2026::{Part, parse_num_from_field_with_equals_split};
use ec_quest_2026::{load_input, parallel_for_each, parse_part};

const DAY: &str = "day07";

fn main() {
    let part = parse_part().unwrap();
    match part {
        Part::Part1 => part1(load_input(DAY, part)),
        Part::Part2 => part2(load_input(DAY, part)),
        Part::Part3 => part3(load_input(DAY, part)),
    }
}

fn part1(input: Vec<String>) {
    solve_part_1_and_2(input, 5);
}

fn part2(input: Vec<String>) {
    solve_part_1_and_2(input, 10);
}

fn part3(input: Vec<String>) {
    let charge_amount = 10;
    let clouds = parse_clouds(input);
    let mut cloud_ids_and_scores: Vec<(u128, u128)> =
        clouds.iter().map(|cloud| (*cloud.0, 0_u128)).collect();

    parallel_for_each(&mut cloud_ids_and_scores, |(cloud_id, score)| {
        let mut cloned_clouds = clouds.clone();
        charge_cloud_with_explosion(&mut cloned_clouds, *cloud_id, charge_amount);
        *score = cloned_clouds.iter().map(|(_, cloud)| cloud.energy).sum();
    });

    let total_charge: u128 = cloud_ids_and_scores
        .iter()
        .map(|(_, score)| {
            *score
        })
        .max()
        .expect("No scores");

    println!("The total charge is {}", total_charge);
}

fn solve_part_1_and_2(input: Vec<String>, charge_amount: u128) {
    let mut clouds = parse_clouds(input);

    let start_cloud = clouds
        .iter()
        .max_by_key(|(_, cloud)| cloud.connections.len())
        .expect("Expected to find a start cloud");

    let start_id = *start_cloud.0;

    charge_cloud(&mut clouds, start_id, charge_amount);
    let total_charge: u128 = clouds.iter().map(|(_, cloud)| cloud.energy).sum();

    println!("The total charge is {}", total_charge);
}

const EXPLOSION_LIMIT: u128 = 1000000;
// const EXPLOSION_LIMIT: u128 = 100;
const EXPLOSION_CHARGE: u128 = 10;

fn charge_cloud_with_explosion(
    clouds: &mut HashMap<u128, Cloud>,
    initial_cloud_id: u128,
    initial_charge: u128,
) {
    // initial charge
    let mut charges: HashMap<u128, HashMap<u128, u128>> =
        HashMap::from([(initial_cloud_id, HashMap::from([(initial_charge, 1)]))]);

    while !charges.is_empty() {
        let mut next_charges: HashMap<u128, HashMap<u128, u128>> = HashMap::new();
        for (id, charges_count) in charges {
            if clouds.contains_key(&id) {
                let connections: Vec<u128> = clouds[&id].connections.clone();
                for (charge_amount, count) in charges_count {
                    clouds
                        .entry(id)
                        .and_modify(|cloud| cloud.energy += charge_amount * count);

                    if charge_amount > 1 {
                        connections
                            .iter()
                            .filter(|conn_cloud_id| clouds.contains_key(conn_cloud_id))
                            .for_each(|conn_cloud_id| {
                                let new_charge_amount = charge_amount - 1;
                                next_charges
                                    .entry(*conn_cloud_id)
                                    .and_modify(|charge_map| {
                                        charge_map
                                            .entry(new_charge_amount)
                                            .and_modify(|old_count| *old_count += count)
                                            .or_insert(count);
                                    })
                                    .or_insert(HashMap::from([(new_charge_amount, count)]));
                            });
                    }
                }
            }
        }
        let mut to_remove = Vec::new();

        for (id, cloud) in clouds.iter() {
            if cloud.energy >= EXPLOSION_LIMIT {
                for each in cloud.connections.clone() {
                    let new_charge_amount = EXPLOSION_CHARGE;
                    next_charges
                        .entry(each)
                        .and_modify(|charge_map| {
                            charge_map
                                .entry(new_charge_amount)
                                .and_modify(|old_count| *old_count += 1)
                                .or_insert(1);
                        })
                        .or_insert(HashMap::from([(new_charge_amount, 1)]));
                }
                to_remove.push(*id);
            }
        }
        for id in to_remove {
            clouds.remove(&id);
        }

        charges = next_charges;
    }
}

fn charge_cloud(clouds: &mut HashMap<u128, Cloud>, cloud_id: u128, charge_amount: u128) {
    if charge_amount == 0 {
        return;
    }
    clouds
        .entry(cloud_id)
        .and_modify(|cloud| cloud.energy += charge_amount);
    clouds
        .get(&cloud_id)
        .expect("Cloud not found")
        .connections
        .clone()
        .into_iter()
        .for_each(|connection| {
            charge_cloud(clouds, connection, charge_amount - 1);
        });
}

fn parse_clouds(input: Vec<String>) -> HashMap<u128, Cloud> {
    let mut result = HashMap::new();
    for line in input {
        let (id, cloud) = parse_line(line);
        result.insert(id, cloud);
    }
    return result;
}

fn parse_line(line: String) -> (u128, Cloud) {
    let splits: Vec<&str> = line.split(",").collect();
    let id = parse_num_from_field_with_equals_split(Some(splits[0]));
    let energy = parse_num_from_field_with_equals_split(Some(splits[1]));
    let connections_string = line.split("connections=").collect::<Vec<&str>>()[1];
    let connections = connections_string
        .split(",")
        .map(|v| v.parse::<u128>().expect("Couldn't parse connection"))
        .collect();

    return (
        id,
        Cloud {
            energy: energy,
            connections: connections,
        },
    );
}

#[derive(Debug, Clone)]
struct Cloud {
    energy: u128,
    connections: Vec<u128>,
}
