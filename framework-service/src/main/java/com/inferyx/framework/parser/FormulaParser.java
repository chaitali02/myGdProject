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
   
