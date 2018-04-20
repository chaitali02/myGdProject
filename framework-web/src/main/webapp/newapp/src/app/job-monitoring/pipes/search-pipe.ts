import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'filterJob',
    pure: false
})
export class FilterJobPipe implements PipeTransform {
    transform(items: any[], term): any {
        //console.log('term', term);
      
        return term 
            ? items.filter(item => item.caption.indexOf(term) !== -1)
            : items;
    }
}
