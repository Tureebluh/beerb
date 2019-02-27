/*************************************************************************************************************************
 * Source Code Form License Notice
 *************************************************************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 **************************************************************************************************************************/
/*************************************************************************************************************************
 *   Author: Jarek Thomas
 *
 *   Model for Theatre
 * 
 *   Main Theatre Model that user sources to streaming software (OBS etc)
 ***************************************************************************************************************************/

package main.models;

import main.controllers.TheatreController;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaMarkerEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.swing.Timer;


public class TheatreModel {
    
    private final static TheatreModel INSTANCE = new TheatreModel();
    private TheatreController controller;
    private Map<String, Text> textObjects = new HashMap();
    private Map<String, ImageView> imageObjects = new HashMap();
    private Map<String, MediaPlayer> audioObjects = new HashMap();
    private Map<String, MediaView> videoObjects = new HashMap();
    private MediaView mediaView;
    private MediaPlayer mediaPlayer;
    private Media media;
    private Duration duration;
    private Boolean fadeStarted;
    private ObservableMap<String, Duration> videoMarkers;
    private int timerCounter, textTransitionCounter, imageTransitionCounter, audioTransitionCounter, videoTransitionCounter, standbyCounter;
    private final Timer standbyTimer;
    private Stage stage;
    private AnchorPane theatreAnchorPane;
    
    private final SimpleStringProperty timerText = new SimpleStringProperty();
    private final SimpleStringProperty commercialBtnText = new SimpleStringProperty();
    private final SimpleStringProperty commercialBtnStyle = new SimpleStringProperty();
    
    private TheatreModel(){
        this.commercialBtnText.setValue("Start Commercial Break");
        this.commercialBtnStyle.setValue("-fx-background-color: #228822;");
        this.mediaView = new MediaView();
        this.standbyTimer = new Timer(1000, new StartStandby());
        this.timerCounter = 0;
        this.audioTransitionCounter = 0;
        this.imageTransitionCounter = 0;
        this.textTransitionCounter = 0;
        this.videoTransitionCounter = 0;
        this.standbyCounter = 6;
    }
    public static TheatreModel getInstance(){
        return INSTANCE;
    }
    
    public void setController(TheatreController controller){
        this.controller = controller;
    }
    
    public final void setStage(Stage stage){
        this.stage = stage;
    }
    public final Stage getStage(){
        return this.stage;
    }
    
    public final void setAnchorPane(AnchorPane theatreAnchorPane){
        this.theatreAnchorPane = theatreAnchorPane;
    }
    
    public final Timer getStandbyTimer(){
        return this.standbyTimer;
    }
    
    public Media getMedia(){
        return this.media;
    }
    public void setMedia(Media media){
        this.media = media;
    }
    
    public MediaPlayer getMediaPlayer(){
        return this.mediaPlayer;
    }
    public void setMediaPlayer(MediaPlayer mediaPlayer){
        this.mediaPlayer = mediaPlayer;
    }
    
    public MediaView getMediaView(){
        return this.mediaView;
    }
    public void setMediaView(MediaView mediaView){
        this.mediaView = mediaView;
    }
    
    public final String getTimerText(){
        return timerText.getValue();
    }
    public final void setTimerText(String timerText){
        this.timerText.setValue(timerText); 
    }
    public StringProperty timerTextProperty(){
        return timerText;
    }
    
    public final String getCommercialBtnText(){
        return commercialBtnText.getValue();
    }
    public final void setCommercialBtnText(String commercialBtnText){
        this.commercialBtnText.setValue(commercialBtnText); 
    }
    public StringProperty commercialBtnTextProperty(){
        return commercialBtnText;
    }
    
    public final String getCommercialBtnStyle(){
        return commercialBtnStyle.getValue();
    }
    public final void setCommercialBtnStyle(String commercialBtnStyle){
        this.commercialBtnStyle.setValue(commercialBtnStyle); 
    }
    public StringProperty commercialBtnStyleProperty(){
        return commercialBtnStyle;
    }
    
    public final int getTimerCounter(){
        return timerCounter;
    }
    public final void setTimerCounter(int timerCounter){
        this.timerCounter = timerCounter;
    }
    
    public final int getStandbyCounter(){
        return standbyCounter;
    }
    public final void setStandbyCounter(){
        if(standbyCounter != 0){
            this.standbyCounter--;
        } else {
            this.standbyCounter = 6;
        }
        
    }
    
    //Starts 5 sec timer for user before launching mediaplayer
    public void startCommercialBreak(){
        getStandbyTimer().start();
        stage.show();
        Platform.runLater(()->{
            stage.toFront();
            Context.getInstance().setTheatreRunning(true);
            commercialBtnText.setValue("Starting Commercial Break");
            commercialBtnStyle.setValue("-fx-background-color: #e07a1a;");
        });
        
    }
    
    //Checks state of media player before pausing to avoid crashes
    //Clear scene properties and queue list and delete video markers
    //Fades mediaplayer out over 5 secs
    public void stopCommercialBreak(){
        MediaPlayer.Status status = this.mediaPlayer.getStatus();
        if ( status != MediaPlayer.Status.PAUSED || status != MediaPlayer.Status.READY || status != MediaPlayer.Status.STOPPED){
            this.mediaPlayer.pause();
        }
        Context.getInstance().getProfileQueueList().clear();
        ProfileModel.getInstance().clearSceneProperties();
        setTimerCounter(0);
        deleteVideoMarkers();
        
        Platform.runLater(()->{
            FadeTransition fadeOut = new FadeTransition(Duration.millis(5000), TheatreModel.getInstance().getMediaView());
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setCycleCount(1);
            fadeOut.setAutoReverse(false);
            fadeOut.setInterpolator(Interpolator.EASE_IN);
            fadeOut.playFromStart();
            
            fadeOut.setOnFinished((ActionEvent event) -> {
                mediaPlayer.dispose();
                this.stage.setResizable(true);
                this.stage.hide();
                Context.getInstance().setTheatreRunning(false);
                this.commercialBtnText.setValue("Start Commercial Break");
                this.commercialBtnStyle.setValue("-fx-background-color: #228822");
                
            });
        });
    }
    
    //Loads next profile in queue and create transition objects
    public void nextProfile(){
        try{
            if(!Context.getInstance().getProfileQueueList().isEmpty()){
                ProfileModel.getInstance().loadProfileFromXML(Context.getInstance().getProfileQueueList().get(0).toString());
                createMedia(ProfileModel.getInstance().getVideoProperties().get(0).toString());
                
                if(!AudioTransitionModel.getInstance().getAudioMarkers().isEmpty()){
                    createAudioMarkers();
                }
                if(!TextTransitionModel.getInstance().getTextMarkers().isEmpty()){
                    createTextMarkers();
                }
                if(!ImageTransitionModel.getInstance().getImageMarkers().isEmpty()){
                    createImageMarkers();
                }
                if(!FFTransitionModel.getInstance().getFFMarkers().isEmpty()){
                    createFFMarkers();
                }
                if(!CutTransitionModel.getInstance().getCutMarkers().isEmpty()){
                    createCutMarkers();
                }
                if(!VideoTransitionModel.getInstance().getVideoMarkers().isEmpty()){
                    createVideoMarkers();
                }
                Platform.runLater(()->{
                    loadVideoPlayer();
                });
            } else {
                stopCommercialBreak();
            }
        }catch(Exception ex){
            Alert alert = new Alert(AlertType.INFORMATION);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            alert.setContentText(exceptionText);
            alert.showAndWait();
        }
    }
    
    //Create media object and initialize media markers
    public void createMedia(String videoPath){
        File temp = new File(videoPath);
        media = new Media(temp.toURI().toString());
        videoMarkers = media.getMarkers();
    }
    
    //Create mediaplayer and set to autoplay
    //Loads volume from profile and fades video in and starts video
    //Uses currentTimeProperty to start fadeout of current profile 5 secs before video ends
    public void loadVideoPlayer(){
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setVolume(Double.parseDouble(ProfileModel.getInstance().getVideoProperties().get(1).toString()));
        
        Platform.runLater(()->{
            mediaView.setMediaPlayer(mediaPlayer);
        });
        
        mediaPlayer.setOnReady(()->{
            duration = media.getDuration();
            fadeStarted = false;
            Platform.runLater(()->{
                FadeTransition fadeIn = new FadeTransition(Duration.millis(2000), TheatreModel.getInstance().getMediaView());
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.setCycleCount(1);
                fadeIn.setAutoReverse(false);
                fadeIn.setInterpolator(Interpolator.EASE_IN);
                fadeIn.playFromStart();
            });
        });
        
        mediaPlayer.setOnPlaying(()->{
            Platform.runLater(()->{
                this.stage.setResizable(false);
            });
        });
        
        mediaPlayer.setOnMarker((MediaMarkerEvent event) -> {
            String tempKey = event.getMarker().getKey();
            String[] tempArray = tempKey.split("-");

            if(tempArray[0].equals("Add")){
                switch(tempArray[1]){
                    case TextTransitionModel.TRANSITION_KEY:
                        Platform.runLater(()->{
                            theatreAnchorPane.getChildren().add(textObjects.get(tempArray[2]));
                            FadeTransition fadeIn = new FadeTransition(Duration.millis(1250), textObjects.get(tempArray[2]));
                            fadeIn.setFromValue(0);
                            fadeIn.setToValue(1);
                            fadeIn.setCycleCount(1);
                            fadeIn.setAutoReverse(false);
                            fadeIn.setInterpolator(Interpolator.LINEAR);
                            fadeIn.playFromStart();
                        });
                        break;
                    case ImageTransitionModel.TRANSITION_KEY:
                        Platform.runLater(()->{
                            theatreAnchorPane.getChildren().add(imageObjects.get(tempArray[2]));
                            FadeTransition fadeIn = new FadeTransition(Duration.millis(1250), imageObjects.get(tempArray[2]));
                            fadeIn.setFromValue(0);
                            fadeIn.setToValue(1);
                            fadeIn.setCycleCount(1);
                            fadeIn.setAutoReverse(false);
                            fadeIn.setInterpolator(Interpolator.LINEAR);
                            fadeIn.playFromStart();
                        });
                        break;
                    case FFTransitionModel.TRANSITION_KEY:
                        Platform.runLater(()->{
                            mediaPlayer.setRate(Double.parseDouble(tempArray[2]));
                        });
                        break;
                    case CutTransitionModel.TRANSITION_KEY:
                        Platform.runLater(()->{
                            if(!tempArray[2].equals("*")){
                                mediaPlayer.seek(Duration.millis(Double.parseDouble(tempArray[2])));
                            }
                        });
                        break;
                    case AudioTransitionModel.TRANSITION_KEY:
                        Platform.runLater(()->{
                            MediaPlayer tempPlayer = audioObjects.get(tempArray[2]);
                            tempPlayer.play();
                        });
                        break;
                    case VideoTransitionModel.TRANSITION_KEY:
                        Platform.runLater(()->{
                            MediaView tempView = videoObjects.get(tempArray[2]);
                            theatreAnchorPane.getChildren().add(tempView);
                            FadeTransition fadeIn = new FadeTransition(Duration.millis(1250), tempView);
                            fadeIn.setFromValue(0);
                            fadeIn.setToValue(1);
                            fadeIn.setCycleCount(1);
                            fadeIn.setAutoReverse(false);
                            fadeIn.setInterpolator(Interpolator.LINEAR);
                            fadeIn.playFromStart();
                            
                            tempView.getMediaPlayer().play();
                        });
                        break;
                    default:
                        break;
                }
            } else {
                switch(tempArray[1]){
                    case TextTransitionModel.TRANSITION_KEY:
                        Platform.runLater(()->{
                            FadeTransition fadeOut = new FadeTransition(Duration.millis(1250), textObjects.get(tempArray[2]));
                            fadeOut.setFromValue(1);
                            fadeOut.setToValue(0);
                            fadeOut.setCycleCount(1);
                            fadeOut.setAutoReverse(false);
                            fadeOut.setInterpolator(Interpolator.LINEAR);
                            fadeOut.playFromStart();
                            fadeOut.setOnFinished((ActionEvent ae) -> {
                                theatreAnchorPane.getChildren().remove(textObjects.get(tempArray[2]));
                            });
                        });
                        break;
                    case ImageTransitionModel.TRANSITION_KEY:
                        Platform.runLater(()->{
                            FadeTransition fadeOut = new FadeTransition(Duration.millis(1250), imageObjects.get(tempArray[2]));
                            fadeOut.setFromValue(1);
                            fadeOut.setToValue(0);
                            fadeOut.setCycleCount(1);
                            fadeOut.setAutoReverse(false);
                            fadeOut.setInterpolator(Interpolator.LINEAR);
                            fadeOut.playFromStart();
                            fadeOut.setOnFinished((ActionEvent ae) -> {
                                theatreAnchorPane.getChildren().remove(imageObjects.get(tempArray[2]));
                            });
                        });
                        break;
                    case FFTransitionModel.TRANSITION_KEY:
                        Platform.runLater(()->{
                            mediaPlayer.setRate(Double.parseDouble(tempArray[2]));
                        });
                        break;
                    case AudioTransitionModel.TRANSITION_KEY:
                        Platform.runLater(()->{
                            MediaPlayer tempPlayer = audioObjects.get(tempArray[2]);
                            tempPlayer.stop();
                        });
                        break;
                    case VideoTransitionModel.TRANSITION_KEY:
                        Platform.runLater(()->{
                            FadeTransition fadeOut = new FadeTransition(Duration.millis(1250), videoObjects.get(tempArray[2]));
                            fadeOut.setFromValue(1);
                            fadeOut.setToValue(0);
                            fadeOut.setCycleCount(1);
                            fadeOut.setAutoReverse(false);
                            fadeOut.setInterpolator(Interpolator.LINEAR);
                            fadeOut.playFromStart();
                            fadeOut.setOnFinished((ActionEvent ae) -> {
                                theatreAnchorPane.getChildren().remove(videoObjects.get(tempArray[2]));
                                videoObjects.get(tempArray[2]).getMediaPlayer().stop();
                            });
                        });
                        break;
                    default:
                        break;
                }
            }
        });
        
        mediaPlayer.currentTimeProperty().addListener((ChangeListener) -> {
            Duration currentTime = mediaPlayer.getCurrentTime();
            Platform.runLater(()->{
                if(duration.greaterThan(Duration.ZERO)) {
                    if(((int)duration.toSeconds() - (int)currentTime.toSeconds()) == 2 && !fadeStarted){
                        Context.getInstance().getProfileQueueList().remove(0);
                        ProfileModel.getInstance().clearSceneProperties();
                        fadeStarted = true;
                        if(!Context.getInstance().getProfileQueueList().isEmpty()){
                            Platform.runLater(()->{
                                FadeTransition fadeOut = new FadeTransition(Duration.millis(1500), TheatreModel.getInstance().getMediaView());
                                fadeOut.setFromValue(1);
                                fadeOut.setToValue(0);
                                fadeOut.setCycleCount(1);
                                fadeOut.setAutoReverse(false);
                                fadeOut.setInterpolator(Interpolator.EASE_IN);
                                fadeOut.playFromStart();
                                fadeOut.setOnFinished((ActionEvent event) -> {
                                    deleteVideoMarkers();
                                    nextProfile();
                                });
                            });
                        } else {
                                stopCommercialBreak();
                        }
                    }
                }
            });  
        });
    }
    
    //Loops through map to create objects for each transition string value
    //Created objects are stored in another map for retrieval by mediaplayer onMarker listener
    public void createTextMarkers(){
        Iterator it = TextTransitionModel.getInstance().getTextMarkersMap().entrySet().iterator();
        
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            
            String[] tempArray = pair.getValue().toString().split(",");
            TextTransitionModel.getInstance().setFontSize(Double.parseDouble(tempArray[1]));
            Text tempText = TextModel.getInstance().createTextObject(tempArray[0], Double.parseDouble(tempArray[1]));
            tempText.setX(Double.parseDouble(tempArray[2]));
            tempText.setY(Double.parseDouble(tempArray[3]) + (tempText.getFont().getSize() - 15));
            tempText.setScaleX(1.04);
            tempText.setOpacity(0);
            
            videoMarkers.put("Add-Text-" + textTransitionCounter, Duration.millis( Integer.parseInt(tempArray[4]) ));
            
            if(!tempArray[5].equals("*")){
                videoMarkers.put("Remove-Text-" + textTransitionCounter, Duration.millis( Integer.parseInt(tempArray[5]) ));
            }
            
            String message = "";
            for(int i = 6; i < tempArray.length; i++){
                if(i != 6){
                    message += ",";
                }
                message += tempArray[i];
            }
            tempText.setText(message);
            
            textObjects.put("" + textTransitionCounter, tempText);
            it.remove();
            textTransitionCounter++;
        }
        textTransitionCounter = 0;
    }
    
    //Loops through map to create objects for each transition string value
    //Created objects are stored in another map for retrieval by mediaplayer onMarker listener
    public void createImageMarkers(){
        Iterator it = ImageTransitionModel.getInstance().getImageMarkersMap().entrySet().iterator();
        
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            
            String[] tempArray = pair.getValue().toString().split(",");
            
            ImageView tempImage = ImageTransitionModel.getInstance().createImageObject(tempArray[0]);
            tempImage.setX(Double.parseDouble(tempArray[1]));
            tempImage.setY(Double.parseDouble(tempArray[2]));
            tempImage.setFitWidth(Double.parseDouble(tempArray[3]));
            tempImage.setFitHeight(Double.parseDouble(tempArray[4]));
            tempImage.setPreserveRatio(Boolean.parseBoolean(tempArray[7]));
            tempImage.setOpacity(0);
            
            videoMarkers.put("Add-Image-" + imageTransitionCounter, Duration.millis( Integer.parseInt(tempArray[5]) ));

            if(!tempArray[6].equals("*")){
                videoMarkers.put("Remove-Image-" + imageTransitionCounter, Duration.millis( Integer.parseInt(tempArray[6]) ));
            }
            
            imageObjects.put(imageTransitionCounter + "", tempImage);
            
            imageTransitionCounter++;
        }
        imageTransitionCounter = 0;
    }
    
    //Loops through map to create references for each transition string value
    //Key-value is enough information passed to mediaPlayer to perform action
    public void createFFMarkers(){
        Iterator it = FFTransitionModel.getInstance().getFFMarkersMap().entrySet().iterator();
        
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String[] tempArray = pair.getValue().toString().split(",");
            
            videoMarkers.put("Add-FF-" + tempArray[2], Duration.millis( Integer.parseInt(tempArray[0]) ));
            
            if(!tempArray[1].equals("*")){
                videoMarkers.put("Remove-FF-" + tempArray[3], Duration.millis( Integer.parseInt(tempArray[1]) ));
            }
        }
    }
    
    //Loops through map to create references for each transition string value
    //Key-value is enough information passed to mediaPlayer to perform action
    public void createCutMarkers(){
        Iterator it = CutTransitionModel.getInstance().getCutMarkersMap().entrySet().iterator();
        
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String[] tempArray = pair.getValue().toString().split(",");
            
            videoMarkers.put("Add-Cut-" + tempArray[1], Duration.millis( Integer.parseInt(tempArray[0]) ));
        }
    }
    
    //Loops through map to create references for each transition string value
    //Created objects are stored in another map for retrieval by mediaplayer onMarker listener
    public void createAudioMarkers(){
        Iterator it = AudioTransitionModel.getInstance().getAudioMarkersMap().entrySet().iterator();
        
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            
            String[] tempArray = pair.getValue().toString().split(",");
            
            MediaPlayer tempPlayer = AudioTransitionModel.getInstance().createAudioObject(tempArray[0]);
            tempPlayer.setAutoPlay(false);
            tempPlayer.setRate(Double.parseDouble(tempArray[3]));
            tempPlayer.setVolume(Double.parseDouble(tempArray[4]));
            tempPlayer.setOnReady(()->{
                tempPlayer.seek(tempPlayer.getMedia().getDuration().multiply(Double.parseDouble(tempArray[5])));
            });
            
            videoMarkers.put("Add-Audio-" + audioTransitionCounter, Duration.millis( Integer.parseInt(tempArray[1]) ));

            if(!tempArray[2].equals("*")){
                videoMarkers.put("Remove-Audio-" + audioTransitionCounter, Duration.millis( Integer.parseInt(tempArray[2]) ));
            }
            
            audioObjects.put(audioTransitionCounter + "", tempPlayer);
            audioTransitionCounter++;
        }
        audioTransitionCounter = 0;
    }
    
    //Loops through map to create references for each transition string value
    //Created objects are stored in another map for retrieval by mediaplayer onMarker listener
    public void createVideoMarkers(){
        VideoTransitionModel.getInstance().getVideoMarkersMap().forEach((key,value)->{
            String[] tempArray = value.toString().split(",");
            
            MediaPlayer tempPlayer = VideoTransitionModel.getInstance().createVideoObject(tempArray[0]);
            tempPlayer.setAutoPlay(false);
            tempPlayer.setRate(Double.parseDouble(tempArray[3]));
            tempPlayer.setVolume(Double.parseDouble(tempArray[4]));
            tempPlayer.setOnReady(()->{
                tempPlayer.seek(tempPlayer.getMedia().getDuration().multiply(Double.parseDouble(tempArray[9])));
            });
            
            MediaView tempView = new MediaView(tempPlayer);
            tempView.setX(Double.parseDouble(tempArray[5]));
            tempView.setY(Double.parseDouble(tempArray[6]));
            tempView.setFitWidth(Double.parseDouble(tempArray[7]));
            tempView.setFitHeight(Double.parseDouble(tempArray[8]));
            tempView.setPreserveRatio(Boolean.parseBoolean(tempArray[10]));
            
            videoMarkers.put("Add-Video-" + videoTransitionCounter, Duration.millis( Integer.parseInt(tempArray[1]) ));

            if(!tempArray[2].equals("*")){
                videoMarkers.put("Remove-Video-" + videoTransitionCounter, Duration.millis( Integer.parseInt(tempArray[2]) ));
            }
            
            videoObjects.put(videoTransitionCounter + "", tempView);
            videoTransitionCounter++;
        });
        videoTransitionCounter = 0;
    }
    
    //Stop all players and remove all objects from scene
    //Clear all objects out of list
    public void deleteVideoMarkers(){
        try{
            audioObjects.forEach((key,tempPlayer)->{
                MediaPlayer.Status status = tempPlayer.getStatus();
                if (status != MediaPlayer.Status.STOPPED  || status != MediaPlayer.Status.HALTED){
                   tempPlayer.stop();
                }
                tempPlayer.dispose();
            });
            videoObjects.forEach((key,tempView)->{
                MediaPlayer.Status status = tempView.getMediaPlayer().getStatus();
                if (status != MediaPlayer.Status.STOPPED  || status != MediaPlayer.Status.HALTED || status != MediaPlayer.Status.PAUSED){
                   tempView.getMediaPlayer().stop();
                }
                tempView.getMediaPlayer().dispose();
            });
            theatreAnchorPane.getChildren().removeAll(videoObjects.values());
            theatreAnchorPane.getChildren().removeAll(imageObjects.values());
            theatreAnchorPane.getChildren().removeAll(textObjects.values());
            videoObjects.clear();
            audioObjects.clear();
            imageObjects.clear();
            textObjects.clear();
            
            videoMarkers.clear();
        }catch(Exception e){e.printStackTrace();}
    }
    
    //Used for displaying countdown timer to user before video plays
    private class StartStandby implements ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent ae) {
            setStandbyCounter();
            switch(getStandbyCounter()){
                case 0:
                    Platform.runLater(()->{
                        timerTextProperty().set(" ");
                        ProfileModel.getInstance().clearSceneProperties();
                        nextProfile();
                        commercialBtnText.setValue("Stop Commercial Break");
                        commercialBtnStyle.setValue("-fx-background-color: #882222;");
                        getStandbyTimer().stop();
                    });
                    break;
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    Platform.runLater(()->{
                        timerTextProperty().set(timeConversion(getStandbyCounter()));
                    });
                    break;
            }
        }
    }
    
    //Converts to a more easily human readable format
    public String timeConversion(int elapsed){
        
        int elapsedHours = elapsed / (60 * 60);
        if (elapsedHours > 0) {
            elapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = elapsed / 60;
        int elapsedSeconds = elapsed - elapsedHours * 60 * 60 
                           - elapsedMinutes * 60;
 
        if (elapsedHours > 0) {
            return String.format("%d:%02d:%02d", elapsedHours, 
                    elapsedMinutes, elapsedSeconds);
        } else {
            return String.format("%02d:%02d",elapsedMinutes, 
                    elapsedSeconds);
        }
    }
}
