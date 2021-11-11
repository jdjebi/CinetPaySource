function GatewayManagePage(data){

    return new Vue({

        el:"#v-app",

        data:{

            test:"yes",

            showSyncForm: false,
            showGatewayPage: false,

            syncIsRunning: false,
            syncFailed:null,
            infosCollected:false,
            syncFinished:null,
            syncFormDisabled:false,

            service:null,
            serviceMetrics:null,
            serviceConfig:{
                env:null,
                operators: null
            },

            MemoryUsageCircleBar:null,

            actuator:{
                configs: null
            },

            dashboard:{

                serviceActivityChart:null,

                dataChart:{
                    labels: ["9h", '10h', '11h', '12h', '13h', '14h', '15h'],
                    datasets: [
                      {
                        label: 'TRX',
                        backgroundColor: '#28a7458a',
                        data: [10, 20, 35, 30, 10, 5, 1]
                      }
                    ],
                }
            },

            gateway_url: "http://localhost:8070",

            urls:{
                services:data.servicesUrls
            }
        },

        computed: {
            getMemoryUsageInPercent: function(){
                if(this.serviceMetrics){
                    return  ( this.serviceMetrics.usedheap / this.serviceMetrics.maxheap * 100 )
                }else{
                    return 0
                }
            }
        },
        
        mounted: function(){

            // Initialisation
            var vm = this; 

            this.getGateway()
                .then(function(response){
                    service = response.data;
                    if(service){
                        vm.service = response.data;
                        vm.showGatewayPage = true;
                        NProgress.start();
                        vm.getAllConfig();
                    }else{
                        vm.showSyncForm = true;
                    }
                })
                .catch(function(error){
                    console.log(error.message);
                    alertify.error("Une erreur c'est produite");
                });  
        },

        updated: function(){
            feather.replace();

            if(this.showGatewayPage){
                if(this.MemoryUsageCircleBar == null){
                    this.MemoryUsageCircleBar = new ldBar("#myldBar");
                }
                showChart(this, this.dashboard.dataChart);
    
                var bar2 = document.getElementById('myldBar').ldBar; 
                
            }
        },

        methods: {

            /* Get Gateway */
            getGateway: function(){
                return axios.get(this.urls.services.getGateway);
            },

            getPasserelleInfo: function(){
                return axios.get(this.urls.services.gateway.host + this.urls.services.actuatorInfo);
            },

            /* Manage Configs */
            updateOperatorGatewayWebConfig: function(){
                return axios.put(this.urls.services.gateway.config.update.operators);
            },

            getGatewayWebConfig: function(){
                return axios.get(this.urls.services.gateway.config.get);
            },

            getGatewayEnvConfig: function(){
                return axios.get(this.urls.services.gateway.host + this.urls.services.actuatorEnv);
            },

            getAllConfig: function(){
                var vm = this;

                Promise.all([this.getGatewayEnvConfig(), this.getGatewayWebConfig()])
                    .then(function(response){
                        vm.setEnvConfig(response[0].data);

                        if(response[1].data.operators){
                            vm.serviceConfig.operators = response[1].data.operators;
                        }
                    })
                    .catch(function(error){
                        console.log(error.message)
                        alertify.error("Une erreur c'est produite");
                    })
                    .then(function(){
                        vm.startMetrics();
                        NProgress.done();
                    }) 
            },


            setEnvConfig: function(data){
                var env_data = getApplicationPropertiesData(data.propertySources);
                this.serviceConfig.env = env_data;
            },

            /* End Configs */


            /* init  */
            initSync: function(){
                this.syncIsRunning = false;
                this.syncFailed = null;
                this.infosCollected = false;
                this.syncFinished = null;
            },

            resetSync: function(){
                this.syncFormDisabled = false;
            },

            syncService: function(){
                this.initSync();
                this.syncIsRunning = true;
                this.syncFailed = false;
                this.syncFormDisabled = true;
                var vm = this;
                this.getPasserelleInfo()
                    .then(function(response){
                        serviceInfos = response.data.app;
                        // Timeout
                        setTimeout(function(){
                            vm.infosCollected = true;
                            serviceData = {
                                name:serviceInfos.name,
                                fullname:serviceInfos.fullname,
                                code:serviceInfos.code,
                                description:serviceInfos.description,
                                version:serviceInfos.version,
                                url:vm.gateway_url
                            };
                            setTimeout(function(){
                                axios.post(vm.urls.services.newDispatcher, serviceData)
                                .then(function(response){
                                    vm.syncFinished = true;
                                    setTimeout(function(){
                                        vm.service = response.data;
                                        vm.showSyncForm = false;
                                        vm.showGatewayPage = true;
                                        vm.initSync();
                                        vm.resetSync();
                                        vm.getAllConfig();
                                    },3000);
                                })
                                .catch(function(error){
                                    vm.syncFinished = false;
                                    vm.resetSync();
                                    console.log(error.message);
                                });
                            },3000)
                        },2000);
                        // Fin Timeout
                    })
                    .catch(function(error){
                        vm.syncFailed = true;
                        vm.syncIsRunning = false;
                        vm.resetSync();
                        console.log(error.message);
                    });
            },
            /* End init  */
        
            /* Service */

            syncWebConfigs: function(){
                NProgress.start();
                var vm = this;
                this.updateOperatorGatewayWebConfig()
                    .then(function(response){
                        vm.serviceConfig.operators = response.data;
                    })
                    .catch(function(error){
                        alertify.error("Une erreur c'est durant la synchronisation")
                        console.log(error.message)
                    })
                    .then(function(){
                        NProgress.done();
                    })
            },

            deleteGateway: function(){
                var vm = this;
                alertify.confirm('Suppression','Voulez vous retirer la passerelle ?', 
                    function() { 
                        NProgress.start();
                        axios.delete(vm.urls.services.deleteGateway)
                            .then(function(response){
                                vm.service = null;
                                vm.showGatewayPage = false;
                                vm.showSyncForm = true;
                                vm.serviceConfig.web = null;
                                vm.serviceConfig.env = null;
                                vm.MemoryUsageCircleBar = null;
                                vm.serviceActivityChart = null;
                                vm.stopMetrics();
                                alertify.success("Passerelle rétirée");
                            })
                            .catch(function(error){
                                alertify.error("Une erreur c'est produite");
                                console.log(error.message);
                            })
                            .then(function(){
                                NProgress.done();
                            })
                    },
                    function() { 
                    }
                ).set({labels:{ok:'Oui', cancel: 'Annuler'}}).set('defaultFocus', 'cancel'); 

            },

            refresh: function(){
                NProgress.start();
                var vm = this;

                this.stopMetrics();

                this.getGateway()
                    .then(function(response){
                        service = response.data;
                            vm.service = response.data;
                            vm.getAllConfig();
                    })
                    .catch(function(error){
                        console.log(error.message);
                        alertify.error("Une erreur c'est produite");
                    }); 
            },

            startMetrics(){
                var vm = this;

                this.timerIntervalId = setInterval(function(){
                    axios.get("http://localhost:8070/metrics")
                        .then(function(response){
                            vm.serviceMetrics = response.data;
                            
                            vm.MemoryUsageCircleBar.set(vm.getMemoryUsageInPercent);
                        })
                        .catch(function(error){
                            console.log(error.message);
                        })
                    
                },3000);
            },

            stopMetrics(){
                clearInterval(this.timerIntervalId);
            }
    

        },

    });

}


function showChart(vm, dataChart){
    if(vm.serviceActivityChart == null){
        vm.serviceActivityChart = document.getElementById('myChart').getContext('2d');
        var myChart = new Chart(vm.serviceActivityChart, {
            type: 'line',
            data: dataChart,
            options: {
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        }, {responsive: true, maintainAspectRatio: false});
    }

}

Vue.component('line-chart', {

    extends: VueChartJs.Line,

    mixins: [VueChartJs.mixins.reactiveProp],

    props: ['chartData','test'],

    mounted () {

      this.renderChart(this.chartData, {responsive: true, maintainAspectRatio: false})
    }
    
})