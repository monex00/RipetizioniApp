

const selectAttive = ()=>{
    document.getElementById('attive_tab_button').classList.add('selected');
    document.getElementById('passate_tab_button').classList.remove('selected');
    document.getElementById('attive_tab').style.display = 'block';
    document.getElementById('passate_tab').style.display = 'none';
};
const selectPassate = ()=>{
    document.getElementById('attive_tab_button').classList.remove('selected');
    document.getElementById('passate_tab_button').classList.add('selected');
    document.getElementById('attive_tab').style.display = 'none';
    document.getElementById('passate_tab').style.display = 'block';
};

new Vue({
    el: '#prenotazioni',
    data: {
        prenotazioniAttive: [],
        prenotazioniPassate: [],
        guest : true,
        zeroAttive : true,
        zeroPassate: true,
        modal: {
            title: "",
            message: "",
        }
    },
    mounted(){
        this.getPrenotazioni();
    },
    methods:{
        getPrenotazioni: function(){
            var self = this;
            $.get('getData',{operation: 'getMyReservations'}, function(data) {
                console.log(JSON.stringify(data));
                if (data != null){
                    self.guest = false;
                    for (var i = 0; i < data.length; i++) {
                        const prenotazione = {
                            key: data[i].id,
                            corso: data[i].insegnamento.corso.nome,
                            docente: data[i].insegnamento.docente.nome + ' ' + data[i].insegnamento.docente.cognome,
                            giorno: self.getDayByNumber(data[i].insegnamento.giorno),
                            ora: data[i].insegnamento.ora,
                            stato: data[i].stato
                        }
                        if (data[i].stato == 'A') {
                            self.prenotazioniAttive.push(prenotazione);
                            if (self.zeroAttive)  self.zeroAttive = false;
                        }
                        else {
                            self.prenotazioniPassate.push(prenotazione);
                            if (self.zeroPassate)  self.zeroPassate = false;
                        }
                    }
                    self.prenotazioniAttive.reverse();
                    self.prenotazioniPassate.reverse();
                }else{
                    window.location.href = window.location.href.replace(/\/[^\/]*$/, '/homepage.html');
                }
            });
        },
        getDayByNumber(day){
            switch (day){
                case 1:
                    return "Lunedì";
                case 2:
                    return "Martedì";
                case 3:
                    return "Mercoledì";
                case 4:
                    return "Giovedì";
                case 5:
                    return "Venerdì";
            }
        },
        updatePrenotazione(id, stato){
            var self = this;
            $.post("operations", {entity: 'reservation', operation: 'userUpdate', idPrenotazione: id, stato: stato}, function (data) {
                self.setModal(data.title, data.message);
                self.zeroPassate = true;
                self.zeroAttive = true;
                self.prenotazioniAttive = [];
                self.prenotazioniPassate = [];
                self.getPrenotazioni();
            });
        },
        closeModal: function (){
            $('#modal').modal('hide');
        },
        setModal: function (title, message){
            this.modal.title = title;
            this.modal.message = message;
            $('#modal').modal('show');
        }

    }
})