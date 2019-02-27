/*************************************************************************************************************************
 * Source Code Form License Notice
 *************************************************************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 **************************************************************************************************************************/
/*************************************************************************************************************************
 *   Author: Jarek Thomas
 *
 *   Model for Fast-forward Transitions
 * 
 *   Transition String Format:  StartTime - StopTime - StartRate(x.x) - StopRate(x.x)
 ***************************************************************************************************************************/

package main.models;

import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class FFTransitionModel {
    private final static FFTransitionModel INSTANCE = new FFTransitionModel();
    public static final String TRANSITION_STRING = "_DEFAULT,*,2.0,1.0";
    public static final String TRANSITION_KEY = "FF";
    
    private final SimpleDoubleProperty startRate = new SimpleDoubleProperty();
    private final SimpleDoubleProperty stopRate = new SimpleDoubleProperty();
    
    private final ObservableMap<String, String> ffMarkersObMap;
    private final Map<String, String> ffMarkersHashMap = new HashMap<>();
    
    private FFTransitionModel(){
        this.ffMarkersObMap = FXCollections.observableMap(ffMarkersHashMap);
    }
    
    public static FFTransitionModel getInstance(){
        return INSTANCE;
    }
    
    public final Map getFFMarkersMap(){
        return this.ffMarkersHashMap;
    }
    
    public final ObservableMap getFFMarkers(){
        return this.ffMarkersObMap;
    }
    
    public String getDefaultTransitionKey(){
        return (TRANSITION_KEY + (getTransitionCounter() + 1));
    }
    
    public final int getTransitionCounter(){
        return getFFMarkers().size();
    }
    
    public final Double getStartRate(){
        return this.startRate.getValue();
    }
    public final void setStartRate(Double startRate){
        this.startRate.setValue(startRate);
    }
    public DoubleProperty startRateValueProperty(){
        return startRate;
    }
    
    public final Double getStopRate(){
        return this.stopRate.getValue();
    }
    public final void setStopRate(Double stopRate){
        this.stopRate.setValue(stopRate);
    }
    public DoubleProperty stopRateValueProperty(){
        return stopRate;
    }
    
    public void clearTransitionProperties(){
        this.startRate.setValue(2.0);
        this.stopRate.setValue(1.0);
    }
    
    public void createDefaultTransition(){
        String temp = getDefaultTransitionKey();
        ProfileModel.getInstance().getMarkerEventsList().add(temp);
        getFFMarkers().put(temp, TRANSITION_STRING);
    }
    
    public void editTransitionString(){
        
        String keyString = "";
        String valueString = "";

        keyString += PreviewModel.getInstance().getCurrentTransition();
        
        valueString += PreviewModel.getInstance().getStartTimestamp() + ",";
        
        if(PreviewModel.getInstance().getStopTimestamp().equals("*") || PreviewModel.getInstance().getStopTimestamp().equals("0")){
            valueString += "*,";
        } else {
            valueString += PreviewModel.getInstance().getStopTimestamp() + ",";
        }
        
        valueString +=  getStartRate() + ",";
        valueString +=  getStopRate();
        
        getFFMarkers().put(keyString, valueString);
        
        Context.getInstance().setDidSave(false);
    }
    
    public void loadTransitionString(String keyString){
        
        String tempString = (String)getFFMarkers().get(keyString);
        String[] tempArray = tempString.split(",");
        
        setStartRate(Double.parseDouble(tempArray[2]));
        setStopRate(Double.parseDouble(tempArray[3]));

        if(tempArray[0].equals("_DEFAULT")){
            PreviewModel.getInstance().setStartTimestamp((int)PreviewModel.getInstance().getMediaPlayer().getCurrentTime().toMillis() + "");
        } else {
            PreviewModel.getInstance().setStartTimestamp(tempArray[0]);
        }
        
        PreviewModel.getInstance().setStopTimestamp(tempArray[1]);
    }
    
}
