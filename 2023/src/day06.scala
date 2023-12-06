package day06

import scala.math.Ordering

import locations.Directory.currentDir
import inputs.Input.loadFileSync
import scala.collection.mutable.Map
import java.nio.charset.MalformedInputException

@main def part1: Unit =
    println(s"The solution is ${part1(loadInput())}")

@main def part2: Unit =
    println(s"The solution is ${part2(loadInput())}")

def loadInput(): String = loadFileSync(s"$currentDir/../input/day06")

def part1(input: String): String = calculateBeatingOfRecords(input).toString

def parseInput(input: String): List[(Long, Long)] = {
    val lines = input.split('\n')
    val times = lines(0).split(':')(1)
    val distances = lines(1).split(':')(1)

    (times
        .split(' ')
        .filter(str => !str.isBlank())
        .map(_.toLong)
        .toList zip distances
        .split(' ')
        .filter(str => !str.isBlank())
        .map(_.toLong)
        .toList)
}

def calculateBeatingOfRecords(input: String): Long = {
    val timeDistancePairs = parseInput(input)
    val results = timeDistancePairs.map(solveParabola)

    results.foldLeft(1.toLong)(_ * _)
}

// Calculate ax^2 - bx - c = y, easy given because we start at origin (0,0) => c = 0
// where x is our time and y is our distance
def solveParabola(input: (Long, Long)): Long = input match {
    case (time, distance) => {
        // We know for x = time then y = 0 so:
        // And we know that a = -1a
        // And we know that b = xMax so time
        val a = -1
        val b = time

        // Time to calculate intersection with abc-formula, we floor everything because
        // this gives us the correct amount of possibilities.
        val x1 =
            ((-1 * b - math.sqrt(math.pow(b, 2) - 4 * distance)) / -2).floor
        val x2 =
            ((-1 * b + math.sqrt(math.pow(b, 2) - 4 * distance)) / -2).floor

        // Time to return the range of x that is above our intersection, except if we are
        // exactly on our distance then we have to drop one of our posibilities.
        val result = (x1 - x2).toLong
        if calculateBoatParabola(x1, a, b) == distance
            && calculateBoatParabola(x2, a, b) == distance
        then result - 1
        else result
    }
}

def calculateBoatParabola(x: Double, a: Long, b: Long) =
    math.pow(x, 2) * a + x * b

def part2(input: String): String = solveSingleRecord(input).toString

def solveSingleRecord(input: String): Long = {
    val lines = input.split('\n')
    val time = lines(0).split(':')(1).filterNot(_.isWhitespace).toLong
    val distance = lines(1).split(':')(1).filterNot(_.isWhitespace).toLong

    solveParabola((time, distance))
}
