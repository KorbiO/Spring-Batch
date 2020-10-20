package com.example.demo;

import org.springframework.batch.item.ItemProcessor;
import com.example.demo.dao.BankTransaction;

import lombok.Getter;

//@Component
public class BankTransactionItemAnalyticsProcessor implements ItemProcessor<BankTransaction, BankTransaction>{

	/*
	 * c'est un processor qui a un état (des attributs) , il a une mémoir dans la laquel il va stocker des données
	 */
	@Getter private double totalDebit ;
	@Getter private double totalCredit ;
	@Override
	public BankTransaction process(BankTransaction bankTransaction) throws Exception {
		if(bankTransaction.getTransactionType().equals("D")) totalDebit+=bankTransaction.getAmount();
		else if(bankTransaction.getTransactionType().equals("C")) totalCredit+=bankTransaction.getAmount();
		
		return bankTransaction;
	}

	
}
