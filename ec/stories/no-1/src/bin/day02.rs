use std::{
    collections::HashMap,
    fmt::Debug,
};

use ec_stories_no_1::{Part, load_input, parse_num_from_field_with_equals_split, parse_part};

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
    let rows = input.iter().map(|line| parse_line(line));

    let mut left = TreeNode {
        root: None,
        left: None,
        right: None,
    };
    let mut right = TreeNode {
        root: None,
        left: None,
        right: None,
    };

    for (_, _, left_node, right_node) in rows {
        left.insert_node(left_node);
        right.insert_node(right_node);
    }

    println!("{:?}", left.return_largest_level_string());
    println!("{:?}", right.return_largest_level_string());
}

fn part2(input: Vec<String>) {
    println!("Not implemented, got input {:?}", input);
}

fn part3(input: Vec<String>) {
    println!("Not implemented, got input {:?}", input);
}

#[derive(Debug)]
enum Operation {
    Add,
}

#[derive(Debug)]
struct BaseNode {
    rank: u128,
    value: String,
}

impl BaseNode {
    fn to_string(&self) -> String {
        format!("Rank: {}, Value: {}", self.rank, self.value)
    }
}

#[derive(Debug)]
struct TreeNode {
    root: Option<BaseNode>,
    left: Option<Box<TreeNode>>,
    right: Option<Box<TreeNode>>,
}

impl TreeNode {
    fn uniserted_node_is_larger(&self, base_node: &BaseNode) -> bool {
        self.root
            .as_ref()
            .is_some_and(|root_node| base_node.rank > root_node.rank)
    }

    fn insert_node(&mut self, base_node: BaseNode) {
        if self.root.is_none() {
            self.root = Some(base_node);
        } else if self.uniserted_node_is_larger(&base_node) {
            if self.right.is_none() {
                self.right = Some(Box::new(TreeNode {
                    root: Some(base_node),
                    left: None,
                    right: None,
                }));
            } else {
                self.right.as_mut().unwrap().insert_node(base_node);
            }
        } else {
            if self.left.is_none() {
                self.left = Some(Box::new(TreeNode {
                    root: Some(base_node),
                    left: None,
                    right: None,
                }));
            } else {
                self.left.as_mut().unwrap().insert_node(base_node);
            }
        }
    }

    fn strings_and_counts_per_level(&self) -> HashMap<u32, (String, u32)> {
        self._strings_and_counts_per_level(0)
    }

    fn _strings_and_counts_per_level(&self, i: u32) -> HashMap<u32, (String, u32)> {
        let next_level = i + 1;
        let left_result = match &self.left {
            Some(left_node) => left_node._strings_and_counts_per_level(next_level),
            None => HashMap::new(),
        };
        let mut right_result = match &self.right {
            Some(right_node) => right_node._strings_and_counts_per_level(next_level),
            None => HashMap::new(),
        };

        left_result.into_iter().for_each(|(index, (left_string, left_count))| {
            let new_tuple = match right_result.get(&index) {
                Some((right_string, right_count)) => (format!("[{}, {}]", right_string, left_string), right_count + left_count),
                None => (left_string, left_count),
            };
            right_result.insert(index, new_tuple);
        });

        right_result.insert(
            i,
            match &self.root {
                Some(root_node) => (root_node.to_string(), 1),
                None => (String::from("<>"), 0),
            },
        );
        return right_result;
    }

    fn return_largest_level_string(&self) -> String {
        let string_per_level = self.strings_and_counts_per_level();
        let mut max = u32::MIN;
        let mut max_value = "<>";
        string_per_level.iter().for_each(|(_, (string_value, count))| {
            if *count > max {
                max = *count;
                max_value = string_value;
            }
        });
        return max_value.to_string();
    }
}

fn parse_line(line: &str) -> (Operation, u128, BaseNode, BaseNode) {
    let splits: Vec<&str> = line.split(" ").collect();
    let operation = parse_operation(splits.get(0).unwrap());
    let id = parse_num_from_field_with_equals_split(splits.get(1).map(|v| &**v));

    let left = parse_base_node(splits.get(2).unwrap());
    let right = parse_base_node(splits.get(3).unwrap());

    return (operation, id, left, right);
}

fn parse_base_node(node_string: &str) -> BaseNode {
    let cleaned_string = node_string.replace("[", "").replace("]", "");
    let splits = cleaned_string.split("=");
    let rank_value_split: Vec<&str> = splits.last().unwrap().split(",").collect();

    let rank: u128 = rank_value_split.first().unwrap().parse::<u128>().unwrap();
    let value: String = rank_value_split
        .last()
        .expect("Wanted a string here")
        .to_string();
    return BaseNode {
        rank: rank,
        value: value,
    };
}

fn parse_operation(operation_string: &str) -> Operation {
    return match operation_string {
        "ADD" => Operation::Add,
        _ => panic!("We don't support this operation"),
    };
}
