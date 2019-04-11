import { MetaIdentifier } from '../domain/domain.metaIdentifier';

export class DropDownIO extends MetaIdentifier{

    label: String;
    value: { label: any, uuid?: any, version?: any, attributeId?: any, u_Id?: any, id?: any}
}