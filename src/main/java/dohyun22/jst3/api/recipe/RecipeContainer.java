package dohyun22.jst3.api.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dohyun22.jst3.api.JSTConstants;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.oredict.OreDictionary;

public class RecipeContainer {
	private final Object[] in;
	private final FluidStack[] fin;
	private final ItemStack[] out;
	private final FluidStack[] fout;
	private final int energy;
	private final int duration;
	private final Object[] obj;
	
	private RecipeContainer(Object[] input, FluidStack[] finput, ItemStack[] output, FluidStack[] foutput, int engy, int time, Object[] obj) {
		if (input != null) {
			for (int n = 0; n < input.length; n++) {
				Object o = input[n];
				if (o instanceof Item)
					input[n] = new ItemStack((Item)o);
				else if (o instanceof Block)
					input[n] = new ItemStack((Block)o);
				else if (o instanceof String)
					input[n] = new OreDictStack((String)o);
			}
		}
		this.in = input;
		this.fin = finput;
		this.out = output;
		this.fout = foutput;
		this.energy = engy;
		this.duration = time;
		this.obj = obj;
	}
	
	public static RecipeContainer newContainer(Object[] input, FluidStack[] finput, ItemStack[] output, FluidStack[] foutput, int engy, int dura) {
		return newContainer(input, finput, output, foutput, engy, dura, new Object[0]);
	}
	
	public static RecipeContainer newContainer(Object[] input, FluidStack[] finput, ItemStack[] output, FluidStack[] foutput, int engy, int dura, Object... obj) {
		if ((input == null && finput == null) || (output == null && foutput == null)) throw new IllegalArgumentException("Wrong recipe");
		return new RecipeContainer(input, finput, output, foutput, engy, dura, obj.length == 0 ? null : obj);
	}
	
	public boolean process(ItemStack[] input, FluidTank[] finput, int tier, boolean shapeless, boolean fshapeless, boolean consume) {
		if (energy > 0 && tier < getTier(energy) || (in == null) != (input == null) || (fin == null) != (finput == null)) return false;
		
		if (in != null && input != null) {
			if (shapeless) {
	            boolean[] flags = new boolean[input.length];
				for (int n = 0; n < in.length; n++) {
					boolean flag = true;
					for (int m = 0; m < input.length; m++) {
						if (flags[m]) continue;
						if (matches(in[n], input[m])) {
							flag = false;
							flags[m] = true;
							if (consume)
								input[m].shrink(getCnt(in[n]));
							break;
						}
					}
					if (flag) return false;
				}
			} else if (in.length == input.length) {
				for (int n = 0; n < in.length; n++) {
					if (!matches(in[n], input[n]))
						return false;
					if (consume)
						input[n].shrink(getCnt(in[n]));
				}
			}
		}
		if (fin != null && finput != null) {
			if (fshapeless) {
				boolean[] flags = new boolean[finput.length];
				for (int n = 0; n < fin.length; n++) {
					boolean flag = true;
					for (int m = 0; m < finput.length; m++) {
						if (flags[m]) continue;
						if (fmatches(fin[n], finput[m].getFluid())) {
							flag = false;
							flags[m] = true;
							if (consume && fin[n] != null)
								finput[m].drainInternal(fin[n].amount, true);
							break;
						}
					}
					if (flag) return false;
				}
			} else if (fin.length == finput.length) {
				for (int n = 0; n < fin.length; n++) {
					if (!fmatches(fin[n], finput[n].getFluid()))
						return false;
					if (consume && fin[n] != null)
						finput[n].drainInternal(fin[n].amount, true);
				}
			}
		}
		
		return true;
	}
	
	private int getCnt(Object obj) {
		if (obj instanceof ItemStack)
			return ((ItemStack)obj).getCount();
		if (obj instanceof IRecipeItem)
			return ((IRecipeItem)obj).getcount();
		return 0;
	}

	public Object[] getInputItems() {
		return in;
	}
	
	public FluidStack[] getInputFluids() {
		return fin;
	}

	public ItemStack[] getOutputItems() {
		return out;
	}
	
	public FluidStack[] getOutputFluids() {
		return fout;
	}
	
	public int getEnergyPerTick() {
		return energy;
	}
	
	public int getDuration() {
		return duration;
	}
	
	@Nullable
	public Object[] getObj() {
		return this.obj;
	}

	@Override
	public String toString() {
		return Arrays.deepToString(in) + "," + Arrays.deepToString(fin) + "," + Arrays.deepToString(out) + "," + Arrays.deepToString(fout) + "," + energy + "EU," + duration + "t," + Arrays.deepToString(obj);
	}
	
	public static boolean matches(@Nullable Object rec, @Nonnull ItemStack in) {
		if (rec == null) return in.isEmpty();
		if (rec instanceof ItemStack) {
			ItemStack st = ((ItemStack)rec);
			if ((st.isEmpty() && in.isEmpty()) || (st.getCount() <= in.getCount() && OreDictionary.itemMatches(st, in, false))) return true;
		}
		if (rec instanceof IRecipeItem)
			return ((IRecipeItem)rec).matches(in) && ((IRecipeItem)rec).getcount() <= in.getCount();
		return false;
	}
	
	public static boolean fmatches(FluidStack rec, FluidStack in) {
		if (rec == null) return true;
		if (in == null) return false;
		return rec.isFluidEqual(in) && rec.amount <= in.amount;
	}

	public static int getTier(int volt) {
		for (int n = 0; n < JSTConstants.V.length; n++)
			if (volt <= JSTConstants.V[n])
				return n;
		return 0;
	}
}
