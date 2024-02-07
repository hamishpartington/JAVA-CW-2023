package edu.uob;

public class Circle extends TwoDimensionalShape {
  long radius;

  public Circle(int r) {
    radius = r;
  }

  public double calculateArea() {
    return (int) Math.round(Math.PI * radius * radius);
  }

  public long calculatePerimeterLength() {
    return Math.round(Math.PI * radius * 2.0);
  }

  public String toString() {
    return super.toString() + " circle with radius " + radius;
  }
}
