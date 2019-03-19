DROP TABLE IF EXISTS equity_executions_new; 

CREATE TABLE equity_executions_new 
  ( 
     trade_execution_identifier              VARCHAR(50), 
     trade_execution_event_number            VARCHAR(50), 
     trade_execution_event_type_code         VARCHAR(50), 
     trade_execution_date                    VARCHAR(50), 
     trade_execution_time                    VARCHAR(50), 
     trade_execution_event_date              VARCHAR(50), 
     trade_execution_event_time              VARCHAR(50), 
     trade_allocation_relationship_code      VARCHAR(50), 
     allocated_trade_execution_id            VARCHAR(50), 
     allocated_trade_execution_date          VARCHAR(50), 
     trade_purpose                           VARCHAR(50), 
     firm_identifier                         VARCHAR(50), 
     originator_identifier                   VARCHAR(50), 
     originator_system                       VARCHAR(50), 
     originating_organization_identifier     VARCHAR(50), 
     submission_channel                      VARCHAR(50), 
     automated_channel                       VARCHAR(50), 
     solicited_indicator                     VARCHAR(50), 
     system_entry_date                       VARCHAR(50), 
     system_entry_time                       VARCHAR(50), 
     trade_approval_indicator                VARCHAR(50), 
     buyer_identifier                        VARCHAR(50), 
     buyer_account_identifier                VARCHAR(50), 
     buyer_type_code                         VARCHAR(50), 
     buyer_organization                      VARCHAR(50), 
     seller_identifier                       VARCHAR(50), 
     seller_account                          VARCHAR(50), 
     seller_type_code                        VARCHAR(50), 
     seller_organization                     VARCHAR(50), 
     seller_position_code                    VARCHAR(50), 
     customer_accounting                     VARCHAR(50), 
     customer_position                       VARCHAR(50), 
     security_identifier                     VARCHAR(50), 
     security_short_name                     VARCHAR(50), 
     isin_identifier                         VARCHAR(50), 
     product_category                        VARCHAR(50), 
     product_type                            VARCHAR(50), 
     product_subtype                         VARCHAR(50), 
     market_identifier                       VARCHAR(50), 
     unit_quantity                           DOUBLE, 
     price_issuing                           DOUBLE, 
     price_base                              DOUBLE, 
     trade_execution_currency                VARCHAR(50), 
     principal_amount_issuing                DOUBLE, 
     principal_amount_base                   DOUBLE, 
     yield_percentage                        DOUBLE, 
     yield_method_code                       VARCHAR(50), 
     agent_identifier                        VARCHAR(50), 
     executing_desk_identifier               VARCHAR(50), 
     executing_sub_desk_identifier           VARCHAR(50), 
     executing_desk_country_code             VARCHAR(50), 
     trading_account                         VARCHAR(50), 
     trader_position                         VARCHAR(50), 
     trader_position_code                    VARCHAR(50), 
     commission_amount_issuing               DOUBLE, 
     commission_amount_base                  DOUBLE, 
     mutual_fund_load_fee_issuing            VARCHAR(50), 
     mutual_fund_load_fee_base               VARCHAR(50), 
     riskless_principal_indicator            VARCHAR(50), 
     riskless_principal_reference_identifier VARCHAR(50), 
     settlement_date                         VARCHAR(50), 
     nonstandard_settlement_indicator        VARCHAR(50), 
     settlement_type                         VARCHAR(50), 
     settlement_currency                     VARCHAR(50), 
     settlement_instruction                  VARCHAR(50), 
     order_identifier                        VARCHAR(50), 
     order_placement_date                    VARCHAR(50), 
     order_execution_sequence_number         VARCHAR(50), 
     source_system_order_remaining_unit_qty  VARCHAR(50), 
     structured_deal                         VARCHAR(50), 
     ipo_indicator                           VARCHAR(50), 
     new_issue_indicator                     VARCHAR(50), 
     replaced_trade_execution_identifier     VARCHAR(50), 
     replaced_trade_execution_date           VARCHAR(50), 
     cancellation_or_correction_reason       VARCHAR(50), 
     business_domain                         VARCHAR(50), 
     jurisdiction                            VARCHAR(50), 
     source_system                           VARCHAR(50) 
  ); 
