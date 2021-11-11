package com.payout.backoffice.services.dispatcher;

import java.util.ArrayList;
import java.util.List;

import com.payout.backoffice.dao.Resource;

/**
*
* Classe representant la configuration d'un operateur
*
* @author  Jean-Marc Dje Bi
* @version 1
* @since   15-07-2021
*
*/
public class OperatorsConfigEntity {
	
	/**
	* Nom de l'operateur
	*/
	private String name;
	
	/**
	* Code de la ressource
	*/
	private String resourcesCode;
	
	/**
	* Url du service de transfert de la SIM de l'operateur
	*/
	private String urlSim;
	
	/**
	* Url de l'API de l'operateur
	*/
	private String urlApi;
	
	/**
	* Topic ecoute par le service de transfert par SIM de l'operateur
	*/
	private String topicSim;
	
	/**
	* Topic ecoute par le service de transfert par API de l'operateur
	*/
	private String topicApi;
	
	/**
	* Resource d'API de l'operateur
	*/
	private ApiEntity api; 
	
	/**
	* Resource de SIM de l'operateur
	*/
	private List<SimEntity> sims; 
	
	public OperatorsConfigEntity() {
		sims = new ArrayList<SimEntity>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getResourcesCode() {
		return resourcesCode;
	}

	public void setResourcesCode(String resourcesCode) {
		this.resourcesCode = resourcesCode;
	}

	public String getUrlSim() {
		return urlSim;
	}

	public void setUrlSim(String urlSim) {
		this.urlSim = urlSim;
	}

	public String getUrlApi() {
		return urlApi;
	}

	public void setUrlApi(String urlApi) {
		this.urlApi = urlApi;
	}

	public ApiEntity getApi() {
		return api;
	}

	public void setApi(Resource apiRessource) {
		if(apiRessource != null) {
			api = new ApiEntity(apiRessource);
		}else {
			api = null;
		}
	}

	public List<SimEntity> getSims() {
		return sims;
	}

	public void setSims(List<SimEntity> sims) {
		this.sims = sims;
	}
	
	/**
	* Ajoute plusieurs SIM a la liste des ressources des SIM
	*/
	public void pushSims(List<Resource> simResources) {
		
		if(simResources.size() != 0) {
			for(int i = 0; i < simResources.size(); i++) {
				this.addSim(simResources.get(i));
			}
		}else {
			sims = null;
		}
		
	}
	
	/**
	* Ajoute une SIM a la liste des ressources des SIM
	*/
	public void addSim(Resource simResource) {
		sims.add(new SimEntity(simResource));
	}

	public String getTopicSim() {
		return topicSim;
	}

	public void setTopicSim(String topicSim) {
		this.topicSim = topicSim;
	}

	public String getTopicApi() {
		return topicApi;
	}

	public void setTopicApi(String topicApi) {
		this.topicApi = topicApi;
	}
}
