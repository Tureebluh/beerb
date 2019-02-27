/*************************************************************************************************************************
 * Source Code Form License Notice
 *************************************************************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 **************************************************************************************************************************/
/*************************************************************************************************************************
 *   Author: Jarek Thomas
 *
 *   Model for Effects Modal
 *   Handles all event listeners for modal
 ***************************************************************************************************************************/

package main.models;

import java.util.Arrays;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class EffectsModel {
    private final static EffectsModel INSTANCE = new EffectsModel();
    
        
    private final ObservableList<String> effectsAddedObList, effectsStringList;
    private final SimpleStringProperty effectsCBValue = new SimpleStringProperty();
    private final ObservableList<Effect> effectsObjectList;
    private final ObjectProperty<Effect> sampleTextEffect = new SimpleObjectProperty<>();
    private VBox root;
    private Stage myDialog, textCreationStage;
    private final Scene myDialogScene;
    private Boolean myDialogOwnerSet = false;
    
    private EffectsModel(){
        this.myDialog = new Stage();
        this.textCreationStage = new Stage();
        this.root = new VBox();
        this.root.setSpacing(15);
        this.root.setPadding(new Insets(20));
        this.root.setAlignment(Pos.CENTER);
        this.myDialogScene = new Scene(root);
        this.effectsAddedObList = FXCollections.observableArrayList();
        this.effectsObjectList = FXCollections.observableArrayList();
        this.effectsStringList = FXCollections.observableArrayList();
        
        myDialog.addEventHandler(WindowEvent.WINDOW_SHOWING, new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent window) {
                if(!myDialogOwnerSet){
                    myDialogOwnerSet = true;
                    myDialog.initModality(Modality.WINDOW_MODAL);
                    myDialog.initOwner(textCreationStage);
                }
            }
        });
        myDialog.setOnHidden(e->{
            Platform.runLater(()->{
                effectsCBValueProperty().set("New Effect...");
            });
        });
    }
    
    public static EffectsModel getInstance(){
        return INSTANCE;
    }
    
    public final ObservableList getEffectsStringList(){
        return this.effectsStringList;
    }
    public ObservableList getEffectsAddedList(){
        return this.effectsAddedObList;
    }
    public ObservableList getEffectsObjectList(){
        return this.effectsObjectList;
    }
    
    public final String getEffectsCBValue(){
        return effectsCBValue.getValue();
    }
    
    public final void setEffectsCBValue(String effectsCBValue){
        this.effectsCBValue.setValue(effectsCBValue); 
    }
    public StringProperty effectsCBValueProperty(){
        return effectsCBValue;
    }
    
        
    public final Effect getSampleTextEffect(){
        return sampleTextEffect.getValue();
    }
    public final void setSampleTextEffect(Effect sampleTextEffect){
        this.sampleTextEffect.setValue(sampleTextEffect); 
    }
    public ObjectProperty<Effect> sampleTextEffectProperty(){
        return sampleTextEffect;
    }
    
    public final Stage getTextCreationStage(){
        return textCreationStage;
    }
    public final void setTextCreationStage(Stage stage){
        textCreationStage = stage;
    }
    
    //Loads effect with associated parameters
    
    public void openEffectParameters(){
        root.getChildren().clear();
        switch(getEffectsCBValue()){
            
            case "Bloom": {
                Bloom bloom = new Bloom();
                Button btn = new Button("Accept");
                
                Text text = new Text("Bloom Threshold");
                TextField bloomThreshold = new TextField("0.3");
                
                text.setFont(Font.font("System", FontWeight.NORMAL, 18));
                bloomThreshold.setTooltip(new Tooltip("Valid range 0.0 - 1.0 "));
                
                root.getChildren().addAll(text, bloomThreshold, btn);
                bloomThreshold.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{1,2}[\\.]{1}\\d{1,2}")) {
                        bloomThreshold.setText(newValue.replaceAll("[a-zA-Z_]", ""));
                    }
                });
                btn.setOnAction((e -> {
                    bloom.setThreshold(Double.parseDouble(bloomThreshold.getText()));
                    getEffectsObjectList().add(bloom);
                    getEffectsAddedList().add("Bloom");
                    addEffectToSample();
                    myDialog.hide();
                }));
                break;
            }
            case "Box Blur": {
                
                BoxBlur boxBlur = new BoxBlur();
                Button btn = new Button("Accept");
                
                Text boxBlurWidthText = new Text("Width");
                TextField boxBlurWidth = new TextField("5.0");
                boxBlurWidth.setTooltip(new Tooltip("Valid range 0.0 - 255.0 "));
                
                Text boxBlurHeightText = new Text("Height");
                TextField boxBlurHeight = new TextField("5.0");
                boxBlurHeight.setTooltip(new Tooltip("Valid range 0.0 - 255.0 "));
                
                Text boxBlurIterationText = new Text("Iterations");
                TextField boxBlurIteration = new TextField("1");
                boxBlurIteration.setTooltip(new Tooltip("Valid range 1 - 3 "));
                
                root.getChildren().addAll(boxBlurWidthText, boxBlurWidth, boxBlurHeightText, boxBlurHeight, boxBlurIterationText, boxBlurIteration, btn);
                
                boxBlurWidth.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{1,2}[\\.]{1}\\d{1,2}")) {
                        boxBlurWidth.setText(newValue.replaceAll("[a-zA-Z_]", ""));
                    }
                });
                boxBlurHeight.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{1,2}[\\.]{1}\\d{1,2}")) {
                        boxBlurHeight.setText(newValue.replaceAll("[a-zA-Z_]", ""));
                    }
                });
                boxBlurIteration.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("[1-3]{1}")) {
                        boxBlurIteration.setText(newValue.replaceAll("[a-zA-Z_]", ""));
                    }
                });
                
                btn.setOnAction((e -> {
                    
                    boxBlur.setHeight(Double.parseDouble(boxBlurHeight.getText()));
                    boxBlur.setWidth(Double.parseDouble(boxBlurWidth.getText()));
                    boxBlur.setIterations(Integer.parseInt(boxBlurIteration.getText()));
                    getEffectsObjectList().add(boxBlur);
                    getEffectsAddedList().add("Box Blur");
                    addEffectToSample();
                    myDialog.hide();
                }));
                
                break;
            }
            case "Gaussian Blur": {
                GaussianBlur gausBlur = new GaussianBlur();
                Button btn = new Button("Accept");
                
                Text gausBlurRadiusText = new Text("Radius");
                TextField gausBlurRadius = new TextField("10.0");
                gausBlurRadius.setTooltip(new Tooltip("Valid range 0.0 - 255.0 "));
                
                root.getChildren().addAll(gausBlurRadiusText, gausBlurRadius, btn);
                
                gausBlurRadius.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{1,2}[\\.]{1}\\d{1,2}")) {
                        gausBlurRadius.setText(newValue.replaceAll("[a-zA-Z_]", ""));
                    }
                });
                
                btn.setOnAction((e -> {
                    
                    gausBlur.setRadius( Double.parseDouble(gausBlurRadius.getText()) );
                    getEffectsObjectList().add(gausBlur);
                    getEffectsAddedList().add("Gaussian Blur");
                    addEffectToSample();
                    myDialog.hide();
                }));
                
                break;
            }
            case "Drop Shadow": {
                
                DropShadow dropShadow = new DropShadow();
                Button btn = new Button("Accept");
                
                Text dropShadowColorText = new Text("Color");
                ColorPicker dropShadowColor = new ColorPicker();
                
                Text dropShadowHeightText = new Text("Height");
                TextField dropShadowHeight = new TextField("21.0");
                dropShadowHeight.setTooltip(new Tooltip("Valid range 0.0 - 255.0 "));
                
                Text dropShadowWidthText = new Text("Width");
                TextField dropShadowWidth = new TextField("21.0");
                dropShadowWidth.setTooltip(new Tooltip("Valid range 0.0 - 255.0 "));
                
                Text dropShadowRadiusText = new Text("Radius");
                TextField dropShadowRadius = new TextField("10.0");
                dropShadowRadius.setTooltip(new Tooltip("Valid range 0.0 - 127.0 "));
                
                Text dropShadowSpreadText = new Text("Spread");
                TextField dropShadowSpread = new TextField("0.0");
                dropShadowSpread.setTooltip(new Tooltip("Valid range 0.0 - 1.0 "));
                
                Text dropShadowXText = new Text("X-Offset (Left to right)");
                TextField dropShadowX = new TextField("0.0");
                dropShadowX.setTooltip(new Tooltip("Valid range 0.0 - ?? "));
                
                Text dropShadowYText = new Text("Y-Offset (Top to bottom)");
                TextField dropShadowY = new TextField("0.0");
                dropShadowY.setTooltip(new Tooltip("Valid range 0.0 - ?? "));
                
                root.getChildren().addAll(dropShadowColorText, dropShadowColor, dropShadowHeightText, dropShadowHeight, dropShadowWidthText, dropShadowWidth, 
                        dropShadowRadiusText, dropShadowRadius, dropShadowSpreadText, dropShadowSpread, dropShadowXText, dropShadowX, dropShadowYText, dropShadowY, btn);

                btn.setOnAction((e -> {
                    
                    dropShadow.setColor(Color.web(dropShadowColor.getValue().toString().substring(2, 8)));
                    dropShadow.setWidth( Double.parseDouble(dropShadowWidth.getText()) );
                    dropShadow.setHeight( Double.parseDouble(dropShadowHeight.getText()) );
                    dropShadow.setRadius( Double.parseDouble(dropShadowRadius.getText()) );
                    dropShadow.setSpread( Double.parseDouble(dropShadowSpread.getText()) );
                    dropShadow.setOffsetX( Double.parseDouble(dropShadowX.getText()) );
                    dropShadow.setOffsetY( Double.parseDouble(dropShadowY.getText()) );
                    dropShadow.setBlurType(BlurType.GAUSSIAN);
                    getEffectsObjectList().add(dropShadow);
                    getEffectsAddedList().add("Drop Shadow");
                    addEffectToSample();
                    myDialog.hide();
                }));
                
                dropShadowHeight.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{1,2}[\\.]{1}\\d{1,2}")) {
                        dropShadowHeight.setText(newValue.replaceAll("[a-zA-Z_]", ""));
                    }
                });
                dropShadowWidth.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{1,2}[\\.]{1}\\d{1,2}")) {
                        dropShadowWidth.setText(newValue.replaceAll("[a-zA-Z_]", ""));
                    }
                });
                dropShadowRadius.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{1,2}[\\.]{1}\\d{1,2}")) {
                        dropShadowRadius.setText(newValue.replaceAll("[a-zA-Z_]", ""));
                    }
                });
                dropShadowSpread.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{1,2}[\\.]{1}\\d{1,2}")) {
                        dropShadowSpread.setText(newValue.replaceAll("[a-zA-Z_]", ""));
                    }
                });
                dropShadowX.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{1,2}[\\.]{1}\\d{1,2}")) {
                        dropShadowX.setText(newValue.replaceAll("[a-zA-Z_]", ""));
                    }
                });
                dropShadowY.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{1,2}[\\.]{1}\\d{1,2}")) {
                        dropShadowY.setText(newValue.replaceAll("[a-zA-Z_]", ""));
                    }
                });
                
                break;
            }
            case "Inner Shadow": {
                
                InnerShadow innerShadow = new InnerShadow();
                Button btn = new Button("Accept");
                
                Text innerShadowColorText = new Text("Color");
                ColorPicker innerShadowColor = new ColorPicker();
                
                Text innerShadowRadiusText = new Text("Radius");
                TextField innerShadowRadius = new TextField("10.0");
                innerShadowRadius.setTooltip(new Tooltip("Valid range 0.0 - 127.0 "));
                
                Text innerShadowWidthText = new Text("Width");
                TextField innerShadowWidth = new TextField("21.0");
                innerShadowWidth.setTooltip(new Tooltip("Valid range 0.0 - 255.0 "));
                
                Text innerShadowHeightText = new Text("Height");
                TextField innerShadowHeight = new TextField("21.0");
                innerShadowHeight.setTooltip(new Tooltip("Valid range 0.0 - 255.0 "));
                
                Text innerShadowChokeText = new Text("Choke");
                TextField innerShadowChoke = new TextField("0.0");
                innerShadowChoke.setTooltip(new Tooltip("Valid range 0.0 - 1.0 "));
                
                Text innerShadowXText = new Text("X-Offset (Left to right)");
                TextField innerShadowX = new TextField("0.0");
                innerShadowX.setTooltip(new Tooltip("Valid range 0.0 - ?? "));
                
                Text innerShadowYText = new Text("Y-Offset (Top to bottom)");
                TextField innerShadowY = new TextField("0.0");
                innerShadowY.setTooltip(new Tooltip("Valid range 0.0 - ?? "));             
                
                root.getChildren().addAll(innerShadowColorText, innerShadowColor, innerShadowRadiusText, innerShadowRadius, innerShadowWidthText, innerShadowWidth, innerShadowHeightText,
                        innerShadowHeight, innerShadowChokeText, innerShadowChoke, innerShadowXText, innerShadowX, innerShadowYText, innerShadowY, btn);
                
                btn.setOnAction((e -> {
                    
                    innerShadow.setColor(Color.web(innerShadowColor.getValue().toString().substring(2, 8)));
                    innerShadow.setWidth( Double.parseDouble(innerShadowWidth.getText()) );
                    innerShadow.setHeight( Double.parseDouble(innerShadowHeight.getText()) );
                    innerShadow.setRadius( Double.parseDouble(innerShadowRadius.getText()) );
                    innerShadow.setChoke( Double.parseDouble(innerShadowChoke.getText()) );
                    innerShadow.setOffsetX( Double.parseDouble(innerShadowX.getText()) );
                    innerShadow.setOffsetY( Double.parseDouble(innerShadowY.getText()) );
                    innerShadow.setBlurType(BlurType.GAUSSIAN);
                    getEffectsObjectList().add(innerShadow);
                    getEffectsAddedList().add("Inner Shadow");
                    addEffectToSample();
                    myDialog.hide();
                }));
                
                innerShadowRadius.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{1,2}[\\.]{1}\\d{1,2}")) {
                        innerShadowRadius.setText(newValue.replaceAll("[a-zA-Z_]", ""));
                    }
                });
                innerShadowWidth.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{1,2}[\\.]{1}\\d{1,2}")) {
                        innerShadowWidth.setText(newValue.replaceAll("[a-zA-Z_]", ""));
                    }
                });
                innerShadowHeight.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{1,2}[\\.]{1}\\d{1,2}")) {
                        innerShadowHeight.setText(newValue.replaceAll("[a-zA-Z_]", ""));
                    }
                });
                innerShadowChoke.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{1,2}[\\.]{1}\\d{1,2}")) {
                        innerShadowChoke.setText(newValue.replaceAll("[a-zA-Z_]", ""));
                    }
                });
                innerShadowX.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{1,2}[\\.]{1}\\d{1,2}")) {
                        innerShadowX.setText(newValue.replaceAll("[a-zA-Z_]", ""));
                    }
                });
                innerShadowY.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{1,2}[\\.]{1}\\d{1,2}")) {
                        innerShadowY.setText(newValue.replaceAll("[a-zA-Z_]", ""));
                    }
                });
               
                
                break;
            }
            case "Reflection": {
                
                Reflection reflection = new Reflection();
                Button btn = new Button("Accept");
                
                Text reflectionTopOffsetText = new Text("Top Offset");
                TextField reflectionTopOffset = new TextField("0.0");
                reflectionTopOffset.setTooltip(new Tooltip("Valid range 0.0 - ?? "));
                
                Text reflectionBotOpacityText = new Text("Bottom Opacity");
                TextField reflectionBotOpacity = new TextField("0.0");
                reflectionBotOpacity.setTooltip(new Tooltip("Valid range 0.0 - 1.0 "));
                
                Text reflectionTopOpacityText = new Text("Top Opacity");
                TextField reflectionTopOpacity = new TextField("0.5");
                reflectionTopOpacity.setTooltip(new Tooltip("Valid range 0.0 - 1.0 "));
                
                Text reflectionFractionText = new Text("Fraction");
                TextField reflectionFraction = new TextField("0.75");
                reflectionFraction.setTooltip(new Tooltip("Valid range 0.0 - 1.0 "));
                
                root.getChildren().addAll(reflectionTopOffsetText, reflectionTopOffset, reflectionBotOpacityText, reflectionBotOpacity, reflectionTopOpacityText, reflectionTopOpacity,
                        reflectionFractionText, reflectionFraction, btn);
                
                btn.setOnAction((e -> {
                    
                    reflection.setTopOffset(Double.parseDouble(reflectionTopOffset.getText()) );
                    reflection.setBottomOpacity(Double.parseDouble(reflectionBotOpacity.getText()) );
                    reflection.setTopOpacity(Double.parseDouble(reflectionTopOpacity.getText()) );
                    reflection.setFraction(Double.parseDouble(reflectionFraction.getText()) );
                    getEffectsObjectList().add(reflection);
                    getEffectsAddedList().add("Reflection");
                    addEffectToSample();
                    myDialog.hide();
                }));
                
                reflectionTopOffset.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{1,2}[\\.]{1}\\d{1,2}")) {
                        reflectionTopOffset.setText(newValue.replaceAll("[a-zA-Z_]", ""));
                    }
                });
                reflectionBotOpacity.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{1,2}[\\.]{1}\\d{1,2}")) {
                        reflectionBotOpacity.setText(newValue.replaceAll("[a-zA-Z_]", ""));
                    }
                });
                reflectionTopOpacity.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{1,2}[\\.]{1}\\d{1,2}")) {
                        reflectionTopOpacity.setText(newValue.replaceAll("[a-zA-Z_]", ""));
                    }
                });
                reflectionFraction.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{1,2}[\\.]{1}\\d{1,2}")) {
                        reflectionFraction.setText(newValue.replaceAll("[a-zA-Z_]", ""));
                    }
                });
                
                break;
            }
            case "Perspective": {
                
                PerspectiveTransform perspectiveTransform = new PerspectiveTransform();
                Button btn = new Button("Accept");
                
                Text perspectiveTransformLlxText = new Text("Lower left corner - X");
                TextField perspectiveTransformLlx = new TextField("-10.0");
                
                Text perspectiveTransformLlyText = new Text("Lower left corner - Y");
                TextField perspectiveTransformLly = new TextField("75.0");
                
                Text perspectiveTransformLrxText = new Text("Lower right corner - X");
                TextField perspectiveTransformLrx = new TextField("250.0");
                
                Text perspectiveTransformLryText = new Text("Lower right corner - Y");
                TextField perspectiveTransformLry = new TextField("65.0");
                
                Text perspectiveTransformUlxText = new Text("Upper left corner - X");
                TextField perspectiveTransformUlx = new TextField("-10.0");
                
                Text perspectiveTransformUlyText = new Text("Upper left corner - Y");
                TextField perspectiveTransformUly = new TextField("5.0");
                
                Text perspectiveTransformUrxText = new Text("Upper right corner - X");
                TextField perspectiveTransformUrx = new TextField("250.0");
                
                Text perspectiveTransformUryText = new Text("Upper right corner - Y");
                TextField perspectiveTransformUry = new TextField("15.0");
                
                root.getChildren().addAll(perspectiveTransformLlxText, perspectiveTransformLlx, perspectiveTransformLlyText, perspectiveTransformLly, perspectiveTransformLrxText, perspectiveTransformLrx, perspectiveTransformLryText,
                    perspectiveTransformLry, perspectiveTransformUlxText, perspectiveTransformUlx, perspectiveTransformUlyText, perspectiveTransformUly, perspectiveTransformUrxText, perspectiveTransformUrx, perspectiveTransformUryText, perspectiveTransformUry, btn);
                
                btn.setOnAction((e -> {
                    
                    perspectiveTransform.setLlx( Double.parseDouble(perspectiveTransformLlx.getText()) );
                    perspectiveTransform.setLly( Double.parseDouble(perspectiveTransformLly.getText()) );
                    perspectiveTransform.setLrx( Double.parseDouble(perspectiveTransformLrx.getText()) );
                    perspectiveTransform.setLry( Double.parseDouble(perspectiveTransformLry.getText()) );
                    perspectiveTransform.setUlx( Double.parseDouble(perspectiveTransformUlx.getText()) );
                    perspectiveTransform.setUly( Double.parseDouble(perspectiveTransformUly.getText()) );
                    perspectiveTransform.setUrx( Double.parseDouble(perspectiveTransformUrx.getText()) );
                    perspectiveTransform.setUry( Double.parseDouble(perspectiveTransformUry.getText()) );
                    getEffectsObjectList().add(perspectiveTransform);
                    getEffectsAddedList().add("Perspective");
                    addEffectToSample();
                    myDialog.hide();
                }));
                perspectiveTransformLlx.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{1,2}[\\.]{1}\\d{1,2}")) {
                        perspectiveTransformLlx.setText(newValue.replaceAll("[a-zA-Z_]", ""));
                    }
                });
                perspectiveTransformLly.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{1,2}[\\.]{1}\\d{1,2}")) {
                        perspectiveTransformLly.setText(newValue.replaceAll("[a-zA-Z_]", ""));
                    }
                });
                perspectiveTransformLrx.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{1,2}[\\.]{1}\\d{1,2}")) {
                        perspectiveTransformLrx.setText(newValue.replaceAll("[a-zA-Z_]", ""));
                    }
                });
                perspectiveTransformLry.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{1,2}[\\.]{1}\\d{1,2}")) {
                        perspectiveTransformLry.setText(newValue.replaceAll("[a-zA-Z_]", ""));
                    }
                });
                perspectiveTransformUlx.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{1,2}[\\.]{1}\\d{1,2}")) {
                        perspectiveTransformUlx.setText(newValue.replaceAll("[a-zA-Z_]", ""));
                    }
                });
                perspectiveTransformUly.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{1,2}[\\.]{1}\\d{1,2}")) {
                        perspectiveTransformUly.setText(newValue.replaceAll("[a-zA-Z_]", ""));
                    }
                });
                perspectiveTransformUrx.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{1,2}[\\.]{1}\\d{1,2}")) {
                        perspectiveTransformUrx.setText(newValue.replaceAll("[a-zA-Z_]", ""));
                    }
                });
                perspectiveTransformUry.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{1,2}[\\.]{1}\\d{1,2}")) {
                        perspectiveTransformUry.setText(newValue.replaceAll("[a-zA-Z_]", ""));
                    }
                });
                
                break;
            }
            default: {
                Platform.runLater(()->{
                    createEffectStrings();
                });
                return;
            }
            
        }//end of switch
        
        Platform.runLater(()->{
            createEffectStrings();
            myDialog.setScene(myDialogScene);
            myDialog.show();
        });
    }
    
    public void createEffectStrings(){
        if(!EffectsModel.getInstance().getEffectsObjectList().isEmpty()){
                getEffectsStringList().clear();
                String tempString = "";
                for(Object effect : EffectsModel.getInstance().getEffectsObjectList()) {
                    if(effect instanceof Bloom){
                        tempString += "Bloom";
                        tempString += ",";
                        tempString += ((Bloom) effect).getThreshold();
                        effectsStringList.add(tempString);
                        tempString = "";
                    } else if(effect instanceof BoxBlur){
                        tempString += "BoxBlur";
                        tempString += ",";
                        tempString += ((BoxBlur) effect).getHeight();
                        tempString += ",";
                        tempString += ((BoxBlur) effect).getWidth();
                        tempString += ",";
                        tempString += ((BoxBlur) effect).getIterations();
                        effectsStringList.add(tempString);
                        tempString = "";
                    } else if(effect instanceof GaussianBlur){
                        tempString += "GaussianBlur";
                        tempString += ",";
                        tempString += ((GaussianBlur) effect).getRadius();
                        effectsStringList.add(tempString);
                        tempString = "";
                    } else if(effect instanceof PerspectiveTransform){
                        tempString += "PerspectiveTransform";
                        tempString += ",";
                        tempString += ((PerspectiveTransform) effect).getLlx();
                        tempString += ",";
                        tempString += ((PerspectiveTransform) effect).getLly();
                        tempString += ",";
                        tempString += ((PerspectiveTransform) effect).getLrx();
                        tempString += ",";
                        tempString += ((PerspectiveTransform) effect).getLry();
                        tempString += ",";
                        tempString += ((PerspectiveTransform) effect).getUlx();
                        tempString += ",";
                        tempString += ((PerspectiveTransform) effect).getUly();
                        tempString += ",";
                        tempString += ((PerspectiveTransform) effect).getUrx();
                        tempString += ",";
                        tempString += ((PerspectiveTransform) effect).getUry();
                        effectsStringList.add(tempString);
                        tempString = "";
                    } else if(effect instanceof InnerShadow){
                        tempString += "InnerShadow";
                        tempString += ",";
                        tempString += ((InnerShadow) effect).getChoke();
                        tempString += ",";
                        tempString += ((InnerShadow) effect).getColor();
                        tempString += ",";
                        tempString += ((InnerShadow) effect).getHeight();
                        tempString += ",";
                        tempString += ((InnerShadow) effect).getOffsetX();
                        tempString += ",";
                        tempString += ((InnerShadow) effect).getOffsetY();
                        tempString += ",";
                        tempString += ((InnerShadow) effect).getRadius();
                        tempString += ",";
                        tempString += ((InnerShadow) effect).getWidth();
                        effectsStringList.add(tempString);
                        tempString = "";
                    } else if(effect instanceof Reflection){
                        tempString += "Reflection";
                        tempString += ",";
                        tempString += ((Reflection) effect).getBottomOpacity();
                        tempString += ",";
                        tempString += ((Reflection) effect).getFraction();
                        tempString += ",";
                        tempString += ((Reflection) effect).getTopOffset();
                        tempString += ",";
                        tempString += ((Reflection) effect).getTopOpacity();
                        effectsStringList.add(tempString);
                        tempString = "";
                    } else if(effect instanceof DropShadow){
                        tempString += "DropShadow";
                        tempString += ",";
                        tempString += ((DropShadow) effect).getColor();
                        tempString += ",";
                        tempString += ((DropShadow) effect).getHeight();
                        tempString += ",";
                        tempString += ((DropShadow) effect).getOffsetX();
                        tempString += ",";
                        tempString += ((DropShadow) effect).getOffsetY();
                        tempString += ",";
                        tempString += ((DropShadow) effect).getRadius();
                        tempString += ",";
                        tempString += ((DropShadow) effect).getSpread();
                        tempString += ",";
                        tempString += ((DropShadow) effect).getWidth();
                        effectsStringList.add(tempString);
                        tempString = "";
                    }
                }  
            }
    }
    
    public void createEffectsFromList(List<String> effectList){
        getEffectsObjectList().clear();
        getEffectsAddedList().clear();
        
        if(!effectList.isEmpty()){
            for(String effect : effectList){
                List<String> tempList = Arrays.asList(effect.split("\\s*,\\s*"));

                switch(tempList.get(0)){

                    case "Bloom": {

                        Bloom bloom = new Bloom();
                        bloom.setThreshold( Double.parseDouble( tempList.get(1)) );

                        getEffectsObjectList().add(bloom);
                        getEffectsAddedList().add("Bloom");

                        break;
                    }
                    case "BoxBlur": {

                        BoxBlur boxBlur = new BoxBlur();
                        boxBlur.setHeight( Double.parseDouble( tempList.get(1)));
                        boxBlur.setWidth( Double.parseDouble( tempList.get(2)));
                        boxBlur.setIterations( Integer.parseInt( tempList.get(3)));

                        getEffectsObjectList().add(boxBlur);
                        getEffectsAddedList().add("Box Blur");

                        break;
                    }
                    case "GaussianBlur": {
                        GaussianBlur gausBlur = new GaussianBlur();
                        gausBlur.setRadius(Double.parseDouble( tempList.get(1)));

                        getEffectsObjectList().add(gausBlur);
                        getEffectsAddedList().add("Gaussian Blur");

                        break;
                    }
                    case "DropShadow": {

                        DropShadow dropShadow = new DropShadow();
                        dropShadow.setBlurType(BlurType.GAUSSIAN);
                        dropShadow.setColor(Color.web(tempList.get(1)));
                        dropShadow.setHeight(Double.parseDouble( tempList.get(2)));
                        dropShadow.setOffsetX(Double.parseDouble( tempList.get(3)));
                        dropShadow.setOffsetY(Double.parseDouble( tempList.get(4)));
                        dropShadow.setRadius(Double.parseDouble( tempList.get(5)));
                        dropShadow.setSpread(Double.parseDouble( tempList.get(6)));
                        dropShadow.setWidth(Double.parseDouble( tempList.get(7)));

                        getEffectsObjectList().add(dropShadow);
                        getEffectsAddedList().add("Drop Shadow");

                        break;
                    }
                    case "InnerShadow": {

                        InnerShadow innerShadow = new InnerShadow();
                        innerShadow.setBlurType(BlurType.GAUSSIAN);
                        innerShadow.setChoke(Double.parseDouble( tempList.get(1)));
                        innerShadow.setColor(Color.web(tempList.get(2)));
                        innerShadow.setHeight(Double.parseDouble( tempList.get(3)));
                        innerShadow.setOffsetX(Double.parseDouble( tempList.get(4)));
                        innerShadow.setOffsetY(Double.parseDouble( tempList.get(5)));
                        innerShadow.setRadius(Double.parseDouble( tempList.get(6)));
                        innerShadow.setWidth(Double.parseDouble( tempList.get(7)));

                        getEffectsObjectList().add(innerShadow);
                        getEffectsAddedList().add("Inner Shadow");

                        break;
                    }
                    case "Reflection": {

                        Reflection reflection = new Reflection();
                        reflection.setBottomOpacity(Double.parseDouble( tempList.get(1)));
                        reflection.setFraction(Double.parseDouble( tempList.get(2)));
                        reflection.setTopOffset(Double.parseDouble( tempList.get(3)));
                        reflection.setTopOpacity(Double.parseDouble( tempList.get(4)));

                        getEffectsObjectList().add(reflection);
                        getEffectsAddedList().add("Reflection");

                        break;
                    }
                    case "PerspectiveTransform": {

                        PerspectiveTransform perspectiveTransform = new PerspectiveTransform();
                        perspectiveTransform.setLlx(Double.parseDouble( tempList.get(1)));
                        perspectiveTransform.setLly(Double.parseDouble( tempList.get(2)));
                        perspectiveTransform.setLrx(Double.parseDouble( tempList.get(3)));
                        perspectiveTransform.setLry(Double.parseDouble( tempList.get(4)));
                        perspectiveTransform.setUlx(Double.parseDouble( tempList.get(5)));
                        perspectiveTransform.setUly(Double.parseDouble( tempList.get(6)));
                        perspectiveTransform.setUrx(Double.parseDouble( tempList.get(7)));
                        perspectiveTransform.setUry(Double.parseDouble( tempList.get(8)));

                        getEffectsObjectList().add(perspectiveTransform);
                        getEffectsAddedList().add("Perspective Transform");

                        break;
                    }
                }
            }    
        }
    }
    
    public void addEffectToSample(){
        
        if(!effectsObjectList.isEmpty()){
            if(effectsObjectList.size() == 1){
                EffectsModel.getInstance().sampleTextEffectProperty().set(effectsObjectList.get(0));
                
            } else {
                
                int i = 0;
                
                Object[] tempArray = effectsObjectList.toArray();
                
                //focus on the order
                for(Object effect : tempArray){                  
                    if( i+1 < tempArray.length && i != 0 ){
                        if(effect instanceof Bloom){
                            ((Bloom) effect).setInput((Effect)tempArray[i-1]);
                        } else if(effect instanceof BoxBlur){
                            ((BoxBlur) effect).setInput((Effect)tempArray[i-1]);    
                        } else if(effect instanceof GaussianBlur){
                            ((GaussianBlur) effect).setInput((Effect)tempArray[i-1]);  
                        } else if(effect instanceof PerspectiveTransform){
                            ((PerspectiveTransform) effect).setInput((Effect)tempArray[i-1]);
                        } else if(effect instanceof InnerShadow){
                            ((InnerShadow) effect).setInput((Effect)tempArray[i-1]); 
                        } else if(effect instanceof Reflection){
                            ((Reflection) effect).setInput((Effect)tempArray[i-1]);
                        } else if(effect instanceof DropShadow){
                            ((DropShadow) effect).setInput((Effect)tempArray[i-1]);
                        }
                    } else if( i+1 == tempArray.length ){
                        if(effect instanceof Bloom){
                            ((Bloom) effect).setInput((Effect)tempArray[i-1]);
                        } else if(effect instanceof BoxBlur){
                            ((BoxBlur) effect).setInput((Effect)tempArray[i-1]);
                        } else if(effect instanceof GaussianBlur){
                            ((GaussianBlur) effect).setInput((Effect)tempArray[i-1]);
                        } else if(effect instanceof PerspectiveTransform){
                            ((PerspectiveTransform) effect).setInput((Effect)tempArray[i-1]);
                        } else if(effect instanceof InnerShadow){
                            ((InnerShadow) effect).setInput((Effect)tempArray[i-1]);
                        } else if(effect instanceof Reflection){
                            ((Reflection) effect).setInput((Effect)tempArray[i-1]);
                        } else if(effect instanceof DropShadow) {
                            ((DropShadow) effect).setInput((Effect)tempArray[i-1]);
                        }
                        Effect tempEffect = (Effect)tempArray[i];
                        sampleTextEffectProperty().set(tempEffect);
                        i = 0;
                    }
                    i++;
                }
            }
        } else {
           EffectsModel.getInstance().sampleTextEffectProperty().set(null);
        }
    }
    
    public void addEffectToText(Text textObject){
        
        if(!effectsObjectList.isEmpty()){
            if(effectsObjectList.size() == 1){
                Effect tempEffect = effectsObjectList.get(0);
                textObject.setEffect(tempEffect);
                
            } else {
                
                int i = 0;
                
                Object[] tempArray = effectsObjectList.toArray();
                
                //focus on the order
                for(Object effect : tempArray){
                    if( i+1 < tempArray.length && i != 0 ){
                        if(effect instanceof Bloom){
                            ((Bloom) effect).setInput((Effect)tempArray[i-1]);
                        } else if(effect instanceof BoxBlur){
                            ((BoxBlur) effect).setInput((Effect)tempArray[i-1]);    
                        } else if(effect instanceof GaussianBlur){
                            ((GaussianBlur) effect).setInput((Effect)tempArray[i-1]);  
                        } else if(effect instanceof PerspectiveTransform){
                            ((PerspectiveTransform) effect).setInput((Effect)tempArray[i-1]);
                        } else if(effect instanceof InnerShadow){
                            ((InnerShadow) effect).setInput((Effect)tempArray[i-1]); 
                        } else if(effect instanceof Reflection){
                            ((Reflection) effect).setInput((Effect)tempArray[i-1]);
                        } else if(effect instanceof DropShadow){
                            ((DropShadow) effect).setInput((Effect)tempArray[i-1]);
                        }
                    } else if( i+1 == tempArray.length ){
                        if(effect instanceof Bloom){
                            ((Bloom) effect).setInput((Effect)tempArray[i-1]);
                        } else if(effect instanceof BoxBlur){
                            ((BoxBlur) effect).setInput((Effect)tempArray[i-1]);
                        } else if(effect instanceof GaussianBlur){
                            ((GaussianBlur) effect).setInput((Effect)tempArray[i-1]);
                        } else if(effect instanceof PerspectiveTransform){
                            ((PerspectiveTransform) effect).setInput((Effect)tempArray[i-1]);
                        } else if(effect instanceof InnerShadow){
                            ((InnerShadow) effect).setInput((Effect)tempArray[i-1]);
                        } else if(effect instanceof Reflection){
                            ((Reflection) effect).setInput((Effect)tempArray[i-1]);
                        } else if(effect instanceof DropShadow) {
                            ((DropShadow) effect).setInput((Effect)tempArray[i-1]);
                        }
                        Effect tempEffect = (Effect)tempArray[i];
                        textObject.setEffect(tempEffect);
                        i = 0;
                        getEffectsAddedList().clear();
                        getEffectsObjectList().clear();
                    }
                    i++;
                }
            }
        } else {
           EffectsModel.getInstance().sampleTextEffectProperty().set(null);
        }
        
    }             
}