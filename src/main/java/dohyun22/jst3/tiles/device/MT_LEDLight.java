package dohyun22.jst3.tiles.device;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.compat.ic2.CompatIC2;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileEnergyInput;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTUtils;
import ic2.api.crops.CropCard;
import ic2.api.crops.ICropTile;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_LEDLight extends MetaTileEnergyInput {
	private final byte tier;
	private static final AxisAlignedBB LED_AABB = new AxisAlignedBB(0.0D, 0.875D, 0.0D, 1.0D, 1.0D, 1.0D);

	public MT_LEDLight(int t) {
		tier = (byte)t;
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_LEDLight(tier);
	}

	@Override
	public List<AxisAlignedBB> getBox() {
		super.getBox();
		return Arrays.asList(new AxisAlignedBB[] { LED_AABB });
	}
	
	@Override
	@Nullable
	public AxisAlignedBB getBoundingBox() {
		return LED_AABB;
	}
	
	@Override
	public int maxEUTransfer() {
		return JSTUtils.getVoltFromTier(tier);
	}
	
	@Override
	public long getMaxEnergy() {
		return JSTUtils.getVoltFromTier(tier);
	}
	
	@Override
	public boolean isEnergyInput(EnumFacing side) {
		return side == EnumFacing.UP;
	}
	
	@Override
	public void onPostTick() {
		super.onPostTick();
		if (isClient()) return;
		
		boolean flag = baseTile.energy < 1;
		if (baseTile.setActive(!flag))
			updateLight();
		if (flag) {
			return;
		} else {
			baseTile.energy -= 1;
		}
		
		if (baseTile.getTimer() % 100 == 0) {
			BlockPos p = getPos();
			MutableBlockPos mp = new MutableBlockPos();
			for (int x = -tier; x <= tier; x++) {
				for (int z = -tier; z <= tier; z++) {
					for (int y = -1; y >= (-4 - tier); y--) {
						mp.setPos(p.getX() + x, p.getY() + y, p.getZ() + z);
						IBlockState bs = getWorld().getBlockState(mp);
						Block b = bs.getBlock();
			            if (b == Blocks.AIR || b == null) continue;
			            if (JSTCfg.ic2Loaded && CompatIC2.growCrop(getWorld().getTileEntity(mp), 8 + getWorld().rand.nextInt(4), false))
			            	break;
			            if (b instanceof IGrowable || b instanceof IPlantable || MRecipes.LEDCrops.contains(b)) {
			            	if (getWorld().rand.nextInt(4) != 0)
			            		getWorld().scheduleBlockUpdate(mp, b, 0, 1);
			            	break;
			            }
			            if (b.getLightOpacity(bs, getWorld(), mp) >= 25)
			            	break;
					}
				}
			}
		}
	}
	
	@Override
	public int getLightValue() {
		return baseTile.isActive() ? 12 : 0;
	}
	
	@Override
	public boolean isSideSolid(EnumFacing s) {
		return s == EnumFacing.UP;
	}
	
	@Override
	public boolean isSideOpaque(EnumFacing s) {
		return s == EnumFacing.UP;
	}
	
	@Override
	public int getLightOpacity() {
		return 0;
	}
	
	@Override
	@SideOnly(value = Side.CLIENT)
	public String getModelKey() {
		return "jst_led" + tier + baseTile.isActive();
	}
	
	@Override
	@SideOnly(value=Side.CLIENT)
	public List<BakedQuad> getModel(boolean isItem) {
		return JSTUtils.makeCubeAABB(isItem ? getDefaultTexture() : getTexture(), isItem ? LED_AABB.offset(0, -0.4375D, 0) : LED_AABB);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite s = getTieredTex(tier);
		return new TextureAtlasSprite[] {getTETex("crop_led" + (!baseTile.isActive() ? "_off" : "")), s, s, s, s, s};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite s = getTieredTex(tier);
		return new TextureAtlasSprite[] {s, getTETex("crop_led"), s, s, s, s};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getInformation(ItemStack st, World w, List<String> ls, ITooltipFlag adv) {
		int a = tier * 2 + 1, b = 4 + tier;
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.led", a + "x" + b + "x" + a));
	}
}
