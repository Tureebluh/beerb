/*************************************************************************************************************************
 * Source Code Form License Notice
 *************************************************************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 **************************************************************************************************************************/
/*************************************************************************************************************************
 *   Author: Jarek Thomas
 *
 *   Model for Audio Transitions
 *   Transition String Format:  Filepath - StartTime(ms) - StopTime(ms) - Playrate(x.x) - Volume(x.x) - SeekTime(ms)
 ***************************************************************************************************************************/

package main.models;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


public class AudioTransitionModel {
    private final static AudioTransitionModel INSTANCE = new AudioTransitionModel();
    public static final String TRANSITION_STRING = "_DEFAULT,0,*,1.0,1.0,0";
    public static final String TRANSITION_KEY = "Audio";
    

    private final SimpleDoubleProperty playRate = new SimpleDoubleProperty();
    private final SimpleDoubleProperty volume = new SimpleDoubleProperty();
    private final SimpleDoubleProperty seekTime = new SimpleDoubleProperty();
    
    private final ObservableMap<String, String> audioMarkersObMap;
    private final Map<String, String> audioMarkersHashMap = new HashMap<>();
    private String audioPath = "";
    
    private AudioTransitionModel(){
        this.audioMarkersObMap = FXCollections.observableMap(audioMarkersHashMap);
    }
    
    public static AudioTransitionModel getInstance(){
        return INSTANCE;
    }
    
    public final Map getAudioMarkersMap(){
        return this.audioMarkersHashMap;
    }
    
    public final ObservableMap getAudioMarkers(){
        return this.audioMarkersObMap;
    }
    
    public String getDefaultTransitionKey(){
        return (TRANSITION_KEY + "0" + getTransitionCounter());
    }
    
    public final int getTransitionCounter(){
        return getAudioMarkers().size();
    }
    
    public String getAudioPath(){
        return this.audioPath;
    }
    public void setAudioPath(String audioPath){
        this.audioPath = audioPath;
    }
    
    public final Double getPlayRate(){
        return this.playRate.getValue();
    }
    public final void setPlayRate(Double playRate){
        this.playRate.setValue(playRate);
    }
    public DoubleProperty playRateValueProperty(){
        return playRate;
    }
    
    public final Double getVolume(){
        return this.volume.getValue();
    }
    public final void setVolume(Double volume){
        this.volume.setValue(volume);
    }
    public DoubleProperty volumeValueProperty(){
        return volume;
    }
    
    public final Double getSeekTime(){
        return this.seekTime.getValue();
    }
    public final void setSeekTime(Double seekTime){
        this.seekTime.setValue(seekTime);
    }
    public DoubleProperty seekTimeValueProperty(){
        return seekTime;
    }
    
    public MediaPlayer createAudioObject(String audioPath){
        File temp = new File(audioPath);
        Media tempMedia = new Media(temp.toURI().toString());
        MediaPlayer tempMediaPlayer = new MediaPlayer(tempMedia);
        return tempMediaPlayer;
    }
    
    public void clearTransitionProperties(){
        this.playRate.setValue(1.0);
        this.volume.setValue(1.0);
        this.seekTime.setValue(0);
        this.audioPath = "";
    }
    
    public void createDefaultTransition(){
        String temp = getDefaultTransitionKey();
        ProfileModel.getInstance().getMarkerEventsList().add(temp);
        getAudioMarkers().put(temp, TRANSITION_STRING);
    }
    
    /*
    Transition String Format:  Filepath - StartTime(ms) - StopTime(ms) - Playrate(x.x) - Volume(x.x) - SeekTime(ms)
    */    
    
    public void editTransitionString(){
        
        String keyString = "";
        String valueString = "";

        keyString += PreviewModel.getInstance().getCurrentTransition();
        
        valueString += getAudioPath() + ",";
        
        valueString += PreviewModel.getInstance().getStartTimestamp() + ",";
        
        if(PreviewModel.getInstance().getStopTimestamp().equals("*") || PreviewModel.getInstance().getStopTimestamp().equals("0")){
            valueString += "*,";
        } else {
            valueString += PreviewModel.getInstance().getStopTimestamp() + ",";
        }
        
        valueString += getPlayRate() + ",";
        valueString += getVolume() + ",";
        valueString += getSeekTime();
        
        getAudioMarkers().put(keyString, valueString);
        
        Context.getInstance().setDidSave(false);
    }
    
    public void loadTransitionString(String keyString){
        
        String tempString = (String)getAudioMarkers().get(keyString);
        String[] tempArray = tempString.split(",");
        
        setAudioPath(tempArray[0]);
        setPlayRate(Double.parseDouble(tempArray[3]));
        setVolume(Double.parseDouble(tempArray[4]));
        setSeekTime(Double.parseDouble(tempArray[5]));

        if(tempArray[0].equals("_DEFAULT")){
            PreviewModel.getInstance().setStartTimestamp((int)PreviewModel.getInstance().getMediaPlayer().getCurrentTime().toMillis() + "");
        } else {
            PreviewModel.getInstance().setStartTimestamp(tempArray[1]);
        }
        
        PreviewModel.getInstance().setStopTimestamp(tempArray[2]);
    }
}
