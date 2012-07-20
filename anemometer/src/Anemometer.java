import org.teleal.cling.binding.annotations.*;

import java.beans.PropertyChangeSupport;
// Upnp Service are classes + annotations.

import org.teleal.cling.binding.annotations.*;
import java.beans.PropertyChangeSupport;
import java.text.DecimalFormat;

@UpnpService(
        serviceId = @UpnpServiceId("Anemometer"),
        serviceType = @UpnpServiceType(value = "Anemometer", version = 1)
)
public class Anemometer {

    private final PropertyChangeSupport propertyChangeSupport;

    public Anemometer() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    @UpnpStateVariable(defaultValue = "0", sendEvents = true)
    private int wind = 0;

    @UpnpAction
    public void setWind(@UpnpInputArgument(name = "NewTargetValue") int newTargetValue) {

        int OldValue = wind;
        wind = newTargetValue;

	main.m_wind_display_label.set_text_message("Wind: "+newTargetValue+"km/h");
	main.m_progress_bar.setValue(newTargetValue);
	
	if(newTargetValue<=5) main.m_progress_bar.setString("Calm");
	else if(newTargetValue<=10) main.m_progress_bar.setString("Light breeze");
	else if(newTargetValue<=20) main.m_progress_bar.setString("Moderate breeze");
	else if(newTargetValue<=40) main.m_progress_bar.setString("Strong breeze");
	else if(newTargetValue<=60) main.m_progress_bar.setString("Moderate wind");
	else if(newTargetValue<=80) main.m_progress_bar.setString("Strong wind");
	else if(newTargetValue<=100) main.m_progress_bar.setString("Very strong wind");
	else if(newTargetValue<=130) main.m_progress_bar.setString("Storm");
	else main.m_progress_bar.setString("Hurricane");

	 // This will send a UPnP event, it's the name of a state variable that sends events
    getPropertyChangeSupport().firePropertyChange("Wind", OldValue, wind);
    }

    public void addWind(int add){
    	if(wind>=-add) setWind(wind+add);
    }
    
    @UpnpAction(out = @UpnpOutputArgument(name = "ResultWind"))
    public int getWind() {
        return wind;
    }

    @UpnpStateVariable(defaultValue="type=Display", sendEvents=true)
    private String MetaData = "type=Display";

    @UpnpAction(out=@UpnpOutputArgument(name="ResultMetaData"))
    public String getMetaData()
    { return MetaData; }

}
