export class CommonUtils {

    /**
     * 获取浏览器BaseUrl
     * @return {[type]} [description]
     */
    getBaseUrl() {
        if (typeof wurl == 'function') {
            if (wurl('port') == 80 || wurl('port') == 443) {
                return (wurl('protocol') + '://' + wurl('hostname'));
            } else {
                return (wurl('protocol') + '://' + wurl('hostname') + ':' + wurl('port'));
            }
        }
        return '';
    }

    /**
     * 获取URL hash
     * @return {[type]} [description]
     */
    getHash() {
        let hash = wurl('hash');
        let index = hash.indexOf('?');
        if (index != -1) {
            return hash.substring(0, index);
        }

        return hash;
    }

    /**
     * 获取url中的查询参数值
     * @param  {[type]} name 查询参数名称
     * @return {[type]}      查询参数值
     */
    urlQuery(name) {
        return wurl('?' + name) || wurl('?' + name, wurl('hash'));
    }

    /**
     * 移除url中的指定查询参数
     * name: 查询参数名称
     * href: 操作的url(可选, 不设置时为当前浏览器页面地址)
     * return: 移除指定查询参数的url地址
     */
    removeUrlQuery(name, href) {

        var s = href ? href : window.location.href;

        var rs = new RegExp('(&|\\?)?' + name + '=?[^&#]*(.)?', 'g').exec(s);
        // eg: ["?accessToken=YUNqUkxiZ3owWXdYdDFaVUp2VmNEM0JTZTNERlowWUhPTUVVbDU1RUROOWROMmcwUlVJeXRGQ2M4ZVBqdmpkSA%3D%3D&", "?", "&"]

        if (rs) {
            // case3: ?name2=value2&name=value => ?name2=value2
            // case4: ?name2=value2&name=value&name3=value3 => ?name2=value2&name3=value3
            if (rs[1] == '&') {
                return s.replace(new RegExp('&' + name + '=?[^&#]+', 'g'), '');
            } else if (rs[1] == '?') {
                if (rs[2] != '&') { // case1: ?name=value => 
                    return s.replace(new RegExp('\\?' + name + '=?[^&#]*', 'g'), '');
                } else { // case2: ?name=value&name2=value2 => ?name2=value2
                    return s.replace(new RegExp('' + name + '=?[^&#]*&', 'g'), '');
                }
            }
        }

        return s;
    }

    /**
     * 网络连接错误后自动重试
     * @param  {Function} callback 重试回调
     * @return {[type]}            [description]
     */
    errorAutoTry(callback, time) {

        if(this.isRunning) {
            return;
        }

        let cnt = time ? time : 10;
        let timer = null;
        let $t = toastr.error(`网络连接错误,${cnt}秒后自动重试!`, null, {
            "closeButton": false,
            "timeOut": "0",
            "preventDuplicates": false,
            "onclick": () => {
                clearInterval(this.timer);
                callback && callback();
            }
        });

        this.isRunning = true;
        timer = setInterval(() => {
            if (cnt === 0) {
                clearInterval(timer);
                this.isRunning = false;
                toastr.remove();
                callback && callback();
                return;
            }
            $t && $t.find('.toast-message').text(`网络连接错误,${cnt}秒后自动重试!`);
            cnt--;
        }, 1000);
    }
}

export default new CommonUtils();
