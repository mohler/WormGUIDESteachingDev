package moleculesampleapp;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

public class MoleculeSampleApp extends Application {

    Group root = new Group();
    XformBox cameraXform = new XformBox();
    XformBox ballXForm = new XformBox();
    XformBox ball2XForm = new XformBox();
    XformBox ball3XForm = new XformBox();
    XformBox ball4XForm = new XformBox();
    Shape3D ball, ball2, ball3, ball4;
    PhongMaterial redMaterial, greenMaterial, blueMaterial;

    PerspectiveCamera camera = new PerspectiveCamera(true);

    private static double CAMERA_INITIAL_DISTANCE = -450;
    private static double CAMERA_INITIAL_X_ANGLE = -10.0;
    private static double CAMERA_INITIAL_Y_ANGLE = 0.0;
    private static double CAMERA_NEAR_CLIP = 0.1;
    private static double CAMERA_FAR_CLIP = 10000.0;
    private static double AXIS_LENGTH = 250.0;
    public static double MOUSE_SPEED = 0.1;
    public static double ROTATION_SPEED = 2.0;

    double mouseStartPosX, mouseStartPosY;
    double mousePosX, mousePosY;
    double mouseOldX, mouseOldY;
    double mouseDeltaX, mouseDeltaY;
	

    private void handleMouse(Scene scene) {
        System.out.printf("handleMouse%n");

        scene.setOnMousePressed(me -> {
            mouseStartPosX = me.getSceneX();
            mouseStartPosY = me.getSceneY();
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        });

        scene.setOnMouseDragged(me -> {
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseDeltaX = (mousePosX - mouseOldX);
            mouseDeltaY = (mousePosY - mouseOldY);

            if (me.isPrimaryButtonDown()) {
                ballXForm.addRotation(-mouseDeltaX * MOUSE_SPEED * ROTATION_SPEED, 0, 0, 0, Rotate.Y_AXIS);
                ballXForm.addRotation(mouseDeltaY * MOUSE_SPEED * ROTATION_SPEED, 0, 0, 0, Rotate.X_AXIS);
                ball2XForm.addRotation(mouseDeltaX * MOUSE_SPEED * ROTATION_SPEED, 0, 0, 0, Rotate.Y_AXIS);
                ball2XForm.addRotation(mouseDeltaY * MOUSE_SPEED * ROTATION_SPEED, 0, 0, 0, Rotate.X_AXIS);
                ball3XForm.addRotation(-mouseDeltaX * MOUSE_SPEED * ROTATION_SPEED, 0, 0, 0, Rotate.Y_AXIS);
                ball3XForm.addRotation(-mouseDeltaY * MOUSE_SPEED * ROTATION_SPEED, 0, 0, 0, Rotate.X_AXIS);
                ball4XForm.addRotation(mouseDeltaX * MOUSE_SPEED * ROTATION_SPEED, 0, 0, 0, Rotate.Y_AXIS);
                cameraXform.addRotation(mouseDeltaX * MOUSE_SPEED * ROTATION_SPEED/2, 0, 0, 0, Rotate.X_AXIS);
                cameraXform.addRotation(-mouseDeltaY * MOUSE_SPEED * ROTATION_SPEED/2, 0, 0, 0, Rotate.Y_AXIS);
            }
        });
    }

    private void handleKeyboard(Scene scene) {
        scene.setOnKeyPressed(event -> {ballXForm.reset();ball2XForm.reset();ball3XForm.reset();ball4XForm.reset();});
    }

    PhongMaterial createMaterial(Color diffuseColor, Color specularColor) {
        PhongMaterial material = new PhongMaterial(diffuseColor);
        material.setSpecularColor(specularColor);
        return material;
    }

    @Override
    public void start(Stage primaryStage) {
        System.out.printf("start%n");
        root.setDepthTest(DepthTest.ENABLE);

        // Create materials
        redMaterial = createMaterial(Color.DARKRED, Color.RED);
        greenMaterial = createMaterial(Color.DARKGREEN, Color.GREEN);
        blueMaterial = createMaterial(Color.DARKBLUE, Color.BLUE);

        // Build Camera
        root.getChildren().add(camera);
        cameraXform.getChildren().add(camera);
        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        cameraXform.addRotation(CAMERA_INITIAL_X_ANGLE, 0, 0, 0, Rotate.X_AXIS);
        cameraXform.addRotation(CAMERA_INITIAL_Y_ANGLE, 0, 0, 0, Rotate.Y_AXIS);

        // Build Axes
        Box xAxis = new Box(AXIS_LENGTH, 1, 1);
        Box yAxis = new Box(1, AXIS_LENGTH, 1);
        Box zAxis = new Box(1, 1, AXIS_LENGTH);
        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);
        root.getChildren().addAll(xAxis, yAxis, zAxis);

        // Build shiney red ball
        ball = new Sphere(50);
        ball.setDrawMode(DrawMode.LINE); // draw mesh so we can watch how it rotates
        ballXForm.setLayoutX(-50.0);
        ballXForm.setLayoutY(-50.0);
        ball.setScaleX(-1);
        ballXForm.getChildren().add(ball);
        ball2 = new Sphere(50);
        ball2.setDrawMode(DrawMode.LINE); // draw mesh so we can watch how it rotates
        ball2XForm.setLayoutX(50.0);
        ball2XForm.setLayoutY(-50.0);
        ball2XForm.getChildren().add(ball2);
        ball3 = new Sphere(50);
        ball3.setDrawMode(DrawMode.LINE); // draw mesh so we can watch how it rotates
        ball3XForm.setLayoutX(-50.0);
        ball3XForm.setLayoutY(50.0);
        ball3XForm.getChildren().add(ball3);
        ball4 = new Sphere(50);
        ball4.setDrawMode(DrawMode.LINE); // draw mesh so we can watch how it rotates
        ball4XForm.setLayoutX(50.0);
        ball4XForm.setLayoutY(50.0);
        ball4.setScaleX(-1);
        ball4XForm.getChildren().add(ball4);
       root.getChildren().addAll(ballXForm);
       root.getChildren().addAll(ball2XForm);
       root.getChildren().addAll(ball3XForm);
       root.getChildren().addAll(ball4XForm);

        Scene scene = new Scene(root, 1024, 768, true);
        scene.setFill(Color.GREY);
        handleKeyboard(scene);
        handleMouse(scene);

        primaryStage.setTitle("TrackBall");
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.setCamera(camera);
    }

    public static void main(String[] args) {
        launch(args);
    }

}