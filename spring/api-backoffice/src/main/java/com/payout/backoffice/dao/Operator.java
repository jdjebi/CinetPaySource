/**
 * @author Jean-Marc Dje Bi
 * @since 16-08-2021
 * @version 1
 */
package com.payout.backoffice.dao;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Classe representant un operateur
 */
@Entity
@Table(name="operators")
public class Operator {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	Integer id;
	
	private String name = null;
	
	private String logo = null;
		
	private String alias = null;
		
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "country_id")
	private Country country = null;
	
	@Column(name="use_api")
	private boolean useApi = false;
	
	@Column(name="use_sim")
	private boolean useSIM = false;
	
	@JsonIgnoreProperties("operator")
	@OneToMany(targetEntity=Resource.class, mappedBy="operator", cascade=CascadeType.REMOVE, fetch = FetchType.EAGER)
	private List<Resource> resources;
	
	@JsonIgnoreProperties("operator")
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="api_service_id")
	private Service apiService;

	@JsonIgnoreProperties("operator")
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="sim_service_id")
	private Service simService;
		
	public Operator() {}
	
	public Operator(String name, String fullname, Country country) {		
		this.setName(name);
		this.setAlias(fullname);
		this.setCountry(country);
		this.setUseApi(useApi);
		this.setUseSIM(useSIM);
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Country getCountry() {
		return country;
	}
	public void setCountry(Country country) {
		this.country = country;
	}
	
	public boolean getUseApi() {
		return useApi;
	}
	public void setUseApi(boolean useApi) {
		this.useApi = useApi;
	}
	
	public boolean getUseSIM() {
		return useSIM;
	}
	public void setUseSIM(boolean useSIM) {
		this.useSIM = useSIM;
	}
	
	public void buildForApi() {
		Country country = this.getCountry();
		
		if(country == null) {
			this.setCountry(new Country());
			this.getCountry().setCurrency(new Currency());	
		}else {
			if(country.getCurrency() == null) {
				country.setCurrency(new Currency());
			}
		}
	}
	
	public String toString() {
		return "Operator [id=" + id + ", name=" + name + ", alias=" + alias + ", useSIM=" + useSIM + ", useApi=" + useApi + "]";
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public List<Resource> getResources() {
		return resources;
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}
	
	public String getResourcesCode() throws Exception {
		
		if(this.useApi && this.useSIM) {
			return "API_SIM";
		}else if(this.useApi) {
			return "API";
		}else if(this.useSIM){
			return "SIM";
		}else {
			throw new Exception("L'opérateur n'utilise ni une API ni une SIM");
		}
	}

	public Service getApiService() {
		return apiService;
	}

	public void setApiService(Service apiService) {
		this.apiService = apiService;
	}

	public Service getSimService() {
		return simService;
	}

	public void setSimService(Service simService) {
		this.simService = simService;
	}
	
}
