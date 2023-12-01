package day01

import scala.math.Ordering

import locations.Directory.currentDir
import inputs.Input.loadFileSync

@main def part1: Unit =
    println(s"The solution is ${part1(loadInput())}")

@main def part2: Unit =
    println(s"The solution is ${part2(loadInput())}")

def loadInput(): String = loadFileSync(s"$currentDir/../input/day01")

def part1(input: String): String =
    createCaloriesList(input).max.toString()

def part2(input: String): String =
    createCaloriesList(input).sorted.reverse.take(3).sum.toString()

def createCaloriesList(input: String): List[Int] =
    val list = input.split('\n').toList
    collectCalories(list)

def collectCalories(input: List[String]): List[Int] = input match {
    case Nil      => Nil
    case "" :: xs => collectCalories(xs)
    case x :: xs => {
        val calories =
            xs.takeWhile(_ != "").foldLeft(x.toInt)((a, b) => a.toInt + b.toInt)
        calories :: collectCalories(xs.dropWhile(_ != ""))
    }

}
