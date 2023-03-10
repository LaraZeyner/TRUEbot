package de.zahrie.trues.api.discord.builder.modal;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface View {
  String value();
}
