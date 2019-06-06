package dohyun22.jst3.tiles.machine;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.BatterySlot;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.container.JSTSlot;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
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

public class MT_Crystalizer extends MT_MachineGeneric {

	public MT_Crystalizer(int tier) {
		super(tier, 2, 1, 1, 0, 16000, MRecipes.CrystalRecipes, true, false);
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_Crystalizer(tier);
	}

	@Override
	protected void onStartWork() {
		getWorld().playSound(null, getPos(), SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0F, 1.5F);
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (baseTile != null && !getWorld().isRemote)
			pl.openGui(JustServerTweak.INSTANCE, 1, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTieredTex(tier);
		return new TextureAtlasSprite[] { t, t, t, t, t, getTETex("crystal") };
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++)
			ret[n] = baseTile.facing == JSTUtils.getFacingFromNum(n) ? getTETex("crystal" + (baseTile.isActive() ? "" : "_off")) : getTieredTex(tier);
		return ret;
	}
	
	@Override
	protected void addSlot(ContainerGeneric cg, InventoryPlayer inv, TileEntityMeta te) {
		cg.addSlot(new Slot(te, 0, 53, 24));
		cg.addSlot(new Slot(te, 1, 71, 24));
		cg.addSlot(new JSTSlot(te, 2, 125, 24, false, true, 64, true));

		cg.addSlot(new JSTSlot(te, 3, 62, 53, false, true, 64, false));
		
		cg.addSlot(new BatterySlot(te, 4, 8, 53, false, true));
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void addComp(GUIGeneric gg) {
		gg.addSlot(53, 24, 0);
		gg.addSlot(71, 24, 0);
		gg.addSlot(125, 24, 0);

		gg.addSlot(62, 53, 3);

		gg.addPrg(94, 24, JustServerTweak.MODID + ".crystalizer");

		gg.addSlot(8, 53, 2);
		gg.addPwr(12, 31);
	}
}
