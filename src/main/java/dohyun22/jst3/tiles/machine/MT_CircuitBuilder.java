package dohyun22.jst3.tiles.machine;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.recipe.RecipeContainer;
import dohyun22.jst3.api.recipe.RecipeList;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.BatterySlot;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.container.JSTSlot;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.items.behaviours.misc.IB_BluePrint;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileEnergyInput;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.machine.MT_CircuitResearchMachine;
import dohyun22.jst3.tiles.machine.MT_MachineProcess;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_CircuitBuilder extends MT_MachineProcess {
	public int solder;

	public MT_CircuitBuilder(int tier) {
		//in-0~3: parts, out-4:circuit, 5:battery 6:blueprint 7:solder
		super(tier, 4, 1, 0, 0, 0, MRecipes.CircuitBuilderRecipes, true, false, "circuit_builder", null);
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
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		solder = tag.getInteger("solder");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("solder", solder);
	}

	@Override
	protected boolean isInputSlot(int sl) {
		return sl != 6 && (sl == 7 || super.isInputSlot(sl));
	}

	@Override
	public boolean canInsertItem(int sl, ItemStack st, EnumFacing f) {
		short s = getCfg(f);
		if (s == 2 || s == 5 || sl == 6) return false;
		boolean b = JSTUtils.oreMatches(st, "wireSolder");
		if (sl == 7) return b;
		if (!b) return super.canInsertItem(sl, st, f);
		return false;
	}

	@Override
	public boolean canExtractItem(int sl, ItemStack st, EnumFacing f) {
		if (sl >= 6) return false;
		return super.canExtractItem(sl, st, f);
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		int[] combined = new int[8];
		if (combined.length > 0)
			for (int n = 0; n < combined.length; n++)
				combined[n] = n;
		return combined;
	}

	@Override
	protected void onStartWork() {
		super.onStartWork();
		solder = Math.max(0, solder - IB_BluePrint.getSolder(inv.get(6)));
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
		RecipeContainer r = MRecipes.getRecipe(recipe, in, fin, tier, sl, fsl);
		if (r != null && !canProcess(t, JSTUtils.getTierFromVolt(r.getEnergyPerTick()))) return null;
		return r;
	}

	@Override
	public boolean canSlotDrop(int num) {
		return super.canSlotDrop(num) || num == 6 || num == 7;
	}

	@Override
	protected void addSlot(ContainerGeneric cg, InventoryPlayer inv, TileEntityMeta te) {
		cg.addSlot(new Slot(te, 0, 62, 17));
		cg.addSlot(new Slot(te, 1, 80, 17));
		cg.addSlot(new Slot(te, 2, 62, 35));
		cg.addSlot(new Slot(te, 3, 80, 35));
		
		cg.addSlot(JSTSlot.out(te, 4, 125, 26));
		
		cg.addSlot(new BatterySlot(te, 5, 8, 53, false, true));

		cg.addSlot(new JSTSlot(te, 6, 8, 12).setPredicate(new JSTSlot.ItemMatcher(new ItemStack(JSTItems.item1, 1, 10051))));
		cg.addSlot(new JSTSlot(te, 7, 26, 12).setPredicate(new JSTSlot.ItemMatcher("wireSolder")));

		cg.addPlayerSlots(inv);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void addComp(GUIGeneric gg) {
		gg.addSlot(62, 17, 0);
		gg.addSlot(80, 17, 0);
		gg.addSlot(62, 35, 0);
		gg.addSlot(80, 35, 0);

		gg.addSlot(125, 26, 0);

		gg.addPrg(98, 26, JustServerTweak.MODID + ".circuitbuilder");

		gg.addSlot(8, 53, 2);
		gg.addPwr(12, 32);

		gg.addSlot(8, 12, 8);
		gg.addSlot(8 + 18, 12, 0);
		gg.addCfg(25, 52, true);
		gg.addText(54, 64, 0);
	}

	@Override
	public int[] getGuiData() {
		return new int[] {solder};
	}

	@Override
	public String guiDataToStr(int id, int dat) {
		return I18n.format("jst.msg.com.solder", dat, JSTUtils.formatNum(dat / (double)MT_CircuitResearchMachine.SOLDER_PER_WIRE));
	}

	private boolean canProcess(int bpt, int rt) {
		switch (bpt) {
		case 1: return rt >= 0 && rt <= 2;
		case 2: return rt >= 3 && rt <= 4;
		case 3: return rt >= 5 && rt <= 6;
		case 4: return rt >= 7 && rt <= 9;
		}
		return false;
	}
}
