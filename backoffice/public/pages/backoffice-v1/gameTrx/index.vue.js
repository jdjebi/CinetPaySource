var create_gameTrx_url = "/backoffice/game-trx-2";
var get_all_gameTrx_url = "/backoffice/game-trx-2/all";

var FormGameTrx = null;
var FormGameTrxModal = null;
var toast = null;
var defaultpaymentmethod = "API";

var vm = new Vue({

    el:"#v-app",

    data:{

        url:{
            gameTrx:{
                create: create_gameTrx_url,
                getAll: get_all_gameTrx_url,
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

        gamesTrx: [],

        toast: {
            content:null
        },

        flash: {
            message:null,
            error:null
        }

    },

    beforeCreate: function(){
        NProgress.start();
    },

    mounted: function(){
        FormGameTrx = document.getElementById('new-game-trx')
        FormGameTrxModal = new bootstrap.Modal(FormGameTrx,{});

        let toastElList = [].slice.call(document.querySelectorAll('.toast'))
        let toasts = toastElList.map(function (toastEl) {
            return new bootstrap.Toast(toastEl, {})
        });

        toast = toasts[0];

        this.getAllGameTrx();
    },

    methods:{
        getAllGameTrx: function(){
            var vm = this;
            let url = this.url.gameTrx.getAll;

            axios.get(url)
                .then(function(response){
                    data = response.data;
                    vm.gamesTrx = data;
                })
                .catch(function (error) {
                    console.log(error);
                })
                .then(function () {
                    NProgress.done();
                });
        },

        FormGameTrxOnSubmit: function(){
            var formData = this.form.gameTrx;
            var vm = this;
            var url = this.url.gameTrx.create;
            vm.form.sending = true;

            axios.post(url,formData)
                .then(function(response){
                    data = response.data;
                    if(data.create_success){
                        vm.form.gameTrx.message.error = data.create_success;
                        vm.gamesTrx.push(data.data.gameTrx);
                        FormGameTrxModal.hide();
                        vm.toast.content = data.comment;
                        toast.show();
                    }else{
                        vm.form.gameTrx.message.content = "Une erreur c'est produite. Veuillez réessayez plus tard.";
                        vm.form.gameTrx.message.error = true;
                    }
                })  
                .catch(function (error) {
                    vm.form.gameTrx.message.content = "Une erreur c'est produite. Veuillez réessayez plus tard.";
                    vm.form.gameTrx.message.error = true;
                    console.log(error);
                    // FormGameTrxModal.hide();
                })
                .then(function () {
                    vm.form.sending = false;
                });
        },

        OpenFormGameTrx: function (){
            this.form.gameTrx.message.content = null;
            this.form.gameTrx.message.error = null;
            this.form.gameTrx.title = null;
            this.form.gameTrx.country = null;
            this.form.gameTrx.paymentmethod = defaultpaymentmethod;
            this.form.gameTrx.numbertrx = null;
            FormGameTrxModal.show();
        }
    }
});