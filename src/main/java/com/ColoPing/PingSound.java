package com.ColoPing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.SoundEffectID;

/**
 * Built-in OSRS sound effects the user can pick for the ping.
 * Each entry pairs a readable name (shown in the settings dropdown)
 * with the game's sound effect ID.
 */
@Getter
@RequiredArgsConstructor
public enum PingSound
{
	GE_DING("Grand Exchange ding", SoundEffectID.GE_ADD_OFFER_DINGALING),
	BELL_DING("Bell ding", SoundEffectID.TOWN_CRIER_BELL_DING),
	BELL_DONG("Bell dong", SoundEffectID.TOWN_CRIER_BELL_DONG),
	ANVIL_TINK("Anvil tink", SoundEffectID.SMITH_ANVIL_TINK),
	COIN_TINKLE("Coin tinkle", SoundEffectID.GE_COIN_TINKLE),
	BOOP("UI boop", SoundEffectID.UI_BOOP),
	// The two sounds used by RuneLite's Metronome plugin.
	METRONOME_TICK("Metronome tick (plop)", SoundEffectID.GE_INCREMENT_PLOP),
	METRONOME_TOCK("Metronome tock (plop)", SoundEffectID.GE_DECREMENT_PLOP);

	private final String displayName;
	private final int id;

	// The settings dropdown uses toString() for each option's label.
	@Override
	public String toString()
	{
		return displayName;
	}
}
