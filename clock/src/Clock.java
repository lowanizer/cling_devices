import org.teleal.cling.binding.annotations.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeSupport;
// Upnp Service are classes + annotations.

import org.teleal.cling.binding.annotations.*;
import java.beans.PropertyChangeSupport;
import java.text.DecimalFormat;

import javax.swing.Timer;

@UpnpService(
        serviceId = @UpnpServiceId("Display"),
        serviceType = @UpnpServiceType(value = "Display", version = 1)
)
public class Clock {

    private final PropertyChangeSupport propertyChangeSupport;

    public Clock() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        t.start();
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    @UpnpStateVariable(defaultValue = "0", sendEvents = true)
    private int hour = 12;
    @UpnpStateVariable(defaultValue = "0", sendEvents = true)
    private int minute = 0;
    @UpnpStateVariable(defaultValue = "0", sendEvents = true)
    private int second = 0;
    
    private DecimalFormat df = new DecimalFormat("00");
    
    Timer t = new Timer(1000,new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			update_clock();
		}
    } );


    public void update_clock(){			
		second++;
		if(second>=60){
			second = 0;
			minute++;
			if(minute>=60){
				minute = 0;
				hour++;
				if(hour>=24){
					hour = 0;						
				}
			}
		}
		
		main.m_clock_display_label.set_text_message(df.format(hour)+":"+df.format(minute)+":"+df.format(second));
	}
    
    
    @UpnpAction
    public void setHour(@UpnpInputArgument(name = "NewTargetValue") int newTargetValue) {

        int hourOldValue = hour;
        hour = newTargetValue;

	main.m_clock_display_label.set_text_message(newTargetValue+":"+minute+":"+second);

	 // This will send a UPnP event, it's the name of a state variable that sends events
    getPropertyChangeSupport().firePropertyChange("Hour", hourOldValue, hour);
    }
    
    @UpnpAction
    public void setMinute(@UpnpInputArgument(name = "NewTargetValue") int newTargetValue) {

        int minuteOldValue = minute;
        minute = newTargetValue;

	main.m_clock_display_label.set_text_message(hour+":"+newTargetValue+":"+second);

	 // This will send a UPnP event, it's the name of a state variable that sends events
    getPropertyChangeSupport().firePropertyChange("Minute", minuteOldValue, minute);
    }
    
    @UpnpAction
    public void setSecond(@UpnpInputArgument(name = "NewTargetValue") int newTargetValue) {

        int secondOldValue = second;
        second = newTargetValue;

	main.m_clock_display_label.set_text_message(hour+":"+minute+":"+newTargetValue);

	 // This will send a UPnP event, it's the name of a state variable that sends events
    getPropertyChangeSupport().firePropertyChange("Second", secondOldValue, second);
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultHour"))
    public int getHour() {
        return hour;
    }
    @UpnpAction(out = @UpnpOutputArgument(name = "ResultMinute"))
    public int getMinute() {
        return minute;
    }
    @UpnpAction(out = @UpnpOutputArgument(name = "ResultSecond"))
    public int getSecond() {
        return second;
    }

    @UpnpStateVariable(defaultValue="type=Display", sendEvents=true)
    private String MetaData = "type=Display";

    @UpnpAction(out=@UpnpOutputArgument(name="ResultMetaData"))
    public String getMetaData()
    { return MetaData; }

}
