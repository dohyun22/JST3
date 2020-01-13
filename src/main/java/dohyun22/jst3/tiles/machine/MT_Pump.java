package dohyun22.jst3.tiles.machine;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.BatterySlot;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.container.JSTSlot;
import dohyun22.jst3.tiles.MTETank;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_Pump extends MT_Machine {
	private FluidTank tank;
	private int wX, wY, wZ;
	private boolean find;
	private short[] cfg = new short[6];
	private Fluid current;

	public MT_Pump(int tier) {
		super(tier);
		batterySlot = 3;
	}

	@Override
	protected boolean checkCanWork() {
		if (tank.getFluidAmount() + 1000 <= tank.getCapacity()) {
			BlockPos p = getPos();
			if (!find) {
				MutableBlockPos p2 = new MutableBlockPos(p);
				p2.move(EnumFacing.DOWN);
				int dist = 0;
				while (p2.getY() >= 0) {
					IFluidHandler fh = findFluid(p2);
					if (fh != null) {
						if (fh != tank) {
							int r = -getRadius();
							wX = r;
							wY = p2.getY() - p.getY();
							wZ = r;
							FluidStack fs = fh.drain(1000, false);
							if (fs != null) {
								find = true;
								current = fs.getFluid();
								wX--;
								updatePos();
							}
							break;
						}
					} else {
						break;
					}
					p2.move(EnumFacing.DOWN);
					if (dist++ > 32) break;
				}
			}
			if (find) {
				int r = getRadius();
				wX = MathHelper.clamp(wX, -r, r);
				wY = Math.min(wY, -1);
				wZ = MathHelper.clamp(wZ, -r, r);
				energyUse = 4;
				mxprogress = 40;
				return true;
			}
		} else {
			sendFluid();
		}
		return false;
	}

	@Override
	protected void finishWork() {
		if (tank.getFluid() == null || tank.getFluid().amount + 1000 <= tank.getCapacity()) {
			BlockPos p = getPos().add(wX, wY, wZ);
			IFluidHandler fh = findFluid(p);
			if (fh != null) {
				if (fh != tank) {
					FluidStack fs = fh.drain(1000, false);
					if (fs != null) {
						if (current == null) current = fs.getFluid();
						if (fs.getFluid() == current && tank.fillInternal(fs, true) > 0) {
							current = fs.getFluid();
							getWorld().setBlockState(p, Blocks.AIR.getDefaultState(), 2);
							sendFluid();
						}
					}
				}
			}
			updatePos();
		}
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		MT_Pump r = new MT_Pump(tier);
		r.tank = new MTETank(12000 + tier * 4000, true, false, r, 2);
		return r;
	}
	
	@Override
	public int getInvSize() {
		return 4;
	}

	@Override
	public void onPostTick() {
		super.onPostTick();
		if (getWorld().isRemote) return;
		ItemStack st = inv.get(0);
		if (!st.isEmpty() && FluidUtil.getFluidHandler(st) != null)
			JSTUtils.fillFluidItemInv(tank, 1000, baseTile, 0, 1);
	}

	@Override
	public <T> T getCapability(Capability<T> c, @Nullable EnumFacing f) {
		if (f != EnumFacing.DOWN && c == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return (T) tank;
		return null;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		tank.readFromNBT(tag);
		wX = tag.getShort("wX");
		wY = tag.getShort("wY");
		wZ = tag.getShort("wZ");
		find = tag.getBoolean("find");
		current = FluidRegistry.getFluid(tag.getString("cf"));
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tank.writeToNBT(tag);
		tag.setShort("wX", (short)wX);
		tag.setShort("wY", (short)wY);
		tag.setShort("wZ", (short)wZ);
		tag.setBoolean("find", find);
		if (current != null) tag.setString("cf", current.getName());
	}

	@Override
	protected void addSlot(ContainerGeneric cg, InventoryPlayer inv, TileEntityMeta te) {
		cg.addSlot(new Slot(te, 0, 128, 17));
		cg.addSlot(JSTSlot.out(te, 1, 128, 52));
		cg.addSlot(JSTSlot.fl(te, 2, 100, 35));
		cg.addSlot(new BatterySlot(te, 3, 8, 53, false, true));
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void addComp(GUIGeneric gg) {
		gg.addSlot(128, 17, 0);
		gg.addSlot(128, 52, 0);
		gg.addSlot(100, 35, 3);
		gg.addSlot(8, 53, 2);
		gg.addPrg(56, 35);
		gg.addPwr(12, 31);
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack st, EnumFacing f, float hX, float hY, float hZ) {
		if (baseTile != null && !isClient())
			pl.openGui(JustServerTweak.INSTANCE, 1, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
		return true;
	}

	@Override
	public boolean canSlotDrop(int num) {
		return num != 2;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite ws = getTETex("pump");
		return new TextureAtlasSprite[] {getTETex("vent"), getTieredTex(tier), ws, ws, ws, ws};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.pump", getRadius()));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getModelKey() {
		return "jst_pump" + tier;
	}

	private int getRadius() {
		return tier * 10;
	}

	private IFluidHandler findFluid(BlockPos p) {
		Block b = getWorld().getBlockState(p).getBlock();
		if (b instanceof BlockFluidBase || b instanceof BlockLiquid) {
			IFluidHandler r = FluidUtil.getFluidHandler(getWorld(), p, EnumFacing.UP);
			if (r != null) {
				FluidStack fs = r.drain(1000, false), fs2 = tank.getFluid();
				if (fs != null && (fs2 == null || fs.isFluidEqual(fs2))) return r;
			}
		} else if (getWorld().isAirBlock(p)) {
			return tank;
		}
		return null;
	}

	private void sendFluid() {
		for (EnumFacing f : EnumFacing.VALUES) {
			if (f == EnumFacing.DOWN) continue;
			int amt = JSTUtils.fillTank(getWorld(), getPos(), f, tank.getFluid());
			if (amt > 0) tank.drainInternal(amt, true);
		}
	}

	private void updatePos() {
		int r = getRadius();
		BlockPos p = getPos();
		while (true) {
			wX++;
			if (wX > r) {
				wX = -r;
				wZ++;
			}
			if (wZ > r) {
				wZ = -r;
				wY--;
				find = false;
				break;
			}
			IFluidHandler fh = findFluid(new BlockPos(p.getX() + wX, p.getY() + wY, p.getZ() + wZ));
			if (fh != null && fh != tank) {
				FluidStack fs = fh.drain(1000, false);
				if (fs != null && (current == null || fs.getFluid() == current))
					break;
			}
		}
	}
}
