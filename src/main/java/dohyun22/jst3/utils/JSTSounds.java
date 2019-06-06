package dohyun22.jst3.utils;

import java.util.ArrayList;

import javax.annotation.ParametersAreNonnullByDefault;

import dohyun22.jst3.JustServerTweak;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

@ParametersAreNonnullByDefault
public class JSTSounds {
	public static SoundEvent TELEPORT;
	public static SoundEvent LASER;
	public static SoundEvent AIE;
	public static SoundEvent SHOCK;
	public static SoundEvent SHOCK2;
	public static SoundEvent WRENCH;
	public static SoundEvent BOOM;
	public static SoundEvent DOWNLINE;
	public static SoundEvent UPLINE;
	public static SoundEvent LEVEL;
	public static SoundEvent BONUS;
	public static SoundEvent SCAN;
	public static SoundEvent BICYCLE;
	public static SoundEvent GEIGER;
	public static SoundEvent DINGDONG;
	public static SoundEvent SWITCH;
	public static SoundEvent SWITCH2;
	public static SoundEvent FORCEFIELD;
	public static SoundEvent TRANSFORMER;
	public static SoundEvent MICRO;
	public static SoundEvent EJECT;
	public static SoundEvent PUMP;
	public static SoundEvent INTERRUPT;
	public static SoundEvent FLAME;
	public static SoundEvent ENGINE;
	
	private static SoundEvent registerSound(String name) {
		ResourceLocation rl = new ResourceLocation(JustServerTweak.MODID, name);
	    SoundEvent ev = new SoundEvent(rl);
	    ForgeRegistries.SOUND_EVENTS.register(ev.setRegistryName(rl));
	    return ev;
	}
	  
	public static void init() {
		TELEPORT = registerSound("teleport");
		LASER = registerSound("laser");
		AIE = registerSound("aie");
		SHOCK = registerSound("shock");
		SHOCK2 = registerSound("shock2");
		WRENCH = registerSound("wrench");
		BOOM = registerSound("boom");
		DOWNLINE = registerSound("downline");
		UPLINE = registerSound("upline");
		LEVEL = registerSound("level");
		BONUS = registerSound("bonus");
		SCAN = registerSound("scan");
		BICYCLE = registerSound("bicycle");
		GEIGER = registerSound("geiger");
		DINGDONG = registerSound("dingdong");
		SWITCH = registerSound("switch");
		SWITCH2 = registerSound("switch2");
		FORCEFIELD = registerSound("forcefield");
		TRANSFORMER = registerSound("transformer");
		MICRO = registerSound("micro");
		EJECT = registerSound("eject");
		PUMP = registerSound("pump");
		INTERRUPT = registerSound("interrupt");
		FLAME = registerSound("flame");
		ENGINE = registerSound("engine");
	}
}
