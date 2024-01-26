package edu.uob;

public class Shapes {

   public static void main(String[] args) {
    TwoDimensionalShape myTriangle = new Triangle(40, 20, 4);
    myTriangle.setShapeColour(Colour.BLUE);
    System.out.println(myTriangle);
    TwoDimensionalShape myRectangle = new Rectangle(20, 40);
    myRectangle.setShapeColour(Colour.RED);
    System.out.println(myRectangle);
    TwoDimensionalShape myCircle = new Circle(10);
    myCircle.setShapeColour(Colour.GREEN);
    System.out.println(myCircle);
  }
}

