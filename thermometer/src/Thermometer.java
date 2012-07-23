import org.teleal.cling.binding.annotations.*;

import java.beans.PropertyChangeSupport;
// Upnp Service are classes + annotations.

import org.teleal.cling.binding.annotations.*;
import java.beans.PropertyChangeSupport;
import java.text.DecimalFormat;

@UpnpService(
        serviceId = @UpnpServiceId("Thermometer"),
        serviceType = @UpnpServiceType(value = "Thermometer", version = 1)
)
public class Thermometer {

    private final PropertyChangeSupport propertyChangeSupport;

    public Thermometer() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    @UpnpStateVariable(defaultValue = "0", sendEvents = true)
    private int temperature = 0;

    @UpnpAction
    public void setTemperature(@UpnpInputArgument(name = "NewTargetValue") int newTargetValue) {

        int tempOldValue = temperature;
        temperature = newTargetValue;

	main.m_temp_display_label.set_text_message("Temperature: "+newTargetValue+"�C");

	 // This will send a UPnP event, it's the name of a state variable that sends events
    getPropertyChangeSupport().firePropertyChange("Temperature", tempOldValue, temperature);
    }
    
    public void addTemp(int add){
    	setTemperature(temperature+add);
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultTemperature"))
    public int getTemperature() {
        return temperature;
    }

    @UpnpStateVariable(defaultValue="type=Display", sendEvents=true)
    private String MetaData = "type=Display";

    @UpnpAction(out=@UpnpOutputArgument(name="ResultMetaData"))
    public String getMetaData()
    { return MetaData; }

}
