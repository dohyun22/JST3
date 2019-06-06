package dohyun22.jst3.compat.ic2;

import java.util.List;
import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorComponent;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BehaviorNeutDoubler extends ReactorItemBehaviour {

	public BehaviorNeutDoubler() {
		maxDamage = 50000;
	}

	@Override
	public boolean acceptPulse(ItemStack st, IReactor re, ItemStack ps, int yX, int yY, int pX, int pY, boolean hr) {
		if (!hr) {
			IReactorComponent src = (IReactorComponent) ps.getItem();
			src.acceptUraniumPulse(ps, re, st, pX, pY, yX, yY, hr);
			src.acceptUraniumPulse(ps, re, st, pX, pY, yX, yY, hr);
		} else if (getDamage(st) + 1 >= getMaxDamage(st)) {
			re.setItemAt(yX, yY, null);
		} else {
			setDamage(st, getDamage(st) + 1);
		}
		return true;
	}

	@Override
	public float influenceExplosion(ItemStack st, IReactor re) {
		return -1.0F;
	}

	@Override
	public boolean canBePlaced(ItemStack st, IReactor re) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack st, World w, List ls, ITooltipFlag adv) {
		int max = this.getMaxDamage(st);
		ls.add((max - this.getDamage(st)) + " / " + max );
	}
}
