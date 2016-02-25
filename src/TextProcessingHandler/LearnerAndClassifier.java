package TextProcessingHandler;

import java.io.File;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import helper.databaseConnection;
import helper.processedText;
import helper.textCategory;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.rules.PART;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.Debug.Random;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class LearnerAndClassifier {
	//attribute
	DataSource source;
	Instances data;
	Boolean read;
	private Instances train;
	private Instances test;
	String [] options;
	FilteredClassifier fc;
	J48 tree;
	ArrayList<textCategory> category = new ArrayList<textCategory>() ;
	ArrayList<processedText> testData = new ArrayList<processedText>();
	databaseConnection dc= new databaseConnection();

	//function
	public void createTestSet(){
		// Declare string attributes
				 Attribute Attribute1 = new Attribute("Text",(FastVector)null);

				 //get class attribute
				 category = dc.getTextCategory();
				 
				 // Declare the class attribute along with its values
				 FastVector fvClassVal = new FastVector(category.size()+1);
				 for(textCategory name : category)
					 fvClassVal.addElement(name.getName());	 
				 

				 Attribute ClassAttribute = new Attribute("theClass", fvClassVal);
				 
				 // Declare the feature vector
				 FastVector fvWekaAttributes = new FastVector(2);
				 fvWekaAttributes.addElement(Attribute1);
				 fvWekaAttributes.addElement(ClassAttribute);
				 
				 // Create an empty test set
				 test = new Instances("Rel", fvWekaAttributes, 10);
				 // Set class index
				 test.setClassIndex(1); //class label is ClassAttribute
				 
				 
				 //
				 //ArrayList<classifiedText>td= dc.getTestData();
				 testData = dc.getProcessedData(0);
				 for(processedText data : testData){
					// Create the instance
					 Instance iExample = new Instance(2);
					 iExample.setDataset(test);
					 iExample.setValue((Attribute)fvWekaAttributes.elementAt(0), data.getMessage());
					 //iExample.classIsMissing();
					 //iExample.setValue((Attribute)fvWekaAttributes.elementAt(1), data.getClassification());
					// add the instance
					 test.add(iExample);
				 }
		
		
	}

	public void createTrainingSet(String date){
		//open database
		
		//get all
		
		 // Declare string attributes
		 Attribute Attribute1 = new Attribute("Text",(FastVector)null);

		 //get class attribute
		 category = dc.getTextCategory();
		 //System.out.println("category size : "+category.size());
		 // Declare the class attribute along with its values
		 FastVector fvClassVal = new FastVector(category.size());
		 
		 for(textCategory name : category){
			 fvClassVal.addElement(name.getName());	 
			 System.out.println(" name : "+name.getName());
		 }

		 Attribute ClassAttribute = new Attribute("theClass", fvClassVal);
		 
		 // Declare the feature vector
		 FastVector fvWekaAttributes = new FastVector(2);
		 fvWekaAttributes.addElement(Attribute1);
		 fvWekaAttributes.addElement(ClassAttribute);
		 
		 // Create an empty training set
		 train = new Instances("Rel", fvWekaAttributes, 10);
		 // Set class index
		 train.setClassIndex(1); //class label is ClassAttribute
		 
		 
		 //
		 ArrayList<processedText>td= dc.getTrainingData(date);
		 for(processedText data : td){
			// Create the instance
			 Instance iExample = new Instance(2);
			 iExample.setValue((Attribute)fvWekaAttributes.elementAt(0), data.getMessage());
			 iExample.setValue((Attribute)fvWekaAttributes.elementAt(1),getCategory(data.getClassificationId()) );
			// add the instance
			 train.add(iExample);
		 }
		
	}

	public String getCategory(int id){
		String ans="";
		for(textCategory name : category){
			if (name.getId() == id){
				ans = name.getName();
				break;
			}
		}
		return ans;
	}
	
	public int getCategory(String name){
		int i=-999;
		for(textCategory C : category){
			if (C.getName().equals(name)){
				i = C.getId();
				break;
			}
		}
		return i;
	}
		
	public Instances stringToWord(Instances str){
		Instances ans = null;
		try {
			stemmer nazief = new stemmer();
			StringToWordVector ST = new StringToWordVector();
			ST.setStemmer(nazief);
			ST.setMinTermFreq(10);
			ST.setLowerCaseTokens(true);
			File file = new File("stopword.txt");
			ST.setStopwords(file);
			//System.out.println("file exist : "+file.exists());
			ST.setInputFormat(str); //data instances that you are going to input to the filter 
			ans = Filter.useFilter(str, ST);
		//	System.out.println("class index : "+ans.attribute(0).toString());
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ans;
	}
		
	public void setOptions(String[] newOpt){
		options = newOpt;
		
	}
	
	public void BuildClassifier(){
		 options = new String[1];
		 options[0] = "-U";            // unpruned tree
		 tree = new J48();         // new instance of tree
		 try {
			tree.setOptions(options);
			tree.buildClassifier(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}     // set the options
		    // build classifier
	}
		
	public void makePrediction(Instances Data){
		// classifier
		test = stringToWord(test);
		//Collections.shuffle(test);
	
		train = stringToWord(train);
		 //J48 j48 = new J48();
		 //j48.setUnpruned(true);        // using an unpruned J48
		 // meta-classifier
		// fc = new FilteredClassifier();
		// fc.setFilter(rm);
		 J48 cs = new J48(); //tree
		 //NaiveBayes cs = new NaiveBayes();
		 //fc.setClassifier(nb);
		 // train and make predictions
		 try {
			cs.buildClassifier(train);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 for (int i = 0; i < test.numInstances(); i++) {
		   double pred = 0;
		try {
			pred = cs.classifyInstance(test.instance(i));
			//test.instance(i).setClassValue(pred);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
			testData.get(i).setClassificationId(getCategory(test.classAttribute().value((int) pred)));
			//testData.get(i).setConversationId(conversationId);(test.classAttribute().value((int) pred));
			
			updateClassifiedText(testData.get(i).getId(), getCategory(test.classAttribute().value((int) pred)));
		
		   System.out.print("ID: " + test.instance(i).value(0));
		   System.out.print(", actual: " + test.classAttribute().value((int) test.instance(i).classValue()));
		   System.out.println(", predicted: " + test.classAttribute().value((int) pred));
		   //System.out.println("test :  "+testData.get(i).getClassification());
		 
		 //update database
		 }
		
	System.out.println("test1.5");
	}
		
	public int getCategoryId(String Name){
		int i=0;
		for(textCategory tC: category){
			if (tC.getName().matches(Name))
				break;
			i++;
		}
		return i;
	}
	
	//melakukan update pada classifiedText
	void updateClassifiedText(Integer id,Integer textCategoryId){
		java.util.Date date = new java.util.Date();
		String sqli = "UPDATE `procesed_texts` SET `text_category_id`='"+textCategoryId+"',"
				+ "`updated_at`='"+new Timestamp(date.getTime())+"' WHERE `id`='"+id+"'";
		//System.out.println("Querry : "+sqli);
		dc.executeUpdateQuery(sqli);
				
	}

	public void readDataSource(String Path){
		try {
			source = new DataSource(Path);
			//Instance instance = new Instance();
			 data = source.getDataSet();
			// System.out.println(data.firstInstance().toString());
			 // setting class attribute if the data format does not provide this information
			 // For example, the XRFF format saves the class attribute information as well
			 if (data.classIndex() == -1)
			   data.setClassIndex(data.numAttributes() - 1);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 System.out.println(data.firstInstance().toString());
	}
	
	public Instances getTrain() {
		return train;
	}

	public void setTrain(Instances train) {
		this.train = train;
	}

	public Instances getTest() {
		return test;
	}

	public void setTest(Instances test) {
		this.test = test;
	}

	public Instances filter(Instances data,String[] options){
		Instances newData = data;
		try {
			//String[] options = new String[2];
			 //options[0] = "-R";                                    // "range"
			 //options[1] = "1";                                     // first attribute
			 Remove remove = new Remove();                         // new instance of filter
			 remove.setOptions(options);                           // set options
			 remove.setInputFormat(data);                          // inform filter about dataset **AFTER** setting options
			 newData = Filter.useFilter(data, remove);   // apply filter
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   // apply filter
		return newData;
	}

	public static void main(String[] args) {
	// TODO Auto-generated method stub
/*		LearnerAndClassifier test = new LearnerAndClassifier();
		//test.readDataSource("trainingdata/pertanyaan.arff");
		System.out.println("test1");
		Date date = new Date(2015, 12, 7);
		test.createTrainingSet(date.toString());
		test.createTestSet();
		test.makePrediction(test.getTrain());
		System.out.println("test2");*/
		//BufferedReader datafile = readDataFile("weather.txt");
		 
		
		LearnerAndClassifier test = new LearnerAndClassifier();
		Date date = new Date(2016, 2, 28);
		test.createTrainingSet();
		int seed = 273;
		Random rand = new Random(seed);
		Instances data =test.stringToWord(test.train); 
		data.randomize(rand);
		int kmean = 10;
		data.stratify(kmean);
		//data.setClassIndex(data.numAttributes() - 1);
 
		// Do 10-split cross validation
		Instances[][] split = crossValidationSplit(data, kmean);
 
		// Separate split into training and testing arrays
		Instances[] trainingSplits = split[0];
		Instances[] testingSplits = split[1];
 
		// Use a set of classifiers
		Classifier[] models = { 
				new J48(), // a decision tree
				new PART(), 
				new DecisionTable(),//decision table majority classifier
				new DecisionStump(), //one-level decision tree
				new ZeroR(),
				new NaiveBayes(),
				
				
		};
 
		// Run for each model
		for (int j = 0; j < models.length; j++) {
 
			// Collect every group of predictions for current model in a FastVector
			FastVector predictions = new FastVector();
 
			// For each training-testing split pair, train and test the classifier
			for (int i = 0; i < trainingSplits.length; i++) {
				Evaluation validation;
			
					try {
						validation = classify(models[j], trainingSplits[i], testingSplits[i]);
						predictions.appendElements(validation.predictions());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
	
				// Uncomment to see the summary for each training-testing pair.
				//System.out.println(models[j].toString());
			}
 
			// Calculate overall accuracy of current classifier on all splits
			double accuracy = calculateAccuracy(predictions);
 
			// Print current classifier's name and accuracy in a complicated,
			// but nice-looking way.
			System.out.println("Accuracy of " + models[j].getClass().getSimpleName() + ": "
					+ String.format("%.2f%%", accuracy)
					+ "\n---------------------------------");
		}
 
	}

	public void createTrainingSet() {
		//open database
		
		//get all
		
		 // Declare string attributes
		 Attribute Attribute1 = new Attribute("Text",(FastVector)null);

		 //get class attribute
		 category = dc.getTextCategory();
		 //System.out.println("category size : "+category.size());
		 // Declare the class attribute along with its values
		 FastVector fvClassVal = new FastVector(category.size());
		 for(textCategory name : category)
			 fvClassVal.addElement(name.getName());	 
		 

		 Attribute ClassAttribute = new Attribute("theClass", fvClassVal);
		 
		 // Declare the feature vector
		 FastVector fvWekaAttributes = new FastVector(2);
		 fvWekaAttributes.addElement(Attribute1);
		 fvWekaAttributes.addElement(ClassAttribute);
		 
		 // Create an empty training set
		 train = new Instances("Rel", fvWekaAttributes, 10);
		 // Set class index
		 train.setClassIndex(1); //class label is ClassAttribute
		 
		 
		 //
		 ArrayList<processedText>td= dc.getTrainingData();
		 for(processedText data : td){
			// Create the instance
			 Instance iExample = new Instance(2);
			 iExample.setValue((Attribute)fvWekaAttributes.elementAt(0), data.getMessage());
			 iExample.setValue((Attribute)fvWekaAttributes.elementAt(1),getCategory(data.getClassificationId()) );
			// add the instance
			 
			 train.add(iExample);
		 }
		
	}
	
	public static Evaluation classify(Classifier model,
			Instances trainingSet, Instances testingSet) throws Exception {
		Evaluation evaluation = new Evaluation(trainingSet);
		//System.out.println("test 1 "+trainingSet.firstInstance().toString());
		trainingSet.firstInstance().toString();
		model.buildClassifier(trainingSet);
	//	model.toString()
		//System.out.println("test 2 "+model.toString());
		evaluation.evaluateModel(model, testingSet);
		
		//evaluation.toSummaryString()
		System.out.println("Confusion Matrix :");
		double[][] asw=evaluation.confusionMatrix();
		int i=64;
		for(int j=65;j<73;j++)
			System.out.print((char)j+" ");
		System.out.println();
		for(double[]aa :asw){
			i++;
			for(double a :aa){
				System.out.print((int)a+ " ");
			}
			System.out.print((char)i+" <=");
			System.out.println();
			
		}
		System.out.println();
	
		//System.out.println("test 3 "+evaluation.predictions().firstElement().toString());
		
		
		return evaluation;
	}
 
	public static double calculateAccuracy(FastVector predictions) {
		double correct = 0;
 
		for (int i = 0; i < predictions.size(); i++) {
			NominalPrediction np = (NominalPrediction) predictions.elementAt(i);
			if (np.predicted() == np.actual()) {
				correct++;
			}
		}
 
		return 100 * correct / predictions.size();
	}
 
	public static Instances[][] crossValidationSplit(Instances data, int numberOfFolds) {
		Instances[][] split = new Instances[2][numberOfFolds];
 
		for (int i = 0; i < numberOfFolds; i++) {
			split[0][i] = data.trainCV(numberOfFolds, i);
			split[1][i] = data.testCV(numberOfFolds, i);
		}
 
		return split;
	}
 
	
	
}

