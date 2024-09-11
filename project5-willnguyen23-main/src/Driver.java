import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

public class Driver implements ActionListener {
	
	// Declare class data
	private static final String FILE_NAME = "triplog.csv";
	private static TripPoint main;
	private static ArrayList<TripPoint> points;
	private static Timer timer;
	private static IconMarker marker;
	private static ImageIcon original = new ImageIcon("raccoon.png");
	private static Image raccoon = original.getImage().getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
	private static JMapViewer map;


    public static void main(String[] args) throws FileNotFoundException, IOException  {

    	// Read file and call stop detection
    	main = new TripPoint();
    	TripPoint.readFile(FILE_NAME);
    	    	
    	TripPoint.h1StopDetection();
    	
    	// Set up frame, include your name in the title
    	JFrame frame = new JFrame("Project 5 - William Nguyen");
    	frame.setVisible(true); 
    	frame.setSize(600, 600);
    	frame.setLayout(new BorderLayout());
  
        // Set up Panel for input selections
    	JPanel panel = new JPanel();
    	
        // Play Button
    	JButton button = new JButton("Play");
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         
        // CheckBox to enable/disable stops
    	JCheckBox checkbox = new JCheckBox("Include Stops");
    	
        // ComboBox to pick animation time
    	String[] animationTime = {"Animation Time", "15", "30", "60", "90"};
    	JComboBox<String>comboBox = new JComboBox<String>(animationTime);
    	
        // Add all to top panel
    	panel.add(button);
    	panel.add(comboBox);
    	panel.add(checkbox);
    	frame.add(panel, BorderLayout.NORTH);
 
        // Set up mapViewer
    	map = new JMapViewer();
    	map.setTileSource(new OsmTileSource.TransportMap());
  
        // Set the map center and zoom level
    	Coordinate firstPoint = new Coordinate(TripPoint.getTrip().get(0).getLat(), TripPoint.getTrip().get(0).getLon());
    	map.setDisplayPosition(firstPoint, 4);
        frame.add(map);
        
        marker = new IconMarker(firstPoint, raccoon);
        map.addMapMarker(marker);
   
        // Add listeners for GUI components
		button.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			map.removeAllMapMarkers();
    			map.removeAllMapPolygons();
    			
    			if (timer != null) {
    				timer.stop();
    			}
  			
    			int animationTime = Integer.parseInt((String) comboBox.getSelectedItem());
    			int timeDelay = (animationTime * 1000) / TripPoint.getTrip().size();
    			
    			if(checkbox.isSelected()) {
    				points = TripPoint.getTrip();
    			}
    			else {
    				points = TripPoint.getMovingTrip();
    			}
    			
    			timer = new Timer(timeDelay, new ActionListener() {
    				private int index = 1;
    				public void actionPerformed(ActionEvent e) {
    					if (index == 1) {
	    					Coordinate begin = new Coordinate(points.get(0).getLat(), points.get(0).getLon());
	    					marker = new IconMarker(begin, raccoon);
	    					map.addMapMarker(marker);
	    					map.removeMapMarker(marker);
    					}
    					if (index < points.size()) {
    						if (marker != null) {
    							map.removeAllMapMarkers();
    						}
    						Coordinate now = new Coordinate(points.get(index).getLat(), points.get(index).getLon());
    						Coordinate before = new Coordinate(points.get(index - 1).getLat(), points.get(index - 1). getLon());
    						MapPolygonImpl spot = new MapPolygonImpl(before, before, now);
    						spot.setColor(Color.RED);
    						spot.setBackColor(Color.RED);
    						IconMarker marker = new IconMarker(now, raccoon);
    						map.addMapMarker(marker);
    						map.addMapPolygon(spot);
    						index++;
    					}
    					else {
    						e.getSource();
    					}
    				}
    			}); 
    			timer.start();
    		}	
		});
    };
    	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
