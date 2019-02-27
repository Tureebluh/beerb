/*************************************************************************************************************************
 * Source Code Form License Notice
 *************************************************************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 **************************************************************************************************************************/
/*************************************************************************************************************************
 *   Author: Jarek Thomas
 *
 *   Handles the Global Scope for application wide variables
 ***************************************************************************************************************************/

package main.models;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.controllers.SceneController;
import main.controllers.TheatreController;

public class Context {
    
    private final static Context INSTANCE = new Context();
    private SceneController controller;
    private TheatreController theatreController;
    private Stage controlPanel;
    private Scene launcherScene;
    private boolean isRunning = false, comboPressed = false, didSave = true, editViewOpened = false, sceneLoading = false;
    private ArrayList userInput;
    private double heightRatio = 1.0, widthRatio = 1.0;
    private double letterBoxingCurrent = 0.0, letterBoxingPrevious = 0.0;
    
    private final ObservableList<String> profileQueueList;
    
    private Context(){
        this.profileQueueList = FXCollections.observableArrayList();
    }
    
    public static Context getInstance(){
        return INSTANCE;
    }

    public ObservableList getProfileQueueList(){
        return this.profileQueueList;
    }
    public ArrayList getUserInput(){
        return this.userInput;
    }
    public void setUserInput(ArrayList userInput){
        this.userInput = userInput;
    }
    
    public void setTheatreController(TheatreController controller){
        this.theatreController = controller;
    }
    public TheatreController getTheatreController(){
        return this.theatreController;
    }
    
    public void setGlobalController(SceneController controller){
        this.controller = controller;
    }
    public SceneController getGlobalController(){
        return this.controller;
    }
    
    public void setControlPanel(Stage controlPanel){
        this.controlPanel = controlPanel;
    }
    public Stage getControlPanel(){
        return this.controlPanel;
    }
    
    public void setLauncherScene(Scene launcherScene){
        this.launcherScene = launcherScene;
    }
    public Scene getLauncherScene(){
        return this.launcherScene;
    }
    
    public void setTheatreRunning(boolean isRunning){
        this.isRunning = isRunning;
    }
    public boolean getTheatreRunning(){
        return this.isRunning;
    }
    
    public void setEditViewOpened(boolean wasOpened){
        this.editViewOpened = wasOpened;
    }
    public boolean getEditViewOpened(){
        return this.editViewOpened;
    }
    
    public void setComboPressed(boolean comboPressed){
        this.comboPressed = comboPressed;
    }
    public boolean getComboPressed(){
        return this.comboPressed;
    }
    
    public void setDidSave(boolean didSave){
        this.didSave = didSave;
    }
    public boolean getDidSave(){
        return this.didSave;
    }
    
    public void setHeightRatio(double heightRatio){
        this.heightRatio = heightRatio;
    }
    public Double getHeightRatio(){
        return this.heightRatio;
    }
    
    public void setWidthRatio(double widthRatio){
        this.widthRatio = widthRatio;
    }
    public Double getWidthRatio(){
        return this.widthRatio;
    }
    
    public void setLetterboxingCurrent(double letterBoxingCurrent){
        this.letterBoxingCurrent = (int)letterBoxingCurrent;
    }
    public double getLetterBoxingCurrent(){
        return this.letterBoxingCurrent;
    }
    
    public void setLetterboxingPrevious(double letterBoxingPrevious){
        this.letterBoxingPrevious = letterBoxingPrevious;
    }
    public double getLetterBoxingPrevious(){
        return this.letterBoxingPrevious;
    }
    
    //Check for running timers and stop them
    public void closeApplication(){
        if(TheatreModel.getInstance().getStandbyTimer().isRunning()){
            TheatreModel.getInstance().getStandbyTimer().stop();
        }
        if(PreviewModel.getInstance().getResizeIncreaseTimer().isRunning()){
            PreviewModel.getInstance().getResizeIncreaseTimer().stop();
        }
    }
}
