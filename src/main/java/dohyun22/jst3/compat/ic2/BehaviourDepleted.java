package dohyun22.jst3.compat.ic2;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class BehaviourDepleted extends ReactorItemBehaviour {
	private final int radPower;
	
	public BehaviourDepleted(int power) {
		radPower = power;
	}
	
	@Override
	public int getRadiation(ItemStack st) {
		return radPower;
	}
}
