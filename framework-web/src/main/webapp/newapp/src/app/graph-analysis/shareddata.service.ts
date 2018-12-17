import { Injectable } from '@angular/core';

@Injectable()
export class SharedDataService {
    private graphData: any;

    constructor() {
        this.graphData;
    }

    public setData(val: any): void {
        this.graphData = val;
    }

    public getData(): any {
        return this.graphData;
    }

}