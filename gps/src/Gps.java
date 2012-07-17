import org.teleal.cling.binding.annotations.*;

import java.beans.PropertyChangeSupport;
// Upnp Service are classes + annotations.

import org.teleal.cling.binding.annotations.*;
import java.beans.PropertyChangeSupport;

@UpnpService(
        serviceId = @UpnpServiceId("Display"),
        serviceType = @UpnpServiceType(value = "Display", version = 1)
)
public class Gps {

    private final PropertyChangeSupport propertyChangeSupport;

    public Gps() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    @UpnpStateVariable(defaultValue = "0", sendEvents = true, name="lastwait")
    private int lastwait = 0;
    @UpnpStateVariable(defaultValue = "unknow", sendEvents = true, name="lastloc")
    private String lastloc = "unknow";
    @UpnpStateVariable(defaultValue = "unknow", sendEvents = true, name="location")
    private String location = "unknow";

    @UpnpAction
    public void setLastwait(@UpnpInputArgument(name = "lastwait") int newTargetValue) {

        int OldValue = lastwait;
        lastwait = newTargetValue;

	main.m_gps_display_label.set_text_messagel1("Last wait: "+newTargetValue+" min");

	 // This will send a UPnP event, it's the name of a state variable that sends events
    getPropertyChangeSupport().firePropertyChange("Lastwait", OldValue, lastwait);
    }
    
    @UpnpAction
    public void setLastloc(@UpnpInputArgument(name = "lastloc") String newTargetValue) {

        String OldValue = lastloc;
        lastloc = newTargetValue;

	main.m_gps_display_label.set_text_messagel2("Last location: "+newTargetValue);

	 // This will send a UPnP event, it's the name of a state variable that sends events
    getPropertyChangeSupport().firePropertyChange("Lastloc", OldValue, lastloc);
    }
    
    @UpnpAction
    public void setLocation(@UpnpInputArgument(name = "location") String newTargetValue) {

        String OldValue = location;
        location = newTargetValue;

	main.m_gps_display_label.set_text_messagel3("Current location: "+newTargetValue);

	 // This will send a UPnP event, it's the name of a state variable that sends events
    getPropertyChangeSupport().firePropertyChange("Location", OldValue, location);
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "lastwait"))
    public int getLastwait() {
        return lastwait;
    }
    @UpnpAction(out = @UpnpOutputArgument(name = "lastloc"))
    public String getLastloc() {
        return lastloc;
    }
    @UpnpAction(out = @UpnpOutputArgument(name = "location"))
    public String getLocation() {
        return location;
    }


    @UpnpStateVariable(defaultValue="type=Display", sendEvents=true)
    private String MetaData = "type=Display";

    @UpnpAction(out=@UpnpOutputArgument(name="ResultMetaData"))
    public String getMetaData()
    { return MetaData; }

}
