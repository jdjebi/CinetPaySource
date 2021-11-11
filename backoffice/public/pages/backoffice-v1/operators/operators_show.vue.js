var addApiServiceModal = null;
var addSimServiceModal = null;
  
var formCreateApi = null;
var formCreateApiModal = null;

function OperatorsManagePage(data){
    
    return new Vue({
        el: "#v-app",

        data:{

            operatorId: data.operatorId,

            operator: null,

            countries: [],

            services: [],

            events: [],

            lastTransactions: [],

            selectIndex: null,

            ApiServiceType:"API-SERVICE",

            SimServiceType:"SIM-SERVICE",

            resources:{
                api:null,
                sims:[]
            },

            urlsPack: data.urlsPack,

            urls:{
                operators:data.urls.operatorsUrls,
                countries:data.urls.countriesUrls,
                resources:data.urls.resourcesUrls,
                services:data.urls.servicesUrls
            },

            styles:{
                serviceSelect:"service-selected"
            },

            form:{
                sending:false,
                operatorFormOpen:false,
                operator:{
                    name:null,
                    logo:null,
                    alias:null,
                    country:{
                        id:null,
                        code:null,
                        name:null,
                        currency:null
                    },
                    useApi:true,
                    useSIM:false,
                },
                api:{
                    name:null,
                    email:null,
                    password:null,
                    token:null,
                    type:"API",
                    apiUrl:null,
                    phone:null,
                    pingUrl:null,
                    extrasData:null,
                    ignoreBalance:null,
                    accessToken:null,
                    startLastBalance:0,
                    operator:{
                        id:null
                    },
                    country:{
                        id:null
                    }
                }
            },

            tabs:{
                dashboard:{
                    active:true
                },
                resources:{
                    active:false,
                    tabs:{
                        home:{
                            active:true
                        },
                        api:{
                            active:false
                        },
                        sim:{
                            active:false
                        }  
                    }
                },
                services:{
                    active:false
                },
                settings:{
                    active:false
                }
            }

        },

        beforeCreate: function(){
            NProgress.start();
        },

        mounted: function(){
            this.getData();
            this.initModal();
            this.showMainWrapp();
        },

        updated: function(){
            vm = this;
            vm.initPage();
        },

        computed:{

            getCountApi: function(){
                return this.resources.api ? 1 : 0;
            },

            getCountSim: function(){
                return this.resources.sims.length;
            },

            getCountResources: function(){
                return this.getCountApi + this.getCountSim;
            },

            getApiBalancePercent: function(){
                if(this.resources.api){
                    if(this.resources.api.balance || this.resources.api.startLastBalance){
                        return this.resources.api.balance / this.resources.api.startLastBalance * 100;
                    }           
                }
               
                return 0
            },

            getCountService: function(){

                count = 0;

                if(this.operator){

                    if(this.operator.apiService){
                        count++;
                    }

                    if(this.operator.simService){
                        count++;
                    }
                }

                return count;
            },

            allResources: function(){

                rApi = this.resources.api;
                rSims = this.resources.sims;

                if(rApi){
                    return [rApi].concat(rSims);
                }else{
                    return rSims  
                }

            }

        },

        methods: {

            showMainWrapp(){
                $("#main-wrapper").removeClass("d-none");
            },

            /* Requests */

            getOperatorRequest: function(operatorId){
                return axios.get(this.urls.operators.rest + operatorId);
            },

            getEvents: function(operator){

                let vm = this;

                let limit = 10;

                axios.get(vm.urlsPack.services.eventlog.get.entityref + operator.alias + "?limit=" + limit)
                    .then(function(response){
                        data = response.data;
                        vm.events = data;
                    })
                    .catch(function(error){
                        console.log(error.message);
                        alertify.error("Impossible de récupérer les evènements");
                    });

            },

            getLastTransactions: function(operator){

                let vm = this;

                let limit = 10;

                axios.get(vm.urlsPack.services.gateway.transactions.operator + operator.alias + "?limit=" + limit)
                    .then(function(response){
                        data = response.data;
                        vm.lastTransactions = data;
                    })
                    .catch(function(error){
                        console.log(error.message);
                        alertity.error("Impossible de récupérer les dernières transactions");
                    });

            },

            getServicesRequest: function(serviceType){

                typeSyntax = null;

                if(serviceType == this.ApiServiceType || serviceType == this.SimServiceType){
                    typeSyntax = "?type=" + serviceType + "&with_no_operator=true";
                }

                return axios.get(this.urls.services.rest + typeSyntax);
            },

            associateServiceRequest: function(data){

                return axios.post(this.urls.operators.addService, data);
            },

            dissociateServiceRequest: function(data){

                return axios.post(this.urls.operators.removeService, data);
            },


            /* Front */

            initPage: function(){
                feather.replace()
            },

            initModal: function(){

                formCreateApi = document.getElementById('formCreateApi');
                formCreateApiModal = new bootstrap.Modal(formCreateApi,{});

                addApiServiceNode = document.getElementById('selectApi');
                addApiServiceModal = new bootstrap.Modal(addApiServiceNode,{});
            },

            parseBalanceToPercent: function(resource){
                if(resource.startLastBalance > 0){
                    v = resource.balance / resource.startLastBalance * 100;
                    return v; 
                }
                return 0;  
            },

            resetApiForm: function(){
                this.form.api = {
                    name:null,
                    logo:null,
                    email:null,
                    password:null,
                    type:"API",
                    apiUrl:null,
                    startLastBalance:0,
                    token:null,
                    phone:null,
                    pingUrl:null,
                    extrasData:null,
                    ignoreBalance:null,
                    accessToken:null,
                    operator:{
                        id:null
                    },
                    country:{
                        id:null
                    }
                }
            },

            newSim: function(){
                return{
                    name:null,
                    type:"SIM",
                    operator:{
                        id:null
                    },
                    country:{
                        id:null
                    }
                }
            },

            changeTab: function(tabSelected){
                for (const [key, tab] of Object.entries(this.tabs)){
                    tab.active = false;
                }

                tabSelected.active = true;

                this.updateForm();
            },

            changeResourcesTab: function(tabSelected){
                for (const [key, tab] of Object.entries(this.tabs.resources.tabs)){
                    tab.active = false;
                }

                tabSelected.active = true;
            },

            showResource: function(resource){
                if(resource.type == "API"){
                    this.changeResourcesTab(this.tabs.resources.tabs.api);
                }else if(resource.type == "SIM"){
                    this.changeResourcesTab(this.tabs.resources.tabs.sim);
                }
            },

            updateForm: function(){
                this.form.operator = this.operator;
            },

            openOperatorForm: function(){
                this.form.operatorFormOpen = true;
            },

            closeOperatorForm: function(){
                this.form.operatorFormOpen = false;
            },

            getData: function(){

                var vm = this;

                NProgress.start();

                Promise.all([axios.get(this.urls.countries.rest), this.getOperatorRequest(vm.operatorId)])
                    .then(function(result){
                        countriesRequest = result[0];
                        operatorRequest = result[1];

                        // Countries
                        vm.countries = countriesRequest.data;

                        // Operateurs
                        operator = operatorRequest.data;
                        vm.updateOperatorData(operator);

                        // Evenements
                        vm.getEvents(operator);

                        // Transactions
                        vm.getLastTransactions(operator);

                    })
                    .catch(function(error){
                        console.log(error.message);
                        alertify.error("Une erreur c'est produite");
                    })
                    .then(function(){
                        NProgress.done();
                    })

            },

            getCountries: function(vmInstance){

                var vm = vmInstance;

                axios.get(this.urls.countries.rest)
                    .then(function(response){
                        countries = response.data;
                        vm.countries = countries
                        vm.initPage()
                        vm.updateForm();
                    }).catch(function(error){
                        alert("Une erreur c'est produite");
                        console.log(error.message);
                    }).then(function(){
                        // vm.getResources(vm);
                    });
            },

            getResources: function(v){

                var vm = v;

                axios.get(vm.urls.resources.rest)
                    .then(function(response){
                        resources = response.data;

                        apiResource = null;
                        simResources = [];

                        resources.forEach(function(r){
                            if(r.type == "API"){
                                apiResource = r;
                            }else if(r.type == "SIM"){
                                simResources.push(r);
                            }
                        }); 

                        vm.resources.api = apiResource; 
                        vm.resources.sims = simResources;                      
                    })
                    .catch(function(error){
                        alertify.error("Une erreur c'est produite");
                        console.log(error.message);
                    })
                    .then(function(){
                        NProgress.done();
                    });
            },

            deleteOpetator: function(){

                var vm = this;

                alertify.confirm(
                    'Confirmation de suppression', 'Voulez vous vraiment supprimer cet opérateur ?', 
                    function(){ 

                        NProgress.start();

                        vm.form.sending = true;

                        axios.delete(vm.urls.operators.rest + vm.operator.id)
                            .then(function (response){
                                alertify.success('Suppression éffectuée');
                                window.location = vm.urls.operators.show;
                            }).catch(function(error){
                                alertify.error("Une erreur c'est produite");
                                console.log(error.message);
                            }).then(function(){
                                NProgress.done();
                                vm.form.sending = false;
                            });
                    }, function(){

                    }).set('defaultFocus', 'cancel'); 
            },

            updateOperator: function(){

                var vm = this;

                NProgress.start();

                vm.form.sending = true;

                vm.countries.forEach(function(c){
                    if(c.id == vm.form.operator.country.id){
                        vm.form.operator.country.id = c.id;
                        vm.form.operator.country.name = c.name;
                        vm.form.operator.country.code = c.code;
                        vm.form.operator.country.useSIM = c.useSIM;
                        vm.form.operator.country.useApi = c.useApi;
                    }
                });

                axios.put(vm.urls.operators.rest + vm.operator.id,vm.form.operator)
                    .then(function (response){
                        alertify.success('Mise à jour éffectuée');
                        vm.operator = response.data;

                    }).catch(function(error){
                        alertify.success("Une erreur c'est produite");
                        console.log(error.message);
                    }).then(function(){
                        NProgress.done();
                        vm.form.sending = false;
                        vm.closeOperatorForm();
                    });
            },

            openCreateApiModal: function(){
                this.resetApiForm();
                formCreateApiModal.show();            
            },

            createApi: function (){
                this.form.api.operator.id = this.operator.id;
                this.form.api.country.id = this.operator.country.id;

                NProgress.start();

                var vm = this;

                axios.post(vm.urls.resources.rest,this.form.api)
                    .then(function(response){
                        apiResource = response.data;
                        vm.resources.api = apiResource;
                        alertify.success("API créée avec succès");
                        console.log(vm.resources.api);
                    })
                    .catch(function(error){
                        console.log(error.message);
                        alertify.error("Une erreur c'est produite");

                    })
                    .then(function(){
                        NProgress.done();
                        formCreateApiModal.hide();
                    });
            },

            updateApi: function(){

                NProgress.start();

                var vm = this;

                axios.put(vm.urls.resources.rest + this.resources.api.id,vm.resources.api)
                    .then(function(response){
                        apiResource = response.data;
                        console.log(apiResource);
                        alertify.success("Mise à jour éffectuée");
                    })
                    .catch(function(error){
                        console.log(error.message);
                        alertify.error("Une erreur c'est produite");

                    })
                    .then(function(){
                        NProgress.done();
                    });
            },

            createSim: function(){
                
                var vm = this;

                alertify.prompt('Nouvelle SIM', 'Nom de la SIM', null, 
                    function(evt, value) { 
                        if(value){
                           
                            NProgress.start();

                            var sim = vm.newSim();
                            sim.name = value;
                            sim.operator.id = vm.operator.id;
                            sim.country.id = vm.operator.country.id;

                            axios.post(vm.urls.resources.rest,sim)
                                .then(function(response){
                                    data = response.data;
                                    vm.resources.sims.push(data);
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
                ).set({labels:{ok:'Ajouter', cancel: 'Annuler'}}).set('defaultFocus', 'cancel'); 
            },

            updateResource: function(resource){

                NProgress.start();

                var vm = this;

                var r = Object.assign({}, resource);

                console.log(r);

                axios.put(vm.urls.resources.rest + r.id,r)
                    .then(function(response){
                        alertify.success("Mise à jour éffectuée");
                    })
                    .catch(function(error){
                        console.log(error.message);
                        alertify.error("Une erreur c'est produite");

                    })
                    .then(function(){
                        NProgress.done();
                    });
            },

            updateResourceBalance: function(r){

                var vm = this;

                alertify.prompt('Rechargement', 'Montant de rechargement', null, 
                    function(evt, value) { 
                        if(value){
                            var balance = parseInt(value);
                            var resource = Object.assign({},r);
                            resource.balance = balance;
                            resource.startLastBalance = balance;
                            NProgress.start();
                            axios.put(vm.urls.resources.rest + resource.id,resource)
                                .then(function(response){
                                    data = response.data;
                                    r.balance = balance;
                                    r.startLastBalance = balance;
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
                ).set({labels:{ok:'Valider', cancel: 'Annuler'}}).set('defaultFocus', 'cancel');

            },

            deleteResource: function(resource, i){
                var resource = resource;
                var index = i;
                var vm = this;

                alertify.confirm(
                    'Confirmation', 'Voulez vous vraiment retiter cette resource ?', 
                    function(){ 

                        NProgress.start();


                        axios.delete(vm.urls.resources.rest + resource.id)
                            .then(function (response){
                                if(resource.type == "API"){
                                    vm.resources.api = null;  
                                    alertify.success("API rétirée");
                                }else{
                                    console.log(index);
                                    vm.resources.sims.splice(index,1);
                                    alertify.success("SIM rétirée");
                                }
                            }).catch(function(error){
                                alertify.error("Une erreur c'est produite");
                                console.log(error.message);
                            }).then(function(){
                                NProgress.done();
                            });
                    }, function(){

                    }).set('defaultFocus', 'cancel');

            },

            /* Support */
            updateOperatorData: function(operator){

                vm = this;
                apiResource = null;
                simResources = [];

                this.operator = operator;

                operator.resources.forEach(function(r){
                    if(r.type == "API"){
                        apiResource = r;
                    }else if(r.type == "SIM"){
                        simResources.push(r);
                    }
                }); 

                vm.resources.api = apiResource; 
                vm.resources.sims = simResources; 
            },

            /* Services */

            selectService: function(index){
                this.selectIndex = index;
            },

            openServiceAdder: function(serviceType){
                addApiServiceModal.show();
                this.selectIndex = null;
                this.addService(serviceType);
            },

            addService: function (ServiceType){

                vm = this;

                NProgress.start();

                this.getServicesRequest(ServiceType)
                    .then(function (response){
                        vm.services = response.data;
                    })
                    .catch(function(error){
                        alertify.error("Une erreur c'est produite");
                        console.log(error.message);
                        vm.services = [];

                    })
                    .then(function(){
                        NProgress.done();
                    })
            },

            validateNewService: function(){

                if(this.selectIndex != null){

                    service = this.services[this.selectIndex];

                    data = {
                        serviceId:service.id,
                        operatorId:this.operatorId
                    }

                    NProgress.start();

                    this.associateServiceRequest(data)
                        .then(function(response){
                            vm.updateOperatorData(response.data);
                            alertify.success("Service ajouté");
                        })
                        .catch(function(error){
                            alertify.error("Une erreur c'est produite");
                            console.log(error.message);
                        })
                        .then(function(){
                            NProgress.done();
                            addApiServiceModal.hide();
                        });
                }else{

                    alertify.error("Aucun service sélectionné");

                    console.log(addApiServiceModal);

                    addApiServiceModal.hide();

                }


            },

             removeOperatorService: function(service){

                data = {
                    serviceId:service.id,
                    operatorId:this.operatorId
                }

                vm = this;

                alertify.confirm(
                    'Confirmation', 'Voulez vous retirer le service ?', 
                    function(){ 

                       NProgress.start();

                        vm.dissociateServiceRequest(data)
                            .then(function(response){
                                vm.updateOperatorData(response.data);
                                alertify.success("Service retiré");
                            })
                            .catch(function(error){
                                alertify.error("Une erreur c'est produite");
                                console.log(error);
                            })
                            .then(function(){
                                NProgress.done();
                            });
                    }, function(){

                    }).set('defaultFocus', 'cancel'); 
             
            },


            /* Helpers */

            getOperatorLogo: function(logoUrl){
                return operatorLogoHelper(logoUrl);
            },

            getOperatorCountryLogo: function(countryCode){
                return getOperatorCountryLogo(countryCode);
            },

            getTrxStatusBgClass: function(status){
                return libGetTransactionStatusStyleBadgeBackgroundClass(status);
            },

            pingApi: function(url){

                NProgress.start();

                axios.get(this.urlsPack.apiBackoffice.ping + "?url=" + url)
                    .then(function(response){
                        data = response.data;

                        if(data == true){
                            alertify.success("PING réussie");
                        }else{
                            alertify.error("PING échoué");
                        }
                    })
                    .catch(function(error){
                        console.log(error.message);
                        alertify.error("Impossible de tester l'API");
                    })
                    .then(function(){
                        NProgress.done();
                    })

            }

        }
    })
}