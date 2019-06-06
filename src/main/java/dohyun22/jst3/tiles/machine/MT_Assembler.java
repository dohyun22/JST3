package dohyun22.jst3.tiles.machine;

import java.util.ArrayList;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.recipe.OreDictStack;
import dohyun22.jst3.api.recipe.RecipeContainer;
import dohyun22.jst3.api.recipe.RecipeList;
import dohyun22.jst3.client.gui.GUIAssembler;
import dohyun22.jst3.container.ContainerAssembler;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_Assembler extends MT_MachineGeneric {

	private boolean forced;

	public MT_Assembler(int tier) {
		super(tier, 9, 2, 1, 0, 16000, MRecipes.AssemblerRecipes, false, false);
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_Assembler(tier);
	}

	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		return new ContainerAssembler(inv, te);
	}

	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		return new GUIAssembler(new ContainerAssembler(inv, te));
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (this.baseTile == null || getWorld().isRemote)
			return true;
		pl.openGui(JustServerTweak.INSTANCE, 1, this.getWorld(), this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTieredTex(tier);
		return new TextureAtlasSprite[] { t, t, t, t, t, getTETex("assembler") };
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++)
			ret[n] = this.baseTile.facing == JSTUtils.getFacingFromNum(n) ? getTETex("assembler" + (this.baseTile.isActive() ? "" : "_off")) : getTieredTex(tier);
		return ret;
	}
	
	@Override
	protected void onStartWork() {
		getWorld().playSound(null, getPos(), JSTSounds.SWITCH2, SoundCategory.BLOCKS, 0.6F, 0.8F + getWorld().rand.nextFloat() * 0.6F);
	}
	
	@Override
	public boolean isItemValidForSlot(int sl, ItemStack st) {
		return super.isItemValidForSlot(sl, st) && !inv.get(sl).isEmpty();
	}
	
	@Override
	@Nullable
	protected RecipeContainer checkRecipe(RecipeList recipe, ItemStack[] in, FluidTank[] fin, int[] iout, FluidTank[] fout, boolean sl, boolean fsl) {
		RecipeContainer ret = super.checkRecipe(recipe, in, fin, iout, fout, sl, fsl);
		if (ret != null && !forced) {
			Object[] arr = ret.getInputItems();
			for (int n = 0; n < in.length; n++) {
				if (n >= arr.length) break;
				if (!isEnough(in[n], arr[n]))
					return null;
			}
		}
		return ret;
	}
	
	@Override
	protected void clear(boolean cr) {
		super.clear(cr);
		forced = false;
	}
	
	@Override
	protected boolean isInputSlot(int sl) {
		if (!super.isInputSlot(sl)) return false;
		return !inv.get(sl).isEmpty();
	}
	
	public void forceWork() {
		if (!forced && baseTile != null && !baseTile.isActive())
			forced = true;
	}
	
	private static boolean isEnough(ItemStack in, Object rec) {
		if (in.isEmpty() || rec == null) return true;
		int c = in.getCount();
		if (rec instanceof ItemStack) {
			ItemStack st = (ItemStack) rec;
			return st.isEmpty() || st.getCount() >= st.getMaxStackSize() || c > st.getCount();
		} else if (rec instanceof OreDictStack) {
			OreDictStack st = (OreDictStack) rec;
			return st.count <= 0 || st.count >= in.getMaxStackSize() || c > st.count;
		}
		return false;
	}
	
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		tag.setBoolean("forced", forced);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		forced = tag.getBoolean("forced");
	}
	
	@Override
	public void onPreTick() {
		if (isClient() || baseTile.getTimer() % 5 != 0) return;
        for (int n = 0; n < inputNum; n++) {
            ItemStack st = inv.get(n);
            if (st.isEmpty())
                continue;
            for (int o = 0; o < inputNum; o++) {
                if (n == o) continue;
                ItemStack st2 = inv.get(o);
                if (st2.isEmpty()) continue;
                if (JSTUtils.canCombine(st, st2) && st.getCount() > st2.getCount() + 1) {
                    st.shrink(1);
                    st2.grow(1);
                }
            }
        }
	}
}
