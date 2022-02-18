var mat = [];

Vue.component('navbar', {
    data: function (){
        return {materie: mat}
    },
    mounted(){
        this.getMaterie();
        $('#drop').click(function() {
            $(this).css('border-radius', '20px 20px 0px 0px');
        });
    },
    methods:{
        getMaterie: function(){
            let self = this;
            $.post('getData',{operation: 'getCourses'}, function(data) {
                console.log(JSON.stringify(data));
                for (let i = 0; i < data.length ; i++){
                    mat.push({key:data[i].id, text: data[i].nome});
                }
            })
        },

    },
    template: '<nav class="navbar navbar-light navbar-expand-lg navigation-clean" style="background: #8ca3a3;">\n' +
        '        <div class="container"><a class="navbar-brand" href="#" style="font-size: 30px;border-color: rgb(0,0,0);color: rgb(0,0,0);">Ripetizioni</a><button data-bs-toggle="collapse" class="navbar-toggler" data-bs-target="#navcol-2"><span class="visually-hidden">Toggle navigation</span><span class="navbar-toggler-icon"></span></button>\n' +
        '            <div class="collapse navbar-collapse" id="navcol-2">\n' +
        '                <ul class="navbar-nav ms-auto">\n' +
        '                    <li class="nav-item" style="background: #c2d4dd;margin-left: 2vh;border-radius: 20px;border-color: rgb(0,0,0);"><a class="nav-link" href="homepage.html" style="border-color: rgb(0,0,0);color: rgb(0,0,0);">Pagina iniziale</a></li>\n' +
        '                    <li id="drop" class="nav-item dropdown show" style="background: #c2d4dd;margin-left: 2vh;border-radius: 20px;color: rgb(0,0,0);border-color: rgb(0,0,0);"><a class="dropdown-toggle nav-link" aria-expanded="true" data-bs-toggle="dropdown" href="#" style="color: rgb(0,0,0);">Materie</a>\n' +
        '                        <div class="dropdown-menu" style="margin-top: 0;background: #c2d4dd;color: rgb(0,0,0);border-radius: 20px;border-top-left-radius: 0; border: 0;"><a v-for="materia in materie" :key="materia.key" class="dropdown-item"  :href="\'corso.html?corso=\' + materia.text + \'&key=\' + materia.key" >{{materia.text}}</a></div>\n' +
        '                    </li>\n' +
        '                    <li class="nav-item" style="background: #c2d4dd;margin-left: 2vh;border-radius: 20px;color: rgb(0,0,0);"><a class="nav-link" href="admin_panel.html" style="color: rgb(0,0,0);">Amministrazione</a></li>\n' +
        '                </ul>\n' +
        '            </div>\n' +
        '        </div>\n' +
        '    </nav>'
})
new Vue({el:"#navbar"})

Vue.component('foot', {
    template: '<footer class="text-center d-xl-flex justify-content-xl-center align-items-xl-center" style="height: 10vh;background: #8ca3a3;border-top-left-radius: 10px;border-top-right-radius: 10px;width:100%;">' +
        '    <p><br>®Parola di Francesco Amadori</p>\n' +
        '</footer>'
})

new Vue({el:"#footer"})