var mat = [];

Vue.component('mynav', {
    data: function (){
        return {materie: mat, admin: null}
    },
    mounted(){
        this.getUser();
        this.getMaterie();
    },
    methods:{
        getMaterie: function(){
            let self = this;
            $.get('getData',{operation: 'getCourses'}, function(data) {
                console.log(JSON.stringify(data));
                for (let i = 0; i < data.length ; i++){
                    mat.push({key:data[i].id, text: data[i].nome});
                }
            })
        },
        getUser: function (){
            var self = this;
            $.get("getData",{operation: 'getUserData'}, function(data){
                if (data != null) self.setUser(data);
            });
        },
        setUser: function (user){
            if (user.ruolo == "admin") {
                console.log("RUOLO ADMIN");
                this.admin = true;
            }else if(user.ruolo == "cliente"){
                this.admin = false;
            }
        },
        logOut: function (){
            $.get("Logout", function(data){
                if (data != null) window.location.href = data.redirectPath;
            });
        },
        redirect(page){
            window.location.href = page;
        }
    },
    template: '<header>\n' +
        '    <h3>RIPETIZIONI</h3>\n' +
        '    <nav>\n' +
        '        <ul>\n' +
        '            <li v-on:click="redirect(\'homepage.html\')"><a>Pagina iniziale</a></li>\n' +
        '            <li class="drop"><a>Materie</a>\n' +
        '                <ul>\n' +
        '                    <li v-for="materia in materie" :key="materia.key" ><a :href="\'corso.html?corso=\' + materia.text + \'&key=\' + materia.key">{{materia.text}}</a></li>\n' +
        '                </ul>\n' +
        '            </li>\n' +
        '            <li v-if="admin != null" v-on:click="redirect(\'reservations.html\')"><a>Le tue prenotazioni</a></li>\n' +
        '            <li v-if="admin == null" v-on:click="redirect(\'index.html\')"><a>Accedi</a></li>\n' +
        '            <li v-if="admin == true" v-on:click="redirect(\'admin_panel.html\')"><a>Amministratore</a></li>\n' +
        '            <li v-if="admin != null" v-on:click="logOut()"><a>Esci</a></li>\n' +
        '        </ul>\n' +
        '    </nav>\n' +
        '</header>'
})
new Vue({el:"#vue-nav"})

Vue.component('myfoot', {
    template: '<div class="text-center d-xl-flex justify-content-xl-center align-items-xl-center footer">' +
        '    <p><br>®Sito realizzato da Simone Multari e Loris Signoretti</p>\n' +
        '</div>'
})

new Vue({el:"#vue-foot"})