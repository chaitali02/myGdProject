SELECT TABNAME,CNT FROM
(
SELECT 'CUSTOMER_ES_ALLOCATION' AS TABNAME, COUNT(*) AS CNT FROM CUSTOMER_ES_ALLOCATION
UNION
SELECT 'CUSTOMER_GENERATE_DATA' AS TABNAME, COUNT(*) AS CNT FROM CUSTOMER_GENERATE_DATA
UNION
SELECT 'CUSTOMER_IDIOSYNCRATIC_RISK' AS TABNAME, COUNT(*) AS CNT FROM CUSTOMER_IDIOSYNCRATIC_RISK
UNION
SELECT 'CUSTOMER_IDIOSYNCRATIC_TRANSPOSE_STAGE' AS TABNAME, COUNT(*) AS CNT FROM CUSTOMER_IDIOSYNCRATIC_TRANSPOSE_STAGE
UNION
SELECT 'CUSTOMER_IDIOSYNCRATIC_TRANSPOSE' AS TABNAME, COUNT(*) AS CNT FROM CUSTOMER_IDIOSYNCRATIC_TRANSPOSE
UNION
SELECT 'CUSTOMER_LOSS_SIMULATION' AS TABNAME, COUNT(*) AS CNT FROM CUSTOMER_LOSS_SIMULATION
UNION
SELECT 'CUSTOMER_PORTFOLIO_CLONE' AS TABNAME, COUNT(*) AS CNT FROM CUSTOMER_PORTFOLIO_CLONE
UNION
SELECT 'CUSTOMER_PORTFOLIO_UL_CALC_ALLOCATION' AS TABNAME, COUNT(*) AS CNT FROM CUSTOMER_PORTFOLIO_UL_CALC_ALLOCATION
UNION
SELECT 'CUSTOMER_PORTFOLIO_UL_CALC_FINAL_CUST_SUMMARY' AS TABNAME, COUNT(*) AS CNT FROM CUSTOMER_PORTFOLIO_UL_CALC_FINAL_CUST_SUMMARY
UNION
SELECT 'CUSTOMER_PORTFOLIO_UL_CALC_SUMMARY' AS TABNAME, COUNT(*) AS CNT FROM CUSTOMER_PORTFOLIO_UL_CALC_SUMMARY
UNION
SELECT 'CUSTOMER_PORTFOLIO_UL_CALC' AS TABNAME, COUNT(*) AS CNT FROM CUSTOMER_PORTFOLIO_UL_CALC
UNION
SELECT 'CUSTOMER_PORTFOLIO_UL' AS TABNAME, COUNT(*) AS CNT FROM CUSTOMER_PORTFOLIO_UL
UNION
SELECT 'CUSTOMER_PORTFOLIO' AS TABNAME, COUNT(*) AS CNT FROM CUSTOMER_PORTFOLIO
UNION
SELECT 'CUSTOMER_VAR_CONTRIBUTION_TOPN_PERC' AS TABNAME, COUNT(*) AS CNT FROM CUSTOMER_VAR_CONTRIBUTION_TOPN_PERC
UNION
SELECT 'INDUSTRY_FACTOR_CORRELATION_ADHOC' AS TABNAME, COUNT(*) AS CNT FROM INDUSTRY_FACTOR_CORRELATION_ADHOC
UNION
SELECT 'INDUSTRY_FACTOR_CORRELATION_TRANSPOSE' AS TABNAME, COUNT(*) AS CNT FROM INDUSTRY_FACTOR_CORRELATION_TRANSPOSE
UNION
SELECT 'INDUSTRY_FACTOR_CORRELATION' AS TABNAME, COUNT(*) AS CNT FROM INDUSTRY_FACTOR_CORRELATION
UNION
SELECT 'INDUSTRY_FACTOR_MEAN_ADHOC' AS TABNAME, COUNT(*) AS CNT FROM INDUSTRY_FACTOR_MEAN_ADHOC
UNION
SELECT 'INDUSTRY_FACTOR_MEAN' AS TABNAME, COUNT(*) AS CNT FROM INDUSTRY_FACTOR_MEAN
UNION
SELECT 'INDUSTRY_FACTOR_SIMULATION_ADHOC' AS TABNAME, COUNT(*) AS CNT FROM INDUSTRY_FACTOR_SIMULATION_ADHOC
UNION
SELECT 'INDUSTRY_FACTOR_SIMULATION_STAGE' AS TABNAME, COUNT(*) AS CNT FROM INDUSTRY_FACTOR_SIMULATION_STAGE
UNION
SELECT 'INDUSTRY_FACTOR_SIMULATION' AS TABNAME, COUNT(*) AS CNT FROM INDUSTRY_FACTOR_SIMULATION
UNION
SELECT 'INDUSTRY_FACTOR_TRANSPOSE' AS TABNAME, COUNT(*) AS CNT FROM INDUSTRY_FACTOR_TRANSPOSE
UNION
SELECT 'LKP_REPORTING_DATE' AS TABNAME, COUNT(*) AS CNT FROM LKP_REPORTING_DATE
UNION
SELECT 'PORTFOLIO_EXPECTED_SUM' AS TABNAME, COUNT(*) AS CNT FROM PORTFOLIO_EXPECTED_SUM
UNION
SELECT 'PORTFOLIO_LOSS_AGGR_ES' AS TABNAME, COUNT(*) AS CNT FROM PORTFOLIO_LOSS_AGGR_ES
UNION
SELECT 'PORTFOLIO_LOSS_AGGR_ES' AS TABNAME, COUNT(*) AS CNT FROM PORTFOLIO_LOSS_AGGR_ES
UNION
SELECT 'PORTFOLIO_LOSS_AGGR_ES' AS TABNAME, COUNT(*) AS CNT FROM PORTFOLIO_LOSS_AGGR_ES
UNION
SELECT 'PORTFOLIO_LOSS_AGGR' AS TABNAME, COUNT(*) AS CNT FROM PORTFOLIO_LOSS_AGGR
UNION
SELECT 'PORTFOLIO_LOSS_HISTOGRAM_PERCENTAGE' AS TABNAME, COUNT(*) AS CNT FROM PORTFOLIO_LOSS_HISTOGRAM_PERCENTAGE
UNION
SELECT 'PORTFOLIO_LOSS_HISTOGRAM' AS TABNAME, COUNT(*) AS CNT FROM PORTFOLIO_LOSS_HISTOGRAM
UNION
SELECT 'PORTFOLIO_LOSS_SIMULATION_AGGR' AS TABNAME, COUNT(*) AS CNT FROM PORTFOLIO_LOSS_SIMULATION_AGGR
UNION
SELECT 'PORTFOLIO_LOSS_SIMULATION_EL' AS TABNAME, COUNT(*) AS CNT FROM PORTFOLIO_LOSS_SIMULATION_EL
UNION
SELECT 'PORTFOLIO_LOSS_SIMULATION' AS TABNAME, COUNT(*) AS CNT FROM PORTFOLIO_LOSS_SIMULATION
UNION
SELECT 'PORTFOLIO_LOSS_SIMULATON_AGGR' AS TABNAME, COUNT(*) AS CNT FROM PORTFOLIO_LOSS_SIMULATON_AGGR
UNION
SELECT 'PORTFOLIO_VAR_HEATMAP_BUCKETS' AS TABNAME, COUNT(*) AS CNT FROM PORTFOLIO_VAR_HEATMAP_BUCKETS
) T1
ORDER BY TABNAME; 















