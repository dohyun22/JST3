package dohyun22.jst3.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

/** Dummy Inventory. can be used for deleting Items.*/
public class InventoryDummy implements IInventory {
	@Override
	public int getSizeInventory() {
		return 1;
	}
	
	@Override
	public ItemStack getStackInSlot(int i) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public ItemStack decrStackSize(int i, int j) {
	    return ItemStack.EMPTY;
	}
	
	@Override
	public ItemStack removeStackFromSlot(int i) {
		return ItemStack.EMPTY;
	}
	  
	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {}
	  
	@Override
	public String getName() {
	    return "InventoryDummy";
	}
	  
	@Override
	public boolean hasCustomName() {
		return false;
	}
	  
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}
	  
	@Override
	public void markDirty() {}
	  
	@Override
	public boolean isUsableByPlayer(EntityPlayer pl) {
		return true;
	}
	  
	@Override
	public void openInventory(EntityPlayer pl) {}
	  
	@Override
	public void closeInventory(EntityPlayer pl) {}
	  
	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(this.getName());
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
	}
}
