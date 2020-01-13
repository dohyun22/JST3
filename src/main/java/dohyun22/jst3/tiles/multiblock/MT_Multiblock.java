package dohyun22.jst3.tiles.multiblock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dohyun22.jst3.api.FineDustCapability;
import dohyun22.jst3.api.IDust;
import dohyun22.jst3.api.recipe.RecipeContainer;
import dohyun22.jst3.api.recipe.RecipeList;
import dohyun22.jst3.recipes.ItemList;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileEnergyInput;
import dohyun22.jst3.tiles.interfaces.IMultiBlockPart;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public abstract class MT_Multiblock extends MetaTileEnergyInput implements IDust {
	protected List<BlockPos> energyInput = new ArrayList(), energyOutput = new ArrayList(), itemInput = new ArrayList(), itemOutput = new ArrayList(), fluidInput = new ArrayList(), fluidOutput = new ArrayList();
	protected boolean structureValid;
	protected byte timer, timer2 = 50;
	protected ItemStack[] itemOut;
	protected FluidStack[] fluidOut;
	protected int energyUse, progress, mxprogress;
	protected byte cooldown, circuitTier = -1;
	private ItemStack upgradeCircuit;
	
	@Override
	public void onStructureUpdate() {
		setUpdateTimer();
	}
	
	public void setUpdateTimer() {
		timer = 5;
	}

	protected abstract boolean checkStructure();

	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		super.onPlaced(p, bs, elb, st);
		setUpdateTimer();
	}

	@Override
	public void onPostTick() {
		super.onPostTick();
		if (isClient()) {
			updateClient();
			return;
		}
		if (isInCooldown()) cooldown--;
		if (timer >= 0) timer--; if (timer2 >= 0) timer2--;
		if (timer == 0 ||timer2 == 0) {
			if (timer <= 0) {
				clearPorts();
				structureValid = checkStructure();
			}
		}
		if (timer2 < 0) {
			if (structureValid)
				update();
			else if (baseTile.isActive())
				stopWorking(true);
		}
	}

	protected void update() {
		if (baseTile.isActive()) {
			doWork();
		} else if (baseTile.getTimer() % getCheckTimer() == 0L && (!needEnergy() || getUsableEnergy() > 0L) && !isInCooldown() && checkCanWork()) {
			markDirty();
			onStartWork();
			baseTile.setActive(true);
		}
	}

	protected int getCheckTimer() {
		return 40;
	}

	protected void updateClient() {}

	protected abstract boolean checkCanWork();

	protected void finishWork() {
	    itemOut = null;
	    fluidOut = null;
	}

	protected boolean isInCooldown() {
		return this.cooldown > 0;
	}

	protected void onStartWork() {}
	
	protected void doWork() {
		if (mxprogress > 0) {
			if (cooldown > 0)
				return;
			int u = getEnergyUse();
			if (getUsableEnergy() >= u) {
				consumeEnergy(u);
				if (getUsableEnergy() < u) {
					interrupt(80);
					return;
				}
				progress += Math.max(1, getSpeed());
				if (progress >= mxprogress) {
					finishWork();
					energyUse = 0;
					progress = 0;
					mxprogress = 0;
					if (!checkCanWork()) {
						baseTile.setActive(false);
					} else {
						markDirty();
						onStartWork();
					}
				}
			}
		} else {
			stopWorking(true);
		}
	}
	
	protected long getUsableEnergy() {
		if (isPortPowered()) {
			long ret = 0;
			for (MT_EnergyPort mep : getEnergyPorts(false))
				ret += mep.baseTile.energy;
			return ret;
		}
		return this.baseTile.energy;
	}
	
	protected void consumeEnergy(long amt) {
		if (this.isPortPowered()) {
			for (MT_EnergyPort mep : getEnergyPorts(false)) {
				long l = Math.min(amt, mep.baseTile.energy);
				mep.baseTile.energy -= l;
				amt -= l;
				if (amt == 0) break;
			}
			return;
		}
		this.baseTile.energy -= amt;
	}
	
	protected int getEnergyUse() {
		return energyUse;
	}
	
	protected int getSpeed() {
		return 1;
	}

	protected void stopWorking(boolean interrupt) {
		if (interrupt)
			interrupt(80);
		baseTile.setActive(false);
	    energyUse = 0;
	    progress = 0;
	    mxprogress = 0;
	}
	
	protected void interrupt(int cd) {
		this.cooldown = ((byte) cd);
		getWorld().playSound((EntityPlayer) null, getPos(), JSTSounds.INTERRUPT, SoundCategory.BLOCKS, 1.0F, getWorld().rand.nextFloat() * 0.4F + 0.8F);
	}

	protected void clearPorts() {
		energyInput.clear();
		energyOutput.clear();
		itemInput.clear();
		itemOutput.clear();
		fluidInput.clear();
		fluidOutput.clear();
	}
	
	protected boolean isPortPowered() {
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		setUpdateTimer();
		structureValid = tag.getBoolean("sv");
		NBTTagList t = tag.getTagList("itemOut", 10);
		if (!t.hasNoTags()) {
			this.itemOut = new ItemStack[t.tagCount()];
	        for (int n = 0; n < t.tagCount(); n++) {
	            NBTTagCompound tc = t.getCompoundTagAt(n);
	            int m = tc.getByte("Slot");
	            if (m >= 0 && m < t.tagCount())
	            	this.itemOut[m] = new ItemStack(tc);
	        }
		}
		t = tag.getTagList("fluidOut", 10);
		if (!t.hasNoTags()) {
			this.fluidOut = new FluidStack[t.tagCount()];
			for (int n = 0; n < t.tagCount(); n++) {
				NBTTagCompound tc = t.getCompoundTagAt(n);
				int m = tc.getByte("Slot");
				if (m >= 0 && m < t.tagCount())
					this.fluidOut[m] = FluidStack.loadFluidStackFromNBT(tc);
			}
		}
		energyUse = tag.getInteger("use");
		progress = tag.getInteger("prg");
		mxprogress = tag.getInteger("mxprg");
		cooldown = tag.getByte("cd");
		circuitTier = tag.getByte("tcirc");
		if (tag.hasKey("circ", Constants.NBT.TAG_COMPOUND))
			upgradeCircuit = new ItemStack(tag.getCompoundTag("circ"));
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setBoolean("sv", structureValid);
		if (itemOut != null && itemOut.length > 0) {
			NBTTagList t = new NBTTagList();
			for (int n = 0; n < itemOut.length; n++) {
				if (itemOut[n] != null && !itemOut[n].isEmpty()) {
					NBTTagCompound t2 = new NBTTagCompound();
	                t2.setInteger("Slot", n);
					itemOut[n].writeToNBT(t2);
					t.appendTag(t2);
				}
			}
			tag.setTag("itemOut", t);
		}
		if (fluidOut != null && fluidOut.length > 0) {
			NBTTagList t = new NBTTagList();
			for (int n = 0; n < fluidOut.length; n++) {
				if (fluidOut[n] != null) {
					NBTTagCompound t2 = new NBTTagCompound();
					t2.setInteger("Slot", n);
					fluidOut[n].writeToNBT(t2);
					t.appendTag(t2);
				}
			}
			tag.setTag("fluidOut", t);
		}
		tag.setInteger("use", energyUse);
		tag.setInteger("prg", progress);
		tag.setInteger("mxprg", mxprogress);
		tag.setByte("cd", cooldown);
		if (circuitTier >= 0) tag.setByte("tcirc", circuitTier);
		if (upgradeCircuit != null && !upgradeCircuit.isEmpty())
			tag.setTag("circ", upgradeCircuit.writeToNBT(new NBTTagCompound()));
	}

	@Override
	@Nonnull
	public void getDrops(ArrayList<ItemStack> ls) {
		super.getDrops(ls);
		if (upgradeCircuit != null && !upgradeCircuit.isEmpty())
			ls.add(upgradeCircuit);
	}

	protected boolean tryUpg(EntityPlayer pl, ItemStack st) {
		return tryUpg(pl, st, 3, 8, 4);
	}

	protected boolean tryUpg(EntityPlayer pl, ItemStack st, int minT, int maxT, int cnt) {
		minT = MathHelper.clamp(minT, 0, 9);
		maxT = MathHelper.clamp(maxT, minT, 9);
		cnt = MathHelper.clamp(cnt, 1, 64);
		if (!st.isEmpty()) {
			for (int n = minT; n <= maxT; n++) {
				if (n <= circuitTier) continue;
				Object obj = ItemList.circuits[n];
				boolean flag = obj instanceof ItemStack ? OreDictionary.itemMatches((ItemStack) obj, st, false) : obj instanceof String ? JSTUtils.oreMatches(st, (String) obj) : false;
				if (flag) {
					if (st.getCount() >= cnt) {
						if (upgradeCircuit != null && !upgradeCircuit.isEmpty())
							pl.addItemStackToInventory(upgradeCircuit.copy());
						circuitTier = (byte) n;
						upgradeCircuit = st.copy();
						upgradeCircuit.setCount(cnt);
						st.shrink(cnt);
						int v = JSTUtils.getVoltFromTier(getTier());
						updateEnergyPort(v, v * 8, false);
						getWorld().playSound(null, getPos(), JSTSounds.SWITCH, SoundCategory.BLOCKS, 0.75F, getWorld().rand.nextFloat() * 0.2F + 0.9F);
						pl.sendMessage(new TextComponentTranslation("jst.msg.multi.upgrade.suc", n));
					} else {
						pl.sendMessage(new TextComponentTranslation("jst.msg.multi.upgrade.err", cnt));
					}
					return true;
				}
			}
		}
		return false;
	}

	protected boolean checkRecipe(RecipeList rec) {
		ItemStack[] st = getAllInputSlots();
		FluidTank[] ft = getAllInputTanks();
		if (st.length <= 0) st = null;
		if (ft.length <= 0) ft = null;
		return startProcess(MRecipes.getRecipe(rec, st, ft, getTier(), true, true), st, ft);
	}

	protected boolean startProcess(RecipeContainer rc, ItemStack[] st, FluidTank[] ft) {
		if (rc != null) {
			if (getUsableEnergy() < rc.getEnergyPerTick()) return false;
			if (energyUse == 0 || mxprogress == 0) {
				energyUse = rc.getEnergyPerTick();
				mxprogress = rc.getDuration();
			} else if (energyUse != rc.getEnergyPerTick() || mxprogress != rc.getDuration()) {
				return false;
			}
			rc.process(st, ft, getTier(), true, true, true);
			itemOut = rc.getOutputItems();
			fluidOut = rc.getOutputFluids();
			return true;
		}
		return false;
	}

	protected void sendItem(ItemStack... out) {
		if (out != null)
			for (MT_ItemPort iop : getItemPorts(true)) {
				if (!JSTUtils.checkInventoryFull(iop.baseTile, null)) {
					for (int n = 0; n < out.length; n++) {
						if (out[n] != null && !out[n].isEmpty()) {
							ItemStack st = JSTUtils.sendStackToInv(iop.baseTile, out[n].copy(), null);
							if (st.isEmpty())
								iop.markDirty();
						}
					}
					break;
				}
			}
	}
	
	protected void sendItem() {
		sendItem(itemOut);
	}

	protected void sendFluid(FluidStack... out) {
		if (out != null) {
			boolean[] arr = new boolean[out.length];
			for (MT_FluidPort fop : getFluidPorts(true))
				for (int n = 0; n < out.length; n++)
					if (!arr[n] && fop.tank.fill(out[n], true) > 0) {
						arr[n] = true;
						break;
					}
		}
	}

	protected void sendFluid() {
		sendFluid(fluidOut);
	}
	
	@Override
	public boolean isMultiBlockPart() {
		return true;
	}
	
	public BlockPos getRelativePos(int x, int y, int z) {
		return getPosFromCoord(this.getPos(), x, y, z, this.getFacing());
	}
	
	/* west = -x
	 * east = +x
	 * north = -z
	 * south = +z
	 * up = +y
	 * down = -y
	 * */
	
	public boolean isComplete() {
		return structureValid;
	}
	
	protected int getTier() {
		int e = 0;
		for (MT_EnergyPort o : getEnergyPorts(false))
			e += o.maxEUTransfer();
		return JSTUtils.getTierFromVolt(e);
	}
	
	/** get BlockPos from relative coordinate and facing
	 * @param x X offset
	 * @param y X offset
	 * @param z X offset
	 * @param f Facing of the Controller.
	 *  */
	public static BlockPos getPosFromCoord(@Nonnull BlockPos start, int x, int y, int z, @Nullable EnumFacing f) {
		if (f == null) f = EnumFacing.DOWN;
		boolean reverse = false;
		if (f == EnumFacing.UP) {
			y *= -1;
		} else if (f == EnumFacing.NORTH) {
			x *= -1;
		} else if (f == EnumFacing.SOUTH) {
			z *= -1;
		} else if (f == EnumFacing.WEST) {
			reverse = true;
		} else if (f == EnumFacing.EAST) {
			reverse = true;
			x *= -1;
			z *= -1;
		}
		return reverse ? start.add(z, y, x) : start.add(x, y, z);
	}
	
	public byte getMode() {
		return (byte) (this.isComplete() ? this.baseTile.isActive() ? 2 : 1 : 0);
	}
	
	protected ItemStack[] getAllInputSlots() {
		ArrayList<ItemStack> ls = new ArrayList();
		for (MT_ItemPort mip : getItemPorts(false))
			ls.addAll(mip.inv);
		return ls.toArray(new ItemStack[0]);
	}

	protected FluidTank[] getAllInputTanks() {
		ArrayList<FluidTank> ls = new ArrayList();
		for (MT_FluidPort mip : getFluidPorts(false))
			ls.add(mip.tank);
		return ls.toArray(new FluidTank[0]);
	}
	
	public int getData() {
		return 0;
	}
	
	@SideOnly(Side.CLIENT)
	public void doDisplay(byte state, int data, FontRenderer fr) {
		fr.drawString(I18n.format(state == 1 ? "jst.msg.com.standby" : state == 2 ? "jst.msg.com.working" : "jst.msg.multi.invalid"), 14, 84, state == 0 ? 0xFF0000 : 0x00FF00);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		addInfo(st, ls);
		int d = getDust();
		if (d > 0) ls.add(I18n.format("jst.tooltip.tile.com.dust", FineDustCapability.toMicrogram(d * 60)));
	}

	@SideOnly(Side.CLIENT)
	protected void addInfo(ItemStack st, List<String> ls) {}

	protected boolean needEnergy() {
		return true;
	}

	@Override
	public boolean canGen() {
		return baseTile.isActive();
	}

	@Override
	public int getDust() {
		return 0;
	}

	@Override
	public <T> T getCapability(Capability<T> c, @Nullable EnumFacing f) {
		if (c == FineDustCapability.FINEDUST && getDust() > 0)
			return FineDustCapability.FINEDUST.cast(this);
		return null;
	}

	/** @param p Location of the Port
	 * @param mode 1=EnergyIN, 2=EnergyOUT, 4=ItemIN, 8=ItemOUT, 16=FluidIN, 32=FluidOUT flags can be added together (i.e using 12 to allow Item Input and Output) */
	protected boolean getAndAddPort(BlockPos p, int mode, @Nullable String tex) {
		if (p instanceof MutableBlockPos) p = ((MutableBlockPos)p).toImmutable();
		MetaTileBase mte = MetaTileBase.getMTE(getWorld(), p);
		if (mte instanceof IMultiBlockPart && tex != null)
			((IMultiBlockPart)mte).setTexture(tex);
		if (mte instanceof MT_EnergyPort) {
			if ((mode & 1) == 1 && !((MT_EnergyPort)mte).isOutput) {
				if (!energyInput.contains(p)) energyInput.add(p);
				return true;
			}
			if ((mode >> 1 & 1) == 1 && ((MT_EnergyPort)mte).isOutput) {
				if (!energyOutput.contains(p)) energyOutput.add(p);
				return true;
			}
		} else if (mte instanceof MT_ItemPort) {
			if ((mode >> 2 & 1) == 1 && !((MT_ItemPort)mte).isOutput) {
				if (!itemInput.contains(p)) itemInput.add(p);
				return true;
			}
			if ((mode >> 3 & 1) == 1 && ((MT_ItemPort)mte).isOutput) {
				if (!itemOutput.contains(p)) itemOutput.add(p);
				return true;
			}
		} else if (mte instanceof MT_FluidPort) {
			if ((mode >> 4 & 1) == 1 && !((MT_FluidPort)mte).isOutput) {
				if (!fluidInput.contains(p)) fluidInput.add(p);
				return true;
			}
			if ((mode >> 5 & 1) == 1 && ((MT_FluidPort)mte).isOutput) {
				if (!fluidOutput.contains(p)) fluidOutput.add(p);
				return true;
			}
		}
		return false;
	}
	
	protected void updateEnergyPort(int tr, int max, boolean output) {
		for (MT_EnergyPort port : getEnergyPorts(output)) {
			port.setMaxTransfer(tr);
			port.setMaxEnergy(max);
		}
	}
	
	protected List<MT_EnergyPort> getEnergyPorts(boolean output) {
		List<MT_EnergyPort> ret = new ArrayList();
		for (BlockPos p : output ? energyOutput : energyInput) {
			MetaTileBase mte = MetaTileBase.getMTE(getWorld(), p);
			if (mte instanceof MT_EnergyPort)
				ret.add((MT_EnergyPort) mte);
		}
		return ret;
	}
	
	protected List<MT_ItemPort> getItemPorts(boolean output) {
		List<MT_ItemPort> ret = new ArrayList();
		for (BlockPos p : output ? itemOutput : itemInput) {
			MetaTileBase mte = MetaTileBase.getMTE(getWorld(), p);
			if (mte instanceof MT_ItemPort)
				ret.add((MT_ItemPort) mte);
		}
		return ret;
	}
	
	protected List<MT_FluidPort> getFluidPorts(boolean output) {
		List<MT_FluidPort> ret = new ArrayList();
		for (BlockPos p : output ? fluidOutput : fluidInput) {
			MetaTileBase mte = MetaTileBase.getMTE(getWorld(), p);
			if (mte instanceof MT_FluidPort)
				ret.add((MT_FluidPort) mte);
		}
		return ret;
	}
	
	@Nullable
	protected MT_EnergyPort getEnergyPort(int idx, boolean output) {
		List<BlockPos> ls = output ? energyOutput : energyInput;
		if (idx < 0 || idx >= ls.size()) return null;
		BlockPos p = ls.get(idx);
		MetaTileBase mte = MetaTileBase.getMTE(getWorld(), p);
		return mte instanceof MT_EnergyPort ? ((MT_EnergyPort)mte) : null;
	}
	
	@Nullable
	protected MT_ItemPort getItemPort(int idx, boolean output) {
		List<BlockPos> ls = output ? itemOutput : itemInput;
		if (idx < 0 || idx >= ls.size()) return null;
		BlockPos p = ls.get(idx);
		MetaTileBase mte = MetaTileBase.getMTE(getWorld(), p);
		return mte instanceof MT_ItemPort ? ((MT_ItemPort)mte) : null;
	}
	
	@Nullable
	protected MT_FluidPort getFluidPort(int idx, boolean output) {
		List<BlockPos> ls = output ? fluidOutput : fluidInput;
		if (idx < 0 || idx >= ls.size()) return null;
		BlockPos p = ls.get(idx);
		MetaTileBase mte = MetaTileBase.getMTE(getWorld(), p);
		return mte instanceof MT_FluidPort ? ((MT_FluidPort)mte) : null;
	}
}
