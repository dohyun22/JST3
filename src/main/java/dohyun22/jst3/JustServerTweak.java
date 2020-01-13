package dohyun22.jst3;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.entity.*;
import dohyun22.jst3.evhandler.EvHandler;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.loader.LoadableLoader;
import dohyun22.jst3.loader.MTELoader;
import dohyun22.jst3.loader.RecipeLoader;
import dohyun22.jst3.network.JSTPacketHandler;
import dohyun22.jst3.proxy.CommonProxy;
import dohyun22.jst3.recipes.ItemList;
import dohyun22.jst3.utils.CrashInfoAdder;
import dohyun22.jst3.utils.JSTChunkData;
import dohyun22.jst3.utils.JSTFluids;
import dohyun22.jst3.utils.JSTPotions;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import dohyun22.jst3.utils.ReflectionUtils;
import dohyun22.jst3.worldgen.JSTWorldgenHandler;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fluids.FluidRegistry;

@Mod(modid = JustServerTweak.MODID, version = JustServerTweak.VERSION, name = JustServerTweak.NAME, acceptedMinecraftVersions="[1.12,1.12.2]", dependencies="after:jei;after:ic2;after:gregtech;after:immersiveengineering;after:railcraft;after:tconstruct;after:redpowercore;after:forestry;after:magicbees;after:buildcraftcore;after:buildcraftenergy;after:buildcraftfactory;after:thermalfoundation;after:thermalexpansion")
public class JustServerTweak {
	@SidedProxy(clientSide = "dohyun22.jst3.proxy.ClientProxy", serverSide = "dohyun22.jst3.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	public static final String MODID = "jst3";
	public static final String VERSION = "3.6.3MC1.12";
	public static final String NAME = "Just Server Tweak 3";

	@Mod.Instance(JustServerTweak.MODID)
	public static JustServerTweak INSTANCE;
	
	public static CreativeTabs JSTTab = new CreativeTabs("JST3") {
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(JSTItems.item1, 1, 11);
		}
		
	};

	static {
		try {
			BlockPos.ORIGIN.add(1, 1, 1).toLong();
			ItemStack.EMPTY.isEmpty();
		} catch (Throwable t) {
			throw new RuntimeException("JST3 detected an incompatible environment, Please use the right version of Minecraft.");
		}
		FluidRegistry.enableUniversalBucket();
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		new CrashInfoAdder();

		JSTCfg.ic2Loaded = Loader.isModLoaded("ic2");
		JSTCfg.gtceLoaded = Loader.isModLoaded("gregtech");
		JSTCfg.bcLoaded = Loader.isModLoaded("buildcraftcore");
		JSTCfg.teLoaded = Loader.isModLoaded("thermalexpansion");
		JSTCfg.tfLoaded = Loader.isModLoaded("thermalfoundation");
		JSTCfg.ticLoaded = Loader.isModLoaded("tconstruct");
		JSTCfg.rcLoaded = Loader.isModLoaded("railcraft");
		JSTCfg.ieLoaded = Loader.isModLoaded("immersiveengineering");
		JSTCfg.tcLoaded = Loader.isModLoaded("thaumcraft");

		JSTCfg.loadCfg(new Configuration(event.getSuggestedConfigurationFile()));

		JSTBlocks.init();
		JSTItems.init();
		JSTFluids.init();
		JSTSounds.init();
		JSTPotions.init();
		MTELoader.init();

		LoadableLoader.preInit();
		JSTPacketHandler.preInit();

		proxy.preinit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, proxy);

		EntityRegistry.registerModEntity(new ResourceLocation(JustServerTweak.MODID, "laserbeam"), EntityLaserBeam.class, "laserbeam", 0, INSTANCE, 100, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(JustServerTweak.MODID, "oreexplosion"), EntityPrimedOre.class, "oreexplosion", 1, INSTANCE, 80, 5, false);
		EntityRegistry.registerModEntity(new ResourceLocation(JustServerTweak.MODID, "ecart"), EntityPoweredCart.class, "ecart", 2, INSTANCE, 150, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(JustServerTweak.MODID, "cardesl"), EntityCarDiesel.class, "cardesl", 3, INSTANCE, 64, 2, true);
		EntityRegistry.registerModEntity(new ResourceLocation(JustServerTweak.MODID, "carelec"), EntityCarElec.class, "carelec", 4, INSTANCE, 64, 2, true);

		GameRegistry.registerWorldGenerator(new JSTWorldgenHandler(), 0);

		MinecraftForge.EVENT_BUS.register(new EvHandler());

		LoadableLoader.init();
		JSTPacketHandler.init();
		proxy.init();
	}

	@EventHandler
	public void postinit(FMLPostInitializationEvent event) {
		EvHandler.init();
		ItemList.init();
		JSTChunkData.init();

		LoadableLoader.postInit();

		JSTWorldgenHandler.addGen(JSTBlocks.blockOre.getStateFromMeta(1), 4, Blocks.BEDROCK, 0, 5, 1, 500, new int[] {0, -1});
		JSTWorldgenHandler.addGen(JSTBlocks.blockOre.getStateFromMeta(2), 3, Blocks.BEDROCK, 0, 5, 1, 100, new int[] {0, -1});
		JSTWorldgenHandler.addGen(JSTBlocks.blockOre.getStateFromMeta(4), 8, Blocks.STONE, 5, 20, 3, 10000, new int[] {1, -1}, true);
		JSTWorldgenHandler.addGen(JSTBlocks.blockOre.getStateFromMeta(6), 3, Blocks.STONE, 5, 100, 1, 2500, new int[] {1, -1}, true);
		JSTWorldgenHandler.addGen(JSTBlocks.blockOre.getStateFromMeta(7), 8, Blocks.STONE, 20, 80, 6, 10000, new int[] {1, -1}, true);
		JSTWorldgenHandler.addGen(JSTBlocks.blockOre.getStateFromMeta(10), 14, Blocks.STONE, 32, 100, 2, 7000, new int[] {1, -1}, true);
		JSTWorldgenHandler.addGen(JSTBlocks.blockOre.getStateFromMeta(11), 4, Blocks.STONE, 5, 24, 2, 10000, new int[] {1, -1}, true);
		JSTWorldgenHandler.addGen(JSTBlocks.blockOre.getStateFromMeta(12), 5, Blocks.STONE, 24, 80, 2, 10000, new int[] {1, -1}, true);
		JSTWorldgenHandler.addGen(JSTBlocks.blockOre.getStateFromMeta(13), 6, Blocks.STONE, 5, 48, 2, 10000, new int[] {1, -1}, true);
		JSTWorldgenHandler.addGen(JSTBlocks.blockOre.getStateFromMeta(14), 6, Blocks.STONE, 5, 48, 2, 10000, new int[] {1, -1}, true);
		JSTWorldgenHandler.addGen(JSTBlocks.blockOre.getStateFromMeta(15), 6, Blocks.STONE, 5, 48, 2, 10000, new int[] {1, -1}, true);
		
		if (JSTCfg.genNO) {
			JSTWorldgenHandler.addGen(JSTBlocks.blockNO.getStateFromMeta(0), 12, Blocks.NETHERRACK, 5, 120, 12, 10000, -1);
			JSTWorldgenHandler.addGen(JSTBlocks.blockNO.getStateFromMeta(1), 8, Blocks.NETHERRACK, 5, 32, 3, 10000, -1);
			JSTWorldgenHandler.addGen(JSTBlocks.blockNO.getStateFromMeta(2), 10, Blocks.NETHERRACK, 5, 120, 4, 10000, -1);
			JSTWorldgenHandler.addGen(JSTBlocks.blockNO.getStateFromMeta(3), 14, Blocks.NETHERRACK, 5, 120, 8, 10000, -1);
			JSTWorldgenHandler.addGen(JSTBlocks.blockNO.getStateFromMeta(4), 4, Blocks.NETHERRACK, 5, 120, 6, 10000, -1);
			JSTWorldgenHandler.addGen(JSTBlocks.blockNO.getStateFromMeta(5), 10, Blocks.NETHERRACK, 5, 120, 8, 10000, -1);
			JSTWorldgenHandler.addGen(JSTBlocks.blockNO.getStateFromMeta(6), 14, Blocks.NETHERRACK, 5, 120, 8, 10000, -1);
			JSTWorldgenHandler.addGen(JSTBlocks.blockNO.getStateFromMeta(7), 14, Blocks.NETHERRACK, 5, 120, 8, 10000, -1);
			JSTWorldgenHandler.addGen(JSTBlocks.blockNO.getStateFromMeta(8), 4, Blocks.NETHERRACK, 5, 64, 2, 10000, -1);
			JSTWorldgenHandler.addGen(JSTBlocks.blockNO.getStateFromMeta(9), 10, Blocks.NETHERRACK, 5, 120, 5, 10000, -1);
			JSTWorldgenHandler.addGen(JSTBlocks.blockNO.getStateFromMeta(10), 10, Blocks.NETHERRACK, 5, 120, 6, 10000, -1);
			JSTWorldgenHandler.addGen(JSTBlocks.blockNO.getStateFromMeta(11), 5, Blocks.NETHERRACK, 5, 64, 3, 10000, -1);
			JSTWorldgenHandler.addGen(JSTBlocks.blockNO.getStateFromMeta(12), 8, Blocks.NETHERRACK, 5, 120, 6, 10000, -1);
			JSTWorldgenHandler.addGen(JSTBlocks.blockNO.getStateFromMeta(13), 6, Blocks.NETHERRACK, 5, 120, 4, 10000, -1);
			JSTWorldgenHandler.addGen(JSTBlocks.blockNO.getStateFromMeta(14), 12, Blocks.NETHERRACK, 5, 120, 16, 10000, -1);
			JSTWorldgenHandler.addGen(JSTBlocks.blockNO.getStateFromMeta(15), 6, Blocks.NETHERRACK, 5, 64, 3, 10000, -1);
		}
		
		if (JSTCfg.genEO) {
			JSTWorldgenHandler.addGen(JSTBlocks.blockEO.getStateFromMeta(0), 12, Blocks.END_STONE, 10, 120, 4, 10000, 1);
			JSTWorldgenHandler.addGen(JSTBlocks.blockEO.getStateFromMeta(1), 6, Blocks.END_STONE, 10, 120, 3, 8000, 1);
			JSTWorldgenHandler.addGen(JSTBlocks.blockEO.getStateFromMeta(2), 10, Blocks.END_STONE, 10, 120, 5, 10000, 1);
			JSTWorldgenHandler.addGen(JSTBlocks.blockEO.getStateFromMeta(3), 14, Blocks.END_STONE, 10, 120, 8, 10000, 1);
			JSTWorldgenHandler.addGen(JSTBlocks.blockEO.getStateFromMeta(4), 10, Blocks.END_STONE, 10, 120, 4, 10000, 1);
			JSTWorldgenHandler.addGen(JSTBlocks.blockEO.getStateFromMeta(5), 8, Blocks.END_STONE, 10, 120, 6, 10000, 1);
			JSTWorldgenHandler.addGen(JSTBlocks.blockEO.getStateFromMeta(6), 14, Blocks.END_STONE, 10, 120, 7, 10000, 1);
			JSTWorldgenHandler.addGen(JSTBlocks.blockEO.getStateFromMeta(7), 14, Blocks.END_STONE, 10, 120, 7, 10000, 1);
			JSTWorldgenHandler.addGen(JSTBlocks.blockEO.getStateFromMeta(8), 4, Blocks.END_STONE, 10, 120, 3, 10000, 1);
			JSTWorldgenHandler.addGen(JSTBlocks.blockEO.getStateFromMeta(9), 10, Blocks.END_STONE, 10, 120, 5, 10000, 1);
			JSTWorldgenHandler.addGen(JSTBlocks.blockEO.getStateFromMeta(10), 10, Blocks.END_STONE, 10, 120, 6, 10000, 1);
			JSTWorldgenHandler.addGen(JSTBlocks.blockEO.getStateFromMeta(11), 8, Blocks.END_STONE, 10, 120, 3, 10000, 1);
			JSTWorldgenHandler.addGen(JSTBlocks.blockEO.getStateFromMeta(12), 10, Blocks.END_STONE, 10, 120, 6, 10000, 1);
			JSTWorldgenHandler.addGen(JSTBlocks.blockEO.getStateFromMeta(13), 9, Blocks.END_STONE, 10, 120, 5, 10000, 1);
			JSTWorldgenHandler.addGen(JSTBlocks.blockEO.getStateFromMeta(14), 5, Blocks.END_STONE, 10, 120, 2, 5000, 1);
			JSTWorldgenHandler.addGen(JSTBlocks.blockEO.getStateFromMeta(15), 3, Blocks.END_STONE, 10, 120, 1, 5000, 1);
		}
		
		proxy.postinit();
	}
}