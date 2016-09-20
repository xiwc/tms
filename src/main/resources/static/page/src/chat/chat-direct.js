import { bindable } from 'aurelia-framework';
import poll from "common/common-poll";
import 'jquery.scrollto'; // https://github.com/flesler/jquery.scrollTo

export class ChatDirect {

    @bindable content;

    /**
     * 当数据绑定引擎绑定到视图时被调用
     * @param  {[object]} ctx 视图绑定上下文环境对象
     */
    bind(ctx) {

        $.get('/admin/chat/direct/list', {
            size: 20,
            chatTo: 'test'
        }, (data) => {
            if (data.success) {
                this.chats = _.reverse(data.data.content);
                _.defer(() => {
                    $('html,body').scrollTo('max');
                });
            } else {
                toastr.error(data.data, '获取消息失败!');
            }
        });

        poll.start(() => {
            $.get('/admin/chat/direct/latest', {
                id: _.last(this.chats).id,
                chatTo: 'test'
            }, (data) => {
                if (data.success) {
                    // this.chats = _.concat(this.chats, data.data);
                    this.chats = _.unionBy(this.chats, data.data, 'id');
                    _.defer(() => {
                        $('html,body').scrollTo('max');
                    });
                } else {
                    toastr.error(data.data, '获取消息失败!');
                }
            });
        });
    }

    /**
     * 当数据绑定引擎从视图解除绑定时被调用
     */
    unbind() {
        poll.stop();
    }

    sendKeydownHandler(evt) {

        if (this.sending) {
            return false;
        }

        if (evt.keyCode === 13) {

            this.sending = true;

            $.post('/admin/chat/direct/create', {
                preMore: false,
                lastId: '',
                baseURL: '',
                chatTo: 'test',
                content: this.content,
                contentHtml: '',
            }, (data, textStatus, xhr) => {
                if (data.success) {
                    this.content = '';
                    poll.reset();
                } else {
                    toastr.error(data.data, '发送消息失败!');
                }
            }).always(() => {
                this.sending = false;
            });

            return false;
        }

        return true;
    }
}
