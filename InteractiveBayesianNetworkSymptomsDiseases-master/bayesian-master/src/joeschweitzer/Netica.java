package joeschweitzer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import com.sun.javafx.collections.MappingChange.Map;

import norsys.netica.Environ;
import norsys.netica.Net;
import norsys.netica.NeticaException;
import norsys.neticaEx.aliases.Node;

/**
 * Driver class to test out Netica bayesian network
 */
public class Netica {

	public static void main(String[] args) throws NeticaException {


		// Setup
		Node.setConstructorClass("norsys.neticaEx.aliases.Node");
		new Environ(null);
		Net net = new Net();
		net.setName("Demo");

		HashMap<String, HashMap<String, Double>> map = readFile();
		//create a map of nodes with string key names and node values
		HashMap<String, Node> nodeMap = new HashMap<String, Node>();
		//add disease nodes
		for (Entry<String, HashMap<String, Double>> entry : map.entrySet()) {
			//add nodes for each disease, remove whitespace, remove commas,remove -,  shorten if longer than 28 characters
			nodeMap.put(entry.getKey().replaceAll("\\s+", "").replaceAll(",", "").replaceAll("-", "").substring(0, Math.min(entry.getKey().replaceAll("\\s+", "").replaceAll(",", "").replaceAll("-", "").length(), 28)),
					new Node(entry.getKey().replaceAll("\\s+", "").replaceAll(",", "").replaceAll("-", "").substring(0, Math.min(entry.getKey().replaceAll("\\s+",  "").replaceAll(",", "").replaceAll("-", "").length(), 28)), 
							entry.getKey().replaceAll("\\s+", "").replaceAll(",", "").replaceAll("-", "").substring(0, Math.min(entry.getKey().replaceAll("\\s+",  "").replaceAll(",", "").replaceAll("-", "").length(), 28))
							+ ", no" + entry.getKey().replaceAll("\\s+", "").replaceAll(",", "").replaceAll("-", "").substring(0, Math.min(entry.getKey().replaceAll("\\s+",  "").replaceAll(",", "").replaceAll("-", "").length(), 28)), net));

		}
		//add symptom nodes, remove whitespace and commas, shorten if longer than 28 characters
		for (Entry<String, HashMap<String, Double>> symptomEntry : map.entrySet()) {
			//get the map of symptoms for the disease
			HashMap<String, Double> symptoms = symptomEntry.getValue();
			//for each symptom of that disease
			for(Entry<String, Double> symptom: symptoms.entrySet()){
				if(nodeMap.containsKey(symptom.getKey().replaceAll("\\s+", "").replaceAll(",", "").substring(0, Math.min(symptom.getKey().replaceAll("\\s+", "").replaceAll(",", "").length(), 28))) == false){
					nodeMap.put(symptom.getKey().replaceAll("\\s+", "").replaceAll(",", "").substring(0, Math.min(symptom.getKey().replaceAll("\\s+", "").replaceAll(",", "").length(), 28)),
							new Node(symptom.getKey().replaceAll("\\s+", "").replaceAll(",", "").substring(0, Math.min(symptom.getKey().replaceAll("\\s+",  "").replaceAll(",",  "").length(), 28)), 
									symptom.getKey().replaceAll("\\s+", "").replaceAll(",", "").substring(0, Math.min(symptom.getKey().replaceAll("\\s+",  "").replaceAll(",", "").length(), 28))
									+ ", no" + symptom.getKey().replaceAll("\\s+", "").replaceAll(",", "").substring(0, Math.min(symptom.getKey().replaceAll("\\s+",  "").replaceAll(",", "").length(), 28)), net));
				}
				else{
					nodeMap = nodeMap;
				}
			}
			
		}

		for(Entry<String, Node> nodeEntry : nodeMap.entrySet()){
			System.out.println(nodeEntry.getKey());
			System.out.println(nodeEntry.getValue().getStateNames());
		}


		Node cancer = new Node("cancer","cancer, noCancer", net);
		Node hernia = new Node("hernia", "hernia, noHernia", net);
		Node smoking = new Node("smoking","smoking, noSmoking", net);
		Node headache = new Node("headache","headache, noHeadache", net);
		//
		//		// Setup nodes with states
		//		Node hearBark = new Node("hearBark", "hearBark, quiet", net);
		//		Node dogOut = new Node("dogOut", "dogOut, dogIn", net);
		//		Node bowelProblem = new Node("bowelProblem", "bowelProblem, noBowelProblem", net);
		//		Node familyOut = new Node("familyOut", "familyOut, familyIn", net);
		//		Node lightOn = new Node("lightOn", "lightOn, lightOff", net);
		//
		//		// Add links child.addLink(parent) where parent usually causes child to happen
		//		lightOn.addLink(familyOut);
		//		dogOut.addLink(familyOut);
		//		dogOut.addLink(bowelProblem);
		//		hearBark.addLink(dogOut);


		//for each disease, add link for every symptom with probabilities

		cancer.addLink(smoking);
		cancer.addLink(headache);


		cancer.setCPTable("smoking","headache", 0.4, 0.6);
		cancer.setCPTable("smoking", "noHeadache", 0.3, 0.7);
		cancer.setCPTable("noSmoking", "headache", 0.2, 0.8);
		//cancer.setCPTable("noSmoking", "noHeadache", 0.1, 0.9);


		// Setup conditional probabilities
		//		hearBark.setCPTable("dogOut", .70, .30);
		//		hearBark.setCPTable("dogIn", .01, .99);
		//
		//		dogOut.setCPTable("familyOut", "bowelProblem", .99, .01);
		//		dogOut.setCPTable("familyOut", "noBowelProblem", .90, .10);
		//		dogOut.setCPTable("familyIn", "bowelProblem", .97, .03);
		//		dogOut.setCPTable("familyIn", "noBowelProblem", .30, .70);
		//
		//		lightOn.setCPTable("familyOut", .60, .40);
		//		lightOn.setCPTable("familyIn", .05, .95);
		//
		//		// Setup 'leaf' probabilities
		//		familyOut.setCPTable(.15, .85);
		//		bowelProblem.setCPTable(.01, .99);

		net.compile();



		// Figure out probability of light being on with no evidence given (just based off of probabilities)
		//double belief = lightOn.getBelief("lightOn");
		//System.out.println("The probability of the light being on is " + belief);

		// Enter evidence, certain things that we observe
		//hearBark.finding().enterState("hearBark");
		//bowelProblem.finding().enterState("noBowelProblem");

		// Recalculate probability of light being on given evidence
		//belief = lightOn.getBelief("lightOn");
		//System.out.println("The probability of the light being on given a bark was heard "
		//+ "and no bowel problem is " + belief);

		headache.finding().enterState("headache");
		//smoking.finding().enterState("smoking");

		double belief = cancer.getBelief("cancer");
		double belief2 = cancer.getBelief("noCancer");
		//System.out.println("the probability of cancer given headache is " + belief+ "and no cancer is " + belief2);
	}

	public static HashMap<String, HashMap<String, Double>> readFile(){
		// The name of the file to open.
		String fileName = "C:\\Users\\Tyler Young\\Desktop\\HEALTHDATA.txt";

		// This will reference one line at a time
		String currentLine = null;
		HashMap<String, HashMap<String, Double>> symptomProbabilityMap = new HashMap<String, HashMap<String, Double>>();
		try {

			Scanner scanner = new Scanner(new FileInputStream(fileName));
			List<String> firstList = new ArrayList<String>();
			List<String> secondList = new ArrayList<String>();

			HashMap<String, HashMap<String, Double>> map = new HashMap<String, HashMap<String, Double>>();

			int lineCount = 0;
			while(scanner.hasNextLine()) {
				currentLine = scanner.nextLine();
				lineCount++;
				String[] tokenizedString = currentLine.split("\t");
				String symptom = tokenizedString[0];
				String disease = tokenizedString[1];
				Double score = Double.parseDouble(tokenizedString[3]);

				//if the disease isn't in the map, add it with it's symptom
				if (map.containsKey(disease) == false){
					HashMap<String, Double> symptomList = new HashMap<String, Double>();
					//symptom with tdif score for that disease
					symptomList.put(symptom, score);
					//put disease with symptom/score value
					map.put(disease,symptomList);
				}
				//if the disease is already in the map, add the next symptom to the disease's hashmap's hashmap value
				else{
					//get the map value for the disease
					HashMap<String, Double> diseaseMap = map.get(disease);
					//put the symptom with the score for that disease
					diseaseMap.put(symptom, score);

					//update the disease/symptom map with new symptom map 
					map.put(disease, diseaseMap);
				}

			}


			//CALL HERE


			//convertTdif(tokenizedString[3]);
			//System.out.println("Symptom Name: " + tokenizedString[0] + " Disease Name: " + 
			//tokenizedString[1] + " PubMed Occurence: " + tokenizedString[2] + " TDIF Score " + score);

			System.out.println(lineCount);
			scanner.close();

			symptomProbabilityMap = getSymptomProbabilities(map);
			return symptomProbabilityMap;
			//checkMapValues(symptomProbabilityMap);
			//			for (Entry<String, HashMap<String, Double>> entry : map.entrySet()) {
			//				Double totalScore = 0.0000000;
			//				String key = entry.getKey();
			//				System.out.println(key);
			//				HashMap<String, Double> symptomMap = entry.getValue();
			//				for(Entry <String, Double> symptomEntry : symptomMap.entrySet()){
			//					String symptomKey = symptomEntry.getKey();
			//					Double symptomScore = symptomEntry.getValue();
			//					totalScore = totalScore + symptomScore;
			//					//System.out.println("Disease Name : " + key);
			//					System.out.println(" Symptom Name : " + symptomKey + " TDIF Score : " + symptomScore);
			//				}
			//				//CALL getsymptomprobs here
			//				
			//				System.out.println("DISEASE : " + key + " TOTAL TDIF SCORE = " + totalScore);
			//				//System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			//			}

		}
		catch(FileNotFoundException ex) {
			System.out.println(
					"Unable to open file '" + 
							fileName + "'");                
		}
		return symptomProbabilityMap;
	}

	public static HashMap<String, HashMap<String, Double>> getSymptomProbabilities(HashMap<String, HashMap<String, Double>> map){
		//symptom probability map for each disease
		HashMap<String, HashMap<String, Double>> symptomProbabilityMap = new HashMap<String, HashMap<String, Double>>();

		for (Entry<String, HashMap<String, Double>> entry : map.entrySet()) {
			//total score for the disease
			Double totalScore = 0.0000000;
			String disease = entry.getKey();
			//System.out.println(disease);
			//get the disease's hashmap of symptoms and tdif scores
			HashMap<String, Double> symptomMap = entry.getValue();
			//for each symptom, add up the total score 
			for(Entry <String, Double> symptomEntry : symptomMap.entrySet()){
				//String symptomKey = symptomEntry.getKey();
				Double symptomScore = symptomEntry.getValue();
				totalScore = totalScore + symptomScore; 
				//System.out.println("Disease Name : " + key);
				//System.out.println(" Symptom Name : " + symptomKey + " TDIF Score : " + symptomScore);
			}

			for(Entry <String, Double> symptomEntryWithScore: symptomMap.entrySet()){
				String symptomKey = symptomEntryWithScore.getKey();
				Double symptomScore = symptomEntryWithScore.getValue();
				Double probabilityYes = symptomScore/totalScore;
				Double probabilityNo = 1-probabilityYes;

				if(symptomProbabilityMap.containsKey(disease) == false){
					//list of symptoms/probabilities for the disease
					HashMap<String, Double> symptomProbabilityListMap = new HashMap<String, Double>();
					//add the symptom with its probability to the symptom list for the disease
					symptomProbabilityListMap.put(symptomKey, probabilityYes);
					//add the symptom list to the map of diseases
					symptomProbabilityMap.put(disease, symptomProbabilityListMap);
				}
				else{
					//get the map of probabilities for the disease and add the symptom with its probability
					HashMap<String, Double> diseaseMap = symptomProbabilityMap.get(disease); 
					diseaseMap.put(symptomKey, probabilityYes);
					symptomProbabilityMap.put(disease, diseaseMap);
				}



				//System.out.println(" Symptom Name : " + symptomKey + " TDIF Score : " + symptomScore + " Total TDIF SCORE: " + totalScore + " Weighted Probability: " + probabilityYes +
				//"PROBABILITY NO: " + probabilityNo);
			}
			//CALL getsymptomprobs here

			//System.out.println("DISEASE : " + disease + " TOTAL TDIF SCORE = " + totalScore);
			//System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
		}
		return symptomProbabilityMap;
	}


	public static void checkMapValues(HashMap<String, HashMap<String, Double>> map){
		//HashMap<String, Node> nodeMap = new HashMap<String, Node>();


		
		//System.out.println(map.size());
		for (Entry<String, HashMap<String, Double>> entry : map.entrySet()) {
			//nodeMap.put(entry.getKey(), new Node(entry.getKey(), entry.getKey()+", no" + entry.getKey(), ))
			String disease = entry.getKey();
			//System.out.println(disease);

			HashMap<String, Double> symptomMap = entry.getValue();
			for(Entry <String, Double> symptomEntry : symptomMap.entrySet()){
				String symptomKey = symptomEntry.getKey();
				Double probabilityYes = symptomEntry.getValue();
				Double probabilityNo = 1-probabilityYes;

				//System.out.println("Disease Name : " + key);
				//System.out.println(" Symptom Name : " + symptomKey + " PROBABILITY : " + probabilityYes + " PROBABILITY NO " + probabilityNo);
			}
			//CALL getsymptomprobs here

		}

	}




	//method to check if TDIF score value in .txt file can be converted to int or double for calculations
	public static void convertTdif(String str){
		int i = 0;
		double d = 0.0000000;

		try {
			//try to parse as a double
			d = Double.parseDouble(str);

		}catch(NumberFormatException e){
			try {
				//if not in double format, try to parse as integer
				i = Integer.parseInt(str);

			}catch(NumberFormatException z){
				// Error indicates string can not be integer or double
				System.out.println(str + " is not in correct integer/double format");
			}
		}

	}


}
