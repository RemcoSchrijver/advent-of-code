package day05

import scala.math.Ordering

import locations.Directory.currentDir
import inputs.Input.loadFileSync
import scala.collection.mutable.Map
import java.nio.charset.MalformedInputException

@main def part1: Unit =
    println(s"The solution is ${part1(loadInput())}")

@main def part2: Unit =
    println(s"The solution is ${part2(loadInput())}")

def loadInput(): String = loadFileSync(s"$currentDir/../input/day05")

val seedsString = "seeds: "
val mappingStrings = List(
  "seed-to-soil map:",
  "soil-to-fertilizer map:",
  "fertilizer-to-water map:",
  "water-to-light map:",
  "light-to-temperature map:",
  "temperature-to-humidity map:",
  "humidity-to-location map:"
)

def part1(input: String): String = getClosestSeed(input).toString

def getClosestSeed(input: String): Long = {
    val lines = input.split('\n').toList
    val (seeds, remainder) = getSeeds(lines)
    val allMappings = createAllMappings(mappingStrings, remainder)

    calculateLocations(seeds, allMappings).min
}

def getSeeds(lines: List[String]): (List[Long], List[String]) = lines match {
    case Nil => throw new RuntimeException("It seems you don't have any seeds")
    case x :: xs =>
        x match {
            case x if x.startsWith(seedsString) => {
                val seeds = x.drop(seedsString.length)
                (seeds.split(' ').map(_.toLong).toList, xs)
            }
        }
}

class Mapping(target: Long, source: Long, range: Long) {
    def isInMapping(input: Long): Boolean = input match {
        case i if i >= source && i < source + range => true
        case _                                      => false
    }

    def translate(input: Long): Long = isInMapping(input) match {
        case true => target + (input - source)
        case false =>
            throw new RuntimeException("Input number is not in mapping")
    }

    def getMappingRanges(
        valRange: Range
    ): (Option[Range], Option[Range], Option[Range]) =
        (valRange.begin, valRange.ending) match {
            case (begin, ending) if begin < source && ending < source =>
                (Some(valRange), None, None)
            case (begin, ending) if begin >= (source + range) =>
                (None, None, Some(valRange))
            case (begin, ending)
                if begin < source && ending >= source + range =>
                (
                  Some(new Range(begin, source - 1)),
                  Some(
                    new Range(translate(source), translate(source + range - 1))
                  ),
                  Some(new Range(source + range, ending))
                )
            case (begin, ending) if begin < source =>
                (
                  Some(new Range(begin, source - 1)),
                  Some(new Range(translate(source), translate(ending))),
                  None
                )
            case (begin, ending) if ending >= source + range =>
                (
                  None,
                  Some(
                    new Range(translate(begin), translate(source + range - 1))
                  ),
                  Some(new Range(source + range, ending))
                )
            case (begin, ending) =>
                (
                  None,
                  Some(new Range(translate(begin), translate(ending))),
                  None
                )
        }

    override def toString(): String =
        (target :: source :: range :: Nil).toString
}

class Range(val begin: Long, val ending: Long) {
    def toLongs: List[Long] = begin :: ending :: Nil
    override def toString(): String = toLongs.toString()
}

def createAllMappings(
    mappingString: List[String],
    list: List[String]
): List[List[Mapping]] = mappingString match {
    case Nil => Nil
    case x :: xs => {
        val (mapping, remainder) = getMapping(x, list)
        mapping :: createAllMappings(xs, remainder)
    }
}

def getMapping(
    mappingString: String,
    list: List[String]
): (List[Mapping], List[String]) =
    list match {
        case Nil                           => (Nil, Nil)
        case x :: xs if x == mappingString => createMapping(xs)
        case x :: xs                       => getMapping(mappingString, xs)
    }

def createMapping(list: List[String]): (List[Mapping], List[String]) =
    list match {
        case Nil                => (Nil, Nil)
        case x :: xs if x == "" => (Nil, xs)
        case x :: xs => {
            val numbers = x.split(' ').map(_.toLong)
            val (mapping, remainder) = createMapping(xs)
            (
              Mapping(numbers(0), numbers(1), numbers(2)) :: mapping,
              remainder
            )
        }
    }

def calculateLocations(
    seeds: List[Long],
    mappings: List[List[Mapping]]
): List[Long] = {
    val total = seeds.length
    var counter = 0
    seeds.map(seed => processMappings(seed, mappings))
}

def processMappings(input: Long, mappings: List[List[Mapping]]): Long =
    mappings match {
        case Nil => input
        case mapping :: remainder =>
            processMappings(processMapping(input, mapping), remainder)
    }

def processMapping(input: Long, mapping: List[Mapping]): Long =
    mapping match {
        case Nil => input
        case map :: remainder if map.isInMapping(input) =>
            map.translate(input)
        case _ :: remainder => processMapping(input, remainder)
    }

def part2(input: String): String = calculateClosestsOfSeedRanges(
  input
).toString

def calculateClosestsOfSeedRanges(input: String): Long = {
    val lines = input.split('\n').toList
    val (seeds, remainder) = getSeedRanges(lines)
    val allMappings = createAllMappings(mappingStrings, remainder)

    calculateLocationRanges(seeds, allMappings).min
}

def getSeedRanges(input: List[String]): (List[Range], List[String]) =
    input match {
        case Nil =>
            throw new RuntimeException("It seems you don't have any seeds")
        case x :: xs =>
            x match {
                case x if x.startsWith(seedsString) => {
                    val seeds = x.drop(seedsString.length)
                    (
                      getSeedRange(seeds.split(' ').map(_.toLong).toList),
                      xs
                    )
                }
            }
    }

def getSeedRange(input: List[Long]): List[Range] = input match {
    case Nil => Nil
    case _ :: Nil =>
        throw new RuntimeException(
          "Seems there are seeds that don't have a range"
        )
    case start :: range :: xs =>
        new Range(start, start + range - 1) :: getSeedRange(xs)
}

def calculateLocationRanges(
    seeds: List[Range],
    mappings: List[List[Mapping]]
): List[Long] = {
    val total = seeds.length
    var counter = 0
    processMappings(seeds, mappings).map(range => range.toLongs).flatten
}

def processMappings(
    inputs: List[Range],
    mappings: List[List[Mapping]]
): List[Range] =
    mappings match {
        case Nil => inputs
        case mapping :: remainder =>
            val result = processMappings(
              inputs.map(input => processMapping(input, mapping)).flatten,
              remainder
            )
            result
    }

def processMapping(input: Range, mapping: List[Mapping]): List[Range] =
    mapping match {
        case Nil => input :: Nil
        case map :: remainder =>
            map.getMappingRanges(input) match {
                case (None, None, None)        => Nil
                case (None, Some(range), None) => range :: Nil
                case (Some(before), None, None) =>
                    processMapping(before, remainder)
                case (Some(before), Some(range), None) =>
                    range :: processMapping(before, remainder)
                case (Some(before), Some(range), Some(after)) =>
                    range :: processMapping(
                      before,
                      remainder
                    ) ++ processMapping(after, remainder)
                case (None, Some(range), Some(after)) =>
                    range :: processMapping(after, remainder)
                case (None, None, Some(after)) =>
                    processMapping(after, remainder)
                case (Some(before), None, Some(after)) =>
                    throw new RuntimeException("This shouldn't be possible?")
            }
    }
