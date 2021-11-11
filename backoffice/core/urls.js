const { env }    = require('process');

const prefix = "http://";

const API_BACKOFFICE_HOST         = prefix + env.API_BACKOFFICE_HOST;
const API_BACKOFFICE_BACKEND_HOST = prefix + env.API_BACKOFFICE_BACKEND_HOST;
const EVENTLOG_HOST               = prefix + env.EVENTLOG_HOST;
const GATEWAY_HOST                = prefix + env.GATEWAY_HOST;
const DISPATCHER_HOST             = prefix + env.DISPATCHER_HOST;
const API_TRANSFERT_SERVICE_HOST  = prefix + env.API_TRANSFERT_SERVICE_HOST;
const SIM_TRANSFERT_SERVICE_HOST  = prefix + env.SIM_TRANSFERT_SERVICE_HOST;
const NOTIFICATION_HOST           = prefix + env.NOTIFICATION_HOST;

var UrlsPack = {

    loginUrl: "/",

    dashboardUrl: "/backoffice/v1/",

    player_url: "/backoffice/v1/player",

    playerUrl: "/backoffice/v1/player",

    psgUrl: GATEWAY_HOST + "/trxpack/receive",

    auth:{

        login:"/auth/login",
    },

    backoffice:{
        
        home:"/dashboard",

        transactions:{

            showTransaction:"/backoffice/transactions/id/",

            generate:"/transactions/generate"
        },

        users:{
            show: "/users/"
        }
    },

    apiBackoffice:{

        host:API_BACKOFFICE_HOST,

        actuator:API_BACKOFFICE_HOST + "/actuator/health/",

        login:API_BACKOFFICE_BACKEND_HOST + "/users/login",

        operators:{
            show:"/operateurs/",
            backendRest: API_BACKOFFICE_BACKEND_HOST + "/operators/",
            rest:API_BACKOFFICE_HOST + "/operators/",
        },

        users:{
            rest:API_BACKOFFICE_HOST + "/users/",
        },

        ping: API_BACKOFFICE_HOST + "/ping/",
    },

    operators:{
        show:"/operateurs/",
        rest:API_BACKOFFICE_HOST + "/operators/",

        addService:API_BACKOFFICE_HOST + "/operators/addservice",
        removeService:API_BACKOFFICE_HOST + "/operators/removeservice",
    },

    countries:{
        rest:API_BACKOFFICE_HOST + "/countries/"
    },

    currencies:{
        rest:API_BACKOFFICE_HOST + "/currencies/"
    },

    resources:{
        rest:API_BACKOFFICE_HOST + "/resources/"
    },

    simboxes:{
        rest:API_BACKOFFICE_HOST + "/simbox/"
    },

    services:{
        show:"/services/",
        rest:API_BACKOFFICE_HOST + "/services/",
        newDispatcher:API_BACKOFFICE_HOST + "/services/new/gateway",

        actuatorInfo:"/actuator/info/",
        actuatorEnv:"/actuator/env/",

        getGateway:API_BACKOFFICE_HOST + "/services/get/gateway",
        getGateway:API_BACKOFFICE_HOST + "/services/get/gateway",
        deleteGateway:API_BACKOFFICE_HOST + "/services/delete/gateway",

        transfert:{
            show: "/services/transfert",
        },

        dispatcher:{
            show: API_BACKOFFICE_HOST + "/services/dispatcher"
        },

        gateway:{

            host: GATEWAY_HOST,
           
            show: "/services/system/passerelle",

            api:{
                sendTransactions: GATEWAY_HOST + "/api/v1/transactions"
            },

            transactions: {

                rest:GATEWAY_HOST + "/transactions",

                stats:GATEWAY_HOST + "/transactions/stats/",

                rest2:GATEWAY_HOST + "/v2/transactions/",

                updateStatus:GATEWAY_HOST + "/v2/transactions/update/status/of/",

                findByTransactionId:GATEWAY_HOST + "/v2/transactions/findbytransactionid/",

                finalstats:GATEWAY_HOST + "/transactions/finalstats/",

                operator:GATEWAY_HOST + "/v2/transactions/operator/",

            },

            sendTrx: GATEWAY_HOST + "/v3/transactions/receive",

            sendTrxV4: GATEWAY_HOST + "/v4/transactions/receive",
            
            config:{

                get:GATEWAY_HOST + "/configs",

                update:{
                    operators:API_BACKOFFICE_HOST + "/configs/gateway/operators"
                    
                }
            }
        },

        eventlog:{

            show:"/services/system/journal",

            rest:EVENTLOG_HOST,

            get:{
                allEvents:EVENTLOG_HOST + "/events",
                byRemoteId:EVENTLOG_HOST + "/events/searchby/entityref",
                entityref:EVENTLOG_HOST + "/events/searchby/entityref/",
                stats: EVENTLOG_HOST + "/events/stats/entityref/"
            }
            
        }
    },

    getGameTrxShowURL: function (gameTrx){
        return '/backoffice/v1/game-trx-2/' + gameTrx.id;
    },

    getGameTrxApi: function (id){
        return '/backoffice/api/game-trx-2/' + id;
    }
};

module.exports = UrlsPack;