package frame;
 
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.fazecast.jSerialComm.SerialPort;
 
public class MainFrame extends JFrame {
	
    private static final long serialVersionUID = 1L;
	SerialPort[] portlist = SerialPort.getCommPorts();
	String[] baudList = {"4800", "9600", "38400", "57600", "115200"};
    XYSeries tempData = new XYSeries("Temperature Sensor Readings");
    XYSeries humidData = new XYSeries("Humidity Sensor Readings");
    XYSeries pressureData = new XYSeries("Pressure Sensor Readings");
    XYSeries lightData = new XYSeries("Light Sensor Readings");
    XYSeries redLightData = new XYSeries("Red Light Sensor Readings");
    XYSeries greenLightData = new XYSeries("Green Light Sensor Readings");
    XYSeries blueLightData = new XYSeries("Blue Light Sensor Readings");
    XYSeries degreesXData = new XYSeries("X-Axis Gyroscope Sensor Readings");
    XYSeries degreesYData = new XYSeries("Y-Axis Gyroscope Sensor Readings");
    JComboBox comboBox_comPort = new JComboBox(portlist);
    JComboBox comboBox_baudRate = new JComboBox(baudList);
    JButton btnConnect = new JButton("Connect");
    SerialPort chosenPort;
    double x = 0;

    public MainFrame(String name) {
        super("Serial Incoming Data");
        setResizable(true);
    }
     
    public void addComponentsToPane(final Container pane) {	
        //controls panel
        JPanel controls = new JPanel();
        controls.setLayout(new GridLayout(2,3)); 
        controls.add(new Label("SELECT BAUD RATE:"));
        controls.add(new Label("SELECT PORT:"));
        controls.add(new Label("OPEN PORT "));
        controls.add(comboBox_baudRate);
        controls.add(comboBox_comPort);
        controls.add(btnConnect);
        
        comboBox_baudRate.setModel(new DefaultComboBoxModel(baudList));
		comboBox_baudRate.setSelectedItem("9600");
        comboBox_comPort.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuCanceled(PopupMenuEvent e) {
			}
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			}
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				comboBox_comPort.removeAllItems();
				SerialPort[] portList = SerialPort.getCommPorts();
				for (SerialPort port : portList) {
					comboBox_comPort.addItem(port.getSystemPortName());
				}
			}
		});
        btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {     
					if (btnConnect.getText().equals("Connect")) {
						SerialPort[] portList = SerialPort.getCommPorts();
						chosenPort = portList[comboBox_comPort.getSelectedIndex()];
						chosenPort.setBaudRate(Integer.parseInt(comboBox_baudRate.getSelectedItem().toString()));
						chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
						chosenPort.openPort();
						
						if(chosenPort.openPort()) {  
							JOptionPane.showMessageDialog(btnConnect, chosenPort.getDescriptivePortName() + " --- Port is OPEN.");
							comboBox_comPort.setEnabled(false);
							comboBox_baudRate.setEnabled(false);
							btnConnect.setText("Disconnect");
							System.out.println("Port is open");
							
				            Thread dataTasks = new Thread(new Runnable() {
				                @Override
				                public void run() {
				                	Scanner scanner = new Scanner(chosenPort.getInputStream());                 
				                    while (scanner.hasNextLine()) {
				                    	try  {
				                            String line = scanner.nextLine();
				                            String[] values = line.split(",");
				                            String tempValue = values[0];
				                            String humidValue = values[1];
				                            String pressureValue = values[2];
				                            String proximityValue = values[3];
				                            String lightValue = values[4];
				                            String redValue = values[5];
				                            String greenValue = values[6];
				                            String blueValue = values[7];
				                            String degreesXValue = values[8];
				                            String degreesYValue = values[9];
				                            System.out.println(lightValue + "," + humidValue + "," + pressureValue + "," + proximityValue
				                                		 + "," + lightValue + "," + redValue + "," + greenValue + "," + blueValue
				                                		 + "," + degreesXValue + degreesYValue);
				                            tempData.add(x++, Double.valueOf(tempValue));
				                            humidData.add(x++, Double.valueOf(humidValue));
				                            pressureData.add(x++, Double.valueOf(pressureValue));
				                            // MainFrame.proximityData.add(x++, Double.valueOf(proximityValue));
				                            lightData.add(x++, Double.valueOf(lightValue));
				                            redLightData.add(x++, Double.valueOf(redValue));
				                            greenLightData.add(x++, Double.valueOf(greenValue));
				                            blueLightData.add(x++, Double.valueOf(blueValue));
				                            degreesXData.add(x++, Double.valueOf(degreesXValue));
				                            degreesYData.add(x++, Double.valueOf(degreesYValue));
				                            } catch (Exception e) {
				                                System.err.println("Corrupt incoming data. " + e);
				                                scanner.nextLine(); 
				                                //throw e;
				                                chosenPort.closePort();
				                            }
				                    }
				                }
				            });
				            dataTasks.start();
					} 
				} else {
					// JOptionPane.showMessageDialog(btnConenct, chosenPort.getDescriptivePortName() + " -- Port is closed.");
					System.out.println("Port is closed.");
					chosenPort.closePort();
					comboBox_comPort.setEnabled(true);
					comboBox_baudRate.setEnabled(true);
					btnConnect.setText("Connect");	
					x = 0;
					tempData.clear();
					humidData.clear();
					pressureData.clear();
					lightData.clear();
					redLightData.clear();
					greenLightData.clear();
					blueLightData.clear();
					degreesXData.clear();
					degreesYData.clear();	                     
				}					
			} catch (IndexOutOfBoundsException e1) {
				JOptionPane.showMessageDialog(btnConnect, "Please chose COM port.", "EROOR", getDefaultCloseOperation());
			} catch (Exception e2) {
				JOptionPane.showMessageDialog(btnConnect, e2, "ERROR", getDefaultCloseOperation());
			}
			}
		});

        //graph panel
        JPanel graphsPanel = new JPanel();
        graphsPanel.setLayout(new GridLayout(0,2));
        
        XYSeriesCollection dataset1 = new XYSeriesCollection(tempData);
        XYSeriesCollection dataset2 = new XYSeriesCollection(humidData);
        XYSeriesCollection dataset3 = new XYSeriesCollection(pressureData);
        XYSeriesCollection dataset4 = new XYSeriesCollection(lightData);
        XYSeriesCollection dataset5 = new XYSeriesCollection();
	    dataset5.addSeries(redLightData);
	    dataset5.addSeries(greenLightData);
	    dataset5.addSeries(blueLightData);
        XYSeriesCollection dataset6 = new XYSeriesCollection();
	    dataset6.addSeries(degreesXData);
	    dataset6.addSeries(degreesYData);
        
        JFreeChart chart1 = ChartFactory.createXYLineChart(
                "Temperature", "Time (seconds)", "degrees Celsius", dataset1);
        JFreeChart chart2 = ChartFactory.createXYLineChart(
                "Humidity", "Time (seconds)", "%RH", dataset2);
        JFreeChart chart3 = ChartFactory.createXYLineChart(
                "Barometric Pressure", "Time (seconds)", "kPa", dataset3);
        JFreeChart chart4 = ChartFactory.createXYLineChart(
                "Light Intensity", "Time (seconds)", "integer (0-4097)", dataset4);
        JFreeChart chart5 = ChartFactory.createXYLineChart(
                "RGB Light Intensity", "Time (seconds)", "integer (0-4097)", dataset5);
        JFreeChart chart6 = ChartFactory.createXYLineChart(
                "Gyroscope Tilt", "Time (seconds)", "degrees", dataset6);
        
        graphsPanel.add(new ChartPanel(chart1), -1);      
        graphsPanel.add(new ChartPanel(chart2), -1);        
        graphsPanel.add(new ChartPanel(chart3), -1);
        graphsPanel.add(new ChartPanel(chart4), -1);
        graphsPanel.add(new ChartPanel(chart5), -1);
        graphsPanel.add(new ChartPanel(chart6), -1);
        
        //add panels to content "pane"
        pane.add(controls, BorderLayout.NORTH);
        pane.add(new JSeparator(), BorderLayout.CENTER);
        pane.add(graphsPanel, BorderLayout.SOUTH);
    }
     
    /**
     * Create the GUI and show it.  For thread safety,
     * this method is invoked from the
     * event dispatch thread.
     */
    public static void createAndShowGUI() {
        //Create and set up the window.
        MainFrame frame = new MainFrame("GridLayoutDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Set up the content pane.
        frame.addComponentsToPane(frame.getContentPane());
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
}
