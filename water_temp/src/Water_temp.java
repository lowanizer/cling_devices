import org.teleal.cling.binding.annotations.*;

import java.beans.PropertyChangeSupport;
// Upnp Service are classes + annotations.

import org.teleal.cling.binding.annotations.*;
import java.beans.PropertyChangeSupport;
import java.text.DecimalFormat;

@UpnpService(
        serviceId = @UpnpServiceId("WaterThermometer"),
        serviceType = @UpnpServiceType(value = "WaterThermometer", version = 1)
)
public class Water_temp {

    private final PropertyChangeSupport propertyChangeSupport;

    public Water_temp() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    @UpnpStateVariable(defaultValue = "0", sendEvents = true)
    private int temperature = 0;

    @UpnpStateVariable(defaultValue = "0 C", sendEvents = true)
    private String state = "0 C";

    @UpnpAction
    public void setTemperature(@UpnpInputArgument(name = "NewTargetValue") int newTargetValue) {

        int tempOldValue = temperature;
        temperature = newTargetValue;

	String old_state=state;
	state=temperature + " C";

	main.m_temp_display_label.set_text_message("Temperature: "+newTargetValue+"°C");

	 // This will send a UPnP event, it's the name of a state variable that sends events
	getPropertyChangeSupport().firePropertyChange("Temperature", tempOldValue, temperature);
	getPropertyChangeSupport().firePropertyChange("State", old_state, state);
    }
    
    public void addTemp(int add){
    	setTemperature(temperature+add);
    }
    
    @UpnpAction(out = @UpnpOutputArgument(name = "ResultTemperature"))
    public int getTemperature() {
        return temperature;
    }

    public void setMetaData(String text){
    	MetaData = text;
    	getPropertyChangeSupport().firePropertyChange("MetaData", "", text);
    }
    
    @UpnpStateVariable(defaultValue="", sendEvents=true)
    private String MetaData = "location=bathroom&owner=alice";

    @UpnpAction(out=@UpnpOutputArgument(name="ResultMetaData"))
    public String getMetaData()
    { return MetaData; }

}
