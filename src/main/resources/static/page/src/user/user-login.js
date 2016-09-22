export class UserLogin {

    username = '';
    password = '';

    loginHandler() {

        $.get('/admin/login', (data) => {

            $.post('/admin/signin', {
                username: this.username,
                password: this.password,
                "remember-me": 'on'
            }).always(() => {
                toastr.success('登录成功!');
                let redirect = utils.urlQuery('redirect');
                if (redirect) {
                    window.location = decodeURIComponent(redirect);
                } else {
                    window.location = wurl('path');
                }

            });
        });

    }
}
