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
define('chat/chat-direct',['exports', 'aurelia-framework', 'common/common-poll', 'marked', 'jquery.scrollto'], function (exports, _aureliaFramework, _commonPoll, _marked) {
    'use strict';

    Object.defineProperty(exports, "__esModule", {
        value: true
    });
    exports.ChatDirect = undefined;

    var _commonPoll2 = _interopRequireDefault(_commonPoll);

    var _marked2 = _interopRequireDefault(_marked);

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

            _marked2.default.setOptions({
                breaks: true
            });
        }

        ChatDirect.prototype.convertMd = function convertMd(chats) {
            _.each(chats, function (item) {
                item.contentMd = (0, _marked2.default)(item.content);
            });
            return chats;
        };

        ChatDirect.prototype.activate = function activate(params, routeConfig, navigationInstruction) {
            this.init(params.username);
        };

        ChatDirect.prototype.switchChatToHandler = function switchChatToHandler(chatTo) {
            this.init(chatTo);
            _commonPoll2.default.reset();
            return true;
        };

        ChatDirect.prototype.init = function init(chatTo) {
            var _this = this;

            this.chatTo = chatTo;

            $.get('/admin/chat/direct/list', {
                size: 20,
                chatTo: chatTo
            }, function (data) {
                if (data.success) {
                    _this.chats = _.reverse(_this.convertMd(data.data.content));
                    _.defer(function () {
                        $('html,body').scrollTo('max');
                    });
                } else {
                    toastr.error(data.data, '获取消息失败!');
                }
            });
        };

        ChatDirect.prototype.bind = function bind(ctx) {
            var _this2 = this;

            $.get('/admin/user/all', {
                enabled: true
            }, function (data) {
                if (data.success) {
                    _this2.users = data.data;
                } else {
                    toastr.error(data.data, '获取全部用户失败!');
                }
            });

            _commonPoll2.default.start(function () {
                $.get('/admin/chat/direct/latest', {
                    id: _.last(_this2.chats).id,
                    chatTo: _this2.chatTo
                }, function (data) {
                    if (data.success) {
                        _this2.chats = _.unionBy(_this2.chats, _this2.convertMd(data.data), 'id');
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

        ChatDirect.prototype.sendKeydownHandler = function sendKeydownHandler(evt) {
            var _this3 = this;

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
                    contentHtml: (0, _marked2.default)(this.content)
                }, function (data, textStatus, xhr) {
                    if (data.success) {
                        _this3.content = '';
                        _commonPoll2.default.reset();
                    } else {
                        toastr.error(data.data, '发送消息失败!');
                    }
                }).always(function () {
                    _this3.sending = false;
                });

                return false;
            } else if (evt.ctrlKey && evt.keyCode === 13) {
                this.content += '\r\n';
            }

            return true;
        };

        return ChatDirect;
    }(), (_descriptor = _applyDecoratedDescriptor(_class.prototype, 'content', [_aureliaFramework.bindable], {
        enumerable: true,
        initializer: null
    })), _class);
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
                throw new Error('轮询实例已经启动!');
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

        CommonUtils.prototype.urlQuery = function urlQuery(name) {
            return wurl('?' + name) || wurl('?' + name, wurl('hash'));
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

        aurelia.globalResources(['resources/value-converters/common-vc']);
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

            this.username = 'admin';
            this.password = '88888888';
        }

        UserLogin.prototype.loginHandler = function loginHandler() {
            var _this = this;

            $.get('/admin/login', function (data) {

                $.post('/admin/signin', {
                    username: _this.username,
                    password: _this.password,
                    "remember-me": 'on'
                }).always(function () {
                    toastr.success('登录成功!');
                    window.location = '/';
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
define('resources/value-converters/common-vc',['exports', 'jquery-format'], function (exports) {
    'use strict';

    Object.defineProperty(exports, "__esModule", {
        value: true
    });
    exports.NumberValueConverter = exports.DateValueConverter = exports.LowerValueConverter = exports.UpperValueConverter = undefined;

    function _classCallCheck(instance, Constructor) {
        if (!(instance instanceof Constructor)) {
            throw new TypeError("Cannot call a class as a function");
        }
    }

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
});
define('text!app.html', ['module'], function(module) { module.exports = "<template>\r\n\t<require from=\"./app.css\"></require>\r\n\t<require from=\"nprogress/nprogress.css\"></require>\r\n\t<require from=\"toastr/build/toastr.css\"></require>\r\n    <require from=\"tms-semantic-ui/semantic.min.css\"></require>\r\n    <router-view></router-view>\r\n</template>\r\n"; });
define('text!app.css', ['module'], function(module) { module.exports = "html,\r\nbody {\r\n    height: 100%;\r\n}\r\n\r\n::-webkit-scrollbar {\r\n    width: 6px;\r\n    height: 6px;\r\n}\r\n\r\n::-webkit-scrollbar-thumb {\r\n    border-radius: 6px;\r\n    background-color: #c6c6c6;\r\n}\r\n\r\n::-webkit-scrollbar-thumb:hover {\r\n    background: #999;\r\n}\r\n"; });
define('text!chat/chat-direct.html', ['module'], function(module) { module.exports = "<template>\r\n    <require from=\"./chat-direct.css\"></require>\r\n    <require from=\"./md-github.css\"></require>\r\n    <div class=\"tms-chat-direct\">\r\n        <div class=\"ui top fixed menu\">\r\n            <!-- <div class=\"item\">\r\n                <i class=\"icon link sidebar\"></i>\r\n            </div> -->\r\n            <a class=\"item header\">@charts</a>\r\n            <div class=\"right menu\">\r\n                <a class=\"item\">...</a>\r\n            </div>\r\n        </div>\r\n        <div class=\"ui left visible segment sidebar\">\r\n            <div class=\"tms-header\"></div>\r\n            <div class=\"ui middle aligned selection list\">\r\n                <a repeat.for=\"item of users\" click.delegate=\"switchChatToHandler(item.username)\" href=\"#/chat-direct/${item.username}\" class=\"item ${item.username == chatTo ? 'active' : ''}\" data-id=\"${item.username}\">\r\n                    <i class=\"user icon\"></i>\r\n                    <div class=\"content\">\r\n                        <div class=\"header\">${item.name}</div>\r\n                    </div>\r\n                </a>\r\n            </div>\r\n        </div>\r\n        <div class=\"tms-content\">\r\n            <div class=\"tms-col w65\">\r\n                <div class=\"ui basic segment minimal selection list segment comments\">\r\n                    <!-- <h3 class=\"ui dividing header\">私聊内容</h3> -->\r\n                    <div repeat.for=\"item of chats\" class=\"comment item\" data-id=\"${item.id}\">\r\n                        <a class=\"avatar\">\r\n                            <i class=\"user icon\"></i>\r\n                        </a>\r\n                        <div class=\"content\">\r\n                            <a class=\"author\">${item.chatTo.name}</a>\r\n                            <div class=\"metadata\">\r\n                                <div class=\"date\">${item.createDate | date:'MM/dd hh:mm:ss'}</div>\r\n                            </div>\r\n                            <div class=\"text markdown-body\" innerhtml.bind=\"item.contentMd\"></div>\r\n                            <div class=\"actions\">\r\n                                <a class=\"save\">复制</a>\r\n                                <a class=\"hide\">分享</a>\r\n                            </div>\r\n                        </div>\r\n                    </div>\r\n                </div>\r\n                <div class=\"ui basic segment tms-msg-input\">\r\n                    <div class=\"ui left action fluid icon input\">\r\n                        <button class=\"ui icon button\">\r\n                            <i class=\"plus icon\"></i>\r\n                        </button>\r\n                        <textarea value.bind=\"content\" keydown.trigger=\"sendKeydownHandler($event)\" rows=\"1\"></textarea>\r\n                        <i class=\"smile link icon\"></i>\r\n                    </div>\r\n                </div>\r\n            </div>\r\n            <div class=\"tms-col w35\"></div>\r\n        </div>\r\n    </div>\r\n</template>\r\n"; });
define('text!chat/chat-direct.css', ['module'], function(module) { module.exports = ".tms-chat-direct {\r\n    height: 100%;\r\n}\r\n\r\n.tms-chat-direct .top.fixed.menu {\r\n    padding-left: 220px;\r\n    height: 60px;\r\n}\r\n\r\n.tms-chat-direct .ui.left.sidebar {\r\n    width: 220px;\r\n}\r\n\r\n.tms-chat-direct .ui.left.sidebar .tms-header {\r\n    height: 40px;\r\n}\r\n\r\n.tms-chat-direct .tms-content {\r\n    padding-top: 60px;\r\n    padding-left: 220px;\r\n    display: flex;\r\n    align-items: stretch;\r\n    height: 100%;\r\n}\r\n\r\n.tms-chat-direct .tms-content .tms-col.w65 {\r\n    flex: auto;\r\n}\r\n\r\n.tms-chat-direct .tms-content .tms-col.w35 {\r\n    width: 380px;\r\n    border-left: 1px #e0e1e2 solid;\r\n}\r\n\r\n.tms-chat-direct .ui.basic.segment.tms-msg-input {\r\n    position: fixed;\r\n    bottom: 0;\r\n    right: 30%;\r\n    left: 220px;\r\n    background-color: white;\r\n}\r\n\r\n.tms-chat-direct .ui.comments {\r\n    margin-top: 20px;\r\n    margin-bottom: 50px;\r\n    max-width: none;\r\n}\r\n\r\n.tms-chat-direct .tms-msg-input .ui[class*=\"left action\"].input>textarea {\r\n    border-top-left-radius: 0!important;\r\n    border-bottom-left-radius: 0!important;\r\n    border-left-color: transparent!important;\r\n}\r\n\r\n.tms-chat-direct .tms-msg-input .ui.icon.input textarea {\r\n    padding-right: 2.67142857em!important;\r\n}\r\n\r\n.tms-chat-direct .tms-msg-input .ui.input textarea {\r\n    margin: 0;\r\n    max-width: 100%;\r\n    -webkit-box-flex: 1;\r\n    -webkit-flex: 1 0 auto;\r\n    -ms-flex: 1 0 auto;\r\n    flex: 1 0 auto;\r\n    outline: 0;\r\n    -webkit-tap-highlight-color: rgba(255, 255, 255, 0);\r\n    text-align: left;\r\n    line-height: 1.2142em;\r\n    padding: .67861429em 1em;\r\n    background: #FFF;\r\n    border: 1px solid rgba(34, 36, 38, .15);\r\n    color: rgba(0, 0, 0, .87);\r\n    border-radius: .28571429rem;\r\n    box-shadow: none;\r\n}\r\n"; });
define('text!user/user-login.html', ['module'], function(module) { module.exports = "<template>\r\n    <require from=\"./user-login.css\"></require>\r\n    <div class=\"ui tms-user-login\">\r\n        <div class=\"ui form segment\">\r\n            <div class=\"field\">\r\n                <label>用户名:</label>\r\n                <input type=\"text\" value.bind=\"username\">\r\n            </div>\r\n            <div class=\"field\">\r\n                <label>密码:</label>\r\n                <input type=\"password\" value.bind=\"password\">\r\n            </div>\r\n            <div class=\"ui green fluid button ${isReq ? 'disabled' : ''}\" click.delegate=\"loginHandler()\">登录</div>\r\n        </div>\r\n    </div>\r\n</template>\r\n"; });
define('text!chat/md-github.css', ['module'], function(module) { module.exports = ".markdown-body {\r\n    font-size: 14px;\r\n    line-height: 1.6;\r\n}\r\n\r\n.markdown-body > *:first-child {\r\n    margin-top: 0 !important;\r\n}\r\n\r\n.markdown-body > *:last-child {\r\n    margin-bottom: 0 !important;\r\n}\r\n\r\n.markdown-body a.absent {\r\n    color: #CC0000;\r\n}\r\n\r\n.markdown-body a.anchor {\r\n    bottom: 0;\r\n    cursor: pointer;\r\n    display: block;\r\n    left: 0;\r\n    margin-left: -30px;\r\n    padding-left: 30px;\r\n    position: absolute;\r\n    top: 0;\r\n}\r\n\r\n.markdown-body h1,\r\n.markdown-body h2,\r\n.markdown-body h3,\r\n.markdown-body h4,\r\n.markdown-body h5,\r\n.markdown-body h6 {\r\n    cursor: text;\r\n    font-weight: bold;\r\n    margin: 20px 0 10px;\r\n    padding: 0;\r\n    position: relative;\r\n}\r\n\r\n.markdown-body h1 .mini-icon-link,\r\n.markdown-body h2 .mini-icon-link,\r\n.markdown-body h3 .mini-icon-link,\r\n.markdown-body h4 .mini-icon-link,\r\n.markdown-body h5 .mini-icon-link,\r\n.markdown-body h6 .mini-icon-link {\r\n    color: #000000;\r\n    display: none;\r\n}\r\n\r\n.markdown-body h1:hover a.anchor,\r\n.markdown-body h2:hover a.anchor,\r\n.markdown-body h3:hover a.anchor,\r\n.markdown-body h4:hover a.anchor,\r\n.markdown-body h5:hover a.anchor,\r\n.markdown-body h6:hover a.anchor {\r\n    line-height: 1;\r\n    margin-left: -22px;\r\n    padding-left: 0;\r\n    text-decoration: none;\r\n    top: 15%;\r\n}\r\n\r\n.markdown-body h1:hover a.anchor .mini-icon-link,\r\n.markdown-body h2:hover a.anchor .mini-icon-link,\r\n.markdown-body h3:hover a.anchor .mini-icon-link,\r\n.markdown-body h4:hover a.anchor .mini-icon-link,\r\n.markdown-body h5:hover a.anchor .mini-icon-link,\r\n.markdown-body h6:hover a.anchor .mini-icon-link {\r\n    display: inline-block;\r\n}\r\n\r\n.markdown-body h1 tt,\r\n.markdown-body h1 code,\r\n.markdown-body h2 tt,\r\n.markdown-body h2 code,\r\n.markdown-body h3 tt,\r\n.markdown-body h3 code,\r\n.markdown-body h4 tt,\r\n.markdown-body h4 code,\r\n.markdown-body h5 tt,\r\n.markdown-body h5 code,\r\n.markdown-body h6 tt,\r\n.markdown-body h6 code {\r\n    font-size: inherit;\r\n}\r\n\r\n.markdown-body h1 {\r\n    color: #000000;\r\n    font-size: 28px;\r\n}\r\n\r\n.markdown-body h2 {\r\n    border-bottom: 1px solid #CCCCCC;\r\n    color: #000000;\r\n    font-size: 24px;\r\n}\r\n\r\n.markdown-body h3 {\r\n    font-size: 18px;\r\n}\r\n\r\n.markdown-body h4 {\r\n    font-size: 16px;\r\n}\r\n\r\n.markdown-body h5 {\r\n    font-size: 14px;\r\n}\r\n\r\n.markdown-body h6 {\r\n    color: #777777;\r\n    font-size: 14px;\r\n}\r\n\r\n.markdown-body p,\r\n.markdown-body blockquote,\r\n.markdown-body ul,\r\n.markdown-body ol,\r\n.markdown-body dl,\r\n.markdown-body table,\r\n.markdown-body pre {\r\n    margin: 15px 0;\r\n}\r\n\r\n.markdown-body hr {\r\n    overflow: hidden;\r\n    background: 0 0\r\n}\r\n\r\n.markdown-body hr:before {\r\n    display: table;\r\n    content: \"\"\r\n}\r\n\r\n.markdown-body hr:after {\r\n    display: table;\r\n    clear: both;\r\n    content: \"\"\r\n}\r\n\r\n.markdown-body hr {\r\n    height: 4px;\r\n    padding: 0;\r\n    margin: 16px 0;\r\n    background-color: #e7e7e7;\r\n    border: 0\r\n}\r\n\r\n.markdown-body hr {\r\n    -moz-box-sizing: content-box;\r\n    box-sizing: content-box\r\n}\r\n\r\n.markdown-body > h2:first-child,\r\n.markdown-body > h1:first-child,\r\n.markdown-body > h1:first-child + h2,\r\n.markdown-body > h3:first-child,\r\n.markdown-body > h4:first-child,\r\n.markdown-body > h5:first-child,\r\n.markdown-body > h6:first-child {\r\n    margin-top: 0;\r\n    padding-top: 0;\r\n}\r\n\r\n.markdown-body a:first-child h1,\r\n.markdown-body a:first-child h2,\r\n.markdown-body a:first-child h3,\r\n.markdown-body a:first-child h4,\r\n.markdown-body a:first-child h5,\r\n.markdown-body a:first-child h6 {\r\n    margin-top: 0;\r\n    padding-top: 0;\r\n}\r\n\r\n.markdown-body h1 + p,\r\n.markdown-body h2 + p,\r\n.markdown-body h3 + p,\r\n.markdown-body h4 + p,\r\n.markdown-body h5 + p,\r\n.markdown-body h6 + p {\r\n    margin-top: 0;\r\n}\r\n\r\n.markdown-body li p.first {\r\n    display: inline-block;\r\n}\r\n\r\n.markdown-body ul,\r\n.markdown-body ol {\r\n    padding-left: 30px;\r\n}\r\n\r\n.markdown-body ul.no-list,\r\n.markdown-body ol.no-list {\r\n    list-style-type: none;\r\n    padding: 0;\r\n}\r\n\r\n.markdown-body ul li > *:first-child,\r\n.markdown-body ol li > *:first-child {\r\n    margin-top: 0;\r\n}\r\n\r\n.markdown-body ul ul,\r\n.markdown-body ul ol,\r\n.markdown-body ol ol,\r\n.markdown-body ol ul {\r\n    margin-bottom: 0;\r\n}\r\n\r\n.markdown-body dl {\r\n    padding: 0;\r\n}\r\n\r\n.markdown-body dl dt {\r\n    font-size: 14px;\r\n    font-style: italic;\r\n    font-weight: bold;\r\n    margin: 15px 0 5px;\r\n    padding: 0;\r\n}\r\n\r\n.markdown-body dl dt:first-child {\r\n    padding: 0;\r\n}\r\n\r\n.markdown-body dl dt > *:first-child {\r\n    margin-top: 0;\r\n}\r\n\r\n.markdown-body dl dt > *:last-child {\r\n    margin-bottom: 0;\r\n}\r\n\r\n.markdown-body dl dd {\r\n    margin: 0 0 15px;\r\n    padding: 0 15px;\r\n}\r\n\r\n.markdown-body dl dd > *:first-child {\r\n    margin-top: 0;\r\n}\r\n\r\n.markdown-body dl dd > *:last-child {\r\n    margin-bottom: 0;\r\n}\r\n\r\n.markdown-body blockquote {\r\n    border-left: 4px solid #DDDDDD;\r\n    color: #777777;\r\n    padding: 0 15px;\r\n}\r\n\r\n.markdown-body blockquote > *:first-child {\r\n    margin-top: 0;\r\n}\r\n\r\n.markdown-body blockquote > *:last-child {\r\n    margin-bottom: 0;\r\n}\r\n\r\n.markdown-body table th {\r\n    font-weight: bold;\r\n}\r\n\r\n.markdown-body table th,\r\n.markdown-body table td {\r\n    border: 1px solid #CCCCCC;\r\n    padding: 6px 13px;\r\n}\r\n\r\n.markdown-body table tr {\r\n    background-color: #FFFFFF;\r\n    border-top: 1px solid #CCCCCC;\r\n}\r\n\r\n.markdown-body table tr:nth-child(2n) {\r\n    background-color: #F8F8F8;\r\n}\r\n\r\n.markdown-body img {\r\n    max-width: 100%;\r\n}\r\n\r\n.markdown-body span.frame {\r\n    display: block;\r\n    overflow: hidden;\r\n}\r\n\r\n.markdown-body span.frame > span {\r\n    border: 1px solid #DDDDDD;\r\n    display: block;\r\n    float: left;\r\n    margin: 13px 0 0;\r\n    overflow: hidden;\r\n    padding: 7px;\r\n    width: auto;\r\n}\r\n\r\n.markdown-body span.frame span img {\r\n    display: block;\r\n    float: left;\r\n}\r\n\r\n.markdown-body span.frame span span {\r\n    clear: both;\r\n    color: #333333;\r\n    display: block;\r\n    padding: 5px 0 0;\r\n}\r\n\r\n.markdown-body span.align-center {\r\n    clear: both;\r\n    display: block;\r\n    overflow: hidden;\r\n}\r\n\r\n.markdown-body span.align-center > span {\r\n    display: block;\r\n    margin: 13px auto 0;\r\n    overflow: hidden;\r\n    text-align: center;\r\n}\r\n\r\n.markdown-body span.align-center span img {\r\n    margin: 0 auto;\r\n    text-align: center;\r\n}\r\n\r\n.markdown-body span.align-right {\r\n    clear: both;\r\n    display: block;\r\n    overflow: hidden;\r\n}\r\n\r\n.markdown-body span.align-right > span {\r\n    display: block;\r\n    margin: 13px 0 0;\r\n    overflow: hidden;\r\n    text-align: right;\r\n}\r\n\r\n.markdown-body span.align-right span img {\r\n    margin: 0;\r\n    text-align: right;\r\n}\r\n\r\n.markdown-body span.float-left {\r\n    display: block;\r\n    float: left;\r\n    margin-right: 13px;\r\n    overflow: hidden;\r\n}\r\n\r\n.markdown-body span.float-left span {\r\n    margin: 13px 0 0;\r\n}\r\n\r\n.markdown-body span.float-right {\r\n    display: block;\r\n    float: right;\r\n    margin-left: 13px;\r\n    overflow: hidden;\r\n}\r\n\r\n.markdown-body span.float-right > span {\r\n    display: block;\r\n    margin: 13px auto 0;\r\n    overflow: hidden;\r\n    text-align: right;\r\n}\r\n\r\n.markdown-body code,\r\n.markdown-body tt {\r\n    background-color: #F8F8F8;\r\n    border: 1px solid #EAEAEA;\r\n    border-radius: 3px 3px 3px 3px;\r\n    margin: 0 2px;\r\n    padding: 0 5px;\r\n    white-space: nowrap;\r\n}\r\n\r\n.markdown-body pre > code {\r\n    background: none repeat scroll 0 0 transparent;\r\n    border: medium none;\r\n    margin: 0;\r\n    padding: 0;\r\n    white-space: pre;\r\n}\r\n\r\n.markdown-body .highlight pre,\r\n.markdown-body pre {\r\n    background-color: #F8F8F8;\r\n    border: 1px solid #CCCCCC;\r\n    border-radius: 3px 3px 3px 3px;\r\n    font-size: 13px;\r\n    line-height: 19px;\r\n    overflow: auto;\r\n    padding: 6px 10px;\r\n}\r\n\r\n.markdown-body pre code,\r\n.markdown-body pre tt {\r\n    background-color: transparent;\r\n    border: medium none;\r\n}\r\n"; });
define('text!user/user-pwd-reset.html', ['module'], function(module) { module.exports = "<template>\r\n    <require from=\"./user-pwd-reset.css\"></require>\r\n    <div class=\"ui container tms-user-pwd-reset\">\r\n        <div class=\"tms-flex\">\r\n            <div if.bind=\"!token\" ref=\"fm\" class=\"ui form segment\" style=\"width: 260px;\">\r\n                <div class=\"ui message\">输入您的邮箱地址,我们会发送密码重置链接到您的邮箱!</div>\r\n                <div class=\"field\">\r\n                    <label style=\"display:none;\">邮件地址</label>\r\n                    <input type=\"text\" name=\"mail\" autofocus=\"\" value.bind=\"mail\" placeholder=\"输入您的邮件地址\">\r\n                </div>\r\n                <div class=\"ui green fluid button ${isReq ? 'disabled' : ''}\" click.delegate=\"resetPwdHandler()\">发送密码重置邮件</div>\r\n            </div>\r\n            <div if.bind=\"token\" ref=\"fm2\" class=\"ui form segment\" style=\"width: 260px;\">\r\n                <div class=\"ui message\">设置您的新密码,密码长度要求至少8位字符!</div>\r\n                <div class=\"field\">\r\n                    <label style=\"display:none;\">新密码</label>\r\n                    <input type=\"password\" name=\"mail\" autofocus=\"\" value.bind=\"pwd\" placeholder=\"设置您的新密码\">\r\n                </div>\r\n                <div class=\"ui green fluid button ${isReq ? 'disabled' : ''}\" click.delegate=\"newPwdHandler()\">确认</div>\r\n            </div>\r\n        </div>\r\n    </div>\r\n</template>\r\n"; });
define('text!user/user-login.css', ['module'], function(module) { module.exports = ".tms-user-login {\r\n\tdisplay: flex;\r\n\theight: 100%;\r\n    display: flex;\r\n    justify-content: center;\r\n    align-items: center;\r\n}"; });
define('text!user/user-register.html', ['module'], function(module) { module.exports = "<template>\r\n    <require from=\"./user-register.css\"></require>\r\n    <div class=\"ui container tms-user-register\">\r\n        <div class=\"tms-flex\">\r\n            <div if.bind=\"!token\" ref=\"fm\" class=\"ui form segment\" style=\"width: 280px;\">\r\n                <div class=\"ui message\">提交账户注册信息成功后,我们会向您的注册邮箱发送一封账户激活邮件,激活账户后即可登录!</div>\r\n                <div class=\"required field\">\r\n                    <label>用户名</label>\r\n                    <input type=\"text\" name=\"username\" autofocus=\"\" value.bind=\"username\" placeholder=\"输入您的登录用户名\">\r\n                </div>\r\n                <div class=\"required field\">\r\n                    <label>密码</label>\r\n                    <input type=\"password\" name=\"pwd\" autofocus=\"\" value.bind=\"pwd\" placeholder=\"输入您的登录密码\">\r\n                </div>\r\n                <div class=\"required field\">\r\n                    <label>姓名</label>\r\n                    <input type=\"text\" name=\"name\" autofocus=\"\" value.bind=\"name\" placeholder=\"输入您的显示名称\">\r\n                </div>\r\n                <div class=\"required field\">\r\n                    <label>邮箱</label>\r\n                    <input type=\"text\" name=\"mail\" autofocus=\"\" value.bind=\"mail\" placeholder=\"输入您的账户激活邮箱\">\r\n                </div>\r\n                <div class=\"ui green fluid button ${isReq ? 'disabled' : ''}\" click.delegate=\"okHandler()\">确认</div>\r\n            </div>\r\n            <div if.bind=\"token\" class=\"ui center aligned very padded segment\" style=\"width: 320px;\">\r\n            \t<h1 class=\"ui header\">${header}</h1>\r\n            \t<a href=\"/admin/login\" class=\"ui green button\">返回登录页面</a>\r\n            </div>\r\n        </div>\r\n    </div>\r\n</template>\r\n"; });
define('text!user/user-pwd-reset.css', ['module'], function(module) { module.exports = ".tms-user-pwd-reset {\r\n    height: 100%;\r\n}\r\n\r\n.tms-user-pwd-reset .tms-flex {\r\n    height: 100%;\r\n    display: flex;\r\n    justify-content: center;\r\n    align-items: center;\r\n}\r\n"; });
define('text!user/user-register.css', ['module'], function(module) { module.exports = ".tms-user-register {\r\n    height: 100%;\r\n}\r\n\r\n.tms-user-register .tms-flex {\r\n    height: 100%;\r\n    display: flex;\r\n    justify-content: center;\r\n    align-items: center;\r\n}\r\n"; });
//# sourceMappingURL=app-bundle.js.map