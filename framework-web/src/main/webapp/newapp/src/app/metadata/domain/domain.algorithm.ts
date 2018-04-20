import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';
import {BaseEntity} from './domain.baseEntity';

export class Algorithm extends BaseEntity{

    private type_algo : MetaIdentifierHolder; // clustering, regression
	private library : String; //sparkML, R, tensorflow
	private trainName : String; //org.apache.spark.ml.classification.LogisticRegression;
	private modelName : String; //org.apache.spark.ml.classification.LogisticRegressionModel;
	private labelRequired : String;
	private returnType : String; 
	private paramList : MetaIdentifierHolder;
		

}



