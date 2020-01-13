package dohyun22.jst3.tiles.noupdate;

import java.util.List;

import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.api.IDCGenerator;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MetaTileDCWatermill extends MetaTileBase implements IDCGenerator {
	private final byte tier;
	private int currentFlow;
	private byte state;

	public MetaTileDCWatermill(int tier) {
		this.tier = (byte)tier;
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MetaTileDCWatermill(tier);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		currentFlow = tag.getInteger("fl");
		state = tag.getByte("ms");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("fl", currentFlow);
		tag.setByte("ms", state);
	}

	@Override
	public double getPower(World w, BlockPos p) {
		if ((state & 1) == 0) return 0;
		currentFlow = MetaTileDCWind.updateWind(currentFlow, w.rand);
		double ret = currentFlow * cntWater(w) / 40.0D;
		if (ret <= 0.0D) return 0;
		if ((state >> 1 & 1) == 1) ret *= 2.0D;
		if (ret > 1.0D) ret = 1.0D;
		if (w.rand.nextInt(5) == 0) w.playSound(null, getPos(), SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, (float)ret * 1.5F, 1.0F);
		ret = (tier < 0 ? 4 : JSTUtils.getVoltFromTier(tier)) * ret;
		return ret;
	}
	
	private double cntWater(World w) {
		BlockPos p = getPos();
		int cnt = 0;
		int rng = Math.max(0, tier + 1);
		for (int dx = -4 - rng; dx <= 4 + rng; dx++)
			for (int dy = -4 - rng; dy <= 4 + rng; dy++)
				for (int dz = -4 - rng; dz <= 4 + rng; dz++)
					if (w.getBlockState(p.add(dx, dy, dz)).getMaterial() == Material.WATER)
						cnt++;
		rng = 9 + rng * 2;
		rng = rng * rng * rng;
		return cnt / (double)rng;
	}

	@Override
	public boolean canUpdate() {
		return false;
	}
	
	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		this.onBlockUpdate();
	}
	
	@Override
	public void onBlockUpdate() {
		state = 0;
		if (JSTUtils.hasType(getWorld().getBiome(getPos()), BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.RIVER))
			state += 2;
		for (EnumFacing f : EnumFacing.HORIZONTALS) {
			BlockPos p = getPos().offset(f);
			if (getWorld().getBlockState(p).getMaterial() == Material.WATER) {
				state += 1;
				break;
			}
		}
	}
	
	@Override
	@SideOnly(value = Side.CLIENT)
	public String getModelKey() {
		return "jst_dcwater" + tier;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite s = tier < 0 ? getTETex("basic_side") : getTieredTex(tier);
		TextureAtlasSprite w = getTETex("basic_water");
		return new TextureAtlasSprite[] {s, s, w, w, w, w};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.dcgen.desc"));
		int rng = Math.max(0, tier + 1);
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.dcgen.water", 4 + rng));
	}
}
