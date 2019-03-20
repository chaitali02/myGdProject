
# coding: utf-8

# In[109]:


import pandas as pd

df = pd.read_csv('/Users/ypalrecha/git/inferyx/framework-web/app/db/data/csv/header/equity_orders_new.csv')


# In[110]:


iterRange = 250
newDF = pd.DataFrame() 
i_new = 0
for i in range(0,iterRange):
    i_new += 1
    df_new = pd.DataFrame()
    df_new = df_new.append(df)
    df_new.order_identifier = df_new.order_identifier+i
    df_new.parent_order_id = df_new.parent_order_id+i
    newDF = newDF.append(df_new)


# In[111]:


newDF.shape


# In[112]:


newDF.to_csv('/Users/ypalrecha/git/inferyx/framework-web/app/db/data/csv/header/equity_orders_new_1000.csv',index=False)


# In[ ]:


import pandas as pd

df = pd.read_csv('/Users/ypalrecha/git/inferyx/framework-web/app/db/data/csv/header/equity_executions_new.csv')


# In[ ]:


iterRange = 250
newDF = pd.DataFrame() 
i_new = 0
for i in range(0,iterRange):
    i_new += 1
    df_new = pd.DataFrame()
    df_new = df_new.append(df)
    df_new.order_identifier = df_new.order_identifier+i
    df_new.trade_execution_identifier = df_new.trade_execution_identifier+i
    newDF = newDF.append(df_new)


# In[ ]:


newDF.shape


# In[ ]:


newDF.to_csv('/Users/ypalrecha/git/inferyx/framework-web/app/db/data/csv/header/equity_executions_new_1000.csv',index=False)

