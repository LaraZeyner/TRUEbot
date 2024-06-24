<h1>TRUE-Bot</h1>
<h2>Channelverwaltung</h2>
<h3>limit:</h3>
<p>
  ~ limitiert aktuellen Voicechannel auf eine bestimmte Anzahl an Mitgliedern. Wird das 
  Argument nicht ausgefüllt, wird dieser auf zwei (2) Mitglieder beschränkt. Durch 
  Verlassen des Channels oder erneute Nutzung des Commands wird die Limitierung aufgehoben.
</p>
<h3>channelcreate/channeledit:</h3>
<p>
  ~ ermöglichen, auf Grundlage von gewissen Permissiontemplates, Channel zu erstellen 
  oder die Rechte auf dieses Template zu setzen. <br>
  Diese Templates können von den Admins in der Kategorie “Templates” bearbeitet werden. 
  Änderungen an den Template-Channeln führen nicht zu einer Änderung der existierenden 
  Channel. Diese Commands existieren, sodass Leader anderen Spielern ihres Teams die 
  Rechte nehmen können Channel zu bearbeiten (da ein neues Mitglied des Teams alle 
  Teamchannel nach Standardeinstellungen löschen könnte).
</p>
<h3>followme:</h3>
<p>
  ~ ermöglicht es, dass alle Mitglieder deines aktuellen Channels mit dir verschoben 
  werden. Dabei gibt es drei (3) Optionen: 
  <ul>
    <li>Alle: Alle Mitglieder des Channels </li>
    <li>Team: Alle Mitglieder des Channels, die auch Teil deines Teams sind</li>
    <li>Match: Alle Mitglieder des Channels, die sich im nächsten Matchlineup deines 
      Teams befinden</li>
  </ul>
</p>
<h2>Registrierung und Bewerbung</h2>
<h3>Profil anzeigen</h3>
<h3>bewerben/”Bewerbung erstellen”</h3>
<p>
  ~ ermöglicht das Erstellen einer Bewerbung. Durch Klicken auf einen anderen Nutzen kann 
  man eine Bewerbung für einen anderen Nutzer schreiben.
</p>
<h3>tryouts</h3>
<p>
  ~ ermöglicht eine Übersicht über alle aktiven Bewerber.
</p>
<h3>settings</h3>
<p>
  ~ ermöglicht das Einstellen verschiedener Dinge:
  <ul>
    <li>Benachrichtigung: wieviele Minuten der Nutzer vor dem Match benachrichtigt werden 
      soll. Wird der Wert auf -1 gesetzt. Kommt keine Benachrichtigung. Zudem kommt keine 
      Benachrichtigung, wenn der Nutzer sich zu diesem Zeitpunkt bereits im dafür 
      vorgesehenen Channel befindet.</li>
    <li>Geburtstag: selbsterklärend</li>
    <li>Riot Account: Verknüpfen des LoL-Accounts</li>
    <li>Ranked Tracking: Posten der Ranked-Achievements</li>
  </ul>
</p>
<h3>accept</h3>
<p>
  ~ nimmt eine Bewerbung an. Der Nutzer darf sich nun Teams als Tryout vorstellen.
</p>
<h3>add/custom</h3>
<p>
  ~ ermöglichen die Planung eines Vorstellungsgesprächs mit einem Admin. Bei custom kann 
  ein Gespräch geplant werden, ohne dass eine Bewerbung geschrieben wurde.
</p>
<h2>Teamplanung</h2>
<h3>training/scrim</h3>
<p>
  ~ ermöglicht die Planung eines Scrims oder anderen Trainings. Dabei wird ein Thread im 
  Scouting-Channel für Notizen, etc. erstellt, um diese leichter wiederzufinden. Bei 
  Scrims wird innerhalb von 2 Stunden ein Scouting der Gegner durchgeführt und 
  entsprechend angezeigt. Das nächste Match des Teams ist dann auf diesen Scrim 
  eingestellt.
</p>
<h3>Notifier</h3>
<p>
  ~ benachrichtigt den Nutzer vor einem Ereignis. Dieses ist über /settings einstellbar.
</p>
<h2>Teaminfos und Scouting</h2>
<h3>Lineup</h3>
<p>
  ~ ermöglicht das Bearbeiten des gegnerischen Lineups des nächsten Matches zum Scouten. 
  Dabei hat man die Möglichkeit einzelne Rollen anzugeben oder das Op.gg einzufügen. Die 
  Spieler müssen beim op.gg in der richtigen Reihenfolge sein. Optional kann man auch die 
  Matchid angeben, wenn man das Lineup für ein bestimmtes, und nicht das nächste, Match 
  ändern will.
</p>
<h3>Scheduling-Tool</h3>
<p>
  ~ dient der Planung der Termine für das Team. Dabei kann man im Teamchat seine 
  Anwesenheit eintragen. Tut man dies, überschreibt man alle zuvor eingetragenen 
  Anwesenheiten für diese Woche. Dabei ist es möglich Uhrzeit und Tag zu verwenden:
  <ul>
    <li>DD.MM.YYYY oder DD.MM. oder DD. oder <Wochentagkürzel> für den Tag</li>
    <li>HH:MM:SS oder HH:MM oder HHh oder HH Uhr für die Zeit</li>
  </ul>
  Diese werden pro Zeile kombiniert. z.B. “Mo Di Fr-So 12-22” bedeutet an den kommenden 
  Wochentagen Montag, Dienstag, Freitag, Samstag und Sonntag kann man von 12 bis 22 Uhr. <br>
  Davor muss man einen Ping des Nutzers setzen, für den die Eintragung gilt oder “<--” 
  wenn diese Eintragung für einen selber gilt. <br>
  In team-info wird euch dann angezeigt, wann jeder in der Woche kann. Wenn an dem Tag 
  der Spieler nicht kann, werden alle Spieler anderer Teams angezeigt, die am Tag 
  verfügbar sind.
</p>
<h3>Teamübersicht</h3>
<p>
  ~ findet ihr in team-info ganz oben. Diese zeigt wichtige Infos über Platzierung in 
  Prime League sowie TRUE-Cup an. Darunter findet ihr euer Lineup und wer bei euch Rechte 
  hat. Weiterhin findet ihr dort kommende Events wie Spiele, Trainings, etc. sowie alle 
  möglichen Trainingstage. Das funktioniert nur, wenn das Scheduling-Tool genutzt wird. 
  Unten findet ihr den Rahmenplan des Prime League Splits.
</p>
<h3>Divisionsübersicht</h3>
<p>
  ~ zeigt eure Prime League Gruppe mit aktueller Tabelle. Dahinter findet ihr die 
  prognostizierte Tabelle. Darunter findet ihr die Spiele mit Spielzeit und Ergebnis. Der 
  * bedeutet, dass das  Spiel noch nicht beendet wurde. Stattdessen wird die Prognose 
  angezeigt.
</p>
<h3>nächstes Match</h3>
<p>
  ~ zeigt euer Team mit Lineup sowie das Team des Gegners an. Wurde ein Lineup ausgewählt, 
  findet ihr das hier auch. Darunter ist der Matchlog sichtbar.
</p>
<h3>TRUE-Cup Infos</h3>
<p>
  ~ zeigt euch Rahmenplan, sowie eure Spiele im TRUE-Cup an.
</p>
<h3>Scouting-Übersicht</h3>
<p>
  Sobald ein neues Match erstellt wird, oder ihr ein Event für euer Team plant, wird ein 
  Thread erstellt. Ist es ein Scrim oder ein offizielles Match werden euch dort 
  Scouting-Informationen übermittelt. Dabei findet ihr oben das Lineup des Teams. Die 
  Spieler sind nach der gespielten Officials auf der Position sortiert. Danach wird auch 
  das Scouting-Lineup erstellt. Mit /lineup könnt ihr dieses überarbeiten. <br>
  Die Scouting-Übersicht zeigt euch Scouting-Stats für jede Position an. Oben findet ihr 
  den Spielernamen mit entsprechenden SoloQ-Informationen. Die Games-Anzahl ist die Menge 
  der gespielten Clash und Prime League Spiele im letzten Jahr. Darunter findet ihr Stats 
  aus Competitive Spielen. Darunter findet ihr eine Liste von Champions. Diese sind nach 
  Competitive-Presence sortiert. In der 2. Spalte seht ihr die Presence und die Anzahl der 
  Pick von dem Spieler. Anschließend findet ihr in Spalte 3 Anzahl Games und Winrate.
</p>
<h3>scout</h3>
<p>
  ~ ist ein Command, der genauere Informationen über eure Gegner gibt:
  <ul>
    <li>Übersicht - Siehe Scouting-Übersicht</li>
    <li>Lineup - Siehe Scouting-Übersicht</li>
    <li>Games</li>
    <li>Champions</li>
    <li>Matchups</li>
    <li>Schedule</li>
    <li>Matchhistory</li>
  </ul>
</p>
<h2>Teamverwaltung</h2>
<h3>create</h3>
<p>
</p>
<h3>link</h3>
<p>
</p>
<h3>“Rollen bearbeiten”</h3>
<p>
</p>
<h3>“Teammember bearbeiten”</h3>
<p>
</p>
<h2>Orgaverwaltung</h2>
<h3>follow</h3>
<p>
</p>
<h3>“Mitglied hinzufügen”</h3>
<p>
</p>
<h2>sonstiges</h2>
<h3>stats</h3>
<p>
</p>
<h3>wetten</h3>
<p>
</p>
