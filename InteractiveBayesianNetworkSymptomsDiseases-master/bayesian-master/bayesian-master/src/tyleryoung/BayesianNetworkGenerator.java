package tyleryoung;



import java.io.FileWriter;
import java.util.Random;

import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.net.BIFReader;
import weka.classifiers.bayes.net.search.SearchAlgorithm;
import weka.classifiers.bayes.net.search.global.K2;
import weka.classifiers.bayes.net.search.global.SimulatedAnnealing;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * @author Tyler Young
 *
 *
 *Generates a BIF XML file using the WEKA api (weka.jar) from an .arff file that can
 *be used as the main input file for the entire program in UserGUI.java
 *
 *link for information on how JavaBayes uses the BIF XML file: http://www.cs.cmu.edu/~javabayes/Home/node7.html
 *
 *make sure that after BIF XML is generated, the last line in file is not incomplete and 
 *the text inside <NAME> </NAME> does not contain any special characters. For example. in biftest.xml,
 *there are special characters in the name and there will be an error if you do not change this.  I was not able to get 
 *the program to generate the names without these characters.
 *
 *Uses cross validation to score how well the network classifies new instances
 *If the folds are set to 5, the first 4/5 of the dataset will be used as the training set,
 *and the last 1/4 will be used as the test set.  You can set the folds to any number you want
 *
 *
 *WEKA API: http://weka.sourceforge.net/doc.dev/overview-summary.html
 */
public class BayesianNetworkGenerator {

	public static void main(String[] args) {
		try {
			//choose the arff file to load and generate a BIF XML for
			DataSource source = new DataSource("125.arff");
			Instances dataset = source.getDataSet();	

			//set class index to the last attribute(disease attribute)
			dataset.setClassIndex(dataset.numAttributes()-1);

			//Bayes Network learning using various search algorithms and quality measures.
			//Base class for a Bayes Network classifier. Provides datastructures (network structure, conditional probability distributions, etc.) 
			//and facilities common to Bayes Network learning algorithms like K2 and B.
			BayesNet nb = new BayesNet();

			//This Bayes Network learning algorithm uses a hill climbing algorithm restricted by an order on the variables.
			SearchAlgorithm searchAlgorithm=new K2();

			nb.setSearchAlgorithm(searchAlgorithm);
			System.out.println("building classifier");

			//generates the bayesnet classifier using the dataset as instances
			nb.buildClassifier(dataset);
			System.out.println("Classifier built");

			//performes evaluation on the classifier using cross-validation, the third argument is the 
			//number of folds and the fourth argument tells the evaluation to randomize the data ordering
			Evaluation eval = new Evaluation(dataset);
			eval.crossValidateModel(nb, dataset, 5, new Random(1));
			System.out.println(eval.toSummaryString());

			//choose the file to output the generated bayesian network to
			FileWriter outfile = new FileWriter("biftest.xml");
			outfile.write(nb.toXMLBIF03());
			outfile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}



}
