import config from './config';

export function configure(aurelia) {

    config.context(aurelia).initHttp().initToastr();

    //config.globalResources([]);
}
