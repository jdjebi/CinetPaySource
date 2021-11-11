package com.payout.backoffice.webconf.gateway;

import com.payout.backoffice.dao.Operator;

/**
*
* Classe representant un operateur au niveau de la passerelle
*
* @author  Jean-Marc Dje Bi
* @version 1.0.1
* @since   15-07-2021
*
*/

public class OperatorGateway {
	
	/**
	* Nom minifie de l'operateur
	*/
	private String alias;
	
	/**
	* Chaine caracterisant le type de ressources utilise par l'operateur (API,SIM,API_SIM) 
	*/
	private String ressourcesCode;
	
	public OperatorGateway(Operator operator){
		
		setAlias(operator.getAlias());
		
		if(operator.getUseApi() && operator.getUseSIM()) {
			setRessourcesCode("API_SIM");
		}else if(operator.getUseApi()) {
			setRessourcesCode("API");
		}else if(operator.getUseSIM()){
			setRessourcesCode("SIM");
		}
				
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getRessourcesCode() {
		return ressourcesCode;
	}

	public void setRessourcesCode(String ressourcesCode) {
		this.ressourcesCode = ressourcesCode;
	}
}
