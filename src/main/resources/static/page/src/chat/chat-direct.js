import { bindable } from 'aurelia-framework';
import poll from "common/common-poll";
import 'jquery.scrollto'; // https://github.com/flesler/jquery.scrollTo
import {
    default as marked
} from 'marked'; // https://github.com/chjj/marked

export class ChatDirect {

    @bindable content;

    /**
     * 构造函数
     */
    constructor() {
        marked.setOptions({
            breaks: true
        });
    }

    convertMd(chats) {
        _.each(chats, (item) => {
            item.contentMd = marked(item.content);
        });
        return chats;
    }

    /**
     * 在视图模型(ViewModel)展示前执行一些自定义代码逻辑
     * @param  {[object]} params                参数
     * @param  {[object]} routeConfig           路由配置
     * @param  {[object]} navigationInstruction 导航指令
     * @return {[promise]}                      你可以可选的返回一个延迟许诺(promise), 告诉路由等待执行bind和attach视图(view), 直到你完成你的处理工作.
     */
    activate(params, routeConfig, navigationInstruction) {
        this.init(params.username);
    }

    switchChatToHandler(chatTo) {
        this.init(chatTo);
        poll.reset();
        return true;
    }

    init(chatTo) {

        this.chatTo = chatTo;

        $.get('/admin/chat/direct/list', {
            size: 20,
            chatTo: chatTo
        }, (data) => {
            if (data.success) {
                this.chats = _.reverse(this.convertMd(data.data.content));
                _.defer(() => {
                    $('html,body').scrollTo('max');
                });
            } else {
                toastr.error(data.data, '获取消息失败!');
            }
        });
    }

    /**
     * 当数据绑定引擎绑定到视图时被调用
     * @param  {[object]} ctx 视图绑定上下文环境对象
     */
    bind(ctx) {

        $.get('/admin/user/all', {
            enabled: true
        }, (data) => {
            if (data.success) {
                this.users = data.data;
            } else {
                toastr.error(data.data, '获取全部用户失败!');
            }
        });

        poll.start(() => {
            $.get('/admin/chat/direct/latest', {
                id: _.last(this.chats).id,
                chatTo: this.chatTo
            }, (data) => {
                if (data.success) {
                    // this.chats = _.concat(this.chats, data.data);
                    this.chats = _.unionBy(this.chats, this.convertMd(data.data), 'id');
                    _.defer(() => {
                        $('html,body').scrollTo('max');
                    });
                } else {
                    toastr.error(data.data, '轮询获取消息失败!');
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

        if (!evt.ctrlKey && evt.keyCode === 13) {

            this.sending = true;

            $.post('/admin/chat/direct/create', {
                baseUrl: utils.getBaseUrl(),
                path: wurl('path'),
                chatTo: this.chatTo,
                content: this.content,
                contentHtml: marked(this.content)
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
        } else if (evt.ctrlKey && evt.keyCode === 13) {
            this.content += '\r\n';
        }

        return true;
    }
}
