package de.zahrie.trues.api.discord.builder.leaderboard;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.zahrie.trues.util.io.cfg.JSON;
import org.json.JSONArray;
import org.json.JSONObject;

public class LeaderboardHandler {
  private static final Map<PublicLeaderboard, Calendar> leaderboards;

  static {
    final Calendar calendar = Calendar.getInstance();
    calendar.set(2000, Calendar.JANUARY, 1);
    leaderboards = load().stream().collect(HashMap::new, (m, v) -> m.put(v, calendar), HashMap::putAll);
  }

  private static List<PublicLeaderboard> load() {
    final JSONArray dataArray = JSON.fromFile("leaderboards.json").getJSONArray("data");
    return IntStream.range(0, dataArray.length()).mapToObj(dataArray::getJSONObject).map(PublicLeaderboard::fromJSON).collect(Collectors.toList());
  }

  public static void handleLeaderboards() {
    final Date date = new Date();
    leaderboards.forEach((leaderboard, last) -> {
      final int frequency = leaderboard.getCustomQuery().getCustomQuery().getFrequencyInMinutes();
      if (frequency == 0) return;
      if (date.getTime() - last.getTimeInMillis() >= (frequency - 1) * 60_000L) {
        leaderboard.updateData();
        leaderboards.replace(leaderboard, Calendar.getInstance());
      }
    });
  }

  public static void add(PublicLeaderboard leaderboard) {
    leaderboards.put(leaderboard, Calendar.getInstance());
    write();
  }

  private static void write() {
    final var leaderboardContent = new JSONObject();
    final var data = new JSONArray(leaderboards.keySet().stream().map(PublicLeaderboard::toJSON).toList());
    leaderboardContent.put("data", data);
    JSON.write("leaderboards.json", leaderboardContent.toString());
  }
}
