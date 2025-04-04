/*
 * Bao Lab 2017
 */

package application_src.controllers.controllers;


import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Timer;
import java.util.Vector;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import application_src.MainApp;
import javafx.application.Platform;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.scene.transform.Affine;
import javafx.scene.transform.MatrixType;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import application_src.NamedNucleusSphere;

import application_src.application_model.data.LineageData;
import application_src.application_model.data.CElegansData.Connectome.Connectome;
import application_src.controllers.layers.SearchLayer;
import application_src.controllers.layers.StoriesLayer;
import application_src.views.popups.LineageTreePane;
import application_src.application_model.cell_case_logic.CasesLists;
import application_src.application_model.annotation.color.Rule;
import application_src.application_model.threeD.subscenegeometry.SceneElement;
import application_src.application_model.threeD.subscenegeometry.SceneElementMeshView;
import application_src.application_model.threeD.subscenegeometry.SceneElementsList;
import application_src.application_model.threeD.subscenegeometry.StructureTreeNode;
import application_src.application_model.resources.ProductionInfo;
import application_src.application_model.resources.utilities.AppFont;
import application_src.application_model.search.ModelSearch.ModelSpecificSearchOps.StructuresSearch;
import application_src.application_model.search.SearchConfiguration.SearchOption;
import application_src.application_model.search.SearchConfiguration.SearchType;
import application_src.application_model.annotation.stories.Note;
import application_src.application_model.annotation.stories.Note.Display;
import application_src.application_model.annotation.color.ColorComparator;
import application_src.application_model.annotation.color.ColorHash;
import application_src.application_model.threeD.subscenesaving.JavaPicture;
import application_src.application_model.threeD.subscenesaving.JpegImagesToMovie;
import moleculesampleapp.MoleculeSampleApp;
import moleculesampleapp.XformBox;

import static application_src.application_model.search.ModelSearch.ModelSpecificSearchOps.ModelSpecificSearchUtil.getFirstOccurenceOf;
import static application_src.application_model.search.ModelSearch.ModelSpecificSearchOps.ModelSpecificSearchUtil.getLastOccurenceOf;
import static java.lang.Math.pow;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import static java.lang.Thread.sleep;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import static javafx.application.Platform.runLater;
import static javafx.embed.swing.SwingFXUtils.fromFXImage;
import static javafx.scene.Cursor.CLOSED_HAND;
import static javafx.scene.Cursor.DEFAULT;
import static javafx.scene.Cursor.HAND;
import static javafx.scene.input.MouseButton.PRIMARY;
import static javafx.scene.input.MouseButton.SECONDARY;
import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;
import static javafx.scene.input.MouseEvent.MOUSE_DRAGGED;
import static javafx.scene.input.MouseEvent.MOUSE_ENTERED;
import static javafx.scene.input.MouseEvent.MOUSE_ENTERED_TARGET;
import static javafx.scene.input.MouseEvent.MOUSE_MOVED;
import static javafx.scene.input.MouseEvent.MOUSE_PRESSED;
import static javafx.scene.input.MouseEvent.MOUSE_RELEASED;
import static javafx.scene.input.ScrollEvent.SCROLL;
import static javafx.scene.layout.AnchorPane.setRightAnchor;
import static javafx.scene.layout.AnchorPane.setTopAnchor;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.RED;
import static javafx.scene.paint.Color.WHITE;
import static javafx.scene.paint.Color.web;
import static javafx.scene.text.FontSmoothingType.LCD;
import static javafx.scene.text.FontSmoothingType.GRAY;
import static javafx.scene.transform.Rotate.X_AXIS;
import static javafx.scene.transform.Rotate.Y_AXIS;
import static javafx.scene.transform.Rotate.Z_AXIS;

import static com.sun.javafx.scene.CameraHelper.project;
import static javax.imageio.ImageIO.write;
import static application_src.application_model.data.CElegansData.PartsList.PartsList.getFunctionalNameByLineageName;
import static application_src.application_model.data.CElegansData.PartsList.PartsList.getLineageNamesByFunctionalName;
import static application_src.application_model.search.SearchConfiguration.SearchType.LINEAGE;
import static application_src.application_model.search.SearchConfiguration.SearchType.NEIGHBOR;
import static application_src.application_model.loaders.NoteImageLoader.createImageView;
import static application_src.application_model.search.SearchConfiguration.SearchOption.CELL_BODY;
import static application_src.application_model.search.SearchConfiguration.SearchOption.CELL_NUCLEUS;
import static application_src.application_model.annotation.stories.Note.Display.CALLOUT_LOWER_LEFT;
import static application_src.application_model.annotation.stories.Note.Display.CALLOUT_LOWER_RIGHT;
import static application_src.application_model.annotation.stories.Note.Display.CALLOUT_UPPER_LEFT;
import static application_src.application_model.annotation.stories.Note.Display.CALLOUT_UPPER_RIGHT;
import static application_src.application_model.annotation.stories.Note.Display.OVERLAY;
import static application_src.application_model.annotation.stories.Note.Display.SPRITE;
import static application_src.application_model.resources.utilities.AppFont.getBillboardFont;
import static application_src.application_model.resources.utilities.AppFont.getClickedContentLabelFont;
import static application_src.application_model.resources.utilities.AppFont.getOrientationIndicatorFont;
import static application_src.application_model.resources.utilities.AppFont.getSpriteAndOverlayFont;
import static application_src.application_model.resources.utilities.AppFont.getBolderFont;
import static application_src.application_model.resources.utilities.AppFont.getFont;
import static application_src.application_model.threeD.subsceneparameters.Parameters.getBillboardScale;
import static application_src.application_model.threeD.subsceneparameters.Parameters.getCameraFarClip;
import static application_src.application_model.threeD.subsceneparameters.Parameters.getCameraInitialDistance;
import static application_src.application_model.threeD.subsceneparameters.Parameters.getCameraNearClip;
import static application_src.application_model.threeD.subsceneparameters.Parameters.getDefaultOthersOpacity;
import static application_src.application_model.threeD.subsceneparameters.Parameters.getInitialTranslateX;
import static application_src.application_model.threeD.subsceneparameters.Parameters.getInitialTranslateY;
import static application_src.application_model.threeD.subsceneparameters.Parameters.getInitialTranslateZ;
import static application_src.application_model.threeD.subsceneparameters.Parameters.getInitialZoom;
import static application_src.application_model.threeD.subsceneparameters.Parameters.getLabelSpriteYOffset;
import static application_src.application_model.threeD.subsceneparameters.Parameters.getNoteBillboardImageScale;
import static application_src.application_model.threeD.subsceneparameters.Parameters.getNoteBillboardTextWidth;
import static application_src.application_model.threeD.subsceneparameters.Parameters.getNoteSpriteTextWidth;
import static application_src.application_model.threeD.subsceneparameters.Parameters.getSelectabilityVisibilityCutoff;
import static application_src.application_model.threeD.subsceneparameters.Parameters.getSizeScale;
import static application_src.application_model.threeD.subsceneparameters.Parameters.getStoryOverlayPaneWidth;
import static application_src.application_model.threeD.subsceneparameters.Parameters.getSubsceneBackgroundColorHex;
import static application_src.application_model.threeD.subsceneparameters.Parameters.getUniformRadius;
import static application_src.application_model.threeD.subsceneparameters.Parameters.getVisibilityCutoff;
import static application_src.application_model.threeD.subsceneparameters.Parameters.getWaitTimeMilli;
import static application_src.application_model.threeD.subsceneparameters.Parameters.getModelScaleFactor;
import static application_src.application_model.threeD.subsceneparameters.Parameters.getShapesIndexPad;
 
/**
 * The controller for the 3D subscene inside the rootEntitiesGroup layout. This class contains the subscene itself, and
 * places it into the AnchorPane called modelAnchorPane inside the rootEntitiesGroup layout. It is also responsible
 * for refreshing the scene on timeProperty, search, wormguides.stories, notes, and rules change. This class contains
 * observable properties that are passed to other classes so that a subscene refresh can be trigger from that other
 * class.
 * <p>
 * An "entity" in the subscene is either a cell, cell body, or multicellular structure. These are graphically
 * represented by the Shape3Ds NamedNucleusSphere and MeshView available in JavaFX. {@link NamedNucleusSphere}s represent cells, and
 * {@link MeshView}s represent cell bodies and multicellular structures. Notes and labels are rendered as
 * {@link Text}s. This class queries the {@link LineageData} and {@link SceneElementsList} for a certain timeProperty
 * and renders the entities, notes, story, and labels present in that timeProperty point.
 * <p>
 * For the coloring of entities, an observable list of {@link Rule}s is queried to see which ones apply to a
 * particular entity, then queries the {@link ColorHash} for the {@link Material} to use for the entity.
 */
public class Window3DController {

	private static final String CS = ", ";
    private static final String ACTIVE_LABEL_COLOR_HEX = "#ffff66",
            SPRITE_COLOR_HEX = "#ffffff",
            TRANSIENT_LABEL_COLOR_HEX = "#f0f0f0";

    private static final int X_COR_INDEX = 0,
            Y_COR_INDEX = 1,
            Z_COR_INDEX = 2;

    /** Y offset of the callout line segment endpoint from the actual callout {@link Text} **/
    private static final int CALLOUT_LINE_Y_OFFSET = 10;
    /** X offset of the callout line segment endpoint from the actual callout {@link Text} **/
    private static final int CALLOUT_LINE_X_OFFSET = 0;

    // rotation stuff
    private Rotate rotateX;
    private Rotate rotateY;
    private Rotate rotateZ;
    private final Rotate oriIndRotX;
    private final Rotate oriIndRotY;
    private final Rotate oriIndRotZ;
    // transformation stuff
    private final Group rootEntitiesGroup;
    // switching timepoints stuff
    private final BooleanProperty playingMovieProperty;
    private final PlayService playService;
    private final RenderService renderService;
    /** Search results local to window 3d that only contains lineage names */
    private final List<String> localSearchResults;
    // color rules stuff
    private final ColorHash colorHash;
    private final Comparator<Color> colorComparator;
    private final Comparator<Node> opacityComparator;
    // opacity value for "other" cells (with no rule attached)
    private final DoubleProperty nucOpacityProperty;
    private final DoubleProperty cellOpacityProperty;
    private final DoubleProperty tractOpacityProperty;
    private final DoubleProperty structureOpacityProperty;
    private final DoubleProperty numPrev;
    private final Spinner<Integer> prevSpinner;
    private final double nonSelectableOpacity = 0.25;
    private final Vector<JavaPicture> javaPictures;
    private final SearchLayer searchLayer;
    private final Stage parentStage;
    private final LineageData lineageData;
    private final SubScene subscene;
    private final TextField searchField;

    // housekeeping stuff
    private final BooleanProperty rebuildSubsceneFlag;
    private final DoubleProperty rotateXAngleProperty;
    private final DoubleProperty rotateYAngleProperty;
    private final DoubleProperty rotateZAngleProperty;
    private final DoubleProperty translateXProperty;
    private final DoubleProperty translateYProperty;
    public final DoubleProperty translateZProperty;
    private final IntegerProperty timeProperty;
    private final IntegerProperty totalNucleiProperty;
    private final double[] initialRotation;

    /** Start timeProperty of the lineage without movie timeProperty offset */
    private final int startTime;
    /** End timeProperty of the lineage without movie timeProperty offset */
    private final int endTime;
    private final DoubleProperty zoomProperty;

    // Cell clicking/selection stuff
    private final IntegerProperty selectedIndex;
    private final StringProperty selectedNameProperty;
    private final StringProperty selectedNameLabeledProperty;
    private final Stage contextMenuStage;
    private final ContextMenuController contextMenuController;
    private final BooleanProperty cellClickedProperty;
    private final ObservableList<String> searchResultsList;
    private final ObservableList<Rule> rulesList;

    // Scene Elements stuff
    private final boolean defaultEmbryoFlag;
    private final SceneElementsList sceneElementsList;

    // Story elements stuff
    private final StoriesLayer storiesLayer;
    /** Map of current note graphics to their note objects */
    private final Map<Node, Note> currentGraphicsToNotesMap;
    /** Map of current notes to their scene elements */
    private final Map<Note, SceneElementMeshView> currentNotesToMeshesMap;
    /** Map of note sprites attached to cell, or cell and timeProperty */
    private final Map<Node, VBox> entitySpriteMap;
    /** Map of front-facing billboards attached to their entities */
    private final Map<Text, Node> billboardFrontEntityMap;
    /** Map of image views to their entities */
    private final Map<ImageView, Node> billboardImageEntityMap;
    /** Map of a cell entity to its label */
    private final Map<Node, Text> entityLabelMap;
    /** Map of upper left note callouts attached to a cell/structure */
    private final Map<Node, List<Text>> entityCalloutULMap;
    /** Map of upper right note callouts attached to a cell/structure */
    private final Map<Node, List<Text>> entityCalloutURMap;
    /** Map of lower left note callouts attached to a cell/structure */
    private final Map<Node, List<Text>> entityCalloutLLMap;
    /** Map of lower right note callouts attached to a cell/structure */
    private final Map<Node, List<Text>> entityCalloutLRMap;
    /* Map of all callout Texts to their Lines */
    private final Map<Text, Line> calloutLineMap;
    // orientation indicator
    private Cylinder orientationIndicator;
    private final ProductionInfo productionInfo;
    private final BooleanProperty bringUpInfoFlag;
    private final SubsceneSizeListener subsceneSizeListener;

    // rotation - AP
    private double[] keyValuesRotate;
    private double[] keyFramesRotate;

    // subscene state parameters
    private LinkedList<NamedNucleusSphere> spheres;
    private LinkedList<SceneElementMeshView> meshes;
    private LinkedList<String> cellNames;
    private LinkedList<String> meshNames;
    private boolean[] isCellSearchedFlags;
    private boolean[] isMeshSearchedFlags;
    private LinkedList<Double[]> positions;
    private LinkedList<Double> diameters;
    private List<SceneElement> sceneElementsAtCurrentTime;
    private List<SceneElementMeshView> currentSceneElementMeshViews;
//    private List<MeshView> currentSceneElementMeshes;
    private List<SceneElement> currentSceneElements;
    private List<String> previouslyShownSEMVnames;

    private PerspectiveCamera camera;
    private XformBox xform1;
    private XformBox xform2;
    private XformBox xform3;
   private double mousePosX, mousePosY, mousePosZ;
    private double mouseOldX, mouseOldY, mouseOldZ;

    // Label stuff
    private double mouseDeltaX, mouseDeltaY;
    // average position offsets of nuclei from zero
    private double offsetX, offsetY, offsetZ;
    private double angleOfRotation;
    // searched highlighting stuff
    private boolean isInSearchMode;
    // Uniform nuclei sizef
    private boolean uniformSize;
    // Cell body and cell nucleus highlighting in search mode
    private boolean cellNucleusTicked;
    private boolean cellBodyTicked;
    /** All notes that are active, or visible, in a frame */
    private List<Note> currentNotes;
    /**
     * Rectangular box that resides in the upper-right-hand corner of the subscene. The active story title and
     * description are shown here.
     */
    private VBox storyOverlayVBox;
    /** Overlay of the subscene. Note sprites are inserted into this overlay. */
    private Pane spritesPane;
    /** Labels that exist in any of the timeProperty frames */
    private List<String> allLabels;
    private List<List<String>> undoableLabels;
    /** Labels currently visible in the frame */
    private List<String> currentLabels;
    /** Label that shows up on hover */
    private Text transientLabelText;
    private Rotate indicatorRotation;// this is the timeProperty varying component of
    private BooleanProperty captureVideo;
    private Timer timer;
    private Vector<File> movieFiles;
    private int count;
    private String movieName;
    private String moviePath;
    private File frameDir;
    private String frameDirPath;

    // private Quaternion quaternion;

    /** X-scale of the subscene coordinate axis read from ProductionInfo.csv */
    private double xScale;
    /** Y-scale of the subscene coordinate axis read from ProductionInfo.csv */
    private double yScale;
    /** Z-scale of the subscene coordinate axis read from ProductionInfo.csv */
    private double zScale;
	private double mouseStartPosX;
	private double mouseStartPosY;
	private RootLayoutController rootLC;
	private Group middleTransformGroup;
	private double oldrotate = 90;
	private Point3D counterAxis;
	private Group orientationIndicatorGroup;
	private VBox transientLabelVBox;
	private Point3D cumRotShiftCoords;
	private Point3D cumShiftCoords;
	private Point3D xform1Pivot;
    private static ScheduledThreadPoolExecutor blinkService;
    private static ScheduledThreadPoolExecutor blinkServiceMeshViews;
    private static ScheduledThreadPoolExecutor blinkServiceRules;
	private ScheduledFuture schfut;
	private ScheduledFuture schfutMeshViews;
	private boolean blinkOn;
	private boolean blinkOnMeshViews;
	private Runnable blinkRunner;
    ObservableList<NamedNucleusSphere> blinkingSpheres;
    ObservableList<SceneElementMeshView> blinkingSceneElementMeshViews;
    ObservableList<String> currentBlinkNames;
    ObservableList<String> currentBlinkNamesMeshViews;
	protected boolean renderComplete;
	private Runnable blinkRunnerMeshViews;
	private ScheduledFuture schfutRules;
	private boolean blinkOnRules;
	private Runnable blinkRunnerRules;
    public ObservableList<Rule> blinkingRules;
    ObservableList<String> currentBlinkRuleNames;
    double initialTranslateZ;
	
	ObservableList<String> searchModeHitLabels;
	private double sceneOverallScale = 10;
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_MAGENTA = "\u001B[35m";
	public static final String ANSI_BRIGHTBLUE = "\u001B[94m";

    public Window3DController(
            final RootLayoutController rootLC, 
            final Stage parentStage,
            final Group rootEntitiesGroup,
            final SubScene subscene,
            final AnchorPane parentPane,
            final LineageData lineageData,
            final ProductionInfo productionInfo,
            final SceneElementsList sceneElementsList,
            final TreeItem<StructureTreeNode> structureTreeRoot,
            final StoriesLayer storiesLayer,
            final SearchLayer searchLayer,
            final BooleanProperty bringUpInfoFlag,
            final double offsetX,
            final double offsetY,
            final double offsetZ,
            final boolean defaultEmbryoFlag,
            final double xScale,
            final double yScale,
            final double zScale,
            final AnchorPane modelAnchorPane,
            final Button playButton,
            final Button backwardButton,
            final Button forwardButton,
            final Button zoomOutButton,
            final Button zoomInButton,
            final Button clearAllLabelsButton,
            final TextField searchField,
            final Slider nucOpacitySlider,
            final Slider cellOpacitySlider, 
            final Slider tractOpacitySlider, 
            final Slider structureOpacitySlider, 
            final Spinner<Integer> prevSpinner,
            final CheckBox uniformSizeCheckBox,
            final CheckBox cellNucleusCheckBox,
            final CheckBox cellBodyCheckBox,
            //final RadioButton multiRadioBtn,
            final int startTime,
            final int endTime,
            final IntegerProperty timeProperty,
            final IntegerProperty totalNucleiProperty,
            final DoubleProperty zoomProperty,
            final DoubleProperty nucOpacityProperty,
            final DoubleProperty cellOpacityProperty,
            final DoubleProperty tractOpacityProperty,
            final DoubleProperty structureOpacityProperty,
            final DoubleProperty numPrev,
            final DoubleProperty rotateXAngleProperty,
            final DoubleProperty rotateYAngleProperty,
            final DoubleProperty rotateZAngleProperty,
            final DoubleProperty translateXProperty,
            final DoubleProperty translateYProperty,
            final DoubleProperty translateZProperty,
            final StringProperty selectedNameProperty,
            final StringProperty selectedNameLabeledProperty,
            final BooleanProperty cellClickedFlag,
            final BooleanProperty playingMovieFlag,
            final BooleanProperty geneResultsUpdatedFlag,
            final BooleanProperty rebuildSubsceneFlag,
            final ObservableList<Rule> rulesList,
            final ColorHash colorHash,
            final Stage contextMenuStage,
            final ContextMenuController contextMenuController,
            final ObservableList<String> searchResultsList) {

    	this.rootLC = rootLC;
    	this.parentStage = requireNonNull(parentStage);
        this.xform1 = new XformBox();
        this.xform2 = new XformBox();
        this.xform1Pivot = new Point3D(0,0,0);
        this.offsetX = offsetX * sceneOverallScale;
        this.offsetY = offsetY * sceneOverallScale;
        this.offsetZ = offsetZ * sceneOverallScale;

        this.startTime = startTime;
        this.endTime = endTime;

        this.rootEntitiesGroup = requireNonNull(rootEntitiesGroup);
        this.lineageData = lineageData;
        this.productionInfo = requireNonNull(productionInfo);
        this.sceneElementsList = requireNonNull(sceneElementsList);
        this.storiesLayer = requireNonNull(storiesLayer);
        this.searchLayer = requireNonNull(searchLayer);

        this.defaultEmbryoFlag = defaultEmbryoFlag;

        // Set listener properties for the timeProperty variable. Updates time. If in movie capture mode,
        // a screenshot is captured per frame. Thus, movies are only captured during play mode
        this.timeProperty = requireNonNull(timeProperty);
        this.timeProperty.addListener((observable, oldValue, newValue) -> {
            final int newTime = newValue.intValue();
            final int oldTime = oldValue.intValue();
            if (startTime <= newTime && newTime <= endTime) {
                hideContextPopups();
            } else if (newTime < startTime) {
                timeProperty.set(endTime);
            } else if (newTime > endTime) {
                timeProperty.set(startTime);
            }

            if (captureVideo.get()) {
                WritableImage screenCapture = subscene.snapshot(new SnapshotParameters(), null);
                try {
                    File file = new File(frameDirPath + "movieFrame" + count++ + ".JPEG");

                    if (file != null) {
                        RenderedImage renderedImage = fromFXImage(screenCapture, null);
                        write(renderedImage, "JPEG", file);
                        movieFiles.addElement(file);
                    }
                } catch (Exception e) {
                    System.out.println("Could not write frame of movie to file.");
                }
            }

        });
        
		if (blinkService ==null){
			blinkService = new ScheduledThreadPoolExecutor(1);
		}

		if (blinkServiceMeshViews ==null){
			blinkServiceMeshViews = new ScheduledThreadPoolExecutor(1);
		}

		if (blinkServiceRules ==null){
			blinkServiceRules = new ScheduledThreadPoolExecutor(1);
		}

		blinkRunner = new Runnable() {

			public void run()
			{
				if (blinkingSpheres != null) {
					if (blinkingSpheres.size()>0) {
						if (blinkOn){
							for (int b=0; b<blinkingSpheres.size(); b++)
								if (blinkingSpheres.get(b) != null) {
									if (isInSearchMode && searchModeHitLabels.contains(blinkingSpheres.get(b).getCellName()))
										blinkingSpheres.get(b).setMaterial(colorHash.getHighlightBlinkMaterial());
									else
										blinkingSpheres.get(b).setMaterial(colorHash.getBlinkMaterial(blinkingSpheres.get(b).getColors()));
								}
							blinkOn = false;
						} else {
							for (int b=0; b<blinkingSpheres.size(); b++)
								if (blinkingSpheres.get(b) != null) {
									if (isInSearchMode && searchModeHitLabels.contains(blinkingSpheres.get(b).getCellName()))
										blinkingSpheres.get(b).setMaterial(colorHash.getHighlightMaterial());
									else
										blinkingSpheres.get(b).setMaterial(colorHash.getMaterial(blinkingSpheres.get(b).getColors()));
								}
							blinkOn =true;
						}                        			
					}
				}
			}
		};
		
		schfut = blinkService.scheduleAtFixedRate(blinkRunner, 0, 500, TimeUnit.MILLISECONDS);

		blinkRunnerMeshViews = new Runnable() {

			public void run()
			{
				if (blinkingSceneElementMeshViews.size()>0  && blinkingSceneElementMeshViews.get(0) != null) {
					if (blinkOnMeshViews){
            			blinkingSceneElementMeshViews.get(0).setMaterial(colorHash.getBlinkMaterial(blinkingSceneElementMeshViews.get(0).getColors()));
						blinkOnMeshViews = false;
					} else {
            			blinkingSceneElementMeshViews.get(0).setMaterial(colorHash.getMaterial(blinkingSceneElementMeshViews.get(0).getColors()));
						blinkOnMeshViews =true;
					}                        			
				}
			}
		};

		schfutMeshViews = blinkServiceMeshViews.scheduleAtFixedRate(blinkRunnerMeshViews, 0, 500, TimeUnit.MILLISECONDS);

		
		blinkRunnerRules = new Runnable() {

			public void run()
			{
				if (blinkingRules.size()>0  ) {
					if (blinkOnRules){
						for (int b=0; b<blinkingRules.size(); b++)
							if (blinkingRules.get(b) != null) {
								final int bFinal = b;
								Platform.runLater(new Runnable() {
								    @Override
								    public void run() {
								    	blinkingRules.get(bFinal).setVisible(blinkOnRules);	
								    }
								});
							}

						blinkOnRules = false;
					} else {
						for (int b=0; b<blinkingRules.size(); b++)
							if (blinkingRules.get(b) != null) {
								final int bFinal = b;
								Platform.runLater(new Runnable() {
								    @Override
								    public void run() {
								    	blinkingRules.get(bFinal).setVisible(blinkOnRules);	
								    }
								});
							}

						blinkOnRules = true;
					}                        			
				}
			}
		};

		schfutRules = blinkServiceRules.scheduleAtFixedRate(blinkRunnerRules, 0, 500, TimeUnit.MILLISECONDS);


        // set orientation indicator frames and rotation from production info
        keyFramesRotate = productionInfo.getKeyFramesRotate();
        keyValuesRotate = productionInfo.getKeyValuesRotate();
        initialRotation = productionInfo.getInitialRotation();

        cumRotShiftCoords = new Point3D(0,0,0);
        cumShiftCoords = new Point3D(0,0,0);
        spheres = new LinkedList<>();
        meshes = new LinkedList<>();
        cellNames = new LinkedList<>();
        meshNames = new LinkedList<>();
        positions = new LinkedList<>();
        diameters = new LinkedList<>();
        
        this.blinkingSceneElementMeshViews = FXCollections.observableArrayList();
        this.blinkingSpheres = FXCollections.observableArrayList();
        this.blinkingRules = FXCollections.observableArrayList();
        this.currentBlinkNames = FXCollections.observableArrayList();
        this.currentBlinkNamesMeshViews = FXCollections.observableArrayList();
        this.searchModeHitLabels = FXCollections.observableArrayList();
       
        isCellSearchedFlags = new boolean[1];
        isMeshSearchedFlags = new boolean[1];

        selectedIndex = new SimpleIntegerProperty(-1);
        captureVideo = new SimpleBooleanProperty();

        this.selectedNameProperty = requireNonNull(selectedNameProperty);
        this.selectedNameProperty.addListener((observable, oldValue, newValue) -> {
            int selected = getIndexByCellName(newValue);
            if (selected != -1) {
                selectedIndex.set(selected);
            }
        });

        this.selectedNameLabeledProperty = requireNonNull(selectedNameLabeledProperty);
        this.selectedNameLabeledProperty.addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                String lineageName = newValue;

                this.selectedNameProperty.set(lineageName);
                this.currentBlinkNames.addAll(rootLC.treePane.currMatchCellNames);
                this.allLabels.addAll(rootLC.treePane.currMatchCellNames);
                this.undoableLabels.add(0, new ArrayList<String>());
                this.undoableLabels.get(0).addAll(rootLC.treePane.currMatchCellNames);
                
 ////vvvvv     non-threaded steps of what is normally called by RenderService               
				/////this is needed to allow scene to build before searching out named entities from the lineage map click the triggers this listener
                
				refreshScene();
				
				//render current time point
				getSceneData();
				addEntitiesAndNotes();

				//render previous time points, AFTER current, so that their transparency transmits the current cells.
				int loop = (int)numPrev.get();
				//avoid index out of bound
				int loop_end = timeProperty.get() - loop;
				if (loop_end < 0) {
					loop_end = 0;
				}
				for(int i = timeProperty.get() - 1; i > loop_end; --i) {
					getCellSceneData(i);
					addEntitiesNoNotesWithColorRule();
				}

				xform1.setTranslateZ(translateZProperty.get());
				if (cumRotShiftCoords != null) {
					for (Node content:xform1.getChildren()) {
						double ctx = content.getTranslateX();
						double cty = content.getTranslateY();
						double ctz = content.getTranslateZ();
						Point3D newTranslateCoords = new Point3D(ctx+cumRotShiftCoords.getX(), cty+cumRotShiftCoords.getY(), ctz-cumRotShiftCoords.getZ());

						if (content.getTransforms().size() > 0) {
							content.getTransforms().set(0, new Translate(newTranslateCoords.getX(), newTranslateCoords.getY(), newTranslateCoords.getZ())
									.createConcatenation(content.getTransforms().get(0)));
						} else {
							content.getTransforms().add(new Translate(newTranslateCoords.getX(), newTranslateCoords.getY(), newTranslateCoords.getZ()));
						}

					}
				}
				xform2.setTranslateZ(this.initialTranslateZ);
				repositionNotes();

				if (blinkingSceneElementMeshViews.size()>0  && blinkingSceneElementMeshViews.get(0) != null && blinkingSceneElementMeshViews.get(0).getColors() !=null && blinkingSceneElementMeshViews.get(0).getColors().size() >0){
					for (int b=0; b<blinkingSceneElementMeshViews.size(); b++) {
						currentBlinkNamesMeshViews.add(0, blinkingSceneElementMeshViews.get(b).getCellName());
						blinkingSceneElementMeshViews.get(b).setMaterial(colorHash.getMaterial(blinkingSceneElementMeshViews.get(b).getColors()));
					}
				}
				blinkingSceneElementMeshViews.clear();
				for (int n=0; n<currentBlinkNamesMeshViews.size(); n++) {
					if (getMeshViewWithName(currentBlinkNamesMeshViews.get(n)) != null)
						blinkingSceneElementMeshViews.add(0,  (SceneElementMeshView) getMeshViewWithName(currentBlinkNamesMeshViews.get(n)));
				}
				
				if (blinkingSpheres.size()>0  && blinkingSpheres.get(0) != null && blinkingSpheres.get(0).getColors() !=null && blinkingSpheres.get(0).getColors().size() >0){
					for (int b=0; b<blinkingSpheres.size(); b++) {
						currentBlinkNames.add(0,blinkingSpheres.get(b).getCellName());
						blinkingSpheres.get(b).setMaterial(colorHash.getMaterial(blinkingSpheres.get(b).getColors()));
					}
				}
				blinkingSpheres.clear();
		        if (currentBlinkNames != null && currentBlinkNames.size() >0) {
		        	for (int n=0; n<currentBlinkNames.size(); n++) {
		        		if (getSphereWithName(currentBlinkNames.get(n)) != null)
		        			blinkingSpheres.add(0,  (NamedNucleusSphere) getSphereWithName(currentBlinkNames.get(n)));
		        	}
		        }
	////^^^^^^     non-threaded steps of what is normally called by RenderService    
					/////this is needed to allow scene to build before searching out named entities from the lineage map click the triggers this listener
				                
                if (!allLabels.contains(lineageName)) {
                    allLabels.add(lineageName);
                    this.undoableLabels.add(0, new ArrayList<String>());
                    this.undoableLabels.get(0).add(lineageName);
                }

                final Shape3D entity = getEntityWithName(lineageName);
                NamedNucleusSphere picked = getSphereWithName(lineageName);

                // go to labeled name
                int startTime1;
                int endTime1;

                startTime1 = getFirstOccurenceOf(lineageName);
                endTime1 = getLastOccurenceOf(lineageName);

                // do not change scene if entity does not exist at any timeProperty
                if (startTime1 <= 0 || endTime1 <= 0) {
                    return;
                }

                if (timeProperty.get() < startTime1 || timeProperty.get() > endTime1) {
                    System.out.println("Updating time property to entity startTime=" + startTime1 + " because current time: " + timeProperty.get() + " isn't in cell lifetime range. Endtime = " + endTime1);
                    timeProperty.set(startTime1);
                } else {
                    insertLabelFor(lineageName, entity);
                }

                insertLabelFor(lineageName, entity);
                highlightActiveCellLabel(entity);
                
           		if (picked instanceof NamedNucleusSphere) {

            		if (blinkingSpheres.size()>0  && blinkingSpheres.get(0) != null && blinkingSpheres.get(0).getColors() !=null && blinkingSpheres.get(0).getColors().size() >0){
            			blinkingSpheres.get(0).setMaterial(colorHash.getMaterial(blinkingSpheres.get(0).getColors()));
            		}
            		blinkingSpheres.clear();

            		if (blinkingSceneElementMeshViews.size()>0  && blinkingSceneElementMeshViews.get(0) != null && blinkingSceneElementMeshViews.get(0).getColors() !=null && blinkingSceneElementMeshViews.get(0).getColors().size() >0){
            			blinkingSceneElementMeshViews.get(0).setMaterial(colorHash.getMaterial(blinkingSceneElementMeshViews.get(0).getColors()));
            		}
            		blinkingSceneElementMeshViews.clear();
            		
            		blinkingSpheres.add(0,  picked);
        		}

            }
        });

        this.rulesList = requireNonNull(rulesList);

        this.cellClickedProperty = requireNonNull(cellClickedFlag);
        this.totalNucleiProperty = requireNonNull(totalNucleiProperty);

        this.subscene = requireNonNull(subscene);
        buildCamera();
        parentPane.getChildren().add(this.getSubscene());
        this.getSubscene().setFill(web(getSubsceneBackgroundColorHex()));

        isInSearchMode = false;

        subsceneSizeListener = new SubsceneSizeListener();
        parentPane.widthProperty().addListener(subsceneSizeListener);
        parentPane.heightProperty().addListener(subsceneSizeListener);

        mousePosX = 0.0;
        mousePosY = 0.0;
        mousePosZ = 0.0;
        mouseOldX = 0.0;
        mouseOldY = 0.0;
        mouseOldZ = 0.0;
        mouseDeltaX = 0.0;
        mouseDeltaY = 0.0;
        angleOfRotation = 0.0;

        playService = new PlayService();
        this.playingMovieProperty = requireNonNull(playingMovieFlag);
        this.playingMovieProperty.addListener((observable, oldValue, newValue) -> {
            hideContextPopups();
            if (newValue) {
                playService.restart();
                playButton.setGraphic(rootLC.pauseIcon);
            } else {
                playService.cancel();
                playButton.setGraphic(rootLC.playIcon);
            }
        });

        renderService = new RenderService();

        this.zoomProperty = requireNonNull(zoomProperty);
        this.zoomProperty.set(getInitialZoom());
//        this.zoomProperty.set(0.3);
        this.zoomProperty.addListener((observable, oldValue, newValue) -> {
            xform1.getTransforms().add(new Scale(zoomProperty.get(),zoomProperty.get(),zoomProperty.get()));
            repositionNotes();
        });
        xform1.getTransforms().add(new Scale(zoomProperty.get(),zoomProperty.get(),zoomProperty.get()));

        localSearchResults = new ArrayList<>();

        requireNonNull(geneResultsUpdatedFlag).addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                updateLocalSearchResults();
                geneResultsUpdatedFlag.set(false);
            }
        });

        oriIndRotX = new Rotate(90, X_AXIS);
        oriIndRotY = new Rotate(0, Y_AXIS);
        oriIndRotZ = new Rotate(0, Z_AXIS);

        rotateX = new Rotate(initialRotation[0], X_AXIS);
        rotateY = new Rotate(initialRotation[1], Y_AXIS);
        rotateZ = new Rotate(initialRotation[2], Z_AXIS);

        // initialize
        this.rotateXAngleProperty = requireNonNull(rotateXAngleProperty);
        this.rotateXAngleProperty.set(rotateX.getAngle());
        this.rotateYAngleProperty = requireNonNull(rotateYAngleProperty);
        this.rotateYAngleProperty.set(rotateY.getAngle());
        this.rotateZAngleProperty = requireNonNull(rotateZAngleProperty);
        this.rotateZAngleProperty.set(rotateZ.getAngle());

        // add listener for control from rotationcontroller
//        this.rotateXAngleProperty.addListener(getRotateXAngleListener());
//        this.rotateYAngleProperty.addListener(getRotateYAngleListener());
//        this.rotateZAngleProperty.addListener(getRotateZAngleListener());
//
        this.initialTranslateZ = getInitialTranslateZ();
        
        this.translateXProperty = requireNonNull(translateXProperty);
        this.translateXProperty.addListener(getTranslateXListener());
        this.translateXProperty.set(getInitialTranslateX());
        this.translateYProperty = requireNonNull(translateYProperty);
        this.translateYProperty.addListener(getTranslateYListener());
        this.translateYProperty.set(getInitialTranslateY());
        this.translateZProperty = requireNonNull(translateZProperty);
        this.translateZProperty.addListener(getTranslateZListener());
        this.translateZProperty.set(getInitialTranslateZ() * sceneOverallScale);
      //above line is one of two cases of sceneOverallScale being multiplied by tlZ but not tlX and tlY...

        this.colorHash = requireNonNull(colorHash);
        colorComparator = new ColorComparator();
        opacityComparator = new OpacityComparator();

        if (defaultEmbryoFlag) {
            currentSceneElementMeshViews = new ArrayList<>();
            currentSceneElements = new ArrayList<>();
        }
        previouslyShownSEMVnames = new ArrayList<String>();

        currentNotes = new ArrayList<>();
        currentGraphicsToNotesMap = new HashMap<>();
        currentNotesToMeshesMap = new HashMap<>();
        billboardImageEntityMap = new HashMap<>();
        entitySpriteMap = new HashMap<>();
        billboardFrontEntityMap = new HashMap<>();
        entityCalloutULMap = new HashMap<>();
        entityCalloutURMap = new HashMap<>();
        entityCalloutLLMap = new HashMap<>();
        entityCalloutLRMap = new HashMap<>();

        calloutLineMap = new HashMap<>();

        allLabels = new ArrayList<>();
        undoableLabels = new ArrayList<>();
        currentLabels = new ArrayList<>();
        entityLabelMap = new HashMap<>();

//        final EventHandler<MouseEvent> mouseHandler = this::handleMouseEvent;
//        subscene.setOnMouseClicked(mouseHandler);
//        subscene.setOnMouseDragged(mouseHandler);
//        subscene.setOnMouseEntered(mouseHandler);
//        subscene.setOnMousePressed(mouseHandler);
//        subscene.setOnMouseReleased(mouseHandler);
        handleMouse(subscene);

        final EventHandler<ScrollEvent> mouseScrollHandler = this::handleScrollEvent;
        subscene.setOnScroll(mouseScrollHandler);

        setNotesPane(parentPane);

        movieFiles = new Vector<>();
        javaPictures = new Vector<>();
        count = -1;

        // set up the orientation indicator in bottom right corner
        double radius = 5.0;
        double height = 15.0;
        final PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(RED);
        if (defaultEmbryoFlag) {
            orientationIndicator = new Cylinder(radius, height);
            orientationIndicator.setMaterial(material);
        }

        this.bringUpInfoFlag = requireNonNull(bringUpInfoFlag);

        this.rebuildSubsceneFlag = requireNonNull(rebuildSubsceneFlag);
        // reset rebuild subscene flag to false because it may have been set to true by another layer's initialization
        this.rebuildSubsceneFlag.set(false);
        this.rebuildSubsceneFlag.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                buildScene();
                rebuildSubsceneFlag.set(false);
            }
        });

        // set up the scaling value to convert from microns to pixel values, we set x,y = 1 and z = ratio of z to
        // original y note that xScale and yScale are not the same
        if (xScale != yScale) {
            System.err.println(
                    "xScale does not equal yScale - using ratio of Z to X for zScale value in pixels\n"
                            + "X, Y should be the same value");
        }
        this.xScale = 1;
        this.yScale = 1;
        this.zScale = zScale / xScale;

        this.searchField = requireNonNull(searchField);
        this.searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                isInSearchMode = false;
                buildScene();
            } else {
                isInSearchMode = true;
            }
        });

        requireNonNull(modelAnchorPane).setOnMouseClicked(getNoteClickHandler());

        requireNonNull(backwardButton).setOnAction(getBackwardButtonListener());
        requireNonNull(forwardButton).setOnAction(getForwardButtonListener());
        requireNonNull(zoomOutButton).setOnAction(getZoomOutButtonListener());
        requireNonNull(zoomInButton).setOnAction(getZoomInButtonListener());

        this.nucOpacityProperty = requireNonNull(nucOpacityProperty);
        requireNonNull(nucOpacitySlider).valueProperty().addListener((observable, oldValue, newValue) -> {
            final double newRounded = round(newValue.doubleValue()) / 100.0;
            final double oldRounded = round(oldValue.doubleValue()) / 100.0;
            if (newRounded != oldRounded) {
                nucOpacityProperty.set(newRounded);
                nucOpacitySlider.setBackground(new Background(new BackgroundFill((newRounded<=0.25?Color.BLACK:null),null,null)));
                nucOpacitySlider.setTooltip(new Tooltip("Unpainted nuclei are "+(newRounded>0.25?"":"UN") +"mousable"));
                nucOpacitySlider.opacityProperty().set(newRounded+0.3);
                buildScene();
            }
        });
        this.nucOpacityProperty.addListener((observable, oldValue, newValue) -> {
            final double newVal = newValue.doubleValue();
            final double oldVal = oldValue.doubleValue();
            if (newVal != oldVal && newVal >= 0 && newVal <= 1.0) {
                nucOpacitySlider.setValue(newVal * 100);
            }
        });
        if (defaultEmbryoFlag) {
            this.nucOpacityProperty.setValue(getDefaultOthersOpacity());
        } else {
            /*
             * if a non-default model has been loaded, set the opacity cutoff at the level where labels will
             * appear by default
             */
            this.nucOpacityProperty.set(0.26);

        }

        this.cellOpacityProperty = requireNonNull(cellOpacityProperty);
        requireNonNull(cellOpacitySlider).valueProperty().addListener((observable, oldValue, newValue) -> {
            final double newRounded = round(newValue.doubleValue()) / 100.0;
            final double oldRounded = round(oldValue.doubleValue()) / 100.0;
            if (newRounded != oldRounded) {
                cellOpacityProperty.set(newRounded);
                cellOpacitySlider.setBackground(new Background(new BackgroundFill((newRounded<=0.25?Color.BLACK:null),null,null)));
                cellOpacitySlider.setTooltip(new Tooltip("Unpainted cells are "+(newRounded>0.25?"":"UN") +"mousable"));
                cellOpacitySlider.opacityProperty().set(newRounded+0.3);
                buildScene();
            }
        });
        this.cellOpacityProperty.addListener((observable, oldValue, newValue) -> {
            final double newVal = newValue.doubleValue();
            final double oldVal = oldValue.doubleValue();
            if (newVal != oldVal && newVal >= 0 && newVal <= 1.0) {
                cellOpacitySlider.setValue(newVal * 100);
            }
        });
        if (defaultEmbryoFlag) {
            this.cellOpacityProperty.setValue(getDefaultOthersOpacity());
        } else {
            /*
             * if a non-default model has been loaded, set the opacity cutoff at the level where labels will
             * appear by default
             */
            this.cellOpacityProperty.set(0.26);

        }

        this.tractOpacityProperty = requireNonNull(tractOpacityProperty);
        requireNonNull(tractOpacitySlider).valueProperty().addListener((observable, oldValue, newValue) -> {
            final double newRounded = round(newValue.doubleValue()) / 100.0;
            final double oldRounded = round(oldValue.doubleValue()) / 100.0;
            if (newRounded != oldRounded) {
                tractOpacityProperty.set(newRounded);
                tractOpacitySlider.setBackground(new Background(new BackgroundFill((newRounded<=0.25?Color.BLACK:null),null,null)));
                tractOpacitySlider.setTooltip(new Tooltip("Unpainted tracts are "+(newRounded>0.25?"":"UN") +"mousable"));
                tractOpacitySlider.opacityProperty().set(newRounded+0.3);
                buildScene();
            }
        });
        this.tractOpacityProperty.addListener((observable, oldValue, newValue) -> {
            final double newVal = newValue.doubleValue();
            final double oldVal = oldValue.doubleValue();
            if (newVal != oldVal && newVal >= 0 && newVal <= 1.0) {
                tractOpacitySlider.setValue(newVal * 100);
            }
        });
        if (defaultEmbryoFlag) {
            this.tractOpacityProperty.setValue(0.15);
        } else {
            /*
             * if a non-default model has been loaded, set the opacity cutoff at the level where labels will
             * appear by default
             */
            this.tractOpacityProperty.set(0.15);

        }

        this.structureOpacityProperty = requireNonNull(structureOpacityProperty);
        requireNonNull(structureOpacitySlider).valueProperty().addListener((observable, oldValue, newValue) -> {
            final double newRounded = round(newValue.doubleValue()) / 100.0;
            final double oldRounded = round(oldValue.doubleValue()) / 100.0;
            if (newRounded != oldRounded) {
                structureOpacityProperty.set(newRounded);
                structureOpacitySlider.setBackground(new Background(new BackgroundFill((newRounded<=0.25?Color.BLACK:null),null,null)));
                structureOpacitySlider.setTooltip(new Tooltip("Unpainted structures are "+(newRounded>0.25?"":"UN") +"mousable"));
                structureOpacitySlider.opacityProperty().set(newRounded+0.3);
                buildScene();
            }
        });
        this.structureOpacityProperty.addListener((observable, oldValue, newValue) -> {
            final double newVal = newValue.doubleValue();
            final double oldVal = oldValue.doubleValue();
            if (newVal != oldVal && newVal >= 0 && newVal <= 1.0) {
                structureOpacitySlider.setValue(newVal * 100);
            }
        });
        if (defaultEmbryoFlag) {
            this.structureOpacityProperty.setValue(0.15);
        } else {
            /*
             * if a non-default model has been loaded, set the opacity cutoff at the level where labels will
             * appear by default
             */
            this.structureOpacityProperty.set(0.15);

        }

        this.numPrev = requireNonNull(numPrev);
        this.prevSpinner = requireNonNull(prevSpinner);
        requireNonNull(prevSpinner).valueProperty().addListener((observable, oldValue, newValue) -> {
        	if (newValue != null) {            
        		final int newRounded = newValue;
        		if (oldValue != null) {
        			final int oldRounded = oldValue;
        			if (newRounded != oldRounded) {
        				numPrev.set(newRounded);
        				buildScene();
        			}
        		} else {
        			numPrev.set(newRounded);
        			buildScene();

        		}
        	}
        });
        this.numPrev.addListener((observable, oldValue, newValue) -> {
            final double newVal = newValue.doubleValue();
            final double oldVal = oldValue.doubleValue();
            if (newVal != oldVal && newVal >= 1 && newVal <= timeProperty.get()) {
                prevSpinner.getValueFactory().setValue((int) newVal);
            }
        });
        if (defaultEmbryoFlag) {
            this.numPrev.setValue(1);
        } else {
            /*
             * if a non-default model has been loaded, set the opacity cutoff at the level where labels will
             * appear by default
             * THE COMMENT ABOVE MAKES NO SENSE TO ME.  OPACITY??
             */
            this.numPrev.set(1);

        }

        uniformSizeCheckBox.setSelected(false);
        uniformSize = false;
        requireNonNull(uniformSizeCheckBox).selectedProperty().addListener((observable, oldValue, newValue) -> {
            uniformSize = newValue;
            buildScene();
        });

        requireNonNull(clearAllLabelsButton).setOnAction(getClearAllLabelsButtonListener());
        requireNonNull(clearAllLabelsButton).setOnMouseClicked(new EventHandler<MouseEvent>() {  
        	  @Override  
        	  public void handle(MouseEvent event) {  
        	    if (event.getClickCount()==2) {  
        	    }  
        	  }  
        	});
        requireNonNull(cellNucleusCheckBox).selectedProperty().addListener(getCellNucleusTickListener());
        requireNonNull(cellBodyCheckBox).selectedProperty().addListener(getCellBodyTickListener());
        //requireNonNull(multiRadioBtn).selectedProperty().addListener(getMulticellModeListener());

        this.contextMenuStage = requireNonNull(contextMenuStage);
        this.contextMenuController = requireNonNull(contextMenuController);

        this.searchResultsList = requireNonNull(searchResultsList);
        this.searchResultsList.addListener((ListChangeListener)(c -> {
            updateLocalSearchResults();
        }));


        this.captureVideo = new SimpleBooleanProperty();
    }

    /**
     * Creates the orientation indicator and transforms
     * <p>
     * (for new model as of 1/5/2016)
     *
     * @return the group containing the orientation indicator texts
     */
    private Group createOrientationIndicator() {
        if (orientationIndicatorGroup == null)
        	orientationIndicatorGroup = new Group();
        if (middleTransformGroup == null) {
        	indicatorRotation = new Rotate();
        	middleTransformGroup = new Group();

        	Cylinder dvCylinder = new Cylinder(1.414,20);
        	dvCylinder.setMaterial(new PhongMaterial(Color.CYAN));
        	middleTransformGroup.getChildren().add(dvCylinder);
        	
        	Cylinder apCylinder = new Cylinder(1.414,30);
        	apCylinder.setMaterial(new PhongMaterial(Color.MAGENTA));
        	apCylinder.setRotate(90);
        	apCylinder.setRotationAxis(Z_AXIS);
        	middleTransformGroup.getChildren().add(apCylinder);
        	
        	Cylinder lrCylinder = new Cylinder(1.414,20);
        	lrCylinder.setMaterial(new PhongMaterial(Color.YELLOW));
        	lrCylinder.setRotate(90);
        	lrCylinder.setRotationAxis(X_AXIS);
        	middleTransformGroup.getChildren().add(lrCylinder);
        	
        	// set up the orientation indicator in bottom right corner
        	Text t = makeOrientationIndicatorText("A");
        	t.setTranslateX(-23);
        	t.setTranslateY(3);
        	//        t.getTransforms().add(new Rotate(90, new Point3D(1, 0, 0)));
        	middleTransformGroup.getChildren().add(t);

        	t = makeOrientationIndicatorText("P");
        	t.setTranslateX(17);
        	t.setTranslateY(3);
        	//        t.getTransforms().add(new Rotate(90, new Point3D(1, 0, 0)));
        	middleTransformGroup.getChildren().add(t);

        	t = makeOrientationIndicatorText("L");
        	t.setTranslateX(-3);
        	t.setTranslateZ(-15);
        	t.setTranslateY(3);
        	//        t.getTransforms().add(new Rotate(90, new Point3D(1, 0, 0)));
        	//        t.getTransforms().add(new Rotate(-90, new Point3D(0, 1, 0)));
        	middleTransformGroup.getChildren().add(t);

        	t = makeOrientationIndicatorText("R");
        	t.setTranslateX(-3);
        	t.setTranslateZ(15);
        	t.setTranslateY(3);
        	//        t.getTransforms().add(new Rotate(90, new Point3D(1, 0, 0)));
        	//        t.getTransforms().add(new Rotate(-90, new Point3D(0, 1, 0)));
        	middleTransformGroup.getChildren().add(t);

        	t = makeOrientationIndicatorText("V");
        	t.setTranslateY(21);
        	t.setTranslateX(-3);
        	//        t.getTransforms().add(new Rotate(90, new Point3D(0, 1, 0)));
        	middleTransformGroup.getChildren().add(t);

        	t = makeOrientationIndicatorText("D");
        	t.setTranslateY(-15);
        	t.setTranslateX(-3);
        	//        t.getTransforms().add(new Rotate(-90, new Point3D(0, 1, 0)));
        	middleTransformGroup.getChildren().add(t);

        	// xy relocates z shrinks apparent by moving away from camera? improves resolution?
        	middleTransformGroup.getTransforms().add(new Scale(1,1,1));

        	// set the location of the indicator in the bottom right corner of the screen
        	middleTransformGroup.getTransforms().add(new Translate(160,120,5));

        	// add rotation variables
        	middleTransformGroup.getTransforms().addAll(oriIndRotX, oriIndRotY, oriIndRotZ);

        	// add the directionLabel symbols to the group
        	orientationIndicatorGroup.getChildren().add(middleTransformGroup);
        	
//      	// add rotation
        	middleTransformGroup.getTransforms().add(indicatorRotation);
        }

        return orientationIndicatorGroup;
    }

    private double computeInterpolatedValue(int timevalue, double[] keyFrames, double[] keyValues) {
        if (timevalue <= keyFrames[0]) {
            return keyValues[0];
        }
        if (timevalue >= keyFrames[keyFrames.length - 1]) {
            return keyValues[keyValues.length - 1];
        }
        int i;
        for (i = 0; i < keyFrames.length; i++) {
            if (keyFrames[i] == timevalue) {
                return (keyValues[i]);
            }
            if (keyFrames[i] > timevalue) {
                break;
            }
        }
        // interpolate btw values at i and i-1
        double alpha = ((timevalue - keyFrames[i - 1]) / (keyFrames[i] - keyFrames[i - 1]));
        double value = keyValues[i] * alpha + keyValues[i - 1] * (1 - alpha);
        return value;
    }

    /**
     * Inserts a transient label into the sprites pane for the specified entity if the entity is not an 'other' entity
     * that has an opacity less than the cutoff (specified as a parameter in
     * /wormguides/util/subsceneparameters/parameters.txt)
     *
     * @param name
     *         the name that appears on the transient label
     * @param entity
     *         The entity that the label should appear on
     * @param longForm
     * 		Whether to include coloring rule hits for a given cell. 
     */
    private void insertTransientLabel(String name, final Shape3D entity, boolean longForm) {
        final double nucOpacity = nucOpacityProperty.get();
        final double cellOpacity = cellOpacityProperty.get();
        final double tractOpacity = tractOpacityProperty.get();
        final double structureOpacity = structureOpacityProperty.get();
        if (entity != null) {
            // do not create transient label for "other" entities when their visibility is under the selectability
            // cutoff
 // NEED TO WORK THROUGH THE COMPLICATED CHECKLIST FOR THESE...THEY CURRENTLY DON'T MAKE SENSE!!!!
            if (entity instanceof Sphere ) {
	            if (entity.getMaterial() == colorHash.getOtherNucleiMaterial(nucOpacity)
	            		&& nucOpacity < getSelectabilityVisibilityCutoff()) {
	            	return;
	            }
            } else if(entity instanceof Shape3D) {
            	if (entity.getMaterial() == colorHash.getOtherCellsMaterial(cellOpacity)
            			&& cellOpacity < getSelectabilityVisibilityCutoff()) {
            		return;
            	} else if (entity.getMaterial() == colorHash.getOtherTractsMaterial(tractOpacity)
            			&& tractOpacity < getSelectabilityVisibilityCutoff()) {
            		return;
            	}else if (entity.getMaterial() == colorHash.getOtherStructuresMaterial(structureOpacity)
            			&& structureOpacity < getSelectabilityVisibilityCutoff()) {
            		return;
            	}
            }
            
///////////////
//            if (!currentLabels.contains(name)) {
                final Bounds b = entity.getBoundsInParent();
                if (b != null) {
                    final String funcName = getFunctionalNameByLineageName(name);
                    String goName = name;
                    if (funcName != null) {
                        goName = funcName +" ("+name+")";
                    }
					String fullTextString = /* ""+ (entity instanceof Sphere?"n":"")+ */ goName;
                    transientLabelText = new Text(fullTextString);
                    transientLabelVBox = new VBox(transientLabelText);
                    if (longForm) {
                    	List<Rule> directNameHitRules = new ArrayList<Rule>();
                    	for (Rule rule : rulesList) {
                    		//System.out.println("checking rule: " + rule.getSearchedText());
                    		if (rule.appliesToCellNucleus(name) || rule.appliesToCellBody(name)) {
                    			if (directNameHitRules.contains(rule)) {
                    				continue;
                    			}                   				
                     			directNameHitRules.add(rule);
                    			String optionsString = "";
                    			SearchOption[] options = rule.getOptions();
                    			String[] optNames = new String[options.length];
                    			for (int o=0; o< optNames.length ;o++)
                    				optNames[o] = options[o].name();
                    			
                    			Arrays.sort(optNames);
                    			for (int so = 0; so<options.length; so++)
                    				optionsString = optionsString + optNames[so];
                    			                   	
                    			                   	
                    			String ruleString = ("\n"+"• "+ rule.getSearchedText() +" "+ optionsString).replace("ANCESTOR", "<")
																											.replace("CELL_NUCLEUS", "N")
																											.replace("CELL_BODY", "C")
																											.replace("DESCENDANT", ">")
																											.replace("LINEAGE", "")
																											.replace("Functional", "Func")
																											.replace("\"PartsList\" Description", "PartDesc")
																											.replace("Gene", "Gene")
																											.replace("Connectome", "Cnx")
																											.replace("Multicellular Structure Cells", "MCSc")
																											.replace("Structure Scene Name", "MCSn")
																											.replace("Structures Heading", "MCSh")
																											.replace("Neighbor", "Nbr");
                    			
                    			Text ruleText = new Text(ruleString);
                    			ruleText.setFill(rule.getColor().invert());
                	    		String lTextFillString = ruleText.getFill().toString();
//                				if (lTextFillString.matches(   "(0x[6-9].[6-9].[6-9]...)"
//								+ "|(0x..[6-9].[6-9]...)"
//								+ "|(0x[6-9].[6-9].....)"
//								+ "|(0x[6-9]...[6-9]...)")) {
                	    		int darknessSum= (Integer.parseInt(lTextFillString.substring(2, 4), 16))
                	    				+(Integer.parseInt(lTextFillString.substring(4, 6), 16))
                	    				+(Integer.parseInt(lTextFillString.substring(6, 8), 16));
                	    		ruleText.setFill((darknessSum < 382/* ||color.getOpacity()>0.3 */)?Color.BLACK:Color.WHITE);
//	}

//}
                    			ruleText.setWrappingWidth(-1);
                    			ruleText.setOnMouseEntered(Event::consume);
                    			ruleText.setOnMouseClicked(Event::consume);
                    			ruleText.setDisable(true);
                    			ruleText.setFont(getBolderFont());
                    			ruleText.setStrokeWidth(0.1);
                    			ruleText.setStroke(Color.BLACK);
                    			TextFlow ruleTextFlow = new TextFlow(ruleText);
                    			ruleTextFlow.setBackground(new Background(new BackgroundFill(Color.color(rule.getColor().getRed(), 
                    																			rule.getColor().getGreen(), 
                    																			rule.getColor().getBlue(),
                    																			0.8), null, null)));
                    			transientLabelVBox.getChildren().add(ruleTextFlow);

                    			if (rule.getColor().getOpacity() <= getSelectabilityVisibilityCutoff()) {
                    				entity.setDisable(true);
                    			}
                    		}
                    		if (rule.appliesToStructureWithSceneNameOrContent(name)) {
                    			if (directNameHitRules.contains(rule)) {
                    				continue;
                    			}                   				
                    			directNameHitRules.add(rule);
                    			ArrayList<String> containedNames = new ArrayList<String>();
                    			containedNames.add(name);
                    			List<String> contentNames = StructuresSearch.getCellsInMulticellularStructure(name);
                    			containedNames.addAll(contentNames); 
                    			for (String sceneName:containedNames) {
                    				boolean sceneTitleHit;
                    				Rule useRule = rule;
                    				
                    				if (getFunctionalNameByLineageName(sceneName) == null || getFunctionalNameByLineageName(sceneName).equals("")) {
                    					sceneTitleHit = true;
                            		} else {
                    					sceneTitleHit = false;
                    					for (Rule assocRule: rulesList) {
                    						if (!assocRule.isVisible()) {
                    							continue;
                    						}else {
                    							if (assocRule.appliesToCellNucleus(sceneName) || assocRule.appliesToCellBody(sceneName)) {
                    								if (directNameHitRules.contains(assocRule)) {
                    									continue;
                    								} else {
                    									useRule = assocRule;
                                            			directNameHitRules.add(useRule);
                    								}
                    							} else {
                    								continue;                                    			
                    							}
                    						} 
                    					}
                    					if (useRule == rule) {
                    						continue;
                    					}
                            		}
                    				String optionsString = "";
                    				for (int so = 0; so<useRule.getOptions().length; so++) {
                    					optionsString = optionsString + useRule.getOptions()[so].name();
                    				}
                    				String ruleString = "\n"+"• "+ useRule.getSearchedText() 
                    												+(sceneTitleHit?"":" "+ optionsString.replace("ANCESTOR", "<")
																										.replace("CELL_NUCLEUS", "N")
																										.replace("CELL_BODY", "C")
																										.replace("DESCENDANT", ">"));
                    				Text ruleText = new Text(ruleString);
                    				ruleText.setFill(useRule.getColor().invert());
                    	    		String lTextFillString = ruleText.getFill().toString();
//                    				if (lTextFillString.matches(   "(0x[6-9].[6-9].[6-9]...)"
//									+ "|(0x..[6-9].[6-9]...)"
//									+ "|(0x[6-9].[6-9].....)"
//									+ "|(0x[6-9]...[6-9]...)")) {
                    	    		int darknessSum= (Integer.parseInt(lTextFillString.substring(2, 4), 16))
                    	    				+(Integer.parseInt(lTextFillString.substring(4, 6), 16))
                    	    				+(Integer.parseInt(lTextFillString.substring(6, 8), 16));
                    	    		ruleText.setFill((darknessSum < 382/* ||color.getOpacity()>0.3 */)?Color.BLACK:Color.WHITE);
//		}

//    }
                    				ruleText.setWrappingWidth(-1);
                    				ruleText.setOnMouseEntered(Event::consume);
                    				ruleText.setOnMouseClicked(Event::consume);
                    				ruleText.setDisable(true);
                    				ruleText.setFont(getBolderFont());
                    				ruleText.setStrokeWidth(0.1);
                    				ruleText.setStroke(Color.BLACK);
                    				TextFlow ruleTextFlow = new TextFlow(ruleText);
                    				ruleTextFlow.setBackground(new Background(new BackgroundFill(Color.color(useRule.getColor().getRed(), 
                    						useRule.getColor().getGreen(), 
                    						useRule.getColor().getBlue(),
                    						0.8), null, null)));
                    				transientLabelVBox.getChildren().add(ruleTextFlow);

                    				if (sceneTitleHit && useRule.getColor().getOpacity() <= getSelectabilityVisibilityCutoff()) {
                    					entity.setDisable(true);
                    				}
                    			}

                    		}
                    	}
                    }
                    transientLabelText.setWrappingWidth(-1);
                    transientLabelText.setFill(web(TRANSIENT_LABEL_COLOR_HEX));
                    transientLabelText.setOnMouseEntered(Event::consume);
                    transientLabelText.setOnMouseClicked(Event::consume);
                    double x = entity.localToScreen(entity.getBoundsInLocal()).getMaxX() 
                    		- entity.localToScreen(entity.getBoundsInLocal()).getWidth()/3
                    		- rootLC.modelAnchorPane.localToScreen(0,0).getX();
                    double y = entity.localToScreen(entity.getBoundsInLocal()).getMaxY()
                    		- entity.localToScreen(entity.getBoundsInLocal()).getWidth()/3
                    		- rootLC.modelAnchorPane.localToScreen(0,0).getY();

                    y -= getLabelSpriteYOffset();
                    transientLabelVBox.getTransforms().add(new Translate(x+10, y+10));
//                    if (longForm) 
//                    	transientLabelTextFlow.setBackground(new Background(new BackgroundFill(Color.valueOf("0x88888888"), null, null)));

                    // disable text to take away label flickering when mouse is on top top of it
                    transientLabelText.setDisable(true);
                    transientLabelText.setFont(getSpriteAndOverlayFont());
                    transientLabelText.setStrokeWidth(0.5);
                    transientLabelText.setStroke(Color.BLACK);
                    transientLabelText.setFill(Color.WHITE);

                    spritesPane.getChildren().add(transientLabelVBox);
                    spritesPane.toFront();
                }
//            }
        }
    }

    /**
     * Removes transient label from sprites pane.
     */
    private void removeTransientLabel() {
        spritesPane.getChildren().remove(transientLabelVBox);
    }

    /**
     * Triggers zoom in and out on mouse wheel scroll
     * DeltaY indicates the direction of scroll:
     * -Y: zoom out
     * +Y: zoom in
     *
     * @param se
     *         the scroll event
     */
    public void handleScrollEvent(final ScrollEvent se) {
        final EventType<ScrollEvent> type = se.getEventType();
        if (type == SCROLL) {
//            double z = zoomProperty.get();
//            if (se.getDeltaY() < 0) {
//                // zoom out
//                if (z < 24.975) {
//                    zoomProperty.set(z + 0.025);
//                } else {
//                	z = 25;
//                }
//            } else if (se.getDeltaY() >= 0) {
//                // zoom in
//                if (z > 0.025) {
//                    z -= 0.025;
//                } else if (z < 0) {
//                    z = 0;
//                }
//                zoomProperty.set(z);
//            }
            double z = translateZProperty.get();
            if (se.getDeltaY() > 0) {
                // zoom out
            	z = (z * 1.125);
             } else if (se.getDeltaY() <= 0) {
                // zoom in
             	z = (z / 1.125);
            }
            translateZProperty.set(z);

        }
    }

    public void handleMouse(SubScene subscene) {
        System.out.printf("handleMouse%n");
        
        subscene.setOnMouseClicked(me -> {
        	if (!me.isStillSincePress())
        		return;
        	
        	if(me.getClickCount() > 2) {
        		this.playingMovieProperty.set(!this.playingMovieProperty.get());
        	} else if(me.getClickCount() == 2) {
        		if (me.isShiftDown()) {
        			PickResult pkres = me.getPickResult();
        			Node pickedNode = pkres.getIntersectedNode();
//    				System.out.println(ANSI_RED+"pickedNode = "+ pickedNode.toString() +" "+ (pickedNode instanceof NamedNucleusSphere?((NamedNucleusSphere)pickedNode).getCellName():"not sphere"));
//    				System.out.println(ANSI_RED+"pickedNodeParent = "+ pickedNode.getParent().toString() );
//    				System.out.println(ANSI_RED+"pickedNodeGrandParent = "+ pickedNode.getParent().getParent().toString() );
//        			Scene mainScene = pickedNode.getScene();
//        			
//     				System.out.println(ANSI_RESET+"xform1Pivot =          "+ xform1Pivot.toString().replace("= ", "= +").replace("= +-", "= -"));

     				
     				Bounds pickLocalBounds = pickedNode.getBoundsInLocal();
     				
/////////////   This block does the sequential stepping from local to Parent to Scene in order to get correct affect, but one-liner below does same using localToScene!!
//   				
//    				Point3D pick1stStepToParent = pickedNode.localToParent((pickLocalBounds.getMaxX()+pickLocalBounds.getMinX())/2,
//									    						(pickLocalBounds.getMaxY()+pickLocalBounds.getMinY())/2,
//									    						(pickLocalBounds.getMaxZ()+pickLocalBounds.getMinZ())/2);
//        			System.out.println(ANSI_BRIGHTBLUE + "pick1stStepToParent =            "+pick1stStepToParent.toString().replace("= ", "= +").replace("= +-", "= -"));
//
//    				xform1Pivot = pickLocalToSceneTnfmed;
//    				xform1Pivot = pickLocalToParentTnfmed;
//    				xform1Pivot = pick1stStepToParent;
//    				xform1Pivot = pickParentBoundsCtr;
//  				    				
//    				System.out.println(ANSI_GREEN+"xform1Pivot_new =      "+ xform1Pivot.toString().replace("= ", "= +").replace("= +-", "= -"));
//    				System.out.println("");
//
//    				Point3D xform1Pivot_2ndStepToScene = pickedNode.getParent().localToScene(xform1Pivot).subtract(new Point3D(0,0, getInitialTranslateZ()*10));    				
//
//    				System.out.println(ANSI_BRIGHTBLUE+"xform1Pivot_newToScene= "+ xform1Pivot_2ndStepToScene.toString().replace("= ", "= +").replace("= +-", "= -"));
//    				xform1Pivot = xform1Pivot_2ndStepToScene;

 
//!!!!!!  WHEN WRITTEN RIGHT, THIS ACTUALLY DOES THE MAGIC IT IS SUPPOSED TO!!!!!!
     				Point3D xform1Pivot_NewDoubleStepPickToScene = pickedNode.localToScene((pickLocalBounds.getMaxX()+pickLocalBounds.getMinX())/2,
															    						(pickLocalBounds.getMaxY()+pickLocalBounds.getMinY())/2,
															    						(pickLocalBounds.getMaxZ()+pickLocalBounds.getMinZ())/2)
    																					.subtract(new Point3D(translateXProperty.get(),translateYProperty.get(), translateZProperty.get()));
//above line WAS one of two cases of sceneOverallScale being multiplied by tlZ but not tlX and tlY...  
//NOW trying here to call the property set in the other such line, instead... THAT WORKS!!  
//but still some residual "losability" of the pivot sync, so now attempting .subtract of all three tlProperties here^^...seems OK.
     				
//     				System.out.println(ANSI_MAGENTA+"*xform1Pivot_DoubleStepPickToScene= "+ xform1Pivot_NewDoubleStepPickToScene.toString().replace("= ", "= +").replace("= +-", "= -"));
//    				System.out.println("");
    				xform1Pivot = xform1Pivot_NewDoubleStepPickToScene;
    				
        		}
        	} else {
                spritesPane.setCursor(DEFAULT);
                hideContextPopups();

                final Node node = me.getPickResult().getIntersectedNode();

                if (node instanceof NamedNucleusSphere) {
                    // Nucleus
                    NamedNucleusSphere picked = (NamedNucleusSphere) node;
                    int index = getPickedNamedNucleusSphereIndex(picked);
//                    String name = normalizeName(cellNames.get(index));
                    String name = normalizeName(picked.getCellName());
                    
                    cellClickedProperty.set(true);
                    selectedNameProperty.set(name);
                    selectedIndex.set(index);

                    // right click
                    if (me.getButton() == SECONDARY
                            || (me.getButton() == PRIMARY
                            && (me.isMetaDown() || me.isControlDown()))) {
                		this.playingMovieProperty.set(false);
                        boolean hasFunctionalName = false;
                        if (getFunctionalNameByLineageName(name) != null) {
                            hasFunctionalName = true;
                        }
                        showContextMenu(
                                name,
                                me.getScreenX(),
                                me.getScreenY(),
                                false,
                                false,
                                hasFunctionalName);

                    } else if (me.getButton() == PRIMARY) {
                    	// regular click
//                		schfut = blinkService.scheduleAtFixedRate(blinkRunner, 0, 500, TimeUnit.MILLISECONDS);

                    	
                    	if (currentBlinkNames.contains(name)) {
//                    		removeLabelFor(name);
//                			if (schfut != null) {
//                				schfut.cancel(true);
//                			}
//                    		if (blinkingSpheres.size()>0  && blinkingSpheres.get(0) != null && blinkingSpheres.get(0).getColors() !=null && blinkingSpheres.get(0).getColors().size() >0){
//                    			blinkingSpheres.get(0).setMaterial(colorHash.getMaterial(blinkingSpheres.get(0).getColors()));
//                    		}
//                    		blinkingSpheres.clear();
//
//                    		if (blinkingSceneElementMeshViews.size()>0  && blinkingSceneElementMeshViews.get(0) != null && blinkingSceneElementMeshViews.get(0).getColors() !=null && blinkingSceneElementMeshViews.get(0).getColors().size() >0){
//                    			blinkingSceneElementMeshViews.get(0).setMaterial(colorHash.getMaterial(blinkingSceneElementMeshViews.get(0).getColors()));
//                    		}
//                    		blinkingSceneElementMeshViews.clear();
//                			currentBlinkNames.remove(name);
//                   			if (blinkingSpheres.size()>0  && blinkingSpheres.get(0) != null && blinkingSpheres.get(0).getColors() !=null && blinkingSpheres.get(0).getColors().size() >0){
//                    			for (int b=0; b<blinkingSpheres.size(); b++)
//                    				if (blinkingSpheres.get(b) != null)
//                    					blinkingSpheres.get(b).setMaterial(colorHash.getMaterial(blinkingSpheres.get(b).getColors()));
//                    		}
//                    		blinkingSpheres.clear();
//                			if (currentBlinkNames != null && currentBlinkNames.size() >0 && getSphereWithName(currentBlinkNames.get(0)) !=null) {
//                				for (int n=0; n<currentBlinkNames.size(); n++) {
//                					if (getSphereWithName(currentBlinkNames.get(n)) != null && getSphereWithName(currentBlinkNames.get(n)) instanceof Sphere)
//                						blinkingSpheres.add(0,  (NamedNucleusSphere) getSphereWithName(currentBlinkNames.get(n)));
//                				}
//                			}
                    		currentBlinkNames.remove(picked.getCellName());
                    		allLabels.remove(picked.getCellName());
                    		currentLabels.remove(picked.getCellName());
                    		undoableLabels.remove(picked.getCellName());

                    		buildScene();

                    	} else {
                    		if (!allLabels.contains(name)) {
                    			allLabels.add(name);
                    			currentLabels.add(name);
                    			this.undoableLabels.add(0, new ArrayList<String>());
                                this.undoableLabels.get(0).add(name);
                                final Shape3D entity = getEntityWithName(name);
                    			insertLabelFor(name, entity);
                    			highlightActiveCellLabel(entity); 
                    			currentBlinkNames.add(name);
                    			if (rootLC.treePane !=null) {
                    				if (rootLC.treePane.pickedCellMarkers != null) {
                    					rootLC.treePane.mainPane.getChildren().removeAll(rootLC.treePane.pickedCellMarkers);
                    				} else {
                    					rootLC.treePane.pickedCellMarkers = new ArrayList<Ellipse>();
                    				}

                    				for (String cbn:currentBlinkNames) {
                    					if (rootLC.treePane.nameXUseMap.get(cbn) == null) {
                    						String zapIt = "";
                    						for (String hiddenRootName : rootLC.treePane.hiddenNodes) {
                    							if (cbn.contains(hiddenRootName))
                    								zapIt = hiddenRootName;
                    						}
                    						rootLC.treePane.hiddenNodes.remove(zapIt);
                    						rootLC.treePane.updateDrawing();
                    					} else {
                    						//                    					rootLC.treePane.hiddenNodes.add(name);
                    					}
                    					if (rootLC.treePane.nameXUseMap.get(cbn) != null) {
                    						rootLC.treePane.pickedCellMarkers.add(0, new Ellipse(rootLC.treePane.nameXUseMap.get(cbn),  rootLC.treePane.iYmin + timeProperty.getValue(), 5,5));
                    						rootLC.treePane.pickedCellMarkers.get(0).setFill(web("#ffffff00"));
                    						rootLC.treePane.pickedCellMarkers.get(0).setStroke(Color.MAGENTA);
                    						rootLC.treePane.pickedCellMarkers.get(0).setStrokeWidth(1);
                    					}
                    					rootLC.treePane.mainPane.getChildren().clear();
                    					for (int pcm=0; pcm<rootLC.treePane.pickedCellMarkers.size(); pcm++) {
                    						if (rootLC.treePane.pickedCellMarkers.get(pcm) != null) {
                    							rootLC.treePane.mainPane.getChildren().add(rootLC.treePane.pickedCellMarkers.get(pcm));
                    							rootLC.treePane.pickedCellMarkers.get(pcm).toBack();
                    						}
                    					}
                    					LineageTreePane.ensureVisible(rootLC.treePane, rootLC.treePane.pickedCellMarkers.get(0), rootLC.treePane.scaleTransform);
                    				}
                    			}
                    		}



                    		if (true) {
                    			if (blinkingSpheres.size()>0  && blinkingSpheres.get(0) != null && blinkingSpheres.get(0).getColors() !=null && blinkingSpheres.get(0).getColors().size() >0){
                    				for (int b=0; b<blinkingSpheres.size(); b++)
                    					if (blinkingSpheres.get(b) != null)
                    						blinkingSpheres.get(b).setMaterial(colorHash.getMaterial(blinkingSpheres.get(b).getColors()));
                    			}
                    			blinkingSpheres.clear();

                    			if (currentBlinkNames != null && currentBlinkNames.size() >0) {
                    				for (int n=0; n<currentBlinkNames.size(); n++) {
                    					if ( getSphereWithName(currentBlinkNames.get(n)) !=null)
                    						blinkingSpheres.add(0,  (NamedNucleusSphere) getSphereWithName(currentBlinkNames.get(n)));
                    				}
                    			}

                    			if (blinkingSceneElementMeshViews.size()>0  && blinkingSceneElementMeshViews.get(0) != null && blinkingSceneElementMeshViews.get(0).getColors() !=null && blinkingSceneElementMeshViews.get(0).getColors().size() >0){
                    				blinkingSceneElementMeshViews.get(0).setMaterial(colorHash.getMaterial(blinkingSceneElementMeshViews.get(0).getColors()));
                    			}
                    			blinkingSceneElementMeshViews.clear();

                    		}
                    	}
                    }

                } else if (node instanceof SceneElementMeshView) {
                    // Structure
                    boolean found = false; // this will indicate whether this meshview is a scene element
                    SceneElementMeshView curr;
//                    MeshView curr;
                    SceneElement clickedSceneElement;
                    String funcName;
                    for (int i = 0; i < currentSceneElementMeshViews.size(); i++) {
                        curr = currentSceneElementMeshViews.get(i);
                        if (curr.equals(node)) {
                            clickedSceneElement = currentSceneElements.get(i);
                            if (!clickedSceneElement.isSelectable()) {
                                selectedIndex.set(-1);
                                selectedNameProperty.set("");
                                return;
                            }

                            found = true;

                            String name = normalizeName(clickedSceneElement.getSceneName());
                            selectedNameProperty.set(name);

                            if (me.getButton() == SECONDARY
                                    || (me.getButton() == PRIMARY && (me.isMetaDown() || me.isControlDown()))) {
                                // right click
                                if (sceneElementsList.isStructureSceneName(name)) {
                                    boolean hasFunctionalName = false;
                                    if (getFunctionalNameByLineageName(name) != null) {
                                        hasFunctionalName = true;
                                    }
                                    showContextMenu(
                                            name,
                                            me.getScreenX(),
                                            me.getScreenY(),
                                            true,
                                            sceneElementsList.isMulticellStructureName(name),
                                            hasFunctionalName);
                                }

                            } else if (me.getButton() == PRIMARY) {
                            	// regular click
                            	if (allLabels.contains(name)) {
                            		removeLabelFor(name);
//                        			if (schfut != null) {
//                        				schfut.cancel(true);
//                        			}
                            		if (blinkingSpheres.size()>0  && blinkingSpheres.get(0) != null && blinkingSpheres.get(0).getColors() !=null && blinkingSpheres.get(0).getColors().size() >0){
                            			blinkingSpheres.get(0).setMaterial(colorHash.getMaterial(blinkingSpheres.get(0).getColors()));
                            		}
                            		blinkingSpheres.clear();

                            		if (blinkingSceneElementMeshViews.size()>0  && blinkingSceneElementMeshViews.get(0) != null && blinkingSceneElementMeshViews.get(0).getColors() !=null && blinkingSceneElementMeshViews.get(0).getColors().size() >0){
                            			blinkingSceneElementMeshViews.get(0).setMaterial(colorHash.getMaterial(blinkingSceneElementMeshViews.get(0).getColors()));
                            		}
                            		blinkingSceneElementMeshViews.clear();
                        			currentBlinkNamesMeshViews.clear();

                            	} else {
                            		if (!allLabels.contains(name)) {
                            			allLabels.add(name);
                            			currentLabels.add(name);
                            			this.undoableLabels.add(0, new ArrayList<String>());
                                        this.undoableLabels.get(0).add(name);
                                        final Shape3D entity = getEntityWithName(name);
                            			insertLabelFor(name, entity);
                            			highlightActiveCellLabel(entity);                               
                            			currentBlinkNamesMeshViews.add(name);
                            		}
                            		if (true) {
                                		if (blinkingSpheres.size()>0  && blinkingSpheres.get(0) != null && blinkingSpheres.get(0).getColors() !=null && blinkingSpheres.get(0).getColors().size() >0){
                                			blinkingSpheres.get(0).setMaterial(colorHash.getMaterial(blinkingSpheres.get(0).getColors()));
                                		}
                                		blinkingSpheres.clear();

                                		if (blinkingSceneElementMeshViews.size()>0  && blinkingSceneElementMeshViews.get(0) != null && blinkingSceneElementMeshViews.get(0).getColors() !=null && blinkingSceneElementMeshViews.get(0).getColors().size() >0){
                                			blinkingSceneElementMeshViews.get(0).setMaterial(colorHash.getMaterial(blinkingSceneElementMeshViews.get(0).getColors()));
                                		}
                                		blinkingSceneElementMeshViews.clear();
                                		
                                		currentBlinkNamesMeshViews.add(0,  curr.getCellName());
                                        if (currentBlinkNamesMeshViews != null && currentBlinkNamesMeshViews.size() >0 && getSphereWithName(currentBlinkNamesMeshViews.get(0)) !=null) {
                                        	for (int n=0; n<currentBlinkNamesMeshViews.size(); n++) {
                                        		if ( getMeshViewWithName(currentBlinkNamesMeshViews.get(n)) !=null)
                                        		blinkingSceneElementMeshViews.add(0,  (SceneElementMeshView) getMeshViewWithName(currentBlinkNamesMeshViews.get(n)));
                                        	}
                                        }                             		
                            		}
                            	}
                            }
                            break;
                        }
                    }

                    // if the node isn't a SceneElement
                    if (!found) {
                        // note structure
                        currentNotesToMeshesMap.keySet()
                                .stream()
                                .filter(note -> currentNotesToMeshesMap.get(note).equals(node))
                                .forEachOrdered(note -> selectedNameProperty.set(note.getTagName()));
                    }
                } else {
                    selectedIndex.set(-1);
                    selectedNameProperty.set("");
                }
            }
        });
        
        subscene.setOnMousePressed(me -> {
            mouseStartPosX = me.getSceneX();
            mouseStartPosY = me.getSceneY();
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        });

        subscene.setOnMouseReleased(me -> {
            mouseStartPosX = me.getSceneX();
            mouseStartPosY = me.getSceneY();
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        });

        subscene.setOnMouseDragged(me -> {
        	mouseOldX = mousePosX;
        	mouseOldY = mousePosY;
        	mousePosX = me.getSceneX();
        	mousePosY = me.getSceneY();
        	mouseDeltaX = (mousePosX - mouseOldX);
        	mouseDeltaY = (mousePosY - mouseOldY);

        	if (me.isPrimaryButtonDown()) {

        		if (me.isShiftDown()) {


        			//// THIS CODE SHIFTS THE CONTENTS TO A NEW PIVOT POINT if leftclick/shiftdown...
        				//actually...does now seem to do so as of fixes 02012024...

        			double shiftX = ((mouseDeltaX * MoleculeSampleApp.MOUSE_SPEED * MoleculeSampleApp.ROTATION_SPEED));
        			double shiftY = ((mouseDeltaY * MoleculeSampleApp.MOUSE_SPEED * MoleculeSampleApp.ROTATION_SPEED));
        			
        			xform1.getTransforms().set(0, new Translate(shiftX*10, shiftY*10, 0).createConcatenation(xform1.getTransforms().get(0)));
        			
        		} else {

        			double angleY = -mouseDeltaX * MoleculeSampleApp.MOUSE_SPEED * MoleculeSampleApp.ROTATION_SPEED;
        			double angleX = mouseDeltaY * MoleculeSampleApp.MOUSE_SPEED * MoleculeSampleApp.ROTATION_SPEED;
        			//Hey fixed this!!																  //vv I HAD THIS WRONG vv
        			xform1.addRotation(angleY, (int)xform1Pivot.getX(), (int)xform1Pivot.getY(), (int)xform1Pivot.getZ(), Rotate.Y_AXIS);
        			xform1.addRotation(angleX, (int)xform1Pivot.getX(), (int)xform1Pivot.getY(), (int)xform1Pivot.getZ(), Rotate.X_AXIS);
        			
        			for (Node thingy:xform1.getChildren()) {
        				if (!(thingy instanceof Text)) {
        					Text thingyLabel = entityLabelMap.get(thingy);

        					if (thingyLabel == null)
        						continue;
        					double xPivot = (thingy.getBoundsInParent().getMaxX() + thingy.getBoundsInParent().getMinX())/2;
        					double yPivot = (thingy.getBoundsInParent().getMaxY() + thingy.getBoundsInParent().getMinY())/2;
        					double zPivot = (thingy.getBoundsInParent().getMinZ() + thingy.getBoundsInParent().getMaxZ())/2;

        					Transform rT = new Rotate();
        					List<Transform> xfm1List = xform1.getTransforms();
        					List<Transform> tLList = thingyLabel.getTransforms();
        					for (int t=0;t<xfm1List.size();t++) {
        						Transform tfm = xfm1List.get(t);
        						if (tfm instanceof Rotate && t<3) {
        							//  THIS may now be unused....
        							Affine rTA = (Affine) ((Rotate)tfm).createConcatenation(new Affine());
        							try {
        								Point3D axis;
        								axis = rTA.inverseDeltaTransform(((Rotate)rT).getAxis());
        								double angle = -(((Rotate)rT).getAngle());
        								tLList.set(0, new Rotate(angle, xPivot, yPivot, zPivot, axis)
        										.createConcatenation(tLList.get(t)));
        							} catch (NonInvertibleTransformException e) {
        								// TODO Auto-generated catch block
        								e.printStackTrace();
        							}
        						} else if (tfm instanceof Affine && t<3) {
        							// THIS FINALLY WORKS!!!!!
        							rT = ((Affine)tfm);
        							try {
        								Point3D axis = ((Affine)rT).inverseDeltaTransform(X_AXIS);
        								tLList.set(0, new Rotate(-angleX, xPivot, yPivot, zPivot, axis)
        										.createConcatenation(tLList.get(0)));
        								axis = ((Affine)rT).inverseDeltaTransform(Y_AXIS);
        								tLList.set(0, new Rotate(-angleY, xPivot, yPivot, zPivot, axis)
        										.createConcatenation(tLList.get(0)));

        							} catch (Exception e) {
        								e.printStackTrace();
        							}
        						}

        					}

        				}
        			}

        			//  THIS FOR ORIENTATION INDICATOR                
        			//              Group middleTransformGroup = ((Group)((Group)xform2.getChildren().get(0)).getChildren().get(0));
        			Bounds mtgBounds = middleTransformGroup.getBoundsInParent();
        			Point3D oriIndCenter = new Point3D((mtgBounds.getMaxX()+mtgBounds.getMinX())/2,
        					(mtgBounds.getMaxY()+mtgBounds.getMinY())/2,
        					(mtgBounds.getMaxZ()+mtgBounds.getMinZ())/2);
        			xform2.addRotation(angleY, (int)oriIndCenter.getX(), (int)oriIndCenter.getY(), (int)oriIndCenter.getZ(), Rotate.Y_AXIS);
        			xform2.addRotation(angleX, (int)oriIndCenter.getX(), (int)oriIndCenter.getY(), (int)oriIndCenter.getZ(), Rotate.X_AXIS);
        			for (Node directionLabel:middleTransformGroup.getChildren()) {
        				if ((directionLabel instanceof Text)) {

        					double xPivot = (directionLabel.getBoundsInLocal().getMaxX() + directionLabel.getBoundsInLocal().getMinX())/2;
        					double yPivot = (directionLabel.getBoundsInLocal().getMaxY() + directionLabel.getBoundsInLocal().getMinY())/2;
        					double zPivot = (directionLabel.getBoundsInLocal().getMinZ() + directionLabel.getBoundsInLocal().getMaxZ())/2;

        					Transform rT = new Rotate();
        					List<Transform> xfm2List = xform2.getTransforms();
        					List<Transform> dirTfmList = directionLabel.getTransforms();
        					for (int t=0;t<xfm2List.size();t++) {
        						Transform tfm = xfm2List.get(t);
        						if (tfm instanceof Rotate && t<3) {
        							//  THIS may now be unused....
        							Affine rTA = (Affine) ((Rotate)tfm).createConcatenation(new Affine());
        							try {
        								Point3D axis;
        								axis = rTA.inverseDeltaTransform(((Rotate)rT).getAxis());
        								double angle = -(((Rotate)rT).getAngle());
        								dirTfmList.set(0, new Rotate(angle, xPivot, yPivot, zPivot, axis)
        										.createConcatenation(dirTfmList.get(t)));
        							} catch (NonInvertibleTransformException e) {
        								// TODO Auto-generated catch block
        								e.printStackTrace();
        							}
        						} else if (tfm instanceof Affine && t<3) {
        							// // THIS works but needs synching to the time-rotated state of orientationIndicator !!!!!
        							//                				rT = ((Affine)tfm);
        							//								try {
        							//									Point3D axis = ((Affine)rT).inverseDeltaTransform(Rotate.X_AXIS);
        							//									if (dirTfmList.size() <1) {
        							//										dirTfmList.add(new Rotate(-angleX, xPivot, yPivot, zPivot, axis));
        							//									}else {
        							//										dirTfmList.set(0, new Rotate(-angleX, xPivot, yPivot, zPivot, axis)
        							//											.createConcatenation(dirTfmList.get(0)));
        							//									}
        							//									axis = ((Affine)rT).inverseDeltaTransform(Rotate.Y_AXIS);
        							//									if (dirTfmList.size() <1) {
        							//										dirTfmList.add(new Rotate(-angleY, xPivot, yPivot, zPivot, axis));
        							//									}else {
        							//										dirTfmList.set(0, new Rotate(-angleY, xPivot, yPivot, zPivot, axis)
        							//												.createConcatenation(dirTfmList.get(0)));
        							//									}
        							//								} catch (Exception e) {
        							//									e.printStackTrace();
        							//								}
        							//								t = xfm2List.size();
        						}

        					}

        				}
        			}

        		}
        	} else { //rightclick case
        		//// THIS CODE SHIFTS xform1 within the scene, but does not affect PIVOT POINT if rightclick alone...
        		
        		//complex symptom (prob easy fix?):  if an object has been shift2xLclicked to set as pivot point, then this right-drag preserves that pivot nicely
        		//But after right-drag, any new shift2xLclick appears to have pivot misplaced (perhaps by an additional length of previous right drag event.

//        		xform1.setTranslateX(xform1.getTranslateX()+(mouseDeltaX * 10 * MoleculeSampleApp.MOUSE_SPEED * MoleculeSampleApp.ROTATION_SPEED));
//        		xform1.setTranslateY(xform1.getTranslateY()+(mouseDeltaY * 10 * MoleculeSampleApp.MOUSE_SPEED * MoleculeSampleApp.ROTATION_SPEED));
        		

// next 3 lines copied from shiftleftdrag case above to compare/use here...
        		
    			double shiftX = ((mouseDeltaX * MoleculeSampleApp.MOUSE_SPEED * MoleculeSampleApp.ROTATION_SPEED));
    			double shiftY = ((mouseDeltaY * MoleculeSampleApp.MOUSE_SPEED * MoleculeSampleApp.ROTATION_SPEED));
    			xform1.getTransforms().set(0, new Translate(shiftX*10, shiftY*10, 0).createConcatenation(xform1.getTransforms().get(0)));
// new line to countershift...I hope.  Yes this works!, but not sure why I needed subtract method and negative arguments...
    			xform1Pivot = this.xform1Pivot.subtract(new Point3D(-shiftX*10, -shiftY*10, 0));

        	}
        });

        subscene.setOnMouseEntered(me -> {
            mouseStartPosX = me.getSceneX();
            mouseStartPosY = me.getSceneY();
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
       });
    }

    private double[] vectorBWPoints(double px, double py, double pz, double qx, double qy, double qz) {
        double[] vector = new double[3];
        double vx, vy, vz;
        vx = qx - px;
        vy = qy - py;
        vz = qz - pz;
        vector[0] = vx;
        vector[1] = vy;
        vector[2] = vz;
        return vector;
    }

    private double computeZCoord(double xCoord, double yCoord, double angleOfRotation) {
        // http://stackoverflow.com/questions/14954317/know-coordinate-of-z-from-xy-value-and-angle
        // --> law of cosines: https://en.wikipedia.org/wiki/Law_of_cosines
        // http://answers.ros.org/question/42803/convert-coordinates-2d-to-3d-point-theoretical-question/
        return sqrt(pow(xCoord, 2) + pow(yCoord, 2) - (2 * xCoord * yCoord * Math.cos(angleOfRotation)));
    }

    private double rotationAngleFromMouseMovement() {
        // http://math.stackexchange.com/questions/59/calculating-an-angle-from-2-points-in-space
        double rotationAngleRadians = Math.acos(
                ((mouseOldX * getMousePosX()) + (mouseOldY * mousePosY) + (mouseOldZ * mousePosZ))
                        / sqrt((pow(mouseOldX, 2) + pow(mouseOldY, 2) + pow(mouseOldZ, 2))
                        * (pow(getMousePosX(), 2) + pow(mousePosY, 2) + pow(mousePosZ, 2))));
        return rotationAngleRadians;
    }

    /**
     * Source: http://mathworld.wolfram.com/CrossProduct.html
     *
     * @return length-3 vector containing the cross product of valid inputs, null otherwise
     */
    private double[] crossProduct(double[] u, double[] v) {
        if (u.length != 3 || v.length != 3) {
            return null;
        }
        double[] cross = new double[3];
        cross[0] = (u[1] * v[2]) - (u[2] * v[1]);
        cross[1] = (u[2] * v[0]) - (u[0] * v[2]);
        cross[2] = (u[0] * v[1]) - (u[1] * v[0]);
        return cross;
    }

    private String normalizeName(String name) {
        if (name.contains("(")
                && name.contains(")")
                && (name.indexOf("(") < name.indexOf(")"))) {
            name = name.substring(0, name.indexOf("("));
        }
        return name.trim();
    }

    private void handleMousePressed(MouseEvent event) {
        mousePosX = event.getSceneX();
        mousePosY = event.getSceneY();
    }

    /**
     * Displays the context menu for an entity in the UI
     *
     * @param name
     *         the lineage name of a cell, the scene name of a multicellular structure or tract, or the functional
     *         name of a cell body
     * @param sceneX
     *         the x coordinate of the mouse in the scene
     * @param sceneY
     *         the y coordinate of the mouse in the scene
     * @param isStructure
     *         true if the entity is a structure, false otherwise
     * @param isMulticellularStructureOrTract
     *         true if the entity is a multicellular structure or a tract model, false otherwise
     * @param hasFunctionalName
     *         true if the entity has a functional name, false otherwise
     */
    private void showContextMenu(
            String name,
            final double sceneX,
            final double sceneY,
            final boolean isStructure,
            final boolean isMulticellularStructureOrTract,
            final boolean hasFunctionalName) {

        contextMenuController.setName(name);
        contextMenuController.setColorButtonText(isStructure);

        // disable terminal cell options for multicellular structures and tracts
        if (isStructure) {
            contextMenuController.disableMoreInfoFunction(isMulticellularStructureOrTract);
            contextMenuController.disableWiredToFunction(isMulticellularStructureOrTract);
            contextMenuController.disableGeneExpressionFunction(isMulticellularStructureOrTract);
            contextMenuController.disableColorNeighborsFunction(isMulticellularStructureOrTract);
            contextMenuController.setIsStructure(true);
        }

        if (hasFunctionalName) {
            contextMenuController.disableWiredToFunction(false);
        } else {
            contextMenuController.disableWiredToFunction(true);
        }

        contextMenuStage.setX(sceneX);
        contextMenuStage.setY(sceneY);

        contextMenuStage.show();
        ((Stage) contextMenuStage.getScene().getWindow()).toFront();
    }

    /**
     * Repositions sprites, labels, callouts, and front-facing billboards
     */
    private void repositionNotes() {
        repositionSpritesAndLabels();
        repositionCallouts();
        repositionFrontFacingBillboardsAndImages();
    }

    /**
     * Repositions labels and note sprites on the overlaid sprites pane
     */
    private void repositionSpritesAndLabels() {
        for (Node entity : entityLabelMap.keySet()) {
            alignTextWithEntity(entityLabelMap.get(entity), entity, null);
        }
        for (Node entity : entitySpriteMap.keySet()) {
            alignTextWithEntity(entitySpriteMap.get(entity), entity, SPRITE);
        }
    }

    /**
     * Repositions callouts on the overlaid sprites pane
     */
    private void repositionCallouts() {
        for (Node entity : entityCalloutULMap.keySet()) {
            for (Node calloutGraphic : entityCalloutULMap.get(entity)) {
                alignTextWithEntity(calloutGraphic, entity, CALLOUT_UPPER_LEFT);
            }
        }
        for (Node entity : entityCalloutLLMap.keySet()) {
            for (Node calloutGraphic : entityCalloutLLMap.get(entity)) {
                alignTextWithEntity(calloutGraphic, entity, CALLOUT_LOWER_LEFT);
            }
        }
        for (Node entity : entityCalloutURMap.keySet()) {
            for (Node calloutGraphic : entityCalloutURMap.get(entity)) {
                alignTextWithEntity(calloutGraphic, entity, CALLOUT_UPPER_RIGHT);
            }
        }
        for (Node entity : entityCalloutLRMap.keySet()) {
            for (Node calloutGraphic : entityCalloutLRMap.get(entity)) {
                alignTextWithEntity(calloutGraphic, entity, CALLOUT_LOWER_RIGHT);
            }
        }
    }

    /**
     * Aligns a note graphic to its entity. The graphic is either a {@link Text} or a {@link VBox}.
     *
     * @param noteOrLabelGraphic
     *         the graphical representation of a note/notes (could be a {@link Text} or a {@link VBox})
     * @param entity
     *         the entity that the note graphic should attach to
     * @param noteDisplay
     *         the display type of the note, null if the graphic is a label
     */
    private void alignTextWithEntity(
            final Node noteOrLabelGraphic,
            final Node entity,
            final Display noteDisplay) {
        if (entity != null) {
            final Bounds b = entity.getBoundsInParent();
            if (b != null) {
                double x = b.getMaxX();
                double y = b.getMaxY();
                double z = b.getMinZ();
        		double xPivot = (entity.getBoundsInParent().getMaxX() + entity.getBoundsInParent().getMinX())/2;
        		double yPivot = (entity.getBoundsInParent().getMaxY() + entity.getBoundsInParent().getMinY())/2;
        		double zPivot = (entity.getBoundsInParent().getMinZ() + entity.getBoundsInParent().getMaxZ())/2;

                double height = b.getHeight();
                double width = b.getWidth();
                double depth = b.getDepth();

                // if graphic is a label
                if (noteDisplay == null) {
                    y -= getLabelSpriteYOffset();
                    ObservableList<Transform> olTfms = noteOrLabelGraphic.getTransforms();
                    olTfms.clear();
					olTfms.add(new Translate(xPivot, yPivot, zPivot));	
                    try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

//         NAILED IT!!!

                    ObservableList<Transform> xfm1List = xform1.getTransforms();
            		for (int t=0;t<xfm1List.size();t++) {
            			Transform xfm = xfm1List.get(t);
            			if (xfm instanceof Rotate && t<3) {
             			} else if (xfm instanceof Affine && t<3) {

							try {
								Transform aff = xfm;
								olTfms.add(new Affine(aff.getMxx(), aff.getMxy(), aff.getMxz(),0,
														aff.getMyx(),aff.getMyy(),aff.getMyz(),0,
														aff.getMzx(),aff.getMzy(),aff.getMzz(),0).createInverse());
								olTfms.add(new Translate(width*.4, height*.4, -depth*.4));	
							} catch (Exception e) {
								e.printStackTrace();
							}
            			}

            		}

                } else {
                    // if graphic is a note
                    final double calloutOffset = 10.0;
                    double calloutX;
                    double calloutY;
                    switch (noteDisplay) {
                        case SPRITE:
                            noteOrLabelGraphic.getTransforms().clear();
                            noteOrLabelGraphic.getTransforms().add(new Translate(x, y, z));
                            break;
                        case CALLOUT_UPPER_LEFT:
                            calloutY = y - (height + calloutOffset);
                            calloutX = x - (width + calloutOffset + (getNoteSpriteTextWidth()));
                            addCalloutSubsceneTranslation(
                                    noteOrLabelGraphic.getTransforms(),
                                    new Translate(calloutX, calloutY));
                            if (entity instanceof NamedNucleusSphere) {
                                realignCalloutLineToNamedNucleusSphere(noteOrLabelGraphic, b, x, y, CALLOUT_UPPER_LEFT);
                            } else if (entity instanceof SceneElementMeshView) {
                                realignCalloutLineToSceneElementMesh(
                                        noteOrLabelGraphic,
                                        (SceneElementMeshView) entity,
                                        CALLOUT_UPPER_LEFT);
                            }
                            break;
                        case CALLOUT_LOWER_LEFT:
                            calloutY = y + (height + calloutOffset);
                            calloutX = x - (width + calloutOffset + (getNoteSpriteTextWidth()));
                            addCalloutSubsceneTranslation(
                                    noteOrLabelGraphic.getTransforms(),
                                    new Translate(calloutX, calloutY));
                            if (entity instanceof NamedNucleusSphere) {
                                realignCalloutLineToNamedNucleusSphere(noteOrLabelGraphic, b, x, y, CALLOUT_LOWER_LEFT);
                            } else if (entity instanceof SceneElementMeshView) {
                                realignCalloutLineToSceneElementMesh(
                                        noteOrLabelGraphic,
                                        (SceneElementMeshView) entity,
                                        CALLOUT_LOWER_LEFT);
                            }
                            break;
                        case CALLOUT_UPPER_RIGHT:
                            calloutY = y - (height + calloutOffset);
                            calloutX = x + (width + calloutOffset);
                            addCalloutSubsceneTranslation(
                                    noteOrLabelGraphic.getTransforms(),
                                    new Translate(calloutX, calloutY));
                            if (entity instanceof NamedNucleusSphere) {
                                realignCalloutLineToNamedNucleusSphere(noteOrLabelGraphic, b, x, y, CALLOUT_UPPER_RIGHT);
                            } else if (entity instanceof SceneElementMeshView) {
                                realignCalloutLineToSceneElementMesh(
                                        noteOrLabelGraphic,
                                        (SceneElementMeshView) entity,
                                        CALLOUT_UPPER_RIGHT);
                            }
                            break;
                        case CALLOUT_LOWER_RIGHT:
                            calloutY = y + (height + calloutOffset);
                            calloutX = x + (width + calloutOffset);
                            addCalloutSubsceneTranslation(
                                    noteOrLabelGraphic.getTransforms(),
                                    new Translate(calloutX, calloutY));
                            if (entity instanceof NamedNucleusSphere) {
                                realignCalloutLineToNamedNucleusSphere(noteOrLabelGraphic, b, x, y, CALLOUT_LOWER_RIGHT);
                            } else if (entity instanceof SceneElementMeshView) {
                                realignCalloutLineToSceneElementMesh(
                                        noteOrLabelGraphic,
                                        (SceneElementMeshView) entity,
                                        CALLOUT_LOWER_RIGHT);
                            }
                            break;
                    }
                }
    
            try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        } else {//entity == null
        	try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

    /**
     * Realigns the line segment for a scene element mesh entity's callout note by setting one end point a marker
     * point on the mesh closest to the callout and the other at the callout
     *
     * @param calloutGraphic
     *         the callout, not null
     * @param meshView
     *         the scene element mesh view, not null
     * @param display
     *         the display type specifying the type of callout, not null
     */
    private void realignCalloutLineToSceneElementMesh(
            final Node calloutGraphic,
            final SceneElementMeshView meshView,
            final Display display) {
        if (calloutGraphic != null
                && calloutGraphic instanceof Text
                && meshView != null
                && display != null) {
            final Bounds calloutBounds = calloutGraphic.getBoundsInParent();
            final Line line = calloutLineMap.get(calloutGraphic);
            if (calloutBounds != null) {
                // create invisible spherical markers (similar to the markers for notes with a location attachment)
                final List<Sphere> sphereMarkers = new ArrayList<>();
                // transform marker points as the rest of the subscene entities
                meshView.getMarkerCoordinates().forEach(marker -> {
                    final Sphere markerSphere = createLocationMarker(marker[0], marker[1], marker[2]);
                    sphereMarkers.add(markerSphere);
                    rootEntitiesGroup.getChildren().add(markerSphere);
                });
                // create projected 2d points from the marker sphere centers
                final List<Point2D> markerPoints2D = new ArrayList<>();
                sphereMarkers.forEach(marker -> {
                    final Bounds b = marker.getBoundsInParent();
                    if (b != null) {
                        markerPoints2D.add(project(
                                camera,
                                new Point3D(
                                        (b.getMinX() + b.getMaxX()) / 2,
                                        (b.getMinY() + b.getMaxY()) / 2,
                                        (b.getMinZ() + b.getMaxZ()) / 2)));
                    }
                });
                switch (display) {
                    case CALLOUT_UPPER_LEFT:
                        // find point with minimum x value and minimum y value
                        Point2D upperLeftPoint = null;
                        for (Point2D marker : markerPoints2D) {
                            if (upperLeftPoint == null) {
                                upperLeftPoint = marker;
                            } else if (marker.getX() < upperLeftPoint.getX()
                                    && marker.getY() < upperLeftPoint.getY()) {
                                upperLeftPoint = marker;
                            }
                        }
                        line.setStartX(upperLeftPoint.getX());
                        line.setStartY(upperLeftPoint.getY());
                        line.setEndX(calloutBounds.getMaxX() + CALLOUT_LINE_X_OFFSET);
                        line.setEndY(calloutBounds.getMinY() + CALLOUT_LINE_Y_OFFSET);
                        break;
                    case CALLOUT_LOWER_LEFT:
                        // find point with minimum x value and maximum y value
                        Point2D lowerLeftPoint = null;
                        for (Point2D marker : markerPoints2D) {
                            if (lowerLeftPoint == null) {
                                lowerLeftPoint = marker;
                            } else if (marker.getX() < lowerLeftPoint.getX()
                                    && marker.getY() > lowerLeftPoint.getY()) {
                                lowerLeftPoint = marker;
                            }
                        }
                        line.setStartX(lowerLeftPoint.getX());
                        line.setStartY(lowerLeftPoint.getY());
                        line.setEndX(calloutBounds.getMaxX() + CALLOUT_LINE_X_OFFSET);
                        line.setEndY(calloutBounds.getMinY() + CALLOUT_LINE_Y_OFFSET);
                        break;
                    case CALLOUT_UPPER_RIGHT:
                        // find point with maximum x value and minimum y value
                        Point2D upperRightPoint = null;
                        for (Point2D marker : markerPoints2D) {
                            if (upperRightPoint == null) {
                                upperRightPoint = marker;
                            } else if (marker.getX() > upperRightPoint.getX()
                                    && marker.getY() < upperRightPoint.getY()) {
                                upperRightPoint = marker;
                            }
                        }
                        line.setStartX(upperRightPoint.getX());
                        line.setStartY(upperRightPoint.getY());
                        line.setEndX(calloutBounds.getMinX() - CALLOUT_LINE_X_OFFSET);
                        line.setEndY(calloutBounds.getMinY() + CALLOUT_LINE_Y_OFFSET);
                        break;
                    case CALLOUT_LOWER_RIGHT:
                        // find point with maximum x value and maximum y value
                        Point2D lowerRightPoint = null;
                        for (Point2D marker : markerPoints2D) {
                            if (lowerRightPoint == null) {
                                lowerRightPoint = marker;
                            } else if (marker.getX() > lowerRightPoint.getX()
                                    && marker.getY() > lowerRightPoint.getY()) {
                                lowerRightPoint = marker;
                            }
                        }
                        line.setStartX(lowerRightPoint.getX());
                        line.setStartY(lowerRightPoint.getY());
                        line.setEndX(calloutBounds.getMinX() - CALLOUT_LINE_X_OFFSET);
                        line.setEndY(calloutBounds.getMinY() + CALLOUT_LINE_Y_OFFSET);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * Realigns the line segment for a spherical entity's callout note by setting one endpoint at the sphere's center
     * and the other at the callout
     *
     * @param calloutGraphic
     *         the callout, not null
     * @param entityBounds
     *         the bounds for the entity that the callout is attached to, not null
     * @param display
     *         the display type specifying the type of callout, not null
     */
    private void realignCalloutLineToNamedNucleusSphere(
            final Node calloutGraphic,
            final Bounds entityBounds,
            final double entityCenterX,
            final double entityCenterY,
            final Display display) {
        if (calloutGraphic != null
                && calloutGraphic instanceof Text
                && entityBounds != null
                && display != null) {
            final Bounds calloutBounds = calloutGraphic.getBoundsInParent();
            final Line line = calloutLineMap.get(calloutGraphic);
            if (calloutBounds != null) {
                switch (display) {
                    case CALLOUT_UPPER_LEFT:
                        line.setStartX(entityCenterX);
                        line.setStartY(entityCenterY);
                        line.setEndX(calloutBounds.getMaxX() + CALLOUT_LINE_X_OFFSET);
                        line.setEndY(calloutBounds.getMinY() + CALLOUT_LINE_Y_OFFSET);
                        break;
                    case CALLOUT_LOWER_LEFT:
                        line.setStartX(entityCenterX);
                        line.setStartY(entityCenterY);
                        line.setEndX(calloutBounds.getMaxX() + CALLOUT_LINE_X_OFFSET);
                        line.setEndY(calloutBounds.getMinY() + CALLOUT_LINE_Y_OFFSET);
                        break;
                    case CALLOUT_UPPER_RIGHT:
                        line.setStartX(entityCenterX);
                        line.setStartY(entityCenterY);
                        line.setEndX(calloutBounds.getMinX() - CALLOUT_LINE_X_OFFSET);
                        line.setEndY(calloutBounds.getMinY() + CALLOUT_LINE_Y_OFFSET);
                        break;
                    case CALLOUT_LOWER_RIGHT:
                        line.setStartX(entityCenterX);
                        line.setStartY(entityCenterY);
                        line.setEndX(calloutBounds.getMinX() - CALLOUT_LINE_X_OFFSET);
                        line.setEndY(calloutBounds.getMinY() + CALLOUT_LINE_Y_OFFSET);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * Adds a translation to the list of transforms for a callout. All of the callout's previous translations are
     * cleared except for the one that defines the horizontal/vertical offsets from the note.
     *
     * @param transforms
     *         the list of transforms for a callout
     * @param translation
     *         the translation to add
     */
    private void addCalloutSubsceneTranslation(final List<Transform> transforms, final Translate translation) {
        for (int i = 0; i < transforms.size(); i++) {
            if (i > 0) {
                transforms.remove(i);
            }
        }
        transforms.add(translation);
    }

    /**
     * Repositions front-facing billboards and image billboards to the right-hand side of the entities they are attached
     * to. The billboard resides on y- and z-coordinates that are the averages of the maximum and minimum y and z
     * values of the entity's bounds in the subscene.
     */
    private void repositionFrontFacingBillboardsAndImages() {
        // billboards with text
        for (Text billboard : billboardFrontEntityMap.keySet()) {
            final Node entity = billboardFrontEntityMap.get(billboard);
            if (entity != null) {
                billboard.getTransforms().clear();
                final Bounds b = entity.getBoundsInParent();
                if (b != null) {
                    billboard.getTransforms().clear();
                    double x = b.getMaxX();
                    double y = (b.getMinY() + b.getMaxY()) / 2;
                    double z = (b.getMinZ() + b.getMaxZ()) / 2;
                    billboard.getTransforms().add(new Translate(x, y, z));
                }
            }
        }
        // image billboards
        for (ImageView image : billboardImageEntityMap.keySet()) {
            final Node entity = billboardImageEntityMap.get(image);
            if (entity != null) {
                image.getTransforms().clear();
                final Bounds b = entity.getBoundsInParent();
                if (b != null) {
                    image.getTransforms().clear();
                    double x = b.getMaxX();
                    double y = (b.getMinY() + b.getMaxY()) / 2;
                    double z = (b.getMinZ() + b.getMaxZ()) / 2;
                    image.getTransforms().add(new Translate(x, y, z));
                    // scale all note billboard images down instead of inserting smaller images to preserve
                    // the image quality
                    image.getTransforms().add(new Scale(
                            getNoteBillboardImageScale(),
                            getNoteBillboardImageScale()));
                }
            }
        }
    }

    private int getIndexByCellName(final String name) {
        for (int i = 0; i < cellNames.size(); i++) {
            if (cellNames.get(i).equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private int getPickedNamedNucleusSphereIndex(final NamedNucleusSphere picked) {
        for (int i = 0; i < cellNames.size(); i++) {
            if (cellNames.get(i).equals(picked.getCellName())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Calls the service to retrieve subscene data at current timeProperty point then render entities, notes, and
     * labels
     */
    public void buildScene() {
        // Spool thread for actual rendering to subscene
    	renderService.restart();
        	
    }

    private void getSceneData() {
        final int requestedTime = timeProperty.get();
        cellNames = new LinkedList<>(asList(lineageData.getNames(requestedTime)));
        positions = new LinkedList<>();
        diameters = new LinkedList<>();
        for (double diameter : lineageData.getDiameters(requestedTime)) {
            diameters.add(diameter);
        }
        
 //FIXed THIS...
        for (double[] position : lineageData.getPositions(requestedTime)) {
        	double displayRadius = uniformSize?getUniformRadius():diameters.get(positions.size());
            positions.add(new Double[]{
                    position[0] - displayRadius*1,
                    position[1] - displayRadius*0,
                    position[2] - displayRadius*0
            });
        }

        totalNucleiProperty.set(cellNames.size());

        spheres = new LinkedList<>();
        if (defaultEmbryoFlag) {
            meshes = new LinkedList<>();
        }

        if (defaultEmbryoFlag) {
            // start scene element list, find scene elements present at current time, build meshes
            // empty meshes and scene element references from last rendering
            // same for story elements
            if (sceneElementsList != null) {
                meshNames = new LinkedList<>(asList(sceneElementsList.getSceneElementNamesAtTime(requestedTime)));
            }
            
        	for (SceneElementMeshView semv : currentSceneElementMeshViews) {
        		previouslyShownSEMVnames.add(semv.getCellName());        		
        	}

        	if (!currentSceneElementMeshViews.isEmpty()) {
        		currentSceneElementMeshViews.clear();
        		currentSceneElements.clear();
        	}


            sceneElementsAtCurrentTime = sceneElementsList.getSceneElementsAtTime(requestedTime);
            for (SceneElement se : sceneElementsAtCurrentTime) {
            	boolean seAlreadyMTLapplied = false;
            	for (String semvstr : previouslyShownSEMVnames) {
            		String sestr = se.getSceneName();
            		if (sestr.equals(semvstr)){
            			seAlreadyMTLapplied = true;
            		}
            	}
                final SceneElementMeshView meshView = se.buildGeometry(requestedTime - getShapesIndexPad());
                if (meshView != null) {
                	meshView.setCellName(se.getSceneName());
                    meshView.getTransforms().addAll(rotateX, rotateY, rotateZ);
                    ArrayList<String> foundNames = new ArrayList<String>();
                    foundNames.add(meshView.getCellName());
                    boolean alreadyRuled = false;
                    for (Rule rule:rootLC.getAnnotationManager().getRulesList()) {
                    	String ruleSearchText = rule.getSearchedText();
                    	if (rule.getSearchType() == SearchType.STRUCTURE_BY_SCENE_NAME 
                    			&& ruleSearchText.equals("\'"+meshView.getCellName()+"\' "+SearchType.STRUCTURE_BY_SCENE_NAME)) {
                    		alreadyRuled = true;
                    	}
                    }
                    
                    if (!seAlreadyMTLapplied && !alreadyRuled && meshView.getColors().size() >0)
                    	rootLC.getAnnotationManager().addColorRule(SearchType.STRUCTURE_BY_SCENE_NAME, meshView.getCellName(), meshView.getColors().get(0), foundNames,null);
                    // TRANSFORMS FOR LIBRARY LOADER
                    //mesh.getTransforms().add(new Rotate(180., new Point3D(1, 0, 0)));
//                    mesh.getTransforms().add(new Translate(
//                            -offsetX * xScale,
//                            offsetY * yScale,
//                            offsetZ * zScale));

                    // TRANSFORMS FOR MANUAL LOADER
                    meshView.getTransforms().add(new Translate(
                            -offsetX * xScale,
                            -offsetY * yScale,
                            -offsetZ * zScale));

                    // add rendered mesh to meshes list
                    currentSceneElementMeshViews.add(meshView);
                    // add scene element to rendered scene element reference for on-click responsiveness
                    currentSceneElements.add(se);
                }
            }
        }

        // Label stuff
        entityLabelMap.clear();
        currentLabels.clear();

        for (String label : allLabels) {
            if (defaultEmbryoFlag) {
                for (SceneElement currentSceneElement : currentSceneElements) {
                    if (!currentLabels.contains(label)
                            && label.equalsIgnoreCase(normalizeName(currentSceneElement.getSceneName()))) {
                        currentLabels.add(label);
                        break;
                    }
                }
            }

            for (String cell : cellNames) {
                if (!currentLabels.contains(label) && cell.equalsIgnoreCase(label)) {
                    currentLabels.add(label);
                    break;
                }
            }
        }
        // End label stuff

        // Story stuff
        // Notes are indexed starting from 1 (1 + offset is shown to the user)
        if (storiesLayer != null) {
            currentNotes.clear();
            currentGraphicsToNotesMap.clear();
            currentNotesToMeshesMap.clear();

            entitySpriteMap.clear();
            entityCalloutULMap.clear();
            entityCalloutLLMap.clear();
            entityCalloutURMap.clear();
            entityCalloutLRMap.clear();

            billboardFrontEntityMap.clear();
            billboardImageEntityMap.clear();

            currentNotes = storiesLayer.getNotesAtTime(requestedTime);

            for (Note note : currentNotes) {
                // Revert to overlay display if we have invalid
                // display/attachment
                // type combination
                if (note.hasLocationError() || note.hasEntityNameError()) {
                    note.setDisplay(OVERLAY);
                }

                if (defaultEmbryoFlag) {
                    // make mesh views for scene elements from note resources
                    if (note.hasSceneElements()) {
                        for (SceneElement se : note.getSceneElements()) {
                            final SceneElementMeshView mesh = se.buildGeometry(requestedTime - getShapesIndexPad());
                            if (mesh != null) {
                                mesh.setMaterial(colorHash.getNoteSceneElementMaterial());
                                mesh.getTransforms().addAll(rotateX, rotateY, rotateZ);
                                mesh.getTransforms().add(new Translate(
                                        -offsetX * xScale,
                                        -offsetY * yScale,
                                        -offsetZ * zScale));
                                currentNotesToMeshesMap.put(note, mesh);
                            }
                        }
                    }
                }
            }
        }
        // End story stuff

        // SearchLayer stuff
        if (localSearchResults.isEmpty()) {
            isCellSearchedFlags = new boolean[cellNames.size()];
            isMeshSearchedFlags = new boolean[meshNames.size()];
        } else {
            consultSearchResultsList();
        }
        // End search stuff
    }

    //Only get the cell scene data to provide faster rendering speed for the previous time point feature
    private void getCellSceneData(int time) {
        final int requestedTime = time;
        cellNames = new LinkedList<>(asList(lineageData.getNames(requestedTime)));
        positions = new LinkedList<>();
        for (double[] position : lineageData.getPositions(requestedTime)) {
            positions.add(new Double[]{
                    position[0],
                    position[1],
                    position[2]
            });
        }
        diameters = new LinkedList<>();
        for (double diameter : lineageData.getDiameters(requestedTime)) {
            diameters.add(diameter);
        }

        totalNucleiProperty.set(cellNames.size());

        //spheres = new LinkedList<>();
        if (defaultEmbryoFlag) {
            meshes = new LinkedList<>();
        }
    }

    // TODO -> this should tap the annotation manager which has just been populated with these results
    private void updateLocalSearchResults() {
        if (searchResultsList == null) {
            return;
        }
        localSearchResults.clear();
        for (String name : searchResultsList) {
            if (name.contains(" (")) {
                localSearchResults.add(name.substring(0, name.indexOf(" (")).trim());
            } else {
                localSearchResults.add(name);
            }
        }
        rebuildSubsceneFlag.set(true);
    }

    private void refreshScene() {
        // clear note billboards, cell spheres and meshes
        rootEntitiesGroup.getChildren().clear();
        rootEntitiesGroup.getChildren().add(xform1);
        rootEntitiesGroup.getChildren().add(xform2);
    	xform1.getChildren().clear();
//    	xform2.getChildren().clear();
        // clear note sprites and overlays
        storyOverlayVBox.getChildren().clear();

        final Iterator<Node> iter = spritesPane.getChildren().iterator();
        while (iter.hasNext()) {
            Node node = iter.next();
            if (node instanceof Text) {
                iter.remove();
            } else if (node instanceof VBox && node != storyOverlayVBox) {
                iter.remove();
            } else if (node instanceof Line) {
                iter.remove();
            }
        }

        if (defaultEmbryoFlag) {
        	if (xform2.getChildren().contains(orientationIndicatorGroup))
        		xform2.getChildren().remove(orientationIndicatorGroup);
        	xform2.getChildren().add(createOrientationIndicator());
        	if (!xform2.getChildren().contains(orientationIndicatorGroup))
            	xform2.getChildren().add(createOrientationIndicator());
        	
        	double newrotate = computeInterpolatedValue(timeProperty.get(), keyFramesRotate, keyValuesRotate);
        	double counterRotate = 0;
        	counterRotate = oldrotate - newrotate;
        	oldrotate = newrotate;
        	indicatorRotation.setAngle(-newrotate);
        	indicatorRotation.setAxis(X_AXIS);        	
        	

        }
    }

    private void addEntitiesAndNotes() {
        final List<Shape3D> entities = new ArrayList<>();
        final List<Node> noteGraphics = new ArrayList<>();

        // add cell and cell body geometries
        addEntities(entities);
        entities.sort(opacityComparator);

        if (blinkingSceneElementMeshViews.size()>0  && blinkingSceneElementMeshViews.get(0) != null && blinkingSceneElementMeshViews.get(0).getColors() !=null && blinkingSceneElementMeshViews.get(0).getColors().size() >0){
        	for (int b=0; b<blinkingSceneElementMeshViews.size(); b++) {
        	blinkingSceneElementMeshViews.get(b).setMaterial(colorHash.getMaterial(blinkingSceneElementMeshViews.get(b).getColors()));
        	}
        }
        blinkingSceneElementMeshViews.clear();
        if (currentBlinkNamesMeshViews != null && currentBlinkNamesMeshViews.size() >0 && getSphereWithName(currentBlinkNamesMeshViews.get(0)) !=null) {
        	for (int n=0; n<currentBlinkNamesMeshViews.size(); n++) {
        		blinkingSceneElementMeshViews.add(0,  (SceneElementMeshView) getMeshViewWithName(currentBlinkNamesMeshViews.get(n)));
        	}
        	// set to load blinker first so all others are transparent to it.
        	for (int b=0; b<blinkingSceneElementMeshViews.size(); b++) {
        		entities.remove(blinkingSceneElementMeshViews.get(b));
        		entities.add(0,blinkingSceneElementMeshViews.get(b));
        	}
        }

        if (blinkingSpheres.size()>0  && blinkingSpheres.get(0) != null && blinkingSpheres.get(0).getColors() !=null && blinkingSpheres.get(0).getColors().size() >0){
        	for (int b=0; b<blinkingSpheres.size(); b++) {
        		if (blinkingSpheres.get(b) != null)
        			blinkingSpheres.get(b).setMaterial(colorHash.getMaterial(blinkingSpheres.get(b).getColors()));
        	}
        }
        blinkingSpheres.clear();
        if (currentBlinkNames != null && currentBlinkNames.size() >0) {
        	for (int n=0; n<currentBlinkNames.size(); n++) {
        		if (getSphereWithName(currentBlinkNames.get(n)) != null)
        			blinkingSpheres.add(0,  (NamedNucleusSphere) getSphereWithName(currentBlinkNames.get(n)));
        	}
        	// set to load blinker first so all others are transparent to it.
        	for (int b=0; b<blinkingSpheres.size(); b++) {
        		entities.remove(blinkingSpheres.get(b));
        		entities.add(0,blinkingSpheres.get(b));
        	}
        }

        xform1.getChildren().addAll(entities);

        // add notes
        insertOverlayTitles();

        if (!currentNotes.isEmpty()) {
//            addNoteGeometries(noteGraphics);
            for (Note note:currentNotes) {
            	currentLabels.add(note.getTagName()+"___"+note.getCellName());
            }
        }

        // add labels
        Shape3D activeEntity = null;
        for (String name : currentLabels) {
        	String targetEntityName = name.matches(".*___.*")?name.replaceAll(".*___(.*)", "$1"):name;
        	String labelFromName = name.split("___")[0];
            insertLabelFor(labelFromName, getEntityWithName(targetEntityName));

            if (name.equalsIgnoreCase(selectedNameProperty.get())) {
                activeEntity = getEntityWithName(name);
            }
        }
        if (activeEntity != null) {
            highlightActiveCellLabel(activeEntity);
        }

        if (!noteGraphics.isEmpty()) {
            // insert note graphics to the beginning of the group so they can be rendered last (otherwise, the notes
            // will not be completely visible behind semi-opaque entities)
//            xform1.getChildren().addAll(0, noteGraphics);
        }
        xform1.setScaleX(rootEntitiesGroup.getScaleX() * getModelScaleFactor());
        xform1.setScaleY(rootEntitiesGroup.getScaleY() * getModelScaleFactor());
        xform1.setScaleZ(rootEntitiesGroup.getScaleZ() * getModelScaleFactor());

        repositionNotes();
    }

    //For Adding previous time points graphics of cells that exist in the ruleslist
    //For previous time points feature
    private void addEntitiesNoNotesWithColorRule() {
        List<Node> entities = new ArrayList();
        this.addColoredGeometries(entities);
        entities.sort(this.opacityComparator);
        xform1.getChildren().addAll(entities);
    }

    /**
     * Inserts appropriate 3d geometries into the list of entities that is later added to the subscene
     *
     * @param entities
     *         list of subscene entities
     */
    private void addEntities(final List<Shape3D> entities) {
        // add spheres
        addNucleusGeometries(entities);
        // add scene element meshes (from notes and from scene elements list)
        addSceneElementGeometries(entities);
    }

    /**
     * Inserts spherical nuclei into the list of entities that is later added to the subscene. "Other" nuclei with
     * visibility under the visibility cutoff are not rendered.
     *
     * @param entities
     *         list of subscene entities
     */
    private void addNucleusGeometries(final List<Shape3D> entities) {
        final Material otherNucsMaterial = colorHash.getOtherNucleiMaterial(nucOpacityProperty.get());
        final ListIterator<String> iter = cellNames.listIterator();
        int index = -1;
        if (isInSearchMode) {
        	if (!undoableLabels.contains(searchModeHitLabels))
        		undoableLabels.add(searchModeHitLabels);
        	currentBlinkNames.removeAll(searchModeHitLabels);
        	allLabels.removeAll(searchModeHitLabels);
        	
        	searchModeHitLabels.clear();
        } else {
        	currentBlinkNames.removeAll(searchModeHitLabels);
        	allLabels.removeAll(searchModeHitLabels);
        }
        while (iter.hasNext()) {
            final String cellName = iter.next();
            index++;

            // size the sphere
            double radius;
            if (!uniformSize) {
                radius = getSizeScale() * diameters.get(index) / 2;
            } else {
                radius = getSizeScale() * getUniformRadius();
            }
            radius = radius *sceneOverallScale;
            final NamedNucleusSphere sphere = new NamedNucleusSphere(cellName, radius, null);

            // create the color material
            Material material;
            // if in search, do highlighting
            if (isInSearchMode) {
                if (isCellSearchedFlags[index]) {
//                    material = colorHash.getHighlightMaterial();
                	searchModeHitLabels.add(cellName);
                	currentBlinkNames.add(cellName);
                	allLabels.add(cellName);
                	
                } else {
//                    material = colorHash.getTranslucentMaterial();
//                    sphere.setDisable(true);
                }
            } 
            
            if (true) {
                // if not in search (flashlight mode), consult active list of rules
                final List<Color> colors = new ArrayList<>();
                for (Rule rule : rulesList) {
                    //System.out.println("checking rule: " + rule.getSearchedText());
                    if (rule.appliesToCellNucleus(cellName)) {
                        //System.out.println("rule applies to: " + cellName);
                        colors.add(web(rule.getColor().toString()));
                        // check if opacity of rule is below cutoff, then it's not selectable
                        if (rule.getColor().getOpacity() <= getSelectabilityVisibilityCutoff()) {
                            sphere.setDisable(true);
                        }
                    }
                }

                if (colors.isEmpty()) {
                    // do not render this "other" cell if visibility is under the cutoff
                    // remove this cell from scene data at current time point
                    double opacity = nucOpacityProperty.get();
                    if (opacity <= getVisibilityCutoff()) {
                        iter.remove();
                        positions.remove(index);
                        diameters.remove(index);
                        currentLabels.remove(cellName);
                        index--;
                        continue;
                    } else {
                    	colors.add(web("#ffffff"));
                    	sphere.setColors(colors);
                    	colorHash.getMaterial(colors);
                        material = colorHash.getOtherNucleiMaterial(opacity);
                        if (opacity <= getSelectabilityVisibilityCutoff()) {
                            sphere.setDisable(true);
                        }
                    }
                } else {
                    colors.sort(colorComparator);
                    sphere.setColors(colors);
                    material = colorHash.getMaterial(colors);
               }
            }
            if (isCellSearchedFlags[index]) {
            	material = colorHash.getHighlightMaterial();
            }
            sphere.setMaterial(material);

            // transform and add sphere to list
            sphere.getTransforms().addAll(rotateX, rotateY, rotateZ);
            final Double[] position = positions.get(index);
            sphere.getTransforms().add(new Translate(
                    position[X_COR_INDEX] * xScale*sceneOverallScale,
                    position[Y_COR_INDEX] * yScale*sceneOverallScale,
                    position[Z_COR_INDEX] * zScale*sceneOverallScale));
            sphere.getTransforms().add(new Rotate(90, sphere.getBoundsInLocal().getWidth()/2 
            											, sphere.getBoundsInLocal().getHeight()/2 
            											, sphere.getBoundsInLocal().getDepth()/2 
            											, Z_AXIS));
            spheres.add(sphere);

            if (!sphere.isDisable()) {
                sphere.setOnMouseEntered((MouseEvent event) -> {
                    spritesPane.setCursor(HAND);
                    // make label appear
//                    if (!currentLabels.contains(cellName.toLowerCase())) {
                        insertTransientLabel(cellName, getEntityWithName(cellName), event.isShiftDown());
//                    }
                });
                sphere.setOnMouseExited(event -> {
                    spritesPane.setCursor(DEFAULT);
                    // make label disappear
                    removeTransientLabel();
                });
            }

            entities.add(sphere);
        }
    }

    /**
     * Inserts meshes into the list of entities that is later added to the subscene. "Other" scene elements with
     * visibility under the visibility cutoff are not rendered.
     *
     * @param entities
     *         list of subscene entities
     */
    private void addSceneElementGeometries(final List<Shape3D> entities) {
        final Material otherElementsMaterial = colorHash.getOtherStructuresMaterial(structureOpacityProperty.get());
        if (defaultEmbryoFlag) {
            // add scene elements from notes
            entities.addAll(currentNotesToMeshesMap.keySet()
                    .stream()
                    .map(currentNotesToMeshesMap::get)
                    .collect(toList()));

            // consult rules/search results
            final ListIterator<SceneElement> iter = currentSceneElements.listIterator();
            SceneElement sceneElement;
//            SceneElementMeshView meshView;
            MeshView meshView;
            int index = -1;
            while (iter.hasNext()) {
                index++;
                sceneElement = iter.next();
                meshView = currentSceneElementMeshViews.get(index);
                String sceneElementName = sceneElement.getSceneName();
                
                if (isInSearchMode) {
                    if (cellBodyTicked && isMeshSearchedFlags[index]) {
//                        meshView.setMaterial(colorHash.getHighlightMaterial());
                    	currentBlinkNamesMeshViews.add(sceneElementName);
                    	allLabels.add(sceneElementName);

                    } else {
//                        meshView.setMaterial(colorHash.getTranslucentMaterial());
//                        meshView.setDisable(true);
                    }
                } 
                if (true) {
                    // in regular viewing mode
                    final List<String> structureCells = sceneElement.getAllCells();
                    final List<Color> colors = new ArrayList<>();

                    if (structureCells.isEmpty()) {
                        // check if any rules apply to this no-cell structure
                        for (Rule rule : rulesList) {
                            if (rule.appliesToStructureWithSceneName(sceneElement.getSceneName())) {
                                colors.add(rule.getColor());
                                if (rule.getColor().getOpacity() <= getSelectabilityVisibilityCutoff()) {
                                    meshView.setDisable(true);
                                }
                            }
                        }
                    } else {
//                    	if (((PhongMaterial)meshView.getMaterial()).getDiffuseColor() != Color.WHITE) {
//                    		colors.add(((PhongMaterial)meshView.getMaterial()).getDiffuseColor());
//                    	} 
//
                    	for (Rule rule : rulesList) {
                    		if (rule.appliesToStructureWithSceneName(sceneElement.getSceneName())) {
                    			colors.add(rule.getColor());

                    			if (rule.getColor().getOpacity() <= getSelectabilityVisibilityCutoff()) {
                    				meshView.setDisable(true);
                    			}
                    		} else {
                    			for (int g = 0; g < structureCells.size(); g++) {
                    				if (rule.appliesToCellBody(structureCells.get(g))) {
                    					colors.add(rule.getColor());
                    					if (rule.getColor().getOpacity() <= getSelectabilityVisibilityCutoff()) {
                    						meshView.setDisable(true);
                    					}
                    				}
                    			}
                    		}
                    	}

                    }

                    // if no rules applied
                    if (colors.isEmpty()) {
                        // do not render this "other" scene element if visibility is under the cutoff
                        // remove scene element and its mesh from scene data at current time point
                        final double cellOpacity = cellOpacityProperty.get();
                        final double tractOpacity = tractOpacityProperty.get();
                        final double structureOpacity = structureOpacityProperty.get();
                        double tmpOpacity = cellOpacity;
                        if (sceneElement.getSceneName().contains("Embryo")
                        						|| sceneElement.getSceneName().contains("Pharynx")
                        						|| sceneElement.getSceneName().contains("Hypoderm")) {
                        	tmpOpacity = structureOpacity;
                        } else if (sceneElement.getSceneName().contains("tract")) {
                        	tmpOpacity = tractOpacity;
                        }
                        final double opacity = tmpOpacity;
                        if (opacity <= getVisibilityCutoff()) {
                            iter.remove();
                            currentSceneElementMeshViews.remove(index--);
                            continue;
                        } else {
                        	colors.add(web("#ffffff"));
                        	((SceneElementMeshView) meshView).setColors(colors);
                        	colorHash.getMaterial(colors);
                           meshView.setMaterial(colorHash.getOtherStructuresMaterial(opacity));
                            if (opacity <= getSelectabilityVisibilityCutoff()) {
                                meshView.setDisable(true);
                            }
                        }
 
                    } else {
                        colors.sort(colorComparator);
                        ((SceneElementMeshView) meshView).setColors(colors);
                        meshView.setMaterial(colorHash.getMaterial(colors));
                    }
                }

                if (sceneElement.isSelectable() && !meshView.isDisable()) {
                    final String sceneName = sceneElement.getSceneName();
                    meshView.setOnMouseEntered(event -> {
                        spritesPane.setCursor(HAND);
                        // make label appear
                        final String name = normalizeName(sceneName);
//                        if (!currentLabels.contains(name.toLowerCase())) {
                            insertTransientLabel(name, getEntityWithName(name), event.isShiftDown());
//                        }
                    });
                    meshView.setOnMouseExited(event -> {
                        spritesPane.setCursor(DEFAULT);
                        // make label disappear
                        removeTransientLabel();
                    });
                } else {
                    meshView.setDisable(true);
                }
                meshView.getTransforms().add(new Scale(sceneOverallScale,sceneOverallScale,sceneOverallScale));
                entities.add(meshView);
            }
        }
    }

    /**
     * only add cells that are on the ruleslist without any interactive functions for faster rendering speed.
     * does not add any extra graphic to the entities when in search mode
     *
     * @param entities
     *         list of subscene entities
     */
    private void addColoredGeometries(final List<Node> entities) {
        final Material othersMaterial = colorHash.getOtherNucleiMaterial(nucOpacityProperty.get());
        final ListIterator<String> iter = cellNames.listIterator();
        int index = -1;
        boolean needRender = false;
        while (iter.hasNext()) {
            final String cellName = iter.next();
            index++;
            needRender = false;

            // create the color material
            Material material;
            // if in search, skip rendering previous time point
            if (isInSearchMode) {
                break;
            } else {
                // if not in search (flashlight mode), consult active list of rules
                // check if a cell is in the rulelist
                final List<Color> colors = new ArrayList<>();
                for (Rule rule : rulesList) {
                    if (rule.appliesToCellNucleus(cellName) && rule.getColor().getOpacity() > getSelectabilityVisibilityCutoff()) {
                        colors.add(web(rule.getColor().toString()));
                        needRender = true;
                    }
                }
                if (colors.size()==0) {
                	colors.add(web("ffffff"));
                    needRender = true;
                }

                if (needRender) { //render the cell if conditions are met
                    // size the sphere
                    double radius;
                    if (!uniformSize) {
                        radius = getSizeScale() * diameters.get(index) / 2;
                    } else {
                        radius = getSizeScale() * getUniformRadius();
                    }
                    radius = radius *sceneOverallScale;
                    final NamedNucleusSphere sphere = new NamedNucleusSphere(cellName, radius, null);

                    colors.sort(colorComparator);
                    for (int c=0;c<colors.size();c++) {
                    	colors.set(c, colors.get(c).darker().deriveColor(0, 1, 1, .3));
                    	try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    }
                    material = colorHash.getMaterial(colors);
                    sphere.setColors(colors);
                    sphere.setMaterial(material);

                    // transform and add sphere to list
                    sphere.getTransforms().addAll(rotateX, rotateY, rotateZ);
                    final Double[] position = positions.get(index);
                    sphere.getTransforms().add(new Translate(
                            position[X_COR_INDEX] * xScale*sceneOverallScale,
                            position[Y_COR_INDEX] * yScale*sceneOverallScale,
                            position[Z_COR_INDEX] * zScale*sceneOverallScale));
                    sphere.getTransforms().add(new Rotate(90, sphere.getBoundsInLocal().getWidth()/2 
							, sphere.getBoundsInLocal().getHeight()/2 
							, sphere.getBoundsInLocal().getDepth()/2 
							, Z_AXIS));
                    spheres.add(sphere);

                    if (!sphere.isDisable()) {
                        sphere.setOnMouseEntered((MouseEvent event) -> {
                            spritesPane.setCursor(HAND);
                            // make label appear
//                            if (!currentLabels.contains(cellName.toLowerCase())) {
                                insertTransientLabel(cellName, getEntityWithName(cellName), event.isShiftDown());
//                            }
                        });
                        sphere.setOnMouseExited(event -> {
                            spritesPane.setCursor(DEFAULT);
                            // make label disappear
                            removeTransientLabel();
                        });
                    }

                    entities.add(sphere);
                    
               } else { // remove this cell from scene data at current time point
                    iter.remove();
                    positions.remove(index);
                    diameters.remove(index);
                    index--;
                    continue;
                }
            }
        }
    }

    /**
     * Removes the label from the entity with the specified name
     *
     * @param name
     *         the name of the entity to be unlabeled
     */
    private void removeLabelFor(final String name) {
        allLabels.remove(name);
        currentLabels.remove(name);

        Node entity = getEntityWithName(name);

        if (entity != null) {
            removeLabelFrom(entity);
        }
    }

    /**
     * Removes the label from the specified entity
     *
     * @param entity
     *         the entity to be unlabeled
     */
    private void removeLabelFrom(final Node entity) {
        if (entity != null) {
            spritesPane.getChildren().remove(entityLabelMap.get(entity));
            xform1.getChildren().remove(entityLabelMap.get(entity));
            entityLabelMap.remove(entity);
        }
    }

    /**
     * Inserts a name label for the specified 3D entity
     *
     * @param name
     *         name to show on the label
     * @param entity
     *         3D shape to label
     */
    private void insertLabelFor(final String name, final Node entity) {
    	//***THIS METHOD SEEMS TO BREAK (MAYBE) WHEN PREVIOUS TIMEPOINTS ARE TRACED...
    	//...issue may now be solved
    	Node altEntity = null;    	
    	
		// block below is a surprisingly hacky approach to parking unspecifically-anchored notes outside the embryo volume.  Works for now.
         if (entity == null) {
//        	return;
        	altEntity = new NamedNucleusSphere(name, 1, null); // a dummy node to target the positioning of the note.
        	Point3D labelPoint = null;
        	for (SceneElement se : currentSceneElements) {
        		if (se.getSceneName().trim().equals("Embryo Outline")) {
        			Bounds embBounds = se.buildGeometry((startTime+endTime)/2).getBoundsInLocal();
        			labelPoint = new Point3D  (embBounds.getMinX()*1.8,embBounds.getMaxY()*1.8,embBounds.getMaxZ()*1.8);
        		}
        	}
        	if (labelPoint != null) {
        		altEntity.setTranslateX(labelPoint.getX());
        		altEntity.setTranslateY(labelPoint.getY());
        		altEntity.setTranslateZ(labelPoint.getZ());
        	}
        	xform1.getChildren().add(altEntity);
          } else {
        	 altEntity = entity;
         }
       // if label is already in scene, make all labels white and highlight that one
    	final Text label = entityLabelMap.get(altEntity);
        if (label != null) {
            for (Node shape : entityLabelMap.keySet()) {
                entityLabelMap.get(shape).setFill(web(SPRITE_COLOR_HEX));
            }
            label.setFill(web(ACTIVE_LABEL_COLOR_HEX));
            return;
        }

        // otherwise, create a highlight new label
        final String funcName = getFunctionalNameByLineageName(name);
        final Text text;
        if (funcName != null) {
            text = makeClickedContentTagText(funcName);
        } else {
            text = makeClickedContentTagText(name);
        }
        text.setOnMouseEntered(event -> text.setCursor(HAND));
        text.setOnMouseExited(event -> text.setCursor(DEFAULT));

        final String tempName = name;
        text.setOnMouseClicked(event -> removeLabelFor(tempName));
        text.setWrappingWidth(-1);

        entityLabelMap.put(altEntity, text);
        //Put new label at head of Children roster for opacity compliance
        xform1.getChildren().add(0, text);

//        spritesPane.getChildren().add(text);

        alignTextWithEntity(text, altEntity, null);

        // set the name in MainApp so that other apps opening WormGUIDES can catch this event
        MainApp.selectedEntityLabelMainApp.set(name);
    }

    private void highlightActiveCellLabel(Shape3D entity) {
        for (Node shape3D : entityLabelMap.keySet()) {
            entityLabelMap.get(shape3D).setFill(web(SPRITE_COLOR_HEX));
        }
        if (entity != null && entityLabelMap.get(entity) != null) {
            entityLabelMap.get(entity).setFill(web(ACTIVE_LABEL_COLOR_HEX));
        }
    }

    /**
     * @return The 3D entity with input name. .
     */
    private Shape3D getEntityWithName(final String name) {
        // mesh view label
        if (defaultEmbryoFlag) {
        	if (true ||cellOpacityProperty.get() > nonSelectableOpacity) {
        		for (int i = 0; i < currentSceneElements.size(); i++) {
        			if (normalizeName(currentSceneElements.get(i).getSceneName()).equalsIgnoreCase(name)
        					&& currentSceneElementMeshViews.get(i) != null) {
        				return currentSceneElementMeshViews.get(i);
        			}
        		}
        	}
        }
        // sphere label
        if (true || nucOpacityProperty.get() > nonSelectableOpacity) {
        	for (int i = 0; i < spheres.size(); i++) {
        		if (spheres.get(i) != null && spheres.get(i).getCellName().equalsIgnoreCase(name)) {
        			return spheres.get(i);
        		}
        	}
        }
        return null;
    }

    /**
     * @return The 3D entity with input name. .
     */
    private Shape3D getMeshViewWithName(final String name) {
        // mesh view label
        if (defaultEmbryoFlag) {
        	if (true ||cellOpacityProperty.get() > nonSelectableOpacity) {
        		for (int i = 0; i < currentSceneElements.size(); i++) {
        			if (normalizeName(currentSceneElements.get(i).getSceneName()).equalsIgnoreCase(name)
        					&& currentSceneElementMeshViews.get(i) != null) {
        				return currentSceneElementMeshViews.get(i);
        			}
        		}
        	}
        }
        return null;
    }

    /**
     * @return The 3D entity with input name. .
     */
    private NamedNucleusSphere getSphereWithName(final String name) {
        // sphere label
        if (true || nucOpacityProperty.get() > nonSelectableOpacity) {
        	for (int i = 0; i < spheres.size(); i++) {
        		if (spheres.get(i) != null && spheres.get(i).getCellName().equalsIgnoreCase(name)) {
        			return spheres.get(i);
        		}
        	}
        }
        return null;
    }

    /**
     * Inserts a note into the list of Text nodes mapped to a specific subscene entity if the list already exists.
     * Creates a list and then adds the note if it does not.
     *
     * @param noteGraphic
     *         the Text object
     * @param subsceneEntity
     *         the subscene entity
     * @param entityCalloutMap
     *         the callout map specific to the callout position
     */
    private void addNoteGraphicToEntityCalloutMap(
            final Text noteGraphic,
            final Node subsceneEntity,
            final Map<Node, List<Text>> entityCalloutMap) {
        if (entityCalloutMap.get(subsceneEntity) == null) {
            final List<Text> noteGraphicsList = new ArrayList<>();
            entityCalloutMap.put(subsceneEntity, noteGraphicsList);
            noteGraphicsList.add(noteGraphic);
        } else {
            entityCalloutMap.get(subsceneEntity).add(noteGraphic);
        }
    }

    /**
     * Inserts note geometries into the subscene. The callout notes objects are tracked in their own maps to their
     * respective entities, but are graphically inserted into the subscene separately later because they have to keep
     * track of their horizontal/vertical offsets from the entity.
     *
     * @param list
     *         the list of nodes that billboards are added to, which are added to to the subscene. Note overlays
     *         and sprites are added to the pane that contains the subscene.
     */
    private void addNoteGeometries(final List<Node> list) {
        for (Note note : currentNotes) {
            if (note.isVisible()) {
                // map notes to their sphere/mesh view
                final Node noteGraphic = makeNoteGraphic(note);
                currentGraphicsToNotesMap.put(noteGraphic, note);

                noteGraphic.setOnMouseEntered(event -> spritesPane.setCursor(HAND));
                noteGraphic.setOnMouseExited(event -> spritesPane.setCursor(DEFAULT));

                // callouts
                if (note.isCallout()) {
                    Shape3D subsceneEntity = null;
                    if (note.attachedToCell()) {
                        subsceneEntity = getSubsceneSphereWithName(note.getCellName());
                    } else if (note.attachedToStructure() && defaultEmbryoFlag) {
                        subsceneEntity = getSubsceneMeshWithName(note.getCellName());
                    }
                    if (subsceneEntity != null) {
                        switch (note.getTagDisplay()) {
                            case CALLOUT_UPPER_LEFT:
                                if (noteGraphic instanceof Text) {
                                    addNoteGraphicToEntityCalloutMap(
                                            (Text) noteGraphic,
                                            subsceneEntity,
                                            entityCalloutULMap);
                                    spritesPane.getChildren().add(noteGraphic);
                                    noteGraphic.getTransforms().add(new Translate(
                                            -note.getCalloutHorizontalOffset(),
                                            -note.getCalloutVerticalOffset()));
                                    final Line line = new Line(0, 0, 0, 0);
                                    line.setStyle("-fx-stroke-width: 2; -fx-stroke: #DDDDDD;");
                                    spritesPane.getChildren().add(line);
                                    // map callout text to its line so they can be repositioned together during
                                    // note-entity alignment
                                    calloutLineMap.put((Text) noteGraphic, line);
                                }
                                break;
                            case CALLOUT_LOWER_LEFT:
                                if (noteGraphic instanceof Text) {
                                    addNoteGraphicToEntityCalloutMap(
                                            (Text) noteGraphic,
                                            subsceneEntity,
                                            entityCalloutLLMap);
                                    spritesPane.getChildren().add(noteGraphic);
                                    noteGraphic.getTransforms().add(new Translate(
                                            -note.getCalloutHorizontalOffset(),
                                            note.getCalloutVerticalOffset()));
                                    final Line line = new Line(0, 0, 0, 0);
                                    line.setStyle("-fx-stroke-width: 2; -fx-stroke: #FFFFFF;");
                                    spritesPane.getChildren().add(line);
                                    calloutLineMap.put((Text) noteGraphic, line);
                                }
                                break;
                            case CALLOUT_UPPER_RIGHT:
                                if (noteGraphic instanceof Text) {
                                    addNoteGraphicToEntityCalloutMap(
                                            (Text) noteGraphic,
                                            subsceneEntity,
                                            entityCalloutURMap);
                                    spritesPane.getChildren().add(noteGraphic);
                                    noteGraphic.getTransforms().add(new Translate(
                                            note.getCalloutHorizontalOffset(),
                                            -note.getCalloutVerticalOffset()));
                                    final Line line = new Line(0, 0, 0, 0);
                                    line.setStyle("-fx-stroke-width: 2; -fx-stroke: #DDDDDD;");
                                    spritesPane.getChildren().add(line);
                                    calloutLineMap.put((Text) noteGraphic, line);
                                }
                                break;
                            case CALLOUT_LOWER_RIGHT:
                                if (noteGraphic instanceof Text) {
                                    addNoteGraphicToEntityCalloutMap(
                                            (Text) noteGraphic,
                                            subsceneEntity,
                                            entityCalloutLRMap);
                                    spritesPane.getChildren().add(noteGraphic);
                                    noteGraphic.getTransforms().add(new Translate(
                                            note.getCalloutHorizontalOffset(),
                                            note.getCalloutVerticalOffset()));
                                    final Line line = new Line(0, 0, 0, 0);
                                    line.setStyle("-fx-stroke-width: 2; -fx-stroke: #DDDDDD;");
                                    spritesPane.getChildren().add(line);
                                    calloutLineMap.put((Text) noteGraphic, line);
                                }
                                break;
                            default:
                                break;
                        }
                    }

                } else if (note.isSprite()) {
                    // sprites
                    // location attachment
                    if (note.attachedToLocation()) {
                        final VBox box = new VBox(3);
                        box.setPrefWidth(getNoteSpriteTextWidth());
                        box.getChildren().add(noteGraphic);
                        // add     inivisible location marker to scene at location specified by note
                        final Sphere marker = createLocationMarker(note.getX(), note.getY(), note.getZ());
                        rootEntitiesGroup.getChildren().add(marker);
                        entitySpriteMap.put(marker, box);
                        // add vbox to sprites pane
                        spritesPane.getChildren().add(box);

                    } else {
                        Node subsceneEntity = null;
                        if (note.attachedToCell()) {
                            // cell attachment
                            subsceneEntity = getSubsceneSphereWithName(note.getCellName());
                        } else if (note.attachedToStructure() && defaultEmbryoFlag) {
                            // structure attachment
                            subsceneEntity = getSubsceneMeshWithName(note.getCellName());
                        }
                        if (subsceneEntity != null) {
                            // if another non-callout note is already attached to the subscene entity,
                            // create a vbox for note stacking
                            if (!entitySpriteMap.containsKey(subsceneEntity)) {
                                final VBox box = new VBox(3);
                                box.getChildren().add(noteGraphic);
                                entitySpriteMap.put(subsceneEntity, box);
                                spritesPane.getChildren().add(box);
                            } else {
                                // otherwise add note to the existing vbox for that entity
                                entitySpriteMap.get(subsceneEntity).getChildren().add(noteGraphic);
                            }
                        }
                    }
                } else if (note.isBillboardFront()) {
                    if (noteGraphic instanceof Text) {
                        if (note.attachedToLocation()) {
                            final Sphere marker = createLocationMarker(note.getX(), note.getY(), note.getZ());
                            rootEntitiesGroup.getChildren().add(marker);
                            billboardFrontEntityMap.put(
                                    (Text) noteGraphic,
                                    marker);
                        } else if (note.attachedToCell()) {
                            billboardFrontEntityMap.put(
                                    (Text) noteGraphic,
                                    getSubsceneSphereWithName(note.getCellName()));
                        } else if (note.attachedToStructure() && defaultEmbryoFlag) {
                            final SceneElementMeshView meshView = getSubsceneMeshWithName(note.getCellName());
                            if (meshView != null) {
                                billboardFrontEntityMap.put(
                                        (Text) noteGraphic,
                                        meshView);
                            }
                        }
                    }

                } else if (note.isBillboardImage()) {
                    if (noteGraphic != null && noteGraphic instanceof ImageView) {
                        // no need to do anything with the note graphic text since it will not be shown
                        // only the image view is shown
                        if (note.attachedToLocation()) {
                            final Sphere marker = createLocationMarker(note.getX(), note.getY(), note.getZ());
                            rootEntitiesGroup.getChildren().add(marker);
                            billboardImageEntityMap.put(
                                    (ImageView) noteGraphic,
                                    marker);
                        } else if (note.attachedToCell()) {
                            billboardImageEntityMap.put(
                                    (ImageView) noteGraphic,
                                    getSubsceneSphereWithName(note.getCellName()));
                        } else if (note.attachedToStructure() && defaultEmbryoFlag) {
//                            final SceneElementMeshView meshView = getSubsceneMeshWithName(note.getCellName());
//                            if (meshView != null) {
//                                billboardImageEntityMap.put(
//                                        (ImageView) noteGraphic,
//                                        meshView);
//                            }
                        }
                    }

                } else if (note.isBillboard()) {
                    // TODO non-front-facing billboard positioning has to be fixed (see below)
                    // they currently move with the entities they are attached to, but are offset far away - need to
                    // find the cause of this offset)
                    if (note.attachedToLocation()) {
                        // location attachment
                        noteGraphic.getTransforms().addAll(rotateX, rotateY, rotateZ);
                        noteGraphic.getTransforms().add(new Translate(note.getX(), note.getY(), note.getZ()));
                    } else if (note.attachedToCell()) {
                        // cell attachment
                        final NamedNucleusSphere sphere = getSubsceneSphereWithName(note.getCellName());
                        if (sphere != null) {
                            double offset = 5;
                            if (!uniformSize) {
                                offset = sphere.getRadius() + 2;
                            }
                            noteGraphic.getTransforms().addAll(sphere.getTransforms());
                            noteGraphic.getTransforms().add(new Translate(offset, offset));
                        }
                    } else if (note.attachedToStructure() && defaultEmbryoFlag) {
                        // structure attachment
//                        final SceneElementMeshView meshView = getSubsceneMeshWithName(note.getCellName());
//                        if (meshView != null) {
//                            double offset = 5;
//                            noteGraphic.getTransforms().addAll(meshView.getTransforms());
////                            noteGraphic.getTransforms().add(new Translate(offset, offset));
//                        }
                    }
                }

                // add graphic to appropriate place (the subscene itself, overlay box, or on sprites pane
                // overlaid on the subscene)
                final Display display = note.getTagDisplay();
                if (display != null) {
                    switch (display) {
                        case CALLOUT_UPPER_LEFT: // all callouts fall to sprite case
                        case CALLOUT_UPPER_RIGHT:
                        case CALLOUT_LOWER_LEFT:
                        case CALLOUT_LOWER_RIGHT:
                        case SPRITE: // do nothing
                            break;
                        case IMAGE: // fall to billboard case
                        case BILLBOARD_FRONT: // fall to billboard case
                        case BILLBOARD:
                            list.add(noteGraphic);
                            break;
                        case OVERLAY: // fall to default case
                        case BLANK: // fall to default case
                        default:
                            storyOverlayVBox.getChildren().add(noteGraphic);
                            break;
                    }
                }
            }
        }
    }

    /**
     * @param sceneName
     *         the scene name of the scene element mesh
     *
     * @return the mesh view representing the scene element with that scene name, null if none were found in the
     * current time frame
     */
    private SceneElementMeshView getSubsceneMeshWithName(final String sceneName) {
        for (int i = 0; i < currentSceneElements.size(); i++) {
            if (currentSceneElements.get(i).getSceneName().equalsIgnoreCase(sceneName)) {
                return currentSceneElementMeshViews.get(i);
            }
        }
        return null;
    }

    /**
     * @param lineageName
     *         the lineage name of the cell
     *
     * @return the sphere representing the cell with that lineage name, null if none were found in the current time
     * frame
     */
    private NamedNucleusSphere getSubsceneSphereWithName(final String lineageName) {
        for (int i = 0; i < cellNames.size(); i++) {
            if (cellNames.get(i).equalsIgnoreCase(lineageName) && spheres.get(i) != null) {
                return spheres.get(i);
            }
        }
        return null;
    }

    private void insertOverlayTitles() {
        if (storiesLayer != null) {
            final Text infoPaneTitle = makeNoteOverlayText("Story Title:");
            if (storiesLayer.getActiveStory() != null) {
                final Text storyTitle = makeNoteOverlayText(storiesLayer.getActiveStory().getTitle());
                storyOverlayVBox.getChildren().addAll(infoPaneTitle, storyTitle);
            } else {
                final Text noStoryTitle = makeNoteOverlayText("none");
                storyOverlayVBox.getChildren().addAll(infoPaneTitle, noStoryTitle);
            }
        }
    }

    private Text makeNoteOverlayText(final String title) {
        final Text text = new Text(title);

        text.setFill(web(SPRITE_COLOR_HEX));
        text.setSmooth(true);
        text.setFontSmoothingType(LCD);
        text.setWrappingWidth(storyOverlayVBox.getWidth());
        text.setFont(getBillboardFont());
        return text;
    }

    private Text makeNoteSpriteText(final String title) {
        final Text text = makeNoteOverlayText(title);
        text.setWrappingWidth(getNoteSpriteTextWidth());
        return text;
    }

    private Text makeClickedContentTagText(final String title) {
        final Text text = new Text(title);

        text.setFill(web(SPRITE_COLOR_HEX));
        text.setSmooth(true);
        text.setFontSmoothingType(LCD);
        text.setWrappingWidth(storyOverlayVBox.getWidth());
        text.setFont(getClickedContentLabelFont());
        return text;
    }


    /**
     * Creates the text for the orientation indicator
     *
     * @param string
     *         the indicator string ("R    L", "A    P", or "V    D")
     *
     * @return the text
     */
    private Text makeOrientationIndicatorText(final String string) {
        final Text text = new Text(string);
        text.setFont(getOrientationIndicatorFont());
        text.setSmooth(true);
        text.setFontSmoothingType(LCD);
        text.setFill(web(SPRITE_COLOR_HEX));
        return text;
    }

    private Text makeNoteBillboardText(final String title) {
        final Text text = new Text(title);
        text.setWrappingWidth(getNoteBillboardTextWidth());
        text.setFont(getBillboardFont());
        text.setSmooth(false);
        text.setFontSmoothingType(LCD);
        text.setFill(web(SPRITE_COLOR_HEX));
        text.getTransforms().add(new Scale(getBillboardScale(), getBillboardScale()));
        return text;
    }

    private Sphere createLocationMarker(final double x, final double y, final double z) {
        final Sphere sphere = new Sphere(0.05);
        sphere.getTransforms().addAll(rotateX, rotateY, rotateZ);
        sphere.getTransforms().add(new Translate(
                (-offsetX + x) * xScale,
                (-offsetY + y) * yScale,
                (-offsetZ + z) * zScale));
        // make marker transparent
        sphere.setMaterial(colorHash.getOtherNucleiMaterial(0));
        return sphere;
    }

    /**
     * Creates the graphic for a note, whether the graphic is a text or an image view
     *
     * @param note
     *         the note to create the graphic for
     *
     * @return the note graphic
     */
    private Node makeNoteGraphic(final Note note) {
        String title = note.getTagName();
        if (note.isExpandedInScene() && note.getTagContents().length() > 0) {
            title += ": " + note.getTagContents();
        } else if (note.getTagContents().length() > 0) {
            title += " [more...]";
        }

        Node node = null;
        if (note.getTagDisplay() != null) {
            switch (note.getTagDisplay()) {
                case CALLOUT_UPPER_LEFT: // fall to callout_lower_left case

                case CALLOUT_LOWER_LEFT: // fall to sprite case
                    Text t = makeNoteSpriteText(title);
                    t.setTextAlignment(TextAlignment.RIGHT); // make the text right aligned for left side callouts
                    node = t;
                    break;

                case CALLOUT_UPPER_RIGHT: // fall to sprite case

                case CALLOUT_LOWER_RIGHT: // fall to sprite case

                case SPRITE:
                    node = makeNoteSpriteText(title);
                    break;

                case BILLBOARD:
                    node = makeNoteBillboardText(title);
                    break;

                case BILLBOARD_FRONT:
                    node = makeNoteBillboardText(title);
                    break;

                case IMAGE:
                    node = createImageView(note.getResourceLocation());
                    break;

                case OVERLAY: // fall to default case

                case BLANK: // fall to default case

                default:
                    node = makeNoteOverlayText(title);
                    break;
            }
        }
        return node;
    }

    private void buildCamera() {
        camera = new PerspectiveCamera(true);

//        xform3 = new XformBox();
//        xform3.reset();
//        rootEntitiesGroup.getChildren().add(xform3);
//        xform3.getChildren().add(camera);
        camera.setNearClip(getCameraNearClip());
        camera.setFarClip(getCameraFarClip());
        camera.setTranslateZ(getCameraInitialDistance());
        getSubscene().setCamera(camera);
    }

    /**
     * Consults the local search results list (containing only lineage names, no functional names) and sets the flags
     * for cell and mesh highlighting. If the sphere or mesh view should be highlighted in the current active search,
     * then the flag at its index it set to true.
     */
    private void consultSearchResultsList() {
        isCellSearchedFlags = new boolean[cellNames.size()];
        if (defaultEmbryoFlag) {
            isMeshSearchedFlags = new boolean[currentSceneElements.size()];
        }

        // cells
        for (int i = 0; i < cellNames.size(); i++) {
            isCellSearchedFlags[i] = localSearchResults.contains(cellNames.get(i));
        }

        // meshes
        if (defaultEmbryoFlag) {
            SceneElement sceneElement;
            String sceneName;
            for (int i = 0; i < currentSceneElements.size(); i++) {
                sceneElement = currentSceneElements.get(i);
                sceneName = sceneElement.getSceneName();
                isMeshSearchedFlags[i] = localSearchResults.contains(sceneName);
            }
        }
    }

    public boolean captureImagesForMovie() {
        movieFiles.clear();
        this.count = 0;

        final Stage fileChooserStage = new Stage();

        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Save Location");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("MOV File", "*.mov"));

        final File tempFile = fileChooser.showSaveDialog(fileChooserStage);

        if (tempFile == null) {
            return false;
        }

        // save the name from the file chooser for later MOV file
        movieName = tempFile.getName();
        moviePath = tempFile.getAbsolutePath();

        // make a temp directory for the frames at the given save location
        String path = tempFile.getAbsolutePath();
        if (path.lastIndexOf("/") < 0) {
            path = path.substring(0, path.lastIndexOf("\\") + 1) + "tempFrameDir";
        } else {
            path = path.substring(0, path.lastIndexOf("/") + 1) + "tempFrameDir";
        }

        frameDir = new File(path);

        try {
            frameDir.mkdir();
        } catch (SecurityException se) {
            return false;
        }

        this.frameDirPath = frameDir.getAbsolutePath() + "/";

        captureVideo.set(true);
//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if (captureVideo.get()) {
//                    runLater(() -> {
//                        WritableImage screenCapture = subscene.snapshot(new SnapshotParameters(), null);
//                        try {
//                            File file = new File(frameDirPath + "movieFrame" + count++ + ".JPEG");
//
//                            if (file != null) {
//                                RenderedImage renderedImage = fromFXImage(screenCapture, null);
//                                write(renderedImage, "JPEG", file);
//                                movieFiles.addElement(file);
//                            }
//                        } catch (Exception e) {
//                            System.out.println("Could not write frame of movie to file.");
//                        }
//                    });
//                } else {
//                    timer.cancel();
//                }
//            }
//        }, 0, 1000);

        return true;
    }

    /**
     * Converts saved frames of development in "play" mode to a single video file
     * Notes:
     * - The outputted video has the dimensions of the subscene width and height at capture time (if the
     * window is resized during capture, these parameters will be their values at the time "Stop Capture..."
     * is pressed)
     * - The frame rate is set at 6 frames/sec
     */
    public void convertImagesToMovie() {
        captureVideo.set(false);
        javaPictures.clear();

        for (File movieFile : movieFiles) {
            JavaPicture jp = new JavaPicture();

            jp.loadImage(movieFile);

            javaPictures.addElement(jp);
        }

        if (javaPictures.size() > 0) {
            new JpegImagesToMovie(
                    (int) getSubscene().getWidth(),
                    (int) getSubscene().getHeight(),
                    6,
                    movieName,
                    javaPictures);

            // move the movie to the originally specified location
            final File movJustMade = new File(movieName);
            movJustMade.renameTo(new File(moviePath + ".mov"));

            // remove the .movtemp.jpg file
            final File movtempjpg = new File(".movtemp.jpg");
            movtempjpg.delete();
        }

        // remove all of the images in the frame directory
        if (frameDir != null && frameDir.isDirectory()) {
            final File[] frames = frameDir.listFiles();
            if (frames != null) {
                for (File frame : frames) {
                    frame.delete();
                }
            }
            frameDir.delete();
        }
    }

    /**
     * Saves a snapshot of the screen
     */
    public void stillscreenCapture() {
        final Stage fileChooserStage = new Stage();
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Save Location");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("PNG File", "*.png"));

        final WritableImage screenCapture = getSubscene().snapshot(new SnapshotParameters(), null);

        //write the image to a file
        try {
            final File file = fileChooser.showSaveDialog(fileChooserStage);
            if (file != null) {
                final RenderedImage renderedImage = fromFXImage(screenCapture, null);
                write(renderedImage, "png", file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printCellNames() {
        for (int i = 0; i < cellNames.size(); i++) {
            System.out.println(cellNames.get(i) + CS + spheres.get(i));
        }
    }

    public void printMeshNames() {
        if (defaultEmbryoFlag) {
            for (int i = 0; i < meshNames.size(); i++) {
                System.out.println(meshNames.get(i) + CS + meshes.get(i));
            }
        }
    }

    /**
     * Sets transparent anchor pane overlay for sprite notes display
     *
     * @param parentPane
     *         The {@link AnchorPane} in which labels and sprites reside
     */
    public void setNotesPane(AnchorPane parentPane) {
        if (parentPane != null) {
            spritesPane = parentPane;

            storyOverlayVBox = new VBox(5);
            storyOverlayVBox.setPrefWidth(getStoryOverlayPaneWidth());
            storyOverlayVBox.setMaxWidth(storyOverlayVBox.getPrefWidth());
            storyOverlayVBox.setMinWidth(storyOverlayVBox.getPrefWidth());

            setTopAnchor(storyOverlayVBox, 5.0);
            setRightAnchor(storyOverlayVBox, 5.0);

            spritesPane.getChildren().add(storyOverlayVBox);
        }
    }

    // Hides cell name label/context menu
    private void hideContextPopups() {
        contextMenuStage.hide();
    }

    private ChangeListener<Number> getTranslateXListener() {
        return (observable, oldValue, newValue) -> {
            final double value = newValue.doubleValue();
            if (xform1.getTranslateX() != value) {
                xform1.setTranslateX(value);
            }
        };
    }

    private ChangeListener<Number> getTranslateYListener() {
        return (observable, oldValue, newValue) -> {
            final double value = newValue.doubleValue();
            if (xform1.getTranslateY() != value) {
                xform1.setTranslateY(value);
            }
        };
    }

    private ChangeListener<Number> getTranslateZListener() {
        return (observable, oldValue, newValue) -> {
            final double value = newValue.doubleValue();
            if (xform1.getTranslateZ() != value) {
                xform1.setTranslateZ(value);
            }
        };
    }

//    private ChangeListener<Number> getRotateXAngleListener() {
//        return (observable, oldValue, newValue) -> {
//            double newAngle = newValue.doubleValue();
//            this.rotateXAngleProperty.set(newAngle);
//            rotateX.setAngle(rotateXAngleProperty.get());
//            oriIndRotX.setAngle(newAngle - initialRotation[0]);
//            repositionNotes();
//        };
//    }
//
//    private ChangeListener<Number> getRotateYAngleListener() {
//        return (observable, oldValue, newValue) -> {
//            double newAngle = newValue.doubleValue();
//            this.rotateYAngleProperty.set(newAngle);
//            rotateY.setAngle(rotateYAngleProperty.get());
//            oriIndRotY.setAngle(newAngle - initialRotation[1]);
//            repositionNotes();
//        };
//    }
//
//    private ChangeListener<Number> getRotateZAngleListener() {
//        return (observable, oldValue, newValue) -> {
//            double newAngle = newValue.doubleValue();
//            this.rotateZAngleProperty.set(newAngle);
//            rotateZ.setAngle(rotateZAngleProperty.get());;
//            oriIndRotZ.setAngle(newAngle - initialRotation[2]);
//            repositionNotes();
//        };
//    }
//
    private EventHandler<ActionEvent> getZoomOutButtonListener() {
        return event -> {
            hideContextPopups();
            double z = translateZProperty.get();
            if (z > 0.25) {
                z = z * 1.125;
            } else if (z < 0) {
                // normalize zoom by making 0 its minimum
                // javafx has a bug where for a zoom below 0, the camera flips and does not pass through the scene
                // The API does not recognize that the camera orientation has changed and thus the back of back face
                // culled shapes appear, surrounded w/ artifacts.
                z = 0;
            }
            translateZProperty.set(z);
        };
    }

    private EventHandler<ActionEvent> getZoomInButtonListener() {
        return event -> {
            hideContextPopups();
            translateZProperty.set(translateZProperty.get() / 1.125);
        };
    }

    private EventHandler<ActionEvent> getBackwardButtonListener() {
        return event -> {
            hideContextPopups();
            if (!playingMovieProperty.get()) {
                timeProperty.set(timeProperty.get() - 1);
            }
        };
    }

    private EventHandler<ActionEvent> getForwardButtonListener() {
        return event -> {
            hideContextPopups();
            if (!playingMovieProperty.get()) {
                timeProperty.set(timeProperty.get() + 1);
            }
        };
    }

    private EventHandler<ActionEvent> getClearAllLabelsButtonListener() {
    	return event -> {
    		if(this.undoableLabels == null || undoableLabels.size() <1)
    			return;
    		List<String> lastSelection = this.undoableLabels.get(0);
    		currentBlinkNames.removeAll(lastSelection);
    		allLabels.removeAll(lastSelection);
    		currentLabels.removeAll(lastSelection);
    		undoableLabels.remove(lastSelection);

    		buildScene();

    	};
    }

    /**
     * This method returns the {@link ChangeListener} that listens for the {@link BooleanProperty} that changes when
     * 'cell nucleus' is ticked/unticked in the search tab. On change, the scene refreshes and cell bodies are
     * highlighted/unhighlighted accordingly.
     *
     * @return The listener.
     */
    private ChangeListener<Boolean> getCellNucleusTickListener() {
        return (observable, oldValue, newValue) -> {
            cellNucleusTicked = newValue;
            buildScene();
        };
    }

    /**
     * This method returns the {@link ChangeListener} that listens for the {@link BooleanProperty} that changes when
     * 'cell body' is ticked/unticked in the search tab. On change, the scene refreshes and cell bodies are
     * highlighted/unhighlighted accordingly.
     *
     * @return The listener.
     */
    private ChangeListener<Boolean> getCellBodyTickListener() {
        return (observable, oldValue, newValue) -> {
            cellBodyTicked = newValue;
            buildScene();
        };
    }

    private ChangeListener<Boolean> getMulticellModeListener() {
        return (observable, oldValue, newValue) -> {
        };
    }

    /**
     * The getter for the {@link EventHandler} for the {@link MouseEvent} that is fired upon clicking on a note. The
     * handler expands the note on click.
     *
     * @return The event handler.
     */
    private EventHandler<MouseEvent> getNoteClickHandler() {
        return event -> {
            if (event.isStillSincePress()) {
                final Node result = event.getPickResult().getIntersectedNode();
                if (result instanceof Text) {
                    final Text picked = (Text) result;
                    final Note note = currentGraphicsToNotesMap.get(picked);
                    if (note != null) {
                        note.setExpandedInScene(!note.isExpandedInScene());
                        if (note.isExpandedInScene()) {
                            picked.setText(note.getTagName() + ": " + note.getTagContents());
                        } else {
                            picked.setText(note.getTagName() + "\n[more...]");
                        }
                    }
                }
            }
        };
    }

    public SubScene getSubscene() {
		return subscene;
	}

	public double getMousePosX() {
		return mousePosX;
	}

	/**
     * This service spools a thread that
     * <p>
     * 1) retrieves the data for cells, cell bodies, and multicellular
     * structures for the current timeProperty
     * <p>
     * 2) clears the notes, labels, and entities in the subscene
     * <p>
     * 3) adds the current notes, labels, and entities to the subscene
     * <p>
     * 4) adds previous time points if requested.
     */
    private final class RenderService extends Service<Void> {

    	
		@Override
        protected Task<Void> createTask() {
			return new Task<Void>() {


				@Override
				protected Void call() throws Exception {
					runLater(() -> {
						refreshScene();


						//render current time point
						getSceneData();
						addEntitiesAndNotes();

						//render previous time points, AFTER current, so that their transparency transmits the current cells.
						int loop = (int)numPrev.get();
						//avoid index out of bound
						int loop_end = timeProperty.get() - loop;
						if (loop_end < 0) {
							loop_end = 0;
						}
						for(int i = timeProperty.get() - 1; i > loop_end; --i) {
							getCellSceneData(i);
							addEntitiesNoNotesWithColorRule();
						}
						double tzp = translateZProperty.get();
						xform1.setTranslateZ(translateZProperty.get());
						if (cumRotShiftCoords != null) {
							for (Node content:xform1.getChildren()) {
								double ctx = content.getTranslateX();
								double cty = content.getTranslateY();
								double ctz = content.getTranslateZ();
								Point3D newTranslateCoords = new Point3D(ctx+cumRotShiftCoords.getX(), cty+cumRotShiftCoords.getY(), ctz-cumRotShiftCoords.getZ());

								if (content.getTransforms().size() > 0) {
									content.getTransforms().set(0, new Translate(newTranslateCoords.getX(), newTranslateCoords.getY(), newTranslateCoords.getZ())
											.createConcatenation(content.getTransforms().get(0)));
								} else {
									content.getTransforms().add(new Translate(newTranslateCoords.getX(), newTranslateCoords.getY(), newTranslateCoords.getZ()));
								}

							}
						}
						xform2.setTranslateZ(initialTranslateZ);
						repositionNotes();

						renderComplete = true;


					});
					return null;
				}
			};
        }
    }


    /**
     * This JavaFX {@link Service} of type Void spools a thread to play the subscene movie. It waits the time
     * in milliseconds defined in the variable WAIT_TIME_MILLI before rendering the next
     * timeProperty frame.
     */
    private final class PlayService extends Service<Void> {
        @Override
        protected final Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    while (true) {
                        if (isCancelled()) {
                            break;
                        }
                        runLater(() -> timeProperty.set(timeProperty.get() + 1));
                        try {
                            sleep(getWaitTimeMilli());
                        } catch (InterruptedException ie) {
                            break;
                        }
                    }
                    return null;
                }
            };
        }
    }

    /**
     * This class is the {@link ChangeListener} that listens changes in the height or width of the modelAnchorPane in
     * which the subscene lives. When the size changes, front-facing billboards and sprites (notes and labels) are
     * repositioned to align with their appropriate positions (whether it is a location to an entity).
     */
    private final class SubsceneSizeListener implements ChangeListener<Number> {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            repositionNotes();
        }
    }

    /**
     * This class is the Comparator for Shape3Ds that compares based on opacity. This is used for z-buffering for
     * semi-opaque materials. Entities with opaque materials should be rendered last (added first to the
     * rootEntitiesGroup group.
     */
    private final class OpacityComparator implements Comparator<Node> {
        @Override
        public int compare(Node o1, Node o2) {
            double op1 = colorHash.getMaterialOpacity(((Shape3D)o1).getMaterial());
            double op2 = colorHash.getMaterialOpacity(((Shape3D)o2).getMaterial());
            if (op1 < op2) {
                return 1;
            } else if (op1 > op2) {
                return -1;
            } else {
                return 0;
            }
        }
    }

	public double getMousePosY() {
		// TODO Auto-generated method stub
		return mousePosY;
	}
}