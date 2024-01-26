package edu.uob;

public class Shapes {

  // TODO use this class as then entry point; play around with your shapes, etc
  public static void main(String[] args) {
    Triangle myTriangle = new Triangle(40, 20, 4);
    int longestSide = myTriangle.getLongestSide();
    System.out.println("The longest side of the myTriangle is " + longestSide);
  }
}

