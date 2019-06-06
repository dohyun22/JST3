package dohyun22.jst3.api;

import net.minecraft.item.ItemStack;

/** Interface for JEU Supporting Items. */
public interface IItemJEU {
	public long injectEU(ItemStack st, long jeu, int tier, boolean itl, boolean sim);
	public long extractEU(ItemStack st, long jeu, int tier, boolean itl, boolean sim);
	public long getEU(ItemStack st);
	public long getMaxEU(ItemStack st);
}
