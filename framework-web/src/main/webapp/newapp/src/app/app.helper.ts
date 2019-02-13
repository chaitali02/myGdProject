import { Injectable } from '@angular/core';

@Injectable()
export class AppHelper {
    sortByProperty(array: Array<string>, args: string): any[] {
        array.sort((a: any, b: any) => {
            if (a[args] < b[args]) {
                return -1;
            } else if (a[args] > b[args]) {
                return 1;
            } else {
                return 0;
            }
        });
        return array;
    }

    getStatus(statusArray: any) {
        return statusArray[statusArray.length - 1];
    }

    convertStringToBoolean(value: String) {
        return value == 'Y' ? true : false;
    }
    convertBooleanToString(value: boolean) {
        return value == true ? 'Y' : 'N'
    }
}
