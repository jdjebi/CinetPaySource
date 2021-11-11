function ServicesPage(urls){
    
    return new Vue({
        el: "#v-app",

        data:{

            services: [],

            urls:{
                services:urls.servicesUrls,
            },

        },

        beforeCreate: function(){
            NProgress.start();
        },

        mounted: function(){
            this.getServices();
            this.showMainWrapp();
        },

        methods: {

            showMainWrapp(){
                $("#main-wrapper").removeClass("d-none");
            },


            createService: function(){
                
                var vm = this;

                alertify.prompt('Nouveau service', 'Nom du service', null, 
                    function(evt, value) { 
                        if(value){
                           
                            NProgress.start();

                            var service = {
                                name:value
                            };

                            axios.post(vm.urls.services.rest,service)
                                .then(function(response){
                                    service = response.data;
                                    vm.services.push(service);
                                    alertify.success("Mise à jour éffectuée");
                                })
                                .catch(function(error){
                                    console.log(error.message);
                                    alertify.error("Une erreur c'est produite");
                                })
                                .then(function(){
                                    NProgress.done();
                                });
                        }
                    },
                    function() { 
                    }
                ).set({labels:{ok:'Créer', cancel: 'Annuler'}}).set('defaultFocus', 'cancel'); 
            },

            getServices: function(){

                var vm = this;

                axios.get(this.urls.services.rest)
                    .then(function(response){
                        vm.services = response.data;
                    }).catch(function(error){
                        alert("Une erreur c'est produite");
                        console.log(error.message);
                    })
                    .then(function(){
                        NProgress.done();
                    })
            },

            getServiceShowUrls(service,urls){

                return getServiceShowUrls(service,urls);
            }

        }
    })
}