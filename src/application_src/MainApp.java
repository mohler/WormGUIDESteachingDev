/*
 * Bao Lab 2017
 */

package application_src;
  
import static application_src.application_model.loaders.IconImageLoader.loadImages;
import static java.time.Duration.between;
import static java.time.Instant.now;
import static javafx.application.Platform.setImplicitExit;

import java.io.IOException;
import java.time.Instant;

import application_src.application_model.resources.NucleiMgrAdapterResource;
import application_src.controllers.controllers.RootLayoutController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Driver class for the WormGUIDES desktop application
 */
public class MainApp extends Application implements ObserveWormGUIDES {

    private  Stage primaryStage;

    private  NucleiMgrAdapterResource nucleiMgrAdapterResource;

    private  Scene scene;

    private BorderPane rootLayout;


    public  RootLayoutController controller;
    public  static int externallySetStartTime = -1;
    public  static IntegerProperty timePropertyMainApp = new SimpleIntegerProperty(1);
    public  static BooleanProperty isPlayButtonEnabled = new SimpleBooleanProperty(false);
    public  static StringProperty selectedEntityLabelMainApp = new SimpleStringProperty("");

	private  EventHandler<KeyEvent> keyHandler;



    public  void startProgramatically(final String[] args, final NucleiMgrAdapterResource nmar) {
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

        this.primaryStage = primaryStage;
        primaryStage.setTitle("WormGUIDESteachingDev");

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
            
            keyHandler = new EventHandler<KeyEvent>() {

            	@Override
            	public void handle(KeyEvent event) {
            		if (scene == null) {
            			return;
            		}
            		if(event.isShortcutDown()) {                    
            			KeyCode code = event.getCode();
            			if (code == KeyCode.UP || code == KeyCode.DOWN) {

            				controller.getWindow3DController().getSubscene().getOnMouseDragged().handle(new MouseDragEvent(MouseDragEvent.ANY, 
            						controller.getWindow3DController().getMousePosX(), 
            						controller.getWindow3DController().getMousePosY() + (code==KeyCode.DOWN? 20:-20), 0, 0, 
            						null, 1, event.isShiftDown(),false,false,false, true, false, false, false, false, null, null ));	
                    		event.consume();

            			} else if (code == KeyCode.RIGHT || code == KeyCode.LEFT) {

            				controller.getWindow3DController().getSubscene().getOnMouseDragged().handle(new MouseDragEvent(MouseDragEvent.ANY, 
            						controller.getWindow3DController().getMousePosX() + (code==KeyCode.RIGHT? 20:-20), 
            						controller.getWindow3DController().getMousePosY(), 0, 0, 
            						null, 1, event.isShiftDown(),false,false,false, true, false, false, false, false, null, null ));	
                    		event.consume();

            			}
            		} else  {
            			KeyCode code = event.getCode();
            			if (code == KeyCode.UP || code == KeyCode.DOWN) {

            	            double z = controller.getWindow3DController().translateZProperty.get();
            	            if (code == KeyCode.UP ) {
            	                // zoom out
            	            	z = (z * 1.125);
            	             } else if (code == KeyCode.DOWN) {
            	                // zoom in
            	             	z = (z / 1.125);
            	            }
            	            controller.getWindow3DController().translateZProperty.set(z);
            	        



            			} else if (code == KeyCode.RIGHT || code == KeyCode.LEFT) {
            				if(true) {
            					controller.setTimePropertyValue(controller.getTimeProperty().get() + (code == KeyCode.RIGHT?1:-1));
                        		event.consume();

            				}
            			}
            		}
            		return;
            	}
            };

//KeyEvent.ANY seems to be crucial thing in next line
            rootLayout.addEventFilter(KeyEvent.KEY_PRESSED, keyHandler);
            final Parent root = scene.getRoot();
            for (Node node : root.getChildrenUnmodifiable()) {
            	node.setStyle("-fx-focus-color: -fx-outer-border; -fx-faint-focus-color: transparent;");
            	node.setFocusTraversable(false);
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
