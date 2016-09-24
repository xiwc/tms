import { inject } from 'aurelia-framework';
import { customAttribute } from 'aurelia-templating';
import {
    default as autosize
} from 'autosize';

@customAttribute('autosize')
@inject(Element)
export class AttrAutosize {

    constructor(element) {
        this.element = element;
    }

    valueChanged(newValue, oldValue) {
    	autosize(this.element);
    }

    bind(bindingContext) {
        this.valueChanged(this.value);
    }
}
