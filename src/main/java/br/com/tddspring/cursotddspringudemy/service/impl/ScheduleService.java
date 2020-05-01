package br.com.tddspring.cursotddspringudemy.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import br.com.tddspring.cursotddspringudemy.model.entity.Loan;
import br.com.tddspring.cursotddspringudemy.service.EmailService;
import br.com.tddspring.cursotddspringudemy.service.LoanService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {
	
	@Value("${application.mail.lateloans.message}")
	private String message;
	
	public static final String CRON_LATE_LOANS = "0 0 0 1/1 * ?";
	
	private final LoanService loanService;
	private final EmailService emailService;
	
	@Scheduled(cron = CRON_LATE_LOANS)
	public void sendMailToLoans() {
		List<Loan> allLateLoans = loanService.getAllLateLoans();
		List<String> mailList = allLateLoans.stream()
				.map(loan -> loan.getCustomerEmail())
				.collect(Collectors.toList());
		
		emailService.sendMails(message, mailList);
		
	}
}
