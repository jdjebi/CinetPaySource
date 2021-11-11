function getApplicationPropertiesData(data){

    configs = [];

    data.forEach(function(elm){
        if(elm.name.includes('application.properties')){
            for (const [key, value] of Object.entries(elm.properties)) {
                configs.push({
                    'name':key,
                    'value':value.value
                });
            }

        }
    });

    return configs;
}

function getServiceShowUrls(service,urls){
    serviceCode = service.code;
    if(serviceCode == "PSP"){
        return urls.services.transfert.show + "/" + service.id;
    }else if(serviceCode == "PES"){
        return urls.services.eventlog.show;
    }else if(serviceCode == "PSG"){
        return urls.services.gateway.show;
    }
    else if(serviceCode == "PDS"){
        return urls.services.dispatcher.show;
    }else{
        return urls.services.transfert.show + "/" + service.id;
    }
}