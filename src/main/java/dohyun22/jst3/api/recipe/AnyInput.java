package dohyun22.jst3.api.recipe;

import java.util.Arrays;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class AnyInput implements IRecipeItem {
	private final int cnt;
	
	public AnyInput(int count) {
		cnt = count;
	}

	@Override
	public int getcount() {
		return cnt;
	}

	@Override
	public boolean matches(ItemStack in) {
		return true;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public List<ItemStack> getAllMatchingItems() {
		return Arrays.asList(new ItemStack(Blocks.COBBLESTONE, cnt));
	}

	@Override
	public String getJEITooltip() {
		return "";
	}
}