var formCreateUser = null;
var formCreateUserModal = null;

function UsersShowPage(data){
    
    return new Vue({
        el: "#v-app",

        data:{

            user: null,

            userId: data.userId,

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
            this.getUser();
            this.showMainWrapp();
        },

        methods: {

            showMainWrapp(){
                $("#main-wrapper").removeClass("d-none");
            },

            getUserRequest: function(){
                return axios.get(this.urls.apiBackoffice.users.rest + this.userId);
            },

            deleteUserRequest: function(id){
                return axios.delete(this.urls.apiBackoffice.users.rest + "/" + id);
            },

            getUser: function(){

                var vm = this;

                vm.getUserRequest()
                    .then(function(response){

                        vm.user = response.data;

                    })
                    .catch(function(error){

                        console.log(error.message);

                        alertify.error("Une erreur c'est produite");

                    })
                    .then(function(){

                        NProgress.done();

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