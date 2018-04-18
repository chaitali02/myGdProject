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
/**
 * 
 */
package com.inferyx.framework.operator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.linalg.distributed.BlockMatrix;
import org.apache.spark.mllib.linalg.distributed.CoordinateMatrix;
import org.apache.spark.mllib.linalg.distributed.MatrixEntry;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.springframework.stereotype.Service;

/**
 * @author netsafe
 *
 */
@Service
public class MatrixMulOperator implements Serializable{

static Logger logger=Logger.getLogger(ExpressionOperator.class);

	
	private static HashMap<String,Integer> rowSeqMap = new HashMap<String,Integer>();
	private static HashMap<String,Integer> colSeqMap = new HashMap<String,Integer>();
	private static HashMap<Integer,String> rowSeqFinalMap = new HashMap<Integer,String>();
	private static HashMap<Integer,String> colSeqFinalMap = new HashMap<Integer,String>();
	
	private static Function rddFun = new Function<Row, Row>() {

		@Override
		public Row call(Row v1) throws Exception {
			// TODO Auto-generated method stub
			Row r = RowFactory.create(v1.get(0),rowSeqMap.get(v1.get(1)),colSeqMap.get(v1.get(2)),v1.get(3));
			logger.info("updated Rows:"+v1.get(0)+":"+rowSeqMap.get(v1.get(1))+":"+colSeqMap.get(v1.get(2))+":"+v1.get(3));
			//r.get
			return r;
		}
		
	};
	
	private static Function rddFunFinal = new Function<Row, Row>() {

		@Override
		public Row call(Row v1) throws Exception {
			// TODO Auto-generated method stub
			logger.info("updated RowsFinal1:"+v1.get(0)+":"+v1.get(1).toString()+":"+v1.get(2).toString()+":"+v1.get(3));
			logger.info("rowseqFianlMap:"+rowSeqFinalMap);
			logger.info("colseqFianlMap:"+colSeqFinalMap);
			Row r = RowFactory.create(v1.get(0),rowSeqFinalMap.get(v1.get(1)),colSeqFinalMap.get(v1.get(2)),v1.get(3));
			logger.info("updated RowsFinal:"+v1.get(0)+":"+rowSeqFinalMap.get(v1.get(1))+":"+colSeqFinalMap.get(v1.get(2))+":"+v1.get(3));
			//r.get
			return r;
		}
		
	};
	
	/**
	 * Input two JavaRDD<MatrixEntry> ies, multiply the two matrices in order of input and return 
	 * result as a BlockMatrix
	 * @param matrixA
	 * @param matrixB
	 * @return
	 */
	private BlockMatrix matMul(JavaRDD<MatrixEntry> matrixA, JavaRDD<MatrixEntry> matrixB) {
		CoordinateMatrix cooMatrixA = new CoordinateMatrix(matrixA.rdd());
		CoordinateMatrix cooMatrixB = new CoordinateMatrix(matrixB.rdd());
		BlockMatrix matA = cooMatrixA.toBlockMatrix();
		matA.validate();
		BlockMatrix matB = cooMatrixB.toBlockMatrix();
		matB.validate();
		return matA.multiply(matB);
	}
	
	/**
	 * Convert JavaRDD<Row> to JavaRDD<MatrixEntry>
	 */
	private JavaRDD<MatrixEntry> rddRowToMatrixEntryConverter(JavaRDD<Row> rowRDD){
		return rowRDD.map(new Function<Row, MatrixEntry>() {

			@Override
			public MatrixEntry call(Row x) throws Exception {
				long i = x.getInt(1);	// Get row
				long j = x.getInt(2);	// Get col
				double value = x.getDouble(3);	// Get value
				return new MatrixEntry(i, j, value);
			}
		});
	}
	
	/**
	 * Convert JavaRDD<MatrixEntry> to JavaRDD<Row>
	 */
	private JavaRDD<Row> rddMatrixEntryToRowConverter(JavaRDD<MatrixEntry> rowRDD){
		return rowRDD.map(new Function<MatrixEntry, Row>() {

			@Override
			public Row call(MatrixEntry matrixEntry) throws Exception {
				long i = matrixEntry.i();	// Get row
				long j = matrixEntry.j();	// Get col
				double value = matrixEntry.value();	// Get value
				return RowFactory.create(i, j, value);
			}
		});
	}
	
	/**
	 * JavaRDD<Row> filter to extract one matrix out of a set of matrices
	 * @param rowRDD
	 * @param matrixNum
	 * @return
	 */
	private JavaRDD<Row> filterMatrix(JavaRDD<Row> rowRDD, final String matrixName) {
		return rowRDD.filter(new Function<Row, Boolean>() {

			@Override
			public Boolean call(Row x) throws Exception {
				if(Integer.toString(x.getInt(0)).equals(matrixName)) {
					return true;
				}
				return false;
			}
		});
	}
	
	/**
	 * Input a JavaRDD<Row> containing matrixName, row, column, value in that order & a list of matrixNames to be processed in order
	 * Outputs the matrix multiplication result in the form of JavaRDD<Row>
	 * @param allMatrices
	 * @param matrixIteratorList
	 * @return
	 */
	public JavaRDD<Row> getMatrixMul(JavaRDD<Row> allMatrices, List<String> matrixIteratorList){
		
		JavaRDD<Row> matrixRDD = null;
		JavaRDD<MatrixEntry> tempMatrix = null;
		JavaRDD<Row> consolidatedRDD = null;
		JavaRDD<Row> everyRDD = null;
		JavaRDD<MatrixEntry> consolidatedMatrix = null;
		JavaRDD<MatrixEntry> matB = null;
		int count = 0;
		
		// terate through matrixNameList
		for(String matrixName : matrixIteratorList) {
			// Get each matrix
			matrixRDD = filterMatrix(allMatrices, matrixName);
			
			if(count == 0) {
				tempMatrix = rddRowToMatrixEntryConverter(matrixRDD);
				consolidatedMatrix = tempMatrix;
				consolidatedRDD = updateMatrixwithIterValue(tempMatrix,matrixName);
				count++;
				continue;
			}
			
			matB = rddRowToMatrixEntryConverter(matrixRDD);
			// Multiply and assign result to tempMatrix for next iteration
			tempMatrix = matMul(tempMatrix, matB).toCoordinateMatrix().entries().toJavaRDD();
			consolidatedMatrix = consolidatedMatrix.union(tempMatrix);
			consolidatedRDD = consolidatedRDD.union(updateMatrixwithIterValue(tempMatrix,matrixName));
			count++;
		} // End for
		
		// Get the result from tempMatrix
		//return rddMatrixEntryToRowConverter(consolidatedMatrix);
		return consolidatedRDD;
	}
	
	public JavaRDD<Row> updateMatrixwithIterValue(JavaRDD<MatrixEntry> matrixEntry, String iterValue){
		JavaRDD<Row> rdd = matrixEntry.map(new Function<MatrixEntry, Row>() {

			@Override
			public Row call(MatrixEntry matrixEntry) throws Exception {
				long i = matrixEntry.i();	// Get row
				long j = matrixEntry.j();	// Get col
				double value = matrixEntry.value();	// Get value
				return RowFactory.create(iterValue, (int)i, (int)j, value);
			}
		});
		
		return rdd;
	}
	
	public JavaRDD<Row> populateNFetch(Dataset<Row> df, HashMap<String,Object> operParams){
		
		// 1. Populate all required data
		List<String> iterListArray = (List<String>)operParams.get("iterlist");
		List<String> rowSeq = (List<String>)operParams.get("rowseq");
		List<String> colSeq = (List<String>)operParams.get("colseq");
		
		Integer i = 1;
		for(String rowSeqStr:rowSeq){
			rowSeqMap.put(rowSeqStr, i);
			rowSeqFinalMap.put(i, rowSeqStr);
			i++;
		}
		i=1;
		for(String colSeqStr:colSeq){
			colSeqMap.put(colSeqStr, i);
			colSeqFinalMap.put(i, colSeqStr);
			i++;
		}
		
		
		
		// Fetch df to JavaRDD
		JavaRDD<Row> javaRDDRow = df.toJavaRDD();
		logger.info("javaRDDRow->count() " +javaRDDRow.count());
		//JavaRDD<Row> jRDDNew = new J
		//javaRDDRow.
		
		// Convert RDD to support Matrix Mul
		JavaRDD<Row> javaRDDUpdated = javaRDDRow.map(rddFun);
		logger.info("javaRDDUpdated->count() " +javaRDDUpdated.count());
		
		//MatrixMulOperator mmo = new MatrixMulOperator();
		//Matrix Mul
		JavaRDD<Row> output = getMatrixMul(javaRDDUpdated, iterListArray);
		logger.info("output->count() " +output.count());
		List<Row> listRow1 = output.collect();
		for(int i2=0;i2<20;i2++){
			logger.info("lisRow :"+listRow1.get(i2));
		}
		// Convert Matrix Mul RDD to desirable output RDD
		JavaRDD<Row> finalOutput = output.map(rddFunFinal);
		
		List<Row> listRow = finalOutput.collect();
		for(int i1=0;i1<20;i1++){
			logger.info("lisRow :"+listRow.get(i1));
		}
		
		
		return finalOutput;
	}

}
