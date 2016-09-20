export class UserLogin {

    username = 'admin';
    password = '88888888';

    loginHandler() {

        $.get('/admin/login', (data) => {

            $.post('/admin/signin', {
                username: this.username,
                password: this.password,
                "remember-me": 'on'
            }).always(() => {
                toastr.success('登录成功!');
                window.location = '/';
            });
        });

    }
}
