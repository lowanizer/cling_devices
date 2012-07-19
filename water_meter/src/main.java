
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
import javax.swing.Timer;

import java.awt.Graphics;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Dimension;

import javax.swing.JProgressBar;
import java.awt.Graphics2D;
import java.awt.BorderLayout;

import java.beans.PropertyChangeSupport;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.*;
import java.awt.*;

class wm_display_label extends javax.swing.JLabel implements MouseListener
{
	public wm_display_label()
	{
		super(main.create_image_icon("water_drop.png", "Water Meter"));
		addMouseListener(this);
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
	
	Timer t = new Timer(50,new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			click();
		}
    } );
	
	int plus_minus=0;
	
	public void click(){
		main.m_manager.getImplementation().addDebit(plus_minus);
	}

	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getY()>=180){
			if(e.getX()<=70){
				plus_minus = -1;
				main.m_manager.getImplementation().addDebit(plus_minus);
				t.setInitialDelay(500);
				t.start();
			}
			else if(e.getX()>=280){
				plus_minus = 1;
				main.m_manager.getImplementation().addDebit(plus_minus);
				t.setInitialDelay(500);
				t.start();
			}
		}	
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		t.stop();
	}

	private String m_text_message;

}

class main implements Runnable
{
	static DefaultServiceManager<Water_meter> m_manager;
	static wm_display_label m_wm_display_label;

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
				UDN.uniqueSystemIdentifier("Demo water meter"));

		DeviceType type=new UDADeviceType("Water_Meter", 1);

		DeviceDetails details=new DeviceDetails(
				"Friendly Water Meter",
				new ManufacturerDetails("ACME"),
				new ModelDetails("Plic ploc 5000",
					"A friendly Water Meter",
					"v1"));
			
		LocalService<Water_meter> display_service=
			(LocalService<Water_meter>)
			new AnnotationLocalServiceBinder().read(Water_meter.class);

		m_manager=new DefaultServiceManager<Water_meter>(display_service,
				Water_meter.class);

		display_service.setManager(m_manager);

		return new LocalDevice(identity, type, details, display_service);

	}

	public static void main(String[] args)
	{
		Logger logger=Logger.getLogger("");
		logger.setLevel(Level.SEVERE);

		JFrame frame=new JFrame("Water Meter");
		frame.setResizable(false);
		frame.setLocation(250, 150);
		
		m_wm_display_label=new wm_display_label();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().setBackground(java.awt.Color.white);
		frame.getContentPane().add(m_wm_display_label, BorderLayout.CENTER);

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

			int debit = 0;
			m_manager.getImplementation().setDebit(debit);
			
		} catch(Exception ex){
			System.err.println(ex);
		}
	}
}
