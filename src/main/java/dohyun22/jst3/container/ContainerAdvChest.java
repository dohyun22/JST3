package dohyun22.jst3.container;

import java.util.Set;

import com.google.common.collect.Sets;

import dohyun22.jst3.tiles.device.MT_AdvChest;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerAdvChest extends ContainerMTE {
	private final IInventory dummy;
	public boolean secured;

	public ContainerAdvChest(IInventory inv, TileEntityMeta te) {
		super(te);

	    for (int r = 0; r < 6; r++) {
	    	for (int c = 0; c < 9; c++) {
	    		addSlotToContainer(new Slot(te, c + r * 9 + 1, 8 + c * 18, 8 + r * 18));
	    	}
	    }
		
		addPlayerInventorySlots(inv, 8, 120);
		
		this.dummy = new InventoryDummy();
		addSlotToContainer(new JSTSlot(this.dummy, 0, 8, 200, false, false, 1, false));
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer pl, int fs) {
	    ItemStack st = ItemStack.EMPTY;
	    Slot sl = (Slot) this.inventorySlots.get(fs);

	    if (sl != null && sl.getHasStack()) {
	        ItemStack current = sl.getStack();
	        st = current.copy();

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

	@Override
	public ItemStack slotClick(int si, int dt, ClickType ct, EntityPlayer pl) {
	    if (!(te.mte instanceof MT_AdvChest)) {
	        return ItemStack.EMPTY;
	    }
	    
	    if (si < 0) return super.slotClick(si, dt, ct, pl);
	    
    	Slot sl = this.getSlot(si);
	    //System.out.println("Slot " + si + ", Mode " + dt + ", Type " + ct);
    	//System.out.println("Stack: " + (sl == null ? "NULL" : sl.getStack().hasTagCompound() ? sl.getStack().getTagCompound().getLong(MetaTileAdvChest.numTagName) :  "0"));
	    
    	
    	if (sl.inventory == te) {
	        ItemStack st = pl.inventory.getItemStack();
	        if (ct == ClickType.CLONE) {
	        	if (pl.capabilities.isCreativeMode && st.isEmpty()) {
		            if (sl != null && sl.getHasStack()) {
		                ItemStack st2 = sl.getStack().copy();
		                if (!MT_AdvChest.isItemEmpty(st2)) {
		                	st2.getTagCompound().removeTag(MT_AdvChest.numTagName);
		                	if (st2.getTagCompound().hasNoTags()) st2.setTagCompound(null);
		                	st2.setCount(st2.getMaxStackSize());
		                	pl.inventory.setItemStack(st2);
		                	pl.inventoryContainer.detectAndSendChanges();
		                }
		            }
	        	}
	            return ItemStack.EMPTY;
		    } else if ((ct == ClickType.PICKUP || ct == ClickType.QUICK_CRAFT || ct == ClickType.QUICK_MOVE) && (dt == 0 || dt == 1)) {
		        if (!st.isEmpty()) {
		        	if (dt == 1) {
		        		ItemStack st2 = st.copy();
		        		st2.setCount(1);
		        		if (((MT_AdvChest)te.mte).insertItemToSlot(sl.getSlotIndex(), st2).isEmpty())
		        			pl.inventory.getItemStack().shrink(1);
		        	} else {
		        		st = ((MT_AdvChest)te.mte).insertItemToSlot(sl.getSlotIndex(), st);
		        		pl.inventory.setItemStack(st);
		        	}
		        } else {
		        	int amt = 64;
		        	if (dt == 1) {
		        		ItemStack in = ((MT_AdvChest)te.mte).inv.get(sl.getSlotIndex());
		        		if (!MT_AdvChest.isItemEmpty(in))
		        			amt = (int)(Math.min(in.getItem().getItemStackLimit(in), in.getTagCompound().getLong(MT_AdvChest.numTagName)) + 1) / 2;
		        	}
		        	if (amt > 0) {
		        		ItemStack st2 = ((MT_AdvChest)te.mte).getItemFromSlot(sl.getSlotIndex(), amt);
		        		pl.inventory.setItemStack(st2);
		        		//return st2;
		        	}
		        }
	        	sl.onSlotChanged();
		    }
	    	return ItemStack.EMPTY;
	    }//*/
	    
    	if (sl.inventory == pl.inventory) {
	    	if (ct == ClickType.QUICK_MOVE) {
	    		ItemStack st = sl.getStack();
	    		for (int n = 1; n < te.mte.inv.size(); n++) {
	    			st = ((MT_AdvChest)te.mte).insertItemToSlot(n, st);
					if (st.isEmpty()) break;
					te.markDirty();
	    		}
				sl.inventory.setInventorySlotContents(sl.getSlotIndex(), st);
    			this.detectAndSendChanges();
				return st;
	    	} else if (ct == ClickType.PICKUP_ALL) {
	    		return ItemStack.EMPTY;
	    	}
	    }//*/
	    
	    //if (ct == ClickType.QUICK_MOVE) {return ItemStack.EMPTY;}
	    
	    if (!pl.world.isRemote && sl.inventory == this.dummy) {
	    	int n = sl.getSlotIndex();
	    	if (n == 0 && ((MT_AdvChest)te.mte).haveAccess(pl, true, false)) {
	    		((MT_AdvChest)te.mte).secured = !((MT_AdvChest)te.mte).secured;
	    	}
	    	return ItemStack.EMPTY;
	    }
	    
		return super.slotClick(si, dt, ct, pl);
	}
	
	@Override
	public void detectAndSendChanges() {
        super.detectAndSendChanges();
		if (JSTUtils.isClient()) return;
		
		if (this.te.getWorld().isRemote ||!(te.mte instanceof MT_AdvChest)) {
			return;
		}
		
		MT_AdvChest r = (MT_AdvChest)te.mte;
		
		for (int i = 0; i < this.listeners.size(); ++i) {
			IContainerListener icl = (IContainerListener)this.listeners.get(i);
			icl.sendWindowProperty(this, 100, r.secured ? 1 : 0);
		}
		
		this.secured = r.secured;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		if (id == 100) this.secured = data != 0;
	}
	
	@Override
	public boolean canDragIntoSlot(Slot s) {
		return s.inventory != te;
	}
	
	/*private void sendSlotData(int id, ItemStack st) {
        for (int j = 0; j < this.listeners.size(); ++j) {
            ((IContainerListener)this.listeners.get(j)).sendSlotContents(this, id, st);
        }
	}*/
}
