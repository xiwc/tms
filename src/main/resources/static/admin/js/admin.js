jQuery(function($) {

    $('.ad-index-btn-menu').click(function() {
        $('.ad-index-menu').sidebar('toggle');
    });

    if ($('#context').size() == 1 && $('.ad-index-rail').is(':visible')) {
        $('.ui.sticky.ad-index-sticky').sticky({
            offset: 70,
            observeChanges: true,
            context: '#context'
        });
    }

    $('.ad-index-logout').click(function() {
        $(this).children('form').submit();
    });

    $('.ad-index-user-edit').click(function() {
        var $btn = $(this);

        $.get('admin/user/get', { username: $btn.attr('data-id') },
            function(data) {
                if (data.success) {
                    $('.ad-user-own-edit').find('.user-username').text($btn.attr('data-id'));
                    $('.ad-user-own-edit').find('input[name="password"]').val('');
                    $('.ad-user-own-edit').find('input[name="mail"]').val(data.data.mails);
                    $('.ad-user-own-edit').find('input[name="name"]').val(data.data.name);

                    $('.ad-user-own-edit').modal({
                        onApprove: function() {
                            $.post('admin/user/update2', {
                                username: $btn.attr('data-id'),
                                password: $('.ad-user-own-edit').find('input[name="password"]').val(),
                                name: $('.ad-user-own-edit').find('input[name="name"]').val(),
                                mail: $('.ad-user-own-edit').find('input[name="mail"]').val()
                            }, function(data, textStatus, xhr) {
                                if (data.success) {
                                    toastr.success('个人信息修改成功!');
                                } else {
                                    toastr.error(data.data, '个人信息修改失败!');
                                }
                            });
                        }
                    }).modal('show');
                } else {
                    toastr.error('获取用户信息失败!');
                }
            });

    });

    $('.ui.accordion').accordion();
    $('.ui.checkbox').checkbox();
    $('.popup-login-user').popup({
        position: 'bottom right',
    });
    $('.ui.dropdown.dd-top-menu-user').dropdown({
        action: 'hide'
    });

    // load markdown help content
    if ($('.markdown-content').size() > 0) {
        var converter = new showdown.Converter();
        $('.markdown-content').each(function(index, el) {
            $.get($(el).attr('data-url'), function(data) {
                $('<div class="markdown-body"/>').html(converter.makeHtml(data)).appendTo(el);
            });
        });
    }

    // semantic-ui ajax api
    $.fn.api.settings.api = {
        'deleteFileById': 'admin/file/delete?id={id}',
        'updateFileName': 'admin/file/update?id={id}&name={name}',
        'saveArticle': 'admin/article/save',
        'updateArticle': 'admin/article/update?id={id}',
        'deleteArticleById': 'admin/article/delete?id={id}',
        'saveFeedback': 'admin/feedback/save'
    };

    $.fn.api.settings.successTest = function(resp) {
        if (resp && resp.success) {
            return resp.success;
        }
        return false;
    };

    // toastr notification options
    if ("undefined" != typeof toastr) {
        toastr.options = {
            "closeButton": true,
            "debug": false,
            "newestOnTop": true,
            "progressBar": false,
            "positionClass": "toast-bottom-center",
            "preventDuplicates": true,
            "onclick": null,
            "showDuration": "300",
            "hideDuration": "1000",
            "timeOut": "5000",
            "extendedTimeOut": "1000",
            "showEasing": "swing",
            "hideEasing": "linear",
            "showMethod": "fadeIn",
            "hideMethod": "fadeOut"
        };
    }

    $(document).ajaxSend(function(event, jqxhr, settings) {

        if (settings.url.lastIndexOf('/unmask') == -1) {
            // $('.ad-page-dimmer').addClass('active');
        }

        var csrf = {};
        csrf[$('.ad-csrf input:hidden').attr('name')] = $('.ad-csrf input:hidden').attr('value');

        if (!!settings.data) {
            settings.data = settings.data + "&" + $.param(csrf);
        } else {
            settings.data = $.param(csrf);
        }
    });

    $(document).on('ajaxStart', function() {
        NProgress && NProgress.start();
    });
    $(document).on('ajaxStop', function() {
        // $('.ad-page-dimmer').removeClass('active');
        NProgress && NProgress.done();
    });


    $('.ad-item-feedback').click(function(event) {
        event.stopImmediatePropagation();
        $(this).find('form').find(':hidden[name="name"]').val($('title').text()).end().submit();
    });

});

jQuery(function($) {
    // custom helper utils
    window.Utils = window.Utils || {};

    var _startId = 0;

    $.extend(window.Utils, {
        removeFileType: function(name) {
            var i = name.lastIndexOf('.');
            return (i != -1) ? name.substring(0, i) : name;
        },
        getFileType: function(name) {
            var i = name.lastIndexOf('.');
            return (i != -1) ? name.substring(i) : '';
        },
        abbreviate: function(str, len) {
            if (!!str && str.length > len) {
                return str.substring(0, len - 3) + "...";
            }

            return str;
        },
        formData: function(selector) {

            var data = {};
            $(selector).find('input[name],textarea[name]').each(function(index, el) {
                var name = $(el).attr('name');
                var val = $(el).val();
                data[name] = val;
            });

            return data;
        },
        remember: function() {
            localStorage && (typeof url == 'function') && localStorage.setItem(url('path'), url('path') + (url('query') ? ('?' + url('query')) : ''));
        },
        getRemember: function(name) {
            return localStorage && localStorage.getItem(name);
        },
        getBaseURL: function() {
            if (typeof url == 'function') {
                if (url('port') == 80 || url('port') == 443) {
                    return (url('protocol') + '://' + url('hostname') + '/');
                } else {
                    return (url('protocol') + '://' + url('hostname') + ':' + url('port') + '/');
                }
            }
            return '';
        },
        md2html: function(markdown) {
            if (showdown) {
                var converter = new showdown.Converter();
                return converter.makeHtml(markdown);
            }
            return markdown;
        },
        imgLoaded: function($imgs, callback) {
            var imgdefereds = [];
            $imgs.each(function() {
                var dfd = $.Deferred();
                $(this).bind('load', function() {
                    dfd.resolve();
                }).bind('error', function() {
                    //图片加载错误，加入错误处理
                    dfd.resolve();
                })
                if (this.complete) {
                    // setTimeout(function() {
                    //     dfd.resolve();
                    // }, 1000);
                    dfd.resolve();
                }

                imgdefereds.push(dfd);
            })
            $.when.apply(null, imgdefereds).done(function() {
                callback && callback.call(null);
            });
        },
        showMappedTxt: function(filterCls) {
            var userMap = {};
            $('input:hidden[data-group="users"]').each(function(index, el) {
                userMap[$(this).attr('name')] = $(this).attr('value');
            });

            $(filterCls).each(function(index, el) {
                var username = $(this).text();
                var name = userMap[username];
                if (name) {
                    $(this).text(name).removeClass(filterCls.replace('.', ''));
                }
            });
        },
        isElementInViewport: function(el) {

            //special bonus for those using jQuery
            if (typeof jQuery === "function" && el instanceof jQuery) {
                el = el[0];
            }

            var rect = el.getBoundingClientRect();

            return (
                rect.top >= 0 &&
                rect.left >= 0 &&
                rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) && /*or $(window).height() */
                rect.right <= (window.innerWidth || document.documentElement.clientWidth) /*or $(window).width() */
            );
        },
        catalog: function($e) {
            var $headers = $(":header", $e);
            var pre = null;

            var link = {
                pre: null,
                arr: []
            };
            var current = link;
            $headers.each(function(index, h) {
                var name = h.nodeName;
                if (!pre) {
                    current.arr.push(h);
                    pre = name;
                } else {
                    if (pre < name) {
                        var last = current;
                        current = {
                            pre: last,
                            arr: [h]
                        };
                        last.arr.push(current);
                        pre = name;
                    } else if (pre == name) {
                        current.arr.push(h);
                    } else {
                        current = current.pre ? current.pre : current;
                        current.arr.push(h);
                        pre = name;
                    }
                }
            });

            return link;
        },
        generateDir: function(link) {
            var $list = $('<div class="ui list"></div>');
            prodDir($list, link);
            return $list;
        },
        id: function(prefix) {
            return (prefix ? prefix : "tms") + '-' + (++_startId);
        },
        dir: function($e) {
            return this.generateDir(this.catalog($e));
        }
    });

    function prodDir($list, link) {
        $.each(link.arr, function(index, item) {
            if (item.hasOwnProperty('arr')) {
                var $l = $('<div class="list"></div>');
                $list.append($l)
                prodDir($l, item);
            } else {
                var id = Utils.id();
                var $item = $('<a class="item wiki-dir-item"></a>').text($(item).attr('id', id).text()).attr('data-id', id);
                $list.append($item);
            }
        });
    }

    // remember the url
    Utils.remember();

    // set remember url
    var translateUrl = Utils.getRemember('/admin/translate');
    translateUrl && $('a.item.mi-translate').attr('href', translateUrl);

    var importUrl = Utils.getRemember('/admin/import');
    importUrl && $('a.item.mi-import').attr('href', importUrl);

    var dynamicUrl = Utils.getRemember('/admin/dynamic');
    dynamicUrl && $('a.item.mi-dynamic').attr('href', dynamicUrl);

});
