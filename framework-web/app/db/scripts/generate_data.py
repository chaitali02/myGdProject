
# coding: utf-8

# In[113]:


import pandas as pd

df_orig = pd.read_csv('/home/inferyx/git/inferyx/framework-web/app/db/data/csv/header/equity_orders_orig.csv')


# In[134]:


iterRange = 25000
writeSize=2500
df_final = pd.DataFrame() 
out_file='/home/inferyx/git/inferyx/framework-web/app/db/data/csv/header/equity_orders.csv'
i_new = 0
for i in range(1,iterRange+1):
    i_new += 1
    df_tmp = pd.DataFrame()
    df_tmp = df_tmp.append(df_orig)
    df_tmp.order_identifier = df_tmp.order_identifier+i
    df_tmp.parent_order_id = df_tmp.parent_order_id+i
    df_final = df_final.append(df_tmp)   
    if i % writeSize == 0:
        print(df_final.shape)
        print('Saving data ' + str(i))
        if i == writeSize:
            print('Creating new file')
            df_final.to_csv(out_file,index=False,mode = 'a')
        else:
            print('Appending to file')
            df_final.to_csv(out_file,index=False,mode = 'a',header=False)
        df_final.drop(df_final.index, inplace=True)


# In[ ]:


import pandas as pd

df_orig = pd.read_csv('/home/inferyx/git/inferyx/framework-web/app/db/data/csv/header/equity_executions_orig.csv')


# In[ ]:


iterRange = 25000
writeSize=2500
df_final = pd.DataFrame() 
out_file='/home/inferyx/git/inferyx/framework-web/app/db/data/csv/header/equity_executions.csv'
i_new = 0
for i in range(1,iterRange+1):
    i_new += 1
    df_tmp = pd.DataFrame()
    df_tmp = df_tmp.append(df_orig)
    df_tmp.order_identifier = df_tmp.order_identifier+i
    df_tmp.trade_execution_identifier = df_tmp.trade_execution_identifier+i
    df_final = df_final.append(df_tmp)   
    if i % writeSize == 0:
        print(df_final.shape)
        print('Saving data ' + str(i))
        if i == writeSize:
            print('Creating new file')
            df_final.to_csv(out_file,index=False,mode = 'a')
        else:
            print('Appending to file')
            df_final.to_csv(out_file,index=False,mode = 'a',header=False)
        df_final.drop(df_final.index, inplace=True)

