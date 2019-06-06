package dohyun22.jst3.client;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.tiles.MetaTileBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TextureLoader {
	public static void initTex(TextureMap tm) {
		String pf = "blocks/tileentity/";
		MetaTileBase.ErrorTex = registerTex(tm, "blocks/metablocks/error");

		registerTex(tm, pf + "basic_side");
		for (int n = 0; n < 10; n++) registerTex(tm, pf + "t" + n + "_side");

		registerTex(tm, pf + "st_in");
		registerTex(tm, pf + "st_out");
		registerTex(tm, pf + "fl_in");
		registerTex(tm, pf + "fl_out");
		
		registerTex(tm, pf + "e1d");
		registerTex(tm, pf + "e1din");
		registerTex(tm, pf + "e1dout");
		registerTex(tm, pf + "e3d");
		registerTex(tm, pf + "e3din");
		registerTex(tm, pf + "e3dout");
		
		registerTex(tm, pf + "basic_solar");
		registerTex(tm, pf + "basic_wind");
		registerTex(tm, pf + "basic_water");
		registerTex(tm, pf + "basic_teg");
		registerTex(tm, pf + "adv_solar");
		
		registerTex(tm, pf + "cable");
		registerTex(tm, pf + "cable_sn");
		registerTex(tm, pf + "cable_cu");
		registerTex(tm, pf + "cable_au");
		registerTex(tm, pf + "cable_hv");
		registerTex(tm, pf + "wire_sn");
		registerTex(tm, pf + "wire_cu");
		registerTex(tm, pf + "wire_au");
		registerTex(tm, pf + "wire_hv");
		registerTex(tm, pf + "cable_gf");
		registerTex(tm, pf + "cable_sw");
		registerTex(tm, pf + "cable_dt");
		
		registerTex(tm, pf + "cable_sc");
		registerTex(tm, pf + "cable_pb");
		registerTex(tm, pf + "cable_ag");
		registerTex(tm, pf + "cable_ba");
		registerTex(tm, pf + "cable_pt");
		registerTex(tm, pf + "cable_nb");
		registerTex(tm, pf + "cable_zn");
		registerTex(tm, pf + "cable_bz");
		registerTex(tm, pf + "cable_ir");
		registerTex(tm, pf + "cable_re");
		registerTex(tm, pf + "wire_pb");
		registerTex(tm, pf + "wire_ag");
		registerTex(tm, pf + "wire_ba");
		registerTex(tm, pf + "wire_pt");
		registerTex(tm, pf + "wire_nb");
		registerTex(tm, pf + "wire_zn");
		registerTex(tm, pf + "wire_bz");
		registerTex(tm, pf + "wire_ir");
		registerTex(tm, pf + "wire_re");
		registerTex(tm, pf + "cable_n");
		
		registerTex(tm, pf + "cable_dc");
		
		registerTex(tm, pf + "furnacegen");
		registerTex(tm, pf + "furnacegen_off");
		
		registerTex(tm, pf + "nqgen");
		registerTex(tm, pf + "nqgen_off");
		
		registerTex(tm, pf + "dieselgen");
		registerTex(tm, pf + "gasgen");
		registerTex(tm, pf + "steamgen");
		registerTex(tm, pf + "lavagen");
		registerTex(tm, pf + "magicgen");
		registerTex(tm, pf + "dieselgen_off");
		registerTex(tm, pf + "gasgen_off");
		registerTex(tm, pf + "steamgen_off");
		registerTex(tm, pf + "lavagen_off");
		registerTex(tm, pf + "magicgen_off");
		
		registerTex(tm, pf + "watergen_side");
		registerTex(tm, pf + "meter_side");
		registerTex(tm, pf + "screen1");
		registerTex(tm, pf + "screen2");
		registerTex(tm, pf + "screen3");
		registerTex(tm, pf + "alloy");
		registerTex(tm, pf + "alloy_off");
		registerTex(tm, pf + "solarthermal");
		registerTex(tm, pf + "sfurnace");
		registerTex(tm, pf + "sfurnace_off");
		registerTex(tm, pf + "block_breaker");
		registerTex(tm, pf + "block_breaker_off");
		registerTex(tm, pf + "radio");
		registerTex(tm, pf + "multigen");
		registerTex(tm, pf + "multigen_off");
		registerTex(tm, pf + "multigen_turb");
		registerTex(tm, pf + "multigen_lava");
		registerTex(tm, pf + "multigen_lava_off");
		registerTex(tm, pf + "firebox");
		registerTex(tm, pf + "firebox_off");
		registerTex(tm, pf + "vent");
		registerTex(tm, pf + "hvsign");
		registerTex(tm, pf + "crop_led");
		registerTex(tm, pf + "crop_led_off");
		registerTex(tm, pf + "cmatron");
		registerTex(tm, pf + "block_placer");
		registerTex(tm, pf + "block_placer_on");
		registerTex(tm, pf + "inductor");
		registerTex(tm, pf + "hr_door");
		registerTex(tm, pf + "waterpurifier");
		
		registerTex(tm, pf + "bgbox");
		registerTex(tm, pf + "sgbox");
		
		registerTex(tm, pf + "ealloy");
		registerTex(tm, pf + "ealloy_off");
		registerTex(tm, pf + "assembler");
		registerTex(tm, pf + "assembler_off");
		registerTex(tm, pf + "separator");
		registerTex(tm, pf + "separator_off");
		registerTex(tm, pf + "chemmixer");
		registerTex(tm, pf + "chemmixer_off");
		registerTex(tm, pf + "disassembler");
		registerTex(tm, pf + "disassembler_off");
		registerTex(tm, pf + "press");
		registerTex(tm, pf + "press_off");
		registerTex(tm, pf + "efurnace");
		registerTex(tm, pf + "efurnace_off");
		registerTex(tm, pf + "upulv");
		registerTex(tm, pf + "upulv_off");
		registerTex(tm, pf + "crystal");
		registerTex(tm, pf + "crystal_off");
		registerTex(tm, pf + "recycler");
		//registerTex(tm, pf + "fl_recycler");
		
		registerTex(tm, pf + "gen");
		registerTex(tm, pf + "heatres");
		registerTex(tm, pf + "csg_b");
		registerTex(tm, pf + "csg_r");
		registerTex(tm, pf + "csg_a");
		registerTex(tm, pf + "fr_par");
		registerTex(tm, pf + "fr1");
		registerTex(tm, pf + "fr2");
		registerTex(tm, pf + "fr3");
		registerTex(tm, pf + "fr4");
		registerTex(tm, pf + "coil1");
		registerTex(tm, pf + "coil2");
		registerTex(tm, pf + "coil3");
		registerTex(tm, pf + "coil4");
		registerTex(tm, pf + "coil5");
		registerTex(tm, pf + "coil6");
		registerTex(tm, pf + "coil7");
		registerTex(tm, pf + "coil8");
		registerTex(tm, pf + "coil_sc");
		registerTex(tm, pf + "frame");
		
		registerTex(tm, pf + "gti_side");
		registerTex(tm, pf + "gti_side_off");
		registerTex(tm, pf + "dummy");
		registerTex(tm, pf + "comp");
		registerTex(tm, pf + "heater");
		registerTex(tm, pf + "besu");
		registerTex(tm, pf + "fan");
		registerTex(tm, pf + "fan_off");
		registerTex(tm, pf + "fueler");
		
		pf = "blocks/fluids/";
		registerTex(tm, pf + "oil");
		registerTex(tm, pf + "fuel");
		registerTex(tm, pf + "nitrofuel");
		registerTex(tm, pf + "liquid_lng");
		registerTex(tm, pf + "liquid_lpg");
		registerTex(tm, pf + "gas.natural");
		registerTex(tm, pf + "hydrogen");
		registerTex(tm, pf + "deuterium");
		registerTex(tm, pf + "tritium");
		registerTex(tm, pf + "helium");
		registerTex(tm, pf + "helium3");
		registerTex(tm, pf + "lithium");
		registerTex(tm, pf + "carbon");
		registerTex(tm, pf + "nitrogen");
		registerTex(tm, pf + "oxygen");
		registerTex(tm, pf + "sodium");
		registerTex(tm, pf + "silicon");
		registerTex(tm, pf + "chlorine");
		registerTex(tm, pf + "mercury");
		registerTex(tm, pf + "steam");
		registerTex(tm, pf + "acid");
		registerTex(tm, pf + "air");
		
		if (JSTCfg.ic2Loaded) {
			pf = "blocks/crop/";
			for (int n = 1; n <= 7; n++) {
				registerTex(tm, pf + "bluewheat" + n);
				if (n <= 5) {
					registerTex(tm, pf + "coalplant" + n);
					registerTex(tm, pf + "diaplant" + n);
					registerTex(tm, pf + "blazewood" + n);
					registerTex(tm, pf + "emeria" + n);
				}
				if (n <= 4) {
					registerTex(tm, pf + "thorwood" + n);
					registerTex(tm, pf + "urania" + n);
					registerTex(tm, pf + "irium" + n);
					registerTex(tm, pf + "chorus" + n);
					registerTex(tm, pf + "elecmushroom" + n);
					registerTex(tm, pf + "lapoleaf" + n);
					registerTex(tm, pf + "glowood" + n);
					registerTex(tm, pf + "nickelum" + n);
					registerTex(tm, pf + "alumia" + n);
					registerTex(tm, pf + "platina" + n);
					registerTex(tm, pf + "niobis" + n);
					registerTex(tm, pf + "zincium" + n);
					registerTex(tm, pf + "naquadium" + n);
					registerTex(tm, pf + "hemp" + n);
					registerTex(tm, pf + "oilplant" + n);
				}
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	private static TextureAtlasSprite registerTex(TextureMap tm, String loc) {
		return tm.registerSprite(new ResourceLocation(JustServerTweak.MODID, loc));
	}
}
