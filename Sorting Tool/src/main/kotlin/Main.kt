package sorting

import java.io.File
import java.util.*

val dataTypes = listOf("long", "line", "word")
val sortingTypes = listOf("natural", "byCount")

fun main(args: Array<String>) {

    val scanner = Scanner(System.`in`)
    val numberList = mutableListOf<Int>()
    val stringList = mutableListOf<String>()
    val mapNumbers = mutableMapOf<Int, String>()
    val mapStrings = mutableMapOf<String, String>()

    val dataType: String?
    val sortingType: String?

    try {
        val inputFileName = checkInputArgs(args)
        val outputFileName = checkOutputArgs(args)
        sortingType = checkSortingTypes(args)
        dataType = checkDataType(args)
        checkArgument(args)

        when(inputFileName) {
            null -> {
                when (dataType) {
                    "long" -> {
                        while (scanner.hasNext()) {
                            val temp = scanner.next()
                            if (temp.matches("-?\\d+".toRegex())) numberList.add(temp.toInt())
                            else {
                                outputResult(outputFileName, "\"$temp\" is not a long. It will be skipped.")
                            }
                        }
                        sortingType?.let { sortingAndPrintNumbers(numberList, stringList, mapNumbers, it, outputFileName) }
                    }
                    "line" -> {
                        while (scanner.hasNextLine()) {
                            stringList.add(scanner.nextLine())
                        }
                        sortingType?.let { sortingAndPrintStrings(stringList, mapStrings, it, outputFileName) }
                    }
                    else -> {
                        while (scanner.hasNext()) {
                            stringList.add(scanner.next())
                        }
                        sortingType?.let { sortingAndPrintStrings(stringList, mapStrings, it, outputFileName) }
                    }
                }
            }
            else -> {
                val file = File(inputFileName)
                val text = file.readText()
                val scanner2 = Scanner(text)
                when (dataType) {
                    "long" -> {
                        while (scanner2.hasNext()) {
                            val temp = scanner2.next()
                            if (temp.matches("-?\\d+".toRegex())) numberList.add(temp.toInt())
                            else outputResult(outputFileName, "\"$temp\" is not a long. It will be skipped.")
                        }
                        sortingType?.let {
                            sortingAndPrintNumbers(
                                numberList,
                                stringList,
                                mapNumbers,
                                it,
                                outputFileName
                            )
                        }
                    }
                    "line" -> {
                        while (scanner2.hasNextLine()) { stringList.add(scanner2.nextLine()) }
                        sortingType?.let { sortingAndPrintStrings(stringList, mapStrings, it, outputFileName) }
                    }
                    else -> {
                        while (scanner2.hasNext()) { stringList.add(scanner2.next()) }
                        sortingType?.let { sortingAndPrintStrings(stringList, mapStrings, it, outputFileName) }
                    }
                }
            }
        }
    } catch (e: Exception) {
        println(e.message)
    }
}

fun checkDataType(args: Array<String>): String {
    val temp = if (args.contains("-dataType")) args.indexOf("-dataType") else null
    val result =
        if (temp != null && (temp + 1) < args.size) args[temp + 1] else {
            "line"
//            throw Exception("No data type defined!")
        }
    return if (dataTypes.contains(result)) result else throw Exception("\"$result\" is not a valid parameter. It will be skipped")
}

fun checkSortingTypes(args: Array<String>): String? {
    val temp = if (args.contains("-sortingType")) args.indexOf("-sortingType") else return "natural"
    val result = if ((temp + 1) < args.size) args[temp + 1] else null
    return if (sortingTypes.contains(result)) result else throw Exception("No sorting type defined!")
}

fun checkArgument(args: Array<String>) {
    args.forEach {
        if (it !in dataTypes && it !in sortingTypes && it != "-sortingType" && it != "-dataType" && it != "-inputFile" && it != "-outputFile" && !it.contains(".txt"))
            println("\"$it\" is not a valid parameter. It will be skipped.")
    }
}

fun checkInputArgs(args: Array<String>): String? {
    val result = if (args.contains("-inputFile") && args.indexOf("-inputFile") + 1 < args.size)
        args[args.indexOf("-inputFile") + 1] else null
    return if (result != null && result.contains(".txt")) result else null
}

fun checkOutputArgs(args: Array<String>): String? {
    val result = if (args.contains("-outputFile") && args.indexOf("-outputFile") + 1 < args.size)
        args[args.indexOf("-outputFile") + 1] else null
    return if (result != null && result.contains(".txt")) result else null
}

fun sortingAndPrintNumbers(
    numberList: MutableList<Int>,
    stringList: MutableList<String>,
    mapNumbers: MutableMap<Int, String>,
    sortingType: String,
    outputName: String?
) {
    when (sortingType) {
        "byCount" -> {
            numberList.sorted().forEach { stringList.add(it.toString()) }
            val result = stringList.toSet()
            for (i in result) {
                val temp = numberList.count { it == i.toInt() }
                mapNumbers[i.toInt()] = temp.toString()
            }
            val newMap = mapNumbers.toList().sortedBy { (_, value) -> value }.toMap()
            outputResult(outputName, "Total numbers: ${numberList.size}")
            newMap.forEach {
                val temp = numberList.count { numb -> numb == it.key }
                outputResult(outputName, "${it.key}: ${it.value} time(s), ${temp * 100 / numberList.size}%")
            }
        }
        "natural" -> {
            outputResult(outputName, "Total numbers: ${numberList.size}")
            outputResult(outputName, numberList.sorted().joinToString(" "))
        }
    }
}

fun sortingAndPrintStrings(
    stringList: MutableList<String>,
    mapStrings: MutableMap<String, String>,
    sortingType: String,
    outputName: String?
) {
    when (sortingType) {
        "byCount" -> {
            val result = stringList.sorted().toSet()
            for (i in result) {
                val temp = stringList.count { it == i }
                mapStrings[i] = temp.toString()
            }
            val newMap = mapStrings.toList().sortedBy { (_, value) -> value }.toMap()
            outputResult(outputName, "Total lines: ${stringList.size}")
            newMap.forEach {
                val temp = stringList.count { str -> str == it.key }
                outputResult(outputName, "${it.key}: ${it.value} time(s), ${temp * 100 / stringList.size}%")
            }
        }
        "natural" -> {
            stringList.sortByDescending { it.length }
            outputResult(outputName, "Total lines: ${stringList.size}")
            stringList.forEach { outputResult(outputName, it) }
        }
    }
}

fun outputResult(outputName: String?, message: String) {
    if (outputName != null) {
        val outputFile = File(outputName)
        if (outputName.isEmpty()) outputFile.writeText("$message\n")
        else outputFile.appendText("$message\n")
    } else {
        println(message)
    }
}