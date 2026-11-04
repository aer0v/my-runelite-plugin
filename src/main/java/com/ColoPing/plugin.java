package com.ColoPing;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

/**
 * Plays a sound a set number of game ticks after the player clicks
 * "Continue" on the Fortis Colosseum wave screen, as a cue to run
 * behind a pillar before the next wave spawns.
 */
@Slf4j
@PluginDescriptor(
	name = "Colosseum Wave Spawn Ping",
	description = "Pings a set number of ticks after you click Continue on the Colosseum wave screen",
	tags = {"colosseum", "fortis", "tick", "ping", "pillar"}
)
public class plugin extends Plugin
{
	// Config group name; must match @ConfigGroup in config.java.
	static final String CONFIG_GROUP = "coloping";

	// Map region of the Fortis Colosseum arena (real-world coordinates).
	private static final int COLOSSEUM_REGION = 7216;

	// Chat-dialogue interfaces that also show a "Continue" option.
	// Clicks on these are ignored so talking to NPCs doesn't start the countdown.
	private static final Set<Integer> DIALOG_GROUPS = ImmutableSet.of(193, 217, 219, 229, 231, 633);

	// Value used when no countdown is running.
	private static final int IDLE = -1;

	// The OSRS sound engine takes volume from 0 to 127.
	private static final int MAX_SOUND_VOLUME = 127;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private config config;

	// Game ticks left until the ping plays, or IDLE.
	private int ticksRemaining = IDLE;

	@Override
	protected void shutDown()
	{
		// Cancel any countdown in progress when the plugin is turned off.
		ticksRemaining = IDLE;
	}

	/**
	 * Starts the countdown when the player clicks "Continue" in the Colosseum.
	 */
	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		// Only care about "Continue" clicks.
		if (!"Continue".equalsIgnoreCase(event.getMenuOption()))
		{
			return;
		}

		// The top 16 bits of param1 hold the interface (widget group) that was clicked.
		int groupId = event.getParam1() >>> 16;
		int region = currentRegion();
		log.debug("Continue clicked: interface group {}, region {}", groupId, region);

		// Ignore NPC/player chat dialogues and anything outside the Colosseum.
		if (DIALOG_GROUPS.contains(groupId) || region != COLOSSEUM_REGION)
		{
			return;
		}

		ticksRemaining = config.tickDelay();
	}

	/**
	 * Runs once per game tick (every 0.6 seconds) and plays the ping
	 * when the countdown reaches zero.
	 */
	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (ticksRemaining == IDLE)
		{
			return;
		}

		if (--ticksRemaining == 0)
		{
			playPing();
			ticksRemaining = IDLE;
		}
	}

	/**
	 * Plays a preview when the user changes the sound or volume setting,
	 * so they can hear their choice straight away.
	 */
	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!CONFIG_GROUP.equals(event.getGroup()))
		{
			return;
		}

		String key = event.getKey();
		if (!"sound".equals(key) && !"volume".equals(key))
		{
			return;
		}

		// Settings events arrive on the UI thread; sounds must be played on the client thread.
		clientThread.invoke(() ->
		{
			if (client.getGameState() == GameState.LOGGED_IN)
			{
				playPing();
			}
		});
	}

	/**
	 * Plays the selected sound at the plugin's own volume,
	 * independent of the game's sound effect volume.
	 */
	private void playPing()
	{
		// Convert the 0-100% setting to the game's 0-127 range.
		int volume = Math.round(config.volume() * MAX_SOUND_VOLUME / 100f);
		if (volume > 0)
		{
			client.playSoundEffect(config.sound().getId(), volume);
		}
	}

	/**
	 * Returns the map region the player is standing in, or -1 if unknown.
	 */
	private int currentRegion()
	{
		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return -1;
		}

		// The Colosseum is an instance, so convert the instanced location
		// back to its real-world position before reading the region.
		return WorldPoint.fromLocalInstance(client, player.getLocalLocation()).getRegionID();
	}

	@Provides
	config provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(config.class);
	}
}
