package dohyun22.jst3.utils;

import javax.annotation.ParametersAreNonnullByDefault;

import dohyun22.jst3.JustServerTweak;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@ParametersAreNonnullByDefault
public class JSTSounds {
	public static SoundEvent TELEPORT, LASER, AIE, SHOCK, SHOCK2, WRENCH, BOOM, DOWNLINE, UPLINE, LEVEL, BONUS, SCAN,
			BICYCLE, GEIGER, DINGDONG, SWITCH, SWITCH2, FORCEFIELD, MICRO, INTERRUPT, FLAME, ENGINE, OVERLOAD;

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
		MICRO = registerSound("micro");
		INTERRUPT = registerSound("interrupt");
		FLAME = registerSound("flame");
		ENGINE = registerSound("engine");
		OVERLOAD = registerSound("overload");
	}
}
