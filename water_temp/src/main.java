
import org.teleal.cling.model.message.header.STAllHeader;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.registry.RegistryListener;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;

import org.teleal.cling.binding.annotations.AnnotationLocalServiceBinder;

import org.teleal.cling.model.DefaultServiceManager;

import org.teleal.cling.model.meta.DeviceDetails;
import org.teleal.cling.model.meta.DeviceDetails;
import org.teleal.cling.model.meta.DeviceIdentity;
import org.teleal.cling.model.meta.Icon;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.meta.ManufacturerDetails;
import org.teleal.cling.model.meta.ModelDetails;
import org.teleal.cling.model.meta.ModelDetails;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.model.types.UDN;

import org.teleal.cling.model.action.ActionExecutor;
import org.teleal.cling.model.action.ActionInvocation;

import java.util.logging.Logger;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Dimension;

import javax.swing.JProgressBar;
import java.awt.Graphics2D;
import java.awt.BorderLayout;

import java.beans.PropertyChangeSupport;

import java.awt.font.*;
import java.awt.*;

class temp_display_label extends javax.swing.JLabel
{
	public temp_display_label()
	{
		super(main.create_image_icon("thermometer.png", "Water thermometer"));
		m_text_message="";
	}

	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if(m_text_message.equals("")) return;

		Font f = new Font("SansSerif", Font.PLAIN, 35);
		g.setFont(f);
		FontMetrics fm=g.getFontMetrics();

		final Dimension d=getSize();

		final int line_height=fm.getHeight();
		final int line_width=fm.stringWidth(m_text_message);

		Graphics2D g2d=(Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					     RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g2d.drawString(m_text_message, (int)d.getWidth()/2-line_width/2,
				(int)d.getHeight()/2);
	}

	public void set_text_message(String message)
	{
		m_text_message=message;
		repaint();
	}


	private String m_text_message;

}

class main implements Runnable
{
	DefaultServiceManager<Water_temp> m_manager;
	static temp_display_label m_temp_display_label;

	// [Helper] Returns an image icon using the gui.getClass() or null 
	// if the path was not valid
	public static  ImageIcon create_image_icon(String path, String description) //{{{
	{
		java.net.URL img_url=main.class.getResource(path);
		if(img_url!=null) return new ImageIcon(img_url, description);
		else {
			System.err.println("WARNING: couldn't find icon \""+path+"\"");
			return null;
		}

	} //}}}

	LocalDevice create_device() throws
		Exception
	{
		DeviceIdentity identity = new DeviceIdentity(
				UDN.uniqueSystemIdentifier("Demo water thermometer"));

		DeviceType type=new UDADeviceType("Water_temp", 1);

		DeviceDetails details=new DeviceDetails(
				"Friendly Water Thermometer",
				new ManufacturerDetails("ACME"),
				new ModelDetails("Hotcold 6000",
					"A friendly Water Thermometer",
					"v1"));
			
		LocalService<Water_temp> display_service=
			(LocalService<Water_temp>)
			new AnnotationLocalServiceBinder().read(Water_temp.class);

		m_manager=new DefaultServiceManager<Water_temp>(display_service,
				Water_temp.class);

		display_service.setManager(m_manager);

		return new LocalDevice(identity, type, details, display_service);

	}

	public static void main(String[] args)
	{
		Logger logger=Logger.getLogger("");
		logger.setLevel(Level.SEVERE);

		JFrame frame=new JFrame("Water Thermometer");
		m_temp_display_label=new temp_display_label();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().setBackground(java.awt.Color.white);
		frame.getContentPane().add(m_temp_display_label, BorderLayout.CENTER);

		frame.pack();
		frame.setVisible(true);

		Thread server_thread = new Thread (new main());
		server_thread.setDaemon(false);
		server_thread.start();
	}

	public void run()
	{
		try {
			final UpnpService upnp_service=new UpnpServiceImpl();
			
			// setup cleanup at shutdown
			Runtime.getRuntime().addShutdownHook(new Thread(){
				@Override
				public void run() {
					upnp_service.shutdown();
				}
			});

			// add the bound local device to the registry
			upnp_service.getRegistry().addDevice(create_device());

			double temperature = 37.5;
			m_manager.getImplementation().setTemperature(temperature);
			/*
			while(true){
				temperature += (Math.floor(Math.random()*11)-5)/10;
				Thread.sleep(1000);
				m_manager.getImplementation().setTemperature(temperature);
			}
			*/
			
		} catch(Exception ex){
			System.err.println(ex);
		}
	}
}
