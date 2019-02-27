/************************************************************************************************************************
                                                    Source Code Form License Notice
*************************************************************************************************************************
    This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
    If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
*************************************************************************************************************************/
/************************************************************************************************************************
*   Author: Jarek Thomas
* 
*   Controller for Profile Editing Scene 
**************************************************************************************************************************/

package main.controllers;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import javax.swing.Timer;
import main.Main;
import main.models.AudioTransitionModel;
import main.models.Context;
import main.models.CutTransitionModel;
import main.models.DraggableModel;
import main.models.FFTransitionModel;
import main.models.ImageTransitionModel;
import main.models.PreviewModel;
import main.models.ProfileModel;
import main.models.SetController;
import main.models.TextModel;
import main.models.TheatreModel;
import main.models.TextTransitionModel;
import main.models.VideoTransitionModel;

public class EditViewController implements Initializable, SetController {
    
    @FXML   private MediaView editMediaView;
    @FXML   private AnchorPane mwAnchorPane;
    @FXML   private Button videoBtn, volumeBtn, playPauseBtn, textBtn, imageBtn, ffBtn, cutBtn, audioBtn;
    @FXML   private Slider mainVideoTimeSlider, mainVolumeSlider;
    @FXML   private Label mainVolumeSliderLabel, mainVideoTimeLabel;
    @FXML   private ListView markerEventsListView;
    @FXML   private HBox controlPanelHBox;
    
    private HBox imageWidthHeightGroup, videoWidthHeightGroup, textProfileBtnGroup;
    private Slider ffStartRateSlider, ffStopRateSlider, audioRateSlider, audioVolumeSlider, audioSeekSlider, videoRateSlider, videoVolumeSlider, videoSeekSlider;
    private CheckBox autoPauseCheckbox;
    private ComboBox textProfileCB;
    private Button  saveBtn, startTimeBtn, stopTimeBtn, backBtn, audioFileBtn, imageFileBtn, videoFileBtn, imageLinkBtn, videoLinkBtn, createTextProfile, editTextProfile, deleteTextProfile;
    private TextField profileNameTextField, messageTextField, fontSizeTextField, imageWidthTextField, imageHeightTextField, videoWidthTextField, videoHeightTextField;
    private Region pushRight, spacer;
    private FileChooser videoChooser, imageChooser, audioChooser;
    private File videoFile, imageFile, audioFile;
    private SceneController controller;
    
    private Text profileNameLabel, startRateLabel, stopRateLabel, startBtnLabel, stopBtnLabel, textProfileLabel, messageTextLabel, fontSizeLabel,
                imageWidthLabel, imageHeightLabel, audioRateLabel, audioSeekLabel, audioVolumeLabel, videoWidthLabel, videoHeightLabel, videoRateLabel, videoSeekLabel, videoVolumeLabel;
    
    private VBox profileNameContainer, ffStartRateContainer, ffStopRateContainer, startBtnContainer, stopBtnContainer, textProfileContainer, messageTextContainer, fontSizeContainer,
                imageWidthContainer, imageHeightContainer, audioRateContainer, audioSeekContainer, audioVolumeContainer, videoWidthContainer, videoHeightContainer, videoRateContainer,
                videoSeekContainer, videoVolumeContainer;
    
    private static final String TEXT_LABEL_STYLE = "-fx-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;";
    private final String toolTipStyle = "-fx-font-size: 14px;";
    
    private Tooltip videoBtnTT, textBtnTT, imageBtnTT, ffBtnTT, cutBtnTT, audioBtnTT, saveBtnTT, backBtnTT,
            mainVolumeSliderTT, mainVideoTimeSliderTT, autoPauseCheckboxTT, createTextBtnTT, editTextBtnTT, deleteTextBtnTT;
    
    private final int resizeDelay = 500;
    private Timer resizeIncreaseTimer, resizeDecreaseTimer;
    
    //Remove transition from profile and reload video with next transition and all appropriate controls for user
    @FXML
    public void removeVideoMarker(ActionEvent event){
        try{
            MediaPlayer.Status status = PreviewModel.getInstance().getMediaPlayer().getStatus();
            if ( status != MediaPlayer.Status.PAUSED || status != MediaPlayer.Status.READY || status != MediaPlayer.Status.STOPPED){
                PreviewModel.getInstance().getMediaPlayer().pause();
            }
        }catch(Exception e){e.printStackTrace();}
        
        if(markerEventsListView.getSelectionModel().getSelectedItem() != null){
            String tempString = markerEventsListView.getSelectionModel().getSelectedItem().toString();
            
            if(TextTransitionModel.getInstance().getTextMarkers().containsKey(tempString)){
                TextTransitionModel.getInstance().getTextMarkers().remove(tempString);
            } else if(ImageTransitionModel.getInstance().getImageMarkers().containsKey(tempString)){
                ImageTransitionModel.getInstance().getImageMarkers().remove(tempString);
            } else if(FFTransitionModel.getInstance().getFFMarkers().containsKey(tempString)){
                FFTransitionModel.getInstance().getFFMarkers().remove(tempString);
            } else if(CutTransitionModel.getInstance().getCutMarkers().containsKey(tempString)){
                CutTransitionModel.getInstance().getCutMarkers().remove(tempString);
            } else if(AudioTransitionModel.getInstance().getAudioMarkers().containsKey(tempString)){
                AudioTransitionModel.getInstance().getAudioMarkers().remove(tempString);
            } else if(VideoTransitionModel.getInstance().getVideoMarkers().containsKey(tempString)){
                VideoTransitionModel.getInstance().getVideoMarkers().remove(tempString);
            }
            
            ProfileModel.getInstance().getMarkerEventsList().remove(tempString);
            
            if(!ProfileModel.getInstance().getMarkerEventsList().isEmpty()){
                tempString = markerEventsListView.getSelectionModel().getSelectedItem().toString();
                PreviewModel.getInstance().setCurrentTransition(tempString);

                if(TextTransitionModel.getInstance().getTextMarkers().containsKey(tempString)){
                    addTextControls();
                    TextTransitionModel.getInstance().loadTransitionString(tempString);
                } else if(ImageTransitionModel.getInstance().getImageMarkers().containsKey(tempString)){
                    addImageControls();
                    ImageTransitionModel.getInstance().loadTransitionString(tempString);
                } else if(FFTransitionModel.getInstance().getFFMarkers().containsKey(tempString)){
                    addFFControls();
                    FFTransitionModel.getInstance().loadTransitionString(tempString);
                } else if(CutTransitionModel.getInstance().getCutMarkers().containsKey(tempString)){
                    addCutControls();
                    CutTransitionModel.getInstance().loadTransitionString(tempString);
                } else if(AudioTransitionModel.getInstance().getAudioMarkers().containsKey(tempString)){
                    addAudioControls();
                    AudioTransitionModel.getInstance().loadTransitionString(tempString);
                } else if(VideoTransitionModel.getInstance().getVideoMarkers().containsKey(tempString)){
                    addVideoControls();
                    VideoTransitionModel.getInstance().loadTransitionString(tempString);
                }

                PreviewModel.getInstance().setStartTimeValue(TheatreModel.getInstance().timeConversion(Integer.parseInt(PreviewModel.getInstance().getStartTimestamp()) / 1000 ));
                if(!PreviewModel.getInstance().getStopTimestamp().equals("*")){
                    PreviewModel.getInstance().setStopTimeValue(TheatreModel.getInstance().timeConversion(Integer.parseInt(PreviewModel.getInstance().getStopTimestamp()) / 1000 ));
                } else {
                    PreviewModel.getInstance().setStopTimeValue("*");
                }
            } else {
                resetControlPanel();
            }
            Context.getInstance().setDidSave(false);
            Platform.runLater(()->{
                PreviewModel.getInstance().reloadMediaPlayer();
            });
        }
    }
    
    //Load the currently selected transition and all appropriate controls for user
    @FXML
    public void editVideoMarker(ActionEvent event){
        try{
            MediaPlayer.Status status = PreviewModel.getInstance().getMediaPlayer().getStatus();
            if ( status != MediaPlayer.Status.PAUSED || status != MediaPlayer.Status.READY || status != MediaPlayer.Status.STOPPED){
                PreviewModel.getInstance().getMediaPlayer().pause();
            }
        }catch(Exception e){e.printStackTrace();}
        
        if(markerEventsListView.getSelectionModel().getSelectedItem() != null){
            String tempString = markerEventsListView.getSelectionModel().getSelectedItem().toString();
            PreviewModel.getInstance().setCurrentTransition(tempString);
            PreviewModel.getInstance().setEditTransitionClicked(true);
            
            if(TextTransitionModel.getInstance().getTextMarkers().containsKey(tempString)){
                addTextControls();
                TextTransitionModel.getInstance().loadTransitionString(tempString);
            } else if(ImageTransitionModel.getInstance().getImageMarkers().containsKey(tempString)){
                addImageControls();
                ImageTransitionModel.getInstance().loadTransitionString(tempString);
                Platform.runLater(()->{
                    if(PreviewModel.getInstance().getImageLinkBtnActive()){
                        imageLinkBtn.setId("imageLinkBtn-active");
                        imageHeightTextField.setDisable(true);
                    } else {
                        imageLinkBtn.setId("imageLinkBtn-inactive");
                        imageHeightTextField.setDisable(false);
                    }
                });
            } else if(FFTransitionModel.getInstance().getFFMarkers().containsKey(tempString)){
                addFFControls();
                FFTransitionModel.getInstance().loadTransitionString(tempString);
            } else if(CutTransitionModel.getInstance().getCutMarkers().containsKey(tempString)){
                addCutControls();
                CutTransitionModel.getInstance().loadTransitionString(tempString);
            } else if(AudioTransitionModel.getInstance().getAudioMarkers().containsKey(tempString)){
                addAudioControls();
                AudioTransitionModel.getInstance().loadTransitionString(tempString);
            } else if(VideoTransitionModel.getInstance().getVideoMarkers().containsKey(tempString)){
                PreviewModel.getInstance().setEditTransitionClicked(true);
                addVideoControls();
                VideoTransitionModel.getInstance().loadTransitionString(tempString);
                Platform.runLater(()->{
                    if(PreviewModel.getInstance().getVideoLinkBtnActive()){
                        videoLinkBtn.setId("videoLinkBtn-active");
                        videoHeightTextField.setDisable(true);
                    } else {
                        videoLinkBtn.setId("videoLinkBtn-inactive");
                        videoHeightTextField.setDisable(false);
                    }
                });
            }
            Platform.runLater(()->{
                PreviewModel.getInstance().reloadMediaPlayer();
            });
        }
    }
    
    //Check state of mediaplayer and play/pause if mediaplayer returns back a valid state
    @FXML
    public void playPauseVideo(ActionEvent event){
        Platform.runLater(()->{
            if(!ProfileModel.getInstance().getVideoProperties().isEmpty()){
                try {
                    MediaPlayer.Status status = PreviewModel.getInstance().getMediaPlayer().getStatus();
                    if (status == MediaPlayer.Status.UNKNOWN  || status == MediaPlayer.Status.HALTED){
                       return;
                    }
                    if ( status == MediaPlayer.Status.PAUSED || status == MediaPlayer.Status.READY || status == MediaPlayer.Status.STOPPED){
                        PreviewModel.getInstance().getMediaPlayer().play();
                    } else {
                        PreviewModel.getInstance().getMediaPlayer().pause();
                    }
                } catch (Exception e){e.printStackTrace();}
            }
        });
    }
    
    //Check if player is muted/unmuted and alternate
    @FXML
    public void muteVideo(ActionEvent event){
        Platform.runLater(()->{
            if(PreviewModel.getInstance().getMediaPlayer().isMute()){
                PreviewModel.getInstance().getMediaPlayer().setMute(false);
                volumeBtn.setId("volumeBtn");
            } else {
                PreviewModel.getInstance().getMediaPlayer().setMute(true);
                volumeBtn.setId("mutedBtn");
            }
        });
    }
    
    //Create new TEXT transition object and reload player
    //Set new transition as current transition
    @FXML
    public void addText(ActionEvent event){
        try{
            MediaPlayer.Status status = PreviewModel.getInstance().getMediaPlayer().getStatus();
            if ( status != MediaPlayer.Status.PAUSED || status != MediaPlayer.Status.READY || status != MediaPlayer.Status.STOPPED){
                PreviewModel.getInstance().getMediaPlayer().pause();
            }
        }catch(Exception e){e.printStackTrace();}
        
        if(!ProfileModel.getInstance().getTextProfileList().isEmpty() && !ProfileModel.getInstance().getVideoProperties().isEmpty()){
            PreviewModel.getInstance().setEditTransitionClicked(true);
            String tempString = TextTransitionModel.getInstance().getDefaultTransitionKey();
            PreviewModel.getInstance().setCurrentTransition(tempString);
            TextTransitionModel.getInstance().createDefaultTransition();
            TextTransitionModel.getInstance().loadTransitionString(tempString);
            
            Platform.runLater(()->{  
                TextTransitionModel.getInstance().editTransitionString();
            });
            
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Looks like we need to do a few things first.");
            String tempString = "The following must be completed first: \n\n";
            if(ProfileModel.getInstance().getTextProfileList().isEmpty()){
                tempString += "\t-Create a Text Profile. \n";
            }
            if(ProfileModel.getInstance().getVideoProperties().isEmpty()){
                tempString += "\t-Add a video to your profile.";
            }
            alert.setContentText(tempString);
            alert.showAndWait();
        }
        
        Platform.runLater(()->{
            if(!ProfileModel.getInstance().getTextProfileList().isEmpty() && !ProfileModel.getInstance().getVideoProperties().isEmpty()){
                PreviewModel.getInstance().reloadMediaPlayer();
                addTextControls();
            }
        });   
    }
    
    //Create new IMAGE transition object and reload player
    //Set new transition as current transition
    @FXML
    public void addImage(ActionEvent event) throws IOException {
        try{
            MediaPlayer.Status status = PreviewModel.getInstance().getMediaPlayer().getStatus();
            if ( status != MediaPlayer.Status.PAUSED || status != MediaPlayer.Status.READY || status != MediaPlayer.Status.STOPPED){
                PreviewModel.getInstance().getMediaPlayer().pause();
            }
            if(!ProfileModel.getInstance().getVideoProperties().isEmpty()){
                PreviewModel.getInstance().setEditTransitionClicked(true);
                imageFile = imageChooser.showOpenDialog(editMediaView.getScene().getWindow());
                if(imageFile != null){
                    try {
                        String tempPath = imageFile.getCanonicalPath();
                        String tempString = ImageTransitionModel.getInstance().getDefaultTransitionKey();
                        PreviewModel.getInstance().setCurrentTransition(tempString);
                        ImageTransitionModel.getInstance().createDefaultTransition();
                        ImageTransitionModel.getInstance().loadTransitionString(tempString);
                        Platform.runLater(()->{
                            ImageTransitionModel.getInstance().setImagePath(tempPath.replace(",", ""));
                            ImageTransitionModel.getInstance().editTransitionString();
                            imageHeightTextField.setDisable(true);
                        });
                    } catch (IOException ex) {
                        Logger.getLogger(EditViewController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Context.getInstance().setDidSave(false);
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Looks like we need to do a few things first.");
                String tempString = "The following must be completed: \n\n";
                
                if(ProfileModel.getInstance().getVideoProperties().isEmpty()){
                    tempString += "\t-Add a video to your profile.";
                }
                alert.setContentText(tempString);
                alert.showAndWait();
            }
            
            Platform.runLater(()->{
                if(!ProfileModel.getInstance().getVideoProperties().isEmpty()){
                    PreviewModel.getInstance().reloadMediaPlayer();
                }
                if(imageFile != null){
                    addImageControls();
                } else {
                    resetControlPanel();
                }
            });
        }catch(Exception e){e.printStackTrace();}
    }
    
    //Create new FF transition object and reload player
    //Set new transition as current transition
    @FXML
    public void addFF(ActionEvent event){
        try{
            MediaPlayer.Status status = PreviewModel.getInstance().getMediaPlayer().getStatus();
            if ( status != MediaPlayer.Status.PAUSED || status != MediaPlayer.Status.READY || status != MediaPlayer.Status.STOPPED){
                PreviewModel.getInstance().getMediaPlayer().pause();
            }
        }catch(Exception e){e.printStackTrace();}
        
        if(!ProfileModel.getInstance().getVideoProperties().isEmpty()){
            String tempString = FFTransitionModel.getInstance().getDefaultTransitionKey();
            PreviewModel.getInstance().setCurrentTransition(tempString);
            FFTransitionModel.getInstance().createDefaultTransition();
            FFTransitionModel.getInstance().loadTransitionString(tempString);
            Platform.runLater(()->{
                FFTransitionModel.getInstance().editTransitionString();
            });
            Context.getInstance().setDidSave(false);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Looks like we need to do a few things first.");
            String tempString = "The following must be completed first: \n\n";
            tempString += "\t-Add a video to your profile.";
            
            alert.setContentText(tempString);
            alert.showAndWait();
        }
        
        Platform.runLater(()->{
            if(!ProfileModel.getInstance().getVideoProperties().isEmpty()){
                PreviewModel.getInstance().reloadMediaPlayer();
                addFFControls();
            }
        });
    }
    
    //Create new CUT transition object and reload player
    //Set new transition as current transition
    @FXML
    public void addCut(ActionEvent event){
        try{
            MediaPlayer.Status status = PreviewModel.getInstance().getMediaPlayer().getStatus();
            if ( status != MediaPlayer.Status.PAUSED || status != MediaPlayer.Status.READY || status != MediaPlayer.Status.STOPPED){
                PreviewModel.getInstance().getMediaPlayer().pause();
            }
        }catch(Exception e){e.printStackTrace();}
        
        if(!ProfileModel.getInstance().getVideoProperties().isEmpty()){
            String tempString = CutTransitionModel.getInstance().getDefaultTransitionKey();
            PreviewModel.getInstance().setCurrentTransition(tempString);
            CutTransitionModel.getInstance().createDefaultTransition();
            CutTransitionModel.getInstance().loadTransitionString(tempString);
            Platform.runLater(()->{
                CutTransitionModel.getInstance().editTransitionString();
            });
            Context.getInstance().setDidSave(false);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Looks like we need to do a few things first.");
            String tempString = "The following must be completed first: \n\n";
            tempString += "\t-Add a video to your profile.";
            
            alert.setContentText(tempString);
            alert.showAndWait();
        }
        
        Platform.runLater(()->{
            if(!ProfileModel.getInstance().getVideoProperties().isEmpty()){
                PreviewModel.getInstance().reloadMediaPlayer();
                addCutControls();
            }
        });
    }
    
    //Create new AUDIO transition object and reload player
    //Set new transition as current transition
    @FXML
    public void addAudio(ActionEvent event){
        try{
            MediaPlayer.Status status = PreviewModel.getInstance().getMediaPlayer().getStatus();
            if ( status != MediaPlayer.Status.PAUSED || status != MediaPlayer.Status.READY || status != MediaPlayer.Status.STOPPED){
                PreviewModel.getInstance().getMediaPlayer().pause();
            }
            if(!ProfileModel.getInstance().getVideoProperties().isEmpty()){
                audioFile = audioChooser.showOpenDialog(editMediaView.getScene().getWindow());
                if(audioFile != null){
                    try {
                        String tempPath = audioFile.getCanonicalPath();
                        String tempString = AudioTransitionModel.getInstance().getDefaultTransitionKey();
                        PreviewModel.getInstance().setCurrentTransition(tempString);
                        AudioTransitionModel.getInstance().createDefaultTransition();
                        AudioTransitionModel.getInstance().loadTransitionString(tempString);
                        Platform.runLater(()->{
                            AudioTransitionModel.getInstance().setAudioPath(tempPath.replace(",", ""));
                            AudioTransitionModel.getInstance().editTransitionString();
                        });
                    } catch (IOException ex) {
                        Logger.getLogger(EditViewController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Context.getInstance().setDidSave(false);
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Looks like we need to do a few things first.");
                String tempString = "The following must be completed first: \n\n";
                
                if(ProfileModel.getInstance().getVideoProperties().isEmpty()){
                    tempString += "\t-Add a video to your profile.";
                }
                alert.setContentText(tempString);
                alert.showAndWait();
            }
            
            Platform.runLater(()->{
                if(!ProfileModel.getInstance().getVideoProperties().isEmpty()){
                    PreviewModel.getInstance().reloadMediaPlayer();
                }
                if(audioFile != null){
                    addAudioControls();
                } else {
                    resetControlPanel();
                }
            });
            
        }catch(Exception e){e.printStackTrace();}
    }
    
    //Create new VIDEO transition object and reload player
    //Set new transition as current transition
    @FXML
    public void addVideo(ActionEvent event) throws IOException{
        try{
            MediaPlayer.Status status = PreviewModel.getInstance().getMediaPlayer().getStatus();
            if ( status != MediaPlayer.Status.PAUSED || status != MediaPlayer.Status.READY || status != MediaPlayer.Status.STOPPED){
                PreviewModel.getInstance().getMediaPlayer().pause();
            }
            if(!ProfileModel.getInstance().getVideoProperties().isEmpty()){
                PreviewModel.getInstance().setEditTransitionClicked(true);
                videoFile = videoChooser.showOpenDialog(editMediaView.getScene().getWindow());
                if(videoFile != null){
                    try {
                        String tempPath = videoFile.getCanonicalPath();
                        String tempString = VideoTransitionModel.getInstance().getDefaultTransitionKey();
                        PreviewModel.getInstance().setCurrentTransition(tempString);
                        VideoTransitionModel.getInstance().createDefaultTransition();
                        VideoTransitionModel.getInstance().loadTransitionString(tempString);
                        Platform.runLater(()->{
                            VideoTransitionModel.getInstance().setVideoPath(tempPath.replace(",", ""));
                            VideoTransitionModel.getInstance().editTransitionString();
                            videoHeightTextField.setDisable(true);
                        });
                    } catch (IOException ex) {
                        Logger.getLogger(EditViewController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Context.getInstance().setDidSave(false);
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Looks like we need to do a few things first.");
                String tempString = "The following must be completed first: \n\n";
                
                if(ProfileModel.getInstance().getVideoProperties().isEmpty()){
                    tempString += "\t-Add a video to your profile.";
                }
                alert.setContentText(tempString);
                alert.showAndWait();
            }
            
            Platform.runLater(()->{
                if(!ProfileModel.getInstance().getVideoProperties().isEmpty()){
                    PreviewModel.getInstance().reloadMediaPlayer();
                }
                if(videoFile != null){
                    addVideoControls();
                } else {
                    resetControlPanel();
                }
            });
            
        }catch(Exception e){e.printStackTrace();}      
    }
    
    //Add appropriate UI elements to control panel for CUT
    private void addCutControls(){
        controlPanelHBox.getChildren().clear();
        controlPanelHBox.getChildren().addAll(backBtn, profileNameContainer, pushRight, startBtnContainer, stopBtnContainer, autoPauseCheckbox, spacer, saveBtn);
    }
    
    //Add appropriate UI elements to control panel for IMAGE
    private void addImageControls(){
        controlPanelHBox.getChildren().clear();
        controlPanelHBox.getChildren().addAll(backBtn, profileNameContainer, pushRight, imageWidthHeightGroup, startBtnContainer, stopBtnContainer, imageFileBtn, autoPauseCheckbox, spacer, saveBtn);
    }
    
    //Add appropriate UI elements to control panel for AUDIO
    private void addAudioControls(){
        controlPanelHBox.getChildren().clear();
        controlPanelHBox.getChildren().addAll(backBtn, profileNameContainer, audioRateContainer, audioSeekContainer, audioVolumeContainer, startBtnContainer, stopBtnContainer, audioFileBtn, autoPauseCheckbox, pushRight, saveBtn);
    }
    
    //Add appropriate UI elements to control panel for VIDEO
    private void addVideoControls(){
        controlPanelHBox.getChildren().clear();
        controlPanelHBox.getChildren().addAll(backBtn, profileNameContainer, videoRateContainer, videoSeekContainer, videoVolumeContainer, videoWidthHeightGroup, startBtnContainer, stopBtnContainer, videoFileBtn, autoPauseCheckbox, pushRight, saveBtn);
    }
    
    //Add appropriate UI elements to control panel for TEXT
    private void addTextControls(){
        controlPanelHBox.getChildren().clear();
        controlPanelHBox.getChildren().addAll(backBtn, profileNameContainer, pushRight, fontSizeContainer, messageTextContainer, startBtnContainer, stopBtnContainer, textProfileContainer, autoPauseCheckbox, spacer, saveBtn);
    }
    
    //Add appropriate UI elements to control panel for FF
    private void addFFControls(){
        controlPanelHBox.getChildren().clear();
        controlPanelHBox.getChildren().addAll(backBtn, profileNameContainer, pushRight, ffStartRateContainer, ffStopRateContainer, startBtnContainer, stopBtnContainer, autoPauseCheckbox, spacer, saveBtn);
    }
    
    //Add appropriate UI elements to control panel for DEFAULT
    private void resetControlPanel(){
        controlPanelHBox.getChildren().clear();
        controlPanelHBox.getChildren().addAll(backBtn, profileNameContainer, autoPauseCheckbox, pushRight, saveBtn);
    }
    
    //Open modal window for creating/editing text profiles
    private void openTextModal(){
        try {
            MediaPlayer.Status status = PreviewModel.getInstance().getMediaPlayer().getStatus();
            if ( status != MediaPlayer.Status.PAUSED || status != MediaPlayer.Status.READY || status != MediaPlayer.Status.STOPPED){
                PreviewModel.getInstance().getMediaPlayer().pause();
            }
        } catch(Exception e){e.printStackTrace();}
        
        Platform.runLater(()->{
            Stage myDialog = new Stage();
            myDialog.initModality(Modality.APPLICATION_MODAL);
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getClassLoader().getResource("main/views/TextView.fxml"));
            } catch (IOException ex) {
                Logger.getLogger(EditViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
            Scene myDialogScene = new Scene(root);
            myDialog.setScene(myDialogScene);
            myDialog.showAndWait();
        });
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //PERCENTAGE STRING CONVERTER FOR SLIDERS 0% 25% 50% 75% 100%
        StringConverter percentageConverter = new StringConverter<Double>() {
            @Override
            public String toString(Double n) {
                if (n == 0.0) return "0%";
                if (n == 0.25) return "25%";
                if (n == 0.5) return "50%";
                if (n == 0.75) return "75%";
                return "100%";
            }
            @Override
            public Double fromString(String s) {
                switch (s) {
                    case "0%":
                        return 0.0d;
                    case "25%":
                        return 0.25d;
                    case "50%":
                        return 0.5d;
                    case "75%":
                        return 0.75d;
                    default:
                        return 1.0d;
                }
            }
        };
        
        //RATE CONVERTER FOR PLAYERS EX. 1x 2x 3x 4x
        StringConverter rateConverter = new StringConverter<Double>() {
            @Override
            public String toString(Double n) {
                if (n == 0.0) return "0x";
                if (n == 1.0) return "1x";
                if (n == 2.0) return "2x";
                if (n == 3.0) return "3x";
                return "4x";
            }
            @Override
            public Double fromString(String s) {
                switch (s) {
                    case "0x":
                        return 0.0d;
                    case "1x":
                        return 1.0d;
                    case "2x":
                        return 2.0d;
                    case "3x":
                        return 3.0d;
                    default:
                        return 4.0d;
                }
            }
        };
        
        //**********************************************************************
        //                          UNIVERSAL CONTROLS
        //**********************************************************************
        saveBtn = new Button();
        backBtn = new Button();
        pushRight = new Region();
        spacer = new Region();
        autoPauseCheckbox = new CheckBox("Auto-Pause");
        
        spacer.setPrefSize(90, 40);
        pushRight.setMinWidth(20);
        
        autoPauseCheckboxTT = new Tooltip("Automatically pause at the begin/end of each transition");
        saveBtnTT = new Tooltip("Save profile");
        backBtnTT = new Tooltip("Go back");
        videoBtnTT = new Tooltip("Add video overlay");
        audioBtnTT = new Tooltip("Add audio clip");
        imageBtnTT = new Tooltip("Add image overlay");
        textBtnTT = new Tooltip("Add text overlay");
        ffBtnTT = new Tooltip("Adjust playrate of main video");
        cutBtnTT = new Tooltip("Skip forward in main video");
        mainVolumeSliderTT = new Tooltip("Adjust main video volume \n*SAVES TO PROFILE*");
        mainVideoTimeSliderTT = new Tooltip("Click/drag the mouse to skip anywhere in the video. Use the left and right arrow keys to skip one frame at a time");
        
        autoPauseCheckboxTT.setStyle(toolTipStyle);
        saveBtnTT.setStyle(toolTipStyle);
        backBtnTT.setStyle(toolTipStyle);
        videoBtnTT.setStyle(toolTipStyle);
        audioBtnTT.setStyle(toolTipStyle);
        imageBtnTT.setStyle(toolTipStyle);
        textBtnTT.setStyle(toolTipStyle);
        ffBtnTT.setStyle(toolTipStyle);
        cutBtnTT.setStyle(toolTipStyle);
        mainVolumeSliderTT.setStyle(toolTipStyle);
        mainVideoTimeSliderTT.setStyle(toolTipStyle);
        
        autoPauseCheckbox.setTooltip(autoPauseCheckboxTT);
        saveBtn.setTooltip(saveBtnTT);
        backBtn.setTooltip(backBtnTT);
        videoBtn.setTooltip(videoBtnTT);
        audioBtn.setTooltip(audioBtnTT);
        imageBtn.setTooltip(imageBtnTT);
        textBtn.setTooltip(textBtnTT);
        ffBtn.setTooltip(ffBtnTT);
        cutBtn.setTooltip(cutBtnTT);
        mainVolumeSlider.setTooltip(mainVolumeSliderTT);
        mainVideoTimeSlider.setTooltip(mainVideoTimeSliderTT);
        
        profileNameLabel = new Text("Profile name:");
        profileNameLabel.setStyle(TEXT_LABEL_STYLE);
        
        profileNameTextField = new TextField();
        profileNameTextField.setPromptText("Profile name required");
        
        profileNameContainer = new VBox(profileNameLabel, profileNameTextField);
        profileNameContainer.setSpacing(5);
        
        startTimeBtn = new Button("Start-time");
        stopTimeBtn = new Button("Stop-time");
        
        startBtnLabel = new Text("Begin");
        startBtnLabel.setStyle(TEXT_LABEL_STYLE);
        startBtnLabel.setTranslateY(-5);
        startBtnContainer = new VBox(startBtnLabel, startTimeBtn);
        startBtnContainer.setAlignment(Pos.CENTER);
        
        stopBtnLabel = new Text("End");
        stopBtnLabel.setStyle(TEXT_LABEL_STYLE);
        stopBtnLabel.setTranslateY(-5);
        stopBtnContainer = new VBox(stopBtnLabel, stopTimeBtn);
        stopBtnContainer.setAlignment(Pos.CENTER);
        
        
        
        Platform.runLater(()->{
            autoPauseCheckbox.setSelected(true);
        });
        
        saveBtn.setId("saveBtn");
        saveBtn.getStylesheets().add("main/stylesheets/btnStyles.css");
        saveBtn.setPrefSize(50, 50);
        
        backBtn.setId("backBtn");
        backBtn.getStylesheets().add("main/stylesheets/btnStyles.css");
        
        markerEventsListView.setEditable(true);
        markerEventsListView.setItems(ProfileModel.getInstance().getMarkerEventsList().sorted());
        markerEventsListView.setCellFactory(TextFieldListCell.forListView());
        markerEventsListView.setPlaceholder(new Label("No events added."));
        DraggableModel.getInstance().setEventListView(markerEventsListView);
        
        resizeIncreaseTimer = new Timer(resizeDelay, new ResizeIncreaseDetected());
        resizeDecreaseTimer = new Timer(resizeDelay, new ResizeDecreaseDetected());
        PreviewModel.getInstance().setMediaView(editMediaView);
        PreviewModel.getInstance().setAnchorPane(mwAnchorPane);
        PreviewModel.getInstance().setResizeIncreaseTimer(resizeIncreaseTimer);
        PreviewModel.getInstance().setResizeDecreaseTimer(resizeDecreaseTimer);
        TextTransitionModel.getInstance().setTextProfileCB(textProfileCB);
        
        
        //**********************************************************************
        //                          FF CONTROLS
        //**********************************************************************
        stopRateLabel = new Text("Stop Rate");
        stopRateLabel.setStyle(TEXT_LABEL_STYLE);
        startRateLabel = new Text("Start Rate");
        startRateLabel.setStyle(TEXT_LABEL_STYLE);
        
        ffStartRateSlider = new Slider(0.0, 4.0, 2.0);
        ffStartRateSlider.setMajorTickUnit(1.0);
        ffStartRateSlider.setBlockIncrement(0.25f);
        ffStartRateSlider.setShowTickMarks(true);
        ffStartRateSlider.setShowTickLabels(true);
        ffStartRateSlider.setMinWidth(300);
        ffStartRateSlider.setLabelFormatter(rateConverter);
        ffStartRateContainer = new VBox(startRateLabel, ffStartRateSlider);
        ffStartRateContainer.setAlignment(Pos.CENTER);
        
        ffStopRateSlider = new Slider(0.0, 4.0, 1.0);
        ffStopRateSlider.setMajorTickUnit(1.0);
        ffStopRateSlider.setBlockIncrement(0.25f);
        ffStopRateSlider.setShowTickMarks(true);
        ffStopRateSlider.setShowTickLabels(true);
        ffStopRateSlider.setMinWidth(300);
        ffStopRateSlider.setLabelFormatter(rateConverter);
        ffStopRateContainer = new VBox(stopRateLabel, ffStopRateSlider);
        ffStopRateContainer.setAlignment(Pos.CENTER);
        
        
        //**********************************************************************
        //                          TEXT CONTROLS
        //**********************************************************************
        textProfileLabel = new Text("Style");
        textProfileLabel.setStyle(TEXT_LABEL_STYLE);
        messageTextLabel = new Text("Text message");
        messageTextLabel.setStyle(TEXT_LABEL_STYLE);
        fontSizeLabel = new Text("Font-size");
        fontSizeLabel.setStyle(TEXT_LABEL_STYLE);
        
        createTextBtnTT = new Tooltip("Create Text Profile");
        editTextBtnTT = new Tooltip("Edit Text Profile");
        deleteTextBtnTT = new Tooltip("Delete Text Profile");
        
        createTextProfile = new Button();
        createTextProfile.setId("addTextBtn");
        createTextProfile.setTooltip(createTextBtnTT);
        
        editTextProfile = new Button();
        editTextProfile.setId("editTextBtn");
        editTextProfile.setTooltip(editTextBtnTT);
        
        deleteTextProfile = new Button();
        deleteTextProfile.setId("removeTextBtn");
        deleteTextProfile.setTooltip(deleteTextBtnTT);
        
        createTextProfile.getStylesheets().add("main/stylesheets/btnStyles.css");
        editTextProfile.getStylesheets().add("main/stylesheets/btnStyles.css");
        deleteTextProfile.getStylesheets().add("main/stylesheets/btnStyles.css");
        textProfileBtnGroup = new HBox(textProfileLabel, createTextProfile, editTextProfile, deleteTextProfile);
        textProfileBtnGroup.setAlignment(Pos.CENTER);
        textProfileBtnGroup.maxHeight(15);
        
        textProfileCB = new ComboBox();
        textProfileCB.setItems(ProfileModel.getInstance().getTextProfileList());
        TextTransitionModel.getInstance().setTextProfileCB(textProfileCB);
        textProfileCB.setMaxWidth(200);
        
        textProfileContainer = new VBox(textProfileBtnGroup, textProfileCB);
        textProfileContainer.setTranslateY(-5);
        
        messageTextField = new TextField();
        messageTextField.setPromptText("Enter message here");
        messageTextContainer = new VBox(messageTextLabel, messageTextField);
        messageTextContainer.setSpacing(5);
        
        fontSizeTextField = new TextField();
        fontSizeTextField.setPromptText("Font Size");
        fontSizeTextField.setPrefWidth(50);
        fontSizeContainer = new VBox(fontSizeLabel, fontSizeTextField);
        fontSizeContainer.setSpacing(5);
        
        
        //**********************************************************************
        //                          IMAGE CONTROLS
        //**********************************************************************
        imageWidthLabel = new Text("Width");
        imageWidthLabel.setStyle(TEXT_LABEL_STYLE);
        imageHeightLabel = new Text("Height");
        imageHeightLabel.setStyle(TEXT_LABEL_STYLE);
        
        imageFileBtn = new Button();
        imageFileBtn.setId("imageFileBtn");
        imageFileBtn.getStylesheets().add("main/stylesheets/btnStyles.css");
        imageFileBtn.setPrefSize(40, 30);
        
        imageLinkBtn = new Button();
        imageLinkBtn.setId("imageLinkBtn-active");
        imageLinkBtn.getStylesheets().add("main/stylesheets/btnStyles.css");
        imageLinkBtn.setPrefSize(20, 20);
        
        imageWidthTextField = new TextField();
        imageWidthContainer = new VBox(imageWidthLabel, imageWidthTextField);
        imageWidthContainer.setSpacing(5);
        imageHeightTextField = new TextField();
        imageHeightContainer = new VBox(imageHeightLabel, imageHeightTextField);
        imageHeightContainer.setSpacing(5);
        
        imageWidthHeightGroup = new HBox(imageWidthContainer, imageLinkBtn, imageHeightContainer);
        imageWidthHeightGroup.setAlignment(Pos.CENTER);
        imageWidthHeightGroup.setSpacing(10);
        
        imageChooser = new FileChooser();
        imageChooser.setTitle("Choose Image...");
        imageChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(
                "Image Files", "*.png", "*.jpg", "*.gif"));
        
        
        //**********************************************************************
        //                          AUDIO CONTROLS
        //**********************************************************************
        audioRateLabel = new Text("Playback Rate");
        audioRateLabel.setStyle(TEXT_LABEL_STYLE);
        audioSeekLabel = new Text("Skip");
        audioSeekLabel.setStyle(TEXT_LABEL_STYLE);
        audioVolumeLabel = new Text("Volume");
        audioVolumeLabel.setStyle(TEXT_LABEL_STYLE);
        
        audioFileBtn = new Button();
        audioFileBtn.setId("audioFileBtn");
        audioFileBtn.getStylesheets().add("main/stylesheets/btnStyles.css");
        audioFileBtn.setPrefSize(40, 30);
        
        audioRateSlider = new Slider(0.0, 4.0, 1.0);
        audioRateSlider.setMajorTickUnit(1.0);
        audioRateSlider.setBlockIncrement(0.25f);
        audioRateSlider.setShowTickMarks(true);
        audioRateSlider.setShowTickLabels(true);
        audioRateSlider.setMinWidth(125);
        audioRateSlider.setLabelFormatter(rateConverter);
        audioRateContainer = new VBox(audioRateLabel, audioRateSlider);
        
        audioVolumeSlider = new Slider(0.0, 1.0, 0.5);
        audioVolumeSlider.setMajorTickUnit(0.25);
        audioVolumeSlider.setBlockIncrement(0.25d);
        audioVolumeSlider.setShowTickMarks(true);
        audioVolumeSlider.setShowTickLabels(true);
        audioVolumeSlider.setMinWidth(100);
        audioVolumeSlider.setLabelFormatter(percentageConverter);
        audioVolumeContainer = new VBox(audioVolumeLabel, audioVolumeSlider);
        
        audioSeekSlider = new Slider(0.0, 1.0, 0.0);
        audioSeekSlider.setMajorTickUnit(0.25);
        audioSeekSlider.setBlockIncrement(0.25d);
        audioSeekSlider.setShowTickMarks(true);
        audioSeekSlider.setShowTickLabels(true);
        audioSeekSlider.setMinWidth(400);
        audioSeekSlider.setLabelFormatter(percentageConverter);
        audioSeekContainer = new VBox(audioSeekLabel, audioSeekSlider);
        
        audioChooser = new FileChooser();
        audioChooser.setTitle("Choose Image...");
        audioChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(
                "Audio Files", "*.wav", "*.mp3", "*.aac"));
        
        
        //**********************************************************************
        //                          VIDEO CONTROLS
        //**********************************************************************
        videoWidthLabel = new Text("Width");
        videoWidthLabel.setStyle(TEXT_LABEL_STYLE);
        videoHeightLabel = new Text("Height");
        videoHeightLabel.setStyle(TEXT_LABEL_STYLE);
        videoSeekLabel = new Text("Skip");
        videoSeekLabel.setStyle(TEXT_LABEL_STYLE);
        videoRateLabel = new Text("Playback Rate");
        videoRateLabel.setStyle(TEXT_LABEL_STYLE);
        videoVolumeLabel = new Text("Volume");
        videoVolumeLabel.setStyle(TEXT_LABEL_STYLE);
        
        videoFileBtn = new Button();
        videoFileBtn.setId("videoFileBtn");
        videoFileBtn.getStylesheets().add("main/stylesheets/btnStyles.css");
        videoFileBtn.setPrefSize(40, 30);
        
        videoLinkBtn = new Button();
        videoLinkBtn.setId("videoLinkBtn-active");
        videoLinkBtn.getStylesheets().add("main/stylesheets/btnStyles.css");
        
        videoWidthTextField = new TextField();
        videoWidthTextField.setPrefWidth(60);
        videoWidthContainer = new VBox(videoWidthLabel, videoWidthTextField);
        videoWidthContainer.setSpacing(5);
        videoHeightTextField = new TextField();
        videoHeightTextField.setPrefWidth(60);
        videoHeightContainer = new VBox(videoHeightLabel, videoHeightTextField);
        videoHeightContainer.setSpacing(5);
        videoWidthHeightGroup = new HBox(videoWidthContainer, videoLinkBtn, videoHeightContainer);
        videoWidthHeightGroup.setAlignment(Pos.CENTER);
        videoWidthHeightGroup.setSpacing(10);
        
        videoRateSlider = new Slider(0.0, 4.0, 1.0);
        videoRateSlider.setMajorTickUnit(1.0);
        videoRateSlider.setBlockIncrement(0.25f);
        videoRateSlider.setShowTickMarks(true);
        videoRateSlider.setShowTickLabels(true);
        videoRateSlider.setMinWidth(125);
        videoRateSlider.setLabelFormatter(rateConverter);
        videoRateContainer = new VBox(videoRateLabel, videoRateSlider);
        
        videoVolumeSlider = new Slider(0.0, 1.0, 0.5);
        videoVolumeSlider.setMajorTickUnit(0.25);
        videoVolumeSlider.setBlockIncrement(0.25d);
        videoVolumeSlider.setShowTickMarks(true);
        videoVolumeSlider.setShowTickLabels(true);
        videoVolumeSlider.setMinWidth(100);
        videoVolumeSlider.setLabelFormatter(percentageConverter);
        videoVolumeContainer = new VBox(videoVolumeLabel, videoVolumeSlider);
        
        videoSeekSlider = new Slider(0.0, 1.0, 0.0);
        videoSeekSlider.setMajorTickUnit(0.25);
        videoSeekSlider.setBlockIncrement(0.25d);
        videoSeekSlider.setShowTickMarks(true);
        videoSeekSlider.setShowTickLabels(true);
        videoSeekSlider.setLabelFormatter(percentageConverter);
        videoSeekContainer = new VBox(videoSeekLabel, videoSeekSlider);
        
        videoChooser = new FileChooser();
        videoChooser.setTitle("Choose Video...");
        videoChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(
                "Video Files", "*.mp4", "*.flv", "*.fxm", "*.m3u8", "*.m4v", "*.m4a")
        );
        
        
        //**********************************************************************
        //              CONTROL PANEL PRIORITIES & DEFAULTS
        //**********************************************************************
        controlPanelHBox.setHgrow(messageTextContainer, Priority.ALWAYS);
        controlPanelHBox.setHgrow(videoSeekSlider, Priority.ALWAYS);
        controlPanelHBox.setHgrow(audioSeekSlider, Priority.ALWAYS);
        controlPanelHBox.setHgrow(pushRight, Priority.SOMETIMES);
        controlPanelHBox.setAlignment(Pos.CENTER_LEFT);
        resetControlPanel();
        
        
        //**********************************************************************
        //                          STYLESHEETS
        //**********************************************************************
        cutBtn.getStylesheets().add("main/stylesheets/btnStyles.css");
        textBtn.getStylesheets().add("main/stylesheets/btnStyles.css");
        imageBtn.getStylesheets().add("main/stylesheets/btnStyles.css");
        videoBtn.getStylesheets().add("main/stylesheets/btnStyles.css");
        playPauseBtn.getStylesheets().add("main/stylesheets/btnStyles.css");
        volumeBtn.getStylesheets().add("main/stylesheets/btnStyles.css");
        ffBtn.getStylesheets().add("main/stylesheets/btnStyles.css");
        audioBtn.getStylesheets().add("main/stylesheets/btnStyles.css");
        
        prepareBindings();
    }
    
    // Listens for edit scene resize and centers mediaplayer in scene
    // 1.78 = 16/9 aspect ratio
    // 288 & 185 are the offsets for the UI elements in the scene
    private class ResizeIncreaseDetected implements ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent ae) {
            Platform.runLater(()->{
                if(!ProfileModel.getInstance().getVideoProperties().isEmpty()){
                    PreviewModel.getInstance().reloadMediaPlayer();
                }
                if(Context.getInstance().getLetterBoxingCurrent() < 0){
                    ProfileModel.getInstance().getStage().setWidth((editMediaView.getFitHeight() * 1.78) + 288);
                } else if (Context.getInstance().getLetterBoxingCurrent() > (60 * Context.getInstance().getWidthRatio()) && !ProfileModel.getInstance().getStage().isMaximized()){
                    ProfileModel.getInstance().getStage().setHeight((editMediaView.getFitWidth() / 1.78) + 185);
                }
                editMediaView.setTranslateX((Context.getInstance().getLetterBoxingCurrent()) / 2);
            });
            resizeIncreaseTimer.stop();
        }
    }
    private class ResizeDecreaseDetected implements ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent ae) {
            Platform.runLater(()->{
                if(!ProfileModel.getInstance().getVideoProperties().isEmpty()){
                    PreviewModel.getInstance().reloadMediaPlayer();
                }
                if(Context.getInstance().getLetterBoxingCurrent() < 0){
                    ProfileModel.getInstance().getStage().setHeight((editMediaView.getFitWidth() / 1.78) + 185);
                } else if (Context.getInstance().getLetterBoxingCurrent() > (60 * Context.getInstance().getWidthRatio()) && !ProfileModel.getInstance().getStage().isMaximized()){
                    ProfileModel.getInstance().getStage().setHeight(((editMediaView.getFitWidth() - Context.getInstance().getLetterBoxingCurrent()) / 1.78) + 185);
                }
                editMediaView.setTranslateX((Context.getInstance().getLetterBoxingCurrent()) / 2);
            });
            resizeDecreaseTimer.stop();
        }
    }
    
    //Bind UI element properties to their respective models
    private void prepareBindings(){
        
        
        //************************************************************************
        //                              BINDINGS                                
        //************************************************************************
        
        StringConverter<Number> fontConverter = new NumberStringConverter();
        
        Bindings.bindBidirectional( playPauseBtn.idProperty(), PreviewModel.getInstance().playPauseIdProperty());
        Bindings.bindBidirectional( mainVideoTimeLabel.textProperty(), PreviewModel.getInstance().videoTimeTextProperty());
        Bindings.bindBidirectional( mainVolumeSliderLabel.textProperty(), PreviewModel.getInstance().videoVolumeLabelProperty());
        Bindings.bindBidirectional( mainVideoTimeSlider.disableProperty(), PreviewModel.getInstance().timeSliderDisableProperty());
        Bindings.bindBidirectional( mainVideoTimeSlider.valueChangingProperty(), PreviewModel.getInstance().timeSliderValueChangingProperty());
        Bindings.bindBidirectional( mainVideoTimeSlider.valueProperty(), PreviewModel.getInstance().timeSliderValueProperty());
        Bindings.bindBidirectional( mainVolumeSlider.valueChangingProperty(), PreviewModel.getInstance().volumeSliderValueChangingProperty());
        Bindings.bindBidirectional( mainVolumeSlider.valueProperty(), PreviewModel.getInstance().volumeSliderValueProperty());
        Bindings.bindBidirectional( textProfileCB.valueProperty(), PreviewModel.getInstance().textProfileCBValueProperty());
        
        Bindings.bindBidirectional( textProfileCB.valueProperty(), DraggableModel.getInstance().textProfileCBValueProperty());
        
        Bindings.bindBidirectional( messageTextField.textProperty(), TextTransitionModel.getInstance().messageProperty());
        Bindings.bindBidirectional( fontSizeTextField.textProperty(), TextTransitionModel.getInstance().fontSizeValueProperty(), fontConverter);
        
        Bindings.bindBidirectional( profileNameTextField.textProperty(), ProfileModel.getInstance().profileNameTextProperty());
        
        Bindings.bindBidirectional( imageWidthTextField.textProperty(), ImageTransitionModel.getInstance().fitWidthValueProperty());
        Bindings.bindBidirectional( imageHeightTextField.textProperty(), ImageTransitionModel.getInstance().fitHeightValueProperty());
        
        Bindings.bindBidirectional( ffStartRateSlider.valueProperty(), FFTransitionModel.getInstance().startRateValueProperty());
        Bindings.bindBidirectional( ffStopRateSlider.valueProperty(), FFTransitionModel.getInstance().stopRateValueProperty());
        
        Bindings.bindBidirectional( audioRateSlider.valueProperty(), AudioTransitionModel.getInstance().playRateValueProperty());
        Bindings.bindBidirectional( audioVolumeSlider.valueProperty(), AudioTransitionModel.getInstance().volumeValueProperty());
        Bindings.bindBidirectional( audioSeekSlider.valueProperty(), AudioTransitionModel.getInstance().seekTimeValueProperty());
        
        Bindings.bindBidirectional( videoRateSlider.valueProperty(), VideoTransitionModel.getInstance().playRateValueProperty());
        Bindings.bindBidirectional( videoVolumeSlider.valueProperty(), VideoTransitionModel.getInstance().volumeValueProperty());
        Bindings.bindBidirectional( videoSeekSlider.valueProperty(), VideoTransitionModel.getInstance().seekTimeValueProperty());
        Bindings.bindBidirectional( videoWidthTextField.textProperty(), VideoTransitionModel.getInstance().fitWidthValueProperty());
        Bindings.bindBidirectional( videoHeightTextField.textProperty(), VideoTransitionModel.getInstance().fitHeightValueProperty());
        
        Bindings.bindBidirectional( autoPauseCheckbox.selectedProperty(), PreviewModel.getInstance().autoPauseProperty());
        
        editMediaView.fitHeightProperty().bind(mwAnchorPane.heightProperty());
        editMediaView.fitWidthProperty().bind(mwAnchorPane.widthProperty());
        
        
        //************************************************************************
        //                        CUSTOM SLIDER LABELS                          
        //************************************************************************
        
                                    
        Pattern validEditingState = Pattern.compile("(([1-9][0-9]*)|0)?(\\.[0-9]*)?");
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText();
            if (validEditingState.matcher(text).matches()) {
                return change;
            } else {
                return null;
            }
        };
        StringConverter<Double> converter = new StringConverter<Double>() {
            @Override
            public Double fromString(String temp) {
                if (temp.isEmpty() || "-".equals(temp) || ".".equals(temp) || "-.".equals(temp)) {
                    return 60.0;
                } else {
                    return Double.valueOf(temp);
                }
            }
            @Override
            public String toString(Double temp) {
                return temp.toString();
            }
        };
        
        TextFormatter<Double> textFormatter = new TextFormatter<>(converter, 60.0, filter);
        
        
        //************************************************************************
        //                           MAIN VIDEO EVENTS                          
        //************************************************************************
        
        //Set volume between 0.00 - 1.00                            
        mainVolumeSlider.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            Platform.runLater(()->{
                if(Context.getInstance().getEditViewOpened() && PreviewModel.getInstance().getMediaPlayer() != null){
                    PreviewModel.getInstance().getMediaPlayer().setVolume(mainVolumeSlider.getValue() / 100);
                    ProfileModel.getInstance().getVideoProperties().set(1, mainVolumeSlider.getValue() / 100);
                }
            });    
        });
        
        //Starts timer for resize so calculations are only running once the user releases the mouse
        //Resets the timer every time the height property or width property is changed
        editMediaView.fitHeightProperty().addListener((obs, oldVal, newVal) -> {
            
            if(oldVal.doubleValue() > newVal.doubleValue()){
                if(resizeDecreaseTimer.isRunning()){
                    resizeDecreaseTimer.stop();
                }
                resizeDecreaseTimer.start();
            } else {
                if(resizeIncreaseTimer.isRunning()){
                    resizeIncreaseTimer.stop();
                }
                resizeIncreaseTimer.start();
            }
            Platform.runLater(()->{
                double ratioWidth = (newVal.doubleValue() * 1.78);

                double remainder = editMediaView.getFitWidth() - ratioWidth;

                Context.getInstance().setLetterboxingCurrent((int)remainder + 1);

                Context.getInstance().setHeightRatio(newVal.doubleValue() / 720.0);
            });
        });
        editMediaView.fitWidthProperty().addListener((obs, oldVal, newVal) -> {
            
            if(oldVal.doubleValue() > newVal.doubleValue()){
                if(resizeDecreaseTimer.isRunning()){
                    resizeDecreaseTimer.stop();
                }
                if(resizeIncreaseTimer.isRunning()){
                    resizeIncreaseTimer.stop();
                }
                resizeDecreaseTimer.start();
            } else {
                if(resizeIncreaseTimer.isRunning()){
                    resizeIncreaseTimer.stop();
                }
                if(resizeDecreaseTimer.isRunning()){
                    resizeDecreaseTimer.stop();
                }
                resizeIncreaseTimer.start();
            }
            
            Platform.runLater(()->{
                double remainder =  ((newVal.doubleValue() - (editMediaView.getFitHeight() * 1.78)));

                Context.getInstance().setLetterboxingCurrent((int)remainder + 1);
            
                Context.getInstance().setWidthRatio((newVal.doubleValue() - ((int)remainder + 1)) / 1280.0);
            });
        });
        
        //Calculate percentage of timeline slider to determine where to skip in the mediaplayer
        //Fires while value is changing to follow video playback, as well as on mouse release if user clicks
        mainVideoTimeSlider.valueProperty().addListener((ov, oldValue, newValue) -> {
            Platform.runLater(()->{
                if (mainVideoTimeSlider.isValueChanging()) {
                    // multiply duration by percentage of slider position
                    PreviewModel.getInstance().getMediaPlayer().seek(PreviewModel.getInstance().getDuration().multiply(mainVideoTimeSlider.getValue() / 100.0));
                }
            });    
        });
        mainVideoTimeSlider.setOnMouseReleased((event) -> {
            Platform.runLater(()->{
                PreviewModel.getInstance().getMediaPlayer().seek(PreviewModel.getInstance().getDuration().multiply(mainVideoTimeSlider.getValue() / 100.0));
            });
        });
        
        //Checking for LEFT or RIGHT arrow keys to skip or rewind video 15ms (~1 frame in a 60 FPS video)
        mainVideoTimeSlider.setOnKeyPressed((event)->{
            Platform.runLater(()->{
                if(event.getCode().equals(KeyCode.LEFT)){
                    if(!ProfileModel.getInstance().getVideoProperties().isEmpty()){
                        MediaPlayer.Status status = PreviewModel.getInstance().getMediaPlayer().getStatus();
                        if ( status != MediaPlayer.Status.PAUSED || status != MediaPlayer.Status.READY || status != MediaPlayer.Status.STOPPED){
                            PreviewModel.getInstance().getMediaPlayer().pause();
                        }
                        PreviewModel.getInstance().getMediaPlayer().seek(PreviewModel.getInstance().getMediaPlayer().getCurrentTime().subtract(Duration.millis(15)));
                    }
                } else if(event.getCode().equals(KeyCode.RIGHT)){
                    if(!ProfileModel.getInstance().getVideoProperties().isEmpty()){
                        MediaPlayer.Status status = PreviewModel.getInstance().getMediaPlayer().getStatus();
                        if ( status != MediaPlayer.Status.PAUSED || status != MediaPlayer.Status.READY || status != MediaPlayer.Status.STOPPED){
                            PreviewModel.getInstance().getMediaPlayer().pause();
                        }
                        PreviewModel.getInstance().getMediaPlayer().seek(PreviewModel.getInstance().getMediaPlayer().getCurrentTime().add(Duration.millis(15)));
                    }
                }
                if(!mainVideoTimeSlider.isValueChanging()){
                    PreviewModel.getInstance().deleteMarkersForTimeline();
                }
            });
        });
        //Added to improve UX so user doesn't have to be pinpoint accurate
        mainVideoTimeSlider.setOnMousePressed((event)->{
            mainVideoTimeSlider.requestFocus();
        });
        
        
        //************************************************************************
        //                          UNIVERSAL EVENTS                            
        //************************************************************************
        
        //Save Transition values and replace transition entry with new user defined key
        markerEventsListView.setOnEditCommit(new EventHandler<ListView.EditEvent<String>>(){
            @Override
            public void handle(ListView.EditEvent<String> event) {
                if(markerEventsListView.getSelectionModel().getSelectedItem() != null){
                    String tempString = markerEventsListView.getSelectionModel().getSelectedItem().toString();
                    
                    String tempValue = event.getNewValue().replace("-", "_");
                    
                    if(TextTransitionModel.getInstance().getTextMarkers().containsKey(tempString)){
                        String value = (String)TextTransitionModel.getInstance().getTextMarkers().get(tempString);
                        TextTransitionModel.getInstance().getTextMarkers().remove(tempString);
                        TextTransitionModel.getInstance().getTextMarkers().put(tempValue, value);
                        
                    } else if(ImageTransitionModel.getInstance().getImageMarkers().containsKey(tempString)){
                        String value = (String)ImageTransitionModel.getInstance().getImageMarkers().get(tempString);
                        ImageTransitionModel.getInstance().getImageMarkers().remove(tempString);
                        ImageTransitionModel.getInstance().getImageMarkers().put(tempValue, value);
                        
                    } else if(FFTransitionModel.getInstance().getFFMarkers().containsKey(tempString)){
                        String value = (String)FFTransitionModel.getInstance().getFFMarkers().get(tempString);
                        FFTransitionModel.getInstance().getFFMarkers().remove(tempString);
                        FFTransitionModel.getInstance().getFFMarkers().put(tempValue, value);
                        
                    } else if(CutTransitionModel.getInstance().getCutMarkers().containsKey(tempString)){
                        String value = (String)CutTransitionModel.getInstance().getCutMarkers().get(tempString);
                        CutTransitionModel.getInstance().getCutMarkers().remove(tempString);
                        CutTransitionModel.getInstance().getCutMarkers().put(tempValue, value);
                        
                    } else if(AudioTransitionModel.getInstance().getAudioMarkers().containsKey(tempString)){
                        String value = (String)AudioTransitionModel.getInstance().getAudioMarkers().get(tempString);
                        AudioTransitionModel.getInstance().getAudioMarkers().remove(tempString);
                        AudioTransitionModel.getInstance().getAudioMarkers().put(tempValue, value);
                        
                    } else if(VideoTransitionModel.getInstance().getVideoMarkers().containsKey(tempString)){
                        String value = (String)VideoTransitionModel.getInstance().getVideoMarkers().get(tempString);
                        VideoTransitionModel.getInstance().getVideoMarkers().remove(tempString);
                        VideoTransitionModel.getInstance().getVideoMarkers().put(tempValue, value);
                    }
                    
                    Platform.runLater(()->{
                        ProfileModel.getInstance().getMarkerEventsList().remove(tempString);
                        ProfileModel.getInstance().getMarkerEventsList().add(tempValue);
                        PreviewModel.getInstance().setCurrentTransition(tempValue);
                        markerEventsListView.getSelectionModel().select(tempValue);
                        PreviewModel.getInstance().reloadMediaPlayer();
                    });
                }
            }
        });
        
        //Save start time for current transition
        startTimeBtn.setOnAction((event) -> {
            PreviewModel.getInstance().captureStartTime();
        });
        
        //Save stop time for current transition
        stopTimeBtn.setOnAction((event) -> {
            PreviewModel.getInstance().captureStopTime();
        });
        
        //Save the currently loaded profile to XML
        saveBtn.setOnAction((event)->{
            Platform.runLater(()->{
                if(!profileNameTextField.getText().isEmpty()){
                    ProfileModel.getInstance().saveProfileToXML();
                    Context.getInstance().setDidSave(true);
                } else {
                    profileNameTextField.requestFocus();
                    profileNameTextField.setStyle("-fx-background-color: #990000;");
                    Timeline changeColor = new Timeline(new KeyFrame(Duration.seconds(3), new EventHandler<ActionEvent>(){
                        @Override
                        public void handle(ActionEvent event) {
                            profileNameTextField.setStyle("-fx-background-color: white;");
                        }
                    }));
                    changeColor.setCycleCount(1);
                    changeColor.play();
                }
            });
        });
        
        //Check if user saved before returning to main menu. Close mediaplayer and clear transitions
        backBtn.setOnAction((event)->{
            Platform.runLater(()->{
                if(Context.getInstance().getDidSave()){
                    
                    Platform.runLater(()->{
                        if(ProfileModel.getInstance().getStage().isMaximized()){
                            ProfileModel.getInstance().getStage().setMaximized(false);
                        }
                    
                        ProfileModel.getInstance().getStage().setHeight(731);
                        ProfileModel.getInstance().getStage().setWidth(1300);
                        ProfileModel.getInstance().getStage().setResizable(false);
                    });
                    
                    Platform.runLater(()->{
                        if(!ProfileModel.getInstance().getVideoProperties().isEmpty()){
                            PreviewModel.getInstance().closeMediaPlayer();
                        }
                        ProfileModel.getInstance().clearSceneProperties();
                        PreviewModel.getInstance().clearTransitionProperties();
                        TextTransitionModel.getInstance().clearTransitionProperties();
                        resetControlPanel();
                        controller.setScene(Main.MAIN_SCENE);
                        Context.getInstance().setEditViewOpened(false);
                    });
                    
                } else {
                    final ButtonType exitNoSave = new ButtonType("Don't save", ButtonBar.ButtonData.BACK_PREVIOUS);
                    final ButtonType saveAndExit = new ButtonType("Save and exit", ButtonBar.ButtonData.OK_DONE);
                    final ButtonType cancelBack = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", saveAndExit, exitNoSave, cancelBack);
                    alert.setTitle("Save Dialog");
                    alert.setHeaderText("After this, there is no turning back. You take the blue pill - the story ends...");
                    alert.setContentText("Save and exit?");
                    Optional<ButtonType> result = alert.showAndWait();
                    
                    if (result.get() == saveAndExit){
                        if(!profileNameTextField.getText().isEmpty()){
                            saveBtn.fire();
                            
                            Platform.runLater(()->{
                                if(ProfileModel.getInstance().getStage().isMaximized()){
                                    ProfileModel.getInstance().getStage().setMaximized(false);
                                }
                                ProfileModel.getInstance().getStage().setHeight(731);
                                ProfileModel.getInstance().getStage().setWidth(1300);
                                ProfileModel.getInstance().getStage().setResizable(false);
                            });
                            controller.setScene(Main.MAIN_SCENE);
                            Context.getInstance().setEditViewOpened(false);
                            Platform.runLater(()->{
                                if(!ProfileModel.getInstance().getVideoProperties().isEmpty()){
                                    PreviewModel.getInstance().closeMediaPlayer();
                                }
                                ProfileModel.getInstance().clearSceneProperties();
                                PreviewModel.getInstance().clearTransitionProperties();
                                TextTransitionModel.getInstance().clearTransitionProperties();
                                resetControlPanel();
                            });
                        }
                        
                    } else if (result.get() == exitNoSave){
                            
                            Platform.runLater(()->{
                                if(ProfileModel.getInstance().getStage().isMaximized()){
                                    ProfileModel.getInstance().getStage().setMaximized(false);
                                }
                                ProfileModel.getInstance().getStage().setHeight(731);
                                ProfileModel.getInstance().getStage().setWidth(1300);
                                ProfileModel.getInstance().getStage().setResizable(false);
                            });
                            
                            controller.setScene(Main.MAIN_SCENE);
                            Context.getInstance().setEditViewOpened(false);
                            ProfileModel.getInstance().clearSceneProperties();
                            PreviewModel.getInstance().clearTransitionProperties();
                            TextTransitionModel.getInstance().clearTransitionProperties();
                            PreviewModel.getInstance().closeMediaPlayer();
                            resetControlPanel();
                            
                    } else if (result.get() == cancelBack){
                        //Do nothing
                    }
                }
            });
        });
        
        
        //************************************************************************
        //                          TEXT EVENTS                                 
        //************************************************************************
        
                                            
        fontSizeTextField.setTextFormatter(textFormatter);
        
        //Clear properties for Text Modal and Open
        createTextProfile.setOnAction((event)->{
            TextModel.getInstance().clearTextSceneProperties();
            openTextModal();
        });
        
        //Load properties for Text Profile and Open Modal
        editTextProfile.setOnAction((event)->{
            TextModel.getInstance().loadTextFromXML(textProfileCB.getSelectionModel().getSelectedItem().toString());
            openTextModal();
        });
        
        //Delete Text Profile
        deleteTextProfile.setOnAction((event)->{
            Platform.runLater(()->{
                if(!ProfileModel.getInstance().getProfileList().isEmpty()){
                    File file = new File("Text//" + textProfileCB.getSelectionModel().getSelectedItem().toString() + ".xml");
                    ProfileModel.getInstance().getTextProfileList().remove(textProfileCB.getSelectionModel().getSelectedItem().toString() );
                    file.delete();
                }
            });
        });
        
        //Change Text Profile for current Text object
        textProfileCB.setOnAction((event) -> {
            Platform.runLater(()->{
                if(!textProfileCB.getValue().equals("")){
                    PreviewModel.getInstance().changeTextProfile(textProfileCB.getValue().toString());
                }
            });
        });
        
        //Change font size when user presses enter
        fontSizeTextField.setOnKeyPressed((event)-> {
            if(event.getCode().equals(KeyCode.ENTER)){
                controlPanelHBox.requestFocus();
            }
        });
        fontSizeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(()->{
                if(!fontSizeTextField.isFocused()){
                    PreviewModel.getInstance().updateFontSize(Double.parseDouble(newValue), PreviewModel.getInstance().getCurrentTransition());
                }
            });
        });
        
        messageTextField.setOnKeyPressed((event)-> {
            Platform.runLater(()->{
                String newValue = messageTextField.getText();
                if(!newValue.equals("Default Text") || !newValue.isEmpty()){
                    PreviewModel.getInstance().updateText(newValue, PreviewModel.getInstance().getCurrentTransition());
                }
            });    
        });
        
        
        //************************************************************************
        //                          IMAGE EVENTS                                
        //************************************************************************
        
        //Prompt user to select image file. Create transition and reload player                                    
        imageFileBtn.setOnAction((event)->{
            imageFile = imageChooser.showOpenDialog(editMediaView.getScene().getWindow());
            if(imageFile != null){
                try {
                    String tempPath = imageFile.getCanonicalPath();
                    Platform.runLater(()->{
                        ImageTransitionModel.getInstance().setImagePath(tempPath.replace(",", ""));
                        ImageTransitionModel.getInstance().editTransitionString();
                        PreviewModel.getInstance().reloadMediaPlayer();
                    });
                } catch (IOException ex) {
                    Logger.getLogger(EditViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
                Context.getInstance().setDidSave(false);
            }
        });
        
        //Change image width
        imageWidthTextField.focusedProperty().addListener((args, oldProp, newProp)->{
            if(newProp){
                imageWidthTextField.setOnKeyPressed((event)-> {
                    if(event.getCode().equals(KeyCode.ENTER)){
                        controlPanelHBox.requestFocus();
                    }
                });
            } else {
                Platform.runLater(()->{
                    if(PreviewModel.getInstance().getImageLinkBtnActive()){
                        PreviewModel.getInstance().updateImageSize(imageWidthTextField.getText(), "0");
                    } else {
                        PreviewModel.getInstance().updateImageSize(imageWidthTextField.getText(), imageHeightTextField.getText());
                    }
                });  
            }
        });
        
        //Change image height
        imageHeightTextField.focusedProperty().addListener((args, oldProp, newProp)->{
            if(newProp){
                imageHeightTextField.setOnKeyPressed((event)-> {
                    if(event.getCode().equals(KeyCode.ENTER)){
                        controlPanelHBox.requestFocus();
                    }
                });
            } else {
                Platform.runLater(()->{
                    if(PreviewModel.getInstance().getImageLinkBtnActive()){
                        PreviewModel.getInstance().updateImageSize("0", imageHeightTextField.getText());
                    } else {
                        PreviewModel.getInstance().updateImageSize(imageWidthTextField.getText(), imageHeightTextField.getText());
                    }
                });  
            }
        });
        
        //Toggle if image should maintain aspect ratio
        imageLinkBtn.setOnAction((event)->{
            Platform.runLater(()->{
                if(PreviewModel.getInstance().getImageLinkBtnActive()){
                    imageLinkBtn.setId("imageLinkBtn-inactive");
                    imageHeightTextField.setDisable(false);
                    PreviewModel.getInstance().setImageLinkBtnActive(false);
                    PreviewModel.getInstance().reloadMediaPlayer();
                } else {
                    imageLinkBtn.setId("imageLinkBtn-active");
                    imageHeightTextField.setDisable(true);
                    PreviewModel.getInstance().setImageLinkBtnActive(true);
                    PreviewModel.getInstance().reloadMediaPlayer();
                }
                ImageTransitionModel.getInstance().editTransitionString();
            });
        });
        
        
        //************************************************************************
        //                          FAST-FORWARD EVENTS                         
        //************************************************************************
                                        
        //LEFT and RIGHT arrow key events to update FF playback rate
        //Focus on Mouse pressed
        //Updates the playrate on mouse release
        ffStartRateSlider.setOnKeyPressed((event)->{
            Platform.runLater(()->{
                if(event.getCode().equals(KeyCode.LEFT) || event.getCode().equals(KeyCode.RIGHT)){
                    PreviewModel.getInstance().updateFFStartRate(ffStartRateSlider.getValue());
                }
            });
        });
        ffStartRateSlider.setOnMouseReleased((event) -> {
            Platform.runLater(()->{
                PreviewModel.getInstance().updateFFStartRate(ffStartRateSlider.getValue());
            });
        });
        ffStartRateSlider.setOnMousePressed((event)->{
            ffStartRateSlider.requestFocus();
        });
        //Same events for stop rate - playback rate to change to when transition ends
        ffStopRateSlider.setOnMouseReleased((event) -> {
            Platform.runLater(()->{
                PreviewModel.getInstance().updateFFStopRate(ffStopRateSlider.getValue());
            });
        });
        ffStopRateSlider.setOnKeyPressed((event)->{
            Platform.runLater(()->{
                if(event.getCode().equals(KeyCode.LEFT) || event.getCode().equals(KeyCode.RIGHT)){
                    PreviewModel.getInstance().updateFFStopRate(ffStopRateSlider.getValue());
                }
            });
        });
        ffStopRateSlider.setOnMousePressed((event)->{
            ffStopRateSlider.requestFocus();
        });
        
        
        //************************************************************************
        //                          AUDIO EVENTS                                
        //************************************************************************
        
        //LEFT and RIGHT arrow key events to update audio volume
        //Focus on Mouse pressed
        //Updates the volume on mouse release                               
        audioVolumeSlider.setOnKeyPressed((event)->{
            Platform.runLater(()->{
                if(event.getCode().equals(KeyCode.LEFT) || event.getCode().equals(KeyCode.RIGHT)){
                    PreviewModel.getInstance().updateAudioVolume(audioVolumeSlider.getValue());
                }
            });
        });
        audioVolumeSlider.setOnMouseReleased((event) -> {
            Platform.runLater(()->{
                PreviewModel.getInstance().updateAudioVolume(audioVolumeSlider.getValue());
            });
        });
        audioVolumeSlider.setOnMousePressed((event)->{
            audioVolumeSlider.requestFocus();
        });
        
        //LEFT and RIGHT arrow key events to update playback rate for audio
        //Focus on Mouse pressed
        //Updates the playback rate on mouse release
        audioRateSlider.setOnKeyPressed((event)->{
            Platform.runLater(()->{
                if(event.getCode().equals(KeyCode.LEFT) || event.getCode().equals(KeyCode.RIGHT)){
                    PreviewModel.getInstance().updateAudioRate(audioRateSlider.getValue());
                }
            });
        });
        audioRateSlider.setOnMouseReleased((event) -> {
            Platform.runLater(()->{
                PreviewModel.getInstance().updateAudioRate(audioRateSlider.getValue());
            });
        });
        audioRateSlider.setOnMousePressed((event)->{
            audioRateSlider.requestFocus();
        });
        
        //LEFT and RIGHT arrow key events to update start time of audio
        //Focus on Mouse pressed
        //Updates the start time on mouse release
        audioSeekSlider.setOnKeyPressed((event)->{
            Platform.runLater(()->{
                if(event.getCode().equals(KeyCode.LEFT) || event.getCode().equals(KeyCode.RIGHT)){
                    PreviewModel.getInstance().updateAudioSeek(audioSeekSlider.getValue());
                }
            });
        });
        audioSeekSlider.setOnMouseReleased((event) -> {
            Platform.runLater(()->{
                PreviewModel.getInstance().updateAudioSeek(audioSeekSlider.getValue());
            });
        });
        audioSeekSlider.setOnMousePressed((event)->{
            audioSeekSlider.requestFocus();
        });
        
        //Prompt user to select audio file with appropriate extensions
        //Load transition string and reload mediaplayer
        audioFileBtn.setOnAction((event)->{
            audioFile = audioChooser.showOpenDialog(editMediaView.getScene().getWindow());
            if(audioFile != null){
                try {
                    String tempPath = audioFile.getCanonicalPath();
                    Platform.runLater(()->{
                        AudioTransitionModel.getInstance().setAudioPath(tempPath.replace(",", ""));
                        AudioTransitionModel.getInstance().editTransitionString();
                        PreviewModel.getInstance().reloadMediaPlayer();
                    });
                } catch (IOException ex) {
                    Logger.getLogger(EditViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
                Context.getInstance().setDidSave(false);
            }
        });
        
        
        //************************************************************************
        //                          VIDEO EVENTS                                
        //************************************************************************
                                            
        //Update video width/height when user presses enter or clicks off
        videoWidthTextField.focusedProperty().addListener((args, oldProp, newProp)->{
            if(newProp){
                videoWidthTextField.setOnKeyPressed((event)-> {
                    if(event.getCode().equals(KeyCode.ENTER)){
                        controlPanelHBox.requestFocus();
                    }
                });
            } else {
                Platform.runLater(()->{
                    if(PreviewModel.getInstance().getVideoLinkBtnActive()){
                        PreviewModel.getInstance().updateVideoSize(videoWidthTextField.getText(), "0");
                    } else {
                        PreviewModel.getInstance().updateVideoSize(videoWidthTextField.getText(), videoHeightTextField.getText());
                    }
                });  
            }
        });
        videoHeightTextField.focusedProperty().addListener((args, oldProp, newProp)->{
            if(newProp){
                videoHeightTextField.setOnKeyPressed((event)-> {
                    if(event.getCode().equals(KeyCode.ENTER)){
                        controlPanelHBox.requestFocus();
                    }
                });
            } else {
                Platform.runLater(()->{
                    if(PreviewModel.getInstance().getVideoLinkBtnActive()){
                        PreviewModel.getInstance().updateVideoSize("0", videoHeightTextField.getText());
                    } else {
                        PreviewModel.getInstance().updateVideoSize(videoWidthTextField.getText(), videoHeightTextField.getText());
                    }
                });  
            }
        });
        
        //Toggle if video should maintain aspect ratio
        videoLinkBtn.setOnAction((event)->{
            Platform.runLater(()->{
                if(PreviewModel.getInstance().getVideoLinkBtnActive()){
                    videoLinkBtn.setId("videoLinkBtn-inactive");
                    videoHeightTextField.setDisable(false);
                    PreviewModel.getInstance().setVideoLinkBtnActive(false);
                    PreviewModel.getInstance().reloadMediaPlayer();
                } else {
                    videoLinkBtn.setId("videoLinkBtn-active");
                    videoHeightTextField.setDisable(true);
                    PreviewModel.getInstance().setVideoLinkBtnActive(true);
                    PreviewModel.getInstance().reloadMediaPlayer();
                }
                VideoTransitionModel.getInstance().editTransitionString();
            });
        });
        
        //LEFT and RIGHT arrow key events to update video volume
        //Focus on Mouse pressed
        //Updates the volume on mouse release
        videoVolumeSlider.setOnKeyPressed((event)->{
            Platform.runLater(()->{
                if(event.getCode().equals(KeyCode.LEFT) || event.getCode().equals(KeyCode.RIGHT)){
                    PreviewModel.getInstance().updateVideoVolume(videoVolumeSlider.getValue());
                }
            });
        });
        videoVolumeSlider.setOnMouseReleased((event) -> {
            Platform.runLater(()->{
                PreviewModel.getInstance().updateVideoVolume(videoVolumeSlider.getValue());
            });
        });
        videoVolumeSlider.setOnMousePressed((event)->{
            videoVolumeSlider.requestFocus();
        });
        
        //LEFT and RIGHT arrow key events to update video playback rate
        //Focus on Mouse pressed
        //Updates the playback rate on mouse release
        videoRateSlider.setOnKeyPressed((event)->{
            Platform.runLater(()->{
                if(event.getCode().equals(KeyCode.LEFT) || event.getCode().equals(KeyCode.RIGHT)){
                    PreviewModel.getInstance().updateVideoRate(videoRateSlider.getValue());
                }
            });
        });
        videoRateSlider.setOnMouseReleased((event) -> {
            Platform.runLater(()->{
                PreviewModel.getInstance().updateVideoRate(videoRateSlider.getValue());
            });
        });
        videoRateSlider.setOnMousePressed((event)->{
            videoRateSlider.requestFocus();
        });
        
        //LEFT and RIGHT arrow key events to update video start time
        //Focus on Mouse pressed
        //Updates the start time on mouse release
        videoSeekSlider.setOnKeyPressed((event)->{
            Platform.runLater(()->{
                if(event.getCode().equals(KeyCode.LEFT) || event.getCode().equals(KeyCode.RIGHT)){
                    PreviewModel.getInstance().updateVideoSeek(videoSeekSlider.getValue());
                }
            });
        });
        videoSeekSlider.setOnMouseReleased((event) -> {
            Platform.runLater(()->{
                PreviewModel.getInstance().updateVideoSeek(videoSeekSlider.getValue());
            });
        });
        videoSeekSlider.setOnMousePressed((event)->{
            videoSeekSlider.requestFocus();
        });
        
        //Prompt user to select a video file with approprate extensions
        //Load the transition and reload the media player
        videoFileBtn.setOnAction((event)->{
            videoFile = videoChooser.showOpenDialog(editMediaView.getScene().getWindow());
            if(videoFile != null){
                try {
                    String tempPath = videoFile.getCanonicalPath();
                    Platform.runLater(()->{
                        VideoTransitionModel.getInstance().setVideoPath(tempPath.replace(",", ""));
                        VideoTransitionModel.getInstance().editTransitionString();
                        PreviewModel.getInstance().reloadMediaPlayer();
                    });
                } catch (IOException ex) {
                    Logger.getLogger(EditViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
                Context.getInstance().setDidSave(false);
            }
        });
        
    }
    
    //For scene controller
    @Override
    public void setParentController(SceneController parentController) {
        this.controller = parentController;
    }
}
