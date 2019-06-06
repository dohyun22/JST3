package dohyun22.jst3.tiles.machine;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.recipe.RecipeContainer;
import dohyun22.jst3.api.recipe.RecipeList;
import dohyun22.jst3.client.gui.GUIAssembler;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.BatterySlot;
import dohyun22.jst3.container.ContainerAssembler;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.container.JSTSlot;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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

public class MT_EFurnace extends MT_MachineGeneric {

	public MT_EFurnace(int tier) {
		super(tier, 1, 1, 0, 0, 0, null, false, false);
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
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTieredTex(tier);
		return new TextureAtlasSprite[] {t, t, t, t, t, getTETex("efurnace")};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++) {
			if (this.baseTile.facing == JSTUtils.getFacingFromNum(n)) {
				ret[n] = getTETex("efurnace" + (this.baseTile.isActive() ? "" : "_off"));
			} else {
				ret[n] = getTieredTex(tier);
			}
		}
		return ret;
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (this.baseTile != null && !isClient())
			pl.openGui(JustServerTweak.INSTANCE, 1, this.getWorld(), this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
		return true;
	}
	
	@Override
	public int getLightValue() {
		return baseTile.isActive() ? 8 : 0;
	}
	
	@Override
	protected void onStartWork() {
		getWorld().playSound(null, getPos(), SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.2F);
	}
	
	@Override
	protected boolean toggleMachine(boolean on) {
		boolean ret = super.toggleMachine(on);
		if (ret) updateLight();
		return ret;
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
