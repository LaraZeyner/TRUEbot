package de.zahrie.trues.api.community.application;

public enum TeamRole {
  /**
   * hilft für einen Tag aus
   */
  STANDIN,
  /**
   * Bewerbungsgespräch angenommen <br>
   * Wenn Tryout für Team dann ist dies die Auswahlrolle
   */
  TRYOUT,
  /**
   * Substitude oder wenn temporär Tryout für dieses Team
   */
  SUBSTITUDE,
  /**
   * Stammspieler (max. 5 pro Team)
   */
  MAIN,
  /**
   * Teil als Staffmember
   */
  ORGA,
  ORGA_TRYOUT
}
