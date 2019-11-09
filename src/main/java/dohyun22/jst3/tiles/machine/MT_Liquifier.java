package dohyun22.jst3.tiles.machine;

import java.util.ArrayList;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.recipe.RecipeContainer;
import dohyun22.jst3.api.recipe.RecipeList;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.BatterySlot;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.container.JSTSlot;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.recipes.OtherModRecipes;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_Liquifier extends MT_MachineProcess {

	public MT_Liquifier(int tier) {
		super(tier, 1, 0, 0, 1, 24000, null, false, false, "liquifier", null);
		setSfx(SoundEvents.BLOCK_LAVA_AMBIENT, 1.0F, 1.5F);
		modes = new ArrayList();
		modes.add(packData(1, true));
		if (JSTCfg.teLoaded) modes.add(packData(2, true));
		if (JSTCfg.ticLoaded) modes.add(packData(3, true));
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_Liquifier(tier);
	}

	@Override
	@Nullable
	protected RecipeContainer getContainer(RecipeList recipe, ItemStack[] in, FluidTank[] fin, boolean sl, boolean fsl) {
		if (in != null && in.length > 0 && in[0] != null && !in[0].isEmpty()) {
			RecipeContainer c = null;
			for (short s : modes) {
				if (!isCfgEnabled(s)) continue;
				short idx = toCfgIndex(s);
				switch (idx) {
				case 1: c = MRecipes.getRecipe(MRecipes.LiquifierRecipes, in, fin, tier, sl, fsl); break;
				case 2: c = OtherModRecipes.getTECrucible(in[0]); break;
				case 3: c = OtherModRecipes.getTicMelting(in[0]); break;
				}
				if (c != null) return c;
			}
		}
		return null;
	}

	@Override
	protected void addSlot(ContainerGeneric cg, InventoryPlayer inv, TileEntityMeta te) {
		cg.addSlot(new Slot(te, 0, 53, 35));
		cg.addSlot(JSTSlot.fl(te, 1, 107, 35));
		cg.addSlot(new BatterySlot(te, 2, 8, 53, false, true));
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void addComp(GUIGeneric gg) {
		gg.addSlot(53, 35, 0);
		gg.addSlot(107, 35, 3);

		gg.addPrg(76, 35, JustServerTweak.MODID + ".liquifier", "thermalexpansion.crucible", "tconstruct.smeltery");

		gg.addSlot(8, 53, 2);
		gg.addPwr(12, 31);
		gg.addCfg(7, 7, true);
		gg.addCfg(7 + 18, 7, false);
	}

	@Override
	public String getCfgName(int num) {
		switch (num) {
		case 1: return "itemGroup.JST3";
		case 2: return "jst.mod.te";
		case 3: return "jst.mod.tic";
		}
		return "";
	}
}
