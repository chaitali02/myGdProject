
import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';
import { BaseEntity } from './domain.baseEntity';

export class Algorithm extends BaseEntity {

	type: String; // clustering, regression, classification
	libraryType: String; //sparkML, R, tensorflow, H2O
	trainClass: String; //e.g. org.apache.spark.ml.classification.LogisticRegression;
	modelClass: String; //e.g. org.apache.spark.ml.classification.LogisticRegressionModel;
	summaryMethods: Array<String>;
	labelRequired: String;
	returnType: String;
	savePmml: String;
	paramListWoH: MetaIdentifierHolder;
	paramListWH: MetaIdentifierHolder;
	customFlag: String = "N";
	scriptName: String;
}



