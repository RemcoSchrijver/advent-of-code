use ec_quest_2026::Part;
use ec_quest_2026::{load_input, parse_part};
use std::collections::{HashMap, HashSet};

const DAY: &str = "day08";

fn main() {
    let part = parse_part().unwrap();
    match part {
        Part::Part1 => part1(load_input(DAY, part)),
        Part::Part2 => {
            part2(load_input(DAY, part));
        }
        Part::Part3 => {
            part3(load_input(DAY, part));
        }
    }
}

fn part1(input: Vec<String>) {
    let board = parse_board(input.clone());

    let shadow_board = cast_shadows(
        board,
        (1, 1),
        (&input.len(), &input.first().expect("uh oh no string").len()),
    );

    let ents_growing = count_lighted_ents(&shadow_board);

    print_board(&shadow_board);
    println!("Number of ents getting energy: {}", ents_growing);
}

fn part2(input: Vec<String>) {
    let board = parse_board(input.clone());

    let directions: Vec<(i32, i32)> = vec![(1, 1), (-1, 1), (0, -1), (-1, -1), (1, -1), (0, 1)];

    let mut shadow_boards: Vec<HashMap<(usize, usize), char>> = Vec::new();
    directions.iter().for_each(|direction| {
        shadow_boards.push(cast_shadows(
            board.clone(),
            *direction,
            (&input.len(), &input.first().expect("uh oh no string").len()),
        ));
    });

    let ents_growing: Vec<usize> = shadow_boards
        .iter()
        .map(|shadow_board| {
            print_board(shadow_board);
            println!();
            count_lighted_ents(&shadow_board)
        })
        .collect();

    println!(
        "Number of ents getting energy: {}",
        ents_growing.into_iter().sum::<usize>()
    );
}

fn part3(input: Vec<String>) {
    let board = parse_board(input.clone());
    let ent_army_size = 100000;
    // let ent_army_size = 42;

    let energy_required = grow_ents(
        board,
        ent_army_size,
        (&input.len(), &input.first().expect("uh oh no string").len()),
    );

    println!(
        "To grow {} ents we need total energy: {}",
        ent_army_size, energy_required
    );
}

const SHADOW_CHAR: char = '.';

fn parse_board(input: Vec<String>) -> HashMap<(usize, usize), char> {
    let mut result: HashMap<(usize, usize), char> = HashMap::new();

    input.iter().enumerate().for_each(|(x, line)| {
        line.chars().enumerate().for_each(|(y, character)| {
            result.insert((x, y), character);
        })
    });

    return result;
}

fn print_board(ent_board: &HashMap<(usize, usize), char>) {
    let mut ent_vec: Vec<(&(usize, usize), &char)> = ent_board.iter().collect();
    ent_vec.sort_by_key(|(coords, _)| *coords);
    let mut previous_x = 0;
    ent_vec.iter().for_each(|((x, _), character)| {
        if previous_x != *x {
            print!("\n");
            previous_x = *x;
        }
        print!("{}", character);
    });
    print!("\n");
}

fn cast_shadows(
    ent_board: HashMap<(usize, usize), char>,
    direction: (i32, i32),
    board_size: (&usize, &usize),
) -> HashMap<(usize, usize), char> {
    let mut result: HashMap<(usize, usize), char> = HashMap::new();

    let start_blocks: HashSet<(usize, usize)> = find_start_blocks(direction, board_size);

    // Casting rays
    for position in start_blocks {
        let mut shadow_size = 0;
        let mut new_position = position;
        let mut old_char = SHADOW_CHAR;

        while ent_board.contains_key(&new_position) {
            if shadow_size > 0 && old_char != SHADOW_CHAR {
                shadow_size -= 1;
            }
            let current_char = ent_board[&new_position];
            old_char = current_char;
            if current_char == SHADOW_CHAR {
                result.insert(new_position, current_char);
            } else {
                let ent_size = current_char.to_digit(10).expect("Wasn't a u8 char");
                if ent_size > shadow_size {
                    shadow_size = ent_size;
                    result.insert(new_position, current_char);
                } else {
                    result.insert(new_position, SHADOW_CHAR);
                }
            }
            match merge_direction(new_position, direction) {
                Some(calculated_position) => new_position = calculated_position,
                None => break,
            }
        }
    }

    return result;
}

const SEED_STAGE_1: char = 'a';
const SEED_STAGE_2: char = 'b';
const SEED_STAGE_3: char = 'c';

fn grow_ents(
    mut ent_board: HashMap<(usize, usize), char>,
    ent_target_count: u128,
    board_size: (&usize, &usize),
) -> u128 {
    let mut energy: u128 = 0;
    let mut mature_ents: u128 = 0;
    let mut rounds: u128 = 0;

    // Order here might be wrong?
    let directions: Vec<(i32, i32)> = vec![(1, 1), (1, -1), (0, -1), (-1, -1), (-1, 1), (0, 1)];

    let all_start_blocks: Vec<HashSet<(usize, usize)>> = directions
        .iter()
        .map(|direction| find_start_blocks(*direction, board_size))
        .collect();

    // Casting rays
    let mut count = 0;
    while mature_ents < ent_target_count {
        rounds += 1;
        let direction = directions[count];
        let start_blocks = all_start_blocks[count].clone();
        for position in start_blocks {
            let mut shadow_size = 0;
            let mut new_position = position;
            let mut old_char = SHADOW_CHAR;

            while ent_board.contains_key(&new_position) {
                if shadow_size > 0 && old_char != SHADOW_CHAR {
                    shadow_size -= 1;
                }
                let current_char = ent_board[&new_position];
                old_char = current_char;
                match current_char {
                    SHADOW_CHAR => {
                        ent_board.insert(new_position, current_char);
                    }
                    SEED_STAGE_1 => {
                        ent_board.insert(new_position, SEED_STAGE_2);
                    }
                    SEED_STAGE_2 => {
                        ent_board.insert(new_position, SEED_STAGE_3);
                    }
                    SEED_STAGE_3 => {
                        ent_board.insert(new_position, '1');
                    }
                    _ => {
                        let ent_size = current_char.to_digit(10).expect("Wasn't a u8 char");
                        if ent_size > shadow_size {
                            energy += 1;
                            shadow_size = ent_size;
                            if ent_size >= 9 {
                                ent_board.insert(new_position, SEED_STAGE_1);
                                mature_ents += 1;
                            } else {
                                ent_board.insert(
                                    new_position,
                                    char::from_digit(ent_size + 1, 10)
                                        .expect("Wasn't a digit char"),
                                );
                            }
                        } else {
                            ent_board.insert(new_position, current_char);
                        }
                    }
                }
                match merge_direction(new_position, direction) {
                    Some(calculated_position) => new_position = calculated_position,
                    None => break,
                }
            }
        }
        count += 1;
        if count >= 6 {
            count = 0;
        }
    }
    println!("Took {} rounds", rounds);

    return energy;
}

fn count_lighted_ents(ent_board: &HashMap<(usize, usize), char>) -> usize {
    let mut ents_growing = 0;
    ent_board.values().for_each(|character| {
        if *character != SHADOW_CHAR {
            ents_growing += 1;
        }
    });
    return ents_growing;
}

fn find_start_blocks(
    direction: (i32, i32),
    board_size: (&usize, &usize),
) -> HashSet<(usize, usize)> {
    let mut start_blocks: HashSet<(usize, usize)> = HashSet::new();
    let num_rows = *board_size.0;
    let num_cols = *board_size.1;

    match direction {
        (1, 1) => {
            start_blocks.extend(&add_top(num_rows, num_cols));
            start_blocks.extend(&add_left(num_rows, num_cols));
        }
        (-1, 1) => {
            start_blocks.extend(&add_bottom(num_rows, num_cols));
            start_blocks.extend(&add_left(num_rows, num_cols));
        }
        (0, -1) => {
            start_blocks.extend(&add_right(num_rows, num_cols));
        }
        (-1, -1) => {
            start_blocks.extend(&add_bottom(num_rows, num_cols));
            start_blocks.extend(&add_right(num_rows, num_cols));
        }
        (1, -1) => {
            start_blocks.extend(&add_top(num_rows, num_cols));
            start_blocks.extend(&add_right(num_rows, num_cols));
        }
        (0, 1) => {
            start_blocks.extend(&add_left(num_rows, num_cols));
        }
        _ => {
            println!("Direction passed: {:?}", direction);
            panic!("Unsupported direction")
        }
    }
    fn add_top(_num_rows: usize, num_cols: usize) -> HashSet<(usize, usize)> {
        let mut blocks = HashSet::new();
        for y in 0..num_cols {
            blocks.insert((0, y));
        }
        blocks
    }

    fn add_bottom(num_rows: usize, num_cols: usize) -> HashSet<(usize, usize)> {
        let mut blocks = HashSet::new();
        for y in 0..num_cols {
            blocks.insert((num_rows - 1, y));
        }
        blocks
    }

    fn add_left(num_rows: usize, _num_cols: usize) -> HashSet<(usize, usize)> {
        let mut blocks = HashSet::new();
        for x in 0..num_rows {
            blocks.insert((x, 0));
        }
        blocks
    }

    fn add_right(num_rows: usize, num_cols: usize) -> HashSet<(usize, usize)> {
        let mut blocks = HashSet::new();
        for x in 0..num_rows {
            blocks.insert((x, num_cols - 1));
        }
        blocks
    }
    return start_blocks;
}

fn merge_direction(position: (usize, usize), direction: (i32, i32)) -> Option<(usize, usize)> {
    let new_x = position.0 as i32 + direction.0;
    let new_y = position.1 as i32 + direction.1;

    if new_x < 0 || new_y < 0 {
        return None;
    }

    Some((new_x as usize, new_y as usize))
}
