package dohyun22.jst3.loader;

import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.device.*;
import dohyun22.jst3.tiles.device.MT_AdvChest.AdvChestType;
import dohyun22.jst3.tiles.machine.*;
import dohyun22.jst3.tiles.earlytech.*;
import dohyun22.jst3.tiles.energy.*;
import dohyun22.jst3.tiles.multiblock.*;
import dohyun22.jst3.tiles.noupdate.*;
import dohyun22.jst3.tiles.test.*;
import dohyun22.jst3.utils.JSTSounds;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MTELoader {
	public static void init() {
		//Negative values are not allowed.
		//#0 represents broken MetaTileEntity and should not be used.
		/* #1~10: Machine Casings */
		MetaTileBase.registerTE(1, new MetaTileCasing("t0_side"));
		MetaTileBase.registerTE(2, new MetaTileCasing("t1_side"));
		MetaTileBase.registerTE(3, new MetaTileCasing("t2_side"));
		MetaTileBase.registerTE(4, new MetaTileCasing("t3_side"));
		MetaTileBase.registerTE(5, new MetaTileCasing("t4_side"));
		MetaTileBase.registerTE(6, new MetaTileCasing("t5_side"));
		MetaTileBase.registerTE(7, new MetaTileCasing("t6_side"));
		MetaTileBase.registerTE(8, new MetaTileCasing("t7_side"));
		MetaTileBase.registerTE(9, new MetaTileCasing("t8_side"));
		MetaTileBase.registerTE(10, new MetaTileCasing("t9_side"));

		/* #11~20: MultiBlock Ports */
		MetaTileBase.registerTE(11, new MT_EnergyPort(false));
		MetaTileBase.registerTE(12, new MT_EnergyPort(true));
		MetaTileBase.registerTE(13, new MT_ItemPort(false));
		MetaTileBase.registerTE(14, new MT_ItemPort(true));
		MetaTileBase.registerTE(15, new MT_FluidPort(false));
		MetaTileBase.registerTE(16, new MT_FluidPort(true));

		/* #21~3999: Tiered Machines */
		MetaTileBase.registerTE(21, new MT_UESU(1, 50000L));
		MetaTileBase.registerTE(22, new MT_UESU(2, 400000L));
		MetaTileBase.registerTE(23, new MT_UESU(3, 3000000L));
		MetaTileBase.registerTE(24, new MT_UESU(4, 25000000L));
		MetaTileBase.registerTE(25, new MT_UESU(5, 160000000L));
		MetaTileBase.registerTE(26, new MT_UESU(6, 1000000000L));
		MetaTileBase.registerTE(27, new MT_UESU(7, 2000000000000L));
		
		MetaTileBase.registerTE(31, new MT_StirlingGen(1));
		MetaTileBase.registerTE(32, new MT_StirlingGen(2));
		MetaTileBase.registerTE(33, new MT_StirlingGen(3));
		
		MetaTileBase.registerTE(40, new MT_SolarGen(-1));
		MetaTileBase.registerTE(41, new MT_SolarGen(0));
		MetaTileBase.registerTE(42, new MT_SolarGen(1));
		MetaTileBase.registerTE(43, new MT_SolarGen(2));
		MetaTileBase.registerTE(44, new MT_SolarGen(3));
		MetaTileBase.registerTE(45, new MT_SolarGen(4));
		MetaTileBase.registerTE(46, new MT_SolarGen(5));
		MetaTileBase.registerTE(47, new MT_SolarGen(6));
		MetaTileBase.registerTE(48, new MT_SolarGen(7));
		MetaTileBase.registerTE(49, new MT_SolarGen(8));
		
		MetaTileBase.registerTE(51, new MT_NaquadahGen(4));
		MetaTileBase.registerTE(52, new MT_NaquadahGen(5));
		MetaTileBase.registerTE(53, new MT_NaquadahGen(6));
		
		MetaTileBase.registerTE(61, new MT_FluidGen(1, 0));
		MetaTileBase.registerTE(62, new MT_FluidGen(2, 0));
		MetaTileBase.registerTE(63, new MT_FluidGen(3, 0));
		
		MetaTileBase.registerTE(71, new MT_FluidGen(1, 1));
		MetaTileBase.registerTE(72, new MT_FluidGen(2, 1));
		MetaTileBase.registerTE(73, new MT_FluidGen(3, 1));
		
		MetaTileBase.registerTE(81, new MT_FluidGen(1, 2));
		MetaTileBase.registerTE(82, new MT_FluidGen(2, 2));
		MetaTileBase.registerTE(83, new MT_FluidGen(3, 2));
		
		MetaTileBase.registerTE(91, new MT_FluidGen(1, 3));
		MetaTileBase.registerTE(92, new MT_FluidGen(2, 3));
		MetaTileBase.registerTE(93, new MT_FluidGen(3, 3));
		
		MetaTileBase.registerTE(101, new MT_Fusion(1));
		MetaTileBase.registerTE(102, new MT_Fusion(2));
		MetaTileBase.registerTE(103, new MT_Fusion(3));
		MetaTileBase.registerTE(104, new MT_Fusion(4));
		
		MetaTileBase.registerTE(111, new MT_MagicGenerator(1));
		MetaTileBase.registerTE(112, new MT_MagicGenerator(2));
		MetaTileBase.registerTE(113, new MT_MagicGenerator(3));

		MetaTileBase.registerTE(121, new MT_BioGen(1));
		MetaTileBase.registerTE(122, new MT_BioGen(2));
		MetaTileBase.registerTE(123, new MT_BioGen(3));
		
		for (int n = 1; n <= 9; n++)
			MetaTileBase.registerTE(180 + n, new MT_BatBuff(n));
		
		for (int n = 0; n <= 8; n++)
			MetaTileBase.registerTE(190 + n, new MT_Transformer(n));
		
		for (int n = 1; n <= 5; n++)
			MetaTileBase.registerTE(200 + n, new MT_WaterMaker(n));
		
		MetaTileBase.registerTE(211, new MT_AdvChest(AdvChestType.STANDARD));
		MetaTileBase.registerTE(212, new MT_AdvChest(AdvChestType.IMPROVED));
		MetaTileBase.registerTE(213, new MT_AdvChest(AdvChestType.GOOD));
		MetaTileBase.registerTE(214, new MT_AdvChest(AdvChestType.ADVANCED));
		
		for (int n = 1; n <= 8; n++)
			MetaTileBase.registerTE(220 + n, new MT_MachineProcess(n, 2, 1, 0, 0, 0, MRecipes.AlloyFurnaceRecipes, true, false, "ealloy", null).setSfx(SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, 1.0F, 1.2F).setLux(10));
		
		for (int n = 1; n <= 8; n++)
			MetaTileBase.registerTE(230 + n, new MT_Assembler(n));

		for (int n = 1; n <= 8; n++)
			MetaTileBase.registerTE(240 + n, new MT_MachineProcess(n, 2, 6, 1, 1, 16000, MRecipes.SeparatorRecipes, true, false, "separator", null).setSfx(JSTSounds.SHOCK, 0.6F, 2.0F));
		
		for (int n = 1; n <= 8; n++)
			MetaTileBase.registerTE(250 + n, new MT_MachineProcess(n, 6, 2, 1, 1, 16000, MRecipes.ChemMixerRecipes, true, false, "chemmixer", null).setSfx(SoundEvents.BLOCK_BREWING_STAND_BREW, 1.0F, 1.2F));
		
		for (int n = 1; n <= 8; n++)
			MetaTileBase.registerTE(260 + n, new MT_Disassembler(n));
		
		for (int n = 1; n <= 8; n++)
			MetaTileBase.registerTE(270 + n, new MT_MachineProcess(n, 2, 2, 0, 0, 0, MRecipes.PressRecipes, true, false, "press", null).setSfx(SoundEvents.BLOCK_PISTON_EXTEND, 0.5F, 0.75F));
		
		for (int n = 1; n <= 8; n++)
			MetaTileBase.registerTE(280 + n, new MT_EFurnace(n));
		
		for (int n = 1; n <= 8; n++)
			MetaTileBase.registerTE(290 + n, new MT_UPulverizer(n));

		for (int n = 1; n <= 8; n++)
			MetaTileBase.registerTE(300 + n, new MT_MachineProcess(n, 2, 1, 1, 0, 16000, MRecipes.CrystalRecipes, true, false, "crystal", null).setSfx(SoundEvents.BLOCK_BREWING_STAND_BREW, 1.0F, 1.5F));
		
		for (int n = 1; n <= 8; n++)
			MetaTileBase.registerTE(310 + n, new MT_Recycler(n));

		for (int n = 1; n <= 8; n++)
			MetaTileBase.registerTE(320 + n, new MT_InductiveCharger(n));

		if (Loader.isModLoaded("toughasnails")) for (int n = 1; n <= 4; n++)
			MetaTileBase.registerTE(330 + n, new MT_AirConditioner(n));

		for (int n = 1; n <= 3; n++)
			MetaTileBase.registerTE(340 + n, new MT_CircuitResearchMachine(n));

		for (int n = 1; n <= 8; n++)
			MetaTileBase.registerTE(350 + n, new MT_CircuitBuilder(n));

		for (int n = 1; n <= 8; n++)
			MetaTileBase.registerTE(360 + n, new MT_Liquifier(n));

		for (int n = 1; n <= 5; n++)
			MetaTileBase.registerTE(370 + n, new MT_Pump(n));

		/* #4000: Cables */
		//IC2 compatible cables
	    MetaTileBase.registerTE(4001, new MT_Cable("cable_sn", 32, 0, 3, 5, 20));
	    MetaTileBase.registerTE(4002, new MT_Cable("wire_sn", 32, 1, 2, 2, 4));
	    MetaTileBase.registerTE(4003, new MT_Cable("cable_cu", 128, 0, 3, 5, 16));
	    MetaTileBase.registerTE(4004, new MT_Cable("wire_cu", 128, 1, 2, 2, 0));
	    MetaTileBase.registerTE(4005, new MT_Cable("cable_au", 512, 0, 4, 3, 34));
	    MetaTileBase.registerTE(4006, new MT_Cable("wire_au", 512, 1, 2, 1, 2));
	    MetaTileBase.registerTE(4007, new MT_Cable("cable_hv", 2048, 0, 5, 1, 51));
	    MetaTileBase.registerTE(4008, new MT_Cable("wire_hv", 2048, 1, 2, -2, 3));
	    MetaTileBase.registerTE(4009, new MT_Cable("cable_gf", 8192, 2, 2, 40, 1));
	    MetaTileBase.registerTE(4010, new MT_CableSwitch());
	    MetaTileBase.registerTE(4011, new MT_CableDetector());
		
		//JST
	    MetaTileBase.registerTE(4012, new MT_Cable("cable_sc", -1, 2, 2, 0));
	    MetaTileBase.registerTE(4013, new MT_Cable("cable_pb", 8, 0, 3, 16));
	    MetaTileBase.registerTE(4014, new MT_Cable("wire_pb", 8, 1, 2, 8));
	    MetaTileBase.registerTE(4015, new MT_Cable("cable_ag", 768, 0, 4, 10));
	    MetaTileBase.registerTE(4016, new MT_Cable("wire_ag", 768, 1, 2, 5));
	    MetaTileBase.registerTE(4017, new MT_Cable("cable_ba", 1536, 0, 4, 12));
	    MetaTileBase.registerTE(4018, new MT_Cable("wire_ba", 1536, 1, 2, 6));
	    MetaTileBase.registerTE(4019, new MT_Cable("cable_pt", 8192, 0, 5, 10));
	    MetaTileBase.registerTE(4020, new MT_Cable("wire_pt", 8192, 1, 2, 5));
	    MetaTileBase.registerTE(4021, new MT_Cable("cable_nb", 32768, 0, 6, -2));
	    MetaTileBase.registerTE(4022, new MT_Cable("wire_nb", 32768, 1, 2, -4));
	    MetaTileBase.registerTE(4023, new MT_Cable("cable_zn", 32, 0, 3, 6));
	    MetaTileBase.registerTE(4024, new MT_Cable("wire_zn", 32, 1, 2, 3));
	    MetaTileBase.registerTE(4025, new MT_Cable("cable_bz", 128, 0, 3, 6));
	    MetaTileBase.registerTE(4026, new MT_Cable("wire_bz", 128, 1, 2, 3));
	    MetaTileBase.registerTE(4027, new MT_Cable("cable_ir", 131072, 0, 6, -3));
	    MetaTileBase.registerTE(4028, new MT_Cable("wire_ir", 131072, 1, 2, -6));
	    MetaTileBase.registerTE(4029, new MT_Cable("cable_re", 524288, 0, 6, -3));
	    MetaTileBase.registerTE(4030, new MT_Cable("wire_re", 524288, 1, 2, -6));
	    MetaTileBase.registerTE(4031, new MT_Cable("cable_n", 32768, 2, 3, 100));

		/* #5000: Non-ticking TEs and MultiBlock structure parts */
		MetaTileBase.registerTE(5000, new MetaTileCasing("gen"));
		MetaTileBase.registerTE(5001, new MetaTileCasing("heatres"));
		MetaTileBase.registerTE(5002, new MetaTileCasing("csg_b"));
		MetaTileBase.registerTE(5003, new MetaTileCasing("csg_r"));
		MetaTileBase.registerTE(5004, new MetaTileDCCable());
		MetaTileBase.registerTE(5005, new MetaTileDCSolar(-1));
		MetaTileBase.registerTE(5006, new MetaTileDCSolar(0));
		MetaTileBase.registerTE(5007, new MetaTileDCSolar(1));
		MetaTileBase.registerTE(5008, new MetaTileDCSolar(2));
		MetaTileBase.registerTE(5010, new MetaTileDCWind(-1));
		MetaTileBase.registerTE(5011, new MetaTileDCWind(0));
		MetaTileBase.registerTE(5012, new MetaTileDCWind(1));
		MetaTileBase.registerTE(5013, new MetaTileDCWind(2));
		MetaTileBase.registerTE(5015, new MetaTileDCWatermill(-1));
		MetaTileBase.registerTE(5016, new MetaTileDCWatermill(0));
		MetaTileBase.registerTE(5017, new MetaTileDCWatermill(1));
		MetaTileBase.registerTE(5018, new MetaTileDCWatermill(2));
		MetaTileBase.registerTE(5020, new MetaTileDCTEG());

		MetaTileBase.registerTE(5065, new MetaTileDrillPipe());
		MetaTileBase.registerTE(5066, new MetaTileCasing("coil1"));
		MetaTileBase.registerTE(5067, new MetaTileCasing("coil2"));
		MetaTileBase.registerTE(5068, new MetaTileCasing("coil3"));
		MetaTileBase.registerTE(5069, new MetaTileCasing("coil4"));
		MetaTileBase.registerTE(5070, new MetaTileCasing("coil5"));
		MetaTileBase.registerTE(5071, new MetaTileCasing("coil6"));
		MetaTileBase.registerTE(5072, new MetaTileCasing("coil7"));
		MetaTileBase.registerTE(5073, new MetaTileCasing("coil8"));
		MetaTileBase.registerTE(5074, new MetaTileCasing("coil_sc"));
		MetaTileBase.registerTE(5075, new MetaTileCasing("fr1"));
		MetaTileBase.registerTE(5076, new MetaTileCasing("fr2"));
		MetaTileBase.registerTE(5077, new MetaTileCasing("fr3"));
		MetaTileBase.registerTE(5078, new MetaTileCasing("fr4"));
		MetaTileBase.registerTE(5080, new MetaTileCasingAdv("frame", null, 1.0F, 5.0F, false));
		MetaTileBase.registerTE(5082, new MetaTileEFenceWire());
		MetaTileBase.registerTE(5083, new MetaTileCasing("csg_a"));
		MetaTileBase.registerTE(5084, new MetaTileCasing("filter"));
		MetaTileBase.registerTE(5085, new MetaTileCasing("fan_off"));

		/* #6000: Non-tiered TEs*/
		//MetaTileBase.registerTE(6000, new MetaTileMESU());
		MetaTileBase.registerTE(6001, new MT_GTI());
		MetaTileBase.registerTE(6002, new MT_EnergyMeter());
		MetaTileBase.registerTE(6003, new MetaTileAlloyFurnace());
		MetaTileBase.registerTE(6004, new MetaTileSolarFurnace());
		MetaTileBase.registerTE(6005, new MT_DummyLoad());
		MetaTileBase.registerTE(6006, new MT_SuperCompressor());
		MetaTileBase.registerTE(6007, new MT_BlockBreaker());
		if (JSTCfg.ic2Loaded) {
		MetaTileBase.registerTE(6008, new MT_TMonitor());
		MetaTileBase.registerTE(6009, new MT_ReactorPlanner());}
		MetaTileBase.registerTE(6010, new MT_LargeGenerator(0));
		MetaTileBase.registerTE(6011, new MT_LargeGenerator(1));
		MetaTileBase.registerTE(6012, new MT_LargeGenerator(2));
		MetaTileBase.registerTE(6013, new MT_LargeGenerator(3));
		MetaTileBase.registerTE(6020, new MT_SHFurnace());
		MetaTileBase.registerTE(6021, new MT_FluidDrill());
		MetaTileBase.registerTE(6022, new MT_Prospector());
		MetaTileBase.registerTE(6024, new MT_EFenceChgr());
		MetaTileBase.registerTE(6025, new MT_HeatBoiler());
		MetaTileBase.registerTE(6026, new MT_Heater());
		MetaTileBase.registerTE(6027, new MT_SaltExtractor(1));
		MetaTileBase.registerTE(6028, new MT_AirCompressor(1));
		MetaTileBase.registerTE(6029, new MT_LargeTesla());
		MetaTileBase.registerTE(6030, new MT_LEDLight(1));
		MetaTileBase.registerTE(6031, new MT_AdvCropMatron());
		//6032=greenhouse
		MetaTileBase.registerTE(6033, new MT_LEDLight(2));
		MetaTileBase.registerTE(6034, new MT_LEDLight(3));
		MetaTileBase.registerTE(6035, new MT_LEDLight(4));
		MetaTileBase.registerTE(6040, new MT_BlockPlacer());
		MetaTileBase.registerTE(6041, new MT_CokeOven());
		MetaTileBase.registerTE(6042, new MT_AirPurifier());
		MetaTileBase.registerTE(6043, new MT_OreGrinder());
		MetaTileBase.registerTE(6044, new MT_Refinery());
		if (Loader.isModLoaded("advancedrocketry")) {
		MetaTileBase.registerTE(6045, new MT_WarpEnergyProvider());
		MetaTileBase.registerTE(6046, new MT_Rectenna());}
		MetaTileBase.registerTE(6047, new MT_BioProcessor());
		MetaTileBase.registerTE(6048, new MT_LargePurifier());
		MetaTileBase.registerTE(6049, new MT_Dam());
		if (Loader.isModLoaded("toughasnails"))
		MetaTileBase.registerTE(6050, new MT_WaterPurifier());
		MetaTileBase.registerTE(6060, new MT_Fueler());
		MetaTileBase.registerTE(6061, new MT_CreativeGenerator());
		MetaTileBase.registerTE(6062, new MT_FlameTrap());
		MetaTileBase.registerTE(6063, new MT_LargeBoiler());
		MetaTileBase.registerTE(6064, new MT_SaltExtractor(3));
		MetaTileBase.registerTE(6065, new MT_AirCompressor(3));
		MetaTileBase.registerTE(6066, new MT_ChunkQuarry());
		MetaTileBase.registerTE(6067, new MT_LargeFurnace());
		for (int n = 0; n <= 9; n++)
			MetaTileBase.registerTE(6070 + n, new MT_Drum(n));

		/* #7000: EarlyTech Kinetic energy based machines */
		MetaTileBase.registerTE(7000, new MetaTileGearBox(true));
		MetaTileBase.registerTE(7001, new MetaTileGearBox(false));
	}
}
