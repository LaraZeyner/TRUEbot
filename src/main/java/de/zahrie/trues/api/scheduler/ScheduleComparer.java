package de.zahrie.trues.api.scheduler;

import java.util.Calendar;

import de.zahrie.trues.api.datatypes.symbol.StringExtention;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(StringExtention.class)
public class ScheduleComparer {
  private final Schedule schedule;
  private final Calendar now = Calendar.getInstance();

  public ScheduleComparer(Schedule schedule) {
    this.schedule = schedule;
  }

  public boolean test() {
    return check(Calendar.MINUTE, schedule.minute())
        && check(Calendar.HOUR_OF_DAY, schedule.hour())
        && check(Calendar.DAY_OF_WEEK, schedule.dayOfWeek())
        && check(Calendar.DAY_OF_MONTH, schedule.dayOfMonth())
        && check(Calendar.MONTH, schedule.month())
        && check(Calendar.YEAR, schedule.year());
  }

  private boolean check(int field, String value) {
    final int currentValue = now.get(field);
    final Integer every = value.between("%").intValue(null);
    return value.equals("*") || (value.contains("%") && every != null && currentValue % every == 0) || value.equals(String.valueOf(currentValue));
  }
}
