package dohyun22.jst3.tiles.machine;

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
import dohyun22.jst3.api.recipe.IRecipeItem;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.interfaces.IScrewDriver;
import dohyun22.jst3.utils.JSTUtils;
import ic2.api.recipe.Recipes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.actors.threadpool.Arrays;

public class MT_Recycler extends MT_MachineGeneric implements IScrewDriver {
	private byte mode;

	public MT_Recycler(int tier) {
		super(tier, 1, 1, 0, 0, 0, null, false, false);
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
		boolean isAdv = tier >= 3 && in[0].getMaxStackSize() >= 8 && (mode == 1 || (mode == 2 && in[0].getCount() >= 8));
		flag = !flag && (isAdv || getWorld().rand.nextInt(8) == 0);
		ItemStack st = flag ? JSTCfg.ic2Loaded ? JSTUtils.getModItemStack("ic2:crafting", 1, 23) : new ItemStack(Blocks.DIRT) : ItemStack.EMPTY;
		return RecipeContainer.newContainer(new Object[] {new AnyInput(isAdv ? 8 : 1)}, null, new ItemStack[] {st}, null, 1, isAdv ? 360 : 45);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTieredTex(tier);
		return new TextureAtlasSprite[] {t, t, t, t, t, getTETex("recycler")};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++) {
			if (this.baseTile.facing == JSTUtils.getFacingFromNum(n)) {
				ret[n] = getTETex("recycler");
			} else {
				ret[n] = getTieredTex(tier);
			}
		}
		return ret;
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (this.baseTile != null && !isClient())
			pl.openGui(JustServerTweak.INSTANCE, 1, this.getWorld(), this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
		return true;
	}

	@Override
	protected void onStartWork() {
		getWorld().playSound(null, getPos(), SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, 1.6F);
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
	protected void addSlot(ContainerGeneric cg, InventoryPlayer inv, TileEntityMeta te) {
		cg.addSlot(new Slot(te, 0, 53, 35));
		cg.addSlot(new JSTSlot(te, 1, 107, 35, false, true, 64, true));
		cg.addSlot(new BatterySlot(te, 2, 8, 53, false, true));
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void addComp(GUIGeneric gg) {
		gg.addSlot(53, 35, 0);
		gg.addSlot(107, 35, 0);
		
		gg.addPrg(76, 35);
		
		gg.addSlot(8, 53, 2);
		gg.addPwr(12, 31);
	}
	
	@Override
	public boolean onScrewDriver(EntityPlayer pl, EnumFacing f, double px, double py, double pz) {
		if (tier < 3) return false;
		mode++;
		if (mode > 2) mode = 0;
		JSTUtils.sendMessage(pl, "jst.msg.recycler." + mode);
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getInformation(ItemStack st, World w, List<String> ls, ITooltipFlag adv) {
		if (tier >= 3) ls.add(I18n.format("jst.tooltip.tile.com.sd.rs"));
	}
	
	public static class AnyInput implements IRecipeItem {
		private final int cnt;
		
		private AnyInput(int count) {
			cnt = count;
		}

		@Override
		public int getcount() {
			return cnt;
		}
	
		@Override
		public boolean matches(ItemStack in) {
			return true;
		}
	
		@Override
		public boolean isValid() {
			return true;
		}
	
		@Override
		public List<ItemStack> getAllMatchingItems() {
			return Arrays.asList(new ItemStack[] {new ItemStack(Blocks.COBBLESTONE, cnt)});
		}
	
		@Override
		public String getJEITooltip() {
			return "";
		}
	}
}
