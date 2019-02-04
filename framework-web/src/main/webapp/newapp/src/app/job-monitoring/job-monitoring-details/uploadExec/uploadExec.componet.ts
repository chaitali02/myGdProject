import { AppConfig } from './../../../app.config';
import { SelectItem } from 'primeng/primeng';
import { Component, ViewChild } from "@angular/core";
import { DatePipe, Location } from "@angular/common";
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../../metadata/services/common.service';
import { Version } from '../../../shared/version';
import { KnowledgeGraphComponent } from '../../../shared/components/knowledgeGraph/knowledgeGraph.component';


@Component({
    selector: 'app-uploadExec',
    styleUrls: [],
    templateUrl: './uploadExec.template.html',

})

export class UploadExecComponent {
    showGraph: boolean;
    isHomeEnable: boolean;

    breadcrumbDataFrom: any;
    id: any;
    version: any;
    VersionList: SelectItem[] = [];
    selectedVersion: Version;
    mode: any;
    uploadData: any;
    uuid: any;
    name: any;
    createdBy: any;
    createdOn: any;
    tags: any;
    desc: any;
    active: any;
    published: any;
    statusList: any;
    dependsOn: any;
    refKeyList: any;
    location: any;
    filename: any;
    showResultTrain: any;
    @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;

    constructor(private datePipe: DatePipe, private _location: Location, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService) {
        this.showResultTrain = true;
        this.uploadData = {};
        this.isHomeEnable = false;
        this.showGraph = false;
        this.breadcrumbDataFrom = [{
            "caption": "Job Monitoring ",
            "routeurl": "/app/jobMonitoring"
        },
        {
            "caption": "upload Exec",
            "routeurl": "/app/list/uploadExec"
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
        });
        if (this.mode !== undefined) {
            this.getOneByUuidAndVersion(this.id, this.version)
            this.getAllVersionByUuid()

        }
    }

    showDagGraph(uuid, version) {
        this.isHomeEnable = true;
        this.showGraph = true;
        setTimeout(() => {
            this.d_KnowledgeGraphComponent.getGraphData(this.id, this.version);
        }, 1000);
    }

    onChangeActive(event) {
        if (event === true) {
            this.uploadData.active = 'Y';
        }
        else {
            this.uploadData.active = 'N';
        }
    }

    onChangePublished(event) {
        if (event === true) {
            this.uploadData.published = 'Y';
        }
        else {
            this.uploadData.published = 'N';
        }
    }

    getOneByUuidAndVersion(id, version) {
        this._commonService.getOneByUuidAndVersion(id, version, 'uploadexec')
            .subscribe(
                response => {//console.log(response)},
                    this.onSuccessgetOneByUuidAndVersion(response)
                },
                error => console.log("Error :: " + error));
    }
    getAllVersionByUuid() {
        this._commonService.getAllVersionByUuid('uploadexec', this.id)
            .subscribe(
                response => {
                    this.OnSuccesgetAllVersionByUuid(response)
                },
                error => console.log("Error :: " + error));
    }
    onSuccessgetOneByUuidAndVersion(response) {
        this.uploadData = response
        this.createdBy = this.uploadData.createdBy.ref.name;
        this.dependsOn = this.uploadData.dependsOn.ref.name;
        this.location = this.uploadData.location
        this.filename = this.uploadData.fileName
        this.published = response['published'];
        if (this.published === 'Y') { this.published = true; } else { this.published = false; }
        this.active = response['active'];
        if (this.active === 'Y') { this.active = true; } else { this.active = false; }
        this.tags = response['tags'];

        this.breadcrumbDataFrom[2].caption = this.uploadData.name;
    }
    OnSuccesgetAllVersionByUuid(response) {
        var temp = []
        for (const i in response) {
            let ver = {};
            ver["label"] = response[i]['version'];
            ver["value"] = {};
            ver["value"]["label"] = response[i]['version'];
            ver["value"]["uuid"] = response[i]['uuid'];
            temp[i] = ver;
        }
        this.VersionList = temp
    }
    onVersionChange() {
        this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'uploadexec')
            .subscribe(
                response => {//console.log(response)},
                    this.onSuccessgetOneByUuidAndVersion(response)
                },
                error => console.log("Error :: " + error));
    }

    public goBack() {
        this._location.back();
    }

    showMainPage() {
        this.isHomeEnable = false
        // this._location.back();
        this.showGraph = false;
    }

}