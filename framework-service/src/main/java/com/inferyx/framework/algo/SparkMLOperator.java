package com.inferyx.framework.algo;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.SparkContext;
import org.apache.spark.ml.clustering.KMeans;
import org.apache.spark.ml.clustering.KMeansModel;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.ParamSetHolder;
import com.inferyx.framework.service.ParamListServiceImpl;
import com.inferyx.framework.service.ParamSetServiceImpl;

@Service
public class SparkMLOperator implements IAlgorithm {
	
	private ParamSetServiceImpl paramSetServiceImpl;
	private ParamListServiceImpl paramListServiceImpl;
	private SparkContext sparkContext;
	private String filePathUrl;
	private Dataset<Row> dataframe;
	private Model model;
	
	
	public Dataset<Row> getDataframe() {
		return dataframe;
	}
	public void setDataframe(Dataset<Row> dataframe) {
		this.dataframe = dataframe;
	}
	public ParamListServiceImpl getParamListServiceImpl() {
		return paramListServiceImpl;
	}
	public Model getModel() {
		return model;
	}
	public void setModel(Model model) {
		this.model = model;
	}
	public void setParamListServiceImpl(ParamListServiceImpl paramListServiceImpl) {
		this.paramListServiceImpl = paramListServiceImpl;
	}
	public ParamSetServiceImpl getParamSetServiceImpl() {
		return paramSetServiceImpl;
	}
	public void setParamSetServiceImpl(ParamSetServiceImpl paramSetServiceImpl) {
		this.paramSetServiceImpl = paramSetServiceImpl;
	}
	public SparkContext getSparkContext() {
		return sparkContext;
	}
	public void setSparkContext(SparkContext sparkContext) {
		this.sparkContext = sparkContext;
	}
	public String getFilePathUrl() {
		return filePathUrl;
	}
	public void setFilePathUrl(String filePathUrl) {
		this.filePathUrl = filePathUrl;
	}



	@Override
	public boolean train(String className, String modelName, Dataset<Row> df, ParamMap paramMap) {
		try {
				
				Class<?> dynamicClass = Class.forName(className);
				Object obj = null;
				if(null != paramMap){
					Class[] paramDF = new Class[2];
					paramDF[0] = Dataset.class;
					paramDF[1] = ParamMap.class;
					Method method = dynamicClass.getMethod("fit", paramDF );
					obj = method.invoke(dynamicClass.newInstance(), df,paramMap);
				} else {
					Class[] paramDF = new Class[1];
					paramDF[0] = Dataset.class;
					Method method = dynamicClass.getMethod("fit", paramDF );
					obj = method.invoke(dynamicClass.newInstance(), df);
				}
				save(modelName, obj, sparkContext, filePathUrl);
			return true;
			
			//return method.getReturnType();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return false;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return false;
		} catch (InstantiationException e) {
			e.printStackTrace();
			return false;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return false;
		} catch (SecurityException e) {
			e.printStackTrace();
			return false;
		}
	}
	public List<ParamMap> getParamMap(ExecParams execParams, String className) throws JsonProcessingException{
		List<ParamMap> paramMapList = new ArrayList<>();
		if(null != execParams){
			for(ParamSetHolder paramSetHolder :execParams.getParamInfo()){
				List<ParamListHolder> paramListHolder = paramSetServiceImpl.getParamListHolder(paramSetHolder);
				ParamMap paramMap = new ParamMap();
				for(ParamListHolder plh:paramListHolder){
					if(className.contains("KMeans")){
						KMeans kmeans = new KMeans();
						paramMap.put(kmeans.maxIter().w(10),kmeans.k().w(8),kmeans.predictionCol().w("test"));
						paramMapList.add(paramMap);
						
					}
				}
			}
		}		
		return paramMapList;
	}
	public boolean save(String className, Object obj, SparkContext sparkContext,String path){
		
		
			Class<?> dynamicClass = obj.getClass();
			//dynamicClass = dynamicClass.cast(obj);
			
			Class[] paramSave = new Class[1];
			//paramSave[0] = SparkContext.class;
			paramSave[0] = String.class;
			
/*			//KMeansModel kmeansModel = obj;
			if(obj. instanceof KMeansModel){
				KMeansModel kmeansModel = (KMeansModel)obj;
				kmeansModel.save(path);
				
				try {
					kmeansModel.save(path);
					return true;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			*/
			Method m1;
			try {
				m1 = dynamicClass.getMethod("save", paramSave );
				try {
					m1.invoke(obj,  path);
					return true;
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					String expMessage = e.getCause().getMessage();//getMessage();
					if(obj instanceof KMeansModel && expMessage.contains("use write().overwrite().save(path) for Java")){
						KMeansModel kmeansModel = (KMeansModel)obj;
						try {
							kmeansModel.write().overwrite().save(path);
							return true;
						} catch (IOException ioExp) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			} catch (NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

			
		
		
		return false;
	}

	@Override
	public Object validate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object predict() {
		// TODO Auto-generated method stub
		return null;
	}

}
