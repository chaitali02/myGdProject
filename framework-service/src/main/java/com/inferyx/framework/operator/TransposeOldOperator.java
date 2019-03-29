/**
 * 
 */
package com.inferyx.framework.operator;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.inferyx.framework.common.ConstantsUtil;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.Datapod;

/**
 * @author joy
 *
 */
@Service
public class TransposeOldOperator {
	static final Logger LOGGER = Logger.getLogger(TransposeOldOperator.class);
	/**
	 * 
	 */
	public TransposeOldOperator() {
		// TODO Auto-generated constructor stub
	}
	

	
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
