package dohyun22.jst3.api.recipe;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class AdvRecipeItem implements IRecipeItem {
	private final Item item;
	private final int count;
	private final int meta;
	private final NBTTagCompound nbt;
	private final Predicate<ItemStack> predicate;
	
	public AdvRecipeItem(Block in, int cnt, int m) {
		this(Item.getItemFromBlock(in), cnt, m, null, null);
	}
	
	public AdvRecipeItem(Item in, int cnt, int m) {
		this(in, cnt, m, null, null);
	}
	
	public AdvRecipeItem(Block in, int cnt, int m, @Nullable NBTTagCompound tag, @Nullable Predicate<ItemStack> pred) {
		this(Item.getItemFromBlock(in), cnt, m, tag, pred);
	}
	
	/** @param in The item
	 * @param cnt Item count. will not be consumed during process if zero.
	 * @param m Metadata
	 * @param NBT for display
	 * @param pred Predicate for finding matching item
	 * */
	public AdvRecipeItem(Item in, int cnt, int m, @Nullable NBTTagCompound tag, @Nullable Predicate<ItemStack> pred) {
		item = in;
		count = cnt;
		meta = m;
		nbt = tag;
		predicate = pred;
	}

	@Override
	public int getcount() {
		return count;
	}

	@Override
	public boolean matches(ItemStack in) {
		if (in == null || in.isEmpty()) return false;
		if (predicate != null && !predicate.apply(in)) return false;
		return in.getItem() == item && (meta == 32767 || in.getMetadata() == meta);
	}
	
	@Override
	public boolean isValid() {
		return item != null && meta >= 0;
	}

	@Override
	public List<ItemStack> getAllMatchingItems() {
		ItemStack ret = new ItemStack(item, Math.max(1, count), meta);
		ret.setTagCompound(nbt);
		return Arrays.asList(ret);
	}

	@Override
	public String getJEITooltip() {
		return count <= 0 ? I18n.format("jst.compat.jei.recipe.noconsume") : null;
	}
}
