import { Injectable } from '@angular/core';

declare let jQuery: any;

@Injectable()
export class AppConfig {
  config = {
    name: 'InferYX',
    logoImg: '/assets/img/logo.png',
    title: 'InferYX',
    version: 'v0.1.0 Patent Pending.',
    year: '2017-19',
    copyright: '2017-19 © Inferyx, Inc. All rights reserved.', //not used
    protocall : 'http://',
    serverHost: 'localhost',
    port: '8080',
    coontextPath: ''
  };

  getConfig(): Object {
    return this.config;
  }

  getBaseUrl() : string {
    return this.config.protocall + '' + this.config.serverHost + ':' + this.config.port + '/' + this.config.coontextPath;
  }
}
