/*************************************************************************************************************************
 * Source Code Form License Notice
 *************************************************************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 **************************************************************************************************************************/
/*************************************************************************************************************************
 *   Author: Jarek Thomas
 *
 *   Controller for Text Modal that pops up when user creates/edits a Text Profile
 **************************************************************************************************************************/

package main.controllers;

import main.models.EffectsModel;
import main.models.TextModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import main.models.ProfileModel;


public class TextController implements Initializable {
    
            private Text textSample;
            private FontWeight weight;
            private ObservableList<String> fontFamilyObList, fontWeightObList, effectsObList;
            
    @FXML   private Pane textSamplePane;
    @FXML   private ColorPicker textColorPicker;
    @FXML   private ComboBox fontFamilyCB, fontWeightCB, effectsCB;
    @FXML   private ListView effectsListView;
    @FXML   private Button saveTextBtn;
    @FXML   private TextField textProfileName;
    
    //Change font color of text sample
    @FXML
    public void changeFontColor(ActionEvent event){
        Platform.runLater(()->{
            textSample.setFill(Color.web( textColorPicker.getValue().toString().substring(2, 8) ));
        });
    }
    
    //Change font family of text sample
    @FXML
    public void changeFontFamily(ActionEvent event){
        Platform.runLater(()->{
            textSample.setFont( Font.font( fontFamilyCB.getValue().toString(), weight, 60.0 ) );
        });
    }
    
    //Change font weight of text sample
    @FXML
    public void changeFontWeight(ActionEvent event){
        weight = TextModel.getInstance().getFontWeight(fontWeightCB.getValue().toString());
        Platform.runLater(()->{
            textSample.setFont( Font.font( fontFamilyCB.getValue().toString(), weight, 60.0) );
        });
    }
    
    //Open effects window
    @FXML
    public void effectOptionsPopup(ActionEvent event){
        Platform.runLater(()->{
            EffectsModel.getInstance().openEffectParameters();
        });
    }
    
    //Remove effect from sample
    @FXML
    public void removeEffect(ActionEvent event){
        if(!EffectsModel.getInstance().getEffectsAddedList().isEmpty()){
            EffectsModel.getInstance().getEffectsStringList().remove(effectsListView.getSelectionModel().getSelectedIndex());
            Platform.runLater(()->{
                EffectsModel.getInstance().createEffectsFromList(EffectsModel.getInstance().getEffectsStringList());
                EffectsModel.getInstance().addEffectToSample();
            });
        }
    }
    
    //Save text profile permamently in XML
    @FXML
    public void saveText(ActionEvent event){
        Platform.runLater(()->{
            if(!textProfileName.getText().equals("")){
                TextModel.getInstance().saveTextToXML();
                if(!ProfileModel.getInstance().getTextProfileList().contains(textProfileName.getText().replace(" ", "_"))){
                    ProfileModel.getInstance().getTextProfileList().add(textProfileName.getText().replace(" ", "_"));
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Oops!");
                alert.setHeaderText("No text name entered.");
                alert.setContentText("Text name is required to save your file.");
                alert.showAndWait();
            }
        });
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        textColorPicker.setTooltip(new Tooltip("Font Color "));
        fontFamilyCB.setTooltip(new Tooltip("Font Family "));
        fontWeightCB.setTooltip(new Tooltip("Font Weight "));
        effectsCB.setTooltip(new Tooltip("Text Effects "));
        saveTextBtn.setTooltip(new Tooltip("Save Text File"));
        
        saveTextBtn.getStylesheets().add("main/stylesheets/btnStyles.css");
        
        fontFamilyObList = FXCollections.observableArrayList();
        fontFamilyObList.addAll(Font.getFamilies());
        fontFamilyCB.setItems(fontFamilyObList);
        
        fontWeightObList = FXCollections.observableArrayList("Thin", "Extra Light", "Light", "Normal", "Medium",
                                                        "Semi Bold", "Bold", "Extra Bold", "Black");
        fontWeightCB.setItems(fontWeightObList);
        
        effectsObList = FXCollections.observableArrayList( "Bloom", "Box Blur", "Gaussian Blur", "Drop Shadow", "Inner Shadow", "Reflection" );
        effectsCB.setItems(effectsObList);
        effectsListView.setPlaceholder(new Label("No effects added."));
        effectsListView.setItems(EffectsModel.getInstance().getEffectsAddedList());
        
        textSample = new Text("Sample");
        textSample.setFill(Color.WHITE);
        textSample.setTextAlignment(TextAlignment.CENTER);
        textSample.setFontSmoothingType(FontSmoothingType.LCD);        
        weight = FontWeight.NORMAL;
        textSample.setFont( Font.font( fontFamilyCB.getValue().toString(), weight, 60.0) );
        textSamplePane.getChildren().addAll(textSample);
        
        Bindings.bindBidirectional( textProfileName.textProperty(), TextModel.getInstance().textNameTextProperty());
        Bindings.bindBidirectional( effectsCB.valueProperty() , EffectsModel.getInstance().effectsCBValueProperty());
        Bindings.bindBidirectional( textColorPicker.valueProperty(), TextModel.getInstance().fontColorProperty());
        Bindings.bindBidirectional( fontWeightCB.valueProperty(), TextModel.getInstance().fontWeightProperty());
        Bindings.bindBidirectional( fontFamilyCB.valueProperty(), TextModel.getInstance().fontFamilyProperty());
        Bindings.bindBidirectional( textSample.fontProperty(), TextModel.getInstance().sampleFontProperty());
        Bindings.bindBidirectional( textSample.fillProperty(), TextModel.getInstance().sampleTextFillProperty());
        Bindings.bindBidirectional( textSample.effectProperty(), EffectsModel.getInstance().sampleTextEffectProperty());
        
        textColorPicker.requestFocus();
    }    
    
}
