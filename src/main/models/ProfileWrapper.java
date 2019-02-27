/*************************************************************************************************************************
 * Source Code Form License Notice
 *************************************************************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 **************************************************************************************************************************/
/*************************************************************************************************************************
 *   Author: Jarek Thomas
 *
 *   XML Wrapper for modeling Video Profiles
 ***************************************************************************************************************************/

package main.models;

import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "profile")
public class ProfileWrapper {
    
    private List<String> videoPath;
    private Map<String, String> textTransitions, imageTransitions, audioTransitions, ffTransitions, cutTransitions, videoTransitions;
    
    @XmlElementWrapper(name = "main")
    @XmlElement(name = "videoproperties")
    public List<String> getVideoProperties(){
        return this.videoPath;
    }
    public void setVideoProperties(List<String> videoPath){
        this.videoPath = videoPath;
    }
    
    @XmlElement(name = "text")
    public Map<String, String> getTextTransitions(){
        return this.textTransitions;
    }
    public void setTextTransitions(Map<String, String> textTransitions){
        this.textTransitions = textTransitions;
    }
    
    @XmlElement(name = "image")
    public Map<String, String> getImageTransitions(){
        return this.imageTransitions;
    }
    public void setImageTransitions(Map<String, String> imageTransitions){
        this.imageTransitions = imageTransitions;
    }
    
    @XmlElement(name = "ff")
    public Map<String, String> getFFTransitions(){
        return this.ffTransitions;
    }
    public void setFFTransitions(Map<String, String> ffTransitions){
        this.ffTransitions = ffTransitions;
    }
    
    @XmlElement(name = "cut")
    public Map<String, String> getCutTransitions(){
        return this.cutTransitions;
    }
    public void setCutTransitions(Map<String, String> cutTransitions){
        this.cutTransitions = cutTransitions;
    }
    
    @XmlElement(name = "audio")
    public Map<String, String> getAudioTransitions(){
        return this.audioTransitions;
    }
    public void setAudioTransitions(Map<String, String> audioTransitions){
        this.audioTransitions = audioTransitions;
    }
    
    @XmlElement(name = "video")
    public Map<String, String> getVideoTransitions(){
        return this.videoTransitions;
    }
    public void setVideoTransitions(Map<String, String> videoTransitions){
        this.videoTransitions = videoTransitions;
    }
}
