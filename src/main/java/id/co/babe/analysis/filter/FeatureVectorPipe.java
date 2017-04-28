package id.co.babe.analysis.filter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.SparseVector;

public class FeatureVectorPipe extends Pipe implements Serializable
{
	boolean binary;

	public FeatureVectorPipe (boolean binary)
	{
		this.binary = binary;
	}

	public FeatureVectorPipe ()
	{
		this (false);
	}
	
	
	public Instance pipe (Instance carrier)
	{
		String article = (String) carrier.getData();
		int[] featureIndices = FeatureCalculator.getFeatureIndces();
		double[] values = FeatureCalculator.getFeatureValues(article);
		FeatureVector vector = new FeatureVector(getDataAlphabet(), featureIndices, values);
		carrier.setData(vector);
		return carrier;
	}

	// Serialization 
	
	private static final long serialVersionUID = 1;
	private static final int CURRENT_SERIAL_VERSION = 1;
	
	private void writeObject (ObjectOutputStream out) throws IOException {
		out.writeInt (CURRENT_SERIAL_VERSION);
		out.writeBoolean (binary);
	}
	
	private void readObject (ObjectInputStream in) throws IOException, ClassNotFoundException {
		int version = in.readInt ();
		if (version > 0)
			binary = in.readBoolean();
	}
}
