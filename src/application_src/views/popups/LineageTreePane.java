/*
 * Bao Lab 2017
 */

package application_src.views.popups;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import application_src.application_model.annotation.AnnotationManager;
import application_src.application_model.search.SearchConfiguration.SearchOption;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;

import application_src.application_model.data.LineageData;
import application_src.controllers.controllers.ContextMenuController;
import application_src.controllers.layers.SearchLayer;
import application_src.application_model.annotation.color.Rule;
import application_src.application_model.annotation.color.ColorHash;

import static java.lang.Math.max;
import static java.lang.Math.round;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import static javafx.application.Platform.runLater;
import static javafx.embed.swing.SwingFXUtils.fromFXImage;
import static javafx.scene.Cursor.DEFAULT;
import static javafx.scene.Cursor.HAND;
import static javafx.scene.control.ContentDisplay.GRAPHIC_ONLY;
import static javafx.scene.control.Tooltip.install;
import static javafx.scene.input.KeyCode.F5;
import static javafx.scene.input.KeyCode.P;
import static javafx.scene.input.KeyEvent.KEY_PRESSED;
import static javafx.scene.input.MouseButton.PRIMARY;
import static javafx.scene.input.MouseButton.SECONDARY;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.WHITE;
import static javafx.scene.paint.Color.web;
import static javafx.scene.text.Font.font;
import static javafx.scene.text.FontWeight.SEMI_BOLD;

import static javax.imageio.ImageIO.write;
import static application_src.application_model.data.CElegansData.PartsList.PartsList.getFunctionalNameByLineageName;
import static application_src.application_model.search.SearchConfiguration.SearchType.LINEAGE;
import static application_src.application_model.search.SearchConfiguration.SearchType.NEIGHBOR;
import static application_src.application_model.loaders.IconImageLoader.getMinusIcon;
import static application_src.application_model.loaders.IconImageLoader.getPlusIcon;
import static application_src.application_model.search.SearchConfiguration.SearchOption.CELL_NUCLEUS;
import application_src.application_model.data.CElegansData.SulstonLineage.SulstonLineageTree;
import application_src.application_model.data.GenericData.GenericLineageTree;
import static application_src.application_model.data.CElegansData.PartsList.PartsList.getLineageNamesByFunctionalName;
import static application_src.application_model.data.CElegansData.PartsList.PartsList.getLineageNamesByDescription;

public class LineageTreePane extends ScrollPane {
    //TODO decouple rendering as separate class from WG app specific interaction -as
    // gui stuff
    private static final int TIME_LABEL_OFFSET_X = 20;
    private static final int ZOOM_BUTTON_SIZE = 30;
    private static final int SEARCH_BOX_HEIGHT = 30;
    private static final int SEARCH_BOX_WIDTH = 250;
    private static final double DEFAULT_WINDOW_HEIGHT = 820;
    private static final double DEFAULT_WINDOW_WIDTH = 775;
    private static final Color ZOOM_BUTTONS_SHADOW_COLOR = web("AAAAAA");

    private final LineageData lineageData;
    private final SearchLayer searchLayer;

    private final Stage contextMenuStage;
    private final ContextMenuController contextMenuController;
    private final StringProperty selectedNameLabeledProperty;
    private final BooleanProperty rebuildSubsceneFlag;
    private final Stage ownStage;
    private final AnchorPane canvas;
    private final EventHandler<MouseEvent> clickHandler;

    private final int movieTimeOffset;

    public final Map<String, Integer> nameXUseMap;
    private final Map<String, Integer> nameYStartUseMap;
    public final List<String> hiddenNodes;
    private final TreeItem<String> lineageTreeRoot;
    private final ColorHash colorHash;
    private final IntegerProperty timeProperty;

    private final ObservableList<Rule> rules;
    public final AnchorPane mainPane;
    private final Group zoomGroup;

    /** Keeps track of the current x layout position */
    private int maxX = 0;

    // branch gap seems to be some multiple of this?
    public Scale scaleTransform;
    private Line timeIndicatorBar;
    private Text timeIndicator;
    private int ttduration = 0;

    // =XScale minimal spacing between branches, inter
    private int xsc = 5;
    // left margin
    private int iXmax = 30;
    public int iYmin = 30;  //testing 19

    private boolean defaultEmbryoFlag;
    private AnnotationManager annotationManager;

    public ArrayList<String> currMatchCellNames;
	public ArrayList<Ellipse> pickedCellMarkers;

    public static void ensureVisible(ScrollPane pane, Node node, Scale scaleTransform) {
        double width = pane.getContent().getBoundsInLocal().getWidth();
        double height = pane.getContent().getBoundsInLocal().getHeight();

        double x = node.getBoundsInParent().getMaxX();
        double y = node.getBoundsInParent().getMaxY();

        // scrolling values range from 0 to 1
        pane.setVvalue(y*scaleTransform.getY()/height);
        pane.setHvalue(x*scaleTransform.getX()/width);

        // just for usability
        node.requestFocus();
    }

	
    public LineageTreePane(
            final Stage ownStage,
            final SearchLayer searchLayer,
            final LineageData lineageData,
            final int movieTimeOffset,
            final TreeItem<String> lineageTreeRoot,
            final ColorHash colorHash,
            final IntegerProperty timeProperty,
            final Stage contextMenuStage,
            final ContextMenuController contextMenuController,
            final StringProperty selectedNameLabeledProperty,
            final BooleanProperty rebuildSubsceneFlag,
            final boolean defaultEmbryoFlag,
            final AnnotationManager annotationManager) {

        super();

        this.searchLayer = requireNonNull(searchLayer);
        this.defaultEmbryoFlag = requireNonNull(defaultEmbryoFlag);
        currMatchCellNames = new ArrayList<>();
        pickedCellMarkers = new ArrayList<>();

        hiddenNodes = new ArrayList<>();


        this.ownStage = requireNonNull(ownStage);

        this.canvas = new AnchorPane();
        this.mainPane = requireNonNull(canvas);
        this.lineageData = requireNonNull(lineageData);
        this.movieTimeOffset = movieTimeOffset;

        this.timeProperty = requireNonNull(timeProperty);
        this.timeProperty.addListener((observable, oldValue, newValue) -> repositionTimeLine());

        this.annotationManager = annotationManager;
        clickHandler = event -> {
            final String sourceName = ((Node) event.getSource()).getId();
            if (sourceName != null && !sourceName.isEmpty()) {
                // right click
                if (event.getButton() == SECONDARY
                        || (event.getButton() == PRIMARY && (event.isControlDown() || event.isMetaDown()))) {
                    resetSelectedNameLabeled(sourceName);
                    showContextMenu(sourceName, event.getScreenX(), event.getScreenY());
                }
                // left click
                else if (event.getButton() == PRIMARY) {
                    // on a double click, expand/contract the clicked node
                    if (event.getClickCount() == 2) {
                        if (hiddenNodes.contains(sourceName)) {
                            hiddenNodes.remove(sourceName);
                        } else {
                            hiddenNodes.add(sourceName);
                        }
                        updateDrawing();
                    } else {
                        // reset the name to activate navigate3d in 3d cell window
        				if (pickedCellMarkers != null) {
        					mainPane.getChildren().removeAll(pickedCellMarkers);
        					pickedCellMarkers.clear();
        				}
                        timeProperty.set(((int) round(event.getY())) - movieTimeOffset - 11); //11 ot offset the increase on iymin
                        pickedCellMarkers.add(0, new Ellipse(event.getX(), event.getY(), 5,5));
                        pickedCellMarkers.get(0).setFill(web("#ffffff00"));
                        pickedCellMarkers.get(0).setStroke(Color.MAGENTA);
                        pickedCellMarkers.get(0).setStrokeWidth(1);
    					if (pickedCellMarkers != null) {
    						mainPane.getChildren().addAll(pickedCellMarkers);
    						for (Ellipse pcm:pickedCellMarkers)
    							pcm.toBack();    	
    					}
                        resetSelectedNameLabeled(sourceName);
                    }
                }
            }
        };

        setUpDefaultView();

        this.colorHash = requireNonNull(colorHash);
        this.lineageTreeRoot = requireNonNull(lineageTreeRoot);

        this.rebuildSubsceneFlag = requireNonNull(rebuildSubsceneFlag);
        this.rebuildSubsceneFlag.addListener((observable, oldValue, newValue) -> {
            updateColoring();
        });

        this.rules = requireNonNull(annotationManager.getRulesList());
        this.rules.addListener((ListChangeListener<Rule>) listener -> rebuildSubsceneFlag.set(true));

        this.nameXUseMap = new HashMap<>();
        this.nameYStartUseMap = new HashMap<>();

        // zooming
        this.scaleTransform = new Scale(1.75, 1.75, 0, 0);

        final Group contentGroup = new Group();
        this.zoomGroup = new Group();

        contentGroup.getChildren().add(zoomGroup);
        this.zoomGroup.getChildren().add(canvas);
        this.zoomGroup.getTransforms().add(scaleTransform);

        this.canvas.setVisible(true);

        getChildren().add(contentGroup);
        setPannable(true);
        setPrefSize(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);

        // add controls for zoom
        final DropShadow shadow = new DropShadow();
        shadow.setRadius(3.5);
        shadow.setOffsetX(4);
        shadow.setOffsetY(3.5);
        shadow.setColor(ZOOM_BUTTONS_SHADOW_COLOR);

        final Button plusButton = new Button();
        plusButton.setContentDisplay(GRAPHIC_ONLY);
        plusButton.setGraphic(new ImageView(getPlusIcon()));
        plusButton.setStyle("-fx-focus-color: -fx-outer-border; "
                + "-fx-faint-focus-color: transparent;"
                + "-fx-background-color: transparent;");
        plusButton.setPrefSize(ZOOM_BUTTON_SIZE, ZOOM_BUTTON_SIZE);
        plusButton.setMaxSize(ZOOM_BUTTON_SIZE, ZOOM_BUTTON_SIZE);
        plusButton.setMinSize(ZOOM_BUTTON_SIZE, ZOOM_BUTTON_SIZE);
        plusButton.setEffect(shadow);

        final Button minusButton = new Button();
        minusButton.setContentDisplay(GRAPHIC_ONLY);
        minusButton.setGraphic(new ImageView(getMinusIcon()));
        minusButton.setStyle("-fx-focus-color: -fx-outer-border; "
                + "-fx-faint-focus-color: transparent;"
                + "-fx-background-color: transparent;");
        minusButton.setPrefSize(ZOOM_BUTTON_SIZE, ZOOM_BUTTON_SIZE);
        minusButton.setMaxSize(ZOOM_BUTTON_SIZE, ZOOM_BUTTON_SIZE);
        minusButton.setMinSize(ZOOM_BUTTON_SIZE, ZOOM_BUTTON_SIZE);
        minusButton.setEffect(shadow);

        contentGroup.getChildren().add(plusButton);
        contentGroup.getChildren().add(minusButton);
        plusButton.getTransforms().add(new Translate(50, 5));
        minusButton.getTransforms().add(new Translate(15, 5));

        plusButton.setOnMousePressed(event -> {
            scaleTransform.setX(scaleTransform.getX() * 1.3333);
            scaleTransform.setY(scaleTransform.getY() * 1.3333);
        });

        minusButton.setOnMousePressed(event -> {
            scaleTransform.setX(scaleTransform.getX() * .75);
            scaleTransform.setY(scaleTransform.getY() * .75);
        });

        //search box
        final TextField searchField = new TextField();
        searchField.setPrefSize(SEARCH_BOX_WIDTH, SEARCH_BOX_HEIGHT);
        searchField.setMaxSize(SEARCH_BOX_WIDTH, SEARCH_BOX_HEIGHT);
        searchField.setMinSize(SEARCH_BOX_WIDTH, SEARCH_BOX_HEIGHT);
        searchField.setEffect(shadow);
        searchField.setPromptText("search here");

        contentGroup.getChildren().add(searchField);
        searchField.getTransforms().add(new Translate(100, 5));

        EventHandler<ActionEvent> event = e -> {
            String currsearchtext = searchField.getText();
            if (!currsearchtext.trim().equals("")) {
                System.out.println("searching " + currsearchtext + "...");
                searchNameNode(currsearchtext);
            } else {
                System.out.println("search string cannot be empty string");
            }
        };

        // when enter is pressed
        searchField.setOnAction(event);

        //end search box

        final Pane yetanotherlevel = new Pane();
        yetanotherlevel.getChildren().add(contentGroup);
        setContent(yetanotherlevel);

        bindLocationButton(plusButton, this, yetanotherlevel);
        bindLocationButton(minusButton, this, yetanotherlevel);
        bindLocationTextField(searchField, this, yetanotherlevel);

        this.contextMenuController = requireNonNull(contextMenuController);
        this.contextMenuStage = requireNonNull(contextMenuStage);

        this.selectedNameLabeledProperty = requireNonNull(selectedNameLabeledProperty);

        // keyboard shortcut for screenshot
//        ownStage.addEventHandler(KEY_PRESSED, keyEvent -> {
//            if (keyEvent.getCode() == F5) {
//                final Stage fileChooserStage = new Stage();
//
//                final FileChooser fileChooser = new FileChooser();
//                fileChooser.setTitle("Choose Save Location");
//                fileChooser.getExtensionFilters().add(new ExtensionFilter("PNG File", "*.png"));
//
//                final WritableImage screenCapture = mainPane.snapshot(new SnapshotParameters(), null);
//                // write the image to a file
//                try {
//                    final File file = fileChooser.showSaveDialog(fileChooserStage);
//                    if (file != null) {
//                        RenderedImage renderedImage = fromFXImage(screenCapture, null);
//                        write(renderedImage, "png", file);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    /**
     * Adds the tree drawing as well as timeline marks to the tree window. This is called after the scene for this
     * tree pane is set by the {RootLayoutController}.
     */
    public void addDrawing() {
        addLines(lineageTreeRoot, mainPane);
    }

    // stolen from web to hack these tooltips to come up faster
    public static void hackTooltipStartTiming(final Tooltip tooltip, final int duration) {
        try {
            final Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            final Object objBehavior = fieldBehavior.get(tooltip);

            final Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            final Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(duration)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Called by {RootLayoutController} to resizes this scrollpane and
     * canvas to fit the window. Gets rid of extraneous space outside of the
     * tree.
     */
    public void resizeStageContents() {
        ownStage.widthProperty().addListener((observableValue, oldStageWidth, newStageWidth)
                -> canvas.setPrefWidth(newStageWidth.doubleValue()));
        canvas.setPrefWidth(ownStage.widthProperty().get());

        ownStage.heightProperty().addListener((observableValue, oldStageHeight, newStageHeight)
                -> canvas.setPrefHeight(newStageHeight.doubleValue()));
        canvas.setPrefHeight(ownStage.heightProperty().get());
    }

    private void resetSelectedNameLabeled(final String name) {
//        selectedNameLabeledProperty.set("");
        if (name != null) {
            selectedNameLabeledProperty.set(name);
        }
    }

    /**
     * Sets up the default view of the lineage tree, with specific nodes hidden
     */
    private void setUpDefaultView() {
        // empty lines indicate a different level of the lineage tree
        if (lineageData.isSulstonMode()) {
//            hiddenNodes.add("ABalaa");
//            hiddenNodes.add("ABalap");
//            hiddenNodes.add("ABalpa");
//            hiddenNodes.add("ABalpp");
//            hiddenNodes.add("ABaraa");
//            hiddenNodes.add("ABarap");
//            hiddenNodes.add("ABarpa");
//            hiddenNodes.add("ABarpp");
//            hiddenNodes.add("ABplaa");
//            hiddenNodes.add("ABplap");
//            hiddenNodes.add("ABplpa");
//
//            hiddenNodes.add("ABplppaa");
//            hiddenNodes.add("ABplpppp");
//
//            hiddenNodes.add("ABpraa");
//            hiddenNodes.add("ABprap");
//
//            hiddenNodes.add("ABprpaaa");
//            hiddenNodes.add("ABprppaa");
//
//            hiddenNodes.add("ABprpapaa");
//
//            hiddenNodes.add("Abprppaa");
//
//            hiddenNodes.add("ABprppp");
//
//            hiddenNodes.add("MSaa");
//            hiddenNodes.add("MSap");
//            hiddenNodes.add("MSpa");
//            hiddenNodes.add("MSpp");
//
//            hiddenNodes.add("Ea");
//            hiddenNodes.add("Ep");
//
//            hiddenNodes.add("Caa");
//            hiddenNodes.add("Cap");
//            hiddenNodes.add("Cpa");
//            hiddenNodes.add("Cpp");
//
//            hiddenNodes.add("D");
//            hiddenNodes.add("P4");
        } else {
            //hide random cells for aesthetic purpose
            //comment out the following snippet to show all cells by default
            List<String> allCellNames = lineageData.getAllCellNames();
            int numCells = allCellNames.size();
            int numDefaultHiddenNodes = 0;
            //set a max limit of 2000 on number of default hidden nodes to avoid unnecessary overhead
            if (numCells < 10000) {
                numDefaultHiddenNodes = numCells / 5;
            } else {
                numDefaultHiddenNodes = 2000;
            }
            Random r = new Random();

            for (int i = 0; i < numDefaultHiddenNodes; i++) {
                int randomIndex = r.nextInt(numCells);
                String nextCellName = allCellNames.get(randomIndex);
                if (!hiddenNodes.contains(nextCellName)) {
                    hiddenNodes.add(nextCellName);
                } else {
                    i--;
                }
            }
        }
        //testing add all cell into hiddenNodes
        /*
        for (String cellName:lineageData.getAllCellNames()) {
            hiddenNodes.add(cellName);
        }
        */
    }

    private void showContextMenu(final String name, final double sceneX, final double sceneY) {
        if (contextMenuStage != null) {
            contextMenuController.setName(name);

            final String funcName = getFunctionalNameByLineageName(name);

            if (funcName == null) {
                contextMenuController.disableWiredToFunction(true);
            } else {
                contextMenuController.disableWiredToFunction(false);
            }

            contextMenuController.setColorButtonListener(event -> {
                System.out.println("Pressed color button");
                List<SearchOption> options = new ArrayList<>();
                options.add(CELL_NUCLEUS);
                final Rule rule = annotationManager.addColorRule(
                        LINEAGE,
                        name,
                        WHITE,
                        new ArrayList<>(),
                        options);
                rule.showEditStage(ownStage);
                contextMenuStage.hide();
            });

            contextMenuController.setColorNeighborsButtonListener(event -> {
                // call distance SearchLayer method
                List<SearchOption> options = new ArrayList<>();
                options.add(CELL_NUCLEUS);
                final Rule rule = annotationManager.addColorRule(NEIGHBOR,
                        name,
                        WHITE,
                        new ArrayList<>(),
                        options);
                rule.showEditStage(ownStage);
                contextMenuStage.hide();
            });

            contextMenuStage.setX(sceneX);
            contextMenuStage.setY(sceneY);
            contextMenuStage.show();

            ((Stage) contextMenuStage.getScene().getWindow()).toFront();
        }
    }

    //search function
    //currently on works with sulston cell name
    private void searchNameNode(String name) {
    	//clear current match cell names from previous search
    	currMatchCellNames.clear();
    	//check if a match exist
    	//check by lineage name
    	TreeItem<String> targetnode = null;
    	String properName = name;
    	targetnode = GenericLineageTree.getNodeWithName(name);
    	//check by function name
    	List<String> cellnamesbyfunc = getLineageNamesByFunctionalName(name);
    	//check by description
    	List<String> cellnamesbyDescip = getLineageNamesByDescription(name);
    	if (targetnode != null) {

    		System.out.println("Searched by lineage name.");
    		//set current match cell name for highlighting
    		currMatchCellNames.add(targetnode.getValue());
    		properName = targetnode.getValue();
    		//show the cell and its ancestors
    		//            if(!hiddenNodes.contains(targetnode.getValue())) {
    		//                hiddenNodes.add(targetnode.getValue());
    		//            }
    		TreeItem<String> currnode = targetnode.getParent();
    		while (currnode != null) {
    			if (hiddenNodes.contains(currnode.getValue())) {
    				hiddenNodes.remove(currnode.getValue());
    			}
    			currnode = currnode.getParent();
    		}
    	} else if (cellnamesbyfunc.size() > 0 && lineageData.isSulstonMode()){
    		System.out.println("Searched by function name.");
    		for (String cname:cellnamesbyfunc) {
    			properName = cname;
    			//add current match cell name for highlighting
    			if (GenericLineageTree.getNodeWithName(cname) != null) {
    				currMatchCellNames.add(cname);
    				//show the cell and its ancestors
    				TreeItem<String> currnode = GenericLineageTree.getNodeWithName(cname).getParent();
    				while (currnode != null) {
    					if (hiddenNodes.contains(currnode.getValue())) {
    						hiddenNodes.remove(currnode.getValue());
    					}
    					currnode = currnode.getParent();
    				}
    			}
    		}
    	} else if (cellnamesbyDescip.size() > 0 && lineageData.isSulstonMode()) {
    		System.out.println("Searched by cell description.");
    		for (String cname:cellnamesbyDescip) {
    			properName = cname;
    			//add current match cell name for highlighting
    			if (GenericLineageTree.getNodeWithName(cname) != null) {
    				currMatchCellNames.add(cname);
    				//show the cell and its ancestors
    				TreeItem<String> currnode = GenericLineageTree.getNodeWithName(cname).getParent();
    				while (currnode != null) {
    					if (hiddenNodes.contains(currnode.getValue())) {
    						hiddenNodes.remove(currnode.getValue());
    					}
    					currnode = currnode.getParent();
    				}
    			}
    		}
    	} else {
    		System.out.println("No result found.");
    	}
    	updateDrawing();
    	if (pickedCellMarkers != null) {
    		mainPane.getChildren().removeAll(pickedCellMarkers);
    		pickedCellMarkers.clear();;
    	}
    	if (currMatchCellNames != null && currMatchCellNames.size() >0)
    		timeProperty.set(((int) round(nameYStartUseMap.get(currMatchCellNames.get(0)))) - movieTimeOffset - 11); //11 ot offset the increase on iymin
    	for (String cmcn:currMatchCellNames) {
    		pickedCellMarkers.add(0, new Ellipse(nameXUseMap.get(cmcn), nameYStartUseMap.get(cmcn), 5,5));
    		pickedCellMarkers.get(0).setFill(web("#ffffff00"));
    		pickedCellMarkers.get(0).setStroke(Color.MAGENTA);
    		pickedCellMarkers.get(0).setStrokeWidth(1);
    	}
    	if (pickedCellMarkers != null) {
    		mainPane.getChildren().addAll(pickedCellMarkers);
    		for (Ellipse pcm:pickedCellMarkers)
    			pcm.toBack();    	
    	}
    	resetSelectedNameLabeled(properName);

    }

    private void repositionTimeLine() {
        timeIndicatorBar.setEndY(iYmin + timeProperty.getValue());
        timeIndicatorBar.setStartY(iYmin + timeProperty.getValue());
        timeIndicator.setY(iYmin + timeProperty.getValue());
        if (defaultEmbryoFlag) {
            timeIndicator.setText(Integer.toString(timeProperty.get() + movieTimeOffset));
        } else {
            timeIndicator.setText(Integer.toString(timeProperty.get()));
        }
    }

    private void bindLocationButton (Button plus, ScrollPane s, Pane scontent) {
        plus.layoutYProperty().bind(
                // to vertical scroll shift (which ranges from 0 to 1)
                s.vvalueProperty()
                        // multiplied by (scrollableAreaHeight - visibleViewportHeight)
                        .multiply(scontent.heightProperty().subtract(new ScrollPaneViewPortHeightBinding(s))));

        plus.layoutXProperty().bind(
                // to vertical scroll shift (which ranges from 0 to 1)
                s.hvalueProperty()
                        // multiplied by (scrollableAreaHeight - visibleViewportHeight)
                        .multiply(scontent.widthProperty().subtract(new ScrollPaneViewPortWidthBinding(s))));

    }

    private void bindLocationTextField (TextField field, ScrollPane s, Pane scontent) {
        field.layoutYProperty().bind(
                // to vertical scroll shift (which ranges from 0 to 1)
                s.vvalueProperty()
                        // multiplied by (scrollableAreaHeight - visibleViewportHeight)
                        .multiply(scontent.heightProperty().subtract(new ScrollPaneViewPortHeightBinding(s))));

        field.layoutXProperty().bind(
                // to vertical scroll shift (which ranges from 0 to 1)
                s.hvalueProperty()
                        // multiplied by (scrollableAreaHeight - visibleViewportHeight)
                        .multiply(scontent.widthProperty().subtract(new ScrollPaneViewPortWidthBinding(s))));
    }

    public void updateDrawing() {
        // clear drawing
        mainPane.getChildren().clear();
        maxX = 0;
        // update drawing
        addLines(lineageTreeRoot, mainPane);
    }

    public void updateColoring() {
        // iterate over all drawn lines and recompute their color
        final ObservableList<Node> contentnodes = mainPane.getChildren();

        // note this is relying on using last color to set colors for division lines that return null because are
        // tagged with both
        contentnodes.stream()
                .filter(currentnode -> currentnode instanceof Line)
                .forEachOrdered(currentnode -> {
                    final Line currline = (Line) currentnode;
                    final Paint lnewcolors = paintThatAppliesToCell(currentnode.getId());

                    // note this is relying on using last color to set colors for
                    // division lines that return null because are tagged with both
                    runLater(() -> {
                        if (lnewcolors != null) {
                            currline.setStroke(lnewcolors);
                        } else {
                            if (currline != null && currline.getId() != null) {
                                if (!currline.getId().equals("timeProperty")) {
                                    currline.setStroke(BLACK);
                                }
                            }
                        }
                    });
                });
    }

    private void addLines(final TreeItem<String> lineageTreeRoot, final Pane mainPane) {
        final Scene scene = getScene();
        if (scene != null && scene.getWindow().isShowing()) {
            if (lineageTreeRoot != null) {
                recursiveDraw(mainPane, 400, 10, lineageTreeRoot, 10);
            }
            // add timeProperty indicator bar
            int timevalue = timeProperty.getValue();
            timeIndicatorBar = new Line(0, iYmin + timevalue, maxX + iXmax, iYmin + timevalue);
            timeIndicatorBar.setStroke(new Color(.5, .5, .5, .5));
            timeIndicatorBar.setId("timeProperty");

            // add timeProperty indicator
            if (defaultEmbryoFlag) {
                timeIndicator = new Text(
                        TIME_LABEL_OFFSET_X,
                        iYmin + timevalue,
                        Integer.toString(timeProperty.get() + movieTimeOffset));
            } else {
                timeIndicator = new Text(TIME_LABEL_OFFSET_X, iYmin + timevalue, Integer.toString(timeProperty.get()));
            }
            timeIndicator.setFont(font("System", SEMI_BOLD, 6));
            timeIndicator.setStroke(new Color(.5, .5, .5, .5));
            timeIndicator.setId("timeValue");
            mainPane.getChildren().addAll(timeIndicatorBar, timeIndicator);
            timeIndicatorBar.toBack();
            
            drawTimeTicks();
        }
    }

    // retrieves material for use as texture on lines
    private Paint paintThatAppliesToCell(String cellname) {
        if (cellname != null) {
            final List<Color> colors = new ArrayList<>();
            // iterate over rulesList
            colors.addAll(rules.stream()
                    .filter(rule -> rule.appliesToCellNucleus(cellname) || rule.appliesToCellBody(cellname))
                    .map(Rule::getColor)
                    .collect(toList()));

            // translate color list to material from material cache
            if (!colors.isEmpty()) {
                final PhongMaterial m = (PhongMaterial) colorHash.getMaterial(colors);
                final Image i = m.getDiffuseMap();

                if (i != null) {
                    final ImagePattern ip = new ImagePattern(i, 0, 0, 21, 21, false);
                    return ip;
                }
            }
        }
        return null;
    }

    private void drawTimeTicks() {
        for (int i = 0; i <= 400; i = i + 100) {
            final Line line = new Line(0, i, 5, i);
            final Text number = new Text(Integer.toString(i));
            number.setFont(new Font(6));
            number.setX(7);
            number.setY(i);
            mainPane.getChildren().addAll(number, line);
        }

        for (int i = 25; i <= 400; i = i + 25) {
            mainPane.getChildren().add(new Line(0, i, 3, i));
        }

    }

    private int recursiveDraw(
            final Pane mainPane,
            final int h,
            int x,
            final TreeItem<String> cell,
            final int rootStart) {
        // Recursively draws each cell in the tree
        // not sure what rootstart is note returns the midpoint of the sublineage just drawn
    	double lineWidth = 1.5;
        boolean done = false;
        final String cellName = cell.getValue();

        if (hiddenNodes.contains(cellName)) {
            done = true;
        }

        //separate drawing case for placeholder cells
        if (cellName.startsWith("Pl")) {
            final ObservableList<TreeItem<String>> childrenlist = cell.getChildren();
            //skip placeholder cells that has no child
            if (childrenlist.size() > 0) {
                final TreeItem<String> cLeft = childrenlist.get(0);
                final int x1 = recursiveDraw(mainPane, h, x, cLeft, rootStart);
                x = x1;
            }
            //for the case where it only has one child
            if (childrenlist.size() > 1) {
                final TreeItem<String> cRite = childrenlist.get(1);
                final int xx = maxX + xsc;
                final int x2 = recursiveDraw(mainPane, h, xx, cRite, rootStart);
                x = (x + x2) / 2;
            }

            return x;

        }

        int startTime = lineageData.getFirstOccurrenceOf(cellName);
        int endTime = lineageData.getLastOccurrenceOf(cellName);

        // fill in semi-arbitrary choronological details for early pre-4-cell stage cells
        if (cellName.equalsIgnoreCase("AB")) {
            endTime = max(
                    lineageData.getFirstOccurrenceOf("ABp"),
                    lineageData.getFirstOccurrenceOf("ABa"))
                    - 1;
            startTime = -2;
        } else if (cellName.equalsIgnoreCase("P1")) {
            endTime = max(
                    lineageData.getFirstOccurrenceOf("EMS"),
                    lineageData.getFirstOccurrenceOf("P2"))
                    - 1;
            startTime = -2;
        } else if (cellName.equalsIgnoreCase("P0")) {
            startTime = -5;
            endTime = -3;
        }

        int length = endTime - startTime;

        int yStartUse = startTime + iYmin;
        nameYStartUseMap.put(cellName, yStartUse);

        // compute color
        final Paint lcolor = paintThatAppliesToCell(cellName);

        if (cell.isLeaf() || done) {
            if (x < iXmax) {
                x = iXmax + xsc;
            }
            // terminal case line drawn
            maxX = max(x, maxX);
            final Line lcell = new Line(x, yStartUse, x, yStartUse + length);
            lcell.setStrokeWidth(lineWidth);
            if (lcolor != null) {
                lcell.setStroke(lcolor); // first for now
            }

            final Tooltip tooltip = new Tooltip(cellName);
            hackTooltipStartTiming(tooltip, ttduration);
            install(lcell, tooltip);
            lcell.setId(cellName);
            lcell.setOnMouseEntered(event -> lcell.setCursor(HAND));
            lcell.setOnMouseExited(event -> lcell.setCursor(DEFAULT));
            lcell.setOnMousePressed(clickHandler);
            if (done && !cell.isLeaf()) {
                // this is a collapsed node not a terminal cell
                final Circle circle = new Circle(2, BLACK);
                circle.setOnMouseEntered(event -> circle.setCursor(HAND));
                circle.setOnMouseExited(event -> circle.setCursor(DEFAULT));
                circle.relocate(x - 2, yStartUse + length - 2);
                tooltip.setText("Expand " + cellName);
                hackTooltipStartTiming(tooltip, ttduration);
                install(circle, tooltip);
                circle.setId(cellName);
                mainPane.getChildren().add(circle);
                circle.setOnMousePressed(clickHandler);
            }
            mainPane.getChildren().add(lcell);

            int offsetx = 2;
            int offsety = 3;
            String cellNameTextString = cellName;
            String terminalName = getFunctionalNameByLineageName(cellName);
            if (!(terminalName == null)) {
                cellNameTextString = cellNameTextString + " (" + terminalName + ")";
            }
            final Text cellnametext = new Text(x - offsetx, yStartUse + length + offsety, cellNameTextString);
            cellnametext.getTransforms().add(new Rotate(90, x - offsetx, yStartUse + length + offsety));
            cellnametext.setFont(new Font(5));
            //highlight cell name for search function result
            if (currMatchCellNames.contains(cellName)) {
                cellnametext.setFont(Font.font("Verdana", FontWeight.BOLD, 5));
                cellnametext.setFill(Color.RED);

//                resetSelectedNameLabeled(cellName);
                timeProperty.set(endTime);
            }

            mainPane.getChildren().add(cellnametext);
            nameXUseMap.put(cellName, x);
            return x;
        }

        // note left right not working here or relying on presort
        final ObservableList<TreeItem<String>> childrenlist = cell.getChildren();
        final TreeItem<String> cLeft = childrenlist.get(0);
        final TreeItem<String> cRite = childrenlist.get(1);
        final int x1 = recursiveDraw(mainPane, h, x, cLeft, rootStart);
        nameXUseMap.put(cLeft.getValue(), x1);
        final int xx = maxX + xsc;
        final int x2 = recursiveDraw(mainPane, h, xx, cRite, rootStart);
        nameXUseMap.put(cRite.getValue(), x2);

        final int leftXUse = nameXUseMap.get(cLeft.getValue());
        final int rightXUse = nameXUseMap.get(cRite.getValue());
        final int leftYUse = nameYStartUseMap.get(cLeft.getValue());
        nameYStartUseMap.get(cRite.getValue());

        // division line
        Line lcell = new Line(leftXUse, leftYUse, rightXUse, leftYUse);
        if (!(lcolor == null)) {
            lcell.setStroke(lcolor); // first for now
        }
        lcell.setStrokeWidth(lineWidth);
        // lines with child names

        lcell.setId(cellName);// set division line to parent id to aid
        // recoloring
        mainPane.getChildren().add(lcell);
        x = (x1 + x2) / 2;

        // nonerminal case line drawn
        lcell = new Line(x, yStartUse, x, leftYUse);
        if (!(lcolor == null)) {
            lcell.setStroke(lcolor); // first for now
        }
        lcell.setStrokeWidth(lineWidth);

        final Line lcellTemp = lcell;
        lcellTemp.setOnMousePressed(clickHandler);// handler for collapse
        lcellTemp.setOnMouseEntered(event -> lcellTemp.setCursor(HAND));
        lcellTemp.setOnMouseExited(event -> lcellTemp.setCursor(DEFAULT));
        lcellTemp.setId(cellName);
        final Tooltip t = new Tooltip(cellName);
        hackTooltipStartTiming(t, ttduration);
        install(lcell, t);
        mainPane.getChildren().add(lcell);

        return x;
    }

    /**
     * Bindind for the view port height to create control zoom overlays. This is needed because {@link
     * javafx.geometry.Bounds} does not support binding.
     */
    private static class ScrollPaneViewPortHeightBinding extends DoubleBinding {
        private final ScrollPane root;

        public ScrollPaneViewPortHeightBinding(ScrollPane root) {
            this.root = root;
            super.bind(root.viewportBoundsProperty());
        }

        @Override
        protected double computeValue() {
            return root.getViewportBounds().getHeight();
        }
    }

    /**
     * Bindind for the view port width to create control zoom overlays. This is needed because {@link
     * javafx.geometry.Bounds} does not support binding.
     */
    private static class ScrollPaneViewPortWidthBinding extends DoubleBinding {

        private final ScrollPane root;

        public ScrollPaneViewPortWidthBinding(final ScrollPane root) {
            this.root = root;
            bind(root.viewportBoundsProperty());
        }

        @Override
        protected double computeValue() {
            return root.getViewportBounds().getWidth();
        }
    }
}