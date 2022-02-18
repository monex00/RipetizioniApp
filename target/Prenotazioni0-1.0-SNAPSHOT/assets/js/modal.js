var modalComponent = Vue.component('modalComponent',{
    props : ['title', 'message', 'no', 'yes', 'ok', 'login'],
    template :
        '<div class="modal fade" role="dialog" tabindex="-1" id="mymodal">' +
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
        '        <button v-if="login" class="btn btn-link" type="button"  onclick="login_redirect()">Log in</button>' +
        '       </div>' +
        '    </div>' +
        '</div>' +
        '</div>',
    methods: {
        closeModal : function(){
            $('#mymodal').modal('hide');
        },
        showModal : function(data){
            $('#mymodal').modal('show');
        },
        confirmPrenota : function(){
            this.closeModal();
            this.$emit('confirm');
        }
    },
})

new Vue({
    el: '#modal',

    components: {
        'modal_component' : modalComponent
    },
    /*
    data: {
        title : 'prova',
        message : 'prova',
        no : false,
        yes : false,
        ok : true,
        login : false
    },
    */

    methods: {
        show: function (){
            this.$refs.childref.showModal()
        }
    },
})


function showModal(){
    var element = document.getElementById('modal');
    alert(element);

}



/*
<div class="modal fade" role="dialog" tabindex="-1" id="modal">
        <div class="modal-dialog modal-dialog-centered" role="document">
            <div class="modal-content">
                <div class="modal-header" style="background: #c2d4dd6e;">
                    <h4 class="modal-title">{{modal.title}}</h4><button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body" style="background: #c2d4dd6e;">
                    <p>{{modal.message}}</p>
                </div>
                <div class="modal-footer" style="background: #c2d4dd6e;">
                <button v-if="modal.no_button" class="btn btn-danger" type="button" v-on:click="closeModal()">No</button>
                <button v-if="modal.yes_button" v-on:click="confirmPrenota()" class="btn btn-success" type="button" >Si</button>
                <button v-on:click="closeModal()" v-if="modal.ok_button" class="btn btn-success" type="button" >Ok</button>
                <button  v-if="modal.login_link" class="btn btn-link" type="button"  onclick="alert('vado al login')">Log in</button></div>
            </div>
        </div>
    </div>
*/
