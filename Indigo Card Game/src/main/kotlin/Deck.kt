object Deck {
    private val deck = sortedMapOf<String, Pair<String, String>>()
    private val RANKS = listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
    private val SUITS = listOf("♦", "♥", "♠", "♣")

    init { createDeck() }

    //    создание колоды карт
    private fun createDeck() {
        deck.clear()
        for (suit in SUITS.indices) {
            for (rank in RANKS.indices) {
                deck["${RANKS[rank]}${SUITS[suit]}"] = Pair(RANKS[rank], SUITS[suit])
            }
        }
    }

    //    получение карты из колоды
    fun getCardFromDeck() = deck.remove((deck.keys).random())

    //    проверка колоды на наличие карт
    fun isDeckEmpty() = deck.isEmpty()
}