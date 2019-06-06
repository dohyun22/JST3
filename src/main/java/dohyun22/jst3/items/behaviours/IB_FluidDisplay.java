package dohyun22.jst3.items.behaviours;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class IB_FluidDisplay extends ItemBehaviour {
	
	@Override
	@SideOnly(Side.CLIENT)
	public List<String> getInformation(ItemStack st, World w, ITooltipFlag adv) {
		List<String> ret = new ArrayList();
		String fn = st.hasTagCompound() ? st.getTagCompound().getString("FluidName") : null;
		int amt = st.hasTagCompound() ? st.getTagCompound().getInteger("Amount") : 0;
		if (fn == null || amt == 0) {
			ret.add("EMPTY");
			return ret;
		}
		Fluid f = FluidRegistry.getFluid(fn);
		if (f != null)
			ret.add(f.getLocalizedName(new FluidStack(f, amt)) + " " + amt + "mB");
		return ret;
	}
	
	@Override
	public void getSubItems(ItemStack st, List<ItemStack> sub) {}
}
