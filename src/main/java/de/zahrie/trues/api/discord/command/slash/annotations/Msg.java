package de.zahrie.trues.api.discord.command.slash.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Msg {
  String value() default "keine Daten";
  String error() default "Dir fehlen die nötigen Rechte.";
  boolean ephemeral() default true;
  Embed[] embeds() default {};
}
