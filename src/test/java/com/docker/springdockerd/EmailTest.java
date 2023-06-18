package com.docker.springdockerd;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.viram.dev.dto.EmailParam;
import com.viram.dev.service.EmailService;

public class EmailTest {

	public static void main(String[] args) throws AddressException, MessagingException, IOException {
		
		EmailService mailService = new EmailService();
		
		EmailParam emailParam = new EmailParam();
		emailParam.setEmail("viram.dhangar@gmail.com");
		emailParam.setSubject("Test");
		emailParam.setOtp("123");
		emailParam.setEmailBody("otp");
		
		mailService.sendmailOTP(emailParam);
	}
	
}
