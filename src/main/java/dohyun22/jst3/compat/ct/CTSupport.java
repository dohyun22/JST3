package dohyun22.jst3.compat.ct;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.IngredientStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.oredict.IOreDictEntry;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import crafttweaker.mc1120.CraftTweaker;
import dohyun22.jst3.api.recipe.AdvRecipeItem;
import dohyun22.jst3.api.recipe.OreDictStack;
import dohyun22.jst3.api.recipe.RecipeContainer;
import dohyun22.jst3.api.recipe.RecipeList;
import dohyun22.jst3.evhandler.DustHandler;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.utils.JSTChunkData;
import dohyun22.jst3.utils.JSTUtils;
import dohyun22.jst3.utils.ReflectionUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.oredict.OreDictionary;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.jst.JST3")
public class CTSupport {
	private static Field ingredient;

	public static void init() {
		ingredient = ReflectionUtils.getField(IngredientStack.class, "ingredient");
	}

	@ZenMethod
	public static void addAlloyRecipe(IIngredient i1, IIngredient i2, IItemStack o, int v, int t) {
		 addLateAction(new IAction() {
			@Override public void apply() {MRecipes.addAlloyFurnaceRecipe(toOB(i1), toOB(i2), toST(o), v, t);}
			@Override public String describe() {return null;}
		 });
	}

	@ZenMethod
	public static void removeAlloyRecipe(IItemStack i1, IItemStack i2) {
		 addLateAction(new IAction() {
			 @Override public void apply() {remove(MRecipes.AlloyFurnaceRecipes, new ItemStack[] {toST(i1), toST(i2)}, null, true, true);}
			 @Override public String describe() {return null;}
		 });
	}

	@ZenMethod
	public static void addSeparatorRecipe(IIngredient i1, IIngredient i2, ILiquidStack fi, IItemStack[] o, ILiquidStack fo, int v, int t) {
		 addLateAction(new IAction() {
			 @Override public void apply() {MRecipes.addSeparatorRecipe(toOB(i1), toOB(i2), toFS(fi), toSTs(o), toFS(fo), v, t);}
			 @Override public String describe() {return null;}
		 });
	}

	@ZenMethod
	public static void removeSeparatorRecipe(IItemStack i1, IItemStack i2, ILiquidStack fi) {
		addLateAction(new IAction() {
			 @Override public void apply() {remove(MRecipes.SeparatorRecipes, new ItemStack[] {toST(i1), toST(i2)}, new FluidStack[] {toFS(fi)}, true, true);}
			 @Override public String describe() {return null;}
		});
	}

	@ZenMethod
	public static void addChemMixerRecipe(IIngredient[] i, ILiquidStack fi, IItemStack o1, IItemStack o2, ILiquidStack fo, int v, int t) {
		addLateAction(new IAction() {
			 @Override public void apply() {MRecipes.addChemMixerRecipe(toOBs(i), toFS(fi), toST(o1), toST(o2), toFS(fo), v, t);}
			 @Override public String describe() {return null;}
		 });
	}

	@ZenMethod
	public static void removeChemMixerRecipe(IItemStack[] i, ILiquidStack fi) {
		addLateAction(new IAction() {
			 @Override public void apply() {remove(MRecipes.ChemMixerRecipes, toSTs(i), new FluidStack[] {toFS(fi)}, true, true);}
			 @Override public String describe() {return null;}
		 });
	}

	@ZenMethod
	public static void addAssemblerRecipe(IIngredient[] i, ILiquidStack fi, IItemStack o1, IItemStack o2, int v, int t) {
		addLateAction(new IAction() {
			@Override public void apply() {MRecipes.addAssemblerRecipe(toOBs(i), toFS(fi), toST(o1), toST(o2), v, t);}
			@Override public String describe() {return null;}
		});
	}

	@ZenMethod
	public static void removeAssemblerRecipe(IItemStack[] i, ILiquidStack fi) {
		addLateAction(new IAction() {
			@Override public void apply() {remove(MRecipes.AssemblerRecipes, toSTs(i), new FluidStack[] {toFS(fi)}, false, true);}
			@Override public String describe() {return null;}
		});
	}

	@ZenMethod
	public static void addCokeOvenRecipe(IIngredient i, IItemStack o, ILiquidStack fo, int v, int t) {
		addLateAction(new IAction() {
			@Override public void apply() {MRecipes.addCokeOvenRecipe(toOB(i), toST(o), toFS(fo), v, t);}
			@Override public String describe() {return null;}
		});
	}

	@ZenMethod
	public static void removeCokeOvenRecipe(IItemStack i) {
		addLateAction(new IAction() {
			@Override public void apply() {remove(MRecipes.ChemMixerRecipes, new ItemStack[] {toST(i)}, null, true, true);}
			@Override public String describe() {return null;}
		});
	}

	@ZenMethod
	public static void addDisassemblerRecipe(IIngredient i, IItemStack[] o, int v, int t) {
		addLateAction(new IAction() {
			@Override public void apply() {MRecipes.addDisassemblerRecipe(toOB(i), toSTs(o), v, t);}
			@Override public String describe() {return null;}
		});
	}

	@ZenMethod
	public static void removeDisassemblerRecipe(IItemStack i) {
		addLateAction(new IAction() {
			@Override public void apply() {remove(MRecipes.DisassemblerRecipes, new ItemStack[] {toST(i)}, null, true, true);}
			@Override public String describe() {return null;}
		});
	}

	@ZenMethod
	public static void addFusionRecipe(ILiquidStack f1, ILiquidStack f2, ILiquidStack fo, int v, int t, int s, int p, boolean n) {
		addLateAction(new IAction() {
			@Override public void apply() {MRecipes.addFusionRecipe(toFS(f1), toFS(f2), toFS(fo), v, t, s, p, n);}
			@Override public String describe() {return null;}
		});
	}

	@ZenMethod
	public static void removeFusionRecipe(ILiquidStack f1, ILiquidStack f2) {
		addLateAction(new IAction() {
			@Override public void apply() {remove(MRecipes.FusionRecipes, null, new FluidStack[] {toFS(f1), toFS(f2)}, true, true);}
			@Override public String describe() {return null;}
		});
	}

	@ZenMethod
	public static void addFusionBreederRecipe(IIngredient i, IItemStack o) {
		addLateAction(new IAction() {
			@Override public void apply() {MRecipes.addFusionBreederRecipe(toOB(i), toST(o));}
			@Override public String describe() {return null;}
		});
	}

	@ZenMethod
	public static void removeFusionBreederRecipe(IItemStack i) {
		addLateAction(new IAction() {
			@Override public void apply() {remove(MRecipes.FusionBreederRecipes, new ItemStack[] {toST(i)}, null, true, true);}
			@Override public String describe() {return null;}
		});
	}

	@ZenMethod
	public static void addSHFurnaceRecipe(IIngredient[] i, IItemStack[] o, int v, int t) {
		addLateAction(new IAction() {
			@Override public void apply() {MRecipes.addSHFurnaceRecipe(toOBs(i), toSTs(o), v, t);}
			@Override public String describe() {return null;}
		});
	}

	@ZenMethod
	public static void removeSHFurnaceRecipe(IItemStack[] i) {
		addLateAction(new IAction() {
			@Override public void apply() {remove(MRecipes.SHFurnaceRecipes, toSTs(i), null, true, true);}
			@Override public String describe() {return null;}
		});
	}

	@ZenMethod
	public static void addPressRecipe(IIngredient i1, IIngredient i2, IItemStack o1, IItemStack o2, int v, int t) {
		addLateAction(new IAction() {
			@Override public void apply() {MRecipes.addPressRecipe(toOB(i1), toOB(i2), toST(o1), toST(o2), v, t);}
			@Override public String describe() {return null;}
		});
	}

	@ZenMethod
	public static void removePressRecipe(IItemStack i1, IItemStack i2) {
		addLateAction(new IAction() {
			@Override public void apply() {remove(MRecipes.PressRecipes, new ItemStack[] {toST(i1), toST(i2)}, null, true, true);}
			@Override public String describe() {return null;}
		});
	}

	@ZenMethod
	public static void addCrystalRecipe(IIngredient i1, IIngredient i2, ILiquidStack fi, IItemStack o, int v, int t) {
		addLateAction(new IAction() {
			@Override public void apply() {MRecipes.addCrystalRecipe(toOB(i1), toOB(i2), toFS(fi), toST(o), v, t);}
			@Override public String describe() {return null;}
		});
	}

	@ZenMethod
	public static void removeCrystalRecipe(IItemStack i1, IItemStack i2, ILiquidStack fi) {
		addLateAction(new IAction() {
			@Override public void apply() {remove(MRecipes.CrystalRecipes, new ItemStack[] {toST(i1), toST(i2)}, new FluidStack[] {toFS(fi)}, true, true);}
			@Override public String describe() {return null;}
		});
	}

	@ZenMethod
	public static void addRefineryRecipe(ILiquidStack fi, ILiquidStack[] fo, IItemStack[] o, int v, int t) {
		addLateAction(new IAction() {
			@Override public void apply() {MRecipes.addRefineryRecipe(toFS(fi), toFSs(fo), toSTs(o), v, t);}
			@Override public String describe() {return null;}
		});
	}

	@ZenMethod
	public static void removeRefineryRecipe(ILiquidStack fi) {
		addLateAction(new IAction() {
			@Override public void apply() {remove(MRecipes.RefineryRecipes, null, new FluidStack[] {toFS(fi)}, true, true);}
			@Override public String describe() {return null;}
		});
	}

	@ZenMethod
	public static void addOreProcessRecipe(IIngredient[] ii, ILiquidStack fi, IItemStack[] io, int v, int t) {
		addLateAction(new IAction() {
			@Override public void apply() {MRecipes.addOreProcessRecipe(toOBs(ii), toFS(fi), toSTs(io), v, t);}
			@Override public String describe() {return null;}
		});
	}

	@ZenMethod
	public static void removeOreProcessRecipe(IItemStack ii, ILiquidStack fi) {
		addLateAction(new IAction() {
			@Override public void apply() {remove(MRecipes.OreProcessRecipes, new ItemStack[] {toST(ii)}, new FluidStack[] {toFS(fi)}, true, true);}
			@Override public String describe() {return null;}
		});
	}

	@ZenMethod
	public static void addBioRecipe(IIngredient ii, ILiquidStack fi, IItemStack io, ILiquidStack fo, int v, int t) {
		addLateAction(new IAction() {
			@Override public void apply() {MRecipes.addBioRecipe(toOB(ii), toFS(fi), toST(io), toFS(fo), v, t);}
			@Override public String describe() {return null;}
		});
	}

	@ZenMethod
	public static void removeBioRecipe(IItemStack ii, ILiquidStack fi) {
		addLateAction(new IAction() {
			@Override public void apply() {remove(MRecipes.BioRecipes, new ItemStack[] {toST(ii)}, new FluidStack[] {toFS(fi)}, true, true);}
			@Override public String describe() {return null;}
		});
	}

	@ZenMethod
	public static void addFertilizer(IItemStack i) {
		addLateAction(new IAction() {
			@Override public void apply() {MRecipes.addFertilizer(toST(i));}
			@Override public String describe() {return null;}
		});
	}

	@ZenMethod
	public static void removeFertilizer(IItemStack i) {
		addLateAction(new IAction() {
			@Override
			public void apply() {
				ItemStack st = toST(i);
				Iterator<ItemStack> it = MRecipes.Fertilizers.iterator();
				while (it.hasNext()) {
					ItemStack st2 = it.next();
					if (OreDictionary.itemMatches(st, st2, false))
						it.remove();
				}
			}
			@Override public String describe() {return null;}
		});
	}

	@ZenMethod
	public static void addDieselFuel(ILiquidStack i, int eu) {
		addFuel(i, eu, MRecipes.DieselGenFuel);
	}

	@ZenMethod
	public static void removeDieselFuel(ILiquidStack i) {
		removeFuel(i, MRecipes.DieselGenFuel);
	}

	@ZenMethod
	public static void addGasFuel(ILiquidStack i, int e) {
		addFuel(i, e, MRecipes.GasGenFuel);
	}

	@ZenMethod
	public static void removeGasFuel(ILiquidStack i) {
		removeFuel(i, MRecipes.GasGenFuel);
	}

	@ZenMethod
	public static void addThermalFuel(ILiquidStack i, int e) {
		addFuel(i, e, MRecipes.HeatGenFuel);
	}

	@ZenMethod
	public static void removeThermalFuel(ILiquidStack i) {
		removeFuel(i, MRecipes.HeatGenFuel);
	}

	@ZenMethod
	public static void addMagicFuel(IIngredient i, int e) {
		MRecipes.addMagicFuel(toOB(i), e);
	}

	@ZenMethod
	public static void removeMagicFuel(IItemStack i) {
		Iterator<Object> it = MRecipes.MagicGenFuel.keySet().iterator();
		while (it.hasNext()) if (RecipeContainer.matches(it.next(), toST(i))) it.remove();
	}

	private static void addFuel(ILiquidStack l, int eu, HashMap<String, Integer> rec) {
		addLateAction(new IAction() {
			@Override public void apply() {if (l != null) MRecipes.addFluidFuel(rec, JSTUtils.getRegName(toFS(l)), eu);}
			@Override public String describe() {return null;}
		});
	}

	private static void removeFuel(ILiquidStack l, HashMap<String, Integer> rec) {
		addLateAction(new IAction() {
			@Override public void apply() {rec.remove(JSTUtils.getRegName(toFS(l)));}
			@Override public String describe() {return null;}
		});
	}

	@ZenMethod
	public static void addFineDustSrc(String c, int a, String[] t) {
		if (c == null || t == null) {
			CraftTweakerAPI.logError("Class/Tag name can't be null");
			return;
		}
		Class cl;
		try {
			cl = Class.forName(c);
		} catch (Exception e) {
			CraftTweakerAPI.logError("Can't find TileEntity " + c);
			return;
		}
		if (t.length <= 0) {
			CraftTweakerAPI.logError("At least 1 tag name is required");
			return;
		}
		DustHandler.TEs.put(cl, new Object[] {Integer.valueOf(a), t});
	}

	@ZenMethod
	public static int getFineDust(IWorld w, IBlockPos p) {
		if (w == null || p == null) return 0;
		return JSTChunkData.getFineDust((World) w.getInternal(), new ChunkPos(p.getX() >> 4, p.getZ() >> 4));
	}

	@ZenMethod
	public static void setFineDust(IWorld w, IBlockPos p, int ng, boolean add) {
		if (w == null || p == null || ng == 0) return;
		World wd = (World) w.getInternal();
		if (!wd.isRemote) {
			ChunkPos cp = new ChunkPos(p.getX() >> 4, p.getZ() >> 4);
			if (add)
				JSTChunkData.addFineDust(wd, cp, ng, true);
			else
				JSTChunkData.setFineDust(wd, cp, ng, true);
		}
	}

	static void remove(RecipeList rl, ItemStack[] st, FluidStack[] fs, boolean sl, boolean fsl) {
		FluidTank[] tanks = null;
		if (fs != null) {
			tanks = new FluidTank[fs.length];
			for (int n = 0; n < fs.length; n++)
				tanks[n] = new FluidTank(fs[n], Integer.MAX_VALUE);
		}
		RecipeContainer rc = MRecipes.getRecipe(rl, st, tanks, Integer.MAX_VALUE, sl, fsl);
		if (rc != null) rl.remove(rc);
	}

	static Object toOB(IIngredient in) {
		if (in instanceof IOreDictEntry)
			return new OreDictStack(((IOreDictEntry)in).getName(), in.getAmount());
		if (in instanceof IngredientStack) {
			try {
				IIngredient ing = (IIngredient) ingredient.get(in);
				if (ing instanceof IOreDictEntry)
					return new OreDictStack(((IOreDictEntry)ing).getName(), in.getAmount());
			} catch (Throwable t) {}
		}
		if (in instanceof IItemStack) {
			IItemStack ii = (IItemStack)in;
			if (ii.getAmount() <= 0)
				return new AdvRecipeItem(((ItemStack)ii.getInternal()).getItem(), 0, ii.getMetadata());
			return ((IItemStack)in).getInternal();
		}
		return null;
	}

	static Object[] toOBs(IIngredient[] in) {
		if (in != null) {
			Object[] ret = new Object[in.length];
			for (int n = 0; n < in.length; n++)
				ret[n] = toOB(in[n]);
			return ret;
		}
		return null;
	}

	static ItemStack toST(IItemStack ii) {
		if (ii == null)
			return ItemStack.EMPTY;
		return (ItemStack) ii.getInternal();
	}

	static FluidStack toFS(ILiquidStack il) {
		if (il == null)
			return null;
		return (FluidStack) il.getInternal();
	}

	static ItemStack[] toSTs(IItemStack... ii) {
		if (ii == null)
			return null;
		ItemStack[] ret = new ItemStack[ii.length];
		for (int n = 0; n < ii.length; n++)
			ret[n] = toST(ii[n]);
		return ret;
	}

	static FluidStack[] toFSs(ILiquidStack... il) {
		if (il == null)
			return null;
		FluidStack[] ret = new FluidStack[il.length];
		for (int n = 0; n < il.length; n++)
			ret[n] = toFS(il[n]);
		return ret;
	}

	static void addLateAction(IAction a) {
		try {CraftTweaker.LATE_ACTIONS.add(a);} catch (Throwable t) {}
	}
}