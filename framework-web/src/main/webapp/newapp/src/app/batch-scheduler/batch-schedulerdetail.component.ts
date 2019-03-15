import { MetaType } from './../metadata/enums/metaType';
import { Component, OnInit, ViewChild } from '@angular/core';
import { JointjsComponent } from '../shared/components/jointjs/jointjs.component';
import { JointjsGroupComponent } from '../shared/components/jointjsgroup/jointjsgroup.component';
import { Router, Event as RouterEvent, ActivatedRoute, Params, NavigationEnd } from '@angular/router';
import { CommonService } from '../metadata/services/common.service';
import { JointjsService } from '../shared/components/jointjs/jointjsservice';
import { SharedDataService } from '../data-pipeline/shareddata.service';
import { DataPipelineService } from '../metadata/services/dataPipeline.service';
import { Http, Headers } from '@angular/http';
import { ResponseContentType } from '@angular/http';
import { AppConfig } from '../app.config';
import { saveAs } from 'file-saver';
import { Location } from '@angular/common';
import { CommonListService } from '../common-list/common-list.service';
import { RoutesParam } from '../metadata/domain/domain.routeParams';

@Component({
    selector: 'app-batch-schedulerdetail',
    templateUrl: './batch-schedulerdetail.component.html',
    //styleUrls: ['./batch-schedulerdetail.component.css']
})
export class BatchSchedulerdetailComponent {


    runMode: any;
    isTableShow1: any;
    typeJointJs: any;
    versionJointJs: any;
    uuidJointJs: any;
    baseUrl: any;
    intervalId: any;
    dagexecdata: any;
    version: any;
    id: any;
    msgs: any[];
    dagdata: any;
    mode: any;
    breadcrumbDataFrom: { "caption": string; "routeurl": string; }[];
    metaType = MetaType;
    @ViewChild(JointjsComponent) d_JointjsComponent: JointjsComponent;
    @ViewChild(JointjsGroupComponent) d_JointjsGroupComponent: JointjsGroupComponent;

    constructor(private _location: Location, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private _jointjsService: JointjsService, private _sharedDataService: SharedDataService, private _dataPipelineService: DataPipelineService, private http: Http, private _config: AppConfig, private _commonListService: CommonListService) {
        this.baseUrl = _config.getBaseUrl();
        this.dagdata = {};
        // setTimeout(() => {
        //     // this.d_JointjsComponent.params={};
        //     // this.d_JointjsComponent.IsGroupGraphShow=false
        // }, 1000);

        this.router.events
            .subscribe((event) => {
                if (event instanceof NavigationEnd) {
                    this.stopStatusUpdate();
                    // console.log('NavigationEnd:', event);
                }
            });
        console.log(this.activatedRoute)
        this.activatedRoute.params.subscribe((params: Params) => {
            let param = <RoutesParam>params;
            this.id = param.id;
            this.version = param.version;
            this.mode = param.mode;
            if (this.mode !== undefined) {
                this.getOneByUuidAndVersion(this.id, this.version);
            }
        });
        this.breadcrumbDataFrom = [{
            "caption": "Data Pipeline ",
            "routeurl": "/app/list/dagexec"
        },
        {
            "caption": "Result",
            "routeurl": "/app/list/dagexec"
        },

        {
            "caption": "",
            "routeurl": null
        }
        ]
    }

    public goBack() {
        if (this.d_JointjsComponent.IsGraphShow == true) {
            this._location.back();
        }
        else if (this.d_JointjsComponent.IsGroupGraphShow == true) {
            this.d_JointjsComponent.goBack();
        }
        else if (this.d_JointjsComponent.IsTableShow == true) {
            this.d_JointjsComponent.IsTableShow = false
            this.d_JointjsComponent.IsGraphShow = true;
        }
        else {
            this.d_JointjsComponent.IsGraphShow = true;
        }
    }

    getOneByUuidAndVersion(id, version) {
        this.stopStatusUpdate();
        this._commonService.getOneByUuidAndVersion(id, version, this.metaType.DAGEXEC)
            .subscribe(
                response => {
                    this.onSuccessgetOneByUuidAndVersion(response)
                },
                error => console.log("Error :: " + error)
            );
    }

    onSuccessgetOneByUuidAndVersion(response) {
        this.breadcrumbDataFrom[2].caption = response.name;
        this.dagexecdata = response;
        setTimeout(() => {
            this.d_JointjsComponent.createGraph(this.dagexecdata);
            this.startStatusUpdate(this.id);
        }, 1000);
        this.intervalId = setInterval(() => {
            this.startStatusUpdate(this.id);
        }, 5000);
    }

    latestStatus(statuses) {
        var latest;
        statuses.forEach(function (status) {
            if (latest) {
                if (status.createdOn > latest.createdOn) {
                    latest = status
                }
            }
            else {
                latest = status;
            }
        });
        return latest;
    }

    startStatusUpdate(uuid) {

        this._dataPipelineService.getStatusByDagExec(uuid)
            .subscribe(
                response => {
                    this.onSuccessGetStatusByDagExec(response)
                },
                error => console.log("Error :: " + error)
            );
    }
    onSuccessGetStatusByDagExec = function (response) {
        //     if(latestStatus(response.status).stage == 'Failed'){
        //         $scope.allowReExecution = true;
        //     }
        if (['Completed', 'Failed', 'Killed'].indexOf(this.latestStatus(response.status).stage) > -1) {
            this.stopStatusUpdate();
        }
        //else{
        //     if(!angular.equals(statusCache, response)){
        setTimeout(() => {
            this.d_JointjsComponent.updateGraphStatus(response);
        }, 1000);

        //statusCache = response;
    }
    stopStatusUpdate() {
        //statusCache = undefined;
        if (this.intervalId)
            clearInterval(this.intervalId);
    }

    downloadPipeline() {
        this.uuidJointJs = this.d_JointjsComponent.uuid;
        this.versionJointJs = this.d_JointjsComponent.version;
        this.typeJointJs = this.d_JointjsComponent.type;

        this._commonService.getNumRowsbyExec(this.uuidJointJs, this.versionJointJs, this.metaType.MAPEXEC)
            .subscribe(
                response => {
                    this.onSuccessgetNumRowsbyExec(response);
                },
                error => console.log("Error :: " + error)
            );
    }

    onSuccessgetNumRowsbyExec(response) {
        this.runMode = response.runMode;
        this.downloadResult();
    }

    downloadResult() {
        const headers = new Headers();
        this.http.get(this.baseUrl + '/map/download?action=view&mapExecUUID=' + this.uuidJointJs + '&mapExecVersion=' + this.versionJointJs + '&mode=' + this.runMode,
            { headers: headers, responseType: ResponseContentType.Blob })
            .toPromise()
            .then(response => this.saveToFileSystem(response));
    }

    saveToFileSystem(response) {
        const contentDispositionHeader: string = response.headers.get('Content-Type');
        const parts: string[] = contentDispositionHeader.split(';');
        const filename = parts[1];
        const blob = new Blob([response._body], { type: 'application/vnd.ms-excel' });
        saveAs(blob, filename);
    }
    refershGrid() {
        // this.active = "";
        // this.status = "";
        // this.rowData1 = null;
        // this.execname = {};

        // this.startDate = "";
        // this.tags = "";
        // this.endDate = "";
        // this.username = ""
        this.getBaseEntityByCriteria();
    }
    getBaseEntityByCriteria(): void {
        this._commonListService.getParamListByRule(((this.metaType.BATCHEXEC).toLowerCase()), "", "", "", "", "", "", "")
        // .subscribe(
        //     response => { this.getGrid(response) },
        //     error => console.log("Error :: " + error)
        // )
    }
}
