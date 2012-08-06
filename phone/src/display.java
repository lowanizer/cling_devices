import org.teleal.cling.binding.annotations.*;

import java.beans.PropertyChangeSupport;
// Upnp Service are classes + annotations.

import org.teleal.cling.binding.annotations.*;
import java.beans.PropertyChangeSupport;

@UpnpService(
        serviceId = @UpnpServiceId("Display"),
        serviceType = @UpnpServiceType(value = "Display", version = 1)
)
public class display {

    private final PropertyChangeSupport propertyChangeSupport;

    public display() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    @UpnpStateVariable(defaultValue = "0", sendEvents = false)
    private int status = 0;

    @UpnpStateVariable(defaultValue = "0", sendEvents = false)
    private String text = "";

    @UpnpAction
    public void setStatus(@UpnpInputArgument(name = "NewTargetValue") int newTargetValue) {

        int statusOldValue = status;
        status = newTargetValue;

	main.m_tv_display_label.set_text_message("Status: "+newTargetValue);

        // This will send a UPnP event, it's the name of a state variable that sends events
        //getPropertyChangeSupport().firePropertyChange("Status", statusOldValue, status);
    }

    @UpnpAction
    public void display(@UpnpInputArgument(name = "Text") String newTargetValue) {

	main.m_tv_display_label.set_text_message(newTargetValue);

    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultStatus"))
    public int getStatus() {
        return status;
    }
    
    public void setMetaData(String t){
    	MetaData = t;
    	getPropertyChangeSupport().firePropertyChange("MetaData", "", t);
    }

    @UpnpStateVariable(defaultValue="type=Display", sendEvents=true)
    private String MetaData = "type=Display";

    @UpnpAction(out=@UpnpOutputArgument(name="ResultMetaData"))
    public String getMetaData()
    { return MetaData; }

}
