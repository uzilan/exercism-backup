object EliudsEggs {

    fun eggCount(number: Int): Int{
        val binary = number.toString(2)
        return binary.count { it == '1' }
    }
}
