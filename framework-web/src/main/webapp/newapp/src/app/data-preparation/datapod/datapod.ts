export class datasource {
  constructor(public ref?: ref){}
}

export class ref {
  constructor(public type: string,
              public uuid: string){}
}

export class Datapod {
  constructor(public uuid: string,
              public name: string,
              public desc: any,
              public active: any,
              public published: any,
              public cache: any,
              public tags: any,
              public attributes: any,
              public datasource?: datasource) {}
}
