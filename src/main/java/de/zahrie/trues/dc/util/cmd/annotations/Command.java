package de.zahrie.trues.dc.util.cmd.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Lara on 11.02.2023 for TRUEbot
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

  String name();

  String descripion() default "";

  Option[] options() default {};

}
