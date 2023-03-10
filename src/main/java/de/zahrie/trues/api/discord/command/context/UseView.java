package de.zahrie.trues.api.discord.command.context;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface UseView {
  String value();
}
