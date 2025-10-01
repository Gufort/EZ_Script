public interface Pointer<T> {
    T getValue();
    void setValue(T value);
}