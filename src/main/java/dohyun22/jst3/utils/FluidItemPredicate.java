package dohyun22.jst3.utils;

import com.google.common.base.Predicate;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

public class FluidItemPredicate implements Predicate<ItemStack> {
	private final String fluidName;
	
	public FluidItemPredicate(String name) {
		fluidName = name;
	}
	
	@Override
	public boolean apply(ItemStack in) {
		FluidStack fs = FluidUtil.getFluidContained(in);
		return fs != null && fluidName.equals(fs.getFluid().getName());
	}
}