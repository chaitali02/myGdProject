export class AttributeIO {

    id: any;
    uuid: String;
    datapodname: String;
    name: String;
    dname: String;
    attributeId?: String;
    attrType: String;
    version: String
    label: String;
    type: String;
    attrName: String;
    
    value?: { 
        id?:any;
        name?: any;
        label?: String,
        value?: String,
        uuid?: String, 
        u_Id?: String,
        attrId?: String,
        attributeId?:String;
        attrName?: String;
        type?: String;
        datapodname?: String;
        version?: String;
        params?: String;
    };

}