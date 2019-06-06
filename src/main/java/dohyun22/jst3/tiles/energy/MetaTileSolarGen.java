package dohyun22.jst3.tiles.energy;

import java.util.List;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MetaTileSolarGen extends MetaTileGenerator {

	public MetaTileSolarGen(int tier) {
		super(tier, false);
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MetaTileSolarGen(tier);
	}
	
	@Override
	public int maxEUTransfer() {
		if (tier < 0)
			return 1;
		return JSTUtils.getVoltFromTier(tier);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = tier < 0 ? getTETex("basic_side") : getTieredTex(tier);
		return new TextureAtlasSprite[] {t , getTETex("basic_solar"), t, t, t, t};
	}
	
	@Override
	public boolean canProvideEnergy() {
		return true;
	}
	
	@Override
	public boolean isEnergyOutput(EnumFacing side) {
		return side != EnumFacing.UP;
	}
	
	@Override
	protected void checkCanGenerate() {
		if (baseTile.getTimer() % 20 == 0)
			baseTile.setActive(JSTUtils.checkSun(getWorld(), getPos()));
	}
	
	@Override
	protected long getPower() {
		return this.maxEUTransfer();
	}
	
	@Override
	public long getMaxEnergy() {
		return this.maxEUTransfer();
	}
	
	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		if (!getWorld().isRemote)
			baseTile.setActive(JSTUtils.checkSun(getWorld(), getPos()));
	}

	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, ITooltipFlag adv) {}
}
