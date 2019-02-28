import { BaseEntity } from './domain.baseEntity';
import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';
import { FilterInfo } from './domain.filterInfo';
import { AttributeRefHolder } from './domain.attributeRefHolder';
import { SaveMode } from '../enums/saveMode';
import { AttributeMap } from './domain.attributeMap';

export class IngestRule extends BaseEntity {

    type: String; //FILE-FILE, FILE-TABLE, TABLE-FILE, TABLE-TABLE
    sourceDatasource: MetaIdentifierHolder;
    sourceDetail: MetaIdentifierHolder;
    targetDatasource: MetaIdentifierHolder;
    targetDetail : MetaIdentifierHolder;
    filterInfo : Array<FilterInfo>;
    runParams: String;
    sourceFormat: String; //CSV, TSV, PSV, PARQUET
    targetFormat:String; //CSV, TSV, PSV, PARQUET
    incrAttr: AttributeRefHolder;
    saveMode: SaveMode;
    ignoreCase: String = "N";
    sourceExtn: String;
    targetExtn: String;
    attributeMap : Array<AttributeMap>;
    splitBy : AttributeRefHolder;
    sourceHeader: String;
    targetHeader: String;

    // filterTableArray: any;
    // attributeTableArray: any;
}