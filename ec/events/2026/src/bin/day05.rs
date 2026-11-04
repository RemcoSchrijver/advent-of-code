use std::collections::{HashMap, HashSet};

use ec_quest_2026::Part;
use ec_quest_2026::{load_input, parse_part};

const DAY: &str = "day05";

fn main() {
    let part = parse_part().unwrap();
    match part {
        Part::Part1 => part1(load_input(DAY, part)),
        Part::Part2 => part2(load_input(DAY, part)),
        Part::Part3 => part3(load_input(DAY, part)),
    }
}

const LAND_CHAR: char = '#';
const WATER_CHAR: char = '.';
const SAND_CHAR: char = '=';
const TREE_CHAR: char = '@';

fn part1(input: Vec<String>) {
    let mut map = Map::parse_map(input);
    let middle = map.find_middle_of_map();

    map.replace_position(middle, LAND_CHAR);
    map.place_land_from_middle(middle);

    let land_piece_count = map.number_of_land_pieces();

    println!("Total land spots on the map: {}", land_piece_count);
    map.print_grid();
}

fn part2(input: Vec<String>) {
    let mut map = Map::parse_map(input);
    let middle = map.find_middle_of_map();

    map.replace_position(middle, LAND_CHAR);
    map.place_land_from_middle(middle);
    map.place_water();
    map.place_sand_from_land();
    map.place_trees_second_phase();

    let sand_piece_count = map.number_of_sand_pieces();

    println!("Total land spots on the map: {}", sand_piece_count);
    map.print_grid();
}

fn part3(input: Vec<String>) {
    let mut map = Map::parse_map(input);
    let middle = map.find_middle_of_map();

    map.replace_position(middle, LAND_CHAR);
    map.place_land_from_middle(middle);
    map.place_water();
    map.place_sand_from_land();
    map.place_initial_trees();
    map.place_trees_second_phase();

    let tree_piece_count = map.number_of_tree_pieces();

    println!("Total tree spots on the map: {}", tree_piece_count);
    map.print_grid();
}

struct Map {
    grid: HashMap<(usize, usize), char>,
    y_max: usize,
    x_max: usize,
}

impl Map {
    fn parse_map(input: Vec<String>) -> Map {
        let mut map = HashMap::new();

        input.iter().enumerate().for_each(|(x, line)| {
            line.chars().enumerate().for_each(|(y, char)| {
                map.insert((x, y), char);
            })
        });

        return Map {
            grid: map,
            x_max: input.len() + 1,
            y_max: input.first().expect("No string in input?").len() + 1,
        };
    }

    fn find_middle_of_map(&self) -> (usize, usize) {
        // Middle is always at .5 so rounds up
        (self.x_max / 2 - 1, self.y_max / 2 - 1)
    }

    fn find_neighbours(&self, position: (usize, usize)) -> Vec<((usize, usize), char)> {
        let mut result = Vec::new();
        for x in 0..3 {
            for y in 0..3 {
                // Skip self
                if position.0 + 1 >= x && position.1 + 1 >= y {
                    let neighbour_position = (position.0 + 1 - x, position.1 + 1 - y);

                    if neighbour_position != position {
                        let char_at_position = self.get_position(neighbour_position);
                        if char_at_position.is_some() {
                            result.push((neighbour_position, char_at_position.unwrap()));
                        }
                    }
                }
            }
        }
        return result;
    }

    fn get_position(&self, position: (usize, usize)) -> Option<char> {
        if self.grid.contains_key(&position) {
            return Some(self.grid[&position]);
        } else {
            return None;
        }
    }

    fn replace_position(&mut self, position: (usize, usize), character: char) {
        self.grid.insert(position, character);
    }

    fn place_land_from_middle(&mut self, middle: (usize, usize)) {
        let mut targets = self.find_neighbours(middle);
        let mut index = 0;
        let mut old_set = HashSet::new();
        self.place_land(middle);

        while index < targets.len() {
            let (position, _) = targets[index];
            self.place_land(position);
            index += 1;

            if index == targets.len() {
                let mut new_targets = HashSet::new();
                self.get_positions_of_land_pieces()
                    .iter()
                    .for_each(|position| {
                        self.find_neighbours(*position)
                            .into_iter()
                            .filter(|(_, char)| *char != LAND_CHAR)
                            .for_each(|val| {
                                new_targets.insert(val);
                            });
                    });

                if old_set != new_targets {
                    targets.append(
                        new_targets
                            .clone()
                            .into_iter()
                            .collect::<Vec<((usize, usize), char)>>()
                            .as_mut(),
                    );
                    old_set = new_targets;
                }
            }
        }
    }

    fn place_land(&mut self, position: (usize, usize)) {
        let land_neighbours: Vec<((usize, usize), char)> = self
            .find_neighbours(position)
            .into_iter()
            .filter(|(_, c)| *c == '1' || *c == LAND_CHAR)
            .collect();
        if land_neighbours.len() >= 5 {
            self.replace_position(position, LAND_CHAR);
        }
    }

    fn place_water(&mut self) {
        self.grid
            .clone()
            .into_iter()
            .filter(|(_, c)| *c != LAND_CHAR)
            .for_each(|(pos, _)| self.replace_position(pos, WATER_CHAR));
    }

    fn place_sand_from_land(&mut self) {
        let targets = self.get_positions_of_land_pieces();
        let mut old_set = HashSet::new();
        for position in &targets {
            self.place_sand(*position);
        }

        while old_set != self.get_positions_of_sand_pieces() {
            old_set = self.get_positions_of_sand_pieces();
            for position in &targets {
                self.place_sand(*position);
            }
        }
    }

    fn place_sand(&mut self, position: (usize, usize)) {
        let total_neighbours_length = self.find_neighbours(position).len();
        let water_neighbours: Vec<((usize, usize), char)> = self
            .find_neighbours(position)
            .into_iter()
            .filter(|(_, c)| *c == WATER_CHAR)
            .collect();
        let sand_neighbours: Vec<((usize, usize), char)> = self
            .find_neighbours(position)
            .into_iter()
            .filter(|(_, c)| *c == SAND_CHAR)
            .collect();
        if total_neighbours_length < 8 || water_neighbours.len() > 0 || sand_neighbours.len() >= 5 {
            self.replace_position(position, SAND_CHAR);
        }
    }

    fn place_initial_trees(&mut self) {
        self.get_positions_of_land_pieces()
            .into_iter()
            .for_each(|(x, y)| {
                let mut checked_positions: Vec<((usize, usize), char)> = Vec::new();
                checked_positions.append(&mut self.find_neighbours((x, y)));
                checked_positions.append(&mut self.find_neighbours((x + 1, y)));
                checked_positions.append(&mut self.find_neighbours((x - 1, y)));
                checked_positions.append(&mut self.find_neighbours((x, y + 1)));
                checked_positions.append(&mut self.find_neighbours((x, y - 1)));
                if checked_positions
                    .iter()
                    .all(|(_, c)| *c == TREE_CHAR || *c == LAND_CHAR)
                {
                    self.replace_position((x, y), TREE_CHAR);
                }
            });
    }

    fn place_trees_second_phase(&mut self) {
        for _ in 0..2 {
            let mut potential_targets = HashMap::new();
            self.get_positions_of_tree_pieces()
                .iter()
                .for_each(|position| potential_targets.extend(self.find_neighbours(*position)));
            let mut actual_targets = HashSet::new();

            for (position, c) in potential_targets {
                if c != TREE_CHAR {
                    let tree_neighbours: Vec<((usize, usize), char)> = self
                        .find_neighbours(position)
                        .into_iter()
                        .filter(|(_, c)| *c == TREE_CHAR)
                        .collect();
                    let sand_neighbours: Vec<((usize, usize), char)> = self
                        .find_neighbours(position)
                        .into_iter()
                        .filter(|(_, c)| *c == SAND_CHAR)
                        .collect();
                    if tree_neighbours.len() >= 4 && sand_neighbours.len() == 0 {
                        actual_targets.insert(position);
                    }
                }
            }
            actual_targets
                .iter()
                .for_each(|pos| self.replace_position(*pos, TREE_CHAR));
        }
    }

    fn number_of_land_pieces(&self) -> usize {
        self.number_of_pieces(LAND_CHAR)
    }

    fn number_of_sand_pieces(&self) -> usize {
        self.number_of_pieces(SAND_CHAR)
    }

    fn number_of_tree_pieces(&self) -> usize {
        self.number_of_pieces(TREE_CHAR)
    }

    fn number_of_pieces(&self, piece: char) -> usize {
        self.grid
            .clone()
            .into_values()
            .filter(|c| *c == piece)
            .collect::<Vec<char>>()
            .len()
    }

    fn get_positions_of_land_pieces(&self) -> HashSet<(usize, usize)> {
        self.get_positions_of_pieces(LAND_CHAR)
    }

    fn get_positions_of_sand_pieces(&self) -> HashSet<(usize, usize)> {
        self.get_positions_of_pieces(SAND_CHAR)
    }

    fn get_positions_of_tree_pieces(&self) -> HashSet<(usize, usize)> {
        self.get_positions_of_pieces(TREE_CHAR)
    }

    fn get_positions_of_pieces(&self, piece: char) -> HashSet<(usize, usize)> {
        self.grid
            .iter()
            .filter(|(_position, char)| **char == piece)
            .map(|(position, _char)| *position)
            .collect()
    }

    fn print_grid(&self) {
        let mut values = self
            .grid
            .clone()
            .into_iter()
            .map(|(k, v)| (k, v))
            .collect::<Vec<((usize, usize), char)>>();
        values.sort();

        let mut start_x = 0;
        let mut collector = String::new();
        for ((x, _), char) in values {
            if x != start_x {
                start_x = x;
                collector += "\n";
            }
            collector += &char.to_string();
        }

        println!("{}", collector);
    }
}
