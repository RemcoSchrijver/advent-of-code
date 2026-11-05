use std::collections::{HashMap, HashSet};

use ec_quest_2026::Part;
use ec_quest_2026::{load_input, parse_part};

const DAY: &str = "day06";

fn main() {
    let part = parse_part().unwrap();
    match part {
        Part::Part1 => part1(load_input(DAY, part)),
        Part::Part2 => part2(load_input(DAY, part)),
        Part::Part3 => part3(load_input(DAY, part)),
    }
}

const NULL_INGREDIENT: &str = "NULL";

fn part1(input: Vec<String>) {
    let ingredients_map = parse_ingredients(input);

    let ingredient_chain: HashSet<String> =
        find_makeup_of_ingredient(&ingredients_map, NULL_INGREDIENT);

    println!(
        "{} is made up of {} ingredients ",
        NULL_INGREDIENT,
        ingredient_chain.len()
    );
}

fn part2(input: Vec<String>) {
    let ingredients_map = parse_ingredients(input);

    let used_in_ingredients: HashSet<String> =
        find_uses_of_ingredient(&ingredients_map, NULL_INGREDIENT);

    println!(
        "{} is part of {} ingredients",
        NULL_INGREDIENT,
        used_in_ingredients.len()
    );
}

fn part3(input: Vec<String>) {
    let ingredients_map = parse_ingredients(input);

    let nullv1 = find_uses_of_ingredient(&ingredients_map, "NULLv1");
    let nullv2 = find_uses_of_ingredient(&ingredients_map, "NULLv2");
    let nullv3 = find_uses_of_ingredient(&ingredients_map, "NULLv3");

    let mut instances_with_atleast_two_null_uses = HashSet::new();

    instances_with_atleast_two_null_uses.extend(nullv1.intersection(&nullv2));
    instances_with_atleast_two_null_uses.extend(nullv1.intersection(&nullv3));
    instances_with_atleast_two_null_uses.extend(nullv2.intersection(&nullv3));

    println!(
        "The number of ingredients with at least 2 null versions is {}",
        instances_with_atleast_two_null_uses.len()
    );
}

fn parse_ingredients(input: Vec<String>) -> HashMap<String, Vec<String>> {
    let mut ingredients_map = HashMap::new();

    for line in input {
        let splits: Vec<&str> = line.split(":").collect();
        let name: String = splits[0].to_string();
        let ingredients: Vec<String> = splits[1].split(",").map(|v| v.to_string()).collect();

        ingredients_map.insert(name, ingredients);
    }

    return ingredients_map;
}

fn find_makeup_of_ingredient(
    ingredients_map: &HashMap<String, Vec<String>>,
    ingredient_to_find: &str,
) -> HashSet<String> {
    let mut result: HashSet<String> = HashSet::new();
    let mut ingredients_to_trace: Vec<String> = ingredients_map
        .get(ingredient_to_find)
        .expect("Didn't find ingredient")
        .to_vec();

    while !ingredients_to_trace.is_empty() {
        let ingredient = ingredients_to_trace
            .pop()
            .expect("Somehow ingredients to trace is empty");
        if !result.contains(&ingredient) {
            if ingredients_map.contains_key(&ingredient) {
                ingredients_map[&ingredient]
                    .iter()
                    .for_each(|v| ingredients_to_trace.push(v.to_string()));
            }
            result.insert(ingredient);
        }
    }
    result.remove(ingredient_to_find);
    return result;
}

fn find_uses_of_ingredient(
    ingredients_map: &HashMap<String, Vec<String>>,
    ingredient_to_find: &str,
) -> HashSet<String> {
    ingredients_map
        .keys()
        .filter(|ing| *ing != ingredient_to_find)
        .filter(|ingredient| {
            find_makeup_of_ingredient(ingredients_map, ingredient).contains(ingredient_to_find)
        })
        .map(|s| s.to_string())
        .collect()
}
