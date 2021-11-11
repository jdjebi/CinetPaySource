var formEditCountry = null;
var formEditCountryModal = null;
var toast = null;
var toastError = null;



function CountriesPage(urls){
    
    return new Vue({
        el: "#v-app",

        data:{

            countries: [],
            currencies: [],

            urls:{
                countries:urls.countriesUrls,
                currencies:urls.currenciesUrls
            },

            form:{
                addCountry: false,
                sending:false,
                country:{
                    name:null,
                    code:null,
                    currency:{
                        id:null,
                        name:null,
                    }
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
            this.initModal();
            this.getCountries();
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

            initModal: function (){
                formEditCountry = document.getElementById('formEditOperator');
                formEditCountryModal = new bootstrap.Modal(formEditCountry,{});
            },

            resetForm: function(){
                 this.form.country = {
                    name:null,
                    code:null,
                    currency:{
                        id:null,
                        name:null,
                    }
                }
            },

            getCountries: function(){

                var vm = this;

                axios.get(this.urls.countries.rest)
                    .then(function(response){
                        countries = response.data;
                        vm.countries = countries
                    }).catch(function(error){
                        alert("Une erreur c'est produite");
                        console.log(error.message);
                    }).then(function(){
                        NProgress.done();
                    });
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

            getCountryLogo: function(countryCode){
                return getOperatorCountryLogo(countryCode);
            },
    
            openAddCountry: function (){

                this.form.addCountry = true;

                this.resetForm();

            },

            removeCountryFromList: function (index){
                this.countries.splice(index,1);  
            },

            openEditCountryModal: function (countryIndex){
  
                country = this.countries[countryIndex];

                this.addCountry = false;
                this.resetForm();
                this.form.country.id = country.id;
                this.form.country.name = country.name;
                this.form.country.code = country.code;
                this.form.country.currency.id = country.currency.id;

                formEditCountryModal.show(); 
            },

            cancelAddCountry: function (){
                this.form.addCountry = false;
            },

            createCountry: function (){

                var vm = this;
                var newCountry = vm.form.country;
                vm.toast.content = null;
                vm.toast.error = null;
                vm.form.sending = true;

                NProgress.start();

                axios.post(vm.urls.countries.rest,newCountry)
                    .then(function(response){
                        country = response.data;
                        vm.countries.unshift(country);
                        vm.toast.content = "Pays créé";
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
                        vm.form.addCountry = false;
                    }); 
            },

            deleteCountry: function (index, id){

                var vm = this;

                NProgress.start();

                axios.delete(vm.urls.countries.rest + id.toString())
                    .then(function(response){
                        vm.toast.content = "Pays supprimé";
                        vm.toast.error = false;
                        vm.removeCountryFromList(index);
                        alertify.success('Pays supprimé');
                    }).catch(function(error){
                        vm.toast.content = "Une erreur c'est produite";
                        vm.toast.error = true;
                        alertify.success("Une erreur c'est produite");
                        console.log(error.message);
                    }).then(function(){
                        NProgress.done();
                        vm.form.sending = false;
                    }); 
            },

            editCountry: function(){

                var vm = this;
                vm.form.sending = true;
                
                this.currencies.forEach(function(c){
                    if(c.id == vm.form.country.currency.id){
                        vm.form.country.currency.name = c.name;
                    }
                });

                NProgress.start();

                axios.put(vm.urls.countries.rest + vm.form.country.id,vm.form.country)
                    .then(function(response){
                        var country = response.data 
                        vm.countries.forEach(function(c){
                            if(c.id == country.id){
                                c.name = country.name;
                                c.code = country.code;
                                c.currency.name = country.currency.name;
                                c.currency.id = country.currency.id;
                            }
                        });
                        alertify.success('Mise à jour enregistrée');
                    }).catch(function(error){
                        alertify.error("Une erreur c'est produite");
                        console.log(error.message);
                    }).then(function(){
                        NProgress.done();
                        vm.form.sending = false;
                        formEditCountryModal.hide(); 
                    }); 

            }


        }
    })
}