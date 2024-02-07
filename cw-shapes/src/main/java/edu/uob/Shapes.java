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
    System.out.println(myTriangle2.calculateArea());
    System.out.println(myTriangle2.calculatePerimeterLength());
    Triangle myTriangle3 = new Triangle(4, 5, 4);
    System.out.println(myTriangle3.getVariant());
    System.out.println(myTriangle3.calculateArea());
    System.out.println(myTriangle3.calculatePerimeterLength());
    Triangle myTriangle4 = new Triangle(33, 33, 33);
    System.out.println(myTriangle4.getVariant());
    System.out.println(myTriangle4.calculateArea());
    System.out.println(myTriangle4.calculatePerimeterLength());
    Triangle myTriangle5 = new Triangle(6, 4, 6);
    System.out.println(myTriangle5.getVariant());
    System.out.println(myTriangle5.calculateArea());
    System.out.println(myTriangle5.calculatePerimeterLength());

    // array of shapes
    TwoDimensionalShape[] TwoDShapesA = new TwoDimensionalShape[100];
    int nTriangles = 0, nRectangles = 0, nCircles = 0;
    for(int i = 0; i < 100; i++) {
     double r = Math.random();
     if(r < 0.333){
      TwoDShapesA[i] = new Triangle(4, 4, 4);
      nTriangles++;
     } else if (r < 0.666){
      TwoDShapesA[i] = new Circle(4);
      nCircles++;
     } else {
      TwoDShapesA[i] = new Rectangle(4, 4);
      nRectangles++;
     }
     TwoDShapesA[i].setShapeColour(Colour.BLUE);
     System.out.println(TwoDShapesA[i].toString());
    }
    System.out.println("No. Triangles = " + nTriangles + "\nNo. Circles = " + nCircles + "\nNo. Rectangles = " +nRectangles);
  }
}

