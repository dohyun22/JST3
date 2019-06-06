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
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_Disassembler extends MT_MachineGeneric {

	public MT_Disassembler(int tier) {
		super(tier, 1, 9, 0, 0, 0, MRecipes.DisassemblerRecipes, false, false);
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_Disassembler(tier);
	}
	
	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (this.baseTile != null && !getWorld().isRemote)
			pl.openGui(JustServerTweak.INSTANCE, 1, this.getWorld(), this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
		return true;
	}
	
	@Override
	protected void onStartWork() {
		getWorld().playSound(null, getPos(), JSTSounds.SWITCH2, SoundCategory.BLOCKS, 0.6F, 0.8F + getWorld().rand.nextFloat() * 0.6F);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTieredTex(tier);
		return new TextureAtlasSprite[] {t, t, t, t, t, getTETex("disassembler")};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++) {
			if (this.baseTile.facing == JSTUtils.getFacingFromNum(n)) {
				ret[n] = getTETex("disassembler" + (this.baseTile.isActive() ? "" : "_off"));
			} else {
				ret[n] = getTieredTex(tier);
			}
		}
		return ret;
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
	}
}
