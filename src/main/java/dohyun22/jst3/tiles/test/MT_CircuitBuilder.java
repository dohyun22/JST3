package dohyun22.jst3.tiles.test;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.recipe.RecipeContainer;
import dohyun22.jst3.api.recipe.RecipeList;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.BatterySlot;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.container.JSTSlot;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.items.behaviours.IB_BluePrint;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileEnergyInput;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.machine.MT_CircuitResearchMachine;
import dohyun22.jst3.tiles.machine.MT_MachineProcess;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_CircuitBuilder extends MT_MachineProcess {
	private int solder;

	public MT_CircuitBuilder(int tier) {
		//in-0~3: parts, out-4:circuit, 5:battery 6:blueprint 7:solder
		super(tier, 4, 1, 0, 0, 0, MRecipes.CircuitBuilderRecipes, false, true, "circuit_builder", null);
		setSfx(JSTSounds.SWITCH2, 0.6F, 1.0F);
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_CircuitBuilder(tier);
	}

	@Override
	public int getInvSize() {
		return super.getInvSize() + 2;
	}

	@Override
	protected boolean isInputSlot(int sl) {
		if (sl == 6) return false;
		if (sl == 7) return true;
		return super.isInputSlot(sl);
	}

	@Override
	public boolean canExtractItem(int sl, ItemStack st, EnumFacing dir) {
		if (sl >= 6) return false;
		return super.canExtractItem(sl, st, dir);
	}

	@Override
	protected RecipeContainer getContainer(RecipeList recipe, ItemStack[] in, FluidTank[] fin, boolean sl, boolean fsl) {
		ItemStack st = inv.get(6);
		int t = IB_BluePrint.getCircuitTier(st), s = IB_BluePrint.getSolder(st);
		st = inv.get(7);
		if (solder < s && JSTUtils.oreMatches(st, "wireSolder")) {
			int n = Math.min(st.getCount(), MathHelper.ceil(s / (double) MT_CircuitResearchMachine.SOLDER_PER_WIRE));
			solder += n * MT_CircuitResearchMachine.SOLDER_PER_WIRE;
			st.shrink(n);
		}
		if (solder < s) return null;
		RecipeContainer r = super.getContainer(recipe, in, fin, sl, fsl);
		if (r != null && t != JSTUtils.getTierFromVolt(r.getEnergyPerTick())) return null;
		return r;
	}

	@Override
	protected void addSlot(ContainerGeneric cg, InventoryPlayer inv, TileEntityMeta te) {
		cg.addSlot(new Slot(te, 0, 62, 17));
		cg.addSlot(new Slot(te, 1, 62 + SLOT_SIZE, 17));
		cg.addSlot(new Slot(te, 2, 62, 17 + SLOT_SIZE));
		cg.addSlot(new Slot(te, 3, 62 + SLOT_SIZE, 17 + SLOT_SIZE));
		
		cg.addSlot(JSTSlot.out(te, 4, 125, 26));
		
		cg.addSlot(new BatterySlot(te, 5, 8, 53, false, true));

		cg.addSlot(new JSTSlot(te, 6, 8, 17).setPredicate(new JSTSlot.ItemMatcher(new ItemStack(JSTItems.item1, 1, 10051))));
		cg.addSlot(new JSTSlot(te, 7, 8, 17 + SLOT_SIZE).setPredicate(new JSTSlot.ItemMatcher("wireSolder")));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	protected void addComp(GUIGeneric gg) {
		gg.addSlot(44, 17, 0);
		gg.addSlot(44 + SLOT_SIZE, 17, 0);
		gg.addSlot(44 + SLOT_SIZE * 2, 17, 0);
		gg.addSlot(44, 17 + SLOT_SIZE, 0);
		gg.addSlot(44 + SLOT_SIZE, 17 + SLOT_SIZE, 0);
		gg.addSlot(44 + SLOT_SIZE * 2, 17 + SLOT_SIZE, 0);

		gg.addSlot(6, 125, 0);

		gg.addPrg(98, 26, JustServerTweak.MODID + ".circuitbuilder");

		gg.addSlot(8, 53, 2);
		gg.addPwr(12, 31);

		gg.addSlot(8, 17, 8);
		gg.addSlot(8, 17 + SLOT_SIZE, 0);
	}
}
