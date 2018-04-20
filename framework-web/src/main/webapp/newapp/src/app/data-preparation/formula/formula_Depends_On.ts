export class DataPreparationFormulaInfo {
  constructor(private type: string, private uuid: string, private attributeId: string, private value: string) {
    this.setType(type);
    this.setUuid(uuid);
    this.setAttributeId(attributeId);
    this.setValue(value);
  }

  setType(type: string) {
    this.type = type;
  }

  setUuid(uuid: string) {
    this.uuid = uuid;
  }

  setAttributeId(attributeId: string) {
    this.attributeId = attributeId;
  }

  setValue(value: string) {
    this.value = value;
  }
}
