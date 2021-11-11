package com.payout.backoffice.repository;

import org.springframework.data.repository.CrudRepository;

import com.payout.backoffice.dao.Country;


public interface CountryRepository extends CrudRepository<Country, Integer>{
	Country findByCode(String code);
}
