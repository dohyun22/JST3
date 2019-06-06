package dohyun22.jst3.compat.ic2;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import dohyun22.jst3.utils.JSTUtils;
import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorComponent;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BehaviourFuelRod extends ReactorItemBehaviour {
	protected final int numOfCell;
	private final float energyOutput;
	private final float heatOutput;
	private final boolean isMox;
	private final int rad;
	@Nonnull
	private final ItemStack depleted;
	
	public BehaviourFuelRod(int cellNum, int dmg, float output, float heat, ItemStack depleted, boolean isMOX, int radio) {
		this.numOfCell = cellNum;
		this.maxDamage = dmg;
		this.energyOutput = output;
		this.heatOutput = heat;
		this.depleted = depleted == null ? ItemStack.EMPTY : depleted;
		this.isMox = isMOX;
		this.rad = radio;
	}
	
	@Override
	public boolean canBePlaced(ItemStack st, IReactor re) {
		return true;
	}
	
	@Override
	public void processChamber(ItemStack st, IReactor re, int x, int y, boolean hr) {
		if (!re.produceEnergy())
			return;
		for (int n = 0; n < this.numOfCell; n++) {
			int pulses = 1 + this.numOfCell / 2;
			if (!hr) {
				for (int i = 0; i < pulses; i++)
					acceptPulse(st, re, st, x, y, x, y, hr);
				checkPulseable(re, x - 1, y, st, x, y, hr);
				checkPulseable(re, x + 1, y, st, x, y, hr);
				checkPulseable(re, x, y - 1, st, x, y, hr);
				checkPulseable(re, x, y + 1, st, x, y, hr);
			} else {
				pulses += checkPulseable(re, x - 1, y, st, x, y, hr) + checkPulseable(re, x + 1, y, st, x, y, hr) + checkPulseable(re, x, y - 1, st, x, y, hr) + checkPulseable(re, x, y + 1, st, x, y, hr);
				int ht = triangularNumber(pulses) * 4;
				ht = getFinalHeat(st, re, x, y, ht);
				ArrayList<ItemStackCoord> HA = new ArrayList();
				checkHeatAcceptor(re, x - 1, y, HA);
				checkHeatAcceptor(re, x + 1, y, HA);
				checkHeatAcceptor(re, x, y - 1, HA);
				checkHeatAcceptor(re, x, y + 1, HA);
				ht = Math.round(ht * this.heatOutput);
				while (HA.size() > 0 && ht > 0) {
					int dheat = ht / HA.size();
					ht -= dheat;
					dheat = ((IReactorComponent) ((ItemStackCoord) HA.get(0)).stack.getItem()).alterHeat(((ItemStackCoord) HA.get(0)).stack, re, ((ItemStackCoord) HA.get(0)).x, ((ItemStackCoord) HA.get(0)).y, dheat);
					ht += dheat;
					HA.remove(0);
				}
				if (ht > 0) re.addHeat(ht);
			}
		}
		int dmg = getDamage(st);
		if (dmg >= this.maxDamage - 1) {
			re.setItemAt(x, y, depleted.isEmpty() ? ItemStack.EMPTY : depleted.copy());
		} else if (hr) {
			setDamage(st, dmg + 1);
		}
	}
	
	@Override
	public boolean acceptPulse(ItemStack st, IReactor re, ItemStack ps, int x, int y, int px, int py, boolean hr) {
		if (!hr) {
			if (isMox)
				re.addOutput(4.0F * ((float)re.getHeat() / (float)re.getMaxHeat()) + 1.0F * energyOutput);
			else
				re.addOutput(energyOutput);
		}
		return true;
	}
	
	@Override
	public int alterHeat(ItemStack st, IReactor re, int x, int y, int h) {
		return h;
	}

	@Override
	public float influenceExplosion(ItemStack yourStack, IReactor reactor) {
		return 2 * numOfCell * Math.max(energyOutput, heatOutput);
	}
	
	@Override
	public int getRadiation(ItemStack st) {
		return rad;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack st, World w, List ls, ITooltipFlag adv) {
		int max = this.getMaxDamage(st);
		ls.add((max - this.getDamage(st)) + " / " + max );
	}
	
	private static byte checkPulseable(IReactor reactor, int x, int y, ItemStack me, int mex, int mey, boolean hr) {
		ItemStack other = reactor.getItemAt(x, y);
		if (other != null && (other.getItem() instanceof IReactorComponent) && ((IReactorComponent) other.getItem()).acceptUraniumPulse(other, reactor, me, x, y, mex, mey, hr)) {
			return 1;
		}
		return 0;
	}

	private int getFinalHeat(ItemStack stack, IReactor re, int x, int y, int heat) {
		if (isMox && re.isFluidCooled()) {
			float eff = (float)re.getHeat() / (float)re.getMaxHeat();
			if (eff > 0.5D)
				heat *= 2;
		}
		return heat;
	}
	  
	private void checkHeatAcceptor(IReactor re, int x, int y, ArrayList<ItemStackCoord> ha) {
		ItemStack st = re.getItemAt(x, y);
		if (st != null && (st.getItem() instanceof IReactorComponent) && ((IReactorComponent) st.getItem()).canStoreHeat(st, re, x, y))
			ha.add(new ItemStackCoord(st, x, y));
	}
	  
	private static int triangularNumber(int x) {
		return (x * x + x) / 2;
	}

	private class ItemStackCoord {
		public ItemStack stack;
		public int x;
		public int y;

		public ItemStackCoord(ItemStack stack1, int x1, int y1) {
			this.stack = stack1;
			this.x = x1;
			this.y = y1;
		}
	}
}
