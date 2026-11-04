package com.ColoPing;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

/**
 * Settings shown in the plugin's configuration panel.
 */
@ConfigGroup(plugin.CONFIG_GROUP)
public interface config extends Config
{
	// Number of game ticks (0.6s each) to wait after clicking Continue.
	@Range(min = 1, max = 20)
	@Units(Units.TICKS)
	@ConfigItem(
		keyName = "tickDelay",
		name = "Tick delay",
		description = "How many game ticks after clicking Continue to play the ping (1 tick = 0.6s)",
		position = 1
	)
	default int tickDelay()
	{
		return 5;
	}

	// Which built-in game sound to play.
	@ConfigItem(
		keyName = "sound",
		name = "Sound",
		description = "Grand Exchange ding, Bell ding, Bell dong, Anvil tink, Coin tinkle, UI boop, Metronome tick, Metronome tock. Changing the sound or volume plays a preview.",
		position = 2
	)
	default PingSound sound()
	{
		return PingSound.GE_DING;
	}

	// Ping volume, separate from the game's own sound effect volume. 0 mutes it.
	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(
		keyName = "volume",
		name = "Volume",
		description = "Ping volume, separate from the game's sound effect volume",
		position = 3
	)
	default int volume()
	{
		return 75;
	}
}
