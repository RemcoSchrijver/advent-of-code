package day07

import scala.math.Ordering

import locations.Directory.currentDir
import inputs.Input.loadFileSync
import scala.collection.mutable.Map
import java.nio.charset.MalformedInputException

@main def part1: Unit =
    println(s"The solution is ${part1(loadInput())}")

@main def part2: Unit =
    println(s"The solution is ${part2(loadInput())}")

def loadInput(): String = loadFileSync(s"$currentDir/../input/day07")

def part1(input: String): String = determineScore(input).toString

def part2(input: String): String = determineScore(input, true).toString 

def determineScore(input: String, jokerRules: Boolean = false): Int = {
    val hands = parseHands(input)

    val sortedHands = hands.sortWith((a1, a2) => handWins(a1._1, a2._1, jokerRules))
    calculateScore(sortedHands)
}

def calculateScore(list: List[(String, Int)]): Int = list match {
    case Nil              => 0
    case (_, bid) :: tail => bid * list.length + calculateScore(tail)
}

def parseHands(input: String): List[(String, Int)] = {
    val lines = input.split('\n')
    lines
        .map(line => {
            val split = line.split(' ')
            (split(0), split(1).toInt)
        })
        .toList
}

def handWins(
    input1: String,
    input2: String,
    jokerRules: Boolean = false
): Boolean = {
    val type1 = returnHandType(input1, jokerRules)
    val type2 = returnHandType(input2, jokerRules)
    if type1.ordinal == type2.ordinal then
        handIsLarger(input1.toList, input2.toList, jokerRules)
    else if type1.ordinal < type2.ordinal then true
    else false
}

enum HandTypes():
    case FiveOfAKind extends HandTypes()
    case FourOfAKind extends HandTypes()
    case FullHouse extends HandTypes()
    case ThreeOfAKind extends HandTypes()
    case TwoPair extends HandTypes()
    case OnePair extends HandTypes()
    case HighCard extends HandTypes()

def returnHandType(hand: String, jokerRules: Boolean): HandTypes = {
    val xs = hand.toList
    val distinct = xs.distinct
    if (jokerRules && distinct.contains('J'))
        val jokerAmount = xs.count(_ == 'J')
        distinct.length match
            case 1 | 2 => HandTypes.FiveOfAKind
            case 3 =>
                val jokerless = distinct.filterNot(_ == 'J')
                val n1 = xs.count(_ == jokerless(0))
                val n2 = xs.count(_ == jokerless(1))
                if (n1 == 3 || n2 == 3 || jokerAmount >= 2) then HandTypes.FourOfAKind
                else HandTypes.FullHouse
            case 4 => HandTypes.ThreeOfAKind
            case _ => HandTypes.OnePair
    else
        distinct.length match {
            case 1 => HandTypes.FiveOfAKind
            case 2 => {
                val n1 = xs.count(_ == distinct(0))
                val n2 = xs.count(_ == distinct(1))
                if (n1 == 4 || n2 == 4) then HandTypes.FourOfAKind
                else HandTypes.FullHouse
            }
            case 3 => {
                val n1 = xs.count(_ == distinct(0))
                val n2 = xs.count(_ == distinct(1))
                val n3 = xs.count(_ == distinct(2))
                if (n1 == 3 || n2 == 3 || n3 == 3) then HandTypes.ThreeOfAKind
                else HandTypes.TwoPair
            }
            case 4 => HandTypes.OnePair
            case _ => HandTypes.HighCard
        }
}

def handIsLarger(
    hand1: List[Char],
    hand2: List[Char],
    jokerRules: Boolean
): Boolean =
    (hand1, hand2) match {
        case (Nil, Nil) =>
            true // Complete tie so just give it to the first I guess?
        case (x :: xs, y :: ys) => {
            if (cardToInt(x, jokerRules) < cardToInt(y, jokerRules)) then true
            else if (cardToInt(x, jokerRules) > cardToInt(y, jokerRules)) then
                false
            else handIsLarger(xs, ys, jokerRules)
        }
        case _ =>
            throw new RuntimeException(
              s"Comparing these hands $hand1 and $hand2"
            )
    }

def cardToInt(char: Char, jokerRules: Boolean): Int = char match {
    case 'A' => 0
    case 'K' => 1
    case 'Q' => 2
    case 'J' =>
        jokerRules match {
            case true  => 13
            case false => 3
        }
    case 'T' => 4
    case '9' => 5
    case '8' => 6
    case '7' => 7
    case '6' => 8
    case '5' => 9
    case '4' => 10
    case '3' => 11
    case '2' => 12
}

