import { Injectable } from '@angular/core';

@Injectable()
export class AppHepler{

    sortByProperty(array: Array<string>, args: string): any[] {
        array.sort((a: any, b: any) => {
            if ( a[args] < b[args] ){
                return -1;
            }else if( a[args] > b[args]){
                return 1;
            }else{
                return 0;	
            }
        });
        return array;
    }
    getStatus(statusArray){
        return statusArray[statusArray.length-1];
    }
  
}
