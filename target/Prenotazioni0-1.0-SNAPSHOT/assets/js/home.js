var index = -1;
var colori = ['#77a8a8',"#b9b0b0", "#b0aac0","#e4d1d1"];



Vue.component('materia', {
    props: ['id','name'],
    data: function () {
        if (index == colori.length - 1) index = 0;
        else index++;
        return {
            colore: colori[index]
        }
    },
    methods:{
        openMateria: function (corso,key){
            window.location.href = "corso.html?corso=" + corso + "&key="+key;
        }
    },
    template: '<div v-on:click="openMateria(name,id)"  class="col-md-3 d-sm-flex d-lg-flex justify-content-sm-start align-items-sm-start justify-content-lg-center align-items-lg-center justify-content-xl-start align-items-xl-start" style="min-width: 30vh;margin-bottom: 3vh;border-width: 2px;border-style: none;"> <div v-bind:style="{ background: colore}" class="d-flex d-sm-flex d-lg-flex flex-grow-1 flex-shrink-0 justify-content-center align-items-center justify-content-sm-center align-items-sm-center justify-content-lg-center align-items-lg-center justify-content-xl-center align-items-xl-center" style="height: 15vh;border-radius: 15px;box-shadow: 4px 4px 6px rgba(33,37,41,0.56);border-width: 2px;border-style: none;"> <h4 class="d-sm-flex d-lg-flex justify-content-sm-center align-items-sm-center justify-content-lg-start align-items-lg-start">{{name}}</h4></div></div>'
})

new Vue({
    el: '#materie',
    data: {
        materie: [],
    },
    mounted(){
        this.getMaterie()
    },
    methods:{
        getMaterie: function(){
            let self = this;
            $.post('getData',{operation: 'getCourses'}, function(data) {
                console.log(JSON.stringify(data));
                for (let i = 0; i < data.length ; i++){
                    self.materie.push({key:data[i].id, text: data[i].nome});
                }
            });
        },

    }
})

new Vue({
    el: '#utente',
    data:{
        nome: "",
        cognome: "",
        ruolo: null,
        isGuest : true,
    },
    mounted(){
        this.getUser();
    },
    methods:{
        getUser: function (){
            var self = this;
            $.get("getData", function(data){
                if (data != null) self.setUser(data);
            });
        },
        setUser: function (user){
            console.log(user);
            this.nome = user.nome;
            this.cognome = user.cognome;
            this.ruolo = user.ruolo;
            this.isGuest = false;
        }
    }
})

new Vue({
    el: '#prenotazioni',
    data: {
        prenotazioniAttive: [],
        prenotazioniPassate: []
    },
    mounted(){
        this.getPrenotazioni()
    },
    methods:{
        getPrenotazioni: function(){
            var self = this;
            $.post('getData',{operation: 'getMyReservations'}, function(data) {
                for (var i = 0; i < data.length ; i++){
                    console.log(JSON.stringify(data[i]));
                    const prenotazione = {
                        key: data[i].id,
                        corso: data[i].insegnamento.corso.nome,
                        docente: data[i].insegnamento.docente.nome + ' ' + data[i].insegnamento.docente.cognome,
                        giorno: data[i].insegnamento.giorno,
                        ora: data[i].insegnamento.ora,
                        stato: data[i].stato
                    }
                    console.log(prenotazione)
                    if (data[i].stato == 'A') self.prenotazioniAttive.push(prenotazione);
                    else self.prenotazioniPassate.push(prenotazione);
                }
            });
        }
    }
})
