package day01

import scala.math.Ordering

import locations.Directory.currentDir
import inputs.Input.loadFileSync

@main def part1: Unit =
    println(s"The solution is ${part1(loadInput())}")

@main def part2: Unit =
    println(s"The solution is ${part2(loadInput())}")

def loadInput(): String = loadFileSync(s"$currentDir/../input/day01")

def part1(input: String): String = createCalibrationsList(input).sum.toString

def createCalibrationsList(input: String): List[Int] =
    input.split("\n").toList.map(calibrationStringToInt)

def calibrationStringToInt(input: String): Int = {
    val digitsList = input.replaceAll("[^\\d]", "").toList
    val digit = (digitsList.head.toString + digitsList.last.toString).toInt
    digit
}

val wordMapping = Map(
  "one" -> 1,
  "two" -> 2,
  "three" -> 3,
  "four" -> 4,
  "five" -> 5,
  "six" -> 6,
  "seven" -> 7,
  "eight" -> 8,
  "nine" -> 9
)

def part2(input: String): String = createCalibrationsListWithWords(
  input
).sum.toString

def createCalibrationsListWithWords(input: String): List[Int] =
    input.split("\n").toList.map(calibrationStringWithWordsToInt)

def calibrationStringWithWordsToInt(input: String): Int = {
    val transformedInput = transformWordsToInts(input, wordMapping)
    val digitsList = transformedInput.replaceAll("[^\\d]", "").toList
    val digit = (digitsList.head.toString + digitsList.last.toString).toInt
    digit
}

def transformWordsToInts(input: String, mapping: Map[String, Int]): String = {
    val indexOfWords =
        mapping.map((key, value) =>
            value -> findAllIndicesOfWord(input, key, 0)
        )
    patchAllWords(input, indexOfWords.keys.toList, indexOfWords.values.toList)
}

def findAllIndicesOfWord(input: String, word: String, index: Int): List[Int] =
    input.indexOf(word, index) match {
        case -1 => Nil
        case x  => x :: findAllIndicesOfWord(input, word, x + 1)
    }

def patchAllWords(
    input: String,
    keys: List[Int],
    values: List[List[Int]]
): String = (keys, values) match {
    case (numericValue :: ns, indices :: is) =>
        patchAllWords(patchString(input, numericValue, indices), ns, is)
    case (_, _) => input
}

def patchString(input: String, numericValue: Int, indices: List[Int]): String =
    indices match {
        case Nil => input
        case index :: xs =>
            patchString(
              input.patch(index, numericValue.toString, 1),
              numericValue,
              xs
            )
    }
