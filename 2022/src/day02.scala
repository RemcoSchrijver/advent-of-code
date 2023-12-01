package day02

import scala.math.Ordering

import locations.Directory.currentDir
import inputs.Input.loadFileSync

// Not the most optimal solution here feel like I could do something
// clever with the ascii codes here.

@main def part1: Unit =
    println(s"The solution is ${part1(loadInput())}")

@main def part2: Unit =
    println(s"The solution is ${part2(loadInput())}")

def loadInput(): String = loadFileSync(s"$currentDir/../input/day02")

def part1(input: String): String =
    input.split("\n").map(calculateScore).sum.toString

val shapeScore = Map[String, Int](
  "X" -> 1,
  "A" -> 1,
  "Y" -> 2,
  "B" -> 2,
  "Z" -> 3,
  "C" -> 3
)

def calculateScore(input: String): Int = {
    val game = input.split(" ")
    val play1 = game(0)
    val play2 = game(1)

    shapeScore(play2) + calculateMatchScore(play1, play2)
}

def calculateMatchScore(play1: String, play2: String): Int = play1 match {
    case "A" =>
        play2 match {
            case "X" => 3
            case "Y" => 6
            case "Z" => 0
        }
    case "B" =>
        play2 match {
            case "X" => 0
            case "Y" => 3
            case "Z" => 6
        }
    case "C" =>
        play2 match {
            case "X" => 6
            case "Y" => 0
            case "Z" => 3
        }
}

def part2(input: String): String =
    input.split("\n").map(calculateStrategyScore).sum.toString

val strategyScore = Map[String, Int](
  "X" -> 0,
  "Y" -> 3,
  "Z" -> 6
)

def calculateStrategyScore(input: String): Int = {
  val game = input.split(" ")
  val play1 = game(0)
  val strategy = game(1)

  strategyScore(strategy) + shapeScore(determineShapeScore(play1, strategy))
}

def determineShapeScore(play1: String, strategy: String): String = strategy match {
    case "X" =>
        play1 match {
            case "A" => "C"
            case "B" => "A"
            case "C" => "B"
        }
    case "Y" =>
        play1 match {
            case "A" => "A"
            case "B" => "B"
            case "C" => "C"
        }
    case "Z" =>
        play1 match {
            case "A" => "B"
            case "B" => "C"
            case "C" => "A"
        }

}
