InferyxApp=angular.module('InferyxApp');
InferyxApp.factory('dagMetaDataService',function($rootScope,$state, uiGridConstants){
  var obj = {};
   $rootScope.loginUser = JSON.parse(localStorage.userdetail).name;

  obj.statusDefs={
    'NotStarted':{
      name : 'NotStarted',
      caption:'Not Started',
      color :'#659be0',//'#006df0',
      iconPath : 'assets/layouts/layout/img/new_status/NotStarted.svg',
    },
    'Not Started':{
      name : 'NotStarted',
      caption:'Not Started',
      color :'#659be0',//'#006df0',
      iconPath : 'assets/layouts/layout/img/new_status/NotStarted.svg',
    },
    'Completed':{
      name : 'Completed',
      caption:'Completed',
      color :'#36c6d3',//'#91dc5a',
      iconPath : 'assets/layouts/layout/img/new_status/Completed.svg',
    },
    'Killed':{
      name : 'Killed',
      caption:'Killed',
      color :'#ed6b75',//'#d80027',
      iconPath : 'assets/layouts/layout/img/new_status/Killed.svg',
    },
    'Failed':{
      name : 'Failed',
      caption:'Failed',
      color :'#ed6b75',//'#d80029',
      iconPath : 'assets/layouts/layout/img/new_status/Failed.svg',
    },
    'Resume':{
      name : 'Resume',
      caption:'Resume',
      color :'#006df0',
      iconPath : 'assets/layouts/layout/img/new_status/Resume.svg',
    },
    'Terminating':{
      name : 'Terminating',
      caption:'Terminating',
      color :'#d80027',
      iconPath : 'assets/layouts/layout/img/new_status/Terminating.svg',
    },
    'OnHold':{
      name : 'OnHold',
      caption:'OnHold',
      color :'#ffda44',
      iconPath : 'assets/layouts/layout/img/new_status/OnHold.svg',
    },
    'In Progress':{
      name : 'InProgress',
      caption:'In Progress',
      color :'#F1C40F',//'#f57f36',
      iconPath : 'assets/layouts/layout/img/new_status/InProgress.svg',
    },
    'InProgress':{
      name : 'InProgress',
      caption:'In Progress',
      color :'#F1C40F',//'#f57f36',
      iconPath : 'assets/layouts/layout/img/new_status/InProgress.svg',
    },
    'active':{
      name : 'active',
      caption:'Active',
      color :'#36c6d3',//'#91dc5a',
      iconPath :""
    },
    'expired':{
      name : 'expire',
      caption:'Expire',
      color :'#ed6b75',//'#d80029',
      iconPath :'',
    },
  }
  $rootScope.imgPath=obj.statusDefs
  obj.gridOptionsDefault = {
    // paginationPageSizes: [10, 25, 50, 75],
    // paginationPageSize: 10,
    // enableFiltering: true,
    enableGridMenu: true,
    rowHeight: 40,
    exporterSuppressCtiolumns: [ 'action' ],
    exporterMenuPdf: true,
    exporterPdfOrientation: 'landscape',
    exporterPdfPageSize: 'A4',
    exporterPdfDefaultStyle: {fontSize: 9},
    exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, italics: true, color: 'red'},
    columnDefs: [
      {
        displayName: 'UUID',
        name: 'uuid',
        visible: false,
        cellClass: 'text-center',
        headerCellClass: 'text-center'
      },
      {
        displayName: 'Name',
        name: 'name',
        minWidth: 220,
        headerCellClass: 'text-center'
      },
      {
        displayName: 'Version',
        name: 'version',
        visible: true,
        maxWidth:100,
        cellClass: 'text-center',
        headerCellClass: 'text-center',
        sort: {
          direction: uiGridConstants.DESC,
         // priority: 0,
        },
      },
      {
        displayName: 'Created By',
        name: 'createdBy.ref.name',
        cellClass: 'text-center',
        maxWidth:100,
        headerCellClass: 'text-center'
      },
      {
        displayName: 'Created On',
        name: 'createdOn',
        minWidth: 220,
        cellClass: 'text-center',
        headerCellClass: 'text-center',

      }
    ]
  };

  obj.gridOptions = angular.copy(obj.gridOptionsDefault);
  obj.gridOptions.columnDefs.push({
    displayName: 'Status',
    name: 'active',
    minWidth:100,
    cellClass: 'text-center',
    headerCellClass: 'text-center',
    cellTemplate: '<div class="ui-grid-cell-contents">{{row.entity.active == "Y" ? "Active" : "Inactive"}}</div>'
  });
  obj.gridOptions.columnDefs.push({
    displayName: 'Published',
    name: 'published',
    cellClass: 'text-center',
    headerCellClass: 'text-center',
    cellTemplate: '<div class="ui-grid-cell-contents">{{row.entity.published == "Y" ? "Yes" : "No"}}</div>'
  });
  obj.gridOptions.columnDefs.push(
    {
      displayName: 'Action',
      name: 'action',
      cellClass: 'text-center',
      headerCellClass: 'text-center',
      minWidth:100,
      cellTemplate: [
        '<div class="ui-grid-cell-contents">',
        '  <div class="dropdown" uib-dropdown dropdown-append-to-body>',
        '    <button class="btn green btn-xs btn-outline dropdown-toggle" uib-dropdown-toggle>Action',
        '    <i class="fa fa-angle-down"></i></button>',
        '    <ul uib-dropdown-menu class="dropdown-menu-grid">',
        '    <li ng-disabled="grid.appScope.privileges.indexOf(\'View\') == -1"><a ng-click="grid.appScope.action(row.entity,\'view\',grid.appScope.privileges)"><i class="fa fa-eye" aria-hidden="true"></i> View </a></li>',
        '    <li ng-disabled="grid.appScope.privileges.indexOf(\'Edit\') == -1" ><a ng-click="grid.appScope.action(row.entity,\'edit\')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i> Edit </a></li>',
        '    <li ng-disabled="grid.appScope.privileges.indexOf(\'Delete\') == -1" ng-if="row.entity.active == \'Y\'"><a ng-click="grid.appScope.delete(row.entity)"><i class="fa fa-times" aria-hidden="true"></i>  Delete</a></li>',
        '    <li ng-disabled="grid.appScope.privileges.indexOf(\'Restore\') == -1" ng-if="row.entity.active == \'N\'"><a ng-click="grid.appScope.delete(row.entity,true)"><i class="fa fa-retweet" aria-hidden="true"></i>  Restore</a></li>',
        '    <li ng-disabled="grid.appScope.privileges.indexOf(\'Publish\') == -1" ng-if="row.entity.published == \'N\'"><a ng-click="grid.appScope.publish(row.entity)"><i class="fa fa-share-alt" aria-hidden="true"></i>  Publish</a></li>',
        '    <li ng-disabled="grid.appScope.privileges.indexOf(\'Unpublish\') == -1 || row.entity.createdBy.ref.name != grid.appScope.loginUser" ng-if="row.entity.published == \'Y\'"><a ng-click="grid.appScope.publish(row.entity,true)"><i class="fa fa-shield" aria-hidden="true"></i>  Unpublish</a></li>',
        '    <li ng-disabled="grid.appScope.privileges.indexOf(\'Execute\') == -1 || row.entity.active==\'N\'" ng-if="grid.appScope.isExecutable == -1"><a ng-click="grid.appScope.execute(row.entity)"><i class="fa fa-tasks" aria-hidden="true"></i>  Execute</a></li>',
        '    <li ng-disabled="grid.appScope.privileges.indexOf(\'Clone\') == -1"><a ng-click="grid.appScope.createCopy(row.entity)"><i class="fa fa-clone" aria-hidden="true"></i>  Clone</a></li>',
        '    <li ng-disabled="grid.appScope.privileges.indexOf(\'Export\') == -1"><a ng-click="grid.appScope.getDetail(row.entity)"><i class="fa fa-file-pdf-o" aria-hidden="true"></i>  Export</a></li>',
        '    <li ng-show="grid.appScope.isUpload !=-1"><a ng-click="grid.appScope.getDetailForUpload(row.entity)"><i class="fa fa-upload" aria-hidden="true"></i>  Upload</a></li>',
        '    </ul>',
        '  </div>',
        '</div>'
      ].join('')
    }
  )


  obj.gridOptionsResults = angular.copy(obj.gridOptionsDefault);
  obj.gridOptionsResults.columnDefs.push(
    {
      displayName: 'Status',
      name: 'status',
      cellClass: 'text-center',
      headerCellClass: 'text-center',
      maxWidth: 100,
      //cellTemplate: '<div class=\"ui-grid-cell-contents ng-scope ng-binding\"><img title="{{row.entity.status}}" ng-src=\"{{grid.appScope.path[row.entity.status].iconPath}}\" border=\"0\" width=\"15\">'
      cellTemplate: '<div class=\"ui-grid-cell-contents ng-scope ng-binding\"><div class="label-sm label-success" style=" width: 88%;font-size: 13px;padding: 2px;color: white;margin: 0 auto;font-weight: 300;background-color:{{grid.appScope.path[row.entity.status].color}} !important" ng-style="">{{row.entity.status}}</div></div>'


    }
  )
  obj.gridOptionsResults.columnDefs.push(
    {
      displayName: 'Action',
      name: 'action',
      cellClass: 'text-center',
      headerCellClass: 'text-center',
      maxWidth: 100,
      // cellTemplate: [
      //   // '<div class="ui-grid-cell-contents"><input type="radio" name="execbutton" ng-disabled="[\'Completed\',\'In Progress\'].indexOf(row.entity.status)==-1" ng-click="grid.appScope.getExec(row.entity)"></div>',
      //   '<div class="ui-grid-cell-contents"> <a class="btn btn-xs btn-primary"  ng-disabled="[\'Completed\',\'InProgress\'].indexOf(row.entity.status)==-1"  ng-click="grid.appScope.getExec(row.entity)">View</a></div>',
      // ].join(''),
      cellTemplate: [
        '<div class="ui-grid-cell-contents">',
        '  <div class="dropdown" uib-dropdown dropdown-append-to-body>',
        '    <button class="btn green btn-xs btn-outline dropdown-toggle" uib-dropdown-toggle>Action',
        '    <i class="fa fa-angle-down"></i></button>',
        '    <ul uib-dropdown-menu class="dropdown-menu-grid">',
        '       <li><a ng-disabled="grid.appScope.newType.indexOf(\'dagexec\')!=-1?[\'Completed\',\'Not Started\',\'Terminating\',\'Failed\',\'In Progress\',\'Killed\'].indexOf(row.entity.status)==-1:grid.appScope.newType.indexOf(\'group\')==-1?[\'Completed\',\'Killed\'].indexOf(row.entity.status)==-1:[\'Completed\',\'In Progress\',\'Killed\',\'Failed\',\'Terminating\'].indexOf(row.entity.status)==-1"  ng-click="grid.appScope.getExec(row.entity)"><i class="fa fa-eye" aria-hidden="true"></i> View </a></li>',
        // '       <li><a ng-disabled="[\'Not Started\'].indexOf(row.entity.status)==-1 || grid.appScope.privileges.indexOf(\'Execute\') == -1"  ng-click="grid.appScope.setStatus(row.entity,\'OnHold\')"><i class="fa fa-eye" aria-hidden="true"></i> On Hold </a></li>',
        // '       <li><a ng-disabled="[\'On Hold\'].indexOf(row.entity.status)==-1 || grid.appScope.privileges.indexOf(\'Execute\') == -1"  ng-click="grid.appScope.setStatus(row.entity,\'Resume\')"><i class="fa fa-eye" aria-hidden="true"></i> Resume </a></li>',
        '       <li><a ng-disabled="[\'In Progress\',\'Resume\'].indexOf(row.entity.status)==-1 || grid.appScope.privileges.indexOf(\'Execute\') == -1"  ng-click="grid.appScope.setStatus(row.entity,\'Killed\')"><i class="fa fa-times" aria-hidden="true"></i> Kill </a></li>',
        '       <li><a ng-disabled="[\'Killed\',\'Failed\'].indexOf(row.entity.status)==-1 || grid.appScope.privileges.indexOf(\'Execute\') == -1"  ng-click="grid.appScope.restartExec(row.entity)"><i class="fa fa-repeat" aria-hidden="true"></i> Restart </a></li>',
        '    </ul>',
        '  </div>',
        '</div>'
      ].join('')
    }
  )//ng-disabled="{{['Completed','In Progress'].indexOf(row.entity.status)==-1}}"

  obj.gridOptionsJobExec = angular.copy(obj.gridOptionsDefault);
  obj.gridOptionsJobExec.columnDefs.push(
    {
      displayName: 'Status',
      name: 'status',
      cellClass: 'text-center',
      headerCellClass: 'text-center',
      maxWidth: 100,
      //cellTemplate: '<div class=\"ui-grid-cell-contents ng-scope ng-binding\"><img title="{{row.entity.status}}" ng-src=\"{{grid.appScope.path[row.entity.status].iconPath}}\" border=\"0\" width=\"15\">'
      cellTemplate: '<div class=\"ui-grid-cell-contents ng-scope ng-binding\"><div class="label-sm label-success" style=" width: 88%;font-size: 13px;padding: 2px;color: white;margin: 0 auto;font-weight: 300;background-color:{{grid.appScope.path[row.entity.status].color}} !important" ng-style="">{{row.entity.status}}</div></div>'

    }
  )
  obj.gridOptionsJobExec.columnDefs.push(
    {
      displayName: 'Action',
      name: 'action',
      cellClass: 'text-center',
      headerCellClass: 'text-center',
      maxWidth:100,
      cellTemplate: [
        '<div class="ui-grid-cell-contents">',
        '  <div class="dropdown" uib-dropdown dropdown-append-to-body>',
        '    <button class="btn green btn-xs btn-outline dropdown-toggle" uib-dropdown-toggle>Action',
        '    <i class="fa fa-angle-down"></i></button>',
        '    <ul uib-dropdown-menu class="dropdown-menu-grid">',
        '    <li><a ng-click="grid.appScope.action(row.entity,\'view\')"><i class="fa fa-eye" aria-hidden="true"></i> View </a></li>',
        '    <li><a ng-click="grid.appScope.getDetail(row.entity)"><i class="fa fa-file-pdf-o" aria-hidden="true"></i>  Export</a></li>',
        '    </ul>',
        '  </div>',
        '</div>'
      ].join('')
    }
  )
  obj.elementDefs = {
    'dag':{
      name : 'dag',
      caption:'Pipeline',
      color : '#91DC5A',
      icon : 'dag.png',
      execType:'dagexec',
      metaType:'dag',
      iconPath : 'assets/layouts/layout/img/dag.svg',
      allowInMenu : false,
      detailState: 'createwf'
    },
    'stage':{
      name : 'stage',
      caption:'Stage',
      color : 'blue',
      icon : 'stage.svg',
      execType:'',
      metaType:'',
      iconPath : 'assets/layouts/layout/img/stage.svg',
      iconPathInactive:'assets/layouts/layout/img/stageinactive.svg',
      allowInMenu : true,
    },
    'dq':{
      name : 'dataqual',
      caption:"Rule",
      color : 'orange',
      icon : 'dq.svg',
      execType:'dqexec',
      metaType:'dq',
      iconPath : 'assets/layouts/layout/img/dq.svg',
      iconPathInactive:'assets/layouts/layout/img/dqinactive.svg',
      allowInMenu : true,
      listState:'viewdataquality',
      detailState: 'createdataquality',
      state: 'createdataquality',
    },
    'dqgroup':{
      name : 'dqgroup',
      caption:" Rule Group",
      color : 'orange',
      icon : 'dq.svg',
      execType:'dqgroupExec',
      metaType:'dqgroup',
      iconPath : 'assets/layouts/layout/img/dq.svg',
      iconPathInactive:'assets/layouts/layout/img/dqinactive.svg',
      allowInMenu : true,
      listState:'viewdataqualitygroup',
      detailState:'createdataqualitygroup',
      state: 'createdataqualitygroup'
    },
    'map':{
      name : 'map',
      caption:"Map",
      color : '#FF4081',
      icon : 'map.svg',
      execType:'mapExec',
      metaType:'map',
      iconPath : 'assets/layouts/layout/img/map.svg',
      iconPathInactive:'assets/layouts/layout/img/mapinactive.svg',
      allowInMenu : true,
      state: 'metaListmap',
      detailState: 'metaListmap'
    },
    'load':{
      name : 'load',
      caption:'Load',
      color : '#933f5b',
      icon : 'load.svg',
      execType:'loadExec',
      metaType:'load',
      iconPath : 'assets/layouts/layout/img/load.svg',
      iconPathInactive:'assets/layouts/layout/img/loadinactive.svg',
      allowInMenu : true,
      state : 'metaListload',
      detailState : 'metaListload',
    },
    'profile':{
      name : 'profile',
      caption:'Profile',
      color : '#29B6F6',
      icon : 'profile.svg',
      execType:'profileExec',
      metaType:'profile',
      iconPath : 'assets/layouts/layout/img/profile.svg',
      iconPathInactive:'assets/layouts/layout/img/profileinactive.svg',
      allowInMenu : true,
      state: 'createprofile',
      detailState: 'createprofile'
    },
    'profilegroup':{
      name : 'profilegroup',
      caption:'Profile Group',
      color : '#00796B',
      icon : 'profilegroup.svg',
      execType:'profilegroupExec',
      metaType:'profilegroup',
      iconPath : 'assets/layouts/layout/img/profilegroup.svg',
      iconPathInactive:'assets/layouts/layout/img/profilegroupinactive.svg',
      allowInMenu : true,
      state: 'createprofilegroup',
      detailState: 'createprofilegroup'
    },
    'model':{
      name : 'model',
      caption : 'Model',
      color : '#1DE9B6',
      icon : 'model.svg',
      execType:'modelExec',
      metaType:'model',
      iconPath : 'assets/layouts/layout/img/model.svg',
      allowInMenu : true,
      state: 'createmodel',
      detailState: 'createmodel'
    },
    'rulegroup':{
      name : 'rulegroup',
      caption : 'Rule Group',
      color : '#2489D1',
      icon : 'rulegroup.svg',
      execType:'rulegroupExec',
      metaType:'rulegroup',
      iconPath : 'assets/layouts/layout/img/rulegroup.svg',
      allowInMenu : true,
      listState:'rulesgroup',
      detailState: 'createrulesgroup',
      state: 'createrulesgroup'
    },
    'rule':{
      name : 'rule',
      caption : 'Rule',
      color : '#2489D1',
      icon : 'rule.svg',
      execType:'ruleExec',
      metaType:'rule',
      iconPath : 'assets/layouts/layout/img/rule.svg',
      allowInMenu : true,
      listState:'viewrule',
      detailState: 'createrules',
      state: 'createrules'

    },
    'loadexec':{
      name : 'load',
      caption :'Load Exec',
      execType:'loadExec',
      metaType:'loadExec',
      color : '#EB54C3',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState:'jobmonitoringlistloadexec'
    },
    'mapexec':{
      name : 'map',
      caption :'Map Exec',
      execType:'mapExec',
      metaType:'mapExec',
      color : '#EB54C3',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState :'jobexecutorlistmapexec'
    },
    'dqgroupexec':{
      name : 'dqgroup',
      caption :'DQ Group Exec',
      execType:'dqgroupExec',
      metaType:'dqgroupExec',
      color : '#dceaab',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState :'jobexecutorlistdqgroupexec'
    },

    'ruleexec':{
      name : 'rule',
      caption :'Rule Exec',
      execType:'ruleExec',
      metaType:'ruleExec',
      color : '#EB54C3',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState :'jobexecutorlistruleexec',
      resultState:'rulerestultpage'
    },
    'rulegroupexec':{
      name : 'rulegroup',
      caption :'Rule Group Exec',
      execType:'rulegroupExec',
      metaType:'rulegroupExec',
      color : '#EB54C3',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState :'jobexecutorlistrulegroupexec'
    },
    'profileexec':{
      name : 'profile',
      caption :'Profile Exec',
      execType:'profileExec',
      metaType:'profileExec',
      color : '#EB54C3',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState :'jobexecutorlistprofileexec',
      resultState :'viewprofileresultspage'
    },
    'profilegroupexec':{
      name : 'profilegroup',
      caption : 'Profile Group Exec',
      execType:'profilegroupExec',
      metaType:'profilegroupExec',
      color : '#EB54C3',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState :'jobexecutorlistprofilegroupexec',
      resultState :'viewprofileresultspage'
    },
    'dagexec':{
      name : 'pipeline',
      caption : 'Pipeline Exec',
      execType:'dagexec',
      metaType:'dagexec',
      color : '#33691E',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState :'jobexecutorlistdagexec',
      resultState :'resultgraphwf'
    },
    'pipelineexec':{
      name : 'pipeline',
      caption : 'Pipeline Exec',
      execType:'dagexec',
      metaType:'dagexec',
      color : '#EB54C3',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState :'jobexecutorlistdagexec'
    },
     'vizexec':{
      name : 'vizpod',
      caption:'Vizpod Exec',
      execType:'vizexec',
      metaType:'vizexec',
      color : '#fff8dc',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState :'jobexecutorlistvizpodexec'

    },
    'viz':{
     name : 'vizpod',
     caption:'Vizpod',
     execType:'vizexec',
     metaType:'vizpod',
     color : '#fff8dc',
     allowInMenu : false,
     listState : 'jobmonitoringlist',
     detailState :'jobexecutorlistvizpodexec'

   },
     'vizpodexec':{
      name : 'vizpod',
      caption : 'Vizpod Exec',
      execType:'vizexec',
      metaType:'vizexec',
      color : '#fff8dc',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState :'jobexecutorlistvizpodexec'
    },
     'dqexec':{
      name : 'dq',
      caption : 'Data Quality Exec',
      execType:'dqexec',
      metaType:'dqexec',
      color : '#EB54C3',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState :'jobexecutorlistdqexec',
      resultState:'viewdqresultspage'
    },
     'modelexec':{
      name : 'model',
      caption : 'Model Exec',
      execType:'modelExec',
      metaType:'modelExec',
      color : '#EB54C3',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState :'jobexecutorlistmodelexec'
    },

    'datapod':{
      name : 'datapod',
      caption : 'Datapod',
      execType:'',
      metaType:'datapod',
      color : '#00E5FF',
      allowInMenu : false,
      listState : 'metadata',
      detailState:'metaListdatapod'
    },
    'pipeline':{
      name : 'dag',
      caption : 'Pipeline',
      execType:'dagexec',
      metaType:'dag',
      color : '#EB54C3',
      allowInMenu : false,
      listState : '',
      detailState:'jobexecutorlistdagexec'
    },

    'dataset':{
      name : 'dataset',
      caption : 'Data Set',
      execType:'',
      metaType:'dataset',
      color : '#00695C',
      allowInMenu : false,
      listState : 'metadata',
      detailState:'metaListdataset'
    },
    'expression':{
      name : 'expression',
      caption : 'Expression',
      execType:'',
      metaType:'expression',
      color : '#6A1B9A',
      allowInMenu : false,
      listState : 'metadata',
      detailState:'metaListexpression'
    },
     'filter':{
      name : 'filter',
      caption : 'Filter',
      execType:'',
      metaType:'filter',
      color : '#5BF5ED',
      allowInMenu : false,
      listState : 'metadata',
      detailState:'metaListfilter'
    },
    'formula':{
      name : 'formula',
      caption : 'Formula',
      execType:'',
      metaType:'formula',
      color : '#FFB300',
      allowInMenu : false,
      listState : 'metadata',
      detailState:'metaListformula'
    },
    'function':{
      name : 'function',
      caption : 'Function',
      execType:'',
      metaType:'function',
      color : '#f79742',
      allowInMenu : false,
      listState : 'metadata',
      detailState:'metaListfunction'
    },
    'relation':{
      name : 'relation',
      caption : 'Relation',
      execType:'',
      metaType:'relation',
      color : '#76FF03',
      allowInMenu : false,
      listState : 'metadata',
      detailState:'metaListrelation'
    },
    'dashboard':{
      name : 'dashboard',
      caption : 'Dashboard',
      execType:'',
      metaType:'dashboard',
      color : '#75E108',
      allowInMenu : false,
      listState : 'dashboard',
      detailState:'showdashboard'
    },
    'vizpod':{
      name : 'vizpod',
      caption : 'Vizpod',
      execType:'vizexec',
      metaType:'vizpod',
      color : '#41E0F5',
      allowInMenu : false,
      listState : 'vizpodlist',
      detailState:'dvvizpod'
    },
    'formulainfo':{
        name : 'formulainfo',
        caption : 'Formula Info',
        execType:'vizexec',
        metaType:'vizpod',
        color : '#FFE082',
        allowInMenu : false,
        listState : 'vizpodlist',
        detailState:'dvvizpod'
      },
    'sectioninfo':{
        name : 'sectioninfo',
        caption : 'Section Info',
        execType:'vizexec',
        metaType:'vizpod',
        color : '#c4ff9a',
        allowInMenu : false,
        listState : 'vizpodlist',
        detailState:'dvvizpod'
      },
    'algorithm':{
      name : 'algorithm',
      caption : 'Algorithm',
      execType:'',
      metaType:'algorithm',
      color : '#00E676',
      allowInMenu : false,
      listState : 'algorithm',
      detailState:'createalgorithm'
    },
    'paramlist':{
      name : 'paramlist',
      caption : 'Parameter List',
      execType:'',
      metaType:'paramlist',
      color : '#CDDC39',
      allowInMenu : false,
      listState : 'paramlist',
      detailState:'createparamlist'
    },
    'paramset':{
      name : 'paramset',
      caption : 'Parameter set',
      execType:'',
      metaType:'paramset',
      color : '#E6EE9C',
      allowInMenu : false,
      listState : 'paramset',
      detailState:'createparamset'
    },
    'activity':{
      name : 'activity',
      caption : 'Activity',
      execType:'',
      metaType:'activity',
      color : '#EB54C3',
      allowInMenu : false,
      listState : 'admin',
      detailState:'adminListactivity'
    },
    'application':{
      name : 'application',
      caption : 'Application',
      execType:'',
      metaType:'application',
      color : '#f794e0',
      allowInMenu : false,
      listState : 'admin',
      detailState:'adminListapplication'
    },
    'datasource':{
      name : 'datasource',
      caption : 'Datasource',
      execType:'',
      metaType:'datasource',
      color : '#FF80AB',
      allowInMenu : false,
      listState : 'admin',
      detailState:'adminListdatasource'
    },
    'datastore':{
      name : 'datastore',
      caption : 'Datastore',
      execType:'',
      metaType:'datastore',
      color : '#efefef',
      allowInMenu : false,
      listState : 'admin',
      detailState:'adminListdatastore'
    },
    'group':{
      name : 'group',
      caption : 'Group',
      execType:'',
      metaType:'group',
      color : '#fce5cd',
      allowInMenu : false,
      listState : 'admin',
      detailState:'adminListgroup'
    },
    'privilege':{
      name : 'privilege',
      caption : 'Privilege',
      execType:'',
      metaType:'privilege',
      color : '#cfe2f3',
      allowInMenu : false,
      listState : 'admin',
      detailState:'adminListprivilege'
    },
    'role':{
      name : 'role',
      caption : 'Role',
      execType:'',
      metaType:'role',
      color : '#ebdef0',
      allowInMenu : false,
      listState : 'admin',
      detailState:'adminListrole'
    },
    'session':{
      name : 'session',
      caption : 'Session',
      execType:'session',
      metaType:'session',
      color : '#d7fcc0',
      allowInMenu : false,
      listState : 'admin',
      detailState:'adminListsession'
    },
    'user':{
      name : 'user',
      caption : 'User',
      execType:'user',
      metaType:'user',
      color : '#b7b7b7',
      allowInMenu : false,
      listState : 'admin',
      detailState:'adminListuser'
    },
    'export':{
      name : 'export',
      caption :'Export',
      execType:'export',
      metaType:'export',
      color : '#EB54C3',
      allowInMenu :false,
      listState : '',
      detailState :'detaitexport'
    },
    'import':{
      name : 'import',
      caption :'Import',
      execType:'import',
      metaType:'import',
      color : '#EB54C3',
      allowInMenu :false,
      listState : '',
      detailState :'detaitimport'
    },
   'filterinfo':{
     name : 'filterinfo',
     caption :'Filter Info',
     execType:'',
     metaType:'',
     color : '#F5CBA7',
     allowInMenu :false,
     listState : '',
     detailState :''
   },
   'simple':{
     name : 'simple',
     caption :'Simple',
     execType:'',
     metaType:'',
     color : '#BBDEFB',
     allowInMenu :false,
     listState : '',
     detailState :''
   },
   'attributemap':{
     name : 'attributemap',
     caption :'Attribute Map',
     execType:'',
     metaType:'',
     color : '#F8BBD0',
     allowInMenu :false,
     listState : '',
     detailState :''
   },
   'stages':{
     name : 'stages',
     caption :'Stages',
     execType:'',
     metaType:'',
     color : '#7CB342',
     allowInMenu :false,
     listState : '',
     detailState :''
   },
   'tasks':{
     name : 'tasks',
     caption :'Tasks',
     execType:'',
     metaType:'',
     color : '#AED581',
     allowInMenu :false,
     listState : '',
     detailState :''
   },
   'operators':{
     name : 'operators',
     caption :'Operators',
     execType:'',
     metaType:'',
     color : '#DCEDC8',
     allowInMenu :false,
     listState : '',
     detailState :''
   },
   'meta':{
     name : 'meta',
     caption :'Meta',
     execType:'',
     metaType:'',
     color : '#fcff85',
     allowInMenu :false,
     listState : '',
     detailState :''
   },
   'from_base':{
	     name : 'from_base',
	     caption :'from_base',
	     execType:'',
	     metaType:'',
	     color : '#f79742',
	     allowInMenu :false,
	     listState : '',
	     detailState :''
	   },
   'attributes':{
	     name : 'attributes',
	     caption :'Attributes',
	     execType:'',
	     metaType:'',
	     color : '#84FFFF',
	     allowInMenu :false,
	     listState : '',
	     detailState :''
 },
 'attributeinfo':{
	     name : 'attributeinfo',
	     caption :'AttributeInfo',
	     execType:'',
	     metaType:'',
	     color : '#B3E5FC',
	     allowInMenu :false,
	     listState : '',
	     detailState :''
 },
 'logicaloperator':{
	     name : 'logicaloperator',
	     caption :'LogicalOperator',
	     execType:'',
	     metaType:'',
	     color : '#E1BEE7',
	     allowInMenu :false,
	     listState : '',
	     detailState :''
 },
 'expressioninfo':{
	     name : 'expressioninfo',
	     caption :'ExpressionInfo',
	     execType:'',
	     metaType:'',
	     color : '#BA68C8',
	     allowInMenu :false,
	     listState : '',
	     detailState :''
 },
 'operand':{
	     name : 'operand',
	     caption :'Operand',
	     execType:'',
	     metaType:'',
	     color : '#E1BEE7',
	     allowInMenu :false,
	     listState : '',
	     detailState :''
 },
 'relationinfo':{
	     name : 'relationinfo',
	     caption :'relationInfo',
	     execType:'',
	     metaType:'',
	     color : '#CCFF90',
	     allowInMenu :false,
	     listState : '',
	     detailState :''
 },
 'joinkey':{
	   name : 'joinkey',
	   caption :'JoinKey',
	   execType:'',
	   metaType:'',
	   color : '#EB54C3',
	   allowInMenu :false,
	   listState : '',
	   detailState :''
	},
 'paraminfo':{
	   name : 'paramInfo',
	   caption :'Parameter Info',
	   execType:'',
	   metaType:'',
	   color : '#EB54C3',
	   allowInMenu :false,
	   listState : '',
 },
 'paramsetval':{
	   name : 'paramsetval',
	   caption :'ParamSet Val',
	   execType:'',
	   metaType:'',
	   color : '#EB54C3',
	   allowInMenu :false,
	   listState : '',
 },
 'params':{
	   name : 'params',
	   caption :'Params',
	   execType:'',
	   metaType:'',
	   color : '#EB54C3',
	   allowInMenu :false,
	   listState : '',
},
 'features':{
	   name : 'features',
	   caption :'Features',
	   execType:'',
	   metaType:'',
	   color : '#B9F6CA',
	   allowInMenu :false,
	   listState : '',
 },
 'appinfo':{
	   name : 'appinfo',
	   caption :'App Info',
	   execType:'',
	   metaType:'',
	   color : '#EEFF41',
	   allowInMenu :false,
	   listState : '',
 },
 'privilegeinfo':{
	   name : 'privilegeinfo',
	   caption :'Privilege Info',
	   execType:'', 
	   metaType:'',
	   color : '#00E676',
	   allowInMenu :false,
	   listState : '',
 },
 'roleinfo':{
	   name : 'roleinfo',
	   caption :'Role Info',
	   execType:'', 
	   metaType:'',
	   color : '#00E676',
	   allowInMenu :false,
	   listState : '',
 },
 'ruleinfo':{
	   name : 'ruleinfo',
	   caption :'Rule Info',
	   execType:'', 
	   metaType:'',
	   color : '#80CBC4',
	   allowInMenu :false,
	   listState : '',
 },
 'functioninfo':{
	   name : 'functioninfo',
	   caption :'Function Info',
	   execType:'', 
	   metaType:'',
	   color : '#d7c288',
	   allowInMenu :false,
	   listState : '',
 },
 'execlist':{
	   name : 'execlist',
	   caption :'Exec List',
	   execType:'', 
	   metaType:'',
	   color : '#80CBC4',
	   allowInMenu :false,
	   listState : '',
},
'keys':{
	   name : 'keys',
	   caption :'Keys',
	   execType:'', 
	   metaType:'',
	   color : '#D50000',
	   allowInMenu :false,
	   listState : '',
},
'values':{
	   name : 'values',
	   caption :'Values',
	   execType:'', 
	   metaType:'',
	   color : '#EF5350',
	   allowInMenu :false,
	   listState : '',
},
'groups':{
	   name : 'groups',
	   caption :'Groups',
	   execType:'', 
	   metaType:'',
	   color : '#e4ff9d',
	   allowInMenu :false,
	   listState : '',
},
  'groupinfo':{
	   name : 'groupinfo',
	   caption :'Groups Info',
	   execType:'', 
	   metaType:'',
	   color : '#FFEB3B',
	   allowInMenu :false,
	   listState : '',
},
'refkeylist':{
	   name : 'refkeylist',
	   caption :'Refkey List',
	   execType:'', 
	   metaType:'',
	   color : '#80CBC4',
	   allowInMenu :false,
	   listState : '',
}



  };

  // var dagExecTypes = {};
  // angular.forEach(obj.elementDefs,function (element,type) {
  //   dagExecTypes[type+'Exec'] = angular.copy(element);
  //   dagExecTypes[type+'Exec'].allowInMenu = false;
  // });
  // angular.merge(obj.elementDefs,dagExecTypes);
  var validElementTypes = ['dag','stage','dq','dqgroup','map','load','profile','profilegroup','model','rule','rulegroup'];
  obj.validTaskTypes = ['dq','dqgroup','map','load','profile','profilegroup','model','rule','rulegroup'];
  var defaultElement = {
      markup: '<g class="rotatable"><g class="scalable"><image class="body"/></g><image class="remove"/><g class="status"><image class="statusImg"><title class="statusTitle">Status</title></image></g><text class="label" /><g class="inPorts"/><g class="outPorts"/></g>',
      size: { width: 50, height: 50 },
      inPorts : ['in'],
      outPorts: ['out'],
      ports: {
          groups: {
              'in': {
                  attrs: {
                      '.port-body': {
                          fill: '#fff',
                          r:5,
                          cx:-5
                      }
                  }
              },
              'out': {
                  attrs: {
                      '.port-body': {
                          fill: '#fff',
                          r: 5,
                          cx: 5
                      },
                      magnet:false
                  }
              }
          }
      },
      attrs: {
          '.body': {
              x:"0", y:"0",height:"50px", width:"50px",
          },
          '.statusImg': {
              x:"55", y:"-20",height:"25px", width:"25px",
              "xlink:href": ""
          },
          '.remove': {
              x:"55", y:"-20",height:"25px", width:"25px",
              "xlink:href": "assets/layouts/layout/img/delete.png"
          },
          magnet:true,
          // text: {
          //   textWrap: {
          //     text: task.name,
          //     width: -10, // reference width minus 10
          //     height: '50%', // half of the reference height
          //     'font-size': 10, style: { 'text-shadow': '1px 1px 1px lightgray' }
          //   }
          // }
          text: { text: 'Default Element',y: '60px', 'font-size': 10, style: { 'text-shadow': '1px 1px 1px lightgray' } }
      }
  };
  var customElements = {
    'dag' : angular.merge({},defaultElement,{
      elementType:'dag',
      inPorts : null,
      attrs: {
          '.body': {
              elementType : 'dag',
              "xlink:href": obj.elementDefs['dag'].iconPath
          },
          '.remove': null
        }
    }),
    'stage' : angular.merge({},defaultElement,{
      elementType:'stage',
      attrs: {
          '.body': {
              elementType : 'stage',
              "xlink:href": obj.elementDefs['stage'].iconPath
          }
        }
    }),
    'dq' : angular.merge({},defaultElement,{
      elementType:'dq',
      attrs: {
          '.body': {
              elementType : 'dq',
              "xlink:href": obj.elementDefs['dq'].iconPath
          }
        }
    }),
    'dqgroup' : angular.merge({},defaultElement,{
      elementType:'dqgroup',
      attrs: {
          '.body': {
              elementType : 'dqgroup',
              "xlink:href": obj.elementDefs['dqgroup'].iconPath
          }
        }
    }),
    'map' : angular.merge({},defaultElement,{
      elementType:'map',
      attrs: {
          '.body': {
              elementType : 'map',
              "xlink:href": obj.elementDefs['map'].iconPath
          }
        }
    }),
    'load' : angular.merge({},defaultElement,{
      elementType:'load',
      attrs: {
          '.body': {
              elementType : 'load',
              "xlink:href": obj.elementDefs['load'].iconPath
          }
        }
    }),
    'profile' : angular.merge({},defaultElement,{
      elementType:'profile',
      attrs: {
          '.body': {
              elementType : 'profile',
              "xlink:href": obj.elementDefs['profile'].iconPath
          }
        }
    }),
    'profilegroup' : angular.merge({},defaultElement,{
      elementType:'profilegroup',
      attrs: {
          '.body': {
              elementType : 'profilegroup',
              "xlink:href": obj.elementDefs['profilegroup'].iconPath
          }
        }
    }),
    'model' : angular.merge({},defaultElement,{
      elementType:'model',
      attrs: {
          '.body': {
              elementType : 'model',
              "xlink:href": obj.elementDefs['model'].iconPath
          }
        }
    }),
   'rulegroup' : angular.merge({},defaultElement,{
      elementType:'rulegroup',
      attrs: {
          '.body': {
              elementType : 'rulegroup',
              "xlink:href": obj.elementDefs['rulegroup'].iconPath
          }
        }
    }),
   'rule' : angular.merge({},defaultElement,{
      elementType:'rule',
      attrs: {
          '.body': {
              elementType : 'rule',
              "xlink:href": obj.elementDefs['rule'].iconPath
          }
        }
    }),
   
    'stageInactive' : angular.merge({},defaultElement,{
      elementType:'stage',
      attrs: {
          '.body': {
              elementType : 'stage',
              "xlink:href": obj.elementDefs['stage'].iconPathInactive
          }
        }
    }),
    'dqInactive' : angular.merge({},defaultElement,{
      elementType:'dq',
      attrs: {
          '.body': {
              elementType : 'dq',
              "xlink:href": obj.elementDefs['dq'].iconPathInactive
          }
        }
    }),
    'dqgroupInactive' : angular.merge({},defaultElement,{
      elementType:'dqgroup',
      attrs: {
          '.body': {
              elementType : 'dqgroup',
              "xlink:href": obj.elementDefs['dqgroup'].iconPathInactive
          }
        }
    }),
    'mapInactive' : angular.merge({},defaultElement,{
      elementType:'map',
      attrs: {
          '.body': {
              elementType : 'map',
              "xlink:href": obj.elementDefs['map'].iconPathInactive
          }
        }
    }),
    'loadInactive' : angular.merge({},defaultElement,{
      elementType:'load',
      attrs: {
          '.body': {
              elementType : 'load',
              "xlink:href": obj.elementDefs['load'].iconPathInactive
          }
        }
    }),
    'profileInactive' : angular.merge({},defaultElement,{
      elementType:'profile',
      attrs: {
          '.body': {
              elementType : 'profile',
              "xlink:href": obj.elementDefs['profile'].iconPathInactive
          }
        }
    }),
    'profilegroupInactive' : angular.merge({},defaultElement,{
      elementType:'profilegroup',
      attrs: {
          '.body': {
              elementType : 'profilegroup',
              "xlink:href": obj.elementDefs['profilegroup'].iconPathInactive
          }
        }
    }),
    'modelInactive' : angular.merge({},defaultElement,{
      elementType:'model',
      attrs: {
          '.body': {
              elementType : 'model',
              "xlink:href": obj.elementDefs['model'].iconPathInactive
          }
        }
    }),
   'rulegroupInactive' : angular.merge({},defaultElement,{
      elementType:'rulegroup',
      attrs: {
          '.body': {
              elementType : 'rulegroup',
              "xlink:href": obj.elementDefs['rulegroup'].iconPathInactive
          }
        }
    }),
   'ruleInactive' : angular.merge({},defaultElement,{
      elementType:'rule',
      attrs: {
          '.body': {
              elementType : 'rule',
              "xlink:href": obj.elementDefs['rule'].iconPathInactive
          }
        }
    }),
  };

  var defaultLink = {
      attrs: { '.connection': { stroke: 'gray' },'.marker-target': { d: 'M 10 0 L 0 5 L 10 10 z',fill:'gray',stroke:'gray' } }
  };
  obj.getCustomElement = function (type,isTemplate) {
    if(type.slice(-4) == 'Exec'){
      type = type.slice(0,-4);
    }
    if(validElementTypes.indexOf(type) == -1){
      return console.error('invalid element type');
    }
    if(isTemplate == true && type !='dag'){
      type=type+"Inactive";
    }
    return customElements[type];
  };
  obj.getDefaultElement = function () {
    return defaultElement;
  };
  obj.getDefaultLink = function () {
    return defaultLink;
  };
  obj.navigateTo = function (ref) {
    $rootScope.previousState = {name : $state.current.name, params : $state.params};
    var ispresent=false
    if(ispresent !=true){
    var state={};
    state.route=obj.elementDefs[ref.type.toLowerCase()].detailState;
    state.param={id:ref.uuid,version:ref.version || " ",name:ref.name,type:ref.type,mode:true,returnBack:true}
    state.active=false;
    $rootScope.$broadcast('onAddTab',state);
    //$rootScope.addTab(state);
    }

    $state.go(obj.elementDefs[ref.type.toLowerCase()].detailState,{id:ref.uuid,version:ref.version || " ",mode:true,returnBack:true});

    //route in new tab :
    // var url = $state.href(obj.elementDefs[ref.type].detailState,{id:ref.uuid,version:ref.version,mode:true,returnBack:true});
    // window.open(url,'_blank');
  }
  return obj;
});
