/*************************************************************************************************************************
 * Source Code Form License Notice
 *************************************************************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 **************************************************************************************************************************/
/*************************************************************************************************************************
 *   Author: Jarek Thomas
 *
 *   Loads all appropriate scenes into a HashMap for quick scene selection
 ***************************************************************************************************************************/

package main.controllers;

import java.util.Arrays;
import main.models.SetController;
import java.util.HashMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import main.Main;

public class SceneController extends StackPane {
    
    //Associative array to store all scenes
    private final HashMap<String, Node> scenes = new HashMap<>();
    
    public SceneController () {
        super();
    }
    
    public Node getScenes(String name){
        return scenes.get(name);
    }
    
    public boolean setScene(final String name) {
        //Check if scene is loaded
        if (scenes.get(name) != null){
            //Check if nodes already exist
            if(!getChildren().isEmpty()) {
                //Remove previous scene
                getChildren().remove(0);
                //Add scene to root
                getChildren().add(0, scenes.get(name));
            } else {
                //Add scene to root
                getChildren().add(0, scenes.get(name));
            }
            return true;
        } else {
            System.out.println("Scene not loaded.");
            return false;
        }
    }
    
    public boolean loadScene(final String name, final String path){
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(path));
            Parent scene = (Parent) loader.load();
            SetController sceneController = (SetController)loader.getController();
            sceneController.setParentController(this);
            scenes.put(name, scene);
            return true;
        } catch (Exception e){
            System.out.println(Arrays.toString(e.getStackTrace()));
            return false;
        }
    }
}
