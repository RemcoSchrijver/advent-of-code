package day02

import scala.math.Ordering

import locations.Directory.currentDir
import inputs.Input.loadFileSync

@main def part1: Unit =
    println(s"The solution is ${part1(loadInput())}")

@main def part2: Unit =
    println(s"The solution is ${part2(loadInput())}")

def loadInput(): String = loadFileSync(s"$currentDir/../input/day02")

def part1(input: String): String = parseGames(input).toString

val red = "red"
val green = "green"
val blue = "blue"

def parseGames(input: String): Int = {
    val games = input.split("\n").map(parseGame)
    games
        .map(game => if game.rounds.foldLeft(true)(_ && _) then game.id else 0)
        .sum
}

def parseGame(input: String): Game = {
    val firstSplit = input.split(':')
    val id = firstSplit(0).drop(5).toInt

    val rounds = firstSplit(1).split(';')
    Game(id, parseRounds(rounds.toList))
}

def parseRounds(roundString: List[String]): List[Boolean] = roundString match {
    case Nil => Nil
    case r :: rs => {
        val cubes = r.split(',')
        cubes
            .map(cube =>
                cube match {
                    case x if x.endsWith(red) =>
                        x.stripSuffix(red).strip().toInt <= 12
                    case x if x.endsWith(green) =>
                        x.stripSuffix(green).strip().toInt <= 13
                    case x if x.endsWith(blue) =>
                        x.stripSuffix(blue).strip().toInt <= 14
                }
            )
            .toList
            .foldLeft(true)((a, b) => a && b) :: parseRounds(rs)
    }
}

class Game(val id: Int, val rounds: List[Boolean])

def part2(input: String): String = parseMinimalGames(input).toString

def parseMinimalGames(input: String): Int = {
    input.split("\n").map(parseMinimalCubes).sum
}

def parseMinimalCubes(input: String): Int = {
    val firstSplit = input.split(':')

    val cubes = firstSplit(1).split(';').map(s => s.split(',')).flatten.toList
    parseRounds(cubes, 1, 1, 1)
}

def parseRounds(
    cubes: List[String],
    redVal: Int,
    greenVal: Int,
    blueVal: Int
): Int = cubes match {
    case Nil => redVal * greenVal * blueVal
    case r :: rs =>
        r match {
            case x if x.endsWith(red) =>
                parseRounds(
                  rs,
                  parseRound(x, red, redVal),
                  greenVal,
                  blueVal
                )
            case x if x.endsWith(green) =>
                parseRounds(
                  rs,
                  redVal,
                  parseRound(x, green, greenVal),
                  blueVal
                )
            case x if x.endsWith(blue) =>
                parseRounds(
                  rs,
                  redVal,
                  greenVal,
                  parseRound(x, blue, blueVal)
                )
        }

}

def parseRound(input: String, matchString: String, baseValue: Int): Int =
    baseValue.max(input.stripSuffix(matchString).strip().toInt)
