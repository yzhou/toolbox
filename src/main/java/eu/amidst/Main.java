/*
package eu.amidst;


import eu.amidst.core.database.DataStream;
import eu.amidst.core.database.statics.readers.DataStreamReaderFromFile;
import eu.amidst.staticmodelling.models.NaiveBayesClassifier;
import eu.amidst.staticmodelling.models.NaiveBayesClusteringModel;


public class Main {

    public static void learningNaiveBayesClusteringModel(){

        DataStreamReaderFromFile reader = new DataStreamReaderFromFile("./data/data.arff");

        DataStream dataStream = reader.getDataStream();

        NaiveBayesClusteringModel clusteringModel = new NaiveBayesClusteringModel();

        clusteringModel.buildStructure(dataStream.getStaticDataHeader());
        clusteringModel.initLearning();
        clusteringModel.learnModelFromStream(dataStream);

        dataStream.restart();
        clusteringModel.clusterMemberShip(dataStream.nextDataInstance());
    }

    public static void learningNaiveBayes(){

        DataStreamReaderFromFile reader = new DataStreamReaderFromFile("./data/data.arff");

        DataStream dataStream = reader.getDataStream();

        NaiveBayesClassifier nb = new NaiveBayesClassifier();
        nb.setClassVarID(dataStream.getStaticDataHeader().getObservedVariables().size()-1);
        nb.buildStructure(dataStream.getStaticDataHeader());
        nb.initLearning();
        nb.learnModelFromStream(dataStream);

        dataStream.restart();
        nb.predict(dataStream.nextDataInstance());
    }

    public static void main(String[] args) {

        System.out.println("Hello World!");
    }
}*/


package eu.amidst;


import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.core.models.BayesianNetworkWriter;
import eu.amidst.examples.BNExample;


public class Main {



 public static void main(String[] args) throws Exception {

     BayesianNetwork bn = BNExample.getAmidst_BN_Example();

     BayesianNetworkWriter.saveToFile(bn, "networks/dbn.ser");


 }

}