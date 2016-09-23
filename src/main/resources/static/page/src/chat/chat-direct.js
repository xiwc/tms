import { bindable } from 'aurelia-framework';
import poll from "common/common-poll";
import 'jquery.scrollto'; // https://github.com/flesler/jquery.scrollTo
import {
    default as marked
} from 'marked'; // https://github.com/chjj/marked
import {
    default as Clipboard
} from 'clipboard';
import {
    default as autosize
} from 'autosize';
import 'paste';
import {
    default as Dropzone
} from 'dropzone';
import 'common/common-plugin';
import 'timeago';

export class ChatDirect {

    @bindable content = '';

    offset = -70;

    selfLink = utils.getBaseUrl() + wurl('path') + '#' + utils.getHash();

    first = true;
    last = true;

    originalHref = wurl();

    /**
     * 构造函数
     */
    constructor() {
        marked.setOptions({
            breaks: true
        });

        Dropzone.autoDiscover = false;

        new Clipboard('.tms-chat-direct .tms-clipboard')
            .on('success', function(e) {
                toastr.success('复制到剪贴板成功!');
            }).on('error', function(e) {
                toastr.error('复制到剪贴板成功!');
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

        this.markId = params.id;

        this.user = _.find(this.users, {
            username: params.username
        });

        let name = this.user ? this.user.name : params.username;
        routeConfig.navModel.setTitle(`${name} | 私聊 | TMS`);

        this.init(params.username);

        $(this.chatToDropdownRef).dropdown('set selected', this.chatTo);

        if (this.markId) {
            if ('pushState' in history) {
                history.replaceState(null, '', utils.removeUrlQuery('id'));
            } else {
                window.location.href = utils.removeUrlQuery('id');
            }
        }

    }

    lastMoreHandler() {

        let start = _.first(this.chats).id;
        $.get('/admin/chat/direct/more', {
            last: true,
            start: start,
            size: 20,
            chatTo: this.chatTo
        }, (data) => {
            if (data.success) {
                this.chats = _.unionBy(_.reverse(this.convertMd(data.data)), this.chats);
                this.last = (data.msgs[0] - data.data.length <= 0);
                !this.last && (this.lastCnt = data.msgs[0] - data.data.length);
                _.defer(() => {
                    $('html,body').scrollTo(`.tms-chat-direct .comments .comment[data-id=${start}]`, {
                        offset: this.offset
                    });
                });
            } else {
                toastr.error(data.data, '获取更多消息失败!');
            }
        });
    }

    firstMoreHandler() {

        let start = _.last(this.chats).id;
        $.get('/admin/chat/direct/more', {
            last: false,
            start: start,
            size: 20,
            chatTo: this.chatTo
        }, (data) => {
            if (data.success) {
                this.chats = _.unionBy(this.chats, this.convertMd(data.data));
                this.first = (data.msgs[0] - data.data.length <= 0);
                !this.first && (this.firstCnt = data.msgs[0] - data.data.length);
                _.defer(() => {
                    $('html,body').scrollTo(`.tms-chat-direct .comments .comment[data-id=${start}]`, {
                        offset: this.offset
                    });
                });
            } else {
                toastr.error(data.data, '获取更多消息失败!');
            }
        });
    }

    init(chatTo) {

        this.chatTo = chatTo;

        var data = {
            size: 20,
            chatTo: chatTo
        };

        if (this.markId) {
            data.id = this.markId;
        }

        $.get('/admin/chat/direct/list', data, (data) => {
            if (data.success) {
                this.chats = _.reverse(this.convertMd(data.data.content));
                this.last = data.data.last;
                this.first = data.data.first;
                !this.last && (this.lastCnt = data.data.totalElements - data.data.numberOfElements);
                !this.first && (this.firstCnt = data.data.size * data.data.number);

                _.defer(() => {
                    if (this.markId) {
                        $('html,body').scrollTo(`.tms-chat-direct .comments .comment[data-id=${this.markId}]`, {
                            offset: this.offset
                        });
                    } else {
                        $('html,body').scrollTo('max');
                    }
                });
            } else {
                toastr.error(data.data, '获取消息失败!');
                window.location = utils.getBaseUrl() + wurl('path') + `#/login?redirect=${encodeURIComponent(this.originalHref)}`;
            }
        }).always((xhr, sts, error) => {
            if (sts == 'error') { // for loal dev & debug.
                window.location = utils.getBaseUrl() + wurl('path') + `#/login?redirect=${encodeURIComponent(this.originalHref)}`;
            }
        });
    }

    /**
     * 当数据绑定引擎绑定到视图时被调用
     * @param  {[object]} ctx 视图绑定上下文环境对象
     */
    bind(ctx) {

        $.get('/admin/user/loginUser', (data) => {
            if (data.success) {
                this.loginUser = data.data;
            } else {
                toastr.error(data.data, '获取当前登录用户失败!');
            }
        });

        $.get('/admin/user/all', {
            enabled: true
        }, (data) => {
            if (data.success) {
                this.users = data.data;
                this.user = _.find(this.users, {
                    username: this.chatTo
                });
            } else {
                toastr.error(data.data, '获取全部用户失败!');
            }
        });

        poll.start(() => {

            if (!this.chats) {
                return;
            }

            let lastChat = _.last(this.chats);

            $.get('/admin/chat/direct/latest', {
                id: lastChat ? lastChat.id : 0,
                chatTo: this.chatTo
            }, (data) => {
                if (data.success) {
                    if (data.data.length == 0) {
                        return;
                    }
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

    chatToUserFilerKeyupHanlder(evt) {
        _.each(this.users, (item) => {
            item.hidden = item.username.indexOf(this.filter) == -1;
        });

        if (evt.keyCode === 13) {
            let user = _.find(this.users, {
                hidden: false
            });

            if (user) {
                window.location = wurl('path') + `#/chat-direct/${user.username}`;
            }
        }
    }

    clearFilterHandler() {
        this.filter = '';
        _.each(this.users, (item) => {
            item.hidden = item.username.indexOf(this.filter) == -1;
        });
    }

    sendChatMsg() {

        if (this.sending) {
            return;
        }

        this.sending = true;

        var html = $('<div class="markdown-body"/>').html('<style>.markdown-body{font-size:14px;line-height:1.6}.markdown-body>:first-child{margin-top:0!important}.markdown-body>:last-child{margin-bottom:0!important}.markdown-body a.absent{color:#C00}.markdown-body a.anchor{bottom:0;cursor:pointer;display:block;left:0;margin-left:-30px;padding-left:30px;position:absolute;top:0}.markdown-body h1,.markdown-body h2,.markdown-body h3,.markdown-body h4,.markdown-body h5,.markdown-body h6{cursor:text;font-weight:700;margin:20px 0 10px;padding:0;position:relative}.markdown-body h1 .mini-icon-link,.markdown-body h2 .mini-icon-link,.markdown-body h3 .mini-icon-link,.markdown-body h4 .mini-icon-link,.markdown-body h5 .mini-icon-link,.markdown-body h6 .mini-icon-link{color:#000;display:none}.markdown-body h1:hover a.anchor,.markdown-body h2:hover a.anchor,.markdown-body h3:hover a.anchor,.markdown-body h4:hover a.anchor,.markdown-body h5:hover a.anchor,.markdown-body h6:hover a.anchor{line-height:1;margin-left:-22px;padding-left:0;text-decoration:none;top:15%}.markdown-body h1:hover a.anchor .mini-icon-link,.markdown-body h2:hover a.anchor .mini-icon-link,.markdown-body h3:hover a.anchor .mini-icon-link,.markdown-body h4:hover a.anchor .mini-icon-link,.markdown-body h5:hover a.anchor .mini-icon-link,.markdown-body h6:hover a.anchor .mini-icon-link{display:inline-block}.markdown-body hr:after,.markdown-body hr:before{display:table;content:""}.markdown-body h1 code,.markdown-body h1 tt,.markdown-body h2 code,.markdown-body h2 tt,.markdown-body h3 code,.markdown-body h3 tt,.markdown-body h4 code,.markdown-body h4 tt,.markdown-body h5 code,.markdown-body h5 tt,.markdown-body h6 code,.markdown-body h6 tt{font-size:inherit}.markdown-body h1{color:#000;font-size:28px}.markdown-body h2{border-bottom:1px solid #CCC;color:#000;font-size:24px}.markdown-body h3{font-size:18px}.markdown-body h4{font-size:16px}.markdown-body h5{font-size:14px}.markdown-body h6{color:#777;font-size:14px}.markdown-body blockquote,.markdown-body dl,.markdown-body ol,.markdown-body p,.markdown-body pre,.markdown-body table,.markdown-body ul{margin:15px 0}.markdown-body hr{overflow:hidden;background:#e7e7e7;height:4px;padding:0;margin:16px 0;border:0;-moz-box-sizing:content-box;box-sizing:content-box}.markdown-body h1+p,.markdown-body h2+p,.markdown-body h3+p,.markdown-body h4+p,.markdown-body h5+p,.markdown-body h6+p,.markdown-body ol li>:first-child,.markdown-body ul li>:first-child{margin-top:0}.markdown-body hr:after{clear:both}.markdown-body a:first-child h1,.markdown-body a:first-child h2,.markdown-body a:first-child h3,.markdown-body a:first-child h4,.markdown-body a:first-child h5,.markdown-body a:first-child h6,.markdown-body>h1:first-child,.markdown-body>h1:first-child+h2,.markdown-body>h2:first-child,.markdown-body>h3:first-child,.markdown-body>h4:first-child,.markdown-body>h5:first-child,.markdown-body>h6:first-child{margin-top:0;padding-top:0}.markdown-body li p.first{display:inline-block}.markdown-body ol,.markdown-body ul{padding-left:30px}.markdown-body ol.no-list,.markdown-body ul.no-list{list-style-type:none;padding:0}.markdown-body ol ol,.markdown-body ol ul,.markdown-body ul ol,.markdown-body ul ul{margin-bottom:0}.markdown-body dl{padding:0}.markdown-body dl dt{font-size:14px;font-style:italic;font-weight:700;margin:15px 0 5px;padding:0}.markdown-body dl dt:first-child{padding:0}.markdown-body dl dt>:first-child{margin-top:0}.markdown-body dl dt>:last-child{margin-bottom:0}.markdown-body dl dd{margin:0 0 15px;padding:0 15px}.markdown-body blockquote>:first-child,.markdown-body dl dd>:first-child{margin-top:0}.markdown-body blockquote>:last-child,.markdown-body dl dd>:last-child{margin-bottom:0}.markdown-body blockquote{border-left:4px solid #DDD;color:#777;padding:0 15px}.markdown-body table th{font-weight:700}.markdown-body table td,.markdown-body table th{border:1px solid #CCC;padding:6px 13px}.markdown-body table tr{background-color:#FFF;border-top:1px solid #CCC}.markdown-body table tr:nth-child(2n){background-color:#F8F8F8}.markdown-body img{max-width:100%}.markdown-body span.frame{display:block;overflow:hidden}.markdown-body span.frame>span{border:1px solid #DDD;display:block;float:left;margin:13px 0 0;overflow:hidden;padding:7px;width:auto}.markdown-body span.frame span img{display:block;float:left}.markdown-body span.frame span span{clear:both;color:#333;display:block;padding:5px 0 0}.markdown-body span.align-center{clear:both;display:block;overflow:hidden}.markdown-body span.align-center>span{display:block;margin:13px auto 0;overflow:hidden;text-align:center}.markdown-body span.align-center span img{margin:0 auto;text-align:center}.markdown-body span.align-right{clear:both;display:block;overflow:hidden}.markdown-body span.align-right>span{display:block;margin:13px 0 0;overflow:hidden;text-align:right}.markdown-body span.align-right span img{margin:0;text-align:right}.markdown-body span.float-left{display:block;float:left;margin-right:13px;overflow:hidden}.markdown-body span.float-left span{margin:13px 0 0}.markdown-body span.float-right{display:block;float:right;margin-left:13px;overflow:hidden}.markdown-body span.float-right>span{display:block;margin:13px auto 0;overflow:hidden;text-align:right}.markdown-body code,.markdown-body tt{background-color:#F8F8F8;border:1px solid #EAEAEA;border-radius:3px;margin:0 2px;padding:0 5px;white-space:nowrap}.markdown-body pre>code{background:none;border:none;margin:0;padding:0;white-space:pre}.markdown-body .highlight pre,.markdown-body pre{background-color:#F8F8F8;border:1px solid #CCC;border-radius:3px;font-size:13px;line-height:19px;overflow:auto;padding:6px 10px}.markdown-body pre code,.markdown-body pre tt{background-color:transparent;border:none}</style>' + marked(this.content)).wrap('<div/>').parent().html();

        $.post('/admin/chat/direct/create', {
            baseUrl: utils.getBaseUrl(),
            path: wurl('path'),
            chatTo: this.chatTo,
            content: this.content,
            contentHtml: html
        }, (data, textStatus, xhr) => {
            if (data.success) {
                this.content = '';
                poll.reset();
                $(this.chatInputRef).css('height', 'auto');
            } else {
                toastr.error(data.data, '发送消息失败!');
            }
        }).always(() => {
            this.sending = false;
        });
    }

    sendKeydownHandler(evt) {

        if (!evt.ctrlKey && evt.keyCode === 13) {
            this.sendChatMsg();
            return false;
        } else if (evt.ctrlKey && evt.keyCode === 13) {
            this.insertTxt($(this.chatInputRef), '\n');
        }

        return true;
    }

    insertTxt($t, txt) {
        $t.insertAtCaret(txt);
        this.content = $t.val();
    }

    deleteHandler(item) {

        // TODO 确认提示框

        $.post('/admin/chat/direct/delete', {
            id: item.id
        }, (data, textStatus, xhr) => {
            if (data.success) {
                this.chats = _.reject(this.chats, {
                    id: item.id
                });
                toastr.success('删除消息成功!');
            } else {
                toastr.error(data.data, '删除消息失败!');
            }
        });
    }

    editHandler(item, editTxtRef) {
        item.isEditing = true;
        _.defer(() => {
            autosize(editTxtRef);
            $(editTxtRef).focus().select();
        });
    }

    eidtKeydownHandler(evt, item) {

        if (this.sending) {
            return false;
        }

        if (!evt.ctrlKey && evt.keyCode === 13) {

            this.sending = true;

            var html = $('<div class="markdown-body"/>').html('<style>.markdown-body{font-size:14px;line-height:1.6}.markdown-body>:first-child{margin-top:0!important}.markdown-body>:last-child{margin-bottom:0!important}.markdown-body a.absent{color:#C00}.markdown-body a.anchor{bottom:0;cursor:pointer;display:block;left:0;margin-left:-30px;padding-left:30px;position:absolute;top:0}.markdown-body h1,.markdown-body h2,.markdown-body h3,.markdown-body h4,.markdown-body h5,.markdown-body h6{cursor:text;font-weight:700;margin:20px 0 10px;padding:0;position:relative}.markdown-body h1 .mini-icon-link,.markdown-body h2 .mini-icon-link,.markdown-body h3 .mini-icon-link,.markdown-body h4 .mini-icon-link,.markdown-body h5 .mini-icon-link,.markdown-body h6 .mini-icon-link{color:#000;display:none}.markdown-body h1:hover a.anchor,.markdown-body h2:hover a.anchor,.markdown-body h3:hover a.anchor,.markdown-body h4:hover a.anchor,.markdown-body h5:hover a.anchor,.markdown-body h6:hover a.anchor{line-height:1;margin-left:-22px;padding-left:0;text-decoration:none;top:15%}.markdown-body h1:hover a.anchor .mini-icon-link,.markdown-body h2:hover a.anchor .mini-icon-link,.markdown-body h3:hover a.anchor .mini-icon-link,.markdown-body h4:hover a.anchor .mini-icon-link,.markdown-body h5:hover a.anchor .mini-icon-link,.markdown-body h6:hover a.anchor .mini-icon-link{display:inline-block}.markdown-body hr:after,.markdown-body hr:before{display:table;content:""}.markdown-body h1 code,.markdown-body h1 tt,.markdown-body h2 code,.markdown-body h2 tt,.markdown-body h3 code,.markdown-body h3 tt,.markdown-body h4 code,.markdown-body h4 tt,.markdown-body h5 code,.markdown-body h5 tt,.markdown-body h6 code,.markdown-body h6 tt{font-size:inherit}.markdown-body h1{color:#000;font-size:28px}.markdown-body h2{border-bottom:1px solid #CCC;color:#000;font-size:24px}.markdown-body h3{font-size:18px}.markdown-body h4{font-size:16px}.markdown-body h5{font-size:14px}.markdown-body h6{color:#777;font-size:14px}.markdown-body blockquote,.markdown-body dl,.markdown-body ol,.markdown-body p,.markdown-body pre,.markdown-body table,.markdown-body ul{margin:15px 0}.markdown-body hr{overflow:hidden;background:#e7e7e7;height:4px;padding:0;margin:16px 0;border:0;-moz-box-sizing:content-box;box-sizing:content-box}.markdown-body h1+p,.markdown-body h2+p,.markdown-body h3+p,.markdown-body h4+p,.markdown-body h5+p,.markdown-body h6+p,.markdown-body ol li>:first-child,.markdown-body ul li>:first-child{margin-top:0}.markdown-body hr:after{clear:both}.markdown-body a:first-child h1,.markdown-body a:first-child h2,.markdown-body a:first-child h3,.markdown-body a:first-child h4,.markdown-body a:first-child h5,.markdown-body a:first-child h6,.markdown-body>h1:first-child,.markdown-body>h1:first-child+h2,.markdown-body>h2:first-child,.markdown-body>h3:first-child,.markdown-body>h4:first-child,.markdown-body>h5:first-child,.markdown-body>h6:first-child{margin-top:0;padding-top:0}.markdown-body li p.first{display:inline-block}.markdown-body ol,.markdown-body ul{padding-left:30px}.markdown-body ol.no-list,.markdown-body ul.no-list{list-style-type:none;padding:0}.markdown-body ol ol,.markdown-body ol ul,.markdown-body ul ol,.markdown-body ul ul{margin-bottom:0}.markdown-body dl{padding:0}.markdown-body dl dt{font-size:14px;font-style:italic;font-weight:700;margin:15px 0 5px;padding:0}.markdown-body dl dt:first-child{padding:0}.markdown-body dl dt>:first-child{margin-top:0}.markdown-body dl dt>:last-child{margin-bottom:0}.markdown-body dl dd{margin:0 0 15px;padding:0 15px}.markdown-body blockquote>:first-child,.markdown-body dl dd>:first-child{margin-top:0}.markdown-body blockquote>:last-child,.markdown-body dl dd>:last-child{margin-bottom:0}.markdown-body blockquote{border-left:4px solid #DDD;color:#777;padding:0 15px}.markdown-body table th{font-weight:700}.markdown-body table td,.markdown-body table th{border:1px solid #CCC;padding:6px 13px}.markdown-body table tr{background-color:#FFF;border-top:1px solid #CCC}.markdown-body table tr:nth-child(2n){background-color:#F8F8F8}.markdown-body img{max-width:100%}.markdown-body span.frame{display:block;overflow:hidden}.markdown-body span.frame>span{border:1px solid #DDD;display:block;float:left;margin:13px 0 0;overflow:hidden;padding:7px;width:auto}.markdown-body span.frame span img{display:block;float:left}.markdown-body span.frame span span{clear:both;color:#333;display:block;padding:5px 0 0}.markdown-body span.align-center{clear:both;display:block;overflow:hidden}.markdown-body span.align-center>span{display:block;margin:13px auto 0;overflow:hidden;text-align:center}.markdown-body span.align-center span img{margin:0 auto;text-align:center}.markdown-body span.align-right{clear:both;display:block;overflow:hidden}.markdown-body span.align-right>span{display:block;margin:13px 0 0;overflow:hidden;text-align:right}.markdown-body span.align-right span img{margin:0;text-align:right}.markdown-body span.float-left{display:block;float:left;margin-right:13px;overflow:hidden}.markdown-body span.float-left span{margin:13px 0 0}.markdown-body span.float-right{display:block;float:right;margin-left:13px;overflow:hidden}.markdown-body span.float-right>span{display:block;margin:13px auto 0;overflow:hidden;text-align:right}.markdown-body code,.markdown-body tt{background-color:#F8F8F8;border:1px solid #EAEAEA;border-radius:3px;margin:0 2px;padding:0 5px;white-space:nowrap}.markdown-body pre>code{background:none;border:none;margin:0;padding:0;white-space:pre}.markdown-body .highlight pre,.markdown-body pre{background-color:#F8F8F8;border:1px solid #CCC;border-radius:3px;font-size:13px;line-height:19px;overflow:auto;padding:6px 10px}.markdown-body pre code,.markdown-body pre tt{background-color:transparent;border:none}</style>' + marked(item.content)).wrap('<div/>').parent().html();

            $.post('/admin/chat/direct/update', {
                baseUrl: utils.getBaseUrl(),
                path: wurl('path'),
                id: item.id,
                content: item.content,
                contentHtml: html
            }, (data, textStatus, xhr) => {
                if (data.success) {
                    toastr.success('更新消息成功!');
                    item.contentMd = marked(item.content);
                    item.isEditing = false;
                } else {
                    toastr.error(data.data, '更新消息失败!');
                }
            }).always(() => {
                this.sending = false;
            });

            return false;
        } else if (evt.ctrlKey && evt.keyCode === 13) {
            let $t = $(evt.target);
            $t.insertAtCaret('\n');
            item.content = $t.val();
        }

        return true;
    }

    focusoutHandler(item) {
        item.isEditing = false;
    }

    chatToUserFilerFocusinHanlder() {
        $(this.userListRef).scrollTo(`a.item[data-id="${this.chatTo}"]`);
    }

    initUploadDropzone(domRef, clickable) {

        let _this = this;

        $(domRef).dropzone({
            url: "/admin/file/upload",
            paramName: 'file',
            clickable: !!clickable,
            dictDefaultMessage: '',
            init: function() {
                this.on("success", function(file, data) {
                    if (data.success) {

                        $.each(data.data, function(index, item) {
                            if (item.type == 'Image') {
                                _this.insertTxt($(_this.chatInputRef), '![{name}]({baseURL}{path}{uuidName}) '
                                    .replace(/\{name\}/g, item.name)
                                    .replace(/\{baseURL\}/g, utils.getBaseUrl() + '/')
                                    .replace(/\{path\}/g, item.path)
                                    .replace(/\{uuidName\}/g, item.uuidName));
                            } else {
                                _this.insertTxt($(_this.chatInputRef), '[{name}]({baseURL}{path}{uuidName}) '
                                    .replace(/\{name\}/g, item.name)
                                    .replace(/\{baseURL\}/g, utils.getBaseUrl() + '/')
                                    .replace(/\{path\}/g, "admin/file/download/")
                                    .replace(/\{uuidName\}/g, item.id));
                            }
                        });
                        toastr.success('上传成功!');
                    } else {
                        toastr.error(data.data, '上传失败!');
                    }

                });
                this.on("error", function(file, errorMessage, xhr) {
                    toastr.error(errorMessage, '上传失败!');
                });
                this.on("complete", function(file) {
                    this.removeFile(file);
                });
            }
        });
    }

    /**
     * 当视图被附加到DOM中时被调用
     */
    attached() {

        let _this = this;

        autosize(this.chatInputRef);

        // clipboard paste image
        $(this.chatInputRef).pastableTextarea().on('pasteImage', (ev, data) => {

            $.post('/admin/file/base64', {
                dataURL: data.dataURL,
                type: data.blob.type
            }, (data, textStatus, xhr) => {
                if (data.success) {
                    this.insertTxt($(this.chatInputRef), '![{name}]({baseURL}{path}{uuidName})'
                        .replace(/\{name\}/g, data.data.name)
                        .replace(/\{baseURL\}/g, utils.getBaseUrl() + '/')
                        .replace(/\{path\}/g, data.data.path)
                        .replace(/\{uuidName\}/g, data.data.uuidName));
                }
            });
        }).on('pasteImageError', (ev, data) => {
            toastr.error(data.message, '剪贴板粘贴图片错误!');
        });

        this.initUploadDropzone(this.inputRef, false);
        // this.initUploadDropzone(this.btnItemUploadRef, true);
        this.initUploadDropzone($(this.btnItemUploadRef).children().andSelf(), true);

        _.delay(() => {
            $(this.userListRef).scrollTo(`a.item[data-id="${this.chatTo}"]`);
        }, 1000);

        let tg = timeago();
        this.timeagoTimer = setInterval(() => {
            $(this.chatContainerRef).find('[data-timeago]').each((index, el) => {
                $(el).text(tg.format($(el).attr('data-timeago'), 'zh_CN'));
            });
        }, 5000);

        $(this.chatBtnRef).popup({
            inline: true,
            hoverable: true,
            position: 'bottom left',
            delay: {
                show: 300,
                hide: 300
            }
        });

    }

    /**
     * 当数据绑定引擎从视图解除绑定时被调用
     */
    unbind() {
        clearInterval(this.timeagoTimer);
    }

    initChatToDropdownHandler(last) {
        if (last) {
            _.defer(() => {
                $(this.chatToDropdownRef).dropdown().dropdown('set selected', this.chatTo).dropdown({
                    onChange: (value, text, $choice) => {
                        window.location = wurl('path') + `#/chat-direct/${value}`;
                    }
                });
            });
        }
    }

    sendChatMsgHandler() {
        this.sendChatMsg();
    }
}
