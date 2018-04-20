import { MetaIdentifierHolder } from "./domain.metaIdentifierHolder";

export class Operator{

	private operatorId : String;
	private dependsOn : MetaIdentifierHolder[];// = new ArrayList<>(0);
	private operatorType : String;
	private operatorInfo : MetaIdentifierHolder;
	private operatorParams : String;//= new HashMap<String, Object>();
}