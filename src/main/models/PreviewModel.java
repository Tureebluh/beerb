/*************************************************************************************************************************
 * Source Code Form License Notice
 *************************************************************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 **************************************************************************************************************************/
/*************************************************************************************************************************
 *   Author: Jarek Thomas
 *
 *   Model for Preview/Editing Scene
 * 
 *   Handles all events with Editing Video Player and transitions
 ***************************************************************************************************************************/

package main.models;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaMarkerEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import javax.swing.Timer;

public class PreviewModel {
    private final static PreviewModel INSTANCE = new PreviewModel();
    
    private final SimpleStringProperty startTimeValue = new SimpleStringProperty();
    private final SimpleStringProperty stopTimeValue = new SimpleStringProperty();
    private final SimpleStringProperty textProfileCBValue = new SimpleStringProperty();
    private final SimpleStringProperty playPauseId = new SimpleStringProperty();
    private final SimpleStringProperty videoTimeText = new SimpleStringProperty();
    private final SimpleStringProperty videoVolumeLabel = new SimpleStringProperty();
    private final SimpleDoubleProperty timeSliderValue = new SimpleDoubleProperty();
    private final SimpleDoubleProperty volumeSliderValue = new SimpleDoubleProperty();
    private final SimpleBooleanProperty timeSliderDisable = new SimpleBooleanProperty();
    private final SimpleBooleanProperty timeSliderValueChanging = new SimpleBooleanProperty();
    private final SimpleBooleanProperty volumeSliderValueChanging = new SimpleBooleanProperty();
    private final SimpleBooleanProperty autoPause = new SimpleBooleanProperty();
    private final SimpleStringProperty startTimestampText = new SimpleStringProperty();
    private final SimpleStringProperty stopTimestampText = new SimpleStringProperty();

    private final Map<String, TextFlow> textObjects = new HashMap();
    private final Map<String, ImageView> imageObjects = new HashMap();
    private final Map<String, MediaPlayer> audioObjects = new HashMap();
    private final Map<String, MediaView> videoObjects = new HashMap();
    private final Set<MediaPlayer> activeAudio = new HashSet();
    private final Set<MediaView> activeVideo = new HashSet();
    
    private ObservableMap<String, Duration> videoMarkersObMap;
    private final Set<String> profileMarkersHashSet = new HashSet();
    private int textTransitionCounter, imageTransitionCounter, audioTransitionCounter, videoTransitionCounter;
    private MediaView mediaView;
    private Media media;
    private MediaPlayer mediaPlayer;
    private Duration duration;
    private AnchorPane previewAnchorPane;
    private StackPane previewStackPane;
    private String currentProfile, currentTransitionSelected;
    private int eventCounter;
    private boolean stopTimeClicked = false, rewindCut = false, imageLinkBtnActive = true, videoLinkBtnActive = true, editTransitionClicked = false;
    private Timer resizeIncreaseTimer, resizeDecreaseTimer;
    
    
    private PreviewModel(){
        this.mediaView = new MediaView();
        this.startTimestampText.set("");
        this.stopTimestampText.set("");
        setStartTimeValue("00:00");
        setStopTimeValue("00:00");
        playPauseId.setValue("playBtn");
        this.textTransitionCounter = 0;
        this.imageTransitionCounter = 0;
        this.audioTransitionCounter = 0;
        this.videoTransitionCounter = 0;
        this.eventCounter = 1;
        this.videoMarkersObMap = FXCollections.observableHashMap();
    }
    
    public final int getEventCounter(){
        return eventCounter;
    }
    public final void setEventCounter(){
        this.eventCounter++;
    }
    
    public final Timer getResizeIncreaseTimer(){
        return this.resizeIncreaseTimer;
    }
    public final void setResizeIncreaseTimer(Timer resizeIncreaseTimer){
        this.resizeIncreaseTimer = resizeIncreaseTimer;
    }
    
    public final Timer getResizeDecreaseTimer(){
        return this.resizeDecreaseTimer;
    }
    public final void setResizeDecreaseTimer(Timer resizeDecreaseTimer){
        this.resizeDecreaseTimer = resizeDecreaseTimer;
    }
    
    public static PreviewModel getInstance(){
        return INSTANCE;
    }
    
    public final void setAnchorPane(AnchorPane previewAnchorPane){
        this.previewAnchorPane = previewAnchorPane;
    }
    public AnchorPane getAnchorPane(){
        return this.previewAnchorPane;
    }
    
    public final void setStackPane(StackPane previewStackPane){
        this.previewStackPane = previewStackPane;
    }
    public StackPane getStackPane(){
        return this.previewStackPane;
    }
    
    public Map getTextObjects(){
        return textObjects;
    }
    public Map getImageObjects(){
        return imageObjects;
    }
    public Map getAudioObjects(){
        return audioObjects;
    }
    public Map getVideoObjects(){
        return videoObjects;
    }
    public Set getActiveAudio(){
        return activeAudio;
    }
    public Set getActiveVideo(){
        return activeVideo;
    }
    
    public final Set getProfileMarkersSet(){
        return this.profileMarkersHashSet;
    }
    
    public final String getStartTimestamp(){
        return startTimestampText.getValue();
    }
    //Check if timestamp exist and recursively add 1ms until its find a free one
    public final void setStartTimestamp(String startTimestampText){
        if(!PreviewModel.getInstance().getProfileMarkersSet().contains(startTimestampText)){
            if(Integer.parseInt(startTimestampText) < 20){
                setStartTimestamp(20 + "");
                return;
            }
            this.startTimestampText.setValue(startTimestampText);
        } else {
            int temp = (Integer.parseInt(startTimestampText)) + 1;
            setStartTimestamp(temp + "");
        } 
    }
    public StringProperty startTimestampTextProperty(){
        return startTimestampText;
    }
    public final String getStopTimestamp(){
        return stopTimestampText.getValue();
    }
    //Check if timestamp exist and recursively add 1ms until its find a free one
    public final void setStopTimestamp(String stopTimestampText){
        if(!PreviewModel.getInstance().getProfileMarkersSet().contains(stopTimestampText)){
            if(!stopTimestampText.equals("*")){
                if(Integer.parseInt(stopTimestampText) < 20){
                    setStopTimestamp(20 + "");
                    return;
                }
            }
            
            this.stopTimestampText.setValue(stopTimestampText);
        } else if(!stopTimestampText.equals("*")){
            int temp = (Integer.parseInt(stopTimestampText)) + 5;
            setStopTimestamp(temp + "");
        } else {
            this.stopTimestampText.setValue("*");
        }
    }
    public StringProperty stopTimestampTextProperty(){
        return stopTimestampText;
    }
    
    public String getStartTimeValue(){
        return this.startTimeValue.getValue();
    }
    public void setStartTimeValue(String startTime){
        this.startTimeValue.setValue(startTime);
    }
    public StringProperty startTimeValueProperty(){
        return startTimeValue;
    }
    
    public Double getTimeSliderValue(){
        return this.timeSliderValue.getValue();
    }
    public void setTimeSliderValue(Double timeSliderValue){
        this.timeSliderValue.setValue(timeSliderValue);
    }
    public DoubleProperty timeSliderValueProperty(){
        return timeSliderValue;
    }
    
    public Double getVolumeSliderValue(){
        return this.volumeSliderValue.getValue();
    }
    public void setVolumeSliderValue(Double volumeSliderValue){
        this.volumeSliderValue.setValue(volumeSliderValue);
    }
    public DoubleProperty volumeSliderValueProperty(){
        return volumeSliderValue;
    }
    
    public String getStopTimeValue(){
        return this.stopTimeValue.getValue();
    }
    public void setStopTimeValue(String stopTime){
        this.stopTimeValue.setValue(stopTime);
    }
    public StringProperty stopTimeValueProperty(){
        return stopTimeValue;
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
    
    public String getPlayPauseId(){
        return this.playPauseId.getValue();
    }
    public void setPlayPauseId(String playPauseId){
        this.playPauseId.setValue(playPauseId);
    }
    public StringProperty playPauseIdProperty(){
        return playPauseId;
    }
    
    public String getVideoTimeText(){
        return this.videoTimeText.getValue();
    }
    public void setVideoTimeText(String videoTimeText){
        this.videoTimeText.setValue(videoTimeText);
    }
    public StringProperty videoTimeTextProperty(){
        return videoTimeText;
    }
    
    public String getVideoVolumeLabel(){
        return this.videoVolumeLabel.getValue();
    }
    public void setVideoVolumeLabel(String videoVolumeLabel){
        this.videoVolumeLabel.setValue(videoVolumeLabel);
    }
    public StringProperty videoVolumeLabelProperty(){
        return videoVolumeLabel;
    }
    
    public Boolean getTimeSliderDisable(){
        return this.timeSliderDisable.getValue();
    }
    public void setTimeSliderDisable(Boolean timeSliderDisable){
        this.timeSliderDisable.setValue(timeSliderDisable);
    }
    public BooleanProperty timeSliderDisableProperty(){
        return this.timeSliderDisable;
    }
    
    public Boolean getAutoPause(){
        return this.autoPause.getValue();
    }
    public void setAutoPause(Boolean autoPauseDisable){
        this.autoPause.setValue(autoPauseDisable);
    }
    public BooleanProperty autoPauseProperty(){
        return this.autoPause;
    }
    
    public Boolean getRewindCut(){
        return this.rewindCut;
    }
    public void setRewindCut(Boolean rewindCut){
        this.rewindCut = rewindCut;
    }
    
    public Boolean getImageLinkBtnActive(){
        return this.imageLinkBtnActive;
    }
    public void setImageLinkBtnActive(Boolean imageLinkBtnActive){
        this.imageLinkBtnActive = imageLinkBtnActive;
    }
    
    public Boolean getVideoLinkBtnActive(){
        return this.videoLinkBtnActive;
    }
    public void setVideoLinkBtnActive(Boolean videoLinkBtnActive){
        this.videoLinkBtnActive = videoLinkBtnActive;
    }
    
    public Boolean getEditTransitionClicked(){
        return this.editTransitionClicked;
    }
    public void setEditTransitionClicked(Boolean editTransitionClicked){
        this.editTransitionClicked = editTransitionClicked;
    }
    
    public Boolean getTimeSliderValueChanging(){
        return this.timeSliderValueChanging.getValue();
    }
    public void setTimeSliderValueChanging(Boolean timeSliderValueChanging){
        this.timeSliderValueChanging.setValue(timeSliderValueChanging);
    }
    public BooleanProperty timeSliderValueChangingProperty(){
        return this.timeSliderValueChanging;
    }
    
    public Boolean getVolumeSliderValueChanging(){
        return this.volumeSliderValueChanging.getValue();
    }
    public void setVolumeSliderValueChanging(Boolean volumeSliderValueChanging){
        this.volumeSliderValueChanging.setValue(volumeSliderValueChanging);
    }
    public BooleanProperty volumeSliderValueChangingProperty(){
        return this.volumeSliderValueChanging;
    }
    
    public MediaView getMediaView(){
        return this.mediaView;
    }
    public void setMediaView(MediaView mediaView){
        this.mediaView = mediaView;
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
    
    public Duration getDuration(){
        return this.duration;
    }
    
    public void setCurrentProfile(String currentProfile){
        this.currentProfile = currentProfile;
    }
    public String getCurrentProfile(){
        return this.currentProfile;
    }
    
    public final String getCurrentTransition(){
        return this.currentTransitionSelected;
    }
    public final void setCurrentTransition(String currentTransition){
        this.currentTransitionSelected = currentTransition;
    }
    
    public void startPreview(String videoPath){
        createMedia(videoPath);
    }
    
    //Create media is called BEFORE loadvideoplayer() to create all transitions before video loads
    public void createMedia(String videoPath){
        File temp = new File(videoPath);
        Media media = new Media(temp.toURI().toString());
        this.media = media;
        videoMarkersObMap = media.getMarkers();
        
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
        
        loadVideoPlayer();
    }
    
    //Set all properties to default values
    public void clearTransitionProperties(){
        setStartTimestamp("0");
        setStopTimestamp("0");
        setCurrentTransition("");
        setVideoTimeText("00:00/00:00");
        setVolumeSliderValue(100.0);
    }
    
    //Check state of mediaPlayer to shutdown properly and delete all transitions
    public void closeMediaPlayer(){
        MediaPlayer.Status status = this.mediaPlayer.getStatus();
        if (status != MediaPlayer.Status.STOPPED  || status != MediaPlayer.Status.HALTED){
           this.mediaPlayer.stop();
        }
        this.mediaPlayer.dispose();
        deleteAllMarkers();
    }
    
    //Completely close player and rebuild player and transitions.
    //Needs optimized to only recreate the current object being edited
    public void reloadMediaPlayer(){
        Platform.runLater(()->{
            closeMediaPlayer();
            String tempString = ProfileModel.getInstance().getVideoProperties().get(0).toString();
            startPreview(tempString);
        });
    }
    
    //Loads new mediaplayer when called and loads parameters into UI elements onReady
    //Uses built-in onmarker event to load transitions
    //Current volume property and currentTime property have listeners to update UI
    public void loadVideoPlayer(){
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        this.mediaPlayer = mediaPlayer;
        this.mediaView.setMediaPlayer(mediaPlayer);
        Platform.runLater(()->{
            if(previewAnchorPane.getChildren().isEmpty()){
                previewAnchorPane.getChildren().add(this.mediaView);
            }
        });
        
        Platform.runLater(()->{
            playPauseId.setValue("playBtn");
        });
        
        mediaPlayer.setOnEndOfMedia(()->{
            reloadMediaPlayer();
        });
        mediaPlayer.setOnPaused(()->{
            Platform.runLater(()->{
                playPauseId.setValue("playBtn");
                activeAudio.forEach((tempPlayer) -> {
                    tempPlayer.pause();
                });
                activeVideo.forEach((tempView)->{
                    tempView.getMediaPlayer().pause();
                });
            });
        });
        mediaPlayer.setOnPlaying(()->{
            Platform.runLater(()->{
                playPauseId.setValue("pauseBtn");
                activeAudio.forEach((tempPlayer) -> {
                    tempPlayer.play();
                });
                activeVideo.forEach((tempView)->{
                    tempView.getMediaPlayer().play();
                });
            });
        });
        mediaPlayer.setOnReady(()->{
            duration = media.getDuration();
            
            if(stopTimeClicked){
                mediaPlayer.seek(Duration.millis(Double.parseDouble(getStopTimestamp()) + 50));
            } else if(!getStartTimestamp().isEmpty()){
                mediaPlayer.seek(Duration.millis(Double.parseDouble(getStartTimestamp()) - 50));
            }
            
            Platform.runLater(()->{
                Double tempVolume = Double.parseDouble(ProfileModel.getInstance().getVideoProperties().get(1).toString());
                mediaPlayer.setVolume(tempVolume);
                setVolumeSliderValue(tempVolume * 100);
                setVideoVolumeLabel((tempVolume.intValue() * 100) + "");
                Duration currentTime = mediaPlayer.getCurrentTime();
                videoTimeText.setValue(formatTime(currentTime, duration));
                mediaPlayer.play();
            });
            
            stopTimeClicked = false;
        });
        mediaPlayer.setOnMarker((MediaMarkerEvent event) -> {
            try {
                
                String tempKey = event.getMarker().getKey();
                String[] tempArray = tempKey.split("-");
                
                if(autoPause.getValue() || editTransitionClicked){
                    MediaPlayer.Status status = PreviewModel.getInstance().getMediaPlayer().getStatus();
                    if (status == MediaPlayer.Status.UNKNOWN  || status == MediaPlayer.Status.HALTED){
                        System.out.println(event.toString() + "  " + status);
                        return;
                    }
                    if ( status != MediaPlayer.Status.PAUSED || status != MediaPlayer.Status.READY || status != MediaPlayer.Status.STOPPED){
                        PreviewModel.getInstance().getMediaPlayer().pause();
                    }
                    setEditTransitionClicked(false);
                }
                
                if(tempArray[0].equals("Add")){
                    switch(tempArray[1]){
                        case TextTransitionModel.TRANSITION_KEY:
                            Platform.runLater(()->{
                                if(!previewAnchorPane.getChildren().contains(textObjects.get(tempArray[2]))){
                                    previewAnchorPane.getChildren().add(textObjects.get(tempArray[2]));
                                    DraggableModel.getInstance().makeDraggable(textObjects.get(tempArray[2]));
                                }
                            });
                            break;
                        case ImageTransitionModel.TRANSITION_KEY:
                            Platform.runLater(()->{
                                if(!previewAnchorPane.getChildren().contains(imageObjects.get(tempArray[2]))){
                                    if(imageObjects.get(tempArray[2]) != null){
                                        previewAnchorPane.getChildren().add(imageObjects.get(tempArray[2]));
                                        DraggableModel.getInstance().makeDraggable(imageObjects.get(tempArray[2]));
                                    }
                                }
                            });
                            break;
                        case FFTransitionModel.TRANSITION_KEY:
                            Platform.runLater(()->{
                                mediaPlayer.setRate(Double.parseDouble(tempArray[2])); //Need to reference object in videoMarkers 
                            });
                            break;
                        case CutTransitionModel.TRANSITION_KEY:
                            Platform.runLater(()->{
                                if(!tempArray[2].equals("*")){
                                    mediaPlayer.seek(Duration.millis(Double.parseDouble(tempArray[2])));
                                    videoMarkersObMap.remove(tempKey);
                                }
                            });
                            break;
                        case AudioTransitionModel.TRANSITION_KEY:
                            Platform.runLater(()->{
                                MediaPlayer tempPlayer = audioObjects.get(tempArray[2]);
                                if(tempPlayer != null){
                                    tempPlayer.play();
                                    activeAudio.add(tempPlayer);
                                }
                            });
                            break;
                        case VideoTransitionModel.TRANSITION_KEY:
                            Platform.runLater(()->{
                                MediaView tempView = videoObjects.get(tempArray[2]);
                                if(tempView != null){
                                    if(!previewAnchorPane.getChildren().contains(tempView)){
                                        previewAnchorPane.getChildren().add(tempView);
                                        DraggableModel.getInstance().makeDraggable(tempView);
                                        tempView.getMediaPlayer().play();
                                        activeVideo.add(tempView);
                                    }
                                }
                            });
                            break;
                        default:
                            break;
                    }
                } else {
                    switch(tempArray[1]){
                        case TextTransitionModel.TRANSITION_KEY:
                            Platform.runLater(()->{
                                previewAnchorPane.getChildren().remove(textObjects.get(tempArray[2]));
                            });
                            
                            break;
                        case ImageTransitionModel.TRANSITION_KEY:
                            Platform.runLater(()->{
                                previewAnchorPane.getChildren().remove(imageObjects.get(tempArray[2]));
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
                                if(tempPlayer != null){
                                    tempPlayer.stop();
                                }
                                activeAudio.remove(tempPlayer);
                            });
                            break;
                        case VideoTransitionModel.TRANSITION_KEY:
                            Platform.runLater(()->{
                                MediaView tempView = videoObjects.get(tempArray[2]);
                                if(tempView != null){
                                    if(previewAnchorPane.getChildren().contains(tempView)){
                                        previewAnchorPane.getChildren().remove(tempView);
                                        tempView.getMediaPlayer().stop();
                                        activeVideo.remove(tempView);
                                    }
                                }
                            });
                            break;
                        default:
                            break;
                    }
                }
            }catch(Exception e){e.printStackTrace();}
        });
        mediaPlayer.volumeProperty().addListener((ChangeListener) -> {
            Integer tempDouble = (int)(mediaPlayer.getVolume() * 100);
            Platform.runLater(()->{
                if(!volumeSliderValueChanging.getValue()){
                    volumeSliderValue.setValue(tempDouble);
                    videoVolumeLabel.setValue(tempDouble.toString());
                }
            });
        });
        mediaPlayer.currentTimeProperty().addListener((ChangeListener) -> {
            Duration currentTime = mediaPlayer.getCurrentTime();
            Platform.runLater(()->{
                timeSliderDisable.setValue(duration.isUnknown());
                if(!timeSliderDisable.getValue() && duration.greaterThan(Duration.ZERO) && !timeSliderValueChanging.getValue()){
                    timeSliderValue.setValue(currentTime.divide(duration).toMillis() * 100.0);
                }
                videoTimeText.setValue(formatTime(currentTime, duration));
            });   
        });
    }
    
    //Loops through map to create objects for each transition string value
    //Created objects are stored in another map for retrieval by mediaplayer onMarker listener
    //Text object placed in TextFlow for compatibiilty with DraggableModel
    public void createTextMarkers(){
        TextTransitionModel.getInstance().getTextMarkersMap().forEach((key, value) ->{
            
            String[] tempArray = value.toString().split(",");
            Text tempText = TextModel.getInstance().createTextObject(tempArray[0], (Double.parseDouble(tempArray[1]) + 2) * Context.getInstance().getHeightRatio());
            
            tempText.setX(Double.parseDouble(tempArray[2]) * Context.getInstance().getWidthRatio());
            tempText.setY(Double.parseDouble(tempArray[3]) * Context.getInstance().getHeightRatio());
            
            videoMarkersObMap.put("Add-Text-" + key, Duration.millis( Integer.parseInt(tempArray[4]) ));
            getProfileMarkersSet().add(tempArray[4]);

            if(!tempArray[5].equals("*")){
                videoMarkersObMap.put("Remove-Text-" + key, Duration.millis( Integer.parseInt(tempArray[5]) ));
                getProfileMarkersSet().add(tempArray[5]);
            }
            
            String message = "";
            for(int i = 6; i < tempArray.length; i++){
                if(i != 6){
                    message += ",";
                }
                message += tempArray[i];
            }
            tempText.setText(message);
            TextFlow tempTextFlow = new TextFlow(tempText);
            
            tempTextFlow.setTranslateX((Double.parseDouble(tempArray[2]) * Context.getInstance().getWidthRatio()) + Context.getInstance().getLetterBoxingCurrent() / 2);
            tempTextFlow.setTranslateY(Double.parseDouble(tempArray[3]) * Context.getInstance().getHeightRatio());
            
            textObjects.put(key + "", tempTextFlow);
            
        });
    }
    
    //Loops through map to create objects for each transition string value
    //Created objects are stored in another map for retrieval by mediaplayer onMarker listener
    public void createImageMarkers(){
        ImageTransitionModel.getInstance().getImageMarkersMap().forEach((key, value)->{
        
            String[] tempArray = value.toString().split(",");
            
            ImageView tempImage = ImageTransitionModel.getInstance().createImageObject(tempArray[0]);
            
            tempImage.setX((Double.parseDouble(tempArray[1]) * Context.getInstance().getWidthRatio()) + Context.getInstance().getLetterBoxingCurrent() / 2);
            tempImage.setY(Double.parseDouble(tempArray[2]) * Context.getInstance().getHeightRatio());
            tempImage.setFitWidth(Double.parseDouble(tempArray[3]) * Context.getInstance().getWidthRatio());
            tempImage.setFitHeight(Double.parseDouble(tempArray[4]) * Context.getInstance().getHeightRatio());
            tempImage.setPreserveRatio(Boolean.parseBoolean(tempArray[7]));
            
            videoMarkersObMap.put("Add-Image-" + key, Duration.millis( Integer.parseInt(tempArray[5]) ));
            getProfileMarkersSet().add(tempArray[5]);

            if(!tempArray[6].equals("*")){
                videoMarkersObMap.put("Remove-Image-" + key, Duration.millis( Integer.parseInt(tempArray[6]) ));
                getProfileMarkersSet().add(tempArray[6]);
            }
            
            imageObjects.put(key + "", tempImage);
        });
    }
    
    //Loops through map to create references for each transition string value
    //Key-value is enough information passed to mediaPlayer to perform action
    public void createFFMarkers(){
        FFTransitionModel.getInstance().getFFMarkersMap().forEach((key, value)->{
            String[] tempArray = value.toString().split(",");
            videoMarkersObMap.put("Add-FF-" + tempArray[2], Duration.millis( Integer.parseInt(tempArray[0]) ));
            
            if(!tempArray[1].equals("*")){
                videoMarkersObMap.put("Remove-FF-" + tempArray[3], Duration.millis( Integer.parseInt(tempArray[1]) ));
            }
        });
    }
    
    //Loops through map to create references for each transition string value
    //Key-value is enough information passed to mediaPlayer to perform action
    public void createCutMarkers(){
        CutTransitionModel.getInstance().getCutMarkersMap().forEach((key, value)->{
            String[] tempArray = value.toString().split(",");
            getProfileMarkersSet().add(tempArray[0]);
            videoMarkersObMap.put("Add-Cut-" + tempArray[1], Duration.millis( Integer.parseInt(tempArray[0]) ));
        });
    }
    
    //Loops through map to create references for each transition string value
    //Created objects are stored in another map for retrieval by mediaplayer onMarker listener
    public void createAudioMarkers(){
        AudioTransitionModel.getInstance().getAudioMarkersMap().forEach((key, value)->{
        
            String[] tempArray = value.toString().split(",");
            
            MediaPlayer tempPlayer = AudioTransitionModel.getInstance().createAudioObject(tempArray[0]);
            tempPlayer.setAutoPlay(false);
            tempPlayer.setRate(Double.parseDouble(tempArray[3]));
            tempPlayer.setVolume(Double.parseDouble(tempArray[4]));
            tempPlayer.setOnReady(()->{
                tempPlayer.seek(tempPlayer.getMedia().getDuration().multiply(Double.parseDouble(tempArray[5])));
            });
            
            videoMarkersObMap.put("Add-Audio-" + key, Duration.millis( Integer.parseInt(tempArray[1]) ));
            getProfileMarkersSet().add(tempArray[1]);

            if(!tempArray[2].equals("*")){
                videoMarkersObMap.put("Remove-Audio-" + key, Duration.millis( Integer.parseInt(tempArray[2]) ));
                getProfileMarkersSet().add(tempArray[2]);
            }
            
            audioObjects.put(key + "", tempPlayer);
        });
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
                Platform.runLater(()->{
                    tempPlayer.seek(tempPlayer.getMedia().getDuration().multiply(Double.parseDouble(tempArray[9])));
                });
            });
            
            MediaView tempView = new MediaView(tempPlayer);
            
            tempView.setX((Double.parseDouble(tempArray[5]) * Context.getInstance().getWidthRatio()) + Context.getInstance().getLetterBoxingCurrent() / 2);
            tempView.setY(Double.parseDouble(tempArray[6]) * Context.getInstance().getHeightRatio());
            tempView.setFitWidth(Double.parseDouble(tempArray[7]) * Context.getInstance().getWidthRatio());
            tempView.setFitHeight(Double.parseDouble(tempArray[8]) * Context.getInstance().getHeightRatio());
            tempView.setPreserveRatio(Boolean.parseBoolean(tempArray[10]));
            
            videoMarkersObMap.put("Add-Video-" + key, Duration.millis( Integer.parseInt(tempArray[1]) ));
            getProfileMarkersSet().add(tempArray[1]);

            if(!tempArray[2].equals("*")){
                videoMarkersObMap.put("Remove-Video-" + key, Duration.millis( Integer.parseInt(tempArray[2]) ));
                getProfileMarkersSet().add(tempArray[2]);
            }
            
            videoObjects.put(key + "", tempView);
        });
    }
    
    //Stop all players and clear all objects from list
    public void deleteAllMarkers(){
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
                if (status != MediaPlayer.Status.STOPPED  || status != MediaPlayer.Status.HALTED){
                   tempView.getMediaPlayer().stop();
                }
                tempView.getMediaPlayer().dispose();
            });
            activeAudio.clear();
            activeVideo.clear();
            imageObjects.clear();
            textObjects.clear();
            audioObjects.clear();
            videoObjects.clear();
            videoMarkersObMap.clear();
            previewAnchorPane.getChildren().clear();
        }catch(Exception e){e.printStackTrace();}
    }
    
    //Same as above except clears previewAnchorPane and adds back the main mediaView
    public void deleteMarkersForTimeline(){
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
                if (status != MediaPlayer.Status.STOPPED  || status != MediaPlayer.Status.HALTED){
                   tempView.getMediaPlayer().stop();
                }
                tempView.getMediaPlayer().dispose();
            });
            activeAudio.clear();
            activeVideo.clear();
            imageObjects.clear();
            textObjects.clear();
            audioObjects.clear();
            videoObjects.clear();
            videoMarkersObMap.clear();
            previewAnchorPane.getChildren().clear();
            previewAnchorPane.getChildren().add(this.mediaView);
        }catch(Exception e){e.printStackTrace();}
    }
    
    //Move X || Y respectively for the current transition
    public void moveX(Double newValue, String currentTransition){
        String tempString = currentTransition;
        
        if(textObjects.containsKey(tempString)){
            
            TextFlow tempTextFlow = ((TextFlow) getTextObjects().get(tempString));
            Text tempText = (Text)tempTextFlow.getChildren().get(0);
            
            tempText.setX(newValue);
            
            newValue = newValue - Context.getInstance().getLetterBoxingCurrent() / 2;
            TextTransitionModel.getInstance().setXPosition((int) (newValue / Context.getInstance().getWidthRatio()));
            
        } else if(imageObjects.containsKey(tempString)){
            
            ImageView tempImageView = ((ImageView) getImageObjects().get(tempString));
            
            tempImageView.setX(newValue);
            
            newValue = newValue - Context.getInstance().getLetterBoxingCurrent() / 2;
            ImageTransitionModel.getInstance().setXPosition((int)(newValue.doubleValue() / Context.getInstance().getWidthRatio()));
            
        } else if(videoObjects.containsKey(tempString)){
            
            MediaView tempView = ((MediaView) getVideoObjects().get(tempString));
            
            tempView.setX(newValue);
            
            newValue = newValue - Context.getInstance().getLetterBoxingCurrent() / 2;
            VideoTransitionModel.getInstance().setXPosition((int)(newValue.doubleValue() / Context.getInstance().getWidthRatio()));
        }          
    }
    public void moveY(Double newValue, String currentTransition){
        String tempString = currentTransition;
        
        if(textObjects.containsKey(tempString)){
            
            TextFlow tempTextFlow = ((TextFlow) getTextObjects().get(tempString));
            Text tempText = (Text)tempTextFlow.getChildren().get(0);
            tempText.setY(newValue);
            TextTransitionModel.getInstance().setYPosition((int)(newValue / Context.getInstance().getHeightRatio()));
            
        } else if(imageObjects.containsKey(tempString)){
            
            ImageView tempImageView = ((ImageView) getImageObjects().get(tempString));
            tempImageView.setY(newValue);
            ImageTransitionModel.getInstance().setYPosition((int)(newValue / Context.getInstance().getHeightRatio()));
            
        } else if(videoObjects.containsKey(tempString)){
            
            MediaView tempView = ((MediaView) getVideoObjects().get(tempString));
            tempView.setY(newValue);
            VideoTransitionModel.getInstance().setYPosition((int)(newValue / Context.getInstance().getHeightRatio()));
        }
    }
    
    //Grab current time from mediaPlayer and set values to appropriate objects
    public void captureStartTime(){
        String tempString = ProfileModel.getInstance().getVideoProperties().get(0).toString();
        Duration currentTime = getMediaPlayer().getCurrentTime();
        setStartTimestamp(((int) currentTime.toMillis()) + "");
        setStartTimeValue(TheatreModel.getInstance().timeConversion((int)currentTime.toSeconds()));
        closeMediaPlayer();
        
        String currentTransition = getCurrentTransition();
        
        if(FFTransitionModel.getInstance().getFFMarkers().containsKey(currentTransition)){
            Platform.runLater(()->{
                stopTimeClicked = false;
                FFTransitionModel.getInstance().editTransitionString();
                startPreview(tempString);
            });
        } else if(CutTransitionModel.getInstance().getCutMarkers().containsKey(currentTransition)){
            Platform.runLater(()->{
                if(Integer.parseInt(getStartTimestamp()) > Integer.parseInt(getStopTimestamp())){
                    rewindCut = true;
                } else {
                    rewindCut = false;
                }
                stopTimeClicked = false;
                CutTransitionModel.getInstance().editTransitionString();
                startPreview(tempString);
            });
        } else if(TextTransitionModel.getInstance().getTextMarkers().containsKey(currentTransition)){
            Platform.runLater(()->{
                stopTimeClicked = false;
                TextTransitionModel.getInstance().editTransitionString();
                startPreview(tempString);
            });
        } else if(ImageTransitionModel.getInstance().getImageMarkers().containsKey(currentTransition)){
            Platform.runLater(()->{
                stopTimeClicked = false;
                ImageTransitionModel.getInstance().editTransitionString();
                startPreview(tempString);
            });
        } else if(AudioTransitionModel.getInstance().getAudioMarkers().containsKey(currentTransition)){
            Platform.runLater(()->{
                stopTimeClicked = false;
                AudioTransitionModel.getInstance().editTransitionString();
                startPreview(tempString);
            });
        } else if(VideoTransitionModel.getInstance().getVideoMarkers().containsKey(currentTransition)){
            Platform.runLater(()->{
                stopTimeClicked = false;
                VideoTransitionModel.getInstance().editTransitionString();
                startPreview(tempString);
            });
        }
    }
    public void captureStopTime(){
        String tempString = ProfileModel.getInstance().getVideoProperties().get(0).toString();
        Duration currentTime = getMediaPlayer().getCurrentTime();
        setStopTimestamp(((int) currentTime.toMillis()) + "");
        setStopTimeValue(TheatreModel.getInstance().timeConversion((int)currentTime.toSeconds()));
        closeMediaPlayer();
        
        String currentTransition = getCurrentTransition();
        if(FFTransitionModel.getInstance().getFFMarkers().containsKey(currentTransition)){
            Platform.runLater(()->{
                stopTimeClicked = true;
                FFTransitionModel.getInstance().editTransitionString();
                startPreview(tempString);
            });
        } else if(CutTransitionModel.getInstance().getCutMarkers().containsKey(currentTransition)){
            Platform.runLater(()->{
                if(Integer.parseInt(getStartTimestamp()) > Integer.parseInt(getStopTimestamp())){
                    rewindCut = true;
                } else {
                    rewindCut = false;
                }
                stopTimeClicked = true;
                CutTransitionModel.getInstance().editTransitionString();
                startPreview(tempString);
            });
        } else if(TextTransitionModel.getInstance().getTextMarkers().containsKey(currentTransition)){
            Platform.runLater(()->{
                stopTimeClicked = true;
                TextTransitionModel.getInstance().editTransitionString();
                startPreview(tempString);
            });
        } else if(ImageTransitionModel.getInstance().getImageMarkers().containsKey(currentTransition)){
            Platform.runLater(()->{
                stopTimeClicked = true;
                ImageTransitionModel.getInstance().editTransitionString();
                startPreview(tempString);
            });
        } else if(AudioTransitionModel.getInstance().getAudioMarkers().containsKey(currentTransition)){
            Platform.runLater(()->{
                stopTimeClicked = true;
                AudioTransitionModel.getInstance().editTransitionString();
                startPreview(tempString);
            });
        } else if(VideoTransitionModel.getInstance().getVideoMarkers().containsKey(currentTransition)){
            Platform.runLater(()->{
                stopTimeClicked = true;
                VideoTransitionModel.getInstance().editTransitionString();
                startPreview(tempString);
            });
        }
    }
    
    //Change Text Profile loading for current object
    public void changeTextProfile(String textProfile){
        String tempString = ProfileModel.getInstance().getVideoProperties().get(0).toString();
        TextTransitionModel.getInstance().editTransitionString();
        closeMediaPlayer();
        Platform.runLater(()->{
            startPreview(tempString);
        });
    }
    
    //Update text for current transition
    public void updateText(String textValue, String currentTransition){
        try{
            if(!currentTransition.equals("")){
                String tempString = currentTransition;
                if(!getTextObjects().isEmpty()){
                    TextFlow tempTextFlow = ((TextFlow) getTextObjects().get(tempString));
                    Text tempText = (Text)tempTextFlow.getChildren().get(0);

                    Platform.runLater(()->{
                        TextTransitionModel.getInstance().editTransitionString();
                        tempText.setText(textValue);
                    });
                }
            }

        }catch(Exception e){e.printStackTrace();}
    }
    
    //Update image size for current transition
    public void updateImageSize(String fitWidth, String fitHeight){
        try{
            if(Context.getInstance().getEditViewOpened()){
                if(!getCurrentTransition().isEmpty()){
                    ImageTransitionModel.getInstance().setFitWidth(fitWidth);
                    ImageTransitionModel.getInstance().setFitHeight(fitHeight);
                    ImageTransitionModel.getInstance().editTransitionString();
                    PreviewModel.getInstance().reloadMediaPlayer();
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }
    
    //Update video size for current transition
    public void updateVideoSize(String fitWidth, String fitHeight){
        try{
            if(Context.getInstance().getEditViewOpened()){
                if(!getCurrentTransition().isEmpty()){
                    VideoTransitionModel.getInstance().setFitWidth(fitWidth);
                    VideoTransitionModel.getInstance().setFitHeight(fitHeight);
                    VideoTransitionModel.getInstance().editTransitionString();
                    PreviewModel.getInstance().reloadMediaPlayer();
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }
    
    //Update FF Start/Stop Rate for current object
    public void updateFFStartRate(Double startRate){
        try{
            if(Context.getInstance().getEditViewOpened()){
                if(!getCurrentTransition().isEmpty()){
                    FFTransitionModel.getInstance().setStartRate(startRate);
                    FFTransitionModel.getInstance().editTransitionString();
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }
    public void updateFFStopRate(Double stopRate){
        try{
            if(Context.getInstance().getEditViewOpened()){
                if(!getCurrentTransition().isEmpty()){
                    FFTransitionModel.getInstance().setStopRate(stopRate);
                    FFTransitionModel.getInstance().editTransitionString();
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }
    
    //Update volume of current audio object
    public void updateAudioVolume(Double audioVolume){
        try{
            if(Context.getInstance().getEditViewOpened()){
                if(!getCurrentTransition().isEmpty()){
                    AudioTransitionModel.getInstance().setVolume(audioVolume);
                    AudioTransitionModel.getInstance().editTransitionString();
                    reloadMediaPlayer();
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }
    
    //Update playback rate of current audio object
    public void updateAudioRate(Double audioRate){
        try{
            if(Context.getInstance().getEditViewOpened()){
                if(!getCurrentTransition().isEmpty()){
                    AudioTransitionModel.getInstance().setPlayRate(audioRate);
                    AudioTransitionModel.getInstance().editTransitionString();
                    reloadMediaPlayer();
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }
    
    //Update seek time of current audio object
    public void updateAudioSeek(Double audioSeek){
        try{
            if(Context.getInstance().getEditViewOpened()){
                if(!getCurrentTransition().isEmpty()){
                    AudioTransitionModel.getInstance().setSeekTime(audioSeek);
                    AudioTransitionModel.getInstance().editTransitionString();
                    reloadMediaPlayer();
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }
    
    //Update volume of current video object
    public void updateVideoVolume(Double videoVolume){
        try{
            if(Context.getInstance().getEditViewOpened()){
                if(!getCurrentTransition().isEmpty()){
                    VideoTransitionModel.getInstance().setVolume(videoVolume);
                    VideoTransitionModel.getInstance().editTransitionString();
                    reloadMediaPlayer();
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }
    
    //Update playback rate of current video object
    public void updateVideoRate(Double videoRate){
        try{
            if(Context.getInstance().getEditViewOpened()){
                if(!getCurrentTransition().isEmpty()){
                    VideoTransitionModel.getInstance().setPlayRate(videoRate);
                    VideoTransitionModel.getInstance().editTransitionString();
                    reloadMediaPlayer();
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }
    
    //Update seek time of current video object
    public void updateVideoSeek(Double videoSeek){
        try{
            if(Context.getInstance().getEditViewOpened()){
                if(!getCurrentTransition().isEmpty()){
                    VideoTransitionModel.getInstance().setSeekTime(videoSeek);
                    VideoTransitionModel.getInstance().editTransitionString();
                    reloadMediaPlayer();
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }
    
    //Update font size of current video object
    public void updateFontSize(Double fontSize, String currentTransition){
        try{
            if(Context.getInstance().getEditViewOpened()){
                if(!currentTransition.isEmpty()){
                    String tempString = currentTransition;
                    if(!getTextObjects().isEmpty()){
                        TextFlow tempTextFlow = ((TextFlow) getTextObjects().get(tempString));
                        Text tempText = (Text)tempTextFlow.getChildren().get(0);

                        TextTransitionModel.getInstance().setFontSize(fontSize);
                        TextTransitionModel.getInstance().editTransitionString();
                        FontWeight weight = TextModel.getInstance().getFontWeight(tempText.getFont().getStyle().toString());
                        tempText.setFont(Font.font(tempText.getFont().getFamily(), weight , fontSize * Context.getInstance().getHeightRatio()));
                    }
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }
    
    //Formats UI text for video playtime/duration
    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int)Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        if(elapsedMinutes > 0){
            intElapsed -= elapsedMinutes * 60;
        }
        int elapsedSeconds = intElapsed;
 
        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int)Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            if(durationMinutes > 0){
                intDuration -= durationMinutes * 60;
            }
            int durationSeconds = intDuration;
            
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d", 
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds,durationMinutes, 
                        durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours, 
                        elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d",elapsedMinutes, 
                        elapsedSeconds);
            }
        }
    }
    
}