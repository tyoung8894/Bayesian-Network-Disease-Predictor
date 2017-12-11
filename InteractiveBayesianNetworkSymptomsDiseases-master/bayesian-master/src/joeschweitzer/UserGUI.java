package joeschweitzer;

import java.awt.EventQueue;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Frame;

import javax.swing.JInternalFrame;
import java.awt.Label;
import java.awt.TextField;
import java.awt.Color;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Vector;
import java.awt.Dimension;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JList;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import com.sun.xml.internal.ws.api.server.Container;

import javabayes.InferenceGraphs.InferenceGraph;
import javabayes.InferenceGraphs.InferenceGraphNode;
import javabayes.InterchangeFormat.IFException;

import javax.swing.AbstractListModel;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.JTextArea;

public class UserGUI extends JPanel{
	private JavaBayes bayesNet;
	private JFrame symptomFrame;
	private JFrame frame;
	public JPanel cards;
	final static String TITLEPANEL = "Title card";
	final static String SYMPTOMPANEL = "Card with Symptoms";
	final static String DISEASEPANEL = "Card with Diseases";
	private JTextField textField;
	private JLabel label;
	public JLabel diseaseOutcome;
	private JList list;
	private JSplitPane splitPane;
	private String[] symptomNames;
	private String name;
	public InferenceGraph guiGraph;
	public Vector<InferenceGraphNode> theNodes;

	/**
	 * Create the application.
	 * @throws IFException 
	 * @throws IOException 
	 */
	public UserGUI() throws IOException, IFException{
		//Create the list of symptoms and put it in a scroll pane.
		InferenceGraph graph = new InferenceGraph("C:\\Users\\Tyler Young\\Desktop\\formatted.xml");
		guiGraph = graph;
		Vector<InferenceGraphNode> guiNodes = guiGraph.get_nodes();
		theNodes = guiNodes;
		
		JavaBayes network = new JavaBayes(guiGraph, theNodes);
		bayesNet = network;
		bayesNet.initialize();
		
		//symptom names
		symptomNames = bayesNet.getSymptomList();
		//list of diseaseNames
		list = new JList(symptomNames);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setSelectedIndex(0);
		
		JScrollPane listScrollPane = new JScrollPane(list);
		//right side, disease results
		diseaseOutcome = new JLabel();
		diseaseOutcome.setFont(diseaseOutcome.getFont().deriveFont(Font.ITALIC));
		diseaseOutcome.setHorizontalAlignment(JLabel.CENTER);
		JScrollPane diseaseScrollPane = new JScrollPane(diseaseOutcome);


		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				listScrollPane, diseaseScrollPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(150);

	
		Dimension minimumSize = new Dimension(100, 50);
		listScrollPane.setMinimumSize(minimumSize);
		diseaseScrollPane.setMinimumSize(minimumSize);

		splitPane.setPreferredSize(new Dimension(400, 200));
		
	}
	
     
    public JavaBayes getBayesNet(){
    	return this.bayesNet;
    }
    
    public InferenceGraph getInferenceGraph(){
    	return this.guiGraph;
    }

   
    protected void updateLabel (String name) throws IOException, IFException {
    	diseaseOutcome.setText(name);
    }
 

    public JList getImageList() {
        return list;
    }
 
    public JSplitPane getSplitPane() {
        return splitPane;
    }
 

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					createAndShowGUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	//	
	//	 public void valueChanged(ListSelectionEvent e) {
	//	        if (e.getValueIsAdjusting())
	//	            return;
	//	 
	//	        JList theList = (JList)e.getSource();
	//	        if (theList.isSelectionEmpty()) {
	//	            label.setText("Nothing selected.");
	//	        } else {
	//	            int index = theList.getSelectedIndex();
	//	            label.setText("Selected image number " + index);
	//	        }
	//	    }

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event-dispatching thread.
	 * @throws IFException 
	 * @throws IOException 
	 */
	private static void createAndShowGUI() throws IOException, IFException {
		//Create and set up the window.
		JFrame frame = new JFrame("UserGUI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		UserGUI tylerGUI = new UserGUI();
        frame.getContentPane().add(tylerGUI.getSplitPane(), BorderLayout.NORTH);
		//Display the window.
		//frame.pack();
		frame.setVisible(true);
	}
}

	

		
