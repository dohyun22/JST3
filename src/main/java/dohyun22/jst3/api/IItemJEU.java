package dohyun22.jst3.api;

import net.minecraft.item.ItemStack;

/** Interface for JEU Supporting Items. */
public interface IItemJEU {
	long injectEU(ItemStack st, long jeu, int tier, boolean itl, boolean sim);
	long extractEU(ItemStack st, long jeu, int tier, boolean itl, boolean sim);
	long getEU(ItemStack st);
	long getMaxEU(ItemStack st);
	int getEnergyTier(ItemStack st);
}
