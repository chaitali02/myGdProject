package com.inferyx.framework.executor;

public enum ExecContext {

	spark, HIVE, IMPALA, ORACLE, MYSQL, FILE, livy_spark, R, PYTHON;
}
