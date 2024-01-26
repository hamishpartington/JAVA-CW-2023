package edu.uob;

public class Triangle extends TwoDimensionalShape {

  int side1, side2, side3;
  public Triangle(int side1, int side2, int side3) {
    this.side1 = side1;
    this.side2 = side2;
    this.side3 = side3;
  }

  public int getLongestSide() {
    return Math.max(side1, Math.max(side2, side3));
  }

  public String toString(){
    return super.toString() + " triangle with sides length " + side1 + ", " + side2 + ", " + side3;
  }


  // TODO implement me!
  public double calculateArea() {
    return 0;
  }

  // TODO implement me!
  public int calculatePerimeterLength() {
    return 0;
  }
}
