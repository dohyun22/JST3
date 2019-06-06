package dohyun22.jst3.entity;

import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public abstract class EntityCar extends Entity {
	private static final DataParameter<Float> DAMAGE = EntityDataManager.createKey(EntityCar.class, DataSerializers.FLOAT);
	private static final DataParameter<Integer> ENERGY = EntityDataManager.createKey(EntityCar.class, DataSerializers.VARINT);

	public EntityCar(World w) {
		super(w);
		setSize(1.375F, 0.5625F);
		stepHeight = 1.0F;
	}

	@Override
	public boolean attackEntityFrom(DamageSource s, float a) {
		if (!world.isRemote && !isDead) {
			if (isEntityInvulnerable(s))
				return false;
			markVelocityChanged();
			setDamage(getDamage() + a * 10.0F);
			boolean flag = s.getTrueSource() instanceof EntityPlayer && (((EntityPlayer)s.getTrueSource()).capabilities.isCreativeMode);
			if (flag || getDamage() > 40.0F) {
				if (!flag && world.getGameRules().getBoolean("doEntityDrops"))
					entityDropItem(getDrop(), 0.0F);
				setDead();
			}
			return true;
		}
		return true;
	}

	@Override
	public boolean processInitialInteract(EntityPlayer pl, EnumHand h) {
		if (pl.isSneaking())
			return false;
		if (isBeingRidden())
			return true;
		if (!world.isRemote)
			pl.startRiding(this);
		return true;
	}

	@Override
	protected void entityInit() {
		dataManager.register(DAMAGE, 0.0F);
		dataManager.register(ENERGY, 0);
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity e) {
		return e.canBePushed() ? e.getEntityBoundingBox() : null;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox() {
		return getEntityBoundingBox();
	}

	@Override
	public boolean canBeCollidedWith() {
		return !isDead;
	}

	@Override
	public void onUpdate() {
		if (!world.isRemote) {
			if (getDamage() > 0.0F)
				setDamage(getDamage() - 1.0F);
			prevPosX = posX;
			prevPosY = posY;
			prevPosZ = posZ;
			if (posY < -64.0D) {
	    		outOfWorld();
	    		return;
	    	}
		}
		setPositionNonDirty();
		super.onUpdate();
		if (!hasNoGravity()) motionY -= 0.25D;
		update();
	    move(MoverType.SELF, motionX, motionY, motionZ);
	}

	@Override
    public boolean shouldDismountInWater(Entity e) {
    	return true;
    }

	@Override
	public ItemStack getPickedResult(RayTraceResult r) {
		return getDrop();
	}

	private void update() {
    	Entity e = getPassengers().isEmpty() ? null : (Entity) getPassengers().get(0);
		if (!world.isRemote && e instanceof EntityPlayer && ticksExisted % 40 == 0)
			sendMsg((EntityPlayer)e);
    	int eu = getEnergy();
    	int u = getUsage();
    	motionX *= 0.8D;
    	motionZ *= 0.8D;
    	if (eu >= u * 2) {
    		if (!inWater && e instanceof EntityLivingBase && ((EntityLivingBase)e).moveForward > 0) {
    			onRunning();
    			double s = getSpeed();
    			motionX = MathHelper.sin(-e.rotationYaw * 0.017453292F) * s;
    			motionZ = MathHelper.cos(e.rotationYaw * 0.017453292F) * s;
    			if (!world.isRemote) {
	    			rotationYaw = e.rotationYaw;
					if (ticksExisted % 16 == 0) world.playSound(null, e.getPosition(), JSTSounds.ENGINE, SoundCategory.MASTER, 1.4F, 0.8F);
					eu -= u;
					if (eu < u * 2) eu += getNewEnergy();
					setEnergy(eu);
				}
    		}
    	} else if (!world.isRemote) {
    		eu = getNewEnergy();
    		if (eu > 0) setEnergy(getNewEnergy());
    	}
	}

	protected void onRunning() {}

	protected void sendMsg(EntityPlayer pl) {}

	protected abstract ItemStack getDrop();

	public abstract ResourceLocation getTex();

	protected abstract int getUsage();

	protected abstract int getNewEnergy();

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		setEnergy(nbt.getInteger("engy"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setInteger("engy", getEnergy());
	}

	public float getDamage() {
		return dataManager.get(DAMAGE);
	}

	public void setDamage(float f) {
		dataManager.set(DAMAGE, f);
	}

	public int getEnergy() {
		return dataManager.get(ENERGY);
	}

	public void setEnergy(int f) {
		dataManager.set(ENERGY, f);
	}

	protected double getSpeed() {
		IBlockState bs = world.getBlockState(getPosition().down());
		String n = JSTUtils.getRegName(bs);
		String[] sa = n.split(":");
		if ((sa.length == 2 && (sa[1].contains("asphalt") || sa[1].contains("concrete"))) || n.equals("immersivepetroleum:stone_decoration"))
			return 0.7D;
		Material m = bs.getMaterial();
		if (m == Material.ROCK || m == Material.IRON) return 0.5D;
		return 0.45D;
	}
}
