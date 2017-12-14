package joeschweitzer;



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

public class BayesianNetworkGenerator {

	public static void main(String[] args) {
		try {
			//load dataset
			DataSource source = new DataSource("125.arff");
			Instances dataset = source.getDataSet();	
			
			//set class index to the last attribute
			dataset.setClassIndex(dataset.numAttributes()-1);
		    BayesNet nb = new BayesNet();
		   
		    SearchAlgorithm searchAlgorithm=new K2();
		   
		    nb.setSearchAlgorithm(searchAlgorithm);
		    System.out.println("building classifier");
		    nb.buildClassifier(dataset);
		    System.out.println("Classifier built");

			int seed = 1;
			int folds = 5;
			//randData.randomize(rand);
			Evaluation eval = new Evaluation(dataset);
			eval.crossValidateModel(nb, dataset, 5, new Random(1));
			System.out.println(eval.toSummaryString());

			
		    FileWriter outfile = new FileWriter("biftest.xml");
		    outfile.write(nb.toXMLBIF03());
		    outfile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	

}
