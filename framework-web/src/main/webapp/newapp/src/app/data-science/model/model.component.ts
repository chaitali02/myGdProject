import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { Location } from '@angular/common';
import { SelectItem } from 'primeng/primeng';

import { AppConfig } from '../../app.config';

import { CommonService } from '../../metadata/services/common.service';
import { ModelService } from '../../metadata/services/model.service';

import { Version } from '../../metadata/domain/version';
import { DependsOn } from './dependsOn';
import { AttributeHolder } from '../../metadata/domain/domain.attributeHolder'
import { DatasetService } from '../../metadata/services/dataset.service';

@Component({
  selector: 'app-model',
  templateUrl: './model.template.html',
})
export class ModelComponent implements OnInit {
  dependsType: any;
  IsLableSelected: boolean;
  selectallattribute: any;
  isTabelShow: boolean;
  paramtable: any[];
  paramtablecol: any;
  paramsetdata: any;
  isShowExecutionparam: boolean;
  allParameterset: any;
  sourceAlgorithm: any;
  checkboxModelexecution: boolean;
  selectedlabel: any;
  dropdownSettings: { singleSelection: boolean; text: string; selectAllText: string; unSelectAllText: string; enableSearchFilter: boolean; classes: string; maxHeight: number; disabled: boolean; };
  allAttribute: any[];
  selectAlgorithm: any;
  allAlgorithm: any[];
  sources: string[];
  createdBy: any;
  allNames: any[];
  sourcedata: any;
  version: any;
  breadcrumbDataFrom: any;
  showModel: any;
  model: any;
  tags: any;
  id: any;
  mode: any;
  uuid: any;
  active: any;
  published: any;
  depends: any;
  continueCount: any;
  progressbarWidth: any;
  isSubmit: any
  selectedVersion: Version;
  VersionList: SelectItem[] = [];
  msgs: any[];
  source: string;
  allSourceAttribute: any[];
  featuresArray: any;
  featuresTags: any[];
  featureResponse: any;
  nameResponse: any;
  labelTags: any;
  labelResponse: any;
  allSourceLabel: any;
  labelArray: any;
  scriptTypes: any;
  type: any;
  dependsOn: any;
  dependsOnName: any;
  dependsOnTypes: String[];
  getAllArray: any;
  getFromulaArray: any;
  allParamlist: any;
  label: any;
  cols: any;
  selectedRows: any;
  isParamColEnable: any;
  getParamArray: any;
  typeOfArray: any;
  isDisabled: any;
  customFlag: boolean;
  scriptCode: any;
  param: any;
  paramListInfo: any;
  modelJson: any;
  // featureInterface = new FeatureInterface();
  // featureObj: FeatureInterface[];
  // newCar: any;
  // attrinfo : any;

  constructor(config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private _location: Location, private _modelService: ModelService, private _datasetService: DatasetService) {
    this.showModel = true;
    this.model = {};
    this.customFlag = false;
    this.dependsOn = {};
    this.selectedRows = [];
    this.model["active"] = true;
    this.continueCount = 1;
    this.isSubmit = "false";
    this.getAllArray = [];
    this.IsLableSelected = false;
    this.isParamColEnable = false;
    this.isDisabled = false;
    this.progressbarWidth = 25 * this.continueCount + "%";
    this.scriptTypes = [
      { "label": "SPARK", "value": "SPARK" },
      { "label": "PYTHON", "value": "PYTHON" },
      { "label": "R", "value": "R" }
    ]
    this.dependsOnTypes = [
      // { 'label': 'formula', 'value': 'formula' },
      // { 'label': 'algorithm', 'value': 'algorithm' }
      "algorithm", "formula"
    ]
    this.dependsType = "";
    this.typeOfArray = [
      { "label": "integer", "value": "integer" },
      { "label": "string", "value": "string" },
      { "label": "double", "value": "double" }
    ]
    this.dropdownSettings = {
      singleSelection: false,
      text: "Select Attrubutes",
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      enableSearchFilter: true,
      classes: "myclass custom-class",
      maxHeight: 110,
      disabled: false
    };
    this.breadcrumbDataFrom = [{
      "caption": "Data Science",
      "routeurl": "/app/list/model"
    },
    {
      "caption": "Model",
      "routeurl": "/app/list/model"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ];
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
      if (this.mode !== undefined) {
        this.getOneByUuidAndVersion();
        this.getAllVersionByUuid();
      }
    });
  }

  changeName(index) {
    this.featuresArray[index].name = this.featuresArray[index].newCol.label;
    console.log(this.featuresArray[index].newCol);
  }

  disableMinMAxVal(index) {
    if (this.featuresArray[index].type == 'string') {
      this.featuresArray[index].minVal.disabled;
      this.featuresArray[index].maxVal.disabled;
    }
  }

  countContinue = function () {
    this.continueCount = this.continueCount + 1;
    this.progressbarWidth = 25 * this.continueCount + "%";
  }

  countBack = function () {
    this.continueCount = this.continueCount - 1;
    this.progressbarWidth = 25 * this.continueCount + "%";
  }
  public goBack() {
    this._location.back();
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('model', this.id)
      .subscribe(
      response => {
        this.OnSuccesgetAllVersionByUuid(response)
      },
      error => console.log("Error :: " + error));
  }

  OnSuccesgetAllVersionByUuid(response) {
    var temp = []
    for (const i in response) {
      let ver = {};
      ver["label"] = response[i]['version'];
      ver["value"] = {};
      ver["value"]["label"] = response[i]['version'];
      ver["value"]["uuid"] = response[i]['uuid'];
      ver["value"]["u_Id"] = response[i]['uuid'] + "_" + response[i]['version']
      temp[i] = ver;
    }
    this.VersionList = temp
  }

  getOneByUuidAndVersion() {
    this._commonService.getOneByUuidAndVersion(this.id, this.version, 'model')
      .subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response) {
    this.breadcrumbDataFrom[2].caption = response.name;
    this.model = response;
    this.uuid = response.uuid;
    const version: Version = new Version();
    version.label = response['version'];
    version.uuid = response['uuid'];
    version.u_Id = response['uuid'] + "_" + response['version'];
    this.selectedVersion = version;
    this.createdBy = response.createdBy.ref.name
    this.model.published = response["published"] == 'Y' ? true : false
    this.model.active = response["active"] == 'Y' ? true : false
    this.type = response.type;
    this.tags = response['tags'];
    //this.source=response["source"]["ref"].type
    //this.dependsOn = response.dependsOn.ref.type;
    //this.dependsOnName = response.dependsOn.ref.name;

    this.customFlag = response["customFlag"] == 'Y' ? true : false;
    if (this.customFlag == true) {
      this.getModelScript();
    }
    else {
      if (response.dependsOn != null) {
        let dependOnTemp: DependsOn = new DependsOn();
        dependOnTemp.label = response.dependsOn.ref.name;
        //  dependOnTemp.type = response["dependsOn"]["ref"]["type"];
        dependOnTemp.uuid = response.dependsOn.ref.uuid;
        this.dependsOn = dependOnTemp;

        this.dependsType = response.dependsOn.ref.type;
        console.log(JSON.stringify(this.dependsOn));
       
        if (this.dependsType == "algorithm") {
          this.getAllLatest();
        }
        else {
          this.featuresArray = response.features;
          for(const i in response.features){
            let value1Temp: DependsOn = new DependsOn();
            value1Temp.label = this.featuresArray[i].paramListInfo.paramName;
            value1Temp.id =  this.featuresArray[i].paramListInfo.paramId;
            value1Temp.uuid =  this.featuresArray[i].paramListInfo.ref.uuid;
          this.featuresArray[i]["newCol"] = value1Temp
          }

          this.getFormulaByType2();
          // this.getParamListByFormula();
        }
      }
      this.label = response.label;
      this.featuresArray = response.features;
     
      console.log(JSON.stringify(response.features));
    }
  }

  getAllLatest() {
    this._commonService.getAllLatest("algorithm").subscribe(
      response => { this.onSuccessgetAllLatest(response) },
      error => console.log('Error ::' + error)
    )
  }

  onSuccessgetAllLatest(response) {
    this.isParamColEnable = false;
    this.getAllArray = [];
    for (const i in response) {
      let getAllObj = {};
      // getAllObj["label"] = response[i]['name'];
      // getAllObj["value"] = response[i]['uuid'];
      getAllObj["label"] = response[i]['name'];
      getAllObj["value"] = {};
      getAllObj["value"]["label"] = response[i]['name'];
      getAllObj["value"]["uuid"] = response[i]['uuid'];
      // getAllObj["value"]["type"] = "algorithm"
      this.getAllArray[i] = getAllObj;
    }
  }

  onChangeDependsOnType1() {
    console.log(this.dependsType);
    if (this.dependsType == "algorithm") {
      this.getAllLatest();
    }
    else if (this.dependsType == "formula") {
      this.getFormulaByType2();

    }
  }

  getFormulaByType2() {
    this._modelService.getFormulaByType2("formula").subscribe(
      response => { this.onSuccessgetFormulaByType2(response) },
      error => console.log('Error ::' + error)
    )
  }

  onSuccessgetFormulaByType2(response) {
    this.isParamColEnable = true;
    this.getAllArray = [];
    for (const i in response) {
      let getAllObj = {};
      //getAllObj["label"] = response[i].ref.name;
      //getAllObj["value"] = response[i].ref.uuid;
      getAllObj["label"] = response[i].ref.name;
      getAllObj["value"] = {};
      getAllObj["value"]["label"] = response[i].ref.name;
      getAllObj["value"]["uuid"] = response[i].ref.uuid
      // getAllObj["value"]["type"] = response[i].ref.type
      this.getAllArray[i] = getAllObj;
      this.getParamListByFormula();
    }
  }

  onChangeDependsOn() {
    if (this.dependsOnName == "formula") {
      this.getParamListByFormula();
      this.getFormulaByType2();
    }
    this.getAllLatest();
  }

  getParamListByFormula() {
    console.log(this.dependsOn.uuid);
    this._modelService.getParamListByFormula(this.getAllArray[0]["value"]["uuid"], "paramlsit").subscribe(
      response => { this.onSuccesgetParamListByFormula(response) },
      error => console.log('Error ::' + error)
    )
  }

  onSuccesgetParamListByFormula(response) {
    this.allParamlist = response;
    this.getParamArray = [];

    for (const i in response) {
      let getParamObj = {};
      getParamObj["label"] = response[i].paramName;
      
      //getParamObj["list"]["label"] = response[i].paramName;
      getParamObj["value"] = {};
      getParamObj["value"]["label"] = response[i].paramName;
      getParamObj["value"]["id"] = response[i].paramId;
      getParamObj["value"]["uuid"] = response[i].ref.uuid;
     // getParamObj["list"]["value"]["paramId"] = response[i].paramId;
      // getParamObj["value"]["type"] = response[i].paramType;

      this.getParamArray[i] = getParamObj;
    }
    console.log(JSON.stringify(this.getParamArray));
    
    // let value1Temp: DependsOn = new DependsOn();
    // value1Temp.label = this.featuresArray[0].paramListInfo.paramName;
    // value1Temp.uuid = this.featuresArray[0].paramListInfo.paramId;

    // this.featuresArray[0]["newCol"] = this.featuresArray[0].paramListInfo.paramName;
    // console.log(JSON.stringify(this.featuresArray));
  }

  addAttribute() {
    if (this.featuresArray == null) {
      this.featuresArray = [];
    }
    let len = this.featuresArray.length + 1

    let attrinfo = {};
    attrinfo["featureId"] = len - 1;
    attrinfo["name"] = "";
    attrinfo["type"] = "";
    attrinfo["desc"] = "";
    attrinfo["minVal"] = "";
    attrinfo["maxVal"] = "";
    attrinfo["paramListInfo"] = "";

    //this.features1.splice(this.features1.length, 0,attrinfo);
    this.featuresArray.push(attrinfo);
    console.log(this.featuresArray);
  }

  changeNameCol(index) {
    console.log(index);
  }

  removeAttribute() {
    var newDataList = [];
    for (const i in this.featuresArray)
      if (this.featuresArray[i]._$visited == true) {
        newDataList.push(this.featuresArray[i]);
      }
    this.featuresArray = newDataList;
  }

  getModelScript() {
    this._modelService.getModelScript(this.uuid, this.version).subscribe(
      response => { this.onSuccessgetModelScript(response) },
      error => console.log("Error ::" + error)
    )
  }

  onSuccessgetModelScript(response) {
    this.scriptCode = response;
    console.log(JSON.stringify(response));
    console.log(JSON.stringify(this.scriptCode));
  }

  submit() {
    this.isSubmit = "true"
    this.modelJson = {};
    this.modelJson["uuid"] = this.uuid;
    this.modelJson["name"] = this.model.name;
    this.modelJson["desc"] = this.model.desc;
    let tagArray = [];
    if (this.model.tags != null) {
      for (var counttag = 0; counttag < this.model.tags.length; counttag++) {
        tagArray[counttag] = this.model.tags[counttag];
      }
    }
    this.modelJson["tags"] = tagArray;
    this.modelJson["active"] = this.model.active == true ? "Y" : "N"
    this.modelJson["published"] = this.model.published == true ? "Y" : "N"
    this.modelJson["type"] = this.type;
    if (this.model.type == "SPARK") {
      this.customFlag = false
    }
    this.modelJson["customFlag"] = this.customFlag == true ? "Y" : "N";
    if (this.customFlag == false) {
     
      let dependsOn1 = {};
      let ref = {};
      ref["type"] = this.dependsType;
      ref["uuid"] = this.dependsOn.uuid;
      ref["name"] = this.dependsOn.label;
      dependsOn1["ref"] = ref;
      this.modelJson["dependsOn"] = dependsOn1;

      this.modelJson["label"] = this.model.label;

      let featuresArray1 = [];
      for (let i = 0; i < this.featuresArray.length; i++) {
        let featureObj = {};
        featureObj["featureId"] = this.featuresArray[i].featureId;
        featureObj["name"] = this.featuresArray[i].name;
        featureObj["type"] = this.featuresArray[i].type;
        featureObj["desc"] = this.featuresArray[i].desc;
        featureObj["minVal"] = this.featuresArray[i].type == "string" ? "null" : this.featuresArray[i].minVal;
        featureObj["maxVal"] = this.featuresArray[i].type == "string" ? "null" : this.featuresArray[i].maxVal;

        if (this.dependsType == "formula") {
          // if(this.featuresArray[i].param == !null){
          let paramListInfo = {};
          let ref = {};
          ref["type"] = "paramlist";
          ref["uuid"] = this.featuresArray[i].newCol.uuid;
          ref["name"] = this.featuresArray[i].newCol.label;
          paramListInfo["ref"] = ref;
          paramListInfo["paramId"] = this.featuresArray[i].newCol.id;
          featureObj["paramListInfo"] = paramListInfo;
        }
        else {
          featureObj["paramListInfo"] = null;
        }
        featuresArray1[i] = featureObj
      }
      this.modelJson["features"] = featuresArray1;
      console.log(JSON.stringify(this.modelJson));
      this._commonService.submit("model", this.modelJson).subscribe(
        response => { this.onSuccesssubmit(response) },
        error => console.log("Error ::" + error)
      )
    }

    else if (this.customFlag == true) {
      var blob = new Blob([this.scriptCode], { type: "text/xml" });
      var fd = new FormData();
      fd.append('file', blob);
      var filetype = this.model.type == "PYTHON" ? "py" : "R"
      this._modelService.uploadFile(filetype, fd, "script").subscribe(
        response => { this.onSuccessUpload(response) },
        error => console.log("Error ::" + error)
      )
    }
  }

  onSuccessUpload(response) {
    console.log(response);
    let responseBody = response._body
    this.modelJson["type"] = this.type
    this.modelJson["scriptName"] = responseBody;
    let result = responseBody.split("_")
    this.modelJson["uuid"] = result[0]
    this.modelJson["version"] = result[1].split(".")[0]
    console.log(JSON.stringify(this.modelJson));
    this._commonService.submit("model", this.modelJson).subscribe(
      response => { this.onSuccesssubmit(response) },
      error => console.log("Error ::" + error)
    )
  }

  onSuccesssubmit(response) {
    console.log(response);
    this.msgs = [];
    // this.isSubmit = "true"
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Model Saved and Submited Successfully' });
    setTimeout(() => {
      this.goBack()
    }, 1000);
  }


  // submit() {
  //   this.isSubmit = "true"
  //   let modelJson = {};
  //   modelJson["uuid"] = this.model.uuid;
  //   modelJson["name"] = this.model.name;
  //   modelJson["desc"] = this.model.desc;
  //   let tagArray = [];
  //   if (this.model.tags != null) {
  //     for (var counttag = 0; counttag < this.model.tags.length; counttag++) {
  //       tagArray[counttag] = this.model.tags[counttag];
  //     }
  //   }
  //   modelJson["tags"] = tagArray;
  //   modelJson["active"] = this.model.active == true ? 'Y' : "N"
  //   modelJson["published"] = this.model.published == true ? 'Y' : "N"

  //   this._commonService.submit("model", modelJson).subscribe(
  //     response => { this.OnSuccessubmit(response) },
  //     error => console.log('Error :: ' + error)
  //   )
  // }

  // OnSuccessubmit(response) {
  //   if (this.checkboxModelexecution == true) {
  //     this._commonService.getOneById("model", response).subscribe(
  //       response => { this.modelExecute(response); },
  //       error => console.log('Error :: ' + error)
  //     )
  //   } //End if
  //   else {
  //     this.msgs = [];
  //     this.isSubmit = "true"
  //     this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Rule Saved Successfully' });
  //     setTimeout(() => {
  //       this.goBack()
  //     }, 1000);
  //   }
  // }

  // modelExecute(modeldetail) {
  //   let newDataList = [];
  //   this.selectallattribute = false;
  //   let execParams = {}
  //   if (this.paramtable) {
  //     this.paramtable.forEach(selected => {
  //       if (selected.selected) {
  //         newDataList.push(selected);
  //       }
  //     });

  //     let paramInfoArray = [];

  //     if (this.paramtable && newDataList.length > 0) {
  //       let ref = {}
  //       ref["uuid"] = this.paramsetdata.uuid;
  //       ref["version"] = this.paramsetdata.version;
  //       for (var i = 0; i < newDataList.length; i++) {
  //         var paraminfo = {};
  //         paraminfo["paramSetId"] = newDataList[i].paramSetId;
  //         paraminfo["ref"] = ref;
  //         paramInfoArray[i] = paraminfo;
  //       }
  //     }

  //     if (paramInfoArray.length > 0) {
  //       execParams["paramInfo"] = paramInfoArray;
  //     }
  //     else {
  //       execParams = null
  //     }
  //   }
  //   console.log(JSON.stringify(execParams));
  //   this._modelService.getExecuteModelWithBody(modeldetail["uuid"], modeldetail["version"], execParams).subscribe(
  //     response => { this.onSuccessExecute(response) },
  //     error => console.log('Error :: ' + error)
  //   )
  // }

  // onSuccessExecute(response) {
  //   this.msgs = [];
  //   this.isSubmit = "true"
  //   this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Model Saved and Submited Successfully' });
  //   setTimeout(() => {
  //     this.goBack()
  //   }, 1000);
  // }

}


