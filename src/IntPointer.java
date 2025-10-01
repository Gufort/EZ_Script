public class IntPointer implements Pointer<Integer> {
    private int value;
    public IntPointer(int value) { this.value = value; }
    public Integer getValue() { return value; }
    public void setValue(Integer value) { this.value = value; }
}