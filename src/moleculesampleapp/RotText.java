package moleculesampleapp;

import javafx.geometry.Point3D;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;

public class RotText extends Text {

    public RotText() {
        super();
        getTransforms().add(new Affine());
    }

    public RotText(String title) {
		super(title);
        getTransforms().add(new Affine());
	}

	/**
     * Accumulate rotation about specified axis
     *
     * @param angle
     * @param axis
     */
    public void addRotation(double angle, double pivotX, double pivotY, double pivotZ, Point3D axis) {
        Rotate r = new Rotate(angle
        					, 0
        					, 0  
        					, 0
        					, axis);
        /**
         * This is the important bit and thanks to bronkowitz in this post
         * https://stackoverflow.com/questions/31382634/javafx-3d-rotations for
         * getting me to the solution that the rotations need accumulated in
         * this way
         */
        getTransforms().set(0, r.createConcatenation(getTransforms().get(0)));

     }

    /**
     * Reset transform to identity transform
     */
    public void reset() {
        getTransforms().set(0, new Affine());
    }
}