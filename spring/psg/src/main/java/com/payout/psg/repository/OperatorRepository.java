/**
 * @author Jean-Marc Dje Bi
 * @since 29-07-2021
 * @version 2
 */

package com.payout.psg.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.payout.psg.model.Operator;

/**
 * Repository de l'objet operateur
 */
public interface OperatorRepository extends CrudRepository<Operator, String>{
	
	Operator findByAlias(String alias);
	
	List<Operator> findAll();
	
}
