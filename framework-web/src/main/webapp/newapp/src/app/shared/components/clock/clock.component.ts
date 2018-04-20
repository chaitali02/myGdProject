import { Component } from '@angular/core';

@Component({
  selector: 'app-clock',
  template: `{{time | date:"MMM dd yyyy - HH:mm:ss"}} {{tzName}}`
})
export class ClockComponent {
  time = new Date();
  tzName : any;

  ngOnInit() {
    this.updateClock();
  }

  updateClock() {
    this.time = new Date();
    this.tzName = this.time.toLocaleString('en', {
      timeZoneName: 'short'
    }).split(' ').pop();

    setTimeout(() => this.updateClock(), 1000 );
  }
}
