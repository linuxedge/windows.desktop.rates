import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;


public class startMonitor extends JWindow {

	private static final long serialVersionUID = 1L;
	static JWindow window = new JWindow();
	static JPanel panel = new JPanel();
	static JLabel lblprevRate = new JLabel();
	static JLabel valprevRate = new JLabel();
	static JLabel lblDate = new JLabel();
	static JLabel lblTime = new JLabel();
	static JLabel lblFooter = new JLabel();
	static JLabel lblRate = new JLabel();
	static JLabel lblError = new JLabel();
	static JLabel lblAbout = new JLabel("o");
	static JLabel lblExit = new JLabel("x");
	static boolean stop = false;
	static String prevRate = ""; 
	static String currRate = "";
	static int delay = 5;
	static Thread prevThread;
	static boolean startMon = false;

	static File logFile = new File(System.getenv("APPDATA").toString() + "\\CurrencyRate.log");

	public startMonitor() {
		stop = false;
		lblRate.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					if (logFile.exists()) {
						ProcessBuilder pb = new ProcessBuilder("Notepad.exe", logFile.toString());
						try {pb.start();	} catch (IOException e1) { JOptionPane.showMessageDialog (null, "Log File not found.", "Monitor Log", JOptionPane.INFORMATION_MESSAGE);	}
					} else {
						JOptionPane.showMessageDialog (null, "Log File not found.", "Monitor Log", JOptionPane.INFORMATION_MESSAGE);
					}
				} else {
					if (!lblError.getText().toString().contains("MONITOR")) {
						stop = true;
					}
					Thread monitor = new Thread() {
						@SuppressWarnings("deprecation")
						public void run() {
							while (!Thread.currentThread().isInterrupted()) {
								for (int i = 0; i < 999999; ++i) {
									if (stop) {
										try { logger(prevThread.toString() + " ended.."); } catch (IOException e) {}
										stop = false; prevThread.stop(); break;
									}
									try {
										prevThread = Thread.currentThread();
										try { logger(prevThread.toString()); } catch (IOException e) {}
										monitorRate();
									} catch (Exception e) {e.printStackTrace();}
									try {
										try { logger("Thread Sleep.."); } catch (IOException e) {}
										TimeUnit.MINUTES.sleep(delay);
									} catch (InterruptedException ex) {}
									if (i > 999) {
										i = 1;
									}
								}
							}
						}
					};
					monitor.start(); 
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {		
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
		});

		lblExit.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent e) {
				try { logger("System Terminated.."); } catch (IOException e1) {} 
				System.exit(0);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

		});

		lblAbout.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent e) {	
				Thread t = new Timer();
				t.start();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

		});

		window.setVisible(true);
	}

	class Timer extends Thread{
		int time = 10;
		public void run(){
			JWindow About = new JWindow();
			JPanel pAbout = new JPanel();
			JLabel pMsg1 = new JLabel("Left Click to Force Update");
			JLabel pMsg2 = new JLabel("Right Click to show Logs");
			JLabel pMsg3 = new JLabel("kemaro®");
			JLabel pMsg4 = new JLabel("xxkemaroxx@gmail.com");
			pMsg1.setBounds(10,4,100,5);
			pMsg2.setBounds(10, 6, 100, 5);
			pMsg3.setBounds(10, 8, 100, 5);
			pMsg4.setBounds(10, 10, 100, 5);
			pAbout.add(pMsg1);
			pAbout.add(pMsg2);
			pAbout.add(pMsg3);
			pAbout.add(pMsg4);
			About.add(pAbout);
			About.setSize(190, 70);
			About.setLocationByPlatform(true);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
			Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
			int x = (int) rect.getMaxX() - About.getWidth();
			int y = 0;
			About.isAlwaysOnTop();
			About.setLocation(x, y);
			About.setVisible(true);
			while(time-- > 0){
				try{
					sleep(150);                     
				}catch(InterruptedException e){}
			}
			About.setVisible(false);
		}
	}

	public void monitorRate() throws IOException, InterruptedException {

		URL dbs;
		lblError.setText("");
		lblRate.setText("<html><span style='font-size:30px'>"+"-------"+"</span></html>");
		lblFooter.setText("<html><span style='font-size:8px'>Connecting to DBS Online..</span></html>");
		logger(lblFooter.getText().toString());
		TimeUnit.MILLISECONDS.sleep(500);
		dbs = new URL("https://www.dbs.com.sg/personal/rates-online/foreign-currency-foreign-exchange.page?pid=sg-dbs-pweb-home-span4module-forex-txtmore-");
		try {
			System.out.println("debug log: URL Fetched Successfully.");
			BufferedReader in = new BufferedReader(new InputStreamReader(dbs.openStream()));
			lblFooter.setText("<html><span style='font-size:8px'>Fetching PHP Rate..</span></html>");
			logger(lblFooter.getText().toString());
			TimeUnit.MILLISECONDS.sleep(500);
			String pesoLine = "<span>Philippine Peso</span>";
			String outputLine = null;
			String inputLine;
			while ((inputLine = in.readLine()) != null)
				if (inputLine.toLowerCase().contains(pesoLine.toLowerCase())) {
					int startPosition = inputLine.indexOf("\"Selling TT/OD\">") + "\"Selling TT/OD\">".length();
					int endPosition = inputLine.indexOf("</td>", startPosition);
					outputLine = inputLine.substring(startPosition, endPosition);
					System.out.println("debug log: PHP Found: " + outputLine);
				}
			in.close();
			lblFooter.setText("<html><span style='font-size:8px'>Rate Found..</span></html>");
			logger(lblFooter.getText().toString());
			TimeUnit.MILLISECONDS.sleep(500);
			float i = 100 / Float.valueOf(outputLine);
			BigDecimal bd = new BigDecimal(Float.toString(i));
			bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
			if (currRate.equals("")) { currRate = bd.toString(); } if (prevRate.equals("")) { prevRate = currRate; }
			if (!currRate.equals(bd.toString())) { prevRate = currRate; currRate = bd.toString(); }
			String date = new SimpleDateFormat("yyyy.MM.dd").format(new Date()); String time = new SimpleDateFormat("hh:mm:ss a").format(new Date());
			lblDate.setText("      " + date); 
			lblTime.setText("     " + time);
			lblRate.setText("<html><span style='font-size:30px'>"+currRate+"</span></html>");
			lblError.setText(""); 
			valprevRate.setText("<html><span style='font-size:8px'>"+prevRate+"</span></html>");
			lblFooter.setText("<html><span style='font-size:8px'>Current Conversion Rate</span></html>");
			lblprevRate.setText("<html><span style='font-size:8px'>Previous Rate: </span></html>");
			logger(lblFooter.getText().toString());
			TimeUnit.MILLISECONDS.sleep(500);
		} catch (IOException e) {
			lblDate.setText("");
			lblTime.setText("");
			lblRate.setText("");
			lblError.setText("NO INTERNET CONNECTION");
			logger(lblError.getText().toString());
		}
	}

	public void logger(String message) throws IOException {

		int startPosition = message.indexOf("<html><span style='font-size:8px'>") + "<html><span style='font-size:8px'>".length();
		int endPosition = message.indexOf("</span></html>", startPosition);

		if (message.contains("<html>")) {
			message = message.substring(startPosition,endPosition); }

		if(!logFile.exists()); {
			logFile.createNewFile();
		}
		long sizeInBytes = logFile.length();
		long sizeInMb = sizeInBytes / (1024 * 1024);
		if(sizeInMb > 1) {
			logFile.delete();
			logFile.createNewFile();
		}
		String date = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss a").format(new Date());
		try(FileWriter fw = new FileWriter(logFile, true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter writeLog = new PrintWriter(bw))
		{
			if (!startMon) {
				writeLog.println("Monitoring Log");
				writeLog.println("---------------------------------------------------------------------");
				writeLog.println("PHP Currency Monitoring Started...");
				startMon = true;
			}
			if(message.toLowerCase().contains("current conversion rate")) {
				writeLog.println(date + ": Rate Updated..");
			} else {
				writeLog.println(date + ": " + message);
			}
			if(message.contains("NO INTERNET CONNECTION") || message.contains("Thread Sleep") || message.contains("ended..") || message.contains("Terminated..")){
				writeLog.println("---------------------------------------------------------------------");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(date + ": " + message);
	}

	public static void buildWindow() {
		panel.setLayout(null);
		lblRate.setBounds(10, 8, 200, 50);
		lblDate.setBounds(100, 22, 200, 10);
		lblTime.setBounds(98, 37, 200, 10);
		lblFooter.setBounds(10, 53, 200, 10);
		lblError.setBounds(20, 33, 200, 10);
		lblprevRate.setBounds(10, 4, 100, 10);
		valprevRate.setBounds(90, 4, 100, 10);
		valprevRate.setForeground(Color.RED);
		lblRate.setForeground(Color.RED);
		lblAbout.setBounds(170, 0, 10, 10);
		lblExit.setBounds(180, 0, 10, 10);
		panel.add(lblRate);
		panel.add(lblDate);
		panel.add(lblTime);
		panel.add(lblFooter);
		panel.add(lblError);
		panel.add(lblprevRate);
		panel.add(valprevRate);
		panel.add(lblAbout);
		panel.add(lblExit);
		window.add(panel);
		window.setSize(190, 70);
		window.setLocationByPlatform(true);
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
		Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
		int x = (int) rect.getMaxX() - window.getWidth();
		int y = 0;
		window.isAlwaysOnTop();
		window.setLocation(x, y);

		lblError.setText("         CLICK TO MONITOR");
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				buildWindow();
				new startMonitor(); 
			}
		}); 
	}	

}