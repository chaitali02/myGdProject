// ORM class for table 'account_mysql'
// WARNING: This class is AUTO-GENERATED. Modify at your own risk.
//
// Debug information:
// Generated date: Thu Sep 27 17:56:34 EDT 2018
// For connector: org.apache.sqoop.manager.MySQLManager
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.lib.db.DBWritable;
import com.cloudera.sqoop.lib.JdbcWritableBridge;
import com.cloudera.sqoop.lib.DelimiterSet;
import com.cloudera.sqoop.lib.FieldFormatter;
import com.cloudera.sqoop.lib.RecordParser;
import com.cloudera.sqoop.lib.BooleanParser;
import com.cloudera.sqoop.lib.BlobRef;
import com.cloudera.sqoop.lib.ClobRef;
import com.cloudera.sqoop.lib.LargeObjectLoader;
import com.cloudera.sqoop.lib.SqoopRecord;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class account_mysql extends SqoopRecord  implements DBWritable, Writable {
  private final int PROTOCOL_VERSION = 3;
  public int getClassFormatVersion() { return PROTOCOL_VERSION; }
  public static interface FieldSetterCommand {    void setField(Object value);  }  protected ResultSet __cur_result_set;
  private Map<String, FieldSetterCommand> setters = new HashMap<String, FieldSetterCommand>();
  private void init0() {
    setters.put("account_id", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        account_id = (String)value;
      }
    });
    setters.put("account_number", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        account_number = (String)value;
      }
    });
    setters.put("account_open_date", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        account_open_date = (String)value;
      }
    });
    setters.put("account_status_id", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        account_status_id = (Integer)value;
      }
    });
    setters.put("account_type_id", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        account_type_id = (Integer)value;
      }
    });
    setters.put("currency_code", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        currency_code = (String)value;
      }
    });
    setters.put("current_balance", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        current_balance = (String)value;
      }
    });
    setters.put("customer_id", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        customer_id = (String)value;
      }
    });
    setters.put("interest_rate", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        interest_rate = (Float)value;
      }
    });
    setters.put("interest_type", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        interest_type = (String)value;
      }
    });
    setters.put("load_date", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        load_date = (String)value;
      }
    });
    setters.put("load_id", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        load_id = (Integer)value;
      }
    });
    setters.put("nationality", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        nationality = (String)value;
      }
    });
    setters.put("opening_balance", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        opening_balance = (String)value;
      }
    });
    setters.put("overdue_balance", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        overdue_balance = (Integer)value;
      }
    });
    setters.put("overdue_date", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        overdue_date = (String)value;
      }
    });
    setters.put("pin_number", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        pin_number = (Integer)value;
      }
    });
    setters.put("primary_iden_doc", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        primary_iden_doc = (String)value;
      }
    });
    setters.put("primary_iden_doc_id", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        primary_iden_doc_id = (String)value;
      }
    });
    setters.put("product_type_id", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        product_type_id = (Integer)value;
      }
    });
    setters.put("secondary_iden_doc", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        secondary_iden_doc = (String)value;
      }
    });
    setters.put("secondary_iden_doc_id", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        secondary_iden_doc_id = (String)value;
      }
    });
  }
  public account_mysql() {
    init0();
  }
  private String account_id;
  public String get_account_id() {
    return account_id;
  }
  public void set_account_id(String account_id) {
    this.account_id = account_id;
  }
  public account_mysql with_account_id(String account_id) {
    this.account_id = account_id;
    return this;
  }
  private String account_number;
  public String get_account_number() {
    return account_number;
  }
  public void set_account_number(String account_number) {
    this.account_number = account_number;
  }
  public account_mysql with_account_number(String account_number) {
    this.account_number = account_number;
    return this;
  }
  private String account_open_date;
  public String get_account_open_date() {
    return account_open_date;
  }
  public void set_account_open_date(String account_open_date) {
    this.account_open_date = account_open_date;
  }
  public account_mysql with_account_open_date(String account_open_date) {
    this.account_open_date = account_open_date;
    return this;
  }
  private Integer account_status_id;
  public Integer get_account_status_id() {
    return account_status_id;
  }
  public void set_account_status_id(Integer account_status_id) {
    this.account_status_id = account_status_id;
  }
  public account_mysql with_account_status_id(Integer account_status_id) {
    this.account_status_id = account_status_id;
    return this;
  }
  private Integer account_type_id;
  public Integer get_account_type_id() {
    return account_type_id;
  }
  public void set_account_type_id(Integer account_type_id) {
    this.account_type_id = account_type_id;
  }
  public account_mysql with_account_type_id(Integer account_type_id) {
    this.account_type_id = account_type_id;
    return this;
  }
  private String currency_code;
  public String get_currency_code() {
    return currency_code;
  }
  public void set_currency_code(String currency_code) {
    this.currency_code = currency_code;
  }
  public account_mysql with_currency_code(String currency_code) {
    this.currency_code = currency_code;
    return this;
  }
  private String current_balance;
  public String get_current_balance() {
    return current_balance;
  }
  public void set_current_balance(String current_balance) {
    this.current_balance = current_balance;
  }
  public account_mysql with_current_balance(String current_balance) {
    this.current_balance = current_balance;
    return this;
  }
  private String customer_id;
  public String get_customer_id() {
    return customer_id;
  }
  public void set_customer_id(String customer_id) {
    this.customer_id = customer_id;
  }
  public account_mysql with_customer_id(String customer_id) {
    this.customer_id = customer_id;
    return this;
  }
  private Float interest_rate;
  public Float get_interest_rate() {
    return interest_rate;
  }
  public void set_interest_rate(Float interest_rate) {
    this.interest_rate = interest_rate;
  }
  public account_mysql with_interest_rate(Float interest_rate) {
    this.interest_rate = interest_rate;
    return this;
  }
  private String interest_type;
  public String get_interest_type() {
    return interest_type;
  }
  public void set_interest_type(String interest_type) {
    this.interest_type = interest_type;
  }
  public account_mysql with_interest_type(String interest_type) {
    this.interest_type = interest_type;
    return this;
  }
  private String load_date;
  public String get_load_date() {
    return load_date;
  }
  public void set_load_date(String load_date) {
    this.load_date = load_date;
  }
  public account_mysql with_load_date(String load_date) {
    this.load_date = load_date;
    return this;
  }
  private Integer load_id;
  public Integer get_load_id() {
    return load_id;
  }
  public void set_load_id(Integer load_id) {
    this.load_id = load_id;
  }
  public account_mysql with_load_id(Integer load_id) {
    this.load_id = load_id;
    return this;
  }
  private String nationality;
  public String get_nationality() {
    return nationality;
  }
  public void set_nationality(String nationality) {
    this.nationality = nationality;
  }
  public account_mysql with_nationality(String nationality) {
    this.nationality = nationality;
    return this;
  }
  private String opening_balance;
  public String get_opening_balance() {
    return opening_balance;
  }
  public void set_opening_balance(String opening_balance) {
    this.opening_balance = opening_balance;
  }
  public account_mysql with_opening_balance(String opening_balance) {
    this.opening_balance = opening_balance;
    return this;
  }
  private Integer overdue_balance;
  public Integer get_overdue_balance() {
    return overdue_balance;
  }
  public void set_overdue_balance(Integer overdue_balance) {
    this.overdue_balance = overdue_balance;
  }
  public account_mysql with_overdue_balance(Integer overdue_balance) {
    this.overdue_balance = overdue_balance;
    return this;
  }
  private String overdue_date;
  public String get_overdue_date() {
    return overdue_date;
  }
  public void set_overdue_date(String overdue_date) {
    this.overdue_date = overdue_date;
  }
  public account_mysql with_overdue_date(String overdue_date) {
    this.overdue_date = overdue_date;
    return this;
  }
  private Integer pin_number;
  public Integer get_pin_number() {
    return pin_number;
  }
  public void set_pin_number(Integer pin_number) {
    this.pin_number = pin_number;
  }
  public account_mysql with_pin_number(Integer pin_number) {
    this.pin_number = pin_number;
    return this;
  }
  private String primary_iden_doc;
  public String get_primary_iden_doc() {
    return primary_iden_doc;
  }
  public void set_primary_iden_doc(String primary_iden_doc) {
    this.primary_iden_doc = primary_iden_doc;
  }
  public account_mysql with_primary_iden_doc(String primary_iden_doc) {
    this.primary_iden_doc = primary_iden_doc;
    return this;
  }
  private String primary_iden_doc_id;
  public String get_primary_iden_doc_id() {
    return primary_iden_doc_id;
  }
  public void set_primary_iden_doc_id(String primary_iden_doc_id) {
    this.primary_iden_doc_id = primary_iden_doc_id;
  }
  public account_mysql with_primary_iden_doc_id(String primary_iden_doc_id) {
    this.primary_iden_doc_id = primary_iden_doc_id;
    return this;
  }
  private Integer product_type_id;
  public Integer get_product_type_id() {
    return product_type_id;
  }
  public void set_product_type_id(Integer product_type_id) {
    this.product_type_id = product_type_id;
  }
  public account_mysql with_product_type_id(Integer product_type_id) {
    this.product_type_id = product_type_id;
    return this;
  }
  private String secondary_iden_doc;
  public String get_secondary_iden_doc() {
    return secondary_iden_doc;
  }
  public void set_secondary_iden_doc(String secondary_iden_doc) {
    this.secondary_iden_doc = secondary_iden_doc;
  }
  public account_mysql with_secondary_iden_doc(String secondary_iden_doc) {
    this.secondary_iden_doc = secondary_iden_doc;
    return this;
  }
  private String secondary_iden_doc_id;
  public String get_secondary_iden_doc_id() {
    return secondary_iden_doc_id;
  }
  public void set_secondary_iden_doc_id(String secondary_iden_doc_id) {
    this.secondary_iden_doc_id = secondary_iden_doc_id;
  }
  public account_mysql with_secondary_iden_doc_id(String secondary_iden_doc_id) {
    this.secondary_iden_doc_id = secondary_iden_doc_id;
    return this;
  }
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof account_mysql)) {
      return false;
    }
    account_mysql that = (account_mysql) o;
    boolean equal = true;
    equal = equal && (this.account_id == null ? that.account_id == null : this.account_id.equals(that.account_id));
    equal = equal && (this.account_number == null ? that.account_number == null : this.account_number.equals(that.account_number));
    equal = equal && (this.account_open_date == null ? that.account_open_date == null : this.account_open_date.equals(that.account_open_date));
    equal = equal && (this.account_status_id == null ? that.account_status_id == null : this.account_status_id.equals(that.account_status_id));
    equal = equal && (this.account_type_id == null ? that.account_type_id == null : this.account_type_id.equals(that.account_type_id));
    equal = equal && (this.currency_code == null ? that.currency_code == null : this.currency_code.equals(that.currency_code));
    equal = equal && (this.current_balance == null ? that.current_balance == null : this.current_balance.equals(that.current_balance));
    equal = equal && (this.customer_id == null ? that.customer_id == null : this.customer_id.equals(that.customer_id));
    equal = equal && (this.interest_rate == null ? that.interest_rate == null : this.interest_rate.equals(that.interest_rate));
    equal = equal && (this.interest_type == null ? that.interest_type == null : this.interest_type.equals(that.interest_type));
    equal = equal && (this.load_date == null ? that.load_date == null : this.load_date.equals(that.load_date));
    equal = equal && (this.load_id == null ? that.load_id == null : this.load_id.equals(that.load_id));
    equal = equal && (this.nationality == null ? that.nationality == null : this.nationality.equals(that.nationality));
    equal = equal && (this.opening_balance == null ? that.opening_balance == null : this.opening_balance.equals(that.opening_balance));
    equal = equal && (this.overdue_balance == null ? that.overdue_balance == null : this.overdue_balance.equals(that.overdue_balance));
    equal = equal && (this.overdue_date == null ? that.overdue_date == null : this.overdue_date.equals(that.overdue_date));
    equal = equal && (this.pin_number == null ? that.pin_number == null : this.pin_number.equals(that.pin_number));
    equal = equal && (this.primary_iden_doc == null ? that.primary_iden_doc == null : this.primary_iden_doc.equals(that.primary_iden_doc));
    equal = equal && (this.primary_iden_doc_id == null ? that.primary_iden_doc_id == null : this.primary_iden_doc_id.equals(that.primary_iden_doc_id));
    equal = equal && (this.product_type_id == null ? that.product_type_id == null : this.product_type_id.equals(that.product_type_id));
    equal = equal && (this.secondary_iden_doc == null ? that.secondary_iden_doc == null : this.secondary_iden_doc.equals(that.secondary_iden_doc));
    equal = equal && (this.secondary_iden_doc_id == null ? that.secondary_iden_doc_id == null : this.secondary_iden_doc_id.equals(that.secondary_iden_doc_id));
    return equal;
  }
  public boolean equals0(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof account_mysql)) {
      return false;
    }
    account_mysql that = (account_mysql) o;
    boolean equal = true;
    equal = equal && (this.account_id == null ? that.account_id == null : this.account_id.equals(that.account_id));
    equal = equal && (this.account_number == null ? that.account_number == null : this.account_number.equals(that.account_number));
    equal = equal && (this.account_open_date == null ? that.account_open_date == null : this.account_open_date.equals(that.account_open_date));
    equal = equal && (this.account_status_id == null ? that.account_status_id == null : this.account_status_id.equals(that.account_status_id));
    equal = equal && (this.account_type_id == null ? that.account_type_id == null : this.account_type_id.equals(that.account_type_id));
    equal = equal && (this.currency_code == null ? that.currency_code == null : this.currency_code.equals(that.currency_code));
    equal = equal && (this.current_balance == null ? that.current_balance == null : this.current_balance.equals(that.current_balance));
    equal = equal && (this.customer_id == null ? that.customer_id == null : this.customer_id.equals(that.customer_id));
    equal = equal && (this.interest_rate == null ? that.interest_rate == null : this.interest_rate.equals(that.interest_rate));
    equal = equal && (this.interest_type == null ? that.interest_type == null : this.interest_type.equals(that.interest_type));
    equal = equal && (this.load_date == null ? that.load_date == null : this.load_date.equals(that.load_date));
    equal = equal && (this.load_id == null ? that.load_id == null : this.load_id.equals(that.load_id));
    equal = equal && (this.nationality == null ? that.nationality == null : this.nationality.equals(that.nationality));
    equal = equal && (this.opening_balance == null ? that.opening_balance == null : this.opening_balance.equals(that.opening_balance));
    equal = equal && (this.overdue_balance == null ? that.overdue_balance == null : this.overdue_balance.equals(that.overdue_balance));
    equal = equal && (this.overdue_date == null ? that.overdue_date == null : this.overdue_date.equals(that.overdue_date));
    equal = equal && (this.pin_number == null ? that.pin_number == null : this.pin_number.equals(that.pin_number));
    equal = equal && (this.primary_iden_doc == null ? that.primary_iden_doc == null : this.primary_iden_doc.equals(that.primary_iden_doc));
    equal = equal && (this.primary_iden_doc_id == null ? that.primary_iden_doc_id == null : this.primary_iden_doc_id.equals(that.primary_iden_doc_id));
    equal = equal && (this.product_type_id == null ? that.product_type_id == null : this.product_type_id.equals(that.product_type_id));
    equal = equal && (this.secondary_iden_doc == null ? that.secondary_iden_doc == null : this.secondary_iden_doc.equals(that.secondary_iden_doc));
    equal = equal && (this.secondary_iden_doc_id == null ? that.secondary_iden_doc_id == null : this.secondary_iden_doc_id.equals(that.secondary_iden_doc_id));
    return equal;
  }
  public void readFields(ResultSet __dbResults) throws SQLException {
    this.__cur_result_set = __dbResults;
    this.account_id = JdbcWritableBridge.readString(1, __dbResults);
    this.account_number = JdbcWritableBridge.readString(2, __dbResults);
    this.account_open_date = JdbcWritableBridge.readString(3, __dbResults);
    this.account_status_id = JdbcWritableBridge.readInteger(4, __dbResults);
    this.account_type_id = JdbcWritableBridge.readInteger(5, __dbResults);
    this.currency_code = JdbcWritableBridge.readString(6, __dbResults);
    this.current_balance = JdbcWritableBridge.readString(7, __dbResults);
    this.customer_id = JdbcWritableBridge.readString(8, __dbResults);
    this.interest_rate = JdbcWritableBridge.readFloat(9, __dbResults);
    this.interest_type = JdbcWritableBridge.readString(10, __dbResults);
    this.load_date = JdbcWritableBridge.readString(11, __dbResults);
    this.load_id = JdbcWritableBridge.readInteger(12, __dbResults);
    this.nationality = JdbcWritableBridge.readString(13, __dbResults);
    this.opening_balance = JdbcWritableBridge.readString(14, __dbResults);
    this.overdue_balance = JdbcWritableBridge.readInteger(15, __dbResults);
    this.overdue_date = JdbcWritableBridge.readString(16, __dbResults);
    this.pin_number = JdbcWritableBridge.readInteger(17, __dbResults);
    this.primary_iden_doc = JdbcWritableBridge.readString(18, __dbResults);
    this.primary_iden_doc_id = JdbcWritableBridge.readString(19, __dbResults);
    this.product_type_id = JdbcWritableBridge.readInteger(20, __dbResults);
    this.secondary_iden_doc = JdbcWritableBridge.readString(21, __dbResults);
    this.secondary_iden_doc_id = JdbcWritableBridge.readString(22, __dbResults);
  }
  public void readFields0(ResultSet __dbResults) throws SQLException {
    this.account_id = JdbcWritableBridge.readString(1, __dbResults);
    this.account_number = JdbcWritableBridge.readString(2, __dbResults);
    this.account_open_date = JdbcWritableBridge.readString(3, __dbResults);
    this.account_status_id = JdbcWritableBridge.readInteger(4, __dbResults);
    this.account_type_id = JdbcWritableBridge.readInteger(5, __dbResults);
    this.currency_code = JdbcWritableBridge.readString(6, __dbResults);
    this.current_balance = JdbcWritableBridge.readString(7, __dbResults);
    this.customer_id = JdbcWritableBridge.readString(8, __dbResults);
    this.interest_rate = JdbcWritableBridge.readFloat(9, __dbResults);
    this.interest_type = JdbcWritableBridge.readString(10, __dbResults);
    this.load_date = JdbcWritableBridge.readString(11, __dbResults);
    this.load_id = JdbcWritableBridge.readInteger(12, __dbResults);
    this.nationality = JdbcWritableBridge.readString(13, __dbResults);
    this.opening_balance = JdbcWritableBridge.readString(14, __dbResults);
    this.overdue_balance = JdbcWritableBridge.readInteger(15, __dbResults);
    this.overdue_date = JdbcWritableBridge.readString(16, __dbResults);
    this.pin_number = JdbcWritableBridge.readInteger(17, __dbResults);
    this.primary_iden_doc = JdbcWritableBridge.readString(18, __dbResults);
    this.primary_iden_doc_id = JdbcWritableBridge.readString(19, __dbResults);
    this.product_type_id = JdbcWritableBridge.readInteger(20, __dbResults);
    this.secondary_iden_doc = JdbcWritableBridge.readString(21, __dbResults);
    this.secondary_iden_doc_id = JdbcWritableBridge.readString(22, __dbResults);
  }
  public void loadLargeObjects(LargeObjectLoader __loader)
      throws SQLException, IOException, InterruptedException {
  }
  public void loadLargeObjects0(LargeObjectLoader __loader)
      throws SQLException, IOException, InterruptedException {
  }
  public void write(PreparedStatement __dbStmt) throws SQLException {
    write(__dbStmt, 0);
  }

  public int write(PreparedStatement __dbStmt, int __off) throws SQLException {
    JdbcWritableBridge.writeString(account_id, 1 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeString(account_number, 2 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeString(account_open_date, 3 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeInteger(account_status_id, 4 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeInteger(account_type_id, 5 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeString(currency_code, 6 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeString(current_balance, 7 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeString(customer_id, 8 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeFloat(interest_rate, 9 + __off, 7, __dbStmt);
    JdbcWritableBridge.writeString(interest_type, 10 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeString(load_date, 11 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeInteger(load_id, 12 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeString(nationality, 13 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeString(opening_balance, 14 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeInteger(overdue_balance, 15 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeString(overdue_date, 16 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeInteger(pin_number, 17 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeString(primary_iden_doc, 18 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeString(primary_iden_doc_id, 19 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeInteger(product_type_id, 20 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeString(secondary_iden_doc, 21 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeString(secondary_iden_doc_id, 22 + __off, 12, __dbStmt);
    return 22;
  }
  public void write0(PreparedStatement __dbStmt, int __off) throws SQLException {
    JdbcWritableBridge.writeString(account_id, 1 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeString(account_number, 2 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeString(account_open_date, 3 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeInteger(account_status_id, 4 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeInteger(account_type_id, 5 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeString(currency_code, 6 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeString(current_balance, 7 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeString(customer_id, 8 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeFloat(interest_rate, 9 + __off, 7, __dbStmt);
    JdbcWritableBridge.writeString(interest_type, 10 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeString(load_date, 11 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeInteger(load_id, 12 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeString(nationality, 13 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeString(opening_balance, 14 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeInteger(overdue_balance, 15 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeString(overdue_date, 16 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeInteger(pin_number, 17 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeString(primary_iden_doc, 18 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeString(primary_iden_doc_id, 19 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeInteger(product_type_id, 20 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeString(secondary_iden_doc, 21 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeString(secondary_iden_doc_id, 22 + __off, 12, __dbStmt);
  }
  public void readFields(DataInput __dataIn) throws IOException {
this.readFields0(__dataIn);  }
  public void readFields0(DataInput __dataIn) throws IOException {
    if (__dataIn.readBoolean()) { 
        this.account_id = null;
    } else {
    this.account_id = Text.readString(__dataIn);
    }
    if (__dataIn.readBoolean()) { 
        this.account_number = null;
    } else {
    this.account_number = Text.readString(__dataIn);
    }
    if (__dataIn.readBoolean()) { 
        this.account_open_date = null;
    } else {
    this.account_open_date = Text.readString(__dataIn);
    }
    if (__dataIn.readBoolean()) { 
        this.account_status_id = null;
    } else {
    this.account_status_id = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.account_type_id = null;
    } else {
    this.account_type_id = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.currency_code = null;
    } else {
    this.currency_code = Text.readString(__dataIn);
    }
    if (__dataIn.readBoolean()) { 
        this.current_balance = null;
    } else {
    this.current_balance = Text.readString(__dataIn);
    }
    if (__dataIn.readBoolean()) { 
        this.customer_id = null;
    } else {
    this.customer_id = Text.readString(__dataIn);
    }
    if (__dataIn.readBoolean()) { 
        this.interest_rate = null;
    } else {
    this.interest_rate = Float.valueOf(__dataIn.readFloat());
    }
    if (__dataIn.readBoolean()) { 
        this.interest_type = null;
    } else {
    this.interest_type = Text.readString(__dataIn);
    }
    if (__dataIn.readBoolean()) { 
        this.load_date = null;
    } else {
    this.load_date = Text.readString(__dataIn);
    }
    if (__dataIn.readBoolean()) { 
        this.load_id = null;
    } else {
    this.load_id = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.nationality = null;
    } else {
    this.nationality = Text.readString(__dataIn);
    }
    if (__dataIn.readBoolean()) { 
        this.opening_balance = null;
    } else {
    this.opening_balance = Text.readString(__dataIn);
    }
    if (__dataIn.readBoolean()) { 
        this.overdue_balance = null;
    } else {
    this.overdue_balance = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.overdue_date = null;
    } else {
    this.overdue_date = Text.readString(__dataIn);
    }
    if (__dataIn.readBoolean()) { 
        this.pin_number = null;
    } else {
    this.pin_number = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.primary_iden_doc = null;
    } else {
    this.primary_iden_doc = Text.readString(__dataIn);
    }
    if (__dataIn.readBoolean()) { 
        this.primary_iden_doc_id = null;
    } else {
    this.primary_iden_doc_id = Text.readString(__dataIn);
    }
    if (__dataIn.readBoolean()) { 
        this.product_type_id = null;
    } else {
    this.product_type_id = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.secondary_iden_doc = null;
    } else {
    this.secondary_iden_doc = Text.readString(__dataIn);
    }
    if (__dataIn.readBoolean()) { 
        this.secondary_iden_doc_id = null;
    } else {
    this.secondary_iden_doc_id = Text.readString(__dataIn);
    }
  }
  public void write(DataOutput __dataOut) throws IOException {
    if (null == this.account_id) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, account_id);
    }
    if (null == this.account_number) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, account_number);
    }
    if (null == this.account_open_date) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, account_open_date);
    }
    if (null == this.account_status_id) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.account_status_id);
    }
    if (null == this.account_type_id) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.account_type_id);
    }
    if (null == this.currency_code) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, currency_code);
    }
    if (null == this.current_balance) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, current_balance);
    }
    if (null == this.customer_id) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, customer_id);
    }
    if (null == this.interest_rate) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeFloat(this.interest_rate);
    }
    if (null == this.interest_type) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, interest_type);
    }
    if (null == this.load_date) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, load_date);
    }
    if (null == this.load_id) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.load_id);
    }
    if (null == this.nationality) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, nationality);
    }
    if (null == this.opening_balance) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, opening_balance);
    }
    if (null == this.overdue_balance) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.overdue_balance);
    }
    if (null == this.overdue_date) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, overdue_date);
    }
    if (null == this.pin_number) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.pin_number);
    }
    if (null == this.primary_iden_doc) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, primary_iden_doc);
    }
    if (null == this.primary_iden_doc_id) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, primary_iden_doc_id);
    }
    if (null == this.product_type_id) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.product_type_id);
    }
    if (null == this.secondary_iden_doc) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, secondary_iden_doc);
    }
    if (null == this.secondary_iden_doc_id) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, secondary_iden_doc_id);
    }
  }
  public void write0(DataOutput __dataOut) throws IOException {
    if (null == this.account_id) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, account_id);
    }
    if (null == this.account_number) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, account_number);
    }
    if (null == this.account_open_date) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, account_open_date);
    }
    if (null == this.account_status_id) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.account_status_id);
    }
    if (null == this.account_type_id) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.account_type_id);
    }
    if (null == this.currency_code) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, currency_code);
    }
    if (null == this.current_balance) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, current_balance);
    }
    if (null == this.customer_id) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, customer_id);
    }
    if (null == this.interest_rate) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeFloat(this.interest_rate);
    }
    if (null == this.interest_type) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, interest_type);
    }
    if (null == this.load_date) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, load_date);
    }
    if (null == this.load_id) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.load_id);
    }
    if (null == this.nationality) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, nationality);
    }
    if (null == this.opening_balance) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, opening_balance);
    }
    if (null == this.overdue_balance) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.overdue_balance);
    }
    if (null == this.overdue_date) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, overdue_date);
    }
    if (null == this.pin_number) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.pin_number);
    }
    if (null == this.primary_iden_doc) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, primary_iden_doc);
    }
    if (null == this.primary_iden_doc_id) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, primary_iden_doc_id);
    }
    if (null == this.product_type_id) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.product_type_id);
    }
    if (null == this.secondary_iden_doc) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, secondary_iden_doc);
    }
    if (null == this.secondary_iden_doc_id) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, secondary_iden_doc_id);
    }
  }
  private static final DelimiterSet __outputDelimiters = new DelimiterSet((char) 44, (char) 10, (char) 0, (char) 0, false);
  public String toString() {
    return toString(__outputDelimiters, true);
  }
  public String toString(DelimiterSet delimiters) {
    return toString(delimiters, true);
  }
  public String toString(boolean useRecordDelim) {
    return toString(__outputDelimiters, useRecordDelim);
  }
  public String toString(DelimiterSet delimiters, boolean useRecordDelim) {
    StringBuilder __sb = new StringBuilder();
    char fieldDelim = delimiters.getFieldsTerminatedBy();
    __sb.append(FieldFormatter.escapeAndEnclose(account_id==null?"null":account_id, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(account_number==null?"null":account_number, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(account_open_date==null?"null":account_open_date, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(account_status_id==null?"null":"" + account_status_id, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(account_type_id==null?"null":"" + account_type_id, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(currency_code==null?"null":currency_code, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(current_balance==null?"null":current_balance, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(customer_id==null?"null":customer_id, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(interest_rate==null?"null":"" + interest_rate, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(interest_type==null?"null":interest_type, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(load_date==null?"null":load_date, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(load_id==null?"null":"" + load_id, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(nationality==null?"null":nationality, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(opening_balance==null?"null":opening_balance, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(overdue_balance==null?"null":"" + overdue_balance, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(overdue_date==null?"null":overdue_date, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(pin_number==null?"null":"" + pin_number, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(primary_iden_doc==null?"null":primary_iden_doc, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(primary_iden_doc_id==null?"null":primary_iden_doc_id, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(product_type_id==null?"null":"" + product_type_id, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(secondary_iden_doc==null?"null":secondary_iden_doc, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(secondary_iden_doc_id==null?"null":secondary_iden_doc_id, delimiters));
    if (useRecordDelim) {
      __sb.append(delimiters.getLinesTerminatedBy());
    }
    return __sb.toString();
  }
  public void toString0(DelimiterSet delimiters, StringBuilder __sb, char fieldDelim) {
    __sb.append(FieldFormatter.escapeAndEnclose(account_id==null?"null":account_id, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(account_number==null?"null":account_number, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(account_open_date==null?"null":account_open_date, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(account_status_id==null?"null":"" + account_status_id, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(account_type_id==null?"null":"" + account_type_id, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(currency_code==null?"null":currency_code, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(current_balance==null?"null":current_balance, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(customer_id==null?"null":customer_id, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(interest_rate==null?"null":"" + interest_rate, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(interest_type==null?"null":interest_type, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(load_date==null?"null":load_date, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(load_id==null?"null":"" + load_id, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(nationality==null?"null":nationality, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(opening_balance==null?"null":opening_balance, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(overdue_balance==null?"null":"" + overdue_balance, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(overdue_date==null?"null":overdue_date, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(pin_number==null?"null":"" + pin_number, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(primary_iden_doc==null?"null":primary_iden_doc, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(primary_iden_doc_id==null?"null":primary_iden_doc_id, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(product_type_id==null?"null":"" + product_type_id, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(secondary_iden_doc==null?"null":secondary_iden_doc, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(secondary_iden_doc_id==null?"null":secondary_iden_doc_id, delimiters));
  }
  private static final DelimiterSet __inputDelimiters = new DelimiterSet((char) 44, (char) 10, (char) 0, (char) 0, false);
  private RecordParser __parser;
  public void parse(Text __record) throws RecordParser.ParseError {
    if (null == this.__parser) {
      this.__parser = new RecordParser(__inputDelimiters);
    }
    List<String> __fields = this.__parser.parseRecord(__record);
    __loadFromFields(__fields);
  }

  public void parse(CharSequence __record) throws RecordParser.ParseError {
    if (null == this.__parser) {
      this.__parser = new RecordParser(__inputDelimiters);
    }
    List<String> __fields = this.__parser.parseRecord(__record);
    __loadFromFields(__fields);
  }

  public void parse(byte [] __record) throws RecordParser.ParseError {
    if (null == this.__parser) {
      this.__parser = new RecordParser(__inputDelimiters);
    }
    List<String> __fields = this.__parser.parseRecord(__record);
    __loadFromFields(__fields);
  }

  public void parse(char [] __record) throws RecordParser.ParseError {
    if (null == this.__parser) {
      this.__parser = new RecordParser(__inputDelimiters);
    }
    List<String> __fields = this.__parser.parseRecord(__record);
    __loadFromFields(__fields);
  }

  public void parse(ByteBuffer __record) throws RecordParser.ParseError {
    if (null == this.__parser) {
      this.__parser = new RecordParser(__inputDelimiters);
    }
    List<String> __fields = this.__parser.parseRecord(__record);
    __loadFromFields(__fields);
  }

  public void parse(CharBuffer __record) throws RecordParser.ParseError {
    if (null == this.__parser) {
      this.__parser = new RecordParser(__inputDelimiters);
    }
    List<String> __fields = this.__parser.parseRecord(__record);
    __loadFromFields(__fields);
  }

  private void __loadFromFields(List<String> fields) {
    Iterator<String> __it = fields.listIterator();
    String __cur_str = null;
    try {
    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.account_id = null; } else {
      this.account_id = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.account_number = null; } else {
      this.account_number = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.account_open_date = null; } else {
      this.account_open_date = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.account_status_id = null; } else {
      this.account_status_id = Integer.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.account_type_id = null; } else {
      this.account_type_id = Integer.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.currency_code = null; } else {
      this.currency_code = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.current_balance = null; } else {
      this.current_balance = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.customer_id = null; } else {
      this.customer_id = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.interest_rate = null; } else {
      this.interest_rate = Float.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.interest_type = null; } else {
      this.interest_type = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.load_date = null; } else {
      this.load_date = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.load_id = null; } else {
      this.load_id = Integer.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.nationality = null; } else {
      this.nationality = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.opening_balance = null; } else {
      this.opening_balance = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.overdue_balance = null; } else {
      this.overdue_balance = Integer.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.overdue_date = null; } else {
      this.overdue_date = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.pin_number = null; } else {
      this.pin_number = Integer.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.primary_iden_doc = null; } else {
      this.primary_iden_doc = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.primary_iden_doc_id = null; } else {
      this.primary_iden_doc_id = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.product_type_id = null; } else {
      this.product_type_id = Integer.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.secondary_iden_doc = null; } else {
      this.secondary_iden_doc = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.secondary_iden_doc_id = null; } else {
      this.secondary_iden_doc_id = __cur_str;
    }

    } catch (RuntimeException e) {    throw new RuntimeException("Can't parse input data: '" + __cur_str + "'", e);    }  }

  private void __loadFromFields0(Iterator<String> __it) {
    String __cur_str = null;
    try {
    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.account_id = null; } else {
      this.account_id = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.account_number = null; } else {
      this.account_number = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.account_open_date = null; } else {
      this.account_open_date = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.account_status_id = null; } else {
      this.account_status_id = Integer.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.account_type_id = null; } else {
      this.account_type_id = Integer.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.currency_code = null; } else {
      this.currency_code = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.current_balance = null; } else {
      this.current_balance = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.customer_id = null; } else {
      this.customer_id = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.interest_rate = null; } else {
      this.interest_rate = Float.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.interest_type = null; } else {
      this.interest_type = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.load_date = null; } else {
      this.load_date = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.load_id = null; } else {
      this.load_id = Integer.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.nationality = null; } else {
      this.nationality = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.opening_balance = null; } else {
      this.opening_balance = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.overdue_balance = null; } else {
      this.overdue_balance = Integer.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.overdue_date = null; } else {
      this.overdue_date = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.pin_number = null; } else {
      this.pin_number = Integer.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.primary_iden_doc = null; } else {
      this.primary_iden_doc = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.primary_iden_doc_id = null; } else {
      this.primary_iden_doc_id = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.product_type_id = null; } else {
      this.product_type_id = Integer.valueOf(__cur_str);
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.secondary_iden_doc = null; } else {
      this.secondary_iden_doc = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("null")) { this.secondary_iden_doc_id = null; } else {
      this.secondary_iden_doc_id = __cur_str;
    }

    } catch (RuntimeException e) {    throw new RuntimeException("Can't parse input data: '" + __cur_str + "'", e);    }  }

  public Object clone() throws CloneNotSupportedException {
    account_mysql o = (account_mysql) super.clone();
    return o;
  }

  public void clone0(account_mysql o) throws CloneNotSupportedException {
  }

  public Map<String, Object> getFieldMap() {
    Map<String, Object> __sqoop$field_map = new HashMap<String, Object>();
    __sqoop$field_map.put("account_id", this.account_id);
    __sqoop$field_map.put("account_number", this.account_number);
    __sqoop$field_map.put("account_open_date", this.account_open_date);
    __sqoop$field_map.put("account_status_id", this.account_status_id);
    __sqoop$field_map.put("account_type_id", this.account_type_id);
    __sqoop$field_map.put("currency_code", this.currency_code);
    __sqoop$field_map.put("current_balance", this.current_balance);
    __sqoop$field_map.put("customer_id", this.customer_id);
    __sqoop$field_map.put("interest_rate", this.interest_rate);
    __sqoop$field_map.put("interest_type", this.interest_type);
    __sqoop$field_map.put("load_date", this.load_date);
    __sqoop$field_map.put("load_id", this.load_id);
    __sqoop$field_map.put("nationality", this.nationality);
    __sqoop$field_map.put("opening_balance", this.opening_balance);
    __sqoop$field_map.put("overdue_balance", this.overdue_balance);
    __sqoop$field_map.put("overdue_date", this.overdue_date);
    __sqoop$field_map.put("pin_number", this.pin_number);
    __sqoop$field_map.put("primary_iden_doc", this.primary_iden_doc);
    __sqoop$field_map.put("primary_iden_doc_id", this.primary_iden_doc_id);
    __sqoop$field_map.put("product_type_id", this.product_type_id);
    __sqoop$field_map.put("secondary_iden_doc", this.secondary_iden_doc);
    __sqoop$field_map.put("secondary_iden_doc_id", this.secondary_iden_doc_id);
    return __sqoop$field_map;
  }

  public void getFieldMap0(Map<String, Object> __sqoop$field_map) {
    __sqoop$field_map.put("account_id", this.account_id);
    __sqoop$field_map.put("account_number", this.account_number);
    __sqoop$field_map.put("account_open_date", this.account_open_date);
    __sqoop$field_map.put("account_status_id", this.account_status_id);
    __sqoop$field_map.put("account_type_id", this.account_type_id);
    __sqoop$field_map.put("currency_code", this.currency_code);
    __sqoop$field_map.put("current_balance", this.current_balance);
    __sqoop$field_map.put("customer_id", this.customer_id);
    __sqoop$field_map.put("interest_rate", this.interest_rate);
    __sqoop$field_map.put("interest_type", this.interest_type);
    __sqoop$field_map.put("load_date", this.load_date);
    __sqoop$field_map.put("load_id", this.load_id);
    __sqoop$field_map.put("nationality", this.nationality);
    __sqoop$field_map.put("opening_balance", this.opening_balance);
    __sqoop$field_map.put("overdue_balance", this.overdue_balance);
    __sqoop$field_map.put("overdue_date", this.overdue_date);
    __sqoop$field_map.put("pin_number", this.pin_number);
    __sqoop$field_map.put("primary_iden_doc", this.primary_iden_doc);
    __sqoop$field_map.put("primary_iden_doc_id", this.primary_iden_doc_id);
    __sqoop$field_map.put("product_type_id", this.product_type_id);
    __sqoop$field_map.put("secondary_iden_doc", this.secondary_iden_doc);
    __sqoop$field_map.put("secondary_iden_doc_id", this.secondary_iden_doc_id);
  }

  public void setField(String __fieldName, Object __fieldVal) {
    if (!setters.containsKey(__fieldName)) {
      throw new RuntimeException("No such field:"+__fieldName);
    }
    setters.get(__fieldName).setField(__fieldVal);
  }

}
