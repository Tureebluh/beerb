/*************************************************************************************************************************
 * Source Code Form License Notice
 *************************************************************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 **************************************************************************************************************************/
/*************************************************************************************************************************
 *   Author: Jarek Thomas
 *
 *   Model for controlling draggable user interaction
 ***************************************************************************************************************************/

package main.models;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaView;
import javafx.scene.text.TextFlow;


public class DraggableModel {
    
    private class DragContext {
        double x;
        double y;
    }
    
    private final static DraggableModel INSTANCE = new DraggableModel();
    private final SimpleStringProperty textProfileCBValue = new SimpleStringProperty();
    private ListView markerEventListView;
    

    DragContext dragContext = new DragContext();

    
    
    public static DraggableModel getInstance(){
        return INSTANCE;
    }

    public void setEventListView(ListView markerEventListView){
        this.markerEventListView = markerEventListView;
    }
    public ListView getEventListView(){
        return this.markerEventListView;
    }
    public String getTextProfileCBValue(){
        return this.textProfileCBValue.getValue();
    }
    public void setTextProfileCBValue(String textProfileCBValue){
        this.textProfileCBValue.setValue(textProfileCBValue);
    }
    public StringProperty textProfileCBValueProperty(){
        return textProfileCBValue;
    }
    
    //Accepts a node as parameter and sets all associated eventhandlers
    public void makeDraggable(Node node) {
        node.setOnMousePressed(onMousePressedEventHandler);
        node.setOnMouseDragged(onMouseDraggedEventHandler);
        node.setOnMouseReleased(onMouseReleasedEventHandler);
    }
    
    //Check the object instanceof to know which list to check for object
    //Drag context adjust for mouse placement on the object itself
    //Loads the transition associated with the object being clicked on pressed
    EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {

            if( event.getSource() instanceof TextFlow) {
                TextFlow text = ((TextFlow) (event.getSource()));
                
                PreviewModel.getInstance().getTextObjects().forEach((key, value)->{
                    if(text.equals(value)){
                        PreviewModel.getInstance().setCurrentTransition("" + key);
                        TextTransitionModel.getInstance().loadTransitionString("" + key);
                        markerEventListView.getSelectionModel().select("" + key);
                    }
                });
                
                dragContext.x = text.getTranslateX() - event.getSceneX();
                dragContext.y = text.getTranslateY() - event.getSceneY();
                
            } else if (event.getSource() instanceof ImageView) {
                
                ImageView image = ((ImageView) (event.getSource()));

                PreviewModel.getInstance().getImageObjects().forEach((key, value)->{
                    if(image.equals(value)){
                        PreviewModel.getInstance().setCurrentTransition("" + key);
                        ImageTransitionModel.getInstance().loadTransitionString("" + key);
                        markerEventListView.getSelectionModel().select("" + key);
                    }
                });
                
                dragContext.x = image.getX() - event.getSceneX();
                dragContext.y = image.getY() - event.getSceneY();
            } else if (event.getSource() instanceof MediaView) {
                
                MediaView video = ((MediaView) (event.getSource()));

                PreviewModel.getInstance().getVideoObjects().forEach((key,value)->{
                    if(video.equals(value)){
                        PreviewModel.getInstance().setCurrentTransition("" + key);
                        VideoTransitionModel.getInstance().loadTransitionString("" + key);
                        markerEventListView.getSelectionModel().select("" + key);
                    }
                });
                
                dragContext.x = video.getX() - event.getSceneX();
                dragContext.y = video.getY() - event.getSceneY();
            }
        }
    };
    
    //Updates X and Y for object being dragged
    EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {

            if( event.getSource() instanceof TextFlow) {
                TextFlow text = ((TextFlow) (event.getSource()));
                
                text.setTranslateX( dragContext.x + event.getSceneX());
                text.setTranslateY( dragContext.y + event.getSceneY());
                
            } else if (event.getSource() instanceof ImageView) {

                ImageView image = ((ImageView) (event.getSource()));

                image.setX( dragContext.x + event.getSceneX());
                image.setY( dragContext.y + event.getSceneY());
            } else if (event.getSource() instanceof MediaView) {

                MediaView video = ((MediaView) (event.getSource()));

                video.setX( dragContext.x + event.getSceneX());
                video.setY( dragContext.y + event.getSceneY());
            }

        }
    };
    
    //Updates the transition string when mouse is released and final position has been determined
    EventHandler<MouseEvent> onMouseReleasedEventHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
            
            if( event.getSource() instanceof TextFlow) {
                
                TextFlow tempTextFlow = (TextFlow) event.getSource();
                
                PreviewModel.getInstance().moveX( tempTextFlow.getTranslateX(), PreviewModel.getInstance().getCurrentTransition());
                
                PreviewModel.getInstance().moveY( tempTextFlow.getTranslateY() , PreviewModel.getInstance().getCurrentTransition());
                
                Platform.runLater(()->{
                    TextTransitionModel.getInstance().editTransitionString();
                });
                  
                
            } else if (event.getSource() instanceof ImageView) {
                
                ImageView image = ((ImageView) (event.getSource()));
                
                PreviewModel.getInstance().moveX( image.getX(), PreviewModel.getInstance().getCurrentTransition());
                PreviewModel.getInstance().moveY( image.getY(), PreviewModel.getInstance().getCurrentTransition());
                
                Platform.runLater(()->{
                    ImageTransitionModel.getInstance().editTransitionString();
                });
                
            } else if (event.getSource() instanceof MediaView) {
                
                MediaView video = ((MediaView) (event.getSource()));
                
                PreviewModel.getInstance().moveX( video.getX(), PreviewModel.getInstance().getCurrentTransition());
                PreviewModel.getInstance().moveY( video.getY(), PreviewModel.getInstance().getCurrentTransition());
                
                Platform.runLater(()->{
                    VideoTransitionModel.getInstance().editTransitionString();
                });
                
            }
        }
    };

}
