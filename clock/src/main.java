
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

class clock_display_label extends javax.swing.JLabel implements MouseListener
{
	public clock_display_label()
	{
		super(main.create_image_icon("clock.png", "Clock"));
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
				(int)(d.getHeight()/2.5));
		
		g.setColor(new Color(150,0,0));
		
		g.drawLine(150, 150, x0, y0);
		
		g.drawLine(150, 150, x1, y1);
		g.drawLine(151, 150, x1, y1);
		g.drawLine(150, 151, x1, y1);
		g.drawLine(149, 150, x1, y1);
		g.drawLine(150, 149, x1, y1);
				
		g.drawLine(150, 150, x2, y2);
		g.drawLine(151, 150, x2, y2);
		g.drawLine(150, 151, x2, y2);
		g.drawLine(149, 150, x2, y2);
		g.drawLine(150, 149, x2, y2);
	}

	public void set_text_message(String message)
	{
		m_text_message=message;
		repaint();
	}
	
	public void set_needle(int hour, int minute, int second){
		double angle = (2*Math.PI*second/60)-(Math.PI/2);
		x0 = (int)(95*Math.cos(angle)+150);
		y0 = (int)(95*Math.sin(angle)+150);
		
		angle = (2*Math.PI*(minute+(double)second/60)/60)-(Math.PI/2);
		x1 = (int)(90*Math.cos(angle)+150);
		y1 = (int)(90*Math.sin(angle)+150);
		
		angle = (2*Math.PI*(hour+(double)minute/60)/12)-(Math.PI/2);
		x2 = (int)(50*Math.cos(angle)+150);
		y2 = (int)(50*Math.sin(angle)+150);
	}
	
	Timer t = new Timer(10,new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			click();
		}
    } );
	
	int plus_minus=0;
	
	public void click(){
		main.m_manager.getImplementation().addMinute(plus_minus);
	}

	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getY()>=230){
			if(e.getX()<=70){
				plus_minus = -1;
				main.m_manager.getImplementation().addMinute(plus_minus);
				t.setInitialDelay(500);
				t.start();
			}
			else if(e.getX()>=230){
				plus_minus = 1;
				main.m_manager.getImplementation().addMinute(plus_minus);
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
	private int x0, y0, x1, y1, x2, y2 = 150; 
}

class main implements Runnable
{
	static DefaultServiceManager<Clock> m_manager;
	static clock_display_label m_clock_display_label;

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
				UDN.uniqueSystemIdentifier("Demo clock"+Math.random()));

		DeviceType type=new UDADeviceType("Clock", 1);

		DeviceDetails details=new DeviceDetails(
				"Friendly Clock",
				new ManufacturerDetails("ACME"),
				new ModelDetails("Tictac 9500",
					"A friendly Clock",
					"v1"));
			
		LocalService<Clock> display_service=
			(LocalService<Clock>)
			new AnnotationLocalServiceBinder().read(Clock.class);

		m_manager=new DefaultServiceManager<Clock>(display_service,
				Clock.class);

		display_service.setManager(m_manager);

		return new LocalDevice(identity, type, details, display_service);

	}

	public static void main(String[] args)
	{
		Logger logger=Logger.getLogger("");
		logger.setLevel(Level.SEVERE);

		JFrame frame=new JFrame("Clock");
		frame.setResizable(false);
		frame.setLocation(250, 150);
		
		m_clock_display_label=new clock_display_label();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().setBackground(java.awt.Color.white);
		frame.getContentPane().add(m_clock_display_label, BorderLayout.CENTER);

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

			m_manager.getImplementation().setHour(12);
			
		} catch(Exception ex){
			System.err.println(ex);
		}
	}
	
	
}
