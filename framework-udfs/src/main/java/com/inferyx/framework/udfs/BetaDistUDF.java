/**
 * 
 */
package com.inferyx.framework.udfs;

import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.hive.ql.exec.UDFMethodResolver;

/**
 * @author joy
 *
 */
//Description of the UDF
@Description(
 name="betaDist",
 value="returns Beta Distribution.",
 extended="select betaDist(input, alpha, beta) from hivesampletable limit 10;"
)
public class BetaDistUDF extends UDF {

	/**
	 * 
	 */
	public BetaDistUDF() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param rslv
	 */
	public BetaDistUDF(UDFMethodResolver rslv) {
		super(rslv);
		// TODO Auto-generated constructor stub
	}
	
	public Double evaluate(Double x,Double alpha,Double beta) {
		BetaDistribution bd = new BetaDistribution(alpha,beta);
		return bd.cumulativeProbability(x);
    }

}
