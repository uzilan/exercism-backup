typealias Position = Pair<Int, Int>

data class FlowerFieldBoard(val board: List<String>) {

    fun withNumbers(): List<String> {
        return board.mapIndexed { lineIndex, line ->
            line.mapIndexed { colIndex, char ->
                if (char == ' ') findStars(lineIndex, colIndex) else char
            }.joinToString("")
        }
    }

    private fun findStars(lineIndex: Int, colIndex: Int): Char {
        return getAdjacentPositions(lineIndex, colIndex)
            .filter { (row, col) -> isValidPosition(row, col) }
            .count { (row, col) -> board[row][col] == '*' }
            .let { count -> if (count == 0) ' ' else count.toString()[0] }
    }
    
    private fun getAdjacentPositions(lineIndex: Int, colIndex: Int): List<Position> {
        val offsets = listOf(-1, 0, 1)
        return offsets.flatMap { rowOffset ->
            offsets.map { colOffset ->
                lineIndex + rowOffset to colIndex + colOffset
            }
        }.filter { (row, col) ->
            row != lineIndex || col != colIndex
        }
    }
    
    private fun isValidPosition(row: Int, col: Int): Boolean {
        return row >= 0 && row < board.size && 
               col >= 0 && col < board[row].length
    }
}
