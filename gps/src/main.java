
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

class gps_display_label extends javax.swing.JLabel
{
	public gps_display_label()
	{
		super(main.create_image_icon("gps.png", "GPS"));
		m_text_messagel1="";
		m_text_messagel2="";
		m_text_messagel3="";
	}

	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if(m_text_messagel1.equals("") && m_text_messagel2.equals("") && m_text_messagel3.equals("")) return;

		Font f = new Font("SansSerif", Font.PLAIN, 26);
		g.setFont(f);
		FontMetrics fm=g.getFontMetrics();

		final Dimension d=getSize();

		final int line_height=fm.getHeight();

		Graphics2D g2d=(Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					     RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g2d.drawString(m_text_messagel1, (int)d.getWidth()/2-fm.stringWidth(m_text_messagel1)/2,
				(int)d.getHeight()/3);
		g2d.drawString(m_text_messagel2, (int)d.getWidth()/2-fm.stringWidth(m_text_messagel2)/2,
				(int)d.getHeight()/2);
		g2d.drawString(m_text_messagel3, (int)d.getWidth()/2-fm.stringWidth(m_text_messagel3)/2,
				(int)(2*d.getHeight()/3));
	}

	public void set_text_messagel1(String message)
	{
		m_text_messagel1=message;
		repaint();
	}
	public void set_text_messagel2(String message)
	{
		m_text_messagel2=message;
		repaint();
	}
	public void set_text_messagel3(String message)
	{
		m_text_messagel3=message;
		repaint();
	}

	private String m_text_messagel1;
	private String m_text_messagel2;
	private String m_text_messagel3;

}

class main implements Runnable
{
	DefaultServiceManager<Gps> m_manager;
	static gps_display_label m_gps_display_label;

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
				UDN.uniqueSystemIdentifier("Demo GPS"));

		DeviceType type=new UDADeviceType("Gps", 1);

		DeviceDetails details=new DeviceDetails(
				"Friendly GPS",
				new ManufacturerDetails("ACME"),
				new ModelDetails("TamTam 9000",
					"A friendly GPS",
					"v1"));
			
		LocalService<Gps> display_service=
			(LocalService<Gps>)
			new AnnotationLocalServiceBinder().read(Gps.class);

		m_manager=new DefaultServiceManager<Gps>(display_service,
				Gps.class);

		display_service.setManager(m_manager);

		return new LocalDevice(identity, type, details, display_service);

	}

	public static void main(String[] args)
	{
		Logger logger=Logger.getLogger("");
		logger.setLevel(Level.SEVERE);

		JFrame frame=new JFrame("GPS");
		frame.setResizable(false);
		frame.setLocation(250, 150);
		
		m_gps_display_label=new gps_display_label();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().setBackground(java.awt.Color.white);
		frame.getContentPane().add(m_gps_display_label, BorderLayout.CENTER);

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
			
			int lastwait = 30;
			m_manager.getImplementation().setLastwait(lastwait);
			String lastloc = "supermarket";
			m_manager.getImplementation().setLastloc(lastloc);
			String location = "on the way home";
			m_manager.getImplementation().setLocation(location);
			
		} catch(Exception ex){
			System.err.println(ex);
		}
	}
}
