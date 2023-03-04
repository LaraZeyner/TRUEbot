package de.zahrie.trues.database.types;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import de.zahrie.trues.api.datatypes.calendar.Time;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

/**
 * Created by Lara on 01.03.2023 for TRUEbot
 */
public class TimeCoverter implements UserType<Time> {

  @Override
  public int getSqlType() {
    return Types.TIMESTAMP;
  }

  @Override
  public Class<Time> returnedClass() {
    return Time.class;
  }

  @Override
  public boolean equals(Time x, Time y) {
    return x.equals(y);
  }

  @Override
  public int hashCode(Time x) {
    return x.hashCode();
  }

  @Override
  public Time nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
    final Date date = rs.getTimestamp(position);
    return Time.of(date);
  }

  @Override
  public void nullSafeSet(PreparedStatement st, Time value, int index, SharedSessionContractImplementor session) throws SQLException {
    if (value == null) {
      st.setNull(index, Types.TIMESTAMP);
    } else {
      st.setTimestamp(index, new Timestamp(value.getTime().getTime()));
    }
  }

  @Override
  public Time deepCopy(Time value) {
    return value == null ? null : (Time) value.clone();
  }

  @Override
  public boolean isMutable() {
    return true;
  }

  @Override
  public Serializable disassemble(Time value) {
    return deepCopy(value);
  }

  @Override
  public Time assemble(Serializable cached, Object owner) {
    return (Time) cached;
  }

}
