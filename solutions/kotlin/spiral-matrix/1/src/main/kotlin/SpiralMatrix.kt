object SpiralMatrix {

    fun ofSize(size: Int): Array<IntArray> {
        return when (size) {
            0 -> emptyArray()
            1 -> arrayOf(intArrayOf(1))
            else -> generateSpiralMatrix(size)
        }
    }

    private fun generateSpiralMatrix(size: Int): Array<IntArray> {
        val matrix = Array(size) { IntArray(size) }
        var value = 1
        var top = 0
        var bottom = size - 1
        var left = 0
        var right = size - 1

        while (top <= bottom && left <= right) {
            for (i in left..right) {
                matrix[top][i] = value++
            }
            top++
            for (i in top..bottom) {
                matrix[i][right] = value++
            }
            right--
            if (top <= bottom) {
                for (i in right downTo left) {
                    matrix[bottom][i] = value++
                }
                bottom--
            }
            if (left <= right) {
                for (i in bottom downTo top) {
                    matrix[i][left] = value++
                }
                left++
            }
        }
        return matrix
    }
}
