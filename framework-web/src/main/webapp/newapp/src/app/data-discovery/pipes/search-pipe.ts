import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'filter',
    pure: false
})
export class FilterPipeDD implements PipeTransform {
    transform(items: any[], term): any {
        return term 
            ? items.filter(item => {
                if (item.title.indexOf(term) !== -1) {
                    return true;
                }
                if (item.title.toLowerCase().indexOf(term) !== -1) {
                    return true;
                }
                if (item.title.toUpperCase().indexOf(term) !== -1) {
                    return true;
                }
            })
            : items;
    }
}
