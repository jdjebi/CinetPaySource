function getCountryPrefix(prefix){
    country_tel_prefix = {
        CI:"225",
        SN:"221",
        CM:"237",
        ML:"223"
    }
    return country_tel_prefix[prefix];
}

function fakePhone(length)
{
    var str = "";
    while (str.length < length) {
        str += Math.floor(Math.random() * 10 + 1);
    }
    return str;
}

function fakeAmount(max,min)
{
   
    return  Math.round(Math.floor(Math.random() * (max - min) + min) / 10) * 10;
}

function GameTrxShowPage(gameTrxRepository){

    var create_gameTrx_url = "/backoffice/game-trx-2";
    var get_all_trx_url = "/backoffice/game-trx-2/trx/" + gameTrxRepository.id;
    var save_tests_trx_url = "/backoffice/game-trx-2/save";
    var delete_tests_trx_url = "/backoffice/game-trx-2/"+ gameTrxRepository.id+"/trx/delete/all";
    var playerUrl = gameTrxRepository.playerUrl;

    var FormGameTrx = null;
    var FormGameTrxModal = null;
    var toast = null;
    var defaultpaymentmethod = "API";
    var gameTrx = null;
    var defaultTrxAmount = 50000;
    var limitToShow = 50;

    return new Vue({

        el:"#v-app",

        data:{

            gameTrx:gameTrxRepository,

            url:{
                gameTrx:{
                    create: create_gameTrx_url,
                    getTrx: get_all_trx_url,
                    save_tests_trx: save_tests_trx_url,
                    deleteTestsTrx:delete_tests_trx_url,
                    playerUrl:playerUrl
                }
            },

            form: {
                gameTrx:{
                    sending:false,
                    message:{
                        content:null,
                        error:false
                    },
                    title:null,
                    numbertrx:null,
                    paymentmethod:null,
                    country:null
                }
            },

            limitToShow: limitToShow,
            trx_creating_logs: [],

            generating:false,

            gamesTrx: [],

            toast: {
                content:null
            },

            counter: 0,

            flash: {
                message:null,
                error:null
            }

        },

        mounted: function(){
            vm = this;
            url = this.url.gameTrx.getTrx;

            NProgress.start();

            axios.get(url)
                .then(function(response){
                    vm.trx_creating_logs = response.data;
                    NProgress.done();
                })
                .catch(function(error){
                    console.log(error);
                    console.log(error.message);
                });
        },

        computed: {
            minTrxCreated: function(){
                return this.trx_creating_logs.slice(0,this.limitToShow);
            },
        },

        methods:{

            error: function(error){
                console.log(error);
                console.log(error.message)
            },

            getCountryPrefix: function(prefix){
                country_tel_prefix = {
                    CI:"225",
                    SN:"221",
                    CM:"237",
                    ML:"223"
                }                
                return country_tel_prefix[prefix];
            },

            dropTrx: function(progress=true){
                var vm = this;
                var progress = progress;
                url = this.url.gameTrx.deleteTestsTrx;

                axios.delete(url)
                    .then(function(response){
                        vm.trx_creating_logs = [];
                    })
                    .catch(function(error){
                        vm.error(error);
                    }).then(function(){
                        if(progress)
                            NProgress.done();
                    })
            },

            cleanTrx: function(){
                NProgress.start();
                this.dropTrx();
            },

            generateTrx: function(){
                var vm = this;
                var g = this.gameTrx;
                var trxList = [];
                var trx_creating_logs = [];
                var batchNumber = "" + (Date.now() + g.id);
                vm.generating = true;

                NProgress.start();

                this.dropTrx(false);

                function genTrxProcessor() {
                    return new Promise(resolve => {
                        for (let i = 0; i < g.numbertrx; i++) {
                            prefix = getCountryPrefix(g.country);
                            phone = fakePhone(10);
                            amount = fakeAmount(10000,1000000);
                            country = g.country;
                            trx = {
                                prefix:prefix,
                                country:country,
                                amount:amount,
                                batchNumber:batchNumber,
                                phone:phone,
                                paymentmethod:country
                            }
                            trxList.push(trx);
                        }
                        resolve(trxList);
                    });
                }
                  
                async function genTrx() {
                    trxList = await genTrxProcessor();
                }
                
                setTimeout(function(){
                    genTrx().then(function(){
                        setTimeout(function(){
                            //vm.trx_creating_logs = trxList;
                            //vm.generating = false;
                            vm.saveTrx(trxList);
                        },500);
                    });  
                },1000);                
            },

            saveTrx: function (trxListData){
                var url = this.url.gameTrx.save_tests_trx;
                axios.post(url,{
                    trxList:trxListData,
                    gameTrxId:this.gameTrx.id
                })
                .then(function(response){
                    vm.trx_creating_logs = response.data;
                }).catch(function(error){
                    console.log("Une erreur c'est produite")
                    // console.log(error);
                    console.log(error.response.status);
                    console.log(error.message);
                }).then(function(){
                    NProgress.done();
                    vm.generating = false;
                });                                                                                                                                                                                                                                                                      
            }
        }
    });

}