import { Component } from '@angular/core'
import { AppConfig } from '../../../app.config';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../../metadata/services/common.service';
import { CommonListService } from '../../../common-list/common-list.service';
import { OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { Observable } from 'rxjs/Observable';
import { MigrationAssistService } from '../../../metadata/services/migration-assist.services';
@Component({
    selector: 'app-migration-assist-import',
    templateUrl: './migration-assist-import.template.html'
})
export class MigrationAssistImportComponent implements OnInit {
    selectAllAttributeRow: any;
    breadcrumbDataFrom: any;
    showImportData: any;
    importData: any;
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
    active: any;
    published: any;
    depends: any;
    allName: any;
    msgs: any;
    isSubmitEnable: any;
    metaTypes: any[];
    metaType: any
    metaInfoArray: any[];
    dropdownSettingsMeta: any;
    metaInfo: any;
    includeDep: any;
    location: any;
    cols: any;
    selectalljoinkey: any;
    myFile: File;
    metaInfoUpload: any;
    isValidate : any;
    constructor(private _location: Location, private config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private _commonListService: CommonListService, private _migrationAssistService: MigrationAssistService) {
        this.showImportData = true;
        this.importData = {};
        this.importData["active"] = true
        this.isSubmitEnable = false;
        this.isValidate =false;
        this.breadcrumbDataFrom = [{
            "caption": "Admin",
            "routeurl": "/app/list"
        },
        {
            "caption": "Migration Assist",
            "routeurl": "/app/list/migration-assist"
        },
        {
            "caption": "Import",
            "routeurl": "/app/list/migration-assist/import"
        },
        {
            "caption": "",
            "routeurl": null
        }
        ]

        this.cols = [
            //  {field: 'uuid', header: 'UUID'},
            { field: 'type', header: 'Type' },
            { field: 'name', header: 'Name' },
            { field: 'version', header: 'Version' },
            //{ field: 'createdBy.ref.name', header: 'Created By'},            
            // {field: 'status', header: 'Status'},
        ];

       
    }
    ngOnInit() {
        this.activatedRoute.params.subscribe((params: Params) => {
            this.id = params['id'];
            this.version = params['version'];
            this.mode = params['mode'];

            if (this.mode !== undefined) {
                this.getOneByUuidAndVersion();
               // this.dropdownSettingsMeta.disabled = "this.mode !== undefined" ? false : true
                // this.getAllVersionByUuid();
            }
            else {
                this.getAll();
            }
        })
        this.metaType = {};
        this.metaInfoArray = null;
    }

    getAll() {
        this._commonService.getAll('meta')
            .subscribe(
            response => { this.onSuccessGetAll(response) },
            error => console.log("Error :: " + error)
            )
    }

    onSuccessGetAll(response) {
        var temp = []
        for (const i in response) {
            let meta = {};
            meta["label"] = response[i]['name'];
            meta["value"] = {};
            meta["value"]["label"] = response[i]['name'];
            meta["value"]["uuid"] = response[i]['uuid'];
            //allName["uuid"]=response[i]['uuid']
            temp[i] = meta;
        }
        this.metaTypes = temp
        this.getAllLatest();
    }

    getAllLatest() {
        this._commonService.getAllLatest(this.metaType.label)
            .subscribe(
            response => {
                this.onSuccessGetAllLatest(response)
            },
            error => console.log("Error :: " + error));
    }

    onSuccessGetAllLatest(response) {
        this.metaInfoArray = [];

        for (const i in response) {
            let metaref = {};
            metaref["id"] = response[i]['uuid'];
            metaref["itemName"] = response[i]['name'];
            metaref["version"] = response[i]['version'];
            // metaref["type"] = response[i]['type'];
            this.metaInfoArray[i] = metaref;
        }
    }

    onItemSelect(item: any) {
        //console.log(item);
        // console.log(this.selectedItems);
    }
    OnItemDeSelect(item: any) {
        // console.log(item);
        // console.log(this.selectedItems);
    }
    onSelectAll(items: any) {
        // console.log(items);
    }
    onDeSelectAll(items: any) {
        // console.log(items);
    }

    getOneByUuidAndVersion() {
        this._commonService.getOneByUuidAndVersion(this.id, this.version, 'import')
            .subscribe(
            response => {
                this.onSuccessgetOneByUuidAndVersion(response)
            },
            error => console.log("Error :: " + error));
    }

    getAllVersionByUuid() {
        this._commonService.getAllVersionByUuid('export', this.id)
            .subscribe(
            response => {
                this.OnSuccesgetAllVersionByUuid(response)
            },
            error => console.log("Error :: " + error));
    }

    onSuccessgetOneByUuidAndVersion(response) {
        this.breadcrumbDataFrom[3].caption = response.name;
        this.importData = response;
        this.createdBy = response.createdBy.ref.name;
        this.uuid = response.uuid;
        this.version = response['version'];
        // this.published = response['published'];
        // if(this.published === 'Y') { this.published = true; } else { this.published = false; }
        this.active = response['active'];
        if (this.active === 'Y') { this.active = true; } else { this.active = false; }

        //this.application.published=response["published"] == 'Y' ? true : false
        this.importData.active = response["active"] == 'Y' ? true : false
        let metaInfoArray1 = [];

        for (const i in response.metaInfo) {
            let metaInfo = {}
            metaInfo["id"] = response.metaInfo[i].ref.uuid;
            metaInfo["itemName"] = response.metaInfo[i].ref.name;
            metaInfo["version"] = response.metaInfo[i].ref.version;
            metaInfo["type"] = response.metaInfo[i].ref.type;
            metaInfoArray1[i] = metaInfo;
        }
        this.metaInfo = metaInfoArray1;

        this.location = response.location;
        this.includeDep = response.includeDep;
        this.getAll();
    }

    OnSuccesgetAllVersionByUuid(response) {
        this.versions = [];
        for (const i in response) {
            let version = {};
            version["label"] = response[i]['version'];
            version["value"] = response[i]['version'];
            version["uuid"] = response[i]['uuid'];
            this.versions[i] = version;
        }
    }

    onChangeActive(event) {
        if (event === true) {
            this.importData.active = 'Y';
        }
        else {
            this.importData.active = 'N';
        }
    }

    onChangeCheckbox(event) {
        if (event === true) {
            this.importData.includeDep = 'Y';
        }
        else {
            this.importData.includeDep = 'N';
        }
    }

    submitImport() {
        let importJson = {};

        // exportJson["uuid"]=this.exportData.uuid;
        importJson["name"] = this.importData.name;
        //let tagArray=[];
        const tagstemp = [];
        for (const t in this.tags) {
            tagstemp.push(this.tags[t]["value"]);
        }
        // if(this.tags.length > 0){
        //   for(let counttag=0;counttag < this.tags.length;counttag++){
        //     tagArray[counttag]=this.tags[counttag]["value"];
        //   }
        // }
        importJson["tags"] = tagstemp;
        importJson["desc"] = this.importData.desc;

        let metaInfoNew = [];
        if (this.metaInfo != null) {
            for (const c in this.metaInfo) {
                let metaInfoObj = {};
                let refMetaInfo = {}
                refMetaInfo["type"] = this.metaType.label
                refMetaInfo["uuid"] = this.metaInfo[c].id
                refMetaInfo["name"] = this.metaInfo[c].itemName
                refMetaInfo["version"] = this.metaInfo[c].version

                metaInfoObj["ref"] = refMetaInfo;
                metaInfoNew[c] = metaInfoObj
            }
        }
        importJson["metaInfo"] = metaInfoNew;

        importJson["active"] = this.importData.active == true ? 'Y' : "N"
        importJson["includeDep"] = this.importData.includeDep;
        console.log(JSON.stringify(importJson));
        this._commonService.submit("export", importJson).subscribe(
            response => { this.OnSuccessubmit(response) },
            error => console.log('Error :: ' + error)
        )
    }


    OnSuccessubmit(response) {
        console.log(response)
        this.msgs = [];
        this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Import Submitted Successfully' });
        setTimeout(() => {
            this.goBack()
        }, 1000);
    }

    public goBack() {
          this._location.back();
    }

    selectAllSubRow(index) {
        console.log("selected");
        this.isValidate = true;
        (this.metaInfo[index]).forEach(attribute => {
            attribute.selected = this.selectAllAttributeRow;
        });

    }

    fileChange(files: any) {
        debugger
        console.log("file upload");
        console.log(files);
        this.myFile = files[0].nativeElement;

        var f = files[0];
        console.log(f);
        var filetype = f.type.split('/')[1];
        console.log(filetype);
        var fd = new FormData();
        fd.append('file', f);
        var filename = f.name.split('.')[0];
        fd.append('fileName', filename);
        console.log(fd);

        this._migrationAssistService.uploadFile(fd, f.name, "import", filetype)
            .subscribe(
            response => {
                this.onSuccessUpload(response);
                // jQuery(this.fileupload.nativeElement).modal('hide');
                // this.msgs = [];
                // this.msgs.push({severity:'success', summary:'Success Message', detail:'Datapod Uploaded Successfully'});

            })
    }

    onSuccessUpload(response) {
        var obj = JSON.parse(response._body);
        this.metaInfoUpload = [];
        for (const i in obj.metaInfo) {
            let metaInfoUpload = {};
            metaInfoUpload["type"] = obj.metaInfo[i].ref.type;
            metaInfoUpload["name"] = obj.metaInfo[i].ref.name;
            if(obj.metaInfo[i].ref.vesion == null ?
                metaInfoUpload["version"] = " " :
                metaInfoUpload["version"] = obj.metaInfo[i].ref.vesion
            )
            metaInfoUpload["status"] = "";
            this.metaInfoUpload[i] = metaInfoUpload;
        }
    }
}

