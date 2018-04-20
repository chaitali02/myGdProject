import { AttributeRefHolder } from './domain.attributeRefHolder';
import { MetaIdentifier } from './domain.metaIdentifier';
import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';
import { ParamSetHolder } from './domain.paramSetHolder';

export class ExecParams{
    
	private refKeyList : MetaIdentifier[] ;
    
        private paramInfo : ParamSetHolder[];
    
        private dimInfo : MetaIdentifierHolder[];
        
        private filterInfo : AttributeRefHolder[];	
    
        private stageInfo : String[];
        
        private paramSetHolder : ParamSetHolder[];
}