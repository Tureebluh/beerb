/************************************************************************************************************************
                                                    Source Code Form License Notice
*************************************************************************************************************************
    This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
    If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
*************************************************************************************************************************/
/************************************************************************************************************************
*   Author: Jarek Thomas
* 
*   Landing page for application.
*   Application begins with start(Stage stage).  Scenes are controlled separately by
*   the SceneController class. All scenes for the application are loaded through this class. 
**************************************************************************************************************************/

package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import main.controllers.SceneController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import main.models.Context;
import main.models.TheatreModel;
import main.models.ProfileModel;


public class Main extends Application {
    
    public static final String MAIN_SCENE = "MAIN";
    public static final String MAIN_SCENE_FILE = "views/MainView.fxml";
    public static final String EDIT_SCENE = "EDIT";
    public static final String EDIT_SCENE_FILE = "views/EditView.fxml";
    public static final String TEXT_SCENE = "TEXT";
    public static final String TEXT_SCENE_FILE = "views/TextView.fxml";
    public static final String THEATRE_SCENE = "THEATRE";
    public static final String THEATRE_SCENE_FILE = "views/TheatreView.fxml";
    private final ArrayList userInput = new ArrayList();
    
    @Override
    public void start(Stage theatreStage) throws Exception {
        
        //Unlike createDirectory(), no exception is thrown if the folder exists
        try{
            Files.createDirectories(Paths.get("Profiles//"));
            Files.createDirectories(Paths.get("Text//"));
        }catch(IOException e){
        }
        
        Context.getInstance().setUserInput(userInput);
        
        SceneController controller = new SceneController();
        controller.loadScene(Main.MAIN_SCENE, Main.MAIN_SCENE_FILE);
        controller.loadScene(Main.EDIT_SCENE, Main.EDIT_SCENE_FILE);
        controller.setScene(Main.MAIN_SCENE);
        
        Scene scene = new Scene(controller);
        
        Parent root = FXMLLoader.load(Main.class.getResource(THEATRE_SCENE_FILE));
        Scene theatrePanel = new Scene(root);
        TheatreModel.getInstance().setStage(theatreStage);
        theatreStage.setScene(theatrePanel);
        theatreStage.setMinWidth(1280);
        theatreStage.setMinHeight(720);
        
        theatreStage.getIcons().add(new Image("main/images/BeeLogo.png") );
        theatreStage.setTitle("Theatre");
        theatreStage.setX(-0.5);
        theatreStage.setY(-0.5);
        theatreStage.setOnCloseRequest(event -> {
            event.consume();
        });
        
        Stage stage = new Stage();
        stage.getIcons().add(new Image("main/images/BeeLogo.png") );
        stage.setTitle("BeeRB");
        stage.setScene(scene);
        stage.setResizable(false);
        ProfileModel.getInstance().setStage(stage);
        
        stage.show();
        
        stage.setOnHidden( event -> {
            TheatreModel.getInstance().getStage().close(); 
        });
    }
    
    @Override
    public void stop(){
        Context.getInstance().closeApplication();
    }
    
    public static void main(String[] args){
        launch(args);
    }
}
