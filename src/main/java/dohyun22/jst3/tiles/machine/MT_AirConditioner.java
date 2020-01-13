package dohyun22.jst3.tiles.machine;

import java.util.List;

import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileEnergyInput;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import toughasnails.api.TANPotions;
import toughasnails.api.stat.capability.ITemperature;
import toughasnails.api.temperature.Temperature;
import toughasnails.api.temperature.TemperatureHelper;

public class MT_AirConditioner extends MetaTileEnergyInput {
	private final byte tier;
	private boolean blocked;
	private boolean manual;
	private boolean working;

	public MT_AirConditioner(int tier) {
		this.tier = (byte) tier;
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_AirConditioner(tier);
	}

	@Override
	public int maxEUTransfer() {
		return JSTUtils.getVoltFromTier(tier);
	}

	@Override
	public long getMaxEnergy() {
		return maxEUTransfer() * 64;
	}

	@Override
	public void onPostTick() {
		super.onPostTick();
		World w = getWorld();
		if (w == null) return;
		if (w.isRemote) {
			if (baseTile.isActive()) {
				BlockPos p = JSTUtils.getOffset(getPos(), baseTile.facing, 1);
				w.spawnParticle(EnumParticleTypes.REDSTONE, p.getX() + w.rand.nextFloat(), p.getY() + w.rand.nextFloat(), p.getZ() + w.rand.nextFloat(), 0.60D, 0.75D, 1.0D);
			}
			return;
		}
		if (blocked || !working || baseTile.energy < maxEUTransfer() || baseTile.facing == null) {
			baseTile.setActive(false);
			return;
		}
		baseTile.energy -= JSTUtils.getVoltFromTier(tier - 1);
		if (baseTile.getTimer() % 100 == 0) {
			baseTile.setActive(true);
			int r = 5 + (tier - 1) * 6;
			AxisAlignedBB ab = new AxisAlignedBB(getPos().getX() - r, getPos().getY() - r, getPos().getZ() - r, getPos().getX() + r, getPos().getY() + r, getPos().getZ() + r);
			ab = ab.offset(baseTile.facing.getFrontOffsetX() * r, baseTile.facing.getFrontOffsetY() * r, baseTile.facing.getFrontOffsetZ() * r);
			List<EntityPlayer> ls = w.getEntitiesWithinAABB(EntityPlayer.class, ab);
			try {
				for (EntityPlayer pl : ls) {
					ITemperature td = TemperatureHelper.getTemperatureData(pl);
					Temperature pt = td.getTemperature();
					int ct = pt.getRawValue();
		            if (ct < 14)
		            	td.setTemperature(new Temperature(Math.min(14, ct + 4)));
		            else if (ct > 14)
		            	td.setTemperature(new Temperature(Math.max(14, ct - 4)));
		            pl.removePotionEffect(TANPotions.hyperthermia);
					pl.removePotionEffect(TANPotions.hypothermia);
				}
			} catch (Throwable t) {}
		}
	}

	@Override
	public void onBlockUpdate() {
		super.onBlockUpdate();
		check();
		boolean rs = isRSPowered();
		if (!rs && manual) return;
		working = rs;
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack st, EnumFacing f, float hitX, float hitY, float hitZ) {
		if (baseTile != null && !getWorld().isRemote) {
			working = !working;
			manual = working;
			if (working) check();
			getWorld().playSound(null, getPos(), JSTSounds.SWITCH2, SoundCategory.BLOCKS, 1.0F, working ? 1.0F : 0.5F);
		}
		return true;
	}

	@Override
	public boolean canConnectRedstone(EnumFacing f) {
		return true;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		byte b = tag.getByte("dat");
		if ((b & 1) == 1) blocked = true;
		if ((b >> 1 & 1) == 1) manual = true;
		if ((b >> 2 & 1) == 1) working = true;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		byte b = 0;
		if (blocked) b += 1;
		if (manual) b += 2;
		if (working) b += 4;
		tag.setByte("dat", b);
	}

	private void check() {
		EnumFacing f = baseTile.facing;
		if (f == null) return;
		BlockPos p = getPos().offset(f);
		IBlockState b = getWorld().getBlockState(p);
		blocked = b.isSideSolid(getWorld(), p, baseTile.facing.getOpposite());
	}

	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		super.onPlaced(p, bs, elb, st);
		if (baseTile != null) baseTile.facing = JSTUtils.getClosestSide(p, elb, false);
	}

	@Override
	public boolean setFacing(EnumFacing f, EntityPlayer pl) {
		if (f != baseTile.facing)
			check();
		return doSetFacing(f, false);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		int r = (5 + (tier - 1) * 4) * 2 + 1;
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.aircon", r +"x" + r + "x" + r));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTieredTex(tier);
		return new TextureAtlasSprite[] {t, t, t, t, t, getTETex("basic_water")};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++)
			ret[n] = baseTile.facing == JSTUtils.getFacingFromNum(n) ? getTETex("basic_water") : getTieredTex(tier);
		return ret;
	}
}
