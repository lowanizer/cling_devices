
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

class beam_display_label extends javax.swing.JLabel implements MouseListener
{

	public beam_display_label(String path)
	{
		super(main.create_image_icon(path, "Beam sensors"));
		addMouseListener(this);
		beam1 = true;
		beam2 = true;
		arrow = 0;
	} 

	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		final Dimension d=getSize();
		
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					     RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		Image img = null;
		String img_path = "";
		
		if(beam1){
			img_path = "laser.png";
		}
		else{
			img_path = "lasercut.png";
		}
		try {
			img = ImageIO.read(new File(img_path));
		} catch (IOException e) {
			System.out.println("Error loading image : "+e);
		}
		g2d.drawImage(img , 0, 0, null);
		
		if(beam2){
			img_path = "laser.png";
		}
		else{
			img_path = "lasercut.png";
		}
		try {
			img = ImageIO.read(new File(img_path));
		} catch (IOException e) {
			System.out.println("Error loading image : "+e);
		}
		g2d.drawImage(img ,(int)d.getWidth()-275,0, null);
		
		if(arrow==1){
			img_path = "arrow_r.png";
		}
		else if(arrow==-1){
			img_path = "arrow_l.png";
		}
		if(arrow!=0){
			try {
				img = ImageIO.read(new File(img_path));
			} catch (IOException e) {
				System.out.println("Error loading image : "+e);
			}
			g2d.drawImage(img ,0,(int)d.getHeight()-110, null);
		}
	}
	
	public void draw_lasers(Boolean b1, Boolean b2){
		beam1 = b1;
		beam2 = b2;
		repaint();
	}
	
	public void draw_arrow(int dir){
		arrow = dir;
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getX()<=275){
			main.m_manager.getImplementation().setBeam1(!beam1);
		}
		else{
			main.m_manager.getImplementation().setBeam2(!beam2);
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	
	private Boolean beam1;
	private Boolean beam2;
	private int arrow;
}

class main implements Runnable
{
	static DefaultServiceManager<Beam_sensor> m_manager;
	static beam_display_label m_beam_display_label;
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
				UDN.uniqueSystemIdentifier("Demo Beam sensor"));

		DeviceType type=new UDADeviceType("Beam_sensor", 1);

		DeviceDetails details=new DeviceDetails(
				"Friendly Double Beam Sensor",
				new ManufacturerDetails("ACME"),
				new ModelDetails("Lazers 7000",
					"A friendly Double Beam Sensor",
					"v1"));
			
		LocalService<Beam_sensor> display_service=
			(LocalService<Beam_sensor>)
			new AnnotationLocalServiceBinder().read(Beam_sensor.class);

		m_manager=new DefaultServiceManager<Beam_sensor>(display_service,
				Beam_sensor.class);

		display_service.setManager(m_manager);

		return new LocalDevice(identity, type, details, display_service);

	}

	public static void main(String[] args)
	{
		Logger logger=Logger.getLogger("");
		logger.setLevel(Level.SEVERE);

		JFrame frame=new JFrame("Double Beam Sensor");
		m_beam_display_label=new beam_display_label("whitebg.png");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().setBackground(java.awt.Color.white);
		frame.getContentPane().add(m_beam_display_label, BorderLayout.CENTER);

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

			Boolean beam1 = true;
			Boolean beam2 = true;
			m_manager.getImplementation().setBeam1(beam1);
			m_manager.getImplementation().setBeam2(beam2);
			m_manager.getImplementation().setLastmove("none");
			
		} catch(Exception ex){
			System.err.println(ex);
		}
	}
}
