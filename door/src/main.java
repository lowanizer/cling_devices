
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

import java.awt.font.*;
import java.awt.image.BufferedImage;
import java.awt.*;

class door_display_label extends javax.swing.JLabel
{

	public door_display_label()
	{
		super(main.create_image_icon("door_closed.png", "Door"));
		closed = true;
	}

	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		Font f = new Font("SansSerif", Font.PLAIN, 35);
		g.setFont(f);

		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					     RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		Image img = null;
		String img_path = "";
		
		if(closed){
			img_path = "door_closed.png";
		}
		else{
			img_path = "door_opened.png";
		}
		try {
			img = ImageIO.read(new File(img_path));
		} catch (IOException e) {
			System.out.println("Error loading image : "+e);
		}
		g2d.drawImage(img , 0, 0, null);
	}
	
	public void draw_door(Boolean c){
		closed = c;
		repaint();
	}

	private Boolean closed;
}

class main implements Runnable
{
	DefaultServiceManager<Door> m_manager;
	static door_display_label m_door_display_label;
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
				UDN.uniqueSystemIdentifier("Demo Door"));

		DeviceType type=new UDADeviceType("Door", 1);

		DeviceDetails details=new DeviceDetails(
				"Friendly Door",
				new ManufacturerDetails("ACME"),
				new ModelDetails("Door 2100",
					"A friendly Door",
					"v1"));
			
		LocalService<Door> display_service=
			(LocalService<Door>)
			new AnnotationLocalServiceBinder().read(Door.class);

		m_manager=new DefaultServiceManager<Door>(display_service,
				Door.class);

		display_service.setManager(m_manager);

		return new LocalDevice(identity, type, details, display_service);

	}

	public static void main(String[] args)
	{
		Logger logger=Logger.getLogger("");
		logger.setLevel(Level.SEVERE);

		JFrame frame=new JFrame("Door");
		m_door_display_label=new door_display_label();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().setBackground(java.awt.Color.white);
		frame.getContentPane().add(m_door_display_label, BorderLayout.CENTER);

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
