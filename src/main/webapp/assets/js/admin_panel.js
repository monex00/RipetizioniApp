var EventBus = new Vue();

var modalComponent = Vue.component('modalComponent',{
    props : ['modalid', 'title', 'message', 'no', 'yes', 'ok', 'login'],
    template :
        '<div class="modal fade" role="dialog" tabindex="-1" :id="modalid">' +
        '<div class="modal-dialog modal-dialog-centered" role="document">' +
        '    <div class="modal-content">'+
        '        <div class="modal-header" style="background: #c2d4dd6e;">' +
        '            <h4 class="modal-title">{{title}}</h4><button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>' +
        '        </div>' +
        '        <div class="modal-body" style="background: #c2d4dd6e;">' +
        '            <p>{{ message }}</p>' +
        '        </div>' +
        '        <div class="modal-footer" style="background: #c2d4dd6e;"> ' +
        '        <button v-if="no" class="btn btn-danger" type="button" v-on:click="closeModal()">No</button>' +
        '        <button v-if="yes" v-on:click="confirmPrenota()" class="btn btn-success" type="button" >Si</button>' +
        '        <button v-if="ok" class="btn btn-success" v-on:click="closeModal()" type="button" >Ok</button>' +
        '        <button v-if="login" class="btn btn-link" type="button" onclick="login_redirect()">Log in</button>' +
        '       </div>' +
        '    </div>' +
        '</div>' +
        '</div>',
    methods: {
        closeModal : function(){
            let id = this.$props.modalid;
            $('#' + id ).modal('hide');
        },
        showModal : function(){
            let id = this.$props.modalid;
            console.log("ID: " + id);
            console.log("title: " + this.$props.title);
            $('#' + id).modal('show');
        },
        confirmPrenota : function(){
            this.closeModal();
            this.$emit('confirm');
        }
    },
})

new Vue({
    el: '#teacher',
    data: {
        entity: 'teacher',
        link : "operations",

        modal_message : "",
        modal_title : "",

        addTeacher: {
            name: '',
            surname: '',
            operation: 'add',
            show: false,
            message: '',
        },

        removeTeacher: {
            show : false,
            message: '',
            operation: 'delete',
        },

        getTeacherData: {
            teachers: [],
            link : "getData",
            operation: 'getTeachers',
        }

    },
    components: {
        'modal_component' : modalComponent
    },

    methods:{
        add : function (){
            if(this.addTeacher.name || this.addTeacher.surname){
                createPostRequest(this.link,{entity: this.entity,
                    name: this.addTeacher.name,
                    surname: this.addTeacher.surname,
                    operation: this.addTeacher.operation}
                ).then(data => {
                    if(data.status === "ok") {
                        this.addTeacher.message = data.message;
                        this.addTeacher.show = true;
                        this.addTeacher.name = '';
                        this.addTeacher.surname = '';
                        this.getTeachers();
                    }else{
                        this.modal_message = data.message;
                        this.modal_title = data.title;
                        this.showModal();
                    }
                });
            }
        },

        remove: function (teacher) {
            createPostRequest(this.link, {
                entity: this.entity,
                idTeacher: teacher.id,
                name: teacher.nome,
                surname: teacher.cognome,
                operation: this.removeTeacher.operation
            }).then(data => {
                if(data.status === "ok"){
                    this.removeTeacher.show = true;
                    this.removeTeacher.message = data.message;

                    for(var i = 0; i < this.getTeacherData.teachers.length; i++){
                        if(this.getTeacherData.teachers[i].nome === teacher.nome && this.getTeacherData.teachers[i].cognome === teacher.cognome){
                            this.getTeacherData.teachers.splice(i, 1);

                        }
                    }
                    EventBus.$emit('teacher_removed', this.getTeacherData.teachers);
                }else{
                    this.modal_message = data.message;
                    this.modal_title = data.title;
                    this.showModal();
                }
            });
        },

        getTeachers: function () {
            $.get(this.getTeacherData.link, {operation: this.getTeacherData.operation}).then(data => {
                this.getTeacherData.teachers = data;
                console.log("getTeacherList"+  JSON.stringify(data));
                EventBus.$emit('teachers', data);
            });
        },

        showModal: function (){
            this.$refs.childref.showModal()
        }
    },

    mounted() {
        this.getTeachers();
    }
})

new Vue({
    el: '#course',
    data: {
        entity: 'courses',
        link : "operations",

        modal_message : "",
        modal_title : "",

        addCourse: {
            show : false,
            message: '',
            name: '',
            operation: 'add',
        },

        removeCourse: {
            show : false,
            message: '',
            operation: 'delete',
        },

        getCourseData: {
            courses: [],
            link : "getData",
            operation: 'getCourses',
        }

    },
    components: {
        'modal_component' : modalComponent
    },

    methods:{
        add :  function (){
            if(this.addCourse.name) {
                createPostRequest(this.link, {
                    name: this.addCourse.name,
                    operation: this.addCourse.operation,
                    entity: this.entity
                }).then(data => {
                    if (data.status === "ok") {
                        this.addCourse.show = true;
                        this.addCourse.message = data.message;
                        this.addCourse.name = '';
                        this.getCourses();
                    }else{
                        this.modal_message = data.message;
                        this.modal_title = data.title;
                        console.log(this.modal_title);
                        this.showModal();
                    }
                });
            }
        },

        remove: function (course) {
            createPostRequest(this.link,
                {
                    idCorso: course.id,
                    name: course.nome,
                    operation: this.removeCourse.operation,
                    entity: this.entity
                }).then(data => {
                    if(data.status === "ok"){
                        this.removeCourse.show = true;
                        this.removeCourse.message = data.message;
                        for(let i = 0; i < this.getCourseData.courses.length; i++){
                            if(this.getCourseData.courses[i].nome === course.nome){
                                this.getCourseData.courses.splice(i, 1);
                            }
                        }
                        EventBus.$emit('courses_removed', this.getCourseData.courses);
                    }else{
                        this.modal_message = data.message;
                        this.modal_title = data.title;
                        this.showModal();
                    }
                });
        },

        getCourses: function () {
            $.get(this.getCourseData.link, {operation: this.getCourseData.operation}).then(data => {
                this.getCourseData.courses = data;
                console.log("getCourseList"+  JSON.stringify(data));
                EventBus.$emit('courses', data);
            });
        },
        showModal: function (){
            this.$refs.childref.showModal()
        }
    },

    mounted() {
        this.getCourses();
    }
})

async function createPostRequest(link, param){
    let data = await $.post(link, param, function (data) {
        return data;
    });
    return data;
}



new Vue({
    el: '#teaching',
    data: {
        entity: 'teaching',
        modal_message : "",
        modal_title : "",

        teachersList : [],
        coursesList : [],
        teachingList : [],

        addTeachingData: {
            docente : '',
            corso : '',
            giorno : '',
            ora : '',

            link: 'operations',
            operation: 'add',

            show : false,
            message: '',

        },

        getTeachingData: {
            link : "getData",
            operation: 'getTeaching',
        },

        removeTeachingData:{
            link : "operations",
            operation: 'delete',
            show: false,
            message: '',
        }

    },
    components: {
        'modal_component' : modalComponent
    },

    methods:{
        setTeachersList(teachers) {
            this.teachersList = teachers;
        },

        setCoursesList(courses){
            this.coursesList = courses;
        },

        handleCoursesRemoved(courses){
            this.coursesList = courses;
            this.getTeachingList();
        },

        handleTeacherRemoved(teachers){
            this.teachersList = teachers;
            this.getTeachingList();
        },
        check_addable: function () {
            console.log("check_addable");
            //check if there is a teaching with same teacher and course and same day and the hour isn't distanced from the current time by 1 hour
            if(this.addTeachingData.docente !== '' || this.addTeachingData.corso !== '' || this.addTeachingData.giorno !== '' || this.addTeachingData.ora !== ''){

                for(let i = 0; i < this.teachingList.length; i++){
                    /*console.log("check_addable");
                    console.log("1: " + ((this.teachingList[i].docente.id).toString() === this.addTeachingData.docente.split('|')[0]) + " " + this.teachingList[i].docente.id + " " + this.addTeachingData.docente.split('|')[0]);
                    console.log("2: " + ( this.teachingList[i].giorno === convertInDay(parseInt(this.addTeachingData.giorno.substring(0,1)))) + " " + this.teachingList[i].giorno + " " + this.addTeachingData.giorno);
                    console.log("3: " + Math.round(Math.abs(this.teachingList[i].ora - this.addTeachingData.ora)) + " " + Number(this.teachingList[i].ora).toFixed(2)  + " " + Number(this.addTeachingData.ora));*/

                    if((this.teachingList[i].docente.id).toString() === this.addTeachingData.docente.split('|')[0] && this.teachingList[i].giorno === convertInDay(parseInt(this.addTeachingData.giorno.substring(0,1))) && Math.round(Math.abs(this.teachingList[i].ora - this.addTeachingData.ora)) < 1){
                        this.modal_title = "Attenzione!";
                        this.modal_message = "Un insegnamento dello stesso professore è già presente in questo orario o non è a distanza di 1 ora dall'orario inserito";
                        this.showModal();
                        return false;
                    }
                }
                return true;
            }
        },
        add : function (){
            if(this.addTeachingData.docente === '' || this.addTeachingData.corso === '' || this.addTeachingData.giorno === '' || this.addTeachingData.ora === ''){
                return;
            }

            if(!this.check_addable()){
                return;
            }

            if(this.addTeachingData.docente && this.addTeachingData.corso && this.addTeachingData.giorno && this.addTeachingData.ora){
                var nGiorno = this.addTeachingData.giorno.substring(0,1);
                createPostRequest(this.addTeachingData.link, {
                    idDocente: this.addTeachingData.docente.split('|')[0],
                    nome: this.addTeachingData.docente.split('|')[1],
                    cognome: this.addTeachingData.docente.split('|')[2],
                    idCorso: this.addTeachingData.corso.split('|')[0],
                    corso: this.addTeachingData.corso.split('|')[1],
                    giorno: nGiorno,
                    ora: this.addTeachingData.ora,
                    operation: this.addTeachingData.operation,
                    entity : this.entity
                }).then(data => {
                    if(data.status === "ok"){
                        this.addTeachingData.show = true;
                        this.addTeachingData.message = data.message;
                        this.getTeachingList();
                        this.addTeachingData.docente = '';
                        this.addTeachingData.corso = '';
                        this.addTeachingData.giorno = '';
                        this.addTeachingData.ora = '';
                        this.deselect();
                    }else{
                        this.modal_message = data.message;
                        this.modal_title = data.title;
                        this.showModal();
                    }
                });
            }
        },
        getTeachingList: function () {
            $.get(this.getTeachingData.link, {operation: this.getTeachingData.operation}).then(data => {
                for(let i=0; i<data.length; i++) {
                    data[i].giorno = convertInDay(data[i].giorno);
                }
                console.log("getTeachingList" + JSON.stringify(data));
                this.teachingList = data;
                EventBus.$emit('teaching', data);
            });
        },
        remove: function (teaching){
            createPostRequest(this.removeTeachingData.link, {
                idInsegnamento: teaching.id,
                operation: this.removeTeachingData.operation,
                entity: this.entity
            }).then(data => {
                if(data.status === "ok"){
                    this.removeTeachingData.show = true;
                    this.removeTeachingData.message = data.message;
                    this.getTeachingList();
                }else{
                    this.modal_message = data.message;
                    this.modal_title = data.title;
                    this.showModal();
                }
            });
        },
        showModal: function (){
            this.$refs.childref.showModal()
        },
        hourSelected: function (hour){
            this.addTeachingData.ora = hour;
            if(!this.check_addable()){
                this.addTeachingData.ora = '';
                this.deselect();
                return;
            }
            const clicked = document.getElementById(hour);
            this.deselect();
            clicked.classList.add('button-hour-clicked');
        },
        deselect: function () {
            const hours = document.getElementsByClassName('button-hour');
            for(let i = 0; i < hours.length; i++){
                hours[i].classList.remove('button-hour-clicked');
            }
        },
    },
    mounted() {
        EventBus.$on('teachers', this.setTeachersList);
        EventBus.$on('courses', this.setCoursesList);
        EventBus.$on('courses_removed', this.handleCoursesRemoved);
        EventBus.$on('teacher_removed', this.handleTeacherRemoved);
        this.getTeachingList();
    }
})



function checkAdminRole(){
    $.get("getData", {operation: "getUserData"}).then(data => {
        if(data.status === "ko"){
            window.location.href = window.location.href.replace(/\/[^\/]*$/, data.redirectPath);
            return;
        }
        if(data.ruolo !== "admin"){
            $("#admin_error").show();
            $("#content").hide();
        }else{
            $("#content").show();
            $("#admin_error").hide();
        }
    });
}

new Vue({
    el: '#reservation',
    data: {
        entity: 'reservation',
        modal_message : "",
        modal_title : "",

        userList : [],
        teachingList : [],
        reservationList : [],

        addReservationData:{
            show: false,
            message: '',
            operation: 'add',
            link: 'operations',
            utente: '',
            insegnamento: '',
            stato: ''
        },

        getReservationData: {
            link : "getData",
            operation: 'getReservation',
        },

        getUserData: {
            link : "getData",
            operation: 'getUsers',
        },

        getAvailableTeachingData: {
            link : "getData",
            operation: 'getAvailableTeaching',
        },

        updateReservationData:{
            link : "operations",
            operation: 'update',
            show: false,
            message: '',
        }
    },
    components: {
        'modal_component' : modalComponent
    },

    methods:{
        getAvailableTeaching: function (teaching) {
            console.log("getTeaching in getAvailableTeaching" + JSON.stringify(teaching));
            $.get(this.getAvailableTeachingData.link, {operation: this.getAvailableTeachingData.operation}).then(data => {
                for(let i=0; i<data.length; i++) {
                    data[i].giorno = convertInDay(data[i].giorno);
                }
                console.log("getAvailableTeaching" + JSON.stringify(data));
                this.teachingList = data;
            });
        },

        getUsersList: function () {
            $.get(this.getUserData.link, {operation: this.getUserData.operation}).then(data => {
                this.userList = data;
            });
        },

        getReservationList: function () {
            $.get(this.getReservationData.link, {operation: this.getReservationData.operation}).then(data => {
                for(let i=0; i<data.length; i++) {
                    data[i].insegnamento.giorno = convertInDay(data[i].insegnamento.giorno);
                }

                this.reservationList = data;
                console.log("getReservationList"+  JSON.stringify(data));
                EventBus.$emit('reservation', data);
            });
        },

        check_addable: function (){
            if(this.addReservationData.utente !== '' && this.addReservationData.insegnamento !== '' && this.addReservationData.stato !== '' && this.addReservationData.stato === 'A') {
                for(let i=0; i<this.reservationList.length; i++){
                    let idUguale = false;
                    let statoA = false;
                    let giornoUguale = false;
                    let oraUguale = false;
                    console.log(i);
                    if((this.reservationList[i].utente.id).toString() === this.addReservationData.utente.split('|')[0]){
                        console.log("id uguale");
                        idUguale = true;
                    }
                    if(this.reservationList[i].stato === 'A'){
                        console.log("stato A");
                        statoA = true;
                    }

                    if(this.reservationList[i].insegnamento.giorno === this.addReservationData.insegnamento.split('|')[4]){
                        console.log("giorno uguale");
                        giornoUguale = true;
                    }

                    if(this.reservationList[i].insegnamento.ora.toString() === this.addReservationData.insegnamento.split('|')[5]){
                        console.log("ora uguale");
                        oraUguale = true;
                    }

                    //TODO: check why isnt working
                    if(idUguale && statoA && giornoUguale && oraUguale){
                            this.modal_message = "L'utente ha gia' una prenotazione attiva per questo giorno e ora";
                            this.modal_title = "Attenzione";
                            console.log("ok");
                            this.showModal()
                            return false;
                    }
                }
            }
            return true
        },
        add: function (){
            if(!this.check_addable()){
                return;
            }

            createPostRequest(this.addReservationData.link, {
                idUtente: this.addReservationData.utente.split('|')[0],
                idInsegnamento: this.addReservationData.insegnamento.split('|')[0],
                stato: this.addReservationData.stato,
                operation: this.addReservationData.operation,
                entity : this.entity
            }).then(data => {
                if(data.status === "ok"){
                    this.addReservationData.show = true;
                    this.addReservationData.message = data.message;

                    this.addReservationData.utente = '';
                    this.addReservationData.insegnamento = '';
                    this.addReservationData.stato = '';

                    this.getAvailableTeaching();
                    this.getReservationList();
                }else{
                    this.modal_message = data.message;
                    this.modal_title = data.title;
                    this.showModal();
                }
            });
        },

        delete: function(reservation){
            /*
          createPostRequest(this.link, {
              idPrenotazione : reservation.id,
              operation: 'delete',
              entity : this.entity
          }).then(data => {
              if(data.status === "ok"){
                  //this.show = true;
                  //this.message = data.message;
                  for(let i = 0; i < this.reservationList.length; i++){
                      if(this.reservationList[i].id === reservation.id){
                          this.reservationList.splice(i, 1);
                      }
                  }
                  this.getReservationList();
                  this.getAvailableTeaching();
              }else{
                  this.modal_message = data.message;
                  this.modal_title = data.title;
                  this.showModal();
              }
              console.log("removeReservationList" + JSON.stringify(this.reservationList));
          });
             */
        },

        update: function(reservation){
            createPostRequest(this.updateReservationData.link, {
                idPrenotazione: reservation.id,
                stato: 'D',
                operation: this.updateReservationData.operation,
                entity: this.entity
            }).then(data => {
                if (data.status === "ok") {
                    this.updateReservationData.show = true;
                    this.updateReservationData.message = data.message;
                    this.getReservationList();
                    this.getAvailableTeaching();
                }else{
                    this.modal_message = data.message;
                    this.modal_title = data.title;
                    this.showModal();
                }
            });
        },

        showModal: function (){
            this.$refs.childref.showModal()
        }
    },
    beforeMount() {
        this.getUsersList();
        EventBus.$on('teaching', this.getAvailableTeaching);
        this.getReservationList();
    }
})


function convertInDay(num){
    switch (num){
        case 1:
            return 'Lunedi';
        case 2:
            return 'Martedi';
        case 3:
            return 'Mercoledi';
        case 4:
            return 'Giovedi';
        case 5:
            return 'Venerdi';
    }
}
function convertInNum(day){
    switch (day){
        case 'Lunedi':
            return 1;
        case 'Martedi':
            return 2;
        case 'Mercoledi':
            return 3;
        case 'Giovedi':
            return 4;
        case 'Venerdi':
            return 5;
    }
}

function selectTabBarItem(id){
    let tabBar = document.getElementById('tabBar');
    let tabBarItems = tabBar.getElementsByTagName('li');

    for(let i=0; i<tabBarItems.length; i++){
        let tabBarLink = tabBarItems[i].getElementsByTagName('a')[0];
        if(tabBarItems[i].id === id) {
            tabBarLink.style.backgroundColor = '#f5f2f2' ;
        }else{
            tabBarLink.style.backgroundColor = '#dddddd' ;
        }
    }
}


function filterTable(inputId) {
    var input, filter, table, tr, tds, i, txtValue;
    var finded;

    var prefix = inputId.split('_')[0];
    input = document.getElementById(inputId);
    filter = input.value.toUpperCase();

    table = document.getElementById(prefix +  '_table');
    tr = table.getElementsByTagName("tr");

    for (i = 0; i < tr.length; i++) {
        tds = tr[i].getElementsByTagName("td");
        finded = false;
        for(let j=0; j<tds.length; j++){
            txtValue = tds[j].textContent;

            if (txtValue.toUpperCase().indexOf(filter) > -1) {
                finded = true;
                break;
            }
        }

        if (finded) {
            tr[i].style.display = "";
        } else {
            tr[i].style.display = "none";
        }
    }
}
