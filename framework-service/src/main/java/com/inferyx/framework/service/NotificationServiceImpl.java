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
import com.inferyx.framework.domain.EMailInfo;
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
		
		//expecting eMailInfo property is already set
		EMailInfo eMailInfo = notification.geteMailInfo();
		
		Properties senderProps = new Properties();
		senderProps.put("mail.smtp.host", notification.getHost());
		senderProps.put("mail.smtp.port", notification.getPort());  
		senderProps.put("mail.smtp.auth", "true"); 
		senderProps.put("mail.smtp.starttls.enable", "true");
		
		Session session = Session.getDefaultInstance(senderProps, new Authenticator() {
			 protected PasswordAuthentication getPasswordAuthentication() {  
				 return new PasswordAuthentication(notification.getFrom(), notification.getPassword());  
			} 
		});
		
		try {
			MimeMessage message = new MimeMessage(session);  
			message.setFrom(new InternetAddress(notification.getFrom())); 
			
			//setting TO e-mail id's
			if (eMailInfo.getEmailTo() != null && !eMailInfo.getEmailTo().isEmpty()) {
				InternetAddress[] toAddress = new InternetAddress[eMailInfo.getEmailTo().size()];
				for (int i = 0; i < eMailInfo.getEmailTo().size(); i++) {
					toAddress[i] = new InternetAddress(eMailInfo.getEmailTo().get(i));
				}
				message.addRecipients(Message.RecipientType.TO, toAddress);
			} else {
				throw new RuntimeException("Please provide recipient e-Mail id(s).");
			}
			
			//setting BCC e-mail id's
			if (eMailInfo.getEmailBCC() != null && !eMailInfo.getEmailBCC().isEmpty()) {
				InternetAddress[] bccAddress = new InternetAddress[eMailInfo.getEmailBCC().size()];
				for (int i = 0; i < eMailInfo.getEmailBCC().size(); i++) {
					bccAddress[i] = new InternetAddress(eMailInfo.getEmailBCC().get(i));
				}
				message.addRecipients(Message.RecipientType.BCC, bccAddress);
			}

			//setting CC e-mail id's
			if (eMailInfo.getEmailCC() != null && !eMailInfo.getEmailCC().isEmpty()) {
				InternetAddress[] ccAddress = new InternetAddress[eMailInfo.getEmailCC().size()];
				for (int i = 0; i < eMailInfo.getEmailCC().size(); i++) {
					ccAddress[i] = new InternetAddress(eMailInfo.getEmailCC().get(i));
				}
				message.addRecipients(Message.RecipientType.CC, ccAddress);
			}
			
			message.setSubject(notification.getSubect()); 

	        // Create a multipar message
	        Multipart multipart = new MimeMultipart();

	        // Set text message part
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(notification.getMessage());
	        multipart.addBodyPart(messageBodyPart);

	        // Part two is attachment
	        if(eMailInfo.getEmailAttachment() != null && !eMailInfo.getEmailAttachment().isEmpty()) {
	        	for(String filePath : eMailInfo.getEmailAttachment()) {
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
			
		    logger.info("e-Mail sent successfully to "+eMailInfo.getEmailTo()+" ....");  
		} catch (MessagingException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed !\n"+"Can not send e-Mail to "+eMailInfo.getEmailTo());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed !\n"+"Can not send e-Mail to "+eMailInfo.getEmailTo());
		}
		
		return true;
	}
	
	public boolean prepareAndSenEMail(String uuid, String version, String type) {
		logger.info("inside prepareAndSenEMail method.");
		try {
			Object object = (Report) commonServiceImpl.getOneByUuidAndVersion(uuid, version, type, "N");
			
			if(object != null) {				
				Notification notification = (Notification) object.getClass().getMethod("getNotification").invoke(object);
				if(notification != null) {
					notification.setFrom(Helper.getPropertyValue("framework.email.from"));
					notification.setPassword(Helper.getPropertyValue("frameowrk.email.password"));
					notification.setHost(Helper.getPropertyValue("framework.email.host"));
					notification.setPort(Helper.getPropertyValue("framework.email.port"));
					notification.setSubect(Helper.getPropertyValue("framework.email.subject"));
					notification.setMessage(Helper.getPropertyValue("framework.email.message"));
					return sendEMail(notification);
				} else {
					throw new RuntimeException("No credentials avilable to send mail.");
				}
			} else {
				throw new RuntimeException("No credentials avilable to send mail.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
