import org.teleal.cling.binding.annotations.*;

import java.beans.PropertyChangeSupport;
// Upnp Service are classes + annotations.

import org.teleal.cling.binding.annotations.*;
import java.beans.PropertyChangeSupport;

@UpnpService(
        serviceId = @UpnpServiceId("Luxmeter"),
        serviceType = @UpnpServiceType(value = "Luxmeter", version = 1)
)
public class Luxmeter {

    private final PropertyChangeSupport propertyChangeSupport;

    public Luxmeter() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    @UpnpStateVariable(defaultValue = "0", sendEvents = true)
    private int light = 0;

    @UpnpAction
    public void setLight(@UpnpInputArgument(name = "NewTargetValue") int newTargetValue) {

        int OldValue = light;
        light = newTargetValue;

	main.m_light_display_label.set_text_message("Light: "+newTargetValue+" lumens");

	 // This will send a UPnP event, it's the name of a state variable that sends events
    getPropertyChangeSupport().firePropertyChange("Light", OldValue, light);
    }
    
    public void addLight(int add){
    	if(light>=-add) setLight(light+add);
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultLight"))
    public int getLight() {
        return light;
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
