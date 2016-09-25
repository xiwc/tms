define('app',['exports', 'toastr', 'wurl', 'common/common-utils', 'tms-semantic-ui', 'lodash'], function (exports, _toastr, _wurl, _commonUtils) {
    'use strict';

    Object.defineProperty(exports, "__esModule", {
        value: true
    });
    exports.App = undefined;

    var _toastr2 = _interopRequireDefault(_toastr);

    var _wurl2 = _interopRequireDefault(_wurl);

    var _commonUtils2 = _interopRequireDefault(_commonUtils);

    function _interopRequireDefault(obj) {
        return obj && obj.__esModule ? obj : {
            default: obj
        };
    }

    function _classCallCheck(instance, Constructor) {
        if (!(instance instanceof Constructor)) {
            throw new TypeError("Cannot call a class as a function");
        }
    }

    var App = exports.App = function () {
        function App() {
            _classCallCheck(this, App);

            window.toastr = _toastr2.default;
            window.wurl = _wurl2.default;
            window.utils = _commonUtils2.default;
            this.init();
        }

        App.prototype.init = function init() {
            _.extend($.fn.form.settings.prompt, {
                empty: '{name}不能为空',
                checked: '{name}必须被勾选',
                email: '{name}必须是正确的邮件格式',
                url: '{name}必须是正确的URL格式',
                regExp: '{name}验证格式不正确',
                integer: '{name}必须为一个整数',
                decimal: '{name}必须为一个小数',
                number: '{name}必须设置为一个数字',
                is: '{name}必须符合规则"{ruleValue}"',
                isExactly: '{name}必须精确匹配"{ruleValue}"',
                not: '{name}不能设置为"{ruleValue}"',
                notExactly: '{name}不能准确设置为"{ruleValue}"',
                contain: '{name}需要包含"{ruleValue}"',
                containExactly: '{name}需要精确包含"{ruleValue}"',
                doesntContain: '{name}不能包含"{ruleValue}"',
                doesntContainExactly: '{name}不能精确包含"{ruleValue}"',
                minLength: '{name}必须至少包含{ruleValue}个字符',
                length: '{name}必须为{ruleValue}个字符',
                exactLength: '{name}必须为{ruleValue}个字符',
                maxLength: '{name}必须不能超过{ruleValue}个字符',
                match: '{name}必须匹配{ruleValue}字段',
                different: '{name}必须不同于{ruleValue}字段',
                creditCard: '{name}必须是一个正确的信用卡数字格式',
                minCount: '{name}必须至少包含{ruleValue}个选择项',
                exactCount: '{name}必须准确包含{ruleValue}个选择项',
                maxCount: '{name} 必须有{ruleValue}或者更少个选择项'
            });
        };

        App.prototype.configureRouter = function configureRouter(config, router) {

            config.map([{
                route: ['pwd-reset'],
                name: 'reset',
                moduleId: 'user/user-pwd-reset',
                nav: false,
                title: '密码重置'
            }, {
                route: ['register'],
                name: 'register',
                moduleId: 'user/user-register',
                nav: false,
                title: '用户注册'
            }, {
                route: ['chat-direct/:username'],
                name: 'chat-direct',
                moduleId: 'chat/chat-direct',
                nav: false,
                title: '私聊'
            }, {
                route: ['login'],
                name: 'login',
                moduleId: 'user/user-login',
                nav: false,
                title: '登录'
            }, {
                route: '',
                redirect: 'chat-direct/admin'
            }]);

            this.router = router;
        };

        return App;
    }();
});
define('environment',["exports"], function (exports) {
  "use strict";

  Object.defineProperty(exports, "__esModule", {
    value: true
  });
  exports.default = {
    debug: true,
    testing: true
  };
});
define('main',['exports', './environment'], function (exports, _environment) {
  'use strict';

  Object.defineProperty(exports, "__esModule", {
    value: true
  });
  exports.configure = configure;

  var _environment2 = _interopRequireDefault(_environment);

  function _interopRequireDefault(obj) {
    return obj && obj.__esModule ? obj : {
      default: obj
    };
  }

  Promise.config({
    warnings: {
      wForgottenReturn: false
    }
  });

  function configure(aurelia) {
    aurelia.use.standardConfiguration().feature('resources');

    if (_environment2.default.debug) {
      aurelia.use.developmentLogging();
    }

    if (_environment2.default.testing) {
      aurelia.use.plugin('aurelia-testing');
    }

    aurelia.start().then(function () {
      return aurelia.setRoot();
    });
  }
});
define('chat/chat-direct',['exports', 'aurelia-framework', 'common/common-poll', 'marked', 'clipboard', 'dropzone', 'autosize', 'common/common-tips', 'jquery.scrollto', 'common/common-plugin', 'timeago', 'hotkeys', 'textcomplete'], function (exports, _aureliaFramework, _commonPoll, _marked, _clipboard, _dropzone, _autosize, _commonTips) {
    'use strict';

    Object.defineProperty(exports, "__esModule", {
        value: true
    });
    exports.ChatDirect = undefined;

    var _commonPoll2 = _interopRequireDefault(_commonPoll);

    var _marked2 = _interopRequireDefault(_marked);

    var _clipboard2 = _interopRequireDefault(_clipboard);

    var _dropzone2 = _interopRequireDefault(_dropzone);

    var _autosize2 = _interopRequireDefault(_autosize);

    var _commonTips2 = _interopRequireDefault(_commonTips);

    function _interopRequireDefault(obj) {
        return obj && obj.__esModule ? obj : {
            default: obj
        };
    }

    function _initDefineProp(target, property, descriptor, context) {
        if (!descriptor) return;
        Object.defineProperty(target, property, {
            enumerable: descriptor.enumerable,
            configurable: descriptor.configurable,
            writable: descriptor.writable,
            value: descriptor.initializer ? descriptor.initializer.call(context) : void 0
        });
    }

    function _classCallCheck(instance, Constructor) {
        if (!(instance instanceof Constructor)) {
            throw new TypeError("Cannot call a class as a function");
        }
    }

    function _applyDecoratedDescriptor(target, property, decorators, descriptor, context) {
        var desc = {};
        Object['ke' + 'ys'](descriptor).forEach(function (key) {
            desc[key] = descriptor[key];
        });
        desc.enumerable = !!desc.enumerable;
        desc.configurable = !!desc.configurable;

        if ('value' in desc || desc.initializer) {
            desc.writable = true;
        }

        desc = decorators.slice().reverse().reduce(function (desc, decorator) {
            return decorator(target, property, desc) || desc;
        }, desc);

        if (context && desc.initializer !== void 0) {
            desc.value = desc.initializer ? desc.initializer.call(context) : void 0;
            desc.initializer = undefined;
        }

        if (desc.initializer === void 0) {
            Object['define' + 'Property'](target, property, desc);
            desc = null;
        }

        return desc;
    }

    function _initializerWarningHelper(descriptor, context) {
        throw new Error('Decorating class property failed. Please ensure that transform-class-properties is enabled.');
    }

    var _desc, _value, _class, _descriptor;

    var ChatDirect = exports.ChatDirect = (_class = function () {
        function ChatDirect() {
            _classCallCheck(this, ChatDirect);

            _initDefineProp(this, 'content', _descriptor, this);

            this.offset = -70;
            this.selfLink = utils.getBaseUrl() + wurl('path') + '#' + utils.getHash();
            this.first = true;
            this.last = true;
            this.originalHref = wurl();

            _marked2.default.setOptions({
                breaks: true
            });

            _dropzone2.default.autoDiscover = false;

            new _clipboard2.default('.tms-chat-direct .tms-clipboard').on('success', function (e) {
                toastr.success('复制到剪贴板成功!');
            }).on('error', function (e) {
                toastr.error('复制到剪贴板成功!');
            });
        }

        ChatDirect.prototype.convertMd = function convertMd(chats) {
            _.each(chats, function (item) {
                item.contentMd = (0, _marked2.default)(item.content);
            });
            return chats;
        };

        ChatDirect.prototype.activate = function activate(params, routeConfig, navigationInstruction) {

            this.markId = params.id;

            this.user = _.find(this.users, {
                username: params.username
            });

            var name = this.user ? this.user.name : params.username;
            routeConfig.navModel.setTitle(name + ' | 私聊 | TMS');

            this.init(params.username);

            $(this.chatToDropdownRef).dropdown('set selected', this.chatTo);

            if (this.markId) {
                if ('pushState' in history) {
                    history.replaceState(null, '', utils.removeUrlQuery('id'));
                } else {
                    window.location.href = utils.removeUrlQuery('id');
                }
            }
        };

        ChatDirect.prototype.lastMoreHandler = function lastMoreHandler() {
            var _this2 = this;

            var start = _.first(this.chats).id;
            $.get('/admin/chat/direct/more', {
                last: true,
                start: start,
                size: 20,
                chatTo: this.chatTo
            }, function (data) {
                if (data.success) {
                    _this2.chats = _.unionBy(_.reverse(_this2.convertMd(data.data)), _this2.chats);
                    _this2.last = data.msgs[0] - data.data.length <= 0;
                    !_this2.last && (_this2.lastCnt = data.msgs[0] - data.data.length);
                    _.defer(function () {
                        $('html,body').scrollTo('.tms-chat-direct .comments .comment[data-id=' + start + ']', {
                            offset: _this2.offset
                        });
                    });
                } else {
                    toastr.error(data.data, '获取更多消息失败!');
                }
            });
        };

        ChatDirect.prototype.firstMoreHandler = function firstMoreHandler() {
            var _this3 = this;

            var start = _.last(this.chats).id;
            $.get('/admin/chat/direct/more', {
                last: false,
                start: start,
                size: 20,
                chatTo: this.chatTo
            }, function (data) {
                if (data.success) {
                    _this3.chats = _.unionBy(_this3.chats, _this3.convertMd(data.data));
                    _this3.first = data.msgs[0] - data.data.length <= 0;
                    !_this3.first && (_this3.firstCnt = data.msgs[0] - data.data.length);
                    _.defer(function () {
                        $('html,body').scrollTo('.tms-chat-direct .comments .comment[data-id=' + start + ']', {
                            offset: _this3.offset
                        });
                    });
                } else {
                    toastr.error(data.data, '获取更多消息失败!');
                }
            });
        };

        ChatDirect.prototype.init = function init(chatTo) {
            var _this4 = this;

            this.chatTo = chatTo;

            var data = {
                size: 20,
                chatTo: chatTo
            };

            if (this.markId) {
                data.id = this.markId;
            }

            $.get('/admin/chat/direct/list', data, function (data) {
                if (data.success) {
                    _this4.chats = _.reverse(_this4.convertMd(data.data.content));
                    _this4.last = data.data.last;
                    _this4.first = data.data.first;
                    !_this4.last && (_this4.lastCnt = data.data.totalElements - data.data.numberOfElements);
                    !_this4.first && (_this4.firstCnt = data.data.size * data.data.number);

                    _.defer(function () {
                        if (_this4.markId) {
                            $('html,body').scrollTo('.tms-chat-direct .comments .comment[data-id=' + _this4.markId + ']', {
                                offset: _this4.offset
                            });
                        } else {
                            $('html,body').scrollTo('max');
                        }
                    });
                } else {
                    toastr.error(data.data, '获取消息失败!');
                    window.location = utils.getBaseUrl() + wurl('path') + ('#/login?redirect=' + encodeURIComponent(_this4.originalHref));
                }
            }).always(function (xhr, sts, error) {
                if (sts == 'error') {
                    window.location = utils.getBaseUrl() + wurl('path') + ('#/login?redirect=' + encodeURIComponent(_this4.originalHref));
                }
            });
        };

        ChatDirect.prototype.bind = function bind(ctx) {
            var _this5 = this;

            $.get('/admin/user/loginUser', function (data) {
                if (data.success) {
                    _this5.loginUser = data.data;
                } else {
                    toastr.error(data.data, '获取当前登录用户失败!');
                }
            });

            $.get('/admin/user/all', {
                enabled: true
            }, function (data) {
                if (data.success) {
                    _this5.users = data.data;
                    _this5.user = _.find(_this5.users, {
                        username: _this5.chatTo
                    });
                } else {
                    toastr.error(data.data, '获取全部用户失败!');
                }
            });

            _commonPoll2.default.start(function () {

                if (!_this5.chats) {
                    return;
                }

                var lastChat = _.last(_this5.chats);

                $.get('/admin/chat/direct/latest', {
                    id: lastChat ? lastChat.id : 0,
                    chatTo: _this5.chatTo
                }, function (data) {
                    if (data.success) {
                        if (data.data.length == 0) {
                            return;
                        }
                        _this5.chats = _.unionBy(_this5.chats, _this5.convertMd(data.data), 'id');
                        _.defer(function () {
                            $('html,body').scrollTo('max');
                        });
                    } else {
                        toastr.error(data.data, '轮询获取消息失败!');
                    }
                });
            });
        };

        ChatDirect.prototype.unbind = function unbind() {
            _commonPoll2.default.stop();
        };

        ChatDirect.prototype.chatToUserFilerKeyupHanlder = function chatToUserFilerKeyupHanlder(evt) {
            var _this6 = this;

            _.each(this.users, function (item) {
                item.hidden = item.username.indexOf(_this6.filter) == -1;
            });

            if (evt.keyCode === 13) {
                var user = _.find(this.users, {
                    hidden: false
                });

                if (user) {
                    window.location = wurl('path') + ('#/chat-direct/' + user.username);
                }
            }
        };

        ChatDirect.prototype.clearFilterHandler = function clearFilterHandler() {
            var _this7 = this;

            this.filter = '';
            _.each(this.users, function (item) {
                item.hidden = item.username.indexOf(_this7.filter) == -1;
            });
        };

        ChatDirect.prototype.sendChatMsg = function sendChatMsg() {
            var _this8 = this;

            if (this.sending) {
                return;
            }

            this.sending = true;
            this.content = $(this.chatInputRef).val();

            var html = $('<div class="markdown-body"/>').html('<style>.markdown-body{font-size:14px;line-height:1.6}.markdown-body>:first-child{margin-top:0!important}.markdown-body>:last-child{margin-bottom:0!important}.markdown-body a.absent{color:#C00}.markdown-body a.anchor{bottom:0;cursor:pointer;display:block;left:0;margin-left:-30px;padding-left:30px;position:absolute;top:0}.markdown-body h1,.markdown-body h2,.markdown-body h3,.markdown-body h4,.markdown-body h5,.markdown-body h6{cursor:text;font-weight:700;margin:20px 0 10px;padding:0;position:relative}.markdown-body h1 .mini-icon-link,.markdown-body h2 .mini-icon-link,.markdown-body h3 .mini-icon-link,.markdown-body h4 .mini-icon-link,.markdown-body h5 .mini-icon-link,.markdown-body h6 .mini-icon-link{color:#000;display:none}.markdown-body h1:hover a.anchor,.markdown-body h2:hover a.anchor,.markdown-body h3:hover a.anchor,.markdown-body h4:hover a.anchor,.markdown-body h5:hover a.anchor,.markdown-body h6:hover a.anchor{line-height:1;margin-left:-22px;padding-left:0;text-decoration:none;top:15%}.markdown-body h1:hover a.anchor .mini-icon-link,.markdown-body h2:hover a.anchor .mini-icon-link,.markdown-body h3:hover a.anchor .mini-icon-link,.markdown-body h4:hover a.anchor .mini-icon-link,.markdown-body h5:hover a.anchor .mini-icon-link,.markdown-body h6:hover a.anchor .mini-icon-link{display:inline-block}.markdown-body hr:after,.markdown-body hr:before{display:table;content:""}.markdown-body h1 code,.markdown-body h1 tt,.markdown-body h2 code,.markdown-body h2 tt,.markdown-body h3 code,.markdown-body h3 tt,.markdown-body h4 code,.markdown-body h4 tt,.markdown-body h5 code,.markdown-body h5 tt,.markdown-body h6 code,.markdown-body h6 tt{font-size:inherit}.markdown-body h1{color:#000;font-size:28px}.markdown-body h2{border-bottom:1px solid #CCC;color:#000;font-size:24px}.markdown-body h3{font-size:18px}.markdown-body h4{font-size:16px}.markdown-body h5{font-size:14px}.markdown-body h6{color:#777;font-size:14px}.markdown-body blockquote,.markdown-body dl,.markdown-body ol,.markdown-body p,.markdown-body pre,.markdown-body table,.markdown-body ul{margin:15px 0}.markdown-body hr{overflow:hidden;background:#e7e7e7;height:4px;padding:0;margin:16px 0;border:0;-moz-box-sizing:content-box;box-sizing:content-box}.markdown-body h1+p,.markdown-body h2+p,.markdown-body h3+p,.markdown-body h4+p,.markdown-body h5+p,.markdown-body h6+p,.markdown-body ol li>:first-child,.markdown-body ul li>:first-child{margin-top:0}.markdown-body hr:after{clear:both}.markdown-body a:first-child h1,.markdown-body a:first-child h2,.markdown-body a:first-child h3,.markdown-body a:first-child h4,.markdown-body a:first-child h5,.markdown-body a:first-child h6,.markdown-body>h1:first-child,.markdown-body>h1:first-child+h2,.markdown-body>h2:first-child,.markdown-body>h3:first-child,.markdown-body>h4:first-child,.markdown-body>h5:first-child,.markdown-body>h6:first-child{margin-top:0;padding-top:0}.markdown-body li p.first{display:inline-block}.markdown-body ol,.markdown-body ul{padding-left:30px}.markdown-body ol.no-list,.markdown-body ul.no-list{list-style-type:none;padding:0}.markdown-body ol ol,.markdown-body ol ul,.markdown-body ul ol,.markdown-body ul ul{margin-bottom:0}.markdown-body dl{padding:0}.markdown-body dl dt{font-size:14px;font-style:italic;font-weight:700;margin:15px 0 5px;padding:0}.markdown-body dl dt:first-child{padding:0}.markdown-body dl dt>:first-child{margin-top:0}.markdown-body dl dt>:last-child{margin-bottom:0}.markdown-body dl dd{margin:0 0 15px;padding:0 15px}.markdown-body blockquote>:first-child,.markdown-body dl dd>:first-child{margin-top:0}.markdown-body blockquote>:last-child,.markdown-body dl dd>:last-child{margin-bottom:0}.markdown-body blockquote{border-left:4px solid #DDD;color:#777;padding:0 15px}.markdown-body table th{font-weight:700}.markdown-body table td,.markdown-body table th{border:1px solid #CCC;padding:6px 13px}.markdown-body table tr{background-color:#FFF;border-top:1px solid #CCC}.markdown-body table tr:nth-child(2n){background-color:#F8F8F8}.markdown-body img{max-width:100%}.markdown-body span.frame{display:block;overflow:hidden}.markdown-body span.frame>span{border:1px solid #DDD;display:block;float:left;margin:13px 0 0;overflow:hidden;padding:7px;width:auto}.markdown-body span.frame span img{display:block;float:left}.markdown-body span.frame span span{clear:both;color:#333;display:block;padding:5px 0 0}.markdown-body span.align-center{clear:both;display:block;overflow:hidden}.markdown-body span.align-center>span{display:block;margin:13px auto 0;overflow:hidden;text-align:center}.markdown-body span.align-center span img{margin:0 auto;text-align:center}.markdown-body span.align-right{clear:both;display:block;overflow:hidden}.markdown-body span.align-right>span{display:block;margin:13px 0 0;overflow:hidden;text-align:right}.markdown-body span.align-right span img{margin:0;text-align:right}.markdown-body span.float-left{display:block;float:left;margin-right:13px;overflow:hidden}.markdown-body span.float-left span{margin:13px 0 0}.markdown-body span.float-right{display:block;float:right;margin-left:13px;overflow:hidden}.markdown-body span.float-right>span{display:block;margin:13px auto 0;overflow:hidden;text-align:right}.markdown-body code,.markdown-body tt{background-color:#F8F8F8;border:1px solid #EAEAEA;border-radius:3px;margin:0 2px;padding:0 5px;white-space:nowrap}.markdown-body pre>code{background:none;border:none;margin:0;padding:0;white-space:pre}.markdown-body .highlight pre,.markdown-body pre{background-color:#F8F8F8;border:1px solid #CCC;border-radius:3px;font-size:13px;line-height:19px;overflow:auto;padding:6px 10px}.markdown-body pre code,.markdown-body pre tt{background-color:transparent;border:none}</style>' + (0, _marked2.default)(this.content)).wrap('<div/>').parent().html();

            $.post('/admin/chat/direct/create', {
                baseUrl: utils.getBaseUrl(),
                path: wurl('path'),
                chatTo: this.chatTo,
                content: this.content,
                contentHtml: html
            }, function (data, textStatus, xhr) {
                if (data.success) {
                    $(_this8.chatInputRef).val('');
                    _commonPoll2.default.reset();
                    $(_this8.chatInputRef).css('height', 'auto');
                } else {
                    toastr.error(data.data, '发送消息失败!');
                }
            }).always(function () {
                _this8.sending = false;
            });
        };

        ChatDirect.prototype.sendKeydownHandler = function sendKeydownHandler(evt, chatInputRef) {

            if (!evt.shiftKey && !evt.ctrlKey && !evt.altKey && evt.keyCode === 13) {
                if ($('.textcomplete-dropdown:visible').size() == 1) {
                    return false;
                }
                if (!$.trim($(this.chatInputRef).val())) {
                    $(this.chatInputRef).val('');
                    return;
                }
                this.sendChatMsg();
                return false;
            } else if (evt.shiftKey && evt.keyCode === 13) {
                _autosize2.default.update(chatInputRef);
            } else if ((evt.ctrlKey || evt.altKey) && evt.keyCode === 13) {
                this.insertTxt($(this.chatInputRef), '\n');
                _autosize2.default.update(chatInputRef);
            } else if (evt.keyCode === 27) {
                $(this.chatInputRef).val('');
            }

            return true;
        };

        ChatDirect.prototype.insertTxt = function insertTxt($t, txt) {
            $t.insertAtCaret(txt);
        };

        ChatDirect.prototype.deleteHandler = function deleteHandler(item) {
            var _this9 = this;

            this.emConfirmModal.show({
                onapprove: function onapprove() {
                    $.post('/admin/chat/direct/delete', {
                        id: item.id
                    }, function (data, textStatus, xhr) {
                        if (data.success) {
                            _this9.chats = _.reject(_this9.chats, {
                                id: item.id
                            });
                            toastr.success('删除消息成功!');
                        } else {
                            toastr.error(data.data, '删除消息失败!');
                        }
                    });
                }
            });
        };

        ChatDirect.prototype.editHandler = function editHandler(item, editTxtRef) {
            item.isEditing = true;
            _.defer(function () {
                $(editTxtRef).focus().select();
                _autosize2.default.update(editTxtRef);
            });
        };

        ChatDirect.prototype.eidtKeydownHandler = function eidtKeydownHandler(evt, item, txtRef) {
            var _this10 = this;

            if (this.sending) {
                return false;
            }

            if (!evt.ctrlKey && evt.keyCode === 13) {

                this.sending = true;

                item.content = $(txtRef).val();

                var html = $('<div class="markdown-body"/>').html('<style>.markdown-body{font-size:14px;line-height:1.6}.markdown-body>:first-child{margin-top:0!important}.markdown-body>:last-child{margin-bottom:0!important}.markdown-body a.absent{color:#C00}.markdown-body a.anchor{bottom:0;cursor:pointer;display:block;left:0;margin-left:-30px;padding-left:30px;position:absolute;top:0}.markdown-body h1,.markdown-body h2,.markdown-body h3,.markdown-body h4,.markdown-body h5,.markdown-body h6{cursor:text;font-weight:700;margin:20px 0 10px;padding:0;position:relative}.markdown-body h1 .mini-icon-link,.markdown-body h2 .mini-icon-link,.markdown-body h3 .mini-icon-link,.markdown-body h4 .mini-icon-link,.markdown-body h5 .mini-icon-link,.markdown-body h6 .mini-icon-link{color:#000;display:none}.markdown-body h1:hover a.anchor,.markdown-body h2:hover a.anchor,.markdown-body h3:hover a.anchor,.markdown-body h4:hover a.anchor,.markdown-body h5:hover a.anchor,.markdown-body h6:hover a.anchor{line-height:1;margin-left:-22px;padding-left:0;text-decoration:none;top:15%}.markdown-body h1:hover a.anchor .mini-icon-link,.markdown-body h2:hover a.anchor .mini-icon-link,.markdown-body h3:hover a.anchor .mini-icon-link,.markdown-body h4:hover a.anchor .mini-icon-link,.markdown-body h5:hover a.anchor .mini-icon-link,.markdown-body h6:hover a.anchor .mini-icon-link{display:inline-block}.markdown-body hr:after,.markdown-body hr:before{display:table;content:""}.markdown-body h1 code,.markdown-body h1 tt,.markdown-body h2 code,.markdown-body h2 tt,.markdown-body h3 code,.markdown-body h3 tt,.markdown-body h4 code,.markdown-body h4 tt,.markdown-body h5 code,.markdown-body h5 tt,.markdown-body h6 code,.markdown-body h6 tt{font-size:inherit}.markdown-body h1{color:#000;font-size:28px}.markdown-body h2{border-bottom:1px solid #CCC;color:#000;font-size:24px}.markdown-body h3{font-size:18px}.markdown-body h4{font-size:16px}.markdown-body h5{font-size:14px}.markdown-body h6{color:#777;font-size:14px}.markdown-body blockquote,.markdown-body dl,.markdown-body ol,.markdown-body p,.markdown-body pre,.markdown-body table,.markdown-body ul{margin:15px 0}.markdown-body hr{overflow:hidden;background:#e7e7e7;height:4px;padding:0;margin:16px 0;border:0;-moz-box-sizing:content-box;box-sizing:content-box}.markdown-body h1+p,.markdown-body h2+p,.markdown-body h3+p,.markdown-body h4+p,.markdown-body h5+p,.markdown-body h6+p,.markdown-body ol li>:first-child,.markdown-body ul li>:first-child{margin-top:0}.markdown-body hr:after{clear:both}.markdown-body a:first-child h1,.markdown-body a:first-child h2,.markdown-body a:first-child h3,.markdown-body a:first-child h4,.markdown-body a:first-child h5,.markdown-body a:first-child h6,.markdown-body>h1:first-child,.markdown-body>h1:first-child+h2,.markdown-body>h2:first-child,.markdown-body>h3:first-child,.markdown-body>h4:first-child,.markdown-body>h5:first-child,.markdown-body>h6:first-child{margin-top:0;padding-top:0}.markdown-body li p.first{display:inline-block}.markdown-body ol,.markdown-body ul{padding-left:30px}.markdown-body ol.no-list,.markdown-body ul.no-list{list-style-type:none;padding:0}.markdown-body ol ol,.markdown-body ol ul,.markdown-body ul ol,.markdown-body ul ul{margin-bottom:0}.markdown-body dl{padding:0}.markdown-body dl dt{font-size:14px;font-style:italic;font-weight:700;margin:15px 0 5px;padding:0}.markdown-body dl dt:first-child{padding:0}.markdown-body dl dt>:first-child{margin-top:0}.markdown-body dl dt>:last-child{margin-bottom:0}.markdown-body dl dd{margin:0 0 15px;padding:0 15px}.markdown-body blockquote>:first-child,.markdown-body dl dd>:first-child{margin-top:0}.markdown-body blockquote>:last-child,.markdown-body dl dd>:last-child{margin-bottom:0}.markdown-body blockquote{border-left:4px solid #DDD;color:#777;padding:0 15px}.markdown-body table th{font-weight:700}.markdown-body table td,.markdown-body table th{border:1px solid #CCC;padding:6px 13px}.markdown-body table tr{background-color:#FFF;border-top:1px solid #CCC}.markdown-body table tr:nth-child(2n){background-color:#F8F8F8}.markdown-body img{max-width:100%}.markdown-body span.frame{display:block;overflow:hidden}.markdown-body span.frame>span{border:1px solid #DDD;display:block;float:left;margin:13px 0 0;overflow:hidden;padding:7px;width:auto}.markdown-body span.frame span img{display:block;float:left}.markdown-body span.frame span span{clear:both;color:#333;display:block;padding:5px 0 0}.markdown-body span.align-center{clear:both;display:block;overflow:hidden}.markdown-body span.align-center>span{display:block;margin:13px auto 0;overflow:hidden;text-align:center}.markdown-body span.align-center span img{margin:0 auto;text-align:center}.markdown-body span.align-right{clear:both;display:block;overflow:hidden}.markdown-body span.align-right>span{display:block;margin:13px 0 0;overflow:hidden;text-align:right}.markdown-body span.align-right span img{margin:0;text-align:right}.markdown-body span.float-left{display:block;float:left;margin-right:13px;overflow:hidden}.markdown-body span.float-left span{margin:13px 0 0}.markdown-body span.float-right{display:block;float:right;margin-left:13px;overflow:hidden}.markdown-body span.float-right>span{display:block;margin:13px auto 0;overflow:hidden;text-align:right}.markdown-body code,.markdown-body tt{background-color:#F8F8F8;border:1px solid #EAEAEA;border-radius:3px;margin:0 2px;padding:0 5px;white-space:nowrap}.markdown-body pre>code{background:none;border:none;margin:0;padding:0;white-space:pre}.markdown-body .highlight pre,.markdown-body pre{background-color:#F8F8F8;border:1px solid #CCC;border-radius:3px;font-size:13px;line-height:19px;overflow:auto;padding:6px 10px}.markdown-body pre code,.markdown-body pre tt{background-color:transparent;border:none}</style>' + (0, _marked2.default)(item.content)).wrap('<div/>').parent().html();

                $.post('/admin/chat/direct/update', {
                    baseUrl: utils.getBaseUrl(),
                    path: wurl('path'),
                    id: item.id,
                    content: item.content,
                    contentHtml: html
                }, function (data, textStatus, xhr) {
                    if (data.success) {
                        toastr.success('更新消息成功!');
                        item.contentMd = (0, _marked2.default)(item.content);
                        item.isEditing = false;
                    } else {
                        toastr.error(data.data, '更新消息失败!');
                    }
                }).always(function () {
                    _this10.sending = false;
                });

                return false;
            } else if (evt.ctrlKey && evt.keyCode === 13) {
                var $t = $(evt.target);
                $t.insertAtCaret('\n');
                _autosize2.default.update(txtRef);
                item.content = $t.val();
            }

            return true;
        };

        ChatDirect.prototype.focusoutHandler = function focusoutHandler(item) {
            item.isEditing = false;
        };

        ChatDirect.prototype.chatToUserFilerFocusinHanlder = function chatToUserFilerFocusinHanlder() {
            $(this.userListRef).scrollTo('a.item[data-id="' + this.chatTo + '"]');
        };

        ChatDirect.prototype.initUploadDropzone = function initUploadDropzone(domRef, getInputTargetCb, clickable) {

            var _this = this;

            $(domRef).dropzone({
                url: "/admin/file/upload",
                paramName: 'file',
                clickable: !!clickable,
                dictDefaultMessage: '',
                maxFilesize: 10,
                addRemoveLinks: true,
                previewsContainer: this.uploadProgressRef,
                previewTemplate: this.previewTemplateRef.innerHTML,
                dictCancelUpload: '取消上传',
                dictCancelUploadConfirmation: '确定要取消上传吗?',
                dictFileTooBig: '文件过大({{filesize}}M),最大限制:{{maxFilesize}}M',
                init: function init() {
                    this.on("sending", function (file, xhr, formData) {
                        if (!getInputTargetCb()) {
                            this.removeAllFiles(true);
                        }
                    });
                    this.on("success", function (file, data) {
                        if (data.success) {

                            $.each(data.data, function (index, item) {
                                if (item.type == 'Image') {
                                    _this.insertTxt($(getInputTargetCb()), '![{name}]({baseURL}{path}{uuidName}) '.replace(/\{name\}/g, item.name).replace(/\{baseURL\}/g, utils.getBaseUrl() + '/').replace(/\{path\}/g, item.path).replace(/\{uuidName\}/g, item.uuidName));
                                } else {
                                    _this.insertTxt($(getInputTargetCb()), '[{name}]({baseURL}{path}{uuidName}) '.replace(/\{name\}/g, item.name).replace(/\{baseURL\}/g, utils.getBaseUrl() + '/').replace(/\{path\}/g, "admin/file/download/").replace(/\{uuidName\}/g, item.id));
                                }
                            });
                            toastr.success('上传成功!');
                        } else {
                            toastr.error(data.data, '上传失败!');
                        }
                    });
                    this.on("error", function (file, errorMessage, xhr) {
                        toastr.error(errorMessage, '上传失败!');
                    });
                    this.on("complete", function (file) {
                        this.removeFile(file);
                    });
                }
            });
        };

        ChatDirect.prototype.attached = function attached() {
            var _this11 = this;

            this.initUploadDropzone(this.inputRef, function () {
                return _this11.chatInputRef;
            }, false);
            this.initUploadDropzone($(this.btnItemUploadRef).children().andSelf(), function () {
                return _this11.chatInputRef;
            }, true);


            _.delay(function () {
                $(_this11.userListRef).scrollTo('a.item[data-id="' + _this11.chatTo + '"]');
            }, 1000);

            var tg = timeago();
            this.timeagoTimer = setInterval(function () {
                $(_this11.chatContainerRef).find('[data-timeago]').each(function (index, el) {
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

            this.initHotkeys();
            this.initTextcomplete();
        };

        ChatDirect.prototype.initTextcomplete = function initTextcomplete() {
            var _this12 = this;

            $(this.chatInputRef).textcomplete([{
                match: /(|\b)(\/.*)$/,
                search: function search(term, callback) {
                    var keys = _.keys(_commonTips2.default);
                    callback($.map(keys, function (key) {
                        return key.indexOf(term) === 0 ? key : null;
                    }));
                },
                template: function template(value, term) {
                    return _commonTips2.default[value].label;
                },
                replace: function replace(value) {
                    _.defer(function () {
                        _autosize2.default.update(_this12.chatInputRef);
                    }, 3000);
                    _this12.tipsActionHandler(value);
                    return _commonTips2.default[value].value;
                }
            }], {
                appendTo: '.tms-upload-progress',
                maxCount: 20
            });
        };

        ChatDirect.prototype.tipsActionHandler = function tipsActionHandler(value) {
            if (value == '/upload') {
                $(this.btnItemUploadRef).find('.content').click();
            } else if (value == '/shortcuts') {
                this.emHotkeysModal.show();
            }
        };

        ChatDirect.prototype.initHotkeys = function initHotkeys() {
            var _this13 = this;

            $(document).bind('keydown', 'ctrl+u', function (evt) {
                evt.preventDefault();
                $(_this13.btnItemUploadRef).find('.content').click();
            }).bind('keydown', 'ctrl+/', function (evt) {
                evt.preventDefault();
                _this13.emHotkeysModal.show();
            });

            $(this.chatInputRef).bind('keydown', 'ctrl+u', function (evt) {
                evt.preventDefault();
                $(_this13.btnItemUploadRef).find('.content').click();
            });
        };

        ChatDirect.prototype.unbind = function unbind() {
            clearInterval(this.timeagoTimer);
        };

        ChatDirect.prototype.initChatToDropdownHandler = function initChatToDropdownHandler(last) {
            var _this14 = this;

            if (last) {
                _.defer(function () {
                    $(_this14.chatToDropdownRef).dropdown().dropdown('set selected', _this14.chatTo).dropdown({
                        onChange: function onChange(value, text, $choice) {
                            window.location = wurl('path') + ('#/chat-direct/' + value);
                        }
                    });
                });
            }
        };

        ChatDirect.prototype.sendChatMsgHandler = function sendChatMsgHandler() {
            this.sendChatMsg();
        };

        return ChatDirect;
    }(), (_descriptor = _applyDecoratedDescriptor(_class.prototype, 'content', [_aureliaFramework.bindable], {
        enumerable: true,
        initializer: function initializer() {
            return '';
        }
    })), _class);
});
define('common/common-plugin',[], function () {
    'use strict';

    (function ($) {
        $.fn.extend({
            insertAtCaret: function insertAtCaret(myValue) {
                var $t = $(this)[0];
                if (document.selection) {
                    this.focus();
                    sel = document.selection.createRange();
                    sel.text = myValue;
                    this.focus();
                } else if ($t.selectionStart || $t.selectionStart == '0') {
                    var startPos = $t.selectionStart;
                    var endPos = $t.selectionEnd;
                    var scrollTop = $t.scrollTop;
                    $t.value = $t.value.substring(0, startPos) + myValue + $t.value.substring(endPos, $t.value.length);
                    this.focus();
                    $t.selectionStart = startPos + myValue.length;
                    $t.selectionEnd = startPos + myValue.length;
                    $t.scrollTop = scrollTop;
                } else {
                    this.value += myValue;
                    this.focus();
                }
            }
        });
    })(jQuery);
});
define('common/common-poll',['exports'], function (exports) {
    'use strict';

    Object.defineProperty(exports, "__esModule", {
        value: true
    });

    var minInterval = 6000;
    var maxInterval = 300000;
    var incInterval = 6000;

    var tolerate = 10;

    var timer = null;

    var inc = 0;

    var interval = minInterval;

    var _pollCb = null;
    var _errCb = null;
    var _isPause = false;

    function oneHandler() {

        if (_isPause) {
            return;
        }

        try {
            _pollCb && _pollCb(_reset, _stop);
        } catch (e) {
            _errCb && _errCb(_reset, _stop, e);

            console.log('轮询异常: ' + e);
        }
    }

    function _start() {
        console.log('poll start...');

        _isPause = false;

        oneHandler();
        timer = setInterval(function () {
            inc++;
            oneHandler();


            if (inc > tolerate) {

                interval = minInterval + incInterval * (inc - tolerate);

                if (interval <= maxInterval) {
                    clearInterval(timer);
                    _start();
                }
            }
        }, interval);
    }

    function _stop() {
        console.log("poll stop...");

        inc = 0;
        interval = minInterval;
        _isPause = false;
        clearInterval(timer);
        timer = null;
    }

    function _reset() {
        console.log("poll reset...");

        _stop();
        _start();
    }

    function _pause() {
        console.log("pause reset...");
        _isPause = true;
    }

    exports.default = {
        start: function start(pollCb, errCb) {
            if (timer) {
                _stop();
            }
            _pollCb = pollCb;
            _errCb = errCb;
            _start();
        },
        reset: function reset() {
            _reset();
        },
        stop: function stop() {
            _stop();
        },
        pause: function pause() {
            _pause();
        }
    };
});
define('common/common-tips',['exports'], function (exports) {
    'use strict';

    Object.defineProperty(exports, "__esModule", {
        value: true
    });
    exports.default = {

        '/h1': {
            label: '/h1 [标题1]',
            value: '# '
        },
        '/h2': {
            label: '/h2 [标题2]',
            value: '## '
        },
        '/h3': {
            label: '/h3 [标题3]',
            value: '### '
        },
        '/h4': {
            label: '/h4 [标题4]',
            value: '#### '
        },
        '/h5': {
            label: '/h5 [标题5]',
            value: '##### '
        },
        '/h6': {
            label: '/h6 [标题6]',
            value: '###### '
        },
        '/b': {
            label: '/b [粗体]',
            value: '****'
        },
        '/i': {
            label: '/i [斜体]',
            value: '**'
        },
        '/s': {
            label: '/s [删除线]',
            value: '~~~~'
        },
        '/code': {
            label: '/code [代码]',
            value: '```\n\n```\n'
        },
        '/quote': {
            label: '/quote [引用]',
            value: '> '
        },
        '/list': {
            label: '/list [列表]',
            value: '* '
        },
        '/href': {
            label: '/href [链接]',
            value: '[](http://)'
        },
        '/img': {
            label: '/img [图片]',
            value: '![](http://)'
        },
        '/table': {
            label: '/table [表格]',
            value: '| 列1 | 列2 | 列3 |\n| ------ | ------ | ------ |\n| 文本 | 文本 | 文本 |\n'
        },
        '/hr': {
            label: '/hr [分隔线]',
            value: '\n-----\n'
        },
        '/upload': {
            label: '/upload [上传文件]',
            value: ''
        },
        '/shortcuts': {
            label: '/shortcuts [热键]',
            value: ''
        }
    };
});
define('common/common-utils',['exports'], function (exports) {
    'use strict';

    Object.defineProperty(exports, "__esModule", {
        value: true
    });

    function _classCallCheck(instance, Constructor) {
        if (!(instance instanceof Constructor)) {
            throw new TypeError("Cannot call a class as a function");
        }
    }

    var CommonUtils = exports.CommonUtils = function () {
        function CommonUtils() {
            _classCallCheck(this, CommonUtils);
        }

        CommonUtils.prototype.getBaseUrl = function getBaseUrl() {
            if (typeof wurl == 'function') {
                if (wurl('port') == 80 || wurl('port') == 443) {
                    return wurl('protocol') + '://' + wurl('hostname');
                } else {
                    return wurl('protocol') + '://' + wurl('hostname') + ':' + wurl('port');
                }
            }
            return '';
        };

        CommonUtils.prototype.getHash = function getHash() {
            var hash = wurl('hash');
            var index = hash.indexOf('?');
            if (index != -1) {
                return hash.substring(0, index);
            }

            return hash;
        };

        CommonUtils.prototype.urlQuery = function urlQuery(name) {
            return wurl('?' + name) || wurl('?' + name, wurl('hash'));
        };

        CommonUtils.prototype.removeUrlQuery = function removeUrlQuery(name, href) {

            var s = href ? href : window.location.href;

            var rs = new RegExp('(&|\\?)?' + name + '=?[^&#]*(.)?', 'g').exec(s);


            if (rs) {
                if (rs[1] == '&') {
                    return s.replace(new RegExp('&' + name + '=?[^&#]+', 'g'), '');
                } else if (rs[1] == '?') {
                    if (rs[2] != '&') {
                        return s.replace(new RegExp('\\?' + name + '=?[^&#]*', 'g'), '');
                    } else {
                        return s.replace(new RegExp('' + name + '=?[^&#]*&', 'g'), '');
                    }
                }
            }

            return s;
        };

        return CommonUtils;
    }();

    exports.default = new CommonUtils();
});
define('resources/config',['exports', 'aurelia-fetch-client', 'aurelia-framework', 'toastr', 'nprogress', 'isomorphic-fetch'], function (exports, _aureliaFetchClient, _aureliaFramework, _toastr, _nprogress) {
    'use strict';

    Object.defineProperty(exports, "__esModule", {
        value: true
    });
    exports.Config = undefined;

    var _toastr2 = _interopRequireDefault(_toastr);

    var _nprogress2 = _interopRequireDefault(_nprogress);

    function _interopRequireDefault(obj) {
        return obj && obj.__esModule ? obj : {
            default: obj
        };
    }

    function _classCallCheck(instance, Constructor) {
        if (!(instance instanceof Constructor)) {
            throw new TypeError("Cannot call a class as a function");
        }
    }

    var Config = exports.Config = function () {
        function Config() {
            _classCallCheck(this, Config);
        }

        Config.prototype.initHttp = function initHttp() {
            window.json = function (param) {
                console.log(JSON.stringify(param));
                return (0, _aureliaFetchClient.json)(param);
            };
            window.http = this.aurelia.container.root.get(_aureliaFetchClient.HttpClient);
            http.configure(function (config) {
                config.withDefaults({
                    credentials: 'same-origin',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json',
                        'X-Requested-With': 'fetch'
                    }
                }).withInterceptor({
                    request: function request(req) {
                        _nprogress2.default && _nprogress2.default.start();
                        return req;
                    },
                    requestError: function requestError(req) {
                        console.log(req);
                    },
                    response: function response(resp) {
                        _nprogress2.default && _nprogress2.default.done();
                        if (!resp.ok) {
                            resp.json().then(function (data) {
                                _toastr2.default.error(data.message);
                            });

                            if (resp.status == 401) {
                                _toastr2.default.error('用户未登录!');
                                return;
                            }
                        }

                        return resp;
                    },
                    responseError: function responseError(resp) {
                        _toastr2.default.error(resp.message, '网络请求错误!');
                        console.log(resp);
                    }
                });
            });

            return this;
        };

        Config.prototype.initToastr = function initToastr() {
            _toastr2.default.options.positionClass = 'toast-bottom-center';
            _toastr2.default.options.preventDuplicates = true;

            return this;
        };

        Config.prototype.initAjax = function initAjax() {

            $(document).on('ajaxStart', function () {
                _nprogress2.default && _nprogress2.default.start();
            });
            $(document).on('ajaxStop', function () {
                _nprogress2.default && _nprogress2.default.done();
            });


            return this;
        };

        Config.prototype.context = function context(aurelia) {
            this.aurelia = aurelia;
            return this;
        };

        return Config;
    }();

    exports.default = new Config();
});
define('resources/index',['exports', './config'], function (exports, _config) {
    'use strict';

    Object.defineProperty(exports, "__esModule", {
        value: true
    });
    exports.configure = configure;

    var _config2 = _interopRequireDefault(_config);

    function _interopRequireDefault(obj) {
        return obj && obj.__esModule ? obj : {
            default: obj
        };
    }

    function configure(aurelia) {

        _config2.default.context(aurelia).initHttp().initToastr().initAjax();

        aurelia.globalResources(['resources/value-converters/vc-common', 'resources/attributes/attr-task', 'resources/attributes/attr-swipebox', 'resources/attributes/attr-pastable', 'resources/attributes/attr-autosize']);
    }
});
define('user/user-login',['exports'], function (exports) {
    'use strict';

    Object.defineProperty(exports, "__esModule", {
        value: true
    });

    function _classCallCheck(instance, Constructor) {
        if (!(instance instanceof Constructor)) {
            throw new TypeError("Cannot call a class as a function");
        }
    }

    var UserLogin = exports.UserLogin = function () {
        function UserLogin() {
            _classCallCheck(this, UserLogin);

            this.username = '';
            this.password = '';
        }

        UserLogin.prototype.attached = function attached() {
            $(this.rememberMeRef).checkbox();
        };

        UserLogin.prototype.loginHandler = function loginHandler() {
            var _this = this;

            $.get('/admin/login', function (data) {

                var rm = $(_this.rememberMeRef).checkbox('is checked') ? 'on' : '';

                $.post('/admin/signin', {
                    username: _this.username,
                    password: _this.password,
                    "remember-me": rm
                }).always(function () {
                    var redirect = utils.urlQuery('redirect');
                    if (redirect) {
                        window.location = decodeURIComponent(redirect);
                    } else {
                        window.location = wurl('path');
                    }
                });
            });
        };

        return UserLogin;
    }();
});
define('user/user-pwd-reset',['exports'], function (exports) {
    'use strict';

    Object.defineProperty(exports, "__esModule", {
        value: true
    });

    function _classCallCheck(instance, Constructor) {
        if (!(instance instanceof Constructor)) {
            throw new TypeError("Cannot call a class as a function");
        }
    }

    var UserPwdReset = exports.UserPwdReset = function () {
        function UserPwdReset() {
            _classCallCheck(this, UserPwdReset);

            this.mail = '';
            this.pwd = '';
            this.isReq = false;
            this.token = utils.urlQuery('id');
        }

        UserPwdReset.prototype.resetPwdHandler = function resetPwdHandler() {
            var _this = this;

            if (!$(this.fm).form('is valid')) {
                toastr.error('邮件地址输入不合法!');
                return;
            }

            this.isReq = true;
            http.fetch('/free/user/pwd/reset', {
                method: 'post',
                body: json({
                    mail: this.mail,
                    baseUrl: utils.getBaseUrl(),
                    path: wurl('path')
                })
            }).then(function (resp) {
                if (resp.ok) {
                    resp.json().then(function (data) {
                        if (data.success) {
                            toastr.success('重置密码邮件链接发送成功!');
                            _.delay(function () {
                                window.location = "/admin/login";
                            }, 2000);
                        } else {
                            toastr.error(data.data, '重置密码邮件链接发送失败!');
                            _this.isReq = false;
                        }
                    });
                }
            });
        };

        UserPwdReset.prototype.newPwdHandler = function newPwdHandler() {
            var _this2 = this;

            if (!$(this.fm2).form('is valid')) {
                toastr.error('新密码输入不合法!');
                return;
            }

            this.isReq = true;
            http.fetch('/free/user/pwd/new', {
                method: 'post',
                body: json({
                    token: this.token,
                    pwd: this.pwd
                })
            }).then(function (resp) {
                if (resp.ok) {
                    resp.json().then(function (data) {
                        if (data.success) {
                            toastr.success('重置密码成功!');
                            _.delay(function () {
                                window.location = "/admin/login";
                            }, 2000);
                        } else {
                            toastr.error(data.data, '重置密码失败!');
                            _this2.isReq = false;
                        }
                    });
                }
            });
        };

        UserPwdReset.prototype.attached = function attached() {

            $(this.fm).form({
                on: 'blur',
                inline: true,
                fields: {
                    mail: ['empty', 'email']
                }
            });

            $(this.fm2).form({
                on: 'blur',
                inline: true,
                fields: {
                    mail: ['empty', 'minLength[8]']
                }
            });
        };

        return UserPwdReset;
    }();
});
define('user/user-register',['exports'], function (exports) {
    'use strict';

    Object.defineProperty(exports, "__esModule", {
        value: true
    });

    function _classCallCheck(instance, Constructor) {
        if (!(instance instanceof Constructor)) {
            throw new TypeError("Cannot call a class as a function");
        }
    }

    var ViewModel = exports.ViewModel = function () {
        function ViewModel() {
            _classCallCheck(this, ViewModel);

            this.header = '账户激活页面';
        }

        ViewModel.prototype.activate = function activate(params, routeConfig, navigationInstruction) {
            var _this = this;

            if (params.id) {
                this.token = params.id;

                this.isReq = true;
                this.header = '账户激活中,请稍后...!';
                http.fetch('/free/user/register/activate', {
                    method: 'post',
                    body: json({
                        token: this.token
                    })
                }).then(function (resp) {
                    if (resp.ok) {
                        resp.json().then(function (data) {
                            if (data.success) {
                                _this.header = '账户激活成功,请返回登录页面登录!';
                            } else {
                                _this.header = '账户激活失败!';
                                toastr.error(data.data, '账户激活失败!');
                            }
                        });
                        _this.isReq = false;
                    }
                });
            }
        };

        ViewModel.prototype.attached = function attached() {

            $(this.fm).form({
                on: 'blur',
                inline: true,
                fields: {
                    username: {
                        identifier: 'username',
                        rules: [{
                            type: 'empty'
                        }, {
                            type: 'minLength[3]'
                        }, {
                            type: 'regExp',
                            value: /^[a-z]+[a-z0-9\.\-_]*[a-z0-9]+$/,
                            prompt: '小写字母数字.-_组合,字母开头,字母数字结尾'
                        }]
                    },
                    pwd: {
                        identifier: 'pwd',
                        rules: [{
                            type: 'empty'
                        }, {
                            type: 'minLength[8]'
                        }]
                    },
                    name: {
                        identifier: 'name',
                        rules: [{
                            type: 'empty'
                        }, {
                            type: 'maxLength[20]'
                        }]
                    },
                    mail: {
                        identifier: 'mail',
                        rules: [{
                            type: 'empty'
                        }, {
                            type: 'email'
                        }]
                    }
                }
            });
        };

        ViewModel.prototype.okHandler = function okHandler() {
            var _this2 = this;

            if (!$(this.fm).form('is valid')) {
                toastr.error('账户注册信息输入不合法!');
                return;
            }

            this.isReq = true;
            http.fetch('/free/user/register', {
                method: 'post',
                body: json({
                    username: this.username,
                    pwd: this.pwd,
                    name: this.name,
                    mail: this.mail,
                    baseUrl: utils.getBaseUrl(),
                    path: wurl('path')
                })
            }).then(function (resp) {
                if (resp.ok) {
                    resp.json().then(function (data) {
                        if (data.success) {
                            toastr.success('注册成功,请通过接收到的激活邮件激活账户!');
                            _.delay(function () {
                                window.location = "/admin/login";
                            }, 2000);
                        } else {
                            toastr.error(data.data, '注册失败!');
                            _this2.isReq = false;
                        }
                    });
                }
            });
        };

        return ViewModel;
    }();
});
define('resources/attributes/attr-autosize',['exports', 'aurelia-framework', 'aurelia-templating', 'autosize'], function (exports, _aureliaFramework, _aureliaTemplating, _autosize) {
    'use strict';

    Object.defineProperty(exports, "__esModule", {
        value: true
    });
    exports.AttrAutosize = undefined;

    var _autosize2 = _interopRequireDefault(_autosize);

    function _interopRequireDefault(obj) {
        return obj && obj.__esModule ? obj : {
            default: obj
        };
    }

    function _classCallCheck(instance, Constructor) {
        if (!(instance instanceof Constructor)) {
            throw new TypeError("Cannot call a class as a function");
        }
    }

    var _dec, _dec2, _class;

    var AttrAutosize = exports.AttrAutosize = (_dec = (0, _aureliaTemplating.customAttribute)('autosize'), _dec2 = (0, _aureliaFramework.inject)(Element), _dec(_class = _dec2(_class = function () {
        function AttrAutosize(element) {
            _classCallCheck(this, AttrAutosize);

            this.element = element;
        }

        AttrAutosize.prototype.valueChanged = function valueChanged(newValue, oldValue) {
            (0, _autosize2.default)(this.element);
        };

        AttrAutosize.prototype.bind = function bind(bindingContext) {
            this.valueChanged(this.value);
        };

        AttrAutosize.prototype.unbind = function unbind() {
            _autosize2.default.destroy(this.elements);
        };

        return AttrAutosize;
    }()) || _class) || _class);
});
define('resources/attributes/attr-pastable',['exports', 'aurelia-framework', 'aurelia-templating', 'common/common-plugin', 'paste'], function (exports, _aureliaFramework, _aureliaTemplating) {
    'use strict';

    Object.defineProperty(exports, "__esModule", {
        value: true
    });
    exports.AttrPastable = undefined;

    function _classCallCheck(instance, Constructor) {
        if (!(instance instanceof Constructor)) {
            throw new TypeError("Cannot call a class as a function");
        }
    }

    var _dec, _dec2, _class;

    var AttrPastable = exports.AttrPastable = (_dec = (0, _aureliaTemplating.customAttribute)('pastable'), _dec2 = (0, _aureliaFramework.inject)(Element), _dec(_class = _dec2(_class = function () {
        function AttrPastable(element) {
            _classCallCheck(this, AttrPastable);

            this.element = element;
        }

        AttrPastable.prototype.valueChanged = function valueChanged(newValue, oldValue) {
            var _this = this;

            $(this.element).pastableTextarea().on('pasteImage', function (ev, data) {

                $.post('/admin/file/base64', {
                    dataURL: data.dataURL,
                    type: data.blob.type
                }, function (data, textStatus, xhr) {
                    if (data.success) {
                        $(_this.element).insertAtCaret('![{name}]({baseURL}{path}{uuidName})'.replace(/\{name\}/g, data.data.name).replace(/\{baseURL\}/g, utils.getBaseUrl() + '/').replace(/\{path\}/g, data.data.path).replace(/\{uuidName\}/g, data.data.uuidName));
                    }
                });
            }).on('pasteImageError', function (ev, data) {
                toastr.error(data.message, '剪贴板粘贴图片错误!');
            });
        };

        AttrPastable.prototype.bind = function bind(bindingContext) {
            this.valueChanged(this.value);
        };

        return AttrPastable;
    }()) || _class) || _class);
});
define('resources/attributes/attr-swipebox',['exports', 'aurelia-framework', 'aurelia-templating', 'swipebox'], function (exports, _aureliaFramework, _aureliaTemplating) {
    'use strict';

    Object.defineProperty(exports, "__esModule", {
        value: true
    });
    exports.AttrSwipebox = undefined;

    function _classCallCheck(instance, Constructor) {
        if (!(instance instanceof Constructor)) {
            throw new TypeError("Cannot call a class as a function");
        }
    }

    var _dec, _dec2, _class;

    var AttrSwipebox = exports.AttrSwipebox = (_dec = (0, _aureliaTemplating.customAttribute)('swipebox'), _dec2 = (0, _aureliaFramework.inject)(Element), _dec(_class = _dec2(_class = function () {
        function AttrSwipebox(element) {
            _classCallCheck(this, AttrSwipebox);

            this.element = element;
        }

        AttrSwipebox.prototype.valueChanged = function valueChanged(newValue, oldValue) {
            var _this = this;

            $(this.element).on('click', 'img', function (event) {
                event.preventDefault();
                var $img = $(event.target);
                var imgs = [];
                var initialIndexOnArray = 0;
                $(_this.element).find('img').each(function (index, img) {
                    imgs.push({ href: $(img).attr('src'), title: $(img).attr('alt') });
                    if (event.target == img) {
                        initialIndexOnArray = index;
                    }
                });
                $.swipebox(imgs, {
                    useCSS: true,
                    useSVG: true,
                    initialIndexOnArray: initialIndexOnArray,
                    hideCloseButtonOnMobile: false,
                    removeBarsOnMobile: true,
                    hideBarsDelay: 3000,
                    videoMaxWidth: 1140,
                    beforeOpen: function beforeOpen() {},
                    afterOpen: null,
                    afterClose: function afterClose() {},
                    loopAtEnd: !!newValue });
            });
        };

        AttrSwipebox.prototype.bind = function bind(bindingContext) {
            this.valueChanged(this.value);
        };

        return AttrSwipebox;
    }()) || _class) || _class);
});
define('resources/attributes/attr-task',['exports', 'aurelia-dependency-injection', 'aurelia-templating'], function (exports, _aureliaDependencyInjection, _aureliaTemplating) {
    'use strict';

    Object.defineProperty(exports, "__esModule", {
        value: true
    });
    exports.AttrTask = undefined;

    function _classCallCheck(instance, Constructor) {
        if (!(instance instanceof Constructor)) {
            throw new TypeError("Cannot call a class as a function");
        }
    }

    var _dec, _dec2, _class;

    var AttrTask = exports.AttrTask = (_dec = (0, _aureliaTemplating.customAttribute)('task'), _dec2 = (0, _aureliaDependencyInjection.inject)(Element), _dec(_class = _dec2(_class = function () {
        function AttrTask(element) {
            _classCallCheck(this, AttrTask);

            this.task = null;
            this.bindingCtx = null;

            this.element = element;
        }

        AttrTask.prototype.valueChanged = function valueChanged(newValue) {
            this.task = newValue;
            if (_.isFunction(this.task)) {
                _.bind(this.task, this.bindingCtx, this.element)();
            }
        };

        AttrTask.prototype.bind = function bind(bindingContext) {
            this.bindingCtx = bindingContext;
            this.valueChanged(this.value);
        };

        AttrTask.prototype.unbind = function unbind() {
            this.element = null;
            this.task = null;
            this.bindingCtx = null;
        };

        return AttrTask;
    }()) || _class) || _class);
});
define('resources/elements/em-confirm-modal',['exports', 'aurelia-framework'], function (exports, _aureliaFramework) {
    'use strict';

    Object.defineProperty(exports, "__esModule", {
        value: true
    });
    exports.EmConfirmModal = undefined;

    function _classCallCheck(instance, Constructor) {
        if (!(instance instanceof Constructor)) {
            throw new TypeError("Cannot call a class as a function");
        }
    }

    var EmConfirmModal = exports.EmConfirmModal = function () {
        function EmConfirmModal() {
            _classCallCheck(this, EmConfirmModal);

            this.config = {};
        }

        EmConfirmModal.prototype.detached = function detached() {
            $(this.md).remove();
        };

        EmConfirmModal.prototype.attached = function attached() {
            var _this = this;

            $(this.md).modal({
                closable: false,
                onApprove: function onApprove() {
                    _this.onapprove && _this.onapprove();
                },
                onDeny: function onDeny() {
                    _this.ondeny && _this.ondeny();
                }
            });
        };

        EmConfirmModal.prototype.reset = function reset() {
            this.config = {
                title: '操作确认',
                content: '确定要执行该操作吗?',
                warning: false
            };
        };

        EmConfirmModal.prototype.show = function show(config) {

            this.reset();

            if (config) {
                this.config = _.extend(this.config, config);
            }

            if (config && config.onapprove) {
                this.onapprove = config.onapprove;
            }

            if (config && config.ondeny) {
                this.ondeny = config.ondeny;
            }

            $(this.md).modal('show');
        };

        EmConfirmModal.prototype.hide = function hide() {
            $(this.md).modal('hide');
        };

        return EmConfirmModal;
    }();
});
define('resources/elements/em-hotkeys-modal',['exports', 'aurelia-framework'], function (exports, _aureliaFramework) {
    'use strict';

    Object.defineProperty(exports, "__esModule", {
        value: true
    });
    exports.EmHotkeysModal = undefined;

    function _classCallCheck(instance, Constructor) {
        if (!(instance instanceof Constructor)) {
            throw new TypeError("Cannot call a class as a function");
        }
    }

    var EmHotkeysModal = exports.EmHotkeysModal = function () {
        function EmHotkeysModal() {
            _classCallCheck(this, EmHotkeysModal);
        }

        EmHotkeysModal.prototype.attached = function attached() {
            $(this.md).modal();
        };

        EmHotkeysModal.prototype.show = function show() {
            $(this.md).modal('show');
        };

        return EmHotkeysModal;
    }();
});
define('resources/value-converters/vc-common',['exports', 'jquery-format', 'timeago'], function (exports) {
    'use strict';

    Object.defineProperty(exports, "__esModule", {
        value: true
    });
    exports.TimeagoValueConverter = exports.NumberValueConverter = exports.DateValueConverter = exports.LowerValueConverter = exports.UpperValueConverter = undefined;

    function _classCallCheck(instance, Constructor) {
        if (!(instance instanceof Constructor)) {
            throw new TypeError("Cannot call a class as a function");
        }
    }

    var tg = timeago();

    var UpperValueConverter = exports.UpperValueConverter = function () {
        function UpperValueConverter() {
            _classCallCheck(this, UpperValueConverter);
        }

        UpperValueConverter.prototype.toView = function toView(value) {
            return value && value.toUpperCase();
        };

        return UpperValueConverter;
    }();

    var LowerValueConverter = exports.LowerValueConverter = function () {
        function LowerValueConverter() {
            _classCallCheck(this, LowerValueConverter);
        }

        LowerValueConverter.prototype.toView = function toView(value) {
            return value && value.toLowerCase();
        };

        return LowerValueConverter;
    }();

    var DateValueConverter = exports.DateValueConverter = function () {
        function DateValueConverter() {
            _classCallCheck(this, DateValueConverter);
        }

        DateValueConverter.prototype.toView = function toView(value) {
            var format = arguments.length <= 1 || arguments[1] === undefined ? 'yyyy-MM-dd hh:mm:ss' : arguments[1];

            return _.isInteger(_.toNumber(value)) ? $.format.date(new Date(value), format) : value ? value : '';
        };

        return DateValueConverter;
    }();

    var NumberValueConverter = exports.NumberValueConverter = function () {
        function NumberValueConverter() {
            _classCallCheck(this, NumberValueConverter);
        }

        NumberValueConverter.prototype.toView = function toView(value) {
            var format = arguments.length <= 1 || arguments[1] === undefined ? '#,##0.00' : arguments[1];

            return _.isNumber(_.toNumber(value)) ? $.format.number(value, format) : value ? value : '';
        };

        return NumberValueConverter;
    }();

    var TimeagoValueConverter = exports.TimeagoValueConverter = function () {
        function TimeagoValueConverter() {
            _classCallCheck(this, TimeagoValueConverter);
        }

        TimeagoValueConverter.prototype.toView = function toView(value) {
            return value ? tg.format(value, 'zh_CN') : '';
        };

        return TimeagoValueConverter;
    }();
});
define('text!app.html', ['module'], function(module) { module.exports = "<template>\r\n\t<require from=\"./app.css\"></require>\r\n\t<require from=\"nprogress/nprogress.css\"></require>\r\n\t<require from=\"toastr/build/toastr.css\"></require>\r\n    <require from=\"tms-semantic-ui/semantic.min.css\"></require>\r\n    <router-view></router-view>\r\n</template>\r\n"; });
define('text!chat/chat-direct.html', ['module'], function(module) { module.exports = "<template>\r\n    <require from=\"./chat-direct.css\"></require>\r\n    <require from=\"./md-github.css\"></require>\r\n    <require from=\"dropzone/dist/basic.css\"></require>\r\n    <require from=\"swipebox/src/css/swipebox.min.css\"></require>\r\n    <require from=\"resources/elements/em-confirm-modal\"></require>\r\n    <require from=\"resources/elements/em-hotkeys-modal\"></require>\r\n    <div ref=\"chatContainerRef\" class=\"tms-chat-direct\">\r\n        <div class=\"ui top fixed menu\">\r\n            <!-- <div class=\"item\">\r\n                <i class=\"icon link sidebar\"></i>\r\n            </div> -->\r\n            <!-- <a class=\"item header\">私聊@${user.name ? user.name : chatTo}</a> -->\r\n            <div ref=\"chatToDropdownRef\" class=\"ui dropdown link item\">\r\n                <!-- <span style=\"font-weight: bold; font-size: 20px;\">私聊</span> -->\r\n                <i class=\"big loading at icon\"></i>\r\n                <!-- <i class=\"big icons\">\r\n                    <i class=\"large loading sun icon\"></i>\r\n                    <i class=\"at icon\"></i>\r\n                </i> -->\r\n                <span class=\"text\"></span>\r\n                <i class=\"dropdown icon\"></i>\r\n                <div class=\"menu\">\r\n                    <div class=\"ui icon search input\">\r\n                        <i class=\"search icon\"></i>\r\n                        <input type=\"text\" placeholder=\"过滤私聊对象\">\r\n                    </div>\r\n                    <div class=\"divider\"></div>\r\n                    <div class=\"header\">\r\n                        <i class=\"user icon\"></i> 切换私聊对象\r\n                    </div>\r\n                    <div class=\"scrolling menu\">\r\n                        <a repeat.for=\"item of users\" task.bind=\"initChatToDropdownHandler($last)\" href=\"#/chat-direct/${item.username}\" class=\"item\" title=\"${item.username}\" data-value=\"${item.username}\">\r\n                            <i class=\"circular icon user\"></i> ${item.name}\r\n                        </a>\r\n                    </div>\r\n                </div>\r\n            </div>\r\n            <div class=\"right menu\">\r\n                <a class=\"item\">\r\n                    <i class=\"circular user icon\"></i> ${loginUser.name}\r\n                </a>\r\n            </div>\r\n        </div>\r\n        <div ref=\"sidebarRef\" class=\"ui left visible segment sidebar tms-left-sidebar\">\r\n            <div class=\"tms-header\">\r\n                <h1 class=\"ui header\"><a href=\"/admin/dynamic?scroll=1\">私聊频道</a></h1>\r\n                <input value.bind=\"filter\" focusin.trigger=\"chatToUserFilerFocusinHanlder()\" keyup.trigger=\"chatToUserFilerKeyupHanlder($event)\" type=\"text\" placeholder=\"私聊对象查找\">\r\n                <i title=\"清空过滤输入\" click.delegate=\"clearFilterHandler()\" class=\"bordered close icon link small\"></i>\r\n            </div>\r\n            <div ref=\"userListRef\" class=\"ui middle aligned selection list\">\r\n                <a repeat.for=\"item of users\" title=\"${item.username}\" show.bind=\"!item.hidden\" href=\"#/chat-direct/${item.username}\" class=\"item ${item.username == chatTo ? 'active' : ''}\" data-id=\"${item.username}\">\r\n                    <i class=\"circular icon user\"></i>\r\n                    <div class=\"content\">\r\n                        <div style=\"color: black;\">${item.name ? item.name : item.username}</div>\r\n                    </div>\r\n                </a>\r\n            </div>\r\n        </div>\r\n        <div class=\"tms-content\">\r\n            <div class=\"tms-col w65\">\r\n                <div ref=\"commentsRef\" class=\"ui basic segment minimal selection list segment comments\">\r\n                    <!-- <h3 class=\"ui dividing header\">私聊内容</h3> -->\r\n                    <button if.bind=\"!last\" click.delegate=\"lastMoreHandler()\" class=\"fluid ui button\">加载更多(${lastCnt})</button>\r\n                    <div repeat.for=\"item of chats\" swipebox class=\"comment item ${item.id == markId ? 'active' : ''}\" data-id=\"${item.id}\">\r\n                        <a class=\"avatar\">\r\n                            <i class=\"circular icon large user\"></i>\r\n                        </a>\r\n                        <div class=\"content\">\r\n                            <a class=\"author\">${item.creator.name}</a>\r\n                            <div class=\"metadata\">\r\n                                <div class=\"date\" data-timeago=\"${item.createDate}\" title=\"${item.createDate | date}\">${item.createDate | timeago}</div>\r\n                            </div>\r\n                            <div show.bind=\"!item.isEditing\" class=\"text markdown-body\" innerhtml.bind=\"item.contentMd\"></div>\r\n                            <textarea ref=\"editTxtRef\" pastable autosize focusout.trigger=\"focusoutHandler(item)\" keydown.trigger=\"eidtKeydownHandler($event, item, editTxtRef)\" show.bind=\"item.isEditing\" value.bind=\"item.content\" class=\"tms-edit-textarea\" rows=\"1\"></textarea>\r\n                            <div class=\"actions\">\r\n                                <a if.bind=\"item.creator.username == loginUser.username\" click.delegate=\"editHandler(item, editTxtRef)\" class=\"tms-edit\">编辑</a>\r\n                                <a if.bind=\"item.creator.username == loginUser.username\" click.delegate=\"deleteHandler(item)\" class=\"tms-delete\">删除</a>\r\n                                <a class=\"tms-copy tms-clipboard\" data-clipboard-text=\"${item.content}\">复制</a>\r\n                                <a class=\"tms-share tms-clipboard\" data-clipboard-text=\"${selfLink + '?id=' + item.id}\">分享</a>\r\n                            </div>\r\n                        </div>\r\n                    </div>\r\n                    <button if.bind=\"!first\" click.delegate=\"firstMoreHandler()\" class=\"fluid ui button\">加载更多(${firstCnt})</button>\r\n                </div>\r\n                <div class=\"ui basic segment tms-msg-input dropzone\">\r\n                    <div ref=\"inputRef\" class=\"ui left action fluid icon input dropzone\">\r\n                        <div ref=\"chatBtnRef\" class=\"ui icon button\">\r\n                            <i class=\"plus icon\"></i>\r\n                        </div>\r\n                        <div class=\"ui flowing popup bottom left transition hidden\">\r\n                            <div class=\"ui middle aligned selection list\">\r\n                                <div ref=\"btnItemUploadRef\" class=\"item\">\r\n                                    <i class=\"upload icon\"></i>\r\n                                    <div class=\"content\">\r\n                                        上传文件\r\n                                    </div>\r\n                                </div>\r\n                            </div>\r\n                        </div>\r\n                        <textarea ref=\"chatInputRef\" placeholder=\"输入聊天内容(按下 / 键提示补全) (Ctrl+Enter换行,Enter发送,Esc清空)\" pastable autosize value.bind=\"content\" keydown.trigger=\"sendKeydownHandler($event, chatInputRef)\" rows=\"1\"></textarea>\r\n                        <i click.delegate=\"sendChatMsgHandler()\" title=\"发送消息(Enter)\" class=\"send link icon\"></i>\r\n                    </div>\r\n                    <div ref=\"uploadProgressRef\" class=\"tms-upload-progress dropzone-previews\"></div>\r\n                </div>\r\n            </div>\r\n            <!-- <div class=\"tms-col w35\"></div> -->\r\n        </div>\r\n    </div>\r\n    <div ref=\"previewTemplateRef\" style=\"display: none;\">\r\n        <div class=\"dz-preview dz-file-preview\">\r\n            <div class=\"dz-details\">\r\n                <div class=\"dz-filename\"><span data-dz-name></span></div>\r\n                <div class=\"dz-size\" data-dz-size></div>\r\n                <img data-dz-thumbnail />\r\n            </div>\r\n            <div class=\"dz-progress\"><span class=\"dz-upload\" data-dz-uploadprogress></span></div>\r\n            <div class=\"dz-success-mark\"><span>✔</span></div>\r\n            <div class=\"dz-error-mark\"><span>✘</span></div>\r\n            <div class=\"dz-error-message\"><span data-dz-errormessage></span></div>\r\n        </div>\r\n    </div>\r\n    <em-confirm-modal em-confirm-modal.ref=\"emConfirmModal\"></em-confirm-modal>\r\n    <em-hotkeys-modal em-hotkeys-modal.ref=\"emHotkeysModal\"></em-hotkeys-modal>\r\n</template>\r\n"; });
define('text!app.css', ['module'], function(module) { module.exports = "html,\nbody {\n  height: 100%;\n}\n::-webkit-scrollbar {\n  width: 6px;\n  height: 6px;\n}\n::-webkit-scrollbar-thumb {\n  border-radius: 6px;\n  background-color: #c6c6c6;\n}\n::-webkit-scrollbar-thumb:hover {\n  background: #999;\n}\n.ui.modal.tms-md450 {\n  width: 450px!important;\n  margin-left: -225px !important;\n}\n.ui.modal.tms-md510 {\n  width: 510px!important;\n  margin-left: -255px !important;\n}\n.ui.modal.tms-md540 {\n  width: 540px!important;\n  margin-left: -275px !important;\n}\n/* for swipebox */\n#swipebox-overlay {\n  background: rgba(13, 13, 13, 0.5) !important;\n}\n.keyboard {\n  background: #fff;\n  font-weight: 700;\n  padding: 2px .35rem;\n  font-size: .8rem;\n  margin: 0 2px;\n  border-radius: .25rem;\n  color: #3d3c40;\n  border-bottom: 2px solid #9e9ea6;\n  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);\n  text-shadow: none;\n}\n"; });
define('text!user/user-login.html', ['module'], function(module) { module.exports = "<template>\r\n    <require from=\"./user-login.css\"></require>\r\n    <div class=\"tms-user-login\">\r\n        <div class=\"container\">\r\n            <h2 class=\"ui center aligned icon header\">\r\n            <i class=\"circular users icon\"></i> 用户登录\r\n            </h2>\r\n            <div class=\"ui form segment\">\r\n                <div class=\"field\">\r\n                    <div class=\"ui left icon input\">\r\n                        <i class=\"user icon\"></i>\r\n                        <input type=\"text\" value.bind=\"username\" placeholder=\"用户名\" />\r\n                    </div>\r\n                </div>\r\n                <div class=\"field\">\r\n                    <div class=\"ui left icon input\">\r\n                        <i class=\"lock icon\"></i>\r\n                        <input type=\"password\" value.bind=\"password\" placeholder=\"密码\" />\r\n                    </div>\r\n                </div>\r\n                <div class=\"field\">\r\n                    <div ref=\"rememberMeRef\" class=\"ui checkbox\">\r\n                        <input type=\"checkbox\" name=\"remember-me\" />\r\n                        <label>记住我在此计算机的登录(2周)</label>\r\n                    </div>\r\n                </div>\r\n                <div class=\"ui center aligned header\">\r\n                    <button click.delegate=\"loginHandler()\" class=\"ui submit fluid button ${isReq ? 'disabled' : ''}\">登录</button>\r\n                </div>\r\n                <div style=\"text-align: center; font-size:12px;\">\r\n                    <a href=\"#/pwd-reset\">忘记密码</a> &nbsp;&nbsp;\r\n                    <a href=\"#/register\">注册用户</a>\r\n                </div>\r\n            </div>\r\n        </div>\r\n    </div>\r\n</template>\r\n"; });
define('text!chat/chat-direct.css', ['module'], function(module) { module.exports = ".tms-chat-direct {\n  height: 100%;\n}\n.tms-chat-direct .top.fixed.menu {\n  padding-left: 220px;\n  height: 60px;\n}\n.tms-chat-direct .ui.left.sidebar {\n  width: 220px;\n}\n.tms-chat-direct .ui.left.sidebar .ui.list {\n  position: absolute;\n  bottom: 0;\n  top: 60px;\n  overflow-y: auto;\n  width: 190px;\n}\n.tms-chat-direct .ui.left.sidebar .tms-header {\n  height: 40px;\n}\n.tms-chat-direct .ui.left.sidebar .tms-header input {\n  border: 1px #e0e1e2 solid;\n  font-size: 12px;\n  padding: 4px;\n  /* border-radius: 3px; */\n  width: 190px;\n  /* border-top-right-radius: 10px;\n    border-bottom-right-radius: 10px; */\n}\n.tms-chat-direct .ui.left.sidebar .tms-header i.close.icon {\n  position: absolute;\n  right: 11px;\n  top: 45px;\n}\n.tms-chat-direct .ui.left.sidebar .tms-header .ui.header {\n  margin-bottom: 0;\n}\n.tms-chat-direct .tms-content {\n  padding-top: 60px;\n  padding-left: 220px;\n  /* display: flex;\n    align-items: stretch; */\n  min-height: 100%;\n}\n.tms-chat-direct .tms-content .tms-col.w65 {\n  /* flex: auto; */\n}\n.tms-chat-direct .tms-content .tms-col.w35 {\n  width: 380px;\n  border-left: 1px #e0e1e2 solid;\n}\n.tms-chat-direct .ui.basic.segment.tms-msg-input {\n  position: fixed;\n  bottom: 0;\n  /* right: 30%; */\n  left: 220px;\n  right: 0;\n  background-color: white;\n  padding-bottom: 22px;\n}\n.tms-chat-direct .ui.basic.segment.tms-msg-input .tms-upload-progress {\n  /* height: 15px; */\n  position: absolute;\n  /* width: 97.5%; */\n  /* top: 5px; */\n  /* bottom: 5px; */\n  bottom: 65px;\n  /* background-color: #e0e1e2; */\n  right: 15px;\n  left: 15px;\n}\n.tms-chat-direct .ui.basic.segment.tms-msg-input .tms-upload-progress .dz-preview {\n  display: block!important;\n  width: auto!important;\n  background: #e0e1e2;\n  margin: 0;\n  padding: 7px;\n}\n.tms-chat-direct .ui.comments {\n  margin-top: 20px;\n  margin-bottom: 50px;\n  max-width: none;\n}\n.tms-chat-direct .tms-msg-input .ui[class*=\"left action\"].input > textarea {\n  border-top-left-radius: 0!important;\n  border-bottom-left-radius: 0!important;\n  border-left-color: transparent!important;\n}\n.tms-chat-direct .tms-msg-input .ui.icon.input textarea {\n  padding-right: 2.67142857em!important;\n}\n.tms-chat-direct .tms-msg-input .ui.input textarea {\n  margin: 0;\n  max-width: 100%;\n  -webkit-box-flex: 1;\n  -webkit-flex: 1 0 auto;\n  -ms-flex: 1 0 auto;\n  flex: 1 0 auto;\n  outline: 0;\n  -webkit-tap-highlight-color: rgba(255, 255, 255, 0);\n  text-align: left;\n  line-height: 1.2142em;\n  padding: .67861429em 1em;\n  background: #FFF;\n  border: 1px solid rgba(34, 36, 38, 0.15);\n  color: rgba(0, 0, 0, 0.87);\n  border-radius: .28571429rem;\n  box-shadow: none;\n}\n.tms-chat-direct .tms-edit-textarea {\n  width: 100%;\n}\n.tms-chat-direct .ui.selection.list > .item {\n  cursor: default;\n}\n@media only screen and (max-width: 767px) {\n  .tms-chat-direct .tms-left-sidebar {\n    display: none;\n  }\n  .tms-chat-direct .top.fixed.menu {\n    padding-left: 0;\n  }\n  .tms-chat-direct .tms-content {\n    padding-left: 0;\n  }\n  .tms-chat-direct .ui.basic.segment.tms-msg-input {\n    left: 0;\n  }\n}\n.textcomplete-dropdown {\n  position: static!important;\n  border: 1px solid #ddd;\n  background-color: white;\n  list-style: none;\n  padding: 0;\n  margin: 0;\n  border-radius: 5px;\n}\n.textcomplete-dropdown li {\n  /* border-top: 1px solid #ddd; */\n  padding: 2px 5px;\n}\n.textcomplete-dropdown li:first-child {\n  border-top: none;\n  border-top-left-radius: 5px;\n  border-top-right-radius: 5px;\n}\n.textcomplete-dropdown li:last-child {\n  border-bottom-left-radius: 5px;\n  border-bottom-right-radius: 5px;\n}\n.textcomplete-dropdown li:hover,\n.textcomplete-dropdown .active {\n  background-color: #439fe0;\n}\n.textcomplete-dropdown a:hover {\n  cursor: pointer;\n}\n.textcomplete-dropdown li.textcomplete-item a {\n  color: black;\n}\n.textcomplete-dropdown li.textcomplete-item:hover a,\n.textcomplete-dropdown li.textcomplete-item.active a {\n  color: white;\n}\n"; });
define('text!user/user-pwd-reset.html', ['module'], function(module) { module.exports = "<template>\r\n    <require from=\"./user-pwd-reset.css\"></require>\r\n    <div class=\"ui container tms-user-pwd-reset\">\r\n        <div class=\"tms-flex\">\r\n            <div if.bind=\"!token\" ref=\"fm\" class=\"ui form segment\" style=\"width: 260px;\">\r\n                <div class=\"ui message\">输入您的邮箱地址,我们会发送密码重置链接到您的邮箱!</div>\r\n                <div class=\"field\">\r\n                    <label style=\"display:none;\">邮件地址</label>\r\n                    <input type=\"text\" name=\"mail\" autofocus=\"\" value.bind=\"mail\" placeholder=\"输入您的邮件地址\">\r\n                </div>\r\n                <div class=\"ui green fluid button ${isReq ? 'disabled' : ''}\" click.delegate=\"resetPwdHandler()\">发送密码重置邮件</div>\r\n            </div>\r\n            <div if.bind=\"token\" ref=\"fm2\" class=\"ui form segment\" style=\"width: 260px;\">\r\n                <div class=\"ui message\">设置您的新密码,密码长度要求至少8位字符!</div>\r\n                <div class=\"field\">\r\n                    <label style=\"display:none;\">新密码</label>\r\n                    <input type=\"password\" name=\"mail\" autofocus=\"\" value.bind=\"pwd\" placeholder=\"设置您的新密码\">\r\n                </div>\r\n                <div class=\"ui green fluid button ${isReq ? 'disabled' : ''}\" click.delegate=\"newPwdHandler()\">确认</div>\r\n            </div>\r\n        </div>\r\n    </div>\r\n</template>\r\n"; });
define('text!chat/md-github.css', ['module'], function(module) { module.exports = ".markdown-body {\n  font-size: 14px;\n  line-height: 1.6;\n}\n.markdown-body > br,\n.markdown-body ul br .markdown-body ol br {\n  display: none;\n}\n.markdown-body > *:first-child {\n  margin-top: 0 !important;\n}\n.markdown-body > *:last-child {\n  margin-bottom: 0 !important;\n}\n.markdown-body a.absent {\n  color: #CC0000;\n}\n.markdown-body a.anchor {\n  bottom: 0;\n  cursor: pointer;\n  display: block;\n  left: 0;\n  margin-left: -30px;\n  padding-left: 30px;\n  position: absolute;\n  top: 0;\n}\n.markdown-body h1,\n.markdown-body h2,\n.markdown-body h3,\n.markdown-body h4,\n.markdown-body h5,\n.markdown-body h6 {\n  cursor: text;\n  font-weight: bold;\n  margin: 20px 0 10px;\n  padding: 0;\n  position: relative;\n}\n.markdown-body h1 .mini-icon-link,\n.markdown-body h2 .mini-icon-link,\n.markdown-body h3 .mini-icon-link,\n.markdown-body h4 .mini-icon-link,\n.markdown-body h5 .mini-icon-link,\n.markdown-body h6 .mini-icon-link {\n  color: #000000;\n  display: none;\n}\n.markdown-body h1:hover a.anchor,\n.markdown-body h2:hover a.anchor,\n.markdown-body h3:hover a.anchor,\n.markdown-body h4:hover a.anchor,\n.markdown-body h5:hover a.anchor,\n.markdown-body h6:hover a.anchor {\n  line-height: 1;\n  margin-left: -22px;\n  padding-left: 0;\n  text-decoration: none;\n  top: 15%;\n}\n.markdown-body h1:hover a.anchor .mini-icon-link,\n.markdown-body h2:hover a.anchor .mini-icon-link,\n.markdown-body h3:hover a.anchor .mini-icon-link,\n.markdown-body h4:hover a.anchor .mini-icon-link,\n.markdown-body h5:hover a.anchor .mini-icon-link,\n.markdown-body h6:hover a.anchor .mini-icon-link {\n  display: inline-block;\n}\n.markdown-body h1 tt,\n.markdown-body h1 code,\n.markdown-body h2 tt,\n.markdown-body h2 code,\n.markdown-body h3 tt,\n.markdown-body h3 code,\n.markdown-body h4 tt,\n.markdown-body h4 code,\n.markdown-body h5 tt,\n.markdown-body h5 code,\n.markdown-body h6 tt,\n.markdown-body h6 code {\n  font-size: inherit;\n}\n.markdown-body h1 {\n  color: #000000;\n  font-size: 28px;\n}\n.markdown-body h2 {\n  border-bottom: 1px solid #CCCCCC;\n  color: #000000;\n  font-size: 24px;\n}\n.markdown-body h3 {\n  font-size: 18px;\n}\n.markdown-body h4 {\n  font-size: 16px;\n}\n.markdown-body h5 {\n  font-size: 14px;\n}\n.markdown-body h6 {\n  color: #777777;\n  font-size: 14px;\n}\n.markdown-body p,\n.markdown-body blockquote,\n.markdown-body ul,\n.markdown-body ol,\n.markdown-body dl,\n.markdown-body table,\n.markdown-body pre {\n  margin: 15px 0;\n}\n.markdown-body hr {\n  overflow: hidden;\n  background: 0 0;\n}\n.markdown-body hr:before {\n  display: table;\n  content: \"\";\n}\n.markdown-body hr:after {\n  display: table;\n  clear: both;\n  content: \"\";\n}\n.markdown-body hr {\n  height: 4px;\n  padding: 0;\n  margin: 16px 0;\n  background-color: #e7e7e7;\n  border: 0;\n}\n.markdown-body hr {\n  -moz-box-sizing: content-box;\n  box-sizing: content-box;\n}\n.markdown-body > h2:first-child,\n.markdown-body > h1:first-child,\n.markdown-body > h1:first-child + h2,\n.markdown-body > h3:first-child,\n.markdown-body > h4:first-child,\n.markdown-body > h5:first-child,\n.markdown-body > h6:first-child {\n  margin-top: 0;\n  padding-top: 0;\n}\n.markdown-body a:first-child h1,\n.markdown-body a:first-child h2,\n.markdown-body a:first-child h3,\n.markdown-body a:first-child h4,\n.markdown-body a:first-child h5,\n.markdown-body a:first-child h6 {\n  margin-top: 0;\n  padding-top: 0;\n}\n.markdown-body h1 + p,\n.markdown-body h2 + p,\n.markdown-body h3 + p,\n.markdown-body h4 + p,\n.markdown-body h5 + p,\n.markdown-body h6 + p {\n  margin-top: 0;\n}\n.markdown-body li p.first {\n  display: inline-block;\n}\n.markdown-body ul,\n.markdown-body ol {\n  padding-left: 30px;\n}\n.markdown-body ul.no-list,\n.markdown-body ol.no-list {\n  list-style-type: none;\n  padding: 0;\n}\n.markdown-body ul li > *:first-child,\n.markdown-body ol li > *:first-child {\n  margin-top: 0;\n}\n.markdown-body ul ul,\n.markdown-body ul ol,\n.markdown-body ol ol,\n.markdown-body ol ul {\n  margin-bottom: 0;\n}\n.markdown-body dl {\n  padding: 0;\n}\n.markdown-body dl dt {\n  font-size: 14px;\n  font-style: italic;\n  font-weight: bold;\n  margin: 15px 0 5px;\n  padding: 0;\n}\n.markdown-body dl dt:first-child {\n  padding: 0;\n}\n.markdown-body dl dt > *:first-child {\n  margin-top: 0;\n}\n.markdown-body dl dt > *:last-child {\n  margin-bottom: 0;\n}\n.markdown-body dl dd {\n  margin: 0 0 15px;\n  padding: 0 15px;\n}\n.markdown-body dl dd > *:first-child {\n  margin-top: 0;\n}\n.markdown-body dl dd > *:last-child {\n  margin-bottom: 0;\n}\n.markdown-body blockquote {\n  border-left: 4px solid #DDDDDD;\n  color: #777777;\n  padding: 0 15px;\n}\n.markdown-body blockquote > *:first-child {\n  margin-top: 0;\n}\n.markdown-body blockquote > *:last-child {\n  margin-bottom: 0;\n}\n.markdown-body table th {\n  font-weight: bold;\n}\n.markdown-body table th,\n.markdown-body table td {\n  border: 1px solid #CCCCCC;\n  padding: 6px 13px;\n}\n.markdown-body table tr {\n  background-color: #FFFFFF;\n  border-top: 1px solid #CCCCCC;\n}\n.markdown-body table tr:nth-child(2n) {\n  background-color: #F8F8F8;\n}\n.markdown-body img {\n  max-width: 100%;\n}\n.markdown-body span.frame {\n  display: block;\n  overflow: hidden;\n}\n.markdown-body span.frame > span {\n  border: 1px solid #DDDDDD;\n  display: block;\n  float: left;\n  margin: 13px 0 0;\n  overflow: hidden;\n  padding: 7px;\n  width: auto;\n}\n.markdown-body span.frame span img {\n  display: block;\n  float: left;\n}\n.markdown-body span.frame span span {\n  clear: both;\n  color: #333333;\n  display: block;\n  padding: 5px 0 0;\n}\n.markdown-body span.align-center {\n  clear: both;\n  display: block;\n  overflow: hidden;\n}\n.markdown-body span.align-center > span {\n  display: block;\n  margin: 13px auto 0;\n  overflow: hidden;\n  text-align: center;\n}\n.markdown-body span.align-center span img {\n  margin: 0 auto;\n  text-align: center;\n}\n.markdown-body span.align-right {\n  clear: both;\n  display: block;\n  overflow: hidden;\n}\n.markdown-body span.align-right > span {\n  display: block;\n  margin: 13px 0 0;\n  overflow: hidden;\n  text-align: right;\n}\n.markdown-body span.align-right span img {\n  margin: 0;\n  text-align: right;\n}\n.markdown-body span.float-left {\n  display: block;\n  float: left;\n  margin-right: 13px;\n  overflow: hidden;\n}\n.markdown-body span.float-left span {\n  margin: 13px 0 0;\n}\n.markdown-body span.float-right {\n  display: block;\n  float: right;\n  margin-left: 13px;\n  overflow: hidden;\n}\n.markdown-body span.float-right > span {\n  display: block;\n  margin: 13px auto 0;\n  overflow: hidden;\n  text-align: right;\n}\n.markdown-body code,\n.markdown-body tt {\n  background-color: #F8F8F8;\n  border: 1px solid #EAEAEA;\n  border-radius: 3px 3px 3px 3px;\n  margin: 0 2px;\n  padding: 0 5px;\n  white-space: nowrap;\n}\n.markdown-body pre > code {\n  background: none repeat scroll 0 0 transparent;\n  border: medium none;\n  margin: 0;\n  padding: 0;\n  white-space: pre;\n}\n.markdown-body .highlight pre,\n.markdown-body pre {\n  background-color: #F8F8F8;\n  border: 1px solid #CCCCCC;\n  border-radius: 3px 3px 3px 3px;\n  font-size: 13px;\n  line-height: 19px;\n  overflow: auto;\n  padding: 6px 10px;\n}\n.markdown-body pre code,\n.markdown-body pre tt {\n  background-color: transparent;\n  border: medium none;\n}\n"; });
define('text!user/user-register.html', ['module'], function(module) { module.exports = "<template>\r\n    <require from=\"./user-register.css\"></require>\r\n    <div class=\"ui container tms-user-register\">\r\n        <div class=\"tms-flex\">\r\n            <div if.bind=\"!token\" ref=\"fm\" class=\"ui form segment\" style=\"width: 280px;\">\r\n                <div class=\"ui message\">提交账户注册信息成功后,我们会向您的注册邮箱发送一封账户激活邮件,激活账户后即可登录!</div>\r\n                <div class=\"required field\">\r\n                    <label>用户名</label>\r\n                    <input type=\"text\" name=\"username\" autofocus=\"\" value.bind=\"username\" placeholder=\"输入您的登录用户名\">\r\n                </div>\r\n                <div class=\"required field\">\r\n                    <label>密码</label>\r\n                    <input type=\"password\" name=\"pwd\" autofocus=\"\" value.bind=\"pwd\" placeholder=\"输入您的登录密码\">\r\n                </div>\r\n                <div class=\"required field\">\r\n                    <label>姓名</label>\r\n                    <input type=\"text\" name=\"name\" autofocus=\"\" value.bind=\"name\" placeholder=\"输入您的显示名称\">\r\n                </div>\r\n                <div class=\"required field\">\r\n                    <label>邮箱</label>\r\n                    <input type=\"text\" name=\"mail\" autofocus=\"\" value.bind=\"mail\" placeholder=\"输入您的账户激活邮箱\">\r\n                </div>\r\n                <div class=\"ui green fluid button ${isReq ? 'disabled' : ''}\" click.delegate=\"okHandler()\">确认</div>\r\n            </div>\r\n            <div if.bind=\"token\" class=\"ui center aligned very padded segment\" style=\"width: 320px;\">\r\n            \t<h1 class=\"ui header\">${header}</h1>\r\n            \t<a href=\"/admin/login\" class=\"ui green button\">返回登录页面</a>\r\n            </div>\r\n        </div>\r\n    </div>\r\n</template>\r\n"; });
define('text!user/user-login.css', ['module'], function(module) { module.exports = ".tms-user-login {\n  width: 100%;\n  min-height: 100%;\n  background-color: #5a3636;\n  overflow: hidden;\n}\n.tms-user-login .container {\n  width: 300px;\n  top: 50px;\n  margin-left: auto;\n  margin-right: auto;\n  position: relative;\n}\n.tms-user-login h2 {\n  color: rgba(197, 164, 164, 0.8) !important;\n}\n.tms-user-login .ui.form {\n  background-color: #353131;\n}\n.tms-user-login .ui.error.message {\n  background-color: #5a3636;\n}\n.tms-user-login .ui.error.message .header {\n  color: #e0b4b4;\n}\n.tms-user-login .ui.checkbox label {\n  color: #ad8b8b;\n}\n.tms-user-login .ui.checkbox input:focus ~ label {\n  color: #ad8b8b;\n}\n.tms-user-login .ui.checkbox label:hover {\n  color: #ad8b8b;\n}\n.tms-user-login .ui.button {\n  background-color: #5a3636;\n  color: #ad8b75;\n}\n"; });
define('text!resources/elements/em-confirm-modal.html', ['module'], function(module) { module.exports = "<template>\r\n    <div ref=\"md\" class=\"ui small modal nx-ui-confirm tms-md450\">\r\n        <div class=\"header\">\r\n            ${config.title}\r\n        </div>\r\n        <div class=\"content\">\r\n            <i if.bind=\"config.warning\" class=\"large yellow warning sign icon\" style=\"float: left;\"></i>\r\n            <i if.bind=\"!config.warning\" class=\"large blue info circle icon\" style=\"float: left;\"></i>\r\n            <p style=\"margin-left: 20px;\">\r\n                <span innerhtml.bind=\"config.content\"></span>\r\n            </p>\r\n        </div>\r\n        <div class=\"actions\">\r\n            <div class=\"ui cancel basic blue left floated button\">取消</div>\r\n            <div class=\"ui ok blue button\">确认</div>\r\n        </div>\r\n    </div>\r\n</template>\r\n"; });
define('text!user/user-pwd-reset.css', ['module'], function(module) { module.exports = ".tms-user-pwd-reset {\n  height: 100%;\n}\n.tms-user-pwd-reset .tms-flex {\n  height: 100%;\n  display: flex;\n  justify-content: center;\n  align-items: center;\n}\n"; });
define('text!resources/elements/em-hotkeys-modal.html', ['module'], function(module) { module.exports = "<template>\n    <require from=\"./em-hotkeys-modal.css\"></require>\n    <div ref=\"md\" class=\"ui basic modal tms-em-hotkeys-modal\">\n        <i class=\"close icon\"></i>\n        <!-- <div class=\"header\">\n            Archive Old Messages\n        </div> -->\n        <div class=\"content\">\n            <h1 class=\"ui center inverted aligned header\">键盘快捷键\n\t\t\t\t<span style=\"position: relative; top: -0.375rem; left: 1rem;\" aria-hidden=\"true\">\n\t\t\t\t\t<span class=\"keyboard\" aria-label=\"Control\">Ctrl</span>\n\t\t\t\t\t<span class=\"keyboard\" aria-label=\"Question mark\">/</span>\n\t\t\t\t</span>\n            </h1>\n            <div class=\"ui grid\">\n                <div class=\"three column row\">\n                    <div class=\"column\">\n                        <ul class=\"no_bullets\">\n                            <li>上一条: <span class=\"keyboard\">Alt</span><span class=\"keyboard\"><i class=\"long arrow up icon\" aria-label=\"Up arrow\"></i></span></li>\n                            <li>下一条: <span class=\"keyboard\">Alt</span><span class=\"keyboard\"><i class=\"long arrow down icon\" aria-label=\"Down arrow\"></i></span></li>\n                            <li>上一条未读: <span class=\"keyboard\">Alt</span><span class=\"keyboard\">Shift</span><span class=\"keyboard\"><i class=\"long arrow up icon\" aria-label=\"Up arrow\"></i></span></li>\n                            <li>下一条未读: <span class=\"keyboard\">Alt</span><span class=\"keyboard\">Shift</span><span class=\"keyboard\"><i class=\"long arrow down icon\" aria-label=\"Down arrow\"></i></span></li>\n                            <li>历史回退: <span class=\"keyboard\">Alt</span><span class=\"keyboard\"><i class=\"long arrow left icon\" aria-label=\"Left arrow\"></i></span></li>\n                            <li>历史向前: <span class=\"keyboard\">Alt</span><span class=\"keyboard\"><i class=\"long arrow right icon\" aria-label=\"Right arrow\"></i></span></li>\n                            <li>标记已读: <span class=\"keyboard\" aria-label=\"Escape\">Esc</span></li>\n                            <li>全部标记已读: <span class=\"keyboard\">Shift</span><span class=\"keyboard\" aria-label=\"Escape\">Esc</span></li>\n                            <li>快速切换: <span class=\"keyboard\" aria-label=\"Control\">Ctrl</span><span class=\"keyboard\">k</span></li>\n                            <li>Browse DMs: <span class=\"keyboard\" aria-label=\"Control\">Ctrl</span><span class=\"keyboard\">Shift</span><span class=\"keyboard\">k</span></li>\n                        </ul>\n                    </div>\n                    <div class=\"column\">\n                        <ul class=\"no_bullets\">\n                            <li>\n                                自动补全\n                                <ul>\n                                    <li>名称: <span class=\"subtle_silver\">[a-z]</span><span class=\"keyboard\">Tab</span> <span class=\"subtle_silver\">or</span> <span class=\"keyboard\">@</span><span class=\"keyboard\">Tab</span></li>\n                                    <li>频道: <span class=\"keyboard\" aria-label=\"Number symbol\">#</span><span class=\"keyboard\">Tab</span></li>\n                                    <li>表情: <span class=\"keyboard\" aria-label=\"Colon\">:</span><span class=\"keyboard\">Tab</span></li>\n                                </ul>\n                            </li>\n                            <li>换行: <span class=\"keyboard\">Shift</span><span class=\"keyboard\">Enter</span></li>\n                            <li>编辑上一条: <span class=\"keyboard\"><i class=\"long arrow up icon\" aria-label=\"Up arrow\"></i></span> <span class=\"subtle_silver\">in input</span></li>\n                            <li>响应最后一条: <span class=\"keyboard\" aria-label=\"control\">Ctrl</span><span class=\"keyboard\">Shift</span><span class=\"keyboard\">\\</span></li>\n                        </ul>\n                    </div>\n                    <div class=\"column\">\n                        <ul class=\"no_bullets\">\n                            <li>切换边栏: <span class=\"keyboard\" aria-label=\"Control\">Ctrl</span><span class=\"keyboard\">.</span></li>\n                            <ul>\n                                <li>团队: <span class=\"keyboard\" aria-label=\"Control\">Ctrl</span><span class=\"keyboard\">Shift</span><span class=\"keyboard\">e</span></li>\n                                <li>标星: <span class=\"keyboard\" aria-label=\"Control\">Ctrl</span><span class=\"keyboard\">Shift</span><span class=\"keyboard\">s</span></li>\n                            </ul>\n                            <li>粘贴代码片段: <span class=\"keyboard\" aria-label=\"Control\">Ctrl</span><span class=\"keyboard\">Shift</span><span class=\"keyboard\">Enter</span></li>\n                            <li>上传文件: <span class=\"keyboard\" aria-label=\"Control\">Ctrl</span><span class=\"keyboard\">u</span></li>\n                            <li>关闭对话框: <span class=\"keyboard\" aria-label=\"Escape\">Esc</span></li>\n                        </ul>\n                    </div>\n                </div>\n            </div>\n            <!-- <div class=\"image\">\n                <i class=\"archive icon\"></i>\n            </div>\n            <div class=\"description\">\n                <p>Your inbox is getting full, would you like us to enable automatic archiving of old messages?</p>\n            </div> -->\n        </div>\n        <!-- <div class=\"actions\">\n            <div class=\"two fluid ui inverted buttons\">\n                <div class=\"ui cancel red basic inverted button\">\n                    <i class=\"remove icon\"></i> No\n                </div>\n                <div class=\"ui ok green basic inverted button\">\n                    <i class=\"checkmark icon\"></i> Yes\n                </div>\n            </div>\n        </div> -->\n    </div>\n</template>\n"; });
define('text!user/user-register.css', ['module'], function(module) { module.exports = ".tms-user-register {\n  height: 100%;\n}\n.tms-user-register .tms-flex {\n  height: 100%;\n  display: flex;\n  justify-content: center;\n  align-items: center;\n}\n"; });
define('text!resources/elements/em-hotkeys-modal.css', ['module'], function(module) { module.exports = ".tms-em-hotkeys-modal ul {\n  padding-left: 30px;\n}\n.tms-em-hotkeys-modal ul.no_bullets {\n  margin: 0 0 2rem;\n}\n.tms-em-hotkeys-modal ul.no_bullets li {\n  line-height: 2rem;\n  list-style-type: none;\n  padding: 0;\n  font-size: 1rem;\n  font-weight: 700;\n}\n.tms-em-hotkeys-modal > .content {\n  background-color: rgba(11, 7, 11, 0.78) !important;\n}\n.tms-em-hotkeys-modal .keyboard i.icon {\n  margin-right: 0px!important;\n}\n.tms-em-hotkeys-modal .subtle_silver {\n  color: #9e9ea6!important;\n}\n.tms-em-hotkeys-modal .ui.grid .column {\n  padding: 0!important;\n}\n"; });
//# sourceMappingURL=app-bundle.js.map