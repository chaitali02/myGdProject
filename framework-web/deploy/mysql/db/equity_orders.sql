DROP TABLE IF EXISTS equity_orders; 

CREATE TABLE equity_orders
  ( 
     order_identifier                        INT(10), 
     order_event_number                      INT(2), 
     order_placement_date                    VARCHAR(50), 
     parent_order_id                         INT(10), 
     parent_order_placement_date             VARCHAR(50), 
     root_order_identifier                   VARCHAR(50), 
     root_order_placement_date               VARCHAR(50), 
     order_allocation_relationship_code      VARCHAR(50), 
     allocated_order_id                      INT(10), 
     allocated_order_placement_date          VARCHAR(50), 
     order_event_type_cd                     VARCHAR(50), 
     order_event_date                        VARCHAR(50), 
     order_event_time                        VARCHAR(50), 
     firm_identifier                         VARCHAR(50), 
     originator_identifier                   VARCHAR(50), 
     originator_system                       VARCHAR(50), 
     originating_organization_identifier     VARCHAR(50), 
     submission_channel                      VARCHAR(50), 
     automated_channel                       VARCHAR(50), 
     solicited_indicator                     VARCHAR(50), 
     order_management_date                   VARCHAR(50), 
     order_management_time                   VARCHAR(50), 
     source_market_participant_identifier    VARCHAR(50), 
     receiving_desk_identifier               VARCHAR(50), 
     order_received_date                     VARCHAR(50), 
     order_received_time                     VARCHAR(50), 
     buyer_or_seller_identifier              VARCHAR(50), 
     buyer_or_seller_account_identifier      VARCHAR(50), 
     buyer_or_seller_type                    VARCHAR(50), 
     buyer_or_seller_organization_identifier VARCHAR(50), 
     buy_or_sell_code                        VARCHAR(50), 
     seller_position_code                    VARCHAR(50), 
     customer_accounting_rule                VARCHAR(50), 
     customer_position                       VARCHAR(50), 
     security_identifier                     VARCHAR(50), 
     security_short_name                     VARCHAR(50), 
     isin_identifier                         VARCHAR(50), 
     product_category                        VARCHAR(50), 
     product_type                            VARCHAR(50), 
     product_subtype                         VARCHAR(50), 
     structured_deal                         VARCHAR(50), 
     order_type_code                         VARCHAR(50), 
     limit_price_issuing                     DOUBLE, 
     limit_price_base                        DOUBLE, 
     stop_price_issuing                      DOUBLE, 
     stop_price_base                         DOUBLE, 
     unit_quantity                           DOUBLE, 
     time_in_force                           VARCHAR(50), 
     expiration_date                         VARCHAR(50), 
     expiration_time                         VARCHAR(50), 
     held_indicator                          VARCHAR(50), 
     do_not_display                          VARCHAR(50), 
     order_handling_code1                    VARCHAR(50), 
     order_handling_code2                    VARCHAR(50), 
     order_handling_code3                    VARCHAR(50), 
     order_handling_code4                    VARCHAR(50), 
     order_handling_code5                    VARCHAR(50), 
     dnr_dni_instructions                    VARCHAR(50), 
     trade_along_side_consent_code           VARCHAR(50), 
     trade_along_side_ratio_code             VARCHAR(50), 
     detailed_instructions                   VARCHAR(50), 
     ipo_indicator                           VARCHAR(50), 
     new_issue_indicator                     VARCHAR(50), 
     issuer_repurchase                       VARCHAR(50), 
     program_trading                         VARCHAR(50), 
     arbitrage_indicator                     VARCHAR(50), 
     destination_market                      VARCHAR(50), 
     routed_unit_quantity                    DOUBLE, 
     bunched_order                           VARCHAR(50), 
     canceling_party_role                    VARCHAR(50), 
     cancellation_reason                     VARCHAR(50), 
     replaced_order_identifier               VARCHAR(50), 
     replaced_order_placement_date           VARCHAR(50), 
     business_domain                         VARCHAR(50), 
     jurisdiction                            VARCHAR(50), 
     source_system                           VARCHAR(50),
	 PRIMARY KEY(order_identifier,order_event_number)     
  );