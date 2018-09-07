/**
 * 
 */
package com.inferyx.framework.udfs;

import org.apache.commons.lang.math.NumberUtils;
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
 name="maxof",
 value="returns maximum of two values.",
 extended="select maxof(input) from hivesampletable limit 10;"
)
public class MaxOfUDF extends UDF {

	/**
	 * 
	 */
	public MaxOfUDF() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param rslv
	 */
	public MaxOfUDF(UDFMethodResolver rslv) {
		super(rslv);
		// TODO Auto-generated constructor stub
	}
	
	public Double evaluate(Double value1,Double value2) {
		double []maxArray = new double[2];
		maxArray[0] = value1;
		maxArray[1] = value2;
		return NumberUtils.max(maxArray);
    }

}
