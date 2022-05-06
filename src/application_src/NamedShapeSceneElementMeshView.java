package application_src;

import application_src.application_model.threeD.subscenegeometry.SceneElementMeshView;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class NamedShapeSceneElementMeshView extends SceneElementMeshView {
	String shapeName;

	public NamedShapeSceneElementMeshView(MeshView meshView, String name) {
		super(meshView);
		shapeName = name;
	}

	public NamedShapeSceneElementMeshView(TriangleMesh mesh, String name) {
		super(mesh);
		shapeName = name;
	}

	public String getShapeName() {
		return shapeName;
	}

	public void setShapeName(String cellName) {
		this.shapeName = cellName;
	}

}
