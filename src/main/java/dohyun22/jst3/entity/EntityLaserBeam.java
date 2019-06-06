package dohyun22.jst3.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityLaserBeam extends Entity implements IThrowableEntity {
	private int timer;
	public EntityLivingBase owner;
	private int mode;
	private boolean headingSet;
	public float range = 0.0F;
	public float power = 0.0F;
	public int blockBreaks;
	public static Set<Block> blacklist = new HashSet(Arrays.asList(Blocks.STONE, Blocks.COBBLESTONE, Blocks.DIRT, Blocks.GRASS, Blocks.MYCELIUM, Blocks.SAND, Blocks.GRAVEL, Blocks.NETHERRACK, Blocks.END_STONE, Blocks.SOUL_SAND, Blocks.MAGMA));
	
	public EntityLaserBeam(World w) {
	    super(w);
	    this.setSize(0.8F, 0.8F);
	    this.timer = 0;
	    this.isImmuneToFire = true;
	    this.noClip = true;
	}
	
	public EntityLaserBeam(World w, EntityLivingBase elb, float rng, int md, double yawD, double pitchD, double y, float pwr, int nBreak) {
		this(w);
	    this.owner = elb;
	    
	    double yaw = Math.toRadians(yawD);
	    double pitch = Math.toRadians(pitchD);
	    
	    double x = elb.posX - Math.cos(yaw) * 0.16D;
	    double z = elb.posZ - Math.sin(yaw) * 0.16D;
	    
	    setPosition(x, y, z);
	    setVelocity(-Math.sin(yaw) * Math.cos(pitch), -Math.sin(pitch), Math.cos(yaw) * Math.cos(pitch));
	    
	    this.range = rng;
	    this.power = pwr;
	    this.blockBreaks = nBreak;
	    this.mode = md;
	}
	
	@Override
	public Entity getThrower() {
		return this.owner;
	}
	  
	@Override
	public void setThrower(Entity e) {
		if (e instanceof EntityLivingBase) {
			this.owner = ((EntityLivingBase)e);
		}
	}

	@Override
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }

    @Override
    public float getBrightness() {
        return 2.0F;
    }
	
	@Override
	public void setVelocity(double x, double y, double z) {
		double currentSpeed = MathHelper.sqrt(x * x + y * y + z * z);
		
	    this.motionX = (x / currentSpeed);
	    this.motionY = (y / currentSpeed);
	    this.motionZ = (z / currentSpeed);
	    
	    this.prevRotationYaw = (this.rotationYaw = (float)Math.toDegrees(Math.atan2(motionX, motionZ)));
	    this.prevRotationPitch = (this.rotationPitch = (float)Math.toDegrees(Math.atan2(motionY, MathHelper.sqrt(motionX * motionX + motionZ * motionZ))));
	    
	    this.headingSet = true;
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();

		if (!JSTUtils.isClient() && (this.range < 1.0F || Float.isNaN(range) || this.power <= 0.0F || this.blockBreaks <= 0 || !(this.owner instanceof EntityPlayer) || this.timer > 300)) {
			setDead();
			return;
		}
		this.timer += 1;
		
		Vec3d oP = new Vec3d(this.posX, this.posY, this.posZ);
		Vec3d nP = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
	    
		RayTraceResult rtr = this.world.rayTraceBlocks(oP, nP, false, true, false);
	    
		oP = new Vec3d(this.posX, this.posY, this.posZ);
		if (rtr != null) {
			nP = new Vec3d(rtr.hitVec.x, rtr.hitVec.y, rtr.hitVec.z);
		} else {
			nP = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
		}
		
		Entity e = null;
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D));
		double d = 0.0D;
		for (int l = 0; l < list.size(); l++) {
			Entity e2 = (Entity)list.get(l);
			if (e2.canBeCollidedWith() && (e2 != this.owner || this.timer >= 5)) {
				float f4 = 0.3F;
				AxisAlignedBB aabb = e2.getEntityBoundingBox().expand(f4, f4, f4);
				RayTraceResult rtr2 = aabb.calculateIntercept(oP, nP);
				if (rtr2 != null) {
					double d1 = oP.distanceTo(rtr2.hitVec);
					if ((d1 < d) || (d == 0.0D)) {
						e = e2;
						d = d1;
					}
				}
			}
		}
		if (e != null) {
			rtr = new RayTraceResult(e);
		}
		if (rtr != null && !JSTUtils.isClient()) {
			if (rtr.entityHit != null) {
				int dmg = (int)(this.power / 2);
				if (dmg > 0 && e != null && e.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), dmg))
					e.setFire(dmg);
				setDead();
				return;
			} else {
				BlockPos p = rtr.getBlockPos();
				IBlockState bs = this.world.getBlockState(p);
				Block b = bs.getBlock();
				if (!b.isAir(bs, world, p) && (bs.getMaterial() != Material.GLASS || bs.isOpaqueCube())) {
					float re = bs.getBlockHardness(world, p);
					
					if (this.mode == 2 || re < 0.0F || bs.getMaterial().isLiquid()) {
						setDead();
						return;
					}
					
					this.power -= re;
					if (this.power >= 0.0F) {
						if (!JSTUtils.canPlayerBreakThatBlock((EntityPlayer) this.owner, p)) {
							setDead();
							return;
						}
						boolean mBreak = true;
						boolean mDrop = this.mode != 1 || !this.blacklist.contains(b);
						List<ItemStack> ls = JSTUtils.getBlockDrops(world, p, bs, 0, 1, false, null, true);
						if (this.mode == 3) {
							for (ItemStack ps : ls) {
								ItemStack sr = FurnaceRecipes.instance().getSmeltingResult(ps);
								if (sr != null && !sr.isEmpty()) {
									int mt = sr.getItemDamage();
									JSTUtils.dropEntityItemInPos(this.world, p, sr);
									mDrop = false;
								}
							}
						}	
						
						if (mDrop)
							JSTUtils.dropAll(world, p, ls);
						if (mBreak) {
							this.world.setBlockToAir(p);
							if ((this.world.rand.nextInt(10) == 0) && (bs.getMaterial().getCanBurn()))
								this.world.setBlockState(p, Blocks.FIRE.getDefaultState(), 3);
						}
					}
					this.blockBreaks -= 1;
				}
			}
		}
		
		setPosition(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
		this.range = ((float)(this.range - Math.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ)));
		
		if (isInWater())
			setDead();
	}

	@Override
	protected void entityInit() {
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
	}
}

