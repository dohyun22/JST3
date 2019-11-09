package dohyun22.jst3.tiles.machine;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

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
import dohyun22.jst3.utils.JSTUtils;
import dohyun22.jst3.utils.ReflectionUtils;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.MachineRecipe;
import ic2.api.recipe.MachineRecipeResult;
import ic2.api.recipe.Recipes;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_UPulverizer extends MT_MachineProcess {

	public MT_UPulverizer(int tier) {
		super(tier, 1, 2, 0, 0, 0, null, false, false, "upulv", "vent");
		setSfx(SoundEvents.BLOCK_METAL_BREAK, 1.0F, 0.5F);
		modes = new ArrayList();
		modes.add(packData(1, true));
		if (JSTCfg.ic2Loaded) modes.add(packData(2, true));
		if (JSTCfg.teLoaded) modes.add(packData(3, true));
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_UPulverizer(tier);
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
				case 1: c = MRecipes.getRecipe(MRecipes.GrinderRecipes, in, fin, tier, sl, fsl); break;
				case 2: c = OtherModRecipes.getIC2Maceration(in[0]); break;
				case 3: c = OtherModRecipes.getTEMaceration(in[0]); break;
				}
				if (c != null) return c;
			}
		}
		return null;
	}
	
	@Override
	protected void addSlot(ContainerGeneric cg, InventoryPlayer inv, TileEntityMeta te) {
		cg.addSlot(new Slot(te, 0, 53, 35));
		cg.addSlot(JSTSlot.out(te, 1, 107, 35));
		cg.addSlot(JSTSlot.out(te, 2, 125, 35));
		cg.addSlot(new BatterySlot(te, 3, 8, 53, false, true));
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void addComp(GUIGeneric gg) {
		gg.addSlot(53, 35, 0);
		gg.addSlot(107, 35, 0);
		gg.addSlot(125, 35, 0);

		gg.addPrg(76, 35, JustServerTweak.MODID + ".grinder", "macerator", "thermalexpansion.pulverizer");

		gg.addSlot(8, 53, 2);
		gg.addPwr(12, 31);
		gg.addCfg(7, 7, true);
		gg.addCfg(7 + 18, 7, false);
	}
	
	@Override
	public void onPostTick() {
		super.onPostTick();
		if (isClient() && baseTile.isActive()) {
			World w = getWorld();
			if (w.rand.nextInt(8) == 0) {
				for (int i = 0; i < 4; i++) {
					BlockPos p = getPos();
					double x = p.getX() + 0.25D + w.rand.nextFloat() * 0.45D;
					double y = p.getY() + 1.1D;
					double z = p.getZ() + 0.25D + w.rand.nextFloat() * 0.45D;
					w.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0D, 0.0D, 0.0D);
				}
			}
		}
	}

	@Override
	public String getCfgName(int num) {
		switch (num) {
		case 1: return "itemGroup.JST3";
		case 2: return "jst.mod.ic2";
		case 3: return "jst.mod.te";
		}
		return "";
	}
}
