/*******************************************************************************
 * Copyright (C) Inferyx Inc, 2018 All rights reserved. 
 *
 * This unpublished material is proprietary to Inferyx Inc.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@inferyx.com>
 *******************************************************************************/
package com.inferyx.framework.register;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.api.java.UDF2;
import org.apache.spark.sql.api.java.UDF3;
import org.apache.spark.sql.types.DataTypes;

public class UDFRegister implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UDFRegister(SparkSession sparkSession) {
		
		//Register NORM.S.INV
		sparkSession.udf().register("normSInv",new UDF1<Double,Double>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
				public Double call(Double p) throws Exception {
					NormalDistribution nd = new NormalDistribution();
					return nd.inverseCumulativeProbability(p);
			}
		}, DataTypes.DoubleType);

		//Register NORM.S.DIST 
		sparkSession.udf().register("normSDist",new UDF1<Double,Double>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
				public Double call(Double p) throws Exception {
					NormalDistribution nd = new NormalDistribution();
					return nd.cumulativeProbability(p);
			}
		}, DataTypes.DoubleType);
	
		//Register BETADIST 
		sparkSession.udf().register("betaDist",new UDF3<Double,Double,Double,Double>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
				public Double call(Double x,Double alpha,Double beta) throws Exception {
				BetaDistribution bd = new BetaDistribution(alpha,beta);
				return bd.cumulativeProbability(x);
			}
		}, DataTypes.DoubleType);

		//Register MAX
		sparkSession.udf().register("maxOf",new UDF2<Double,Double,Double>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Double call(Double value1,Double value2) throws Exception {
				double []maxArray = new double[2];
				maxArray[0] = value1;
				maxArray[1] = value2;
				return NumberUtils.max(maxArray);
			}
		}, DataTypes.DoubleType);
		
		//Register MAX
		sparkSession.udf().register("minOf",new UDF2<Double,Double,Double>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Double call(Double value1,Double value2) throws Exception {
				double []maxArray = new double[2];
				maxArray[0] = value1;
				maxArray[1] = value2;
				return NumberUtils.min(maxArray);
			}
		}, DataTypes.DoubleType);
		
	}
}
