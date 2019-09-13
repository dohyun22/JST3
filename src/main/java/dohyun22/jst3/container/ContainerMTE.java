package dohyun22.jst3.container;

import javax.annotation.Nullable;

import dohyun22.jst3.network.JSTPacketHandler;
import dohyun22.jst3.tiles.TileEntityMeta;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerMTE extends Container {
	public TileEntityMeta te;
	protected int ss;

	public ContainerMTE (TileEntityMeta te) {
		this.te = te;
		ss = te.getSizeInventory();
	}
	
	public ContainerMTE (TileEntityMeta te, int sz) {
		this.te = te;
		ss = sz;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer ep) {
		return this.te.isUsableByPlayer(ep);
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer pl, int fs) {
	    ItemStack st = ItemStack.EMPTY;
	    Slot sl = (Slot) this.inventorySlots.get(fs);

	    if (sl != null && sl.getHasStack()) {
	        ItemStack current = sl.getStack();
	        st = current.copy();

	        if (fs < ss) {
	            if (!mergeItemStack(current, ss, Math.min(ss + 36, inventorySlots.size()), true))
	                return ItemStack.EMPTY;
	        } else {
	            if (!mergeItemStack(current, 0, ss, false))
	                return ItemStack.EMPTY;
	        }

	        if (current.getCount() == 0)
	            sl.putStack(ItemStack.EMPTY);
	        else
	            sl.onSlotChanged();

	        if (current.getCount() == st.getCount())
	            return ItemStack.EMPTY;
	        sl.onTake(pl, current);
	    }
	    return st;
	}

	protected void addPlayerInventorySlots(IInventory inv, int sx, int sy) {
	    for (int r = 0; r < 3; r++)
	    	for (int c = 0; c < 9; c++)
	    		addSlotToContainer(new Slot(inv, c + r * 9 + 9, sx + c * 18, sy + r * 18));
	    for (int h = 0; h < 9; h++)
	    	addSlotToContainer(new Slot(inv, h, sx + h * 18, sy + 58));
	}

	public static void splitLongAndSend(IContainerListener icl, Container con, int s, long in) {
		icl.sendWindowProperty(con, s, (short) (in & 0xFFFF));
		icl.sendWindowProperty(con, s + 1, (short) (in >> 16 & 0xFFFF));
		icl.sendWindowProperty(con, s + 2, (short) (in >> 32 & 0xFFFF));
		icl.sendWindowProperty(con, s + 3, (short) (in >> 48 & 0xFFFF));
	}

	public static long getLongFromData(long org, int s, int id, int data) {
		if (id == s) {return (org & 0xFFFFFFFFFFFF0000L | (long)(data));}
		if (id == s + 1) {return (org & 0xFFFFFFFF0000FFFFL | (long)(data) << 16);}
		if (id == s + 2) {return (org & 0xFFFF0000FFFFFFFFL | (long)(data) << 32);}
		if (id == s + 3) {return (org & 0xFFFFFFFFFFFFL | (long)(data) << 48);}
		return org;
	}

	public static void splitIntAndSend(IContainerListener icl, Container con, int s, int in) {
	      icl.sendWindowProperty(con, s, in & 0xFFFF);
	      icl.sendWindowProperty(con, s + 1, in >>> 16);
	}

	public static int getIntFromData(int org, int s, int id, int data) {
		if (id == s) {return (org & 0xFFFF0000 | data);}
		if (id == s + 1) {return (org & 0xFFFF | data << 16);}
		return org;
	}

	public static void combineBytesAndSend(IContainerListener icl, Container con, int s, byte a, byte b) {
		icl.sendWindowProperty(con, s, ((short)a << 8) + (short)b);
	}
}
