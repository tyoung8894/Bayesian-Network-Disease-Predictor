package tyleryoung;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.*;

import javabayes.InferenceGraphs.InferenceGraph;
import javabayes.InferenceGraphs.InferenceGraphNode;
import javabayes.InterchangeFormat.IFException;

import java.util.*;


/**
 * @author Tyler Young
 *
 *main class of the project that runs the GUI
 *read the README for info how this project works
 *
 *used https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/examples/components/SplitPaneDemo2Project/src/components/SplitPaneDemo2.java
 *as resource for general layout for my GUI
 */
public class UserGUI2 extends JFrame implements ListSelectionListener {
	private JLabel label;
	private JButton btnNewButton_1;
	private JButton btnNewButton_2;
	private JPanel panel;
	private JSplitPane buttonPane;
	private JButton addSymptomButton;
	private JButton generateButton;
	private JButton clearButton;
	private UserGUI diseaseSymptomGUI;
	private String symptomToBeAdded;
	private ArrayList<String> observationSymptoms;

	public UserGUI2() throws IOException, IFException {
		//set JFrame title 
		super("Symptom Checker- Identify possible diseases for your symptoms");
		//create a list that will hold the user's input symptoms
		observationSymptoms = new ArrayList<String>();

		//Create an instance of the GUI
		diseaseSymptomGUI = new UserGUI();

		JSplitPane top = diseaseSymptomGUI.getSplitPane();

		//add selection listener to the list in gui
		diseaseSymptomGUI.getSymptomList().addListSelectionListener(this);
		top.setBorder(null);

		//Provide minimum sizes for the two components in the split pane
		top.setMinimumSize(new Dimension(100, 50));

		//Clear button that removes all symptoms set to be observed
		clearButton = new JButton("Clear");
		clearButton.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				observationSymptoms.clear();
				label.setText("Symptoms cleared.  Enter new set of symptoms");
				try {
					diseaseSymptomGUI.updateLabel("No symptoms added yet");
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (IFException e1) {
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

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				top, label);

		panel.add(splitPane, BorderLayout.NORTH);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(180);

		buttonPane = new JSplitPane();
		panel.add(buttonPane, BorderLayout.SOUTH);

		//adds a button for selecting a symptom, if a symptom in the scroll list is 
		//selected and the user clicks the button, the symptom will be added to the 
		//list of symptoms to be set as observed
		addSymptomButton = new JButton("Add The Selected Symptom ");
		addSymptomButton.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				label.setText(symptomToBeAdded + " added to symptoms");
				observationSymptoms.add(symptomToBeAdded);
				String textToDisplay = "<html><p>Symptoms entered:</p> ";
				int count = 0;
				for(String userEnteredSymptom: observationSymptoms){
					count++;
					textToDisplay += "<p>" + count + ". " + userEnteredSymptom + "</p>";
				}
				textToDisplay += "</html>";
				try {
					diseaseSymptomGUI.updateLabel(textToDisplay);
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (IFException e1) {
					e1.printStackTrace();
				}

			}
		});

		buttonPane.setLeftComponent(addSymptomButton);
		generateButton = new JButton("Generate Likely Diseases");

		//if "generate likely diseases" button is clicked in gui, all of the symptoms the user
		//has added will be set as observed variables in the graph, and the top diseases will output on the right label of the GUI
		generateButton.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				try {
					InferenceGraph graph = diseaseSymptomGUI.generateGraph();
					Vector<InferenceGraphNode> guiNodes = graph.get_nodes();
					JavaBayes newBayes = new JavaBayes(graph, guiNodes);

					//finds the top diseases and updates the label with the text of the disease names
					//formats the label with html
					ArrayList<String> likelyDiseases = newBayes.getBelief(observationSymptoms, guiNodes, graph);
					try {
						String diseaseOutcomeText = "<html><p>Top Diseases:</p> ";
						int count=0;
						for(String disease: likelyDiseases){
							count++;
							diseaseOutcomeText += "<p>" + count + "." + disease + "</p>";
						}
						diseaseSymptomGUI.updateLabel(diseaseOutcomeText);
					} catch (IOException e2) {
						e2.printStackTrace();
					} catch (IFException e2) {
						e2.printStackTrace();
					}

				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (IFException e1) {
					e1.printStackTrace();
				}

			}
		});
		buttonPane.setRightComponent(generateButton);
		label.setMinimumSize(new Dimension(30, 30));
	}


	/* 
	 * Changes the text of the label bar to the selected symptom name in the scroll panel of symptoms
	 */
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting())
			return;

		JList theList = (JList)e.getSource();
		if (theList.isSelectionEmpty()) {
			label.setText("Nothing selected.");
		} else {
			String symptomSelected = theList.getSelectedValue().toString();
			symptomToBeAdded = symptomSelected;
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



	/**
	 * @param args
	 * main class to run the entire project
	 */
	public static void main(String[] args) {
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					createAndShowGUI();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (IFException e) {
					e.printStackTrace();
				}
			}
		});
	}
}


