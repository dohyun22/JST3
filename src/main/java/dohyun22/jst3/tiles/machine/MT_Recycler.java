package dohyun22.jst3.tiles.machine;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.recipe.RecipeContainer;
import dohyun22.jst3.api.recipe.RecipeList;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.BatterySlot;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.container.JSTSlot;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.api.recipe.AnyInput;
import dohyun22.jst3.api.recipe.IRecipeItem;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.api.IScrewDriver;
import dohyun22.jst3.utils.JSTUtils;
import ic2.api.recipe.Recipes;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_Recycler extends MT_MachineProcess implements IScrewDriver {
	private byte mode;

	public MT_Recycler(int tier) {
		super(tier, 1, 1, 0, 0, 0, null, false, false, "recycler", null);
		setSfx(SoundEvents.BLOCK_PISTON_EXTEND, 0.5F, 1.6F);
	}
	
	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_Recycler(tier);
	}

	@Override
	protected boolean checkCanWork() {
		if (inv.get(1).getCount() >= inv.get(1).getMaxStackSize()) return false;
		return super.checkCanWork();
	}
	
	@Override
	@Nullable
	protected RecipeContainer getContainer(RecipeList recipe, ItemStack[] in, FluidTank[] fin, boolean sl, boolean fsl) {
		if (in == null || in.length <= 0 || in[0] == null || in[0].isEmpty() || (mode == 1 && in[0].getMaxStackSize() >= 8 && in[0].getCount() < 8)) return null;
		boolean flag = false;
		if (JSTCfg.ic2Loaded) {
			try {
				flag = Recipes.recyclerWhitelist.isEmpty() ? Recipes.recyclerBlacklist.contains(in[0]) : !Recipes.recyclerWhitelist.contains(in[0]);
			} catch (Throwable t) {}
		}
		int mx = getMaxCnt();
		boolean isAdv = tier >= 3 && in[0].getMaxStackSize() >= mx && (mode == 1 || (mode == 2 && in[0].getCount() >= 8));
		flag = !flag && (isAdv || getWorld().rand.nextInt(mx) == 0);
		ItemStack st = flag ? JSTCfg.ic2Loaded ? JSTUtils.getModItemStack("ic2:crafting", 1, 23) : new ItemStack(Blocks.DIRT) : ItemStack.EMPTY;
		return RecipeContainer.newContainer(new Object[] {new AnyInput(isAdv ? mx : 1)}, null, new ItemStack[] {st}, null, 1, isAdv ? 360 : 45);
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (baseTile != null && !isClient())
			pl.openGui(JustServerTweak.INSTANCE, 1, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
		return true;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		mode = tag.getByte("Mode");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setByte("Mode", mode);
	}

	@Override
	public boolean onScrewDriver(EntityPlayer pl, EnumFacing f, double px, double py, double pz) {
		if (tier < 3) return false;
		mode++;
		if (mode > 2) mode = 0;
		JSTUtils.sendMessage(pl, "jst.msg.recycler." + mode, getMaxCnt());
		return true;
	}

	private int getMaxCnt() {
		return 8 - (MathHelper.clamp(tier - 1, 0, 4));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getInformation(ItemStack st, World w, List<String> ls, ITooltipFlag adv) {
		double n = 100.0D / getMaxCnt();
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.recycler", ItemStack.DECIMALFORMAT.format(n)));
		if (tier >= 3) ls.add(I18n.format("jst.tooltip.tile.com.sd.rs"));
	}
}
