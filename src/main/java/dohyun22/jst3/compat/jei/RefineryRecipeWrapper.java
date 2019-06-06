package dohyun22.jst3.compat.jei;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import dohyun22.jst3.api.recipe.RecipeContainer;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.utils.JSTFluids;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class RefineryRecipeWrapper extends GenericRecipeWrapper {
	public byte enabled;

	public RefineryRecipeWrapper(RecipeContainer rec) {
		super(rec);
	}

	@Override
	public void getIngredients(IIngredients ing) {
		super.getIngredients(ing);
		if (enabled == 0) {
			FluidStack[] fs = container.getOutputFluids();
			if (fs != null)
				for (int n = 0; n < fs.length; n++)
					if (fs[n] != null)
						enabled += 1 << n;
		}
	}

	@Override
	public int getOutputNum() {
		return 4;
	}

	@Override
	public int getFOutputNum() {
		return 8;
	}

	public static List<RefineryRecipeWrapper> make() {
	    List<RefineryRecipeWrapper> ret = new LinkedList();
	    for (RecipeContainer rec : MRecipes.RefineryRecipes.list)
	    	ret.add(new RefineryRecipeWrapper(rec));
	    return ret;
	}
}
