import kotlin.concurrent.fixedRateTimer

class EmptyBufferException : Throwable()

class BufferFullException : Throwable()

class CircularBuffer<T>(val size: Int) {

    private val buffer: MutableList<T?> = MutableList(size) { null }
    private var start = 0

    fun read(): T {
        val value = buffer[start] ?: throw EmptyBufferException()
        buffer[start] = null
        start = next(start)
        return value
    }

    fun write(value: T) {
        if (full()) throw BufferFullException()
        var i = start
        while (buffer[i] != null) {
            i = next(i)
        }
        buffer[i] = value
    }

    fun overwrite(value: T) {
        if (full()) {
            buffer[start] = null
            start = next(start)
        }
        write(value)
    }

    fun clear() {
        buffer.indices.forEach { buffer[it] = null }
    }

    private fun next(i: Int): Int = if (i < size - 1) i + 1 else 0


    private fun full(): Boolean = buffer.none { it == null }
}
