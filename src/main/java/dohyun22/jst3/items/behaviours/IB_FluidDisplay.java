package dohyun22.jst3.items.behaviours;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class IB_FluidDisplay extends ItemBehaviour {
	
	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		String fn = st.hasTagCompound() ? st.getTagCompound().getString("FluidName") : null;
		int amt = st.hasTagCompound() ? st.getTagCompound().getInteger("Amount") : 0;
		if (fn == null || amt == 0) {
			ls.add("EMPTY");
			return;
		}
		Fluid f = FluidRegistry.getFluid(fn);
		if (f != null)
			ls.add(f.getLocalizedName(new FluidStack(f, amt)) + " " + amt + "mB");
	}
	
	@Override
	public void getSubItems(ItemStack st, List<ItemStack> sub) {}
}
