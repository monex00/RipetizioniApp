
new Vue({
    el: '#ripetizioni',
    data: {
        id_corso: null,
        ripetizioni: [],
        isGuest: true,
        modal:{
            id_ripetizione: null,
            title: "",
            message: "",
            no_button: null,
            yes_button: null,
            ok_button: null,
            login_link: null
        }
    },
    mounted(){
        this.getUser();
        const queryString = window.location.search;
        const urlParams = new URLSearchParams(queryString);
        const name = urlParams.get('corso');
        const key = urlParams.get('key');
        $("#titolo").text(name);
        this.id_corso = key;
        this.getRipetizioni(this.id_corso);
    },
    methods:{
        getUser: function (){
            var self = this;
            $.get("getData",{operation: 'getUserData'}, function(data){
                if (data != null) self.isGuest=false;
            });
        },
        getRipetizioni: function (key){
            var self = this;
            $.get('getData',{ operation:'getTeachingByCourse' ,id: key}, function(data) {
                console.log(JSON.stringify(data));
                if (data != null) {
                    self.ripetizioni = data;
                    self.ripetizioni.forEach(rip => {
                        rip.insegnamenti.forEach(ins => {
                            ins.giorno = self.getDayByNumber(ins.giorno);
                        });
                    });
                }
                console.log(self.ripetizioni);
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
        confirmPrenota: function (){
            console.log("prenoto "+ this.modal.id_ripetizione);
            var self = this;
            $.post("operations", {entity: 'reservation', operation: 'userAdd', idInsegnamento: this.modal.id_ripetizione}, function (data){
                console.log(JSON.stringify(data));
                self.modal.id_ripetizione = null;
                switch (data.status) {
                    case "ok":
                        self.setModal(data.title,data.message, false, false, true,false);
                        break;
                    case "ko":
                        self.setModal(data.title,data.message, false, false, true,false);
                        break;
                    case "ko_auth":
                        self.setModal(data.title,data.message, false, false, false,true);
                        break;
                }
            })
        },
        prenota: function (id){
            this.modal.id_ripetizione = id;
            this.setModal("Conferma","Vuoi confermare la prenotazione?", true, true, false,false);
            $('#modal').modal('show');
            this.getRipetizioni(this.id_corso);
        },
        closeModal: function (){
            $('#modal').modal('hide');
            this.getRipetizioni(this.id_corso);
        },
        setModal: function (title, message, no, yes, ok, login){
            this.modal.title = title;
            this.modal.message = message;
            this.modal.no_button = no;
            this.modal.yes_button = yes;
            this.modal.ok_button = ok;
            this.modal.login_link = login;
        }
    }
})