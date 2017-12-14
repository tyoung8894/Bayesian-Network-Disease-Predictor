package joeschweitzer;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.*;

import javabayes.InferenceGraphs.InferenceGraph;
import javabayes.InferenceGraphs.InferenceGraphNode;
import javabayes.InterchangeFormat.IFException;

import java.util.*;


public class UserGUI2 extends JFrame implements ListSelectionListener {
	private JLabel label;
	private JButton btnNewButton_1;
	private JButton btnNewButton_2;
	private JPanel panel;
	private JSplitPane splitPane_1;
	private JButton btnNewButton;
	private JButton generateButton;
	private JButton clearButton;
	public UserGUI diseaseSymptomGUI;
	public String symptomToBeAdded;
	public ArrayList<String> observationSymptoms;

	public UserGUI2() throws IOException, IFException {
		super("Symptom Checker- Identify possible diseases for your symptoms");
		observationSymptoms = new ArrayList<String>();

		//Create an instance of the GUI
		UserGUI newGui = new UserGUI();
		diseaseSymptomGUI = newGui;
		JSplitPane top = diseaseSymptomGUI.getSplitPane();

		//add selection listener to the list in gui
		diseaseSymptomGUI.getImageList().addListSelectionListener(this);
		top.setBorder(null);

		//Provide minimum sizes for the two components in the split pane
		top.setMinimumSize(new Dimension(100, 50));
		
		clearButton = new JButton("Clear");
		clearButton.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				observationSymptoms.clear();
				label.setText("Symptoms cleared.  Enter new set of symptoms");
				try {
					diseaseSymptomGUI.updateLabel("No symptoms added yet");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IFException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		getContentPane().add(clearButton, BorderLayout.SOUTH);
		panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));
		label = new JLabel("Click on a Symptom name in the list.",
				SwingConstants.CENTER);

		//Create a split pane and put "top" (a split pane)
		//and JLabel instance in it.
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				top, label);
		panel.add(splitPane, BorderLayout.NORTH);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(180);

		splitPane_1 = new JSplitPane();
		panel.add(splitPane_1, BorderLayout.SOUTH);

		btnNewButton = new JButton("Add The Selected Symptom ");
		btnNewButton.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				label.setText(symptomToBeAdded + " added to symptoms");
				observationSymptoms.add(symptomToBeAdded);
				String textToDisplay = "<html><p>Symptoms entered:</p> ";
				int count = 0;
				for(String userEnteredSymptom: observationSymptoms){
					count++;
					textToDisplay = textToDisplay+ "<p>" + count + ". " + userEnteredSymptom + "</p>";
				}
				textToDisplay = textToDisplay + "</html>";
				try {
					diseaseSymptomGUI.updateLabel(textToDisplay);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IFException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});


		splitPane_1.setLeftComponent(btnNewButton);
		generateButton = new JButton("Generate Likely Diseases");
		generateButton.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){

				JavaBayes network = diseaseSymptomGUI.getBayesNet();
				Vector<InferenceGraphNode> nodes = diseaseSymptomGUI.theNodes;
				InferenceGraph graph = diseaseSymptomGUI.guiGraph;

				network.setNodes(nodes);
				network.setGraph(graph);
				ArrayList<String> likelyDiseases = network.getBelief(observationSymptoms);
				try {
					String diseaseOutcomeText = "<html><p>Top Diseases:</p> ";
					int count=0;
					for(String disease: likelyDiseases){
						count++;
						diseaseOutcomeText = diseaseOutcomeText + "<p>" + count + "." + disease + "</p>";
					}
					diseaseSymptomGUI.updateLabel(diseaseOutcomeText);
					//diseaseSymptomGUI.updateLabel("Top diseases: " + likelyDisease);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IFException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//System.out.println("DISEASE OUTCOME: " + likelyDisease);
			}
		});
		splitPane_1.setRightComponent(generateButton);
		label.setMinimumSize(new Dimension(30, 30));
	}
	
	

	public JLabel getLabel(){
		return label;
	}

	
	
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting())
			return;

		JList theList = (JList)e.getSource();
		if (theList.isSelectionEmpty()) {
			label.setText("Nothing selected.");
		} else {
			String symptomSelected = theList.getSelectedValue().toString();
			int index = theList.getSelectedIndex();
			symptomToBeAdded = symptomSelected;
			//splitPaneDemo.updateText(name);
			label.setText("Selected Symptom:  " + symptomSelected);
		}
	}

	

	private static void createAndShowGUI() throws IOException, IFException {
		//Create and set up the window.
		JFrame frame = new UserGUI2();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	
	
	public static void main(String[] args) {
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					createAndShowGUI();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IFException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}


