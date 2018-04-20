export class DataPreparationFormulaAttributes {
  constructor(private uuid: string, private datapodname: string, private name: string, private dname: string, private attributeId: string) {
    this.setUuid(uuid);
    this.setDataPodName(datapodname);
    this.setName(name);
    this.setDname(dname);
    this.setAttributeId(attributeId);
  }
  setUuid(uuid: string) {
    this.uuid = uuid;
  }
  setDataPodName(datapodname: string) {
    this.datapodname = datapodname;
  }
  setName(name: string) {
    this.name = name;
  }
  setDname(dname: string) {
    this.dname = dname;
  }
  setAttributeId(attributeId: any) {
    this.attributeId = attributeId;
  }
}
