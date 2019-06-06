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
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_ChemMixer extends MT_MachineGeneric {

	public MT_ChemMixer(int tier) {
		super(tier, 6, 2, 1, 1, 16000, MRecipes.ChemMixerRecipes, true, false);
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_ChemMixer(tier);
	}
	
	@Override
	protected void onStartWork() {
		getWorld().playSound(null, getPos(), SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0F, 1.2F);
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (baseTile != null && !getWorld().isRemote)
			pl.openGui(JustServerTweak.INSTANCE, 1, this.getWorld(), this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTieredTex(tier);
		return new TextureAtlasSprite[] { t, t, t, t, t, getTETex("chemmixer") };
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++)
			ret[n] = this.baseTile.facing == JSTUtils.getFacingFromNum(n) ? getTETex("chemmixer" + (this.baseTile.isActive() ? "" : "_off")) : getTieredTex(tier);
		return ret;
	}
	
	@Override
	protected void addSlot(ContainerGeneric cg, InventoryPlayer inv, TileEntityMeta te) {
		cg.addSlot(new Slot(te, 0, 37, 15));
		cg.addSlot(new Slot(te, 1, 55, 15));
		cg.addSlot(new Slot(te, 2, 73, 15));
		cg.addSlot(new Slot(te, 3, 37, 33));
		cg.addSlot(new Slot(te, 4, 55, 33));
		cg.addSlot(new Slot(te, 5, 73, 33));

		cg.addSlot(new JSTSlot(te, 6, 123, 24, false, true, 64, true));
		cg.addSlot(new JSTSlot(te, 7, 141, 24, false, true, 64, true));
		
		cg.addSlot(new JSTSlot(te, 8, 55, 55, false, true, 64, false));
		cg.addSlot(new JSTSlot(te, 9, 132, 55, false, true, 64, false));
		
		cg.addSlot(new BatterySlot(te, 10, 8, 53, false, true));
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void addComp(GUIGeneric gg) {
		gg.addSlot(37, 15, 0);
		gg.addSlot(55, 15, 0);
		gg.addSlot(73, 15, 0);
		gg.addSlot(37, 33, 0);
		gg.addSlot(55, 33, 0);
		gg.addSlot(73, 33, 0);
		
		gg.addSlot(123, 24, 0);
		gg.addSlot(141, 24, 0);
		
		gg.addSlot(55, 55, 3);
		gg.addSlot(132, 55, 3);
		
		gg.addPrg(94, 24, JustServerTweak.MODID + ".chemmixer");
		
		gg.addSlot(8, 53, 2);
		gg.addPwr(12, 31);
	}
}
