/*************************************************************************************************************************
 * Source Code Form License Notice
 *************************************************************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 **************************************************************************************************************************/
/*************************************************************************************************************************
 *   Author: Jarek Thomas
 *
 *   Model for Image Transitions
 * 
 *   Transition String Format:  FilePath - xPosition - yPosition - FitWidth - FitHeight - StartTime - StopTime - AspectRatioBoolean
 ***************************************************************************************************************************/

package main.models;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageTransitionModel {
    private final static ImageTransitionModel INSTANCE = new ImageTransitionModel();
    public static final String TRANSITION_STRING = "_DEFAULT,540,260,200,200,0,*,true";
    public static final String TRANSITION_KEY = "Image";
    
    private final SimpleStringProperty fitWidth = new SimpleStringProperty();
    private final SimpleStringProperty fitHeight = new SimpleStringProperty();
    private final SimpleIntegerProperty xPosition = new SimpleIntegerProperty();
    private final SimpleIntegerProperty yPosition = new SimpleIntegerProperty();
    
    private String imagePath = "";
    private final ObservableMap<String, String> imageMarkersObMap;
    private final Map<String, String> imageMarkersHashMap = new HashMap<>();
    
    private ImageTransitionModel() {
        this.imageMarkersObMap = FXCollections.observableMap(imageMarkersHashMap);
    }
    
    public static ImageTransitionModel getInstance(){
        return INSTANCE;
    }
    
    public String getDefaultTransitionKey(){
        return (TRANSITION_KEY + "0" + getTransitionCounter());
    }
    
    public final Map getImageMarkersMap(){
        return this.imageMarkersHashMap;
    }
    public final ObservableMap getImageMarkers(){
        return this.imageMarkersObMap;
    }
    
    public final int getTransitionCounter(){
        return getImageMarkers().size();
    }
    
    public String getImagePath(){
        return this.imagePath;
    }
    public void setImagePath(String imagePath){
        this.imagePath = imagePath;
    }
    
    public final int getXPosition(){
        return xPosition.getValue();
    }
    public final void setXPosition(int xPositionText){
        this.xPosition.setValue(xPositionText); 
    }
    public IntegerProperty xPositionValueProperty(){
        return xPosition;
    }
    
    public final int getYPosition(){
        return yPosition.getValue();
    }
    public final void setYPosition(int yPositionText){
        this.yPosition.setValue(yPositionText); 
    }
    public IntegerProperty yPositionValueProperty(){
        return yPosition;
    }
    
    public final String getFitWidth(){
        return fitWidth.getValue();
    }
    public final void setFitWidth(String fitWidth){
        this.fitWidth.setValue(fitWidth); 
    }
    public StringProperty fitWidthValueProperty(){
        return fitWidth;
    }
    
    public final String getFitHeight(){
        return fitHeight.getValue();
    }
    public final void setFitHeight(String fitHeight){
        this.fitHeight.setValue(fitHeight); 
    }
    public StringProperty fitHeightValueProperty(){
        return fitHeight;
    }
    
    public void createDefaultTransition(){
        String temp = getDefaultTransitionKey();
        ProfileModel.getInstance().getMarkerEventsList().add(temp);
        getImageMarkers().put(temp, TRANSITION_STRING);
    }
    
    public ImageView createImageObject(String imagePath){
        File temp = new File(imagePath);
        Image tempImage = new Image(temp.toURI().toString());
        ImageView tempImageView = new ImageView(tempImage);
        tempImageView.scaleZProperty().setValue(10);
        return tempImageView;
    }
    
    public void clearTransitionProperties(){
        setYPosition(0);
        setXPosition(0);
        setFitWidth("0");
        setFitHeight("0");
        setImagePath("");
    }
    
    public void editTransitionString(){
        String keyString = "";
        String valueString = "";
        
        keyString += PreviewModel.getInstance().getCurrentTransition();
        valueString += getImagePath() + ",";
        valueString += getXPosition() + ",";
        valueString += getYPosition() + ",";
        valueString += getFitWidth() + ",";
        valueString += getFitHeight() + ",";
        valueString += PreviewModel.getInstance().getStartTimestamp() + ",";
        if(PreviewModel.getInstance().getStopTimestamp().equals("*") || PreviewModel.getInstance().getStopTimestamp().equals("0")){
            valueString += "*,";
        } else {
            valueString += PreviewModel.getInstance().getStopTimestamp() + ",";
        }
        valueString += PreviewModel.getInstance().getImageLinkBtnActive();
        
        getImageMarkers().put(keyString, valueString);
        Context.getInstance().setDidSave(false);
    }
    
    public void loadTransitionString(String keyString){
        String tempString = (String)getImageMarkers().get(keyString);
        String[] tempArray = tempString.split(",");
        setImagePath(tempArray[0]);
        setXPosition(Integer.parseInt(tempArray[1])); 
        setYPosition(Integer.parseInt(tempArray[2]));
        setFitWidth(tempArray[3]);
        setFitHeight(tempArray[4]);

        if(tempArray[0].equals("_DEFAULT")){
            PreviewModel.getInstance().setStartTimestamp((int)PreviewModel.getInstance().getMediaPlayer().getCurrentTime().toMillis() + "");
        } else {
            PreviewModel.getInstance().setStartTimestamp(tempArray[5]);
        }
        PreviewModel.getInstance().setStopTimestamp(tempArray[6]);
        PreviewModel.getInstance().setImageLinkBtnActive(Boolean.parseBoolean(tempArray[7]));
    }
}
