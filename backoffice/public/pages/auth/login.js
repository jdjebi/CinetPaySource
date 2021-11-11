function loginPage(urls){
  return new Vue({
    el: '#v-app',
    data:{

      urls:urls,

      form:{
        sending:false,
        message:{
          content: errorMessage,
          error:errorStatus
        },
        username: null,
        password: null
      },

    },

    mounted: function(){
      this.showWrapper();
    },

    methods:{

      showWrapper: function(){
        $("#main-wrapper").removeClass("d-none");
      },

      OnSubmit: function(){
  
        var vm = this;
        var url = this.urls.auth.login;
        var username = this.form.username;
        var password = this.form.password;
  
        this.form.sending = true;
        this.form.message.content = "";
        this.form.message.error = true;
  
        axios.post(url,{
          username:username,
          password:password
        })
  
        .then(function(response){
          data = response.data;
    
          if(data.auth_success == true){
              window.location = vm.urls.backoffice.home;
          }else if(data.auth_success == false){
              vm.form.message.content = data.comment;
              vm.form.password = null;
          }else{
              vm.form.message.content = data.comment;
              vm.form.password = null;
          }
        })
        
        .catch(function (error) {
          console.log(error)
        })
        
        .then(function () {
          vm.form.sending = false;
        });
      }
    }
  })
}

function backendSync(urls){
  return new Vue({

    el:"#v-sync",

    data: {
      urls:urls,
      syncOk:true
    },

    mounted: function(){
      var vm = this;
      setInterval(vm.checkSync,5000);
    },

    methods: {

      checkSync: function(){

        axios.get(this.urls.apiBackoffice.host)
          .then(function(response){
            $("#sync-messager").hide();
          })
          .catch(function(error){
            $("#sync-messager").show();
          })
      }

    }

  });
}