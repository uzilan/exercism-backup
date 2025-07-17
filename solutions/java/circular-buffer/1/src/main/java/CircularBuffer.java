import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CircularBuffer<T> {
    private final List<T> buffer;
    private final int size;
    private int start;

    @SuppressWarnings("unchecked")
    public CircularBuffer(int size) {
        this.size = size;
        buffer = Arrays.asList((T[]) new Object[size]);
    }

    public T read() throws BufferIOException {
        T value = buffer.get(start);
        if (value == null) {
            throw new BufferIOException("Tried to read from empty buffer");
        }
        buffer.set(start, null);
        start = next(start);
        return value;
    }

    public void write(T value) throws BufferIOException {
        if (full()) throw new BufferIOException("Tried to write to full buffer");
        var i = start;
        while (buffer.get(i) != null) {
            i = next(i);
        }
        buffer.set(i, value);
    }

    public void clear() {
        buffer.replaceAll(value -> null);
    }

    public void overwrite(T value) throws BufferIOException {
        if (full()) {
            buffer.set(start, null);
            start = next(start);
        }
        write(value);
    }

    private int next(int i) {
        if (i < size - 1) return i + 1;
        else return 0;
    }

    private boolean full() {
        return buffer.stream().noneMatch(Objects::isNull);
    }
}
