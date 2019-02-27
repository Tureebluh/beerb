/*************************************************************************************************************************
 * Source Code Form License Notice
 *************************************************************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 **************************************************************************************************************************/
/*************************************************************************************************************************
 *   Author: Jarek Thomas
 *
 *   Controller for Theatre Scene that user sources to streaming software (OBS etc.)
 ***************************************************************************************************************************/

package main.controllers;

import main.models.EffectsModel;
import main.Main;
import main.models.Context;
import main.models.TextModel;
import main.models.TheatreModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;


public class TheatreController implements Initializable {
    
    //Theatre plays a 5 sec timer countdown for user when start button is pressed
    
    private Text mainMessage, timer;
    @FXML private MediaView theatreMediaView;
    @FXML private AnchorPane theatreAnchorPane;
    @FXML private StackPane timerStackPane;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Context.getInstance().setTheatreController(this);
        TheatreModel.getInstance().setMediaView(theatreMediaView);
        TheatreModel.getInstance().setAnchorPane(theatreAnchorPane);
        theatreAnchorPane.setStyle("-fx-background-color: black");
        
        mainMessage = new Text("Sample");
        mainMessage.setFill(Color.BLACK);
        mainMessage.setTextAlignment(TextAlignment.CENTER);
        
        Font.loadFont(Main.class.getResource("fonts/alarm_clock.ttf").toExternalForm(), 12);
        
        timer = new Text("");
        Font digital = Font.font("alarm clock", FontWeight.BOLD, 200.0);
        timer.setFont(digital);
        timer.setFill(Color.LIMEGREEN);
        timerStackPane.getChildren().add(timer);
        
        Bindings.bindBidirectional( timer.textProperty(), TheatreModel.getInstance().timerTextProperty());
        Bindings.bindBidirectional( mainMessage.fontProperty(), TextModel.getInstance().sampleFontProperty());
        Bindings.bindBidirectional( mainMessage.fillProperty(), TextModel.getInstance().sampleTextFillProperty());
        Bindings.bindBidirectional( mainMessage.effectProperty() , EffectsModel.getInstance().sampleTextEffectProperty());
        
        timer.textProperty().set("");
    }    
}
