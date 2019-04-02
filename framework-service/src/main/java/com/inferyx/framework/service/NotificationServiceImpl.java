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
import com.inferyx.framework.domain.Notification;
import com.inferyx.framework.domain.SenderInfo;

/**
 * @author Ganesh
 *
 */
@Service
public class NotificationServiceImpl {
	
	@Autowired
	CommonServiceImpl commonServiceImpl;

	static final Logger logger = Logger.getLogger(NotificationServiceImpl.class);
		
	public boolean sendNotification(Notification notification) {
		logger.info("inside sendEMail method.");
		
		//expecting senderInfo property is alReady set
		SenderInfo senderInfo = notification.getSenderInfo();
		
		Properties senderProps = new Properties();
		senderProps.put("mail.smtp.host", notification.getHost());
		senderProps.put("mail.smtp.port", notification.getPort());  
		senderProps.put("mail.smtp.auth", "true"); 
		senderProps.put("mail.smtp.starttls.enable", "true");
		if(notification.getHost().contains("gmail")) {
			senderProps.put("mail.smtp.socketFactory.class",    
	                "javax.net.ssl.SSLSocketFactory");
		}
		
		Session session = Session.getDefaultInstance(senderProps, new Authenticator() {
			 protected PasswordAuthentication getPasswordAuthentication() {  
				 return new PasswordAuthentication(notification.getFrom(), notification.getPassword());  
			} 
		});
		
		try {
			MimeMessage message = new MimeMessage(session);  
			message.setFrom(new InternetAddress(notification.getFrom())); 
			
			//setting TO e-mail id's
			if (senderInfo.getEmailTo() != null && !senderInfo.getEmailTo().isEmpty()) {
				InternetAddress[] toAddress = new InternetAddress[senderInfo.getEmailTo().size()];
				for (int i = 0; i < senderInfo.getEmailTo().size(); i++) {
					toAddress[i] = new InternetAddress(senderInfo.getEmailTo().get(i));
				}
				message.addRecipients(Message.RecipientType.TO, toAddress);
			} else {
				throw new RuntimeException("Please provide recipient e-Mail id(s).");
			}
			
			//setting BCC e-mail id's
			if (senderInfo.getEmailBCC() != null && !senderInfo.getEmailBCC().isEmpty()) {
				InternetAddress[] bccAddress = new InternetAddress[senderInfo.getEmailBCC().size()];
				for (int i = 0; i < senderInfo.getEmailBCC().size(); i++) {
					bccAddress[i] = new InternetAddress(senderInfo.getEmailBCC().get(i));
				}
				message.addRecipients(Message.RecipientType.BCC, bccAddress);
			}

			//setting CC e-mail id's
			if (senderInfo.getEmailCC() != null && !senderInfo.getEmailCC().isEmpty()) {
				InternetAddress[] ccAddress = new InternetAddress[senderInfo.getEmailCC().size()];
				for (int i = 0; i < senderInfo.getEmailCC().size(); i++) {
					ccAddress[i] = new InternetAddress(senderInfo.getEmailCC().get(i));
				}
				message.addRecipients(Message.RecipientType.CC, ccAddress);
			}
			
			message.setSubject(notification.getSubject()); 

	        // Create a multipar message
	        Multipart multipart = new MimeMultipart();

	        // Set text message part
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(notification.getMessage());
	        multipart.addBodyPart(messageBodyPart);

	        // Part two is attachment
	        if(senderInfo.getEmailAttachment() != null && !senderInfo.getEmailAttachment().isEmpty() && senderInfo.getSendAttachment().equalsIgnoreCase("Y")) {
	        	for(String fileName : senderInfo.getEmailAttachment().keySet()) {
	        		logger.info("notification file path: "+senderInfo.getEmailAttachment().get(fileName));
			        BodyPart attachmentBodyPart = new MimeBodyPart();
			        DataSource source = new FileDataSource(senderInfo.getEmailAttachment().get(fileName));
			        attachmentBodyPart.setDataHandler(new DataHandler(source));
			        attachmentBodyPart.setFileName(fileName);
			        multipart.addBodyPart(attachmentBodyPart);
	        	}
	        }

	        // Send the complete message parts
	        message.setContent(multipart);
	         
		    //send the message 
	        Transport.send(message);  
			
		    logger.info("e-Mail sent successfully to "+senderInfo.getEmailTo()+" ....");  
		} catch (MessagingException e) {
			e.printStackTrace();
			throw new RuntimeException("FAILED !\n"+"Can not send e-Mail to "+senderInfo.getEmailTo());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("FAILED !\n"+"Can not send e-Mail to "+senderInfo.getEmailTo());
		}
		
		return true;
	}
	
	public boolean prepareAndSendNotification(Notification notification) {
		logger.info("inside prepareAndSenEMail method.");
		try {
			if(notification != null && notification.getSenderInfo() != null) {
				notification.setFrom(commonServiceImpl.getConfigValue("framework.email.from"));
				notification.setPassword(commonServiceImpl.getConfigValue("frameowrk.email.password"));
				notification.setHost(commonServiceImpl.getConfigValue("framework.email.host"));
				notification.setPort(commonServiceImpl.getConfigValue("framework.email.port"));
//				notification.setSubject(commonServiceImpl.getConfigValue("framework.email.subject"));
//				notification.setMessage(commonServiceImpl.getConfigValue("framework.email.body"));
				return sendNotification(notification);
			} else {
				throw new RuntimeException("No credentials avilable to send mail.");
			}			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
