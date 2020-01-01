package dohyun22.jst3.tiles.machine;

import java.util.ArrayList;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.recipe.OreDictStack;
import dohyun22.jst3.api.recipe.RecipeContainer;
import dohyun22.jst3.api.recipe.RecipeList;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.BatterySlot;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.container.JSTSlot;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_Assembler extends MT_MachineProcess {

	private boolean forced;

	public MT_Assembler(int tier) {
		super(tier, 9, 2, 1, 0, 64000, MRecipes.AssemblerRecipes, false, false, "assembler", null);
		setSfx(JSTSounds.SWITCH2, 0.6F, 1.0F);
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_Assembler(tier);
	}

	@Override
	protected void addSlot(ContainerGeneric cg, InventoryPlayer inv, TileEntityMeta te) {
		for (int x = 0; x < 3; x++)
			for (int y = 0; y < 3; y++)
				cg.addSlot(new Slot(te, y + x * 3, 44 + y * 18, 7 + x * 18));
		cg.addSlot(new JSTSlot(te, 9, 130, 25, false, true, 64, true));
		cg.addSlot(new JSTSlot(te, 10, 148, 25, false, true, 64, true));
		cg.addSlot(new JSTSlot(te, 11, 62, 63, false, true, 64, false));
		cg.addSlot(new BatterySlot(te, 12, 8, 53, false, true));
		cg.addPlayerSlots(inv);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void addComp(GUIGeneric gg) {
		for (int x = 0; x < 3; x++)
			for (int y = 0; y < 3; y++)
				gg.addSlot(44 + y * 18, 7 + x * 18, 0);
		gg.addSlot(130, 25, 0);
		gg.addSlot(148, 25, 0);
		gg.addSlot(62, 63, 3);
		gg.addPrg(101, 25, JustServerTweak.MODID + ".assembler");
		gg.addSlot(8, 53, 2);
		gg.addPwr(12, 31);
		gg.addButton(128, 50, 20, 20, 1, "->", false);
		gg.addHoverText(128, 50, 20, 20, "jst.msg.com.build");
		gg.addCfg(7, 7, true);
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

	@Override
	public void handleBtn(int id, EntityPlayer pl) {
		if (!forced && baseTile != null && !baseTile.isActive())
			forced = true;
	}
}
