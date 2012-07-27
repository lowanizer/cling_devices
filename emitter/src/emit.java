import org.teleal.cling.binding.annotations.*;

import java.beans.PropertyChangeSupport;
import org.teleal.cling.binding.annotations.*;
import java.beans.PropertyChangeSupport;

@UpnpService(
        serviceId = @UpnpServiceId("Emit"),
        serviceType = @UpnpServiceType(value = "Emit", version = 1)
)
public class emit {

    private final PropertyChangeSupport propertyChangeSupport;

    public emit() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    @UpnpStateVariable(defaultValue = "false", sendEvents = true)
    private boolean status = false;

    @UpnpAction
    public void emit()
    {
	    getPropertyChangeSupport().firePropertyChange("Status", status, !status);
	    status=!status;
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultStatus"))
    public boolean getStatus() {
        return status;
    }

}
