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

public class MT_Separator extends MT_MachineGeneric {
	
	public MT_Separator(int tier) {
		super(tier, 2, 6, 1, 1, 16000, MRecipes.SeparatorRecipes, true, false);
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_Separator(this.tier);
	}
	
	@Override
	protected void onStartWork() {
		getWorld().playSound((EntityPlayer)null, getPos(), JSTSounds.SHOCK, SoundCategory.BLOCKS, 0.6F, 2.0F);
	}
	
	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (this.baseTile != null && !getWorld().isRemote)
			pl.openGui(JustServerTweak.INSTANCE, 1, this.getWorld(), this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTieredTex(tier);
		return new TextureAtlasSprite[] {t, t, t, t, t, getTETex("separator")};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++) ret[n] = this.baseTile.facing == JSTUtils.getFacingFromNum(n) ? getTETex("separator" + (this.baseTile.isActive() ? "" : "_off")) : getTieredTex(tier);
		return ret;
	}
	
	@Override
	protected void addSlot(ContainerGeneric cg, InventoryPlayer inv, TileEntityMeta te) {
		cg.addSlot(new Slot(te, 0, 37, 24));
		cg.addSlot(new Slot(te, 1, 55, 24));
		
		cg.addSlot(new JSTSlot(te, 2, 105, 15, false, true, 64, true));
		cg.addSlot(new JSTSlot(te, 3, 123, 15, false, true, 64, true));
		cg.addSlot(new JSTSlot(te, 4, 141, 15, false, true, 64, true));
		cg.addSlot(new JSTSlot(te, 5, 105, 33, false, true, 64, true));
		cg.addSlot(new JSTSlot(te, 6, 123, 33, false, true, 64, true));
		cg.addSlot(new JSTSlot(te, 7, 141, 33, false, true, 64, true));
		
		cg.addSlot(new JSTSlot(te, 8, 46, 55, false, true, 64, false));
		cg.addSlot(new JSTSlot(te, 9, 123, 55, false, true, 64, false));
		
		cg.addSlot(new BatterySlot(te, 10, 8, 53, false, true));
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void addComp(GUIGeneric gg) {
		gg.addSlot(37, 24, 0);
		gg.addSlot(55, 24, 0);
		
		gg.addSlot(105, 15, 0);
		gg.addSlot(123, 15, 0);
		gg.addSlot(141, 15, 0);
		gg.addSlot(105, 33, 0);
		gg.addSlot(123, 33, 0);
		gg.addSlot(141, 33, 0);
		
		gg.addSlot(46, 55, 3);
		gg.addSlot(123, 55, 3);
		
		gg.addPrg(76, 24, JustServerTweak.MODID + ".separator");
		
		gg.addSlot(8, 53, 2);
		gg.addPwr(12, 31);
	}
}
