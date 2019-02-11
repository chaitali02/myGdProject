export class AttributeIO {

    id: String;
    uuid: String;
    datapodname: String;
    name: String;
    dname: String;
    attributeId: String;
    attrType: String;

    label?: String;
    value?: { 
        label?: String,
        value?: String,
        uuid?: String, 
        u_Id?: String,
        attrId?: String,
        attributeId?:String;
    };

}