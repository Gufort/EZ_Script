package Pointers;

public interface Pointer<T> {
    T getValue();
    void setValue(T value);
}