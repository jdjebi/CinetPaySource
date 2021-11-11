function libAuditOperator(operator){

    rxCounter = operator.resources ? operator.resources.length : 0;

    sxCounter = (operator.apiService ? 1 : 0) + (operator.simService ? 1 : 0);

    auditStatus = false;

   if(rxCounter > 0 && sxCounter > 0){
    auditStatus = true;
   }

    return {
        resourcesCounter:rxCounter,
        servicesConter: sxCounter,
        status: auditStatus
    }

}

function libGetTransactionStatusStyleBadgeBackgroundClass(status){
    if(status == "REC" || status == "INFO"){
      return "bg-primary";
    } else if(status == "PAUSE"){
      return "bg-warning";
    } else if(status == "REJ"){
      return "bg-danger";
    }else if(status == "SUCCESS" || status == "EVENT"){
      return "bg-success";
    }else if(status == "FAILURE" || status == "ERROR"){
      return "bg-danger";
    }else if(status == "LOG"){
      return "bg-secondary";
    }
    else{
      return "bg-dark";
    }
}