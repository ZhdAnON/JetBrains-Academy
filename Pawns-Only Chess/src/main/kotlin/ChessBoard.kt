object ChessBoard {
    private var board = mutableListOf<MutableList<Char>>()
    private var player1Name: String = ""
    private var player2Name: String = ""
    private var isFirstPlayer = true
    //    хранение информации о первом ходе фигуры
    private val whiteFirstStep = MutableList(8) { '0' }
    private val blackFirstStep = MutableList(8) { '0' }
    //    проверка возможность взятия стоящей рядом фигуры противника
    private val isNextStep = MutableList(8) { MutableList(8) { false } }

    init {
        board = MutableList(8) { MutableList(8) { ' ' } }
        for (i in 0..7) {
            board[1][i] = 'W'
            board[6][i] = 'B'
        }
        println("Pawns-Only Chess")
        setPlayersNames()
        printBoard()
    }
    //    установка имён игроков
    private fun setPlayersNames() {
        println("First Player's name:")
        player1Name = readLine()!!
        println("Second Player's name:")
        player2Name = readLine()!!
    }
    //    старт игры
    fun playGame() {
        val regex = "[a-h][1-8][a-h][1-8]".toRegex()
        while (true) {
            println(if (isFirstPlayer) "$player1Name's turn:" else "$player2Name's turn:")
            val input = readLine()!!
            when {
                input == "exit" -> break
                input.matches(regex) -> {
                    val symbol = if (isFirstPlayer) 'W' else 'B'
                    val enemy = if (isFirstPlayer) 'B' else 'W'
                    when (checkTurn(input, symbol)) {
                        2 -> println(
                            if (isFirstPlayer) "No white pawn at ${input[0]}${input[1]}"
                            else "No black pawn at ${input[0]}${input[1]}"
                        )
                        5, 6 -> {
                            if (isFirstPlayer) whiteFirstStep[letterToNumber(input[0])] = '1'
                            else blackFirstStep[letterToNumber(input[0])] = '1'
                            board[input[1].toString().toInt() - 1][letterToNumber(input[0])] = ' '
                            board[input[3].toString().toInt() - 1][letterToNumber(input[2])] = symbol
                            for (i in 0 until isNextStep.size) {
                                for (j in 0 until isNextStep[i].size) isNextStep[i][j] = false
                            }
                            isNextStep[input[3].toString().toInt() - 1][letterToNumber(input[2])] = true
                            isFirstPlayer = !isFirstPlayer
                            printBoard()

                            when (gameState(enemy)) {
                                1 -> { println("White Wins!"); break }
                                2 -> { println("Black Wins!"); break }
                                3 -> { println("Stalemate!"); break }
                                0 -> continue
                            }
                        }
                        7 -> {
                            if (isFirstPlayer) whiteFirstStep[letterToNumber(input[0])] = '1'
                            else blackFirstStep[letterToNumber(input[0])] = '1'
                            board[input[1].toString().toInt() - 1][letterToNumber(input[0])] = ' '
                            board[input[1].toString().toInt() - 1][letterToNumber(input[2])] = ' '
                            board[input[3].toString().toInt() - 1][letterToNumber(input[2])] = symbol
                            isFirstPlayer = !isFirstPlayer
                            printBoard()
                            when (gameState(enemy)) {
                                1 -> { println("White Wins!"); break }
                                2 -> { println("Black Wins!"); break }
                                3 -> { println("Stalemate!"); break }
                                0 -> continue
                            }
                        }
                        1 -> println("Invalid input")
                    }
                }
                else -> println("Invalid input")
            }
        }
        println("Bye!")
    }
    //    проверка легитимности хода
    private fun checkTurn(turn: String, symbol: Char): Int {
        val letter1 = letterToNumber(turn[0])
        val number1 = turn[1].toString().toInt()
        val letter2 = letterToNumber(turn[2])
        val number2 = turn[3].toString().toInt()
        val enemySymbol = if (symbol == 'W') 'B' else 'W'
        val isValidTurn: Boolean = if ((letter2 - letter1 <= 1 || letter1 - letter2 <= 1)) {
            if (symbol == 'W') {
                if (whiteFirstStep[letter1] == '0') {
                    number2 - number1 == 1 || number2 - number1 == 2 || letter1 != letter2
                } else number2 - number1 == 1
            } else {
                if (blackFirstStep[letter1] == '0') {
                    number1 - number2 == 1 || number1 - number2 == 2 || letter1 != letter2
                } else number1 - number2 == 1
            }
        } else false
        return when (isValidTurn) {
            true -> {
                if (letter1 != letter2) {
                    if (letter2 - letter1 > 1 || letter1 - letter2 > 1) 1
                    else if (board[number2 - 1][letter2] != ' ' && isEnemyOnDiagonal(turn, enemySymbol)) 6
                    else if (board[number2 - 1][letter2] == ' ' && isEnemyNear(
                            turn,
                            enemySymbol
                        ) && isNextStep[number1 - 1][letter2]
                    ) 7
                    else 1
                } else if (board[number1 - 1][letter1] != symbol) 2
                else if (board[number2 - 1][letter2] != ' ') 1
                else {
                    5
                }
            }
            false -> {
                if (board[number1 - 1][letter1] != symbol) 2
                else if (board[number2 - 1][letter2] != ' ') 1
                else 1
            }
        }
    }
    //    перевод буквы в число (для задания координаты фигуры)
    private fun letterToNumber(letter: Char): Int {
        return when (letter) {
            'a' -> 0
            'b' -> 1
            'c' -> 2
            'd' -> 3
            'e' -> 4
            'f' -> 5
            'g' -> 6
            else -> 7
        }
    }
    //    проверка нахождения фигуры противника по диагонали
    private fun isEnemyOnDiagonal(turn: String, enemy: Char = 'B'): Boolean {
        val let1 = letterToNumber(turn[0])
        val let2 = letterToNumber(turn[2])
        val num2 = turn[3].toString().toInt()
        val result = when (let1) {
            in 1..6 -> board[num2 - 1][let2] == enemy
            0 -> board[num2 - 1][1] == enemy
            else -> board[num2 - 1][6] == enemy
        }
        return result
    }
    //    проверка нахождения фигуры противника рядом
    private fun isEnemyNear(turn: String, enemy: Char): Boolean {
        val let1 = letterToNumber(turn[0])
        val num1 = turn[1].toString().toInt()
        val let2 = letterToNumber(turn[2])
        return when (let1) {
            in 1..6 -> board[num1 - 1][let2] == enemy
            0 -> board[num1 - 1][1] == enemy
            else -> board[num1 - 1][6] == enemy
        }
    }
    //    печать игрового поля
    private fun printBoard() {
        println("  +---+---+---+---+---+---+---+---+")
        for (i in board.lastIndex downTo 0) {
            print("${i + 1} |")
            for (j in 0 until board[i].size) {
                print(" ${board[i][j]} |")
            }
            println("\n  +---+---+---+---+---+---+---+---+")
        }
        print(" ")
        for (i in 'a'..'h') {
            print("   $i")
        }
        println()
    }
    //    проверка состояния игры
    private fun gameState(enemy: Char): Int {
        if (board[7].isNotEmpty() || board[0].isNotEmpty()) {
            if (board[7].contains('W')) return 1
            else if (board[0].contains('B')) return 2
        }

        if (isNotEnemySymbol('W')) return 2
        if (isNotEnemySymbol('B')) return 1

        if (checkStalemate(enemy)) return 3
        return 0
    }
    //    проверка наличия фигур противника
    private fun isNotEnemySymbol(enemy: Char): Boolean {
        var temp = true
        for (i in 0 until board.size) {
            if (board[i].contains(enemy)) {
                temp = false; break
            }
        }
        return temp
    }
    //    проверка на предмет ничейного результата партии
    private fun checkStalemate(symbolTemp: Char): Boolean {
        var resultCount = countFigure(symbolTemp)
        for (i in 0 until board.size) {
            for (j in 0 until board[i].size) {
                if (board[i][j] == symbolTemp) {
                    if (!isFigureCanStep(i, j, symbolTemp)) {
                        resultCount--
                    }
                }
            }
        }
        return resultCount == 0
    }
    //    проверка возможности сделать ход фигурами заданного цвета
    private fun isFigureCanStep(number: Int, letter: Int, symbol: Char): Boolean {
        val enemy = if (symbol == 'W') 'B' else 'W'

//    check near kill
        if (isNextStep[number][letter] && board[number][letter] == symbol) {
            if (letter - 1 >= 0) {
                if (board[number][letter - 1] == enemy) {
                    if (board[number + 1][letter - 1] == ' ') { return true }
                }
            }
            if (letter + 1 <= 7) {
                if (board[number][letter + 1] == enemy) {
                    if (board[number + 1][letter + 1] == ' ') { return true }
                }
            }
        }

//    check diagonal kill
        if (symbol == 'W') {
            if (number + 1 <= 7) {
                if (letter - 1 >= 0) { if (board[number + 1][letter - 1] == enemy) return true }
                if (letter + 1 <= 7) { if (board[number + 1][letter + 1] == enemy) return true }
            }
        } else {
            if (number - 1 >= 0) {
                if (letter - 1 >= 0) { if (board[number - 1][letter - 1] == enemy) return true }
                if (letter + 1 <= 7) { if (board[number - 1][letter + 1] == enemy) return true }
            }
        }

//        check vertical step
        var tempVertical = false
        if (symbol == 'W') {
            if (number == 1 && whiteFirstStep[letter] == '0') { tempVertical = board[number + 2][letter] == ' ' }
            else { if (number + 1 <= 7) { tempVertical = board[number + 1][letter] == ' ' } }
        } else {
            if (number == 6 && blackFirstStep[letter] == '0') { tempVertical = board[number - 2][letter] == ' ' }
            else { if (number - 1 >= 0) { tempVertical = board[number - 1][letter] == ' ' } }
        }
        if (tempVertical) return true

        return false
    }
    //    подсчёт фигур заданного цвета
    private fun countFigure(symbol: Char): Int {
        var result = 0
        for (i in board.indices) {
            result += board[i].count { it == symbol }
        }
        return result
    }
}