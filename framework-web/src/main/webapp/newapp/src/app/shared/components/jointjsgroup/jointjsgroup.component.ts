
import { Component, Input, Output, OnInit, ViewChild, EventEmitter } from '@angular/core';
import { Location } from '@angular/common';
import { Router, Event as RouterEvent, ActivatedRoute, Params } from '@angular/router';
import * as $ from 'jquery';
import * as _ from 'lodash';
import * as Backbone from 'backbone';
declare var require: any;
const joint = require('../../../../../node_modules/jointjs/dist/joint.js');
import { D3Service, D3, Selection } from 'd3-ng2-service';
import { AppMetadata } from '../../../app.metadata';
import { TableRenderComponent } from '../resulttable/resulttable.component'
import { JointjsService } from '../../components/jointjs/jointjsservice'
import { jointjsGroupService } from './joinjsgroup.service'

@Component({
    selector: 'app-jointjs-group',
    templateUrl: './Jointjsgroup.template.html',
    styleUrls: []
})

export class JointjsGroupComponent {
    restartStatus: boolean;
    tableparms: {};
    columnOptions: any[];
    cols: any[];
    colsdata: any;
    groupExecUuid: any;
    intervalId: any;
    paper: any;
    graph: any;
    private d3: D3; // Define the private member which will hold the this.d3 reference
    @Input()
    graphParms: any
    IsGraphShow: any;
    @Output() downloadShow = new EventEmitter<any>();

    @Output() jointJSRunStatus = new EventEmitter<any>();
    @ViewChild(TableRenderComponent) d_tableRenderComponent: TableRenderComponent;

    constructor(private _location: Location, d3Service: D3Service, private activatedRoute: ActivatedRoute, private router: Router, public appMetadata: AppMetadata, private _jointjsService: JointjsService, private _jointjsGroupService: jointjsGroupService) {
        this.d3 = d3Service.getD3();
        this.IsGraphShow = true;
    }

    ngOnInit() {
        this.IsGraphShow = true
        $('#paper1 svg').addClass('exec-mode');
        setTimeout(() => {
            // this.d_JointjsComponent.params={};
            this.createGraph();
            this.generateGroupGraph(this.graphParms);
            this._jointElementMouseOver();
            console.log(this.graphParms);
        }, 1000);

    }
    private _jointElementMouseOver() {
        let d_graph = this.graph;
        let d_appMetadata = this.appMetadata
        let elementType;
        let elementModel;
        let txt1;
        let txt2;
        let txt3;
        $("#paper1").on("mouseover", ".joint-element .body", function (e) {
            $('.joint-element').mouseout(function (e) {
                var divid = 'divtoshow';
                $("#" + divid).hide();
                $('.connection').removeClass('active');
            });

            var jointElement = $(this).closest(".joint-element");
            var s = jointElement.attr("model-id");
            $('.connection[source-id=' + s + ']').addClass('active');

            var cell = d_graph.getCell(s);
            elementModel = cell.attributes['model-data'];
            try {
                elementType = cell.attributes.elementType;
                if (elementType.slice(-4) == 'Exec') {
                    elementType = elementType.slice(0, -4);
                }
            }
            catch (e) {
                if (s.substr(0, 3) == 'dag') {
                    let cell = $(this);
                    elementModel = cell.attr("model-data") ? JSON.parse(cell.attr("model-data")) : undefined;
                    elementType = 'dag';
                }
                else if (cell.attributes.elementType == 'stage') {
                    elementType = 'stage';
                    elementModel = { name: cell.attributes['model-data'].name, type: elementType, uuid: cell.attributes['model-data'].stageId };
                } else {
                    elementType = undefined;
                }
            }
            var divid = 'divtoshow';
            var xPercent = e.clientX / $(window).width() * 100;
            if (xPercent > 50) {
                var left = (e.clientX - 400) + "px";
                var top = e.clientY + "px";
            }
            else {
                var left = (e.clientX + 40) + "px";
                var top = e.clientY + "px";
            }
            var div = document.getElementById(divid);
            div.style.left = left;
            div.style.top = top;
            var dagtypetext = '';
            try {
                dagtypetext = d_appMetadata.getMetadataDefs(elementType)["caption"];
                let color = d_appMetadata.getMetadataDefs(elementType)["color"];
                $("#dagcolorID").css("background-color", color);
            }
            catch (e) {
                dagtypetext = elementType;
                $("#dagcolorID").css("background-color", "blue");
            }
            if (!elementModel || Object.keys(elementModel).length == 0) {
                txt1 = "", txt2 = "", txt3 = "";
            }
            else {
                var txt1 = elementModel.uuid || '';
                var txt2 = elementModel.name || '';
                var txt3 = elementModel.version || '';
            }

            $("#elementTypeText").html(dagtypetext);
            $("#task_Id").html(txt1);
            $("#task_Name").html(txt2);
            if (txt3 == '') {
                $("#task_Version_label").html('');
                $("#task_Version").html('');
            }
            else {
                $("#task_Version_label").html('Version');
                $("#task_Version").html(txt3);
            }
            $("#" + divid).show();
        })
    }
    _setGrid(paper, gridSize, color) {
        paper.options.gridSize = gridSize; // Set grid size on the JointJS paper object (joint.dia.Paper instance
        var canvas = $('<canvas/>', { width: gridSize, height: gridSize }); // Draw a grid into the HTML 5 canvas and convert it to a data URI image
        canvas[0]["width"] = gridSize;
        canvas[0]["height"] = gridSize;
        var context = canvas[0]["getContext"]('2d');
        context["beginPath"]();
        context["rect"](1, 1, 1, 1);
        context["fillStyle"] = color || '#AAAAAA';
        context["fill"]();
        var gridBackgroundImage = canvas[0]["toDataURL"]('image/png'); // // Finally, set the grid background image of the paper container element.
        paper.$el.css('background-image', 'url("' + gridBackgroundImage + '")');
    }

    createGraph() {
        let d_link = this.appMetadata.getDefaultLink();
        let d_functionCall = this._jointjsService;
        let dx = $(document).width();
        let dy = $(document).height();
        let d_graph = this.graph
        var paperEl = $('#paper1');
        this.graph = new joint.dia.Graph;
        this.paper = new joint.dia.Paper({
            el: paperEl, gridSize: 1, width: dx > 2000 ? dx : 2000, height: dy * 2 > 1000 ? dy * 2 : 1000, model: this.graph,
            linkPinning: false,
            linkView: joint.dia.LinkView,
            multiLinks: false,
            snapLinks: true,
            markAvailable: true,
            interactive: false,
            defaultLink: function (cellView, magnet) {
                console.log(d_graph.getCells())
                return new joint.dia.Link(Object.assign({}, d_link, {
                    attrs: { '.connection': { 'source-id': cellView.model.id } }
                }));

            },
            validateConnection: function (cellViewS, magnetS, cellViewT, magnetT, end, linkView) {
                if (magnetT.attributes['port'].value == 'out') {
                    return false
                }

                return false //d_functionCall.validate(d_graph,cellViewS, magnetS, cellViewT, magnetT, end, linkView);
            },
            validateMagnet: function (cellView, magnet) {
                if (magnet.attributes['port'].value == 'in') {
                    return false
                }
                return false
            }
        });

        this.paper.scale(0.7);
        this._setGrid(this.paper, 10, '#AAAAAA');
    }

    generateGroupGraph(params) {
        console.log(params)
        let actualType = params["type"].slice(0, -9);
        if (actualType == "dq") {
            actualType = "dataQual";
        }
        let apiuri = params.url + actualType + "GroupExecUuid=" + params.id + "&" + actualType + "GroupExecVersion=" + params.version + "&action=view"
        this._jointjsGroupService.getExecByGroupExec(apiuri)
            .subscribe(
                response => {
                    this.drawChildGraph(response)
                    setTimeout(() => {
                        this.startStatusUpdate(params);
                    }, 1000);
                    this.intervalId = setInterval(() => {
                        this.startStatusUpdate(params);
                    }, 5000);

                },
                error => console.log("Error :: " + error));
    }

    drawChildGraph(data) {
        let posArray = null
        posArray = this.circlePos(data);
        let cells = [];
        let links = [];
        let d_appMetadata = this.appMetadata
        let d_graphParms = this.graphParms
        cells.push(new joint.shapes.devs.Model(
            this.appMetadata.assignDeep(this.appMetadata.getCustomElement(this.graphParms.elementType), {
                id: this.graphParms.id,
                elementType: this.graphParms.elementType,
                "model-data": { dependsOn: { ref: this.graphParms.ref }, uuid: this.graphParms.id, name: this.graphParms.name, version: this.graphParms.version },
                position: { x: (data.length > 7 ? 31 * data.length : 250) + 75, y: (data.length > 7 ? 30 * data.length : 250) + 75 }, //circle
                attrs: {
                    '.body': {
                        'element-id': this.graphParms.id,
                        "model-data": JSON.stringify({ dependsOn: { ref: this.graphParms.ref } }),
                    },
                    '.status': {
                        'element-id': this.graphParms.id,
                    },
                    '.remove': {
                        x: "55", y: "-20", height: "25px", width: "25px",
                        "xlink:href": ""
                    },
                    text: { text: this.graphParms.name, y: '60px', 'font-size': 10, style: { 'text-shadow': '1px 1px 1px lightgray' } || this.graphParms.id }
                }
            })
        ));
        console.log(cells);
        if (data.length > 0) {
            var taskXPos = 500;
            var taskYPos = 100;
            data.forEach(function (task, taskKey) {
                var ref = task.dependsOn.ref;
                var type = ref.type;
                taskXPos = posArray[taskKey].x;
                taskYPos = posArray[taskKey].y;
                task.taskId = task.uuid.length > 3 ? task.uuid : d_graphParms.id + '_' + "task_" + task.uuid
                cells.push(new joint.shapes.devs.Model(
                    d_appMetadata.assignDeep(d_appMetadata.getCustomElement(type), {
                        id: task.taskId,
                        elementType: type + "exec",
                        "model-data": task,
                        position: { x: task.xPos || taskXPos, y: task.yPos || taskYPos },
                        attrs: {
                            '.body': {
                                'element-id': task.taskId,
                                "model-data": JSON.stringify(task),
                            },
                            '.status': {
                                'element-id': task.taskId,

                            },
                            '.status image': {
                                "xlink:href": "assets/img/" + task.statusList[task.statusList.length - 1].stage + ".svg"
                            },
                            '.remove': {
                                x: "55", y: "-20", height: "25px", width: "25px",
                                "xlink:href": ""
                            },
                            text: { text: task.name, y: '60px', 'font-size': 10, style: { 'text-shadow': '1px 1px 1px lightgray' } }
                        }
                    })
                ));
                var s = d_graphParms.id;
                links.push(
                    new joint.dia.Link({
                        source: { id: s }, target: { id: task.taskId },
                        attrs: { '.connection': { 'source-id': s, stroke: 'gray' }, '.marker-target': { d: 'M 10 0 L 0 5 L 10 10 z', fill: 'gray', stroke: 'gray' } }
                    })
                )
            });
        }
        this.graph.addCells(cells);
        this.graph.addCells(links);
        $('#paper1 svg').addClass('exec-mode');
        this._jointElementMouseOver();
        let d_this = this;
        let d_d3 = this.d3
        this.paper.$el.on('contextmenu', ".joint-element .body", function () {

            var vm = this;
            var id = vm.getAttribute("element-id");
            var cell = d_this.graph.getCell(id);
            var type = cell.attributes.elementType;
            let menuItem = d_this.getMenuItem(id, cell);
            if (menuItem.length > 0)
                d_this.contextMenu1(d_d3, menuItem, d_this)
        })

    }
    circlePos(data) {

        var length = data.length;
        var angle,
            radius = length > 7 ? 30 * length : 250,
            width = (radius * 2) + 150,
            height = (radius * 2) + 150,
            posArray = [];
        for (let i = 0; i < length; i++) {
            angle = (i / (length / 2)) * Math.PI; // Calculate the angle at which the element will be placed.                                  // For a semicircle, we would use (i / numNodes) * Math.PI.
            let x = (radius * Math.cos(angle)) + (width / 2); // Calculate the x position of the element.
            let y = (radius * Math.sin(angle)) + (width / 2); // Calculate the y position of the element.
            posArray[i] = { 'x': x, 'y': y };
        }

        return posArray;
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
    startStatusUpdate(params) {
        let type = this.appMetadata.getMetadataDefs(params.elementType)["execType"];
        this.groupExecUuid = params.id;
        this._jointjsGroupService.getGroupExecStatus(params.id, params.version, type)
            .subscribe(
                response => {
                    this.onSuccessGetStatusByDagExec(response)
                },
                error => console.log("Error :: " + error)
            );

    }

    onSuccessGetStatusByDagExec = function (response) {
        this.updateGroupGraphStatus(response);

    }

    stopStatusUpdate() {
        if (this.intervalId)
            clearInterval(this.intervalId);
    }

    updateGroupGraphStatus(data) {
        let d_this = this
        data.forEach(function (task) {

            let statusTask = task.statusList.length == 0 ? 'InProgress' : d_this.latestStatus(task.statusList)["stage"];
            let taskid = task.metaRef.ref.uuid;

            //   if(taskid == d_this.groupExecUuid){

            //     if(['Failed','Killed'].indexOf(statusTask) !=-1){
            //         this.restartStatus = true;
            //     }
            //     else{
            //         this.restartStatus = false;
            //     }
            //   }

            if (taskid == d_this.groupExecUuid && ['Completed', 'Failed', 'Killed'].indexOf(statusTask) > -1) {
                d_this.stopStatusUpdate();
            }
            $(".status[element-id=" + taskid + "] .statusImg").attr("xlink:href", "assets/img/" + statusTask + ".svg");
            $(".status[element-id=" + taskid + "] .statusTitle").text(statusTask);
            let exec = task.statusList
            task.statusList.forEach(function (status) {
                $(".status[element-id=" + taskid + "]").attr(status.stage, status.createdOn);
            });
        });

        //  this.restartStatus = data[data.length - 1].statusList[2].stage;

        for (const i in data) {
            this.restartStatus = false;
            if (d_this.groupExecUuid == data[i].metaRef.ref.uuid) {
                for (const j in data[i].statusList) {
                    if (data[i].statusList[j].stage == "Failed" || data[i].statusList[j].stage == "Killed") {
                        this.restartStatus = true;
                        break;
                    }
                    else {
                        this.restartStatus = false;
                    }
                }
                console.log("1");
            }
            console.log(this.restartStatus);
            this.jointJSRunStatus.emit(this.restartStatus);
        }
    }

    getMenuItem(id, cell): any {
        console.log(cell)
        let menuItem = [];
        let status = $(".status[element-id=" + id + "] .statusTitle")[0].innerHTML;
        let type = cell.attributes.elementType;
        let isGroupExec;
        let isExec;

        if (type.slice(-4) == 'Exec' || type.slice(-4) == 'exec') {
            if (type.slice(-9) == 'groupExec' || type.slice(-9) == 'groupexec') {
                isGroupExec = true;
            }
            else {
                isExec = true;
            }
            type = type.slice(0, -4);
        }
        if (isExec || isGroupExec) {
            let itemgroble = {}
            itemgroble["action"] = this.actionEvent
            itemgroble["title"] = "Show Details";
            itemgroble["type"] = "element";
            itemgroble["exectype"] = type;
            itemgroble["cell"] = cell;
            menuItem.push(itemgroble);
            let item = {}
            item["action"] = this.actionEvent;
            if (status && (status == 'Completed') && !isGroupExec) {
                item["title"] = "Show Results"
                item["type"] = "results"
                itemgroble["exectype"] = type;
                item["cell"] = cell;
                menuItem.push(item);
            }
            else if (status && (status == 'NotStarted' || status == 'Resume')) {
                item["title"] = "On Hold"
                item["type"] = "onhold"
                item["status"] = "OnHold"
                item["cell"] = cell;
                menuItem.push(item);
            }
            else if (status && status == 'OnHold') {
                item["title"] = "Resume"
                item["status"] = "Resume"
                item["type"] = "resume"
                item["dag_uuid"] = id
                item["cell"] = cell;
                menuItem.push(item);

            }
            if (status && status == 'InProgress') {
                item["title"] = "Kill"
                item["status"] = "Killed"
                item["type"] = "killexecution"
                item["cell"] = cell;
                menuItem.push(item);

            }
            let itemInfo = {}
            itemInfo["action"] = this.actionEvent
            itemInfo["title"] = "Show Logs"
            itemInfo["type"] = "element"
            itemInfo["cell"] = cell;
            itemInfo["exectype"] = type;
            menuItem.push(itemInfo);

        }


        return menuItem;
    }
    contextMenu1(d_d3, menu, d_this) {

        d_d3.select("#paper1").selectAll('.context-menu').data([1])
            .enter()
            .append('div')
            .attr('class', 'context-menu')

        // close menu
        d_d3.select('#paper1').on('click.context-menu', function () {
            d_d3.selectAll('.context-menu').style('display', 'none');
        });

        var elm = this;
        d_d3.selectAll('.context-menu')
            .html('')
            .append('ul')
            .selectAll('li')
            .data(menu).enter()
            .append('li')
            .html(function (d) {
                return d.title;
            })
            .on('click', function (d, index) {
                d.action(elm, d, d_this);
                d_d3.selectAll('.context-menu').style('display', 'none');
            })

        // show the context menu
        d_d3.selectAll('.context-menu')
            .style('left', (event["layerX"] - 2) + 'px')
            .style('top', (event["layerY"] - 12) + 'px')
            .style('display', 'block');
        //     event.preventDefault();

    };
    actionEvent(elm, data, d_this) {
        console.log(elm);
        if (data.type == "killexecution" || data.type == "resume" || data.type == "onhold") {
            d_this.setStatus(data);
        }
        else if (data.type == "element") {
            d_this.navigateTo(data);
        }
        else if (data.type == "results") {
            d_this.showResult(data);
        }
    }
    setStatus(data) {
        console.log(data)
        let uuid = data.cell.attributes.id;
        let version = "";
        let status = data.status;
        let api;
        var execType = this.appMetadata.getMetadataDefs(data.cell.attributes.elementType.toLowerCase())["execType"];
        let modeldata = data.cell.attributes["model-data"]
        switch (data.cell.attributes.elementType) {
            case 'dqexec':
                api = 'dataqual';
                break;
            case 'dqgroupexec':
                api = 'dataqual';
                break;
            case 'profileexec':
                api = 'profile';
                break;
            case 'profilegroupexec':
                api = 'profile';
                break;
            case 'ruleexec':
                api = 'rule';
                break;
            case 'rulegroupexec':
                api = 'rule';
                break;
            case 'recongroupexec':
                api = 'recon';
                break;
            case 'reconexec':
                api = 'recon';
                break;
            case 'ingestgroupexec':
                api = 'ingest';
                break;
            case 'ingestexec':
                api = 'ingest';
                break;
        }
        this._jointjsGroupService.setStatus(api, uuid, version, execType, status)
            .subscribe(
                response => {
                    console.log(response)
                },
                error => console.log("Error :: " + error)
            );
    }

    navigateTo(data) {
        console.log(data);
        let modeldata = data.cell.attributes["model-data"];
        let type = data.cell.attributes.elementType
        let uuid = modeldata.uuid;
        let version = modeldata.version;
        let d_this = this;
        if (data.title == "Show Logs") {
            let _moduleUrl
            let _routerUrl
            _moduleUrl = 'JobMonitoring'
            _routerUrl = ((data["exectype"]) + 'Exec')
            d_this.router.navigate(["/app/" + _moduleUrl + "/" + _routerUrl, uuid, version, 'true']);
        }
        else {
            this._jointjsGroupService.getMetaIdByExecId(uuid, version, type)
                .subscribe(
                    response => {
                        let _moduleUrl
                        let _routerUrl
                        _moduleUrl = this.appMetadata.getMetadataDefs(response.type)['moduleState']
                        _routerUrl = this.appMetadata.getMetadataDefs(response.type)['detailState']
                        d_this.router.navigate(["./" + _moduleUrl + "/" + _routerUrl, response.uuid, response.version || "", 'true'], { relativeTo: this.activatedRoute });
                    },
                    error => console.log("Error :: " + error)
                );
        }
    }

    showResult(data) {
        this.IsGraphShow = false;
        console.log(data)
        // this.colsdata=null;
        //this.cols=null;
        let modeldata = data.cell.attributes["model-data"]
        let type = this.appMetadata.getMetadataDefs(data.cell.attributes.elementType.toLowerCase())['name']
        let uuid = modeldata.uuid;
        let version = modeldata.version;
        let url;
        this.tableparms = {};
        this.tableparms["uuid"] = uuid
        this.tableparms["version"] = version
        this.tableparms["type"] = type;
        setTimeout(() => {
            this.downloadShow.emit(this.tableparms);
            this.d_tableRenderComponent.renderTable(this.tableparms);
        }, 1000);
    }
}



