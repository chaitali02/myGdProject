import { Injectable, Inject, Input } from "@angular/core";
import { Http, Response } from "@angular/http";
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { DataIngestRuleIO } from '../domainIO/domain.dataIngestRuleIO';
import { CommonService } from "./common.service";
import { SharedService } from "../../shared/shared.service";
import { DataQualityIO } from '../domainIO/domain.dataQualityIO';
import { FilterInfoIO } from '../domainIO/domain.filterInfoIO';
import { AttributeMapIO } from '../domainIO/domain.attributeMapIO';
import { AttributeIO } from '../domainIO/domain.attributeIO';

@Injectable()

export class DataIngestionService {
	sessionId: string;
	baseUrl: string;
	headers: Headers;
	constructor(@Inject(Http) private http: Http, private _sharedService: SharedService, private _commonService: CommonService) {

	}
	private handleError<T>(error: any, result?: T) {
		return throwError(error);
	}

	getDatasourceForFile(type: String): Observable<any[]> {
		let url = 'metadata/getDatasourceForFile?action=view&type=' + type;
		return this._sharedService.getCall(url)
			.pipe(
				map(response => { return <any[]>response.json(); }),
				catchError(error => this.handleError<string>(error, "Network Error!")));
	}

	getDatasourceForTable(type: String): Observable<any[]> {
		let url = 'metadata/getDatasourceForTable?action=view&type=' + type;
		return this._sharedService.getCall(url)
			.pipe(
				map(response => { return <any[]>response.json(); }),
				catchError(error => this.handleError<string>(error, "Network Error!")));
	}

	getDatasourceForStream(type: String): Observable<any[]> {
		let url = 'metadata/getDatasourceForStream?action=view&type=' + type;
		return this._sharedService.getCall(url)
			.pipe(
				map(response => { return <any[]>response.json(); }),
				catchError(error => this.handleError<string>(error, "Network Error!")));
	}

	getDatapodByDatasource(type: String, uuid: any): Observable<any[]> {
		let url = 'metadata/getDatapodByDatasource?action=view&type=' + type + '&uuid=' + uuid;
		return this._sharedService.getCall(url)
			.pipe(
				map(response => { return <any[]>response.json(); }),
				catchError(error => this.handleError<string>(error, "Network Error!")));
	}

	getAttributesByDatapod(type: String, uuid: any): Observable<any[]> {
		let url = 'metadata/getAttributesByDatapod?action=view&type=' + type + '&uuid=' + uuid;
		return this._sharedService.getCall(url)
			.pipe(
				map(response => { return <any[]>response.json(); }),
				catchError(error => this.handleError<string>(error, "Network Error!")));
	}

	getAttributesByDataset(type: String, uuid: any): Observable<any[]> {
		let url = 'metadata/getAttributesByDataset?action=view&type=' + type + '&uuid=' + uuid;
		return this._sharedService.getCall(url)
			.pipe(
				map(response => { return <any[]>response.json(); }),
				catchError(error => this.handleError<string>(error, "Network Error!")));
	}

	getTopicList(uuid: any, version: any): Observable<any[]> {
		let url = 'ingest/getTopicList?action=view&type=datasource&uuid=' + uuid + '&version=';
		return this._sharedService.getCall(url)
			.pipe(
				map(response => { return <any[]>response.json(); }),
				catchError(error => this.handleError<string>(error, "Network Error!")));
	}

	getFunctionByCriteria(category: any, inputReq: any, type: any): Observable<any[]> {
		let url = '/metadata/getFunctionByCriteria?action=view&category=' + category + '&inputReq=' + inputReq + '&type=' + type;
		return this._sharedService.getCall(url)
			.pipe(
				map(response => { return <any[]>response.json(); }),
				catchError(error => this.handleError<string>(error, "Network Error!")));
	}

	getParamByApp(uuid: any, type: any): Observable<any[]> {
		let url = '/metadata/getParamByApp?action=view&uuid=' + uuid + '&type=' + type;
		return this._sharedService.getCall(url)
			.pipe(
				map(response => { return <any[]>response.json(); }),
				catchError(error => this.handleError<string>(error, "Network Error!")));
	}

	getOneByUuidAndVersion(uuid: any, version: any, type: String) {
		return this._commonService.getOneByUuidAndVersion(uuid, version, type)
			.pipe(
				map(response => {
					let dataIngestIO = new DataIngestRuleIO();
					dataIngestIO.ingestRule = response;

					if (response.filterInfo != null) {
						let filterInfoArray = [new FilterInfoIO];
						for (let k = 0; k < response.filterInfo.length; k++) {
							let filterInfoIO = new FilterInfoIO();
							filterInfoIO.logicalOperator = response.filterInfo[k].logicalOperator
							filterInfoIO.lhsType = response.filterInfo[k].operand[0].ref.type;
							filterInfoIO.operator = response.filterInfo[k].operator;
							filterInfoIO.rhsType = response.filterInfo[k].operand[1].ref.type;

							if (response.filterInfo[k].operand[0].ref.type == 'formula') {
								let lhsAttribute = new AttributeIO()
								lhsAttribute.uuid = response.filterInfo[k].operand[0].ref.uuid;
								lhsAttribute.label = response.filterInfo[k].operand[0].ref.name;
								filterInfoIO.lhsAttribute = lhsAttribute;
							}
							else if (response.filterInfo[k].operand[0].ref.type == 'datapod') {
								let lhsAttribute = new AttributeIO();
								lhsAttribute.uuid = response.filterInfo[k].operand[0].ref.uuid;
								lhsAttribute.label = response.filterInfo[k].operand[0].ref.name + "." + response.filterInfo[k].operand[0].attributeName;
								lhsAttribute.attributeId = response.filterInfo[k].operand[0].attributeId;
								filterInfoIO.lhsAttribute = lhsAttribute;
							}
							else if (response.filterInfo[k].operand[0].ref.type == 'attribute') {
								filterInfoIO.lhsType = 'datapod';
								filterInfoIO.lhsAttribute = response.filterInfo[k].operand[0].value;
							}
							else if (response.filterInfo[k].operand[0].ref.type == 'simple') {
								let stringValue = response.filterInfo[k].operand[0].value;
								let onlyNumbers = /^[0-9]+$/;
								let result = onlyNumbers.test(stringValue);
								if (result == true) {
									filterInfoIO.lhsType = 'integer';
								} else {
									filterInfoIO.lhsType = 'string';
								}
								filterInfoIO.lhsAttribute = response.filterInfo[k].operand[0].value;
							}

							if (response.filterInfo[k].operand[1].ref.type == 'formula') {
								let rhsAttribute = new AttributeIO();
								rhsAttribute.uuid = response.filterInfo[k].operand[1].ref.uuid;
								rhsAttribute.label = response.filterInfo[k].operand[1].ref.name;
								filterInfoIO.rhsAttribute = rhsAttribute;
							}
							else if (response.filterInfo[k].operand[1].ref.type == 'function') {
								let rhsAttribute = new AttributeIO();
								rhsAttribute.uuid = response.filterInfo[k].operand[1].ref.uuid;
								rhsAttribute.label = response.filterInfo[k].operand[1].ref.name;
								filterInfoIO.rhsAttribute = rhsAttribute;
							}
							else if (response.filterInfo[k].operand[1].ref.type == 'paramlist') {
								let rhsAttribute = new AttributeIO();
								rhsAttribute.uuid = response.filterInfo[k].operand[1].ref.uuid;
								rhsAttribute.attributeId = response.filterInfo[k].operand[1].attributeId;
								rhsAttribute.label = "app." + response.filterInfo[k].operand[1].attributeName;
								filterInfoIO.rhsAttribute = rhsAttribute;
							}
							else if (response.filterInfo[k].operand[1].ref.type == 'dataset') {
								let rhsAttribute = new AttributeIO();
								rhsAttribute.uuid = response.filterInfo[k].operand[1].ref.uuid;
								rhsAttribute.attributeId = response.filterInfo[k].operand[1].attributeId;
								rhsAttribute.label = response.filterInfo[k].operand[1].attributeName;
								filterInfoIO.rhsAttribute = rhsAttribute;
							}
							else if (response.filterInfo[k].operand[1].ref.type == 'datapod') {
								let rhsAttribute = new AttributeIO();
								rhsAttribute.uuid = response.filterInfo[k].operand[1].ref.uuid;
								rhsAttribute.label = response.filterInfo[k].operand[1].ref.name + "." + response.filterInfo[k].operand[1].attributeName;
								rhsAttribute.attributeId = response.filterInfo[k].operand[1].attributeId;
								filterInfoIO.rhsAttribute = rhsAttribute;
							}
							else if (response.filterInfo[k].operand[1].ref.type == 'attribute') {
								filterInfoIO.rhsType = 'datapod';
								filterInfoIO.rhsAttribute = response.filterInfo[k].operand[1].value;
							}
							else if (response.filterInfo[k].operand[1].ref.type == 'simple') {
								let stringValue = response.filterInfo[k].operand[1].value;
								let onlyNumbers = /^[0-9]+$/;
								let result = onlyNumbers.test(stringValue);
								if (result == true) {
									filterInfoIO.rhsType = 'integer';
								} else {
									filterInfoIO.rhsType = 'string';
								}
								filterInfoIO.rhsAttribute = response.filterInfo[k].operand[1].value;

								let result2 = stringValue.includes("and")
								if (result2 == true) {
									filterInfoIO.rhsType = 'integer';
									let betweenValArray = []
									betweenValArray = stringValue.split("and");
									filterInfoIO.rhsAttribute1 = betweenValArray[0];
									filterInfoIO.rhsAttribute2 = betweenValArray[1];
								}
							}
							filterInfoArray[k] = filterInfoIO;
							console.log(filterInfoArray)
							dataIngestIO.filterInfo = filterInfoArray;
						}
					} else {
						dataIngestIO.filterInfo = [];
					}

					if (response.attributeMap) {
						let attributeTableArray = [];
						for (let i = 0; i < response.attributeMap.length; i++) {
							let attributeInfo = new AttributeMapIO();
							attributeInfo.attrMapId = response.attributeMap[i].attrMapId;
							attributeInfo.sourceType = response.attributeMap[i].sourceAttr.ref.type

							if (response.attributeMap[i].sourceAttr.ref.type == "datapod" ||
								response.attributeMap[i].sourceAttr.ref.type == "dataset" ||
								response.attributeMap[i].sourceAttr.ref.type == "rule") {
								let sourceAttribute = new AttributeIO();
								let a = response.attributeMap[i].attrMapId;
								sourceAttribute.attributeId = a;
								sourceAttribute.attrName = response.attributeMap[i].sourceAttr.attrName;
								sourceAttribute.uuid = response.attributeMap[i].sourceAttr.ref.uuid;
								sourceAttribute.label = response.attributeMap[i].sourceAttr.ref.name + "." + response.attributeMap[i].sourceAttr.attrName;
								attributeInfo.sourceAttribute = sourceAttribute;
							}

							else if (response.attributeMap[i].sourceAttr.ref.type == "simple") {

								attributeInfo.sourceAttribute = response.attributeMap[i].sourceAttr.value;
								attributeInfo.sourceType = "string";
							}
							else if (response.attributeMap[i].sourceAttr.ref.type == "attribute") {
								attributeInfo.sourceAttribute = response.attributeMap[i].sourceAttr.value;
								attributeInfo.sourceType = "datapod";
							}

							if (response.attributeMap[i].sourceAttr.ref.type == "formula") {
								let sourceAttribute = new AttributeIO();
								sourceAttribute.uuid = response.attributeMap[i].sourceAttr.ref.uuid;
								sourceAttribute.label = response.attributeMap[i].sourceAttr.ref.name;
								attributeInfo.sourceAttribute = sourceAttribute;
							}
							if (response.attributeMap[i].sourceAttr.ref.type == "function") {
								let sourceAttribute = new AttributeIO();
								sourceAttribute.uuid = response.attributeMap[i].sourceAttr.ref.uuid;
								sourceAttribute.label = response.attributeMap[i].sourceAttr.ref.name;
								attributeInfo.sourceAttribute = sourceAttribute;
							}
							if (response.attributeMap[i].targetAttr.ref.type != "simple" && response.attributeMap[i].targetAttr.ref.type != "attribute") {
								let targetAttribute = new AttributeIO();
								targetAttribute.uuid = response.attributeMap[i].targetAttr.ref.uuid;
								//targetAttribute["name"] = response["attributeMap"][i].targetAttr.ref.name;
								targetAttribute.label = response.attributeMap[i].targetAttr.ref.name + "." + response.attributeMap[i].targetAttr.attrName;
								targetAttribute.type = response.attributeMap[i].targetAttr.ref.type;
								targetAttribute.attributeId = response.attributeMap[i].targetAttr.attrId;
								targetAttribute.attrName = response.attributeMap[i].targetAttr.attrName;
								attributeInfo.targetAttribute = targetAttribute;
								attributeInfo.IsTargetAttributeSimple = "false";

							}
							else {
								let targetAttribute = response.attributeMap[i].targetAttr.value;
								attributeInfo.IsTargetAttributeSimple = "true";
								attributeInfo.targetAttribute = targetAttribute;
							}
							attributeTableArray[i] = attributeInfo;
						}
						//response.attributeMap = attributeTableArray;

						//dataIngestIO.attributeMap= attributeTableArray;  
						dataIngestIO.attributeMap = attributeTableArray;

					} 
					else {
						dataIngestIO.attributeMap = [];
					}
					console.log(JSON.stringify(dataIngestIO.attributeMap));
					console.log(JSON.stringify(dataIngestIO));
					return <any>dataIngestIO;
				}), catchError(error => this.handleError<string>(error, "Network Error!")))

	}


}