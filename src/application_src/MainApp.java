/*
 * Bao Lab 2017
 */

package application_src;
  
import java.io.IOException;
import java.time.Instant;
import java.util.Map;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import application_src.controllers.controllers.RootLayoutController;
import application_src.application_model.resources.NucleiMgrAdapterResource;

import static java.time.Duration.between;
import static java.time.Instant.now;

import static javafx.application.Platform.setImplicitExit;

import static application_src.application_model.loaders.IconImageLoader.loadImages;

/**
 * Driver class for the WormGUIDES desktop application
 */
public class MainApp extends Application implements ObserveWormGUIDES {

    private static Stage primaryStage;

    private static NucleiMgrAdapterResource nucleiMgrAdapterResource;

    private Scene scene;

    private BorderPane rootLayout;


    public static RootLayoutController controller;
    public static int externallySetStartTime = -1;
    public static IntegerProperty timePropertyMainApp = new SimpleIntegerProperty(1);
    public static BooleanProperty isPlayButtonEnabled = new SimpleBooleanProperty(false);
    public static StringProperty selectedEntityLabelMainApp = new SimpleStringProperty("");



    public static void startProgramatically(final String[] args, final NucleiMgrAdapterResource nmar) {
        nucleiMgrAdapterResource = nmar;
        launch(args);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {
        System.out.println("Starting WormGUIDES JavaFX application");

        loadImages();

        MainApp.primaryStage = primaryStage;
        MainApp.primaryStage.setTitle("WormGUIDESteachingDev");

        final Instant start = now();
        initRootLayout();
        final Instant end = now();
        System.out.println("Root layout initialized in "
                + between(start, end).toMillis()
                + "ms");

        primaryStage.setResizable(true);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            if (controller != null) {
                controller.initCloseApplication();
            }
        });

        controller.getTimeProperty().addListener((observable, oldValue, newValue) -> {
            if (!controller.isTimeSliderPressed()) { // wait until the time slider is release to update the time
                timePropertyMainApp.set(newValue.intValue());
            }
        });

        controller.getPlayingMovieFlag().addListener((observable, oldValue, newValue) -> {
            isPlayButtonEnabled.set(newValue);
        });


    }

    public void initRootLayout() {
        // Load root layout from FXML file.
        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("views/layouts/RootLayout.fxml"));

        if (nucleiMgrAdapterResource != null) {
            loader.setResources(nucleiMgrAdapterResource);
            setImplicitExit(false);
        }
        controller = new RootLayoutController();
        controller.setStage(primaryStage);
        loader.setController(controller);
        loader.setRoot(controller);

        try {
            rootLayout = loader.load();

            scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.centerOnScreen();
            
//  ADDED THIS FILTER TO PREVENT WHACKY CAPTURING OF ARROW KEYS BY EVERY RANDOM WIDGET IN THE CONTROLLER.
            primaryStage.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    Scene scene = controller.getScene();
                    if (scene == null) {
                        return;
                    }
                    if(event.isAltDown()) {                    
                    	if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {

                    		controller.getWindow3DController().getSubscene().getOnMouseDragged().handle(new MouseDragEvent(MouseDragEvent.ANY, 
                    				controller.getWindow3DController().getMousePosX(), 
                    				controller.getWindow3DController().getMousePosY() + (event.getCode()==KeyCode.DOWN? 10:-10), 0, 0, 
                    				null, 0, false,false,false,false, event.isShiftDown(), false, false, false, false, null, null ));	
                    		
                    	} else if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT) {
                    		
                    		controller.getWindow3DController().getSubscene().getOnMouseDragged().handle(new MouseDragEvent(MouseDragEvent.ANY, 
                    				controller.getWindow3DController().getMousePosX() + (event.getCode()==KeyCode.RIGHT? 10:-10), 
                    				controller.getWindow3DController().getMousePosY(), 0, 0, 
                    				null, 0, false,false,false,false, event.isShiftDown(), false, false, false, false, null, null ));	
                    		
                    	}
                    } else {
                    	if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT) {
                    		if(true) {
                    			controller.setTimePropertyValue(controller.getTimeProperty().get() + (event.getCode()==KeyCode.RIGHT?1:-1));
                    		}
                    	}
                    }
                    event.consume();
                    return;
                }
            });

            final Parent root = scene.getRoot();
            for (Node node : root.getChildrenUnmodifiable()) {
                node.setStyle("-fx-focus-color: -fx-outer-border; -fx-faint-focus-color: transparent;");
            }

        } catch (IOException e) {
            System.out.println("Could not initialize root layout");
            e.printStackTrace();
        }
    }

    @Override
    public void updateTime(int time) {
        if (nucleiMgrAdapterResource != null) {
            if (controller != null) {
                Platform.runLater(() -> controller.setTimePropertyValue(time));
            }
        }
    }

    public void flipPlayButtonIcon() {
        if (controller != null) {
            Platform.runLater(() -> controller.flipPlayButtonIcon());
        }
    }

    public void showMainStage() {
        if (controller != null) {
            Platform.runLater(() -> controller.showMainStage());
        }
    }

    public void enableTimeControls() {
        if (controller != null) {
            Platform.runLater(() -> controller.enableTimeControls());
        }
    }

    public void disableTimeControls() {
        if (controller != null) {
            Platform.runLater(() -> controller.disableTimeControls());
        }
    }

    public void buildScene() {
        if (controller != null) {
            controller.buildScene();
        }
    }

    public boolean isClosed() {
        if (primaryStage == null) return true;

        return !primaryStage.isShowing();
    }
}
