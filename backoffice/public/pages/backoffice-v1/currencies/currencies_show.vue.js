var toast = null;
var toastError = null;

function CurrenciesPage(currenciesUrls){
    
    return new Vue({
        el: "#v-app",

        data:{

            currencies: [],

            urls:{
                currencies:currenciesUrls
            },

            form:{
                addCurrency: false,
                sending:false,
                currency:{
                    name:null,
                }
            },

            toast:{
                content:null,
                error:false
            },

        },

        beforeCreate: function(){
            NProgress.start();
        },

        mounted: function(){
            this.initToast();
            this.getCurrencies();
            this.showMainWrapp();
        },

        methods: {

            showMainWrapp(){
                $("#main-wrapper").removeClass("d-none");
            },

            initToast: function(){
                let toastElList = [].slice.call(document.querySelectorAll('.toast'))
                let toasts = toastElList.map(function (toastEl) {
                    return new bootstrap.Toast(toastEl, {})
                });
                toast = toasts[0];

                toastElList = [].slice.call(document.querySelectorAll('#toast-error'))
                toasts = toastElList.map(function (toastEl) {
                    return new bootstrap.Toast(toastEl, {})
                });

                toastError = toasts[0];
            },

            getCurrencies: function(){

                var vm = this;

                axios.get(this.urls.currencies.rest)
                    .then(function(response){
                        currencies = response.data;
                        vm.currencies = currencies
                    }).catch(function(error){
                        alert("Une erreur c'est produite");
                        console.log(error.message);
                    }).then(function(){
                        NProgress.done();
                    });
            },
    
            openAddCurrency: function (){

                this.form.addCurrency = true;

                this.form.currencies = {
                    name:null,
                    code:null
                }

            },

            cancelAddCurrency: function (){

                this.form.addCurrency = false;

            },

            createCurrency: function (){

                var vm = this;
                var newCurrency = vm.form.currency;
                vm.toast.content = null;
                vm.toast.error = null;
                vm.form.sending = true;

                NProgress.start();

                axios.post(vm.urls.currencies.rest,newCurrency)
                    .then(function(response){
                        currency = response.data;
                        vm.currencies.unshift(currency);
                        vm.toast.content = "Devise créé";
                        vm.toast.error = false;
                        toast.show();
                    }).catch(function(error){
                        vm.toast.content = "Une erreur c'est produite";
                        vm.toast.error = true;
                        toastError.show();
                        alert("Une erreur c'est produite");
                        console.log(error.message);
                    }).then(function(){
                        NProgress.done();
                        vm.form.sending = false;
                        vm.form.addCurrency = false;
                    }); 
            },

            deleteCurrency: function (index, id){

                var vm = this;

                NProgress.start();

                axios.delete(vm.urls.currencies.rest + id.toString())
                    .then(function(response){
                        vm.toast.content = "Pays supprimé";
                        vm.toast.error = false;
                        vm.removeCurrencyFromList(index);
                        toast.show();
                    }).catch(function(error){
                        vm.toast.content = "Une erreur c'est produite";
                        vm.toast.error = true;
                        toastError.show();
                        alert("Une erreur c'est produite");
                        console.log(error.message);
                    }).then(function(){
                        NProgress.done();
                        vm.form.sending = false;
                    }); 
            },

            removeCurrencyFromList: function (index){
                this.currencies.splice(index,1);  
            }

        }
    })
}