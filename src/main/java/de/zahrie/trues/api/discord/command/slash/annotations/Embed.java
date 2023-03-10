package de.zahrie.trues.api.discord.command.slash.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Embed {

  String value() default "";

  String description() default "keine Infos";

  DBQuery[] queries();

}
