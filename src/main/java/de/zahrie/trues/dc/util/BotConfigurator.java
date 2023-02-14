package de.zahrie.trues.dc.util;

import de.zahrie.trues.util.io.cfg.JSON;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Lara on 10.02.2023 for TRUEbot
 */
public class BotConfigurator extends ListenerAdapter {
  private JDABuilder builder;

  public JDA run() {
    final var json = JSON.fromFile("connect.json");
    final var apiKey = json.getString("discord");
    this.builder = JDABuilder.createDefault(apiKey);
    return this.configure();
  }

  private JDA configure() {
    this.builder.setActivity(ConfigLoader.getActivity());
    this.builder.setStatus(ConfigLoader.getStatus());
    final JDA jda = this.builder.build();
    jda.addEventListener(this);
    return jda;
  }

  @Override
  public void onReady(@NotNull ReadyEvent event) {
    System.out.println("Nunu ist bereit!");
  }
}
