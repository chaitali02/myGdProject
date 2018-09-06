
import { Component } from '@angular/core';
import { Location } from '@angular/common';
import { Router, ActivatedRoute, Params } from '@angular/router';

import { DashboardService } from '../../metadata/services/dashboard.service'
import { CommonService } from '../../metadata/services/common.service';
import { VizpodService } from '../../metadata/services/vizpod.service'

import { Version } from './../../metadata/domain/version'
import { DependsOn } from './../dependsOn'
import { AttributeHolder } from './../../metadata/domain/domain.attributeHolder'

@Component({
    selector: 'app-vizpod-detail',
    styleUrls: [],
    templateUrl: './vizpoddetail.template.html'
})

export class VizpodDetailComponent {
    IsEmpty: string;
    IsDisable: string;
    valuelist: any;
    grouplist: any;
    keylist: any;
    vizpodtype: void;
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
    vizpoddata: any;
    mode: any;
    version: any;
    uuid: any;
    vizpodTypes: any;
    filterAttributeTags: any;
    dropdownList: any[];
    dropdownSettings: { singleSelection: boolean; text: string; selectAllText: string; unSelectAllText: string; enableSearchFilter: boolean; classes: string; maxHeight: number; disabled: boolean; };

    constructor(private activatedRoute: ActivatedRoute, public router: Router, private _dashboardService: DashboardService, private _commonService: CommonService, private _location: Location, private _vizpodService: VizpodService) {
        this.vizpoddata = {};
        this.IsDisable = "false";
        this.isSubmitEnable = true;
        this.vizpoddata["active"] = true;
        this.keylist = [];
        this.valuelist = [];
        this.grouplist = [];
        this.vizpodTypes = ["bar-chart", "pie-chart", "line-chart", "donut-chart", "area-chart", "bubble-chart", "world-map", "usa-map", "data-grid"]
        this.breadcrumbDataFrom = [
            {
                "caption": "Data Visualization",
                "routeurl": "/app/list/vizpod"
            },

            {
                "caption": "Vizpod",
                "routeurl": "/app/list/vizpod"
            },
            {
                "caption": "",
                "routeurl": null
            }
        ]
        this.sources = ["datapod", "relation"];
        this.source = this.sources[0];
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
            }
            else {
                this.getAllLatest(true);


            }
        });

    }
    onVersionChange(version) {
        this.version = version;
        this.getOneByUuidAndVersion();
    }

    public goBack() {
        //this._location.back();
        this.router.navigate(['app/list/vizpod'])

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
    ChangeSourceType() {
        this.keylist = [];
        this.valuelist = [];
        this.grouplist = [];
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
        let attribute = []
        for (const n in response) {
            let allname = {};
            allname["id"] = response[n]['uuid'] + "_" + response[n]['attributeId'];
            allname["itemName"] = response[n]['dname'];
            allname["name"] = response[n]['dname'];
            allname["type"] = "datapod"
            allname["uuid"] = response[n]['uuid'];
            allname["class"] = "tagit-choice-default-dd";
            allname["attrId"] = response[n]['attributeId'];
            allname["attrName"] = response[n]['name'];
            attribute[n] = allname
        }
        this.allAttributes = attribute;
        this.getAllFormula();
        this.getAllExpression();
        this.dropdownList = attribute;
    }
  

getAllExpression() {
        this._commonService.getExpressionByType(this.sourcedata.uuid, this.source).subscribe(
            response => { this.onSuccessExpression(response) },
            error => console.log('Error :: ' + error)
        )
    }

    onSuccessExpression(response) {
        let temp = []
        for (const n in response) {
            let allname = {};
            allname["id"] = response[n]['uuid'];
            allname["itemName"] = response[n]['name'];
            allname["name"] = response[n]['dname'];
            allname["type"] = "expression"
            allname["uuid"] = response[n]['uuid'];
            allname["class"] = "tagit-choice_expression-dd";
            this.allAttributes.push(allname);
        }
        //this.allExpression = temp
    }

    getAllFormula() {
        this._commonService.getFormulaByType(this.sourcedata.uuid, "formula").subscribe(
            response => { this.onSuccessgetAllFormula(response) },
            error => console.log('Error :: ' + error)
        )
    }

    onSuccessgetAllFormula(response) {
        let temp = []
        for (const n in response) {
            let allname = {};
            allname["id"] = response[n]['uuid'];
            allname["itemName"] = response[n]['name'];
            allname["name"] = response[n]['dname'];
            allname["type"] = "expression"
            allname["uuid"] = response[n]['uuid'];
            allname["class"] = "tagit-choice_formula-dd";
            this.allAttributes.push(allname);
        }
        //this.allFormula = temp
    }
    getAllVersionByUuid() {
        this._commonService.getAllVersionByUuid('vizpod', this.uuid)
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
        this._vizpodService.getOneByUuidAndVersion(this.uuid, this.version, "vizpod")
            .subscribe(
            response => { this.onSuccessGetOneByUuidAndVersion(response) },
            error => console.log("Error :: " + error));
    }
    onSuccessGetOneByUuidAndVersion(response) {
        this.breadcrumbDataFrom[2].caption = response.name;
        this.vizpoddata = response;
        this.uuid = response.uuid;
        const version: Version = new Version();
        version.label = response['version'];
        version.uuid = response['uuid'];
        version.u_Id = response['uuid'] + "_" + response['version'];
        this.selectedVersion = version;
        this.vizpoddata.published = response["published"] == 'Y' ? true : false
        this.vizpoddata.active = response["active"] == 'Y' ? true : false;

        var tags = [];
        if (response.tags != null) {
            for (var i = 0; i < response.tags.length; i++) {
                var tag = {};
                tag['value'] = response.tags[i];
                tag['display'] = response.tags[i];
                tags[i] = tag

            }//End For
            this.vizpoddata.tags = tags;
        }//End If

        this.source = response["source"]["ref"].type
        let dependOnTemp: DependsOn = new DependsOn();
        dependOnTemp.label = response["source"]["ref"]["name"];
        dependOnTemp.uuid = response["source"]["ref"]["uuid"];
        this.sourcedata = dependOnTemp;
        this.vizpodtype = response.type
        this.getAllLatest(false);
        this.keylist = response.keys;
        this.grouplist = response.groups;
        this.valuelist = response.values;
        let tmp = [];
        for (let i = 0; i < response.detailAttr.length; i++) {
            let detailAttr = {};
            detailAttr["id"] = response.detailAttr[i]["ref"]["uuid"] + "_" + response.detailAttr[i].attributeId;
            detailAttr["itemName"] = response.detailAttr[i]["ref"]["name"] + "." + response.detailAttr[i]["attributeName"];
            detailAttr["uuid"] = response.detailAttr[i]["ref"]["uuid"];
            detailAttr["attributeId"] = response.detailAttr[i]["attributeId"];
            tmp[i] = detailAttr;
        }

        this.vizpoddata.detailAttr = tmp;
 }

    indexOfByMultiplaValue(array, data) {
        let result = -1;
        for (let i = 0; i < array.length; i++) {
            if (array[i].uuid == data.uuid && array[i].attrId == data.attrId) {
                result = i;
                break
            }
            else {
                result = -1
            }

        }//End For
        return result;
    }//End indexOfdata

    indexOfBySingleValue = function (array, data) {
        let result = -1;
        for (let i = 0; i < array.length; i++) {
            if (array[i].uuid == data.uuid) {
                result = 0;
                break
            }
            else {
                result = -1
            }

        }//End For
        return result;
    }//End indexOfdata

    addToKey($event: any) {

        let data = $event.dragData
        let len = this.keylist.length;
        let keyjson = {};
        keyjson["id"] = len;
        keyjson["itemName"] = data["itemName"]
        keyjson["name"] = data["name"]
        keyjson["type"] = data["type"]
        keyjson["uuid"] = data["uuid"]
        keyjson["attrId"] = data["attrId"]
        keyjson["attrName"] = data["attrName"];
        if (this.indexOfByMultiplaValue(this.keylist, data) == -1 && this.indexOfByMultiplaValue(this.grouplist, data) == -1 && this.IsDisable != "true") {
            this.IsEmpty = "false"
            this.keylist.push(keyjson);
        }
    }
    addToGroup($event: any) {
        let data = $event.dragData
        let len = this.grouplist.length;
        let groupjson = {};
        groupjson["id"] = len;
        groupjson["itemName"] = data["itemName"]
        groupjson["name"] = data["name"]
        groupjson["type"] = data["type"]
        groupjson["uuid"] = data["uuid"]
        groupjson["attrId"] = data["attrId"]
        groupjson["attrName"] = data["attrName"];
        if (this.indexOfByMultiplaValue(this.grouplist, data) == -1 && this.indexOfByMultiplaValue(this.keylist, data) == -1 && this.IsDisable != "true") {
            this.grouplist.push(groupjson);
        }
    }
    addToValue($event: any) {

        let data = $event.dragData
        let len = this.valuelist.length;
        let valuejson = {};
        valuejson["id"] = len;
        valuejson["itemName"] = data["itemName"]

        valuejson["type"] = data["type"]
        valuejson["uuid"] = data["uuid"]
        valuejson["attrId"] = data["attrId"]
        valuejson["attrName"] = data["attrName"];
        if (data.type == "formula" || data.type == "expression") {
            valuejson["name"] = data["name"]
            if (this.indexOfBySingleValue(this.valuelist, data) == -1 && this.IsDisable != "true") {
                this.IsEmpty = "false"
                this.valuelist.push(valuejson);

            }
        }
        else if (this.indexOfByMultiplaValue(this.valuelist, data) == -1 && this.IsDisable != "true") {
            valuejson["name"] = data["attrName"]
            this.IsEmpty = "false"
            this.valuelist.push(valuejson);
        }
    }

    removeValue(index) {
        if (this.IsDisable != "true") {
            this.valuelist.splice(index, 1);
        }
    }//End removeValue
    removeGroup(index) {
        if (this.IsDisable != "true") {
            this.grouplist.splice(index, 1);
        }
    }//End removeValue

    removeKey(index) {
        if (this.IsDisable != "true") {
            this.keylist.splice(index, 1);
        }
    }//End removeValue

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
        let vizpodjson = {}
        vizpodjson["uuid"] = this.vizpoddata.uuid;
        vizpodjson["name"] = this.vizpoddata.name;
        vizpodjson["desc"] = this.vizpoddata.desc;
        var tagArray = [];
        if (this.vizpoddata.tags != null) {
            for (var counttag = 0; counttag < this.vizpoddata.tags.length; counttag++) {
                tagArray[counttag] = this.vizpoddata.tags[counttag].value;

            }
        }
        vizpodjson['tags'] = tagArray;
        vizpodjson["active"] = this.vizpoddata.active == true ? 'Y' : "N"
        vizpodjson["published"] = this.vizpoddata.published == true ? 'Y' : "N"
        let dependsOn = {};
        let ref = {};
        ref["type"] = this.source
        ref["uuid"] = this.sourcedata.uuid;
        dependsOn["ref"] = ref;
        vizpodjson["source"] = dependsOn;
        vizpodjson["type"] = this.vizpodtype;
        vizpodjson["limit"] = this.vizpoddata.limit;

        let attributeInfoArray = [];
        if (this.vizpoddata.detailAttr != null) {
            for (let i = 0; i < this.vizpoddata.detailAttr.length; i++) {
                let attributeInfo = {}
                let ref = {};
                ref['type'] = "datapod";
                ref['uuid'] = this.vizpoddata.detailAttr[i].uuid;
                attributeInfo['ref'] = ref;
                attributeInfo["attributeId"] = this.vizpoddata.detailAttr[i].attrId
                attributeInfoArray[i] = attributeInfo;
            }
        }
        vizpodjson['detailAttr'] = attributeInfoArray;

        let key = []
        if (this.keylist.length > 0) {
            for (let i = 0; i < this.keylist.length; i++) {
                let ref = {};
                let keyjson = {};
                ref["uuid"] = this.keylist[i].uuid
                ref["type"] = this.keylist[i].type;
                keyjson["ref"] = ref;
                keyjson["attributeId"] = this.keylist[i].attrId
                key[i] = keyjson;
            }
        }
        vizpodjson["keys"] = key
        let group = []
        if (this.grouplist.length > 0) {
            for (let i = 0; i < this.grouplist.length; i++) {
                let ref = {};
                let groupjson = {};
                ref["uuid"] = this.grouplist[i].uuid
                ref["type"] = this.grouplist[i].type;
                groupjson["ref"] = ref;
                groupjson["attributeId"] = this.grouplist[i].attrId
                group[i] = groupjson;
            }
        }
        vizpodjson["groups"] = group
        let value = []
        if (this.valuelist.length > 0) {
            for (let i = 0; i < this.valuelist.length; i++) {
                let ref = {};
                let valuejson = {};
                ref["uuid"] = this.valuelist[i].uuid
                ref["type"] = this.valuelist[i].type;
                valuejson["ref"] = ref;
                if (this.valuelist[i].type == "datapod") {
                    valuejson["attributeId"] = this.valuelist[i].attrId
                }
                value[i] = valuejson;
            }
        }
        vizpodjson["values"] = value
        vizpodjson['filterInfo'] = [],
            vizpodjson['dimension'] = [],
            console.log(JSON.stringify(vizpodjson));
        this._vizpodService.submit(vizpodjson, 'vizpod', upd_tag).subscribe(
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
        this.router.navigate(['app/dataVisualization/vizpod', uuid, version, 'false']);
        this.dropdownSettings.disabled =true
         
    }
    showview(uuid, version) {
        this.router.navigate(['app/dataVisualization/vizpod', uuid, version, 'true']);
        this.dropdownSettings.disabled =false;
        
    }

}