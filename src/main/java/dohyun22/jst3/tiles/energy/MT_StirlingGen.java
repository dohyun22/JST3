package dohyun22.jst3.tiles.energy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.BatterySlot;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.container.JSTSlot;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.tiles.MTETank;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MultiTankHandler;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.interfaces.IGenericGUIMTE;
import dohyun22.jst3.utils.FluidItemPredicate;
import dohyun22.jst3.utils.JSTChunkData;
import dohyun22.jst3.utils.JSTFluids;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_StirlingGen extends MetaTileGenerator implements IGenericGUIMTE {
	private double fuelLeft;
	private int fuelValue, /* 20¥ìB */water;
	private MultiTankHandler tank;
	private boolean steam, watergen;
	
	public MT_StirlingGen(int tier) {
		super(tier, true);
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_StirlingGen(tier);
	}

	@Override
	protected void checkCanGenerate() {
		if (baseTile.setActive((long)fuelLeft > 0))
			updateLight();
	}

	@Override
	protected void doGenerate() {
		double use = Math.min(fuelLeft, JSTUtils.getVoltFromTier(tier) / 2.5D);
		if (tank != null) {
			FluidTank t = tank.getTank(0);
			if (t.getFluidAmount() > 0) {
				use = tank.getTank(1).fillInternal(new FluidStack(JSTFluids.steam, (int)(use * 2.5D)), true);
				use /= 12.5D;
				if (use > 0) {
					water += use;
					int amt = water / 50;
					if (amt > 0) {
						tank.getTank(0).drainInternal(amt, true);
						water -= amt * 50;
					}
				}
			} else
				use = 0.0D;
		} else {
			baseTile.energy += use * 2.5D;
		}
		fuelLeft -= use;
	}
	
	@Override
	public int getInvSize() {
		return 6;
	}
	
	@Override
	public int maxEUTransfer() {
		return JSTUtils.getVoltFromTier(tier);
	}

	@Override
	public void onPreTick() {
		if (isClient()) return;
		ItemStack in = inv.get(0);
		if ((long)fuelLeft <= 0) {
			int fv = getFuelValue(in);
			if (fv > 0) {
				fuelLeft = fv;
				fuelValue = fv;
				Item it = in.getItem();
				in.shrink(1);
				if (in.isEmpty()) {
					ItemStack st2 = it.getContainerItem(in);
					inv.set(0, st2);
				}
			}
		}
	    in = inv.get(1);
	    if (!in.isEmpty() && baseTile.energy > 0L)
	    	baseTile.energy -= JSTUtils.chargeItem(in, Math.min(baseTile.energy, maxEUTransfer()), tier, false, false);
	    if (tank != null) {
	    	if (watergen && tank.getTank(0).getCapacity() > tank.getTank(0).getFluidAmount())
	    		tank.getTank(0).fillInternal(new FluidStack(FluidRegistry.WATER, JSTUtils.getVoltFromTier(tier)), true);
			if (tank.getTank(1).getFluid() != null) {
				int amt = JSTUtils.fillTank(getWorld(), getPos(), EnumFacing.UP, new FluidStack(tank.getTank(1).getFluid(), tank.getTank(1).getFluidAmount()));
				if (amt > 0) tank.getTank(1).drain(amt, true);
			}
	    	if (baseTile.getTimer() % 10 == 0) {
				FluidStack fs = FluidUtil.getFluidContained(inv.get(2));
				if (fs != null && fs.getFluid() == FluidRegistry.WATER)
					JSTUtils.drainFluidItemInv(tank.getTank(0), 1000, baseTile, 2, 3);
				int cap = tank.getTank(1).getCapacity();
				if (tank.getTank(1).getFluidAmount() >= cap - 100) {
					tank.getTank(1).drainInternal(cap / 4, true);
					sendEvent(90, 0);
				}
	    	}
	    }
	}

	@Override
	public boolean receiveClientEvent(int id, int arg) {
		if (id == 90) {
			if (isClient()) {
				getWorld().playSound(getPos().getX() + 0.5D, getPos().getY(), getPos().getZ() + 0.5D, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.8F, 0.75F, false);
				for (int l = 0; l < 12; l++)
					getWorld().spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, getPos().getX() - 0.2D + getWorld().rand.nextFloat() * 1.4D, getPos().getY(), getPos().getZ() - 0.2D + getWorld().rand.nextFloat() * 1.4D, 0.0D, 0.0D, 0.0D);
			}
			return true;
		}
		return super.receiveClientEvent(id, arg);
	}

	@Override
	public void onPostTick() {
		super.onPostTick();
		
		if (isClient() && baseTile.isActive() && getWorld().rand.nextInt(8) == 0) {
            EnumFacing ef = baseTile.facing;
            World w = baseTile.getWorld();
            Random rd = w.rand;
            
            double x = (double)baseTile.getPos().getX() + 0.5D;
			double y = (double)baseTile.getPos().getY() + rd.nextDouble() * 6.0D / 16.0D;
            double z = (double)baseTile.getPos().getZ() + 0.5D;
            double o = rd.nextDouble() * 0.6D - 0.3D;

            if (rd.nextInt(6) <= tier)
            	w.playSound(getPos().getX() + 0.5D, getPos().getY(), getPos().getZ() + 0.5D, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);

            switch (ef) {
                case WEST:
                    w.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x - 0.52D, y, z + o, 0.0D, 0.0D, 0.0D, new int[0]);
                    w.spawnParticle(EnumParticleTypes.FLAME, x - 0.52D, y, z + o, 0.0D, 0.0D, 0.0D, new int[0]);
                    break;
                case EAST:
                    w.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + 0.52D, y, z + o, 0.0D, 0.0D, 0.0D, new int[0]);
                    w.spawnParticle(EnumParticleTypes.FLAME, x + 0.52D, y, z + o, 0.0D, 0.0D, 0.0D, new int[0]);
                    break;
                case NORTH:
                    w.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + o, y, z - 0.52D, 0.0D, 0.0D, 0.0D, new int[0]);
                    w.spawnParticle(EnumParticleTypes.FLAME, x + o, y, z - 0.52D, 0.0D, 0.0D, 0.0D, new int[0]);
                    break;
                case SOUTH:
                    w.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + o, y, z + 0.52D, 0.0D, 0.0D, 0.0D, new int[0]);
                    w.spawnParticle(EnumParticleTypes.FLAME, x + o, y, z + 0.52D, 0.0D, 0.0D, 0.0D, new int[0]);
                    break;
                default:
            }
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		fuelLeft = tag.getDouble("fuel");
		fuelValue = tag.getInteger("fuelval");
		if (tag.hasKey("tank")) {
			createTank();
			tank.readFromNBT(tag.getCompoundTag("tank"));
			water = tag.getInteger("water");
			watergen = tag.getBoolean("wgen");
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setDouble("fuel", fuelLeft);
		tag.setInteger("fuelval", fuelValue);
		if (tank != null) {
			NBTTagCompound t2 = new NBTTagCompound();
			tank.writeToNBT(t2);
			tag.setTag("tank", t2);
			tag.setInteger("water", water);
			if (watergen) tag.setBoolean("wgen", true);
		}
	}

	@Override
	public void readSyncableDataFromNBT(NBTTagCompound tag) {
		steam = tag.getBoolean("steam");
	}

	@Override
	public void writeSyncableDataToNBT(NBTTagCompound tag) {
		tag.setBoolean("steam", steam);
	}

	@Override
	public <T> T getCapability(Capability<T> c, @Nullable EnumFacing f) {
		if (c == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && tank != null)
			return (T) tank;
		return super.getCapability(c, f);
	}

	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		if (baseTile != null) baseTile.facing = JSTUtils.getClosestSide(p, elb, true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTieredTex(tier);
		return new TextureAtlasSprite[] {t, t, t, t, t, getTETex("furnacegen")};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++) {
			if (baseTile.facing == JSTUtils.getFacingFromNum(n))
				ret[n] = getTETex("furnacegen" + (baseTile.isActive() ? "" : "_off"));
			else
				ret[n] = getTieredTex(tier);
		}
		if (steam) ret[1] = getTETex("fl_out");
		return ret;
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack st, EnumFacing f, float hitX, float hitY, float hitZ) {
		if (!isClient()) pl.openGui(JustServerTweak.INSTANCE, 1, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
		return true;
	}

	@Override
	public boolean canProvideEnergy() {
		return true;
	}

	@Override
	public boolean isEnergyOutput(EnumFacing side) {
		return true;
	}

	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		ContainerGeneric r = new ContainerGeneric(te);
		r.addSlot(new SlotFurnaceFuel(te, 0, 44, 56));
		r.addSlot(steam ? new JSTSlot(te, 1, 44, 12, false, true, 64, true) : new BatterySlot(te, 1, 44, 12, true, false));
		if (steam) {
			r.addSlot(new JSTSlot(te, 2, 71, 12).setPredicate(new FluidItemPredicate("water")));
			r.addSlot(new JSTSlot(te, 3, 71, 56, false, true, 64, true));
			r.addSlot(new JSTSlot(te, 4, 71, 34, false, false, 64, false));
			r.addSlot(new JSTSlot(te, 5, 98, 34, false, false, 64, false));
		}
		r.addPlayerSlots(inv);
		return r;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		GUIGeneric r = new GUIGeneric((ContainerGeneric) getServerGUI(id, inv, te));
		r.addSlot(44, 56, 9);
		r.addSlot(44, 12, steam ? 1 : 2);
		if (steam) {
			r.addSlot(71, 12, 6);
			r.addSlot(71, 56, 7);
			r.addSlot(71, 34, 3);
			r.addSlot(98, 34, 3);
		} else
			r.addPwr2(80, 32);
		r.addFuel(44, 35);
		r.addJEI(7, 33, JustServerTweak.MODID + ".fgenfuel");
		return r;
	}

	@Override
	public boolean canInsertItem(int sl, ItemStack st, EnumFacing dir) {
		return sl == 0;
	}

	@Override
	public boolean canExtractItem(int sl, ItemStack st, EnumFacing dir) {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int sl, ItemStack st) {
		return sl == 0 && getFuelValue(st) > 0;
	}

	@Override
	public boolean canSlotDrop(int sl) {
		return sl < 4;
	}

	@Override
	public int getLightValue() {
		return baseTile.isActive() ? 14 : 0;
	}

	@Override
	public int getDust() {
		return 15 * JSTUtils.getVoltFromTier(tier) / 32;
	}

	public static int getFuelValue(ItemStack in) {
		if (in == null || in.isEmpty()) return 0;
		FluidStack fs = FluidUtil.getFluidContained(in);
		if (fs != null && fs.getFluid() == FluidRegistry.LAVA) return 0;
		return TileEntityFurnace.getItemBurnTime(in);
	}

	@Override
	public int getFuel() {
		return (int)fuelLeft;
	}

	@Override
	public int getMxFuel() {
		return fuelValue;
	}

	@Override
	public boolean tryUpgrade(String id) {
		if (tank == null) {
			if (tier < 3 && id.equals("jst_boiler")) {
				createTank();
				steam = true;
				baseTile.issueUpdate();
				return true;
			}
		} else if (!watergen && id.equals("jst_water")) {
			watergen = true;
			return true;
		}
		return false;
	}

	@Override
	@Nonnull
	public void getDrops(ArrayList<ItemStack> ls) {
		super.getDrops(ls);
		if (tank != null) ls.add(new ItemStack(JSTItems.item1, 1, 13000));
		if (watergen) ls.add(new ItemStack(JSTItems.item1, 1, 13002));
	}

	private void createTank() {
		if (tank == null) tank = new MultiTankHandler(new MTETank(16000 + (tier - 1) * 8000, false, true, this, 4, false, "water"), new MTETank(16000 + (tier - 1) * 8000, true, false, this, 5, false, "steam"));
	}
}
