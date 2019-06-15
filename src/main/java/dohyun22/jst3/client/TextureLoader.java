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
		
		List<String> tList = new LinkedList<String>();
		tList.add("basic_side");
		
		for (int n = 0; n < 10; n++) tList.add("t" + n + "_side");

		tList.add("st_in");
		tList.add( "st_out");
		tList.add("fl_in");
		tList.add("fl_out");
		
		tList.add("e1d");
		tList.add("e1din");
		tList.add("e1dout");
		tList.add("e3d");
		tList.add("e3din");
		tList.add("e3dout");
		
		tList.add("basic_solar");
		tList.add("basic_wind");
		tList.add("basic_water");
		tList.add("basic_teg");
		tList.add("adv_solar");
		
		tList.add("cable");
		tList.add("cable_sn");
		tList.add("cable_cu");
		tList.add("cable_au");
		tList.add("cable_hv");
		tList.add("wire_sn");
		tList.add("wire_cu");
		tList.add("wire_au");
		tList.add("wire_hv");
		tList.add("cable_gf");
		tList.add("cable_sw");
		tList.add("cable_dt");
		
		tList.add("cable_sc");
		tList.add("cable_pb");
		tList.add("cable_ag");
		tList.add("cable_ba");
		tList.add("cable_pt");
		tList.add("cable_nb");
		tList.add("cable_zn");
		tList.add("cable_bz");
		tList.add("cable_ir");
		tList.add("cable_re");
		tList.add("wire_pb");
		tList.add("wire_ag");
		tList.add("wire_ba");
		tList.add("wire_pt");
		tList.add("wire_nb");
		tList.add("wire_zn");
		tList.add("wire_bz");
		tList.add("wire_ir");
		tList.add("wire_re");
		tList.add("cable_n");
		
		tList.add("cable_dc");
		
		tList.add("furnacegen");
		tList.add("furnacegen_off");
		
		tList.add("nqgen");
		tList.add("nqgen_off");
		
		tList.add("dieselgen");
		tList.add("gasgen");
		tList.add("steamgen");
		tList.add("lavagen");
		tList.add("magicgen");
		tList.add("dieselgen_off");
		tList.add("gasgen_off");
		tList.add("steamgen_off");
		tList.add("lavagen_off");
		tList.add("magicgen_off");
		
		tList.add("watergen_side");
		tList.add("meter_side");
		tList.add("screen1");
		tList.add("screen2");
		tList.add("screen3");
		tList.add("alloy");
		tList.add("alloy_off");
		tList.add("solarthermal");
		tList.add("sfurnace");
		tList.add("sfurnace_off");
		tList.add("block_breaker");
		tList.add("block_breaker_off");
		tList.add("radio");
		tList.add("multigen");
		tList.add("multigen_off");
		tList.add("multigen_turb");
		tList.add("multigen_lava");
		tList.add("multigen_lava_off");
		tList.add("firebox");
		tList.add("firebox_off");
		tList.add("vent");
		tList.add("hvsign");
		tList.add("crop_led");
		tList.add("crop_led_off");
		tList.add("cmatron");
		tList.add("block_placer");
		tList.add("block_placer_on");
		tList.add("inductor");
		tList.add("hr_door");
		tList.add("waterpurifier");
		
		tList.add("bgbox");
		tList.add("sgbox");
		
		tList.add("ealloy");
		tList.add("ealloy_off");
		tList.add("assembler");
		tList.add("assembler_off");
		tList.add("separator");
		tList.add("separator_off");
		tList.add("chemmixer");
		tList.add("chemmixer_off");
		tList.add("disassembler");
		tList.add("disassembler_off");
		tList.add("press");
		tList.add("press_off");
		tList.add("efurnace");
		tList.add("efurnace_off");
		tList.add("upulv");
		tList.add("upulv_off");
		tList.add("crystal");
		tList.add("crystal_off");
		tList.add("recycler");
		//tList.add("fl_recycler");
		
		tList.add("gen");
		tList.add("heatres");
		tList.add("csg_b");
		tList.add("csg_r");
		tList.add("csg_a");
		tList.add("fr_par");
		tList.add("fr1");
		tList.add("fr2");
		tList.add("fr3");
		tList.add("fr4");
		tList.add("coil1");
		tList.add("coil2");
		tList.add("coil3");
		tList.add("coil4");
		tList.add("coil5");
		tList.add("coil6");
		tList.add("coil7");
		tList.add("coil8");
		tList.add("coil_sc");
		tList.add("frame");
		
		tList.add("gti_side");
		tList.add("gti_side_off");
		tList.add("dummy");
		tList.add("comp");
		tList.add("heater");
		tList.add("besu");
		tList.add("fan");
		tList.add("fan_off");
		tList.add("fueler");
		
		registerTex(tm, pf, tList);
		tList.clear();
		
		pf = "blocks/fluids/";
		tList.add("oil");
		tList.add("fuel");
		tList.add("nitrofuel");
		tList.add("liquid_lng");
		tList.add("liquid_lpg");
		tList.add("gas.natural");
		tList.add("hydrogen");
		tList.add("deuterium");
		tList.add("tritium");
		tList.add("helium");
		tList.add("helium3");
		tList.add("lithium");
		tList.add("carbon");
		tList.add("nitrogen");
		tList.add("oxygen");
		tList.add("sodium");
		tList.add("silicon");
		tList.add("chlorine");
		tList.add("mercury");
		tList.add("steam");
		tList.add("acid");
		tList.add("air");
		registerTex(tm, pf, tList);
		tList.clear();
		
		if (JSTCfg.ic2Loaded) {
			pf = "blocks/crop/";
			for (int n = 1; n <= 7; n++) {
				tList.add("bluewheat" + n);
				if (n <= 5) {
					tList.add("coalplant" + n);
					tList.add("diaplant" + n);
					tList.add("blazewood" + n);
					tList.add("emeria" + n);
				}
				if (n <= 4) {
					tList.add("thorwood" + n);
					tList.add("urania" + n);
					tList.add("irium" + n);
					tList.add("chorus" + n);
					tList.add("elecmushroom" + n);
					tList.add("lapoleaf" + n);
					tList.add("glowood" + n);
					tList.add("nickelum" + n);
					tList.add("alumia" + n);
					tList.add("platina" + n);
					tList.add("niobis" + n);
					tList.add("zincium" + n);
					tList.add("naquadium" + n);
					tList.add("hemp" + n);
					tList.add("oilplant" + n);
				}
			}
			registerTex(tm, pf, tList);
			tList.clear();
		}
	}
	
	@SideOnly(Side.CLIENT)
	private static void registerTex(TextureMap tm, String pf, List<String> locs) {
		for(String s : locs) {
			registerTex(tm, pf + s);
		}
	}
	
	@SideOnly(Side.CLIENT)
	private static TextureAtlasSprite registerTex(TextureMap tm, String loc) {
		return tm.registerSprite(new ResourceLocation(JustServerTweak.MODID, loc));
	}
}
