import org.teleal.cling.binding.annotations.*;

import java.beans.PropertyChangeSupport;
// Upnp Service are classes + annotations.

import org.teleal.cling.binding.annotations.*;
import java.beans.PropertyChangeSupport;

@UpnpService(
        serviceId = @UpnpServiceId("Window"),
        serviceType = @UpnpServiceType(value = "Window", version = 1)
)
public class Window {

    private final PropertyChangeSupport propertyChangeSupport;
  
    public Window() {
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

	main.m_window_display_label.draw_window(newTargetValue);

	 // This will send a UPnP event, it's the name of a state variable that sends events
    getPropertyChangeSupport().firePropertyChange("Closed", OldValue, closed);
    }
    

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultClosed"))
    public Boolean getClosed() {
        return closed;
    }
    


    @UpnpStateVariable(defaultValue="type=Display", sendEvents=true)
    private String MetaData = "type=Display";

    @UpnpAction(out=@UpnpOutputArgument(name="ResultMetaData"))
    public String getMetaData()
    { return MetaData; }

}
