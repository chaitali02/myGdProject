import { BaseEntity } from './domain.baseEntity';
import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';
import { Attribute } from './domain.attribute';

export class Datapod extends BaseEntity{

     cache : String;
     datasource : MetaIdentifierHolder;
     attributes : Attribute[];
}