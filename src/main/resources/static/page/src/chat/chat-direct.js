import { bindable } from 'aurelia-framework';
import poll from "common/common-poll";

export class ChatDirect {

    @bindable content;

    /**
     * 当数据绑定引擎绑定到视图时被调用
     * @param  {[object]} ctx 视图绑定上下文环境对象
     */
    bind(ctx) {
        poll.start(() => {
            console.log('polling...');
        });
    }

    /**
     * 当数据绑定引擎从视图解除绑定时被调用
     */
    unbind() {
        poll.stop();
    }

    sendKeydownHandler(evt) {
        if (evt.keyCode === 13) {

            $.post('/admin/chat/direct/create', {
                preMore: false,
                lastId: '',
                baseURL: '',
                chatTo: 'test',
                content: this.content,
                contentHtml: '',
            }, function(data, textStatus, xhr) {
                if (data.success) {
                    this.content = '';
                    console.log('...........');
                }
            });

            console.log(this.content);
            evt.stopPropagation();
            return false;
        }

        return true;
    }
}
