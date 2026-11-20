package net.frozenorb.foxtrot.scoreboard;

import cc.fyre.proton.scoreboard.construct.TitleGetter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FoxtrotTitleGetter extends TitleGetter {

    private String title = "STRIVE";
    private String text = "STRIVE";
    private boolean domain = false;
    private int length = 1;
    private int flicker = -1;

    public FoxtrotTitleGetter() {
        Bukkit.getServer().getScheduler().runTaskTimer(Foxtrot.getInstance(), () -> {
            final StringBuilder title = new StringBuilder();
            boolean finish = this.length > this.text.length();

            if (this.flicker >= 0) {
                title.append((this.flicker % 2 == 0 ? ChatColor.WHITE : ChatColor.DARK_RED) + ChatColor.BOLD.toString() + this.text);

                if (this.flicker == 8) {
                    this.flicker = -1;

                    if (this.domain) {
                        this.text = "STRIVE";
                    } else {
                        this.text = "SOULS";
                    }

                    this.domain = !this.domain;
                } else {
                    this.flicker++;
                }
            } else {

                if (finish) {
                    title.append(ChatColor.RED + ChatColor.BOLD.toString() + this.text);
                    this.length = 1;
                    this.flicker = 0;
                } else {
                    title.append(ChatColor.RED + ChatColor.BOLD.toString() + this.text.substring(0, length - 1));
                    title.append(ChatColor.DARK_RED + ChatColor.BOLD.toString() + this.text.substring(length - 1));
                    this.length++;
                }

            }

            this.title = title.toString();
        }, 6, 6);
    }

    @Override
    public String getTitle(Player player) {
        return this.title;
    }
}
