import { Injectable, Inject, Input } from "@angular/core";
import { Http, Response } from "@angular/http";
import { SharedService } from "../../shared/shared.service";
import { Observable } from "rxjs/Observable";
import { CommonService } from "./common.service";

@Injectable()

export class DataIngestionService {
	sessionId: string;
	baseUrl: string;
	headers: Headers;
	constructor( @Inject(Http) private http: Http, private _sharedService: SharedService, private _commonService: CommonService) { }

	getDatasourceForFile(type: String): Observable<any[]> {
		let url = 'metadata/getDatasourceForFile?action=view&type=' + type;
		return this._sharedService.getCall(url)
			.map((response: Response) => {
				return <any[]>response.json();
			})
			.catch(this.handleError);
	}

	getDatasourceForTable(type: String): Observable<any[]> {
		let url = 'metadata/getDatasourceForTable?action=view&type=' + type;
		return this._sharedService.getCall(url)
			.map((response: Response) => {
				return <any[]>response.json();
			})
			.catch(this.handleError);
	}

	getDatasourceForStream(type: String): Observable<any[]> {
		let url = 'metadata/getDatasourceForStream?action=view&type=' + type;
		return this._sharedService.getCall(url)
			.map((response: Response) => {
				return <any[]>response.json();
			})
			.catch(this.handleError);
	}

	getDatapodByDatasource(type: String, uuid: any): Observable<any[]> {
		let url = 'metadata/getDatapodByDatasource?action=view&type=' + type + '&uuid=' + uuid;
		return this._sharedService.getCall(url)
			.map((response: Response) => {
				return <any[]>response.json();
			})
			.catch(this.handleError);
	}

	getAttributesByDatapod(type: String, uuid: any): Observable<any[]> {
		let url = 'metadata/getAttributesByDatapod?action=view&type=' + type + '&uuid=' + uuid;
		return this._sharedService.getCall(url)
			.map((response: Response) => {
				return <any[]>response.json();
			})
			.catch(this.handleError);
	}

	getAttributesByDataset(type: String, uuid: any): Observable<any[]> {
		let url = 'metadata/getAttributesByDataset?action=view&type=' + type + '&uuid=' + uuid;
		return this._sharedService.getCall(url)
			.map((response: Response) => {
				return <any[]>response.json();
			})
			.catch(this.handleError);
	}

	getTopicList(uuid: any, version: any): Observable<any[]> {
		let url = 'ingest/getTopicList?action=view&type=datasource&uuid=' + uuid+'&version=';
		return this._sharedService.getCall(url)
			.map((response: Response) => {
				return <any[]>response.json();
			})
			.catch(this.handleError);
	}

	getFunctionByCriteria(category: any, inputReq: any, type: any): Observable<any[]> {
		let url = '/metadata/getFunctionByCriteria?action=view&category=' + category + '&inputReq=' + inputReq + '&type=' + type;
		return this._sharedService.getCall(url)
			.map((response: Response) => {
				return <any[]>response.json();
			})
			.catch(this.handleError);
	}

	getParamByApp(uuid: any, type: any): Observable<any[]> {
		let url = '/metadata/getParamByApp?action=view&uuid=' + uuid + '&type=' + type;
		return this._sharedService.getCall(url)
			.map((response: Response) => {
				return <any[]>response.json();
			})
			.catch(this.handleError);
	}

	getOneByUuidAndVersion(uuid: any, version: any, type: String): Observable<any[]> {
		return this._commonService.getOneByUuidAndVersion(uuid, version, type)
			.map((response: Response) => {

				if (response["filterInfo"] != null) {
					let filterInfoArray = [];
					for (let k = 0; k < response["filterInfo"].length; k++) {
						let filterInfo = {};
						filterInfo["logicalOperator"] = response["filterInfo"][k].logicalOperator
						filterInfo["lhsType"] = response["filterInfo"][k].operand[0].ref.type;
						filterInfo["operator"] = response["filterInfo"][k].operator;
						filterInfo["rhsType"] = response["filterInfo"][k].operand[1].ref.type;

						if (response["filterInfo"][k].operand[0].ref.type == 'formula') {
							let lhsAttri1 = {}
							lhsAttri1["uuid"] = response["filterInfo"][k].operand[0].ref.uuid;
							lhsAttri1["label"] = response["filterInfo"][k].operand[0].ref.name;
							filterInfo["lhsAttribute"] = lhsAttri1;
						}
						else if (response["filterInfo"][k].operand[0].ref.type == 'datapod') {
							let lhsAttri = {}
							lhsAttri["uuid"] = response["filterInfo"][k].operand[0].ref.uuid;
							lhsAttri["label"] = response["filterInfo"][k].operand[0].ref.name + "." + response["filterInfo"][k].operand[0].attributeName;
							lhsAttri["attributeId"] = response["filterInfo"][k].operand[0].attributeId;
							filterInfo["lhsAttribute"] = lhsAttri;
						}
						else if (response["filterInfo"][k].operand[0].ref.type == 'attribute') {
							filterInfo["lhsType"] = 'attribute';
							filterInfo["lhsAttribute"] = response["filterInfo"][k].operand[0].value;
						}

						else if (response["filterInfo"][k].operand[0].ref.type == 'simple') {
							let stringValue = response["filterInfo"][k].operand[0].value;
							let onlyNumbers = /^[0-9]+$/;
							let result = onlyNumbers.test(stringValue);
							if (result == true) {
								filterInfo["lhsType"] = 'integer';
							} else {
								filterInfo["lhsType"] = 'string';
							}
							filterInfo["lhsAttribute"] = response["filterInfo"][k].operand[0].value;
						}

						if (response["filterInfo"][k].operand[1].ref.type == 'formula') {
							let rhsAttri = {}
							rhsAttri["uuid"] = response["filterInfo"][k].operand[1].ref.uuid;
							rhsAttri["label"] = response["filterInfo"][k].operand[1].ref.name;
							filterInfo["rhsAttribute"] = rhsAttri;
						}
						else if (response["filterInfo"][k].operand[1].ref.type == 'function') {
							let rhsAttri = {}
							rhsAttri["uuid"] = response["filterInfo"][k].operand[1].ref.uuid;
							rhsAttri["label"] = response["filterInfo"][k].operand[1].ref.name;
							filterInfo["rhsAttribute"] = rhsAttri;
						}
						else if (response["filterInfo"][k].operand[1].ref.type == 'paramlist') {
							let rhsAttri = {}
							rhsAttri["uuid"] = response["filterInfo"][k].operand[1].ref.uuid;
							rhsAttri["attributeId"] = response["filterInfo"][k].operand[1].attributeId;
							rhsAttri["label"] = "app."+response["filterInfo"][k].operand[1].attributeName;
							filterInfo["rhsAttribute"] = rhsAttri;
						}
						else if (response["filterInfo"][k].operand[1].ref.type == 'datapod') {
							let rhsAttri1 = {}
							rhsAttri1["uuid"] = response["filterInfo"][k].operand[1].ref.uuid;
							rhsAttri1["label"] = response["filterInfo"][k].operand[1].ref.name + "." + response["filterInfo"][k].operand[1].attributeName;
							rhsAttri1["attributeId"] = response["filterInfo"][k].operand[1].attributeId;
							filterInfo["rhsAttribute"] = rhsAttri1;
						}
						else if (response["filterInfo"][k].operand[1].ref.type == 'attribute') {
							filterInfo["rhsType"] = 'datapod';
							filterInfo["rhsAttribute"] = response["filterInfo"][k].operand[1].value;
						}
						else if (response["filterInfo"][k].operand[1].ref.type == 'simple') {
							let stringValue = response["filterInfo"][k].operand[1].value;
							let onlyNumbers = /^[0-9]+$/;
							let result = onlyNumbers.test(stringValue);
							if (result == true) {
								filterInfo["rhsType"] = 'integer';
							} else {
								filterInfo["rhsType"] = 'string';
							}
							filterInfo["rhsAttribute"] = response["filterInfo"][k].operand[1].value;

							let result2 = stringValue.includes("and")
							if (result2 == true) {
								filterInfo["rhsType"] = 'integer';

								let betweenValArray = []
								betweenValArray = stringValue.split("and");
								filterInfo["rhsAttribute1"] = betweenValArray[0];
								filterInfo["rhsAttribute2"] = betweenValArray[1];
							}
						}
						filterInfoArray.push(filterInfo);
						console.log(filterInfoArray)
						response["filterTableArray"] = filterInfoArray
					}
				}

				if(response["attributeMap"]){
					let attributeTableArray = [];
					for (let i = 0; i < response["attributeMap"].length; i++) {
						let attributeInfo = {};
						attributeInfo["attrMapId"] = response["attributeMap"][i].attrMapId;
						attributeInfo["sourceType"] = response["attributeMap"][i].sourceAttr.ref.type

						if(response["attributeMap"][i].sourceAttr.ref.type == "datapod" ||
						 response["attributeMap"][i].sourceAttr.ref.type == "dataset" || 
						 response["attributeMap"][i].sourceAttr.ref.type == "rule"){
							let sourceAttribute = {};
							
							sourceAttribute["attributeId"] = response["attributeMap"][i].attrMapId;
							sourceAttribute["attrName"] = response["attributeMap"][i].sourceAttr.attrName;
							sourceAttribute["uuid"] = response["attributeMap"][i].sourceAttr.ref.uuid;
							sourceAttribute["label"] = response["attributeMap"][i].sourceAttr.ref.name+"."+response["attributeMap"][i].sourceAttr.attrName;
							attributeInfo["sourceAttribute"] = sourceAttribute;
						}

						else if(response["attributeMap"][i].sourceAttr.ref.type == "simple" ){
							attributeInfo["sourceAttribute"] = response["attributeMap"][i]["sourceAttr"].value;
							attributeInfo["sourceType"] = "string";
						}
						else if(response["attributeMap"][i].sourceAttr.ref.type == "attribute"){
							attributeInfo["sourceAttribute"] = response["attributeMap"][i]["sourceAttr"].value;
							attributeInfo["sourceType"] = "datapod";

							// let sourceAttribute = {};
							// sourceAttribute["uuid"] = response["attributeMap"][i].sourceAttr.ref.uuid;
							// sourceAttribute["attrMapId"] = response["attributeMap"][i].attrMapId;
							// sourceAttribute["label"] = response["attributeMap"][i].sourceAttr.ref.name+"."+response["attributeMap"][i].sourceAttr.attrName;
						}

						if(response["attributeMap"][i].sourceAttr.ref.type == "formula"){
							let sourceAttribute = {};
							sourceAttribute["uuid"] = response["attributeMap"][i].sourceAttr.ref.uuid;
							sourceAttribute["label"] = response["attributeMap"][i].sourceAttr.ref.name;
							attributeInfo["sourceAttribute"] = sourceAttribute;
						}
						if(response["attributeMap"][i].sourceAttr.ref.type == "function"){
							let sourceAttribute = {};
							sourceAttribute["uuid"] = response["attributeMap"][i].sourceAttr.ref.uuid;
							sourceAttribute["label"] = response["attributeMap"][i].sourceAttr.ref.name;
							attributeInfo["sourceAttribute"] = sourceAttribute;
						}
						if(response["attributeMap"][i].targetAttr.ref.type !="simple" && response["attributeMap"][i].targetAttr.ref.type !="attribute"){
						
							let targetAttribute = {};
							targetAttribute
							targetAttribute["uuid"] = response["attributeMap"][i].targetAttr.ref.uuid;
							//targetAttribute["name"] = response["attributeMap"][i].targetAttr.ref.name;
							targetAttribute["label"] = response["attributeMap"][i].targetAttr.ref.name+"."+response["attributeMap"][i].targetAttr.attrName;
							targetAttribute["type"] = response["attributeMap"][i].targetAttr.ref.type;
							targetAttribute["attributeId"] = response["attributeMap"][i].targetAttr.attrId;
							targetAttribute["attrName"]=response["attributeMap"][i].targetAttr.attrName;
							attributeInfo["IsTargetAttributeSimple"] = "false";
							attributeInfo["targetAttribute"] = targetAttribute;
							//attributeInfo["targetAttribute"] = [];
						}
						else {
							let targetAttribute=response["attributeMap"][i].targetAttr.value;
							attributeInfo["IsTargetAttributeSimple"] = "true";
							attributeInfo["targetAttribute"] = targetAttribute;
							//attributeInfo["targetAttribute"] = [];
						}

						attributeTableArray[i] = attributeInfo;
					}
					response["attributeTableArray"] = attributeTableArray;
				}
				return <any>response;
			})
			.catch(this.handleError);
	}

	private handleError(error: Response) {
		return Observable.throw(error.statusText);
	}

}