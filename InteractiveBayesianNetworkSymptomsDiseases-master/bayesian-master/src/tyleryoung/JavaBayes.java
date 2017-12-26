package tyleryoung;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Vector;
import javabayes.BayesianNetworks.DiscreteVariable;
import javabayes.InferenceGraphs.InferenceGraph;
import javabayes.InferenceGraphs.InferenceGraphNode;
import javabayes.InterchangeFormat.IFException;
import javabayes.QuasiBayesianInferences.QBInference;

/**
 * @author Tyler Young
 * 
 *JavaBayes bayesian network for inference
 *uses JavaBayes API(javabayes-0.346.jar)
 *
 *http://www.cs.cmu.edu/~javabayes/EBayes/index.html/JavaDoc/index.html
 */
public class JavaBayes {
	private Vector<InferenceGraphNode> nodes;
	private String[] symptomNames;
	private InferenceGraph graph;

	public JavaBayes(InferenceGraph graph, Vector<InferenceGraphNode> nodes){
		this.graph = graph;
		this.nodes = nodes;
	}


	/**
	 * @throws IOException
	 * @throws IFException
	 * 
	 * Generates the symptom names from the nodes in the graph
	 * and puts them in the symptomNames list to be set as the list of symptoms in the GUI
	 */
	public void initializeSymptomList() throws IOException, IFException{
		ArrayList<String> list = new ArrayList<String>();
		for(InferenceGraphNode symptomNode: nodes){
			list.add(symptomNode.get_name());
		}
		String[] answer = list.toArray(new String[list.size()]);
		this.symptomNames = answer;
		//setSymptomList(answer);
	}

	public String[] getSymptomList(){
		return this.symptomNames;
	}


	/**
	 * Sets user entered symptoms from GUI as observed and returns the top 3 likely diseases 
	 * those symptoms.
	 * 
	 * Loops through each disease's probability and finds the top probabilities for the observed symptoms.
	 * Returns the names of those diseases.
	 * 
	 */
	public ArrayList<String> getBelief(ArrayList<String> observations, Vector<InferenceGraphNode> inputNodes, InferenceGraph inputGraph) {
		//set the values of the symptom nodes to true(observed) if they are entered by user in the GUI
		for(InferenceGraphNode node: inputNodes){
			for(String observation: observations){
				if(node.get_name().equals(observation)){
					System.out.println(node.get_name());
					node.set_observation_value("TRUE");
				}
			}

		}

		//create inference object of the input graph
		QBInference qbi = new QBInference(inputGraph.get_bayes_net(), false);

		//run probability inference using JavaBayes's bucket tree elimination
		//on the last node, which is the disease node that has values for each disease name
		qbi.inference(inputNodes.get(inputNodes.size()-1).get_name());

		//get each diseases probability value
		double[] values = qbi.get_result().get_values();

		//set the initial max to the probability of the first disease
		double dMax = qbi.get_result().get_value(0);
		BigDecimal maxD = BigDecimal.valueOf(dMax);                      

		int countD = 0;
		int indexD = 0;	

		//index list that holds the index values of disease probabilities that are greater than the previous max probability
		//use these indices to get the names of the diseases that these represent below, by finding the value of the disease not at that index
		ArrayList<Integer> indexTopDiseases = new ArrayList<Integer>();
		for(double d:values){
			BigDecimal bd = BigDecimal.valueOf(d);
			if(bd.compareTo(maxD)>0){
				maxD=bd;
				System.out.println("New Max:  " +maxD);

				//add the index to the top diseases index list
				indexD = countD;
				indexTopDiseases.add(indexD);
				System.out.println(indexD);
				countD++;
			}
			else{
				countD++;
			}
		}

		ArrayList<String> topDiseases = new ArrayList<String>();

		//funcD only has one variable, which is the disease node
		DiscreteVariable[] funcD = qbi.get_result().get_variables();

		//if there are more than 3 top diseases in the list, take the last 3 added, which are the 3 with largest probabilities
		//otherwise, add all of the top diseases to the list
		//
		//diseases are added by finding the value of the disease node at the index of the top diseases,
		//since the values of the disease node represent each distinct disease
		if(indexTopDiseases.size()>=3){
			for(DiscreteVariable f: funcD){
				topDiseases.add(f.get_value(indexTopDiseases.get(indexTopDiseases.size()-1)));
				topDiseases.add(f.get_value(indexTopDiseases.get(indexTopDiseases.size()-2)));
				topDiseases.add(f.get_value(indexTopDiseases.get(indexTopDiseases.size()-3)));
			}

		}
		else{
			for(DiscreteVariable f: funcD){
				for(int index:indexTopDiseases){
					//get the name of the disease node's value at that index, which is the disease name
					topDiseases.add(f.get_value(index));
				}

			}
		}

		return topDiseases;

	}
}




