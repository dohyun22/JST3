package dohyun22.jst3.compat.ic2;

import java.util.List;

import ic2.api.reactor.IReactor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BehaviourEnrichable extends ReactorItemBehaviour {
	private final ItemStack result;
	
	public BehaviourEnrichable(int dmg, ItemStack result) {
		maxDamage = dmg;
		this.result = result;
	}
	
	@Override
	public boolean canBePlaced(ItemStack st, IReactor re) {
		return true;
	}
	
	@Override
	public boolean acceptPulse(ItemStack st, IReactor re, ItemStack ps, int yX, int yY, int pX, int pY, boolean hr) {
		if (hr) {
			int d = getDamage(st) + 1;
			if (d >= getMaxDamage(st)) {
				re.setItemAt(yX, yY, result.copy());
			} else {
				setDamage(st, d);
			}
		}
		return true;
	}
	
	@Override
	public double getDisplayDamage(ItemStack st) {
		int dmg = getMaxDamage(st);
		if (dmg > 0)
			return 1.0D - ((double)getDamage(st) / (double)dmg);
		return 0.0D;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack st, World w, List ls, ITooltipFlag adv) {
		ls.add(this.getDamage(st) + " / " + this.getMaxDamage(st));
	}
}
