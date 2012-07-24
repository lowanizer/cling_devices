import org.teleal.cling.binding.annotations.*;

import java.beans.PropertyChangeSupport;
// Upnp Service are classes + annotations.

import org.teleal.cling.binding.annotations.*;
import java.beans.PropertyChangeSupport;

@UpnpService(
        serviceId = @UpnpServiceId("Door"),
        serviceType = @UpnpServiceType(value = "Door", version = 1)
)
public class Door {

    private final PropertyChangeSupport propertyChangeSupport;
  
    public Door() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    @UpnpStateVariable(defaultValue = "true", sendEvents = true)
    private Boolean closed = true;
  
    
    @UpnpAction
    public void setClosed(@UpnpInputArgument(name = "NewTargetValue") Boolean newTargetValue) {

        Boolean OldValue = closed;
        closed = newTargetValue;

	main.m_door_display_label.draw_door(newTargetValue);

	 // This will send a UPnP event, it's the name of a state variable that sends events
    getPropertyChangeSupport().firePropertyChange("Closed", OldValue, closed);
    }
    

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultClosed"))
    public Boolean getClosed() {
        return closed;
    }
    
    public void setMetaData(String text){
    	MetaData = text;
    	getPropertyChangeSupport().firePropertyChange("MetaData", "", text);
    }

    @UpnpStateVariable(defaultValue="none", sendEvents=true)
    private String MetaData = "location=kitchen&owner=alice";

    @UpnpAction(out=@UpnpOutputArgument(name="ResultMetaData"))
    public String getMetaData()
    { return MetaData; }

}
