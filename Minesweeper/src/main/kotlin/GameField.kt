import kotlin.random.Random

private const val FIELD_SIZE = 9

class GameField {
    private val gameField = MutableList(FIELD_SIZE) { MutableList(FIELD_SIZE) { '.' } }
    private val gameFieldForCheck = MutableList(FIELD_SIZE + 2) { MutableList(FIELD_SIZE + 2) { '.' } }
    private val tempField = MutableList(FIELD_SIZE + 2) { MutableList(FIELD_SIZE + 2) { ' ' } }
    private val mineNumbers: Int = enterMinesNumber()

    fun startGame() {
        createField()
        printField(gameField)
        while (true) {
            println("Set/unset mine marks or claim a cell as free:")
            val command = readLine()!!.split(" ")
            if (command.contains("exit")) { println("Bye!"); return }
            else {
                val x = command[1].toInt()
                val y = command[0].toInt()
                when {
                    command.contains("free") -> {
                        when {
                            gameFieldForCheck[x][y] == 'X' -> {
                                for (i in gameFieldForCheck.indices) {
                                    for (j in gameFieldForCheck[i].indices) {
                                        if (gameFieldForCheck[i][j] == 'X') gameField[i - 1][j - 1] =
                                            gameFieldForCheck[i][j]
                                    }
                                }
                                println("You stepped on a mine and failed!")
                                printField(gameField)
                                break
                            }
                            gameFieldForCheck[x][y].isDigit() -> {
                                gameField[x - 1][y - 1] = gameFieldForCheck[x][y]
                                printField(gameField)
                            }
                            gameField[x - 1][y - 1] == '.' -> {
                                fillEmptyCells(x, y)
                                for (i in gameField.indices) {
                                    for (j in gameField[i].indices) {
                                        if (gameField[i][j] != tempField[i + 1][j + 1] && tempField[i + 1][j + 1] != ' ')
                                            gameField[i][j] = tempField[i + 1][j + 1]
                                    }
                                }
                                printField(gameField)
                            }
                        }
                    }
                    command.contains("mine") -> {
                        if (gameField[x - 1][y - 1] == '.') {
                            gameField[x - 1][y - 1] = '*'
                        } else gameField[x - 1][y - 1] = '.'
                        printField(gameField)
                    }
                }
            }
            if (gameState() == "win") {
                println("Congratulations! You found all the mines!")
                break
            }
        }
    }

    //    проверка состояния игры
    private fun gameState(): String {
        return if (countPoints() == mineNumbers || checkCellsWithMine()) "win"
        else "continue"
    }
    private fun countPoints(): Int {
        var result = 0
        for (i in gameField.indices) {
            for (j in gameField[i].indices) {
                if (gameField[i][j] == '.') result++
            }
        }
        return result
    }
    private fun checkCellsWithMine(): Boolean {
        var result = true
        checkLoop@for (i in gameField.indices) {
            for (j in gameField[i].indices) {
                if (gameField[i][j] == '*' && gameFieldForCheck[i + 1][j + 1] != 'X') {
                    result = false
                    break@checkLoop
                }
            }
        }
        return result
    }

    //    заполнение игрового поля
    private fun enterMinesNumber(): Int {
        println("How many mines do you want on the field?")
        return readLine()!!.toInt()
    }
    private fun createField() {
        var mineCount = 0
        while (mineCount != mineNumbers) {
            val (x, y) = Array(2) { Random.nextInt(0, FIELD_SIZE) }
            if (gameFieldForCheck[x + 1][y + 1] != 'X') {
                gameFieldForCheck[x + 1][y + 1] = 'X'
                mineCount++
            }
        }
        for (i in 1..FIELD_SIZE) {
            for (j in 1..FIELD_SIZE) {
                gameFieldForCheck[i][j] = countMinesAround(i, j)
            }
        }
    }
    //    подсчёт мин вокруг клетки поля
    private fun countMinesAround(i: Int, j: Int): Char {
        var count = 0
        return if (gameFieldForCheck[i][j] != 'X') {
            if (gameFieldForCheck[i - 1][j - 1] == 'X') count++
            if (gameFieldForCheck[i - 1][j] == 'X') count++
            if (gameFieldForCheck[i - 1][j + 1] == 'X') count++
            if (gameFieldForCheck[i][j - 1] == 'X') count++
            if (gameFieldForCheck[i][j + 1] == 'X') count++
            if (gameFieldForCheck[i + 1][j - 1] == 'X') count++
            if (gameFieldForCheck[i + 1][j] == 'X') count++
            if (gameFieldForCheck[i + 1][j + 1] == 'X') count++
            if (count != 0) {
                count.toString().toCharArray().first()
            } else '.'
        } else 'X'
    }

    //    заполнение проверенных ячеек
    private fun fillEmptyCells(x: Int, y: Int) {
        if (gameFieldForCheck[x][y] == '.') {
            tempField[x][y] = '/'
            gameFieldForCheck[x][y] = '/'
            if (x - 1 >= 0) fillEmptyCells(x - 1, y)
            if (x + 1 <= FIELD_SIZE) fillEmptyCells(x + 1, y)
            if (y - 1 >= 0) fillEmptyCells(x, y - 1)
            if (y + 1 <= FIELD_SIZE) fillEmptyCells(x, y + 1)
            if (x - 1 >= 0 && y - 1 >= 0) fillEmptyCells(x - 1, y - 1)
            if (x + 1 <= FIELD_SIZE && y + 1 <= FIELD_SIZE) fillEmptyCells(x + 1, y + 1)
            if (x - 1 >= 0 && y + 1 <= FIELD_SIZE) fillEmptyCells(x - 1, y + 1)
            if (x + 1 <= FIELD_SIZE && y - 1 >= 0) fillEmptyCells(x + 1, y - 1)
        }
        if (gameFieldForCheck[x][y].isDigit()) tempField[x][y] = gameFieldForCheck[x][y]
    }

    //    отображение игрового поля
    private fun printField(list: MutableList<MutableList<Char>>) {
        print(" |")
        repeat(list[0].size) { print(it + 1) }
        println("|")
        print("-|")
        repeat(list[0].size) { print("-") }
        println("|")
        var line = 1
        for (i in 0 until list.size) {
            print("$line|")
            for (j in 0 until list[i].size) {
                print(list[i][j])
            }
            println("|")
            line++
        }
        print("-|")
        repeat(list[0].size) { print("-") }
        println("|")
    }
}