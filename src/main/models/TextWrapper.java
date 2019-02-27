/*************************************************************************************************************************
 * Source Code Form License Notice
 *************************************************************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 **************************************************************************************************************************/
/*************************************************************************************************************************
 *   Author: Jarek Thomas
 *
 *   XML wrapper for modeling a Text Profile
 ***************************************************************************************************************************/

package main.models;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "text")
public class TextWrapper {
    
    private List<String> effects;
    private String fontColor, fontWeight, fontFamily;
    private double fontSize;
    
    @XmlElementWrapper(name = "effects")
    @XmlElement(name = "effect")
    public List<String> getEffects(){
        return this.effects;
    }
    public void setEffects(List<String> effects){
        this.effects = effects;
    }

    @XmlElement(name = "fontColor")
    public String getFontColor(){
        return this.fontColor;
    }
    public void setFontColor(String fontColor){
        this.fontColor = fontColor;
    }
    
    @XmlElement(name = "fontSize")
    public double getFontSize(){
        return this.fontSize;
    }
    public void setFontSize(double fontSize){
        this.fontSize = fontSize;
    }
    
    @XmlElement(name = "fontWeight")
    public String getFontWeight(){
        return this.fontWeight;
    }
    public void setFontWeight(String fontWeight){
        this.fontWeight = fontWeight;
    }
    
    @XmlElement(name = "fontFamily")
    public String getFontFamily(){
        return this.fontFamily;
    }
    public void setFontFamily(String fontFamily){
        this.fontFamily = fontFamily;
    }
}
