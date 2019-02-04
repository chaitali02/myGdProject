import { ActivatedRoute, Router, Params } from '@angular/router';
import { AppConfig } from './../../app.config';
import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonService } from '../../metadata/services/common.service';
import { SelectItem } from 'primeng/primeng';
import { Version } from '../../metadata/domain/version'
import { KnowledgeGraphComponent } from '../../shared/components/knowledgeGraph/knowledgeGraph.component';


@Component({
  selector: 'app-function',
  templateUrl: './function.template.html',
  styleUrls: ['./function.component.css']
})
export class FunctionComponent implements OnInit {
  selectFunctionType: any;
  selectCatogory: any;
  msgs: any[];
  selectallrow: any;
  allParamType: { label: string; value: string; }[];
  functionTableArray: any;
  category: { label: string; value: string; }[];
  allTypes: any[];
  funcType: any[];
  selectedVersion: Version;
  VersionList: SelectItem[] = [];
  allNames: SelectItem[] = [];
  breadcrumbDataFrom: any;
  showFunctionData: boolean;
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
  functionData: any;
  depends: any;
  allName: any;
  active: any;
  published: any;
  isSubmitEnable: any;
  isHomeEnable: boolean = false;
  showGraph: boolean = false;
  isGraphEnable: boolean = true;
  isEditEnable: boolean = true;
  isRefreshEnable: boolean = true;
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;

  constructor(config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService) {
    this.showFunctionData = true;
    this.isHomeEnable = false
    this.functionData = {};
    this.functionData["active"] = true

    //this.selectVersion={"version":""};
    this.breadcrumbDataFrom = [{
      "caption": "Data Preparation ",
      "routeurl": "/app/list/function"
    },
    {
      "caption": "Function",
      "routeurl": "/app/list/function"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ]
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
      else {
        this.functionData.active = "Y"
        // this.functionTableArray=[{}]
        // this.functionTableArray[0].paramInfoHolder={}
      }
      this.funcType = [{ label: "hive", value: "hive" },
      { label: "impala", value: "impala" },
      { label: "oracle", value: "oracle" },
      ]
      this.allTypes = [{ label: "file", value: "file" },
      { label: "hive", value: "hive" },
      { label: "impala", value: "impala" },
      { label: "mysql", value: "mysql" },
      { label: "oracle", value: "oracle" },
      ]
      this.category = [{ label: "math", value: "math" },
      { label: "string", value: "string" },
      { label: "date", value: "date" },
      { label: "conditional", value: "conditional" },
      { label: "aggregate", value: "aggregate" },
      ]
      this.allParamType = [
        { label: "string", value: "string" },
        { label: "function", value: "function" },]
      //this.funcType = ["hive", "impala", "oracle"];
      //this.allTypes = ["file", "hive", "impala", "mysql", "oracle"];
    })
  }

  showDagGraph(uuid, version) {
    this.isHomeEnable = true;
    this.showGraph = true;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(this.id, this.version);
    }, 1000);
  }

  getOneByUuidAndVersion() {
    this._commonService.getOneByUuidAndVersion(this.id, this.version, 'function')
      .subscribe(
        response => {
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => console.log("Error :: " + error));
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('function', this.id)
      .subscribe(
        response => {
          this.OnSuccesgetAllVersionByUuid(response)
        },
        error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response) {
    this.functionData = response;
    this.createdBy = response.createdBy.ref.name;
    this.functionData.published = response["published"] == 'Y' ? true : false
    this.functionData.active = response["active"] == 'Y' ? true : false
    this.functionData.inputFlag = response["inputReq"] == 'Y' ? true : false
    const version: Version = new Version();
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.selectedVersion = version
    var tags = [];
    if (response.tags != null) {
      for (var i = 0; i < response.tags.length; i++) {
        var tag = {};
        tag['value'] = response.tags[i];
        tag['display'] = response.tags[i];
        tags[i] = tag

      }//End For
      this.functionData.tags = tags;
    }//End If

    this.breadcrumbDataFrom[2].caption = this.functionData.name;
    this.functionTableArray = response.functionInfo;
    for (let i = 0; i < this.functionTableArray.length; i++) {
      for (let j = 0; j < this.functionTableArray[i].paramInfoHolder.length; j++) {
        if (this.functionTableArray[i].paramInfoHolder[j]["paramReq"] == 'N') {
          this.functionTableArray[i].paramInfoHolder[j]["paramReq"] = false
        }
        else {
          this.functionTableArray[i].paramInfoHolder[j]["paramReq"] = true
        }
      }
    }
  }

  OnSuccesgetAllVersionByUuid(response) {
    for (const i in response) {
      let ver = {};
      ver["label"] = response[i]['version'];
      ver["value"] = {};
      ver["value"]["label"] = response[i]['version'];
      ver["value"]["uuid"] = response[i]['uuid'];
      this.VersionList[i] = ver;
    }
  }
  onChangeActive(event) {
    if (event === true) {
      this.functionData.active = 'Y';
    }
    else {
      this.functionData.active = 'N';
    }
  }
  onChangePublish(event) {
    if (event === true) {
      this.functionData.published = 'Y';
    }
    else {
      this.functionData.published = 'N';
    }
  }
  onChangeInputFlag(event) {
    if (event === true) {
      this.functionData.inputFlag = 'Y';
    }
    else {
      this.functionData.inputFlag = 'N';
    }
  }
  onVersionChange() {
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'function')
      .subscribe(
        response => {//console.log(response)},
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => console.log("Error :: " + error));
  }
  selectAllRow() {
    this.functionTableArray.forEach(relation => {
      relation.selected = this.selectallrow;
    });
  }
  selectAllSubRow(index) {
    (this.functionTableArray[index].paramInfoHolder).forEach(paramInfoHolder => {
      paramInfoHolder.selected = this.functionTableArray[index].selectallparamInfoHolder;
    });

  }
  addJoinSubRow(index) {

    var paramInfoHolder = {}
    paramInfoHolder["paramId"] = this.functionTableArray[index].paramInfoHolder.length + 1;
    paramInfoHolder["paramReq"] = false;
    paramInfoHolder["paramType"] = this.allParamType[0];
    paramInfoHolder["paramName"] = "''";
    this.functionTableArray[index].paramInfoHolder.splice(this.functionTableArray[index].paramInfoHolder.length, 0, paramInfoHolder);
  }

  addRow() {
    if (this.functionTableArray == null) {
      this.functionTableArray = [];
    }
    var functionTable = {};
    var paramInfoHolder = [];
    functionTable["type"] = this.allTypes[0];
    var paramInfoHolders = {};
    paramInfoHolder["paramId"] = 1;
    paramInfoHolder["paramReq"] = false;
    paramInfoHolder["paramType"] = this.allParamType[0];
    paramInfoHolder["paramName"] = "''";
    paramInfoHolder.push(paramInfoHolders)
    functionTable["paramInfoHolder"] = paramInfoHolder
    functionTable["name"] = ''
    this.functionTableArray.splice(this.functionTableArray.length, 0, functionTable);

  }
  removeJoinSubRow(index) {
    this.functionTableArray[index].selectalljoinkey = false
    var newDataList = [];
    this.functionTableArray[index].checkalljoinKeyrow = false;
    this.functionTableArray[index].paramInfoHolder.forEach(selected => {
      if (!selected.selected) {
        newDataList.push(selected);
      }
    });
    if (newDataList.length > 0) {
      newDataList[0].logicalOperators = "";
    }
    this.functionTableArray[index].paramInfoHolder = newDataList;
  }
  removeRow() {
    let newDataList = [];
    this.selectallrow = false;
    this.functionTableArray.forEach(selected => {
      if (!selected.selected) {
        newDataList.push(selected);
      }
    });
    if (newDataList.length > 0) {
      newDataList[0].logicalOperator = "";
    }
    this.functionTableArray = newDataList;
  }
  submitFunction() {
    this.isSubmitEnable = true;
    var functionJson = {};
    // this.isshowmodel = true;
    // this.dataLoading = true;
    // this.iSSubmitEnable = false;
    // this.functionHasChanged = true;
    // this.myform.$dirty = false;
    var functionJson = {}
    functionJson["uuid"] = this.functionData.uuid
    functionJson["name"] = this.functionData.name
    functionJson["desc"] = this.functionData.desc
    functionJson["published"] = this.functionData.published == true ? 'Y' : "N"
    functionJson["functionInfo"] = this.functionData.functionInfo;
    functionJson["category"] = this.selectCatogory;
    functionJson["funcType"] = this.selectFunctionType;
    functionJson["inputReq"] = this.functionData.inputReq;

    var tagArray = [];
    if (this.functionData.tags != null) {
      for (var counttag = 0; counttag < this.functionData.tags.length; counttag++) {
        tagArray[counttag] = this.functionData.tags[counttag].value;

      }
    }
    functionJson['tags'] = tagArray

    var functionInfoArray = [];
    if (this.functionTableArray != null) {
      if (this.functionTableArray.length > 0) {
        for (let j = 0; j < this.functionTableArray.length; j++) {
          var functionInfo = {};
          var paramInfoHolder = [];
          functionInfo["name"] = this.functionTableArray[j].name
          //if(this.functionTableArray[j].type.value){
          functionInfo["type"] = this.functionTableArray[j].type
          //}else{
          //   functionInfo["type"] = this.functionTableArray[j].type
          // }

          if (this.functionTableArray[j].paramInfoHolder) {
            for (var i = 0; i < this.functionTableArray[j].paramInfoHolder.length; i++) {
              var paramInfoHolderDetail = {};
              paramInfoHolderDetail["paramId"] = this.functionTableArray[j].paramInfoHolder[i].paramId;
              paramInfoHolderDetail["paramName"] = this.functionTableArray[j].paramInfoHolder[i].paramName;
              paramInfoHolderDetail["paramDefVal"] = this.functionTableArray[j].paramInfoHolder[i].paramName;
              if (this.functionTableArray[j].paramInfoHolder[i]["paramReq"] == false) {
                paramInfoHolderDetail["paramReq"] = "N"
              }
              else {
                paramInfoHolderDetail["paramReq"] = "Y"
              }
              //paramInfoHolderDetail["paramReq"] = this.functionTableArray[j].paramInfoHolder[i].paramReq;
              //if(this.functionTableArray[j].paramInfoHolder[i].paramType){
              paramInfoHolderDetail["paramType"] = this.functionTableArray[j].paramInfoHolder[i].paramType;
              // }
              // else{
              //   paramInfoHolderDetail["paramType"] = this.functionTableArray[j].paramInfoHolder[i].paramType.value;
              // }
              paramInfoHolder[i] = paramInfoHolderDetail;
            }
          }
          functionInfo["paramInfoHolder"] = paramInfoHolder;
          functionInfoArray[j] = functionInfo

        }

      }
    }
    functionJson["functionInfo"] = functionInfoArray
    console.log(JSON.stringify(functionJson))
    this._commonService.submit("function", functionJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccessubmit(response) {
    this.isSubmitEnable = true;
    this.msgs = [];
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Function Submitted Successfully' });
    setTimeout(() => {
      this.goBack()
    }, 1000);
  }

  enableEdit(uuid, version) {
    this.isRefreshEnable = false;
    this.router.navigate(['app/dataPreparation/function', uuid, version, 'false']);
  }
  showview(uuid, version) {
    this.router.navigate(['app/dataPreparation/function', uuid, version, 'true']);
  }
  public goBack() {
    // this._location.back();
    this.router.navigate(['app/list/function']);
  }
  showMainPage(uuid, version) {
    this.isHomeEnable = false;
    this.showGraph = false;
  }

}
