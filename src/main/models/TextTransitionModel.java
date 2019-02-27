/*************************************************************************************************************************
 * Source Code Form License Notice
 *************************************************************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 **************************************************************************************************************************/
/*************************************************************************************************************************
 *   Author: Jarek Thomas
 *
 *   Model for Text Transitions
 * 
 *   Transition String Format:  TextProfile - FontSize - xPosition - yPosition - StartTime - StopTime - Message
 ***************************************************************************************************************************/

package main.models;

import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.control.ComboBox;


public class TextTransitionModel {
    private final static TextTransitionModel INSTANCE = new TextTransitionModel();
    public static final String TRANSITION_STRING = "_DEFAULT,60.0,485,300,0,*,Default Text";
    public static final String TRANSITION_KEY = "Text";
    
    private final SimpleStringProperty message = new SimpleStringProperty();
    private final SimpleIntegerProperty xPositionText = new SimpleIntegerProperty();
    private final SimpleIntegerProperty yPositionText = new SimpleIntegerProperty();
    private final SimpleDoubleProperty fontSize = new SimpleDoubleProperty();
    
    
    private final ObservableMap<String, String> textMarkersObMap;
    private final Map<String, String> textMarkersHashMap = new HashMap<>();
    private ComboBox textProfileCB;
    
    private TextTransitionModel(){
        this.textMarkersObMap = FXCollections.observableMap(textMarkersHashMap);
    }
    
    public static TextTransitionModel getInstance(){
        return INSTANCE;
    }
    
    public String getDefaultTransitionKey(){
        return (TRANSITION_KEY + "0" + getTransitionCounter());
    }
    
    public final Map getTextMarkersMap(){
        return this.textMarkersHashMap;
    }
    public final ObservableMap getTextMarkers(){
        return this.textMarkersObMap;
    }
    
    public final int getTransitionCounter(){
        return getTextMarkers().size();
    }
    
    public ComboBox getTextProfileCB(){
        return this.textProfileCB;
    }
    public void setTextProfileCB(ComboBox textProfileCB){
        this.textProfileCB = textProfileCB;
    }
    
    public final String getMessage(){
        return message.getValue();
    }
    public final void setMessage(String message){
        this.message.setValue(message); 
    }
    public StringProperty messageProperty(){
        return message;
    }
    
    public final Double getFontSize(){
        return fontSize.getValue();
    }
    public final void setFontSize(Double fontSize){
        this.fontSize.setValue(fontSize); 
    }
    public DoubleProperty fontSizeValueProperty(){
        return this.fontSize;
    }
    
    public final int getXPosition(){
        return xPositionText.getValue();
    }
    public final void setXPosition(int xPositionText){
        this.xPositionText.setValue(xPositionText); 
    }
    public IntegerProperty xPositionValueProperty(){
        return xPositionText;
    }
    
    public final int getYPosition(){
        return yPositionText.getValue();
    }
    public final void setYPosition(int yPositionText){
        this.yPositionText.setValue(yPositionText); 
    }
    public IntegerProperty yPositionValueProperty(){
        return yPositionText;
    }
    
    public void clearTransitionProperties(){
        setYPosition(0);
        setXPosition(0);
        setFontSize(60.0);
        setMessage("");
        if(this.textProfileCB != null){
            getTextProfileCB().setValue("");
        }
    }
    
    public void createDefaultTransition(){
        String temp = getDefaultTransitionKey();
        ProfileModel.getInstance().getMarkerEventsList().add(temp);
        getTextMarkers().put(temp, TRANSITION_STRING);
    }
    
    public void editTransitionString(){
        if(!textProfileCB.getValue().toString().isEmpty()){
            String keyString = "";
            String valueString = "";
            
            keyString += PreviewModel.getInstance().getCurrentTransition();
            valueString += textProfileCB.getValue() + ",";
            valueString += getFontSize() + ",";
            valueString += getXPosition() + ",";
            valueString += getYPosition() + ",";
            valueString += PreviewModel.getInstance().getStartTimestamp() + ",";
            if(PreviewModel.getInstance().getStopTimestamp().equals("*") || PreviewModel.getInstance().getStopTimestamp().equals("0")){
                valueString += "*,";
            } else {
                valueString += PreviewModel.getInstance().getStopTimestamp() + ",";
            }
            valueString += getMessage();
            
            getTextMarkers().put(keyString, valueString);
            Context.getInstance().setDidSave(false);
        }
    }
    
    public void loadTransitionString(String keyString){
        String tempString = (String)getTextMarkers().get(keyString);
        String[] tempArray = tempString.split(",");
        PreviewModel.getInstance().setTextProfileCBValue(tempArray[0]);
        setFontSize(Double.parseDouble(tempArray[1]));
        setXPosition(Integer.parseInt(tempArray[2])); 
        setYPosition(Integer.parseInt(tempArray[3]));

        if(tempArray[0].equals("_DEFAULT")){
            PreviewModel.getInstance().setStartTimestamp((int)PreviewModel.getInstance().getMediaPlayer().getCurrentTime().toMillis() + "");
        } else {
            PreviewModel.getInstance().setStartTimestamp(tempArray[4]);
        }

        PreviewModel.getInstance().setStopTimestamp(tempArray[5]);     

        String message = "";

        for(int i = 6; i < tempArray.length; i++){
            if(i != 6){
                message += ",";
            }
            message += tempArray[i];
        }
        setMessage(message);
    }
}
