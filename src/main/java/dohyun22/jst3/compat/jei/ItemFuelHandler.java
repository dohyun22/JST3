package dohyun22.jst3.compat.jei;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class ItemFuelHandler implements IRecipeHandler<ItemFuelRecipeWrapper> {

	@Override
	public Class<ItemFuelRecipeWrapper> getRecipeClass() {
		return ItemFuelRecipeWrapper.class;
	}

	@Override
	public String getRecipeCategoryUid(ItemFuelRecipeWrapper recipe) {
		return null;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(ItemFuelRecipeWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(ItemFuelRecipeWrapper recipe) {
		return true;
	}

}
