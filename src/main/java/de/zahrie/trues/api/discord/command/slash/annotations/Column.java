package de.zahrie.trues.api.discord.command.slash.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

  String value();

  boolean left() default true;

  boolean inline() default true;

  int round() default 0;

  int maxLength() default Integer.MAX_VALUE;

  boolean withPrevious() default false;

}
