import { version } from './../../../../bower_components/moment/moment.d';

import { Component, ViewChild } from '@angular/core';
import { Location } from '@angular/common';
import { Router, ActivatedRoute, Params } from '@angular/router';

import { DashboardService } from '../../metadata/services/dashboard.service'
import { CommonService } from '../../metadata/services/common.service';

import { Version } from '../../metadata/domain/version'
import { DependsOn } from '../dependsOn'
import { AttributeHolder } from '../../metadata/domain/domain.attributeHolder'
import { DatasetService } from '../../metadata/services/dataset.service';
import { ReportService } from '../../metadata/services/report.service';
import { SelectItem } from 'primeng/primeng';
import { Http } from '@angular/http';
import { saveAs } from 'file-saver/FileSaver';
import { KnowledgeGraphComponent } from '../../shared/components/knowledgeGraph/knowledgeGraph.component';

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
    filterPopUpData: any[];
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
    allMapParamList: any;
    reportExec: any;
    reportExecData: any;
    isShowSimpleData: boolean = false;
    isShowReportData: boolean = true;
    isShowSpinner: boolean = false;
    showDialogSpinner: boolean = false;
    colsdata: any;
    cols: any;
    filterAttribureIdValues: any[];
    selectedAttributeValue: any;
    displayDialog: boolean = false;
    filterAttribureIdData: any;
    sourcedata1: any;
    filterDropdownValue: any[] = [];
    filterDropdownName: any;
    filterValue: any;
    displayDialogBox: boolean = false;
    downloadFormatArray: any[];
    downloadFormat: any;
    reportExecUuid: any;
    reportExecVersion: any;
    numRows: any = 100;
    isHomeEnable: boolean = false
    showGraph: boolean = false;
    isRunReportEnable: boolean = true;
    isRefreshEnable: boolean = true;
    isDownloadEnable: boolean = false;
    isEditEnable: boolean = false;
    isDependencyGraphEnable: boolean = true;
    dragIndex: any;
    dropIndex: any;
    @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;
    constructor(private activatedRoute: ActivatedRoute, private http: Http, public router: Router, private _dashboardService: DashboardService, private _commonService: CommonService, private _location: Location, private _datasetService: DatasetService, private _reportService: ReportService) {
        this.reportdata = {};
        this.IsDisable = "false";
        this.isSubmitEnable = false;
        this.reportdata["active"] = true;
        this.isHomeEnable = false;
        this.showGraph = false;
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
            { "value": "string", "label": "string" },
            { "value": "datapod", "label": "attribute" },
            { "value": "expression", "label": "expression" },
            { "value": "formula", "label": "formula" },
            { "value": "function", "label": "function" },
            { "value": "paramlist", "label": "paramlist" }
        ]

        this.downloadFormatArray = [
            { "value": "excel", "label": "excel" }];
        this.downloadFormat = this.downloadFormatArray[0];
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
            //this.getAllLatestDatapod();
        });
    }
    // getAllLatestDatapod() {
    //     this._commonService.getAllLatest('datapod')
    //         .subscribe(
    //             response => {
    //                 this.onSuccessgetAllLatestDatapod(response)
    //             },
    //             error => console.log("Error ::" + error));
    // }

    // onSuccessgetAllLatestDatapod(response) {
    //     this.privResponse = response
    //     this.filterInfoArray = [];

    //     for (const i in response) {
    //         let filterRef = {};
    //         filterRef["id"] = response[i]['uuid'];
    //         filterRef["itemName"] = response[i]['name'];
    //         filterRef["version"] = response[i]['version'];

    //         this.filterInfoArray[i] = filterRef;
    //     }
    // }
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
        console.log("onChange call...");
        this.keylist = [];
        this.valuelist = [];
        this.grouplist = [];
        this.filterInfoTags = []
        this.getAllAttributeBySource();
    }
    selectType() {
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
        //this.getAttributesByDatapod();
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

        this.filterInfoArray = [];
        for (const i in response) {
            let filterRef = {};
            filterRef["id"] = response[i]['id'];
            filterRef["itemName"] = response[i]['dname'];
            this.filterInfoArray[i] = filterRef;
        }
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
            pipelinetag["id"] = response.filterInfo[i].ref.uuid + "_" + response.filterInfo[i].attrId;
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
                    attributeinfojson["isSourceAtributeFunction"] = false;
                    attributeinfojson["isSourceAtributeParamList"] = false;
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
                    attributeinfojson["isSourceAtributeParamList"] = false;
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
                    attributeinfojson["isSourceAtributeParamList"] = false;
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
                    attributeinfojson["isSourceAtributeParamList"] = false;
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
                    attributeinfojson["isSourceAtributeParamList"] = false;
                    this.getAllFunctions(false, 0);
                }
                if (response.attributeInfo[i].sourceAttr.ref.type == "paramlist") {
                    let sourceparamlist = {};
                    sourceparamlist["uuid"] = response.attributeInfo[i].sourceAttr.ref.uuid;
                    sourceparamlist["label"] = response.attributeInfo[i].sourceAttr.ref.name
                    let obj = {}
                    obj["value"] = "paramlist"
                    obj["label"] = "paramlist"
                    attributeinfojson["sourceAttributeType"] = obj;
                    attributeinfojson["sourceparamlist"] = sourceparamlist;
                    attributeinfojson["isSourceAtributeSimple"] = false;
                    attributeinfojson["isSourceAtributeDatapod"] = false;
                    attributeinfojson["isSourceAtributeFormula"] = false;
                    attributeinfojson["isSourceAtributeExpression"] = false;
                    attributeinfojson["isSourceAtributeFunction"] = false;
                    attributeinfojson["isSourceAtributeParamList"] = true;
                    this.getParamByApp(false, 0);
                }
                attributeinfojson["sourceattribute"] = sourceattribute;
                attributearray[i] = attributeinfojson
            }
            this.attributeTableArray = attributearray
        }


        console.log(JSON.stringify(response));
    }

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
        reportjson["active"] = this.reportdata.active == true ? 'Y' : "N";
        reportjson["published"] = this.reportdata.published == true ? 'Y' : "N";
        reportjson["title"] = this.reportdata.title;
        reportjson["header"] = this.reportdata.header;
        reportjson["footer"] = this.reportdata.footer;
        reportjson["headerAlign"] = this.headerAlign;
        reportjson["footerAlign"] = this.footerAlign;

        var tagArray = [];
        if (this.reportdata.tags != null) {
            for (var counttag = 0; counttag < this.reportdata.tags.length; counttag++) {
                tagArray[counttag] = this.reportdata.tags[counttag].value;

            }
        }
        reportjson['tags'] = tagArray;

        let dependsOn = {};
        let ref = {};
        ref["type"] = this.source
        ref["uuid"] = this.sourcedata.uuid;
        dependsOn["ref"] = ref;
        reportjson["dependsOn"] = dependsOn;

        let filterInfoArrayNew = [];
        if (this.filterInfoTags != null) {
            for (const c in this.filterInfoTags) {
                let filterInfoRef = {};
                let filterRef = {};
                filterInfoRef["uuid"] = this.filterInfoTags[c].id.split("_")[0];
                filterInfoRef["type"] = this.source;
                filterRef["ref"] = filterInfoRef;
                filterRef["attrId"] = this.filterInfoTags[c].id.split("_")[1];
                filterInfoArrayNew.push(filterRef);
            }
        }

        reportjson["filterInfo"] = filterInfoArrayNew;
        var sourceAttributesArray = [];
        for (var i = 0; i < this.attributeTableArray.length; i++) {
            var attributemap = {};
            attributemap["attrSourceId"] = i;
            attributemap["attrSourceName"] = this.attributeTableArray[i].name
            var sourceAttr = {};
            var sourceref = {};
            if (this.attributeTableArray[i].sourceAttributeType.value == "string") {
                sourceref["type"] = "simple";
                sourceAttr["ref"] = sourceref;
                if (typeof this.attributeTableArray[i].sourcesimple == "undefined") {
                    sourceAttr["value"] = "";
                }
                else {
                    sourceAttr["value"] = this.attributeTableArray[i].sourcesimple;
                }
                attributemap["sourceAttr"] = sourceAttr;
            }
            else if (this.attributeTableArray[i].sourceAttributeType.value == "datapod") {
                let uuid = this.attributeTableArray[i].sourceattribute.id.split("_")[0]
                var attrid = this.attributeTableArray[i].sourceattribute.id.split("_")[1]
                sourceref["uuid"] = uuid;
                if (this.source == "relation") {
                    sourceref["type"] = "datapod";
                }
                else {
                    sourceref["type"] = this.source;
                }
                sourceAttr["ref"] = sourceref;
                sourceAttr["attrId"] = attrid;
                sourceAttr["attrType"] = null;
                attributemap["sourceAttr"] = sourceAttr;
            }
            if (this.attributeTableArray[i].sourceAttributeType.value == "expression") {
                sourceref["type"] = "expression";
                sourceref["uuid"] = this.attributeTableArray[i].sourceexpression.uuid;
                sourceAttr["ref"] = sourceref;
                attributemap["sourceAttr"] = sourceAttr;

            }
            if (this.attributeTableArray[i].sourceAttributeType.value == "formula") {
                sourceref["type"] = "formula";
                sourceref["uuid"] = this.attributeTableArray[i].sourceformula.uuid;
                sourceAttr["ref"] = sourceref;
                attributemap["sourceAttr"] = sourceAttr;

            }
            if (this.attributeTableArray[i].sourceAttributeType.value == "function") {
                sourceref["type"] = "function"
                sourceref["uuid"] = this.attributeTableArray[i].sourcefunction.uuid;
                sourceAttr["ref"] = sourceref;
                attributemap["sourceAttr"] = sourceAttr
            }
            if (this.attributeTableArray[i].sourceAttributeType.value == "paramlist") {
                sourceref["type"] = "paramlist"
                sourceref["uuid"] = this.attributeTableArray[i].sourceparamlist.uuid;
                sourceAttr["ref"] = sourceref;
                //sourceAttr["attrId"] = attrid;
                attributemap["sourceAttr"] = sourceAttr
            }
            sourceAttributesArray[i] = attributemap;
        }
        reportjson["attributeInfo"] = sourceAttributesArray;
        //reportjson['dimension'] = [],
        console.log(JSON.stringify(reportjson));
        this._commonService.submit('report', reportjson, upd_tag).subscribe(
            response => { this.OnSuccessubmit(response) },
            error => console.log('Error :: ' + error)
        )

    }
    OnSuccessubmit(response) {
        this.msgs = [];
        this.isSubmitEnable = true;
        this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Report Saved Successfully' });
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
        this._datasetService.getExpressionByType2(this.sourcedata.uuid, this.source).subscribe(
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
        // this.allMapExpression.splice(0, 0, {
        //     "label": "---CreateNew---",
        //     "value": { "label": "---CreateNew---", "uuid": "01" }
        // });
        if (defaulfMode == true) {
            //   let sourceexpression = {};
            //   sourceexpression["uuid"] = this.allMapExpression[0]["value"].uuid;
            //   sourceexpression["label"] = this.allMapExpression[0].label;
            //   this.dataset.attributeTableArray[index].sourceexpression = sourceexpression;
        }
    }

    getAllFormula(defaulfMode, index) {
        this._commonService.getFormulaByType2(this.sourcedata.uuid, "datapod").subscribe(
            response => { this.onSuccessgetAllFormula(response, defaulfMode, index) },
            error => console.log('Error :: ' + error)
        )
    }
    onSuccessgetAllFormula(response, defaulfMode, index) {
        let temp = []
        if (response.length > 0) {
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

    getParamByApp(defaulfMode, index) {
        this._reportService.getParamByApp("", "application")
            .subscribe(response => { this.onSuccessgetParamByApp(response, defaulfMode, index) },
                error => console.log("Error ::", error)
            )
    }
    onSuccessgetParamByApp(response, defaulfMode, index) {
        let temp = []
        for (const n in response) {
            let allname = {};
            allname["label"] = "app." + response[n]['paramName'];
            allname["value"] = {};
            allname["value"]["label"] = "app." + response[n]['paramName'];
            allname["value"]["uuid"] = response[n].ref['uuid'];
            temp[n] = allname;
        }
        this.allMapParamList = temp
        if (defaulfMode == true) {
            let sourcefunction = {};
            sourcefunction["uuid"] = this.allMapParamList[0]["value"].uuid;
            sourcefunction["label"] = this.allMapParamList[0].label;
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
            this.attributeTableArray[index].isSourceAtributeParamList = false;
        }
        else if (type == "datapod") {
            this.attributeTableArray[index].isSourceAtributeSimple = false;
            this.attributeTableArray[index].isSourceAtributeDatapod = true;
            this.attributeTableArray[index].isSourceAtributeFormula = false;
            this.attributeTableArray[index].isSourceAtributeExpression = false;
            this.attributeTableArray[index].isSourceAtributeFunction = false;
            this.attributeTableArray[index].isSourceAtributeParamList = false;
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
            this.attributeTableArray[index].isSourceAtributeParamList = false;
            this.attributeTableArray[index].sourceformula = {}
            this.getAllFormula(true, index);

        }
        else if (type == "expression") {
            this.attributeTableArray[index].isSourceAtributeSimple = false;
            this.attributeTableArray[index].isSourceAtributeDatapod = false;
            this.attributeTableArray[index].isSourceAtributeFormula = false;
            this.attributeTableArray[index].isSourceAtributeExpression = true;
            this.attributeTableArray[index].isSourceAtributeFunction = false;
            this.attributeTableArray[index].isSourceAtributeParamList = false;
            this.attributeTableArray[index].sourceexpression = {}
            this.getAllExpression(true, index);

        }
        else if (type == "function") {
            this.attributeTableArray[index].isSourceAtributeSimple = false;
            this.attributeTableArray[index].isSourceAtributeDatapod = false;
            this.attributeTableArray[index].isSourceAtributeFormula = false;
            this.attributeTableArray[index].isSourceAtributeExpression = false;
            this.attributeTableArray[index].isSourceAtributeFunction = true;
            this.attributeTableArray[index].isSourceAtributeParamList = false;
            this.attributeTableArray[index].sourcefunction = {}
            this.getAllFunctions(true, index);
        }
        else if (type == "paramlist") {
            this.attributeTableArray[index].isSourceAtributeSimple = false;
            this.attributeTableArray[index].isSourceAtributeDatapod = false;
            this.attributeTableArray[index].isSourceAtributeFormula = false;
            this.attributeTableArray[index].isSourceAtributeExpression = false;
            this.attributeTableArray[index].isSourceAtributeFunction = false;
            this.attributeTableArray[index].isSourceAtributeParamList = true;
            this.attributeTableArray[index].sourceparamlist = {}
            this.getParamByApp(true, index);
        }
    }

    onChangeAttributeDatapod(data, index) {
        if (data != null) {
            this.attributeTableArray[index].name = data.label.split(".")[1]
        }
    }
    onChangeParamlist(data, index) {
        if (!this.attributeTableArray[index].isSourceName)
            this.attributeTableArray[index].name = data.label.split(".")[1];
    }

    runReport(uuid, version) {
        this.isRunReportEnable = false;
        this.isHomeEnable = true;
        this.isShowReportData = false;
        this.isDownloadEnable = true;
        this.isRefreshEnable = true;
        this.isEditEnable = true;
        this.reportExecute(uuid, version, null);
    }

    reportExecute(uuid, version, data) {
        this.isShowSpinner = true;
        this.isShowSimpleData = false;
        this._reportService.reportExecute(uuid, version, data)
            .subscribe(response => { this.onSuccessReportExecute(response) },
                error => console.log("Error ::", error)
            )
    }
    onSuccessReportExecute(response: any): any {
        this.isShowSpinner = false
        this.isShowSimpleData = true;
        this.getSample(response);
    }

    getSample(data: any): any {
        this.reportExecUuid = data["uuid"];
        this.reportExecVersion = data["version"];
        this._reportService.getReportSample(data["uuid"], data["version"])
            .subscribe(response => { this.onSuccessGetSample(response) },
                error => console.log("Error ::", error)
            )
    }
    onSuccessGetSample(response: any[]): any {
        this.colsdata = response;
        let columns = [];
        console.log(response)
        if (response.length && response.length > 0) {
            Object.keys(response[0]).forEach(val => {
                if (val != "rownum") {
                    let width = ((val.split("").length * 9) + 20) + "px"
                    columns.push({ "field": val, "header": val, colwidth: width });
                }
            });
        }
        this.cols = columns
    }
    openFilterPopup() {
        this.displayDialog = true;
        if (this.filterAttribureIdValues == null) {
            this.getFilterValue(this.reportdata);
        }
    }
    getFilterValue(data: any): any {
        this.showDialogSpinner = true;
        this.filterAttribureIdValues = [];

        if (data.filterInfo && data.filterInfo.length > 0) {
            for (var n = 0; n < data.filterInfo.length; n++) {
                let attrName = data.filterInfo[n].attrName;
                this._reportService.getAttributeValues(data.filterInfo[n].ref.uuid, data.filterInfo[n].attrId, data.filterInfo[n].ref.type)
                    .subscribe(response => { this.onSuccessGetAttributeValues(response, attrName) },
                        error => console.log("Error ::", error)
                    );
            }
            this.filterInfoArray = [];
        }
    }

    onSuccessGetAttributeValues(response: any, attrName: any): any {
        var filterAttribure = {}
        let allNameArray = [];
        for (const n in response) {
            let allname = {};
            let count = 0;
            allname["label"] = response[n]['value'];
            allname["value"] = {};
            allname["value"]["label"] = response[n]['value'];
            allname["value"]["u_Id"] = response[n]['value'] + "_" + count;
            allNameArray.push(allname);
            count++;
        }
        filterAttribure["attrValue"] = allNameArray;
        filterAttribure["attrName"] = attrName;
        this.filterAttribureIdValues.push(filterAttribure);
        this.filterDropdownValue.push({});
        this.showDialogSpinner = false;
    }

    cancelFilterDialog() {
        this.displayDialog = false;
        this.filterDropdownValue = [];
        console.log("Cancel call....");
    }

    submitFilterDialog(filterValue: any[], filterName: any) {
        this.isShowSimpleData = false;
        this.isShowSpinner = true;
        let tags = [];
        for (let i = 0; i < filterValue.length; i++) {
            let attrName = filterName[i]["attrName"];
            var tag = {};
            if (filterValue[i] && filterValue[i].hasOwnProperty('label')) {
                tag['display'] = attrName + "-" + filterValue[i]["label"];
                tags.push(tag);
            }
        }
        this.filterValue = tags;

        //let uuid = this.reportdata["uuid"];
        //let version = this.reportdata["version"];
        let filterjson = {}
        let filterInfoArrayNew = [];
        if (this.filterInfoTags != null) {
            for (const c in this.filterInfoTags) {
                let filterInfoRef = {};
                let filterRef = {};
                filterInfoRef["uuid"] = this.filterInfoTags[c].id.split("_")[0];
                filterInfoRef["type"] = "datapod";
                filterRef["ref"] = filterInfoRef;
                filterRef["attrId"] = this.filterInfoTags[c].id.split("_")[1];

                for (let i = 0; i < this.filterInfoTags.length; i++) {
                    let name = this.filterInfoTags[c].itemName.split(".")[1];
                    let name2 = filterName[i]["attrName"];
                    if (name == filterName[i]["attrName"]) {
                        if (filterValue[i]) {
                            if (filterValue[i] && filterValue[i].hasOwnProperty('label')) {
                                filterRef["value"] = filterValue[i]["label"];
                                filterInfoArrayNew.push(filterRef);
                            }
                            else {
                                filterRef["value"] = "";
                            }
                        }
                        break;
                    }
                }
            }
        }
        filterjson["filterInfo"] = filterInfoArrayNew;
        this.reportExecute(this.reportdata["uuid"], this.reportdata["version"], filterjson);
        this.displayDialog = false;
    }

    onChipsRemove(filterValues) {
        this.filterDropdownValue = [];
        let filterjson = {}
        let filterInfoArrayNew = [];
        for (const c in filterValues) {
            for (const d in this.filterInfoTags) {
                let filterValuesName = filterValues[c].display.split("-")[0];
                let otherName = this.filterInfoTags[d].itemName.split(".")[1];
                if (filterValuesName == otherName) {
                    let filterInfoRef = {};
                    let filterRef = {};
                    filterInfoRef["uuid"] = this.filterInfoTags[d].id.split("_")[0];
                    filterInfoRef["type"] = "datapod";
                    filterRef["ref"] = filterInfoRef;
                    filterRef["attrId"] = this.filterInfoTags[d].id.split("_")[1];
                    filterRef["value"] = filterValues[c].display.split("-")[1];
                    filterInfoArrayNew.push(filterRef);
                    break;
                }
            }
        }
        filterjson["filterInfo"] = filterInfoArrayNew;
        this.reportExecute(this.reportdata["uuid"], this.reportdata["version"], filterjson);
    }
    onClickChips() {
        console.log("on Change chips");
    }

    downloadResult() {
        this.displayDialogBox = true;
    }

    submitDialogBox(numRows, downloadFormat) {
        this.displayDialogBox = false;
        this._reportService.download(this.reportExecUuid, this.reportExecVersion, numRows)
            .toPromise()
            .then(response => this.onSucessDownload(response));
    }
    onSucessDownload(response) {
        const contentDispositionHeader: string = response.headers.get('Content-Type');
        const parts: string[] = contentDispositionHeader.split(';');
        const filename = this.reportExecUuid + "-" + this.reportExecVersion + ".xls";
        const blob = new Blob([response._body], { type: 'application/vnd.ms-excel' });
        saveAs(blob, filename);
    }

    cancelDialogBox() {
        this.displayDialogBox = false;
    }
//   showMainPage(uuid, version) {
//         this.isHomeEnable = false
//         this.showGraph = false;
//         this.isShowReportData = true;
//         this.isShowSimpleData = false;
//         this.isRunReportEnable = true;
//     }  
    // showDependencyGraph(uuid, version) {
    //     console.log("showDependencyGraph call.....");
    //     this.showGraph = true;
    //     this.isHomeEnable = true;
    // }

    dragStart(event, data) {
        this.dragIndex = data
    }
    dragEnd(event) {
        console.log(event)
    }
    drop(event, data) {
        if (this.mode == 'false') {
            this.dropIndex = data
            var item = this.attributeTableArray[this.dragIndex]
            this.attributeTableArray.splice(this.dragIndex, 1)
            this.attributeTableArray.splice(this.dropIndex, 0, item)
            this.isSubmitEnable = true
        }
    }

    autoPopulate() {
        this.isSubmitEnable = true
        this.attributeTableArray = [];
        for (var i = 0; i < this.allMapSourceAttribute.length; i++) {
            var attributeinfo = {};
            attributeinfo["id"] = i;
            attributeinfo["sourceattribute"] = {};
            attributeinfo["name"] = this.allMapSourceAttribute[i].label.split(".")[1];
            attributeinfo["sourceattribute"]["id"] = this.allMapSourceAttribute[i].value.id;
            attributeinfo["sourceattribute"]["label"] = this.allMapSourceAttribute[i].label;
            let obj = {}
            obj["value"] = "datapod"
            obj["label"] = "attribute"
            attributeinfo["sourceAttributeType"] = obj;
            attributeinfo["isSourceAtributeSimple"] = false;
            attributeinfo["isSourceAtributeDatapod"] = true;
            attributeinfo["isSourceAtributeFormula"] = false;
            attributeinfo["isSourceAtributeExpression"] = false;
            attributeinfo["isSourceAtributeFunction"] = false;
            attributeinfo["isSourceAtributeParamList"] = false;
            this.attributeTableArray.push(attributeinfo);
        }
    }
    showMainPage() {
        this.isHomeEnable = false
        this.showGraph = false;
        this.isShowReportData = true;
        this.isShowSimpleData = false;
        this.isRunReportEnable = true;
    }

    showDagGraph(uuid, version) {
        this.isHomeEnable = true;
        this.showGraph = true;
        setTimeout(() => {
            this.d_KnowledgeGraphComponent.getGraphData(uuid,version);
          }, 1000);  
    }
}