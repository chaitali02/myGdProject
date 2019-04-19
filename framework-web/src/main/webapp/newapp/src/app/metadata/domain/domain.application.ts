import { BaseEntity } from './domain.baseEntity';
import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';
import { ApplicationType } from '../enums/applicationType';

export class Application extends BaseEntity {
    dataSource: MetaIdentifierHolder;
    paramList: MetaIdentifierHolder;
    deployPort: String;
    orgInfo: MetaIdentifierHolder;
    applicationType: ApplicationType;

    paramlistChg?: any;
    applicationChg?: any;
}