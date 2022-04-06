package application_src;

import javafx.scene.shape.Sphere;

public class NamedNucleusSphere extends Sphere {
	String cellName;

	public NamedNucleusSphere(String name) {
		cellName = name;
	}

	public NamedNucleusSphere(String name, double radius) {
		super(radius);
		cellName = name;
	}

	public NamedNucleusSphere(String name, double radius, int divisions) {
		super(radius, divisions);
		cellName = name;
	}

	public String getCellName() {
		return cellName;
	}

	public void setCellName(String cellName) {
		this.cellName = cellName;
	}

}
