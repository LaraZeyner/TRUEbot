package de.zahrie.trues.dc.util.cmd.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Lara on 12.02.2023 for TRUEbot
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DBQuery {

  String query();

  Column[] columns();

  String[] params() default {};

  boolean enumerated() default false;

}
