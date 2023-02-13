package de.zahrie.trues.truebot.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Created by Lara on 13.02.2023 for TRUEbot
 * <table class="striped">
 * <thead>
 *     <tr>
 *         <th scope="col" style="text-align:left">Letter
 *         <th scope="col" style="text-align:left">Date or Time Component
 *         <th scope="col" style="text-align:left">Presentation
 *         <th scope="col" style="text-align:left">Examples
 * </thead>
 * <tbody>
 *     <tr>
 *         <th scope="row">{@code y}
 *         <td>Year
 *         <td><a href="#year">Year</a>
 *         <td>{@code 1996}; {@code 96}
 *     <tr>
 *         <th scope="row">{@code M}
 *         <td>Month in year (context sensitive)
 *         <td><a href="#month">Month</a>
 *         <td>{@code July}; {@code Jul}; {@code 07}
 *     <tr>
 *         <th scope="row">{@code w}
 *         <td>Week in year
 *         <td><a href="#number">Number</a>
 *         <td>{@code 27}
 *     <tr>
 *         <th scope="row">{@code W}
 *         <td>Week in month
 *         <td><a href="#number">Number</a>
 *         <td>{@code 2}
 *     <tr>
 *         <th scope="row">{@code D}
 *         <td>Day in year
 *         <td><a href="#number">Number</a>
 *         <td>{@code 189}
 *     <tr>
 *         <th scope="row">{@code d}
 *         <td>Day in month
 *         <td><a href="#number">Number</a>
 *         <td>{@code 10}
 *     <tr>
 *         <th scope="row">{@code E}
 *         <td>Day name in week
 *         <td><a href="#text">Text</a>
 *         <td>{@code Tuesday}; {@code Tue}
 *     <tr>
 *         <th scope="row">{@code u}
 *         <td>Day number of week (1 = Monday, ..., 7 = Sunday)
 *         <td><a href="#number">Number</a>
 *         <td>{@code 1}
 *     <tr>
 *         <th scope="row">{@code H}
 *         <td>Hour in day (0-23)
 *         <td><a href="#number">Number</a>
 *         <td>{@code 0}
 *     <tr>
 *         <th scope="row">{@code m}
 *         <td>Minute in hour
 *         <td><a href="#number">Number</a>
 *         <td>{@code 30}
 *     <tr>
 *         <th scope="row">{@code s}
 *         <td>Second in minute
 *         <td><a href="#number">Number</a>
 *         <td>{@code 55}
 *     <tr>
 *         <th scope="row">{@code S}
 *         <td>Millisecond
 *         <td><a href="#number">Number</a>
 *         <td>{@code 978}
 *     <tr>
 *         <th scope="row">{@code Z}
 *         <td>Time zone
 *         <td><a href="#rfc822timezone">RFC 822 time zone</a>
 *         <td>{@code -0800}
 * </tbody>
 * </table>
 */
@Getter
@RequiredArgsConstructor
@ToString
public enum Time {
  DEFAULT("E, d. MMM YYYY HH:mm");

  private final String format;

  public Date of(String date) {
    try {
      return new SimpleDateFormat(this.format).parse(date);
    } catch (ParseException ignored) { }
    return null;
  }

  public String now() {
    return this.of(new Date());
  }

  public String of(Date date) {
    return new SimpleDateFormat(this.format).format(date);
  }

}
