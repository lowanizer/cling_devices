import org.teleal.cling.binding.annotations.*;

import java.beans.PropertyChangeSupport;
// Upnp Service are classes + annotations.

import org.teleal.cling.binding.annotations.*;
import java.beans.PropertyChangeSupport;

@UpnpService(
        serviceId = @UpnpServiceId("RainGauge"),
        serviceType = @UpnpServiceType(value = "RainGauge", version = 1)
)
public class Rain_gauge {

    private final PropertyChangeSupport propertyChangeSupport;

    public Rain_gauge() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    @UpnpStateVariable(defaultValue = "0", sendEvents = true)
    private int raining = 0;

    @UpnpAction
    public void setRaining(@UpnpInputArgument(name = "NewTargetValue") int newTargetValue) {

        int OldValue = raining;
        raining = newTargetValue;

	main.m_rain_display_label.set_text_message("Raining: "+newTargetValue+" cm/h");

	 // This will send a UPnP event, it's the name of a state variable that sends events
    getPropertyChangeSupport().firePropertyChange("Raining", OldValue, raining);
    }
    
    public void addRain(int add){
    	if(raining>=-add) setRaining(raining+add);
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultRaining"))
    public int getRaining() {
        return raining;
    }

    public void setMetaData(String text){
    	MetaData = text;
    	getPropertyChangeSupport().firePropertyChange("MetaData", "", text);
    }
    
    @UpnpStateVariable(defaultValue="", sendEvents=true)
    private String MetaData = "location=garden&owner=bob";

    @UpnpAction(out=@UpnpOutputArgument(name="ResultMetaData"))
    public String getMetaData()
    { return MetaData; }

}
