package day03

import scala.math.Ordering

import locations.Directory.currentDir
import inputs.Input.loadFileSync
import scala.annotation.switch
import scala.collection.mutable.Map

@main def part1: Unit =
    println(s"The solution is ${part1(loadInput())}")

@main def part2: Unit =
    println(s"The solution is ${part2(loadInput())}")

def loadInput(): String = loadFileSync(s"$currentDir/../input/day03")

def part1(input: String): String = findPartNumbers(input).toString

def findPartNumbers(input: String): Int = {
    val grid = createGrid(input)
    var sum = 0
    for (i <- grid.indices)
        var j = 0
        while (j < grid(i).length)
            grid(i)(j) match {
                case x if x.isDigit => {
                    val (fullNumber, ending) = findNumberRemainder(grid(i), j)
                    if isPartNumber(grid, i, j, ending) then
                        sum = sum + fullNumber
                    j = ending + 1
                }
                case x => j += 1
            }
    return sum
}

// Ending is at one after the number
def findNumberRemainder(row: List[Char], position: Int): (Int, Int) = {
    var charList: List[Char] = List()
    var counter = position
    while (counter < row.length && row(counter).isDigit)
        charList = charList :+ row(counter)
        counter += 1
    if charList.isEmpty then charList = List('0')
    (charList.mkString.toInt, counter)
}

def isPartNumber(
    grid: List[List[Char]],
    row: Int,
    start: Int,
    end: Int
): Boolean = {
    val startRow = 0.max(row - 1)
    val endRow = (grid.length - 1).min(row + 1)

    val startSpot = 0.max(start - 1)
    val endSpot = grid(row).length.min(end + 1)

    var isValidNumber = false

    for (i <- startRow until endRow + 1)
        for (j <- startSpot until endSpot)
            if grid(i)(j).isSymbol then isValidNumber = true
    isValidNumber
}

def createGrid(input: String): List[List[Char]] =
    input.split('\n').map(_.toList).toList

extension (c: Char)
    def isSymbol = c match {
        case '=' | '&' | '#' | '+' | '/' | '*' | '%' | '-' | '@' | '$' => true
        case _                                                         => false
    }
    def isGear = c match {
        case '*' => true
        case _   => false
    }

def part2(input: String): String = findGearNumbers(input).toString

def findGearNumbers(input: String): Int = {
    val grid = createGrid(input)
    var gearMap: Map[(Int, Int), List[Int]] = Map.empty
    for (i <- grid.indices) {
        var j = 0
        while (j < grid(i).length)
            grid(i)(j) match {
                case x if x.isDigit => {
                    val (fullNumber, ending) = findNumberRemainder(grid(i), j)
                    makeGearMap(gearMap, grid, i, j, ending, fullNumber)
                    j = ending + 1
                }
                case x => j += 1
            }
    }
    return gearMap.values
        .filter(x => x.length == 2)
        .map(x => x.foldLeft(1)(_ * _))
        .sum
}

def makeGearMap(
    gearMap: Map[(Int, Int), List[Int]],
    grid: List[List[Char]],
    row: Int,
    start: Int,
    ending: Int,
    fullNumber: Int
): Unit = {
    val startRow = 0.max(row - 1)
    val endRow = (grid.length - 1).min(row + 1)

    val startSpot = 0.max(start - 1)
    val endSpot = grid(row).length.min(ending + 1)

    for (i <- startRow until endRow + 1)
        for (j <- startSpot until endSpot)
            if grid(i)(j).isGear then {
                if gearMap.contains((i, j)) then {
                    gearMap((i, j)) = fullNumber :: gearMap((i, j))
                } else gearMap((i, j)) = fullNumber :: Nil
            }
}
