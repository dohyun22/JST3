package dohyun22.jst3.compat.ic2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ChunkPos;

public class BehaviourHeatExc extends BehaviourHeatStorage {
	  public final int side;
	  public final int reactor;
	  
	public BehaviourHeatExc(int cap, int ss, int sr) {
		super(cap);
		this.side = ss;
		this.reactor = sr;
	}

	@Override
	public void processChamber(ItemStack st, IReactor re, int x, int y, boolean hr) {
		if (!hr) return;
		
		int heat = 0;
		HashMap<ChunkPos, ItemStack> hm = new HashMap();
		if (this.side > 0) {
			check(re, x - 1, y, hm);
			check(re, x + 1, y, hm);
			check(re, x, y - 1, hm);
			check(re, x, y + 1, hm);
		}
		if (this.side > 0) {
			for (Entry<ChunkPos, ItemStack> et : hm.entrySet()) {
				IReactorComponent rc = (IReactorComponent) et.getValue().getItem();

				double tmp = getCurrentHeat(st, re, x, y) * 100.0D / getMaxHeat(st, re, x, y);
				double ctm = rc.getCurrentHeat(et.getValue(), re, et.getKey().x, et.getKey().z) * 100.0D / rc.getMaxHeat(et.getValue(), re, et.getKey().x, et.getKey().z);

				int add = (int) (rc.getMaxHeat(et.getValue(), re, et.getKey().x, et.getKey().z) / 100.0D
						* (ctm + tmp / 2.0D));
				if (add > side)
					add = side;
				if (ctm + tmp / 2.0D < 1.0D)
					add = side / 2;
				if (ctm + tmp / 2.0D < 0.75D)
					add = side / 4;
				if (ctm + tmp / 2.0D < 0.5D)
					add = side / 8;
				if (ctm + tmp / 2.0D < 0.25D)
					add = 1;
				if (Math.round(ctm * 10.0D) / 10.0D > Math.round(tmp * 10.0D) / 10.0D)
					add -= 2 * add;
				else if (Math.round(ctm * 10.0D) / 10.0D == Math.round(tmp * 10.0D) / 10.0D)
					add = 0;
				heat -= add;
				add = rc.alterHeat(et.getValue(), re, et.getKey().x, et.getKey().z, add);
				heat += add;
			}
		}
		if (this.reactor > 0) {
			double tmp = getCurrentHeat(st, re, x, y) * 100.0D / getMaxHeat(st, re, x, y);
			double rtm = re.getHeat() * 100.0D / re.getMaxHeat();

			int add = (int) Math.round(re.getMaxHeat() / 100.0D * (rtm + tmp / 2.0D));
			if (add > this.reactor) {
				add = this.reactor;
			}
			if (rtm + tmp / 2.0D < 1.0D) {
				add = this.side / 2;
			}
			if (rtm + tmp / 2.0D < 0.75D) {
				add = this.side / 4;
			}
			if (rtm + tmp / 2.0D < 0.5D) {
				add = this.side / 8;
			}
			if (rtm + tmp / 2.0D < 0.25D) {
				add = 1;
			}
			if (Math.round(rtm * 10.0D) / 10.0D > Math.round(tmp * 10.0D) / 10.0D) {
				add -= 2 * add;
			} else if (Math.round(rtm * 10.0D) / 10.0D == Math.round(tmp * 10.0D) / 10.0D) {
				add = 0;
			}
			heat -= add;
			re.setHeat(re.getHeat() + add);
		}
		alterHeat(st, re, x, y, heat);
	}

	private void check(IReactor re, int x, int y, HashMap<ChunkPos, ItemStack> map) {
		ItemStack st = re.getItemAt(x, y);
		if (st != null && (st.getItem() instanceof IReactorComponent) && ((IReactorComponent)st.getItem()).canStoreHeat(st, re, x, y))
			map.put(new ChunkPos(x, y), st);
	}
}
