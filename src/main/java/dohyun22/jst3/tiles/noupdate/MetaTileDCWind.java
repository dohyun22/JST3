package dohyun22.jst3.tiles.noupdate;

import java.util.List;
import java.util.Random;

import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.interfaces.IDCGenerator;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MetaTileDCWind extends MetaTileBase implements IDCGenerator {
	private final byte tier;
	private int currentWind;

	public MetaTileDCWind(int tier) {
		this.tier = (byte)tier;
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MetaTileDCWind(tier);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		currentWind = tag.getInteger("wind");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("wind", currentWind);
	}

	@Override
	public double getPower(World w, BlockPos p) {
		currentWind = updateWind(currentWind, w.rand);
		double ret = currentWind * (getPos().getY() - 32 - cntBlocks(w)) / 1400.0D;
		if (ret <= 0.0D) return 0;
		if (w.isThundering())
			ret *= 1.5D;
		else if (w.isRaining())
			ret *= 1.2D;
		if (ret > 1.0D) ret = 1.0D;
		if (w.rand.nextInt(5) == 0) w.playSound(null, getPos(), SoundEvents.ITEM_ELYTRA_FLYING, SoundCategory.BLOCKS, (float)ret * 1.5F, 1.0F);
		ret = (tier < 0 ? 4 : JSTUtils.getVoltFromTier(tier)) * ret;
		return ret;
	}
	
	@Override
	public boolean canUpdate() {
		return false;
	}
	
	private int cntBlocks(World w) {
		BlockPos p = getPos();
		int cnt = -1;
		int rng = Math.max(0, tier + 1);
		for (int dx = -4 - rng; dx <= 4 + rng; dx++)
			for (int dy = -2 - rng / 2; dy <= 4 + rng; dy++)
				for (int dz = -4 - rng; dz <= 4 + rng; dz++)
					if (!w.isAirBlock(p.add(dx, dy, dz)))
						cnt++;
		return cnt;
	}
	
	public static int updateWind(int pwr, Random r) {
		if (pwr <= 0) pwr = 10 + r.nextInt(10);
		int upChance = 10;
		int downChance = 10;
		if (pwr > 20) upChance -= pwr - 20;
	    if (pwr < 5) downChance -= (5 - pwr) * 2;
	    if (r.nextInt(100) <= upChance) {
	    	pwr += 1;
	    	return pwr;
	    }
	    if (r.nextInt(100) <= downChance) {
	    	pwr -= 1;
	    	return pwr;
	    }
		return pwr;
	}
	
	@Override
	@SideOnly(value = Side.CLIENT)
	public String getModelKey() {
		return "jst_dcwind" + tier;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite s = tier < 0 ? getTETex("basic_side") : getTieredTex(tier);
		TextureAtlasSprite w = getTETex("basic_wind");
		return new TextureAtlasSprite[] {s, s, w, w, w, w};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getInformation(ItemStack st, World w, List<String> ls, ITooltipFlag adv) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.dcgen.desc"));
		int rng = Math.max(0, tier + 1);
		ls.add(I18n.format("jst.tooltip.tile.dcgen.wind", 4 + rng, 2 + rng / 2));
	}
}
