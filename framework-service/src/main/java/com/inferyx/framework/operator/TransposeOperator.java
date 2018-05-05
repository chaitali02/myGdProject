/**
 * 
 */
package com.inferyx.framework.operator;

import java.util.List;

import org.apache.log4j.Logger;

import com.inferyx.framework.common.ConstantsUtil;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.Datapod;

/**
 * @author joy
 *
 */
public class TransposeOperator {
	static final Logger LOGGER = Logger.getLogger(TransposeOperator.class);
	/**
	 * 
	 */
	public TransposeOperator() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 * @param dp
	 * @return
	 *//*
	public String generateCollectSql(Datapod dp) {
		List<Attribute> srcAttrs = dp.getAttributes();
		StringBuilder sb = new StringBuilder(ConstantsUtil.SELECT);
		// Pick up the pivot
		Attribute pivotAttr = srcAttrs.get(0);
		sb = sb.append(srcAttrs.get(0).getName())
			   .append(ConstantsUtil.COMMA);
		// Collect set for others
		for (int i = 1; i < srcAttrs.size(); i++) {
			sb = sb.append("collect_set(")
			.append(srcAttrs.get(i).getName())
			.append(")").append(ConstantsUtil.AS)
			.append(srcAttrs.get(i).getName());
			if (i+1 != srcAttrs.size()) {
				sb = sb.append(ConstantsUtil.COMMA);
			}
		}
		sb = sb.append(ConstantsUtil.FROM);
		sb = sb.append(dp.getName());
		return sb.toString();		
	}*/
	
	/**
	 * 
	 * @param dp
	 * @param attrindex
	 * @return
	 *//*
	public String generateAttrSql(Datapod dp, int attrindex) {
		StringBuilder sb = new StringBuilder(ConstantsUtil.SELECT);
		List<Attribute> srcAttrs = dp.getAttributes();
		// Pick up the pivot
		Attribute pivotAttr = srcAttrs.get(0);
		sb = sb.append(srcAttrs.get(0).getName())
			   .append(ConstantsUtil.COMMA);
		sb = sb.append(srcAttrs.get(attrindex).getName())
				.append(ConstantsUtil.AS)
				.append(" trial ");
		sb = sb.append(ConstantsUtil.FROM);
		sb = sb.append("(")
			   .append(generateCollectSql(dp))
			   .append(")")
			   .append(ConstantsUtil.AS)
			   .append(" trialDp ");
		sb = sb.append(" LATERAL VIEW explode(")
				.append(srcAttrs.get(attrindex).getName())
				.append(")")
				.append(ConstantsUtil.AS)
				.append(srcAttrs.get(attrindex).getName());
		return sb.toString();		
	}*/
	
	/**
	 * 
	 * @param dp
	 * @return
	 *//*
	public String generateSql(Datapod dp) {
		List<Attribute> srcAttrs = dp.getAttributes();
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < srcAttrs.size(); i++) {
			sb = sb.append(generateAttrSql(dp, i));
			if (i+1 != srcAttrs.size()) {
				sb = sb.append(ConstantsUtil.UNION_ALL);
			}
		}
		LOGGER.info("query: "+sb.toString());
		return sb.toString();
	}*/
	
	/**
	 * 
	 * @param dp
	 * @return
	 */
	public String generateSql(Datapod dp, String tableName) {
		String query = generateAttrSql(dp, tableName);
		LOGGER.info("query: "+query);
		return query;
	}
	
	/**
	 * 
	 * @param dp
	 * @param attrindex
	 * @return
	 */
	public String generateAttrSql(Datapod dp, String tableName) {
		StringBuilder sb = new StringBuilder(ConstantsUtil.SELECT);
		List<Attribute> srcAttrs = dp.getAttributes();
		// Pick up the pivot
		Attribute pivotAttr = srcAttrs.get(0);
		sb = sb.append(pivotAttr.getName())
			   .append(ConstantsUtil.COMMA)
			   .append("key")
			   .append(ConstantsUtil.COMMA)
			   .append("value");		
		sb = sb.append(ConstantsUtil.FROM);		
		sb = sb.append("(")
			   .append(generateMapQuery(dp, tableName))
			   .append(")")
			   .append(" tab_transpose ");
		sb = sb.append(" LATERAL VIEW explode(")
		       .append("trialDp")
			   .append(")")
			   .append(" explode_tab ")
			   .append(ConstantsUtil.AS)
			   .append("key")
			   .append(ConstantsUtil.COMMA)
			   .append("value");
		return sb.toString();		
	}
	
	public String generateMapQuery(Datapod datapod, String tableName) {
		List<Attribute> srcAttrs = datapod.getAttributes();
		StringBuilder sb = new StringBuilder(ConstantsUtil.SELECT);
		// Pick up the pivot
		Attribute pivotAttr = srcAttrs.get(0);
		sb = sb.append(pivotAttr.getName())
			   .append(ConstantsUtil.COMMA);
		sb = sb.append("MAP")
			   .append("(");
		for (int i = 1; i < srcAttrs.size(); i++) {
			sb = sb.append("'")
					.append(srcAttrs.get(i).getName())
					.append("'");
			sb = sb.append(",");
			sb = sb.append(srcAttrs.get(i).getName());
			if(i < srcAttrs.size()-1)
				sb = sb.append(",");
		}
		sb = sb.append(")")
			   .append(ConstantsUtil.AS)
			   .append(" trialDp ")
			   .append(ConstantsUtil.FROM)
			   .append(" ")
			   .append(tableName);
		
		return sb.toString();		
	}
}
