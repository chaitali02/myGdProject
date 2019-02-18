import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'filterMeta',
    pure: false
})
export class FilterMetaPipe implements PipeTransform {
    transform(items: any[], term: any): any {
    return term 
        ? items.filter(item => {
            if (item.caption.indexOf(term) !== -1) {
                return true;
            }
            if (item.caption.toLowerCase().indexOf(term) !== -1) {
                return true;
            }
            if (item.caption.toUpperCase().indexOf(term) !== -1) {
                return true;
            }
        })
        : items
    }
}
