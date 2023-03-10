package de.zahrie.trues.api.discord;

import java.util.ArrayList;
import java.util.List;

import de.zahrie.trues.api.discord.builder.modal.ModalHandler;
import de.zahrie.trues.api.discord.command.context.ContextHandler;
import de.zahrie.trues.api.discord.command.slash.SlashHandler;
import de.zahrie.trues.api.discord.util.BotConfigurator;
import de.zahrie.trues.api.discord.util.ConfigLoader;
import de.zahrie.trues.discord.listener.EventRegisterer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class Willump {
  public static JDA client;
  public static Guild guild;

  protected static void run() {
    client = new BotConfigurator().run();
    guild = client.getGuildById(ConfigLoader.getGuildId());

    Willump.handleEvents();
    SlashHandler.handleCommands();
    ContextHandler.handleCommands();

    try {
      client.awaitReady();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private static void handleEvents() {
    final var adapters = new ArrayList<>(List.of(new BotConfigurator(), new ContextHandler(), new ModalHandler(), new SlashHandler()));
    adapters.addAll(new EventRegisterer().register());
    client.addEventListener(adapters.toArray());
  }
}
