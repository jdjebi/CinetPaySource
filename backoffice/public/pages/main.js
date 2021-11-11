var operatorCounter = $("#operatorCounter");
var servicesIndicator = $("#servicesIndicator");

var URLS_PACK = null;
var apiBackofficeOperators = null
var apiBackofficeServices = null;

function getOperatorsRequest(){
	return axios.get(apiBackofficeOperators);		
}

function getServicesRequest(){
	return axios.get(apiBackofficeServices);		
}

function getData(){
	Promise.all([getOperatorsRequest(), getServicesRequest()])
		.then(function(result){
				updateOperators(result[0].data);
				updateServices(result[1].data);
			})
			.catch(function(error){
				console.log(error.message);
				// alertify.error("Le backoffice semble indisponible");
			});	
}

function updateOperators(operators){
	nbrOperators = operators.length;
	operatorCounter.text(nbrOperators);
}

function updateServices(services){

	downServices = 0;
	downSystemServices = 0;

	for (var i = 0; i < services.length; i++) {
		service = services[i];

		if(service.status == false && service.type != "SYSTEM"){
			downServices++;
		}else if(service.status == false && service.type == "SYSTEM"){
			downSystemServices++;
		}
	}

	if(downSystemServices > 0){
		if(!servicesIndicator.hasClass("red")){
			servicesIndicator.removeClass("green");
			servicesIndicator.removeClass("orange");
			servicesIndicator.addClass("red");
		}
	} else if(downServices > 0){
		if(!servicesIndicator.hasClass("orange")){
			servicesIndicator.removeClass("green");
			servicesIndicator.removeClass("red");
			servicesIndicator.addClass("orange");
		}
	}else{
		if(!servicesIndicator.hasClass("green")){
			servicesIndicator.removeClass("orange");
			servicesIndicator.removeClass("red");
			servicesIndicator.addClass("green");	
		}
	}
				
				
}

function mainLoop(){
	getData();
}

function main(){

	axios.get("/urls")

		.then(function(response){

			URLS_PACK = response.data;

			apiBackofficeOperators = URLS_PACK.operators.rest;
			apiBackofficeServices = URLS_PACK.services.rest;

			mainLoop();

			setInterval(function(){
				mainLoop();
			},3000);

		})

		.catch(function(error){
			console.log(error.message);
			alertify.error("Connexion indisponible");
		});

}

main();
