/**
 * 
 */
package com.inferyx.framework.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration.Builder;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.Layer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

/**
 * @author joy
 *
 */
public class MultilayerNetwork {

	/**
	 * 
	 */
	public MultilayerNetwork() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		final int seed = 12345;
		// Number of iterations per minibatch
		final int iterations = 1;
		// Number of epochs (full passes of the data)
		final int nEpochs = 200;
		// Number of data points
		final int nSamples = 1000;
		// Batch size: i.e., each epoch has nSamples/batchSize parameter updates
		final int batchSize = 100;
		// Network learning rate
		final double learningRate = 0.01;
		final Random rng = new Random(seed);
		int numInput = 2;
		int numOutputs = 1;
		int nHidden = 10;

		List<Layer> layers = new ArrayList<Layer>();
		layers.add(new DenseLayer.Builder().nIn(numInput).nOut(nHidden).activation(Activation.TANH).build());
		layers.add(new OutputLayer.Builder(LossFunctions.LossFunction.MSE).activation(Activation.IDENTITY)
						.nIn(nHidden).nOut(numOutputs).build());
		
		MultiLayerConfiguration multiLayerConfiguration = multLayerNetConf(neuralNetConf(seed, iterations, learningRate), layers);
		// Generate the training data
		DataSetIterator iterator = getTrainingData(batchSize, rng);
		multilayer(multiLayerConfiguration, seed, iterations, nEpochs, nSamples, batchSize, learningRate, iterator);
	}
	
	/**
	 * 
	 * @param seed
	 * @param iterations
	 * @param learningRate
	 * @return
	 */
	private static Builder neuralNetConf(int seed, int iterations, double learningRate) {
		return new NeuralNetConfiguration.Builder().seed(seed)
				.iterations(iterations).optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.learningRate(learningRate).weightInit(WeightInit.XAVIER).updater(Updater.NESTEROVS).momentum(0.9);
	}
	
	/**
	 * 
	 * @param builder
	 * @param layers
	 * @return
	 */
	private static MultiLayerConfiguration multLayerNetConf(Builder builder, List<Layer> layers) {
		Layer[] layerArr = new Layer[layers.size()];
		MultiLayerConfiguration.Builder multBuilder = builder.list(layers.toArray(layerArr)).pretrain(false).backprop(true);
		return multBuilder.build();
	}

	/**
	 * 
	 * @param multiLayerConfiguration
	 * @param seed
	 * @param iterations
	 * @param nEpochs
	 * @param nSamples
	 * @param batchSize
	 * @param learningRate
	 * @param iterator
	 * @throws IOException
	 */
	public static void multilayer(MultiLayerConfiguration multiLayerConfiguration, 
									int seed, 
									int iterations, 
									int nEpochs, 
									int nSamples, 
									int batchSize,
									double learningRate, 
									DataSetIterator iterator) throws IOException {

		MultiLayerNetwork net = new MultiLayerNetwork(multiLayerConfiguration);
		net.init();
		net.setListeners(new ScoreIterationListener(1));

		// Train the network on the full data set, and evaluate in periodically
		for (int i = 0; i < nEpochs; i++) {
			iterator.reset();
			net.fit(iterator);
		}
		// Test the addition of 2 numbers (Try different numbers here)
		final INDArray input = Nd4j.create(new double[] { 4.111111, 3.3333333333333 }, new int[] { 1, 2 });
		INDArray out = net.output(input, false);
		System.out.println(out);
	}

	/**
	 * 
	 * @param batchSize
	 * @param rand
	 * @return
	 */
	private static DataSetIterator getTrainingData(int batchSize, Random rand) {
		int nSamples = 10000;
		double[] sum = new double[nSamples];
		double[] input1 = new double[nSamples];
		double[] input2 = new double[nSamples];
		for (int i = 0; i < nSamples; i++) {
			int MIN_RANGE = 0;
			int MAX_RANGE = 3;
			input1[i] = MIN_RANGE + (MAX_RANGE - MIN_RANGE) * rand.nextDouble();
			input2[i] = MIN_RANGE + (MAX_RANGE - MIN_RANGE) * rand.nextDouble();
			sum[i] = input1[i] + input2[i];
		}
		INDArray inputNDArray1 = Nd4j.create(input1, new int[] { nSamples, 1 });
		INDArray inputNDArray2 = Nd4j.create(input2, new int[] { nSamples, 1 });
		INDArray inputNDArray = Nd4j.hstack(inputNDArray1, inputNDArray2);
		INDArray outPut = Nd4j.create(sum, new int[] { nSamples, 1 });
		DataSet dataSet = new DataSet(inputNDArray, outPut);
		List<DataSet> listDs = dataSet.asList();
		Collections.shuffle(listDs, rand);
		return new ListDataSetIterator(listDs, batchSize);

	}

}
