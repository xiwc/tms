define('app',['exports', 'toastr', 'wurl', 'common/common-utils', 'wlzc-semantic-ui', 'lodash'], function (exports, _toastr, _wurl, _commonUtils) {
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
                route: '',
                redirect: 'pwd-reset'
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

        _config2.default.context(aurelia).initHttp().initToastr();
    }
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
                    username: ['empty'],
                    pwd: ['empty', 'minLength[8]'],
                    name: ['empty'],
                    mail: ['empty', 'email']
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
define('text!app.html', ['module'], function(module) { module.exports = "<template>\r\n\t<require from=\"./app.css\"></require>\r\n\t<require from=\"nprogress/nprogress.css\"></require>\r\n\t<require from=\"toastr/build/toastr.css\"></require>\r\n    <require from=\"wlzc-semantic-ui/semantic.css\"></require>\r\n    <router-view></router-view>\r\n</template>\r\n"; });
define('text!app.css', ['module'], function(module) { module.exports = "html,\r\nbody {\r\n    height: 100%;\r\n}\r\n"; });
define('text!user/user-pwd-reset.html', ['module'], function(module) { module.exports = "<template>\r\n    <require from=\"./user-pwd-reset.css\"></require>\r\n    <div class=\"ui container tms-user-pwd-reset\">\r\n        <div class=\"tms-flex\">\r\n            <div if.bind=\"!token\" ref=\"fm\" class=\"ui form segment\" style=\"width: 260px;\">\r\n                <div class=\"ui message\">输入您的邮箱地址,我们会发送密码重置链接到您的邮箱!</div>\r\n                <div class=\"field\">\r\n                    <label style=\"display:none;\">邮件地址</label>\r\n                    <input type=\"text\" name=\"mail\" autofocus=\"\" value.bind=\"mail\" placeholder=\"输入您的邮件地址\">\r\n                </div>\r\n                <div class=\"ui green fluid button ${isReq ? 'disabled' : ''}\" click.delegate=\"resetPwdHandler()\">发送密码重置邮件</div>\r\n            </div>\r\n            <div if.bind=\"token\" ref=\"fm2\" class=\"ui form segment\" style=\"width: 260px;\">\r\n                <div class=\"ui message\">设置您的新密码,密码长度要求至少8位字符!</div>\r\n                <div class=\"field\">\r\n                    <label style=\"display:none;\">新密码</label>\r\n                    <input type=\"password\" name=\"mail\" autofocus=\"\" value.bind=\"pwd\" placeholder=\"设置您的新密码\">\r\n                </div>\r\n                <div class=\"ui green fluid button ${isReq ? 'disabled' : ''}\" click.delegate=\"newPwdHandler()\">确认</div>\r\n            </div>\r\n        </div>\r\n    </div>\r\n</template>\r\n"; });
define('text!user/user-pwd-reset.css', ['module'], function(module) { module.exports = ".tms-user-pwd-reset {\r\n    height: 100%;\r\n}\r\n\r\n.tms-user-pwd-reset .tms-flex {\r\n    height: 100%;\r\n    display: flex;\r\n    justify-content: center;\r\n    align-items: center;\r\n}\r\n"; });
define('text!user/user-register.html', ['module'], function(module) { module.exports = "<template>\r\n    <require from=\"./user-register.css\"></require>\r\n    <div class=\"ui container tms-user-register\">\r\n        <div class=\"tms-flex\">\r\n            <div if.bind=\"!token\" ref=\"fm\" class=\"ui form segment\" style=\"width: 280px;\">\r\n                <div class=\"ui message\">填写完账户注册信息,点击确认提交成功后,我们会发送到您注册邮箱一封账户激活邮件,激活后账户即可登录使用!</div>\r\n                <div class=\"required field\">\r\n                    <label>用户名</label>\r\n                    <input type=\"text\" name=\"username\" autofocus=\"\" value.bind=\"username\" placeholder=\"输入您的登录用户名\">\r\n                </div>\r\n                <div class=\"required field\">\r\n                    <label>密码</label>\r\n                    <input type=\"password\" name=\"pwd\" autofocus=\"\" value.bind=\"pwd\" placeholder=\"输入您的登录密码\">\r\n                </div>\r\n                <div class=\"required field\">\r\n                    <label>姓名</label>\r\n                    <input type=\"text\" name=\"name\" autofocus=\"\" value.bind=\"name\" placeholder=\"输入您的显示名称\">\r\n                </div>\r\n                <div class=\"required field\">\r\n                    <label>邮箱</label>\r\n                    <input type=\"text\" name=\"mail\" autofocus=\"\" value.bind=\"mail\" placeholder=\"输入您的账户激活邮箱\">\r\n                </div>\r\n                <div class=\"ui green fluid button ${isReq ? 'disabled' : ''}\" click.delegate=\"okHandler()\">确认</div>\r\n            </div>\r\n            <div if.bind=\"token\" class=\"ui center aligned very padded segment\" style=\"width: 320px;\">\r\n            \t<h1 class=\"ui header\">${header}</h1>\r\n            \t<a href=\"/admin/login\" class=\"ui green button\">返回登录页面</a>\r\n            </div>\r\n        </div>\r\n    </div>\r\n</template>\r\n"; });
define('text!user/user-register.css', ['module'], function(module) { module.exports = ".tms-user-register {\r\n    height: 100%;\r\n}\r\n\r\n.tms-user-register .tms-flex {\r\n    height: 100%;\r\n    display: flex;\r\n    justify-content: center;\r\n    align-items: center;\r\n}\r\n"; });
//# sourceMappingURL=app-bundle.js.map