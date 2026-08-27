use std::{collections::HashMap, fmt::Debug};

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
        left.insert_node(left_node.unwrap());
        right.insert_node(right_node.unwrap());
    }

    println!("{}", pretty_string_for_answer(&left, &right, false));
}

fn part2(input: Vec<String>) {
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

    for (operation, id, left_node, right_node) in rows {
        match operation {
            Operation::Swap => {
                swap_basenode_between_trees_by_id(&mut left, &mut right, id);
            }
            Operation::Add => {
                left.insert_node(left_node.unwrap());
                right.insert_node(right_node.unwrap());
            }
        }
    }

    println!("{}", pretty_string_for_answer(&left, &right, false));
}

fn part3(input: Vec<String>) {
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

    for (operation, id, left_node, right_node) in rows {
        match operation {
            Operation::Swap => {
                swap_treenodes_by_id(&mut left, &mut right, id);
            }
            Operation::Add => {
                left.insert_node(left_node.unwrap());
                right.insert_node(right_node.unwrap());
            }
        }
    }

    println!("{}", pretty_string_for_answer(&left, &right, true));
}

#[derive(Debug)]
enum Operation {
    Add,
    Swap,
}

#[derive(Debug, Clone)]
struct BaseNode {
    rank: u128,
    value: String,
    id: u128,
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

    fn basenodes_and_counts_per_level(&self) -> HashMap<u32, (Vec<BaseNode>, u32)> {
        self._basenodes_and_counts_per_level(0)
    }

    fn _basenodes_and_counts_per_level(&self, i: u32) -> HashMap<u32, (Vec<BaseNode>, u32)> {
        let next_level = i + 1;
        let left_result = match &self.left {
            Some(left_node) => left_node._basenodes_and_counts_per_level(next_level),
            None => HashMap::new(),
        };
        let mut right_result = match &self.right {
            Some(right_node) => right_node._basenodes_and_counts_per_level(next_level),
            None => HashMap::new(),
        };

        left_result
            .into_iter()
            .for_each(|(index, (left_nodes, left_count))| {
                let new_tuple = match right_result.remove(&index) {
                    Some((mut right_nodes, right_count)) => {
                        right_nodes.extend(left_nodes);
                        (right_nodes, right_count + left_count)
                    }
                    None => (left_nodes, left_count),
                };
                right_result.insert(index, new_tuple);
            });

        right_result.insert(
            i,
            match &self.root {
                Some(root_node) => (vec![root_node.clone()], 1),
                None => (Vec::new(), 0),
            },
        );
        return right_result;
    }

    fn return_largest_level_nodes(&self, find_lowest_level: bool) -> Vec<BaseNode> {
        let tree_level_map = self.basenodes_and_counts_per_level();
        let mut max = u32::MIN;
        let mut max_found_at_level = u32::MAX;
        let mut max_nodes = Vec::new();

        for (level, (nodes, count)) in tree_level_map.into_iter() {
            if count > max {
                // 1. New highest count found! It automatically wins.
                max = count;
                max_found_at_level = level;
                max_nodes = nodes;
            } else if count == max {
                // 2. WE HAVE A TIE! Use your level rules to break the tie.
                if find_lowest_level && level < max_found_at_level {
                    max_found_at_level = level;
                    max_nodes = nodes;
                } else if !find_lowest_level && level > max_found_at_level {
                    max_found_at_level = level;
                    max_nodes = nodes;
                }
            }
        }
        return max_nodes;
    }

    fn find_node_by_id(&mut self, id: u128) -> Option<&mut BaseNode> {
        if let Some(root_node) = &mut self.root {
            if root_node.id == id {
                return Some(root_node);
            }
        }

        if let Some(left_tree_node) = &mut self.left {
            if let Some(found) = left_tree_node.find_node_by_id(id) {
                return Some(found);
            }
        }

        if let Some(right_tree_node) = &mut self.right {
            if let Some(found) = right_tree_node.find_node_by_id(id) {
                return Some(found);
            }
        }

        return None;
    }

    fn find_all_tree_nodes_by_id(&mut self, id: u128) -> Vec<*mut TreeNode> {
        let mut result = Vec::new();
        self.collect_node_pointers_by_id(id, &mut result);
        result
    }

    fn collect_node_pointers_by_id(&mut self, id: u128, acc: &mut Vec<*mut TreeNode>) {
        if self.root.as_ref().is_some_and(|node| node.id == id) {
            acc.push(self)
        }

        if let Some(left) = &mut self.left {
            left.collect_node_pointers_by_id(id, acc);
        }

        if let Some(right) = &mut self.right {
            right.collect_node_pointers_by_id(id, acc);
        }
    }
}

fn swap_basenode_between_trees_by_id(left: &mut TreeNode, right: &mut TreeNode, id: u128) {
    let left_node = left.find_node_by_id(id);
    let right_node = right.find_node_by_id(id);

    if let (Some(l_node), Some(r_node)) = (left_node, right_node) {
        std::mem::swap(&mut l_node.rank, &mut r_node.rank);
        std::mem::swap(&mut l_node.value, &mut r_node.value);
    } else {
        panic!("Couldn't find the id {} in one or both of the trees", id);
    }
}

fn swap_treenodes_by_id(left: &mut TreeNode, right: &mut TreeNode, id: u128) {
    let mut tree_nodes = left.find_all_tree_nodes_by_id(id);
    tree_nodes.extend(right.find_all_tree_nodes_by_id(id));

    if tree_nodes.len() == 2 {
        let first_node_pointer = tree_nodes[0];
        let second_node_pointer = tree_nodes[1];

        unsafe {
            std::mem::swap(&mut *first_node_pointer, &mut *second_node_pointer);
        }
    } else {
        panic!(
            "Didn't find exactly 2 tree nodes, found more instead, namely: {}",
            tree_nodes.len()
        );
    }
}

fn parse_line(line: &str) -> (Operation, u128, Option<BaseNode>, Option<BaseNode>) {
    let splits: Vec<&str> = line.split(" ").collect();
    let operation = parse_operation(splits.get(0).unwrap());
    let id = parse_num_from_field_with_equals_split(splits.get(1).map(|v| &**v));

    match operation {
        Operation::Add => {
            let left = parse_base_node(splits.get(2).unwrap(), id);
            let right = parse_base_node(splits.get(3).unwrap(), id);

            (operation, id, Some(left), Some(right))
        }
        _ => (operation, id, None, None),
    }
}

fn parse_base_node(node_string: &str, id: u128) -> BaseNode {
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
        id: id,
    };
}

fn parse_operation(operation_string: &str) -> Operation {
    return match operation_string {
        "ADD" => Operation::Add,
        "SWAP" => Operation::Swap,
        _ => panic!("We don't support this operation"),
    };
}

fn pretty_string_for_answer(left: &TreeNode, right: &TreeNode, find_lowest_level: bool) -> String {
    let left_largest_level = left.return_largest_level_nodes(find_lowest_level);
    let right_largest_level = right.return_largest_level_nodes(find_lowest_level);

    fn stringify_values(nodes: Vec<BaseNode>) -> String {
        let mut nodes_result = String::from("");
        for node in nodes.into_iter() {
            nodes_result = node.value + &nodes_result;
        }
        return nodes_result.to_string();
    }

    stringify_values(left_largest_level) + &stringify_values(right_largest_level)
}
