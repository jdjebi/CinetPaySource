function updateClipboard(newClip) {
  navigator.clipboard.writeText(newClip).then(function() {
  }, function() {
    alert("Copie impossible")
  });
}

function SimulatorPage(data){
    return  new Vue({

        el: "#v-app",

        data:{

            urls:{
                operators:data.urls.operators,
                services:data.urls.services
            },

            trxSelectedIndex: null,

            operators: [],

            transactionsModels: [],
            transactions: []

        },

        mounted: function (){

            NProgress.start();

            var vm = this;

            this.getAllOperators()
                .then(function(response){
                    vm.operators = response.data;
                    vm.generateOneTrx();
                    vm.trxSelectedIndex = 0
                })
                .catch(function(error){
                    alertify.error("Une erreur c'est produite");
                    console.log(error.message);
                })
                .then(function(){
                    NProgress.done();
                });

            setInterval(function(){
                vm.updateTransactionEvents();
            },500)
       
        },

        methods: {

            /* Requêtes */
            getAllOperators: function(){
                return axios.get(this.urls.operators.rest)
            },

            sendTransactionRequest: function(dataTrx){
                return axios.post(this.urls.services.gateway.sendTrx,dataTrx)
            },

            getTransactionEventsByEntityRefRequest: function(remoteId){
                return axios.get(this.urls.services.eventlog.get.byRemoteId + "/" + remoteId)
            },

            /* Géneration des transactions */

            generateOneTrx: function(){

                let operatorIndex = randomInt(this.operators.length);

                let operator = this.operators[operatorIndex];

                let data = this.getRandomTrxData();

                let trxModel = this.createTrxModel(data, operator);

                let trx = this.createTrx(trxModel);

                return trx;

            },

            createTrxModel: function(data,operator){

                var resourceOperatorCode = null;

                if(operator.useApi && operator.useSIM){
                    resourceOperatorCode = "API_SIM";
                }else if(operator.useApi){
                    resourceOperatorCode = "API";
                }else if(operator.useSIM){
                    resourceOperatorCode = "SIM";
                }

                return {
                    id:null,
                    remoteId:data.remoteId,
                    amount:data.amount,
                    batchnumber:data.batchNumber,
                    country:operator.country.code,
                    phone:data.phone,
                    resourcesOperatorCode: resourceOperatorCode,
                    currency: this.getRandomCurrency(operator),
                    operator:operator.alias
                }

            },

            createTrx: function(trxModel){

                trxModelIndex = this.transactionsModels.length;
                this.transactionsModels.push(trxModel);

                trx = {
                    start:false,
                    sending:false,
                    modelIndex:trxModelIndex,
                    events:[],
                    processStarted:false,
                    lastStartDate:null
                }

                this.transactions.push(trx);

                return trx;

            },

            getRandomTrxData: function(){
                return {
                    remoteId: btoa(Date.now() * fakePhone(10) * fakePhone(10) ),
                    batchNumber: Date.now(),
                    phone: fakePhone(10),
                    amount: fakeAmount(100,500)
                }
            },

            getRandomCurrency: function(operator){
                return operator.country.currency.name
            },

            /* Transactions */

            playTransaction: function(trx, index){

                this.trxSelectedIndex = index;

                trx.lastStartDate = new Date();

                trx.sending = true;

                trx.processStarted = true;

                trx.events = [];

                trxModel = this.transactionsModels[trx.modelIndex];

                NProgress.start();

                this.sendTransactionRequest(trxModel)
                    .then(function(response){
                        console.log(response.data);

                        trxModel.id = response.data.id;
                        trxModel.status = response.data.status
                        alertify.success("Transaction envoyée");
                    })
                    .catch(function(error){
                        trx.processStarted = false;
                        alertify.error("Une erreur c'est produite");
                    })
                    .then(function(){
                        trx.sending = false;
                        NProgress.done();
                    })

            },

            updateTransactionEvents: function(){

                var vm = this;

                this.transactions.forEach(function(trx){

                    vm.getTransactionEventsByEntityRefRequest(vm.transactionsModels[trx.modelIndex].remoteId)
                        .then(function(response){
                            trx.events = response.data;   
                        })
                        .catch(function(error){
                            console.log(error.message);
                            alertify.error("Une erreur c'est produite");
                        });
                })

            },

            /* Helpers */

            getTransactonJSON: function(trx){
                updateClipboard(JSON.stringify(trx,null, 2))
                alertify.success("Version JSON copiée")
            },

            getBadgeBackground:  function(tag){
                if(tag == "INFO"){
                    return "bg-info"
                }else if(tag == "WARNING"){
                    return "bg-warning"
                }else if(tag == "ERROR"){
                    return "bg-danger"
                }else if(tag == "LOG"){
                    return "bg-secondary"
                }else if(tag == "EVENT"){
                    return "bg-success"
                }else if(tag == "UPDATED"){
                    return "bg-primary"
                }else{
                    return "bg-secondary"
                }
            },

            getProcessTime: function(trx){
                events = trx.events;

                nbrEvents = events.length;

                if(nbrEvents < 0){
                    return 0;
                }else if(nbrEvents == 0){
                    return 0
                }
                else if(nbrEvents == 1){
                    return 0
                }else{
                    return (new Date(events[nbrEvents - 1].date).getTime()) - trx.lastStartDate.getTime();
                }
            }

        }

    })
}