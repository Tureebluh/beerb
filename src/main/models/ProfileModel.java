/*************************************************************************************************************************
 * Source Code Form License Notice
 *************************************************************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 **************************************************************************************************************************/
/*************************************************************************************************************************
 *   Author: Jarek Thomas
 *
 *   Model for Loading and Saving Profiles
 ***************************************************************************************************************************/

package main.models;

import java.io.File;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;


public class ProfileModel {
    private final static ProfileModel INSTANCE = new ProfileModel();
    
    private final ObservableList<String> profileList, videoPropsObList, markerEventList, textProfileList;
    
    private final SimpleStringProperty profileNameText = new SimpleStringProperty();
    private final SimpleStringProperty videoBtnStyle = new SimpleStringProperty();
    
    private final SimpleBooleanProperty videoBtnDisable = new SimpleBooleanProperty();
    
    private final ObjectProperty<Image> thumbnailImage = new SimpleObjectProperty();
    
    private MediaView tempMediaView;
    private final Timeline TIMELINE;
    private WritableImage videoThumbnailImage;
    private File tempVideoFile;
    private MediaPlayer mediaPlayer;
    private Media media;
    private Stage stage;
    
    private ProfileModel(){
        this.tempMediaView = new MediaView();
        this.TIMELINE = new Timeline(new KeyFrame(Duration.millis(100), event -> takeThumbnail()));
        this.profileList = FXCollections.observableArrayList();
        this.videoPropsObList = FXCollections.observableArrayList();
        this.markerEventList = FXCollections.observableArrayList();
        this.textProfileList = FXCollections.observableArrayList();
    }
    public static ProfileModel getInstance(){
        return INSTANCE;
    }
    public Stage getStage(){
        return this.stage;
    }
    public void setStage(Stage stage){
        this.stage = stage;
    }
    public ObservableList getVideoProperties(){
        return this.videoPropsObList;
    }
    public ObservableList getProfileList(){
        return this.profileList;
    }
    public ObservableList getMarkerEventsList(){
        return this.markerEventList;
    }
    public ObservableList getTextProfileList(){
        return this.textProfileList;
    }
    
    public final String getProfileName(){
        return profileNameText.getValue();
    }
    public final void setProfileName(String profileName){
        this.profileNameText.setValue(profileName); 
    }
    public StringProperty profileNameTextProperty(){
        return profileNameText;
    }
    
    public Image getThumbnailImage(){
        return this.thumbnailImage.getValue();
    }
    public void setThumbnailImage(Image thumbnailImage){
        this.thumbnailImage.setValue(thumbnailImage);
    }
    public ObjectProperty<Image> thumbnailImageProperty(){
        return thumbnailImage;
    }
    
    public final Boolean getVideoBtnDisable(){
        return videoBtnDisable.getValue();
    }
    public final void setVideoBtnDisable(Boolean videoBtnDisable){
        this.videoBtnDisable.setValue(videoBtnDisable); 
    }
    public BooleanProperty videoBtnDisableProperty(){
        return videoBtnDisable;
    }
    
    public final String getVideoBtnStyle(){
        return videoBtnStyle.getValue();
    }
    public final void setVideoBtnStyle(String videoBtnStyle){
        this.videoBtnStyle.setValue(videoBtnStyle); 
    }
    public StringProperty videoBtnStyleProperty(){
        return videoBtnStyle;
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
    
    //Load XML profiles from Profiles folder
    public void getProfiles(){
        profileList.clear();
        File path = new File("Profiles//");
        File[] fileList = path.listFiles((File dir, String name) -> name.toLowerCase().endsWith(".xml"));       
        for(File file : fileList){
            profileList.add(file.getName().substring( 0,  file.getName().length() - 4 )); 
        }
    }
    
    //Load XML text profiles from Text folder
    public void getTextProfiles(){
        textProfileList.clear();
        textProfileList.add("_DEFAULT");
        File path = new File("Text//");
        File[] fileList = path.listFiles((File dir, String name) -> name.toLowerCase().endsWith(".xml"));       
        for(File file : fileList){
            textProfileList.add(file.getName().substring( 0,  file.getName().length() - 4 )); 
        }
    }
    
    //Set all list and text to their default values
    public void clearSceneProperties(){
        TextModel.getInstance().clearTextSceneProperties();
        getVideoProperties().clear();
        getMarkerEventsList().clear();
        TextTransitionModel.getInstance().getTextMarkers().clear();
        ImageTransitionModel.getInstance().getImageMarkers().clear();
        VideoTransitionModel.getInstance().getVideoMarkers().clear();
        AudioTransitionModel.getInstance().getAudioMarkers().clear();
        FFTransitionModel.getInstance().getFFMarkers().clear();
        CutTransitionModel.getInstance().getCutMarkers().clear();
        PreviewModel.getInstance().getProfileMarkersSet().clear();
        profileNameTextProperty().set("");
        EffectsModel.getInstance().addEffectToSample();
    }
    
    //Loads video into mediaplayer temporarily to take screenshot for thumbnail
    public void prepareScreenshot(String videoPath){
        tempVideoFile = new File(videoPath);
        media = new Media(tempVideoFile.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setVolume(0);
        tempMediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.setOnPlaying(()->{
            TIMELINE.play();
        });
    }
    
    //TIMELINE.play() will call this method to save a snapshot of the temp mediaview
    public void takeThumbnail(){
        this.videoThumbnailImage = tempMediaView.snapshot(new SnapshotParameters(), null);
        thumbnailImageProperty().set(videoThumbnailImage);
        this.mediaPlayer.stop();
        TIMELINE.stop();
    }
    
    //Save the profile to XML with all transitions
    public void saveProfileToXML(){
        
        try{
            JAXBContext context = JAXBContext.newInstance(ProfileWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            File xmlProfile = new File("Profiles//" + profileNameText.getValue().replaceAll(" ", "_") + ".xml");
            ProfileWrapper wrapper = new ProfileWrapper();

            wrapper.setVideoProperties(ProfileModel.getInstance().getVideoProperties());
            wrapper.setTextTransitions(TextTransitionModel.getInstance().getTextMarkers());
            wrapper.setImageTransitions(ImageTransitionModel.getInstance().getImageMarkers());
            wrapper.setFFTransitions(FFTransitionModel.getInstance().getFFMarkers());
            wrapper.setCutTransitions(CutTransitionModel.getInstance().getCutMarkers());
            wrapper.setAudioTransitions(AudioTransitionModel.getInstance().getAudioMarkers());
            wrapper.setVideoTransitions(VideoTransitionModel.getInstance().getVideoMarkers());

            m.marshal(wrapper, xmlProfile);

            ProfileModel.getInstance().getProfiles();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success!");
            alert.setHeaderText("Profile saved.");
            alert.setContentText(xmlProfile.getName() + " successfully created.");
            alert.setGraphic( new ImageView( "main/images/BeeLogo.png" ) );
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add("main/stylesheets/dialog.css");
            dialogPane.getStyleClass().add("success");
            alert.show();

        }catch(Exception e){

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Woah - Something went wrong.");
            alert.setContentText("An error occured while trying to save your profile.");
            alert.showAndWait();
        }
    }
    
    //Load profile from XML by profile name
    public void loadProfileFromXML(String profileName){
        
        try{
            JAXBContext context = JAXBContext.newInstance(ProfileWrapper.class);
            Unmarshaller u = context.createUnmarshaller();
            ProfileWrapper wrapper = (ProfileWrapper) u.unmarshal(new File("Profiles//" + profileName + ".xml"));
            
            getVideoProperties().addAll(wrapper.getVideoProperties());
            profileNameTextProperty().set(profileName);
            
            getMarkerEventsList().addAll(wrapper.getTextTransitions().keySet());
            getMarkerEventsList().addAll(wrapper.getImageTransitions().keySet());
            getMarkerEventsList().addAll(wrapper.getFFTransitions().keySet());
            getMarkerEventsList().addAll(wrapper.getCutTransitions().keySet());
            getMarkerEventsList().addAll(wrapper.getAudioTransitions().keySet());
            getMarkerEventsList().addAll(wrapper.getVideoTransitions().keySet());
            
            ImageTransitionModel.getInstance().getImageMarkers().putAll(wrapper.getImageTransitions());
            TextTransitionModel.getInstance().getTextMarkers().putAll(wrapper.getTextTransitions());
            FFTransitionModel.getInstance().getFFMarkers().putAll(wrapper.getFFTransitions());
            CutTransitionModel.getInstance().getCutMarkers().putAll(wrapper.getCutTransitions());
            AudioTransitionModel.getInstance().getAudioMarkers().putAll(wrapper.getAudioTransitions());
            VideoTransitionModel.getInstance().getVideoMarkers().putAll(wrapper.getVideoTransitions());
            
            for(String temp : wrapper.getTextTransitions().values()){
                String[] tempArray = temp.split(",");
                PreviewModel.getInstance().getProfileMarkersSet().add(tempArray[4]);
                PreviewModel.getInstance().getProfileMarkersSet().add(tempArray[5]);
            }
            for(String temp : wrapper.getImageTransitions().values()){
                String[] tempArray = temp.split(",");
                PreviewModel.getInstance().getProfileMarkersSet().add(tempArray[5]);
                PreviewModel.getInstance().getProfileMarkersSet().add(tempArray[6]);
            }
            for(String temp : wrapper.getFFTransitions().values()){
                String[] tempArray = temp.split(",");
                PreviewModel.getInstance().getProfileMarkersSet().add(tempArray[0]);
                PreviewModel.getInstance().getProfileMarkersSet().add(tempArray[1]);
            }
            for(String temp : wrapper.getCutTransitions().values()){
                String[] tempArray = temp.split(",");
                PreviewModel.getInstance().getProfileMarkersSet().add(tempArray[0]);
                PreviewModel.getInstance().getProfileMarkersSet().add(tempArray[1]);
            }
            for(String temp : wrapper.getAudioTransitions().values()){
                String[] tempArray = temp.split(",");
                PreviewModel.getInstance().getProfileMarkersSet().add(tempArray[1]);
                PreviewModel.getInstance().getProfileMarkersSet().add(tempArray[2]);
            }
            for(String temp : wrapper.getVideoTransitions().values()){
                String[] tempArray = temp.split(",");
                PreviewModel.getInstance().getProfileMarkersSet().add(tempArray[1]);
                PreviewModel.getInstance().getProfileMarkersSet().add(tempArray[2]);
            }
            
        }catch(Exception e){
            e.printStackTrace();
            ProfileModel.getInstance().getProfiles();
        }
        
    }
}
