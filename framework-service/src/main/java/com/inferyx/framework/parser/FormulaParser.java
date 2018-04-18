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
package com.inferyx.framework.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.SourceAttr;
import com.inferyx.framework.service.DatapodServiceImpl;
import com.inferyx.framework.service.FormulaServiceImpl;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;


public class FormulaParser {
	
	Logger logger=Logger.getLogger(FormulaParser.class);
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	FormulaServiceImpl formulaServiceImpl;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	
    private Node root;
 
    public FormulaParser() {
        root = null;
    }

	/********************** UNUSED **********************/
   	/*public DBObject ConvertFormula(Formula formula) throws IOException, ParseException  {
    	String formulaUuid = formula.getUuid();
    	Formula formulaLatest = formulaServiceImpl.findLatestByUuid(formulaUuid);
    	List<SourceAttr> formulaInfo = formulaLatest.getFormulaInfo();
    	StringBuilder formulaSB = new StringBuilder();
    	String[] alphabet = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"}; 
    	List<String> formulaList = new ArrayList<>();  
    	String form;
    	int  j=0;
    	for(int i=0; i<formulaInfo.size(); i++)	{
    		char ch,ch1;
    		if(formulaInfo.get(i).getRef().getType() == MetaType.simple) {
    				if(formulaInfo.get(i).getValue().contains("(") || formulaInfo.get(i).getValue().contains(")") || formulaInfo.get(i).getValue().contains("+") || formulaInfo.get(i).getValue().contains("-") || formulaInfo.get(i).getValue().contains("*") || formulaInfo.get(i).getValue().contains("/")) {
    					String s=formulaInfo.get(i).getValue();
    					formulaList.add(s);
    				} else {
    					String s=formulaInfo.get(i).getValue();
    					formulaList.add(s);						
					}
    		}
    		if(formulaInfo.get(i).getRef().getType() == MetaType.datapod) {
	    		String datapodUuid =  formulaInfo.get(i).getRef().getUuid();
	    		Datapod datapodDO = datapodServiceImpl.findLatestByUuid(datapodUuid);
	    		String datapodName = datapodDO.getName();
	    		Integer attrId = formulaInfo.get(i).getAttributeId();
	    		List<Attribute> attrList = datapodDO.getAttributes();
	    		for(Attribute a: attrList) {
	    			if(a.getAttributeId() == attrId) {
	    			String attrName = a.getName(); 
	    			formulaList.add(datapodUuid+"."+attrId);
	    			}
	    		}    		
    		}     		
    	}    	
    	List<String> finalList = new ArrayList<>();
    	for(int i=0; i<formulaList.size(); i++) {*/
//    		if(!(formulaList.get(i).matches("[()*/+-]"))) {
//    			finalList.add(formulaList.get(i).replace(formulaList.get(i), alphabet[j]));   
//    			j++;    			
//    		} else {
//        		finalList.add(formulaList.get(i));
//        	}
    	/*}    	
    	for(String s : finalList) {
    		formulaSB.append(s);    	   
    	}
    	 String[] stringToken = formulaList.toString().split("[\\[\\]\\,\\s()]");        	 
    	 List<String> stringList = new ArrayList<>(); 
    	 for(String sT : stringToken)  {
    		 if(sT != null && sT.length()>0)  {
    			 stringList.add(sT);
    		 }
    	 }          
    	
    	form = formulaSB.toString(); 	    	
        Conversion c = new Conversion(form);       
        String finalStr = form.replaceAll("[()]", "");
        char[] finalChar = finalStr.toString().toCharArray();
        String s = c.inToPost();       
        Stack1 stk = new Stack1(s.length());
        s = s + "#";
        int i = 0;        
        char[] symbol = s.toCharArray();              
         Node newNode;   
         for(char ch : symbol)  {        	 
        	 MetaIdentifierHolder finalNode = null; 
        	 MetaIdentifier nodeMeta =null;                 	             	         	             	
            		 if (ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z' ) {	
            			 int k=0;
            			 while(finalChar[k] != ch)  {
            				 k++;
            			 }            				            				 
        				 if(stringList.get(k).length() >= 38)  {
	        				logger.info("datapod: "+stringList.get(k));
		        			newNode = new Node(stringList.get(k));  
		        			stk.push(newNode);
		        			k++;
        				 }else if(stringList.get(k) != "/" || stringList.get(k) != "*" || stringList.get(k) != "+" || stringList.get(k) != "-") { 
            					logger.info("value: "+stringList.get(k));
                         		 newNode = new Node(stringList.get(k));
                         		stk.push(newNode);
                         		k++;
            				 } 
                     } else if (ch == '+' || ch == '-' || ch == '/'
                                 || ch == '*')  {  
            				 int k=0;
            				 while(finalChar[k] != ch) {
                				 k++;
                			 }                        
                             Node ptr1 = stk.pop();               
                             Node ptr2 = stk.pop();      
                            logger.info("simple: "+stringList.get(k));
                             newNode = new Node(stringList.get(k));             
                             newNode.leftChild = ptr2;                
                             newNode.rightChild = ptr1;                                 
                             stk.push(newNode);
                             k++;             				
                         }            			
            	 }            	 

        root = stk.pop();     
        JSONObject js=new JSONObject();
        js.put("value",root);   
    	JsonParser crunhifyParser = new JsonParser();
		JsonObject json = crunhifyParser.parse(js.toJSONString()).getAsJsonObject(); 
		Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
		String prettyJson = prettyGson.toJson(json);    		
		DBObject obj=getData(prettyJson);			
		return obj;               	
    }  */
 
    public void traverse(int type)
    {
        switch (type)
        {
            case 1:
              //  System.out.print("Preorder Traversal:-    ");
                preOrder(root);
                break; 
            default:
               // System.out.println("Invalid Choice");
        }
    }
 
    private void preOrder(Node localRoot)
    {
        if (localRoot != null)
        {
            localRoot.displayNode();
            preOrder(localRoot.leftChild);
            preOrder(localRoot.rightChild);
        }
    }    
    
    public DBObject getData(String formula)
	{      
        
    	DBObject formulaNew = (DBObject)JSON.parse(formula);	    
        mongoTemplate.getCollection("formulanew").insert(formulaNew);
  		return (DBObject) formulaNew;
	}
	
}

class Node<E>
{

	Logger logger=Logger.getLogger(Node.class);
    public String data;
    public E value;
    public Node leftChild;
    public Node rightChild;

    @JsonCreator
    public Node(@JsonProperty("Formula") final E value) {
        this.value = value;
    }
   
 
    public Node(String x) throws IOException
    {	    	
        data = x;
    }
 
    public void displayNode()
    {
        logger.info(data);
    }


	@Override
	public String toString() {
		return "{\"Value\":" +"\""+data+"\""+",\"Left\":"+leftChild + ",\"Right\":" +rightChild + "}";
	}

}
   
 
class Stack1
{
    private Node[] a;
    private int    top, m;
 
    public Stack1(int max)
    {
        m = max;
        a = new Node[m];
        top = -1;
    }
 
    public void push(Node key)
    {
        a[++top] = key;
    }
 
    public Node pop()
    {
        return (a[top--]);
    }
 
    public boolean isEmpty()
    {
        return (top == -1);
    }
}
 
class Stack2
{
    private char[] a;
    private int    top, m;
 
    public Stack2(int max)
    {
        m = max;
        a = new char[m];
        top = -1;
    }
 
    public void push(char key)
    {
        a[++top] = key;
    }
 
    public char pop()
    {
        return (a[top--]);
    }
 
    public boolean isEmpty()
    {
        return (top == -1);
    }
}
 
class Conversion
{
    private Stack2 s;
    private String input;
    private String output = "";
 
    public Conversion(String str)
    {
        input = str;
        s = new Stack2(str.length());
    }
 
    public String inToPost()
    {
        for (int i = 0; i < input.length(); i++)
        {
            char ch = input.charAt(i);
            switch (ch)
            {
                case '+':
                case '-':
                    gotOperator(ch, 1);
                    break;
                case '*':
                case '/':
                    gotOperator(ch, 2);
                    break;
                case '(':
                    s.push(ch);
                    break;
                    
                case ')':
                    gotParenthesis();
                    break;
                default:
                    output = output + ch;
            }
        }
        while (!s.isEmpty())
            output = output + s.pop();
        return output;
    }
 
    private void gotOperator(char opThis, int prec1)
    {
        while (!s.isEmpty())
        {
            char opTop = s.pop();
            if (opTop == '(')
            {
                s.push(opTop);
                break;
            } else
            {
                int prec2;
                if (opTop == '+' || opTop == '-')
                    prec2 = 1;
                else
                    prec2 = 2;
                if (prec2 < prec1)
                {
                    s.push(opTop);
                    break;
                } else
                    output = output + opTop;
            }
        }
        s.push(opThis);
    }
 
    private void gotParenthesis()
    {
        while (!s.isEmpty())
        {
            char ch = s.pop();
            if (ch == '(')
                break;
            else
                output = output + ch;
        }
    }

}
   
