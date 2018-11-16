tbl <- read.table("/user/hive/warehouse/framework/upload/account.csv",header=TRUE,sep=",")
population <- tbl["current_balance"]
print(summary(population[-1:-5,]))