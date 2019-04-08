import { DropDownIO } from './../../../metadata/domainIO/domain.dropDownIO';
import { MetaIdentifierHolder } from './../../../metadata/domain/domain.metaIdentifierHolder';

import { ActivatedRoute, Router, Params } from '@angular/router';

import { Component, OnInit, ViewChild } from '@angular/core';
import { SelectItem } from 'primeng/primeng';
import { AppConfig } from '../../../app.config';
import { Version } from '../../version';
import { CommonService } from '../../../metadata/services/common.service';
import { DependsOn } from '../../../data-science/dependsOn';
import { ParamlistService } from '../../../metadata/services/paramlist.service';
import { Location, DatePipe } from '@angular/common';
import { KnowledgeGraphComponent } from '../knowledgeGraph/knowledgeGraph.component'
import { RoutesParam } from './../../../metadata/domain/domain.routeParams';
import { MetaType } from './../../../metadata/enums/metaType';
import { ParamList } from './../../../metadata/domain/domain.paramList';
import { AppHelper } from './../../../app.helper';
import { ParamIO } from './../../../metadata/domainIO/domain.paramIO';
import { MetaIdentifierHolderIO } from './../../../metadata/domainIO/domain.metaIdentifierHolderIO';
import { Param } from './../../../metadata/domain/domain.param';
import { MetaIdentifier } from './../../../metadata/domain/domain.metaIdentifier';
import { AttributeIO } from './../../../metadata/domainIO/domain.attributeIO';

@Component({
  selector: 'app-paramlist',
  templateUrl: './paramlist.component.html',
  styleUrls: []
})
export class ParamlistComponent implements OnInit {
  isUseTemlateText: boolean;
  functnListOptions: any[];
  isHomeEnable: boolean;
  parentType: any;
  allTemplateList: any[];
  isTemplateInfoRequired: boolean = false;
  selectedTemplate: {};
  isUseTemplate: boolean = false;
  isTableDisable: boolean = false;
  defaultDistValue: any;
  distributionListOptions: any[];
  showParamlist: any;
  versions: any[];
  tags: any;
  desc: any;
  createdOn: any;
  createdBy: any;
  name: any;
  id: any;
  mode: any;
  version: any;
  uuid: any;
  VersionList: SelectItem[] = [];
  selectedVersion: Version
  breadcrumbDataFrom: any;
  paramlist: any;
  paramId: any;
  paramsArrays: any;
  loop: any;
  type: any;
  value: any;
  types: any;
  selectAllAttributeRow: any;
  msgs: any;
  isSubmitEnable: any;
  allDistribution: any;
  typeSimple: any;
  params: any;
  ref: any;
  paramtableArray: any;
  templateFlg: any;
  template: any;
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;
  isEditInprogess: boolean = false;
  isEditError: boolean = false;
  isEdit: boolean = false;
  isversionEnable: boolean;
  isAdd: boolean;
  showForm: boolean = true;
  showDivGraph: boolean;
  published: boolean;
  locked: boolean;
  active: boolean;
  metaType = MetaType;
  isGraphInprogess: boolean;
  isGraphError: boolean;

  constructor(private datePipe: DatePipe, private _location: Location, config: AppConfig,
    private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService,
    private _paramlistService: ParamlistService, public appHelper: AppHelper) {

    this.showParamlist = true;
    this.paramlist = new ParamList();
    this.isHomeEnable = false
    this.active = true;
    this.paramlist.templateFlg = true;
    this.template = { uuid: "", label: "" };
    this.isSubmitEnable = true;
    this.paramtableArray = null;
    this.types = [{ "value": "string", "label": "string" },
    { "value": "double", "label": "double" },
    { "value": "date", "label": "date" },
    { "value": "integer", "label": "integer" },
    { "value": "decimal", "label": "decimal" },
    { "value": "attribute", "label": "attribute" },
    { "value": "attributes", "label": "attribute[s]" },
    { "value": "distribution", "label": "distribution" },
    { "value": "datapod", "label": "datapod" },
    { "value": "function", "label": "function" },
    { "value": "list", "label": "list" },
    { "value": "array", "label": "array" }];

    this.typeSimple = ["string", "double", "integer", "list"];

    this.isEditInprogess = false;
    // this.isEditError = false;
    this.active = true;
    this.locked = false;
    this.published = false;
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      let param = <RoutesParam>params;
      this.id = param.id;
      this.version = param.version;
      this.mode = param.mode;
      this.parentType = param.parentType;
    });
    if (this.parentType == this.metaType.RULE) {
      this.breadcrumbDataFrom = [
        {
          "caption": "BusinessRules",
          "routeurl": "/app/list/rule/paramlist"
        },
        {
          "caption": "Parameter List",
          "routeurl": "/app/list/rule/paramlist"
        },
        {
          "caption": "",
          "routeurl": null
        }]
    } else if (this.parentType == this.metaType.MODEL) {
      this.breadcrumbDataFrom = [
        {
          "caption": "DataScience",
          "routeurl": "/app/list/model/paramlist"
        },
        {
          "caption": "Parameter List",
          "routeurl": "/app/list/model/paramlist"
        },
        {
          "caption": "",
          "routeurl": null
        }]
    }

    if (this.mode !== undefined) {
      this.getOneByUuidAndVersion(this.id, this.version);
      this.getAllVersionByUuid();
    }
    else {
      this.getAllLatest1();
      this.getAllLatest();
    };

    this.setMode(this.mode);
  }

  setMode(mode: any) {
    if (mode == 'true') {
      this.isEdit = false;
      this.isversionEnable = false;
      this.isAdd = false;
    } else if (mode == 'false') {
      this.isEdit = true;
      this.isversionEnable = true;
      this.isAdd = false;
    } else {
      this.isAdd = true;
      this.isEdit = false;
    }
  }
  enableEdit(uuid, version) {
    if (this.parentType == this.metaType.RULE) {
      this.router.navigate(['app/businessRules/paramlist', this.parentType, uuid, version, 'false']);
      this.isEdit = true;
    }
    else
      this.router.navigate(['app/dataScience/paramlist', this.parentType, uuid, version, 'false']);
  }

  showMainPage() {
    this.isHomeEnable = false;
    this.showDivGraph = false;
    this.showForm = true;
  }

  showGraph(uuid, version) {
    this.isHomeEnable = true;
    this.showDivGraph = true;
    this.showForm = false;
    this.isGraphInprogess = true;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(uuid, version);
      this.isGraphInprogess = this.d_KnowledgeGraphComponent.isInprogess;
      this.isGraphError = this.d_KnowledgeGraphComponent.isError;
    }, 1000);
  }

  onChangeIsTemplate(event, flag?) {
    if (event === false) {
      this.paramlist.templateFlg = false;

      this._paramlistService.getAllLatestParamListByTemplate('Y', this.metaType.PARAMLIST, "")
        .subscribe(response => { this.onSuccessgetAllLatestParamListByTemplate(response, flag) },
          error => console.log("Error :: " + error));
    }
    else {
      this.paramlist.templateFlg = true;
      this.template = {};
    }
  }

  onSuccessgetAllLatestParamListByTemplate(response, flag?) {
    console.log(response)
    var temp = []
    for (const i in response) {
      let ver = new AttributeIO()
      ver.label = response[i].name;
      ver.value = {label: "", uuid: "", version: "", params: ""};
      ver.value.label = response[i].name;
      ver.value.uuid = response[i].uuid;
      ver.value.version = response[i].version;
      ver.value.params = response[i].params;
      temp[i] = ver;
    }
    this.allTemplateList = temp;
    console.log(JSON.stringify(this.template.params));
    if (flag) {

    }
    else {
      this.changeParamValue()
    }

  }
  changeParamValue() {
    var arrayTemp = [];
    for (const i in this.template.params) {
      let paramtableObj = new ParamIO();
      paramtableObj.paramName = this.template.params[i].paramName;
      paramtableObj.paramType = this.template.params[i].paramType;
      //paramtableObj["paramValue"] = this.template.params[i].paramValue;

      if (this.typeSimple.indexOf(this.template.params[i].paramType) != -1) {
        paramtableObj.paramValue = this.template.params[i].paramValue.value;
      }
      else if (this.template.params[i].paramType == this.metaType.DISTRIBUTION) {
        let value1Temp: DependsOn = new DependsOn();
        value1Temp.label = this.template.params[i].paramValue.ref.name;
        value1Temp.uuid = this.template.params[i].paramValue.ref.uuid;

        paramtableObj.paramValue = value1Temp;
      }
      else if (this.template.params[i].paramValue == null) {
        paramtableObj.paramValue = "";
      }
      arrayTemp[i] = paramtableObj;
    }
    this.paramtableArray = arrayTemp;
  }
  onChangeTemplate() {
    // this.paramlist.templateFlg = 'Y';

    this._paramlistService.getAllLatestParamListByTemplate('Y', this.metaType.PARAMLIST, "")
      .subscribe(response => { this.onSuccessgetAllLatestParamListByTemplate(response) },
        error => console.log("Error :: " + error));
  }

  getOneByUuidAndVersion(id, version) {
    this._commonService.getOneByUuidAndVersion(this.id, this.version, this.metaType.PARAMLIST)
      .subscribe(
        response => {
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response) {
    this.paramlist = response;

    const version: Version = new Version();
    this.uuid = response.uuid;
    version.label = response.version;
    version.uuid = response.uuid;
    this.selectedVersion = version;

    this.createdBy = this.paramlist.createdBy.ref.name;

    // this.published = response.published;
    // if (this.published === 'Y') {
    //   this.published = true;
    // }
    // else {
    //   this.published = false;
    // }

    // this.active = response.active;
    // if (this.active === 'Y') {
    //   this.active = true;
    // }
    // else {
    //   this.active = false;
    // }

    this.paramlist.templateFlg = this.appHelper.convertStringToBoolean(response.templateFlg);
    if (this.paramlist.templateFlg == false) {
      this.template.uuid = response.templateInfo.ref.uuid;
      this.template.label = response.templateInfo.ref.name;
      this.onChangeIsTemplate(false, true)
    }
    this.paramlist.parentType = response.paramListType;
    this.parentType = this.paramlist.parentType;

    // var tags = [];
    // if (response.tags != null) {
    //   for (var i = 0; i < response.tags.length; i++) {
    //     var tag = {};
    //     tag['value'] = response.tags[i];
    //     tag['display'] = response.tags[i];
    //     tags[i] = tag

    //   }//End For
    //   this.tags = tags;
    // }//End If

    this.published = this.appHelper.convertStringToBoolean(response.published)
    this.active = this.appHelper.convertStringToBoolean(response.active)
    this.locked = this.appHelper.convertStringToBoolean(response.locked)
    this.breadcrumbDataFrom[2].caption = response.name;

    var arrayTemp = [];
    for (const i in response.params) {
      let paramtableObj = new ParamIO();
      paramtableObj.paramName = response.params[i].paramName;
      paramtableObj.paramType = response.params[i].paramType;
      paramtableObj.paramDispName = response.params[i].paramDispName;
      paramtableObj.paramDesc = response.params[i].paramDesc;

      if (this.typeSimple.indexOf(response.params[i].paramType) != -1) {
        paramtableObj.paramValue = response.params[i].paramValue.value;
      }
      else if (response.params[i].paramType == this.metaType.DISTRIBUTION) {
        let value1Temp = new MetaIdentifierHolderIO();
        value1Temp.label = response.params[i].paramValue.ref.name;
        value1Temp.uuid = response.params[i].paramValue.ref.uuid;

        paramtableObj.paramValue = value1Temp;
      }
      else if (response.params[i].paramType == this.metaType.FUNCTION) {
        let value1Temp = new MetaIdentifierHolderIO();
        value1Temp.label = response.params[i].paramValue.ref.name;
        value1Temp.uuid = response.params[i].paramValue.ref.uuid;

        paramtableObj.paramValue = value1Temp;
      }
      else if (response.params[i].paramType == "array") {
        var temp = response.params[i].paramValue.value.split(",");
        // let tempParamArray = []
        // for (let i = 0; i < temp.length; i++) {
        //   let tempParam = { value: "", display: "" }
        //   tempParam.value = temp[i];
        //   tempParam.display = temp[i];
        //   tempParamArray[i] = tempParam
        // }
        paramtableObj.paramValue = temp;
      }
      else if (response.params[i].paramType == "date") {
        let tempDate = new Date(response.params[i].paramValue.value)
        paramtableObj.paramValue = tempDate;
      }

      else if (response.params[i].paramValue == null) {
        paramtableObj.paramValue = "";
      }
      arrayTemp[i] = paramtableObj;
    }
    this.paramtableArray = arrayTemp;

    this.getAllLatest1();
    this.getAllLatest();
    this.checkIsTemplateUsed()
  }
  checkIsTemplateUsed() {
    this._commonService.getParamListChilds(this.id, this.version, this.metaType.PARAMLIST).subscribe(
      response => {
        if (response.length > 0) {
          //$scope.isTableDisable=true;
          this.isUseTemlateText = true;
        } else {
          this.isUseTemlateText = false;
        }
      },
      error => console.log('Error :: ' + error)
    )
  }
  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid(this.metaType.PARAMLIST, this.id).subscribe(
      response => { this.OnSuccesgetAllVersionByUuid(response) },
      error => console.log('Error :: ' + error)
    )
  }

  OnSuccesgetAllVersionByUuid(response) {
    var temp = []
    for (const i in response) {
      let ver = new DropDownIO();
      ver.label = response[i].version;
      ver.value = {label: "", uuid: ""};
      ver.value.label = response[i].version;
      ver.value.uuid = response[i].uuid;
      //allName["uuid"]=response[i]['uuid']/*  */
      temp[i] = ver;
    }
    this.VersionList = temp
  }

  getAllLatest1() {
    this._commonService.getAllLatest(this.metaType.DISTRIBUTION).subscribe(
      response => { this.onSuccessgetAllLatest1(response) },
      error => console.log("Error ::" + error)
    )
  }

  onSuccessgetAllLatest1(response) {
    let distributionListOptions = [];

    for (const i in response) {
      let distributionObj = new DropDownIO();
      distributionObj.label = response[i].name;
      distributionObj.value = { label: "", uuid: "", u_Id: "" };
      distributionObj.value.label = response[i].name;
      distributionObj.value.uuid = response[i].uuid;
      distributionListOptions[i] = distributionObj;
    }
    this.distributionListOptions = distributionListOptions;
    console.log(this.distributionListOptions);
  }
  getAllLatest() {
    this._commonService.getAllLatest(this.metaType.FUNCTION).subscribe(
      response => { this.onSuccessgetAllLatest(response) },
      error => console.log("Error ::" + error)
    )
  }

  onSuccessgetAllLatest(response) {
    let functnListOptions = [];

    for (const i in response) {
      let distributionObj = new DropDownIO();
      distributionObj.label = response[i].name;
      distributionObj.value = { label: "", uuid: "", u_Id: "" };
      distributionObj.value.label = response[i].name;
      distributionObj.value.uuid = response[i].uuid;
      functnListOptions[i] = distributionObj;
    }
    this.functnListOptions = functnListOptions;
    console.log(this.distributionListOptions);
  }
  // onChangeActive(event) {
  //   if (event === true) {
  //     this.paramlist.active = 'Y';
  //   }
  //   else {
  //     this.paramlist.active = 'N';
  //   }
  // }

  // onChangePublished(event) {
  //   if (event === true) {
  //     this.paramlist.published = 'Y';
  //   }
  //   else {
  //     this.paramlist.published = 'N';
  //   }
  // }

  addAttribute() {
    if (this.paramtableArray == null) {
      this.paramtableArray = [];
    }
    let len = this.paramtableArray.length + 1
    let attrinfo = new AttributeIO();
    attrinfo.name = "";
    attrinfo.id = len - 1;
    attrinfo.type = "";
    attrinfo.value = null;
    this.paramtableArray.splice(this.paramtableArray.length, 0, attrinfo);
  }

  removeAttribute() {
    var newDataList = [];
    this.selectAllAttributeRow = false
    this.paramtableArray.forEach(selected => {
      if (!selected.selected) {
        newDataList.push(selected);
      }
    });
    this.paramtableArray = newDataList;
    console.log(JSON.stringify(this.paramtableArray))
  }

  checkAllAttributeRow() {
    if (!this.selectAllAttributeRow) {
      this.selectAllAttributeRow = true;
    }
    else {
      this.selectAllAttributeRow = false;
    }
    this.paramtableArray.forEach(attribute => {
      attribute.selected = this.selectAllAttributeRow;
    });
  }

  onVersionChange() {
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, this.metaType.PARAMLIST)
      .subscribe(
        response => {
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => console.log("Error :: " + error));
  }

  onChangeName() {
    this.breadcrumbDataFrom[2].caption = this.paramlist.name;
  }

  submitParamlist() {
    this.isSubmitEnable = true;
    let paramlistJson = new ParamList();
    paramlistJson.uuid = this.paramlist.uuid
    paramlistJson.name = this.paramlist.name
    // var tagArray = [];
    // if (this.tags != null) {
    //   for (var counttag = 0; counttag < this.tags.length; counttag++) {
    //     tagArray[counttag] = this.tags[counttag].value;
    //   }
    // }
    paramlistJson.tags = this.paramlist.tags
    paramlistJson.desc = this.paramlist.desc
    paramlistJson.active = this.appHelper.convertBooleanToString(this.active);
    paramlistJson.published = this.appHelper.convertBooleanToString(this.published);
    paramlistJson.locked = this.appHelper.convertBooleanToString(this.locked);
    paramlistJson.templateFlg = this.appHelper.convertBooleanToString(this.paramlist.templateFlg);
    let templateInfo = new MetaIdentifierHolder();
    if (this.paramlist.templateFlg == false) {
      let templateInfoRef = new MetaIdentifier();
      templateInfoRef.type = this.metaType.PARAMLIST;
      templateInfoRef.uuid = this.template.uuid;
      templateInfo.ref = templateInfoRef;
    } else {
      templateInfo = null;
    }
    paramlistJson.templateInfo = templateInfo;

    if (this.parentType) {
      paramlistJson.paramListType = this.parentType;
    }

    var paramInfoArray = [];
    for (var i = 0; i < this.paramtableArray.length; i++) {
      var attributemap = new Param();
      attributemap.paramId = i;
      attributemap.paramName = this.paramtableArray[i].paramName
      attributemap.paramType = this.paramtableArray[i].paramType
      attributemap.paramDispName = this.paramtableArray[i].paramDispName
      attributemap.paramDesc = this.paramtableArray[i].paramDesc

      let paramValue = new MetaIdentifierHolder();
      if (this.typeSimple.indexOf(this.paramtableArray[i].paramType) != -1) {
        let paramRef = new MetaIdentifier();
        paramRef.type = this.metaType.SIMPLE
        paramValue.ref = paramRef;
        paramValue.value = this.paramtableArray[i].paramValue;
        attributemap.paramValue = paramValue;
        paramInfoArray[i] = attributemap;
      }
      else if (this.paramtableArray[i].paramType == this.metaType.DISTRIBUTION) {
        var paramRef = new MetaIdentifier();
        paramRef.type = this.paramtableArray[i].paramType;
        if (this.paramtableArray[i].paramValue != null) {
          paramRef.uuid = this.paramtableArray[i].paramValue.uuid;
        }
        paramValue.ref = paramRef;
        attributemap.paramValue = paramValue;
        paramInfoArray[i] = attributemap;
      }
      else if (this.paramtableArray[i].paramType == this.metaType.FUNCTION) {
        var paramRef = new MetaIdentifier();
        paramRef.type = this.paramtableArray[i].paramType;
        if (this.paramtableArray[i].paramValue != null) {
          paramRef.uuid = this.paramtableArray[i].paramValue.uuid;
        }
        paramValue.ref = paramRef;
        attributemap.paramValue = paramValue;
        paramInfoArray[i] = attributemap;
      }
      else if (this.paramtableArray[i].paramType == 'array') {
        //         var paramArray = [];
        //         if (this.paramtableArray[i].paramValue != null) {
        //           for (var counttag = 0; counttag < this.paramtableArray[i].paramValue.length; counttag++) {
        //             paramArray[counttag] = this.paramtableArray[i].paramValue[counttag].value;
        //           }
        //         }   
        // 
        //         let tagas = this.paramtableArray[i].paramValue.toString();;
        // paramlistJson['tags'] = paramArray
        var paramRef = new MetaIdentifier();
        paramRef.type = this.metaType.SIMPLE;
        paramValue.ref = paramRef;
        paramValue.value = this.paramtableArray[i].paramValue.toString();;
        attributemap.paramValue = paramValue
        paramInfoArray[i] = attributemap;
      }
      else if (this.paramtableArray[i].paramType == 'date') {
        var paramRef = new MetaIdentifier();
        paramRef.type = this.metaType.SIMPLE;
        paramValue.ref = paramRef;

        let tempDate = new Date(this.paramtableArray[i].paramValue)
        let date = this.datePipe.transform(tempDate, "MM/dd/yyyy");
        paramValue.value = this.paramtableArray[i].paramValue;
        attributemap.paramValue = paramValue
        paramInfoArray[i] = attributemap;
      }
      else {
        paramValue = null;
        attributemap.paramValue = paramValue
        paramInfoArray[i] = attributemap;
      }
    }
    paramlistJson.params = paramInfoArray;
    console.log(JSON.stringify(paramlistJson))
    this._commonService.submit(this.metaType.PARAMLIST, paramlistJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }

  OnSuccessubmit(response) {
    this.isSubmitEnable = true;
    this.msgs = [];
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Paramlist Submitted Successfully' });
    setTimeout(() => {
      this.goBack()
    }, 1000);
  }

  public goBack() {
    this.router.navigate(['app/list/' + this.parentType + '/paramlist']);
  }


  onChangeType(index) {
    if (this.paramtableArray[index].paramType == "array") {
      this.paramtableArray[index].paramValue = [];
    }
    if (this.paramtableArray[index].paramType == "date") {
      this.paramtableArray[index].paramValue = "";
    }
  }
}
