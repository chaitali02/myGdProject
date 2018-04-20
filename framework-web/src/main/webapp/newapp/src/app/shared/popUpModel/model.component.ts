

import { Component,ViewEncapsulation,ElementRef, Renderer, NgZone,ViewChild,Input,Output, EventEmitter } from '@angular/core';
// import{CommonListComponent} from '../../common-list/common-list.component'
declare var jQuery:any;
@Component({
  selector: 'app-model',
  styleUrls: ['./model.component.css'],
  templateUrl: './model.template.html'
})
export class ModelComponent {
    @ViewChild('ConfirmModel') ConfirmModel:ElementRef;
    @Input()
    ModelData:any;
    @Output()
    parentFunction:EventEmitter<any>=new EventEmitter();
  ngOnInit(){
      this.open();
      console.log(this.ModelData);
  }
  
  public invokeParentMethod(functionName) {
    console.log(functionName)
    jQuery(this.ConfirmModel.nativeElement).modal('hide');
    this.parentFunction.emit(functionName);
    //this.params.context.componentParent.view(this.params.node.data)

}

  open(){
    jQuery(this.ConfirmModel.nativeElement).modal('show'); 
  }
  
}