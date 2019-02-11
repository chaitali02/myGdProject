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

import java.util.Random;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.log4j.Logger;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.api.java.UDF2;
import org.apache.spark.sql.api.java.UDF3;
import org.apache.spark.sql.types.DataTypes;
import org.springframework.stereotype.Component;

@Component
public class UDFRegister implements java.io.Serializable {
	
	static final Logger logger = Logger.getLogger(UDFRegister.class);
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void register(SparkSession sparkSession) {
		logger.info("Inside UDF Register");
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
		
		sparkSession.udf().register("randn", new UDF1<Object, Double>() {
			
			private static final long serialVersionUID = 1L;
			Random random = new Random();

			@Override
			public Double call(Object t1) throws Exception {
				return random.nextGaussian();
			}
		}, DataTypes.DoubleType);
		
		sparkSession.udf().register("nanif", new UDF2<Double, Double, Double>() {

			@Override
			public Double call(Double t1, Double t2) throws Exception {
				if (t1 == null || t1.isNaN() || t1.isInfinite()) {
					return t2;
				}
				return t1;
			}
		}, DataTypes.DoubleType);
		
	}
}
