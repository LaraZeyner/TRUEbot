package de.zahrie.trues.truebot.dc.util;

import de.zahrie.trues.truebot.util.io.cfg.JSON;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

/**
 * Created by Lara on 10.02.2023 for TRUEbot
 */
public final class ConfigLoader {

  public static OnlineStatus getStatus() {
    final var json = JSON.fromFile("config.json");
    final var bot = json.getJSONObject("bot");
    final var status = bot.getString("status");
    return OnlineStatus.fromKey(status);
  }

  public static Activity getActivity() {
    final var json = JSON.fromFile("config.json");
    final var bot = json.getJSONObject("bot");
    final var activity = bot.getJSONObject("activity");
    return Activity.of(getActivityType(activity), activity.getString("text"));
  }

  @NotNull
  private static Activity.ActivityType getActivityType(JSONObject activity) {
    final var activityType = activity.getString("type");
    return Activity.ActivityType.valueOf(activityType);
  }

}
