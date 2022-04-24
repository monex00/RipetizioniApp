new Vue({
    el: '#login',
    data: {
        email: '',
        password: '',
        error: false,
        error_message: "",
        success: false,
        success_message: "",
        link : "login"
    },
    methods:{
        log :function () {
            if(this.email && this.password){
                var _this = this;
                $.post(this.link ,{
                    email: this.email,
                    password: this.password,
                    remember: this.remember
                }, function (data){
                    _this.redirectIfSuccess(data);
                })
            }
        },
        asGuest : function () {
            var self = this;
            $.post(this.link, {
                guest: 'true'
            }, function (data) {
                self.redirectIfSuccess(data)
            })
        },
        redirectIfSuccess : function (data){
            if(data.status === 'ko'){
                this.error = true;
                this.success = false;
                this.error_message = data.message;
            }else{
                this.success = true;
                this.error = false;
                this.success_message = data.message;
                window.location.href = data.redirectPath;
            }
        }
    }
})