package dohyun22.jst3.recipes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dohyun22.jst3.api.IItemJEU;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class JSTCraftingRecipe {

	public static class ShapedRecipe extends ShapedOreRecipe {
		public ShapedRecipe(ResourceLocation rl, ItemStack out, Object... rec) {
			super(rl, out, rec);
			replaceIC2Wires(input);
		}

		@Override
		@Nonnull
		public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
			ItemStack ret = output.copy();
			charge(inv, ret);
			return ret;
		}
	}

	public static class ShapelessRecipe extends ShapelessOreRecipe {
		public ShapelessRecipe(ResourceLocation rl, ItemStack out, Object... rec) {
			super(rl, out, rec);
			replaceIC2Wires(input);
		}

		@Override
		@Nonnull
		public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
			ItemStack ret = output.copy();
			charge(inv, ret);
			return ret;
		}
	}

	private static void charge(@Nonnull InventoryCrafting inv, ItemStack st) {
		if (st != null && st.getItem() instanceof IItemJEU) {
			long eu = 0;
			for (int i = 0; i < inv.getSizeInventory(); i++)
				eu += JSTUtils.getEUInItem(inv.getStackInSlot(i));
			JSTUtils.chargeItem(st, eu, Integer.MAX_VALUE, true, false);
		}
	}

	private static class CableIngredient extends Ingredient {

		private CableIngredient(Ingredient in) {
			super(in.getMatchingStacks());
		}
		
		@Override
		public boolean apply(@Nullable ItemStack in) {
			if (in == null) {
				return false;
			} else {
				for (ItemStack st : getMatchingStacks()) {
					if (st.getItem() == in.getItem()) {
						if (st.getMetadata() == 32767) {
							return true;
						} else {
							NBTTagCompound t1 = st.getTagCompound(), t2 = in.getTagCompound();
							byte ty1 = t1 != null ? t1.getByte("type") : 0, ty2 = t2 != null ? t2.getByte("type") : 0;
							byte in1 = t1 != null ? t1.getByte("insulation") : 0, in2 = t2 != null ? t2.getByte("insulation") : 0;
							if (ty1 == ty2 && in1 == in2)
								return true;
						}
					}
				}
				return false;
			}
		}
	}

	private static void replaceIC2Wires(NonNullList<Ingredient> in) {
		if (JSTCfg.ic2Loaded && in != null && ItemList.cables[1] != null && !ItemList.cables[1].isEmpty()) {
			Item cable = ItemList.cables[1].getItem();
			for (int n = 0; n < in.size(); n++) {
				Ingredient ing = in.get(n);
				ItemStack[] st = ing.getMatchingStacks();
				if (st.length > 0 && st[0].getItem() == cable)
					in.set(n, new CableIngredient(ing));
			}
		}
	}
}
