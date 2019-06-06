package dohyun22.jst3.compat.ic2;

import ic2.api.reactor.IReactor;
import net.minecraft.item.ItemStack;

public class BehaviourVent extends BehaviourHeatStorage {
	public final int selfVent;
	public final int reactorVent;

	public BehaviourVent(int cap, int vent, int rvent) {
		super(cap);
	    this.selfVent = vent;
	    this.reactorVent = rvent;
	}
	
	@Override
	public void processChamber(ItemStack st, IReactor re, int x, int y, boolean hr) {
        if (hr) {
            if (this.reactorVent > 0) {
                int rD;
                int rH = rD = re.getHeat();
                if (rD > this.reactorVent)
                    rD = this.reactorVent;
                rH -= rD;
                if ((rD = this.alterHeat(st, re, x, y, rD)) > 0)
                    return;
                re.setHeat(rH);
            }
            int self = this.alterHeat(st, re, x, y, -this.selfVent);
            if (self <= 0)
                re.addEmitHeat(self + this.selfVent);
        }
	}
}
