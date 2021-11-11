var formCreateOperator = null;
var formCreateOperatorModal = null;
var toast = null;
var toastError = null;

function OperatorsPage(urls){
    
    return new Vue({
        el: "#v-app",

        data:{

            operators: [],

            countries: [],

            urls:{
                operators:urls.operatorsUrls,
                countries:urls.countriesUrls
            },

            form:{
                sending:false,
                operator:{
                    name:null,
                    alias:null,
                    country:{
                        id:null,
                        code:null,
                        name:null,
                        currency:null
                    },
                    useApi:true,
                    useSIM:false
                },
                operator_meta:{
                    country_index:null
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
            this.showMainWrapp();
            this.initModal();
            this.initToast();
            this.getOperators();
        },

        methods: {

            auditOperator(operator){
                return libAuditOperator(operator);
            },

            showMainWrapp(){
                $("#main-wrapper").removeClass("d-none");
            },

            initModal: function(){
                formCreateOperator = document.getElementById('formCreateOperator');
                formCreateOperatorModal = new bootstrap.Modal(formCreateOperator,{});
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

            getOperators: function(){

                var vm = this;

                axios.get(this.urls.operators.rest)
                    .then(function(response){
                        vm.operators = response.data;
                    }).catch(function(error){
                        alert("Une erreur c'est produite");
                        console.log(error.message);
                    }).then(function(){
                        
                        vm.getCountries(vm);

                    });
            },

            getCountries: function(vm){

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

            createOperator: function(){
                this.resetOperator();
                formCreateOperatorModal.show();
            },

            resetOperator: function(){
                this.form.operator ={
                    name:null,
                    alias:null,
                    country:{
                        id:null,
                        code:null,
                        name:null,
                        currency:null
                    },
                    useApi:true,
                    useSIM:false
                }

                this.form.operator_meta.country_index = null;
            },

            OnSubmitOperatorForm: function(){

                var vm = this;
                vm.toast.content = null;
                vm.toast.error = null;
                vm.form.sending = true;

                NProgress.start();

                vm.form.operator.country = vm.countries[vm.form.operator_meta.country_index];

                formData = vm.form.operator;

                if(!formData.alias || !formData.country || !formData.name || (!formData.useApi && !formData.useSIM)){
                    alertify.error("Veuillez remplir tous les champs");
                    formCreateOperatorModal.hide();
                    NProgress.done();
                    vm.form.sending = false;
                    return;
                }
                
                axios.post(vm.urls.operators.rest,vm.form.operator)
                    .then(function(response){
                        operator = response.data;
                        vm.operators.unshift(operator);
                        alertify.success("Opérateur créé");
                    }).catch(function(error){
                        console.log(error.message);
                        alertify.success("Une erreur c'est produite, réessayez plus tard.");
                    }).then(function(){
                        NProgress.done();
                        formCreateOperatorModal.hide();
                        vm.form.sending = false;
                    });
            },

            /* Helpers */

            getOperatorLogo: function(logoUrl){
                return operatorLogoHelper(logoUrl);
            },

            getOperatorCountryLogo: function(countryCode){
                return getOperatorCountryLogo(countryCode);
            },

            getOperatorCountResource: function(resources){
                return resources == null ? 0 : resources.length;
            },

            getCountService: function(operator){

                count = 0;

                if(operator){

                    if(operator.apiService){
                        count++;
                    }

                    if(operator.simService){
                        count++;
                    }
                }

                return count;
            },

        }
    })
}