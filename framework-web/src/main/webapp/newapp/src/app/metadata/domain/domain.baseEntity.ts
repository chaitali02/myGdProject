
import {MetaIdentifierHolder} from './domain.metaIdentifierHolder';

export class BaseEntity extends MetaIdentifierHolder{
  
  id : String;
  uuid :String;
  version : String;
  name : String;
  desc : String;
  createdBy : MetaIdentifierHolder;
  createdOn : Date;
  active : String="Y" ;
  published : String="N";
  locked : String="N";
  tags : String[];
  appInfo :MetaIdentifierHolder[];

}