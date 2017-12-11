package joeschweitzer;

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
		//this.diseaseResult= diseaseResult;
		//this.symptomNames = symptomNames;	
	}


	public static void main(String[] args) throws IFException, Throwable {
		//initialize();
	}



	public void initialize() throws IOException, IFException{
		ArrayList<String> list = new ArrayList<String>();
		for(InferenceGraphNode symptomNode: nodes){
			list.add(symptomNode.get_name());
		}
		String[] answer = list.toArray(new String[list.size()]);
		setSymptomList(answer);
		//return answer;
	}

	public void setObservations(ArrayList<String> list){
		//1 get node with that name, then set observation to true

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

	//	public static String initialize() throws IOException, IFException{
	//		InferenceGraph graph = new InferenceGraph("C:\\Users\\Tyler Young\\Desktop\\formatted.xml");
	//		Vector<InferenceGraphNode> symptomNodes = graph.get_nodes();
	//		//nodes = symptomNodes;
	//		//nodes = symptomNodes;
	////		nodes.get(95).set_observation_value("TRUE");
	////		nodes.get(54).set_observation_value("TRUE");
	////		nodes.get(31).set_observation_value("TRUE");
	////		nodes.get(12).set_observation_value("TRUE");
	////		String belief = getBelief(graph, nodes.get(nodes.size()-1)); 
	////		return belief;
	//		
	//	}



	//		long freeMemory = Runtime.getRuntime().freeMemory()/MegaBytes;
	//		long totalMemory = Runtime.getRuntime().totalMemory()/MegaBytes;
	//		long maxMemory = Runtime.getRuntime().maxMemory()/MegaBytes;
	//		
	//		System.out.println("JVM freeMemory: " + freeMemory);
	//		System.out.println("JVM totalMemory also equals to initial heap size of JVM: " + totalMemory);
	//		System.out.println("JVM maxMemory also equal to max heap size of JVM " + maxMemory);





	/**
	 * Helper function to create node since not as straightforward with JavaBayes
	 * to get a pointer back to the node that is being added
	 */
	private static InferenceGraphNode createNode(
			InferenceGraph ig, String name, String trueVariable, String falseVariable) {
		ig.create_node(0, 0);
		InferenceGraphNode node = (InferenceGraphNode) ig.get_nodes().lastElement();

		node.set_name(name);
		ig.change_values(node, new String[] {trueVariable, falseVariable});

		return node;
	}

	/**
	 * Sets probabilities for a leaf node
	 */
	private static void setProbabilityValues(InferenceGraphNode node, double trueValue, double falseValue) {
		node.set_function_values(new double[] {trueValue, falseValue});
	}

	/**
	 * Returns the index of the variable for the parent that has the given variable 
	 */
	private static int getVariableIndex(InferenceGraphNode node, String parentVariable) {

		for (InferenceGraphNode parent : (Vector<InferenceGraphNode>) node.get_parents()) {
			int variableIndex = 0;

			for (String variable : parent.get_values()) {
				if (variable.equals(parentVariable)) {
					return variableIndex;
				}

				variableIndex++;
			}
		}

		return 0;
	}

	/**
	 * Returns the total number of values for the parent that has the given variable
	 */
	private static int getTotalValues(InferenceGraphNode node, String parentVariable) {
		for (InferenceGraphNode parent : (Vector<InferenceGraphNode>) node.get_parents()) {

			for (String variable : parent.get_values()) {
				if (variable.equals(parentVariable)) {
					return parent.get_number_values();
				}
			}
		}

		return 0;
	}

	/**
	 * Sets probabilities for a node that has a parent
	 */
	private static void setProbabilityValues(InferenceGraphNode node, String parentVariable, 
			double trueValue, double falseValue) {
		int variableIndex = getVariableIndex(node, parentVariable);
		int totalValues = getTotalValues(node, parentVariable);

		double[] probabilities = node.get_function_values();
		probabilities[variableIndex] = trueValue;
		probabilities[variableIndex + totalValues] = falseValue;
		node.set_function_values(probabilities);
	}

	/**
	 * Sets probabilities for a node that has two parents
	 */
	private static void setProbabilityValues(InferenceGraphNode node, String firstParentVariable, 
			String secondParentVariable, double trueValue, double falseValue) {

		int variableIndex = (getVariableIndex(node, firstParentVariable) * 2) + 
				getVariableIndex(node, secondParentVariable);
		int totalValues = getTotalValues(node, firstParentVariable) + 
				getTotalValues(node, secondParentVariable);

		double[] probabilities = node.get_function_values();
		probabilities[variableIndex] = trueValue;
		probabilities[variableIndex + totalValues] = falseValue;
		node.set_function_values(probabilities);
	}



	/**
	 * Sets user entered symptoms as observed and returns the top 3 likely diseases
	 * 
	 */
	public ArrayList<String> getBelief(ArrayList<String> observations) {

		String topDisease="";
		for(InferenceGraphNode node: nodes){
			for(String observation: observations){
				if(node.get_name().equals(observation)){
					System.out.println(node.get_name());
					node.set_observation_value("TRUE");
				}
			}

		}

		QBInference qbi = new QBInference(graph.get_bayes_net(), false);
		qbi.inference(nodes.get(nodes.size()-1).get_name());
		//double[] results = qbi.get_result().get_values();

		DiscreteVariable[] func = qbi.get_result().get_variables();
		int varIndex = -1;
		//	for(DiscreteVariable var: func){
		//String[] val = var.get_values();
		//			for(String str: val){
		//				varIndex++;
		//				System.out.println(varIndex + " " + str);
		//			}

		//}
		double[] values = qbi.get_result().get_values();

		///for (double dd: values){
		//System.out.println(dd);
		//}
		//qbi.get_result().print();

		//ProbabilityFunction pFunc = qbi.get_result();
		//pFunc.print();

		int countD = 0;
		int indexD = 0;
		//		
		//		for(Double dd:values){
		//			
		//			System.out.println(BigDecimal.valueOf(dd));
		//		}

		ArrayList<BigDecimal> eList = new ArrayList<BigDecimal>();
		//for(double d:pFunc.get_values
		double dMax = qbi.get_result().get_value(0);
		BigDecimal maxD = BigDecimal.valueOf(dMax);                      

		ArrayList<Integer> indexTopDiseases = new ArrayList<Integer>();
		for(double d:values){
			BigDecimal bd = BigDecimal.valueOf(d);
			if(bd.compareTo(maxD)>0){
				maxD=bd;

				System.out.println("New Max:  " +maxD);
				//add the index to the top diseases
				
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
		DiscreteVariable[] funcD = qbi.get_result().get_variables();

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
				
					topDiseases.add(f.get_value(index));
				}

			}
		}
		//topDisease = f.get_value(indexD);


		return topDiseases;

	}
}




//Code to insert data from .txt file into MySQL database, code that selects rows from database
//and generates strings to properly build the .arff file needed to import into JavaBayes 




//String fileName = "C:\\Users\\Tyler Young\\Desktop\\test3.txt";

// This will reference one line at a time
//String currentLine = null;


//
//		try {
//
//			Class.forName("com.mysql.jdbc.Driver");
//			Connection con = DriverManager.getConnection("jdbc:mysql://cs.elon.edu:3306/tyoung12", "tyoung12", "putpasswordhere");
//			Statement stmt = con.createStatement();

//while(rs.next()){

//int id = rs.getInt(1);
//System.out.println(id);
//}

//Scanner scanner = new Scanner(new FileInputStream(fileName));


//int lineCount = 0;
//while(scanner.hasNextLine()) {
//currentLine = scanner.nextLine();
//lineCount++;
//String[] tokenizedString = currentLine.split("\t");
//String disease = tokenizedString[0];
//String query = "INSERT INTO diseases(disease_name) VALUES (?)";
//String query = "INSERT INTO symptoms(symptom_name) VALUES (?)";
//String query = "INSERT INTO diseases_symptoms(disease_name, symptom_name, score) VALUES (?,?,?)";

//			
//				HashMap<String, InferenceGraphNode> map = new HashMap<String, InferenceGraphNode>();
//				//String query = "SELECT disease_name, symptom_name, score FROM diseases_symptoms WHERE score > 3.5";
//				String query = "SELECT disease_name,symptom_name, score FROM `diseases_symptoms` GROUP BY disease_name HAVING score > 3.5";
//				PreparedStatement statement = con.prepareStatement(query);
//				ResultSet rs = statement.executeQuery();



//			//build symptom list, disease list, and evidence list of symptoms/diseases


//			List<String> symptomList = new ArrayList<String>();
//			String query = "SELECT * FROM random_diseases_symptom WHERE symptom_name!=disease_name";
//			PreparedStatement statement = con.prepareStatement(query);
//			ResultSet rs = statement.executeQuery();
//
//			while(rs.next()){
//				if(symptomList.contains(rs.getString("symptom_name").replaceAll("'","").replaceAll("_","").replaceAll(",","").replaceAll("-","").replaceAll(" ",""))){
//					//System.out.println("Symptom already in list");
//				}
//				else{
//					symptomList.add(rs.getString("symptom_name").replaceAll("'","").replaceAll("_","").replaceAll(",","").replaceAll("-","").replaceAll(" ",""));
//				}
//			}
//
//			query = "SELECT * FROM random_diseases_symptom WHERE symptom_name!=disease_name";
//			statement = con.prepareStatement(query);
//			rs = statement.executeQuery();
//			//int entryCount = 0;
//
//			PrintWriter pw = null;
//			pw = new PrintWriter(new File("C:\\Users\\Tyler Young\\Desktop\\125.csv"));
//			StringBuilder builder = new StringBuilder();
//			while(rs.next()){
//				//entryCount++;
//				int count = 0;
//				String answerString = "";
//				String symptom = rs.getString("symptom_name").replaceAll("'","").replaceAll("_","").replaceAll(",","").replaceAll("-","").replaceAll(" ","");
//				String disease = rs.getString("disease_name").replaceAll("'","").replaceAll("_","").replaceAll(",","").replaceAll("-","").replaceAll(" ","");
//				//answerString = answerString + "    FOR SYMPTOM: " + symptom;
//				for(String listSymptom: symptomList){
//
//					if(symptom.equals(listSymptom)){
//						count++;
//						//answerString = answerString +"COUNT: " + count+ " "+"true,";
//						answerString = answerString +"true,";
//
//
//					}
//					else{
//						answerString = answerString +"false,";
//						count++;
//					}
//				}
//				answerString = answerString+"'"+disease+"'";
//				builder.append(answerString);
//				builder.append("\n");
//				//System.out.println(answerString);
//				//System.out.println(count);
//				//System.out.println(count);
//			}
//			pw.write(builder.toString());
//			pw.close();
//			//System.out.println(entryCount);
//			int symptomCount = 0;
//			for(String symptom:symptomList){
//				//symptom.replaceAll("'", "");
//				String str = "@ATTRIBUTE " + symptom + " {true,false}";
//				//System.out.println(str);
//				//symptomCount++;
//				//System.out.println(symptomCount + ":  " +symptom);
//
//			}
//
//			query = "SELECT DISTINCT(disease_name) FROM random_diseases_symptom WHERE symptom_name!=disease_name";
//			statement = con.prepareStatement(query);
//			rs = statement.executeQuery();
//			String diseasesString = "@ATTRIBUTE diseases {";
//			int diseaseCount =0;
//			while(rs.next()){
//				diseaseCount++;
//				String disease = "'" +rs.getString("disease_name").replaceAll("'","").replaceAll("_","").replaceAll(",","").replaceAll("-","").replaceAll(" ","") + "'";
//				//System.out.println(diseaseCount + ":  " +disease);
//				//diseasesString = diseasesString + disease+",";
//				//System.out.println(disease);
//			}
//			//System.out.println(diseasesString);
//		}
//
//		catch(Exception e){
//			System.out.println(e);
//		}
//	}

//System.out.println(diseasesString + "}");

//System.out.println(diseasesString+"}");

//				
//				
//				
//				
////			    query ="SELECT * FROM random_diseases_symptom";
////			    statement = con.prepareStatement(query);
////			    rs = statement.executeQuery();
////				int count =0;
////				while(rs.next()){
////					String disease = rs.getString("disease_name");
////					String symptom = rs.getString("symptom_name");
////				count = (int) rs.getDouble("score");
////			}
////				
////				
//				
//				
//				
//				
//				
//				//END
//				













//manually create nodes for network using MySQL database of symptoms/diseases
//				
//				while(rs.next()){
//					String disease = rs.getString("disease_name");
//					if(!map.containsKey(disease)){
//					InferenceGraphNode node = createNode(ig, disease,disease, "no "+disease);
//			
//					map.put(disease, node);
//					}
//					else{
//						map = map;
//					}
//					//line++;
//					//System.out.println(disease);
//					//System.out.println(line);
////					
//				}
//				
//				//rs.close();
//				//String queryTwo = "SELECT disease_name, symptom_name, score FROM diseases_symptoms WHERE score > 3.5";
//				String queryTwo = "SELECT disease_name,symptom_name, score FROM `diseases_symptoms` GROUP BY disease_name HAVING score > 3.5";
//				PreparedStatement statementTwo = con.prepareStatement(queryTwo);
//				rs = statementTwo.executeQuery();
//				while(rs.next()){
//					String symptom = rs.getString("symptom_name");
//					if(!map.containsKey(symptom)){
//					InferenceGraphNode node = createNode(ig, symptom,symptom, "no " +symptom);
//					map.put(symptom, node);
//					}
//					else{
//						map = map;
//					}
//					//System.out.println(symptom);
//					//System.out.println(symptom);
//					
//				}
//				


//				
//				//String queryThree = "SELECT disease_name,symptom_name, score FROM `diseases_symptoms` GROUP BY disease_name HAVING score > 3.5";
//				String queryThree = "SELECT disease_name,symptom_name FROM `diseases_symptoms` WHERE score > 5";
//				PreparedStatement statementThree = con.prepareStatement(queryThree);
//				ResultSet rs = statementThree.executeQuery();
//				rs = statementThree.executeQuery();
//				while(rs.next()){
//					
//					//String query = "INSERT INTO diseases_symptoms(disease_name, symptom_name, score) VALUES (?,?,?)";
//				//}
//				//String queryFour = "SELECT disease_name, symptom_name FROM diseases_symptoms WHERE symptom_name=" + "'" +rsThree.getString(1) + "'";
//				//PreparedStatement statementFour = con.prepareStatement(queryFour);
//				//ResultSet rsFour = statementFour.executeQuery();
//				//while(rsFour.next() && line<5000){
////					for(InferenceGraphNode node :  (Vector<InferenceGraphNode>)ig.get_nodes()){
////						for(InferenceGraphNode nodeTwo : (Vector<InferenceGraphNode>)ig.get_nodes()){
////							if(node.get_name().equals(rsFour.getString(2))){
////								if(nodeTwo.get_name().equals(rsFour.getString(1))){
////									ig.create_arc(node, nodeTwo);
////								}
////						}
////						
////						
////							
////							
////						}
////					}
//					line++;
//					//System.out.println("LINE: " + line + " SYMPTOM : "  + rs.getString(2) + " DISEASE: " + rs.getString(1));
//					//InferenceGraphNode symptomNode = map.get(rs.getString("symptom_name"));
//					//InferenceGraphNode diseaseNode = map.get(rs.getString("disease_name"));
//					//ig.create_arc(symptomNode, diseaseNode);
//				
//				//InferenceGraphNode
//					//ig.create_arc(map.get(rs.getString(2)), map.get(rs.getString(1)));
//					//ig.create_arc(map.get(rsFour.getString(2)), map.get(rsFour.getString(1)));
//					//disease, symptom
//					//line++;
//					//System.out.println("LINE: " + line);
//					//System.out.println(rsFour.getString(1) + " " + rsFour.getString(2));
//					
//				}
//				 freeMemory = Runtime.getRuntime().freeMemory()/MegaBytes;
//				 totalMemory = Runtime.getRuntime().totalMemory()/MegaBytes;
//				 maxMemory = Runtime.getRuntime().maxMemory()/MegaBytes;
//				 
//				 System.out.println("Used memory in JVM: " + (maxMemory-freeMemory));
//				 System.out.println("freeMemory in JVM: " + freeMemory);
//				 System.out.println("totalMemory in JVM shows current size of java heap: " + totalMemory);
//				 System.out.println("maxMemory in JVM : " + maxMemory);
//				//}


//statement.setString(1, "NULL");;
//				statement.setString(1, tokenizedString[1]);
//				statement.setString(2, tokenizedString[0]);
//				statement.setDouble(3, Double.parseDouble(tokenizedString[3]));
//				try{
//				statement.executeUpdate();
//				}
//				catch(Exception e){
//					System.out.println(e);
//				}
//String disease = tokenizedString[1];
//Double score = Double.parseDouble(tokenizedString[3]);
//}

//			scanner.close();
//			con.close();
//		}
////
//		catch(Exception e){
//		System.out.print(e);
//		}
//	}



// Setup to create HashMaps of symptoms w/ probability and one with symptom/disease
//		InferenceGraph ig = new InferenceGraph();
//		HashMap<String, HashMap<String, Double>> map = readFile();
//		//create a map of nodes with string key names and node values
//		HashMap<String, InferenceGraphNode> nodeMap = new HashMap<String, InferenceGraphNode>();
//		
//		//add disease nodes
//		for (Entry<String, HashMap<String, Double>> entry : map.entrySet()) {
//			//System.out.println(entry.getKey() + " " + "no "+entry.getKey());
//			InferenceGraphNode node = createNode(ig, entry.getKey(),entry.getKey(), "no "+entry.getKey());
//			nodeMap.put(entry.getKey(), node);
//		}
//		//add symptom nodes, remove whitespace and commas, shorten if longer than 28 characters
//		for (Entry<String, HashMap<String, Double>> symptomEntry : map.entrySet()) {
//			//get the map of symptoms for the disease
//			HashMap<String, Double> symptoms = symptomEntry.getValue();
//			//for each symptom of that disease
//			for(Entry<String, Double> symptom: symptoms.entrySet()){
//				if(nodeMap.containsKey(symptom.getKey()) == false){
//					InferenceGraphNode node = createNode(ig, symptom.getKey(), symptom.getKey(), "no " + symptom.getKey());
//					nodeMap.put(symptom.getKey(), node);
//				}
//				else{
//					nodeMap = nodeMap;
//				}
//			}
//		}
//
//		//
//		//		for(Entry<String, InferenceGraphNode> nodeEntry : nodeMap.entrySet()){
//		//			System.out.println(nodeEntry.getKey());
//		//			for(String value : nodeEntry.getValue().get_values()){
//		//				//System.out.println(nodeEntry.getKey());
//		//				System.out.print(value + " ");
//		//			}
//		//			System.out.println();
//		//		}
//
//		//loop through and add symptom to disease arc
//		//for(String str : map.keySet()){
//			
//			//map.get(str);
//			
//		//}
//		for (Entry<String, HashMap<String, Double>> entry : map.entrySet()){
//			//get the node for each disease
//			
//
//			//System.out.println("DISEASE : " + entry.getKey() + " ");
//
//			//InferenceGraphNode diseaseNode = nodeMap.get(entry.getKey());
//
//			//for(String value : diseaseNode.get_values()){
//			//System.out.print("DISEASE NODE VALUE : " + value);
//			//}
//
//			//Symptoms for the disease
//		
//			//for each symptom for the disease
//			for(String symptom: entry.getValue().keySet()){
//				//get the node with the name of the symptom
//				//System.out.print("SYMPTOM: " + symptom + " ");
//				//InferenceGraphNode symptomNode = nodeMap.get(symptom);
//
//				//for(String value : symptomNode.get_values()){
//				//System.out.print("SYMPTOM NODE VALUE : " + value);
//				//}
//
//				//add link(parent, child) where parent(symptom) usually causes child(disease) to happen
//				ig.create_arc(nodeMap.get(symptom), nodeMap.get(entry.getKey()));
//			
//			}
//			//System.out.println(" ");
//		}
//	}

//		for(Entry<String, InferenceGraphNode> entry: nodeMap.entrySet()){
//			InferenceGraphNode node = entry.getValue();
//			int children = node.get_children().size();
//			int parents = node.get_parents().size();
//			System.out.println("NODE NAME : " + entry.getKey() + " children size : " + children + " parent size: " + parents);
//			
//		}

