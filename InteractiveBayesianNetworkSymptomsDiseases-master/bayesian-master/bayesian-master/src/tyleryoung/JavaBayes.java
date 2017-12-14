package tyleryoung;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.text.html.HTMLDocument.Iterator;

import com.sun.xml.internal.bind.v2.runtime.reflect.ListIterator;

import java.util.Map.Entry;

import javabayes.BayesianNetworks.DiscreteFunction;
import javabayes.BayesianNetworks.DiscreteVariable;
import javabayes.BayesianNetworks.ProbabilityFunction;
import javabayes.BayesianNetworks.ProbabilityVariable;
import javabayes.InferenceGraphs.InferenceGraph;
import javabayes.InferenceGraphs.InferenceGraphNode;
import javabayes.InterchangeFormat.IFException;
import javabayes.InterchangeFormat.InterchangeFormat;
import javabayes.QuasiBayesianInferences.QBInference;
import norsys.neticaEx.aliases.Node;

import java.sql.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 *JavaBayes bayesian network for inference
 *uses JavaBayes API(javabayes-0.346.jar)
 */
public class JavaBayes {

	private static final int MegaBytes = 10241024;
	public String diseaseResult = "";
	public Vector<InferenceGraphNode> nodes;
	public String[] symptomNames;
	public InferenceGraph graph;

	public JavaBayes(InferenceGraph graph, Vector<InferenceGraphNode> nodes){
		this.graph = graph;
		this.nodes =nodes;
	}


	/**
	 * @throws IOException
	 * @throws IFException
	 * 
	 * Generates the symptom names from the nodes in the graph
	 * and puts them in the symptomNames list
	 */
	public void initialize() throws IOException, IFException{
		ArrayList<String> list = new ArrayList<String>();
		for(InferenceGraphNode symptomNode: nodes){
			list.add(symptomNode.get_name());
		}
		String[] answer = list.toArray(new String[list.size()]);
		setSymptomList(answer);
		//return answer;
	}


	public void setGraph(InferenceGraph inputGraph){
		this.graph = inputGraph;
	}
	public void setSymptomList(String[] symptoms){
		symptomNames = symptoms;
	}

	public void setNodes(Vector<InferenceGraphNode> symptomNodes){
		nodes = symptomNodes;
	}

	public InferenceGraph getGraph(){
		return this.graph;
	}

	public String[] getSymptomList(){
		return this.symptomNames;
	}

	public Vector<InferenceGraphNode> getNodes(){
		return this.nodes;
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

		//String topDisease="";

		//set the values of the symptom nodes to true if they are entered by user in the GUI
		for(InferenceGraphNode node: inputNodes){
			for(String observation: observations){
				if(node.get_name().equals(observation)){
					System.out.println(node.get_name());
					node.set_observation_value("TRUE");
				}
			}

		}

		QBInference qbi = new QBInference(inputGraph.get_bayes_net(), false);

		//run probability inference on the last node, which is the disease node that has values for each disease name
		qbi.inference(inputNodes.get(inputNodes.size()-1).get_name());

		//get each disease probability values
		double[] values = qbi.get_result().get_values();


		////test loop to print all probabilities
		//
		///for (double dd: values){
		//System.out.println(dd);
		//}
		//qbi.get_result().print();
		//ProbabilityFunction pFunc = qbi.get_result();
		//pFunc.print();
		//		for(Double dd:values){
		//			
		//			System.out.println(BigDecimal.valueOf(dd));
		//		}


		//get probability values of each disease based on observed symptoms
		ArrayList<BigDecimal> eList = new ArrayList<BigDecimal>();

		//set the initial max to the probability of the first disease
		double dMax = qbi.get_result().get_value(0);
		BigDecimal maxD = BigDecimal.valueOf(dMax);                      

		int countD = 0;
		int indexD = 0;	
		//index list that holds the index values of disease probabilities that are greater than the previous max probability
		//
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

		////test loops to get the names of the top diseases
		//		for(String disease:topDiseases){
		//			System.out.println(disease);
		//		}
		//topDisease = f.get_value(indexD);


		return topDiseases;

	}
}




