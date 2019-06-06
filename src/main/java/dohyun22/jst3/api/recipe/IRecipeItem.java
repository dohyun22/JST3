package dohyun22.jst3.api.recipe;

import java.util.List;

import net.minecraft.item.ItemStack;

public interface IRecipeItem {
	public int getcount();
	public boolean matches(ItemStack in);
	public boolean isValid();
	public List<ItemStack> getAllMatchingItems();
	public String getJEITooltip();
}
