package de.zahrie.trues.api.discord.util.cmd.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Lara on 11.02.2023 for TRUEbot
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Embed {

  String value() default "";

  String description() default "keine Infos";

  DBQuery[] queries();

}
