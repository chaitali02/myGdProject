package com.inferyx.framework.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.math.NumberUtils;

public class TestPatternMatching {
	public static void main(String[] args) {
		String test = "portfolio_loss_summary.portfolio_avg_lgd = 0.5"+" portfolio_loss_summary.portfolio_avg_lgd = 0.5";
		Pattern pattern = Pattern.compile("(\\b(\\w+)\\.)(?=([^\"']*[\"'][^\"']*[\"'])*[^\"']*$)");
		Matcher matcher = pattern.matcher(test);

		while(matcher.find()) {
			System.out.print("Start index: " + matcher.start());
	        System.out.print(" End index: " + matcher.end());
	        System.out.println(" Found: " + matcher.group());
	        System.out.print("Is creatable: "+NumberUtils.isCreatable(matcher.group()));
	        System.out.println(" Is digits: "+NumberUtils.isDigits(matcher.group()));
	        if(!NumberUtils.isCreatable(matcher.group()))
	        	System.out.println(test.replace(matcher.group(), ""));
	        System.out.println();
		}
	}
}
