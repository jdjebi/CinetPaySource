var formCreateSimbox = null;
var modalFormCreateSimbox = null;

function simboxIndexPage(data){
    
    return new Vue({
        el: "#v-app",

        data:{
            simboxes: [],
            simboxes2: [],
            simboxIdSelected:null,
            urls:{
                simboxes:data.urls.simboxesUrls
            },
            form:{
                simbox:{
                    name:null,
                    serialNumber:null,
                    description:null,
                    url:null,
                    localIp:null,
                    internetIp:null,
                    password:null,
                    totalPort:null
                }
            }
        },

        beforeCreate: function(){
            // NProgress.start();
        },

        mounted: function(){
            this.getSimboxes();  
            this.initModal();
        },

        updated: function(){
            this.initPage()   
        },

        computed:{

        },

        methods: {

            initPage: function(){
                feather.replace()
            },

            initModal: function(){
                formCreateSimbox = document.getElementById('formCreateSimbox');
                modalFormCreateSimbox = new bootstrap.Modal(formCreateSimbox,{});
            },


            getSimboxes: function(){

                NProgress.start();

                var vm = this;

                axios.get(this.urls.simboxes.rest)
                    .then(function(response){
                        simboxes = response.data;
                        vm.simboxes = simboxes;
                    })
                    .catch(function(error){
                        console.log(error.message);
                        alertify.error("Une erreur c'est produite");
                    })
                    .then(function(){
                        NProgress.done();
                    })

            },

            openCreateSimboxModal: function(){
                this.form.simbox = {
                    name:null,
                    serialNumber:null,
                    description:null,
                    url:null,
                    localIp:null,
                    internetIp:null,
                    password:null,
                    totalPort:null
                };

                modalFormCreateSimbox.show();  
            },

            createSimbox: function (){

                var vm = this;

                NProgress.start();

                axios.post(vm.urls.simboxes.rest,this.form.simbox)
                    .then(function(response){
                        data = response.data;
                        vm.simboxes.push(data);
                        vm.simboxIdSelected =  vm.simboxes.length - 1
                        alertify.success("SIMBOX crée");
                    })
                    .catch(function(error){
                        console.log(error.message);
                        alertify.error("Une erreur c'est produite");
                    })
                    .then(function(){
                        NProgress.done();
                        modalFormCreateSimbox.hide();  
                    });

            },

            selectSimbox: function(index,simbox){
                console.log(simbox);
                this.simboxIdSelected = index;
            },

            isSelected: function(index,simbox){
                if(index == this.simboxIdSelected){
                    return true;
                }else{
                    return false;
                }
            }

        }
    })
}