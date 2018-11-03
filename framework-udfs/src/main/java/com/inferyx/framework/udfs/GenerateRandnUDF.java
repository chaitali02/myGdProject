/**
 * 
 */
package com.inferyx.framework.udfs;

import java.util.Random;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.hive.ql.exec.vector.VectorizedExpressions;
import org.apache.hadoop.hive.ql.exec.vector.expressions.FuncRand;
import org.apache.hadoop.hive.ql.exec.vector.expressions.FuncRandNoSeed;
import org.apache.hadoop.hive.ql.udf.UDFType;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;

/**
 * @author joy
 *
 */
@Description(name = "randn",
value = "_FUNC_([seed]) - Returns a gaussian number between 0 and 1")
@UDFType(deterministic = false)
@VectorizedExpressions({FuncRandNoSeed.class, FuncRand.class})
public class GenerateRandnUDF extends UDF {

	private Random random;

	  private final DoubleWritable result = new DoubleWritable();

	  public GenerateRandnUDF() {
	  }

	  public DoubleWritable evaluate() {
	    if (random == null) {
	      random = new Random();
	    }
	    result.set(random.nextGaussian());
	    return result;
	  }

	  public DoubleWritable evaluate(LongWritable seed) {
	    if (random == null) {
	      long seedValue = 0;
	      if (seed != null) {
	        seedValue = seed.get();
	      }
	      random = new Random(seedValue);
	    }
	    result.set(random.nextGaussian());
	    return result;
	  }

}
