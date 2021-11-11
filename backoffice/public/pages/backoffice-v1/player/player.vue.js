function TrxPlayerModeAllPage(data){

    var scope = data.scope;
    var getGameTrxUrl = data.urls.getGameTrxUrl;
    var strategiesData = data.strategies.info;
    var strategiesData2 = data.strategies;
    
    return new Vue({

        el:"#v-app",

        data:{

            urls:{
                getGameTrx: getGameTrxUrl,
            },

            gameTrx:null,

            scope:scope,

            strategies:strategiesData2,

            strategy:{
                database: null,
                insertionStrategy: null,
                useKafka: null
            },

            sessions: []

        },

        beforeCreate: function (){
            NProgress.start();
        },

        beforeMount: function (){

            this.getGameTrx();

        },

        mounted: function (){
          
        },

        methods: {

            getGameTrx: function (){

                var vm = this;

                NProgress.start();

                axios.get(this.urls.getGameTrx,{})
                    .then(function(response){
                        NProgress.done();
                        data = response.data,
                        vm.gameTrx = data
                        vm.strategy.database = "mongodb";
                        vm.strategy.insertionStrategy = "onetoone";
                        vm.strategy.useKafka = false;
                        vm.strategy.nbrTrx = data.nbrTrxMax
                    })  
                    .catch(function(error){
                        console.log(error.message);
                    })
                    .then(function(){

                    })
            },
            
            onPlay: function (){
                // Insertion d'une ligne d'éxécution dans tab des éxécutions
                // Variable d'indication de traitement pour session

                let session = this.createSession();

                this.startSession(session);
            },

            createSession: function(){

                console.log(this.strategy);

                let sessionId = this.sessions.length;

                let session = {
                    sessionId: sessionId,
                    gameTrxId: this.gameTrx.id,
                    scope: this.scope,
                    nbrTrx: this.strategy.nbrTrx,
                    database: this.strategy.database,
                    databaseLabel: strategiesData.database[this.strategy.database],
                    insertionStrategy: this.strategy.insertionStrategy,
                    insertionStrategyLabel: strategiesData.insertion[this.strategy.insertionStrategy],
                    useKafka: this.strategy.useKafka,
                    useKafkaLabel: this.strategy.useKafka ? "Oui" : "Non",
                    status:"NEW",
                    running: false,
                    runtime: null,
                    success:null,
                    startingTime: new Date().toLocaleTimeString(),
                    startingTimestamp: Date.now(),
                    endingTime: null,
                    endingtingTimestamp: null,
                    processTime: null,
                    tasks: [],
                };

                this.sessions.unshift(session);

                return session;
            },

            startSession: function (session){
                session.running = true;

                dataToPost = {
                    gameTrxId: this.gameTrx.id,
                    scope: this.scope,
                    nbrTrx: this.strategy.nbrTrx,
                    database: this.strategy.database,
                    insertionStrategy: this.strategy.insertionStrategy,
                    useKafka: this.strategy.useKafka,
                }

                session.tasks.push({
                    action:"Envoie des transactions",
                    date:new Date().toLocaleTimeString()
                });

                session.running = true;

                axios.post('/backoffice/v1/player/send',dataToPost)
                    .then(function(response){

                        data = response.data;
                        tasks = data.tasks;
                        session.success = true;

                        tasks.forEach(element => {
                            session.tasks.push(element);
                        });

                        session.status = "PENDING";

                        session.processTime = data.startedProcessTime;

                    })  
                    .catch(function(error){

                        var message = "";

                        session.success = false;

                        if(error.response){
                            message = "Echec: " + error.response.data;
                        }else{
                            message = "Echec: " + error.message;
                            console.log(error);
                        }
                      
                        session.status = "FAILED";

                        session.tasks.push({
                            action:"Transactions non envoyées",
                            date:new Date().toLocaleTimeString(),
                            data:message
                        });
                    })
                    .then(function(){
                        session.running = false;
                        endingTime = new Date().toLocaleTimeString();
                        session.endingTime = endingTime;
                        session.endingTimestamp = Date.now();
                        session.runtime = session.endingTimestamp - session.startingTimestamp;

                        if(session.success){

                            session.processTime = session.endingTimestamp - session.processTime;

                            session.tasks.push({
                                action:"Traitement terminé",
                                date: new Date().toLocaleTimeString(),
                                data:"Test réussie en " + session.processTime + "ms"
                            });

                        }else{

                            session.tasks.push({
                                action:"Traitement terminé",
                                date: new Date().toLocaleTimeString(),
                                data:"Test échoué !"
                            });

                        }

                    })
            }

        }
    });
}