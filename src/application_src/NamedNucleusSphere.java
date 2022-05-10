package application_src;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;
import javafx.scene.shape.Sphere;

public class NamedNucleusSphere extends Sphere {
	String cellName;
	List<Color> colors;

	public NamedNucleusSphere(String name, List<Color> colors) {
		cellName = name;
		this.colors = colors;
	}

	public NamedNucleusSphere(String name, double radius, List<Color> colors) {
		super(radius);
		cellName = name;
		this.colors = colors;
	}

	public NamedNucleusSphere(String name, double radius, int divisions, List<Color> colors) {
		super(radius, divisions);
		cellName = name;
		this.colors = colors;
	}

	public String getCellName() {
		return cellName;
	}

	public void setCellName(String cellName) {
		this.cellName = cellName;
	}

	public List<Color> getColors() {
		return colors;
	}

	public void setColors(List<Color> colors) {
		this.colors = colors;
	}

}
