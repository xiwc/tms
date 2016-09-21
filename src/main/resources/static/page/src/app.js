import 'tms-semantic-ui';
import {
    default as toastr
}
from 'toastr';
import {
    default as wurl
}
from 'wurl';
import 'lodash';
import utils from 'common/common-utils';

export class App {
    constructor() {
        window.toastr = toastr;
        window.wurl = wurl;
        window.utils = utils;
        this.init();
    }

    init() {
        // ui form 验证提示信息国际化
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
    }

    /**
     * 配置路由
     * @param  {[object]} config 路由配置
     * @param  {[object]} router 路由
     */
    configureRouter(config, router) {

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

    }
}