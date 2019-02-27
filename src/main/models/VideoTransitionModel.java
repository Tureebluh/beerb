/*************************************************************************************************************************
 * Source Code Form License Notice
 *************************************************************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 **************************************************************************************************************************/
/*************************************************************************************************************************
 *   Author: Jarek Thomas
 *
 *   Model for Video Transitions
 * 
 *   Transition String Format:  VideoPath - StartTime - StopTime - PlayRate - Volume - xPosition - yPosition - Width - Height - SeekTime - AspectRatioBoolean
 ***************************************************************************************************************************/

package main.models;

import java.io.File;
import java.text.DecimalFormat;
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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


public class VideoTransitionModel {
    private final static VideoTransitionModel INSTANCE = new VideoTransitionModel();
    public static final String TRANSITION_STRING = "_DEFAULT,0,*,1.0,1.0,540,300,200,200,0,true";
    public static final String TRANSITION_KEY = "Video";
    

    private final SimpleDoubleProperty playRate = new SimpleDoubleProperty();
    private final SimpleDoubleProperty volume = new SimpleDoubleProperty();
    private final SimpleDoubleProperty seekTime = new SimpleDoubleProperty();
    private final SimpleStringProperty fitWidth = new SimpleStringProperty();
    private final SimpleStringProperty fitHeight = new SimpleStringProperty();
    private final SimpleIntegerProperty xPosition = new SimpleIntegerProperty();
    private final SimpleIntegerProperty yPosition = new SimpleIntegerProperty();
    
    private final ObservableMap<String, String> videoMarkersObMap;
    private final Map<String, String> videoMarkersHashMap = new HashMap<>();
    private String videoPath = "";
    
    private DecimalFormat df = new DecimalFormat("#.00");
    
    
    private VideoTransitionModel(){
        this.videoMarkersObMap = FXCollections.observableMap(videoMarkersHashMap);
    }
    
    public static VideoTransitionModel getInstance(){
        return INSTANCE;
    }
    
    public final Map getVideoMarkersMap(){
        return this.videoMarkersHashMap;
    }
    
    public final ObservableMap getVideoMarkers(){
        return this.videoMarkersObMap;
    }
    
    public String getDefaultTransitionKey(){
        return (TRANSITION_KEY + "0" + getTransitionCounter());
    }
    
    public final int getTransitionCounter(){
        return getVideoMarkers().size();
    }
    
    public String getVideoPath(){
        return this.videoPath;
    }
    public void setVideoPath(String videoPath){
        this.videoPath = videoPath;
    }
    
    public final Double getPlayRate(){
        return this.playRate.getValue();
    }
    public final void setPlayRate(Double playRate){
        this.playRate.setValue(Double.parseDouble(df.format(playRate)));
    }
    public DoubleProperty playRateValueProperty(){
        return playRate;
    }
    
    public final Double getVolume(){
        return this.volume.getValue();
    }
    public final void setVolume(Double volume){
        this.volume.setValue(Double.parseDouble(df.format(volume)));
    }
    public DoubleProperty volumeValueProperty(){
        return volume;
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
    
    public final Double getSeekTime(){
        return this.seekTime.getValue();
    }
    public final void setSeekTime(Double seekTime){
        this.seekTime.setValue(Double.parseDouble(df.format(seekTime)));
    }
    public DoubleProperty seekTimeValueProperty(){
        return seekTime;
    }
    
    public MediaPlayer createVideoObject(String videoPath){
        File temp = new File(videoPath);
        Media tempMedia = new Media(temp.toURI().toString());
        MediaPlayer tempMediaPlayer = new MediaPlayer(tempMedia);
        return tempMediaPlayer;
    }
    
    public void clearTransitionProperties(){
        setYPosition(0);
        setXPosition(0);
        setFitWidth("0");
        setFitHeight("0");
        this.playRate.setValue(1.0);
        this.volume.setValue(1.0);
        this.seekTime.setValue(0);
        this.videoPath = "";
    }
    
    public void createDefaultTransition(){
        String temp = getDefaultTransitionKey();
        ProfileModel.getInstance().getMarkerEventsList().add(temp);
        getVideoMarkers().put(temp, TRANSITION_STRING);
    }
    
    public void editTransitionString(){
        
        String keyString = "";
        String valueString = "";

        keyString += PreviewModel.getInstance().getCurrentTransition();
        
        valueString += getVideoPath() + ",";
        
        valueString += PreviewModel.getInstance().getStartTimestamp() + ",";
        
        if(PreviewModel.getInstance().getStopTimestamp().equals("*") || PreviewModel.getInstance().getStopTimestamp().equals("0")){
            valueString += "*,";
        } else {
            valueString += PreviewModel.getInstance().getStopTimestamp() + ",";
        }
        
        valueString += getPlayRate() + ",";
        valueString += getVolume() + ",";
        valueString += getXPosition() + ",";
        valueString += getYPosition() + ",";
        valueString += getFitWidth() + ",";
        valueString += getFitHeight() + ",";
        valueString += getSeekTime() + ",";
        valueString += PreviewModel.getInstance().getVideoLinkBtnActive();
        
        getVideoMarkers().put(keyString, valueString);
        
        Context.getInstance().setDidSave(false);
    }
    
    public void loadTransitionString(String keyString){
        
        String tempString = (String)getVideoMarkers().get(keyString);
        String[] tempArray = tempString.split(",");
        
        setVideoPath(tempArray[0]);
        setPlayRate(Double.parseDouble(tempArray[3]));
        setVolume(Double.parseDouble(tempArray[4]));
        setXPosition(Integer.parseInt(tempArray[5])); 
        setYPosition(Integer.parseInt(tempArray[6]));
        setFitWidth(tempArray[7]);
        setFitHeight(tempArray[8]);
        setSeekTime(Double.parseDouble(tempArray[9]));
        PreviewModel.getInstance().setVideoLinkBtnActive(Boolean.parseBoolean(tempArray[10]));

        if(tempArray[0].equals("_DEFAULT")){
            PreviewModel.getInstance().setStartTimestamp((int)PreviewModel.getInstance().getMediaPlayer().getCurrentTime().toMillis() + "");
        } else {
            PreviewModel.getInstance().setStartTimestamp(tempArray[1]);
        }
        
        PreviewModel.getInstance().setStopTimestamp(tempArray[2]);
    }
}
