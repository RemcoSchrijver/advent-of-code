package day08

import scala.math.Ordering

import locations.Directory.currentDir
import inputs.Input.loadFileSync
import scala.collection.mutable.Map
import java.nio.charset.MalformedInputException

@main def part1: Unit =
    println(s"The solution is ${part1(loadInput())}")

@main def part2: Unit =
    println(s"The solution is ${part2(loadInput())}")

def loadInput(): String = loadFileSync(s"$currentDir/../input/day08")

val ending = "ZZZ"
val start = "AAA"

def part1(input: String): String = stepsUntilHome(input).toString

var globalMapping: Map[String, (String, String)] = Map.empty

def stepsUntilHome(input: String): Int = {
    val (instructions, mappingList) = readInstructions(input.split('\n').toList)
    val mapping = createMapping(mappingList)
    globalMapping = mapping

    var done = false
    var result = 0
    var startPoint = start
    while (!done) {
        val (isDone, nextPoint) = countSteps(instructions.toList, startPoint)
        done = isDone
        startPoint = nextPoint
        result += instructions.length
    }
    result
}

def countSteps(
    instructions: List[Char],
    currentPoint: String
): (Boolean, String) = {
    instructions match {
        case Nil if currentPoint.equals(ending) => (true, currentPoint)
        case Nil                                => (false, currentPoint)
        case x :: xs => {
            val (left, right) = globalMapping.getOrElse(
              currentPoint,
              throw new RuntimeException(
                "uh oh this is a non-existent label for a node"
              )
            )
            x match {
                case 'L' => countSteps(xs, left)
                case 'R' => countSteps(xs, right)
                case _   => throw new RuntimeException("Illegal character")
            }
        }
    }
}

def createMapping(mappingStrings: List[String]): Map[String, (String, String)] =
    mappingStrings match {
        case Nil => Map.empty
        case mapString :: tail => {
            val splitted = mapString.split(" = ")
            val source = splitted(0)
            val targets = splitted(1).split(", ")
            val parsedTarget = (targets(0).drop(1), targets(1).dropRight(1))

            Map(source -> parsedTarget) ++ createMapping(tail)
        }
    }

def readInstructions(input: List[String]): (String, List[String]) =
    input match {
        case instructions :: whitespace :: remainder =>
            (instructions, remainder)
        case _ => throw new RuntimeException("Input is malformed")
    }

def part2(input: String): String = findStepsForMultiple(input).toString 

def findStepsForMultiple(input: String): Long = {
    val (instructions, mappingList) = readInstructions(input.split('\n').toList)
    val mapping = createMapping(mappingList)
    globalMapping = mapping

    var done = false
    var result : Long = 0
    var startingPositions: List[String] =
        globalMapping.keys.filter(position => position.last == 'A').toList
    while (!done) {
        val (iterations, isDone, nextPoints) =
            countStepsMultiple(instructions.toList, startingPositions, result)
        done = isDone
        startingPositions = nextPoints
        result = iterations
    }
    result
}

def countStepsMultiple(
    instructions: List[Char],
    currentPoints: List[String],
    currentIteration: Long 
): (Long, Boolean, List[String]) = {
    checkIfEndIsReach(currentPoints) match {
        case true => (currentIteration, true, Nil)
        case false =>
            instructions match {
                case Nil => (currentIteration, false, currentPoints)
                case x :: xs => {
                    val points = currentPoints.map(currentPoint =>
                        globalMapping.getOrElse(
                          currentPoint,
                          throw new RuntimeException(
                            "uh oh this is a non-existent label for a node"
                          )
                        )
                    )
                    x match {
                        case 'L' =>
                            countStepsMultiple(
                              xs,
                              points.map(point => point._1),
                              1 + currentIteration
                            )
                        case 'R' =>
                            countStepsMultiple(
                              xs,
                              points.map(point => point._2),
                              1 + currentIteration
                            )
                        case _ =>
                            throw new RuntimeException("Illegal character")
                    }
                }
            }
    }
}

def checkIfEndIsReach(currentPoints: List[String]): Boolean =
    currentPoints match {
        case Nil => true
        case point :: tail if point.last == 'Z' =>
            true && checkIfEndIsReach(tail)
        case _ => false
    }
