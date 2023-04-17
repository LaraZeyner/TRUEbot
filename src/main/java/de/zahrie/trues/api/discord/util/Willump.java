package de.zahrie.trues.api.discord.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import de.zahrie.trues.api.discord.builder.leaderboard.LeaderboardHandler;
import de.zahrie.trues.api.discord.builder.modal.ModalHandler;
import de.zahrie.trues.api.discord.command.context.ContextHandler;
import de.zahrie.trues.api.discord.command.slash.SlashHandler;
import de.zahrie.trues.discord.event.EventRegisterer;
import de.zahrie.trues.discord.notify.NotificationManager;
import de.zahrie.trues.discord.scouting.teaminfo.TeamInfoManager;
import de.zahrie.trues.util.Connectable;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

@Getter
public class Willump implements Connectable {
  private JDA client;
  private Guild guild;

  public void connect() {
    this.client = new BotConfigurator().run();
    handleEvents();
    SlashHandler.handleCommands();
    ContextHandler.handleCommands();
    LeaderboardHandler.handleLeaderboards();
    try {
      client.awaitReady();
      this.guild = client.getGuildById(ConfigLoader.getGuildId());
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    System.out.println("DONE");
    // ScheduleManager.run();
    test();
    System.out.println("TEST COMPLETED");
  }

  private void test() {
    TeamInfoManager.loadAllData();
    if (!NotificationManager.getDay().equals(LocalDate.now())) NotificationManager.create();
    NotificationManager.sendNotifications();
  }

  @Override
  public void disconnect() {
    // no actions
  }

  private void handleEvents() {
    final var adapters = new ArrayList<>(List.of(new BotConfigurator(), new ContextHandler(), new ModalHandler(), new SlashHandler()));
    adapters.addAll(new EventRegisterer().register());
    client.addEventListener(adapters.toArray());
  }
}
