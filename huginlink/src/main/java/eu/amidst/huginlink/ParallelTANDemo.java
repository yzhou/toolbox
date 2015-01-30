package eu.amidst.huginlink;


import COM.hugin.HAPI.ExceptionHugin;
import com.google.common.base.Stopwatch;

import eu.amidst.core.database.DataBase;
import eu.amidst.core.database.filereaders.StaticDataOnDiskFromFile;
import eu.amidst.core.database.filereaders.arffFileReader.ARFFDataReader;
import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.core.models.BayesianNetworkLoader;
import eu.amidst.core.utils.BayesianNetworkGenerator;
import eu.amidst.core.utils.BayesianNetworkSampler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.lang.Runtime;

import org.apache.commons.cli.*;


/**
 * Created by afa on 16/12/14.
 */
//TODO Move this class to the module huginLink
public class ParallelTANDemo {

    static int numCores = Runtime.getRuntime().availableProcessors();
    static int sampleSize = 10000;
    static int samplesOnMemory = 1000;
    static int numDiscVars = 2000;
    static String dataFileInput = "";
    static boolean onServer = false;
    static int batchSize = 1000;
    static int numStates = 2;

    public static void demoPigs() throws ExceptionHugin, IOException, ClassNotFoundException {


        //It needs GBs, so avoid putting this file in a Dropbox folder!!!!
        String dataFile = new String("/Users/afa/Pigs.arff");


        BayesianNetwork bn = BayesianNetworkLoader.loadFromFile("networks/Pigs.ser");


        //int sampleSize = 100000;
        //BayesianNetworkSampler sampler = new BayesianNetworkSampler(bn);
        //sampler.setParallelMode(true);
        //sampler.sampleToAnARFFFile(dataFile, sampleSize);

        ArrayList<Integer> vSamplesOnMemory = new ArrayList(Arrays.asList(5000));
        ArrayList<Integer> vNumCores = new ArrayList(Arrays.asList(1, 2, 3, 4));

        for (Integer samplesOnMemory : vSamplesOnMemory) {
            for (Integer numCores : vNumCores) {
                System.out.println("Learning TAN: " + samplesOnMemory + " samples on memory, " + numCores + "core/s ...");
                DataBase data = new StaticDataOnDiskFromFile(new ARFFDataReader(dataFile));

                ParallelTAN tan = new ParallelTAN();
                tan.setNumCores(numCores);
                tan.setNumSamplesOnMemory(samplesOnMemory);
                tan.setNameRoot(bn.getStaticVariables().getListOfVariables().get(0).getName());
                tan.setNameTarget(bn.getStaticVariables().getListOfVariables().get(1).getName());
                Stopwatch watch = Stopwatch.createStarted();
                BayesianNetwork model = tan.learnBN(data);
                System.out.println(watch.stop());
            }
        }
    }


    public static void demoLive() throws ExceptionHugin, IOException {

        String dataFile = "";
        int numContVars = 0;
        String nameRoot = "";
        String nameTarget = "";
        DataBase data;
        int nOfVars;

        //It may need many GBs, so avoid putting this file in a Dropbox folder!!!
        dataFile = new String("./datasets/Data_#v" + numDiscVars + "_#s" + sampleSize + ".arff");
        BayesianNetworkGenerator.setNumberOfContinuousVars(numContVars);
        BayesianNetworkGenerator.setNumberOfDiscreteVars(numDiscVars);
        BayesianNetworkGenerator.setNumberOfStates(2);
        BayesianNetworkGenerator.setSeed(0);

        BayesianNetwork bn = BayesianNetworkGenerator.generateNaiveBayes(2);


        BayesianNetworkSampler sampler = new BayesianNetworkSampler(bn);
        sampler.setParallelMode(true);
        sampler.sampleToAnARFFFile(dataFile, sampleSize);
        data = new StaticDataOnDiskFromFile(new ARFFDataReader(dataFile));
        nOfVars = numContVars + numDiscVars;
        System.out.println("Learning TAN: " + nOfVars + " variables, " + sampleSize + " samples on disk, " + samplesOnMemory + " samples on memory, 1 core(s) ...");


        nameRoot = data.getAttributes().getList().get(numDiscVars - 1).getName();
        nameTarget = data.getAttributes().getList().get(0).getName();


        ParallelTAN tan = new ParallelTAN();
        tan.setParallelMode(false);
        tan.setNumSamplesOnMemory(samplesOnMemory);
        tan.setNameRoot(nameRoot);
        tan.setNameTarget(nameTarget);
        BayesianNetwork model = tan.learnBN(data);
        System.out.println();


        System.out.println("Learning TAN: " + nOfVars + " variables, " + sampleSize + " samples on disk, " + samplesOnMemory + " samples on memory, " + numCores + " core(s) ...");

        data = new StaticDataOnDiskFromFile(new ARFFDataReader(dataFile));

        tan = new ParallelTAN();
        tan.setParallelMode(true);
        tan.setNumSamplesOnMemory(samplesOnMemory);
        tan.setNameRoot(nameRoot);
        tan.setNameTarget(nameTarget);
        tan.setNumCores(numCores);
        tan.setBatchSize(batchSize);
        model = tan.learnBN(data);


    }

    public static void demoLuxembourg() throws ExceptionHugin, IOException {
        String dataFile = "";
        int numContVars = 0;
        String nameRoot = "";
        String nameTarget = "";
        DataBase data;
        int nOfVars;

    /* Generate some fake data and write to file */
        dataFile = new String("./datasets/Data_#v" + numDiscVars + "_#s" + sampleSize + ".arff");
        BayesianNetworkGenerator.setNumberOfContinuousVars(numContVars);
        BayesianNetworkGenerator.setNumberOfDiscreteVars(numDiscVars);
        BayesianNetworkGenerator.setNumberOfStates(2);
        BayesianNetworkGenerator.setSeed(0);

        BayesianNetwork bn = BayesianNetworkGenerator.generateNaiveBayes(2);
        BayesianNetworkSampler sampler = new BayesianNetworkSampler(bn);
        sampler.setParallelMode(true);
        sampler.sampleToAnARFFFile(dataFile, sampleSize);
        data = new StaticDataOnDiskFromFile(new ARFFDataReader(dataFile));
        nOfVars = numContVars + numDiscVars;

    /* Get information about the model: Root and Target */
        nameRoot = data.getAttributes().getList().get(numDiscVars - 1).getName();
        nameTarget = data.getAttributes().getList().get(0).getName();

    /* Setup the TAN object */
        ParallelTAN tan = new ParallelTAN();
        tan.setParallelMode(numCores > 1);
        tan.setNumCores(numCores);
        tan.setNumSamplesOnMemory(samplesOnMemory);
        tan.setNameRoot(nameRoot);
        tan.setNameTarget(nameTarget);
        tan.setBatchSize(batchSize);

        System.out.println("\nLearning TAN (" + nOfVars + " variables) using " + tan.getNumCores() + " core/s.");
        System.out.println("Structure learning (Hugin) uses " + tan.getNumSamplesOnMemory() + " samples.");
        System.out.println("Parameter learning (toolbox) uses " +
                sampleSize + " samples (batch size " + tan.getBatchSize() + ").");

    /* Learn */
        System.out.println("Run-times:");
        BayesianNetwork model = tan.learnBN(data);
        System.out.println();
    }

    public static void demoOnServer() throws ExceptionHugin, IOException {


        String dataFile = "";
        int numContVars = 0;
        String nameRoot = "";
        String nameTarget = "";
        DataBase data;
        int nOfVars;


        /**
         * Sample from NB network with the specified features.
         */
        if (dataFileInput.isEmpty()) {
            //It may need many GBs, so avoid putting this file in a Dropbox folder!!!
            dataFile = new String("./datasets/Data_#v" + numDiscVars + "_#s" + sampleSize + ".arff");
            BayesianNetworkGenerator.setNumberOfContinuousVars(numContVars);
            BayesianNetworkGenerator.setNumberOfDiscreteVars(numDiscVars);
            BayesianNetworkGenerator.setNumberOfStates(numStates);
            BayesianNetworkGenerator.setSeed(0);
            BayesianNetwork bn = BayesianNetworkGenerator.generateNaiveBayes(2);

            BayesianNetworkSampler sampler = new BayesianNetworkSampler(bn);
            sampler.setParallelMode(true);
            sampler.sampleToAnARFFFile(dataFile, sampleSize);
            data = new StaticDataOnDiskFromFile(new ARFFDataReader(dataFile));
            nOfVars = numContVars + numDiscVars;
            System.out.println("Learning TAN: " + nOfVars + " variables, " + numStates + " states/var, " + sampleSize + " samples on disk, " + samplesOnMemory + " samples on memory, " + numCores + " core(s) ...");
        } else {
            data = new StaticDataOnDiskFromFile(new ARFFDataReader(dataFileInput));
            numDiscVars = data.getAttributes().getNumberOfAttributes();
            nOfVars = numContVars + numDiscVars;
            System.out.println("Learning TAN: " + nOfVars + " variables, " + " samples on file " + dataFileInput + "," + samplesOnMemory + " samples on memory, " + numCores + " core(s) ...");
        }

        nameRoot = data.getAttributes().getList().get(numDiscVars - 1).getName();
        nameTarget = data.getAttributes().getList().get(0).getName();


        /**
         * Serial mode
         */
        if (numCores == 1) {
            ParallelTAN tan = new ParallelTAN();
            tan.setParallelMode(false);
            tan.setNumSamplesOnMemory(samplesOnMemory);
            tan.setNameRoot(nameRoot);
            tan.setNameTarget(nameTarget);
            BayesianNetwork model = tan.learnBN(data);
        } else {
            /**
             * Parallel mode (by default, and also by default all available cores are used)
             */
            ParallelTAN tan = new ParallelTAN();
            tan.setParallelMode(true);
            tan.setNumSamplesOnMemory(samplesOnMemory);
            tan.setNameRoot(nameRoot);
            tan.setNameTarget(nameTarget);
            tan.setNumCores(numCores);
            tan.setBatchSize(batchSize);
            BayesianNetwork model = tan.learnBN(data);
        }


    }

    public static void useGnuParser(final String[] commandLineArguments) {
        final CommandLineParser cmdLineGnuParser = new GnuParser();

        final Options gnuOptions = constructOptions();
        CommandLine commandLine;
        try {
            commandLine = cmdLineGnuParser.parse(gnuOptions, commandLineArguments);
            if (commandLine.hasOption("c")) {
                numCores = Integer.parseInt(commandLine.getOptionValue("c"));
                System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", Integer.toString(numCores));
            }
            if (commandLine.hasOption("s")) {
                sampleSize = Integer.parseInt(commandLine.getOptionValue("s"));
            }
            if (commandLine.hasOption("m")) {
                samplesOnMemory = Integer.parseInt(commandLine.getOptionValue("m"));
            }
            if (commandLine.hasOption("v")) {
                numDiscVars = Integer.parseInt(commandLine.getOptionValue("v"));
            }
            if (commandLine.hasOption("d")) {
                dataFileInput = commandLine.getOptionValue("d");
            }
            if (commandLine.hasOption("b")) {
                batchSize = Integer.parseInt(commandLine.getOptionValue("b"));
            }
            if (commandLine.hasOption("onServer")) {
                onServer = true;
            }
            if (commandLine.hasOption("r")) {
                numStates = Integer.parseInt(commandLine.getOptionValue("r"));
            }

        } catch (ParseException parseException)  // checked exception
        {
            System.err.println(
                    "Encountered exception while parsing using GnuParser:\n"
                            + parseException.getMessage());
        }
    }


    /**
     * Write "help" to the provided OutputStream.
     */
    public static void printHelp(
            final Options options,
            final int printedRowWidth,
            final String header,
            final String footer,
            final int spacesBeforeOption,
            final int spacesBeforeOptionDescription,
            final boolean displayUsage,
            final OutputStream out) {
        final String commandLineSyntax = "run.sh eu.amidst.examples.ParallelTANDemo";
        final PrintWriter writer = new PrintWriter(out);
        final HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(
                writer,
                printedRowWidth,
                commandLineSyntax,
                header,
                options,
                spacesBeforeOption,
                spacesBeforeOptionDescription,
                footer,
                displayUsage);
        writer.flush();
    }


    /**
     * Construct and provide the Options.
     *
     * @return Options expected from command-line.
     */
    public static Options constructOptions() {
        final Options options = new Options();
        options.addOption("c", "numCores", true, "Here you can set # of cores for hugin.");
        options.addOption("s", "samples", true, "Here you can set # of (out-of-core) samples for parameter learning (amidst).");
        options.addOption("m", "samplesOnMemory", true, "Here you can set # of (in memory) samples for structural learning (hugin).");
        options.addOption("v", "variables", true, "Here you can set # of variables .");
        options.addOption("d", "dataPath", true, "Here you can specify the data path .");
        options.addOption("b", "batchSize", true, "Here you can specify the batch size for learning.");
        options.addOption("onServer", "onServer", false, "write onServer to run onServer method (with more options).");
        options.addOption("r", "numStates", true, "Here you can set # of states.");

        return options;
    }


    //TODO: Subobtions should be considered in the future
    public static void main(String[] args) throws ExceptionHugin, IOException, ClassNotFoundException {
        ParallelTANDemo.demoPigs();
    }

   /*     final String applicationName = "run.sh eu.amidst.examples.ParallelTANDemo";

        if (args.length < 1) {

            System.out.println("\n-- USAGE/HELP --\n");
            printHelp(
                    constructOptions(), 85, "Parallel TAN HELP", "End of PARALLEL TAN Help",
                    3, 5, true, System.out);

            System.out.println();
            System.out.println("Running using by default parameters:");
        }

        useGnuParser(args);

        if(onServer)
            ParallelTANDemo.demoOnServer();
        else
            ParallelTANDemo.demoLuxembourg();
    }
*/
}