package dohyun22.jst3.tiles.machine;

import java.util.ArrayList;

import javax.annotation.Nullable;

import dohyun22.jst3.api.recipe.RecipeContainer;
import dohyun22.jst3.api.recipe.RecipeList;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.BatterySlot;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.tiles.MTETank;
import dohyun22.jst3.tiles.MultiTankHandler;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class MT_MachineGeneric extends MT_Machine {
	protected final int inputNum;
	protected final int outputNum;
	protected final byte fInputNum;
	protected final byte fOutputNum;
	protected final boolean rsl;
	protected final boolean fsl;
	protected final RecipeList recipe;
	protected ItemStack[] result;
	protected FluidStack[] fResult;

	public MT_MachineGeneric(int tier, int in, int out, int FIn, int FOut, int cap, RecipeList recipe, boolean sl, boolean fsl) {
		super(tier);
		this.inputNum = in;
		this.outputNum = out;
		this.fInputNum = (byte) FIn;
		this.fOutputNum = (byte) FOut;
		this.recipe = recipe;
		this.rsl = sl;
		this.fsl = fsl;
		this.batterySlot = (this.inputNum + this.outputNum + this.fInputNum + this.fOutputNum);
		this.inv = NonNullList.withSize(getInvSize(), ItemStack.EMPTY);
		if ((this.fInputNum + this.fOutputNum > 0) && (cap > 0)) {
			FluidTank[] ft = new FluidTank[this.fInputNum + this.fOutputNum];
			for (int n = 0; n < ft.length; n++) {
				boolean b = n < this.fInputNum;
				ft[n] = new MTETank(cap, !b, b, this, this.inputNum + this.outputNum + n);
			}
			this.tankHandler = new MultiTankHandler(ft);
		}
	}

	@Override
	public int getInvSize() {
		return this.inputNum + this.outputNum + this.fInputNum + this.fOutputNum + 1;
	}

	@Override
	protected boolean checkCanWork() {
		RecipeContainer rc = getRecipe();
		if (rc != null) {
			if (this.energyUse == 0 || this.mxprogress == 0) {
				ItemStack[] arr = new ItemStack[this.inputNum];
				for (int n = 0; n < this.inputNum; n++)
					arr[n] = (ItemStack) this.inv.get(n);
				FluidTank[] ft = this.tankHandler == null ? null : this.tankHandler.getTanks();
				FluidTank[] ft2 = null;
				if (ft != null && this.fInputNum > 0) {
					ft2 = new FluidTank[this.fInputNum];
					for (int n = 0; n < ft2.length; n++)
						ft2[n] = ft[n];
				}
				rc.process(arr, ft2, tier, this.rsl, this.fsl, true);
				result = rc.getOutputItems();
				fResult = rc.getOutputFluids();
				energyUse = rc.getEnergyPerTick();
				mxprogress = rc.getDuration();
			} else if (this.energyUse != rc.getEnergyPerTick() || this.mxprogress != rc.getDuration()) {
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	protected void finishWork() {
		if (result != null) {
			for (int n = 0; n < Math.min(result.length, outputNum); n++) {
				ItemStack ro = result[n];
				if (ro == null || ro.isEmpty()) continue;
				ItemStack out = (ItemStack) inv.get(n + inputNum);
				if (out.isEmpty()) {
					inv.set(n + inputNum, ro.copy());
				} else if (JSTUtils.canCombine(out, ro)) {
					out.grow(ro.getCount());
				}
			}
		}
		if (this.tankHandler != null && fResult != null) {
			FluidTank[] t = tankHandler.getTanks();
			for (int n = 0; n < Math.min(fResult.length, fOutputNum); n++) t[(n + fOutputNum)].fillInternal(this.fResult[n], true);
		}
	}
	
	@Override
	protected void clear(boolean cr) {
		super.clear(cr);
		if (cr) {
			result = null;
			fResult = null;
		}
	}

	@Nullable
	protected RecipeContainer getRecipe() {
		ItemStack[] il = this.inputNum <= 0 ? null : new ItemStack[this.inputNum];
		if (il != null)
			for (int n = 0; n < il.length; n++)
				il[n] = ((ItemStack) this.inv.get(n));
		
		int[] sl = this.outputNum <= 0 ? null : new int[this.outputNum];
		if (sl != null)
			for (int n = 0; n < sl.length; n++)
				sl[n] = (n + this.inputNum);
		
		FluidTank[] ft = this.tankHandler == null ? null : this.tankHandler.getTanks();
		FluidTank[] ft2 = null;
		FluidTank[] rft = null;
		if (ft != null) {
			if (this.fInputNum > 0) {
				ft2 = new FluidTank[this.fInputNum];
				for (int n = 0; n < ft2.length; n++)
					ft2[n] = ft[n];
			}
			if (this.fOutputNum > 0) {
				rft = new FluidTank[this.fOutputNum];
				for (int n = 0; n < rft.length; n++)
					rft[n] = ft[(n + this.fInputNum)];
			}
		}
		return checkRecipe(this.recipe, il, ft2, sl, rft, this.rsl, this.fsl);
	}

	@Nullable
	protected RecipeContainer checkRecipe(RecipeList recipe, ItemStack[] in, FluidTank[] fin, int[] iout, FluidTank[] fout, boolean sl, boolean fsl) {
		RecipeContainer rc = getContainer(recipe, in, fin, sl, fsl);
		if (rc == null)
			return null;
		boolean flag = false;
		ItemStack[] outputs = rc.getOutputItems();
		if (outputs != null) {
			if (iout == null || outputs.length != iout.length)
				return null;
			for (int n = 0; n < outputs.length; n++) {
				ItemStack ro = outputs[n];
				if (ro == null || ro.isEmpty()) continue;
				ItemStack out = (ItemStack) inv.get(iout[n]);
				if (!out.isEmpty()) {
					if (!JSTUtils.canCombine(out, ro))
						return null;
					int cnt = out.getCount() + ro.getCount();
					if (cnt > baseTile.getInventoryStackLimit() || cnt > out.getMaxStackSize())
						return null;
				}
			}
			flag = true;
		}
		FluidStack[] foutputs = rc.getOutputFluids();
		if (foutputs != null) {
			if (fout == null || foutputs.length != fout.length)
				return null;
			for (int n = 0; n < foutputs.length; n++) {
				FluidStack ro = foutputs[n];
				if (ro == null) continue;
				int amt = fout[n].fillInternal(ro, false);
				if (amt != ro.amount)
					return null;
			}
			flag = true;
		}
		return flag ? rc : null;
	}
	
	@Nullable
	protected RecipeContainer getContainer(RecipeList recipe, ItemStack[] in, FluidTank[] fin, boolean sl, boolean fsl) {
		return MRecipes.getRecipe(recipe, in, fin, tier, sl, fsl);
	}

	@Override
	protected boolean isInputSlot(int sl) {
		return (sl >= 0) && (sl < this.inputNum);
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		int[] combined = new int[this.inputNum + this.outputNum];
		if (combined.length > 0)
			for (int n = 0; n < combined.length; n++)
				combined[n] = n;
		return combined;
	}

	@Override
	public boolean isItemValidForSlot(int sl, ItemStack st) {
		return isInputSlot(sl);
	}
	
	@Override
	public boolean canExtractItem(int sl, ItemStack st, EnumFacing dir) {
		return sl >= this.inputNum && sl < this.inputNum + this.outputNum;
	}
	
	@Override
	public boolean canSlotDrop(int num) {
		return num < this.inputNum + this.outputNum || num == this.batterySlot;
	}

	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		super.onPlaced(p, bs, elb, st);
		if (this.baseTile != null)
			this.baseTile.facing = JSTUtils.getClosestSide(p, elb, st, true);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		
		NBTTagList t = tag.getTagList("itemOut", 10);
		if (!t.hasNoTags()) {
			int sz = tag.getInteger("IOSZ");
			this.result = new ItemStack[sz];
	        for (int n = 0; n < t.tagCount(); n++) {
	            NBTTagCompound tc = t.getCompoundTagAt(n);
	            int m = tc.getByte("Slot");
	            if (m >= 0 && m < sz)
	            	this.result[m] = new ItemStack(tc);
	        }
		}
		t = tag.getTagList("fluidOut", 10);
		if (!t.hasNoTags()) {
			int sz = tag.getInteger("FOSZ");
			this.fResult = new FluidStack[sz];
			for (int n = 0; n < t.tagCount(); n++) {
				NBTTagCompound tc = t.getCompoundTagAt(n);
				int m = tc.getByte("Slot");
				if (m >= 0 && m < sz)
					this.fResult[m] = FluidStack.loadFluidStackFromNBT(tc);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (this.result != null && this.result.length > 0) {
			tag.setInteger("IOSZ", this.result.length);
			NBTTagList t = new NBTTagList();
			for (int n = 0; n < this.result.length; n++) {
				if (this.result[n] != null && !this.result[n].isEmpty()) {
					NBTTagCompound t2 = new NBTTagCompound();
	                t2.setInteger("Slot", n);
					this.result[n].writeToNBT(t2);
					t.appendTag(t2);
				}
			}
			tag.setTag("itemOut", t);
		}
		if (this.fResult != null && this.fResult.length > 0) {
			tag.setInteger("FOSZ", this.fResult.length);
			NBTTagList t = new NBTTagList();
			for (int n = 0; n < this.fResult.length; n++) {
				if (this.fResult[n] != null) {
					NBTTagCompound t2 = new NBTTagCompound();
					t2.setInteger("Slot", n);
					this.fResult[n].writeToNBT(t2);
					t.appendTag(t2);
				}
			}
			tag.setTag("fluidOut", t);
		}
	}
}
