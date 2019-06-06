package dohyun22.jst3.compat.ct;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import dohyun22.jst3.compat.ic2.CompatIC2;
import ic2.api.recipe.IBasicMachineRecipeManager;
import ic2.api.recipe.Recipes;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.jst.IC2")
public class IC2Support {

	@ZenMethod
	public static void addMaceration(IIngredient in, IItemStack out) {
		CTSupport.addLateAction(new IAction() {
			@Override public void apply() {CompatIC2.addMacRec(CTSupport.toOB(in), CTSupport.toST(out));}
			@Override public String describe() {return null;}
		});
	}

	@ZenMethod
	public static void removeMaceration(IItemStack in) {remove(in, Recipes.macerator);}

	@ZenMethod
	public static void addExtraction(IIngredient in, IItemStack out) {
		CTSupport.addLateAction(new IAction() {
			@Override public void apply() {CompatIC2.addExtRec(CTSupport.toOB(in), CTSupport.toST(out));}
			@Override public String describe() {return null;}
		});
	}

	@ZenMethod
	public static void removeExtraction(IItemStack in) {remove(in, Recipes.extractor);}

	@ZenMethod
	public static void addCompression(IIngredient in, IItemStack out) {
		CTSupport.addLateAction(new IAction() {
			@Override public void apply() {CompatIC2.addExtRec(CTSupport.toOB(in), CTSupport.toST(out));}
			@Override public String describe() {return null;}
		});
	}

	@ZenMethod
	public static void removeCompression(IItemStack in) {remove(in, Recipes.compressor);}

	@ZenMethod
	public static void addScrapDrop(IItemStack out, double cnc) {
		CTSupport.addLateAction(new IAction() {
			@Override public void apply() {CompatIC2.addScrapDrop(CTSupport.toOB(out), (float) cnc);}
			@Override public String describe() {return null;}
		});
	}

	private static void remove(IItemStack in, IBasicMachineRecipeManager m) {
		CTSupport.addLateAction(new IAction() {
			@Override public void apply() {CompatIC2.removeIC2RecipeByInput(CTSupport.toST(in), m);}
			@Override public String describe() {return null;}
		});
	}
}
