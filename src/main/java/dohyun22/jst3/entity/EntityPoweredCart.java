package dohyun22.jst3.entity;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.client.gui.GUIECart;
import dohyun22.jst3.compat.rc.CompatRC;
import dohyun22.jst3.container.ContainerECart;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.api.charge.Charge;
import mods.railcraft.common.carts.IDirectionalCart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface="mods.railcraft.common.carts.IDirectionalCart", modid="railcraft")
public class EntityPoweredCart extends EntityMinecart implements IDirectionalCart, IGUIEntity {
	public boolean reverse;
	public double engy;
	public double draw;
	private double ltd;
	private static final double MAXC = 2500.0D;
	private BlockPos trackXYZ;
	private int idle;
	private int timer;
	private byte sTimer;
	private static final DataParameter<Byte> SPEED = EntityDataManager.createKey(EntityPoweredCart.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> PAN_STATE = EntityDataManager.createKey(EntityPoweredCart.class, DataSerializers.BYTE);

	public EntityPoweredCart(World w) {
		super(w);
		this.setSize(0.98F, 1.2F);
	}
	
	public EntityPoweredCart(World w, double x, double y, double z) {
	    super(w, x, y, z);
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		getDataManager().register(SPEED, Byte.valueOf((byte)0));
		getDataManager().register(PAN_STATE, Byte.valueOf((byte)0));
	}
	
	@Override
	protected void moveAlongTrack(BlockPos p, IBlockState bs) {
		super.moveAlongTrack(p, bs);
	    if (world.isRemote)
	    	return;
	    
	    try {
		    if (trackXYZ == null || !trackXYZ.equals(p)) {
		    	trackChange(p);
		    	trackXYZ = p;
		    }
		    
		    byte h = findCatenary(p);
		    setPanState(h);
		    if (engy < MAXC && ticksExisted % 8 == 0) {
		    	if (h > 0) {
		    		if (JSTCfg.rcLoaded) {
		    			double get = Charge.distribution.network(world).access(p.up(h + 1)).removeCharge(MAXC - engy);
		    			engy += get;
		    			if (get > 0 && world.rand.nextInt(30) == 0 && getSpeed() > 0)
		    				Charge.hostEffects().zapEffectDeath(world, new Vec3d(posX, p.getY() + h + 1, posZ));
		    		}
		    	}
		    	if (engy < MAXC && JSTCfg.rcLoaded)
		    		engy += Charge.distribution.network(world).access(p).removeCharge(MAXC - engy);
		    }
	    } catch (Throwable t) {}
	}

    protected void moveDerailedMinecart() {
    	super.moveDerailedMinecart();
	    if (!world.isRemote) setPanState((byte) 0);
    }
	
	@Override
	protected void applyDrag() {
		motionX *= 0.9D;
		motionY *= 0.0D;
	    motionZ *= 0.9D;
	    
	    byte sp = getSpeed();
	    if (engy > 0.0D && idle <= 0 && sp != 0) {
	    	boolean hs = false;
	    	float fo = 0.32F;
	    	if (sp == 4) {
	    		hs = getEntityData().getBoolean("HighSpeed");
	    		if (hs) fo *= 5.0F;
	    	}
	    	drawEngy(Math.abs(sp) * 3.0D * (hs ? 1.2D : 1.0D));
	    	double yaw = rotationYaw * Math.PI / 180.0D;
	    	int rev = reverse ? -1 : 1;
	    	motionX += Math.cos(yaw) * fo * rev;
	    	motionZ += Math.sin(yaw) * fo * rev;
	    }
	    if (sp != 4) {
	    	float lim = sp * 0.1F;
	    	motionX = Math.copySign(Math.min(Math.abs(motionX), lim), motionX);
	    	motionZ = Math.copySign(Math.min(Math.abs(motionZ), lim), motionZ);
	    }
	}

	@Override
	public Type getType() {
		return null;
	}

	@Override
	public boolean isPoweredCart() {
	    return true;
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setBoolean("rev", reverse);
		tag.setByte("speed", getSpeed());
		tag.setDouble("engy", engy);
		tag.setInteger("tm1", idle);
		tag.setInteger("tm2", timer);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		reverse = tag.getBoolean("rev");
		setSpeed(tag.getByte("speed"));
		engy = tag.getDouble("engy");
		idle = tag.getInteger("tm1");
		timer = tag.getInteger("tm2");
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!world.isRemote) {
		    if (timer > 0) timer -= 1;
			if (idle > 0) idle -= 1;
			
			draw = ((draw * 49.0D + ltd) / 50.0D);
			ltd = 0.0D;
		}
	}
	  
	@Override
	public void killMinecart(DamageSource ds) {
		setDead();
	    entityDropItem(getCartItem(), 0.0F);
	}
	
	@Override
	public ItemStack getCartItem() {
		return new ItemStack(JSTItems.item1, 1, 10031);
	}
	
	@Override
	public boolean processInitialInteract(EntityPlayer pl, EnumHand h) {
		if (!MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, pl, h)) && !world.isRemote)
			pl.openGui(JustServerTweak.INSTANCE, 2000, pl.world, getEntityId(), 0, 0);
		return true;
	}

	@Override
	public void reverse() {
	    rotationYaw += 180.0F;
	    motionX *= -1;
	    motionZ *= -1;
	}

	@Override
	public void setRenderYaw(float yaw) {}
	
	protected void trackChange(BlockPos p) {
		 TileEntity tile = world.getTileEntity(p);
		 if (tile != null) {
			 boolean hs = getEntityData().getBoolean("HighSpeed");
			 NBTTagCompound nbt = tile.writeToNBT(new NBTTagCompound());
			 if (nbt == null || !nbt.getString("id").equals("railcraft:track.outfitted")) return;
			 String s = nbt.getString("kit");
			 String rc = "railcraft_";
			 if (s.equals(rc + "locomotive")) {
				 if (nbt.getBoolean("powered")) {
					 byte m = nbt.getByte("mode"), v = getSpeed();
					 if (m == 0) {
						 if (v > 0) {
							 setSpeed(m);
							 if (hs) slowDown();
						 }
					 } else if (v <= 0)
						 setSpeed(2);
				 }
			 } else if (s.equals(rc + "throttle")) {
				 if (getSpeed() > 0 && nbt.getBoolean("powered") && Math.sqrt(motionX * motionX + motionZ * motionZ) > 0) {
					 byte m = 0;
					 switch (nbt.getString("locoSpeed")) {
					 case "MAX": m = 4; break;
					 case "NORMAL": m = 3; break;
					 case "SLOWER": m = 2; break;
					 case "SLOWEST": m = 1; break;
					 }
					 setSpeed(m);
					 boolean b = nbt.getBoolean("locoReverse"), b2 = false;
					 IBlockState bs = world.getBlockState(p);
					 m = (byte) bs.getBlock().getMetaFromState(bs);
					 if (m == 0 || m == 4 || m == 5) {
						 if (b != motionZ > 0)
							 b2 = true;
					 } else if (m == 1 || m == 2 || m == 3) {
						 if (!b != motionX > 0)
							 b2 = true;
					 }
					 if (b2) {
						if (hs) slowDown();
						reverse = !reverse;
						for (EntityMinecart c : CompatRC.getCartsInTrain(this)) {
							c.motionX *= -0.4D;// -x:w +x:e -z:n +z:s
							c.motionZ *= -0.4D;
							if (c instanceof EntityPoweredCart)
								((EntityPoweredCart) c).reverse = reverse;
						}
					}
				 }
			 } else if (s.equals(rc + "booster")) {
				 if (hs && !nbt.getBoolean("powered"))
					 slowDown();
			 } else if (s.equals(rc + "transition")) {
				 boolean reversed = nbt.getBoolean("reversed");
				 IBlockState bs = world.getBlockState(p);
				 int m = bs.getBlock().getMetaFromState(bs);
				 if (nbt.getBoolean("powered")) {
					 if (Math.sqrt(motionX * motionX + motionZ * motionZ) > 0.01D) {
						 if (m == 0 || m == 4 || m == 5) {
							 if ((reversed == motionZ < 0) && hs)
								 slowDown();
						 } else if (m == 1 || m == 2 || m == 3) {
							 if ((!reversed == motionX < 0) && hs)
								 slowDown();
						 }
					 }
				 } else if (hs) {
					 slowDown();
				 }
			 } else if (s.equals(rc + "whistle")) {
				 if (nbt.getBoolean("powered"))
					 for (EntityMinecart c : CompatRC.getCartsInTrain(this))
						 if (c instanceof EntityPoweredCart)
							 ((EntityPoweredCart)c).klaxon();
			 }
		 }
	}

	protected byte findCatenary(BlockPos p) {
		for (byte i = 1; i <= 3; i++) {
			IBlockState bs = world.getBlockState(p.up(i + 1));
			if (bs.getBlock() == JSTBlocks.blockOHW)
				return i;
			if (bs.getMaterial().blocksMovement())
				return 0;
		}
		return 0;
	}
	
    public double drawEngy(double amt) {
    	if (engy >= amt) {
    		engy -= amt;
    		ltd += amt;
    		return amt;
    	}
    	double r = engy;
    	engy = 0.0D;
    	ltd += r;
    	return r;
    }

	public byte getSpeed() {
		 return getDataManager().get(SPEED);
	}

	public void setSpeed(int s) {
		byte spd = getSpeed();
	    if (spd != s) {
	    	/*if (spd == 0)
	    		world.playSound(null, posX, posY, posZ, JSTSounds.ACCEL, SoundCategory.BLOCKS, 1.5F, 1.0F);
	    	else if (s == 0)
	    		world.playSound(null, posX, posY, posZ, JSTSounds.DECEL, SoundCategory.BLOCKS, 1.5F, 1.0F);*/
	    	getDataManager().set(SPEED, (byte)s);
	    }
	}
	
	public byte getPanState() {
	    return getDataManager().get(PAN_STATE);
	}
	
	public void setPanState(byte b) {
		if (getPanState() != b) getDataManager().set(PAN_STATE, b);
	}

	public void switchSpeed(boolean i) {
		byte s = getSpeed();
		if (i) {
			if (s > 0) {
				setSpeed(s--);
		    } else {
		    	s = 4;
		    	setSpeed(s);
		    }
		} else {
			if (s < 4) {
				setSpeed(s++);
		    } else {
		    	s = 0;
		    	setSpeed(s);
		    }
		}
	}

	private void slowDown() {
		idle = 20; getEntityData().removeTag("HighSpeed");
	}

	public void klaxon() {
		if (timer <= 0) {
	    	world.playSound(null, posX, posY, posZ, JSTSounds.BICYCLE, SoundCategory.BLOCKS, 2.0F, 1.0F);
	    	timer = 120;
	    }
	}

	@Override
	public Object getServerGUI(int id, EntityPlayer pl, World w) {
		return new ContainerECart(pl, this);
	}

	@Override
	public Object getClientGUI(int id, EntityPlayer pl, World w) {
		return new GUIECart(new ContainerECart(pl, this));
	}
}
