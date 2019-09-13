package dohyun22.jst3.tiles.test;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.BatterySlot;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.container.JSTSlot;
import dohyun22.jst3.items.behaviours.IB_BluePrint;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileEnergyInput;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.machine.MT_MachineProcess;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_CircuitProduce extends MT_MachineProcess {	
	public MT_CircuitProduce(int tier) {
		//in-0:blueprint, 1:circuit board 2:solder 
		//out-0:circuit
		super(tier, 3, 1, 0, 0, 0, null, false, false, "circuit_produce", null);
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_CircuitProduce(tier);
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (baseTile != null && !getWorld().isRemote)
			pl.openGui(JustServerTweak.INSTANCE, 1, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
		return true;
	}

	public int getAmountOfUse(ItemStack itemStack) {
		return IB_BluePrint.getSizeOfComsumedLead(itemStack);
	}

	public int getTier(ItemStack itemStack) {
		return IB_BluePrint.getSizeOfTier(itemStack);
	}

	@Override
	protected boolean checkCanWork() {
		return super.checkCanWork();
	}

	@Override
	protected void addSlot(ContainerGeneric cg, InventoryPlayer inv, TileEntityMeta te) {
		cg.addSlot(new JSTSlot(te, 0, 8 + NORMAL_SLOT_SIZE, 31, true, true, 1, true));
		cg.addSlot(new Slot(te, 1, 71, 31));
		cg.addSlot(new Slot(te, 2, 71 + NORMAL_SLOT_SIZE, 31));
		
		cg.addSlot(new JSTSlot(te, 2, 71 + NORMAL_SLOT_SIZE * 2 + 24, 31, false, true, 64, true));
		
		cg.addSlot(new BatterySlot(te, 4, 8, 53, false, true));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	protected void addComp(GUIGeneric gg) {
		gg.addSlot(8 + NORMAL_SLOT_SIZE, 31, 8);
		gg.addSlot(71, 31, 0);
		gg.addSlot(71 + NORMAL_SLOT_SIZE, 31, 0);

		gg.addSlot(71 + NORMAL_SLOT_SIZE * 2 + NORMAL_PRG_SIZE_X + 1, 31, 0);

		gg.addPrg(71 + NORMAL_SLOT_SIZE * 2, 31, JustServerTweak.MODID + ".circuitproduce");

		gg.addSlot(8, 53, 2);
		gg.addPwr(12, 31);
	}
}
