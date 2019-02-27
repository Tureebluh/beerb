/*************************************************************************************************************************
 * Source Code Form License Notice
 *************************************************************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 **************************************************************************************************************************/
/*************************************************************************************************************************
 *   Author: Jarek Thomas
 *
 *   Model for Text Profiles
 * 
 *   Handles loading and saving XML data
 ***************************************************************************************************************************/

package main.models;

import java.io.File;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class TextModel {
    private final static TextModel INSTANCE = new TextModel();
    
    private final SimpleStringProperty textName = new SimpleStringProperty();
    private final ObjectProperty<Color> fontColor = new SimpleObjectProperty<>();
    private final SimpleStringProperty fontWeight = new SimpleStringProperty();
    private final SimpleStringProperty fontFamily = new SimpleStringProperty();
    private final ObjectProperty<Font> sampleFont = new SimpleObjectProperty<>();
    private final ObjectProperty<Paint> sampleTextFill = new SimpleObjectProperty<>();
    
    
    private TextModel(){
        
    }
    
    public static TextModel getInstance(){
        return INSTANCE;
    }
    
    public final String getTextName(){
        return textName.getValue();
    }
    public final void setTextName(String textProfileName){
        this.textName.setValue(textProfileName); 
    }
    public StringProperty textNameTextProperty(){
        return textName;
    }
    
    public final Color getFontColor(){
        return fontColor.getValue();
    }
    public final void setFontColor(Color fontColor){
        this.fontColor.setValue(fontColor); 
    }
    public ObjectProperty<Color> fontColorProperty(){
        return fontColor;
    }
    
    public final String getFontWeight(){
        return fontWeight.getValue();
    }
    public final void setFontWeight(String fontWeight){
        this.fontWeight.setValue(fontWeight); 
    }
    public StringProperty fontWeightProperty(){
        return fontWeight;
    }
    
    public final String getFontFamily(){
        return fontFamily.getValue();
    }
    public final void setFontFamily(String fontFamily){
        this.fontFamily.setValue(fontFamily); 
    }
    public StringProperty fontFamilyProperty(){
        return fontFamily;
    }
    
    public final Font getSampleFont(){
        return sampleFont.getValue();
    }
    public final void setSampleFont(Font sampleFont){
        this.sampleFont.setValue(sampleFont); 
    }
    public ObjectProperty<Font> sampleFontProperty(){
        return sampleFont;
    }
    
    public final Paint getSampleTextFill(){
        return sampleTextFill.getValue();
    }
    public final void setSampleTextFill(Color sampleTextFill){
        this.sampleTextFill.setValue(sampleTextFill); 
    }
    public ObjectProperty<Paint> sampleTextFillProperty(){
        return sampleTextFill;
    }
    
    public FontWeight getFontWeight(String selection){
        
        FontWeight weight = FontWeight.NORMAL;
        
        switch( selection ){
                case "Thin": {
                    weight = FontWeight.THIN;
                    break;
                }
                case "Extra Light": {
                    weight = FontWeight.EXTRA_LIGHT;
                    break;
                }
                case "Light": {
                    weight = FontWeight.LIGHT;
                    break;
                }
                case "Normal": {
                    weight = FontWeight.NORMAL;
                    break;
                }
                case "Medium": {
                    weight = FontWeight.MEDIUM;
                    break;
                }
                case "Semi Bold": {
                    weight = FontWeight.SEMI_BOLD;
                    break;
                }
                case "Bold": {
                    weight = FontWeight.BOLD;
                    break;
                }
                case "Extra Bold": {
                    weight = FontWeight.EXTRA_BOLD;
                    break;
                }
                case "Black": {
                    weight = FontWeight.BLACK;
                    break;
                }
            }
        
        return weight;
    }
    
    public void clearTextSceneProperties(){
        Font font = Font.font("System", FontWeight.NORMAL, 60.0);
        fontColorProperty().set(Color.BLACK);
        fontWeightProperty().set("Normal");
        fontFamilyProperty().set("System");
        sampleFontProperty().set( font );
        sampleTextFillProperty().set(Color.BLACK);
        EffectsModel.getInstance().sampleTextEffectProperty().set(null);        
        EffectsModel.getInstance().getEffectsAddedList().clear();
        EffectsModel.getInstance().getEffectsObjectList().clear();
        EffectsModel.getInstance().getEffectsStringList().clear();
        textNameTextProperty().set("");
    }
    
    public void saveTextToXML(){
        try {
            JAXBContext context = JAXBContext.newInstance(TextWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            File xmlTextProfile = new File("Text//" + textName.getValue().replaceAll(" ", "_") + ".xml");
            TextWrapper wrapper = new TextWrapper();
            
            wrapper.setFontColor( fontColor.getValue().toString().substring(2, 8) );
            wrapper.setFontWeight( fontWeight.getValue() );
            wrapper.setFontFamily( fontFamily.getValue() );

            EffectsModel.getInstance().createEffectStrings();
            wrapper.setEffects(EffectsModel.getInstance().getEffectsStringList());
            
            m.marshal(wrapper, xmlTextProfile);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success!");
            alert.setHeaderText("Text saved.");
            alert.setContentText(xmlTextProfile.getName() + " successfully created.");
            alert.setGraphic( new ImageView( "main/images/BeeLogo.png" ) );
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add("main/stylesheets/dialog.css");
            dialogPane.getStyleClass().add("success");
            alert.show();
            
        } catch(Exception e){
                
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Woah - Something went wrong.");
            alert.setContentText("An error occured while trying to save your text profile.");
            alert.showAndWait();
        }
    }
    
    public void loadTextFromXML(String textName){
        clearTextSceneProperties();
        try {
            JAXBContext context = JAXBContext.newInstance(TextWrapper.class);
            Unmarshaller u = context.createUnmarshaller();
            TextWrapper wrapper = (TextWrapper) u.unmarshal(new File("Text//" + textName + ".xml"));
            Color color = Color.web(wrapper.getFontColor());
            fontColorProperty().set(color);
            sampleTextFillProperty().set(color);
            fontWeightProperty().set(wrapper.getFontWeight());
            fontFamilyProperty().set(wrapper.getFontFamily());
            FontWeight weight = getFontWeight( wrapper.getFontWeight() );
            Font font = Font.font(wrapper.getFontFamily(), weight, TextTransitionModel.getInstance().getFontSize());
            sampleFontProperty().set( font );
            EffectsModel.getInstance().createEffectsFromList(wrapper.getEffects());
            EffectsModel.getInstance().getEffectsStringList().addAll(wrapper.getEffects());
            EffectsModel.getInstance().addEffectToSample();
            textNameTextProperty().set(textName);
        } catch(Exception e) {
            e.printStackTrace();
        }    
    }
    
    public Text createTextObject(String textName, Double fontSize){
        Text temp = new Text();
        temp.setFontSmoothingType(FontSmoothingType.LCD);
        temp.scaleZProperty().setValue(0);
        
        if(!textName.equals("_DEFAULT")){
            try {
                JAXBContext context = JAXBContext.newInstance(TextWrapper.class);
                Unmarshaller u = context.createUnmarshaller();
                TextWrapper wrapper = (TextWrapper) u.unmarshal(new File("Text//" + textName + ".xml"));

                Color color = Color.web(wrapper.getFontColor());
                temp.setFill(color);

                FontWeight weight = getFontWeight( wrapper.getFontWeight() );  
                Font font = Font.font(wrapper.getFontFamily(), weight, fontSize);
                temp.setFont(font);

                EffectsModel.getInstance().createEffectsFromList(wrapper.getEffects());
                EffectsModel.getInstance().addEffectToText(temp);
            } catch(Exception e){
                e.printStackTrace();
            }
        } else {
            Color color = Color.web("FFFFFF");
            temp.setFill(color);
            FontWeight weight = FontWeight.NORMAL;
            Font font = Font.font("System", weight, 60.0);
            temp.setFont(font);
        }
        return temp;
    }
    
}//end of class
