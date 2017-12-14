Author: Tyler Young

**Bayesian network for symptoms and diseases**

This program takes data from a PubMed database dataset of symptoms and diseases
from https://www.nature.com/articles/ncomms5212

The dataset used is taken from: https://images.nature.com/original/nature-assets/ncomms/2014/140626/ncomms5212/extref/ncomms5212-s4.txt

This is the data.txt file in the project.


The dataset lists all the relationships of symptoms/diseases found in published articles in the 
PubMed database, with a TDIF score of how frequent the symptom was associated with the disease.
There are over 140,000 symptom/disease relationships.


I created a table in MySQL of the first 44,000 entries and used these as my dataset for generating the 
bayesian network.  The database has three columns: symptom_name, disease_name, and score

This program used the Weka API and JavaBayes API 
WEKA: http://weka.sourceforge.net/doc.dev/overview-summary.html
JavaBayes: http://www.cs.cmu.edu/~javabayes/
http://www.cs.cmu.edu/~javabayes/EBayes/index.html/JavaDoc/index.html

The JavaBayes source has not been updated for a long time and is made in Java 1, but I found a project that 
allowed JavaBayes to compile with higher versions of Java : https://dataworks-inc.com/simple-bayesian-network-inference-using-netica-and-javabayes/
I used https://github.com/joeschweitzer/javabayes as my project and used my own code so it would compile



How it works:

dataset.txt --> MySQL database --> formatted text file using CsvConverter.java----> save generated text file with .arff extension --> use .arff file as input to BayesianNetworkGenerator.java --> used generated .xml file as input to UserGUI.java --> run UserGUI2.java

The CSVConverter.java creates .txt file from the MySQL database of symptoms and diseases that 
then has to manually be saved by the user as an .arff file so it can be loaded into the WEKA API to 
generate the bayesian network.  Info on .arff files here: https://www.cs.waikato.ac.nz/ml/weka/arff.html
Make sure last line of .arff file is complete, the writer sometimes cuts off at the end. If it isn't delete that line
Also make sure the NAME tag in the .arff file doesn't contain any characters other than letters or it won't work

The .arff file that is saved should be used as the input DataSource file in BayesianNetworGenerator.java.
This class generates a BIF XML of the network which can then be imported into the JavaBayes API.  This
BIF XML should be set to the InferenceGraph file input at the beginning of UserGUI.java.  
Make sure the <NAME> tag of the BIF XML file only contains letters, if it doesn't change the name or it won't work


I had to use two APIs since Weka does not have functionality for inference, but is great for generating
the bayesian networks and learning the probability distributions.  JavaBayes is great for 
running inference using posterior probabilities of events given evidence, but is not good for generating
the networks since you have to build everything manually.  

The main class to run the entire program is in UserGUI2.java, so run this class to run the program.


For example of what BIF XML should look like, open formatted.xml with a text editor
For example of what .arff file should look like, open 125.arff with text editor
Use networkBIF.xml as input in UserGUI to run the program with my generated file
  
