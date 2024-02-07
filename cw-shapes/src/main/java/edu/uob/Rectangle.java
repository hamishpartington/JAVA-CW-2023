package edu.uob;

public class Rectangle extends TwoDimensionalShape {
  long width;
  long height;

  public Rectangle(int w, int h) {
    width = w;
    height = h;
  }

  public double calculateArea() {
    return width * height;
  }

  public long calculatePerimeterLength() {
    return 2 * (width + height);
  }

  public String toString() {
    return super.toString() + " rectangle of dimensions " + width + " x " + height;
  }
}
