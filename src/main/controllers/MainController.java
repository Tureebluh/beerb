/*************************************************************************************************************************
 * Source Code Form License Notice
 *************************************************************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 **************************************************************************************************************************/
/*************************************************************************************************************************
 *   Author: Jarek Thomas
 *
 *   Controller for Main Scene user sees when they open the application.
 *   Initialize method is called at controller load.
 ***************************************************************************************************************************/

package main.controllers;

import main.Main;
import main.models.SetController;
import main.models.Context;
import main.models.EffectsModel;
import main.models.ProfileModel;
import main.models.TextTransitionModel;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.models.AudioTransitionModel;
import main.models.CutTransitionModel;
import main.models.DraggableModel;
import main.models.FFTransitionModel;
import main.models.ImageTransitionModel;
import main.models.PreviewModel;
import main.models.TheatreModel;
import main.models.VideoTransitionModel;

public class MainController implements Initializable, SetController {

            private SceneController controller;
    @FXML   private Button helpBtn, videoCreateBtn, liveCreateBtn, mainAddToQueueBtn, removeFromQueueBtn, startCommercialBtn;
    @FXML   private ImageView logoImageView, videoThumbnail;
    @FXML   private ListView profileListView, profileQueueListView;
    @FXML   private Text welcomeText, mainBodyText, removePlaylistText, addPlaylistText;
            private String toolTipStyle = "-fx-font-size: 14px;";
            private File videoFile;
            private FileChooser videoChooser;
            private Tooltip listViewTT, helpBtnTT, videoCreateBtnTT, liveCreateBtnTT;
    
    //Platform.exit() is called on app closing to prevent threads not stopping
    @FXML
    public void closeApp(ActionEvent event){
        Platform.exit();
    }
    
    //Check global context to see if Theatre is already running
    @FXML
    public void startCommercialBreak(ActionEvent event){
        if(!Context.getInstance().getProfileQueueList().isEmpty()){
            if(Context.getInstance().getTheatreRunning() == false){
                Platform.runLater(()->{
                    TheatreModel.getInstance().startCommercialBreak();
                });
            } else {
                Platform.runLater(()->{
                    TheatreModel.getInstance().stopCommercialBreak();
                });
            }
        }
    }
    
    //Prompt user to select a single file with appropriate extensions.
    //Load video into editing scene and clear all transitions
    @FXML
    public void createVideoProfile(ActionEvent event){
        Platform.runLater(()->{
            if(!Context.getInstance().getTheatreRunning()){
                
                videoFile = videoChooser.showOpenDialog(helpBtn.getScene().getWindow());
                if(videoFile != null){
                    
                    ProfileModel.getInstance().getStage().setResizable(true);
                    ProfileModel.getInstance().getStage().setHeight(900);
                    ProfileModel.getInstance().getStage().setWidth(1571);
                    controller.setScene(Main.EDIT_SCENE);
                    TheatreModel.getInstance().getStage().hide();
                    ProfileModel.getInstance().clearSceneProperties();
                    FFTransitionModel.getInstance().clearTransitionProperties();
                    TextTransitionModel.getInstance().clearTransitionProperties();
                    ImageTransitionModel.getInstance().clearTransitionProperties();
                    AudioTransitionModel.getInstance().clearTransitionProperties();
                    VideoTransitionModel.getInstance().clearTransitionProperties();
                    PreviewModel.getInstance().clearTransitionProperties();
                    
                    try {
                        removeMedia();
                        String tempString = videoFile.getCanonicalPath();
                        ProfileModel.getInstance().getVideoProperties().add(tempString);
                        ProfileModel.getInstance().getVideoProperties().add("1.0");
                        Platform.runLater(()->{
                            PreviewModel.getInstance().startPreview(tempString);
                        });
                        
                    } catch (IOException ex) {
                        Logger.getLogger(EditViewController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    Context.getInstance().setEditViewOpened(true);
                    Context.getInstance().setDidSave(false);
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText("ERROR");
                alert.setContentText("Can't create profile while Theatre is running.");
                alert.setGraphic( new ImageView( "main/images/BeeLogo.png" ) );
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add("main/stylesheets/dialog.css");
                dialogPane.getStyleClass().add("error");
                alert.show();
            }
        });        
    }
    
    //Refresh user XML profiles on main screen
    @FXML
    public void refreshProfiles(ActionEvent event){
        Platform.runLater(()->{
            ProfileModel.getInstance().getProfiles();
        });        
    }
    
    //Delete XML profile from selected list
    @FXML
    public void deleteProfile(ActionEvent event){
        Platform.runLater(()->{
            if(!ProfileModel.getInstance().getProfileList().isEmpty() && profileListView.getSelectionModel().getSelectedItem() != null){
                File file = new File("Profiles//" + profileListView.getSelectionModel().getSelectedItem().toString() + ".xml");
                ProfileModel.getInstance().getProfileList().remove(profileListView.getSelectionModel().getSelectedItem().toString() );
                file.delete();
            }
        });        
    }
    
    //Load profile from XML and load video into scene. Clear all transitions and load the first selected transition
    @FXML
    public void editProfile(ActionEvent event){
        
            if(!Context.getInstance().getTheatreRunning()){
                if(!ProfileModel.getInstance().getProfileList().isEmpty() && profileListView.getSelectionModel().getSelectedItem() != null){
                    TheatreModel.getInstance().getStage().hide();
                    ProfileModel.getInstance().getStage().setResizable(true);
                    ProfileModel.getInstance().getStage().setHeight(900);
                    ProfileModel.getInstance().getStage().setWidth(1571);
                    controller.setScene(Main.EDIT_SCENE);

                    ProfileModel.getInstance().clearSceneProperties();
                    FFTransitionModel.getInstance().clearTransitionProperties();
                    TextTransitionModel.getInstance().clearTransitionProperties();
                    ImageTransitionModel.getInstance().clearTransitionProperties();
                    AudioTransitionModel.getInstance().clearTransitionProperties();
                    VideoTransitionModel.getInstance().clearTransitionProperties();
                    
                    PreviewModel.getInstance().setCurrentProfile(profileListView.getSelectionModel().getSelectedItem().toString());
                    ProfileModel.getInstance().loadProfileFromXML(profileListView.getSelectionModel().getSelectedItem().toString());
                    DraggableModel.getInstance().getEventListView().getSelectionModel().selectFirst();
                    
                    Platform.runLater(()->{
                        if(!ProfileModel.getInstance().getMarkerEventsList().isEmpty()){
                            String tempString = DraggableModel.getInstance().getEventListView().getSelectionModel().getSelectedItem().toString();
                            if(tempString.contains("Text")){
                                TextTransitionModel.getInstance().loadTransitionString(tempString);
                            } else if(tempString.contains("Image")){
                                ImageTransitionModel.getInstance().loadTransitionString(tempString);
                            } else if(tempString.contains("FF")){
                                FFTransitionModel.getInstance().loadTransitionString(tempString);
                            } else if(tempString.contains("Audio")){
                                AudioTransitionModel.getInstance().loadTransitionString(tempString);
                            } else if(tempString.contains("Cut")){
                                CutTransitionModel.getInstance().loadTransitionString(tempString);
                            } else if(tempString.contains("Video")){
                                VideoTransitionModel.getInstance().loadTransitionString(tempString);
                            }
                            PreviewModel.getInstance().setCurrentTransition(tempString);
                        }

                        EffectsModel.getInstance().addEffectToSample();
                        if(!ProfileModel.getInstance().getVideoProperties().isEmpty()){
                            PreviewModel.getInstance().startPreview(ProfileModel.getInstance().getVideoProperties().get(0).toString());
                        }
                    });
                    
                    
                    Context.getInstance().setEditViewOpened(true);
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText("ERROR");
                alert.setContentText("Can't edit profile while Theatre is running.");
                alert.setGraphic( new ImageView( "main/images/BeeLogo.png" ) );
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add("main/stylesheets/dialog.css");
                dialogPane.getStyleClass().add("error");
                alert.show();
            }        
    }
    
    //Add profile to Theatre queue
    @FXML
    public void addProfileToQueue(ActionEvent event) throws IOException{
        Platform.runLater(()->{
            if(!ProfileModel.getInstance().getProfileList().isEmpty() && profileListView.getSelectionModel().getSelectedItem() != null){
                Context.getInstance().getProfileQueueList().add(profileListView.getSelectionModel().getSelectedItem().toString());
            }
        });        
    }
    
    //Remove profile from Theatre queue
    @FXML
    public void removeProfileFromQueue(ActionEvent event){
        Platform.runLater(()->{
            if(!Context.getInstance().getProfileQueueList().isEmpty() && profileQueueListView.getSelectionModel().getSelectedItem() != null){
                Context.getInstance().getProfileQueueList().remove(profileQueueListView.getSelectionModel().getSelectedItem().toString());
            }
        });        
    }
    
    //Open webview to load youtube tutorial video
    @FXML
    public void openWebView(ActionEvent event){
        Platform.runLater(()->{
            Stage myDialog = new Stage();
            myDialog.initModality(Modality.WINDOW_MODAL);

            WebView browser = new WebView();
            WebEngine webEngine = browser.getEngine();
            webEngine.load("https://www.youtube.com/embed/tpfanktJC40?autoplay=1");
            browser.setPrefSize(1300, 731);
            Scene myDialogScene = new Scene(browser);

            myDialog.setScene(myDialogScene);
            myDialog.showAndWait();
            webEngine.load(null);
        });
    }
    
    //Close editing mediaplayer
    public void removeMedia(){
        if(!ProfileModel.getInstance().getVideoProperties().isEmpty()){
            PreviewModel.getInstance().closeMediaPlayer();
            ProfileModel.getInstance().getVideoProperties().remove(0);
        }       
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        Image logoImage = new Image( "main/images/BeeLogo.png" );
        logoImageView.setImage(logoImage);
        
        Bindings.bindBidirectional( videoThumbnail.imageProperty(), ProfileModel.getInstance().thumbnailImageProperty());
        Bindings.bindBidirectional(startCommercialBtn.textProperty(), TheatreModel.getInstance().commercialBtnTextProperty());
        Bindings.bindBidirectional(startCommercialBtn.styleProperty(), TheatreModel.getInstance().commercialBtnStyleProperty());
        
        mainBodyText.setText("To get started, create a profile by clicking one of the buttons below.\n"
                + "Once you've created a profile, you can add it to your playlist to start your commercial break.");
        
        helpBtnTT = new Tooltip("Open a tutorial video");
        listViewTT = new Tooltip("Right-click to open sub-menu");
        videoCreateBtnTT = new Tooltip("Choose a video to create a new video profile");
        liveCreateBtnTT = new Tooltip("Open the presentation editor to create a live presentation profile \n*Not yet implemented*");
        
        listViewTT.setStyle(toolTipStyle);
        helpBtnTT.setStyle(toolTipStyle);
        videoCreateBtnTT.setStyle(toolTipStyle);
        liveCreateBtnTT.setStyle(toolTipStyle);
        
        profileQueueListView.setTooltip(listViewTT);
        profileListView.setTooltip(listViewTT);
        videoCreateBtn.setTooltip(videoCreateBtnTT);
        liveCreateBtn.setTooltip(liveCreateBtnTT);

        profileListView.setPlaceholder(new Label("No profiles found."));
        profileListView.setItems(ProfileModel.getInstance().getProfileList());
        profileListView.getSelectionModel().selectLast();
        profileListView.setFixedCellSize(40);
        profileListView.getStylesheets().add("main/stylesheets/listViewStyles.css");
        profileListView.getItems().addListener((ListChangeListener.Change c) -> {
            Platform.runLater(()->{
                if(!profileListView.getItems().isEmpty()){
                    mainBodyText.setText("");
                    welcomeText.setText("");
                    addPlaylistText.setStyle("-fx-fill: #f2f2f2");
                    mainAddToQueueBtn.setOpacity(1);
                } else {
                    String mainBodyString = "To get started, create a profile by clicking the one of the buttons below.\n";
                    mainBodyString += "Once you've created a profile, you can add it to your playlist to start your commercial break.";
                    mainBodyText.setText(mainBodyString);
                    welcomeText.setText("Welcome!");
                    videoThumbnail.setImage(null);
                    mainAddToQueueBtn.setOpacity(0.5);
                    addPlaylistText.setStyle("-fx-fill: #3b3b3b");
                }
            });
        });
        profileListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
            if(!Context.getInstance().getEditViewOpened() && !profileListView.getItems().isEmpty()){
                ProfileModel.getInstance().clearSceneProperties();
                ProfileModel.getInstance().loadProfileFromXML(newValue.toString());
                Platform.runLater(()->{
                    ProfileModel.getInstance().prepareScreenshot(ProfileModel.getInstance().getVideoProperties().get(0).toString());
                });
            }
        });
        
        
        profileQueueListView.setPlaceholder(new Label("No profiles added to playlist."));
        profileQueueListView.setItems(Context.getInstance().getProfileQueueList());
        profileQueueListView.setFixedCellSize(50);
        profileQueueListView.getStylesheets().add("main/stylesheets/listViewStyles.css");
        profileQueueListView.getItems().addListener((ListChangeListener.Change c) -> {
            Platform.runLater(()->{
                if(!profileQueueListView.getItems().isEmpty()){
                    removePlaylistText.setStyle("-fx-fill: #f2f2f2");
                    removeFromQueueBtn.setOpacity(1);
                } else {
                    removePlaylistText.setStyle("-fx-fill: #3b3b3b");
                    removeFromQueueBtn.setOpacity(0.5);
                }
            });
        });
        
        Platform.runLater(()->{
            ProfileModel.getInstance().getProfiles();
            ProfileModel.getInstance().getTextProfiles();
        });
        
        mainAddToQueueBtn.getStylesheets().add("main/stylesheets/btnStyles.css");
        videoCreateBtn.getStylesheets().add("main/stylesheets/btnStyles.css");
        liveCreateBtn.getStylesheets().add("main/stylesheets/btnStyles.css");
        helpBtn.setTooltip(helpBtnTT);
        helpBtn.getStylesheets().add("main/stylesheets/btnStyles.css");
        removeFromQueueBtn.getStylesheets().add("main/stylesheets/btnStyles.css");
        
        videoChooser = new FileChooser();
        videoChooser.setTitle("Choose Video...");
        videoChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(
                "Video Files", "*.mp4", "*.flv", "*.fxm", "*.m3u8", "*.m4v", "*.m4a")
        );
    }
    
    @Override
    public void setParentController(SceneController parentController) {
        Context.getInstance().setGlobalController(parentController);
        this.controller = parentController;
    }
    
    
}
