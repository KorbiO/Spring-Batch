package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.example.demo.dao.BankTransaction;


@Configuration
@EnableBatchProcessing	//activer spring-batch
public class SpringBatchConfig {
	//Configurer un Job
	
	@Autowired private JobBuilderFactory jobBuilderFactory;
	@Autowired private StepBuilderFactory stepBuilderFactory;
	@Autowired private ItemReader<BankTransaction> bankTransactionItemReader;
	@Autowired private ItemWriter<BankTransaction> bankTransactionItemWriter;
	//@Autowired private ItemProcessor<BankTransaction, BankTransaction> bankTransactionItemProcess;
	
	@Bean
	public Job bankJob() {
		Step step1 = stepBuilderFactory.get("step-load-data")
				.<BankTransaction,BankTransaction>chunk(100)
				.reader(bankTransactionItemReader)
				.processor(compositeItemProcessor())	//c'est un pipe ligne de processor
				.writer(bankTransactionItemWriter)
				.build();
		return jobBuilderFactory.get("bank-data-loader-job")
				.start(step1).build();
	}
	
	/*
	 * on va ajouter une liste de processor 
	 * */
	 
	@Bean
	public ItemProcessor<BankTransaction,BankTransaction> compositeItemProcessor(){
		List<ItemProcessor<BankTransaction, BankTransaction>> itemProcessors = new ArrayList<ItemProcessor<BankTransaction,BankTransaction>>();		//déclarer une liste de processor
		itemProcessors.add(itemProcessor1());
		itemProcessors.add(itemProcessor2());
		CompositeItemProcessor<BankTransaction, BankTransaction> compositeItemProcessor = new CompositeItemProcessor<>();
		compositeItemProcessor.setDelegates(itemProcessors);
		return compositeItemProcessor;
	}
	@Bean
	public ItemProcessor<BankTransaction, BankTransaction> itemProcessor1(){	//instancier le processor (BankTransactionItemProcessor)
		return new BankTransactionItemProcessor();
	}
	@Bean
	public ItemProcessor<BankTransaction, BankTransaction> itemProcessor2(){		//instancier le processor (BankTransactionItemAnalyticsProcessor)
		return new BankTransactionItemAnalyticsProcessor();
	}
   @Bean
	public FlatFileItemReader<BankTransaction> flatFileItemReader(@Value("${inputFile}") Resource inputFile){
		
		FlatFileItemReader<BankTransaction> fileItemReader = new FlatFileItemReader<>();
		fileItemReader.setName("FFIR1");
		fileItemReader.setLinesToSkip(1);
		fileItemReader.setResource(inputFile);
		fileItemReader.setLineMapper(lineMapper());
		return fileItemReader;
		
	}
   
   @Bean
   public LineMapper<BankTransaction> lineMapper() {
	   DefaultLineMapper<BankTransaction> lineMapper = new DefaultLineMapper<>();
	   DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
	   lineTokenizer.setDelimiter(",");  	//Délimiteur de notre fichier CSV
	   lineTokenizer.setStrict(false); 	
	   lineTokenizer.setNames("id","accountID","strTransactionDate","transactionType","amount");	//il faut respecter l'ordre de la classe utiliser
	   lineMapper.setLineTokenizer(lineTokenizer);
	   BeanWrapperFieldSetMapper<BankTransaction> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
	   fieldSetMapper.setTargetType(BankTransaction.class);
	   lineMapper.setFieldSetMapper(fieldSetMapper);
	   
	   
	return lineMapper;
}
  
}
