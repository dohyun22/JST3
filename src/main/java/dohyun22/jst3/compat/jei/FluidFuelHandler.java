package dohyun22.jst3.compat.jei;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class FluidFuelHandler implements IRecipeHandler<FluidFuelRecipeWrapper> {

	@Override
	public Class<FluidFuelRecipeWrapper> getRecipeClass() {
		return FluidFuelRecipeWrapper.class;
	}

	@Override
	public String getRecipeCategoryUid(FluidFuelRecipeWrapper recipe) {
		return null;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(FluidFuelRecipeWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(FluidFuelRecipeWrapper recipe) {
		return true;
	}

}
