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
 name="minof",
 value="returns minimum of two values.",
 extended="select minof(input) from hivesampletable limit 10;"
)
public class MinOfUDF extends UDF {

	/**
	 * 
	 */
	public MinOfUDF() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param rslv
	 */
	public MinOfUDF(UDFMethodResolver rslv) {
		super(rslv);
		// TODO Auto-generated constructor stub
	}
	
	public Double evaluate(Double value1,Double value2) {
		double []maxArray = new double[2];
		maxArray[0] = value1;
		maxArray[1] = value2;
		return NumberUtils.min(maxArray);
    }

}
