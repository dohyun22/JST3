package dohyun22.jst3.container;

import dohyun22.jst3.items.ItemJST1;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.items.behaviours.IB_Laser;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerLaser extends Container {
	private final IInventory dummy;
	private final InventoryPlayer playerinv;
	
	public byte range;
	public boolean breac;
	public boolean smelt;
	
	public ContainerLaser(InventoryPlayer pi) {
		this.playerinv = pi;
		this.dummy = new InventoryDummy();
		for (int n = 0; n < 6; n++)
			addSlotToContainer(new JSTSlot(this.dummy, n, 35 + 18 * n, 14, false, false, 1, false));
		
		addSlotToContainer(new JSTSlot(this.dummy, 6, 71, 34, false, false, 1, false));
		addSlotToContainer(new JSTSlot(this.dummy, 7, 89, 34, false, false, 1, false));
	}

	@Override
	public boolean canInteractWith(EntityPlayer pl) {
		ItemStack st = playerinv.getCurrentItem();
		return !st.isEmpty() && st.getItem() == JSTItems.item1 && ((ItemJST1)JSTItems.item1).getBehaviour(st) instanceof IB_Laser;
	}

	@Override
	public ItemStack slotClick(int si, int mc, ClickType ct, EntityPlayer pl) {
		ItemStack st = playerinv.getCurrentItem();
	    if (si < 0 || si >= this.inventorySlots.size() || st.getItem() != JSTItems.item1 || ct != ClickType.PICKUP) {
	        return ItemStack.EMPTY;
	    }
	    
	    if (!pl.world.isRemote) {
	    	NBTTagCompound tag = JSTUtils.getOrCreateNBT(st);
	    	if (si >= 0 && si <= 5) {
	    		tag.setByte("range", (byte) si);
	    	} else if (si == 6) {
	    		boolean flag = tag.getBoolean("break");
				tag.setBoolean("break", !flag);
				if (flag) tag.setBoolean("smelt", false);
	    	} else if (si == 7) {
	    		if (tag.getBoolean("break")) tag.setBoolean("smelt", !tag.getBoolean("smelt"));
	    	}
	    }
		
	    return ItemStack.EMPTY;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (JSTUtils.isClient()) return;
		NBTTagCompound tag = playerinv.getCurrentItem().getTagCompound();
		if (tag == null) return;
	    
        for (IContainerListener icl : listeners) {

            if (this.range != tag.getByte("range")) {
            	icl.sendWindowProperty(this, 50, tag.getByte("range"));
            }
            
            if (this.breac != tag.getBoolean("break")) {
            	icl.sendWindowProperty(this, 51, tag.getBoolean("break") ? 1 : 0);
            }

            if (this.smelt != tag.getBoolean("smelt")) {
            	icl.sendWindowProperty(this, 52, tag.getBoolean("smelt") ? 1 : 0);
            }
        }
        
    	this.range = tag.getByte("range");
    	this.breac = tag.getBoolean("break");
    	this.smelt = tag.getBoolean("smelt");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		switch (id) {
		case 50:
			this.range = (byte) data;
			break;
		case 51:
			this.breac = data != 0;
			break;
		case 52:
			this.smelt = data != 0;
		default:
		}
	}
}
