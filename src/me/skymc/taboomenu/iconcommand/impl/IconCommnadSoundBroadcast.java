package me.skymc.taboomenu.iconcommand.impl;

import me.skymc.taboomenu.iconcommand.AbstractIconCommand;
import me.skymc.taboomenu.sound.SoundPack;
import me.skymc.taboomenu.util.VersionUtils;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

/**
 * @Author sky
 * @Since 2018-06-05 20:20
 */
public class IconCommnadSoundBroadcast extends AbstractIconCommand {

    private SoundPack soundPack;

    public IconCommnadSoundBroadcast(String command) {
        super(command);
        soundPack = new SoundPack(command.toUpperCase().replace(" ", "_"));
    }

    @Override
    public void execute(Player player) {
        VersionUtils.getOnlinePlayers().forEach(soundPack::play);
    }

    @Override
    public String toString() {
        return MessageFormat.format("IconCommandSound'{'soundPack={0}, command=''{1}'''}'", soundPack, command);
    }
}
