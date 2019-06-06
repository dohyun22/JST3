package dohyun22.jst3.compat.jei;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class GenericRecipeHandler implements IRecipeHandler<GenericRecipeWrapper> {

	@Override
    public Class<GenericRecipeWrapper> getRecipeClass() {
        return GenericRecipeWrapper.class;
    }
    
	@Override
    public String getRecipeCategoryUid(GenericRecipeWrapper recipe) {
        return null;
    }
    
	@Override
    public IRecipeWrapper getRecipeWrapper(GenericRecipeWrapper recipe) {
        return (IRecipeWrapper) recipe;
    }
    
	@Override
    public boolean isRecipeValid(GenericRecipeWrapper recipe) {
        return true;
    }
}
