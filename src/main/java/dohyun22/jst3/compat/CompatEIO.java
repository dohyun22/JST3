package dohyun22.jst3.compat;

import dohyun22.jst3.api.recipe.OreDictStack;
import dohyun22.jst3.loader.Loadable;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.utils.JSTFluids;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public class CompatEIO extends Loadable {

	@Override
	public String getRequiredMod() {
		return "enderio";
	}

	@Override
	public void postInit() {
		Object o1 = new OreDictStack("ingotIron"), o2 = new ItemStack(Items.ENDER_PEARL);
		MRecipes.addAlloyFurnaceRecipe(o1, new OreDictStack("dustRedstone"), JSTUtils.getFirstItem("ingotConductiveIron"), 5, 200);
		MRecipes.addAlloyFurnaceRecipe(o1, o2, JSTUtils.getFirstItem("ingotPulsatingIron"), 10, 200);
		MRecipes.addAlloyFurnaceRecipe(new OreDictStack("ingotGold"), new ItemStack(Blocks.SOUL_SAND), JSTUtils.getFirstItem("ingotSoularium"), 10, 200);
		MRecipes.addAlloyFurnaceRecipe(new OreDictStack("ingotEnergeticAlloy"), o2, JSTUtils.getFirstItem("ingotVibrantAlloy"), 10, 200);
		MRecipes.addAlloyFurnaceRecipe(new OreDictStack("ingotSteel"), new OreDictStack("dustObsidian", 4), JSTUtils.getFirstItem("ingotDarkSteel"), 10, 200);
		addFluidFuel(JSTFluids.fuel, 200, 10000);
		addFluidFuel(JSTFluids.nitrofuel, 200, 20480);
		addFluidFuel(JSTFluids.lng, 64, 20000);
		addFluidFuel(JSTFluids.lpg, 64, 25000);
	}

	public static void addFluidFuel(Fluid f, int rf, int t) {
		NBTTagCompound n = new NBTTagCompound();
		n.setString("fluidName", f.getName());
		n.setInteger("powerPerCycle", rf);
		n.setInteger("totalBurnTime", t);
		FMLInterModComms.sendMessage("enderio", "fluidFuel:add", n);
	}
}
