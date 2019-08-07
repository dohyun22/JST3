package dohyun22.jst3.items;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.items.behaviours.*;
import dohyun22.jst3.items.behaviours.IB_GenericTool.EnumToolType;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fluids.FluidUtil;

public class JSTItems {
	public static ItemMetaBase item1;
	public static Item mask;

	public static void preinit() {
		item1 = new ItemJST1();
		mask = new ItemMask();
		ForgeRegistries.ITEMS.register(item1);
		ForgeRegistries.ITEMS.register(mask);
		
		registerMetaItems();
	}

	private static void registerMetaItems() {
		//Crafting Materials
		boolean[] fl = new boolean[] {Loader.isModLoaded("advancedrocketry"), JSTCfg.customMat};
		Object[] tmp = new Object[] {new IB_AlkaliMetal()};
    	Object[] obj = {
    			"gashyd", "ti_ingot", "ti_dust", "ti_plate", "elite_circuit", "master_circuit", "shard1", "shard2", "shard3", "shard4",
    			"shard5", "admin_core", "just_coin", "wireless_set", "ir_plate", "uu_1", "ruby", "peridot", "sapphire", new Object[] {"garilc", new IB_Food(2, 2, 32, false, false)},
    			"bedrock_shard", "bedrock_dust", "airc_circuit", "airc_engine", "neut_ingot", "ra_ingot", "ba_ingot", "nikolite", "hadv_circuit", "ir_ingot",
    			"ir_dust", "zn_ingot", "zn_dust", "si_ingot", "ra_dust", "ba_dust", "brass_ingot", "brass_dust", "nq_ingot", "nq_dust",
    			"ub_ingot", "ub_dust", "ub_plate", "zn_plate", "ra_plate", "ba_plate", "comp_mtr_1", "comp_mtr_2", "comp_mtr_3", "q_nugget",
    			"q_mtr", "super_circuit", "hyper_circuit", "si_plate", "brass_plate", "nq_plate", "rubydust", "peridotdust", "sapphiredust", "si_dust",
    			"nq_ref", "rad_dust", "scr_fe", "sfe_dust", "cr_ingot", "cr_dust", "cr_plate", new Object[] {"li_dust", tmp[0]}, "ialloy", "ialloy_2",
    			"al_ingot", "al_dust", "al_plate", "w_ingot", "w_dust", "w_plate", "liph", "pblue", "es_ingot", "es_dust",
    			"es_plate", "insp", "p_ecap", "p_mosfet", "p_igbt", "p_tr", "circ", "circ_i", "circ_a", "re_ingot",
    			"re_dust", "re_plate", null, null, null, "nb_ingot", "nb_dust", "nb_plate", "fuel", "fuel2",
    			"th", "fuel3", "qcrystal", "ncrystal", "bx_dust", "pla", "pla_adv", "salt", new Object[] {"na_dust", tmp[0]}, "c_dust",
    			null, "mt1", "mt2", "mt3", "mt4", "mt5", "mt6", "mt7", "mt8", null,
    			null, "se1", "se2", "se3", "se4", "se5", "se6", "se7", "se8", null,
    			null, "re1", "re2", "re3", "re4", "re5", "re6", "re7", "re8", null,
    			null, null, null, null, null, null, null, null, null, null, //140~149: Reserved for tiered part
    			"cnt", new Object[] {"oilberry", new IB_Food(6, 10, 32, false, false, new PotionEffect(MobEffects.NAUSEA, 200))}, "vtube", "inf_circuit", ac("arsolar", fl[0]), null, null, "est_ingot", "est_dust", "est_plate",
    			"mold_cable", "mold_plate", "mold_rod", null, null, null, null, null, null, null, //160~169: Reserved for molds
    			ac("cu_ingot", fl[1]), ac("cu_dust", fl[1]), ac("cu_plate", fl[1]), ac("sn_ingot", fl[1]), ac("sn_dust", fl[1]), ac("sn_plate", fl[1]), ac("pb_ingot", fl[1]), ac("pb_dust", fl[1]), ac("pb_plate", fl[1]), null,
    			null, null, null, "solder_ingot", "solder_dust", "solder_wire", null, null, null, null, 
    			"circ_board"
    	};

		for (int n = 0; n < obj.length; n++) {
			Object o = obj[n];
			if (o instanceof String && !((String)o).isEmpty())
				item1.registerMetaItem(n, (String)o);
			else if (o instanceof Object[])
				item1.registerMetaItem(n, (String)((Object[])o)[0], (ItemBehaviour)((Object[])o)[1]);
		}
		
		//Fluid Containers #9000
		for (IB_FluidCan.CanType c : IB_FluidCan.CanType.values())
			item1.registerMetaItem(IB_FluidCan.startID + c.ordinal(), "can_" + c.name().toLowerCase(), new IB_FluidCan(c));
		
		item1.registerMetaItem(9999, "fdisp", new IB_FluidDisplay());
		
		//Tools and special items #10000
		item1.registerMetaItem(10000, "tool_uwrench", new IB_Wrench(1024));
		item1.registerMetaItem(10001, "tool_athame", new IB_Athame());
		item1.registerMetaItem(10002, "tool_entitydel", new IB_EntityRemover());
		item1.registerMetaItem(10003, "tool_ruler", new IB_Ruler());
		item1.registerMetaItem(10004, "tool_jstsword", new IB_JSTSword());
		item1.registerMetaItem(10005, "nq_cell", new IB_Damageable(1000000));
		item1.registerMetaItem(10006, "tool_neutdrill", new IB_NeutroniumDrill());
		item1.registerMetaItem(10007, "tool_laser", new IB_Laser());
		item1.registerMetaItem(10008, "tool_dscanner", new IB_Scanner());
		item1.registerMetaItem(10009, "tool_fueler", new IB_Fueler());
		item1.registerMetaItem(10010, "tool_dsd", new IB_DSD());
		item1.registerMetaItem(10011, "tool_trowel", new IB_Trowel());
		item1.registerMetaItem(10012, "tool_mfg", new IB_CropTool());
		item1.registerMetaItem(10013, "tool_sd", new IB_Screwdriver());
		item1.registerMetaItem(10014, "tool_sde", new IB_ScrewdriverE());
		item1.registerMetaItem(10015, "tool_eligter", new IB_ELighter());
		item1.registerMetaItem(10016, "tool_fdst", new IB_DustMeter());
		item1.registerMetaItem(10017, "tool_dustgen", new IB_DustGen());
		item1.registerMetaItem(10018, "tool_fdstadv", new IB_DustMeterAdv());
		item1.registerMetaItem(10019, "tool_ft", new IB_Flamethrower());
		item1.registerMetaItem(10020, "tool_ion", new IB_IonCannon());
		if (Loader.isModLoaded("toughasnails")) {
		item1.registerMetaItem(10021, "tool_wpuri", new IB_WaterPurifier());
		item1.registerMetaItem(10022, "tool_aircon", new IB_AirConditioner());}
		item1.registerMetaItem(10030, "tool_ecart", new IB_ECartControl());
		item1.registerMetaItem(10031, "ecart", new IB_ECart());
		item1.registerMetaItem(10040, "tool_mts", new IB_MTS());
		item1.registerMetaItem(10041, "tool_meter", new IB_Meter());
		item1.registerMetaItem(10042, "car_f", new IB_EntityEgg(JustServerTweak.MODID + ":cardesl", "jst.tooltip.cardesl"));
		item1.registerMetaItem(10043, "car_e", new IB_EntityEgg(JustServerTweak.MODID + ":carelec", "jst.tooltip.carelec"));
		
		item1.registerMetaItem(10050, "blueprint_write", new IB_BluePrint());
		
		item1.registerMetaItem(10100, "tool_rby_sw", new IB_GenericTool(500, EnumToolType.SWORD, 7.0F, 2, 8.0F, 10));
		item1.registerMetaItem(10101, "tool_rby_sh", new IB_GenericTool(500, EnumToolType.SHOVEL, 5.5F, 2, 8.0F, 10));
		item1.registerMetaItem(10102, "tool_rby_pi", new IB_GenericTool(500, EnumToolType.PICK, 5.0F, 2, 8.0F, 10));
		item1.registerMetaItem(10103, "tool_rby_ax", new IB_GenericTool(500, EnumToolType.AXE, 9.0F, 2, 8.0F, 10));
		item1.registerMetaItem(10104, "tool_rby_ho", new IB_GenericTool(500, EnumToolType.HOE, 0.0F, 2, 8.0F, 10));
		item1.registerMetaItem(10105, "tool_rby_si", new IB_GenericTool(500, EnumToolType.SICKLE, 6.0F, 2, 8.0F, 10));

		item1.registerMetaItem(10106, "tool_olv_sw", new IB_GenericTool(500, EnumToolType.SWORD, 7.0F, 2, 8.0F, 10));
		item1.registerMetaItem(10107, "tool_olv_sh", new IB_GenericTool(500, EnumToolType.SHOVEL, 5.5F, 2, 8.0F, 10));
		item1.registerMetaItem(10108, "tool_olv_pi", new IB_GenericTool(500, EnumToolType.PICK, 5.0F, 2, 8.0F, 10));
		item1.registerMetaItem(10109, "tool_olv_ax", new IB_GenericTool(500, EnumToolType.AXE, 9.0F, 2, 8.0F, 10));
		item1.registerMetaItem(10110, "tool_olv_ho", new IB_GenericTool(500, EnumToolType.HOE, 0.0F, 2, 8.0F, 10));
		item1.registerMetaItem(10111, "tool_olv_si", new IB_GenericTool(500, EnumToolType.SICKLE, 6.0F, 2, 8.0F, 10));

		item1.registerMetaItem(10112, "tool_sph_sw", new IB_GenericTool(500, EnumToolType.SWORD, 7.0F, 2, 8.0F, 10));
		item1.registerMetaItem(10113, "tool_sph_sh", new IB_GenericTool(500, EnumToolType.SHOVEL, 5.5F, 2, 8.0F, 10));
		item1.registerMetaItem(10114, "tool_sph_pi", new IB_GenericTool(500, EnumToolType.PICK, 5.0F, 2, 8.0F, 10));
		item1.registerMetaItem(10115, "tool_sph_ax", new IB_GenericTool(500, EnumToolType.AXE, 9.0F, 2, 8.0F, 10));
		item1.registerMetaItem(10116, "tool_sph_ho", new IB_GenericTool(500, EnumToolType.HOE, 0.0F, 2, 8.0F, 10));
		item1.registerMetaItem(10117, "tool_sph_si", new IB_GenericTool(500, EnumToolType.SICKLE, 6.0F, 2, 8.0F, 10));

		//Batteries #12000
		item1.registerMetaItem(12000, "bat_rs", new IB_Battery(12000L, 1));
		item1.registerMetaItem(12001, "bat_rs2", new IB_Battery(48000L, 2));
		item1.registerMetaItem(12002, "bat_nk", new IB_Battery(25000L, 1));
		item1.registerMetaItem(12003, "bat_nk2", new IB_Battery(100000L, 2));
		item1.registerMetaItem(12004, "bat_pb", new IB_Battery(100000L, 1, 1.5F, 0));
		item1.registerMetaItem(12005, "bat_pb2", new IB_Battery(400000L, 2, 1.5F, 0));
		item1.registerMetaItem(12006, "bat_pb3", new IB_Battery(1600000L, 3, 1.5F, 0));
		item1.registerMetaItem(12007, "bat_ni", new IB_Battery(120000L, 1, 8.0F, 0));
		item1.registerMetaItem(12008, "bat_ni2", new IB_Battery(480000L, 2, 8.0F, 0));
		item1.registerMetaItem(12009, "bat_ni3", new IB_Battery(1920000L, 3, 8.0F, 0));
		item1.registerMetaItem(12010, "bat_li", new IB_Battery(150000L, 1, 2.0F, 0));
		item1.registerMetaItem(12011, "bat_li2", new IB_Battery(600000L, 2, 2.0F, 0));
		item1.registerMetaItem(12012, "bat_li3", new IB_Battery(2400000L, 3, 2.0F, 0));
		item1.registerMetaItem(12013, "bat_lp", new IB_Battery(200000L, 1, 4.0F, 0));
		item1.registerMetaItem(12014, "bat_lp2", new IB_Battery(800000L, 2, 4.0F, 0));
		item1.registerMetaItem(12015, "bat_lp3", new IB_Battery(3200000L, 3, 4.0F, 0));
		item1.registerMetaItem(12016, "bat_lp4", new IB_Battery(12800000L, 4, 4.0F, 0));
		item1.registerMetaItem(12017, "bat_n", new IB_Battery(25000000L, 4, 2.0F, 0));
		item1.registerMetaItem(12018, "bat_n2", new IB_Battery(100000000L, 5, 2.0F, 0));
		item1.registerMetaItem(12019, "bat_n3", new IB_Battery(400000000L, 6, 2.0F, 0));
		item1.registerMetaItem(12020, "bat_s", new IB_Battery(1000000000000L, 7, 4.0F, 0));
		item1.registerMetaItem(12021, "bat_s2", new IB_Battery(4000000000000L, 8, 4.0F, 0));
		item1.registerMetaItem(12022, "bat_max", new IB_Battery(Long.MAX_VALUE, 9));
		item1.registerMetaItem(12023, "bat_cr", new IB_Battery(15000L, 1, 1));
		item1.registerMetaItem(12024, "bat_cr2", new IB_Battery(60000L, 2, 1));
		item1.registerMetaItem(12025, "bat_sn", new IB_Battery(50000L, 1, 1));
		item1.registerMetaItem(12026, "bat_sn2", new IB_Battery(200000L, 2, 1));
		item1.registerMetaItem(12027, "bat_na", new IB_Battery(100000L, 1));
		item1.registerMetaItem(12028, "bat_na2", new IB_Battery(400000L, 2));
		item1.registerMetaItem(12029, "bat_na3", new IB_Battery(1600000L, 3));
		item1.registerMetaItem(12030, "sb_rtg", new IB_AdvBattery(0));
		item1.registerMetaItem(12031, "sb_fcell", new IB_AdvBattery(1));
		item1.registerMetaItem(12040, "chgr1", new IB_Charger(100000, 1, false));
		item1.registerMetaItem(12041, "chgr2", new IB_Charger(600000, 2, false));
		item1.registerMetaItem(12042, "chgr3", new IB_Charger(2400000, 3, false));
		item1.registerMetaItem(12043, "chgr4", new IB_Charger(25000000, 4, false));
		item1.registerMetaItem(12044, "chgr5", new IB_Charger(100000000, 5, false));
		item1.registerMetaItem(12050, "chgs1", new IB_Charger(100000, 1, true));
		item1.registerMetaItem(12051, "chgs2", new IB_Charger(600000, 2, true));
		item1.registerMetaItem(12052, "chgs3", new IB_Charger(2400000, 3, true));
		item1.registerMetaItem(12053, "chgs4", new IB_Charger(25000000, 4, true));
		
		item1.addFuelValue(98, 25600);
		item1.addFuelValue(99, 102400);
		item1.addFuelValue(101, 409600);
		
		if (JSTUtils.isClient()) {
			registerItemModel(item1, "metaitems/");
			registerItemModel(mask, "");
		}
	}

	@SideOnly(Side.CLIENT)
	private static void registerItemModel(Item b, String f) {
		ResourceLocation l = b.getRegistryName();
		if (b instanceof ItemMetaBase)
			for (Integer i : ((ItemMetaBase)b).names.keySet()) {
				if (i == null) continue;
				try {
					String s2 = ((ItemMetaBase)b).names.get(i);
					ModelLoader.setCustomModelResourceLocation(b, i, new ModelResourceLocation(l.getResourceDomain() + ":" + f + s2, "inventory"));
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		else
			try {
				ModelLoader.setCustomModelResourceLocation(b, 0, new ModelResourceLocation(l.getResourceDomain() + ":" + f + l.getResourcePath()));
			} catch (Throwable t) {
				t.printStackTrace();
			}
	}

	private static Object ac(Object o, boolean b) {return b ? o : null;}
}
