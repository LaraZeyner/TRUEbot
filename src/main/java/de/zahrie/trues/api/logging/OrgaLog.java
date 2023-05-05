package de.zahrie.trues.api.logging;

import de.zahrie.trues.api.database.connector.Table;

@Table("orga_log")
public interface OrgaLog {
  String getDetails();
}
