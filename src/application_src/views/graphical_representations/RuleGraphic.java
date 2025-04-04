/*
 * Bao Lab 2017
 */

package application_src.views.graphical_representations;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import application_src.MainApp;
import application_src.application_model.annotation.color.Rule;
import application_src.application_model.search.SearchConfiguration.SearchType;

import static java.util.Objects.requireNonNull;

import static javafx.geometry.Insets.EMPTY;
import static javafx.scene.control.ContentDisplay.GRAPHIC_ONLY;
import static javafx.scene.control.OverrunStyle.ELLIPSIS;
import static javafx.scene.layout.Priority.ALWAYS;
import static javafx.scene.layout.Priority.SOMETIMES;
import static javafx.scene.paint.Color.LIGHTGREY;
import static javafx.scene.paint.Color.web;

import static application_src.application_model.loaders.IconImageLoader.getCloseIcon;
import static application_src.application_model.loaders.IconImageLoader.getEditIcon;
import static application_src.application_model.loaders.IconImageLoader.getEyeIcon;
import static application_src.application_model.loaders.IconImageLoader.getEyeInvertIcon;
import static application_src.application_model.resources.utilities.AppFont.getBolderFont;
import static application_src.application_model.resources.utilities.AppFont.getFont;

/**
 * Graphical representation of a rule (used to display a rule in a list view)
 */
public class RuleGraphic extends HBox {

    /** Length and width (in pixels) of color rule UI buttons */
    public static final int UI_SIDE_LENGTH = 22;

    private final Button label;
    private final Button colorRectangle;
    private final Button editBtn;
    private final Button visibleBtn;
    private final Button deleteBtn;
    private final Tooltip toolTip;
    private final ImageView eyeIcon;
    private final ImageView eyeIconInverted;

	private Text lText;

	private TextFlow lTextFlow;

    public RuleGraphic(final Rule rule, final BooleanProperty ruleChanged) {
        super();

        requireNonNull(rule);
        requireNonNull(ruleChanged);

        label = new Button();
        colorRectangle = new Button();
        editBtn = new Button();
        visibleBtn = new Button();
        deleteBtn = new Button();
        toolTip = new Tooltip();

        setSpacing(3);
        setPadding(new Insets(3));
        setPrefWidth(275);
        setMinWidth(getPrefWidth());

        colorRectangle.setPrefSize(150, UI_SIDE_LENGTH);
        colorRectangle.setMaxSize(500, UI_SIDE_LENGTH);
        colorRectangle.setMinSize(150, UI_SIDE_LENGTH);
        colorRectangle.setContentDisplay(GRAPHIC_ONLY);
        colorRectangle.setPadding(new Insets(0,2,0,2));

        label.setFont(getFont());
        label.setPrefSize(150, UI_SIDE_LENGTH);
        label.setMaxSize(500, UI_SIDE_LENGTH);
        label.setMinSize(150, UI_SIDE_LENGTH);
        setHgrow(label, ALWAYS);
        label.setPadding(EMPTY);
        label.textOverrunProperty().set(ELLIPSIS);
        label.setOnAction(event -> {
        	Platform.runLater(new Runnable() {
        		@Override
        		public void run() {
        			if (!MainApp.controller.getWindow3DController().blinkingRules.contains(rule)) {
        				MainApp.controller.getWindow3DController().blinkingRules.add(rule);
        			}else {
        				MainApp.controller.getWindow3DController().blinkingRules.remove(rule);
        				rule.setVisible(true);
        			}
        		}
        	});        
        });
        label.setOnMouseClicked(event -> {
        	if (event.getButton() == MouseButton.SECONDARY)
        		rule.showEditStage(null);
        });
    	lText = new Text(rule.getSearchedText()); 				//Weird that this always receives null value, must be reset later...
    	lTextFlow = new TextFlow(lText);
		lText.setWrappingWidth(-1);
		lText.setOnMouseEntered(Event::consume);
		lText.setOnMouseClicked(Event::consume);
		lText.setDisable(true);
		lText.setFont(getBolderFont());
		lText.setStrokeWidth(0.1);
		lText.setStroke(Color.BLACK);
		label.setText(lText.getText());
        label.setGraphic(lTextFlow);

        final Region r = new Region();
        setHgrow(r, SOMETIMES);

        colorRectangle.setPrefSize(UI_SIDE_LENGTH, UI_SIDE_LENGTH);
        colorRectangle.setMaxSize(UI_SIDE_LENGTH, UI_SIDE_LENGTH);
        colorRectangle.setMinSize(UI_SIDE_LENGTH, UI_SIDE_LENGTH);
        colorRectangle.setContentDisplay(GRAPHIC_ONLY);
        colorRectangle.setPadding(EMPTY);
        colorRectangle.setBackground(new Background(new BackgroundFill(rule.getColor(), null, null)));
        colorRectangle.setGraphicTextGap(0);
        colorRectangle.setOnAction(event -> rule.showEditStage(null));

        editBtn.setPrefSize(UI_SIDE_LENGTH, UI_SIDE_LENGTH);
        editBtn.setMaxSize(UI_SIDE_LENGTH, UI_SIDE_LENGTH);
        editBtn.setMinSize(UI_SIDE_LENGTH, UI_SIDE_LENGTH);
        editBtn.setContentDisplay(GRAPHIC_ONLY);
        editBtn.setPadding(EMPTY);
        editBtn.setGraphic(getEditIcon());
        editBtn.setGraphicTextGap(0);
        editBtn.setOnAction(event -> rule.showEditStage(null));

        eyeIcon = getEyeIcon();
        eyeIconInverted = getEyeInvertIcon();

        visibleBtn.setPrefSize(UI_SIDE_LENGTH, UI_SIDE_LENGTH);
        visibleBtn.setMaxSize(UI_SIDE_LENGTH, UI_SIDE_LENGTH);
        visibleBtn.setMinSize(UI_SIDE_LENGTH, UI_SIDE_LENGTH);
        visibleBtn.setPadding(EMPTY);
        visibleBtn.setContentDisplay(GRAPHIC_ONLY);
        visibleBtn.setGraphic(eyeIcon);
        visibleBtn.setGraphicTextGap(0);
        visibleBtn.setOnAction(event -> {
            rule.setVisible(!rule.isVisible());
            blackOutVisibleButton(!rule.isVisible());
            ruleChanged.set(true);
        });

        deleteBtn.setPrefSize(UI_SIDE_LENGTH, UI_SIDE_LENGTH);
        deleteBtn.setMaxSize(UI_SIDE_LENGTH, UI_SIDE_LENGTH);
        deleteBtn.setMinSize(UI_SIDE_LENGTH, UI_SIDE_LENGTH);
        deleteBtn.setPadding(EMPTY);
        deleteBtn.setContentDisplay(GRAPHIC_ONLY);
        deleteBtn.setGraphic(getCloseIcon());

        toolTip.setFont(getFont());
        label.setTooltip(toolTip);
        
        if (rule.getSearchType() == SearchType.ALL_RULES_IN_LIST) {
        	colorRectangle.setDisable(true);
        	editBtn.setDisable(true);
        }

		getChildren().addAll(label, r, /* colorRectangle, editBtn, */visibleBtn, deleteBtn);
    }

    /**
     * Sets the graphic for the visible eye icon
     *
     * @param isRuleInvisible
     *         true if the rule is visible, false otherwise
     */
    public void blackOutVisibleButton(final boolean isRuleInvisible) {
        if (isRuleInvisible) {
            visibleBtn.setGraphic(eyeIconInverted);
        } else {
            visibleBtn.setGraphic(eyeIcon);
        }
    }

    /**
     * @return the rule label text
     */
    public String getLabelText() {
        return label.getText().trim();
    }

    /**
     * Changes the color of the rectangle displayed next to the rule name in the rule's graphical representation.
     *
     * @param color
     *         color that the rectangle in the graphical representation of the rule should be changed to
     */
    public void setColorButton(final Color color) {
        if (label.getGraphic() != null) {
            lTextFlow.setBackground(new Background(new BackgroundFill(color!=null?color:Color.WHITE, null, null)));
    		lText.setFill(color!=null?color.invert():Color.BLACK);
    		String lTextFillString = lText.getFill().toString();
//			if (lTextFillString.matches(   "(0x[6-9].[6-9].[6-9]...)"
//										+ "|(0x..[6-9].[6-9]...)"
//										+ "|(0x[6-9].[6-9].....)"
//										+ "|(0x[6-9]...[6-9]...)")) {
				int darknessSum= (Integer.parseInt(lTextFillString.substring(2, 4), 16))
								+(Integer.parseInt(lTextFillString.substring(4, 6), 16))
								+(Integer.parseInt(lTextFillString.substring(6, 8), 16));
				lText.setFill((darknessSum < 382/* ||color.getOpacity()>0.3 */)?Color.BLACK:Color.WHITE);
			}

//        }
    }

    /**
     * Resets the label to the text
     *
     * @param labelText
     *         the text
     */
    public void resetLabel(final String labelText) {
        if (labelText != null) {
        	lText.setText("  "+labelText);
        }
    }

    /**
     * Resets the tooltip to the text
     *
     * @param tooltipText
     *         the text
     */
    public void resetTooltip(final String tooltipText) {
        if (tooltipText != null) {
            toolTip.setText(tooltipText);
        }
    }

    public Button getDeleteButton() {
        return deleteBtn;
    }
}
