package day04

import scala.math.Ordering

import locations.Directory.currentDir
import inputs.Input.loadFileSync
import scala.collection.mutable.Map

@main def part1: Unit =
    println(s"The solution is ${part1(loadInput())}")

// Part two is overoptimized because I read the problem wrong, increasing my problem space enormously.
// So now with hashmaps it's really fast now though.
@main def part2: Unit =
    println(s"The solution is ${part2(loadInput())}")

def loadInput(): String = loadFileSync(s"$currentDir/../input/day04")

def part1(input: String): String = input.split('\n').map(parseCard).sum.toString

def parseCard(input: String): Int = {
    val amountOfWinningNumbers = parseCardCorrectNumbers(input)
    val result = math.pow(2, amountOfWinningNumbers - 1).toInt
    cardMap(input) = result
    result
}

var cardMap: Map[String, Int] = Map.empty

def parseCardCorrectNumbers(input: String): Int = {
    if cardMap.contains(input) then cardMap(input)
    else {
        val numbers = input.split(':')(1).split('|')
        val winningNumbers = numbers(0).split(' ').filter(x => x != "")
        val cardNumbers = numbers(1).split(' ').filter(x => x != "")

        val correctNumbers = cardNumbers.foldLeft(0)((a, elem) => {
            if (winningNumbers.contains(elem)) then {
                a + 1
            } else {
                a + 0
            }
        })
        cardMap(input) = correctNumbers
        correctNumbers
    }
}

def part2(input: String): String = {
    val list = input.split('\n').toList
    countWonCards(list.length, list).toString
}

var wonCardsMap: Map[(Int, List[String]), Int] = Map.empty

def countWonCards(counter: Int, cards: List[String]): Int = counter match {
    case 0 => 0
    case _ =>
        cards match {
            case Nil => 0
            case card :: cs => {
                val winnings = parseCardCorrectNumbers(card)

                var subCards = 0
                if wonCardsMap.contains((winnings, cs)) then
                    subCards = wonCardsMap((winnings, cs))
                else {
                    subCards = countWonCards(winnings, cs)
                    wonCardsMap((winnings, cs)) = subCards
                }
                val result = 1 + countWonCards(counter - 1, cs) + subCards
                if (result < 0 || result > 10000) {
                    println(result)
                }
                result
            }
        }
}
