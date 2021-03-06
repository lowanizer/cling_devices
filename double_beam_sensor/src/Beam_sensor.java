import org.teleal.cling.binding.annotations.*;

import java.beans.PropertyChangeSupport;
// Upnp Service are classes + annotations.

import org.teleal.cling.binding.annotations.*;
import java.beans.PropertyChangeSupport;

@UpnpService(
        serviceId = @UpnpServiceId("BeamSensor"),
        serviceType = @UpnpServiceType(value = "BeamSensor", version = 1)
)
public class Beam_sensor {

    private final PropertyChangeSupport propertyChangeSupport;
  
    public Beam_sensor() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    @UpnpStateVariable(defaultValue = "true", sendEvents = true)
    private Boolean beam1 = true;
    @UpnpStateVariable(defaultValue = "true", sendEvents = true)
    private Boolean beam2 = true;
    @UpnpStateVariable(defaultValue = "none", sendEvents = true)
    private String lastmove = "none";
    
    private Boolean r1 = false;
    private Boolean r2 = false;
    private int entrances = 0;
    private int exits = 0;
    private int side = 0;

    public void update_move_dir(int nbeam, Boolean new_state){
    	if(!new_state){ // block
    		if(!r1 && !r2 && side==0){ // first entry
    			side = nbeam;
    		}
    		r1 = false;
    		r2 = false;    		
    	}
    	else{ // release
    		if(nbeam==1) r1 = true;
    		else r2 = true;
    		
    		// 2 releases = passage
    		if(r1 && r2){
    			if(side!=nbeam){ // no go-and-forth through the door : actual passage
    				if(nbeam==1){
    					main.m_beam_display_label.draw_arrow(-1);
    					exits++;
    					setLastmove("exit ("+exits+")");
    				}
        			else{
        				main.m_beam_display_label.draw_arrow(1);
        				entrances++;
        				setLastmove("entrance ("+entrances+")");  				
        			}
    			}

    			side = 0;
    			r1 = false;
        		r2 = false;
    		}
    		
    		if(beam1 && beam2){
    			side=0;
    			r1 = false;
        		r2 = false;
    		}
    	}
    }
    
    
    @UpnpAction
    public void setLastmove(@UpnpInputArgument(name = "NewTargetValue") String newTargetValue) {

        String oldValue = lastmove;
        lastmove = newTargetValue;

	 // This will send a UPnP event, it's the name of a state variable that sends events
    getPropertyChangeSupport().firePropertyChange("lastmove", oldValue, lastmove);
    }
    
    @UpnpAction
    public void setBeam1(@UpnpInputArgument(name = "NewTargetValue") Boolean newTargetValue) {

        Boolean beamOldValue = beam1;
        beam1 = newTargetValue;

	main.m_beam_display_label.draw_lasers(newTargetValue, beam2);

	update_move_dir(1, beam1);
	 // This will send a UPnP event, it's the name of a state variable that sends events
    getPropertyChangeSupport().firePropertyChange("Beam1", beamOldValue, beam1);
    }
    
    @UpnpAction 
    public void setBeam2(@UpnpInputArgument(name = "NewTargetValue") Boolean newTargetValue) {

        Boolean beamOldValue = beam2;
        beam2 = newTargetValue;

	main.m_beam_display_label.draw_lasers(beam1, newTargetValue);
    
    update_move_dir(2, beam2);
	 // This will send a UPnP event, it's the name of a state variable that sends events
    getPropertyChangeSupport().firePropertyChange("Beam2", beamOldValue, beam2);
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultBeam1"))
    public Boolean getBeam1() {
        return beam1;
    }
    
    @UpnpAction(out = @UpnpOutputArgument(name = "ResultBeam2"))
    public Boolean getBeam2() {
        return beam2;
    }
    
    @UpnpAction(out = @UpnpOutputArgument(name = "ResultLastmove"))
    public String getLastmove() {
        return lastmove;
    }

    public void setMetaData(String text){
    	MetaData = text;
    	getPropertyChangeSupport().firePropertyChange("MetaData", "", text);
    }
    
    @UpnpStateVariable(defaultValue="", sendEvents=true)
    private String MetaData = "location=bedroom&owner=charlie";
    
    @UpnpAction(out=@UpnpOutputArgument(name="ResultMetaData"))
    public String getMetaData()
    { return MetaData; }

}
