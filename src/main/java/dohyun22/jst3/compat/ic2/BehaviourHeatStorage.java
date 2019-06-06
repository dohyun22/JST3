package dohyun22.jst3.compat.ic2;

import java.util.List;

import ic2.api.reactor.IReactor;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BehaviourHeatStorage extends ReactorItemBehaviour {
	
	public BehaviourHeatStorage(int cap) {
		maxDamage = cap;
	}
	
	@Override
	public boolean canBePlaced(ItemStack st, IReactor re) {
		return true;
	}

	@Override
	public boolean canStoreHeat(ItemStack st, IReactor re, int x, int y) {
		return true;
	}

	@Override
	public int getMaxHeat(ItemStack st, IReactor re, int x, int y) {
		return getMaxDamage(st);
	}

	@Override
	public int getCurrentHeat(ItemStack st, IReactor re, int x, int y) {
		return getDamage(st);
	}

	@Override
	public int alterHeat(ItemStack st, IReactor re, int x, int y, int heat) {
		int myHeat = getCurrentHeat(st, re, x, y);
		myHeat += heat;
		int max = getMaxHeat(st, re, x, y);
		if (myHeat > max) {
			re.setItemAt(x, y, null);
			heat = max - myHeat + 1;
		} else {
			if (myHeat < 0) {
				heat = myHeat;
				myHeat = 0;
			} else {
				heat = 0;
			}
			setDamage(st, myHeat);
		}
		return heat;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack st, World w, List ls, ITooltipFlag adv) {
		int max = this.getMaxDamage(st);
		ls.add((max - this.getDamage(st)) + " / " + max );
		if (getDamage(st) > 0) {
			ls.add(I18n.format("ic2.reactoritem.heatwarning.line1"));
			ls.add(I18n.format("ic2.reactoritem.heatwarning.line2"));
		}
	}
}
