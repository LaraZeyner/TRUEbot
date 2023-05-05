package de.zahrie.trues.api.discord.builder.queryCustomizer;

import java.util.Arrays;
import java.util.List;

import de.zahrie.trues.api.discord.command.slash.annotations.DBQuery;
import de.zahrie.trues.util.io.NamedQueries;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
public class CustomQuery {
  @RequiredArgsConstructor
  @Getter
  public enum Queries {
    ORGA_GAMES("", "", new CustomQuery(NamedQueries.ORGA_GAMES,
        List.of(new CustomColumn("Championname", 16), new CustomColumn("Picks", 8)))),
    TEAM_CHAMPIONS("", "", new CustomQuery(NamedQueries.ORGA_CHAMPIONS,
        List.of(new CustomColumn("Championname", 16), new CustomColumn("Picks", 8))));
    private final String title;
    private final String description;
    private final CustomQuery customQuery;
  }

  public static CustomQuery fromDBQuery(DBQuery dbQuery) {
    return new CustomQuery(dbQuery.query(),
        Arrays.stream(dbQuery.columns()).map(CustomColumn::fromDBColumn).toList(),
        Arrays.asList(dbQuery.params()), dbQuery.enumerated());
  }

  private final String query;
  private final List<CustomColumn> columns;
  private List<String> parameters;
  private final boolean enumerated;
  private final int frequencyInMinutes;

  public CustomQuery(String query, List<CustomColumn> columns) {
    this(query, columns, List.of());
  }

  public CustomQuery(String query, List<CustomColumn> columns, int frequencyInMinutes) {
    this(query, columns, List.of(), frequencyInMinutes);
  }

  public CustomQuery(String query, List<CustomColumn> columns, List<String> parameters) {
    this(query, columns, parameters, false);
  }

  public CustomQuery(String query, List<CustomColumn> columns, List<String> parameters, int frequencyInMinutes) {
    this(query, columns, parameters, false, frequencyInMinutes);
  }

  public CustomQuery(String query, List<CustomColumn> columns, List<String> parameters, boolean enumerated) {
    this(query, columns, parameters, enumerated, 60);
  }

  public CustomQuery(String query, List<CustomColumn> columns, List<String> parameters, boolean enumerated, int frequencyInMinutes) {
    this.query = query;
    this.columns = columns;
    this.parameters = parameters;
    this.enumerated = enumerated;
    this.frequencyInMinutes = frequencyInMinutes;
  }
}
