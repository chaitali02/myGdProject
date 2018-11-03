/**
 * 
 */
package com.inferyx.framework.udfs;

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
 name="normSDist",
 value="returns cumulative probability.",
 extended="select normSDist(input) from hivesampletable limit 10;"
)
public class NormSDistUDF extends UDF {

	/**
	 * 
	 */
	public NormSDistUDF() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param rslv
	 */
	public NormSDistUDF(UDFMethodResolver rslv) {
		super(rslv);
		// TODO Auto-generated constructor stub
	}
	
	public Double evaluate(Double input) {
		NormalDistribution nd = new NormalDistribution();
		return nd.cumulativeProbability(input);
    }

}
