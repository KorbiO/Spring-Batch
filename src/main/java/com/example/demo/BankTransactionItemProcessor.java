package com.example.demo;

import java.text.SimpleDateFormat;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.example.demo.dao.BankTransaction;

//@Component
public class BankTransactionItemProcessor implements ItemProcessor<BankTransaction, BankTransaction>{

	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm");
	/*
	 * on va convertir la date en bonne format 
	 */
	@Override
	public BankTransaction process(BankTransaction bankTransaction) throws Exception {
		bankTransaction.setTransactionDate(dateFormat.parse(bankTransaction.getStrTransactionDate()));
		return bankTransaction;
	}

	
}
