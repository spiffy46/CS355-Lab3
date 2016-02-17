package cs355.controller;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

import cs355.GUIFunctions;
import cs355.model.drawing.*;

public class MyController extends Observable implements CS355Controller{
	
	MyModel model;
	Shape currentShape;
	List<Shape> shapeList;
	public Color col = Color.white;
	public String button = "";
	public Point2D.Double p1;
	public Point2D.Double p2;
	public int triangleCount = 0;
	public Point2D.Double t1;
	public Point2D.Double t2;
	public Point2D.Double t3;
	public Point2D.Double diff;
	public int selectedIndex = -1;
	public Shape selectedShape;
	public boolean handleSelected = false;
	public int lineHandleSelected = 0;
	public int zoomLevel = 3;
	public Point2D.Double viewPoint = new Point2D.Double(768,768);
	public int viewWidth = 512;

	public void init() {
		triangleCount = 0;
		selectedIndex = -1;
		handleSelected = false;
		col = Color.white;
		lineHandleSelected = 0;
		button = "";
		viewPoint = new Point2D.Double(768,768);
		zoomLevel = 3;
		viewWidth = 512;
		selectedShape = null;
	}
	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		AffineTransform viewToWorld = new AffineTransform(1,0,0,1,viewPoint.getX(),viewPoint.getY());
		AffineTransform scale = new AffineTransform(this.getScale(),0,0,this.getScale(),0,0);
		viewToWorld.concatenate(scale);		
		
		p1 = new Point2D.Double(e.getPoint().getX(),e.getPoint().getY());
		viewToWorld.transform(p1, p1);
				
		if (button == "line"){
			currentShape = new Line(col,p1,p1);
		}else if(button == "square"){
			currentShape = new Square(col,p1,0);
		}else if(button == "rectangle"){
			currentShape = new Rectangle(col,p1,0,0);
		}else if(button == "circle"){
			currentShape = new Circle(col,p1,0);
		}else if(button == "ellipse"){
			currentShape = new Ellipse(col,p1,0,0);
		}else if (button == "triangle"){
			if (triangleCount == 0){
				t1 = new Point2D.Double(p1.getX(),p1.getY());
				triangleCount++;
			}else if(triangleCount ==1){
				t2 = new Point2D.Double(p1.getX(),p1.getY());
				triangleCount++;
			}else{
				t3 = new Point2D.Double(p1.getX(),p1.getY());
				Point2D.Double center = new Point2D.Double();
				center.setLocation((t1.getX()+t2.getX()+t3.getX())/3, (t1.getY()+t2.getY()+t3.getY())/3);
				triangleCount = 0;
				Shape t = new Triangle(col,center,t1,t2,t3);
				model.addShape(t);
			}	
			return;
		}else if (button == "select"){
			selectedIndex = geometryTest(p1, 4);
			if (selectedIndex > -1){
				Shape s = model.getShape(selectedIndex);
				GUIFunctions.changeSelectedColor(s.getColor());
				diff = new Point2D.Double(p1.getX()-s.getCenter().getX(), p1.getY()-s.getCenter().getY());
				if(doHandleCheck(p1,selectedShape)){
					handleSelected = true;
					if(s instanceof Line){
						Line l = (Line)s;
						Point2D.Double len = new Point2D.Double(l.getEnd().getX() - l.getCenter().getX(), l.getEnd().getY() - l.getCenter().getY());

						AffineTransform worldToObj = new AffineTransform(1,0,0,1,-s.getCenter().getX(), -s.getCenter().getY());
						Point2D.Double objCoord = new Point2D.Double();
						worldToObj.transform(p1, objCoord);
						
						if (objCoord.getX()*objCoord.getX() + objCoord.getY()*objCoord.getY() < 100*this.getScale()){
							lineHandleSelected = 1;
						} else if((objCoord.getX()-len.getX())*(objCoord.getX()-len.getX()) + (objCoord.getY()-len.getY())*(objCoord.getY()-len.getY()) < 100*this.getScale()){
							lineHandleSelected = 2;
						}
					}
				}
			} 
			return;
		}else{
			return;
		}
		model.addShape(currentShape);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		handleSelected = false;
		lineHandleSelected = 0;
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e) {
		AffineTransform viewToWorld = new AffineTransform(1,0,0,1,viewPoint.getX(),viewPoint.getY());
		AffineTransform scale = new AffineTransform(this.getScale(),0,0,this.getScale(),0,0);
		viewToWorld.concatenate(scale);	
		
		p2 = new Point2D.Double(e.getPoint().getX(),e.getPoint().getY());
		viewToWorld.transform(p2, p2);
				
		if (button == "line"){
			currentShape = model.getShape(model.getSize()-1);
			Line l = (Line)currentShape;
			l.setEnd(p2);
			model.deleteShape(model.getSize()-1);
			model.addShape(l);
		}else if(button == "square"){
			double size = Math.min((Math.abs(p1.getX()-p2.getX())),(Math.abs(p1.getY()-p2.getY())));
			Point2D.Double upLeft = new Point2D.Double();
			
			if(p1.getX() <= p2.getX() && p1.getY() <= p2.getY()){
				upLeft.setLocation(p1.getX(), p1.getY());
			}else if(p1.getX() <= p2.getX() && p1.getY() > p2.getY()){
				upLeft.setLocation(p1.getX(), p1.getY()-size);
			}else if(p1.getX() > p2.getX() && p1.getY() <= p2.getY()){
				upLeft.setLocation(p1.getX()-size, p1.getY());
			}else{
				upLeft.setLocation(p1.getX()-size, p1.getY()-size);
			}
			Point2D.Double center = new Point2D.Double(upLeft.getX()+size/2, upLeft.getY()+size/2);
			currentShape = model.getShape(model.getSize()-1);
			Square s = (Square)currentShape;
			s.setCenter(center);
			s.setSize(size);
			model.deleteShape(model.getSize()-1);
			model.addShape(s);
		}else if(button == "rectangle"){
			Point2D.Double upLeft = new Point2D.Double();
			upLeft.setLocation(Math.min(p1.getX(), p2.getX()), Math.min(p1.getY(),p2.getY()));
			Double width = Math.abs(p1.getX() - p2.getX());
			Double height = Math.abs(p1.getY() - p2.getY());
			currentShape = model.getShape(model.getSize()-1);
			Rectangle r = (Rectangle)currentShape;
			Point2D.Double center = new Point2D.Double(upLeft.getX()+width/2, upLeft.getY()+height/2);
			r.setHeight(height);
			r.setWidth(width);
			r.setCenter(center);
			model.deleteShape(model.getSize()-1);
			model.addShape(r);
		}else if(button == "circle"){
			double size = Math.min((Math.abs(p1.getX()-p2.getX())),(Math.abs(p1.getY()-p2.getY())));
			double radius = size/2;
			Point2D.Double upLeft = new Point2D.Double();
			
			if(p1.getX() <= p2.getX() && p1.getY() <= p2.getY()){
				upLeft.setLocation(p1.getX(), p1.getY());
			}else if(p1.getX() <= p2.getX() && p1.getY() > p2.getY()){
				upLeft.setLocation(p1.getX(), p1.getY()-size);
			}else if(p1.getX() > p2.getX() && p1.getY() <= p2.getY()){
				upLeft.setLocation(p1.getX()-size, p1.getY());
			}else{
				upLeft.setLocation(p1.getX()-size, p1.getY()-size);
			}
			Point2D.Double center = new Point2D.Double(upLeft.getX()+radius, upLeft.getY()+radius);
			currentShape = model.getShape(model.getSize()-1);
			Circle c = (Circle)currentShape;
			c.setCenter(center);
			c.setRadius(radius);
			model.deleteShape(model.getSize()-1);
			model.addShape(c);
		}else if(button == "ellipse"){
			Point2D.Double center = new Point2D.Double((p1.getX() + p2.getX())/2, (p1.getY() + p2.getY())/2);
			Double width = Math.abs(p1.getX() - p2.getX());
			Double height = Math.abs(p1.getY() - p2.getY());
			currentShape = model.getShape(model.getSize()-1);
			Ellipse el = (Ellipse)currentShape;
			el.setCenter(center);
			el.setHeight(height);
			el.setWidth(width);
			model.deleteShape(model.getSize()-1);
			model.addShape(el);
		}else if(button == "select" && selectedIndex > -1){

			if(handleSelected) {
				AffineTransform worldToObj = new AffineTransform(1,0,0,1,-selectedShape.getCenter().getX(), -selectedShape.getCenter().getY());
				Point2D.Double objCoord = new Point2D.Double();
				worldToObj.transform(p2, objCoord);
				
				if(selectedShape instanceof Line){
					Line l = (Line)selectedShape;

					if (lineHandleSelected == 1){
						Point2D.Double newCenter = new Point2D.Double(p2.getX(), p2.getY());
						l.setCenter(newCenter);
					} else if(lineHandleSelected == 2){
						Point2D.Double newEnd = new Point2D.Double(p2.getX(), p2.getY());
						l.setEnd(newEnd);
					}
					selectedShape = l;
					model.setShape(selectedIndex, selectedShape);
				}else{
					
					double theta = Math.acos(-objCoord.getY()/Math.sqrt(Math.pow(objCoord.getX(), 2) + Math.pow(-objCoord.getY(), 2)));
					if(objCoord.getX() < 0){
						theta = -theta;
					}
					selectedShape.setRotation(theta);
					model.setShape(selectedIndex, selectedShape);
				}
			} else {
				Point2D.Double newCenter = new Point2D.Double((p2.getX()-diff.getX()), (p2.getY()-diff.getY()));
			
				if(selectedShape instanceof Line) {
					Line l = (Line)selectedShape;
					Point2D.Double len = new Point2D.Double(l.getEnd().getX() - l.getCenter().getX(), l.getEnd().getY() - l.getCenter().getY());
					Point2D.Double newEnd = new Point2D.Double((newCenter.getX()+len.getX()), (newCenter.getY()+len.getY()));
					l.setEnd(newEnd);
					selectedShape = l;
				}else if(selectedShape instanceof Triangle) {
					Triangle t = (Triangle)selectedShape;
					Point2D.Double change = new Point2D.Double(newCenter.getX() - selectedShape.getCenter().getX(), newCenter.getY() - selectedShape.getCenter().getY());
					t.setA(new Point2D.Double(t.getA().getX()+change.getX(),t.getA().getY()+change.getY()));
					t.setB(new Point2D.Double(t.getB().getX()+change.getX(),t.getB().getY()+change.getY()));
					t.setC(new Point2D.Double(t.getC().getX()+change.getX(),t.getC().getY()+change.getY()));
					selectedShape = t;
				}
				selectedShape.setCenter(newCenter);
				model.setShape(selectedIndex, selectedShape);
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {}
	
	@Override
	public void colorButtonHit(Color c) {
		col = c;
		if (selectedIndex > -1){
			Shape s = model.getShape(selectedIndex);
			s.setColor(col);
			model.setShape(selectedIndex, s);
		}
		GUIFunctions.changeSelectedColor(c);
	}

	@Override
	public void lineButtonHit() {
		button = "line";
		triangleCount = 0;
		selectedIndex = -1;
		selectedShape = null;
		setChanged();
		notifyObservers();
	}

	@Override
	public void squareButtonHit() {
		button = "square";
		triangleCount = 0;
		selectedIndex = -1;
		selectedShape = null;
		setChanged();
		notifyObservers();
	}

	@Override
	public void rectangleButtonHit() {
		button = "rectangle";
		triangleCount = 0;
		selectedIndex = -1;
		selectedShape = null;
		setChanged();
		notifyObservers();
	}

	@Override
	public void circleButtonHit() {
		button = "circle";
		triangleCount = 0;
		selectedIndex = -1;
		selectedShape = null;
		setChanged();
		notifyObservers();
	}

	@Override
	public void ellipseButtonHit() {
		button = "ellipse";
		triangleCount = 0;
		selectedIndex = -1;
		selectedShape = null;
		setChanged();
		notifyObservers();
	}

	@Override
	public void triangleButtonHit() {
		button = "triangle";
		triangleCount = 0;
		selectedIndex = -1;
		selectedShape = null;
		setChanged();
		notifyObservers();
	}

	@Override
	public void selectButtonHit() {
		button = "select";
		triangleCount = 0;
		selectedIndex = -1;
		selectedShape = null;
		setChanged();
		notifyObservers();
	}

	@Override
	public void zoomInButtonHit() {
		if(zoomLevel > 1) {
			zoomLevel--;
			
			double x = viewPoint.getX() + viewWidth/2;
			double y = viewPoint.getY() + viewWidth/2;
			viewWidth = viewWidth/2;
			x = x - viewWidth/2;
			y = y - viewWidth/2;
			viewPoint.setLocation(x,y);
			
			setChanged();
			notifyObservers();
		}		
	}

	@Override
	public void zoomOutButtonHit() {
		if(zoomLevel < 5) {
			zoomLevel++;
			
			double x = viewPoint.getX() + viewWidth/2;
			double y = viewPoint.getY() + viewWidth/2;
			viewWidth = viewWidth*2;
			x = x - viewWidth/2;
			y = y - viewWidth/2;

			if (x < 0) {x = 0;}
			else if (x + viewWidth > 2048) {x = x - (x + viewWidth - 2048);}
			if (y < 0) {y = 0;}
			else if (y + viewWidth > 2048) {y = y - (y + viewWidth - 2048);}
			
			viewPoint.setLocation(x, y);
			
			setChanged();
			notifyObservers();
		}
	}

	@Override
	public void hScrollbarChanged(int value) {
		if(value != viewPoint.getX()){
			viewPoint.setLocation(value, viewPoint.getY());
			setChanged();
			notifyObservers();
		}
	}

	@Override
	public void vScrollbarChanged(int value) {
		if(value != viewPoint.getY()){
			viewPoint.setLocation(viewPoint.getX(), value);
			setChanged();
			notifyObservers();
		}
	}

	@Override
	public void openScene(File file) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void toggle3DModelDisplay() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(Iterator<Integer> iterator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void openImage(File file) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveImage(File file) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void toggleBackgroundDisplay() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveDrawing(File file) {
		model.save(file);
	}

	@Override
	public void openDrawing(File file) {
		init();
		model.open(file);
	}

	@Override
	public void doDeleteShape() {
		if(selectedIndex > -1){
			model.deleteShape(selectedIndex);
			selectedIndex = -1;
			selectedShape = null;
			setChanged();
			notifyObservers();
		}
	}

	@Override
	public void doEdgeDetection() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doSharpen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doMedianBlur() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doUniformBlur() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doGrayscale() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doChangeContrast(int contrastAmountNum) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doChangeBrightness(int brightnessAmountNum) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doMoveForward() {
		if (selectedIndex > -1 && selectedIndex != model.getShapes().size()-1){
			model.moveForward(selectedIndex);
			selectedIndex++;
		}
	}

	@Override
	public void doMoveBackward() {
		if (selectedIndex > -1 && selectedIndex != 0){
			model.moveBackward(selectedIndex);
			selectedIndex--;
		}
	}

	@Override
	public void doSendToFront() {
		if (selectedIndex > -1){
			model.moveToFront(selectedIndex);
			selectedIndex = model.getShapes().size()-1;
		}
	}

	@Override
	public void doSendtoBack() {
		if (selectedIndex > -1){
			model.movetoBack(selectedIndex);
			selectedIndex = 0;
			
		}
	}

	public void setModel(MyModel model2) {
		model = model2;
	}

	public Shape getShape() {
		return currentShape;
	}
	
	public Point2D.Double getViewPoint() {
		return viewPoint;
	}
	
	public int getViewWidth() {
		return viewWidth;
	}
	
	public double getScale() {
		if(zoomLevel == 1) {
			return .25;
		} else if (zoomLevel == 2) {
			return .5;
		} else if (zoomLevel == 3) {
			return 1;
		} else if (zoomLevel == 4) {
			return 2;
		} else {
			return 4;
		}
	}
	
	public Shape getSelectedShape() {
		return selectedShape;
	}
	
	public int geometryTest(Point2D worldCoord, int tolerance) {
		shapeList = model.getShapes();
		if(selectedShape != null) {
			if(doHandleCheck(worldCoord,selectedShape)) {
				return selectedIndex;
			}
		}
		
		Point2D.Double objCoord = new Point2D.Double();
		List<Shape> reversed = model.getShapesReversed();
		
		for(int i = 0; i < reversed.size(); i++){
			Shape s = reversed.get(i);
			AffineTransform worldToObj = new AffineTransform(Math.cos(s.getRotation()),-Math.sin(s.getRotation()),Math.sin(s.getRotation()),Math.cos(s.getRotation()),0,0);
			worldToObj.concatenate(new AffineTransform(1,0,0,1,-s.getCenter().getX(), -s.getCenter().getY()));
			worldToObj.transform(worldCoord, objCoord);
			if(s instanceof Line){
				Line l = (Line)s;
				Point2D.Double d = new Point2D.Double();
				double x1 = l.getEnd().getX() - l.getCenter().getX();
				double y1 = l.getEnd().getY() - l.getCenter().getY();
				double lineLength = Math.sqrt((x1)*(x1) + (y1)*(y1));
				d.setLocation((x1)/lineLength, (y1)/lineLength);
				double t = (objCoord.getX())*d.getX() + (objCoord.getY())*d.getY();
				Point2D.Double q = new Point2D.Double();
				q.setLocation(t * d.getX(), t * d.getY());
				double qdist = Math.sqrt((objCoord.getX() - q.getX())*(objCoord.getX() - q.getX()) + (objCoord.getY() - q.getY())*(objCoord.getY() - q.getY()));
				tolerance = (int)(tolerance * this.getScale());
				if (qdist <= tolerance && t >= -tolerance && t <= lineLength + tolerance){
					selectedShape = l;
					selectedIndex = shapeList.size()-i-1;
					setChanged();
					notifyObservers();
					return selectedIndex;
				}
			} else if(s instanceof Square){
				Square sq = (Square)s;
				if (Math.abs(objCoord.getX())<sq.getSize()/2 && Math.abs(objCoord.getY())<sq.getSize()/2){
					selectedShape = sq;
					selectedIndex = shapeList.size()-i-1;
					setChanged();
					notifyObservers();
					return selectedIndex;
				}
			} else if(s instanceof Rectangle){
				Rectangle r = (Rectangle)s;
				if (Math.abs(objCoord.getX())<r.getWidth()/2 && Math.abs(objCoord.getY())<r.getHeight()/2){
					selectedShape = r;
					selectedIndex = shapeList.size()-i-1;
					setChanged();
					notifyObservers();
					return selectedIndex;
				}
			} else if(s instanceof Circle){
				Circle c = (Circle)s;
				if (objCoord.getX()*objCoord.getX() + objCoord.getY()*objCoord.getY() < (c.getRadius()*c.getRadius())){
					selectedShape = c;
					selectedIndex = shapeList.size()-i-1;
					setChanged();
					notifyObservers();
					return selectedIndex;
				}
			}else if(s instanceof Ellipse){
				Ellipse el = (Ellipse)s;
				double a = el.getWidth()/2;
				double b = el.getHeight()/2;
				if ((objCoord.getX()*objCoord.getX())/(a*a) + (objCoord.getY()*objCoord.getY())/(b*b) <= 1){
					selectedShape = el;
					selectedIndex = shapeList.size()-i-1;
					setChanged();
					notifyObservers();
					return selectedIndex;
				}
			}else if(s instanceof Triangle){
				Triangle t = (Triangle)s;
				Point2D.Double a = new Point2D.Double(t.getA().getX() - t.getCenter().getX(), t.getA().getY() - t.getCenter().getY());
				Point2D.Double b = new Point2D.Double(t.getB().getX() - t.getCenter().getX(), t.getB().getY() - t.getCenter().getY());
				Point2D.Double c = new Point2D.Double(t.getC().getX() - t.getCenter().getX(), t.getC().getY() - t.getCenter().getY());

				double triArea = calcArea(a,b,c);
				double a1 = calcArea(objCoord,b,c);
				double a2 = calcArea(objCoord,a,c);
				double a3 = calcArea(objCoord,a,b);
				
				if(a1 + a2 + a3 <= triArea) {
					selectedShape = t;
					selectedIndex = shapeList.size()-i-1;
					setChanged();
					notifyObservers();
					return selectedIndex;
				}
			}else{
				selectedShape = null;
				selectedIndex = -1;
				setChanged();
				notifyObservers();
			}
		}
		selectedShape = null;
		selectedIndex = -1;
		setChanged();
		notifyObservers();
		return -1;
	}
	
	public double calcArea(Point2D A, Point2D B, Point2D C) {
		double area = Math.abs(A.getX() * (B.getY()-C.getY()) + B.getX() * (C.getY() - A.getY()) + C.getX() * (A.getY() - B.getY()));
		return area/2;
	}
	
	public boolean doHandleCheck(Point2D worldCoord, Shape selectedShape) {
		Point2D.Double objCoord = new Point2D.Double();
		AffineTransform worldToObj = new AffineTransform(Math.cos(selectedShape.getRotation()),-Math.sin(selectedShape.getRotation()),Math.sin(selectedShape.getRotation()),Math.cos(selectedShape.getRotation()),0,0);
		worldToObj.concatenate(new AffineTransform(1,0,0,1,-selectedShape.getCenter().getX(), -selectedShape.getCenter().getY()));
		worldToObj.transform(worldCoord, objCoord);
		GUIFunctions.printf("Converted Point: " + (int)objCoord.getX() + "," + (int)objCoord.getY());
		
		if(selectedShape instanceof Line){
			Line s = (Line) selectedShape;
			
			Point2D.Double len = new Point2D.Double(s.getEnd().getX() - s.getCenter().getX(), s.getEnd().getY() - s.getCenter().getY());
			
			if (objCoord.getX()*objCoord.getX() + objCoord.getY()*objCoord.getY() < 100*this.getScale()){
				return true;
			} else if((objCoord.getX()-len.getX())*(objCoord.getX()-len.getX()) + (objCoord.getY()-len.getY())*(objCoord.getY()-len.getY()) < 100*this.getScale()){
				return true;
			}
		}else if(selectedShape instanceof Square){
			Square s = (Square)selectedShape;
			double c = s.getSize()/2 + (20 * this.getScale());
			
			if (objCoord.getX()*objCoord.getX() + (objCoord.getY()+c)*(objCoord.getY()+c) < (100)*this.getScale()){
				return true;
			} 
		} else if (selectedShape instanceof Rectangle) {
			Rectangle s = (Rectangle)selectedShape;
			double c = s.getHeight()/2 + (20 * this.getScale());
			
			if (objCoord.getX()*objCoord.getX() + (objCoord.getY()+c)*(objCoord.getY()+c) < (100)*this.getScale()){
				return true;
			} 
		} else if (selectedShape instanceof Ellipse) {
			Ellipse s = (Ellipse)selectedShape;
			double c = s.getHeight()/2 + (20 * this.getScale());
			
			if (objCoord.getX()*objCoord.getX() + (objCoord.getY()+c)*(objCoord.getY()+c) < (100)*this.getScale()){
				return true;
			} 
		} else if (selectedShape instanceof Triangle) {
			Triangle s = (Triangle)selectedShape;
			double lca = Math.sqrt(Math.pow((s.getCenter().getX() - s.getA().getX()), 2) + Math.pow((s.getCenter().getY() - s.getA().getY()), 2));
			double lcb = Math.sqrt(Math.pow((s.getCenter().getX() - s.getB().getX()), 2) + Math.pow((s.getCenter().getY() - s.getB().getY()), 2));
			double lcc = Math.sqrt(Math.pow((s.getCenter().getX() - s.getC().getX()), 2) + Math.pow((s.getCenter().getY() - s.getC().getY()), 2));

			double c = Math.max(lca, Math.max(lcb,lcc));	
			
			if (objCoord.getX()*objCoord.getX() + (objCoord.getY()+c)*(objCoord.getY()+c) < (100)*this.getScale()){
				return true;
			} 
		}
		return false;
	}
}
