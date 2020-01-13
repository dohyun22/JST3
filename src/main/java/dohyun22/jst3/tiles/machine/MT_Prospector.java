package dohyun22.jst3.tiles.machine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.BatterySlot;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.container.JSTSlot;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.network.JSTPacketHandler;
import dohyun22.jst3.tiles.MTETank;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTChunkData;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_Prospector extends MT_Machine {
	private boolean activated, prospected, empty;
	private byte hlt;
	private MTETank tank;

	public MT_Prospector() {
		super(1);
		batterySlot = 0;
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		MT_Prospector r = new MT_Prospector();
		r.tank = new MTETank(16000, true, false, r, 4);
		return r;
	}

	@Override
	public int getInvSize() {
		return 5;
	}

	@Override
	protected boolean checkCanWork() {
		if (activated) {
			if (prospected) {
				if (!empty && tank.getFluidAmount() + 1000 <= tank.getCapacity()) {
					energyUse = 12;
					mxprogress = 200;
					return true;
				}
			} else {
				energyUse = 8;
				mxprogress = 200;
				return true;
			}
		}
		return false;
	}

	@Override
	protected void finishWork() {
		World w = getWorld(); ChunkPos c = new ChunkPos(getPos());
		FluidStack[] fs = JSTChunkData.getOrCreateFluid(w, c, !prospected);
		if (prospected) {
			if (fs != null && fs[0] != null) {
				int amt = tank.fillInternal(new FluidStack(fs[0].getFluid(), Math.min(fs[0].amount, 1000)), true);
				if (amt > 0) {
					fs[0].amount -= amt;
					if (fs[0].amount <= 0) {
						fs[0] = null;
						empty = true;
					}
					JSTChunkData.putFluidData(w, c, fs[0], false);
				}
			}
		} else {
			if (fs == null || fs[0] == null)
				empty = true;
			else
				getWorld().playSound(null, getPos(), JSTSounds.SCAN, SoundCategory.BLOCKS, 3.0F, 1.0F);
			prospected = true;
			activated = false;
		}
		updateFluidSlot(fs);
	}

	private void updateFluidSlot(FluidStack[] f) {
		ItemStack st = inv.get(3);
		if (f == null && !st.isEmpty())
			setInventorySlotContents(3, ItemStack.EMPTY);
		if (f != null) {
			if (st.isEmpty()) {
				st = new ItemStack(JSTItems.item1, 1, 9999);
				setInventorySlotContents(3, st);
			}
			NBTTagCompound t = new NBTTagCompound();
			t.setString("Info", f[0] == null ? "jst.msg.com.nofluid" : "jst.msg.prospect");
			st.setTagCompound(f[0] == null ? t : f[0].writeToNBT(t));
		}
	}

	@Override
	public void onPostTick() {
		super.onPostTick();
		if (isClient()) {
			if (baseTile.isActive()) {
				World w = getWorld();
				if (w.rand.nextInt(8) == 0) {
					BlockPos p = getPos();
					for (int i = 0; i < 5; i++) {
						double x = p.getX() + 0.5D + w.rand.nextFloat() * 0.6D - 0.3D;
						double y = p.getY() + 1 + w.rand.nextFloat() * 0.2D;
						double z = p.getZ() + 0.5D + w.rand.nextFloat() * 0.6D - 0.3D;
						w.spawnParticle(w.rand.nextBoolean() ? EnumParticleTypes.SMOKE_LARGE : EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0D, 0.0D, 0.0D);
					}
				}
			}
		} else {
			if (hlt > 0) {
				hlt--;
				if (hlt % 5 == 0) JSTPacketHandler.playCustomEffect(getWorld(), getPos(), 3, 0);
			}
			if (prospected && !empty && baseTile.getTimer() % 100 == 0)
				updateFluidSlot(JSTChunkData.getOrCreateFluid(getWorld(), new ChunkPos(getPos()), false));
			if (tank.getFluid() != null) {
				ItemStack st = inv.get(1);
				if (!st.isEmpty() && FluidUtil.getFluidHandler(st) != null)
					JSTUtils.fillFluidItemInv(tank, 1000, baseTile, 1, 2);
				for (EnumFacing f : EnumFacing.HORIZONTALS) {
					int amt = JSTUtils.fillTank(getWorld(), getPos(), f, tank.getFluid());
					if (amt > 0) tank.drainInternal(amt, true);
				}
			}
		}
	}

	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		if (baseTile == null) return;
		super.onPlaced(p, bs, elb, st);
		baseTile.energy = st.hasTagCompound() ? st.getTagCompound().getLong("Energy") : 0L;
		hlt = 100;
	}

	@Override
	public void getDrops(ArrayList<ItemStack> ls) {
		if (baseTile == null) return;
	    ItemStack st = new ItemStack(JSTBlocks.blockTile, 1, baseTile.getID());
	    if (!isClient() && baseTile.energy > 0) {
	    	NBTTagCompound t = new NBTTagCompound();
	    	t.setLong("Energy", baseTile.energy);
	    	st.setTagCompound(t);
	    }
	    ls.add(st);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTieredTex(1), t2 = getTETex("fl_out");
		return new TextureAtlasSprite[] {t, getTETex("vent"), t2, t2, t2, t2};
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		activated = tag.getBoolean("clicked");
		prospected = tag.getBoolean("prospect");
		empty = tag.getBoolean("empty");
		tank.readFromNBT(tag);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setBoolean("clicked", activated);
		tag.setBoolean("prospect", prospected);
		tag.setBoolean("empty", empty);
		tank.writeToNBT(tag);
	}

	@Override
	public long getMaxEnergy() {
		return 25000;
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack st, EnumFacing f, float hitX, float hitY, float hitZ) {
		if (!isClient())
			pl.openGui(JustServerTweak.INSTANCE, 1, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
		return true;
	}

	@Override
	public boolean canInsertItem(int sl, ItemStack st, EnumFacing dir) {
		return sl == 1;
	}

	@Override
	public boolean canExtractItem(int sl, ItemStack st, EnumFacing dir) {
		return sl == 2;
	}

	@Override
	public boolean canSlotDrop(int sl) {
		return sl < 3;
	}

	@Override
	protected void addSlot(ContainerGeneric cg, InventoryPlayer inv, TileEntityMeta te) {
		cg.addSlot(new BatterySlot(te, 0, 17, 35, false, true));
		cg.addSlot(new Slot(te, 1, 143, 13));
		cg.addSlot(new JSTSlot(te, 2, 143, 57, false, true, 64, true));
		cg.addSlot(new JSTSlot(te, 3, 80, 13, false, false, 64, false));
		cg.addSlot(new JSTSlot(te, 4, 143, 35, false, false, 64, false));
		cg.addPlayerSlots(inv);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void addComp(GUIGeneric gg) {
		gg.addSlot(17, 35, 2);
		gg.addSlot(143, 13, 6);
		gg.addSlot(143, 57, 7);
		gg.addSlot(80, 13, 3);
		gg.addSlot(143, 35, 3);
		gg.addPwr2(9, 13);
		gg.addButton(52, 35, 72, 20, 0, "jst.msg.com.startstop", true);
		gg.addButton(52, 55, 72, 20, 1, "jst.msg.com.highlight", true);
		gg.addJEI(16, 56, JustServerTweak.MODID + ".fluidresource");
	}

	@Override
	protected void onStartWork() {
		getWorld().playSound(null, getPos(), prospected ? SoundEvents.ENTITY_GENERIC_SWIM : SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0F, 2.0F);
	}

	@Override
	public <T> T getCapability(Capability<T> c, @Nullable EnumFacing f) {
		if (c == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && tank != null)
			return (T) tank;
		return super.getCapability(c, f);
	}

	@Override
	public void getSubBlocks(int id, NonNullList<ItemStack> list) {
		ItemStack st = new ItemStack(JSTBlocks.blockTile, 1, id);
		list.add(st);
		st = st.copy();
		NBTTagCompound nbt = new NBTTagCompound();
    	nbt.setLong("Energy", getMaxEnergy());
    	st.setTagCompound(nbt);
		list.add(st);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.prospect"));
		long e = st.hasTagCompound() ? st.getTagCompound().getLong("Energy") : 0, m = getMaxEnergy();
		ls.add(I18n.format("jst.tooltip.energy.eu", e, m));
		ls.add(I18n.format("jst.tooltip.energy.rf", e * 4, m * 4));
	}

	@Override
	public void handleBtn(int id, EntityPlayer pl) {
		if (id == 0)
			activated = empty ? false : prospected ? !activated : true;
		else
			hlt = 100;
	}

	@Override
	public boolean showDurability(ItemStack st) {
		return st.getCount() == 1;
	}

	@Override
	public double getDurability(ItemStack st) {
		if (!st.hasTagCompound()) return 1.0D;
		return 1.0D - (double)st.getTagCompound().getLong("Energy") / (double)getMaxEnergy();
	}
}
