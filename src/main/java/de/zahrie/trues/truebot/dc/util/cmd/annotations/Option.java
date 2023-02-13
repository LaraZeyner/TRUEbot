package de.zahrie.trues.truebot.dc.util.cmd.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import net.dv8tion.jda.api.interactions.commands.OptionType;

/**
 * Created by Lara on 11.02.2023 for TRUEbot
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface Option {

  String name();

  String description() default "keine Beschreibung";

  boolean required() default true;

  OptionType type() default OptionType.STRING;

  String[] choices() default {};

  String completion() default "";

}
