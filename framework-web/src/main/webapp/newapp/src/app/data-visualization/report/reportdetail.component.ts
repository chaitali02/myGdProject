
import { Component } from '@angular/core';
import { Location } from '@angular/common';
import { Router, ActivatedRoute, Params } from '@angular/router';

import { DashboardService } from '../../metadata/services/dashboard.service'
import { CommonService } from '../../metadata/services/common.service';

import { Version } from '../../metadata/domain/version'
import { DependsOn } from '../dependsOn'
import { AttributeHolder } from '../../metadata/domain/domain.attributeHolder'
import { DatasetService } from '../../metadata/services/dataset.service';
import { SelectItem } from 'primeng/primeng';

@Component({
    selector: 'app-report-detail',
    styleUrls: [],
    templateUrl: './reportdetail.template.html'
})

export class ReportDetailComponent {
    IsEmpty: string;
    IsDisable: string;
    valuelist: any;
    grouplist: any;
    keylist: any;
    allAttributes: any[];
    intervalId: any;
    isSubmitEnable: any;
    msgs: any;
    sources: string[];
    allNames: any[];
    sourcedata: DependsOn;
    source: any;
    breadcrumbDataFrom: { "caption": string; "routeurl": string; }[];
    selectedVersion: Version;
    VersionList: any[];
    reportdata: any;
    mode: any;
    version: any;
    uuid: any;
    //limit: any;
    filterAttributeTags: any;
    dropdownList: any[];
    dropdownSettings: { singleSelection: boolean; text: string; selectAllText: string; unSelectAllText: string; enableSearchFilter: boolean; classes: string; maxHeight: number; disabled: boolean; };
    privResponse: any;
    filterInfoArray: any[] = [];
    filterInfoTags: any;
    dropdownSettingsPipeline: any;
    headerAlignes: string[];
    headerAlign: any;
    footerAlignes: string[];
    footerAlign: any;
    selectAllAttributeRow: any;
    //dataset: any;
    attributeTableArray: any[];
    allMapExpression: any;
    allMapFormula: any;
    ruleLoadFunction: any;
    sourceAttributeTypes: { "value": string; "label": string; }[];
    allMapSourceAttribute: SelectItem[] = [];
    lhsdatapodattributefilter: any;

    constructor(private activatedRoute: ActivatedRoute, public router: Router, private _dashboardService: DashboardService, private _commonService: CommonService, private _location: Location, private _datasetService: DatasetService) {
        this.reportdata = {};
        this.IsDisable = "false";
        this.isSubmitEnable = true;
        this.reportdata["active"] = true;
        this.keylist = [];
        this.valuelist = [];
        this.grouplist = [];
        this.filterInfoTags = null;
        this.breadcrumbDataFrom = [
            {
                "caption": "Data Visualization",
                "routeurl": "/app/list/report"
            },

            {
                "caption": "Report",
                "routeurl": "/app/list/report"
            },
            {
                "caption": "",
                "routeurl": null
            }
        ]

        this.dropdownSettingsPipeline = {
            singleSelection: false,
            selectAllText: 'Select All',
            unSelectAllText: 'UnSelect All',
            enableSearchFilter: true,
            disabled: false
        };

        this.sources = ["datapod", "relation", "dataset"];
        this.source = this.sources[0];

        this.headerAlignes = ["left", "center", "right"];
        this.headerAlign = this.headerAlignes[0];

        this.footerAlignes = ["left", "center", "right"];
        this.footerAlign = this.footerAlignes[0];

        this.dropdownSettings = {
            singleSelection: false,
            text: "Select Attrubutes",
            selectAllText: 'Select All',
            unSelectAllText: 'UnSelect All',
            enableSearchFilter: true,
            classes: "myclass custom-class",
            maxHeight: 90,
            disabled: false
        };

        this.attributeTableArray = [];

        this.sourceAttributeTypes = [
            { "value": "function", "label": "function" },
            { "value": "string", "label": "string" },
            { "value": "datapod", "label": "attribute" },
            { "value": "expression", "label": "expression" },
            { "value": "formula", "label": "formula" },
            { "value": "paramlist", "label": "paramlist" }
        ]
    }

    ngOnInit() {
        this.activatedRoute.params.subscribe((params: Params) => {
            this.uuid = params['id'];
            this.version = params['version'];
            this.mode = params['mode'];
            this.IsDisable = this.mode
            if (this.mode !== undefined) {
                this.getAllVersionByUuid();
                this.getOneByUuidAndVersion();
                this.dropdownSettings.disabled = this.mode == "false" ? false : true
                this.dropdownSettingsPipeline.disabled = this.mode == "false" ? false : true
            }
            else {
                this.getAllLatest(true);
            }
            this.getAllLatestDatapod();
        });
    }
    getAllLatestDatapod() {
        this._commonService.getAllLatest('datapod')
            .subscribe(
                response => {
                    this.onSuccessgetAllLatestDatapod(response)
                },
                error => console.log("Error ::" + error));
    }

    onSuccessgetAllLatestDatapod(response) {
        this.privResponse = response
        this.filterInfoArray = [];

        for (const i in response) {
            let filterRef = {};
            filterRef["id"] = response[i]['uuid'];
            filterRef["itemName"] = response[i]['name'];
            filterRef["version"] = response[i]['version'];

            this.filterInfoArray[i] = filterRef;
        }
    }
    onVersionChange(version) {
        this.version = version;
        this.getOneByUuidAndVersion();
    }

    public goBack() {
        //this._location.back();
        this.router.navigate(['app/list/report'])

    }

    getAllLatest(IsDefault) {
        this._commonService.getAllLatest(this.source).subscribe(
            response => { this.OnSuccesgetAllLatest(response, IsDefault) },
            error => console.log('Error :: ' + error)
        )
    }
    changeType() {
        this.keylist = [];
        this.valuelist = [];
        this.grouplist = [];
        this.getAllAttributeBySource();
    }
    selectType() {
        // this.keylist = [];
        // this.valuelist = [];
        // this.grouplist = [];
        this.getAllLatest(true);
    }

    OnSuccesgetAllLatest(response1, IsDefault) {
        let temp = []
        if (IsDefault == true) {
            let dependOnTemp: DependsOn = new DependsOn();
            dependOnTemp.label = response1[0]["name"];
            dependOnTemp.uuid = response1[0]["uuid"];
            this.sourcedata = dependOnTemp;


        }
        for (const n in response1) {
            let allname = {};
            allname["label"] = response1[n]['name'];
            allname["value"] = {};
            allname["value"]["label"] = response1[n]['name'];
            allname["value"]["uuid"] = response1[n]['uuid'];
            temp[n] = allname;
        }
        this.allNames = temp
        this.getAllAttributeBySource();


    }

    getAllAttributeBySource() {
        this._commonService.getAllAttributeBySource(this.sourcedata.uuid, this.source).subscribe(
            response => { this.OnSuccesgetAllAttributeBySource(response) },
            error => console.log('Error :: ' + this.sourcedata.uuid)
        )
    }

    OnSuccesgetAllAttributeBySource(response) {
        this.lhsdatapodattributefilter = response;
        let temp = []
        for (const n in response) {
            let allname = {};
            allname["label"] = response[n]['dname'];
            allname["value"] = {};
            allname["value"]["label"] = response[n]['dname'];
            allname["value"]["id"] = response[n]['id'];
            temp[n] = allname;
        }
        this.allMapSourceAttribute = temp
    }

    getAllVersionByUuid() {
        this._commonService.getAllVersionByUuid('report', this.uuid)
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
        this._commonService.getOneByUuidAndVersion(this.uuid, this.version, "report")
            .subscribe(
                response => { this.onSuccessGetOneByUuidAndVersion(response) },
                error => console.log("Error :: " + error));
    }
    onSuccessGetOneByUuidAndVersion(response) {

        this.breadcrumbDataFrom[2].caption = response.name;
        this.reportdata = response;

        this.uuid = response.uuid;

        const version: Version = new Version();
        version.label = response['version'];
        version.uuid = response['uuid'];
        version.u_Id = response['uuid'] + "_" + response['version'];
        this.selectedVersion = version;

        this.reportdata.published = response["published"] == 'Y' ? true : false
        this.reportdata.active = response["active"] == 'Y' ? true : false;

        var tags = [];
        if (response.tags != null) {
            for (var i = 0; i < response.tags.length; i++) {
                var tag = {};
                tag['value'] = response.tags[i];
                tag['display'] = response.tags[i];
                tags[i] = tag
            }
            this.reportdata.tags = tags;
        }

        this.source = response["dependsOn"]["ref"].type

        let dependOnTemp: DependsOn = new DependsOn();
        dependOnTemp.label = response["dependsOn"]["ref"]["name"];
        dependOnTemp.uuid = response["dependsOn"]["ref"]["uuid"];
        this.sourcedata = dependOnTemp;

        this.headerAlign = response["headerAlign"];
        this.footerAlign = response["footerAlign"];

        this.getAllLatest(false);

        let filterInfoNew = [];
        for (const i in response.filterInfo) {
            let pipelinetag = {};
            pipelinetag["id"] = response.filterInfo[i].ref.uuid + "." + response.filterInfo[i].attrId;
            pipelinetag["itemName"] = response.filterInfo[i].ref.name + "." + response.filterInfo[i].attrName;
            filterInfoNew[i] = pipelinetag;
        }
        this.filterInfoTags = filterInfoNew

        let attributeJson = {};
        attributeJson["attributeData"] = response;
        if (response.attributeInfo != null) {
            let attributearray = [];
            for (let i = 0; i < response.attributeInfo.length; i++) {
                let attributeinfojson = {};
                attributeinfojson["name"] = response.attributeInfo[i].attrSourceName
                if (response.attributeInfo[i].sourceAttr.ref.type == "datapod" || response.attributeInfo[i].sourceAttr.ref.type == "dataset" || response.attributeInfo[i].sourceAttr.ref.type == "rule") {
                    var sourceattribute = {}
                    sourceattribute["uuid"] = response.attributeInfo[i].sourceAttr.ref.uuid;
                    sourceattribute["label"] = response.attributeInfo[i].sourceAttr.ref.name;
                    sourceattribute["dname"] = response.attributeInfo[i].sourceAttr.ref.name + '.' + response.attributeInfo[i].sourceAttr.attrName;
                    sourceattribute["type"] = response.attributeInfo[i].sourceAttr.ref.type;
                    sourceattribute["attributeId"] = response.attributeInfo[i].sourceAttr.attrId;
                    sourceattribute["id"] = sourceattribute["uuid"] + "_" + sourceattribute["attributeId"]
                    let obj = {}
                    obj["value"] = "datapod"
                    obj["label"] = "attribute"
                    attributeinfojson["sourceAttributeType"] = obj;
                    attributeinfojson["isSourceAtributeSimple"] = false;
                    attributeinfojson["isSourceAtributeDatapod"] = true;
                    attributeinfojson["isSourceAtributeFormula"] = false;
                    attributeinfojson["isSourceAtributeExpression"] = false;
                }
                else if (response.attributeInfo[i].sourceAttr.ref.type == "simple") {
                    let obj = {}
                    obj["value"] = "string"
                    obj["label"] = "string"
                    attributeinfojson["sourceAttributeType"] = obj;
                    attributeinfojson["isSourceAtributeSimple"] = true;
                    attributeinfojson["sourcesimple"] = response.attributeInfo[i].sourceAttr.value
                    attributeinfojson["isSourceAtributeDatapod"] = false;
                    attributeinfojson["isSourceAtributeFormula"] = false;
                    attributeinfojson["isSourceAtributeExpression"] = false;
                    attributeinfojson["isSourceAtributeFunction"] = false;

                }
                if (response.attributeInfo[i].sourceAttr.ref.type == "expression") {
                    let sourceexpression = {};
                    sourceexpression["uuid"] = response.attributeInfo[i].sourceAttr.ref.uuid;
                    sourceexpression["label"] = response.attributeInfo[i].sourceAttr.ref.name
                    let obj = {}
                    obj["value"] = "expression"
                    obj["label"] = "expression"
                    attributeinfojson["sourceAttributeType"] = obj;
                    attributeinfojson["sourceexpression"] = sourceexpression;
                    attributeinfojson["isSourceAtributeSimple"] = false;
                    attributeinfojson["isSourceAtributeDatapod"] = false;
                    attributeinfojson["isSourceAtributeFormula"] = false;
                    attributeinfojson["isSourceAtributeExpression"] = true;
                    attributeinfojson["isSourceAtributeFunction"] = false;
                    this.getAllExpression(false, 0)
                }
                if (response.attributeInfo[i].sourceAttr.ref.type == "formula") {
                    let sourceformula = {};
                    sourceformula["uuid"] = response.attributeInfo[i].sourceAttr.ref.uuid;
                    sourceformula["label"] = response.attributeInfo[i].sourceAttr.ref.name;
                    let obj = {}
                    obj["value"] = "formula"
                    obj["label"] = "formula"
                    attributeinfojson["sourceAttributeType"] = obj;
                    attributeinfojson["sourceformula"] = sourceformula;
                    attributeinfojson["isSourceAtributeSimple"] = false;
                    attributeinfojson["isSourceAtributeDatapod"] = false;
                    attributeinfojson["isSourceAtributeFormula"] = true;
                    attributeinfojson["isSourceAtributeExpression"] = false;
                    attributeinfojson["isSourceAtributeFunction"] = false;
                    this.getAllFormula(false, 0);
                }
                if (response.attributeInfo[i].sourceAttr.ref.type == "function") {
                    let sourcefunction = {};
                    sourcefunction["uuid"] = response.attributeInfo[i].sourceAttr.ref.uuid;
                    sourcefunction["label"] = response.attributeInfo[i].sourceAttr.ref.name
                    let obj = {}
                    obj["value"] = "function"
                    obj["label"] = "function"
                    attributeinfojson["sourceAttributeType"] = obj;
                    attributeinfojson["sourcefunction"] = sourcefunction;
                    attributeinfojson["isSourceAtributeSimple"] = false;
                    attributeinfojson["isSourceAtributeDatapod"] = false;
                    attributeinfojson["isSourceAtributeFormula"] = false;
                    attributeinfojson["isSourceAtributeExpression"] = false;
                    attributeinfojson["isSourceAtributeFunction"] = true;
                    this.getAllFunctions(false, 0);
                }
                attributeinfojson["sourceattribute"] = sourceattribute;
                attributearray[i] = attributeinfojson
            }
            this.attributeTableArray = attributearray
        }


        console.log(JSON.stringify(response));
    }

    // indexOfByMultiplaValue(array, data) {
    //     let result = -1;
    //     for (let i = 0; i < array.length; i++) {
    //         if (array[i].uuid == data.uuid && array[i].attrId == data.attrId) {
    //             result = i;
    //             break
    //         }
    //         else {
    //             result = -1
    //         }

    //     }//End For
    //     return result;
    // }//End indexOfdata

    // indexOfBySingleValue = function (array, data) {
    //     let result = -1;
    //     for (let i = 0; i < array.length; i++) {
    //         if (array[i].uuid == data.uuid) {
    //             result = 0;
    //             break
    //         }
    //         else {
    //             result = -1
    //         }

    //     }//End For
    //     return result;
    // }//End indexOfdata

    // addToKey($event: any) {

    //     let data = $event.dragData
    //     let len = this.keylist.length;
    //     let keyjson = {};
    //     keyjson["id"] = len;
    //     keyjson["itemName"] = data["itemName"]
    //     keyjson["name"] = data["name"]
    //     keyjson["type"] = data["type"]
    //     keyjson["uuid"] = data["uuid"]
    //     keyjson["attrId"] = data["attrId"]
    //     keyjson["attrName"] = data["attrName"];
    //     if (this.indexOfByMultiplaValue(this.keylist, data) == -1 && this.indexOfByMultiplaValue(this.grouplist, data) == -1 && this.IsDisable != "true") {
    //         this.IsEmpty = "false"
    //         this.keylist.push(keyjson);
    //     }
    // }
    // addToGroup($event: any) {
    //     let data = $event.dragData
    //     let len = this.grouplist.length;
    //     let groupjson = {};
    //     groupjson["id"] = len;
    //     groupjson["itemName"] = data["itemName"]
    //     groupjson["name"] = data["name"]
    //     groupjson["type"] = data["type"]
    //     groupjson["uuid"] = data["uuid"]
    //     groupjson["attrId"] = data["attrId"]
    //     groupjson["attrName"] = data["attrName"];
    //     if (this.indexOfByMultiplaValue(this.grouplist, data) == -1 && this.indexOfByMultiplaValue(this.keylist, data) == -1 && this.IsDisable != "true") {
    //         this.grouplist.push(groupjson);
    //     }
    // }
    // addToValue($event: any) {

    //     let data = $event.dragData
    //     let len = this.valuelist.length;
    //     let valuejson = {};
    //     valuejson["id"] = len;
    //     valuejson["itemName"] = data["itemName"]

    //     valuejson["type"] = data["type"]
    //     valuejson["uuid"] = data["uuid"]
    //     valuejson["attrId"] = data["attrId"]
    //     valuejson["attrName"] = data["attrName"];
    //     if (data.type == "formula" || data.type == "expression") {
    //         valuejson["name"] = data["name"]
    //         if (this.indexOfBySingleValue(this.valuelist, data) == -1 && this.IsDisable != "true") {
    //             this.IsEmpty = "false"
    //             this.valuelist.push(valuejson);

    //         }
    //     }
    //     else if (this.indexOfByMultiplaValue(this.valuelist, data) == -1 && this.IsDisable != "true") {
    //         valuejson["name"] = data["attrName"]
    //         this.IsEmpty = "false"
    //         this.valuelist.push(valuejson);
    //     }
    // }

    // removeValue(index) {
    //     if (this.IsDisable != "true") {
    //         this.valuelist.splice(index, 1);
    //     }
    // }//End removeValue
    // removeGroup(index) {
    //     if (this.IsDisable != "true") {
    //         this.grouplist.splice(index, 1);
    //     }
    // }//End removeValue

    // removeKey(index) {
    //     if (this.IsDisable != "true") {
    //         this.keylist.splice(index, 1);
    //     }
    // }//End removeValue

    checkEmpty() {

        if (this.keylist.length > 0 && this.valuelist.length > 0) {
            this.IsEmpty = "false"
        }
        else {
            this.IsEmpty = "true"
        }
    }
    submit() {
        var upd_tag = 'Y'
        this.isSubmitEnable = true
        let reportjson = {}
        reportjson["uuid"] = this.reportdata.uuid;
        reportjson["name"] = this.reportdata.name;
        reportjson["desc"] = this.reportdata.desc;
        var tagArray = [];
        if (this.reportdata.tags != null) {
            for (var counttag = 0; counttag < this.reportdata.tags.length; counttag++) {
                tagArray[counttag] = this.reportdata.tags[counttag].value;

            }
        }
        reportjson['tags'] = tagArray;
        reportjson["active"] = this.reportdata.active == true ? 'Y' : "N"
        reportjson["published"] = this.reportdata.published == true ? 'Y' : "N"
        let dependsOn = {};
        let ref = {};
        ref["type"] = this.source
        ref["uuid"] = this.sourcedata.uuid;
        dependsOn["ref"] = ref;
        reportjson["source"] = dependsOn;

        let filterInfoArrayNew = [];
        if (this.filterInfoTags != null) {
            for (const c in this.filterInfoTags) {
                let filterInfoRef = {};
                let filterRef = {};
                filterInfoRef["uuid"] = this.filterInfoTags[c].id;
                filterInfoRef["type"] = "datapod";
                filterRef["ref"] = filterInfoRef;
                filterInfoArrayNew.push(filterRef);
            }
        }

        reportjson["filterInfo"] = filterInfoArrayNew;

        reportjson['filterInfo'] = [],
            reportjson['dimension'] = [],
            console.log(JSON.stringify(reportjson));
        this._commonService.submit(reportjson, 'report', upd_tag).subscribe(
            response => { this.OnSuccessubmit(response) },
            error => console.log('Error :: ' + error)
        )

    }
    OnSuccessubmit(response) {
        this.msgs = [];
        this.isSubmitEnable = true;
        this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Vizpod Saved Successfully' });
        setTimeout(() => {
            this.goBack()
        }, 1000);


    }
    enableEdit(uuid, version) {
        this.router.navigate(['app/dataVisualization/report', uuid, version, 'false']);
        this.dropdownSettings.disabled = true
        this.dropdownSettingsPipeline = {
            singleSelection: false,
            selectAllText: 'Select All',
            unSelectAllText: 'UnSelect All',
            enableSearchFilter: true,
            disabled: true
        };

    }
    showview(uuid, version) {
        this.router.navigate(['app/dataVisualization/report', uuid, version, 'true']);
        this.dropdownSettings.disabled = false;
        this.dropdownSettingsPipeline = {
            singleSelection: false,
            selectAllText: 'Select All',
            unSelectAllText: 'UnSelect All',
            enableSearchFilter: true,
            disabled: false
        };

    }
    addAttribute() {
        if (this.attributeTableArray == null) {
            this.attributeTableArray = [];
        }
        let len = this.attributeTableArray.length + 1
        let attrinfo = {};
        attrinfo["name"] = "attribute" + len;
        attrinfo["id"] = len - 1;
        attrinfo["sourceAttributeType"] = { "value": "string", "label": "string" };
        attrinfo["isSourceAtributeSimple"] = true;
        attrinfo["isSourceAtributeDatapod"] = false;
        this.attributeTableArray.splice(this.attributeTableArray.length, 0, attrinfo);
    }
    removeAttribute() {
        var newDataList = [];
        this.selectAllAttributeRow = false
        this.attributeTableArray.forEach(selected => {
            if (!selected.selected) {
                newDataList.push(selected);
            }
        });
        this.attributeTableArray = newDataList;
    }
    checkAllAttributeRow() {
        if (!this.selectAllAttributeRow) {
            this.selectAllAttributeRow = true;
        }
        else {
            this.selectAllAttributeRow = false;
        }
        this.attributeTableArray.forEach(attribute => {
            attribute.selected = this.selectAllAttributeRow;
        });
    }

    getAllExpression(defaulfMode, index) {
        this._datasetService.getExpressionByType(this.sourcedata.uuid, this.source).subscribe(
            response => { this.onSuccessExpression(response, defaulfMode, index) },
            error => console.log('Error :: ' + error)
        )
    }
    onSuccessExpression(response, defaulfMode, index) {
        let temp = []
        for (const n in response) {
            let allname = {};
            allname["label"] = response[n]['name'];
            allname["value"] = {};
            allname["value"]["label"] = response[n]['name'];
            allname["value"]["uuid"] = response[n]['uuid'];
            temp[n] = allname;
        }
        this.allMapExpression = temp;
        this.allMapExpression.splice(0, 0, {
            "label": "---CreateNew---",
            "value": { "label": "---CreateNew---", "uuid": "01" }
        });
        if (defaulfMode == true) {
            //   let sourceexpression = {};
            //   sourceexpression["uuid"] = this.allMapExpression[0]["value"].uuid;
            //   sourceexpression["label"] = this.allMapExpression[0].label;
            //   this.dataset.attributeTableArray[index].sourceexpression = sourceexpression;
        }
    }

    getAllFormula(defaulfMode, index) {
        debugger
        this._commonService.getFormulaByType(this.sourcedata.uuid, "formula").subscribe(
            response => { this.onSuccessgetAllFormula(response, defaulfMode, index) },
            error => console.log('Error :: ' + error)
        )
    }
    onSuccessgetAllFormula(response, defaulfMode, index) {
        //this.allMapFormula = response
        let temp = []
        for (const n in response) {
            let allname = {};
            allname["label"] = response[n]['name'];
            allname["value"] = {};
            allname["value"]["label"] = response[n]['name'];
            allname["value"]["uuid"] = response[n]['uuid'];
            temp[n] = allname;
        }
        this.allMapFormula = temp;
        if (this.allMapFormula != null) {
            if (defaulfMode == true) {
                let sourceformula = {};
                sourceformula["uuid"] = this.allMapFormula[0]["value"].uuid;
                sourceformula["label"] = this.allMapFormula[0].label;
                this.attributeTableArray[index].sourceformula = sourceformula;
            }
        }
    }

    getAllFunctions(defaulfMode, index) {
        this._datasetService.getAllLatestFunction("function", "N").subscribe(
            response => { this.onSuccessFunction(response, defaulfMode, index) },
            error => console.log('Error :: ' + error)
        )
    }
    onSuccessFunction(response, defaulfMode, index) {
        let temp = []
        for (const n in response) {
            let allname = {};
            allname["label"] = response[n]['name'];
            allname["value"] = {};
            allname["value"]["label"] = response[n]['name'];
            allname["value"]["uuid"] = response[n]['uuid'];
            temp[n] = allname;
        }
        this.ruleLoadFunction = temp
        if (defaulfMode == true) {
            let sourcefunction = {};
            sourcefunction["uuid"] = this.ruleLoadFunction[0]["value"].uuid;
            sourcefunction["label"] = this.ruleLoadFunction[0].label;
            this.attributeTableArray[index].sourcefunction = sourcefunction;
        }
    }
    onChangeSourceAttribute(type, index) {
        if (type == "string") {
            this.attributeTableArray[index].isSourceAtributeSimple = true;
            this.attributeTableArray[index].isSourceAtributeDatapod = false;
            this.attributeTableArray[index].isSourceAtributeFormula = false;
            this.attributeTableArray[index].sourcesimple = "''";
            this.attributeTableArray[index].isSourceAtributeExpression = false;
            this.attributeTableArray[index].isSourceAtributeFunction = false;
        }
        else if (type == "datapod") {
            this.attributeTableArray[index].isSourceAtributeSimple = false;
            this.attributeTableArray[index].isSourceAtributeDatapod = true;
            this.attributeTableArray[index].isSourceAtributeFormula = false;
            this.attributeTableArray[index].isSourceAtributeExpression = false;
            this.attributeTableArray[index].isSourceAtributeFunction = false;
            this.attributeTableArray[index].sourceattribute = {}
            this.getAllAttributeBySource();
            if (this.allMapSourceAttribute && this.allMapSourceAttribute.length > 0) {
                let sourceattribute = {}
                sourceattribute["dname"] = this.allMapSourceAttribute[0]["label"]
                sourceattribute["id"] = this.allMapSourceAttribute[0]["value"]["id"];
                this.attributeTableArray[index].sourceattribute = sourceattribute;
            }
        }
        else if (type == "formula") {
            this.attributeTableArray[index].isSourceAtributeSimple = false;
            this.attributeTableArray[index].isSourceAtributeDatapod = false;
            this.attributeTableArray[index].isSourceAtributeFormula = true;
            this.attributeTableArray[index].isSourceAtributeExpression = false;
            this.attributeTableArray[index].isSourceAtributeFunction = false;
            this.attributeTableArray[index].sourceformula = {}
            this.getAllFormula(true, index);

        }
        else if (type == "expression") {
            this.attributeTableArray[index].isSourceAtributeSimple = false;
            this.attributeTableArray[index].isSourceAtributeDatapod = false;
            this.attributeTableArray[index].isSourceAtributeFormula = false;
            this.attributeTableArray[index].isSourceAtributeExpression = true;
            this.attributeTableArray[index].isSourceAtributeFunction = false;
            this.attributeTableArray[index].sourceexpression = {}
            this.getAllExpression(true, index);

        }
        else if (type == "function") {
            this.attributeTableArray[index].isSourceAtributeSimple = false;
            this.attributeTableArray[index].isSourceAtributeDatapod = false;
            this.attributeTableArray[index].isSourceAtributeFormula = false;
            this.attributeTableArray[index].isSourceAtributeExpression = false;
            this.attributeTableArray[index].isSourceAtributeFunction = true;
            this.attributeTableArray[index].isSourceAtributeFunction = true;
            this.attributeTableArray[index].sourcefunction = {}
            this.getAllFunctions(true, index);
        }
    }

    onChangeAttributeDatapod(data, index) {
        if (data != null) {
            this.attributeTableArray[index].name = data.label.split(".")[1]
        }
    }
}