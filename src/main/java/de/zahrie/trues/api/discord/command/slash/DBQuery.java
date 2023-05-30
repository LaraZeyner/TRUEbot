package de.zahrie.trues.api.discord.command.slash;

import java.util.List;

public record DBQuery(String title, String description, List<Column> columns, List<String> params, boolean enumerated) {
  public DBQuery(List<Column> columns) {
    this(null, null, columns, List.of(), false);
  }

  public DBQuery(List<Column> columns, boolean enumerated) {
    this(null, null, columns, List.of(), enumerated);
  }

  public DBQuery(List<Column> columns, List<String> params) {
    this(null, null, columns, params, false);
  }

  public DBQuery(List<Column> columns, List<String> params, boolean enumerated) {
    this(null, null, columns, params, enumerated);
  }

  public DBQuery(String title, String description, List<Column> columns) {
    this(title, description, columns, List.of(), false);
  }

  public DBQuery(String title, String description, List<Column> columns, boolean enumerated) {
    this(title, description, columns, List.of(), enumerated);
  }

  public DBQuery(String title, String description, List<Column> columns, List<String> params) {
    this(title, description, columns, params, false);
  }
}
