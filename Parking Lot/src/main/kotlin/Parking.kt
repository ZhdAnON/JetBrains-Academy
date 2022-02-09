class Parking {
    private lateinit var parking: MutableList<String>

    fun parkingCommand(command: String) {
        val temp = command.split(" ")
        when {
            command.contains("create") -> create(temp[1].toInt())
            command.contains("park") -> park(temp[1], temp[2])
            command.contains("leave") -> leave(temp[1].toInt())
            command.contains("status") -> status()
            command.contains("reg_by_color") -> regByColor(temp[1])
            command.contains("spot_by_color") -> spoByColor(temp[1])
            command.contains("spot_by_reg") -> spotByReg(temp[1])
        }
    }

    private fun create(size: Int) {
        parking = MutableList(size) { "" }
        println("Created a parking lot with $size spots.")
    }

    private fun park(carNumber: String, carColor: String) {
        if (::parking.isInitialized) {
            if (parking.contains("")) {
                for (index in parking.indices) {
                    if (parking[index].isEmpty()) {
                        parking[index] = "$carNumber $carColor"
                        println("$carColor car parked in spot ${index + 1}.")
                        break
                    }
                }
            } else {
                println("Sorry, the parking lot is full.")
            }
        } else println("Sorry, a parking lot has not been created.")
    }

    private fun leave(place: Int) {
        if (::parking.isInitialized) {
            if (parking[place - 1].isEmpty())
                println("There is no car in spot $place.")
            else {
                parking[place - 1] = ""
                println("Spot $place is free.")
            }
        } else println("Sorry, a parking lot has not been created.")
    }

    private fun status() {
        if (::parking.isInitialized) {
            if (!isParkingEmpty()) {
                for (i in parking.indices) {
                    if (parking[i].isNotEmpty()) println("${i + 1} ${parking[i]}")
                }
            } else println("Parking lot is empty.")
        } else println("Sorry, a parking lot has not been created.")
    }

    private fun isParkingEmpty(): Boolean {
        val count = parking.count { it.isEmpty() }
        return parking.size == count
    }

    private fun regByColor(color: String) {
        if (::parking.isInitialized) {
            val result = mutableListOf<String>()
            for (car in parking) {
                if (car.isNotEmpty()) {
                    val temp = car.split(" ")
                    if (temp[1].equals(color, ignoreCase = true)) {
                        result.add(temp[0])
                    }
                }
            }
            if (result.isNotEmpty()) println(result.joinToString())
            else println("No cars with color $color were found.")
        } else println("Sorry, a parking lot has not been created.")
    }

    private fun spoByColor(color: String) {
        if (::parking.isInitialized) {
            val result = mutableListOf<Int>()
            for (car in parking) {
                if (car.isNotEmpty()) {
                    val temp = car.split(" ")
                    if (temp[1].equals(color, ignoreCase = true)) {
                        result.add(parking.indexOf(car) + 1)
                    }
                }
            }
            if (result.isNotEmpty()) println(result.joinToString())
            else println("No cars with color $color were found.")
        } else println("Sorry, a parking lot has not been created.")
    }

    private fun spotByReg(carNumber: String) {
        if (::parking.isInitialized) {
            var result = 0
            for (car in parking) {
                if (car.isNotEmpty() && car.contains(carNumber)) {
                    result = parking.indexOf(car) + 1
                    break
                }
            }
            if (result != 0) println(result)
            else println("No cars with registration number $carNumber were found.")
        } else println("Sorry, a parking lot has not been created.")
    }
}