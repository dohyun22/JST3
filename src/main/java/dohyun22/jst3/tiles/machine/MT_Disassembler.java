package dohyun22.jst3.tiles.machine;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.BatterySlot;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.container.JSTSlot;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_Disassembler extends MT_MachineProcess {

	public MT_Disassembler(int tier) {
		super(tier, 1, 9, 0, 0, 0, MRecipes.DisassemblerRecipes, false, false, "disassembler", null);
		setSfx(JSTSounds.SWITCH2, 0.6F, 1.0F);
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_Disassembler(tier);
	}
	
	@Override
	protected void addSlot(ContainerGeneric cg, InventoryPlayer inv, TileEntityMeta te) {
		cg.addSlot(new Slot(te, 0, 44, 35));
	    for (int r = 0; r < 3; r++) for (int c = 0; c < 3; c++)
	    	cg.addSlot(new JSTSlot(te, c + r * 3 + 1, 98 + c * 18, 17 + r * 18, false, true, 64, true));
		cg.addSlot(new BatterySlot(te, 10, 8, 53, false, true));
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void addComp(GUIGeneric gg) {
		gg.addSlot(44, 35, 0);
		for (int r = 0; r < 3; r++) for (int c = 0; c < 3; c++)
			gg.addSlot(98 + c * 18, 17 + r * 18, 0);
		gg.addPrg(67, 35, JustServerTweak.MODID + ".disassembler");
		gg.addSlot(8, 53, 2);
		gg.addPwr(12, 31);
		gg.addCfg(7, 7, true);
	}
}
