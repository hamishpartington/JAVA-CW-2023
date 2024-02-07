package edu.uob;

public abstract class TwoDimensionalShape {

  public Colour shapeColour;

  public Colour getShapeColour() {
    return shapeColour;
  }

  public void setShapeColour(Colour shapeColour) {
    this.shapeColour = shapeColour;
  }

  public String toString(){
    return "" + shapeColour;
  }


  public TwoDimensionalShape() {
  }

  public abstract double calculateArea();

  public abstract long calculatePerimeterLength();
}
