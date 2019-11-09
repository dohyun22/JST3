package dohyun22.jst3.loader;

import dohyun22.jst3.utils.JSTChunkData;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;

public class JSTCfg {
	public static boolean ic2Loaded, gtceLoaded, rongGtLoaded, bcLoaded, teLoaded, tfLoaded, ticLoaded, rcLoaded, ieLoaded;
	private static Configuration cfgObj;
	
	//Config related stuff
	public static boolean PNT;
	public static boolean DFHL;
	public static boolean DVEP;
	public static int ECnc;
	public static int ECncST;
	public static boolean genNO;
	public static boolean genEO;
	public static int VolcCnc;
	public static int MarbCnc;
	public static int OilSandCnc;
	public static int EndBedrockOreCnc;
	public static byte RFPerEU = 4;
	public static byte NerfZombies;
	public static boolean fineDust;
	public static String[] fineDustTEs;
	public static String[] fineDustBlocks;
	public static boolean hardEG;
	public static boolean customMat;
	public static boolean harderCircuit;
	public static boolean onlyUseJSTCircuit;
	public static boolean ovExplosion;
	//InterMod
	public static boolean RIC2C;
	public static boolean CheaperIC2;
	public static boolean ExpensiveMassFab;
	public static boolean nerfCS;
	public static boolean noSuddenHoes;
	public static boolean NoElecEngine;
	public static boolean BuffIEDieselGen;
	public static boolean gtOverlaps;
	public static boolean removeGolemMelting;

	public static void loadCfg(Configuration cfg) {
		cfgObj = cfg;
		try {
			cfg.load();
			
			String c = "Tweaks";
			Property pr = cfg.get(c, "ProtectTopOfTheNether", true);
			pr.setComment("If true, Non-Creative players cannot place/break blocks above bedrock layer. (Y > 128)");
			PNT = pr.getBoolean();
			
			c = "Difficulty";
			pr = cfg.get(c, "DisableVanillaEndPortal", true);
			pr.setComment("If true, Vanilla End Portals will be disabled. (You'll have to use the Ender Gate instead)");
			DVEP = pr.getBoolean();

			pr = cfg.get(c, "HarderEnderGate", false);
			hardEG = pr.getBoolean();

			pr = cfg.get(c, "NerfZombies", 0);
			pr.setComment("1: Nerf Baby Zombies, 2: Disable Baby Zombies, 4: Reduce sight range, Flags can be added together.");
			NerfZombies = (byte) pr.getInt();

			pr = cfg.get(c, "FineDustEnabled", true);
			pr.setComment("Set this to false to disable Fine Dust(Pollution) effects.");
			fineDust = pr.getBoolean();

			if (fineDust) {
				pr = cfg.get(c, "FineDustTEs", new String[] {
				        "minecraft:furnace;20;BurnTime",
				        "ic2:iron_furnace;20;fuel",
				        "ic2:generator;20;fuel",
				        "ic2:semifluid_generator;30;fuel",
				        "ic2:geo_generator;5;fuel",
				        "ic2:solid_heat_generator;20;fuel",
				        "ic2:fluid_heat_generator;30;HeatBuffer",
				        "ic2:blast_furnace;75;active",
				        "immersiveengineering:dieselgenerator;300;active",
				        "immersiveengineering:blastfurnace;150;burnTime",
				        "immersiveengineering:blastfurnaceadvanced;150;burnTime",
				        "immersiveengineering:cokeoven;150;process",
				        "immersiveengineering:alloysmelter;50;burnTime",
				        "forestry:peat;30;EngineBurnTime",
				        "forestry:biogas;15;EngineProgress",
				        "forestry:generator;15;tanks",
				        "thermalexpansion:dynamo_steam;25;Active",
				        "thermalexpansion:dynamo_magmatic;10;Active",
				        "thermalexpansion:dynamo_compression;30;Active",
				        "minecraft:tconstruct.smeltery_controller;10;fuel",
				        "railcraft:boiler_firebox_fluid;100;burnTime",
				        "railcraft:boiler_firebox_solid;100;burnTime",
				        "railcraft:coke_oven;150;cookTime",
				        "railcraft:blast_furnace;150;burnTime",
				        "industrialforegoing:sludge_refiner_tile;150;tick_lastWork",
				        "industrialforegoing:sewage_composter_solidifier_tile;50;tick_lastWork",
				        "industrialforegoing:animal_byproduct_recolector_tile;25;tick_lastWork"
				});
				pr.setComment("Syntax: TE class;ng/s;NBT tag names to trigger");
				fineDustTEs = pr.getStringList();

				pr = cfg.get(c, "FineDustBlocks", new String[] {
				        "minecraft:fire;0;100",
				        "minecraft:leaves;0;-50",
				        "minecraft:leaves2;0;-50",
				        "forestry:wood_pile;65280;500",
				        "forestry:leaves;0;-50"
				});
				pr.setComment("Syntax: TE class;Meta bit mask(set 0 to allow any meta);ng/update");
				fineDustBlocks = pr.getStringList();
			}

			pr = cfg.get(c, "OreExplosionChance", 20);
			pr.setComment("Chance of Nether Ore Explosion.\n(Default: 1 in 20, Set 0 to disable)");
			ECnc = pr.getInt();
			
			pr = cfg.get(c, "ExplosionChanceWithSilktouch", 100);
			pr.setComment("Chance of Nether Ore Explosion with Silk Touch enchanted tools.\n(Default: 1 in 100, will not explode if zero)");
			ECncST = pr.getInt();

			pr = cfg.get(c, "HarderCircuit", true);
			pr.setComment("If true, MV+ circuits will require Circuit Builder.");
			harderCircuit = pr.getBoolean();

			pr = cfg.get(c, "OnlyUseJSTCircuit", false);
			pr.setComment("If true, JST Circuits can only be used in JST Recipes.");
			onlyUseJSTCircuit = pr.getBoolean();

			pr = cfg.get(c, "OvervoltageExplosion", false);
			pr.setComment("If true, JST Machines can explode due to overvoltage.");
			ovExplosion = pr.getBoolean();

			c = "General";
			pr = cfg.get(c, "RFPerEU", 4);
			pr.setComment("Conversion Ratio between JST/IC2 EU and RF/Forge Energy\nRange: 1-" + Byte.MAX_VALUE + "\nChange this value if you have energy dupe with other Energy Conversion Mods.");
			RFPerEU = (byte) MathHelper.clamp(pr.getInt(), 1, Byte.MAX_VALUE);

			pr = cfg.get(c, "CuSnPbEnabled", !(ic2Loaded || gtceLoaded || rongGtLoaded || tfLoaded || rcLoaded || ieLoaded));
			pr.setComment("Set this to true to enable custom copper, tin and lead materials.");
			customMat = pr.getBoolean();

			//pr = cfg.get(c, "EnableDebugger", false);
			//pr.setComment("Set this to true to enable Debugging");
			//Debug = pr.getBoolean();

			c = "Worldgen";
			pr = cfg.get(c, "VolcanoChance", 3000);
			pr.setComment("Chance of Volcano per chunk\n(Default: 1 in 3000 chunks, Minimum Value: 500, Set 0 to disable)");
			VolcCnc = pr.getInt() <= 0 ? 0 : Math.max(pr.getInt(), 500);

			pr = cfg.get(c, "MarbleChance", 8);
			pr.setComment("Chance of Marble Cave per chunk\n(Default: 1 in 8 chunks, Set 0 to disable)");
			MarbCnc = pr.getInt();

			pr = cfg.get(c, "OilSandChance", 24);
			pr.setComment("Chance of Oil Sand per chunk\n(Default: 1 in 24 chunks, Set 0 to disable)");
			OilSandCnc = pr.getInt();

			pr = cfg.get(c, "EndBedrockOreChance", 32);
			pr.setComment("Chance of End Bedrock Ore Cluster per chunk\n(Default: 1 in 32 chunks, Set 0 to disable)");
			EndBedrockOreCnc = pr.getInt();

			pr = cfg.get(c, "DoGenerateNetherOre", true);
			pr.setComment("Set false to disable all Nether Ores\nTODO: create more advanced OreGen config");
			genNO = pr.getBoolean();

			pr = cfg.get(c, "DoGenerateEnderOre", true);
			pr.setComment("Set false to disable all End Ores except Bedrock Ores\nTODO: create more advanced OreGen config");
			genEO = pr.getBoolean();

			pr = cfg.get(c, "undergroundFluidSources", new String[] {
					"3000;none;0;0;0;;;",
					"100;oil;100000;3900000;0;;;",
					"100;gas.natural;200000;4800000;0;;;",
					"200;lava;200000;9800000;0;;;",
					"300;water;500000;9500000;0;;;",
					"25;helium;50000;150000;0;;;",
					"25;petrotheum;50000;500000;0;;;",
					"25;aerotheum;50000;500000;0;;;",
					"50;oil;100000;9900000;0;;SANDY;",
					"50;gas.natural;200000;14800000;0;;SANDY;",
					"50;oil;100000;4900000;0;;OCEAN;",
					"50;gas.natural;200000;7300000;0;;OCEAN;",
					"200;ice;100000;500000;0;;SNOWY;",
					"25;cryotheum;50000;450000;0;;SNOWY;",
					"1200;none;0;0;-1;;;",
					"300;lava;500000;19500000;-1;;;",
					"100;pyrotheum;50000;450000;-1;;;",
					"25;glowstone;50000;450000;-1;;;",
					"950;none;0;0;-1;;;",
					"50;ender;50000;450000;1;;;",
					"50;experience;50000;200000;1;;;"
			});
			pr.setComment("Syntax: Random Weight;Fluid Name;Min;Range;Dimension Whitelist[1];Dimension Blacklist[1];Biome Whitelist[2];Biome Blacklist[2]\n[1]: You can only use numeric Dimension ID. comma separated.\n[2]: You can use numeric BiomeID or BiomeDictionary(ex. SANDY, JUNGLE). comma separated.\nEmpty whitelist value will allow any biomes/dimensions except the blacklisted one.");
			JSTChunkData.data = pr.getStringList();

			c = "Compat";
			if (ic2Loaded) {
				pr = cfg.get(c, "ReplaceIC2Cable", true);
				pr.setComment("If true, IC2 Cable blocks will be replaced by JST Cables.\nNote: This feature will NOT replace IC2 cables that already exist in the world. (You'll have to replace them if needed)");
				RIC2C = pr.getBoolean();

				pr = cfg.get(c, "CheaperIC2Recipes", false);
				pr.setComment("If true, Some of IC2 Recipes will be replaced by cheaper/simpler alternative recipes. (those recipes are closer to Pre-Experimental IC2 Recipes)");
				CheaperIC2 = pr.getBoolean();

				pr = cfg.get(c, "ExpensiveMassFab", false);
				pr.setComment("If true, IC2 Mass Fabricator will be more expensive.");
				ExpensiveMassFab = pr.getBoolean();

				pr = cfg.get(c, "NoSuddenHoes", false);
				noSuddenHoes = pr.getBoolean();

				pr = cfg.get(c, "RestrictCropFarmland", false);
				pr.setComment("If true, IC2 Crops can't be placed on non-farmland blocks.");
				nerfCS = pr.getBoolean();
			}

			if (gtceLoaded) {
				pr = cfg.get(c, "DisableGTOverlaps", false);
				pr.setComment("If true, Overlapping features between JST and GTCE will be disabled.");
				gtOverlaps = pr.getBoolean();
			}

			if (Loader.isModLoaded("forestry")) {
				pr = cfg.get(c, "DarkerHive", false);
				pr.setComment("If true, Forestry Hives will not emit any light.");
				DFHL = pr.getBoolean();
				
				pr = cfg.get(c, "DisableElectricEngineRecipe", false);
				pr.setComment("Set true to disable Forestry Electric Engine Recipe.");
				NoElecEngine = pr.getBoolean();
			}

			if (ieLoaded) {
				pr = cfg.get(c, "BuffIEDieselGen", false);
				pr.setComment("If true, Immersive Engineering's Diesel Generator will be more efficient.");
				BuffIEDieselGen = pr.getBoolean();
			}

			if (ticLoaded) {
				pr = cfg.get(c, "RemoveIronGolemMelting", false);
				removeGolemMelting = pr.getBoolean();
			}
		} catch (Exception e) {
			throw new RuntimeException("Error loading config! Please report this log to JustServer developer team!", e);
		} finally {
			cfg.save();
		}
	}
}
