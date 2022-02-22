object IndigoGame {
    private const val startNumberCardsOnTable = 4
    private const val startCardsOnHand = 6
    private val scoresCard = listOf("10", "J", "Q", "K", "A")

    private val playersCards = mutableMapOf<String, Pair<String, String>>()
    //    private val computersCards = mutableMapOf<String, Pair<String, String>>()
    private val cardsOnDesk = mutableMapOf<String, Pair<String, String>>()
    private lateinit var lastCardOnDesk: Pair<String, String>

    private var playersScore = 0
    private var computersScore = 0
    private var scoresOnDesk = 0
    private var playersWinsCard = 0
    private var computersWinsCards = 0

    private var isFirst = true
    private lateinit var firstMove: String
    private lateinit var lastWinner: String

    init {
        println("Indigo Card Game")
        repeat(startNumberCardsOnTable) {
            val card = Deck.getCardFromDeck()!!
            if (it + 1 == startNumberCardsOnTable) lastCardOnDesk = card
            cardsOnDesk["${card.first}${card.second}"] = Pair(card.first, card.second)
        }
        isFirst()
        if (isFirst) firstMove = "player"
        dealCardsToHand()
        Computer.dealCards()
    }

    //    запуск процесса игры
    fun startIndigoGame() {
        println("Initial cards on the table: ${cardsOnDesk.keys.joinToString(" ")}")
        game@ while (true) {
            printDeskInfo()
            if (isFirst) {
                //    ход игрока
                printPlayersCard()
                playerMove@ while (true) {
                    println("Choose a card to play (1-${playersCards.size}):")
                    val cardNumber = readLine()
                    //    проверка ввода команды выхода
                    if (cardNumber == "exit") {
                        break@game
                    }
                    if (cardNumber != null && cardNumber.matches("\\d+".toRegex())) {
                        if (cardNumber.toInt() in 1..playersCards.size) {
                            var numberInput = 1
                            val iterator = playersCards.iterator()
                            playerMoveCheck@ while (iterator.hasNext()) {
                                val item = iterator.next()
                                if (numberInput == cardNumber.toInt()) {
                                    if (!checkRankAndSuit(Pair(item.value.first, item.value.second))) {
                                        lastCardOnDesk = Pair(item.value.first, item.value.second)
                                        cardsOnDesk[item.key] = Pair(item.value.first, item.value.second)
                                        iterator.remove()
                                        numberInput++
                                    } else {
                                        cardsOnDesk[item.key] = Pair(item.value.first, item.value.second)
                                        playersCards.remove(item.key)
                                        scoring()
                                        lastWinner = "player"
                                        break@playerMoveCheck
                                    }
                                } else numberInput++
                            }
                            break@playerMove
                        }
                    }
                }
            } else {
                //    ход компьютера
                val card1 = Computer.getMove(lastCardOnDesk, cardsOnDesk.size, lastCardOnDesk)
                println("Computer plays ${card1.first}${card1.second}")
                if (!checkRankAndSuit(card1)) {
                    cardsOnDesk["${card1.first}${card1.second}"] = card1
                    lastCardOnDesk = card1
                } else {
                    cardsOnDesk["${card1.first}${card1.second}"] = card1
                    scoring()
                    cardsOnDesk.clear()
                    lastWinner = "computer"
                }
            }

            isFirst = !isFirst
            if ((playersCards.isEmpty() && Computer.getComputersCards().isEmpty()) && Deck.isDeckEmpty()) {
                finalScoring()
                break@game
            } else {
                if (playersCards.isEmpty()) dealCardsToHand()
                if (Computer.getComputersCards().isEmpty()) Computer.dealCards()
            }
        }
        println("Game Over")
    }

    //    определение первого хода
    private fun isFirst() {
        while (true) {
            println("Play first?")
            val input = readLine()!!
            isFirst = when {
                input.equals("yes", ignoreCase = true) -> true
                input.equals("no", ignoreCase = true) -> false
                else -> continue
            }
            break
        }
    }

    //    раздача карт
    private fun dealCardsToHand() {
        if (!Deck.isDeckEmpty()) {
            repeat(startCardsOnHand) {
                val card = Deck.getCardFromDeck()!!
                playersCards["${card.first}${card.second}"] = card
            }
        }
    }

    //    отображение карт игрока/компьютера
    private fun printPlayersCard() {
        print("Cards in hand:")
        var index = 1
        playersCards.forEach { print(" $index)${it.key}"); index++ }
        println()
    }

    //    проверка ранга/масти карты
    private fun checkRankAndSuit(card: Pair<String, String>): Boolean {
        var result = true
        val rank = card.first
        val suite = card.second
        //    проверка соответствия ранга/масти карты рангу/масти верхней карты на столе
        if (cardsOnDesk.isNotEmpty() && (lastCardOnDesk.first == rank || lastCardOnDesk.second == suite)) {
            println("${if (isFirst) "Player" else "Computer"} wins cards")
        } else result = false
        return result
    }

    //    начисление очков в ТЕЧЕНИИ игры
    private fun scoring() {
        cardsOnDesk.forEach { if (scoresCard.contains(it.value.first)) scoresOnDesk++ }
        if (isFirst) {
            playersWinsCard += cardsOnDesk.size
            playersScore += scoresOnDesk
        } else {
            computersWinsCards += cardsOnDesk.size
            computersScore += scoresOnDesk
        }
        scoresOnDesk = 0
        cardsOnDesk.clear()
        printScores()
    }

    //    начисление очков в КОНЦЕ игры
    private fun finalScoring() {
        cardsOnDesk.forEach { if (scoresCard.contains(it.value.first)) scoresOnDesk++ }
        when (lastWinner) {
            "player" -> {
                playersWinsCard += cardsOnDesk.size
                playersScore += scoresOnDesk
            }
            "computer" -> {
                computersWinsCards += cardsOnDesk.size
                computersScore += scoresOnDesk
            }
        }
        if (playersWinsCard == computersWinsCards) {
            if (firstMove == "player") playersScore += 3 else computersScore += 3
        } else if (playersWinsCard > computersWinsCards) playersScore += 3 else computersScore += 3
        scoresOnDesk = 0
        printDeskInfo()
        printScores()
    }

    //    информация о количестве карт на столе и о верхней карте
    private fun printDeskInfo() {
        if (cardsOnDesk.isNotEmpty())
            println("\n${cardsOnDesk.size} cards on the table, and the top card is ${lastCardOnDesk.first}${lastCardOnDesk.second}")
        else
            println("\nNo cards on the table")
    }

    //    отображение количества очков
    private fun printScores() {
        println("Score: Player $playersScore - Computer $computersScore")
        println("Cards: Player $playersWinsCard - Computer $computersWinsCards")
    }
}