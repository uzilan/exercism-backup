public class SpiralMatrixBuilder {
    public int[][] buildMatrixOfSize(int size) {
        var matrix = new int[size][size];
        var value = 1;
        var top = 0;
        var bottom = size - 1;
        var left = 0;
        var right = size - 1;

        while (top <= bottom && left <= right) {
            for (var i = left; i <= right; i++) {
                matrix[top][i] = value++;
            }
            top++;
            for (var i = top; i <= bottom; i++) {
                matrix[i][right] = value++;
            }
            right--;
            if (top <= bottom) {
                for (var i = right; i >= left; i--) {
                    matrix[bottom][i] = value++;
                }
                bottom--;
            }
            if (left <= right) {
                for (var i = bottom; i >= top; i--) {
                    matrix[i][left] = value++;
                }
                left++;
            }
        }
        return matrix;
    }
}
