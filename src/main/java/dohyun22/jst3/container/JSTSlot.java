package dohyun22.jst3.container;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import dohyun22.jst3.api.recipe.OreDictStack;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class JSTSlot extends Slot {
	public final int si;
	public boolean cii;
	public boolean csi;
	public boolean cts;
	public int mss = 127;
	public boolean hov;
	@Nullable
	private Predicate<ItemStack> predicate;
	
	public JSTSlot(IInventory inv, int index, int xDp, int yDp) {
		this(inv, index, xDp, yDp, true, true, 64, true, true);
	}
	  
	public JSTSlot(IInventory inv, int index, int xDp, int yDp, boolean canInsert, boolean canStack, int maxSize, boolean canTake) {
		this(inv, index, xDp, yDp, canInsert, canStack, maxSize, canTake, true);
	}
	
	public JSTSlot(IInventory inv, int index, int xDp, int yDp, boolean canInsert, boolean canStack, int maxSize, boolean canTake, boolean canHover) {
		super(inv, index, xDp, yDp);
	    this.cii = canInsert;
	    this.csi = canStack;
	    this.mss = maxSize;
	    this.cts = canTake;
	    this.si = index;
	    this.hov = canHover;
	}
	
	public JSTSlot setPredicate(Predicate<ItemStack> p) {
		this.predicate = p;
		return this;
	}
	
	
	@Override
	public boolean isItemValid(ItemStack st) {
		if (!this.cii) return false;
		if (predicate != null) return predicate.apply(st);
		return true;
	}
	  
	@Override
	public int getSlotStackLimit() {
		return this.mss;
	}
	  
	@Override
	public ItemStack decrStackSize(int par1) {
		if (!this.csi) {
			return ItemStack.EMPTY;
		}
		return super.decrStackSize(par1);
	}
	  
	@Override
	public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
		return cts;
	}
	
    @SideOnly(Side.CLIENT)
    @Override
    public boolean isEnabled() {
        return hov;
    }

    public static class ItemMatcher implements Predicate<ItemStack> {
    	private final Object[] obj;
    	public ItemMatcher(Object... o) {
    		obj = o;
    	}
    	
		@Override
		public boolean apply(ItemStack in) {
			if (obj != null && in != null) {
				for (Object o : obj) {
					if (o instanceof ItemStack) {
						if (OreDictionary.itemMatches((ItemStack)o, in, false)) return true;
					} else if (o instanceof Item) {
						if (in.getItem() == o) return true;
					} else if (o instanceof String) {
						if (JSTUtils.oreMatches(in, (String)o)) return true;
					} else if (o instanceof OreDictStack) {
						if (JSTUtils.oreMatches(in, ((OreDictStack)o).name)) return true;
					}
				}
			}
			return false;
		}
    }
}
