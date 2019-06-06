package dohyun22.jst3.entity;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIBasic;
import dohyun22.jst3.container.ContainerCarElec;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class EntityCarElec extends EntityCar implements IInventory, IGUIEntity, IEnergyStorage {
	private static final ResourceLocation TEX = new ResourceLocation("jst3:textures/entity/car_e.png");
	private static final int MAX_EU = 600000;
	public boolean dropItem = true;
	private NonNullList<ItemStack> con = NonNullList.withSize(1, ItemStack.EMPTY);

	public EntityCarElec(World w) {
		super(w);
	}

	@Override
	protected ItemStack getDrop() {
		ItemStack ret = new ItemStack(JSTItems.item1, 1, 10043);
		if (getEnergy() > 0) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("engy", getEnergy());
			JSTUtils.getOrCreateNBT(ret).setTag("EntityNBT", nbt);
		}
		return ret;
	}

	@Override
	public ResourceLocation getTex() {
		return TEX;
	}

	@Override
	protected int getUsage() {
		return 8;
	}

	@Override
	protected int getNewEnergy() {
		return 0;
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		if (ticksExisted % 20 == 0) {
			int eu = getEnergy();
			int s = MAX_EU - eu;
			if (s > 0)
				setEnergy(eu + JSTUtils.convLongToInt(JSTUtils.dischargeItem(con.get(0), Math.min(s, 10000), 3, true, false)));
		}
	}

	@Override
	public void setDead() {
		if (!world.isRemote) InventoryHelper.dropInventoryItems(world, this, this);
		super.setDead();
	}

	@Override
	public Entity changeDimension(int d, ITeleporter t) {
		dropItem = false;
		return super.changeDimension(d, t);
	}

	@Override
	public void setDropItemsWhenDead(boolean b) {
		dropItem = b;
	}

	@Override
	public boolean processInitialInteract(EntityPlayer pl, EnumHand h) {
		if (!world.isRemote && pl.isSneaking()) {
			pl.openGui(JustServerTweak.INSTANCE, 2000, pl.world, getEntityId(), 0, 0);
			return true;
		}
		return super.processInitialInteract(pl, h);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		ItemStackHelper.loadAllItems(nbt, con);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		ItemStackHelper.saveAllItems(nbt, con);
	}

	@Override
	protected void sendMsg(EntityPlayer pl) {
		JSTUtils.sendStatTrsl(pl, true, "jst.tooltip.energy.eu", getEnergy(), MAX_EU);
	}

    @Override
	public boolean hasCapability(Capability<?> c, EnumFacing f) {
    	if (c == CapabilityEnergy.ENERGY) return true;
    	return super.hasCapability(c, f);
	}

    @Override
	public <T> T getCapability(Capability<T> c, EnumFacing f) {
    	if (c == CapabilityEnergy.ENERGY)
    		return (T) this;
    	return super.getCapability(c, f);
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack s : con)
			if (!s.isEmpty())
				return false;
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return con.get(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int c) {
		return ItemStackHelper.getAndSplit(con, i, c);
	}

	@Override
	public ItemStack removeStackFromSlot(int i) {
		ItemStack s = con.get(i);
		if (s.isEmpty()) return ItemStack.EMPTY;
		con.set(i, ItemStack.EMPTY);
		return s;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack st) {
	    con.set(i, st);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {}

	@Override
	public boolean isUsableByPlayer(EntityPlayer pl) {
	    if (isDead) return false;
	    return pl.getDistanceSq(this) <= 64.0D;
	}

	@Override
	public void openInventory(EntityPlayer pl) {}

	@Override
	public void closeInventory(EntityPlayer pl) {}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack st) {
		return JSTUtils.dischargeItem(st, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) > 0;
	}

	@Override
	public int getField(int i) {
		return 0;
	}

	@Override
	public void setField(int i, int v) {}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		con.clear();
	}

	@Override
	public Object getServerGUI(int id, EntityPlayer pl, World w) {
		return new ContainerCarElec(pl.inventory, this);
	}

	@Override
	public Object getClientGUI(int id, EntityPlayer pl, World w) {
		return new GUIBasic((Container)getServerGUI(id, pl, w), new ResourceLocation(JustServerTweak.MODID, "textures/gui/1by1.png"));
	}

	@Override
	public int receiveEnergy(int a, boolean s) {
	    int eu2 = getEnergy();
	    int ret = Math.min(MAX_EU - eu2, Math.min(512, a / JSTCfg.RFPerEU));
	    if (!s) {
	    	eu2 += ret;
	    	setEnergy(eu2);
	    }
		return ret * JSTCfg.RFPerEU;
	}

	@Override
	public int extractEnergy(int a, boolean s) {return 0;}

	@Override
	public int getEnergyStored() {return getEnergy() * JSTCfg.RFPerEU;}

	@Override
	public int getMaxEnergyStored() {return getEnergy() * JSTCfg.RFPerEU;}

	@Override
	public boolean canExtract() {return false;}

	@Override
	public boolean canReceive() {return true;}
}
