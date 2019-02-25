/*******************************************************************************
 * Copyright (C) Inferyx Inc, 2018 All rights reserved. 
 *
 * This unpublished material is proprietary to Inferyx Inc.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@inferyx.com>
 *******************************************************************************/
package com.inferyx.framework.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Notification;
import com.inferyx.framework.domain.Report;

/**
 * @author Ganesh
 *
 */
@Service
public class NotificationServiceImpl {

	static final Logger logger = Logger.getLogger(NotificationServiceImpl.class);
	
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	
	public boolean sendEMail(Notification notification) {
		logger.info("inside sendEMail method.");
		
		Map<String, Object> emailFromDetails = notification.getEmailFromDetails();
		
		Properties senderProps = new Properties();
		senderProps.put("mail.smtp.host", emailFromDetails.get("host").toString());
		senderProps.put("mail.smtp.port", emailFromDetails.get("port").toString());  
		senderProps.put("mail.smtp.auth", "true");
//		senderProps.put("mail.smtp.connectiontimeout", 5000); 
//		senderProps.put("mail.smtp.socketFactory.port", emailFromDetails.get("port").toString());   
//		senderProps.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");  
//		senderProps.put("mail.smtp.auth.mechanisms", "PLAIN");
	
	//test properties for GMail
//		senderProps.put("mail.smtp.host", "smtp.gmail.com");    
//        senderProps.put("mail.smtp.socketFactory.port", "465");    
//        senderProps.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");    
//        senderProps.put("mail.smtp.auth", "true");    
//        senderProps.put("mail.smtp.port", "465");  
		
		Session session = Session.getDefaultInstance(senderProps, new Authenticator() {
			 protected PasswordAuthentication getPasswordAuthentication() {  
				 return new PasswordAuthentication(emailFromDetails.get("user").toString(), emailFromDetails.get("password").toString());  
			} 
		});
		
		try {
			

			MimeMessage message = new MimeMessage(session);  
			message.setFrom(new InternetAddress(emailFromDetails.get("user").toString())); 
			
			//setting TO e-mail id's
			if (notification.getEmailTo() != null && !notification.getEmailTo().isEmpty()) {
				InternetAddress[] toAddress = new InternetAddress[notification.getEmailTo().size()];
				for (int i = 0; i < notification.getEmailTo().size(); i++) {
					toAddress[i] = new InternetAddress(notification.getEmailTo().get(i));
				}
				message.addRecipients(Message.RecipientType.TO, toAddress);
			} else {
				throw new RuntimeException("Please provide recipient e-Mail id(s).");
			}
			
			//setting BCC e-mail id's
			if (notification.getEmailBCC() != null && !notification.getEmailBCC().isEmpty()) {
				InternetAddress[] bccAddress = new InternetAddress[notification.getEmailBCC().size()];
				for (int i = 0; i < notification.getEmailBCC().size(); i++) {
					bccAddress[i] = new InternetAddress(notification.getEmailBCC().get(i));
				}
				message.addRecipients(Message.RecipientType.BCC, bccAddress);
			}

			//setting CC e-mail id's
			if (notification.getEmailCC() != null && !notification.getEmailCC().isEmpty()) {
				InternetAddress[] ccAddress = new InternetAddress[notification.getEmailCC().size()];
				for (int i = 0; i < notification.getEmailCC().size(); i++) {
					ccAddress[i] = new InternetAddress(notification.getEmailCC().get(i));
				}
				message.addRecipients(Message.RecipientType.CC, ccAddress);
			}
			
			message.setSubject(notification.getEmailSubect());
//			message.setText(notification.getMessage());	     

	        // Create a multipar message
	        Multipart multipart = new MimeMultipart();

	        // Set text message part
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(notification.getMessage());
	        multipart.addBodyPart(messageBodyPart);

	        // Part two is attachment
	        if(notification.getEmailAttachment() != null && !notification.getEmailAttachment().isEmpty()) {
	        	for(String filePath : notification.getEmailAttachment()) {
			        BodyPart attachmentBodyPart = new MimeBodyPart();
			        DataSource source = new FileDataSource(filePath);
			        attachmentBodyPart.setDataHandler(new DataHandler(source));
			        attachmentBodyPart.setFileName(filePath);
			        multipart.addBodyPart(attachmentBodyPart);
	        	}
	        }

	        // Send the complete message parts
	        message.setContent(multipart);
	         
		    //send the message 
	        Transport.send(message);  
			
		    logger.info("message sent successfully...");  
		} catch (MessagingException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed !\n"+"Can not send e-Mail to "+notification.getEmailTo());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed !\n"+"Can not send e-Mail to "+notification.getEmailTo());
		}
		
		return true;
	}
	
	public boolean prepareAndSenEMail(String reportUuid, String reportVersion) {
		logger.info("inside prepareAndSenEMail method.");
		try {
			Report report = (Report) commonServiceImpl.getOneByUuidAndVersion(reportUuid, reportVersion, MetaType.report.toString(), "N");
			if(report != null && report.getNotification() != null) {
				Notification notification = report.getNotification();
				
				Map<String, Object> emailFromDetails = notification.getEmailFromDetails();
				if(emailFromDetails == null || (emailFromDetails != null && !emailFromDetails.isEmpty())) {
					emailFromDetails = new HashMap<>();
				}
				emailFromDetails.put("host", Helper.getPropertyValue("framework.email.host"));
				emailFromDetails.put("port", Helper.getPropertyValue("framework.email.port"));
				emailFromDetails.put("user", Helper.getPropertyValue("framework.email.from"));
				emailFromDetails.put("password", Helper.getPropertyValue("frameowrk.email.password"));
				
				notification.setEmailSubect(Helper.getPropertyValue("framework.email.subject"));
				notification.setEmailFromDetails(emailFromDetails);
				return sendEMail(notification);
			} else {
				throw new RuntimeException("No credentials avilable to send mail.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
