package dohyun22.jst3.recipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.recipe.IRecipeItem;
import dohyun22.jst3.api.recipe.OreDictStack;
import dohyun22.jst3.api.recipe.RecipeContainer;
import dohyun22.jst3.api.recipe.RecipeList;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.oredict.OreDictionary;

public class MRecipes {
	/** Key: Fluid name, Value: Energy value per 1mB */
	public static final HashMap<String, Integer> DieselGenFuel = new HashMap();
	/** Key: Fluid name, Value: Energy value per 1mB */
	public static final HashMap<String, Integer> GasGenFuel = new HashMap();
	/** Key: Fluid name, Value: Energy value per 1mB */
	public static final HashMap<String, Integer> HeatGenFuel = new HashMap();
	/** Key: Fluid name, Value: Energy value per 1mB */
	public static final HashMap<String, Integer> AcceptableSteam = new HashMap();
	public static final List<String> allowedFuelerFuels = new ArrayList();

	public static final RecipeList AssemblerRecipes = new RecipeList("assembler");
	public static final RecipeList SeparatorRecipes = new RecipeList("separator");
	public static final RecipeList AlloyFurnaceRecipes = new RecipeList("alloyfurnace");
	public static final RecipeList ChemMixerRecipes = new RecipeList("chemmixer");
	public static final RecipeList DisassemblerRecipes = new RecipeList("disassembler");
	public static final RecipeList PressRecipes = new RecipeList("press");
	public static final RecipeList CrystalRecipes = new RecipeList("crystalizer");
	public static final RecipeList SHFurnaceRecipes = new RecipeList("shfurnace");
	public static final RecipeList CokeOvenRecipes = new RecipeList("cokeoven");
	public static final RecipeList OreProcessRecipes = new RecipeList("oregrinder");
	public static final RecipeList RefineryRecipes = new RecipeList("refinery");
	public static final RecipeList FusionRecipes = new RecipeList("fusion");
	public static final RecipeList FusionBreederRecipes = new RecipeList("fusionbreeder");
	public static final RecipeList HeatExcFakeRecipes = new RecipeList("heatexc");
	public static final RecipeList BioRecipes = new RecipeList("bioprocess");
	/** Key: ItemStack, OreDictStack, or String name of the Material. Value: Mass value per Item. Denser Item (i.e gold, osmium) will have higher value. */
	public static final HashMap<Object, Integer> CompressorValue = new HashMap();
	public static final HashMap<Object, Integer> MagicGenFuel = new HashMap();
	public static final ArrayList<ItemStack> NuclearItems = new ArrayList();
	public static final ArrayList<ItemStack> Fertilizers = new ArrayList();
	public static final ArrayList<Block> LEDCrops = new ArrayList();
    
    @Nullable
    public static RecipeContainer getRecipe(RecipeList recipe, ItemStack[] input, FluidTank[] finput, int tier, boolean sl, boolean fsl) {
		if (recipe != null)
			for (RecipeContainer rc : recipe.list)
				if (rc != null && rc.process(input, finput, tier, sl, fsl, false))
					return rc;
    	return null;
    }
    
    public static RecipeContainer addAssemblerRecipe(Object[] recipe, @Nullable FluidStack fin, @Nullable ItemStack out1, @Nullable ItemStack out2, int energy, int tick) {
    	if (recipe == null || recipe.length == 0 || recipe.length > 9 || !isValid(out1)) return null;
    	if (recipe.length > 0 && recipe.length < 9) {
    		Object[] r = new Object[9];
    		for (int n = 0; n < recipe.length; n++)
    			r[n] = recipe[n];
    		recipe = r;
    	}
    	
    	RecipeContainer ret = RecipeContainer.newContainer(recipe, new FluidStack[] {fin}, new ItemStack[] {out1, out2}, null, energy, tick);
    	MRecipes.AssemblerRecipes.add(ret);
    	return ret;
    }
    
    public static void addAlloyFurnaceRecipe(Object in1, Object in2, ItemStack out, int energy, int tick) {
    	if (isValid(in1) && isValid(out))
    		MRecipes.AlloyFurnaceRecipes.add(RecipeContainer.newContainer(isValid(in2) ? new Object[] {in1, in2} : new Object[] {in1}, null, new ItemStack[] {out}, null, energy, tick));
    }
    
    public static void addSeparatorRecipe(Object in1, Object in2, FluidStack fin, ItemStack[] out, FluidStack fout, int energy, int tick) {
		boolean flag = false;
		if (out == null || out.length <= 0 || out.length > 6) {
			out = new ItemStack[6];
		} else {
			ItemStack[] r;
			int n;
			if (out.length < 6) {
				r = new ItemStack[6];
				for (n = 0; n < out.length; n++) {
					r[n] = out[n];
					if (isValid(r[n]))
						flag = true;
				}
				out = r;
			} else {
				for (ItemStack st : out) {
					if (isValid(st)) {
						flag = true;
						break;
					}
				}
			}
		}
    	if ((isValid(in1) || isValid(fin)) && (flag || isValid(fout)))
    		MRecipes.SeparatorRecipes.add(RecipeContainer.newContainer(isValid(in2) ? new Object[] {in1, in2} : new Object[] {in1}, new FluidStack[] {fin}, out, new FluidStack[] {fout}, energy, tick));
    }
    
    public static void addChemMixerRecipe(Object[] in, FluidStack fin, ItemStack out1, ItemStack out2, FluidStack fout, int energy, int tick) {
		boolean flag = false;
		if (in != null && in.length > 0 && in.length <= 6) {
			for (Object obj : in) {
				if (isValid(obj)) {
					flag = true;
					break;
				}
			}
		}
		if ((flag || isValid(fin)) && (isValid(out1) || isValid(out2) || isValid(fout)))
    		MRecipes.ChemMixerRecipes.add(RecipeContainer.newContainer(in, new FluidStack[] {fin}, new ItemStack[] {out1, out2}, new FluidStack[] {fout}, energy, tick));
    }
	
	public static void addDisassemblerRecipe(Object in, ItemStack[] out, int energy, int tick) {
		boolean flag = false;
		if (out == null || out.length <= 0 || out.length > 9) {
			out = new ItemStack[9];
		} else {
			ItemStack[] r;
			int n;
			if (out.length < 9) {
				r = new ItemStack[9];
				for (n = 0; n < out.length; n++) {
					r[n] = out[n];
					if (isValid(r[n]))
						flag = true;
				}
				out = r;
			} else {
				for (ItemStack st : out) {
					if (isValid(st)) {
						flag = true;
						break;
					}
				}
			}
		}
		if (isValid(in) && flag)
			MRecipes.DisassemblerRecipes.add(RecipeContainer.newContainer(new Object[] {in}, null, out, null, energy, tick));
	}
    
    public static void addFusionRecipe(FluidStack fs1, FluidStack fs2, FluidStack out, int energy, int tick, int start, int production, boolean neutronic) {
    	if (isValid(fs1) && isValid(fs2) && isValid(out))
    		MRecipes.FusionRecipes.add(RecipeContainer.newContainer(null, new FluidStack[] {fs1, fs2}, null, new FluidStack[] {out}, energy, tick, start, production, neutronic));
    }
    
    public static void addFusionBreederRecipe(Object in, ItemStack out) {
    	if (isValid(in) && isValid(out))
    		MRecipes.FusionBreederRecipes.add(RecipeContainer.newContainer(new Object[] {in}, null, new ItemStack[] {out}, null, 0, 0));
    }
    
    public static void addSHFurnaceRecipe(Object[] in, ItemStack[] out, int energy, int tick) {
    	if (in == null || out == null || in.length <= 0 || out.length <= 0) return;
    	for (Object obj : in) if (!isValid(obj)) return;
    	for (ItemStack st : out) if (!isValid(st)) return;
    	MRecipes.SHFurnaceRecipes.add(RecipeContainer.newContainer(in, null, out, null, energy, tick));
    }

    public static void addCokeOvenRecipe(Object in, ItemStack out, FluidStack fout, int energy, int tick) {
    	if (isValid(in) && isValid(out))
    		MRecipes.CokeOvenRecipes.add(RecipeContainer.newContainer(new Object[] {in}, null, new ItemStack[] {out}, new FluidStack[] {fout}, energy, tick));
    }

    public static void addOreProcessRecipe(Object[] in, FluidStack fin, ItemStack[] out, int energy, int tick) {
    	if (in != null && in.length > 0 && out != null && out.length > 0 && isValid(in[0]) && isValid(out[0]))
    		MRecipes.OreProcessRecipes.add(RecipeContainer.newContainer(in, new FluidStack[] {fin}, out, null, energy, tick));
    }

    public static void addPressRecipe(Object in1, Object in2, ItemStack out1, ItemStack out2, int energy, int tick) {
    	if (isValid(in1) && isValid(out1))
    		MRecipes.PressRecipes.add(RecipeContainer.newContainer(isValid(in2) ? new Object[] {in1, in2} : new Object[] {in1}, null, new ItemStack[] {out1, out2}, null, energy, tick));
    }

    public static void addRefineryRecipe(FluidStack in, FluidStack[] out, ItemStack[] bp, int energy, int tick) {
    	if (in == null || out == null || out.length <= 0 || out.length > 8) return;
    	int n = 0;
    	for (FluidStack fs : out) if (!isValid(fs)) n++;
    	if (n >= out.length) return;
    	MRecipes.RefineryRecipes.add(RecipeContainer.newContainer(null, new FluidStack[] {in}, bp, out, energy, tick));
    }

    public static void addCrystalRecipe(Object in1, Object in2, FluidStack fin, ItemStack out, int energy, int tick) {
    	if (isValid(in1) && isValid(out))
    		MRecipes.CrystalRecipes.add(RecipeContainer.newContainer(new Object[] {in1, in2}, new FluidStack[] {fin}, new ItemStack[] {out}, null, energy, tick));
    }

    public static void addBioRecipe(Object in, FluidStack fin, ItemStack out, FluidStack fout, int energy, int tick) {
    	if ((isValid(in) || isValid(fin)) && (isValid(fout) || isValid(out)))
    		MRecipes.BioRecipes.add(RecipeContainer.newContainer(new Object[] {in}, new FluidStack[] {fin}, new ItemStack[] {out}, new FluidStack[] {fout}, energy, tick));
    }

    public static void addDieselFuel(String name, int e) {
    	addFluidFuel(DieselGenFuel, name, e);
    }
    
    public static void addGasFuel(String name, int e) {
    	addFluidFuel(GasGenFuel, name, e);
    }
    
    public static void addHeatFuel(String name, int e) {
    	addFluidFuel(HeatGenFuel, name, e);
    }
    
    public static void addSteam(String name, int e) {
    	addFluidFuel(AcceptableSteam, name, e);
    }
    
    public static void addMagicFuel(Object obj, int e) {
    	if (obj instanceof String) obj = new OreDictStack((String)obj);
    	else if (obj instanceof Item) obj = new ItemStack((Item)obj, 1, 32767);
    	else if (obj instanceof Block) obj = new ItemStack((Block)obj, 1, 32767);
    	if (!(obj instanceof FluidStack) && isValid(obj)) MagicGenFuel.put(obj, e);
    }
    
    public static void addFertilizer(ItemStack in) {
    	if (in != null && !in.isEmpty())
    		Fertilizers.add(in);
    }
    
	public static boolean isFertilizer(ItemStack in) {
		for (ItemStack st : Fertilizers)
			if (OreDictionary.itemMatches(in, st, false))
				return true;
		return false;
	}

    public static void addLEDCrop(Block in) {
    	if (in != null && in != Blocks.AIR)
    		LEDCrops.add(in);
    }
    
    public static void addFluidFuel(Map<String, Integer> map, String name, int e) {
    	if (name == null) return;
    	Fluid f = FluidRegistry.getFluid(name);
    	if (f != null)
    		map.put(name, Integer.valueOf(e));
    }
    
    public static boolean isValid(Object obj) {
    	if (obj instanceof ItemStack) {
    		return !((ItemStack)obj).isEmpty();
    	} else if (obj instanceof IRecipeItem) {
    		return ((IRecipeItem)obj).isValid();
    	} else if (obj instanceof FluidStack) {
    		return ((FluidStack)obj).amount > 0;
    	}
    	return false;
    }

	public static int getMagicFuelValue(ItemStack in) {
		if (in == null || in.isEmpty()) return 0;
		for (Object o : MRecipes.MagicGenFuel.keySet()) {
			Integer ret = MRecipes.MagicGenFuel.get(o);
			if (ret == null) return 0;
			if (o instanceof ItemStack && ((ItemStack)o).getCount() <= in.getCount() && OreDictionary.itemMatches((ItemStack)o, in, false)) return ret;
			if (o instanceof OreDictStack)
				for (int id : OreDictionary.getOreIDs(in))
					if (id == OreDictionary.getOreID(((OreDictStack)o).name))
						return ret;
		}
		return 0;
	}
}
