
var formCreateUser = null;
var formCreateUserModal = null;

function UsersPage(data){
    
    return new Vue({
        el: "#v-app",

        data:{

            users: [],

            countries: [],

            urls:data.urls,

            form:{
                sending:false,
                user:{
                    name:null,
                    surname:null,
                    username:null,
                    password:null,
                    role:null
                }
            }

        },

        beforeCreate: function(){
            NProgress.start();
        },

        mounted: function(){
            this.showMainWrapp();
            this.initModal();
            this.getUsers();
        },

        methods: {

            showMainWrapp(){
                $("#main-wrapper").removeClass("d-none");
            },

            initModal: function(){
                formCreateUser = document.getElementById('formCreateUser');
                formCreateUserModal = new bootstrap.Modal(formCreateUser,{});
            },

            getUsersRequest: function(){
                return axios.get(this.urls.apiBackoffice.users.rest);
            },

            createUserRequest: function(data){
                return axios.post(this.urls.apiBackoffice.users.rest,data);
            },

            deleteUserRequest: function(id){

                console.log(this.urls.apiBackoffice.users.rest + "/" + id);

                return axios.delete(this.urls.apiBackoffice.users.rest + "/" + id);
            },

            getUsers: function(){

                var vm = this;

                vm.getUsersRequest()
                    .then(function(response){

                        vm.users = response.data;

                    })
                    .catch(function(error){
                        console.log(error.message);
                        console.log(error)
                        alertify.error("Une erreur c'est produite");
                    })
                    .then(function(){
                        NProgress.done();
                    })
            },

            openCreateUserModal: function(){

                formCreateUserModal.show();

            },

            createUser: function(){

                var vm = this;
                
                NProgress.start();

                vm.form.sending = true;

                this.createUserRequest(vm.form.user)
                    .then(function(response){

                        $("#formCreateUser form").get(0).reset();

                        data = response.data;

                        if(data.errorCode == null){

                            vm.users.unshift(data.user);

                            formCreateUserModal.hide();

                        }else if(data.errorCode == "USER_ALREADY_EXIST"){

                            alertify.error("Nom d'utilisateur déjà utilisé");

                        }   

                    })
                    .catch(function(error){
                        console.log(error.message);
                        alertify.error("Une erreur c'est produite !");
                        formCreateUserModal.hide();
                    })
                    .then(function(){
                        NProgress.done();
                        vm.form.sending = false;

                    });

            },

            deleteUser: function(id,index){

                var vm = this;
                
                alertify.confirm(
                    'Confirmation de suppression', 'Voulez vous vraiment supprimer cet utilisateur ?', 
                    function(){ 

                        NProgress.start();

                        vm.deleteUserRequest(id)
                            .then(function(response){
                                alertify.success('Suppression éffectuée');
                                vm.users.splice(index,1);
                            })
                            .catch(function(error){
                                console.log(error.message);
                                alertify.error("Une erreur c'est produite !");
                            })
                            .then(function(){
                                NProgress.done();
                            });

                    }, function(){

                    }).set('defaultFocus', 'cancel'); 

            }

        }
    })
}