package cs355.model.drawing;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyModel extends CS355Drawing{

	List<Shape> shapeList = new ArrayList<Shape>();
	
	@Override
	public Shape getShape(int index) {
		return shapeList.get(index);
	}

	@Override
	public int addShape(Shape s) {
		shapeList.add(s);
		setChanged();
		notifyObservers();
		return (shapeList.size()-1);
	}

	@Override
	public void deleteShape(int index) {
		shapeList.remove(index);
		setChanged();
		notifyObservers();
	}

	@Override
	public void moveToFront(int index) {
		Shape s = shapeList.get(index);
		shapeList.remove(index);
		shapeList.add(s);
		setChanged();
		notifyObservers();
	}

	@Override
	public void movetoBack(int index) {
		Shape s = shapeList.get(index);
		shapeList.remove(index);
		ArrayList<Shape> newList = new ArrayList<Shape>();
		newList.add(s);
		newList.addAll(shapeList);
		shapeList = new ArrayList<Shape>(newList);
		setChanged();
		notifyObservers();
	}

	@Override
	public void moveForward(int index) {
		Shape s = shapeList.get(index);
		Shape s1 = shapeList.get(index+1);
		shapeList.set(index, s1);
		shapeList.set(index+1, s);
		setChanged();
		notifyObservers();
	}

	@Override
	public void moveBackward(int index) {
		Shape s = shapeList.get(index);
		Shape s1 = shapeList.get(index-1);
		shapeList.set(index, s1);
		shapeList.set(index-1, s);
		setChanged();
		notifyObservers();
	}

	@Override
	public List<Shape> getShapes() {
		return shapeList;
	}

	@Override
	public List<Shape> getShapesReversed() {
		List<Shape> r = new ArrayList<Shape>(shapeList);
		Collections.reverse(r);
		return r;
	}

	@Override
	public void setShapes(List<Shape> shapes) {
		shapeList = shapes;
		setChanged();
		notifyObservers();
	}
	
	public int getSize() {
		return shapeList.size();
	}
	
	public double calcArea(Point2D A, Point2D B, Point2D C) {
		double area = Math.abs(A.getX() * (B.getY()-C.getY()) + B.getX() * (C.getY() - A.getY()) + C.getX() * (A.getY() - B.getY()));
		return area/2;
	}

	public void setShape(int index, Shape s) {
		shapeList.set(index, s);
		setChanged();
		notifyObservers();
	}
	
}
