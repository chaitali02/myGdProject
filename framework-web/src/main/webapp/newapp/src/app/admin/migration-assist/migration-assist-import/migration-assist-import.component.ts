import { Component } from '@angular/core'
import { AppConfig } from '../../../app.config';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../../metadata/services/common.service';
import { CommonListService } from '../../../common-list/common-list.service';
import { OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { Observable } from 'rxjs/Observable';
import { MigrationAssistService } from '../../../metadata/services/migration-assist.services';
import { AppMetadata } from '../../../app.metadata';

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
  
    metaType: any
    
    dropdownSettingsMeta: any;
    metaInfo: any;
    includeDep: any;
    location: any;
    cols: any;
    selectalljoinkey: any;
    myFile: File;
    metaInfoUpload: any;
    isValidate: any;
    filename: any;
    importTags: any;
    selectedRows: any;
    count : any;
    statusPath : any;
    statusColor : any;
    statusCaption : any;
    statusInfo : any[];
    statusType : any;
    constructor(private _location: Location, private config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private _commonListService: CommonListService, private _migrationAssistService: MigrationAssistService, private _appMetaData : AppMetadata) {
        this.showImportData = true;
        this.importData = {};
        this.importData["active"] = true
        this.isSubmitEnable = false;
        this.isValidate = true;
        this.count =0;
        this.breadcrumbDataFrom = [{
            "caption": "Admin",
            "routeurl": "/app/admin/migration-assist"
        },
        {
            "caption": "Migration Assist",
            "routeurl": "/app/admin/migration-assist"
        },
        {
            "caption": "Import",
            "routeurl": "/app/admin/migration-assist"
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
                this.isSubmitEnable = true;
            }
           
        })
        this.metaType = {};
        
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
       
        this.location = response.location;
        this.includeDep = response.includeDep;
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

        console.log(this.filename)
        let metainfoarray = []
        for (var i = 0; i < this.metaInfoUpload.length; i++) {
            var metainfo = {};
            var ref = {}
            ref["type"] = this.metaInfoUpload[i].type
            ref["uuid"] = this.metaInfoUpload[i].uuid;
            ref["name"] = this.metaInfoUpload[i].name;
            ref["version"] = this.metaInfoUpload[i].version;
            metainfo["ref"] = ref;
            metainfoarray[i] = metainfo;
        }
        importJson["metaInfo"] = metainfoarray;

        importJson["includeDep"] = this.importData.includeDep == true ? 'Y' : "N";
        console.log(JSON.stringify(importJson));


        this._migrationAssistService.importSubmit(this.filename,"import",importJson).subscribe(
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

    checkAllAttributeRow() {
        this.isValidate = false;
        console.log("selected");
        if (this.selectAllAttributeRow) {
            this.selectAllAttributeRow = true;
        }
        else {
            this.selectAllAttributeRow = false;
        }

        console.log(this.selectAllAttributeRow);
        this.metaInfoUpload.forEach(tablejson => {
            tablejson.selected = this.selectAllAttributeRow;
            console.log(JSON.stringify(tablejson))
        });
    }

    fileChange(files: any) {
        console.log("file upload");
        console.log(files);
        this.myFile = files[0].nativeElement;
        console.log("file name is" + this.myFile);

        var f = files[0];
        console.log(f);
        var filetype = f.type.split('/')[1];
        console.log(filetype);
        var fd = new FormData();
        fd.append('file', f);
        this.filename = f.name.split('.')[0];
        fd.append('fileName', this.filename);
        console.log(fd);

        this._migrationAssistService.uploadFile(fd, f.name, "import", filetype)
            .subscribe(
            response => {
                this.onSuccessUpload(response);
            })
    }

    onSuccessUpload(response) {
        var obj = JSON.parse(response._body);
        this.metaInfoUpload = [];
        for (const i in obj.metaInfo) {
            let metaInfoUpload = {};
            metaInfoUpload["type"] = obj.metaInfo[i].ref.type;
            metaInfoUpload["name"] = obj.metaInfo[i].ref.name;
            metaInfoUpload["uuid"] = obj.metaInfo[i].ref.uuid;
            if (obj.metaInfo[i].ref.version == null ?
                metaInfoUpload["version"] = " " :
                metaInfoUpload["version"] = obj.metaInfo[i].ref.version
            )
            metaInfoUpload["status"] = this._appMetaData.getStatusDefs("Resume")['caption'];
            this.metaInfoUpload[i] = metaInfoUpload;
        }
    }

    clickOnCheckbox(i) {
        console.log(this.metaInfoUpload[i].selected);
        if(this.metaInfoUpload[i].selected == true)
        { this.count++ }
        else{this.count--}
       
        if(this.count !== 0 )
        {this.isValidate = false}
    }

    validate() {
        var importJson1 = {};
        importJson1["includeDep"] = this.importData.includeDep == true ? 'Y' : "N";
        importJson1["name"] = this.importData.name;
        //  var filename = f.name.split('.')[0];

        console.log(this.filename)

        let metainfoarray = [];
        this.selectAllAttributeRow = false
        this.metaInfoUpload.forEach(selectedRows1 => {
            if (selectedRows1.selected) {
                metainfoarray.push(selectedRows1);
            }
            this.selectedRows = selectedRows1;

        });
        this.importTags = metainfoarray
        console.log(JSON.stringify(this.importTags))
     
        for (var i = 0; i < this.importTags.length; i++) {
            var metainfo = {};
            var ref = {}
            ref["type"] = this.importTags[i].type
            ref["uuid"] = this.importTags[i].uuid;
            ref["name"] = this.importTags[i].name;
            ref["version"] = this.importTags[i].version;
            metainfo["ref"] = ref;
            metainfoarray[i] = metainfo;
        }
        importJson1["metaInfo"] = metainfoarray;
        console.log(JSON.stringify(importJson1));

        this._migrationAssistService.validateDependancy(this.filename, importJson1)
            .subscribe(
            response => { this.onSuccessSubmit1(response) },
            error => console.log('Error :: ' + error))
    }
    onSuccessSubmit1(response) {
        let validateResponse = JSON.parse(response);
        let validateArray = [];
        for (const i in validateResponse.metaInfo) {
            let validateObj = {};
            let ref = {};
            ref["uuid"] = validateResponse.metaInfo[i].ref.uuid;
            ref["type"] = validateResponse.metaInfo[i].ref.type;
            validateObj["ref"] = ref;
            validateObj["status"] = validateResponse.metaInfo[i].status;
            validateArray[i] = validateObj;
        }

        for (const i in this.metaInfoUpload) {
            for (const j in validateArray) {
                if (this.metaInfoUpload[i].uuid == validateArray[j].ref.uuid) { 
                    if( validateArray[j]["status"]== 'true'){            
                        let st1 =  this._appMetaData.getStatusDefs("Completed")['caption'];           
                        this.metaInfoUpload[i]["status"] = this._appMetaData.getStatusDefs("Completed")['caption']; 
                    } 
                    else if( validateArray[j]["status"] == 'false'){
                        let st2 = this._appMetaData.getStatusDefs("Failed")['caption'];
                        this.metaInfoUpload[i]["status"] = this._appMetaData.getStatusDefs("Failed")['caption'];
                    }
                }
                else { console.log("validation error") }
            }
        }

        for (const i in this.metaInfoUpload) {
            let check = "on"
            this.isSubmitEnable = true;
            if ((this.metaInfoUpload[i]["status"] == "Resume" || this.metaInfoUpload[i]["status"] == "Failed") && check !== "off") {
                check = "off";              
            }
            if (check == "off")
            {this.isSubmitEnable = false}
        }
    }

}

