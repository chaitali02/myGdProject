package com.inferyx.framework.writer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.springframework.data.domain.Sort;

import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;

public class OracleWriter implements IWriter {
	Logger logger = Logger.getLogger(OracleWriter.class);
	private MetadataUtil daoRegister;

	public MetadataUtil getDaoRegister() {
		return daoRegister;
	}

	public void setDaoRegister(MetadataUtil daoRegister) {
		this.daoRegister = daoRegister;
	}

	@Override
	public void write(Dataset<Row> dataFrame, String filePathUrl, Datapod datapod, String saveMode) {
		try {
			Datasource dataSource = daoRegister.getiDatasourceDao().findLatestByUuid(
					datapod.getDatasource().getRef().getUuid(), new Sort(Sort.Direction.DESC, "version"));
			logger.info("Table Name: " + dataSource.getDbname().concat(".").concat(datapod.getName()));
			Properties prop = new Properties();
			prop.put("user", dataSource.getUsername());
			prop.put("password", dataSource.getPassword());
			List<Attribute> attrList = datapod.getAttributes();
			List<String> parList = new ArrayList<String>();
			for (Attribute attr : attrList)
				if (attr.getPartition().equalsIgnoreCase("y"))
					parList.add(attr.getName());
			if (parList.size() > 0) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < parList.size(); i++)
					sb.append(parList.get(i)).append(",");
				String[] columnList = sb.substring(0, sb.length() - 1).toString().split(",");
				logger.info("Partition column list >----->> " + Arrays.asList(columnList));

				dataFrame.write().mode(SaveMode.Append).partitionBy(columnList).option("driver", dataSource.getDriver())
						.jdbc("jdbc:oracle:thin:" + dataSource.getUsername() + "/" + dataSource.getPassword() + "@//"
								+ dataSource.getHost() + ":" + dataSource.getPort() + "/" + dataSource.getSid(),
								dataSource.getDbname().concat(".").concat(datapod.getName()), prop);

			} else {
				if (saveMode.equalsIgnoreCase("append"))
					dataFrame.write().mode(SaveMode.Append).option("driver", dataSource.getDriver()).jdbc(
							"jdbc:oracle:thin:" + dataSource.getUsername() + "/" + dataSource.getPassword() + "@//"
									+ dataSource.getHost() + ":" + dataSource.getPort() + "/" + dataSource.getSid(),
							dataSource.getDbname().concat(".").concat(datapod.getName()), prop);
				else if (saveMode.equalsIgnoreCase("overwrite"))
					dataFrame.write().mode(SaveMode.Overwrite).option("driver", dataSource.getDriver()).jdbc(
							"jdbc:oracle:thin:" + dataSource.getUsername() + "/" + dataSource.getPassword() + "@//"
									+ dataSource.getHost() + ":" + dataSource.getPort() + "/" + dataSource.getSid(),
							dataSource.getDbname().concat(".").concat(datapod.getName()), prop);
			}
		} /*
			 * catch(FileNotFoundException e){ e.printStackTrace(); }
			 */catch (Exception e) {
			e.printStackTrace();
		}
	}
}
