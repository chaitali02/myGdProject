package com.inferyx.framework.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.dao.IMessageDao;
import com.inferyx.framework.domain.Message;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.register.GraphRegister;

public class MessageServiceImpl {
	@Autowired
	IMessageDao iMessageDao;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	GraphRegister<?> registerGraph;

	public Message save(Message message) throws JsonProcessingException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		message.setBaseEntity();		
		Message savedMessage = (Message) iMessageDao.save(message);
		registerGraph.updateGraph((Object) savedMessage, MetaType.message);
		return savedMessage;
	}
}
