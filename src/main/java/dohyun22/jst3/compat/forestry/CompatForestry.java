package dohyun22.jst3.compat.forestry;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.recipe.OreDictStack;
import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.compat.forestry.ItemIridiumFrame;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.loader.Loadable;
import dohyun22.jst3.loader.RecipeLoader;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.utils.JSTFluids;
import dohyun22.jst3.utils.JSTUtils;
import dohyun22.jst3.utils.ReflectionUtils;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.BackpackManager;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CompatForestry extends Loadable {
	public static Item IridiumFrame = null;

	@Override
	public String getRequiredMod() {
		return "forestry";
	}

	@Override
	@Method(modid = "forestry")
	public void preInit() {
		IridiumFrame = new ItemIridiumFrame();
		ForgeRegistries.ITEMS.register(IridiumFrame);
		if (JSTUtils.isClient()) ModelLoader.setCustomModelResourceLocation(IridiumFrame, 0, new ModelResourceLocation(JustServerTweak.MODID + ":iridiumframe", "inventory"));
	}

	@Override
	@Method(modid = "forestry")
	public void postInit() {
		MRecipes.addFertilizer(JSTUtils.getModItemStack("forestry:fertilizer_compound"));
		MRecipes.addFertilizer(JSTUtils.getModItemStack("forestry:fertilizer_bio"));
		MRecipes.addCokeOvenRecipe(JSTUtils.getModItemStack("forestry:wood_pile", 4), new ItemStack(Items.COAL, 32, 1), FluidRegistry.getFluidStack("creosote", 4000), 64, 300);

		FluidStack fs = FluidRegistry.getFluidStack("bio.ethanol", 4000), fs2;
		ItemStack st = new ItemStack(JSTItems.item1, 1, 9017);
		if (fs != null) {
			MRecipes.addChemMixerRecipe(new Object[] {st}, fs, new ItemStack(JSTItems.item1, 8, 105), new ItemStack(JSTItems.item1, 1, 9000), null, 30, 500);
			MRecipes.addChemMixerRecipe(new Object[] {st, new ItemStack(JSTItems.item1, 4, 9028)}, null, new ItemStack(JSTItems.item1, 8, 105), new ItemStack(JSTItems.item1, 5, 9000), null, 30, 500);
		}
		
		fs = FluidRegistry.getFluidStack("ic2distilled_water", 10);
	    if (fs != null) RecipeManagers.stillManager.addRecipe(200, new FluidStack(FluidRegistry.WATER, 10), fs);
		
		st = JSTUtils.getModItemStack("forestry:can", 16);
		if (!st.isEmpty()) {
			RecipeLoader.addShapedRecipe(st, " I ", "I I", 'I', "ingotAluminum");
			RecipeLoader.addShapedRecipe(st, " I ", "I I", 'I', "ingotZinc");
		}
		
		st = JSTUtils.getModItemStack("forestry:frame_impregnated");
		if (!st.isEmpty() && CompatForestry.IridiumFrame != null) {
			ItemStack st2 = JSTUtils.getModItemStack("ic2:crafting", 1, 4);
			RecipeLoader.addShapedRecipe(new ItemStack(CompatForestry.IridiumFrame), 
					"FFF", "FIF", "FFF",
					'F', st,
					'I', st2.isEmpty() ? "ingotIridium" : st2
					);
		}

	    if (JSTCfg.DFHL) {
			Block b = JSTUtils.getModBlock("forestry:beehives");
			if (b != Blocks.AIR) b.setLightLevel(0.0f);
			b = JSTUtils.getModBlock("magicbees:hiveblock");
			if (b != Blocks.AIR) b.setLightLevel(0.0f);
			
			if (Loader.isModLoaded("magicbees")) {
				try {
					Class c = Class.forName("magicbees.bees.EnumBeeHives");
					Field f = null;
					for (Field f2 : c.getDeclaredFields()) {
						if (f2.getName().equals("light")) {
							f2.setAccessible(true);
							ReflectionUtils.removeFinal(f2);
							f = f2;
							break;
						}
					}
					String[] names = new String[] {"CURIOUS", "UNUSUAL", "RESONANT"};
					for (String str : names)
						f.set(c.getField(str).get(null), 0);
					
				} catch (Throwable t) {
					JSTUtils.LOG.error("Failed to disable hive lighting for MagicBees");
					t.printStackTrace();
				}
			}
		}
	}
}
