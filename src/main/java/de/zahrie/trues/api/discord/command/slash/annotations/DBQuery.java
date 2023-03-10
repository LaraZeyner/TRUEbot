package de.zahrie.trues.api.discord.command.slash.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DBQuery {

  String query();

  Column[] columns();

  String[] params() default {};

  boolean enumerated() default false;

}
