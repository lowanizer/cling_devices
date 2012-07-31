import org.teleal.cling.binding.annotations.*;

import java.beans.PropertyChangeSupport;
// Upnp Service are classes + annotations.

import org.teleal.cling.binding.annotations.*;
import java.beans.PropertyChangeSupport;

@UpnpService(
        serviceId = @UpnpServiceId("GPS"),
        serviceType = @UpnpServiceType(value = "GPS", version = 1)
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
    @UpnpStateVariable(defaultValue = "unknown", sendEvents = true, name="lastloc")
    private String lastloc = "unknown";
    @UpnpStateVariable(defaultValue = "unknown", sendEvents = true, name="location")
    private String location = "unknown";

    @UpnpStateVariable(defaultValue = "unknown", sendEvents = true)
    private String state = "unknown";


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

	state=location;

	main.m_gps_display_label.set_text_messagel3("Current location: "+newTargetValue);

	 // This will send a UPnP event, it's the name of a state variable that sends events
	getPropertyChangeSupport().firePropertyChange("Location", OldValue, location);
	getPropertyChangeSupport().firePropertyChange("State", OldValue, state);
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


    public void setMetaData(String text){
    	MetaData = text;
    	getPropertyChangeSupport().firePropertyChange("MetaData", "", text);
    }
    
    @UpnpStateVariable(defaultValue="", sendEvents=true)
    private String MetaData = "location=car&owner=maurice";

    @UpnpAction(out=@UpnpOutputArgument(name="ResultMetaData"))
    public String getMetaData()
    { return MetaData; }

}
