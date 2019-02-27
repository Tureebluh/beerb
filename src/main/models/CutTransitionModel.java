/*************************************************************************************************************************
 * Source Code Form License Notice
 *************************************************************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 **************************************************************************************************************************/
/*************************************************************************************************************************
 *   Author: Jarek Thomas
 *
 *   Model for Cut Transitions
 * 
 *   Transition String Format:  StartTime - StopTime - RewindBoolean
 *   If RewindBoolean is true, it means the user performed a cut to a point before the startTime
 ***************************************************************************************************************************/

package main.models;

import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;


public class CutTransitionModel {
    private final static CutTransitionModel INSTANCE = new CutTransitionModel();
    public static final String TRANSITION_STRING = "_DEFAULT,*,false";
    public static final String TRANSITION_KEY = "Cut";
    
    private final ObservableMap<String, String> cutMarkersObMap;
    private final Map<String, String> cutMarkersHashMap = new HashMap<>();
    
    private CutTransitionModel(){
        this.cutMarkersObMap = FXCollections.observableMap(cutMarkersHashMap);
    }
    
    public static CutTransitionModel getInstance(){
        return INSTANCE;
    }
    
    public final Map getCutMarkersMap(){
        return this.cutMarkersHashMap;
    }
    
    public final ObservableMap getCutMarkers(){
        return this.cutMarkersObMap;
    }
    
    public String getDefaultTransitionKey(){
        return (TRANSITION_KEY + (getTransitionCounter() + 1));
    }
    
    public final int getTransitionCounter(){
        return getCutMarkers().size();
    }
    
    public void createDefaultTransition(){
        String temp = getDefaultTransitionKey();
        ProfileModel.getInstance().getMarkerEventsList().add(temp);
        getCutMarkers().put(temp, TRANSITION_STRING);
    }
    
    public void editTransitionString(){
        
        String keyString = "";
        String valueString = "";

        keyString += PreviewModel.getInstance().getCurrentTransition();
        
        valueString += PreviewModel.getInstance().getStartTimestamp() + ",";
        
        if(PreviewModel.getInstance().getStopTimestamp().equals("*") || PreviewModel.getInstance().getStopTimestamp().equals("0")){
            valueString += (Integer.parseInt(PreviewModel.getInstance().getStartTimestamp()) + 10 ) + ",";
        } else {
            valueString += PreviewModel.getInstance().getStopTimestamp() + ",";
        }
        
        valueString += PreviewModel.getInstance().getRewindCut();
        getCutMarkers().put(keyString, valueString);
        
        Context.getInstance().setDidSave(false);
    }
    
    public void loadTransitionString(String keyString){
        
        String tempString = (String)getCutMarkers().get(keyString);
        String[] tempArray = tempString.split(",");

        if(tempArray[0].equals("_DEFAULT")){
            PreviewModel.getInstance().setStartTimestamp((int)PreviewModel.getInstance().getMediaPlayer().getCurrentTime().toMillis() + "");
        } else {
            PreviewModel.getInstance().setStartTimestamp(tempArray[0]);
        }
        
        PreviewModel.getInstance().setStopTimestamp(tempArray[1]);
        PreviewModel.getInstance().setRewindCut(Boolean.parseBoolean(tempArray[2]));
    }
    
}
