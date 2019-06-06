package dohyun22.jst3.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class InventoryChecker implements IInventory {
	private static final int size = 54;
	public int page = 1;
	public final IInventory target;
	
	public InventoryChecker(IInventory tgt) {
		this.target = tgt;
	}

	@Override
	public int getSizeInventory() {
		return size;
	}

	@Override
	public ItemStack getStackInSlot(int sl) {
		sl += (page - 1) * size;
		return sl < 0 || sl >= target.getSizeInventory() ? ItemStack.EMPTY : target.getStackInSlot(sl);
	}

	@Override
	public ItemStack decrStackSize(int sl, int amt) {
		sl += (page - 1) * size;
		return sl < 0 || sl >= target.getSizeInventory() ? ItemStack.EMPTY : target.decrStackSize(sl, amt);
	}

	@Override
	public ItemStack removeStackFromSlot(int sl) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int sl, ItemStack st) {
		sl += (page - 1) * size;
		if (sl >= 0 && sl < target.getSizeInventory())
			target.setInventorySlotContents(sl, st);
	}

	@Override
	public String getName() {
		return "Checker";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return target.getInventoryStackLimit();
	}

	@Override
	public void markDirty() {
		target.markDirty();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer pl) {
		return target.isUsableByPlayer(pl);
	}

	@Override
	public void openInventory(EntityPlayer pl) {}

	@Override
	public void closeInventory(EntityPlayer pl) {}

	@Override
	public boolean isItemValidForSlot(int sl, ItemStack st) {
		sl += (page - 1) * size;
		//System.out.println("SlotNum: " + sl);
		return sl < 0 || sl >= target.getSizeInventory() ? false : target.isItemValidForSlot(sl, st);
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
