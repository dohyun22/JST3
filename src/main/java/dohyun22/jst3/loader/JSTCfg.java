package dohyun22.jst3.loader;

import dohyun22.jst3.utils.JSTChunkData;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;

public class JSTCfg {
	public static boolean ic2Loaded, gtceLoaded, rongGtLoaded, bcLoaded, teLoaded, tfLoaded, ticLoaded, rcLoaded, ieLoaded, tcLoaded;
	private static Configuration cfgObj;

	//General
	public static boolean PNT;
	public static byte RFPerEU = 4;
	//WorldGen
	public static boolean genNO, genEO;
	public static int volcCnc, marbCnc, oilSandCnc, endBedrockOreCnc;
	//Difficulty
	public static int eCnc, eCncST;
	public static byte nerfZombies;
	public static String[] fineDustTEs, fineDustBlocks;
	public static boolean DVEP, fineDust, hardEG, customMat, harderCircuit, onlyUseJSTCircuit, ovExplosion;
	//InterMod
	public static boolean DFHL, rIC2C, cheaperIC2, expensiveMassFab, nerfCS, noSuddenHoes,
	noElecEngine, buffIEDieselGen, gtOverlaps, removeGolemMelting, suppModFarmland, unlimitKC;

	public static void loadCfg(Configuration f) {
		cfgObj = f;
		try {
			f.load();
			
			String c = "Tweaks";
			Property p = f.get(c, "ProtectTopOfTheNether", true);
			p.setComment("If true, Non-Creative players cannot place/break blocks above bedrock layer. (Y > 128)");
			PNT = p.getBoolean();
			
			c = "Difficulty";
			p = f.get(c, "DisableVanillaEndPortal", true);
			p.setComment("If true, Vanilla End Portals will be disabled. (You'll have to use the Ender Gate instead)");
			DVEP = p.getBoolean();

			p = f.get(c, "HarderEnderGate", false);
			hardEG = p.getBoolean();

			p = f.get(c, "NerfZombies", 0);
			p.setComment("1: Nerf Baby Zombies, 2: Disable Baby Zombies, 4: Reduce sight range, Flags can be added together.");
			nerfZombies = (byte) p.getInt();

			p = f.get(c, "FineDustEnabled", true);
			p.setComment("Set this to false to disable Fine Dust(Pollution) effects.");
			fineDust = p.getBoolean();

			if (fineDust) {
				p = f.get(c, "FineDustTEs", new String[] {
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
				p.setComment("Syntax: TE class;ng/s;NBT tag names to trigger");
				fineDustTEs = p.getStringList();

				p = f.get(c, "FineDustBlocks", new String[] {
				        "minecraft:fire;0;100",
				        "minecraft:leaves;0;-50",
				        "minecraft:leaves2;0;-50",
				        "forestry:wood_pile;65280;500",
				        "forestry:leaves;0;-50"
				});
				p.setComment("Syntax: TE class;Meta bit mask(set 0 to allow any meta);ng/update");
				fineDustBlocks = p.getStringList();
			}

			p = f.get(c, "OreExplosionChance", 20);
			p.setComment("Chance of Nether Ore Explosion.\n(Default: 1 in 20, Set 0 to disable)");
			eCnc = p.getInt();
			
			p = f.get(c, "ExplosionChanceWithSilktouch", 100);
			p.setComment("Chance of Nether Ore Explosion with Silk Touch enchanted tools.\n(Default: 1 in 100, will not explode if zero)");
			eCncST = p.getInt();

			p = f.get(c, "HarderCircuit", true);
			p.setComment("If true, MV+ circuits will require Circuit Builder.");
			harderCircuit = p.getBoolean();

			p = f.get(c, "OnlyUseJSTCircuit", false);
			p.setComment("If true, JST recipes can only use JST circuits.");
			onlyUseJSTCircuit = p.getBoolean();

			p = f.get(c, "OvervoltageExplosion", false);
			p.setComment("If true, JST Machines can explode due to overvoltage.");
			ovExplosion = p.getBoolean();

			c = "General";
			p = f.get(c, "RFPerEU", 4);
			p.setComment("Conversion Ratio between JST/IC2 EU and RF/Forge Energy\nRange: 1-" + Byte.MAX_VALUE + "\nChange this value if you have energy dupe with other Energy Conversion Mods.");
			RFPerEU = (byte) MathHelper.clamp(p.getInt(), 1, Byte.MAX_VALUE);

			p = f.get(c, "CuSnPbEnabled", !(ic2Loaded || gtceLoaded || rongGtLoaded || tfLoaded || rcLoaded || ieLoaded));
			p.setComment("Set this to true to enable custom copper, tin and lead materials.");
			customMat = p.getBoolean();

			//pr = cfg.get(c, "EnableDebugger", false);
			//pr.setComment("Set this to true to enable Debugging");
			//Debug = pr.getBoolean();

			c = "Worldgen";
			p = f.get(c, "VolcanoChance", 3000);
			p.setComment("Chance of Volcano per chunk\n(Default: 1 in 3000 chunks, Minimum Value: 500, Set 0 to disable)");
			volcCnc = p.getInt() <= 0 ? 0 : Math.max(p.getInt(), 500);

			p = f.get(c, "MarbleChance", 8);
			p.setComment("Chance of Marble Cave per chunk\n(Default: 1 in 8 chunks, Set 0 to disable)");
			marbCnc = p.getInt();

			p = f.get(c, "OilSandChance", 24);
			p.setComment("Chance of Oil Sand per chunk\n(Default: 1 in 24 chunks, Set 0 to disable)");
			oilSandCnc = p.getInt();

			p = f.get(c, "EndBedrockOreChance", 32);
			p.setComment("Chance of End Bedrock Ore Cluster per chunk\n(Default: 1 in 32 chunks, Set 0 to disable)");
			endBedrockOreCnc = p.getInt();

			p = f.get(c, "DoGenerateNetherOre", true);
			p.setComment("Set false to disable all Nether Ores\nTODO: create more advanced OreGen config");
			genNO = p.getBoolean();

			p = f.get(c, "DoGenerateEnderOre", true);
			p.setComment("Set false to disable all End Ores except Bedrock Ores\nTODO: create more advanced OreGen config");
			genEO = p.getBoolean();

			p = f.get(c, "undergroundFluidSources", new String[] {
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
			p.setComment("Syntax: Random Weight;Fluid Name;Min;Range;Dimension Whitelist[1];Dimension Blacklist[1];Biome Whitelist[2];Biome Blacklist[2]\n[1]: You can only use numeric Dimension ID. comma separated.\n[2]: You can use numeric BiomeID or BiomeDictionary(ex. SANDY, JUNGLE). comma separated.\nEmpty whitelist value will allow any biomes/dimensions except the blacklisted one.");
			JSTChunkData.data = p.getStringList();

			c = "Compat";
			if (ic2Loaded) {
				p = f.get(c, "ReplaceIC2Cable", true);
				p.setComment("If true, IC2 Cable blocks will be replaced by JST Cables.\nNote: This feature will NOT replace IC2 cables that already exist in the world. (You'll have to replace them if needed)");
				rIC2C = p.getBoolean();

				p = f.get(c, "CheaperIC2Recipes", false);
				p.setComment("If true, Some of IC2 Recipes will be replaced by cheaper/simpler alternative recipes. (those recipes are closer to Pre-Experimental IC2 Recipes)");
				cheaperIC2 = p.getBoolean();

				p = f.get(c, "ExpensiveMassFab", false);
				p.setComment("If true, IC2 Mass Fabricator will be more expensive.");
				expensiveMassFab = p.getBoolean();

				p = f.get(c, "NoSuddenHoes", false);
				noSuddenHoes = p.getBoolean();

				p = f.get(c, "RestrictCropFarmland", false);
				p.setComment("If true, IC2 Crops can't be placed on non-farmland blocks.");
				nerfCS = p.getBoolean();

				p = f.get(c, "SupportModFarmland", true);
				p.setComment("If true, IC2 Crops can be placed on other mod's farmlands.");
				suppModFarmland = p.getBoolean();
			}

			/*if (gtceLoaded) {
				pr = cfg.get(c, "DisableGTOverlaps", false);
				pr.setComment("If true, Overlapping features between JST and GTCE will be disabled.");
				gtOverlaps = pr.getBoolean();
			}*/

			if (Loader.isModLoaded("forestry")) {
				p = f.get(c, "DarkerHive", false);
				p.setComment("If true, Forestry Hives will not emit any light.");
				DFHL = p.getBoolean();
				
				p = f.get(c, "DisableElectricEngineRecipe", false);
				p.setComment("Set true to disable Forestry Electric Engine Recipe.");
				noElecEngine = p.getBoolean();
			}

			if (ieLoaded) {
				p = f.get(c, "BuffIEDieselGen", false);
				p.setComment("If true, Immersive Engineering's Diesel Generator will be more efficient.");
				buffIEDieselGen = p.getBoolean();
			}

			if (ticLoaded) {
				p = f.get(c, "RemoveIronGolemMelting", false);
				removeGolemMelting = p.getBoolean();
			}

			if (Loader.isModLoaded("koreanchat")) {
				p = f.get(c, "UnlimitKoreanChat", false);
				p.setComment("If true, 100 character limit of Korean Chat will increase to 256.");
				unlimitKC = p.getBoolean();
			}
		} catch (Exception e) {
			throw new RuntimeException("Error loading config! Please report this log to JustServer developer team!", e);
		} finally {
			f.save();
		}
	}
}
