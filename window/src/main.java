
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

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.JProgressBar;
import java.awt.Graphics2D;
import java.awt.BorderLayout;

import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.*;
import java.awt.image.BufferedImage;
import java.awt.*;

class window_display_label extends javax.swing.JLabel implements MouseListener
{

	public window_display_label()
	{
		open_icon=main.create_image_icon("window_opened.png", "Window");
		close_icon=main.create_image_icon("window_closed.png", "Window");
		addMouseListener(this);
		
		setIcon(close_icon);
		closed = true;
	}

	
	public void draw_window(Boolean c){
		closed = c;
		setIcon(c?close_icon:open_icon);
	}

	private Boolean closed;
	private ImageIcon open_icon;
	private ImageIcon close_icon;
	
	@Override
	public void mouseClicked(MouseEvent e) {
		main.m_manager.getImplementation().setClosed(!closed);
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
}

class main implements Runnable
{
	static DefaultServiceManager<Window> m_manager;
	static window_display_label m_window_display_label;
	static JFrame frame;

	// [Helper] Returns an image icon using the gui.getClass() or null 
	// if the path was not valid
	public static ImageIcon create_image_icon(String path, String description) //{{{
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
				UDN.uniqueSystemIdentifier("Demo Window"));

		DeviceType type=new UDADeviceType("Window", 1);

		DeviceDetails details=new DeviceDetails(
				"Friendly Window",
				new ManufacturerDetails("ACME"),
				new ModelDetails("Window 2100",
					"A friendly Window",
					"v1"));
			
		LocalService<Window> display_service=
			(LocalService<Window>)
			new AnnotationLocalServiceBinder().read(Window.class);

		m_manager=new DefaultServiceManager<Window>(display_service,
				Window.class);

		display_service.setManager(m_manager);

		return new LocalDevice(identity, type, details, display_service);

	}

	public static void main(String[] args)
	{
		Logger logger=Logger.getLogger("");
		logger.setLevel(Level.SEVERE);

		JFrame frame=new JFrame("Window");
		frame.setResizable(false);
		frame.setLocation(250, 150);
		
		m_window_display_label=new window_display_label();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//frame.getContentPane().setBackground(java.awt.Color.white);
		frame.getContentPane().add(m_window_display_label, BorderLayout.CENTER);

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

			Boolean closed = true;
			m_manager.getImplementation().setClosed(closed);
			
		} catch(Exception ex){
			System.err.println(ex);
		}
	}
}
