TRUNCATE TABLE FACT_TRANSACTION_JOURNAL;

Copy FACT_TRANSACTION_JOURNAL FROM '/user/hive/warehouse/framework/app/aml/data/csv/noheader/fact_transaction_journal.csv' With csv delimiter ',';

