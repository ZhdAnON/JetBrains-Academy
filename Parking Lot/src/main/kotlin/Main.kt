fun main() {
    val parking = Parking()

    while (true) {
        val command = readLine()!!
        when {
            command.contains("exit") -> return
            else -> parking.parkingCommand(command)
        }
    }
}