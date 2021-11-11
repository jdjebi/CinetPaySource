function ServiceManagePage(data){
    
    return new Vue({
        el: "#v-app",

        data:{

            serviceId:data.serviceId,

            service: null,


            systemConfigs: [],

            actuator:{
                configs: null
            },

            urls:{
                services:data.urls.servicesUrls,
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

            getServices: function(){

                var vm = this;

                axios.get(this.urls.services.rest + this.serviceId)
                    .then(function(response){
                        vm.service = response.data;
                    }).catch(function(error){
                        alert("Une erreur c'est produite");
                        console.log(error.message);
                    })
                    .then(function(){

                        // vm.collectConfig();

                        NProgress.done();
                    })
            },

            buildFormObject: function(data){
                var formData = {};
                data.forEach(function(element){
                    formData[element.name] = element.value;
                });
                return formData;
            },

            /* Manage */

            updateService: function(e){
                var data = $(e.target).serializeArray();
                var formData = this.buildFormObject(data);


                console.log(formData);


                formData["active"] = this.service.active;
                formData["status"] = this.service.status;
                formData["role"] = this.service.role;
                formData["code"] = this.service.code;


                axios.put(this.urls.services.rest + this.serviceId, formData)
                    .then(function(response){
                        vm.service = response.data;
                        alertify.success("Mise à jour réussie");
                    }).catch(function(error){
                        alertity.error("Une erreur c'est produite");
                        console.log(error.message);
                    })
                    .then(function(){
                        NProgress.done();
                    })
            },

            deleteService: function(){
                var vm = this;
                alertify.confirm('Confirmation','Voulez vous retirer ce service ?', 
                    function() { 
                        NProgress.start();
                        axios.delete(vm.urls.services.rest + vm.serviceId)
                            .then(function(response){
                                alertify.success("Service retiré");
                                window.location = vm.urls.services.show
                            })
                            .catch(function(error){
                                alertify.error("Une erreur c'est produite");
                                console.log(error.message);
                            })
                            .then(function(){
                                NProgress.done();
                            })
                    },
                    function(){}
                ).set({labels:{ok:'Oui', cancel: 'Annuler'}}).set('defaultFocus', 'cancel'); 

            },

            /* others */

            collectConfig: function(){
                console.log("Collection de configuration");

                axios.get(this.service.url + "/actuator/env")
                    .then(function(response){

                        data = response.data;

                        env = data.propertySources[4].properties;

                        const formatter = new JSONFormatter(env);

                        vm.actuator.configs = formatter.render();

                        document.getElementById("web-config").innerHTML = '';
                        document.getElementById("web-config").appendChild(vm.actuator.configs);

                        for (const [key, value] of Object.entries(env)) {
                            vm.systemConfigs.push({
                                'name':key,
                                'value':value.value
                            });
                        }

                        console.log(vm.systemConfigs);

                        NProgress.done();

                    })
                    .catch(function(error){
                        console.log(error)
                    });
            },

            refresh:function(){
                NProgress.start();
                this.collectConfig();
            }

        }
    })
}