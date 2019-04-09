  /**
  **/
  MetadataNavigatorModule = angular.module('MetadataNavigatorModule');
  MetadataNavigatorModule.controller('MetadataNavigatorController', function($stateParams,$rootScope,$scope,$sessionStorage,CommonService,$state) {
    var notify = {
      type: 'success',
      title: 'Success',
      content: 'Dashboard deleted Successfully',
      timeout: 3000 //time in ms
    };
    $scope.optionsort = [{
        "caption": "Name A-Z",
        name: "caption"
      },
      {
        "caption": "Name Z-A",
        name: "-caption"
      },
      {
        "caption": "Date Asc",
        name: "lastUpdatedOn"
      },
      {
        "caption": "Date Desc",
        name: "-lastUpdatedOn"
      },
    ]
    $scope.optiondata = {
      "caption": "Name A-Z",
      name: "caption"
    };
    $scope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams) {
     // console.log(fromParams)
      $sessionStorage.fromStateName = fromState.name
      $sessionStorage.fromParams = fromParams

    });
    CommonService.getMetaStats().then(function(response) {onSuccess(response.data)});
    var onSuccess = function(response) {
      var colorclassarray = ["blue-sharp", "green-sharp", "purple-soft", "red-haze"]
      var noMetaType=['message','paramlistrule','paramlistmodel','operatortype','lov','comment','graphExec','paramlistdag','schedule','trainresult','paramlistreport'];
      var metaarray = []
      for (var i = 0; i < response.length; i++) {
        var metajson = {};
        metajson.type = response[i].type;
        metajson.count = response[i].count;
        metajson.lastUpdatedBy = response[i].lastUpdatedBy;
        var date =response[i].lastUpdatedOn.split(" ");
        date.splice(date.length-2,1);
        metajson.lastUpdatedOn = new Date(date.toString().replace(/,/g," "));
        //metajson.lastUpdatedOn = new Date(response[i].lastUpdatedOn.split("IST")[0]);
        var randomno = Math.floor((Math.random() * 4) + 0);
        //metajson.class=colorarray[randomno];
        metajson.class = colorclassarray[randomno];
        var patt = new RegExp("exec");
        var res = patt.exec(response[i].type);
        if (response[i].type.indexOf("exec") == -1 && noMetaType.indexOf(response[i].type) == -1) {
          switch (response[i].type) {
            case "datapod":
              metajson.caption = "Datapod";
              metajson.icon = "fa fa-link"
              //metajson.state = "metadata({type:'datapod'})";
              metajson.state = "metadata";
              metajson.param={type:'datapod'}

              break;
            case "dataset":
              metajson.caption = "Dataset";
              metajson.icon = "fa fa-link"
              //metajson.state = "metadata({type:'dataset'})";
              metajson.state = "metadata";
              metajson.param={type:'dataset'}

              break;
            case "expression":
              metajson.caption = "Expression";
              metajson.icon = "fa fa-link"
              //metajson.state = "metadata({type:'expression'})";
              metajson.state = "metadata";
              metajson.param={type:'expression'}
              break;

            case "filter":
              metajson.caption = "filter";
              metajson.icon = "fa fa-link";
              //metajson.state = "metadata({type:'filter'})";
              metajson.state = "metadata";
              metajson.param={type:'filter'}
              break;
            case "formula":
              metajson.caption = "Formula";
              metajson.icon = "fa fa-link"
              //metajson.state = "metadata({type:'formula'})";
              metajson.state = "metadata";
              metajson.param={type:'formula'}
              break;

            case "load":
              metajson.caption = "Load";
              metajson.icon = "fa fa-link"
              //metajson.state = "metadata({type:'load'})";
              metajson.state = "metadata";
              metajson.param={type:'load'}
              break;

            case "map":
              metajson.caption = "Map";
              metajson.icon = "fa fa-link"
              //metajson.state = "metadata({type:'map'})";
              metajson.state = "metadata";
              metajson.param={type:'map'}
              break;
            case "function":
              metajson.caption = "Function";
              metajson.icon = "fa fa-link"
              //metajson.state = "metadata({type:'function'})";
              metajson.state = "metadata";
              metajson.param={type:'function'}
              break;
            case "relation":
              metajson.caption = "Relation";
              metajson.icon = "fa fa-link"
              metajson.state = "metadata({type:'relation'})";
              metajson.state = "metadata";
              metajson.param={type:'relation'}

              break;
            case "dashboard":
              metajson.caption = "Dashboard";
              metajson.icon = "fa fa-desktop"
              metajson.state = "dashboard";
              break;

            case "vizpod":
              metajson.caption = "Vizpod";
              metajson.icon = "fa fa-line-chart"
              metajson.state = "vizpodlist";
              break;

            case "profile":
              metajson.caption = "Profile";
              metajson.icon = "fa fa-users"
              metajson.state = "viewprofile";
              break;

            case "profilegroup":
              metajson.caption = "Profile Group";
              metajson.icon = "fa fa-users"
              metajson.state = "viewprofilegroup";
              break;

            case "dq":
              metajson.caption = "Data Quality";
              metajson.icon = "fa fa-rss"
              metajson.state = "viewdataquality";
              break;

            case "dqgroup":
              metajson.caption = "DQ Group";
              metajson.icon = "fa fa-rss"
              metajson.state = "viewdataqualitygroup";
              break;

            case "rule":
              metajson.caption = "Rule";
              metajson.icon = "fa fa-cogs"
              metajson.state = "viewrule";
              break;

            case "rulegroup":
              metajson.caption = "Rule Group";
              metajson.icon = "fa fa-cogs"
              metajson.state = "rulesgroup";
              break;

            case "algorithm":
              metajson.caption = "Algorithm";
              metajson.icon = "fa fa-flask"
              metajson.state = "algorithm";
              break;
            case "model":
              metajson.caption = "Model";
              metajson.icon = "fa fa-flask"
              metajson.state = "model";
              break;
            case "paramlist":
              metajson.caption = "param List";
              metajson.icon = "fa fa-flask"
              metajson.state = "paramlistmodel";
              break;
            case "operator":
              metajson.caption = "operator";
              metajson.icon = "fa fa-flask"
              metajson.state = "operator";
            break;
            case "distribution":
             metajson.caption = "distribution";
             metajson.icon = "fa fa-flask"
             metajson.state = "distribution";
            break;
            case "paramset":
              metajson.caption = "Param Set";
              metajson.icon = "fa fa-flask"
              metajson.state = "paramset";
              break;
            case "dag":
              metajson.caption = "Pipeline";
              metajson.type = "pipeline";
              metajson.icon = "fa fa-random"
              metajson.state = "listwf";
              break;
            case "activity":
              metajson.caption = "Activity";
              metajson.icon = "fa fa-wrench"
              //metajson.state = "admin({type:'activity'})";
              metajson.state = "admin";
              metajson.param={type:'activity'}
              break;
            case "application":
              metajson.caption = "Application";
              metajson.icon = "fa fa-wrench"
              //metajson.state = "admin({type:'application'})";
              metajson.state = "admin";
              metajson.param={type:'application'}
              break;
            case "datasource":
              metajson.caption = "Datasource";
              metajson.icon = "fa fa-wrench"
              //metajson.state = "admin({type:'datasource'})";
              metajson.state = "admin";
              metajson.param={type:'datasource'}
              break;
            case "datastore":
              metajson.caption = "Datastore";
              metajson.icon = "fa fa-wrench"
              //metajson.state = "admin({type:'datastore'})";
              metajson.state = "admin";
              metajson.param={type:'datastore'}
              break;
            case "group":
              metajson.caption = "Group";
              metajson.icon = "fa fa-wrench"
              //metajson.state = "admin({type:'group'})";
              metajson.state = "admin";
              metajson.param={type:'group'}
              break;
            case "privilege":
              metajson.caption = "Privilege";
              metajson.icon = "fa fa-wrench"
              //metajson.state = "admin({type:'privilege'})";
              metajson.state = "admin";
              metajson.param={type:'privilege'}
              break;
            case "role":
              metajson.caption = "Role";
              metajson.icon = "fa fa-wrench"
              //metajson.state = "admin({type:'role'})";
              metajson.state = "admin";
              metajson.param={type:'role'}
              break;
            case "session":
              metajson.caption = "Session";
              metajson.icon = "fa fa-wrench"
              //metajson.state = "admin({type:'session'})";
              metajson.state = "admin";
              metajson.param={type:'session'}
              break;
            case "user":
              metajson.caption = "User";
              metajson.icon = "fa fa-wrench"
              metajson.state = "admin";
              metajson.param={type:'user'}
              break;
            case "domain":
              metajson.caption = "Domain";
              metajson.icon = "fa fa-wrench"
              metajson.state = "admin";
              metajson.param={type:'domain'}
            case "export":
              metajson.caption="Export";
              metajson.icon='fa fa-wrench';
              //metajson.state="migrationassist({type:'Export'})";
              metajson.param={type:'Export'};
              metajson.state="migrationassist"
              break;
            case "import":
              metajson.caption="Import";
              metajson.icon='fa fa-wrench';
              //metajson.state="migrationassist({type:'Import'})";
              metajson.state="migrationassist"
              metajson.param={type:'Import'};
              break;
            case "train":
              metajson.caption="Training";
              metajson.icon='fa fa-flask';
              metajson.state="train"
              metajson.param={type:'train'};
              break;
            case "predict":
              metajson.caption="Prediction";
              metajson.icon='fa fa-flask';
              metajson.state="predict"
              metajson.param={type:'predict'};
              break;
            case "simulate":
              metajson.caption="Simulation";
              metajson.icon='fa fa-flask';
              metajson.state="simulate"
              metajson.param={type:'simulate'};
              break;
            case "recon":
              metajson.caption="Reconciliation";
              metajson.icon='fa fa-compress';
              metajson.state="datareconrule"
              metajson.param={type:'recon'};
              break;
            case "recongroup":
              metajson.caption="Recon Group";
              metajson.icon='fa fa-compress';
              metajson.state="datareconrulegroup"
              metajson.param={type:'recongroup'};
              break; 
            case "graphpod":
              metajson.caption="Graphpod"
              metajson.icon='fa fa-bar-chart';
              metajson.state="listgraphpod"
              break
            case "report":
              metajson.caption="Report"
              metajson.icon='fa fa-bar-chart';
              metajson.state="reportlist"
              break
            case "batch":
              metajson.caption="Batch"
              metajson.icon='fa fa-tasks';
              metajson.state="batchlist"
              break
            case "ingest":
                metajson.caption="Ingest"
                metajson.icon='fa fa-random';
                metajson.state="ingestrulelist2"	
                break
            case "ingestgroup":
                metajson.caption="Ingest Group"
                metajson.icon='fa fa-random';
                metajson.state="ingestrulegrouplist"
                break
            case "organization":
                metajson.caption="Organization"
                metajson.icon='fa fa-wrench';
                metajson.state = "admin";
                metajson.param={type:'organization'}
                break
            default:
              console.log(response[i].type)
                 
          }


          metaarray[i] = metajson
        }
      }
      $scope.allMetaCount = metaarray;
    }
    $scope.selectState=function(state,param){
      if(state =='admin' && $rootScope.role =="admin"){
       $state.go(state,param)
      }
      else{
        if(state !='admin'){
          $state.go(state,param)
        }
        else{
          notify.type='info',
          notify.title= 'Info',
          notify.content='Unauthorized to view selected item.'
          $scope.$emit('notify', notify);
        }
      }
    }
  });
