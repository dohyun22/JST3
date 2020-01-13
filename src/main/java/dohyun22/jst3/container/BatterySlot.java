package dohyun22.jst3.container;

import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class BatterySlot extends Slot {
	private final boolean charge;
	private final boolean discharge;

	public BatterySlot(IInventory inv, int index, int xp, int yp, boolean charge, boolean discharge) {
		super(inv, index, xp, yp);
		this.charge = charge;
		this.discharge = discharge;
	}

	@Override
	public boolean isItemValid(ItemStack in) {
		in = in.copy();
		in.setCount(1);
		return (JSTUtils.chargeItem(in, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) > 0 && charge) || (JSTUtils.dischargeItem(in, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) > 0 && discharge);
	}
}
