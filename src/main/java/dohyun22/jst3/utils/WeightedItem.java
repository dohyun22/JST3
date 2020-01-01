package dohyun22.jst3.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;

public class WeightedItem extends WeightedRandom.Item {
	private final ItemStack item;

	public WeightedItem(ItemStack st, int w) {
		super(w);
		item = st;
	}

	public ItemStack getStack() {
		return item == null ? ItemStack.EMPTY : item.copy();
	}
}