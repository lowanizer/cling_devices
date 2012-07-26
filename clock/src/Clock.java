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
        serviceId = @UpnpServiceId("Clock"),
        serviceType = @UpnpServiceType(value = "Clock", version = 1)
)
public class Clock {

    private final PropertyChangeSupport propertyChangeSupport;

    public Clock() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        t0.start();
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
    
    Timer t0 = new Timer(1000,new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			update_clock(true);
		}
    } );


    public void update_clock(Boolean add_sec){			
		Boolean change_hour = false;
    	Boolean change_minute = false;
		
    	if(add_sec) second++;
		if(second>=60){
			second = 0;
			minute++;
			change_minute = true;
		}
		if(minute>=60){
			minute = 0;
			hour++;
			change_hour = true;
		}
		if(minute<0){
			minute = 59;
			hour--;
			change_hour = true;
		}
		if(hour<0){
			hour = 23;						
		}
		if(hour>=24){
			hour = 0;						
		}
		
		if(change_minute) getPropertyChangeSupport().firePropertyChange("Minute", minute-1, minute);
		if(change_hour) getPropertyChangeSupport().firePropertyChange("Hour", hour-1, hour);
		
		main.m_clock_display_label.set_text_message(df.format(hour)+":"+df.format(minute)+":"+df.format(second));
		main.m_clock_display_label.set_needle(hour, minute, second);
    }
    
    
    @UpnpAction
    public void setHour(@UpnpInputArgument(name = "NewTargetValue") int newTargetValue) {

        int hourOldValue = hour;
        hour = newTargetValue;

	//main.m_clock_display_label.set_text_message(newTargetValue+":"+minute+":"+second);
	update_clock(false);
	 // This will send a UPnP event, it's the name of a state variable that sends events
    getPropertyChangeSupport().firePropertyChange("Hour", hourOldValue, hour);
    }
    
    @UpnpAction
    public void setMinute(@UpnpInputArgument(name = "NewTargetValue") int newTargetValue) {

        int minuteOldValue = minute;
        minute = newTargetValue;

	//main.m_clock_display_label.set_text_message(hour+":"+newTargetValue+":"+second);
    update_clock(false);
	 // This will send a UPnP event, it's the name of a state variable that sends events
    getPropertyChangeSupport().firePropertyChange("Minute", minuteOldValue, minute);
    }
    
    @UpnpAction
    public void setSecond(@UpnpInputArgument(name = "NewTargetValue") int newTargetValue) {

        int secondOldValue = second;
        second = newTargetValue;

	//main.m_clock_display_label.set_text_message(hour+":"+minute+":"+newTargetValue);
	update_clock(false);
	 // This will send a UPnP event, it's the name of a state variable that sends events
    getPropertyChangeSupport().firePropertyChange("Second", secondOldValue, second);
    }
    
    public void addMinute(int add){
    	setMinute(minute+add);
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

    public void setMetaData(String text){
    	MetaData = text;
    	getPropertyChangeSupport().firePropertyChange("MetaData", "", text);
    }
    
    @UpnpStateVariable(defaultValue="", sendEvents=true)
    private String MetaData = "location=livingroom&owner=charlie";

    @UpnpAction(out=@UpnpOutputArgument(name="ResultMetaData"))
    public String getMetaData()
    { return MetaData; }

}
