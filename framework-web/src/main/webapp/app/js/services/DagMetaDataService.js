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
  // ng-disabled="grid.appScope.newType.indexOf(\'dagexec\')!=-1?[\'Completed\',\'Not Started\',\'Terminating\',\'Failed\',\'In Progress\',\'Killed\'].indexOf(row.entity.status)==-1:grid.appScope.newType.indexOf(\'group\')==-1?[\'Completed\',\'Killed\'].indexOf(row.entity.status)==-1:[\'Completed\',\'In Progress\',\'Killed\',\'Failed\',\'Terminating\'].indexOf(row.entity.status)==-1"
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
      parentIconCaption:'Pipeline',
      execType:'dagexec',
      metaType:'dag',
      iconPath : 'assets/layouts/layout/img/dag.svg',
      allowInMenu : false,
      detailState: 'createwf',
      childMenu:[],
      allowInChildMenu : false,
    },
    'stage':{
      name : 'stage',
      caption:'Stage',
      color : 'blue',
      icon : 'stage.svg',
      parentIconCaption:'Stage',
      execType:'',
      metaType:'',
      iconPath : 'assets/layouts/layout/img/stage.svg',
      iconPathInactive:'assets/layouts/layout/img/stageinactive.svg',
      allowInMenu : true,
      childMenu:[],
      allowInChildMenu : false,
    },
    'dq':{
      name : 'dq',
      caption:"Rule",
      color : 'orange',
      icon : 'dq.svg',
      parentIconCaption:'Data Quality',
      childIconCaption:'Rule',
      execType:'dqexec',
      metaType:'dq',
      iconPath : 'assets/layouts/layout/img/dq.svg',
      iconPathInactive:'assets/layouts/layout/img/dqinactive.svg',
      allowInMenu : true,
      listState:'viewdataquality',
      detailState: 'createdataquality',
      state: 'createdataquality',
      childMenu:['dq','dqgroup'],
      allowInChildMenu : true,
    },
    'dqgroup':{
      name : 'dqgroup',
      caption:" Rule Group",
      color : 'orange',
      icon : 'dq.svg',
      parentIconCaption:"",
      childIconCaption:'Rule Group',
      execType:'dqgroupExec',
      metaType:'dqgroup',
      iconPath : 'assets/layouts/layout/img/dq.svg',
      iconPathInactive:'assets/layouts/layout/img/dqgroupinactive.svg',
      allowInMenu : false,
      listState:'viewdataqualitygroup',
      detailState:'createdataqualitygroup',
      state: 'createdataqualitygroup',
      childMenu:[],
      allowInChildMenu : true,
    },
    'map':{
      name : 'map',
      caption:"Map",
      color : '#FF4081',
      icon : 'map.svg',
      parentIconCaption:'Map',
      childIconCaption:'Map',
      execType:'mapExec',
      metaType:'map',
      iconPath : 'assets/layouts/layout/img/map.svg',
      iconPathInactive:'assets/layouts/layout/img/mapinactive.svg',
      allowInMenu : false,
      state: 'metaListmap',
      detailState: 'metaListmap',
      childMenu:[],
      allowInChildMenu : true,
    },
    'load':{
      name : 'load',
      caption:'Load',
      color : '#933f5b',
      icon : 'load.svg',
      parentIconCaption:'Load',
      execType:'loadExec',
      metaType:'load',
      iconPath : 'assets/layouts/layout/img/load.svg',
      iconPathInactive:'assets/layouts/layout/img/loadinactive.svg',
      allowInMenu : false,
      state : 'metaListload',
      detailState : 'metaListload',
      childMenu:[],
      allowInChildMenu : false,
    },
    'profile':{
      name : 'profile',
      caption:'Profile',
      color : '#29B6F6',
      icon : 'profile.svg',
      parentIconCaption:' Data Profiling',
      childIconCaption:'Rule',
      execType:'profileExec',
      metaType:'profile',
      iconPath : 'assets/layouts/layout/img/profile.svg',
      iconPathInactive:'assets/layouts/layout/img/profileinactive.svg',
      allowInMenu : true,
      state: 'createprofile',
      detailState: 'createprofile',
      childMenu:['profile','profilegroup'],
      allowInChildMenu :true,
    },
    'profilegroup':{
      name : 'profilegroup',
      caption:'Profile Group',
      color : '#00796B',
      icon : 'profilegroup.svg',
      parentIconCaption:'',
      childIconCaption:'Rule Group',
      execType:'profilegroupExec',
      metaType:'profilegroup',
      iconPath : 'assets/layouts/layout/img/profilegroup.svg',
      iconPathInactive:'assets/layouts/layout/img/profilegroupinactive.svg',
      allowInMenu : false,
      state: 'createprofilegroup',
      detailState: 'createprofilegroup',
      childMenu:[],
      allowInChildMenu :true,
    },
    'model':{
      name : 'model',
      caption : 'Model',
      color : '#1DE9B6',
      icon : 'model.svg',
      parentIconCaption:'Data Science',
      childIconCaption:'',
      execType:'modelExec',
      metaType:'model',
      iconPath : 'assets/layouts/layout/img/model.svg',
      allowInMenu : true,
      state: 'createmodel',
      detailState: 'createmodel',
      childMenu:["train","predict","simulate"],
      allowInChildMenu : false,
    },
    
    'rulegroup':{
      name : 'rulegroup',
      caption : 'Rule Group',
      color : '#2489D1',
      icon : 'rulegroup.svg',
      parentIconCaption:'',
      childIconCaption:'Rule Group',
      execType:'rulegroupExec',
      metaType:'rulegroup',
      iconPath : 'assets/layouts/layout/img/rulegroup.svg',
      allowInMenu : false,
      listState:'rulesgroup',
      detailState: 'createrulesgroup',
      state: 'createrulesgroup',
      childMenu:[],
      allowInChildMenu : true,
    },
    'rule':{
      name : 'rule',
      caption : 'Rule',
      color : '#2489D1',
      icon : 'rule.svg',
      parentIconCaption:'Business Rules',
      childIconCaption:'Rule',
      execType:'ruleExec',
      metaType:'rule',
      iconPath : 'assets/layouts/layout/img/rule.svg',
      allowInMenu :true,
      listState:'viewrule',
      detailState: 'createrules',
      state: 'createrules',
      childMenu:['rule','rulegroup'],
      allowInChildMenu :true,

    },
    'recon':{
      name : 'recon',
      caption : 'Rule',
      color : '#2489D1',
      icon : 'rule.svg',
      parentIconCaption:'Data Recon',
      childIconCaption:'Rule',
      execType:'reconExec',
      metaType:'recon',
      iconPath : 'assets/layouts/layout/img/recon.svg',
      iconPathInactive:'assets/layouts/layout/img/reconinactive.svg',
      allowInMenu : true,
      listState:'datareconrule',
      detailState: 'createreconerule',
      state: 'createreconerule',
      childMenu:['recon','recongroup'],
      allowInChildMenu : true,
    },

    'recongroup':{
      name : 'recongroup',
      caption : 'Rule Group',
      color : '#2489D1',
      icon : 'rule.svg',
      parentIconCaption:'',
      childIconCaption:'Rule Group',
      execType:'recongroupExec',
      metaType:'recongroup',
      iconPath : 'assets/layouts/layout/img/recongroup.svg',
      iconPathInactive:'assets/layouts/layout/img/recongroupinactive.svg',
      allowInMenu : false,
      listState:'datareconrulegroup',
      detailState: 'createreconerulegroup',
      state: 'createreconerulegroup',
      childMenu:[],
      allowInChildMenu : true,
    },

    'reconexec':{
      name : 'recon',
      caption :'Recon Exec',
      color : '#EB54C3',
      icon : '',
      parentIconCaption:'',
      execType:'reconExec',
      metaType:'reconExec',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState :'jobexecutorlistreconexec',
      resultState:'viewdrresultspage',
      childMenu:[],
      allowInChildMenu : false,
    },
    'recongroupexec':{
      name : 'recongroup',
      caption :'Recon Group Exec',
      execType:'recongroupExec',
      metaType:'recongroupExec',
      color : '#EB54C3',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState :'jobexecutorlistrecongroupexec',
      childMenu:[],
      allowInChildMenu : false,
    },
    'loadexec':{
      name : 'load',
      caption :'Load Exec',
      execType:'loadExec',
      metaType:'loadExec',
      color : '#EB54C3',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState:'jobmonitoringlistloadexec',
      childMenu:[],
      allowInChildMenu : false,
    },
    'mapexec':{
      name : 'map',
      caption :'Map Exec',
      execType:'mapExec',
      metaType:'mapExec',
      color : '#EB54C3',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState :'jobexecutorlistmapexec',
      childMenu:[],
      allowInChildMenu : false,
    },
    'dqgroupexec':{
      name : 'dqgroup',
      caption :'DQ Group Exec',
      execType:'dqgroupExec',
      metaType:'dqgroupExec',
      color : '#dceaab',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState :'jobexecutorlistdqgroupexec',
      childMenu:[],
      allowInChildMenu : false,
    },

    'ruleexec':{
      name : 'rule',
      caption :'Rule Exec',
      execType:'ruleExec',
      metaType:'ruleExec',
      color : '#EB54C3',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState :'jobexecutorlistruleexec',
      resultState:'rulerestultpage',
      childMenu:[],
      allowInChildMenu : false,
    },
    'rulegroupexec':{
      name : 'rulegroup',
      caption :'Rule Group Exec',
      execType:'rulegroupExec',
      metaType:'rulegroupExec',
      color : '#EB54C3',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState :'jobexecutorlistrulegroupexec',
      childMenu:[],
      allowInChildMenu : false,
    },
    'profileexec':{
      name : 'profile',
      caption :'Profile Exec',
      execType:'profileExec',
      metaType:'profileExec',
      color : '#EB54C3',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState :'jobexecutorlistprofileexec',
      resultState :'viewprofileresultspage',
      childMenu:[],
      allowInChildMenu : false,
    },
    'profilegroupexec':{
      name : 'profilegroup',
      caption : 'Profile Group Exec',
      execType:'profilegroupExec',
      metaType:'profilegroupExec',
      color : '#EB54C3',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState :'jobexecutorlistprofilegroupexec',
      resultState :'viewprofileresultspage',
      childMenu:[],
      allowInChildMenu : false,
    },
    'dagexec':{
      name : 'pipeline',
      caption : 'Pipeline Exec',
      execType:'dagexec',
      metaType:'dagexec',
      color : '#33691E',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState :'jobexecutorlistdagexec',
      resultState :'resultgraphwf',
      childMenu:[],
      allowInChildMenu : false,
    },
    'downloadexec':{
      name : 'download',
      caption : 'download Exec',
      execType:'downloadExec',
      metaType:'downloadExec',
      color : '#33691E',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState :'jobexecutorlistdownloadexec',
      resultState :'resultgraphwf',
      childMenu:[],
      allowInChildMenu : false,
    },
    'uploadexec':{
      name : 'download',
      caption : 'upload Exec',
      execType:'uploadExec',
      metaType:'uploadExec',
      color : '#33691E',
      parentIconCaption:'',
      allowInMenu : false,
      listState   : 'jobmonitoringlist',
      detailState :'jobexecutorlistuploadexec',
      resultState :'resultgraphwf',
      childMenu:[],
      allowInChildMenu : false,
    },
    'pipelineexec':{
      name : 'pipeline',
      caption : 'Pipeline Exec',
      execType:'dagexec',
      metaType:'dagexec',
      color : '#EB54C3',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState :'jobexecutorlistdagexec',
      childMenu:[],
      allowInChildMenu : false,
    },
     'vizexec':{
      name : 'vizpod',
      caption:'Vizpod Exec',
      execType:'vizexec',
      metaType:'vizexec',
      color : '#fff8dc',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState :'jobexecutorlistvizpodexec',
      childMenu:[],
      allowInChildMenu : false,

    },
    'viz':{
     name : 'vizpod',
     caption:'Vizpod',
     execType:'vizexec',
     metaType:'vizpod',
     color : '#fff8dc',
     parentIconCaption:'',
     allowInMenu : false,
     listState : 'jobmonitoringlist',
     detailState :'jobexecutorlistvizpodexec',
     childMenu:[],
     allowInChildMenu : false,

   },
     'vizpodexec':{
      name : 'vizpod',
      caption : 'Vizpod Exec',
      execType:'vizexec',
      metaType:'vizexec',
      color : '#fff8dc',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState :'jobexecutorlistvizpodexec',
      childMenu:[],
      allowInChildMenu : false,
    },
     'dqexec':{
      name : 'dq',
      caption : 'Data Quality Exec',
      execType:'dqexec',
      metaType:'dqexec',
      color : '#EB54C3',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState :'jobexecutorlistdqexec',
      resultState:'viewdqresultspage',
      childMenu:[],
      allowInChildMenu : false,
    },
     'modelexec':{
      name : 'model',
      caption : 'Model Exec',
      execType:'modelExec',
      metaType:'modelExec',
      color : '#EB54C3',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState :'jobexecutorlistmodelexec',
      resultState:'modelrestultpage',
      childMenu:[],
      allowInChildMenu : false,
    },

    'datapod':{
      name : 'datapod',
      caption : 'Datapod',
      execType:'',
      metaType:'datapod',
      color : '#00E5FF',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'metadata',
      detailState:'metaListdatapod',
      childMenu:[],
      allowInChildMenu : false,
    },
    'pipeline':{
      name : 'dag',
      caption : 'Pipeline',
      execType:'dagexec',
      metaType:'dag',
      color : '#EB54C3',
      parentIconCaption:'',
      allowInMenu : false,
      listState : '',
      detailState:'jobexecutorlistdagexec',
      childMenu:[],
      allowInChildMenu : false,
    },

    'dataset':{
      name : 'dataset',
      caption : 'Data Set',
      execType:'',
      metaType:'dataset',
      color : '#00695C',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'metadata',
      detailState:'metaListdataset',
      childMenu:[],
      allowInChildMenu : false,
    },
    'expression':{
      name : 'expression',
      caption : 'Expression',
      execType:'',
      metaType:'expression',
      color : '#6A1B9A',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'metadata',
      detailState:'metaListexpression',
      childMenu:[],
      allowInChildMenu : false,
    },
     'filter':{
      name : 'filter',
      caption : 'Filter',
      execType:'',
      metaType:'filter',
      color : '#5BF5ED',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'metadata',
      detailState:'metaListfilter',
      childMenu:[],
      allowInChildMenu : false,
    },
    'formula':{
      name : 'formula',
      caption : 'Formula',
      execType:'',
      metaType:'formula',
      color : '#FFB300',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'metadata',
      detailState:'metaListformula',
      childMenu:[],
      allowInChildMenu : false,
    },
    'function':{
      name : 'function',
      caption : 'Function',
      execType:'',
      metaType:'function',
      color : '#f79742',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'metadata',
      detailState:'metaListfunction',
      childMenu:[],
      allowInChildMenu : false,
    },
    'relation':{
      name : 'relation',
      caption : 'Relation',
      execType:'',
      metaType:'relation',
      color : '#76FF03',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'metadata',
      detailState:'metaListrelation',
      childMenu:[],
      allowInChildMenu : false,
    },
    'dashboard':{
      name : 'dashboard',
      caption : 'Dashboard',
      execType:'',
      metaType:'dashboard',
      color : '#75E108',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'dashboard',
      detailState:'showdashboard',
      childMenu:[],
      allowInChildMenu : false,
    },
    'vizpod':{
      name : 'vizpod',
      caption : 'Vizpod',
      execType:'vizexec',
      metaType:'vizpod',
      color : '#41E0F5',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'vizpodlist',
      detailState:'dvvizpod',
      childMenu:[],
      allowInChildMenu : false,
    },
    'formulainfo':{
        name : 'formulainfo',
        caption : 'Formula Info',
        execType:'vizexec',
        metaType:'vizpod',
        color : '#FFE082',
        parentIconCaption:'',
        allowInMenu : false,
        listState : 'vizpodlist',
        detailState:'dvvizpod',
        childMenu:[],
        allowInChildMenu : false,
      },
    'sectioninfo':{
        name : 'sectioninfo',
        caption : 'Section Info',
        execType:'vizexec',
        metaType:'vizpod',
        color : '#c4ff9a',
        parentIconCaption:'',
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
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'algorithm',
      detailState:'createalgorithm',
      childMenu:[],
      allowInChildMenu : false,
    },
    'distribution':{
      name : 'distribution',
      caption : 'Distribution',
      execType:'',
      metaType:'algorithm',
      color : '#00E676',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'distribution',
      detailState:'createdistribution',
      childMenu:[],
      allowInChildMenu : false,
    },
    'paramlist':{
      name : 'paramlist',
      caption : 'Parameter List',
      execType:'',
      metaType:'paramlist',
      color : '#CDDC39',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'paramlist',
      detailState:'createparamlist',
      childMenu:[],
      allowInChildMenu : false,
    },
    'paramset':{
      name : 'paramset',
      caption : 'Parameter set',
      execType:'',
      metaType:'paramset',
      color : '#E6EE9C',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'paramset',
      detailState:'createparamset',
      childMenu:[],
      allowInChildMenu : false,
    },
    'activity':{
      name : 'activity',
      caption : 'Activity',
      execType:'',
      metaType:'activity',
      color : '#EB54C3',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'admin',
      detailState:'adminListactivity',
      childMenu:[],
      allowInChildMenu : false,
    },
    'application':{
      name : 'application',
      caption : 'Application',
      execType:'',
      metaType:'application',
      color : '#f794e0',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'admin',
      detailState:'adminListapplication',
      childMenu:[],
      allowInChildMenu : false,
    },
    'datasource':{
      name : 'datasource',
      caption : 'Datasource',
      execType:'',
      metaType:'datasource',
      color : '#FF80AB',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'admin',
      detailState:'adminListdatasource',
      childMenu:[],
      allowInChildMenu : false,
    },
    'datastore':{
      name : 'datastore',
      caption : 'Datastore',
      execType:'',
      metaType:'datastore',
      color : '#efefef',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'admin',
      detailState:'adminListdatastore',
      childMenu:[],
      allowInChildMenu : false,
    },
    'group':{
      name : 'group',
      caption : 'Group',
      execType:'',
      metaType:'group',
      color : '#fce5cd',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'admin',
      detailState:'adminListgroup',
      childMenu:[],
      allowInChildMenu : false,
    },
    'privilege':{
      name : 'privilege',
      caption : 'Privilege',
      execType:'',
      metaType:'privilege',
      color : '#cfe2f3',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'admin',
      detailState:'adminListprivilege',
      childMenu:[],
      allowInChildMenu : false,
    },
    'role':{
      name : 'role',
      caption : 'Role',
      execType:'',
      metaType:'role',
      color : '#ebdef0',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'admin',
      detailState:'adminListrole',
      childMenu:[],
      allowInChildMenu : false,
    },
    'session':{
      name : 'session',
      caption : 'Session',
      execType:'session',
      metaType:'session',
      color : '#d7fcc0',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'admin',
      detailState:'adminListsession',
      childMenu:[],
      allowInChildMenu : false,
    },
    'user':{
      name : 'user',
      caption : 'User',
      execType:'user',
      metaType:'user',
      color : '#b7b7b7',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'admin',
      detailState:'adminListuser',
      childMenu:[],
      allowInChildMenu : false,
    },
    'export':{
      name : 'export',
      caption :'Export',
      execType:'export',
      metaType:'export',
      color : '#EB54C3',
      parentIconCaption:'',
      allowInMenu :false,
      listState : '',
      detailState :'detaitexport',
      childMenu:[],
      allowInChildMenu : false,
    },
    'import':{
      name : 'import',
      caption :'Import',
      execType:'import',
      metaType:'import',
      color : '#EB54C3',
      parentIconCaption:'',
      allowInMenu :false,
      listState : '',
      detailState :'detaitimport',
      childMenu:[],
      allowInChildMenu : false,
    },
   'filterinfo':{
     name : 'filterinfo',
     caption :'Filter Info',
     execType:'',
     metaType:'',
     color : '#F5CBA7',
     parentIconCaption:'',
     allowInMenu :false,
     listState : '',
     detailState :'',
     childMenu:[],
     allowInChildMenu : false,
   },
   'simple':{
     name : 'simple',
     caption :'Simple',
     execType:'',
     metaType:'',
     color : '#BBDEFB',
     parentIconCaption:'',
     allowInMenu :false,
     listState : '',
     detailState :'',
     childMenu:[],
     allowInChildMenu : false,
   
   },
   'attributemap':{
     name : 'attributemap',
     caption :'Attribute Map',
     execType:'',
     metaType:'',
     color : '#F8BBD0',
     parentIconCaption:'',
     allowInMenu :false,
     listState : '',
     detailState :'',
     childMenu:[],
     allowInChildMenu : false,
   },
   'stages':{
     name : 'stages',
     caption :'Stages',
     execType:'',
     metaType:'',
     color : '#7CB342',
     parentIconCaption:'',
     allowInMenu :false,
     listState : '',
     detailState :'',
     childMenu:[],
     allowInChildMenu : false,
   },
   'tasks':{
     name : 'tasks',
     caption :'Tasks',
     execType:'',
     metaType:'',
     color : '#AED581',
     parentIconCaption:'',
     allowInMenu :false,
     listState : '',
     detailState :'',
     childMenu:[],
     allowInChildMenu : false,
   
   },
   'operators':{
     name : 'operators',
     caption :'Operators',
     execType:'',
     metaType:'',
     color : '#DCEDC8',
     parentIconCaption:'',
     allowInMenu :false,
     listState : '',
     detailState :'',
     childMenu:[],
     allowInChildMenu : false,
   },
   'operator':{
    name : 'operator',
    caption :'Operator',
    execType:'Operatorexec',
    metaType:'Operator',
    iconPath : 'assets/layouts/layout/img/operator.svg',
    color : '#DCEDC8',
    parentIconCaption:'Operator',
    childIconCaption:'',
    allowInMenu :true,
    listState : 'operator',
    detailState :'createoperator',
    state: 'createoperatortype',
    childMenu:['map','generatedata','transpose'],
    allowInChildMenu : false,
  },
  'operatorexec':{
    name : 'operatorexec',
    caption :'Operator Exec',
    execType:'operatorexec',
    metaType:'operatorexec',
    color : '#DCEDC8',
    parentIconCaption:'',
    allowInMenu :false,
    listState : 'jobmonitoringlist',
    detailState :'jobexecutorlistoperatorexec',
    resultState:'modelrestultpage',
    childMenu:[],
    allowInChildMenu : false,
  },
   'meta':{
     name : 'meta',
     caption :'Meta',
     execType:'',
     metaType:'',
     color : '#fcff85',
     parentIconCaption:'',
     allowInMenu :false,
     listState : '',
     detailState :'',
     childMenu:[],
     allowInChildMenu : false,
   },
   'from_base':{
	    name : 'from_base',
	    caption :'from_base',
	    execType:'',
	    metaType:'',
      color : '#f79742',
      parentIconCaption:'',
	    allowInMenu :false,
	    listState : '',
      detailState :'',
      childMenu:[],
      allowInChildMenu : false,
	  },
   'attributes':{
	    name : 'attributes',
	    caption :'Attributes',
	    execType:'',
	    metaType:'',
      color : '#84FFFF',
      parentIconCaption:'',
	    allowInMenu :false,
	    listState : '',
      detailState :'',
      childMenu:[],
      allowInChildMenu : false,
    },
    'attributeinfo':{
	    name : 'attributeinfo',
	    caption :'AttributeInfo',
	    execType:'',
	    metaType:'',
      color : '#B3E5FC',
      parentIconCaption:'',
	    allowInMenu :false,
	    listState : '',
      detailState :'',
      childMenu:[],
      allowInChildMenu : false,
    },
   'logicaloperator':{
	    name : 'logicaloperator',
	    caption :'LogicalOperator',
	    execType:'',
	    metaType:'',
      color : '#E1BEE7',
      parentIconCaption:'',
	    allowInMenu :false,
	    listState : '',
      detailState :'',
      childMenu:[],
      allowInChildMenu : false,   
    },
    'expressioninfo':{
	    name : 'expressioninfo',
	    caption :'ExpressionInfo',
	    execType:'',
	    metaType:'',
      color : '#BA68C8',
      parentIconCaption:'',
	    allowInMenu :false,
	    listState : '',
      detailState :'',
      childMenu:[],
      allowInChildMenu : false,
    },
    'operand':{
      name : 'operand',
      caption :'Operand',
      execType:'',
      metaType:'',
      color : '#E1BEE7',
      parentIconCaption:'',
      allowInMenu :false,
      listState : '',
      detailState :'',
      childMenu:[],
      allowInChildMenu : false,
    },
    'relationinfo':{
	    name : 'relationinfo',
	    caption :'relationInfo',
	    execType:'',
	    metaType:'',
      color : '#CCFF90',
      parentIconCaption:'',
	    allowInMenu :false,
	    listState : '',
      detailState :'',
      childMenu:[],
      allowInChildMenu : false,
    },
    'joinkey':{
	    name : 'joinkey',
	    caption :'JoinKey',
	    execType:'',
	    metaType:'',
      color : '#EB54C3',
      parentIconCaption:'',
	    allowInMenu :false,
	    listState : '',
      detailState :'',
      childMenu:[],
      allowInChildMenu : false,
	  },
    'paraminfo':{
	    name : 'paramInfo',
	    caption :'Parameter Info',
	    execType:'',
	    metaType:'',
      color : '#EB54C3',
      parentIconCaption:'',
	    allowInMenu :false,
      listState : '',
      childMenu:[],
      allowInChildMenu : false,
    },
    'paramsetval':{
	    name : 'paramsetval',
	    caption :'ParamSet Val',
	    execType:'',
	    metaType:'',
      color : '#EB54C3',
      parentIconCaption:'',
	    allowInMenu :false,
      listState : '',
      childMenu:[],
      allowInChildMenu : false,
    },
    'params':{
	    name : 'params',
	    caption :'Params',
	    execType:'',
	    metaType:'',
      color : '#EB54C3',
      parentIconCaption:'',
	    allowInMenu :false,
      listState : '',
      childMenu:[],
      allowInChildMenu : false,
    },
    'features':{
	    name : 'features',
	    caption :'Features',
	    execType:'',
	    metaType:'',
      color : '#B9F6CA',
      parentIconCaption:'',
	    allowInMenu :false,
      listState : '',
      childMenu:[],
      allowInChildMenu : false,
    },
    'appinfo':{
	    name : 'appinfo',
	    caption :'App Info',
	    execType:'',
	    metaType:'',
      color : '#EEFF41',
      parentIconCaption:'',
	    allowInMenu :false,
      listState : '',
      childMenu:[],
      allowInChildMenu : false,
    },
    'privilegeinfo':{
	    name : 'privilegeinfo',
	    caption :'Privilege Info',
	    execType:'', 
	    metaType:'',
      color : '#00E676',
      parentIconCaption:'',
	    allowInMenu :false,
      listState : '',
      childMenu:[],
      allowInChildMenu : false,
    },
    'roleinfo':{
	    name : 'roleinfo',
	    caption :'Role Info',
	    execType:'', 
	    metaType:'',
      color : '#00E676',
      parentIconCaption:'',
	    allowInMenu :false,
      listState : '',
      childMenu:[],
      allowInChildMenu : false,
    },
    'ruleinfo':{
	    name : 'ruleinfo',
	    caption :'Rule Info',
	    execType:'', 
	    metaType:'',
      color : '#80CBC4',
      parentIconCaption:'',
	    allowInMenu :false,
	    listState : '',
    },
    'functioninfo':{
	    name : 'functioninfo',
	    caption :'Function Info',
	    execType:'', 
	    metaType:'',
      color : '#d7c288',
      parentIconCaption:'',
	    allowInMenu :false,
      listState : '',
      childMenu:[],
      allowInChildMenu : false,
    },
    'execlist':{
	    name : 'execlist',
	    caption :'Exec List',
	    execType:'', 
	    metaType:'',
      color : '#80CBC4',
      parentIconCaption:'',
	    allowInMenu :false,
      listState : '',
      childMenu:[],
      allowInChildMenu : false,
    },
    'keys':{
	    name : 'keys',
	    caption :'Keys',
	    execType:'', 
	    metaType:'',
      color : '#D50000',
      parentIconCaption:'',
	    allowInMenu :false,
      listState : '',
      childMenu:[],
      allowInChildMenu : false, 
    },
    'values':{
	    name : 'values',
	    caption :'Values',
	    execType:'', 
	    metaType:'',
      color : '#EF5350',
      parentIconCaption:'',
	    allowInMenu :false,
      listState : '',
      childMenu:[],
      allowInChildMenu : false,
   
    },
    'groups':{
	    name : 'groups',
	    caption :'Groups',
	    execType:'', 
	    metaType:'',
      color : '#e4ff9d',
      parentIconCaption:'',
	    allowInMenu :false,
      listState : '',
      childMenu:[],
      allowInChildMenu : false,
    },
    'groupinfo':{
	    name : 'groupinfo',
	    caption :'Groups Info',
	    execType:'', 
	    metaType:'',
      color : '#FFEB3B',
      parentIconCaption:'',
	    allowInMenu :false,
      listState : '',
      childMenu:[],
      allowInChildMenu : false,
    },
    'refkeylist':{
	    name : 'refkeylist',
	    caption :'Refkey List',
	    execType:'', 
	    metaType:'',
      color : '#80CBC4',
      parentIconCaption:'',
	    allowInMenu :false,
      listState : '',
      childMenu:[],
      allowInChildMenu : false,
    },
    'predict':{
      name : 'predict',
      caption : 'Prediction',
      color : '#1DE9B6',
      icon : 'model.svg',
      parentIconCaption:'',
      childIconCaption:'Prediction',
      execType:'predictExec',
      metaType:'predict',
      iconPath : 'assets/layouts/layout/img/predict.svg',
      allowInMenu : false,
      state: 'createpredict',
      detailState: 'createpredict',
      childMenu:[],
      allowInChildMenu : true,
    },
    'predictexec':{
      name : 'predictexec',
      caption : 'Prediction Exec',
      execType:'predictExec',
      metaType:'predictExec',
      color : '#EB54C3',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState :'jobexecutorlistpredictexec',
      resultState:'modelrestultpage'
    },
    'simulate':{
      name : 'simulate',
      caption : 'Simulation',
      color : '#1DE9B6',
      icon : 'model.svg',
      parentIconCaption:'',
      childIconCaption:'Simulation',
      execType:'simulateExec',
      metaType:'simulate',
      iconPath : 'assets/layouts/layout/img/simulate.svg',
      allowInMenu : false,
      state: 'createsimulate',
      detailState: 'createsimulate',
      childMenu:[],
      allowInChildMenu : true,
    },
    'simulateexec':{
      name : 'simulateexec',
      caption : 'Simulation Exec',
      execType:'simulateExec',
      metaType:'simulateExec',
      color : '#EB54C3',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState :'jobexecutorlistsimulateexec',
      resultState:'modelrestultpage',
      childMenu:[],
      allowInChildMenu : false,
    },
    'train':{
      name : 'train',
      caption : 'Training',
      color : '#1DE9B6',
      icon : 'model.svg',
      parentIconCaption:'',
      childIconCaption:'Training',
      execType:'trainExec',
      metaType:'simulate',
      iconPath : 'assets/layouts/layout/img/train.svg',
      allowInMenu : false,
      state: 'createtrain',
      detailState: 'createtrain',
      childMenu:[],
      allowInChildMenu : true,
    },
    'trainexec':{
      name : 'trainexec',
      caption : 'train Exec',
      execType:'trainexec',
      metaType:'trainexec',
      color : '#EB54C3',
      parentIconCaption:'',
      allowInMenu : false,
      listState : 'jobmonitoringlist',
      detailState :'jobexecutorlisttarinexec',
      resultState:'modelrestultpage',
      childMenu:[],
      allowInChildMenu : false,
    },
    'featureattrmap':{
      name : 'featureattrmap',
      caption :'Feature Attr Map',
      execType:'', 
      metaType:'',
      color : '#c6ff00',
      parentIconCaption:'',
      allowInMenu :false,
      listState : '',
      childMenu:[],
      allowInChildMenu : false,
    },
    'appconfig':{
      name : 'appconfig',
      caption :'App Config',
      execType:'', 
      metaType:'appConfig',
      color : '#c6ff00',
      parentIconCaption:'',
      detailState: 'createappconfig',
      allowInMenu :false,
      listState : '',
      childMenu:[],
      allowInChildMenu : false,
    },
    'operatortype':{
      name : 'operatortype',
      caption :'Operator',
      execType:'', 
      metaType:'operatortype',
      color : '#c6ff00',
      parentIconCaption:'',
      childIconCaption:'Operator Type',
      iconPath : 'assets/layouts/layout/img/operator.svg',
      detailState: 'createoperatortype',
      allowInMenu :false,
      listState : 'operatortype',
      childMenu:[],
      allowInChildMenu : false,
    },
    'generatedata':{
      name : 'operatortype',
      caption :'Generate Data',
      execType:'', 
      metaType:'operatortype',
      color : '#c6ff00',
      parentIconCaption:'',
      childIconCaption:'Generate Data',
      iconPath : 'assets/layouts/layout/img/operator.svg',
      detailState: 'createoperatortype',
      allowInMenu :false,
      listState : 'operatortype',
      childMenu:[],
      allowInChildMenu : true,
    },
    'transpose':{
      name : 'operatortype',
      caption :'Transpose',
      execType:'', 
      metaType:'operatortype',
      color : '#c6ff00',
      parentIconCaption:'',
      childIconCaption:'Transpose',
      iconPath : 'assets/layouts/layout/img/operator.svg',
      detailState: 'createoperatortype',
      allowInMenu :false,
      listState : 'operatortype',
      childMenu:[],
      allowInChildMenu : true,
	  }
  };

  var validElementTypes = ['dag','stage','dq','dqgroup','map','load','profile','profilegroup','model','rule','rulegroup','train','predict','simulate','recon','recongroup','operatortype','operator'];
  obj.validTaskTypes = ['dq','dqgroup','map','load','profile','profilegroup','model','rule','rulegroup','train','predict','simulate','recon','recongroup','operatortype','operator'];
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
    'recon' : angular.merge({},defaultElement,{
      elementType:'recon',
      attrs: {
        '.body': {
          elementType : 'recon',
          "xlink:href": obj.elementDefs['recon'].iconPath
        }
      }
    }),
    'recongroup' : angular.merge({},defaultElement,{
      elementType:'recongroup',
      attrs: {
        '.body': {
          elementType : 'recongroup',
          "xlink:href": obj.elementDefs['recongroup'].iconPath
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
    'train' : angular.merge({},defaultElement,{
      elementType:'train',
      attrs: {
        '.body': {
          elementType : 'train',
          "xlink:href": obj.elementDefs['train'].iconPath
        }
      }
    }),
    'predict' : angular.merge({},defaultElement,{
      elementType:'predict',
      attrs: {
        '.body': {
          elementType : 'predict',
          "xlink:href": obj.elementDefs['predict'].iconPath
        }
      }
    }),
    'simulate' : angular.merge({},defaultElement,{
      elementType:'simulate',
      attrs: {
        '.body': {
          elementType : 'simulate',
          "xlink:href": obj.elementDefs['simulate'].iconPath
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
    
    'operatortype' : angular.merge({},defaultElement,{
      elementType:'operatortype',
      attrs: {
        '.body': {
          elementType : 'operatortype',
          "xlink:href": obj.elementDefs['operatortype'].iconPath
        }
      }
    }),
    'operator' : angular.merge({},defaultElement,{
      elementType:'operator',
      attrs: {
        '.body': {
          elementType : 'operator',
          "xlink:href": obj.elementDefs['operator'].iconPath
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
    'reconInactive' : angular.merge({},defaultElement,{
      elementType:'recon',
      attrs: {
        '.body': {
          elementType : 'recon',
          "xlink:href": obj.elementDefs['recon'].iconPathInactive
        }
      }
    }),
    'recongroupInactive' : angular.merge({},defaultElement,{
      elementType:'recongroup',
      attrs: {
        '.body': {
          elementType : 'recongroup',
          "xlink:href": obj.elementDefs['recongroup'].iconPathInactive
        }
      }
    }),
  };

  var defaultLink = {
    attrs: { '.connection': { stroke: 'gray' },'.marker-target': { d: 'M 10 0 L 0 5 L 10 10 z',fill:'gray',stroke:'gray' } }
  };
  
  obj.getCustomElement = function (type,isTemplate,addMode) {
    if(type.slice(-4) == 'Exec'){
      type = type.slice(0,-4);
    }
    if(validElementTypes.indexOf(type) == -1){
      return console.error('invalid element type');
    }
    if(isTemplate == true &&  addMode && type !='dag'){
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
    }
    $state.go(obj.elementDefs[ref.type.toLowerCase()].detailState,{id:ref.uuid,version:ref.version || " ",mode:true,returnBack:true});
  }
  return obj;
});
