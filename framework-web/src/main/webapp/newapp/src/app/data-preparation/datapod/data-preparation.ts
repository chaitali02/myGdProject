export class DataPreparation {
  constructor(private uuid:string, private name:string, private createdBy:any, private createdOn:any, private attributes:any, private version:any, private active:any, private published:any, private cache:any) {
    this.setUuid(uuid);
    this.setName(name);
    this.setCreatedBy(createdBy);
    this.setCreatedOn(createdOn);
    this.setAttributes(attributes);
    this.setVersion(version);
    this.setActive(active);
    this.setPublished(published);
    this.setCache(cache);
  }

  setUuid(uuid:string){
    this.uuid = uuid;
  }

  setName(name:string){
    this.name = name;
  }

  setCreatedBy(createdBy:any){
    this.createdBy = createdBy;
  }

  setCreatedOn(createdOn:any){
    this.createdOn = createdOn;
  }

  setAttributes(attributes:any){
    this.attributes = attributes;
  }

  setVersion(version:any) {
    this.version = version;
  }

  setActive(active:any) {
    this.active = active;
  }

  setPublished(published:any) {
    this.published = published;
  }

  setCache(cache:any) {
    this.cache = cache;
  }

  getUuid(){
    return this.uuid;
  }

  getName(){
    return this.name;
  }

  getCreatedBy(){
    return this.createdBy;
  }

  getCreatedOn(){
    return this.createdOn;
  }

  getAttributes(){
    return this.attributes;
  }

  getVersion() {
    return this.version;
  }

  getActive() {
    return this.active;
  }

  getPublished() {
    return this.published;
  }

  getCache() {
    return this.cache;
  }
}
