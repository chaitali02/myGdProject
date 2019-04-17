import { Injectable } from '@angular/core';
@Injectable()
export class AppMetadata {
  validTaskTypes: string[];
  defaultElement: any
  validElementTypes: string[];
  defaultLink: { attrs: { '.connection': { stroke: string; }; '.marker-target': { d: string; fill: string; stroke: string; }; }; };
  customElements: { 'dag': any; 'stage': any; 'dq': any; 'dqgroup': any; 'map': any; 'load': any; 'profile': any; 'profilegroup': any; 'model': any; 'rulegroup': any; 'rule': any; 'recon': any, 'recongroup': any,'ingest':any,'ingestgroup':any};
  obj: any;
  constructor() {
    this.obj = {};
    this.obj.metadataDefs = {
      'dag': {
        name: 'dag',
        caption: 'Pipeline',
        execType: 'dagexec',
        metaType: 'dag',
        color: '#EB54C3',
        icon: '',
        iconPath: 'assets/img/dag.svg',
        iconCaption:'',
        allowInMenu: false,
        listState: '',
        detailState: 'dag',
        resultState: '',
        moduleState: 'dataPipeline',
        moduleCaption: 'Data Pipeline',
        class: 'fa fa-random'
      },
      'dq': {
        name: 'dataqual',
        caption: "Data Quality",
        execType: 'dqexec',
        metaType: 'dq',
        color: 'orange',
        icon: 'dq.svg',
        iconPath: 'assets/img/dq.svg',
        iconCaption: '',
        allowInMenu: true,
        listState: 'viewdataquality',
        detailState: 'dq',
        resultState: '',
        moduleState: 'dataQuality',
        moduleCaption: 'Data Quality',
        class: 'fa fa-rss'
      },
      'dqgroup': {
        name: 'dqgroup',
        caption: "Data Quality Group",       
        execType: 'dqgroupexec',
        metaType: 'dqgroup',
        color: 'orange',
        icon: 'dqgroup.svg',
        iconPath: 'assets/img/dq.svg',
        iconCaption: '',
        allowInMenu: true,
        listState: 'viewdataqualitygroup',
        detailState: 'dqgroup',
        resultState: '',
        moduleState: 'dataQuality',
        moduleCaption: 'Data Quality',
        class: 'fa fa-rss'
      },
      'map': {
        name: 'map',
        caption: "Map",
        execType: 'mapExec',
        metaType: 'map',
        color: '#f75b8f',
        icon: 'map.svg',
        iconPath: 'assets/img/map.svg',
        iconCaption: '',
        allowInMenu: true,
        listState: '',
        state: 'metaListmap',
        detailState: 'map',
        resultState: '',
        moduleState: 'dataPreparation',
        moduleCaption: 'Data Preparation',
        class: 'fa fa-link'
      },
      'load': {
        name: 'load',
        caption: 'Load',
        execType: 'loadExec',
        metaType: 'load',
        color: '#933f5b',
        icon: 'load.svg',
        iconPath: 'assets/img/load.svg',
        iconCaption: '',
        allowInMenu: true,
        state: 'metaListload',
        listState: '',
        detailState: 'load',
        resultState: '',
        moduleState: 'dataPreparation',
        moduleCaption: 'Data Preparation',
        class: 'fa fa-link'
      },
      'profile': {
        name: 'profile',
        caption: 'Profile',
        execType: 'profileExec',
        metaType: 'profile',
        color: '#00A8F0',
        icon: 'profile.svg',
        iconPath: 'assets/img/profile.svg',
        iconCaption: '',
        allowInMenu: true,
        state: 'createprofile',
        listState: '',
        detailState: 'profile',
        resultState: '',
        moduleState: 'dataProfiling',
        moduleCaption: 'Data Profiling',
        class: 'fa fa-users'
      },
      'profilegroup': {
        name: 'profilegroup',
        caption: 'Profile Group',
        execType: 'profilegroupExec',
        metaType: 'profilegroup',
        color: '#00A8F0',
        icon: 'profilegroup.svg',
        iconPath: 'assets/img/profilegroup.svg',
        allowInMenu: true,
        state: 'createprofilegroup',
        detailState: 'profilegroup',
        listState: '',
        resultState: '',
        moduleCaption: 'Data Profiling',
        moduleState: 'dataProfiling',
        class: 'fa fa-users'
      },
      'model': {
        name: 'model',
        caption: 'Model',
        execType: 'modelExec',
        metaType: 'model',
        color: '#4132C7',
        icon: 'model.svg',
        iconPath: 'assets/img/model.svg',
        allowInMenu: true,
        //state: 'createmodel',
        detailState: 'model',
        listState: '',
        resultState: '',
        moduleState: 'dataScience',
        moduleCaption: 'Data Science',
        class: 'fa fa-flask'
      },
      'rulegroup': {
        name: 'rulegroup',
        caption: 'Rule Group',
        execType: 'rulegroupExec',
        metaType: 'rulegroup',
        color: '#2489D1',
        icon: 'rulegroup.svg',
        iconPath: 'assets/img/rulegroup.svg',
        allowInMenu: false,
        listState: 'rulesgroup',
        detailState: 'rulegroup',
        resultState: '',
        //state: 'createrulesgroup',
        moduleState: 'businessRules',
        moduleCaption: 'Business Rule',
        class: 'fa fa-cogs'
      },
      'rule': {
        name: 'rule',
        caption: 'Rule',
        execType: 'ruleExec',
        metaType: 'rule',
        color: '#2489D1',
        icon: 'rule.svg',
        iconPath: 'assets/img/rule.svg',
        iconCaption: '',
        allowInMenu: false,
        listState: 'viewrule',
        detailState: 'rule',
        moduleState: 'businessRules',
        moduleCaption: 'Business Rule',
        resultState: '',
        //state: 'createrules',
        class: 'fa fa-cogs'
      },
      'recon': {
        name: 'recon',
        caption: 'Rule',
        iconCaption: 'Recon',
        execType: 'reconExec',
        metaType: 'recon',
        color: '#2489D1',
        icon: 'rule.svg',
        iconPath: 'assets/img/recon.svg',
        iconPathInactive: 'assets/img/reconinactive.svg',
        allowInMenu: true,
        listState: 'datareconrule',
        detailState: 'createreconerule',
        state: 'createreconerule',
        moduleState: 'recon',
        moduleCaption: 'Data Reconciliation',
        childMenu: [],
        allowInChildMenu: false,
        class: 'fa fa-compress'
      },

      'recongroup': {
        name: 'recongroup',
        caption: 'Rule Group',
        color: '#2489D1',
        icon: 'rule.svg',
        iconCaption: 'Recon Group',
        execType: 'recongroupExec',
        metaType: 'recongroup',
        iconPath: 'assets/img/recongroup.svg',
        iconPathInactive: 'assets/img/recongroupinactive.svg',
        allowInMenu: true,
        listState: 'datareconrulegroup',
        detailState: 'createreconerulegroup',
        state: 'createreconerulegroup',
        moduleCaption: 'Data Reconciliation',
        moduleState: 'recongroup',
        childMenu: [],
        allowInChildMenu: false,
        class: 'fa fa-compress'
      },










      'reconexec': {
        name: 'recon',
        caption: 'Recon',
        color: '#EB54C3',
        icon: '',
        iconCaption: '',
        execType: 'reconExec',
        metaType: 'reconExec',
        allowInMenu: false,
        moduleState: 'reconexec',
        listState: 'jobmonitoringlist',
        detailState: 'jobexecutorlistreconexec',
        resultState: 'viewdrresultspage',
        moduleCaption: 'Data Reconciliation',
        childMenu: [],
        allowInChildMenu: false,
        class: 'fa fa-compress'
      },
      'reconExec': {
        name: 'recon',
        caption: 'Recon Exec',
        execType: 'reconexec',
        metaType: 'reconexec',
        color: '#8E44AD',
        iconCaption: '',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'reconExec',
        moduleState: 'JobMonitoring',
        resultState: 'reconExec',
        moduleCaption: 'Job Monitoring',
        childMenu: [],
        allowInChildMenu: false,
        class: 'fa fa-compress'
      },
      'recongroupexec': {
        name: 'recongroup',
        caption: 'Recon Group',
        execType: 'recongroupExec',
        metaType: 'recongroupExec',
        color: '#EB54C3',
        iconCaption: '',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        moduleState: 'reconexec',
        detailState: 'jobexecutorlistrecongroupexec',
        moduleCaption: 'Data Reconciliation',
        resultState: 'viewdrresultspage',
        childMenu: [],
        allowInChildMenu: false,
        class: 'fa fa-compress'
      },
      'recongroupExec': {
        name: 'recongroup',
        caption: 'Recon Group Exec',
        execType: 'recongroupExec',
        metaType: 'recongroupExec',
        color: '#EB54C3',
        iconCaption: '',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        resultState: 'recongroupExec',
        detailState: 'recongroupExec',
        moduleState: 'JobMonitoring',
        moduleCaption: 'Job Monitoring',
        childMenu: [],
        allowInChildMenu: false,
        class: 'fa fa-compress'
      },
      'loadexec': {
        name: 'load',
        caption: 'load',
        execType: 'loadExec',
        metaType: 'loadExec',
        color: '#EB54C3',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'loadExec',
        moduleState: 'JobMonitoring',
        moduleCaption: 'Job Monitoring',
        class: 'fa fa-link'
      },
      'loadExec': {
        name: 'load',
        caption: 'Load',
        execType: 'loadExec',
        metaType: 'loadExec',
        color: '#EB54C3',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'loadExec',
        moduleState: 'JobMonitoring',
        moduleCaption: 'Job Monitoring',
        class: 'fa fa-link'
      },
  
      'mapExec': {
        name: 'map',
        caption: 'Map',
        execType: 'mapExec',
        metaType: 'map',
        color: '#EB54C3',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'mapExec',
        moduleState: 'JobMonitoring',
        moduleCaption: 'Job Monitoring',
        class: 'fa fa-link'
      },
      'dqgroupexec': {
        name: 'dqgroup',
        caption: 'DQ Group',
        execType: 'dqgroupExec',
        metaType: 'dqgroupExec',
        color: '#dceaab',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        resultState: "dqgroupexec",
        detailState: 'jobexecutorlistdqgroupexec',
        moduleState: 'dataQuality',
        moduleCaption: 'Job Monitoring',
        class: 'fa fa-rss'
      },
      'dqgroupExec': {
        name: 'dqgroup',
        caption: 'DQ Group',
        execType: 'dqgroupExec',
        metaType: 'dqgroupExec',
        color: '#dceaab',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        resultState: "dqgroupExec",
        detailState: 'dqgroupExec',
        moduleState: 'JobMonitoring',
        moduleCaption: 'Job Monitoring',
        class: 'fa fa-rss'
      },
      'ruleexec': {
        name: 'rule',
        caption: 'Rule',
        execType: 'ruleExec',
        metaType: 'ruleExec',
        color: '#EB54C3',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'ruleExec',
        resultState: 'ruleexec',
        moduleState: 'businessRules',
        moduleCaption: 'Business Rule',
        class: 'fa fa-cogs'
      },
      'ruleExec': {
        name: 'rule',
        caption: 'Rule',
        execType: 'ruleExec',
        metaType: 'ruleExec',
        color: '#EB54C3',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'ruleExec',
        resultState: 'ruleExec',
        moduleState: 'JobMonitoring',
        moduleCaption: 'Job Monitoring',
        class: 'fa fa-cogs'
      },
      'rulegroupexec': {
        name: 'rulegroup',
        caption: 'Rule Group',
        execType: 'rulegroupExec',
        metaType: 'rulegroupExec',
        color: '#EB54C3',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'jobexecutorlistrulegroupexec',
        resultState: 'rulegroupexec',
        moduleState: 'businessRules',
        moduleCaption: 'Business Rule',
        class: 'fa fa-cogs'
      },
      'rulegroupExec': {
        name: 'rulegroup',
        caption: 'Rule Group',
        execType: 'rulegroupExec',
        metaType: 'rulegroupExec',
        color: '#EB54C3',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'rulegroupExec',
        resultState: 'rulegroupExec',
        moduleState: 'JobMonitoring',
        moduleCaption: 'Job Monitoring',
        class: 'fa fa-cogs'
      },
      'profileexec': {
        name: 'profile',
        caption: 'Profile',
        execType: 'profileExec',
        metaType: 'profileExec',
        color: '#EB54C3',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'profileExec',
        resultState: 'profileexec',
        moduleState: 'dataProfiling',
        moduleCaption: 'Data Profiling',
        class: 'fa fa-users'
      },
      'profileExec': {
        name: 'profile',
        caption: 'Profile',
        execType: 'profileExec',
        metaType: 'profileExec',
        color: '#EB54C3',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'profileExec',
        resultState: 'profileExec',
        moduleState: 'JobMonitoring',
        moduleCaption: 'Job Monitoring',
        class: 'fa fa-users'
      },
      'profilegroupexec': {
        name: 'profilegroup',
        caption: 'Profile Group',
        execType: 'profilegroupExec',
        metaType: 'profilegroupExec',
        color: '#EB54C3',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'profilegroupExec',
        resultState: 'profilegroupexec',
        moduleState: 'dataProfiling',
        moduleCaption: 'Data Profiling',
        class: 'fa fa-users'
      },
      'profilegroupExec': {
        name: 'profilegroup',
        caption: 'Profile Group',
        execType: 'profilegroupExec',
        metaType: 'profilegroupExec',
        color: '#EB54C3',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'profilegroupExec',
        resultState: 'profilegroupExec',
        moduleState: 'JobMonitoring',
        moduleCaption: 'Job Monitoring',
        class: 'fa fa-users'
      },
      'dagexec': {
        name: 'pipeline',
        caption: 'Pipeline',
        execType: 'dagexec',
        metaType: 'dagexec',
        color: '#e59866',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'jobexecutorlistdagexec',
        resultState: 'dagexec',
        moduleState: 'dataPipeline',
        moduleCaption: 'Data Pipeline',
        class: 'fa fa-random'
      },
      'dagExec': {
        name: 'dag',
        caption: 'Pipeline',
        execType: 'dagExec',
        metaType: 'dagExec',
        color: '#EB54C3',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'dagExec',
        resultState: 'dagExec',
        moduleState: 'JobMonitoring',
        moduleCaption: 'Job Monitoring',
        class: 'fa fa-flask'
      },
      'downloadexec': {
        name: 'downloadexec',
        caption: 'Download',
        execType: 'downloadexec',
        metaType: 'downloadexec',
        color: '#5C9BD1',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'downloadExec',
        resultState: 'downloadexec',
        moduleState: 'dataScience',
        moduleCaption: 'Data Science',
        class: 'fa fa-download'
      },
      'downloadExec': {
        name: 'download',
        caption: 'download Exec',
        execType: 'downloadExec',
        metaType: 'downloadExec',
        color: '#5C9BD1',
        iconCaption: '',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'downloadExec',
        moduleState: 'JobMonitoring',
        resultState: 'downloadExec',
        moduleCaption: 'Job Monitoring',
        class: 'fa fa-download'
      },
      'uploadexec': {
        name: 'upload',
        caption: 'Upload',
        execType: 'uploadexec',
        metaType: 'uploadexec',
        color: '#2AB4C0',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'uploadExec',
        resultState: 'uploadexec',
        moduleState: 'admin',
        moduleCaption: 'Admin',
        class: 'fa fa-upload'
      },
      'uploadExec': {
        name: 'upload',
        caption: 'upload Exec',
        execType: 'uploadExec',
        metaType: 'uploadExec',
        color: '#2AB4C0',
        iconCaption: '',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'uploadExec',
        moduleState: 'JobMonitoring',
        resultState: 'uploadExec',
        moduleCaption: 'Job Monitoring',
        class: 'fa fa-upload'
      },
      'pipelineexec': {
        name: 'pipeline',
        caption: 'Pipeline',
        execType: 'dagexec',
        metaType: 'dagexec',
        color: '#EB54C3',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'jobexecutorlistdagexec',
        moduleState: 'dataPipeline',
        moduleCaption: 'Data Pipeline',
        class: 'fa fa-random'
      },
      'vizexec': {
        name: 'vizpod',
        caption: 'Vizpod',
        execType: 'vizexec',
        metaType: 'vizexec',
        color: '#fff8dc',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'vizExec',
        moduleState: 'JobMonitoring',
        moduleCaption: 'Job Monitoring',
        class: 'fa fa-line-chart'

      },
      'vizExec': {
        name: 'vizpod',
        caption: 'Vizpod',
        execType: 'vizexec',
        metaType: 'vizexec',
        color: '#fff8dc',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'vizExec',
        moduleState: 'JobMonitoring',
        moduleCaption: 'Job Monitoring',
        class: 'fa fa-line-chart'
      },
      'viz': {
        name: 'vizpod',
        caption: 'Vizpod',
        execType: 'vizexec',
        metaType: 'vizpod',
        color: '#fff8dc',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'jobexecutorlistvizpodexec',
        moduleState: 'dataVisualization',
        moduleCaption: 'Data Visualization',
        class: 'fa fa-line-chart'
      },
      'vizpodexec': {
        name: 'vizpod',
        caption: 'Vizpod',
        execType: 'vizexec',
        metaType: 'vizexec',
        color: '#fff8dc',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'jobexecutorlistvizpodexec',
        moduleState: 'dataVisualization',
        moduleCaption: 'Data Visualization',
        class: 'fa fa-line-chart'
      },
      'dqexec': {
        name: 'dq',
        caption: 'Data Quality',
        execType: 'dqexec',
        metaType: 'dqexec',
        color: '#EB54C3',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'jobexecutorlistdqexec',
        resultState: 'dqexec',
        moduleState: 'dataQuality',
        moduleCaption: 'Data Quality',
        class: 'fa fa-rss'
      },
      'dqExec': {
        name: 'dq',
        caption: 'Data Quality',
        execType: 'dqExec',
        metaType: 'dqExec',
        color: '#EB54C3',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'dqExec',
        resultState: 'dqExec',
        moduleState: 'JobMonitoring',
        moduleCaption: 'Job Monitoring',
        class: 'fa fa-rss'
      },
      'ModelResult': {
        name: 'model',
        caption: 'ModelResult',
        execType: 'modelExec',
        metaType: 'modelExec',
        color: '#EB54C3',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'modelExec',
        resultState: 'modelexec',
        moduleState: 'dataScience',
        moduleCaption: 'Data Science',
        class: 'fa fa-flask'
      },
      'modelExec': {
        name: 'model',
        caption: 'Model',
        execType: 'modelExec',
        metaType: 'modelExec',
        color: '#EB54C3',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'modelExec',
        resultState: 'modelExec',
        moduleState: 'JobMonitoring',
        moduleCaption: 'Job Monitoring',
        class: 'fa fa-flask'
      },

      'datapod': {
        name: 'datapod',
        caption: 'Datapod',
        execType: '',
        metaType: 'datapod',
        color: '#606df2',
        allowInMenu: false,
        listState: 'metadata',
        detailState: 'datapod',
        moduleState: 'dataPreparation',
        moduleCaption: 'Data Preparation',
        class: 'fa fa-link'
      },
      'pipeline': {
        name: 'dag',
        caption: 'Pipeline',
        execType: 'dagexec',
        metaType: 'dag',
        color: '#EB54C3',
        allowInMenu: false,
        listState: '',
        detailState: 'jobexecutorlistdagexec',
        moduleState: 'dataPipeline',
        moduleCaption: 'Data Pipeline',
        class: 'fa fa-random'
      },

      'dataset': {
        name: 'dataset',
        caption: 'Dataset',
        execType: '',
        metaType: 'dataset',
        color: '#0d01b5',
        allowInMenu: false,
        listState: 'metadata',
        detailState: 'dataset',
        moduleState: 'dataPreparation',
        moduleCaption: 'Data Preparation',
        class: 'fa fa-link'
      },
      'expression': {
        name: 'expression',
        caption: 'Expression',
        execType: '',
        metaType: 'expression',
        color: '#740491',
        allowInMenu: false,
        listState: 'metadata',
        detailState: 'expression',
        moduleState: 'dataPreparation',
        moduleCaption: 'Data Preparation',
        class: 'fa fa-link'
      },
      'filter': {
        name: 'filter',
        caption: 'Filter',
        execType: '',
        metaType: 'filter',
        color: '#5BF5ED',
        allowInMenu: false,
        listState: 'metadata',
        detailState: 'filter',
        moduleState: 'dataPreparation',
        moduleCaption: 'Data Preparation',
        class: 'fa fa-link'
      },
      'formula': {
        name: 'formula',
        caption: 'Formula',
        execType: '',
        metaType: 'formula',
        color: '#EB54C3',
        allowInMenu: false,
        listState: 'metadata',
        detailState: 'formula',
        moduleState: 'dataPreparation',
        moduleCaption: 'Data Preparation',
        class: 'fa fa-link'
      },
      'function': {
        name: 'function',
        caption: 'Function',
        execType: '',
        metaType: 'function',
        color: '#f79742',
        allowInMenu: false,
        listState: 'metadata',
        detailState: 'function',
        moduleState: 'dataPreparation',
        moduleCaption: 'Data Preparation',
        class: 'fa fa-link'
      },
      'relation': {
        name: 'relation',
        caption: 'Relation',
        execType: '',
        metaType: 'relation',
        color: '#75E108',
        allowInMenu: false,
        listState: 'metadata',
        detailState: 'relation',
        moduleState: 'dataPreparation',
        moduleCaption: 'Data Preparation',
        class: 'fa fa-link'
      },
      'dashboard': {
        name: 'dashboard',
        caption: 'Dashboard',
        execType: '',
        metaType: 'dashboard',
        color: '#AFFF75',
        allowInMenu: false,
        listState: 'dashboard',
        moduleState: 'dataVisualization',
        moduleCaption: 'Data Visualization',
        graphState: 'dashboard',
        detailState: 'dashboarddetail',
        class: 'fa fa-desktop'
      },
      'vizpod': {
        name: 'vizpod',
        caption: 'Vizpod',
        execType: 'vizexec',
        metaType: 'vizpod',
        color: '#41E0F5',
        allowInMenu: false,
        listState: 'vizpodlist',
        detailState: 'vizpod',
        moduleState: 'dataVisualization',
        moduleCaption: 'Data Visualization',
        class: 'fa fa-desktop'
      },
      'formulainfo': {
        name: 'formulainfo',
        caption: 'Formula Info',
        execType: 'vizexec',
        metaType: 'vizpod',
        color: '#41E0F5',
        allowInMenu: false,
        listState: 'vizpodlist',
        detailState: 'dvvizpod',
        moduleState: 'dataVisualization',
        moduleCaption: 'Data Visualization',
        class: 'fa fa-line-chart'
      },
      'sectioninfo': {
        name: 'sectioninfo',
        caption: 'Section Info',
        execType: 'vizexec',
        metaType: 'vizpod',
        color: '#c4ff9a',
        allowInMenu: false,
        listState: 'vizpodlist',
        detailState: 'dvvizpod',
        moduleState: 'dataVisualization',
        moduleCaption: 'Data Visualization',
        class: 'fa fa-line-chart'
      },
      'algorithm': {
        name: 'algorithm',
        caption: 'Algorithm',
        execType: '',
        metaType: 'algorithm',
        color: '#EB54C3',
        allowInMenu: false,
        listState: 'algorithm',
        detailState: 'algorithm',
        moduleState: 'dataScience',
        moduleCaption: 'Data Science',
        class: 'fa fa-flask'
      },
      'distribution': {
        name: 'distribution',
        caption: 'Distribution',
        execType: '',
        metaType: 'algorithm',
        color: '#00E676',
        iconCaption: '',
        allowInMenu: false,
        listState: 'distribution',
        detailState: 'distribution',
        moduleState: 'dataScience',
        moduleCaption: 'Data Science',
        childMenu: [],
        allowInChildMenu: false,
        class: 'fa fa-flask'
      },
      'paramlist': {
        name: 'paramlist',
        caption: 'Parameter List',
        execType: '',
        metaType: 'paramlist',
        color: '#EB54C3',
        allowInMenu: false,
        listState: 'paramlist',
        detailState: 'paramlist',
        moduleState: '',
        moduleCaption: '',
        class: 'fa fa-flask'
      },
      'paramset': {
        name: 'paramset',
        caption: 'Parameter set',
        execType: '',
        metaType: 'paramset',
        color: '#EB54C3',
        allowInMenu: false,
        listState: 'paramset',
        detailState: 'paramset',
        moduleState: 'dataScience',
        moduleCaption: 'Data Science',
        class: 'fa fa-flask'
      },
      'activity': {
        name: 'activity',
        caption: 'Activity',
        execType: '',
        metaType: 'activity',
        color: '#EB54C3',
        allowInMenu: false,
        listState: 'admin',
        detailState: 'activity',
        moduleState: 'admin',
        moduleCaption: 'Admin',
        class: 'fa fa-wrench'
      },
      'application': {
        name: 'application',
        caption: 'Application',
        execType: '',
        metaType: 'application',
        color: '#f794e0',
        allowInMenu: false,
        listState: 'admin',
        detailState: 'application',
        moduleState: 'admin',
        moduleCaption: 'Admin',
        class: 'fa fa-wrench'
      },
      'datasource': {
        name: 'datasource',
        caption: 'Datasource',
        execType: '',
        metaType: 'datasource',
        color: '#EC7063',
        allowInMenu: false,
        listState: 'admin',
        detailState: 'datasource',
        moduleState: 'admin',
        moduleCaption: 'Admin',
        class: 'fa fa-wrench'
      },
      'datastore': {
        name: 'datastore',
        caption: 'Datastore',
        execType: '',
        metaType: 'datastore',
        color: '#efefef',
        allowInMenu: false,
        listState: 'admin',
        detailState: 'datastore',
        moduleState: 'admin',
        moduleCaption: 'Admin',
        class: 'fa fa-wrench'
      },
      'group': {
        name: 'group',
        caption: 'Group',
        execType: '',
        metaType: 'group',
        color: '#fce5cd',
        allowInMenu: false,
        listState: 'admin',
        detailState: 'group',
        moduleState: 'admin',
        moduleCaption: 'Admin',
        class: 'fa fa-wrench'
      },
      'privilege': {
        name: 'privilege',
        caption: 'Privilege',
        execType: '',
        metaType: 'privilege',
        color: '#cfe2f3',
        allowInMenu: false,
        listState: 'admin',
        detailState: 'privilege',
        moduleState: 'admin',
        moduleCaption: 'Admin',
        class: 'fa fa-wrench'
      },
      'role': {
        name: 'role',
        caption: 'Role',
        execType: '',
        metaType: 'role',
        color: '#ebdef0',
        allowInMenu: false,
        listState: 'admin',
        detailState: 'role',
        moduleState: 'admin',
        moduleCaption: 'Admin',
        class: 'fa fa-wrench'
      },
      'session': {
        name: 'session',
        caption: 'Session',
        execType: 'session',
        metaType: 'session',
        color: '#d7fcc0',
        allowInMenu: false,
        listState: 'admin',
        detailState: 'session',
        moduleState: 'admin',
        moduleCaption: 'Admin',
        class: 'fa fa-wrench'
      },
      'user': {
        name: 'user',
        caption: 'User',
        execType: 'user',
        metaType: 'user',
        color: '#b7b7b7',
        allowInMenu: false,
        listState: 'admin',
        detailState: 'user',
        moduleState: 'admin',
        moduleCaption: 'Admin',
        class: 'fa fa-wrench'
      },
      'export': {
        name: 'export',
        caption: 'Export',
        execType: 'export',
        metaType: 'export',
        color: '#EB54C3',
        allowInMenu: false,
        listState: 'export',
        detailState: 'migration-assist',
        moduleCaption: 'Admin',
        moduleState: 'admin'
      },
      'import': {
        name: 'import',
        caption: 'Import',
        execType: 'import',
        metaType: 'import',
        color: '#EB54C3',
        allowInMenu: false,
        listState: 'import',
        moduleCaption: 'Admin',
        moduleState: 'admin',
        detailState: 'migration-assist'
      },
      'filterinfo': {
        name: 'filterinfo',
        caption: 'Filter Info',
        execType: '',
        metaType: '',
        color: '#F5CBA7',
        allowInMenu: false,
        listState: '',
        moduleCaption: '',
        moduleState: '',
        detailState: ''
      },
      'simple': {
        name: 'simple',
        caption: 'Simple',
        execType: '',
        metaType: '',
        color: '#BBDEFB',
        allowInMenu: false,
        listState: '',
        moduleCaption: '',
        moduleState: '',
        detailState: ''
      },
      'attributemap': {
        name: 'attributemap',
        caption: 'Attribute Map',
        execType: '',
        metaType: '',
        color: '#F8BBD0',
        allowInMenu: false,
        listState: '',
        moduleCaption: '',
        moduleState: '',
        detailState: ''
      },
      'stages': {
        name: 'stages',
        caption: 'Stages',
        execType: '',
        metaType: '',
        color: '#7CB342',
        allowInMenu: false,
        listState: '',
        moduleCaption: '',
        moduleState: '',
        detailState: ''
      },
      'tasks': {
        name: 'tasks',
        caption: 'Tasks',
        execType: '',
        metaType: '',
        color: '#AED581',
        allowInMenu: false,
        listState: '',
        moduleCaption: '',
        moduleState: '',
        detailState: ''
      },
      'operators': {
        name: 'operators',
        caption: 'Operators',
        execType: '',
        metaType: '',
        color: '#DCEDC8',
        allowInMenu: false,
        listState: '',
        moduleCaption: '',
        moduleState: '',
        detailState: ''
      },
      'operator': {
        name: 'operator',
        caption: 'Operator',
        execType: 'Operatorexec',
        metaType: 'Operator',
        iconPath: 'assets/img/operator.svg',
        color: '#DCEDC8',
        parentIconCaption: 'Operator',
        allowInMenu: true,
        listState: 'operator',
        detailState: 'operator',
        moduleState: 'dataScience',
        moduleCaption: 'Data Science',
        //  detailState: 'createoperator'
        class: 'fa fa-flask'
      },
      'operatorexec': {
        name: 'operatorexec',
        caption: 'Operator',
        execType: 'operatorexec',
        metaType: 'operatorexec',
        color: '#DCEDC8',
        parentIconCaption: '',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        moduleCaption: 'Data Science',
        moduleState: 'Data Science',
        detailState: 'jobexecutorlistoperatorexec'
      },
      'meta': {
        name: 'meta',
        caption: 'Meta',
        execType: '',
        metaType: '',
        color: '#fcff85',
        parentIconCaption: '',
        allowInMenu: false,
        listState: '',
        moduleCaption: '',
        moduleState: '',
        detailState: ''
      },
      'from_base': {
        name: 'from_base',
        caption: 'from_base',
        execType: '',
        metaType: '',
        color: '#f79742',
        parentIconCaption: '',
        allowInMenu: false,
        listState: '',
        moduleCaption: '',
        moduleState: '',
        detailState: ''
      },
      'attributes': {
        name: 'attributes',
        caption: 'Attributes',
        execType: '',
        metaType: '',
        color: '#84FFFF',
        parentIconCaption: '',
        allowInMenu: false,
        listState: '',
        moduleCaption: '',
        moduleState: '',
        detailState: ''
      },
      'attributeinfo': {
        name: 'attributeinfo',
        caption: 'AttributeInfo',
        execType: '',
        metaType: '',
        color: '#B3E5FC',
        parentIconCaption: '',
        allowInMenu: false,
        listState: '',
        moduleCaption: '',
        moduleState: '',
        detailState: ''
      },
      'logicaloperator': {
        name: 'logicaloperator',
        caption: 'LogicalOperator',
        execType: '',
        metaType: '',
        color: '#E1BEE7',
        parentIconCaption: '',
        allowInMenu: false,
        listState: '',
        moduleCaption: '',
        moduleState: '',
        detailState: ''
      },
      'expressioninfo': {
        name: 'expressioninfo',
        caption: 'ExpressionInfo',
        execType: '',
        metaType: '',
        color: '#BA68C8',
        parentIconCaption: '',
        allowInMenu: false,
        listState: '',
        moduleCaption: '',
        moduleState: '',
        detailState: ''
      },
      'operand': {
        name: 'operand',
        caption: 'Operand',
        execType: '',
        metaType: '',
        color: '#E1BEE7',
        parentIconCaption: '',
        allowInMenu: false,
        listState: '',
        moduleCaption: '',
        moduleState: '',
        detailState: ''
      },
      'relationinfo': {
        name: 'relationinfo',
        caption: 'RelationInfo',
        execType: '',
        metaType: '',
        color: '#CCFF90',
        parentIconCaption: '',
        allowInMenu: false,
        listState: '',
        moduleCaption: '',
        moduleState: '',
        detailState: ''
      },
      'joinkey': {
        name: 'joinkey',
        caption: 'JoinKey',
        execType: '',
        metaType: '',
        color: '#EB54C3',
        parentIconCaption: '',
        allowInMenu: false,
        listState: '',
        moduleCaption: '',
        moduleState: '',
        detailState: ''
      },
      'paraminfo': {
        name: 'paraminfo',
        caption: 'Parameter Info',
        execType: '',
        metaType: '',
        color: '#EB54C3',
        parentIconCaption: '',
        allowInMenu: false,
        listState: '',
        moduleCaption: '',
        moduleState: '',
        detailState: ''
      },
      'paramsetval': {
        name: 'paramsetval',
        caption: 'ParamSet Val',
        execType: '',
        metaType: '',
        color: '#EB54C3',
        parentIconCaption: '',
        allowInMenu: false,
        listState: '',
        moduleCaption: '',
        moduleState: '',
        detailState: ''
      },
      'params': {
        name: 'params',
        caption: 'Params',
        execType: '',
        metaType: '',
        color: '#EB54C3',
        parentIconCaption: '',
        allowInMenu: false,
        listState: '',
        moduleCaption: '',
        moduleState: '',
        detailState: ''
      },
      'features': {
        name: 'features',
        caption: 'Features',
        execType: '',
        metaType: '',
        color: '#B9F6CA',
        parentIconCaption: '',
        allowInMenu: false,
        listState: '',
        moduleCaption: '',
        moduleState: '',
        detailState: ''
      },
      'appinfo': {
        name: 'appinfo',
        caption: 'App Info',
        execType: '',
        metaType: '',
        color: '#EEFF41',
        parentIconCaption: '',
        allowInMenu: false,
        listState: '',
        moduleCaption: '',
        moduleState: '',
        detailState: ''
      },
      'privilegeinfo': {
        name: 'privilegeinfo',
        caption: 'Privilege Info',
        execType: '',
        metaType: '',
        color: '#00E676',
        parentIconCaption: '',
        allowInMenu: false,
        listState: '',
        moduleCaption: '',
        moduleState: '',
        detailState: ''
      },
      'roleinfo': {
        name: 'roleinfo',
        caption: 'Role Info',
        execType: '',
        metaType: '',
        color: '#00E676',
        parentIconCaption: '',
        allowInMenu: false,
        listState: '',
        moduleCaption: '',
        moduleState: '',
        detailState: ''
      },
      'ruleinfo': {
        name: 'ruleinfo',
        caption: 'Rule Info',
        execType: '',
        metaType: '',
        color: '#00E676',
        parentIconCaption: '',
        allowInMenu: false,
        listState: '',
        moduleCaption: '',
        moduleState: '',
        detailState: ''
      },
      'functioninfo': {
        name: 'functioninfo',
        caption: 'Function Info',
        execType: '',
        metaType: '',
        color: '#d7c288',
        parentIconCaption: '',
        allowInMenu: false,
        listState: '',
        moduleCaption: '',
        moduleState: '',
        detailState: ''
      },
      'execlist': {
        name: 'execlist',
        caption: 'Exec List',
        execType: '',
        metaType: '',
        color: '#d7c288',
        parentIconCaption: '',
        allowInMenu: false,
        listState: '',
        moduleCaption: '',
        moduleState: '',
        detailState: ''
      },
      'keys': {
        name: 'keys',
        caption: 'Keys',
        execType: '',
        metaType: '',
        color: '#D50000',
        parentIconCaption: '',
        allowInMenu: false,
        listState: '',
        moduleCaption: '',
        moduleState: '',
        detailState: ''
      },
      'values': {
        name: 'values',
        caption: 'Values',
        execType: '',
        metaType: '',
        color: '#EF5350',
        parentIconCaption: '',
        allowInMenu: false,
        listState: '',
        moduleCaption: '',
        moduleState: '',
        detailState: ''
      },
      'groups': {
        name: 'groups',
        caption: 'Groups',
        execType: '',
        metaType: '',
        color: '#e4ff9d',
        parentIconCaption: '',
        allowInMenu: false,
        listState: '',
        moduleCaption: '',
        moduleState: '',
        detailState: ''
      },
      'groupinfo': {
        name: 'groupinfo',
        caption: 'Group Info',
        execType: '',
        metaType: '',
        color: '#FFEB3B',
        parentIconCaption: '',
        allowInMenu: false,
        listState: '',
        moduleCaption: '',
        moduleState: '',
        detailState: ''
      },
      'refkeylist': {
        name: 'refkeylist',
        caption: 'Refkey List',
        execType: '',
        metaType: '',
        color: '#80CBC4',
        parentIconCaption: '',
        allowInMenu: false,
        listState: '',
        moduleCaption: '',
        moduleState: '',
        detailState: ''
      },
      'predict': {
        name: 'predict',
        caption: 'Prediction',
        color: '#1DE9B6',
        icon: 'model.svg',
        iconCaption: 'Prediction',
        execType: 'predictExec',
        metaType: 'predict',
        iconPath: 'assets/layouts/layout/img/predict.svg',
        allowInMenu: false,
        detailState: 'prediction',
        moduleState: 'dataScience',
        moduleCaption: 'Data Science',
        childMenu: [],
        allowInChildMenu: true,
        class: 'fa fa-flask'
      },
      'predictexec': {
        name: 'predictexec',
        caption: 'Prediction',
        execType: 'predictexec',
        metaType: 'predictexec',
        color: '#EB54C3',
        iconCaption: '',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'predictExec',
        resultState: 'predictExec',
        moduleState: 'dataScience',
        moduleCaption: 'Data Science',
        childMenu: [],
        class: 'fa fa-flask'
      },
      'predictExec': {
        name: 'predict',
        caption: 'predict Exec',
        execType: 'predictExec',
        metaType: 'predictExec',
        color: '#EB54C3',
        iconCaption: '',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'predictExec',
        moduleState: 'JobMonitoring',
        resultState: 'predictExec',
        moduleCaption: 'Job Monitoring',
        class: 'fa fa-flask'
      },
      'simulate': {
        name: 'simulate',
        caption: 'Simulation',
        color: '#1DE9B6',
        icon: 'model.svg',
        iconCaption: 'Simulation',
        execType: 'simulateExec',
        metaType: 'simulate',
        iconPath: 'assets/layouts/layout/img/simulate.svg',
        allowInMenu: false,
        detailState: 'simulation',
        moduleState: 'dataScience',
        moduleCaption: 'Data Science ',
        childMenu: [],
        allowInChildMenu: true,
        class: 'fa fa-flask'
      },
      'simulateexec': {
        name: 'simulateexec',
        caption: 'Simulation',
        execType: 'simulateExec',
        metaType: 'simulateExec',
        color: '#EB54C3',
        iconCaption: '',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'jobexecutorlistsimulateexec',
        resultState: 'modelrestultpage',
        moduleState: 'dataScience',
        childMenu: [],
        allowInChildMenu: false,
        class: 'fa fa-flask'
      },

      'simulateExec': {
        name: 'simulate',
        caption: 'simulate Exec',
        execType: 'simulateexec',
        metaType: 'simulateexec',
        color: '#EB54C3',
        iconCaption: '',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'simulateExec',
        moduleState: 'JobMonitoring',
        resultState: 'simulateexec',
        moduleCaption: 'Job Monitoring',
        class: 'fa fa-flask'
      },

      'train': {
        name: 'train',
        caption: 'Training',
        color: '#1DE9B6',
        icon: 'model.svg',
        iconCaption: 'Training',
        execType: 'trainExec',
        metaType: 'simulate',
        iconPath: 'assets/layouts/layout/img/train.svg',
        allowInMenu: false,
        detailState: 'train',
        moduleState: 'dataScience',
        moduleCaption: 'Data Science',
        childMenu: [],
        allowInChildMenu: true,
        class: 'fa fa-flask'
      },
      'trainexec': {
        name: 'trainexec',
        caption: 'Train',
        execType: 'trainexec',
        metaType: 'trainexec',
        color: '#8E44AD',
        iconCaption: '',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'trainExec',
        moduleState: 'dataScience',
        resultState: 'trainexec',
        moduleCaption: 'Data Science',
        childMenu: [],
        allowInChildMenu: false,
        class: 'fa fa-flask'
      },
      'trainExec': {
        name: 'train',
        caption: 'Train Exec',
        execType: 'trainExec',
        metaType: 'trainExec',
        color: '#8E44AD',
        iconCaption: '',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'trainExec',
        moduleState: 'JobMonitoring',
        resultState: 'trainExec',
        moduleCaption: 'Job Monitoring',
        class: 'fa fa-flask'
      },
      'featureattrmap': {
        name: 'featureattrmap',
        caption: 'Feature Attr Map',
        execType: '',
        metaType: '',
        color: '#c6ff00',
        iconCaption: '',
        allowInMenu: false,
        listState: '',
        detailState: '',
        resultState: '',
        childMenu: [],
        allowInChildMenu: false,
        class: 'fa fa-flask'
      },
      'appconfig': {
        name: 'appconfig',
        caption: 'App Config',
        execType: '',
        metaType: '',
        color: '#c6ff00',
        iconCaption: '',
        allowInMenu: false,
        listState: '',
        detailState: '',
        resultState: '',
        childMenu: [],
        allowInChildMenu: false,
      },
      'operatortype': {
        name: 'operatortype',
        caption: 'Operator',
        execType: '',
        metaType: 'operatortype',
        color: '#c6ff00',
        iconPath: 'assets/layouts/layout/img/operator.svg',
        iconCaption: 'operatortype',
        allowInMenu: false,
        detailState: 'operatortype',
        moduleState: 'dataScience',
        moduleCaption: 'Data Science',
        childMenu: [],
        allowInChildMenu: true,
      },
      'generatedata': {
        name: 'generatedata',
        caption: 'Generate Data',
        execType: '',
        metaType: 'operatortype',
        color: '#c6ff00',
        iconPath: 'assets/layouts/layout/img/operator.svg',
        iconCaption: '',
        allowInMenu: false,
        listState: 'operatortype',
        detailState: 'createoperatortype',
        resultState: '',
        childMenu: [],
        allowInChildMenu: false,
      },
      'transpose': {
        name: 'transpose',
        caption: 'Transpose',
        execType: '',
        metaType: 'operatortype',
        color: '#c6ff00',
        iconPath: 'assets/layouts/layout/img/operator.svg',
        iconCaption: '',
        allowInMenu: false,
        listState: 'operatortype',
        detailState: 'createoperatortype',
        resultState: '',
        childMenu: [],
        allowInChildMenu: false,
      },
      'ingest': {
        name: 'dataIngest',
        caption: 'Rule',
        color: '#00A8F0',
        icon: 'ingest.svg',
        execType: 'ingestExec',
        metaType: 'ingest',
        iconPath: 'assets/img/ingest.svg',
        allowInMenu: true,
        state: 'createingest',
        detailState: 'ingest',
        moduleState: 'dataIngestion',
        moduleCaption: 'Data Ingestion',
        class: 'fa fa-users'
      },
      'ingestgroup': {
        name: 'dataIngestgroup',
        caption: 'Rule Group',
        color: '#00A8F0',
        icon: 'dataIngestgroup.svg',
        execType: 'dataIngestgroupExec',
        metaType: 'dataIngestgroup',
        iconPath: 'assets/img/ingest.svg',
        allowInMenu: true,
        state: 'createingestgroup',
        detailState: 'ingestgroup',
        moduleCaption: 'Data Ingestion',
        moduleState: 'dataIngestion',
        class: 'fa fa-users'
      },
      'ingestexec': {
        name: 'ingestexec',
        caption: 'Ingest',
        execType: 'ingestexec',
        metaType: 'ingestexec',
        color: '#8E44AD',
        iconCaption: '',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'ingestExec',
        moduleState: 'dataIngestion',
        resultState: 'ingestexec',
        moduleCaption: 'Data Ingestion',
        childMenu: [],
        allowInChildMenu: false,
        class: 'fa fa-random'
      },
      'ingestExec': {
        name: 'ingest',
        caption: 'Ingest Exec',
        execType: 'ingestexec',
        metaType: 'ingestexec',
        color: '#8E44AD',
        iconCaption: '',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'ingestExec',
        moduleState: 'JobMonitoring',
        resultState: 'ingestExec',
        moduleCaption: 'Job Monitoring',
        childMenu: [],
        allowInChildMenu: false,
        class: 'fa fa-random'
      },
      'ingestgroupexec': {
        name: 'ingestgroup',
        caption: 'Ingest Group',
        execType: 'ingestgroupExec',
        metaType: 'ingestgroupExec',
        color: '#EB54C3',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'ingestgroupExec',
        resultState: 'ingestgroupexec',
        moduleState: 'dataIngestion',
        moduleCaption: 'Data Ingestion',
        class: 'fa fa-random'
      },
      'ingestgroupExec': {
        name: 'ingestgroup',
        caption: 'Ingest Group',
        execType: 'ingestgroupExec',
        metaType: 'ingestgroupExec',
        color: '#EB54C3',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'ingestgroupExec',
        resultState: 'ingestgroupExec',
        moduleState: 'JobMonitoring',
        moduleCaption: 'Job Monitoring',
        class: 'fa fa-random'
      },
      'batch': {
        name: 'batch',
        caption: 'Batch',
        color: '#00A8F0',
        icon: 'batch.svg',
        execType: 'batchExec',
        metaType: 'batch',
        iconPath: 'assets/img/batch.svg',
        allowInMenu: true,
        //state: 'createbatch',
        detailState: 'batch',
        moduleState: 'batchScheduler',
        moduleCaption: 'Batch Scheduler',
        class: 'fa fa-tasks'
      },
      'batchexec': {
        name: 'batchexec',
        caption: 'Batch Result',
        execType: 'batchexec',
        metaType: 'batchexec',
        color: '#EB54C3',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'jobexecutorlistbatchexec',
        resultState: 'batchexec',
        moduleState: 'batchScheduler',
        moduleCaption: 'BatchScheduler',
        class: 'fa fa-tasks'
      },
      'mapexec': {
        name: 'mapexec',
        caption: 'Map',
        execType: 'mapexec',
        metaType: 'mapexec',
        color: '#EB54C3',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'mapexec',
        resultState: 'mapexec',
        moduleState: 'dataPreparation',
        moduleCaption: 'Data Preparation',
        class: 'fa fa-link'
      },
      'batchExec': {
        name: 'batch',
        caption: 'Batch Exec',
        execType: 'batchExec',
        metaType: 'batchExec',
        color: '#EB54C3',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'batchExec',
        resultState: 'batchExec',
        moduleState: 'JobMonitoring',
        moduleCaption: 'Job Monitoring',
        class: 'fa fa-tasks'
      },
      'graphpod': {
        name: 'graphpod',
        caption: 'Graph',
        execType: 'graphExec',
        metaType: 'graphpod',
        color: '#00E5FF',
        parentIconCaption: '',
        allowInMenu: false,
        listState: 'listgraphpod',
        detailState: 'graphpod',
        childMenu: [],
        moduleState: 'graphAnalysis',
        moduleCaption: 'Graph Analysis',
        allowInChildMenu: false,
        class: 'fa fa-bar-chart'
      },
      'graphexec': {
        name: 'graphexec',
        caption: 'Graph',
        execType: 'graphexec',
        metaType: 'graphexec',
        color: '#EB54C3',
        parentIconCaption: '',
        allowInMenu: false,
        listState: 'graphpodresultlist',
        joblistState: 'jobmonitoringlist',
        detailState: 'jobexecutorlistgraphexec',
        resultState: 'graphpodresult',
        moduleState: 'graphpod',
        moduleCaption: 'Graph Analysis',
        childMenu: [],
        allowInChildMenu: false,
        class: 'fa fa-bar-chart'
      },
      'report': {
        name: 'report',
        caption: 'Report',
        execType: 'report',
        metaType: 'report',
        color: '#EB54C3',
        parentIconCaption: '',
        allowInMenu: false,
        listState: 'reportresultlist',
        //joblistState:'jobmonitoringlist',
        detailState: 'report',
        //resultState:'reportresult',
        moduleState: 'dataVisualization',
        moduleCaption: 'Data Visualization',
        childMenu: [],
        allowInChildMenu: false,
        class: 'fa fa-desktop'
      },
      'reportexec': {
        name: 'reportexec',
        caption: 'Report',
        execType: 'reportexec',
        metaType: 'reportexec',
        color: '#EB54C3',
        parentIconCaption: '',
        allowInMenu: false,
        listState: 'reportresultlist',
        joblistState: 'jobmonitoringlist',
        detailState: 'jobexecutorlistreportexec',
        resultState: 'reportresult',
        moduleState: 'dataVisualization',
        moduleCaption: 'Data Visualization',
        childMenu: [],
        allowInChildMenu: false,
        class: 'fa fa-line-chart'
      },
      'reportExec': {
        name: 'report',
        caption: 'Report Exec',
        execType: 'reportexec',
        metaType: 'reportexec',
        color: '#8E44AD',
        iconCaption: '',
        allowInMenu: false,
        listState: 'jobmonitoringlist',
        detailState: 'reportExec',
        moduleState: 'JobMonitoring',
        resultState: 'reportExec',
        moduleCaption: 'Job Monitoring',
        childMenu: [],
        allowInChildMenu: false,
        class: 'fa fa-flask'
      },
      'stage': {
        name: 'stage',
        caption: 'Stage',
        color: 'blue',
        icon: 'stage.svg',
        execType: '',
        metaType: '',
        iconPath: 'assets/img/stage.svg',
        allowInMenu: true,
      },
      paramlistrule:{
        name: '',
        caption: 'Paramlistmodel',
        execType: '',
        metaType: '',
        color: '',
        iconCaption: '',
        allowInMenu: false,
        listState: '',
        detailState: '',
        moduleState: '',
        resultState: '',
        moduleCaption: '',
        childMenu: [],
        allowInChildMenu: '',
        class: 'fa fa-flask'
      },
      paramlistmodel:{
        name: '',
        caption: 'Paramlistmodel',
        execType: '',
        metaType: '',
        color: '',
        iconCaption: '',
        allowInMenu: false,
        listState: '',
        detailState: '',
        moduleState: '',
        resultState: '',
        moduleCaption: '',
        childMenu: [],
        allowInChildMenu: '',
        class: 'fa fa-flask'
      },
      paramlistdag:{
        name: '',
        caption: 'Paramlistdag',
        execType: '',
        metaType: '',
        color: '',
        iconCaption: '',
        allowInMenu: false,
        listState: '',
        detailState: '',
        moduleState: '',
        resultState: '',
        moduleCaption: '',
        childMenu: [],
        allowInChildMenu: '',
        class: 'fa fa-flask'
      },
      lov:{
        name: '',
        caption: 'Lov',
        execType: '',
        metaType: '',
        color: '',
        iconCaption: '',
        allowInMenu: false,
        listState: '',
        detailState: '',
        moduleState: '',
        resultState: '',
        moduleCaption: '',
        childMenu: [],
        allowInChildMenu: '',
        class: 'fa fa-flask'
      },
      organization:{
        name: '',
        caption: 'Organization',
        execType: '',
        metaType: '',
        color: '',
        iconCaption: '',
        allowInMenu: false,
        listState: '',
        detailState: '',
        moduleState: '',
        resultState: '',
        moduleCaption: '',
        childMenu: [],
        allowInChildMenu: '',
        class: 'fa fa-wrench'
      },
      schedule:{
        name: '',
        caption: 'Schedule',
        execType: '',
        metaType: '',
        color: '',
        iconCaption: '',
        allowInMenu: false,
        listState: '',
        detailState: '',
        moduleState: '',
        resultState: '',
        moduleCaption: '',
        childMenu: [],
        allowInChildMenu: '',
        class: 'fa fa-flask'
      },
    }

    this.obj.statusDefs = {
      'NotStarted': {
        name: 'NotStarted',
        caption: 'Not Started',
        color: '#659be0',//'#006df0',
        iconPath: 'assets/layouts/layout/img/new_status/NotStarted.svg',
      },
      'Not Started': {
        name: 'NotStarted',
        caption: 'Not Started',
        color: '#659be0',//'#006df0',
        iconPath: 'assets/layouts/layout/img/new_status/NotStarted.svg',
      },
      'Completed': {
        name: 'Completed',
        caption: 'Completed',
        color: '#36c6d3',//'#91dc5a',
        iconPath: 'assets/layouts/layout/img/new_status/Completed.svg',
      },
      'Killed': {
        name: 'Killed',
        caption: 'Killed',
        color: '#ed6b75',//'#d80027',
        iconPath: 'assets/layouts/layout/img/new_status/Killed.svg',
      },
      'Failed': {
        name: 'Failed',
        caption: 'Failed',
        color: '#ed6b75',//'#d80029',
        iconPath: 'assets/layouts/layout/img/new_status/Failed.svg',
      },
      'Resume': {
        name: 'Resume',
        caption: 'Resume',
        color: '#006df0',
        iconPath: 'assets/layouts/layout/img/new_status/Resume.svg',
      },
      'Terminating': {
        name: 'Terminating',
        caption: 'Terminating',
        color: '#d80027',
        iconPath: 'assets/layouts/layout/img/new_status/Terminating.svg',
      },
      'OnHold': {
        name: 'OnHold',
        caption: 'OnHold',
        color: '#ffda44',
        iconPath: 'assets/layouts/layout/img/new_status/OnHold.svg',
      },
      'In Progress': {
        name: 'InProgress',
        caption: 'In Progress',
        color: '#F1C40F',//'#f57f36',
        iconPath: 'assets/layouts/layout/img/new_status/InProgress.svg',
      },
      'InProgress': {

        name: 'InProgress',
        caption: 'In Progress',
        color: '#F1C40F',//'#f57f36',
        iconPath: 'assets/layouts/layout/img/new_status/InProgress.svg',
      },
      'active': {
        name: 'active',
        caption: 'Active',
        color: '#36c6d3',//'#91dc5a',
        iconPath: ""
      },
      'expired': {
        name: 'expire',
        caption: 'Expire',
        color: '#ed6b75',//'#d80029',
        iconPath: '',
      },
      'NOCHANGE':{
        name : 'NoChange',
        caption:'No Change',
        color :'#36c6d3',//'#9CBB62',
      },
      'MODIFIED':{
        name : 'Modified',
        caption:'Modified',
        color :'#F1C40F',//'#5083C3',
      },
      'DELETED':{
        name : 'Deleted',
        caption:'Deleted',
        color :'#ed6b75',//'#C34E4E',
      },
      'NEW':{
        name : 'New',
        caption:'New',
        color :'#006df0',//'#4FACC5',
      },
    }
    this.defaultLink = {
      attrs: { '.connection': { stroke: 'gray' }, '.marker-target': { d: 'M 10 0 L 0 5 L 10 10 z', fill: 'gray', stroke: 'gray' } }
    };

    this.validElementTypes = ['dag', 'stage', 'dq', 'dqgroup', 'map', 'load', 'profile', 'profilegroup', 'model', 'rule', 'rulegroup', 'recon', 'recongroup', 'ingest', 'ingestgroup','mapexec'];
    this.validTaskTypes = ['dq', 'dqgroup', 'map', 'load', 'profile', 'profilegroup', 'model', 'rule', 'rulegroup', 'recon', 'recongroup', 'ingest', 'ingestgroup','mapexec'];
    this.defaultElement = {
      markup: '<g class="rotatable"><g class="scalable"><image class="body" /></g><image class="remove" title="Remove"/><g class="status"><image class="statusImg"><title class="statusTitle">Status</title></image></g><text class="label" /><g class="inPorts"/><g class="outPorts"/></g>',
      size: { width: 50, height: 50 },
      inPorts: ['in'],
      outPorts: ['out'],
      ports: {
        groups: {
          'in': {
            attrs: {
              '.port-body': {
                fill: '#fff',
                r: 5,
                cx: -5
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
              magnet: false
            }
          }
        }
      },
      attrs: {
        '.body': {
          x: "0", y: "0", height: "10", width: "10",
        },
        '.statusImg': {
          x: "55", y: "-20", height: "25px", width: "25px",
          "xlink:href": ""
        },
        '.remove': {
          x: "55", y: "-20", height: "25px", width: "25px",
          "xlink:href": "assets/img/delete.png"
        },
        magnet: true,
        text: { text: 'Default Element', y: '60px', 'font-size': 10, style: { 'text-shadow': '1px 1px 1px lightgray' } }
      }


    };

    this.customElements = {
      'dag': Object.assign({}, this.defaultElement, {
        elementType: 'dag',
        inPorts: null,
        attrs: {
          '.body': {
            elementType: 'dag',
            "xlink:href": this.obj.metadataDefs['dag'].iconPath
          },
          '.statusImg': {
            x: "55", y: "-20", height: "25px", width: "25px",
            "xlink:href": ""
          },

          // '.remove': null
        }
      }),
      'stage': Object.assign({}, this.defaultElement, {
        elementType: 'stage',
        attrs: {
          '.body': {
            elementType: 'stage',
            "xlink:href": this.obj.metadataDefs['stage'].iconPath
          },
          '.statusImg': {
            x: "55", y: "-20", height: "25px", width: "25px",
            "xlink:href": ""
          },
          '.remove': {
            x: "55", y: "-20", height: "25px", width: "25px",
            "xlink:href": "assets/img/delete.png"
          }
        }
      }),

      'dq': Object.assign({}, this.defaultElement, {
        elementType: 'dq',
        attrs: {
          '.body': {
            elementType: 'dq',
            "xlink:href": this.obj.metadataDefs['dq'].iconPath
          },
          '.statusImg': {
            x: "55", y: "-20", height: "25px", width: "25px",
            "xlink:href": ""
          },
          '.remove': {
            x: "55", y: "-20", height: "25px", width: "25px",
            "xlink:href": "assets/img/delete.png"
          }
        }
      }),

      'dqgroup': Object.assign({}, this.defaultElement, {
        elementType: 'dqgroup',
        attrs: {
          '.body': {
            elementType: 'dqgroup',
            "xlink:href": this.obj.metadataDefs['dqgroup'].iconPath
          },
          '.statusImg': {
            x: "55", y: "-20", height: "25px", width: "25px",
            "xlink:href": ""
          },
          '.remove': {
            x: "55", y: "-20", height: "25px", width: "25px",
            "xlink:href": "assets/img/delete.png"
          }
        }
      }),

      'map': Object.assign({}, this.defaultElement, {
        elementType: 'map',
        attrs: {
          '.body': {
            elementType: 'map',
            "xlink:href": this.obj.metadataDefs['map'].iconPath
          },
          '.statusImg': {
            x: "55", y: "-20", height: "25px", width: "25px",
            "xlink:href": ""
          },
          '.remove': {
            x: "55", y: "-20", height: "25px", width: "25px",
            "xlink:href": "assets/img/delete.png"
          }
        }
      }),

      'load': Object.assign({}, this.defaultElement, {
        elementType: 'load',
        attrs: {
          '.body': {
            elementType: 'load',
            "xlink:href": this.obj.metadataDefs['load'].iconPath
          }
          , '.statusImg': {
            x: "55", y: "-20", height: "25px", width: "25px",
            "xlink:href": ""
          },
        }
      }),

      'profile': Object.assign({}, this.defaultElement, {
        elementType: 'profile',
        attrs: {
          '.body': {
            elementType: 'profile',
            "xlink:href": this.obj.metadataDefs['profile'].iconPath
          },
          '.statusImg': {
            x: "55", y: "-20", height: "25px", width: "25px",
            "xlink:href": ""
          },
        }
      }),

  

      'profilegroup': Object.assign({}, this.defaultElement, {
        elementType: 'profilegroup',
        attrs: {
          '.body': {
            elementType: 'profilegroup',
            "xlink:href": this.obj.metadataDefs['profilegroup'].iconPath
          },
          '.statusImg': {
            x: "55", y: "-20", height: "25px", width: "25px",
            "xlink:href": ""
          },
        }
      }),
      'ingestgroup': Object.assign({}, this.defaultElement, {
        elementType: 'ingestgroup',
        attrs: {
          '.body': {
            elementType: 'ingestgroup',
            "xlink:href": this.obj.metadataDefs['ingestgroup'].iconPath
          },
          '.statusImg': {
            x: "55", y: "-20", height: "25px", width: "25px",
            "xlink:href": ""
          },
        }
      }),
      'ingest': Object.assign({}, this.defaultElement, {
        elementType: 'ingest',
        attrs: {
          '.body': {
            elementType: 'ingest',
            "xlink:href": this.obj.metadataDefs['ingest'].iconPath
          },
          '.statusImg': {
            x: "55", y: "-20", height: "25px", width: "25px",
            "xlink:href": ""
          },
        }
      }),
      'recon': Object.assign({}, this.defaultElement, {
        elementType: 'recon',
        attrs: {
          '.body': {
            elementType: 'recon',
            "xlink:href": this.obj.metadataDefs['recon'].iconPath
          },
          '.statusImg': {
            x: "55", y: "-20", height: "25px", width: "25px",
            "xlink:href": ""
          },
        }
      }),

      'recongroup': Object.assign({}, this.defaultElement, {
        elementType: 'recongroup',
        attrs: {
          '.body': {
            elementType: 'recongroup',
            "xlink:href": this.obj.metadataDefs['recongroup'].iconPath
          },
          '.statusImg': {
            x: "55", y: "-20", height: "25px", width: "25px",
            "xlink:href": ""
          },
        }
      }),

      'model': Object.assign({}, this.defaultElement, {
        elementType: 'model',
        attrs: {
          '.body': {
            elementType: 'model',
            "xlink:href": this.obj.metadataDefs['model'].iconPath
          },
          '.statusImg': {
            x: "55", y: "-20", height: "25px", width: "25px",
            "xlink:href": ""
          },

        }
      }),

      'rulegroup': Object.assign({}, this.defaultElement, {
        elementType: 'rulegroup',
        attrs: {
          '.body': {
            elementType: 'rulegroup',
            "xlink:href": this.obj.metadataDefs['rulegroup'].iconPath
          },
          '.statusImg': {
            x: "55", y: "-20", height: "25px", width: "25px",
            "xlink:href": ""
          },
        }
      }),

      'rule': Object.assign({}, this.defaultElement, {
        elementType: 'rule',
        attrs: {
          '.body': {
            elementType: 'rule',
            "xlink:href": this.obj.metadataDefs['rule'].iconPath
          },
          '.statusImg': {
            x: "55", y: "-20", height: "25px", width: "25px",
            "xlink:href": ""
          },

        }
      }),
    };

  }
  getObj(): Object {
    return this.obj;
  }
  getMetadataDefs(type):any {
    return this.obj.metadataDefs[type];
  }

  getAllMetadataDefs(): Object {
    return this.obj.metadataDefs;
  }
  getStatusDefs(status): any {
    return this.obj.statusDefs[status];
  }

  getCustomElement = function (type) {
    if (type.slice(-4) == 'Exec' || type.slice(-4) == 'exec') {
      type = type.slice(0, -4);
    }
    // if(this.validElementTypes.indexOf(type) == -1){
    //   return console.error('invalid element type');
    // }
    //console.log(JSON.stringify(this.customElements[type]))
    return this.customElements[type];
  };

  getDefaultElement = function () {
    return this.defaultElement;
  };

  getDefaultLink = function () {
    return this.defaultLink;
  };

  assignDeep = function (target, varArgs) { // .length of function is 2
    'use strict';
    if (target == null) { // TypeError if undefined or null
      throw new TypeError('Cannot convert undefined or null to object');
    }
    var to = Object(target);
    for (var index = 1; index < arguments.length; index++) {
      var nextSource = arguments[index];
      if (nextSource != null) { // Skip over if undefined or null
        for (var nextKey in nextSource) {
          // Avoid bugs when hasOwnProperty is shadowed
          if (Object.prototype.hasOwnProperty.call(nextSource, nextKey)) {
            if (typeof to[nextKey] === 'object'
              && to[nextKey] && typeof nextSource[nextKey] === 'object'
              && nextSource[nextKey]) {
              this.assignDeep(to[nextKey], nextSource[nextKey]);
            }
            else {
              to[nextKey] = nextSource[nextKey];
            }
          }
        }
      }
    }
    return to;
  };
}
