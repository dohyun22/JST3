package dohyun22.jst3.compat.ic2;

import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorComponent;
import net.minecraft.item.ItemStack;

public class BehaviourVentComponent extends ReactorItemBehaviour {
	public final int vent;

	public BehaviourVentComponent(int amt) {
		this.vent = amt;
	}
	
	@Override
	public boolean canBePlaced(ItemStack st, IReactor re) {
		return true;
	}

	@Override
	public void processChamber(ItemStack st, IReactor re, int x, int y, boolean hr) {
		if (hr) {
			cool(re, x - 1, y);
			cool(re, x + 1, y);
			cool(re, x, y - 1);
			cool(re, x, y + 1);
		}
	}

	private void cool(IReactor re, int x, int y) {
		ItemStack st = re.getItemAt(x, y);
		if (st == null || !(st.getItem() instanceof IReactorComponent))
			return;
		IReactorComponent c = (IReactorComponent) st.getItem();
		if (c.canStoreHeat(st, re, x, y)) {
			int h = c.alterHeat(st, re, x, y, -this.vent);
			if (h <= 0)
				re.addEmitHeat(h + this.vent);
		}
	}
}
