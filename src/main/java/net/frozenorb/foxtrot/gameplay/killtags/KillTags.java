package net.frozenorb.foxtrot.gameplay.killtags;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@AllArgsConstructor
public enum KillTags {
    NONE("", " was slain by ", ""),
    RATIO("Ratio'd"," has been ratio'd by ", "The most phrase in mankind"),
    ROLLED("Rolled", " was rolled into a backwood by ", "Light up and pack 'em out"),
    BEAMED("Beamed", " was beamed by ", "Show your enemies that they've been scammed"),
    INHALED("Inhaled", " was inhaled by ", "Gone in one hit"),
    FARMED("Farmed", " got farmed by ", "Free elo"),
    TOASTED("Toasted", " got toasted by ", "Extra crispy"),
    SMOKED("Smoked"," was smoked by ", "Smoke their pack bro, just smoke it"),
    BLACKLISTED("Blacklisted", " was blacklisted by ", "Typical of Hxmilton"),
    PACKED("Packed", " was packed by ", "Raise the heat, show em who's boss"),
    COMBOED("Combo'd", " was combo'd by ", "Just be honest, did you actually?"),
    PISS("Piss", " was pissed on by ", "Claim territory, show players who's boss");

    @Getter String displayName;
    @Getter String deathTag;
    @Getter String description;

}
