package br.com.tddspring.cursotddspringudemy.service;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface EmailService {

	void sendMails(String message, List<String> mailList);

}
