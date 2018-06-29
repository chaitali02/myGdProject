export class ApplicationResource {
  constructor(private uuid: string, private name: string) {
    this.setUuid(uuid);
    this.setName(name);
  }

  setUuid(uuid: string) {
    this.uuid = uuid;
  }

  setName(name: string) {
    this.name = name;
  }

}
