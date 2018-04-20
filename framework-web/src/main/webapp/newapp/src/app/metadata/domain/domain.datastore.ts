import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';

export class Datastore{

    private metaId : MetaIdentifierHolder ;
	private execId : MetaIdentifierHolder;
	private location : String;
	private persistMode : String;
	private dimInfo : MetaIdentifierHolder[];
	private numRows : Number;
}