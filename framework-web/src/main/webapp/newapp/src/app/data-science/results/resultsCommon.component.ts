import { Http } from "@angular/http";
import { SharedService } from "../../shared/shared.service";
import { DatePipe } from "@angular/common";
import { Router, ActivatedRoute, Params } from "@angular/router";
import { AppMetadata } from "../../app.metadata";
import { CommonService } from "../../metadata/services/common.service";
import { Component, ViewEncapsulation, ElementRef, Renderer, NgZone, ViewChild, Input, } from "@angular/core";
import { CommonListService } from "../../common-list/common-list.service";
import { AppHepler } from '../../app.helper';
@Component({
    selector: 'app-results',
    templateUrl: './resultsCommon.component.html'
    // styleUrls: ['./resultsCommon.component.css']
})
export class ResultsComponent {
    msgs: any[];
    restartVersion: any;
    restartId: any;
    isModel: string;
    killStatus: any;
    killVersion: any;
    killId: any;
    rowStatus1: any;
    routerUrl: any;
    execType: string;
    gridTitle: string;
    rowStatus: any;
    rowName: any;
    rowID: any;
    rowVersion: any;
    rowUUid: any;
    rowData1: any;
    _RowData: any;
    selectedType: any;
    columnDefs: any;
    isExec: string;
    @ViewChild('ParamsModel') ParamsModel: ElementRef;
    ModelDataFrom: { "caption": string; "message": any; "functionName": any };
    items: ({ label: string; icon: string; disabled: any; command: (onclick: any) => void; } | { label: string; icon: string; visible: boolean; command: (onclick: any) => void; } | any)[];
    arrayNames: any[];
    type: any;
    mode: any;
    version: any;
    id: any;
    name: any = "";
    // clear: any;
    breadcrumbDataFrom: any;
    startDate: any = "";
    tags: any = "";
    endDate: any = "";
    status: any
    typesArray: any[];
    typeName: any;
    allStatus = [
        {
            "caption": "All",
            "name": " ",
            "label": "All",
            "value": " "
        },
        {
            "caption": "Not Started",
            "name": "NotStarted",
            "label": "Not Started",
            "value": "NotStarted"
        },
        {
            "caption": "In Progress",
            "name": "InProgress",
            "label": "In Progress",
            "value": "InProgress"
        },
        {
            "caption": "Completed",
            "name": "Completed",
            "label": "Completed",
            "value": "Completed"
        },
        {
            "caption": "Killed",
            "name": "Killed",
            "label": "All",
            "value": ""
        },
        {
            "caption": "Failed",
            "name": "Failed",
            "label": "Failed",
            "value": "Failed"
        }
    ];
    constructor(public apphelper: AppHepler, private http: Http, public _sharedService: SharedService, public datePipe: DatePipe, public router: Router, public metaconfig: AppMetadata, private activatedRoute: ActivatedRoute, private _commonService: CommonService, private activeroute: ActivatedRoute, private _commonListService: CommonListService) {
        this.rowStatus = {}
        this.isModel = "false"
        this.breadcrumbDataFrom = [{
            "caption": "Data Science ",
            "routeurl": "/app/dataScience/results"
        },
        // {
        //   "caption":"Results",
        //   "routeurl":"/app/list/modelexec"
        // },
        {
            "caption": "Model Results",
            "routeurl": null
        }
        ];
        this.typesArray = [
            { 'label': 'training', 'value': 'training' },
            { 'label': 'prediction', 'value': 'prediction' },
            { 'label': 'simulation', 'value': 'simulation' },
            // {'label' : 'operator', 'value' : 'operator'}
        ]
        // this.clear();
        //this.view = this.showDiv == 'true' ? 'graph' : 'all';
        this.activatedRoute.params.subscribe((params: Params) => {
            this.id = params['id'];
            this.version = params['version'];
            this.mode = params['mode'];
            this.type = params['type'];
        })
        this.gridTitle = "Training"
    }

    ngOnInit() {
        //this.rowData1=null;
        this.type = 'training';
        this.getAllLatest();
        this.getBaseEntityStatusByCriteria();
        // this.getAllLatestUser();
        // this.getAllMeta();

        // this.cols = [
        //   //{field: 'uuid', header: 'UUID'},
        //   { field: 'appName', header: 'Application' },
        //   // {field: 'version', header: 'Version'},
        //   { field: 'createdBy.ref.name', header: 'User' },
        //   { field: 'createdOn', header: 'Created On' },
        //   { field: 'status', header: 'Status' },
        // ];
        // this.gridTitle = "Sessions Details"
        // this.getActiveSession();
        // this.items = [
        //   {
        //     label: 'View', icon: 'fa fa-eye', command: (onclick) => {
        //       this.viewPage(this.rowUuid, this.rowVersion)
        //     }
        //   },
        //   {
        //     label: 'Kill', icon: 'fa fa-pencil-square-o', command: (onclick) => {
        //       //this.edit(this.rowUUid,this.rowVersion)
        //     }
        //   }]
    }

    getAllLatest() {
        if (this.type == 'training') {
            this.execType = "trainexec"
            this.typeName = 'train';
            this.gridTitle = "Training"
        } else if (this.type == 'prediction') {
            this.execType = "predictexec"
            this.typeName = 'predict';
            this.gridTitle = "Prediction"
        } else if (this.type == 'simulation') {
            this.execType = "simulateexec"
            this.typeName = 'simulate';
            this.gridTitle = "Simulation"
        }
        this._commonService.getAllLatest(this.typeName).subscribe(
            response => { this.onSuccessGetAllLatest(response) },
            error => console.log("Error:" + error)
        )
    }

    onSuccessGetAllLatest(response) {
        this.arrayNames = []
        for (const i in response) {
            let namesObj = {};
            namesObj["label"] = response[i].name;
            namesObj["value"] = {};
            namesObj["value"]["label"] = response[i].name;
            namesObj["value"]["uuid"] = response[i].uuid;
            this.arrayNames[i] = namesObj;
        }
    }

    onChangeType() {
        this.getAllLatest();
        this.onSearchCriteria()
    }

    getBaseEntityStatusByCriteria() {
        this._commonListService.getBaseEntityByCriteria("trainexec", "", "", "", "", "", "", "").subscribe(
            response => { this.onSuccess(response) },
            error => console.log("Error:" + error)
        )
    }

    onSuccess(response) {
        // let len = response[0].status.length;
        // let menuStatus= response[0].status[len-1]["stage"];
        this.items = [
            {// menuStatus!= 'Completed' || this.rowStatus.stage != 'Inprogress')
                label: 'View', icon: 'fa fa-eye', command: (onclick) => {
                    this.view(this.rowUUid, this.rowVersion, this.rowStatus)
                }, disabled: (false)
            },
            { //menuStatus != 'Inprogress'
                label: 'Kill', icon: 'fa fa-times', disabled: (false), command: (onclick) => {
                    this.kill(this.rowUUid, this.rowVersion, this.rowStatus)
                }
            },
            {//menuStatus != 'Failed'
                label: 'Restart', icon: 'fa fa-repeat', disabled: (false), command: (onclick) => {
                    this.restart(this.rowUUid, this.rowVersion)
                }
            },
        ]

        for (let i = 0; i < response.length; i++) {
            if (response[i]["status"] != null) {
                response[i]["status"] = this.apphelper.sortByProperty(response[i]["status"], "createdOn");
                let status = response[i]["status"];
                response[i]["status"] = {};
                response[i]["status"].stage = this.apphelper.getStatus(status)["stage"];
                response[i]["status"].color = this.metaconfig.getStatusDefs(response[i]["status"].stage)['color'];
            }
        }
        this._RowData = response;
        this.rowData1 = this._RowData;
        this.getAllLatest();
        //this.getAllLatestUser();

    }

    onClickMenu(data) {
        // alert(this.type)
        console.log(data);
        this.rowUUid = data.uuid
        this.rowVersion = data.version
        this.rowID = data.id
        this.rowName = data.name
        this.rowStatus = data.status
        if (data.status != null) {
            this.items[0].disabled = ['Completed'].indexOf(data.status.stage) == -1
            this.items[1].disabled = ['InProgress'].indexOf(data.status.stage) == -1
            this.items[2].disabled = ['Completed', 'NotStarted', 'Terminating', 'InProgress'].indexOf(data.status.stage) != -1
        }
        // this.rowStatus1 = data.status.stage
    }
    onSearchCriteria() {
        let startDateUtcStr = "";
        let endDateUtcStr = "";
        if (this.startDate) {
            console.log(this.startDate);
            let startDateUtc = new Date(this.startDate.getUTCFullYear(), this.startDate.getUTCMonth(), this.startDate.getUTCDate(), this.startDate.getUTCHours(), this.startDate.getUTCMinutes(), this.startDate.getUTCSeconds())
            console.log(startDateUtc);
            startDateUtcStr = this.datePipe.transform(startDateUtc, "EEE MMM dd hh:mm:ss yyyy").toString() + " UTC"//startDateUtc.toString().split("GMT")[0]+"UTC";
        }
        if (this.endDate) {
            let endDateUtc = new Date(this.endDate.getUTCFullYear(), this.endDate.getUTCMonth(), this.endDate.getUTCDate(), this.endDate.getUTCHours(), this.endDate.getUTCMinutes(), this.endDate.getUTCSeconds())
            endDateUtcStr = this.datePipe.transform(endDateUtc, "EEE MMM dd hh:mm:ss yyyy").toString() + " UTC"//endDateUtc.toString().split("GMT")[0]+"UTC";
        }
        console.log(endDateUtcStr);
        console.log(startDateUtcStr);
        console.log(this.tags);
        this._commonListService.getBaseEntityByCriteria(this.execType, this.name.label || "", startDateUtcStr, this.tags, "", endDateUtcStr, "", this.status || "")
            .subscribe(
            response => { this.onSuccess(response) },
            error => console.log("Error :: " + error)
            )
    }
    view(uuid, version, status) {
        this.router.navigate(["../resultDetails", uuid, version, this.type], { relativeTo: this.activeroute });
    }
    kill(uuid, version, status) {
        this.killId = uuid
        this.killVersion = version;
        this.killStatus = status
        this.isModel = "true"
        this.ModelDataFrom = {
            "caption": this.type,
            "message": "Kill",
            "functionName": "okKill()"
        }
    }
    restart(uuid, version) {
        this.restartId = uuid
        this.restartVersion = version;
        this.isModel = "true"
        this.ModelDataFrom = {
            "caption": this.type,
            "message": "Restart",
            "functionName": "okRestart()"
        }
    }
    okRestart() {
        let type = this.type.split("exec")[0]
        this._commonListService.restart(this.restartId, this.restartVersion, this.execType, "execute")
            .subscribe(
            response => {
                // this.onSearchCriteria()
                // this.isModel = "false";
                // this.msgs = [];
                // this.msgs.push({ severity: 'success', summary: 'Success Message', detail: this.gridTitle + ' Restarted Successfully' });
            },
            error => console.log("Error :: " + error)
            )

    }
    okKill() {
        let type = this.type.split("exec")[0]
        this._commonListService.kill(this.killId, this.killVersion, this.execType, "Killed")
            .subscribe(
            response => {
                // this.onSearchCriteria()
                // this.isModel = "false";
                // this.msgs = [];
                // this.msgs.push({ severity: 'success', summary: 'Success Message', detail: this.gridTitle + ' Killed Successfully' });
            },
            error => console.log("Error :: " + error)
            )

    }
    ok(data) {

        if (data == "okKill()") {
            this.okKill();
        }
        if (data == "okRestart()") {
            this.okRestart();
        }
    }
}