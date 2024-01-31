package edu.uob;

public class Shapes {

   public static void main(String[] args) {
    TwoDimensionalShape myTriangle = new Triangle(40, 40, 40);
    myTriangle.setShapeColour(Colour.BLUE);
    System.out.println(myTriangle);
    TwoDimensionalShape myRectangle = new Rectangle(20, 40);
    myRectangle.setShapeColour(Colour.RED);
    System.out.println(myRectangle);
    TwoDimensionalShape myCircle = new Circle(10);
    myCircle.setShapeColour(Colour.GREEN);
    System.out.println(myCircle);
    Triangle myTriangle2 = new Triangle(4, 4, 4);
    System.out.println(myTriangle2.getVariant());
    Triangle myTriangle3 = new Triangle(4, 5, 4);
    System.out.println(myTriangle3.getVariant());
    Triangle myTriangle4 = new Triangle(33, 33, 33);
    System.out.println(myTriangle4.getVariant());
    Triangle myTriangle5 = new Triangle(6, 4, 6);
    System.out.println(myTriangle5.getVariant());

  }
}

