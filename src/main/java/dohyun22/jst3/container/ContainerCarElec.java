package dohyun22.jst3.container;

import dohyun22.jst3.entity.EntityCarElec;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerCarElec extends Container {
	private EntityCarElec car;

	public ContainerCarElec(IInventory pi, EntityCarElec car) {
		this.car = car;
		addSlotToContainer(new BatterySlot(car, 0, 80, 35, false, true));
	    for (int r = 0; r < 3; r++)
	    	for (int c = 0; c < 9; c++)
	    		addSlotToContainer(new Slot(pi, c + r * 9 + 9, 8 + c * 18, 84 + r * 18));
	    for (int h = 0; h < 9; h++)
	    	addSlotToContainer(new Slot(pi, h, 8 + h * 18, 84 + 58));
	}

	@Override
	public boolean canInteractWith(EntityPlayer pl) {
		return car != null && !car.isDead;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer pl, int fs) {
	    ItemStack st = ItemStack.EMPTY;
	    Slot sl = (Slot) inventorySlots.get(fs);

	    if (sl != null && sl.getHasStack()) {
	        ItemStack current = sl.getStack();
	        st = current.copy();
	        int ss = car.getSizeInventory();
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
}
