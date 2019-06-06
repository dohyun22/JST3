package dohyun22.jst3.entity;

import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.tiles.energy.MetaTileFluidGen;
import dohyun22.jst3.utils.JSTChunkData;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class EntityCarDiesel extends EntityCar {
	private static final ResourceLocation TEX = new ResourceLocation("jst3:textures/entity/car_f.png");
	private FluidTank tank = new MetaTileFluidGen.FuelTank(4000, null, -1, 0);

	public EntityCarDiesel(World w) {
		super(w);
	}

	@Override
	protected ItemStack getDrop() {
		return new ItemStack(JSTItems.item1, 1, 10042);
	}

	@Override
	public ResourceLocation getTex() {
		return TEX;
	}

	@Override
	protected int getUsage() {
		return 20;
	}

	@Override
	protected int getNewEnergy() {
		FluidStack fs = tank.getFluid();
		if (fs == null) return 0;
		Integer ret = MRecipes.DieselGenFuel.get(JSTUtils.getRegName(fs));
		if (ret == null) return 0;
		int amt = Math.min(10, fs.amount);
		tank.drainInternal(amt, true);
		return ret.intValue() * amt;
	}

	@Override
	protected void onRunning() {
		if (!world.isRemote && ticksExisted % 20 == 0) JSTChunkData.addFineDust(world, new ChunkPos(getPosition()), 20, true);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		tank.readFromNBT(nbt);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		tank.writeToNBT(nbt);
	}

	@Override
	public boolean processInitialInteract(EntityPlayer pl, EnumHand h) {
		ItemStack st = pl.getHeldItem(h);
		if (!pl.world.isRemote && FluidUtil.interactWithFluidHandler(pl, h, tank))
			return true;
		return super.processInitialInteract(pl, h);
	}

	@Override
	protected void sendMsg(EntityPlayer pl) {
		JSTUtils.sendStatTrsl(pl, true, "jst.msg.com.tanksimple", tank.getFluidAmount(), tank.getCapacity());
	}

    @Override
	public boolean hasCapability(Capability<?> c, EnumFacing f) {
    	if (c == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) return true;
    	return super.hasCapability(c, f);
	}
   
    @Override
	public <T> T getCapability(Capability<T> c, EnumFacing f) {
    	if (c == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) return (T) tank;
    	return super.getCapability(c, f);
	}
}
