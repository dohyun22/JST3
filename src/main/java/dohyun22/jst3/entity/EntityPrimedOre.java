package dohyun22.jst3.entity;

import java.util.Random;

import dohyun22.jst3.evhandler.EvHandler;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityPrimedOre extends Entity {
	private int fuse;

	public EntityPrimedOre (World w) {
		super(w);
		fuse = 60 + world.rand.nextInt(40);
		noClip = true;
		preventEntitySpawning = false;
		setSize(0.0F, 0.0F);
	}

	public EntityPrimedOre(World w, double x, double y, double z) {
		this(w);
		setPosition(x, y, z);
		motionX = 0.0F;
		motionY = 0.0F;
		motionZ = 0.0F;
		fuse = 60 + world.rand.nextInt(40);
		prevPosX = x;
		prevPosY = y;
		prevPosZ = z;
	}
	
	@Override
	protected void entityInit() {}

	@Override
	public boolean canTriggerWalking() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}
	
	@Override
	public void onUpdate() {
		if(!EvHandler.NetherOreList.contains(world.getBlockState(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY), MathHelper.floor(posZ))).getBlock())) {
			setDead();
			return;
		}
		if(!world.isRemote) {
			if (fuse-- <= 0) {
				setDead();
				explode(MathHelper.floor(posX), MathHelper.floor(posY), MathHelper.floor(posZ));
			}/* else if (this.fuse % 20 == 0) {
				world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, JSTSounds.EJECT, SoundCategory.BLOCKS, 2.0F, 1.0F);
			}*/
		} else {
			genEffect(world, MathHelper.floor(posX), MathHelper.floor(posY - 0.1D), MathHelper.floor(posZ));
		}
		if (world.isRemote) genEffect(world, MathHelper.floor(posX), MathHelper.floor(posY - 0.1D), MathHelper.floor(posZ));
	}
	
	private void explode(int x, int y, int z) {
		world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, JSTSounds.BOOM, SoundCategory.BLOCKS, 4.0F, 1.0F);
	    world.createExplosion(null, this.posX, this.posY, this.posZ, 2.5F, false);
		for(int xd = -1; xd <= 1; xd++) {
			for(int yd = -1; yd <= 1; yd++) {
				for(int zd = -1; zd <= 1; zd++) {
					int bx = x + xd;
					int by = y + yd;
					int bz = z + zd;
					BlockPos pos = new BlockPos(bx, by, bz);
					Block bl = this.world.getBlockState(pos).getBlock();
					if ((bl != null && EvHandler.NetherOreList.contains(bl)) || (this.world.getTileEntity(pos) == null && bl.getExplosionResistance(this) <= 0.5F)) {
						int a_8 = this.world.rand.nextInt(3);
						switch (a_8) {
						case 0: {
							if (this.world.getGameRules().getBoolean("doTileDrops"))
								this.world.getBlockState(pos).getBlock().dropBlockAsItemWithChance(this.world, pos, this.world.getBlockState(pos), 1.0F, 0);
							this.world.setBlockToAir(pos);
							break;
						}
						case 1: 
							this.world.setBlockToAir(pos);
							break;
						case 2:	
							this.world.setBlockState(pos, Blocks.FIRE.getDefaultState());
							break;
						}
					}
				}
			}
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		tag.setByte("fuse", (byte)fuse);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		fuse = tag.getByte("fuse");
	}
	
	@SideOnly(Side.CLIENT)
	private static void genEffect(World w, int x, int y, int z) {
		for (int n = 0; n < 5; ++n) {
			double dx = (x - 0.12D) + (w.rand.nextFloat() * 1.24D);
			double dy = (y - 0.12D) + (w.rand.nextFloat() * 1.24D);
			double dz = (z - 0.12D) + (w.rand.nextFloat() * 1.24D);
			if (dx < x || dx > x + 1 || dy < y || dy > y + 1 || dz < z || dz > z + 1)
				w.spawnParticle(w.rand.nextInt(5) == 0 ? EnumParticleTypes.FLAME : EnumParticleTypes.SMOKE_NORMAL, dx, dy, dz, 0.0D, 0.0D, 0.0D, new int[0]);
		}
	}
}