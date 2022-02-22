object Computer {
    private val computersCards = mutableMapOf<String, Pair<String, String>>()
    private var candidateCards = mutableMapOf<String, Pair<String, String>>()

    //    выдача карт
    fun dealCards() {
        if (!Deck.isDeckEmpty()) {
            repeat(6) {
                val card = Deck.getCardFromDeck()!!
                computersCards["${card.first}${card.second}"] = card
            }
        }
    }

    //    обращение к колоде
    fun getComputersCards() = computersCards

    //    ход компьютера
    fun getMove(card: Pair<String, String>, cardsOnDesk: Int, topCard: Pair<String, String>): Pair<String, String> {
        println(computersCards.keys.joinToString(" "))
        checkCandidatesCards(card)
        return if (cardsOnDesk != 0) {
            when (candidateCards.size) {
                0 -> {
                    val temp = getAnyCard()
                    computersCards.remove("${temp.first}${temp.second}")
                    Pair(temp.first, temp.second)
                }
                else -> {
                    val temp = getCardToMove(cardsOnDesk, topCard)
                    Pair(temp.first, temp.second)
                }
            }
        } else {
            val temp = getCardToMove(cardsOnDesk, topCard)
            Pair(temp.first, temp.second)
        }
    }

    //    проверка наличия/количества карт-кандидатов, заполнение списка карт-кандидатов
    private fun checkCandidatesCards(topCard: Pair<String, String>): Int {
        for (card in computersCards) {
            if (card.value.first == topCard.first || card.value.second == topCard.second) {
                candidateCards[card.key] = card.value
            }
        }
        return candidateCards.size
    }

    //    выбор карты-кандидата для хода
    private fun getCardToMove(cardsOnDesk: Int, topCard: Pair<String, String>): Pair<String, String> {
        val tempCard: Pair<String, String>
        if (cardsOnDesk == 0) {
            tempCard = getAnyCard()
        } else {
            tempCard = if (candidateCards.size <= 2) {
                val tempCandidateList = candidateCards.values.shuffled()
                tempCandidateList.first()
            } else {
                var countRang = 0
                candidateCards.forEach { if (it.value.first == topCard.first) countRang++ }
                var countSuit = 0
                candidateCards.forEach { if (it.value.second == topCard.second) countSuit++ }
                if (countRang > countSuit) {
                    candidateCards.values.first { it.first == topCard.first }
                } else {
                    candidateCards.values.first { it.second == topCard.second }
                }
            }
        }
        candidateCards.clear()
        computersCards.remove("${tempCard.first}${tempCard.second}")
        return tempCard
    }

    //    выбор карты для хода при отсутствии карт на столе
    private fun getAnyCard(): Pair<String, String> {
        //    проверка наличия карт одной масти
        //    подсчёт количества карт в каждой масти
        val countSuit1 = computersCards.values.count { it.second == "♦" }
        val countSuit2 = computersCards.values.count { it.second == "♥" }
        val countSuit3 = computersCards.values.count { it.second == "♠" }
        val countSuit4 = computersCards.values.count { it.second == "♣" }

        val suitsCountList = mapOf("♦" to countSuit1, "♥" to countSuit2, "♠" to countSuit3, "♣" to countSuit4)
        val maxCountSuits = suitsCountList.values.maxOrNull()


        //    проверка наличия карт одного ранга
        //    множество рангов карт
        val tempRangSet = mutableSetOf<String>()
        computersCards.forEach { tempRangSet.add(it.value.first) }
        //    список рангов карт
        val tempRangList = mutableListOf<String>()
        computersCards.values.forEach { tempRangList.add(it.first) }
        //    количество карт по рангам
        val tempRangMap = mutableMapOf<String, Int>()
        for (elem in tempRangSet) { tempRangMap[elem] = tempRangList.count { it == elem } }

        //    максимальное количество карт одного ранга
        val maxCountRang = tempRangMap.values.maxOrNull()
        //    список рангов карт с максимальным повтором
        val maxCountRangMap = tempRangMap.filter { it.value == maxCountRang }.keys
        //    список карт одного ранга
        val resultRangList = computersCards.filter { it.value.first == maxCountRangMap.first().toString() }

        return if (maxCountSuits!! > 1) {
            //    есть карты одной масти
            val tempSuitsList = suitsCountList.filter { it.value == maxCountSuits }.keys.toList()
            val index = (tempSuitsList.indices).random()
            computersCards.filter { it.value.second == tempSuitsList[index] }.values.first()
        } else if (maxCountRang!! > 1) {
            //    есть карты одного ранга
            val index = (0 until resultRangList.size).random()
            val tempList = resultRangList.values.toList()
            tempList[index]
        } else {
            //    нет карт одной масти/одного ранга
            computersCards.values.first()
        }
    }
}