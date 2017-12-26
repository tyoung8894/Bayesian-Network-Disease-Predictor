package tyleryoung;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.Font;
import javax.swing.JScrollPane;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JSplitPane;
import javax.swing.JList;
import javabayes.InferenceGraphs.InferenceGraph;
import javabayes.InferenceGraphs.InferenceGraphNode;
import javabayes.InterchangeFormat.IFException;



/**
 * @author Tyler Young
 *GUI that works with main GUI
 */
public class UserGUI extends JPanel{
	private JavaBayes bayesNet;
	private JFrame symptomFrame;
	private JFrame frame;
	private JTextField textField;
	private JLabel label;
	private JLabel diseaseOutcome;
	private JList list;
	private JSplitPane splitPane;
	private String[] symptomNames;
	private String name;
	private InferenceGraph guiGraph;
	private Vector<InferenceGraphNode> theNodes;

	
	public UserGUI() throws IOException, IFException{
		//choose which BIF XML file the program should use to generate
		//the graph to run inference
		this.guiGraph = new InferenceGraph("networkBIF.xml");
		this.theNodes = guiGraph.get_nodes();

		//creates a JavaBayes object, representing the bayesian network of the .xml file that is in BIF xml format
		this.bayesNet = new JavaBayes(guiGraph, theNodes);
		bayesNet.initializeSymptomList();

		//set the symptom names to the list of symptoms 
		//generated in JavaBayes
		symptomNames = bayesNet.getSymptomList();

		//list of symptomNames that are put in a scrollable JList
		//and can be selected
		list = new JList(symptomNames);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setSelectedIndex(0);

		JScrollPane listScrollPane = new JScrollPane(list);

		//right side of GUI, shows top diseases based on entered symptoms from the list
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



	/**
	 * @return
	 * generates a new graph to be used in the GUI
	 * when disease outcomes are generated, otherwise the graph
	 * will still have observed variables from previous symptoms
	 * the user has selected, even if they have hit the "clear button"
	 * @throws IOException
	 * @throws IFException
	 */
	public InferenceGraph generateGraph() throws IOException, IFException{
		InferenceGraph graph = new InferenceGraph("networkBIF.xml");
		return graph;
	}



	/**
	 * @param name  
	 * sets the text on right side of the GUI to the input name string
	 * @throws IOException
	 * @throws IFException
	 */
	protected void updateLabel (String name) throws IOException, IFException {
		diseaseOutcome.setText(name);
	}


	
	/**
	 * @return scrollable list of symptoms
	 */
	public JList getSymptomList() {
		return list;
	}
	
	

	public JSplitPane getSplitPane() {
		return splitPane;
	}

}




