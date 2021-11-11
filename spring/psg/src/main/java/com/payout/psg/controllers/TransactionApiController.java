/**
 * @author Jean-Marc Dje Bi
 * @since 21-07-2021
 * @version 1
 */

package com.payout.psg.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.payout.psg.transactions.Transaction;
import com.payout.psg.transactions.TransactionReceiveStatus;
import com.payout.psg.transactions.TransactionRepository;
import com.payout.psg.transactions.TransactionService;
import com.payout.psg.transactions.TransactionService2;
import com.payout.psg.transactions.exceptions.TransactionNotFoundException;

@CrossOrigin
@RestController
/**
 * Classe representant les endpoints pour effectuer un transfert
 */
public class TransactionApiController {
	
	/**
	 * @see com.payout.psg.transactions.TransactionService
	 */
	@Autowired
	private TransactionService transactionService;
	
	/**
	 * @see com.payout.psg.transactions.TransactionService
	 */
	@Autowired
	private TransactionService2 transactionService2;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	/**
	 * Gere la reception des transactions et retourne un statut sur l'acceptation ou le rejet de la transaction
	 * @param trxList
	 * @return List<TransactionReceiveStatus>
	 * @throws Exception
	 */
	@PostMapping("/api/v1/transactions")
	public List<TransactionReceiveStatus> sendTransactions(@RequestBody List<Transaction> trxList) throws Exception {
				
		if(!trxList.isEmpty()) { // Si la liste des transactions n'est pas vide								
			return transactionService.receive(trxList); // Lancement du processus de reception
		}else {			
			return null;			
		}
		
	}
	
	/**
	 * Recupere les informations d'une transaction
	 * @param id Identifiant
	 * @return
	 */
	@GetMapping("api/v1/transactions/{id}")
	public Transaction getTransaction(@PathVariable String id) {
		
		return transactionRepository.findByTransactionId(id).orElseThrow(() -> new TransactionNotFoundException(id));
		
	}
	
	/**
	 * Test de reponse de l'API
	 * @return
	 */
	@GetMapping("/hello")
	public String hello() {	
							
		return "hello world !";			
	}
	
}
