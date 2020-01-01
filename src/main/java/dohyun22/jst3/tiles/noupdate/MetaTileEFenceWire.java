package dohyun22.jst3.tiles.noupdate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import dohyun22.jst3.network.JSTPacketHandler;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.device.MT_EFenceChgr;
import dohyun22.jst3.utils.JSTDamageSource;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import dohyun22.jst3.utils.JSTDamageSource.EnumHazard;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MetaTileEFenceWire extends MetaTileBase {
	private byte connection;

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MetaTileEFenceWire();
	}

	@Override
	public boolean canUpdate() {
		return false;
	}
	
	@Override
	public List<AxisAlignedBB> getBox() {
		float d = 0.125F;
		float s = (1.0F - d) / 2.0F;
		List<AxisAlignedBB> ret = new ArrayList(7);

		ret.add(new AxisAlignedBB(s, s, s, s + d, s + d, s + d));

		for (byte n = 0; n < 6; n++) {
			if ((this.connection & 1 << n) != 0) {
				float xS, yS, zS;
				xS = yS = zS = s;
				float xE, yE, zE;
				xE = yE = zE = s + d;
				switch (n) {
				case 0:
					yS = 0.0F;
					yE = s;
					break;
				case 1:
					yS = s + d;
					yE = 1.0F;
					break;
				case 2:
					zS = 0.0F;
					zE = s;
					break;
				case 3:
					zS = s + d;
					zE = 1.0F;
					break;
				case 4:
					xS = 0.0F;
					xE = s;
					break;
				case 5:
					xS = s + d;
					xE = 1.0F;
					break;
				}
				ret.add(new AxisAlignedBB(xS, yS, zS, xE, yE, zE));
			}
		}
		return ret;
	}
	
	@Override
	@Nullable
	public AxisAlignedBB getBoundingBox() {
		List<AxisAlignedBB> boxes = getBox();
		if (boxes.isEmpty())
			return null;
		if (boxes.size() == 1)
			return (AxisAlignedBB) boxes.get(0);
		double zS;
		double yS;
		double xS = yS = zS = Double.POSITIVE_INFINITY;
		double zE;
		double yE;
		double xE = yE = zE = Double.NEGATIVE_INFINITY;
		for (AxisAlignedBB ab : boxes) {
			xS = Math.min(xS, ab.minX);
			yS = Math.min(yS, ab.minY);
			zS = Math.min(zS, ab.minZ);
			xE = Math.max(xE, ab.maxX);
			yE = Math.max(yE, ab.maxY);
			zE = Math.max(zE, ab.maxZ);
		}
		return new AxisAlignedBB(xS, yS, zS, xE, yE, zE);
	}
	
	@Override
	public void onBlockUpdate() {
		if (isClient()) return;
		
		byte c = 0;
		byte i = 0;
		for (byte j = 0; i < 6; i++) {
			j = (byte) EnumFacing.VALUES[i].getOpposite().getIndex();
			MetaTileBase mte = MetaTileBase.getMTE(getWorld(), getPos().offset(EnumFacing.VALUES[i]));
			if (mte instanceof MetaTileEFenceWire || mte instanceof MT_EFenceChgr)
				c = ((byte) (c | 1 << i));
		}
		if (connection != c) {
			connection = c;
			doBlockUpdate();
			markDirty();
		}
	}
	
	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		this.onBlockUpdate();
	}
	
	@Override
	public void readSyncableDataFromNBT(NBTTagCompound tag) {
		connection = tag.getByte("cdir");
	}

	@Override
	public void writeSyncableDataToNBT(NBTTagCompound tag) {
		tag.setByte("cdir", connection);
	}
	
	@Override
	public boolean isOpaque() {
		return false;
	}
	
	@Override
	public boolean isSideSolid(EnumFacing s) {
		return false;
	}
	
	@Override
	public boolean isSideOpaque(EnumFacing f) {
		return false;
	}
	
	@Override
	public long getMaxEnergy() {
		return 51200;
	}
	
	public long chargeFence(long in, int lvl) {
		long eu = Math.max(Math.min(Math.min(lvl, getMaxEnergy()) - baseTile.energy, in), 0);
		baseTile.energy += eu;
		return eu;
	}
	
	@Override
	public void onEntityCollided(Entity e) {
		if (!isClient() && e instanceof EntityLivingBase) {
			int dmg = Math.max(2, (int)(baseTile.energy / 3200));
			if (baseTile.energy > 50 * dmg && !JSTDamageSource.hasFullHazmat(EnumHazard.ELECTRIC, (EntityLivingBase)e) && e.attackEntityFrom(JSTDamageSource.ELECTRIC, dmg)) {
				JSTPacketHandler.playCustomEffect(getWorld(), getPos(), 1, -10);
				baseTile.energy -= 50 * dmg;
			}
		}
	}
	
	@Override
	@SideOnly(value = Side.CLIENT)
	public String getModelKey() {
		return "efc" + connection;
	}

	@Override
	@SideOnly(value = Side.CLIENT)
	public List<BakedQuad> getModel(boolean isItem) {
		return makePipeModel(MetaTileBase.getSingleTex("minecraft:blocks/anvil_base"), 1.0F, connection, isItem);
	}
}
