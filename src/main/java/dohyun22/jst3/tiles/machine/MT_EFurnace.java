package dohyun22.jst3.tiles.machine;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.recipe.RecipeContainer;
import dohyun22.jst3.api.recipe.RecipeList;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.BatterySlot;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.container.JSTSlot;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_EFurnace extends MT_MachineProcess {

	public MT_EFurnace(int tier) {
		super(tier, 1, 1, 0, 0, 0, null, false, false, "efurnace", null);
		setSfx(SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, 1.0F, 1.2F).setLux(10);
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_EFurnace(tier);
	}

	@Override
	@Nullable
	protected RecipeContainer getContainer(RecipeList recipe, ItemStack[] in, FluidTank[] fin, boolean sl, boolean fsl) {
		if (in == null || in.length <= 0 || in[0] == null || in[0].isEmpty()) return null;
		ItemStack st = FurnaceRecipes.instance().getSmeltingResult(in[0]);
		if (!st.isEmpty()) {
			ItemStack st2 = in[0].copy();
			st2.setCount(1);
			return RecipeContainer.newContainer(new Object[] {st2}, null, new ItemStack[] {st}, null, 5, 50);
		}
		return null;
	}
	
	@Override
	protected void addSlot(ContainerGeneric cg, InventoryPlayer inv, TileEntityMeta te) {
		cg.addSlot(new Slot(te, 0, 53, 35));
		cg.addSlot(new JSTSlot(te, 1, 107, 35, false, true, 64, true));
		cg.addSlot(new BatterySlot(te, 2, 8, 53, false, true));
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void addComp(GUIGeneric gg) {
		gg.addSlot(53, 35, 0);
		gg.addSlot(107, 35, 0);
		
		gg.addPrg(76, 35, "minecraft.smelting");
		
		gg.addSlot(8, 53, 2);
		gg.addPwr(12, 31);
	}
}
