import org.teleal.cling.binding.annotations.*;

import java.beans.PropertyChangeSupport;
// Upnp Service are classes + annotations.

import org.teleal.cling.binding.annotations.*;
import java.beans.PropertyChangeSupport;

@UpnpService(
        serviceId = @UpnpServiceId("MetaChecker"),
        serviceType = @UpnpServiceType(value = "MetaChecker", version = 1)
)
public class MetaChecker {

    private final PropertyChangeSupport propertyChangeSupport;

    public MetaChecker() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }
    
    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    @UpnpStateVariable(defaultValue = "", sendEvents = true)
    private String metaData = "";
    @UpnpStateVariable(defaultValue = "false", sendEvents = true)
    private Boolean actionbool = false;
    @UpnpStateVariable(defaultValue = "0", sendEvents = true)
    private int actionint = 0;
    @UpnpStateVariable(defaultValue = "false", sendEvents = true)
    private Boolean event = false;
    @UpnpStateVariable(defaultValue = "", sendEvents = true)
    private String md = "";

    
    // Sets the metadatas we seek in the device
    @UpnpAction
    public void setMetaData(@UpnpInputArgument(name = "NewTargetValue") String newTargetValue) {

       metaData = newTargetValue;
	
		 // This will send a UPnP event, it's the name of a state variable that sends events
	    getPropertyChangeSupport().firePropertyChange("MetaData", "x", "y");
      	main.m_check_display_label.set_text_message("Search for "+metaData);
    }
    
    // Sets the value of action if boolean (ex: true or false for door closed or open)
    @UpnpAction
    public void setActionbool(@UpnpInputArgument(name = "NewTargetValue") Boolean newTargetValue) {
       actionbool = newTargetValue;
    }
    
    // Sets the value of action if int (ex: thermometer at 22°C)
    @UpnpAction
    public void setActionint(@UpnpInputArgument(name = "NewTargetValue") int newTargetValue) {
       actionint = newTargetValue;
    }
    
    /* Checks if the given metadata from a device correspond to the metadatas we seek
     * Launch callEvent() if success
     */
    @UpnpAction
    public void checkMetadata(@UpnpInputArgument(name = "Md") String md){
       	String[] cond = metaData.split("&");
       	Boolean ok = true;

       	for(int i=0;i<cond.length;i++){
       		if(md.indexOf(cond[i])==-1){
       			ok = false;
       			break;
       		}       		
       	}

    	if(ok){
       		callEvent();
       	}
    }
    
    // Changes the value of an event variable to launch an event
    @UpnpAction
    public void callEvent() {
    	// This will send a UPnP event, it's the name of a state variable that sends events
    	event=!event;
    	getPropertyChangeSupport().firePropertyChange("Event", false, true);
    }
    
    // Returns the value of the action of type Boolean 
    @UpnpAction(out = @UpnpOutputArgument(name = "Actionbool"))
    public Boolean makeActionBool(){
    	main.m_check_display_label.set_text_message("Metadata OK, action = "+actionbool);
    	return actionbool;
    }
    
 // Returns the value of the action of type int
    @UpnpAction(out = @UpnpOutputArgument(name = "Actionint"))
    public int makeActionInt(){
    	main.m_check_display_label.set_text_message("Metadata OK, action = "+actionint);
    	return actionint;
    }
    

}
