package de.zahrie.trues.database.types;

import java.util.Date;

import de.zahrie.trues.api.datatypes.calendar.Day;
import de.zahrie.trues.api.datatypes.calendar.Time;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class DayConverter implements AttributeConverter<Day, Date> {
  @Override
  public Date convertToDatabaseColumn(Day day) {
    return day.next().start().getTime();
  }

  @Override
  public Day convertToEntityAttribute(Date date) {
    return Time.of(date).getDay();
  }
}