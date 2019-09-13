package dohyun22.jst3.tiles.machine;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.recipe.RecipeContainer;
import dohyun22.jst3.api.recipe.RecipeList;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.BatterySlot;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.container.JSTSlot;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.interfaces.IScrewDriver;
import dohyun22.jst3.utils.JSTUtils;
import dohyun22.jst3.utils.ReflectionUtils;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.MachineRecipe;
import ic2.api.recipe.MachineRecipeResult;
import ic2.api.recipe.Recipes;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_UPulverizer extends MT_MachineProcess implements IScrewDriver {
	private static Method PMngCache;
	private static boolean error;
	private byte mode;

	public MT_UPulverizer(int tier) {
		super(tier, 1, 2, 0, 0, 0, null, false, false, "upulv", "vent");
		setSfx(SoundEvents.BLOCK_METAL_BREAK, 1.0F, 0.5F);
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		MT_UPulverizer r = new MT_UPulverizer(tier);
		r.updateMode();
		return r;
	}

	@Override
	@Nullable
	protected RecipeContainer getContainer(RecipeList recipe, ItemStack[] in, FluidTank[] fin, boolean sl, boolean fsl) {
		if (in == null || in.length <= 0 || in[0] == null || in[0].isEmpty()) return null;
		if (JSTCfg.ic2Loaded || JSTCfg.teLoaded) return getPulvRecipe(in[0], getWorld().rand, mode);
		return null;
	}

	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, ITooltipFlag adv) {
		if (JSTCfg.ic2Loaded && JSTCfg.teLoaded) ls.add(I18n.format("jst.tooltip.tile.com.sd.rs"));
	}
	
	@Override
	protected void addSlot(ContainerGeneric cg, InventoryPlayer inv, TileEntityMeta te) {
		cg.addSlot(new Slot(te, 0, 53, 35));
		cg.addSlot(new JSTSlot(te, 1, 107, 35, false, true, 64, true));
		cg.addSlot(new JSTSlot(te, 2, 125, 35, false, true, 64, true));
		cg.addSlot(new BatterySlot(te, 3, 8, 53, false, true));
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void addComp(GUIGeneric gg) {
		gg.addSlot(53, 35, 0);
		gg.addSlot(107, 35, 0);
		gg.addSlot(125, 35, 0);

		gg.addPrg(76, 35, "macerator", "thermalexpansion.pulverizer");

		gg.addSlot(8, 53, 2);
		gg.addPwr(12, 31);
	}
	
	@Override
	public void onPostTick() {
		super.onPostTick();
		if (isClient() && baseTile.isActive()) {
			World w = getWorld();
			if (w.rand.nextInt(8) == 0) {
				for (int i = 0; i < 4; i++) {
					BlockPos p = getPos();
					double x = p.getX() + 0.25D + w.rand.nextFloat() * 0.45D;
					double y = p.getY() + 1.1D;
					double z = p.getZ() + 0.25D + w.rand.nextFloat() * 0.45D;
					w.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0D, 0.0D, 0.0D);
				}
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		mode = tag.getByte("MD");
		updateMode();
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setByte("MD", mode);
	}
	
	public static RecipeContainer getPulvRecipe(ItemStack in, Random r, int m) {
		if (in == null || in.isEmpty()) return null;
		ItemStack[] io = new ItemStack[] {ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};
		int dur = 150;
		if (m == 0) {
			getIC2(in, io);
			if (!MRecipes.isValid(io[1]))
				dur = getTE(in, r, io);
		} else if (m == 1) {
			dur = getTE(in, r, io);
			if (!MRecipes.isValid(io[1]))
				getIC2(in, io);
		} else if (m == 2) {
			getIC2(in, io);
		} else if (m == 3) {
			dur = getTE(in, r, io);
		}
		if (io[0].isEmpty() || !MRecipes.isValid(io[1]))
			return null;
		return RecipeContainer.newContainer(new Object[] {io[0]}, null, new ItemStack[] {io[1], MRecipes.isValid(io[2]) ? io[2] : null}, null, 5, dur);
	}

	@Override
	public boolean onScrewDriver(EntityPlayer pl, EnumFacing f, double px, double py, double pz) {
		byte prev = mode;
		mode++;
		if (mode > 3) mode = 0;
		updateMode();
		if (prev != mode && mode >= 0) {
			JSTUtils.sendMessage(pl, "jst.msg.pulverizer." + mode);
			return true;
		}
		return false;
	}

	private static void getIC2(ItemStack in, ItemStack[] io) {
		if (JSTCfg.ic2Loaded) {
			try {
				MachineRecipeResult<IRecipeInput, Collection<ItemStack>, ItemStack> res = Recipes.macerator.apply(in, false);
				if (res != null) {
					Collection<ItemStack> out = res.getOutput();
					if (out != null && !out.isEmpty()) {
						io[0] = in.copy();
						io[0].setCount(in.getCount() - res.getAdjustedInput().getCount());
						io[1] = out.toArray(new ItemStack[0])[0].copy();
					}
				}
			} catch (Throwable t) {}
		}
	}

	private static int getTE(ItemStack in, Random r, ItemStack[] io) {
		int ret = 150;
		try {
			if (PMngCache == null)
				PMngCache = Class.forName("cofh.thermalexpansion.util.managers.machine.PulverizerManager").getMethod("getRecipe", ItemStack.class);
			Object obj = PMngCache.invoke(null, in);
			if (obj != null) {
				if (io[1].isEmpty()) {
					io[0] = in.copy();
					io[0].setCount(((ItemStack)ReflectionUtils.callMethod(obj, "getInput")).getCount());
					io[1] = (ItemStack)ReflectionUtils.callMethod(obj, "getPrimaryOutput");
					ret = Math.max(1, ((int)ReflectionUtils.callMethod(obj, "getEnergy")) / 20);
				}
				int cnc = (int) ReflectionUtils.callMethod(obj, "getSecondaryOutputChance");
				io[2] = cnc >= 100 || r.nextInt(100) < cnc ? (ItemStack)ReflectionUtils.callMethod(obj, "getSecondaryOutput") : null;
			}
		} catch (Throwable t) {
			t.printStackTrace(); error = true;
		}
		return ret;
	}

	private void updateMode() {
		if (!JSTCfg.ic2Loaded && !JSTCfg.teLoaded) mode = -1;
		else if (!JSTCfg.ic2Loaded) mode = 3;
		else if (!JSTCfg.teLoaded) mode = 2;
	}
}
