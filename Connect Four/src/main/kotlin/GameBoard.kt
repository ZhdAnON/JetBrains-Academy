import java.util.*

class GameBoard {
    private var rows = 0
    private var columns = 0
    private var boardList = mutableListOf<MutableList<Char>>()
    private var player1Name = ""
    private var player2Name = ""
    private var firstPlayer = true
    private var gameCount: Int
    private var player1Count = 0
    private var player2Count = 0

    init {
        println("Connect Four")
        setPlayersName()
        setGameBoardSize()
        gameCount = setGameCount()
        boardList = MutableList(rows) { MutableList(columns) { ' ' } }
        println(
            """|$player1Name VS $player2Name
        |$rows X $columns board""".trimMargin()
        )
        println(if (gameCount != 1) "Total $gameCount games" else "Single game")
    }

    //    setting game parameters
    private fun setPlayersName() {
        println("First player's name:")
        player1Name = readLine()!!
        println("Second player's name:")
        player2Name = readLine()!!
    }

    private fun setGameBoardSize() {
        val regex = Regex("""^\s*\d+\s*[xX]{1}\s*\d+\s*$""")
        while (true) {
            println(
                """Set the board dimensions (Rows x Columns)
            |Press Enter for default (6 x 7)""".trimMargin()
            )
            val boardSize = readLine()
            if (boardSize!!.isEmpty()) {
                rows = 6
                columns = 7
                break
            }
            if (boardSize.matches(regex)) {
                val boardSizeTemp = boardSize.split("x", ignoreCase = true).toMutableList()
                val scanner0 = Scanner(boardSizeTemp[0])
                val scanner1 = Scanner(boardSizeTemp[1])
                val tempRows = scanner0.nextInt()
                val tempColumns = scanner1.nextInt()
                if (tempRows in 5..9) {
                    rows = tempRows
                    if (tempColumns in 5..9) {
                        columns = tempColumns
                        break
                    } else println("Board columns should be from 5 to 9")
                } else println("Board rows should be from 5 to 9")
            } else println("Invalid input")
        }
    }

    private fun setGameCount(): Int {
        var tempCount = 1
        while (true) {
            println(
                """Do you want to play single or multiple games?
For a single game, input 1 or press Enter
Input a number of games:"""
            )
            val input = readLine()!!
            if (Regex("\\d+").matches(input) || input.isEmpty()) {
                if (input == "" || input.toInt() == 1) break
                else if (input.toInt() > 1) {
                    tempCount = input.toInt()
                    break
                } else println("Invalid input")
            } else println("Invalid input")
        }
        return tempCount
    }

    //    game board printing
    private fun printBoardList() {
        repeat(columns) { print(" ${it + 1}") }
        println()
        for (row in boardList.size - 1 downTo 0) {
            for (column in boardList[row].indices) {
                print("║${boardList[row][column]}")
            }
            println("║")
        }
        println("╚═" + "╩═".repeat(columns - 1) + "╝")
    }

    //    game process
    fun playGame() {
        var counter = 0
        var isFirstPlayer = firstPlayer
        multiply@ do {
            if (gameCount != 1) println("Game #${counter + 1}")
            printBoardList()
            var currentPlayer: String
            input@ while (true) {
                val symbol = if (isFirstPlayer) {
                    println("$player1Name's turn:"); currentPlayer = player1Name; 'o'
                } else {
                    println("$player2Name's turn:"); currentPlayer = player2Name; '*'
                }
                val input = readLine()
                if (input!! == "end") {
                    break@multiply
                }
                if (Regex("\\d+").matches(input)) {
                    if (input.toInt() !in 1..columns)
                        println("The column number is out of range (1 - $columns)")
                    else {
                        val number = input.toInt() - 1
                        if (input.toInt() in 1..columns) {
                            var count = 0
                            for (i in boardList.indices) {
                                if (boardList[i][number] != 'o' && boardList[i][number] != '*') {
                                    boardList[i][number] = symbol
                                    isFirstPlayer = !isFirstPlayer
                                    printBoardList()
                                    if (checkGameBoard(symbol)) {
                                        println("Player $currentPlayer won")
                                        if (currentPlayer == player1Name) player1Count += 2
                                        else player2Count += 2
                                        break@input
                                    }
                                    if (isBoardFull()) {
                                        println("It is a draw")
                                        player1Count++
                                        player2Count++
                                        break@input
                                    }
                                    break
                                } else {
                                    count++
                                    if (count == boardList.size) {
                                        println("Column ${number + 1} is full")
                                        break
                                    }
                                }
                            }
                        }
                    }
                } else println("Incorrect column number")
            }
            println(
                """Score
                    |$player1Name: $player1Count $player2Name: $player2Count""".trimMargin()
            )
            counter++
            firstPlayer = !firstPlayer
            for (i in boardList.indices) {
                for (j in boardList[i].indices)
                    boardList[i][j] = ' '
            }
        } while (counter != gameCount)
        println("Game over!")
    }

    //    checking the state of the game
    private fun checkGameBoard(symbol: Char): Boolean {
        var countSymbol: Int
        var result = false
        checkGameBoard@ while (true) {
            for (i in 0 until boardList[0].size) {
                countSymbol = 0
                for (j in 0 until boardList.size) {
                    if (boardList[j][i] == symbol) countSymbol++
                    else break
                    if (countSymbol == 4) {
                        result = true
                        break@checkGameBoard
                    }
                }
            }
            for (i in 0 until boardList.size) {
                countSymbol = 0
                for (j in 0 until boardList[i].size) {
                    if (boardList[i][j] == symbol) countSymbol++
                    else countSymbol = 0
                    if (countSymbol == 4) if (countSymbol == 4) {
                        result = true
                        break@checkGameBoard
                    }
                }
            }
            if (checkDiagonal(symbol)) {
//                println("diagonal")
                result = true
            }
            break@checkGameBoard
        }
        return result
    }

    private fun checkDiagonal(symbol: Char): Boolean {
        var symbolCount = 0
        var result = false
//    half1LeftBottomRightTop
        for (i in 0 until boardList.size) {
            var temp1 = i
            for (j in 0 until boardList[i].size) {
                if (temp1 + 1 <= boardList.size && j + 1 <= boardList[i].size) {
                    if (boardList[temp1][j] == symbol) {
                        symbolCount++
                        temp1++
                        if (symbolCount == 4) {
                            result = true
//                            println("1")
                            break
                        }
                    } else {
                        symbolCount = 0
                        break
                    }
                } else break
            }
        }
//    half2LeftBottomRightTop
        for (i in 1 until boardList.size) {
            var temp1 = i
            for (j in 0 until boardList[i].size) {
                if (temp1 + 1 <= boardList.size && j + 1 <= boardList[i].size) {
                    if (boardList[boardList.size - 1 - temp1][boardList[0].size - 1 - j] == symbol) {
                        symbolCount++
                        temp1++
                        if (symbolCount == 4) {
                            result = true
//                            println("2")
                            break
                        }
                    } else {
                        symbolCount = 0
                        break
                    }
                } else break
            }
        }
//    half1RightBottomLeftTop
        for (i in boardList.size - 1 downTo 0) {
            var temp1 = i
            for (j in boardList[i].size - 1 downTo 0) {
                if (temp1 + 1 <= boardList.size && j + 1 <= boardList[i].size) {
                    if (boardList[temp1][j] == symbol) {
                        symbolCount++
                        temp1++
                        if (symbolCount == 4) {
                            result = true
//                            println("3")
                            break
                        }
                    } else {
                        symbolCount = 0
                        break
                    }
                } else break
            }
        }
//    half2RightBottomLeftTop
        for (i in boardList.size - 1 downTo 1) {
            var temp1 = i
            for (j in boardList[i].size - 1 downTo 0) {
                if (temp1 + 1 <= boardList.size && j + 1 <= boardList[i].size) {
                    if (boardList[boardList.size - 1 - temp1][boardList[0].size - 1 - j] == symbol) {
                        symbolCount++
                        temp1++
                        if (symbolCount == 4) {
                            result = true
//                            println("4")
                            break
                        }
                    } else {
                        symbolCount = 0
                        break
                    }
                } else {
                    symbolCount = 0
                    break
                }
            }
        }
        return result
    }

    private fun isBoardFull(): Boolean {
        var result = false
        for (i in 0 until boardList[0].size) {
            if (boardList[boardList.lastIndex][i] == '*' || boardList[boardList.lastIndex][i] == 'o')
                result = true
            else {
                result = false
                break
            }
        }
        return result
    }
}