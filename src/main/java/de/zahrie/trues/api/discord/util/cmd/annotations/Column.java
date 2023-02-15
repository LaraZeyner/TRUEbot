package de.zahrie.trues.api.discord.util.cmd.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Lara on 11.02.2023 for TRUEbot
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

  String value();

  boolean left() default true;

  boolean inline() default true;

  int round() default 0;

  int maxLength() default Integer.MAX_VALUE;

  boolean withPrevious() default false;

}
