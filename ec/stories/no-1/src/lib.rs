use core::fmt;
use std::fs::File;
use std::io::BufRead;
use std::io::BufReader;

pub fn load_input(day: &str, part: Part) -> Vec<String> {
    let is_test = parse_test_flag();

    let path = if is_test {
        format!("tests/{}/{}", day, part)
    } else {
        format!("input/{}/{}", day, part)
    };
    let reader = BufReader::new(File::open(path).unwrap());
    return reader.lines().map(|item| item.unwrap()).collect();
}

pub fn parse_test_flag() -> bool {
    let args: Vec<String> = std::env::args().collect();
    return args.iter().any(|arg| arg == "--test");
}

pub enum Part {
    Part1,
    Part2,
    Part3,
}

impl std::fmt::Display for Part {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        let printable = match *self {
            Part::Part1 => "part1",
            Part::Part2 => "part2",
            Part::Part3 => "part3",
        };
        write!(f, "{}", printable)
    }
}

pub fn parse_part() -> Result<Part, String> {
    let args: Vec<String> = std::env::args().collect();

    println!("Got args: {:?}", args);
    let part = args
        .get(1)
        .expect("Usage: cargo run <day> <part> [--test]")
        .to_lowercase();

    match part.as_str() {
        "part1" => return Ok(Part::Part1),
        "part2" => return Ok(Part::Part2),
        "part3" => return Ok(Part::Part3),
        _ => {
            return Err(String::from(
                "Please enter proper part, select either part1, part2, or part3",
            ));
        }
    }
}

pub fn parse_num_from_field_with_equals_split(field: Option<&str>) -> u128 {
    field
        .and_then(|actual| actual.split("=").last())
        .and_then(|value| value.parse::<u128>().ok())
        .expect("Failed to parse value")
}
