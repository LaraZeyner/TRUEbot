package de.zahrie.trues.api.discord.util;

import de.zahrie.trues.util.io.cfg.JSON;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;

public class BotConfigurator extends ListenerAdapter {
  private JDABuilder builder;

  public JDA run() {
    final var json = JSON.read("connect.json");
    final var apiKey = json.getString("discord");
    this.builder = JDABuilder.create(apiKey, ConfigLoader.getIntents());
    return this.configure();
  }

  private JDA configure() {
    final JDA discordAPI = builder.setActivity(ConfigLoader.getActivity())
        .enableCache(CacheFlag.ACTIVITY, CacheFlag.EMOJI, CacheFlag.CLIENT_STATUS, CacheFlag.FORUM_TAGS, CacheFlag.VOICE_STATE, CacheFlag.STICKER, CacheFlag.MEMBER_OVERRIDES, CacheFlag.ROLE_TAGS, CacheFlag.FORUM_TAGS, CacheFlag.ONLINE_STATUS, CacheFlag.SCHEDULED_EVENTS)
        .setChunkingFilter(ChunkingFilter.ALL)
        .setMemberCachePolicy(MemberCachePolicy.ALL)
        .setStatus(ConfigLoader.getStatus())
        .build();
    discordAPI.addEventListener(this);
    return discordAPI;
  }

  @Override
  public void onReady(@NotNull ReadyEvent event) {
    System.out.println("Nunu ist bereit!");
  }
}
