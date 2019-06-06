package dohyun22.jst3.compat.tic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.recipe.OreDictStack;
import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.compat.tic.CustomCombination;
import dohyun22.jst3.compat.tic.ModElec;
import dohyun22.jst3.compat.tic.ModExtraMod;
import dohyun22.jst3.compat.tic.ModNanoRepair;
import dohyun22.jst3.compat.tic.ModSolar;
import dohyun22.jst3.evhandler.EvHandler;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.loader.Loadable;
import dohyun22.jst3.loader.RecipeLoader;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.utils.JSTFluids;
import dohyun22.jst3.utils.JSTUtils;
import dohyun22.jst3.utils.ReflectionUtils;
import ic2.api.item.ElectricItem;
import ic2.api.item.IBackupElectricItemManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.client.model.ModifierModelLoader;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.ToolCore;

public class CompatTiC extends Loadable {
	private static final ResourceLocation RL = new ResourceLocation(JustServerTweak.MODID, "ticcap");

	@Override
	public boolean canLoad() {
		return JSTCfg.ticLoaded;
	}

	@Override
	public String getRequiredMod() {
		return null;
	}
	
	@Override
	@Method(modid = "tconstruct")
	public void preInit() {
		HashMap<IModifier, String> mods = new HashMap();
		Modifier mod = new ModExtraMod(1);
		mod.addRecipeMatch(new CustomCombination(1, new ItemStack(Items.BOOK), new ItemStack(Blocks.GOLD_BLOCK)));
		mod = new ModExtraMod(2);
		mod.addRecipeMatch(new CustomCombination(1, new ItemStack(Items.GOLDEN_APPLE, 1, 1), new ItemStack(Blocks.DIAMOND_BLOCK)));
		mod = new ModExtraMod(3);
		mod.addItem(new ItemStack(JSTItems.item1, 1, 11), 1, 1);

		if (JSTCfg.ic2Loaded) {
			mod = new ModElec(15000, 1, "rs1");
			String str = "jst3:models/item/compat/tcon/elec";
			mods.put(mod, str);
			mod.addRecipeMatch(new CustomCombination(1, new ItemStack(JSTItems.item1, 1, 12000), new ItemStack(JSTItems.item1, 1, 111)));
			mod = new ModElec(100000, 2, "nk2");
			mods.put(mod, str);
			mod.addRecipeMatch(new CustomCombination(1, new ItemStack(JSTItems.item1, 1, 12003), new ItemStack(JSTItems.item1, 1, 112)));
			mod = new ModElec(2400000, 3, "li3");
			mods.put(mod, str);
			mod.addRecipeMatch(new CustomCombination(1, new ItemStack(JSTItems.item1, 1, 12012), new ItemStack(JSTItems.item1, 1, 113)));
			mod = new ModSolar();
			mods.put(mod, "jst3:models/item/compat/tcon/solar");
			ItemStack st = JSTUtils.getModItemStack("ic2:te", 1, 8);
			if (!st.isEmpty()) mod.addItem(st, 1, 1);
			mod.addItem(new ItemStack(JSTBlocks.blockTile, 1, 5005), 1, 1);
			mod.addItem(new ItemStack(JSTBlocks.blockTile, 1, 5006), 1, 8);
			mod.addItem(new ItemStack(JSTBlocks.blockTile, 1, 5007), 1, 32);
			mod = new ModNanoRepair();
			st = new ItemStack(JSTItems.item1, 2, 103);
			mod.addRecipeMatch(new CustomCombination(1, st, st));

			try {
				addChargeManager();
			} catch (Throwable t) {}
		}

		if (JSTUtils.isClient()) {
			try {
				java.lang.reflect.Method m = ReflectionUtils.getMethod("slimeknights.tconstruct.common.ModelRegisterUtil", "registerModifierModel", IModifier.class, ResourceLocation.class);
				for (Entry<IModifier, String> e : mods.entrySet())
					m.invoke(null, e.getKey(), new ResourceLocation(e.getValue()));
			} catch (Throwable t) {
				JSTUtils.LOG.error("Failed to register Modifier models");
				t.printStackTrace();
			}
		}
	}

	@Override
	public void init() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	@Method(modid = "tconstruct")
	public void postInit() {
		RecipeLoader.addShapedRecipe(JSTUtils.getModItemStack("tconstruct:edible", 8, 10), "RRR", "RSR", "RRR", 'R', new ItemStack(Items.ROTTEN_FLESH), 'S', "dustSalt");
		MRecipes.addChemMixerRecipe(new Object[] {JSTUtils.getModItemStack("tconstruct:clear_stained_glass", 1, 32767)}, new FluidStack(JSTFluids.chlorine, 125), JSTUtils.getModItemStack("tconstruct:clear_glass"), null, null, 10, 200);
		MRecipes.addAlloyFurnaceRecipe(new OreDictStack("ingotArdite"), new OreDictStack("ingotCobalt"), JSTUtils.getFirstItem("ingotManyullyn"), 5, 100);
		MRecipes.addAlloyFurnaceRecipe(new OreDictStack("ingotCopper", 1), new OreDictStack("ingotAluminum", 3), JSTUtils.getFirstItem("ingotAlubrass", 4), 5, 100);
	
		EvHandler.addOre(JSTUtils.getModBlock("tconstruct:ore"), false);
	}
	
	public static boolean isTiCTool(ItemStack st) {
		if (!JSTCfg.ticLoaded || st == null || st.isEmpty()) return false;
		try {return st.getItem() instanceof ToolCore;} catch (Throwable t) {}
		return false;
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void initCapabilities(AttachCapabilitiesEvent<ItemStack> ev) {
		ItemStack st = ev.getObject();
		if (isTiCTool(st)) ev.addCapability(RL, new EnergyCapTiC(st));
	}

	public static class EnergyCapTiC implements IEnergyStorage, ICapabilityProvider {
		private final ItemStack st;

		public EnergyCapTiC(ItemStack st) {
			this.st = st;
		}

		@Override
		public boolean hasCapability(Capability<?> c, EnumFacing f) {
			return c == CapabilityEnergy.ENERGY;
		}

		@Override
		public <T> T getCapability(Capability<T> c, EnumFacing f) {
			if (hasCapability(c, f)) return (T) this;
			return null;
		}

		@Override
		public int receiveEnergy(int a, boolean s) {
			NBTTagCompound tags = st.getTagCompound();
			if (tags == null) return 0;
		    long eu2 = tags.getLong("JST_EU");
		    long ret = JSTUtils.getVoltFromTier(tags.getInteger("JST_EU_LVL"));
		    long max = tags.getLong("JST_EU_MAX");
		    ret = Math.min(max - eu2, Math.min(ret, a / JSTCfg.RFPerEU));
		    if (!s) {
		    	eu2 += ret;
		    	tags.setLong("JST_EU", eu2);
		    }
		    return JSTUtils.convLongToInt(ret * JSTCfg.RFPerEU);
		}

		@Override
		public int extractEnergy(int a, boolean s) {
			NBTTagCompound tags = st.getTagCompound();
			if (tags == null) return 0;
			long eu2 = tags.getLong("JST_EU");
			long ret = JSTUtils.getVoltFromTier(tags.getInteger("JST_EU_LVL"));
			ret = Math.min(eu2, Math.min(ret, a / JSTCfg.RFPerEU));
			if (!s) {
				eu2 -= ret;
				tags.setLong("JST_EU", eu2);
			}
			return JSTUtils.convLongToInt(ret * JSTCfg.RFPerEU);
		}

		@Override
		public int getEnergyStored() {
		    NBTTagCompound t = st.getTagCompound();
		    if (t == null) return 0;
		    return JSTUtils.convLongToInt(t.getLong("JST_EU") * JSTCfg.RFPerEU);
		}

		@Override
		public int getMaxEnergyStored() {
		    NBTTagCompound tags = st.getTagCompound();
		    if (tags == null) return 0;
		    return JSTUtils.convLongToInt(tags.getLong("JST_EU_MAX") * JSTCfg.RFPerEU);
		}

		@Override
		public boolean canExtract() {
			return st.hasTagCompound() && st.getTagCompound().getLong("JST_EU_MAX") > 0;
		}

		@Override
		public boolean canReceive() {
			return canExtract();
		}
	}

	/** Makes TiC Tools with JST EU modifiers compatible with IC2 EU*/
	@Method(modid = "ic2")
	public void addChargeManager() {
		ElectricItem.registerBackupManager(new IBackupElectricItemManager() {
			
			@Override
			public double charge(ItemStack st, double eu, int tier, boolean itl, boolean sim) {
				NBTTagCompound tags = st.getTagCompound();
				if (tags == null || !tags.hasKey("JST_EU") || tags.getInteger("JST_EU_LVL") > tier)
					return 0;
			    long eu2 = tags.getLong("JST_EU");
			    long ret = itl ? Long.MAX_VALUE : JSTUtils.getVoltFromTier(tags.getInteger("JST_EU_LVL"));
			    long max = tags.getLong("JST_EU_MAX");
			    ret = Math.min(max - eu2, Math.min(ret, (long) eu));
			    if (!sim) {
			    	eu2 += ret;
			    	tags.setLong("JST_EU", eu2);
			    }
			    return ret;
			}

			@Override
			public double discharge(ItemStack st, double eu, int tier, boolean itl, boolean ext, boolean sim) {
				NBTTagCompound tags = st.getTagCompound();
				if (tags == null || !tags.hasKey("JST_EU") || tags.getInteger("JST_EU_LVL") > tier)
					return 0;
				long eu2 = tags.getLong("JST_EU");
				long ret = itl ? Long.MAX_VALUE : JSTUtils.getVoltFromTier(tags.getInteger("JST_EU_LVL"));
				ret = Math.min(eu2, Math.min(ret, (long) eu));
				if (!sim) {
					eu2 -= ret;
					tags.setLong("JST_EU", eu2);
				}
				return ret;
			}

			@Override
			public double getCharge(ItemStack st) {
			    NBTTagCompound tags = st.getTagCompound();
			    if (tags == null) return 0;
			    return tags.getLong("JST_EU");
			}

			@Override
			public boolean canUse(ItemStack st, double eu) {
				return getCharge(st) > eu;
			}

			@Override
			public boolean use(ItemStack st, double amt, EntityLivingBase e) {
				return false;
			}

			@Override
			public void chargeFromArmor(ItemStack st, EntityLivingBase e) {}

			@Override
			public String getToolTip(ItemStack st) {
				return null;
			}

			@Override
			public boolean handles(ItemStack st) {
				return st != null && !st.isEmpty() && isTiCTool(st) && st.hasTagCompound() && st.getTagCompound().hasKey("JST_EU");
			}

			@Override
			public double getMaxCharge(ItemStack st) {
			    NBTTagCompound tags = st.getTagCompound();
			    if (tags == null) return 0;
			    return tags.getLong("JST_EU_MAX");
			}

			@Override
			public int getTier(ItemStack st) {
			    NBTTagCompound tags = st.getTagCompound();
			    if (tags == null) return 0;
			    return tags.getInteger("JST_EU_LVL");
			}
		});
	}

	/*public static class CapEvHandler {
		@SubscribeEvent(priority=EventPriority.LOWEST)
		public void onAttachCapabilityST(AttachCapabilitiesEvent<ItemStack> ev) {
			
		}
	}*/
}
