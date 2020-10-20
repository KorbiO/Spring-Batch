package com.example.demo.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.BankTransactionItemAnalyticsProcessor;

@RestController
public class BankRestController {

	@Autowired private JobLauncher jobLauncher; 			//celui qui va démarrer le job
	@Autowired private Job job;
	@Autowired private BankTransactionItemAnalyticsProcessor bankTransactionItemAnalyticsProcessor;
	@RequestMapping("/loadData")
	public BatchStatus load() throws Exception {
		Map<String, JobParameter> parameters = new HashMap<>();
		parameters.put("time", new JobParameter(System.currentTimeMillis()));
		JobParameters jobParameters = new JobParameters(parameters);
		JobExecution jobExecution = jobLauncher.run(job, jobParameters);
		while (jobExecution.isRunning()) {
			System.out.println("....");
			
		}
		return jobExecution.getStatus();
		
	}
	@GetMapping("/analytics")
	public Map<String , Double> analytics(){
		Map<String, Double> map = new HashMap<String, Double>();
		map.put("totalCredit",bankTransactionItemAnalyticsProcessor.getTotalCredit());
		map.put("totalDebit",bankTransactionItemAnalyticsProcessor.getTotalDebit());
		return map;
		
	}
	
}
