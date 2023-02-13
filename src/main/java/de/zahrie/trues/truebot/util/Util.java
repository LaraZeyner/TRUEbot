package de.zahrie.trues.truebot.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.zahrie.trues.truebot.PrimeData;
import org.hibernate.Session;

/**
 * Created by Lara on 06.04.2022 for web
 */
public final class Util {
  public static String uncapitalizeFirst(String str) {
    return str.substring(0, 1).toLowerCase() + str.substring(1);
  }

  public static double getDouble(BigDecimal decimal) {
    return decimal != null ? decimal.doubleValue() : 0;
  }

  public static double div(double divident, double divisor) {
    return div(divident, divisor, 0);
  }

  public static double div(double divident, double divisor, boolean re) {
    if (re) {
      return div(divident, divisor, divident);
    }
    return div(divident, divisor, 0);
  }

  public static double div(double divident, double divisor, double result) {
    if (divisor == 0) {
      return result;
    }
    return divident / divisor;
  }

  public static Calendar getCalendar(Date date) {
    final Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return cal;
  }

  public static List query(String entityType) {
    final Session session = PrimeData.getInstance().getSession();
    return session.createQuery("from " + entityType).list();
  }

  public static String strip(String text, int length) {
    return text.substring(0, Math.min(length, text.length()));
  }

  public static int ordinalIndexOf(String str, String substr, int n) {
    int pos = str.indexOf(substr);
    while (--n > 0 && pos != -1)
      pos = str.indexOf(substr, pos + 1);
    return pos;
  }

  public static String capitalizeFirst(String str) {
    return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
  }

  public static int getInt(Object object) {
    return object != null ? (int) (((Long) object).longValue()) : 0;
  }

  public static int longToInt(Long l) {
    return Integer.parseInt(String.valueOf(l));
  }

  public static String until(Date start, String prefix) {
    long distance = Math.abs((System.currentTimeMillis() - start.getTime()) / 1000);

    final int seconds = (int) (distance % 60);
    String secondsString = ("00" + seconds).substring(("00" + seconds).length() - 2);
    final int minutes = (int) ((distance / 60) % 60);
    String minutesString = ("00" + minutes).substring(("00" + minutes).length() - 2);
    final int hours = (int) ((distance / 3_600) % 24);
    String hoursString = ("00" + hours).substring(("00" + hours).length() - 2);
    final int days = (int) (distance / 86_400);

    StringBuilder str = new StringBuilder(prefix);
    if (days > 2) {
      return new SimpleDateFormat("dd.MM. HH:mm").format(start);
    } else if (days > 1) {
      str.append(days).append("d ").append(hoursString).append(":").append(minutesString).append(":").append(secondsString);
    } else if (hours > 1) {
      str.append(days * 24 + hours).append(":").append(minutesString).append(":").append(secondsString);
    } else if (minutes > 1) {
      str.append(hours * 60 + minutes).append(":").append(secondsString);
    } else {
      str.append(minutes * 60 + seconds).append("s");
    }

    return str.toString();
  }

  public static Date getDate(LocalDateTime dateToConvert) {
    return Date.from(dateToConvert.atZone(ZoneId.systemDefault()).toInstant());
  }

  public static LocalDateTime getLocalDate(Date dateToConvert) {
    return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
  }

  @SuppressWarnings({"unchecked"})
  public static <T> T cast(Object obj) {
    return (T) obj;
  }

}
