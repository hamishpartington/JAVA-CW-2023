package edu.uob;

public class Triangle extends TwoDimensionalShape implements MultiVariantShape{

  long side1, side2, side3;
  TriangleVariant tV;
  public Triangle(long side1, long side2, long side3) {
    this.side1 = side1;
    this.side2 = side2;
    this.side3 = side3;
    if(side1 <= 0 || side2 <= 0 || side3 <= 0){
      this.tV = TriangleVariant.ILLEGAL;
    }else if((side3 - side1 - side2) > 0 || (side2 - side1 - side3) > 0 || (side1 - side2 - side3) > 0){
      this.tV = TriangleVariant.IMPOSSIBLE;
    }else if((side1 + side2) == side3 || (side1 + side3) == side2 || (side2 + side3) == side1) {
      this.tV = TriangleVariant.FLAT;
    }else if(side1 == side2 && side2 == side3){
      this.tV = TriangleVariant.EQUILATERAL;
    }else if(side1 == side2 || side1 == side3 || side2 == side3){
      this.tV = TriangleVariant.ISOSCELES;
    }else if(side1 * side1 + side2 * side2 == side3 * side3 ||
            side1 * side1 + side3 * side3 == side2 * side2 ||
            side2 * side2 + side3 * side3 == side1 * side1){
      this.tV = TriangleVariant.RIGHT;
    }else{
      this.tV = TriangleVariant.SCALENE;
    }
  }

  public long getLongestSide() {
    return Math.max(side1, Math.max(side2, side3));
  }

  public String toString(){
    return super.toString() + " " + tV +" triangle with sides length " + side1 + ", " + side2 + ", " + side3;
  }

  public TriangleVariant getVariant(){
    return tV;
  }


  public double calculateArea() {

    double s = 0.5 * calculatePerimeterLength();


    return Math.sqrt((s * (s - side1) * (s - side2) * (s - side3)));
  }

  public long calculatePerimeterLength() {

    return (side1 + side2 + side3);
  }
}
