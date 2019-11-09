package dohyun22.jst3.compat.jei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.recipe.RecipeList;
import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.client.gui.GUIFusion;
import dohyun22.jst3.client.gui.GUIMagicGen;
import dohyun22.jst3.client.gui.GUISolarFurnace;
import dohyun22.jst3.client.gui.GUISuperCompressor;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.recipes.ItemList;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.utils.JSTUtils;
import ic2.core.ref.TeBlock;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@JEIPlugin
public class JEISupport implements IModPlugin {
	@SideOnly(Side.CLIENT) public static final ResourceLocation TEX_LOC = new ResourceLocation(JustServerTweak.MODID + ":textures/gui/generic.png");
	@Nullable private static Object runTime;

	@Override
	@Method(modid = "jei")
	public void registerItemSubtypes(ISubtypeRegistry reg) {
		reg.registerSubtypeInterpreter(JSTItems.item1, new ISubtypeRegistry.ISubtypeInterpreter() {
			@Override
			public String apply(ItemStack st) {
				return JustServerTweak.MODID + ":" + st.getMetadata();
			}
		});
	}

	@Override
	@Method(modid = "jei")
	public void register(IModRegistry rg) {
		IGuiHelper gh = rg.getJeiHelpers().getGuiHelper();

		rg.addRecipeHandlers(new GenericRecipeHandler(), new FluidFuelHandler(), new ItemFuelHandler());

		addGenericRecipe(rg, gh, MRecipes.AlloyFurnaceRecipes);
		addGenericRecipe(rg, gh, MRecipes.SeparatorRecipes);
		addGenericRecipe(rg, gh, MRecipes.FusionBreederRecipes);
		addGenericRecipe(rg, gh, MRecipes.SHFurnaceRecipes);
		addGenericRecipe(rg, gh, MRecipes.ChemMixerRecipes);
		addGenericRecipe(rg, gh, MRecipes.PressRecipes);
		addGenericRecipe(rg, gh, MRecipes.CokeOvenRecipes);
		addGenericRecipe(rg, gh, MRecipes.OreProcessRecipes);
		addGenericRecipe(rg, gh, MRecipes.CrystalRecipes);
		addGenericRecipe(rg, gh, MRecipes.HeatExcFakeRecipes);
		addGenericRecipe(rg, gh, MRecipes.BioRecipes);
	    addGenericRecipe(rg, gh, MRecipes.CircuitBuilderRecipes);
	    addGenericRecipe(rg, gh, MRecipes.GrinderRecipes);
	    addGenericRecipe(rg, gh, MRecipes.LiquifierRecipes);

		rg.addRecipeCategories(new FusionCategory());
		rg.addRecipes(FusionRecipeWrapper.make(), JustServerTweak.MODID + "." + MRecipes.FusionRecipes.name);

		rg.addRecipeCategories(new AssemblerCategory());
		rg.addRecipes(AssemblerRecipeWrapper.make(), JustServerTweak.MODID + "." + MRecipes.AssemblerRecipes.name);

		rg.addRecipeCategories(new DisassemblerCategory());
		rg.addRecipes(DisassemblerRecipeWrapper.make(), JustServerTweak.MODID + "." + MRecipes.DisassemblerRecipes.name);

		rg.addRecipeCategories(new RefineryCategory());
		rg.addRecipes(RefineryRecipeWrapper.make(), JustServerTweak.MODID + "." + MRecipes.RefineryRecipes.name);

		rg.addRecipeCategories(new FluidFuelCategory("dieselfuel"));
		rg.addRecipes(FluidFuelRecipeWrapper.make(MRecipes.DieselGenFuel), JustServerTweak.MODID + ".dieselfuel");

		rg.addRecipeCategories(new FluidFuelCategory("gasfuel"));
		rg.addRecipes(FluidFuelRecipeWrapper.make(MRecipes.GasGenFuel), JustServerTweak.MODID + ".gasfuel");

		rg.addRecipeCategories(new FluidFuelCategory("steamfuel"));
		rg.addRecipes(FluidFuelRecipeWrapper.make(MRecipes.AcceptableSteam), JustServerTweak.MODID + ".steamfuel");

		rg.addRecipeCategories(new FluidFuelCategory("heatfuel"));
		rg.addRecipes(FluidFuelRecipeWrapper.make(MRecipes.HeatGenFuel), JustServerTweak.MODID + ".heatfuel");

		rg.addRecipeCategories(new ItemFuelCategory("magicfuel"));
		rg.addRecipes(ItemFuelRecipeWrapper.make(MRecipes.MagicGenFuel, false), JustServerTweak.MODID + ".magicfuel");

		rg.addRecipeCategories(new ItemFuelCategory("fgenfuel"));
		rg.addRecipes(ItemFuelRecipeWrapper.makeFGFuelList(rg), JustServerTweak.MODID + ".fgenfuel");

		rg.addRecipeCategories(new FluidResourceCategory());
		rg.addRecipes(FluidResourceWrapper.make(), JustServerTweak.MODID + ".fluidresource");

		rg.addRecipeCategories(new ItemFuelCategory("degcomp"));
		rg.addRecipes(ItemFuelRecipeWrapper.make(MRecipes.CompressorValue, true), JustServerTweak.MODID + ".degcomp");

		Block b = JSTBlocks.blockTile;
		String s = JustServerTweak.MODID + ".fgenfuel";
		rg.addRecipeCatalyst(new ItemStack(b, 1, 31), s);
		rg.addRecipeCatalyst(new ItemStack(b, 1, 32), s);
		rg.addRecipeCatalyst(new ItemStack(b, 1, 33), s);

		s = JustServerTweak.MODID + ".dieselfuel";
		rg.addRecipeCatalyst(new ItemStack(b, 1, 61), s);
		rg.addRecipeCatalyst(new ItemStack(b, 1, 62), s);
		rg.addRecipeCatalyst(new ItemStack(b, 1, 63), s);
		rg.addRecipeCatalyst(new ItemStack(b, 1, 6010), s);
		rg.addRecipeCatalyst(new ItemStack(JSTItems.item1, 1, 10019), s);
		rg.addRecipeCatalyst(new ItemStack(JSTItems.item1, 1, 10042), s);

		s = JustServerTweak.MODID + ".gasfuel";
		rg.addRecipeCatalyst(new ItemStack(b, 1, 71), s);
		rg.addRecipeCatalyst(new ItemStack(b, 1, 72), s);
		rg.addRecipeCatalyst(new ItemStack(b, 1, 73), s);
		rg.addRecipeCatalyst(new ItemStack(JSTItems.item1, 1, 12031), s);
		rg.addRecipeCatalyst(new ItemStack(b, 1, 6011), s);

		s = JustServerTweak.MODID + ".steamfuel";
		rg.addRecipeCatalyst(new ItemStack(b, 1, 81), s);
		rg.addRecipeCatalyst(new ItemStack(b, 1, 82), s);
		rg.addRecipeCatalyst(new ItemStack(b, 1, 83), s);
		rg.addRecipeCatalyst(new ItemStack(b, 1, 6012), s);

		s = JustServerTweak.MODID + ".heatfuel";
		rg.addRecipeCatalyst(new ItemStack(b, 1, 91), s);
		rg.addRecipeCatalyst(new ItemStack(b, 1, 92), s);
		rg.addRecipeCatalyst(new ItemStack(b, 1, 93), s);
		rg.addRecipeCatalyst(new ItemStack(b, 1, 6013), s);

		s = JustServerTweak.MODID + ".magicfuel";
		rg.addRecipeCatalyst(new ItemStack(b, 1, 111), s);
		rg.addRecipeCatalyst(new ItemStack(b, 1, 112), s);
		rg.addRecipeCatalyst(new ItemStack(b, 1, 113), s);

		String[] list = new String[] {JustServerTweak.MODID + ".fusion", JustServerTweak.MODID + ".fusionbreeder"};
		rg.addRecipeCatalyst(new ItemStack(b, 1, 101), list);
		rg.addRecipeCatalyst(new ItemStack(b, 1, 102), list);
		rg.addRecipeCatalyst(new ItemStack(b, 1, 103), list);
		rg.addRecipeCatalyst(new ItemStack(b, 1, 104), list);

		rg.addRecipeCatalyst(new ItemStack(b, 1, 6003), JustServerTweak.MODID + ".alloyfurnace", "minecraft.fuel");
		rg.addRecipeCatalyst(new ItemStack(b, 1, 6004), "minecraft.smelting");
		s = JustServerTweak.MODID + ".degcomp";
		rg.addRecipeCatalyst(new ItemStack(b, 1, 6006), s);
		rg.addRecipeCatalyst(new ItemStack(JSTItems.item1, 1, 24), s);
		rg.addRecipeCatalyst(new ItemStack(b, 1, 6020), JustServerTweak.MODID + ".shfurnace");
		s = JustServerTweak.MODID + ".fluidresource";
		rg.addRecipeCatalyst(new ItemStack(b, 1, 6021), s);
		rg.addRecipeCatalyst(new ItemStack(b, 1, 6022), s);
		rg.addRecipeCatalyst(new ItemStack(b, 1, 6025), JustServerTweak.MODID + ".heatexc");
		rg.addRecipeCatalyst(new ItemStack(b, 1, 6041), JustServerTweak.MODID + ".cokeoven");
		rg.addRecipeCatalyst(new ItemStack(b, 1, 6043), JustServerTweak.MODID + ".oregrinder");
		rg.addRecipeCatalyst(new ItemStack(b, 1, 6044), JustServerTweak.MODID + ".refinery");
		rg.addRecipeCatalyst(new ItemStack(b, 1, 6047), JustServerTweak.MODID + ".bioprocess");

		rg.addRecipeClickArea(GUISolarFurnace.class, 78, 32, 28, 23, "minecraft.smelting");
		rg.addRecipeClickArea(GUIFusion.class, 152, 8, 16, 16, list);
		rg.addRecipeClickArea(GUIMagicGen.class, 39, 32, 16, 17, JustServerTweak.MODID + ".magicfuel");
		rg.addRecipeClickArea(GUISuperCompressor.class, 118, 23, 12, 12, JustServerTweak.MODID + ".degcomp");

		s = JustServerTweak.MODID + ".alloyfurnace";
		for (int n = 1; n <= 8; n++) rg.addRecipeCatalyst(new ItemStack(b, 1, 220 + n), s);
		s = JustServerTweak.MODID + ".assembler";
		for (int n = 1; n <= 8; n++) rg.addRecipeCatalyst(new ItemStack(b, 1, 230 + n), s);
		s = JustServerTweak.MODID + ".separator";
		for (int n = 1; n <= 8; n++) rg.addRecipeCatalyst(new ItemStack(b, 1, 240 + n), s);
		s = JustServerTweak.MODID + ".chemmixer";
		for (int n = 1; n <= 8; n++) rg.addRecipeCatalyst(new ItemStack(b, 1, 250 + n), s);
		s = JustServerTweak.MODID + ".disassembler";
		for (int n = 1; n <= 8; n++) rg.addRecipeCatalyst(new ItemStack(b, 1, 260 + n), s);
		s = JustServerTweak.MODID + ".press";
		for (int n = 1; n <= 8; n++) rg.addRecipeCatalyst(new ItemStack(b, 1, 270 + n), s);
		s = "minecraft.smelting";
		for (int n = 1; n <= 8; n++) rg.addRecipeCatalyst(new ItemStack(b, 1, 280 + n), s);
		list = new String[] {JustServerTweak.MODID + ".grinder", "macerator", "thermalexpansion.pulverizer"};
		for (int n = 1; n <= 8; n++) rg.addRecipeCatalyst(new ItemStack(b, 1, 290 + n), list);
		s = JustServerTweak.MODID + ".crystalizer";
		for (int n = 1; n <= 8; n++) rg.addRecipeCatalyst(new ItemStack(b, 1, 300 + n), s);
		s = JustServerTweak.MODID + ".circuitbuilder";
		for (int n = 1; n <= 8; n++) rg.addRecipeCatalyst(new ItemStack(b, 1, 350 + n), s);
		list = new String[] {JustServerTweak.MODID + ".liquifier", "thermalexpansion.crucible", "tconstruct.smeltery"};
		for (int n = 1; n <= 8; n++) rg.addRecipeCatalyst(new ItemStack(b, 1, 360 + n), list);

		if (JSTCfg.ic2Loaded && JSTCfg.RIC2C)
			addDescription(rg, "cablechange", new ItemStack(ItemList.cables[1].getItem(), 1, 32767));
		addDescription(rg, "neutdrill", new ItemStack(JSTItems.item1, 1, 10006));
		addDescription(rg, "scanner", new ItemStack(JSTItems.item1, 1, 10008));
		addDescription(rg, "fusion", new ItemStack(b, 1, 101), new ItemStack(b, 1, 102), new ItemStack(b, 1, 103), new ItemStack(b, 1, 104));
		addDescription(rg, "dust", new ItemStack(JSTItems.item1, 1, 10016), new ItemStack(JSTItems.item1, 1, 10017), new ItemStack(JSTItems.item1, 1, 10018), new ItemStack(JSTBlocks.blockTile, 1, 6042), new ItemStack(JSTBlocks.blockTile, 1, 6048), new ItemStack(JSTItems.mask));
		addDescription(rg, "stirling", new ItemStack(b, 1, 31), new ItemStack(b, 1, 32), new ItemStack(b, 1, 33));
		addDescription(rg, "degcomp", new ItemStack(b, 1, 6006), new ItemStack(JSTItems.item1, 1, 24));
		ArrayList<ItemStack> a = new ArrayList();
		for (int n = 0; n < 8; n++) a.add(new ItemStack(b, 1, 351 + n));
		addDescription(rg, "circuit", a.toArray(new ItemStack[0]));
		a = new ArrayList();
		for (int n = 0; n < 3; n++) a.add(new ItemStack(b, 1, 341 + n));
		addDescription(rg, "circuit2", a.toArray(new ItemStack[0]));
	}
	
	@Override
	@Method(modid = "jei")
	public void onRuntimeAvailable(IJeiRuntime jr) {
		this.runTime = jr;
	}
	
	@Method(modid = "jei")
	private static void addGenericRecipe(IModRegistry rg, IGuiHelper guiHelper, @Nonnull RecipeList rl) {
        rg.addRecipeCategories(new GenericCategory(rl));
        rg.addRecipes(GenericRecipeWrapper.make(rl), JustServerTweak.MODID + "." + rl.name);
	}
	
	@Nullable
	@Method(modid = "jei")
	public static List<FluidStack> getFluidFromList(List<List<FluidStack>> in, int index) {
		if (in == null || index < 0 || index >= in.size()) return null;
		return in.get(index);
	}
	
	@Method(modid = "jei")
	public static void addDescription(IModRegistry reg, String str, ItemStack... items) {
		List<ItemStack> li = new ArrayList();
		for (ItemStack st : items) li.add(st);
		reg.addIngredientInfo(li, ItemStack.class, new String[] {"jst.compat.jei.desc." + str});
	}

	public static void loadJEI(List<String> ls) {
		try {
			if (runTime != null) ((IJeiRuntime)runTime).getRecipesGui().showCategories(ls);
		} catch (Throwable t) {
			JSTUtils.LOG.error("An error occurred while trying to show JEI recipes");
			t.printStackTrace();
		}
	}
}
