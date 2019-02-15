export class DataDiscovery {
  constructor(private title:string, private uuid:string, private type:string, private version:string, private dataSource:string, private numRows:number, private lastUpdatedOn:any , private classColor:any) {
    this.setTitle(title);
    this.setUuid(uuid);
    this.setType(type);
    this.setVersion(version);
    this.setDataSource(dataSource);
    this.setnumRows(numRows);
    this.setlastUpdatedOn(lastUpdatedOn);
    this.setclassColor(classColor);
  }

  setTitle(title:string){
    this.title = title;
  }

  setUuid(uuid:string){
    this.uuid = uuid;
  }

  setType(type:string){
    this.type = type;
  }

  setVersion(version:string){
    this.version = version;
  }

  setDataSource(dataSource:string){
    this.dataSource = dataSource;
  }

  setnumRows(numRows:number){
    this.numRows = numRows;
  }

  setlastUpdatedOn(lastUpdatedOn:any) {
    this.lastUpdatedOn = lastUpdatedOn;
  }

  setclassColor(classColor:any){
    this.classColor = classColor;
  }

  getTitle(){
    return this.title;
  }

  getUuid(){
    return this.uuid;
  }

  getType(){
    return this.type;
  }

  getVersion(){
    return this.version;
  }

  getDataSource(){
    return this.dataSource;
  }

  getnumRows(){
    return this.numRows;
  }

  getlastUpdatedOn() {
    return this.lastUpdatedOn;
  }

  getclassColor(){
    return this.classColor;
  }
}
