package br.com.tddspring.cursotddspringudemy.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import br.com.tddspring.cursotddspringudemy.service.EmailService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{

	@Value("${application.mail.default-remetent}")
	private String remetent;
	
	private final JavaMailSender javaMailSender;
	
	@Override
	public void sendMails(String message, List<String> mailList) {
		String[] mail = mailList.toArray(new String[mailList.size()]);
		
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(remetent);
		mailMessage.setSubject("Livro atrasado");
		mailMessage.setText(message);
		mailMessage.setTo(mail);
		
		javaMailSender.send(mailMessage);
		
	}

}
