package dohyun22.jst3.container;

import java.util.Iterator;

import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerChecker extends Container {
	// private InventoryItem inv;
	public int maxPage;
	public InventoryChecker inv;
	private IInventory playerinv;
	private IInventory dummy;
	public int pg;
	public int sz;
	private int pg2;
	private int sz2;

	public ContainerChecker(IInventory pi, IInventory te) {
		this.inv = new InventoryChecker(te);
		this.playerinv = pi;
		this.dummy = new InventoryDummy();

		addSlotToContainer(new JSTSlot(this.dummy, 0, 8, 200, false, false, 1, false));
		addSlotToContainer(new JSTSlot(this.dummy, 0, 152, 200, false, false, 1, false));

		for (int n = 0; n < 54; n++) {
			addSlotToContainer(new CustomSlot(this.inv, n, 8 + 18 * (n % 9), 8 + 18 * (n / 9)));
		}

		// inv
		for (int r = 0; r < 3; r++) {
			for (int c = 0; c < 9; c++) {
				addSlotToContainer(new Slot(playerinv, c + r * 9 + 9, 8 + c * 18, 120 + r * 18));
			}
		}
		for (int h = 0; h < 9; h++) {
			addSlotToContainer(new Slot(playerinv, h, 8 + h * 18, 178));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer ep) {
		return inv != null && inv.isUsableByPlayer(ep);
	}

	@Override
	public ItemStack slotClick(int si, int mc, ClickType sh, EntityPlayer pl) {
		if (si < 0 || si >= this.inventorySlots.size()) {
			return null;
		}

		if (!pl.world.isRemote) {
			if (si == 0) {
				this.inv.page = Math.max(1, this.inv.page - 1);
				super.detectAndSendChanges();
			} else if (si == 1) {
				this.inv.page = Math.min((int)(Math.ceil(this.inv.target.getSizeInventory() / 54.0F)), this.inv.page + 1);
				super.detectAndSendChanges();
				// System.out.println("size:" + inv.getSizeInventory() + ", " +
				// (inv.getSizeInventory() / 27));
			}
		}
		return super.slotClick(si, mc, sh, pl);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer pl, int fs) {
		return ItemStack.EMPTY;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		if (JSTUtils.isClient()) {
			return;
		}

		this.pg = this.inv.page;
		this.sz = this.inv.target.getSizeInventory();

		Iterator cr = this.listeners.iterator();
		while (cr.hasNext()) {
			IContainerListener p = (IContainerListener) cr.next();
			if (this.pg2 != this.pg)
				p.sendWindowProperty(this, 50, this.pg);
			if (this.sz2 != this.sz)
				p.sendWindowProperty(this, 51, this.sz);
		}

		this.pg2 = this.pg;
		this.sz2 = this.sz;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		if (id == 50) {
			pg = data;
			//System.out.println("page: " + this.inv.page);
		} else if (id == 51) {
			sz = data;
			//System.out.println("page: " + this.inv.page);
		}
	}

	private class CustomSlot extends Slot {
		public CustomSlot(IInventory inv, int index, int px, int py) {
			super(inv, index, px, py);
		}

		@Override
		public boolean isItemValid(ItemStack st) {
			return getSlotIndex() < (sz - ((pg - 1) * 54));
			//return JSTUtils.jso_17032() ? lim : this.inventory.isItemValidForSlot(getSlotIndex(), st);
		}
	}
}
