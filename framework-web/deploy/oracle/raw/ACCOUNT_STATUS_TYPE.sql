 CREATE TABLE "XE"."ACCOUNT_STATUS_TYPE" 
   (	"ACCOUNT_STATUS_ID" NUMBER(30,0) DEFAULT 0 NOT NULL ENABLE, 
	"ACCOUNT_STATUS_CODE" VARCHAR2(70 BYTE), 
	"ACCOUNT_STATUS_DESC" VARCHAR2(70 BYTE), 
	"LOAD_DATE" VARCHAR2(70 BYTE) NOT NULL ENABLE, 
	"VERSION" VARCHAR2(70 BYTE), 
	"LOAD_ID" NUMBER(30,0) DEFAULT 0, 
	 CONSTRAINT "ACCOUNT_STATUS_TYPE_PK" PRIMARY KEY ("ACCOUNT_STATUS_ID", "LOAD_DATE")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE
   ) PARTITION BY RANGE (LOAD_DATE,LOAD_ID) 
 (PARTITION PARTITION_NAME  VALUES LESS THAN (MAXVALUE, MAXVALUE) )


exit

su oracle

sqlldr xe/admin /opt/oracle/app/controlLoadFile/ACCOUNT_STATUS_TYPE.ctl
