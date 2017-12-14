package tyleryoung;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;



/**
 * @author Tyler Young
 *
 *
 *Generates a .txt file in .arff format needed to create the bayesian network in Weka
 *Refresh the project after class is ran 
 *Save the generated .txt file as a new file with .arff file extension
 *
 *input this new .arff file in BayesianNetworkGenerator.java to generate a BIF XML file 
 *that represents the bayesian network and can be used as the input file for the entire program by
 *setting it as the graph's file in UserGUI.java
 */
public class CsvConverter {
	public static BufferedWriter bw;


	public static void main(String[] args) {

		try {	
			//enter name of the generated .txt file here
			File fout = new File("newOutput.txt");
			FileOutputStream fos = new FileOutputStream(fout);
			bw = new BufferedWriter(new OutputStreamWriter(fos));

			log("@RELATION symptomsDiseases");
			bw.newLine();

			//connects to the MySQL database of symptoms, diseases, and score for significance of symptom to the disease
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://cs.elon.edu:3306/tyoung12", "tyoung12", "changeme");
			Statement stmt = con.createStatement();

			//add each unique symptom name to a list
			List<String> symptomList = new ArrayList<String>();
			String query = "SELECT * FROM diseases_symptoms WHERE symptom_name!=disease_name";
			PreparedStatement statement = con.prepareStatement(query);
			ResultSet rs = statement.executeQuery();

			//remove characters in symptom names that are not allowed in Weka
			while(rs.next()){
				if(symptomList.contains(rs.getString("symptom_name").replaceAll("'","").replaceAll("_","").replaceAll(",","").replaceAll("-","").replaceAll(" ",""))){
					//System.out.println("Symptom already in list");
				}
				else{
					symptomList.add(rs.getString("symptom_name").replaceAll("'","").replaceAll("_","").replaceAll(",","").replaceAll("-","").replaceAll(" ",""));
				}
			}

			//System.out.println(symptomList.get(98));


			//Create the attribute line in .arff file for each symptom with value options of true and false
			//The attribute lines in .arff file tell Weka to create a node object for that string

			for(String currentSymptom:symptomList){
				String str = "@ATTRIBUTE " + currentSymptom + " {TRUE,FALSE}";
				log(str);
				//System.out.println(str);
			}

			//create a list for each unique disease in the database
			List<String> diseaseList = new ArrayList<String>();
			query = "SELECT DISTINCT(disease_name) FROM diseases_symptoms WHERE symptom_name!=disease_name";
			statement = con.prepareStatement(query);
			rs = statement.executeQuery();

			while(rs.next()){
				if(diseaseList.contains(rs.getString("disease_name").replaceAll("'","").replaceAll("_","").replaceAll(",","").replaceAll("-","").replaceAll(" ",""))){
					//System.out.println("Symptom already in list");
				}
				else{
					diseaseList.add(rs.getString("disease_name").replaceAll("'","").replaceAll("_","").replaceAll(",","").replaceAll("-","").replaceAll(" ",""));
				}

			}

			//create a disease attribute in .arff file with values of each distinct disease
			String diseaseString = "@ATTRIBUTE diseases {";
			diseaseLog(diseaseString);
			int count = 0;
			for(String currentDisease:diseaseList){
				if(count ==0){
					diseaseLog("'" + currentDisease + "'" +  ",");
					count++;
				}
				else if(count==1){
					diseaseLog("'" + currentDisease + "'");
					count++;
				}
				else{
					diseaseLog("," + "'" + currentDisease + "'");
					count++;
				}

			}
			diseaseLog("}");
			bw.newLine();


			//build the strings for the .arff dataset needed to build the network in Weka
			//
			//
			//
			//the string will add a true/false value for each symptom in the database using the symptom list of distinct symptoms
			//created above.  For each row in the MySQL database, loop through the symptom list and check if 
			//the symptom name in database and list are equal. If they are, add "true" and if they aren't, add "false" to string
			//add the disease string and score for that row in the database to the end of string, which indicate what disease output the string
			//of true/false values correlates to and the weight of the symptom/disease relationship
			//
			//
			//The order of the @ATTRIBUTE symptoms generated above correspond to each true/false on each line created here, so if
			//the first true/false value on one the lines is "true", then the first attribute listed in the .arff file will be set to "true"
			//
			//For example, using the file 125.arff, you can see that the first true/false value is set to "TRUE", which
			//means the first @ATTRIBUTE declared, which is Obesity, is set to true.  The disease this corresponds to is
			//'CognitionDisorders'.  Overall, this line is saying that there is symptom/diseases relationship in the database 
			//with a symptom named Obesity and disease named CognitionDisorders


			query = "SELECT * FROM diseases_symptoms WHERE symptom_name!=disease_name";
			statement = con.prepareStatement(query);
			rs = statement.executeQuery();

			log("@DATA");
			String totalString = "";
			while(rs.next()){
				String answerString = "";
				String symptom = rs.getString("symptom_name").replaceAll("'","").replaceAll("_","").replaceAll(",","").replaceAll("-","").replaceAll(" ","");
				String disease = rs.getString("disease_name").replaceAll("'","").replaceAll("_","").replaceAll(",","").replaceAll("-","").replaceAll(" ","");
				String score = "{" + rs.getInt("score")+"}";	
				int trueIndex = 0;
				int printCount = 0;
				for(String currentSymptom: symptomList){
					if(symptom.equals(currentSymptom)){
						answerString = answerString + "TRUE,";
						trueIndex = printCount;
					}
					else{
						answerString = answerString + "FALSE,";
						printCount++;

					}

				}
				answerString = answerString + "'" + disease + "'" + "," + score;

				log(answerString);
				//System.out.println(answerString);
			}

		}

		catch(Exception e){
			System.out.println(e);
		}

	}



	/**
	 * @param message  
	 * The input string to print to the .txt file, followed by a new line
	 * 
	 */
	public static void log(String message) { 
		try {
			bw.write(message);
			bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param message  The input string to print to the .txt file on the same line
	 */
	public static void diseaseLog(String message){
		try {
			bw.write(message);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}



}
