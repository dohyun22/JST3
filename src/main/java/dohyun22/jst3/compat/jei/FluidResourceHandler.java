package dohyun22.jst3.compat.jei;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class FluidResourceHandler implements IRecipeHandler<FluidResourceWrapper> {

	@Override
	public Class<FluidResourceWrapper> getRecipeClass() {
		return FluidResourceWrapper.class;
	}

	@Override
	public String getRecipeCategoryUid(FluidResourceWrapper recipe) {
		return null;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(FluidResourceWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(FluidResourceWrapper recipe) {
		return true;
	}

}
