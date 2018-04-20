import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'filter',
    pure: false
})
export class FilterPipeDD implements PipeTransform {
    transform(items: any[], term): any {
        //console.log('term', term);
      
        return term 
            ? items.filter(item => item.title.indexOf(term) !== -1)
            : items;
    }
}
