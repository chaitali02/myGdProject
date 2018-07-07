import { Http } from "@angular/http";
import { SharedService } from "../../shared/shared.service";
import { DatePipe } from "@angular/common";
import { Router, ActivatedRoute, Params } from "@angular/router";
import { AppMetadata } from "../../app.metadata";
import { CommonService } from "../../metadata/services/common.service";
import { OnInit, Component } from "@angular/core";
import { CommonListService } from "../../common-list/common-list.service";

@Component({
    selector: 'app-results',
    templateUrl: './resultsCommon.component.html'
    // styleUrls: ['./resultsCommon.component.css']
  })
export class ResultsComponent implements OnInit {
  arrayNames: any[];
  type: any;
  mode: any;
  version: any;
  id: any;
  name1 :  any;
  // clear: any;
  breadcrumbDataFrom : any;
  startDate :any;
  tags : any;
  endDate : any;
  status : any;
  typesArray : any[];
  typeName : any;
  allActive = [
    {
      "caption": "All",
      "name": " ",
      "label": "All",
      "value": " "
    },
    {
      "caption": "Active",
      "name": "Y",
      "label": "Active",
      "value": "active"
    },
    {
      "caption": "Expired",
      "name": "N",
      "label": "Expired",
      "value": "expired"
    }
  ];
    constructor(private http: Http, public _sharedService: SharedService, public datePipe: DatePipe, public router: Router, public metaconfig: AppMetadata, private activatedRoute: ActivatedRoute, private _commonService: CommonService, private activeroute: ActivatedRoute,private _commonListService : CommonListService) {
    this.breadcrumbDataFrom = [{
      "caption":"Data Science ",
      "routeurl":"/app/list/trainexec"
    },
    // {
    //   "caption":"Results",
    //   "routeurl":"/app/list/modelexec"
    // },
    {
      "caption":"Model Results",
      "routeurl":null 
    }
    ];
    this.typesArray = [
      {'label' : 'training', 'value' : 'training'},
      {'label' : 'prediction', 'value' : 'prediction'},
      {'label' : 'simulation', 'value' : 'simulation'},
      {'label' : 'operator', 'value' : 'operator'}
    ]
   // this.clear();
    //this.view = this.showDiv == 'true' ? 'graph' : 'all';
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
      this.type = params['type'];
    })
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

  getAllLatest(){debugger
    if(this.type == 'training'){
      this.typeName = 'train';
    }else if(this.type == 'prediction'){
      this.typeName = 'predict';
    }else if(this.type == 'simulation'){
      this.typeName = 'simulate';
    }else if(this.type == 'operator'){
      this.typeName = 'operator';
    }
    this._commonService.getAllLatest(this.typeName).subscribe(
      response => {this.onSuccessGetAllLatest(response)},
      error => console.log("Error:"+error)
    )
  }

  onSuccessGetAllLatest(response){debugger
    this.arrayNames = []
    for(const i in response){
      let namesObj = {};
      namesObj["label"] = response[i].name;
      namesObj["value"] = {};
      namesObj["value"]["label"] = response[i].name;
      namesObj["value"]["uuid"] = response[i].uuid;
      this.arrayNames[i]=namesObj;
    }
  }

  onChangeType(){
    this.getAllLatest();
  }

  getBaseEntityStatusByCriteria(){
    this._commonListService.getBaseEntityByCriteria("trainexec","","","","","","","").subscribe(
      response => {this.onSuccess(response)},
      error => console.log("Error:"+error)
    )
  }
  
  onSuccess(response){
    console.log("Done:")
  }
}