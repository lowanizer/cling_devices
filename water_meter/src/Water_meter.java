import org.teleal.cling.binding.annotations.*;

import java.beans.PropertyChangeSupport;
// Upnp Service are classes + annotations.

import org.teleal.cling.binding.annotations.*;
import java.beans.PropertyChangeSupport;

@UpnpService(
        serviceId = @UpnpServiceId("WaterMeter"),
        serviceType = @UpnpServiceType(value = "WaterMeter", version = 1)
)
public class Water_meter {

    private final PropertyChangeSupport propertyChangeSupport;

    public Water_meter() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    @UpnpStateVariable(defaultValue = "0", sendEvents = true)
    private int debit = 0;

    @UpnpAction
    public void setDebit(@UpnpInputArgument(name = "NewTargetValue") int newTargetValue) {

        int debitOldValue = debit;
        debit = newTargetValue;

	main.m_wm_display_label.set_text_message("Debit: "+newTargetValue+" L/min");

	 // This will send a UPnP event, it's the name of a state variable that sends events
    getPropertyChangeSupport().firePropertyChange("Debit", debitOldValue, debit);
    }
    
    public void addDebit(int add){
    	if(debit>=-add) setDebit(debit+add);
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultDebit"))
    public int getDebit() {
        return debit;
    }

    @UpnpStateVariable(defaultValue="type=Display", sendEvents=true)
    private String MetaData = "type=Display";

    @UpnpAction(out=@UpnpOutputArgument(name="ResultMetaData"))
    public String getMetaData()
    { return MetaData; }

}
