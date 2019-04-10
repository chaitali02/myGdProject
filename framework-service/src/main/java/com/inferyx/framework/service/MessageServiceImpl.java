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

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.dao.IMessageDao;
import com.inferyx.framework.domain.Message;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.register.GraphRegister;

@Service
public class MessageServiceImpl {
	@Autowired
	IMessageDao iMessageDao;
	@Autowired
	GraphRegister<?> registerGraph;

	public Message save(Message message) throws JsonProcessingException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		message.setBaseEntity();		
		Message savedMessage = (Message) iMessageDao.save(message);
		registerGraph.updateGraph((Object) savedMessage, MetaType.message);
		return savedMessage;
	}
}
