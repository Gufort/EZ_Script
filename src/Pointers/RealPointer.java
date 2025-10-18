package Pointers;

public class RealPointer implements Pointer<Double> {
    private double value;
    public RealPointer(double value) { this.value = value; }
    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
}