package dohyun22.jst3.client;

import java.util.LinkedList;
import java.util.List;

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
		
		List<String> ls = new LinkedList<String>();
		ls.add("basic_side");
		
		for (int n = 0; n < 10; n++) ls.add("t" + n + "_side");

		ls.add("st_in");
		ls.add( "st_out");
		ls.add("fl_in");
		ls.add("fl_out");
		
		ls.add("e1d");
		ls.add("e1din");
		ls.add("e1dout");
		ls.add("e3d");
		ls.add("e3din");
		ls.add("e3dout");
		
		ls.add("basic_solar");
		ls.add("basic_wind");
		ls.add("basic_water");
		ls.add("basic_teg");
		ls.add("adv_solar");
		
		ls.add("cable");
		ls.add("cable_sn");
		ls.add("cable_cu");
		ls.add("cable_au");
		ls.add("cable_hv");
		ls.add("wire_sn");
		ls.add("wire_cu");
		ls.add("wire_au");
		ls.add("wire_hv");
		ls.add("cable_gf");
		ls.add("cable_sw");
		ls.add("cable_dt");
		
		ls.add("cable_sc");
		ls.add("cable_pb");
		ls.add("cable_ag");
		ls.add("cable_ba");
		ls.add("cable_pt");
		ls.add("cable_nb");
		ls.add("cable_zn");
		ls.add("cable_bz");
		ls.add("cable_ir");
		ls.add("cable_re");
		ls.add("wire_pb");
		ls.add("wire_ag");
		ls.add("wire_ba");
		ls.add("wire_pt");
		ls.add("wire_nb");
		ls.add("wire_zn");
		ls.add("wire_bz");
		ls.add("wire_ir");
		ls.add("wire_re");
		ls.add("cable_n");
		
		ls.add("cable_dc");
		
		ls.add("furnacegen");
		ls.add("furnacegen_off");
		
		ls.add("nqgen");
		ls.add("nqgen_off");
		
		ls.add("dieselgen");
		ls.add("gasgen");
		ls.add("steamgen");
		ls.add("lavagen");
		ls.add("magicgen");
		ls.add("dieselgen_off");
		ls.add("gasgen_off");
		ls.add("steamgen_off");
		ls.add("lavagen_off");
		ls.add("magicgen_off");
		
		ls.add("watergen_side");
		ls.add("meter_side");
		ls.add("screen1");
		ls.add("screen2");
		ls.add("screen3");
		ls.add("alloy");
		ls.add("alloy_off");
		ls.add("solarthermal");
		ls.add("sfurnace");
		ls.add("sfurnace_off");
		ls.add("block_breaker");
		ls.add("block_breaker_off");
		ls.add("radio");
		ls.add("multigen");
		ls.add("multigen_off");
		ls.add("multigen_turb");
		ls.add("multigen_lava");
		ls.add("multigen_lava_off");
		ls.add("firebox");
		ls.add("firebox_off");
		ls.add("vent");
		ls.add("hvsign");
		ls.add("crop_led");
		ls.add("crop_led_off");
		ls.add("cmatron");
		ls.add("block_placer");
		ls.add("block_placer_on");
		ls.add("inductor");
		ls.add("hr_door");
		ls.add("waterpurifier");
		ls.add("filter");
		ls.add("pump");
		ls.add("miner");
		ls.add("creativegen");
		ls.add("drum");
		
		ls.add("bgbox");
		ls.add("sgbox");
		
		ls.add("ealloy");
		ls.add("ealloy_off");
		ls.add("assembler");
		ls.add("assembler_off");
		ls.add("separator");
		ls.add("separator_off");
		ls.add("chemmixer");
		ls.add("chemmixer_off");
		ls.add("disassembler");
		ls.add("disassembler_off");
		ls.add("press");
		ls.add("press_off");
		ls.add("efurnace");
		ls.add("efurnace_off");
		ls.add("upulv");
		ls.add("upulv_off");
		ls.add("crystal");
		ls.add("crystal_off");
		ls.add("recycler");
		ls.add("recycler_off");
		//ls.add("fl_recycler");
		//ls.add("fl_recycler_off");
		ls.add("circuit_research");
		ls.add("circuit_builder");
		ls.add("circuit_builder_off");
		ls.add("liquifier");
		ls.add("liquifier_off");
		
		ls.add("gen");
		ls.add("heatres");
		ls.add("csg_b");
		ls.add("csg_r");
		ls.add("csg_a");
		ls.add("fr_par");
		ls.add("fr1");
		ls.add("fr2");
		ls.add("fr3");
		ls.add("fr4");
		ls.add("coil1");
		ls.add("coil2");
		ls.add("coil3");
		ls.add("coil4");
		ls.add("coil5");
		ls.add("coil6");
		ls.add("coil7");
		ls.add("coil8");
		ls.add("coil_sc");
		ls.add("frame");
		
		ls.add("gti_side");
		ls.add("gti_side_off");
		ls.add("dummy");
		ls.add("comp");
		ls.add("heater");
		ls.add("besu");
		ls.add("fan");
		ls.add("fan_off");
		ls.add("fueler");
		ls.add("bioprocess");
		
		registerTex(tm, pf, ls);
		ls.clear();
		
		if (JSTCfg.ic2Loaded) {
			pf = "blocks/crop/";
			for (int n = 1; n <= 7; n++) {
				ls.add("bluewheat" + n);
				if (n <= 5) {
					ls.add("coalplant" + n);
					ls.add("diaplant" + n);
					ls.add("blazewood" + n);
					ls.add("emeria" + n);
				}
				if (n <= 4) {
					ls.add("thorwood" + n);
					ls.add("urania" + n);
					ls.add("irium" + n);
					ls.add("chorus" + n);
					ls.add("elecmushroom" + n);
					ls.add("lapoleaf" + n);
					ls.add("glowood" + n);
					ls.add("nickelum" + n);
					ls.add("alumia" + n);
					ls.add("platina" + n);
					ls.add("niobis" + n);
					ls.add("zincium" + n);
					ls.add("naquadium" + n);
					ls.add("hemp" + n);
					ls.add("oilplant" + n);
					ls.add("saltgrass" + n);
					ls.add("cactus" + n);
					ls.add("milkberry" + n);
					ls.add("graveplant" + n);
					ls.add("enderberry" + n);
					ls.add("slimeberry" + n);
					ls.add("rubium" + n);
					ls.add("peridotia" + n);
					ls.add("sapphiria" + n);
					ls.add("quartzint" + n);
					ls.add("wolframia" + n);
					ls.add("titania" + n);
					ls.add("sulfonia" + n);
					ls.add("goldenapple" + n);
				}
				if (n <= 3) {
					ls.add("oak" + n);
					ls.add("spruce" + n);
					ls.add("birch" + n);
					ls.add("jungle" + n);
					ls.add("acacia" + n);
					ls.add("roofedoak" + n);
					ls.add("rubwood" + n);
					ls.add("nitrowood" + n);
					ls.add("barley" + n);
					ls.add("rice" + n);
					ls.add("bamboo" + n);
					ls.add("chickenbush" + n);
					ls.add("meatgrass" + n);
					ls.add("ghastplant" + n);
					ls.add("leatherleaf" + n);
					ls.add("starflower" + n);
					ls.add("avataria" + n);
					ls.add("quarkium" + n);
					ls.add("spiderbush" + n);
					ls.add("oceanplant" + n);
					ls.add("fishingbush" + n);
					ls.add("apatium" + n);
					ls.add("shimmerleaf" + n);
					ls.add("cinderpearl" + n);
				}
			}
			registerTex(tm, pf, ls);
			ls.clear();
		}
	}
	
	@SideOnly(Side.CLIENT)
	private static void registerTex(TextureMap tm, String pf, List<String> locs) {
		for(String s : locs) registerTex(tm, pf + s);
	}
	
	@SideOnly(Side.CLIENT)
	private static TextureAtlasSprite registerTex(TextureMap tm, String loc) {
		return tm.registerSprite(new ResourceLocation(JustServerTweak.MODID, loc));
	}
}
