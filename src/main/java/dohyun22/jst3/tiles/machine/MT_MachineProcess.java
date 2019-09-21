package dohyun22.jst3.tiles.machine;

import java.util.ArrayList;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.recipe.RecipeContainer;
import dohyun22.jst3.api.recipe.RecipeList;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.BatterySlot;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.container.JSTSlot;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.tiles.MTETank;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MultiTankHandler;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_MachineProcess extends MT_Machine {
	protected final int inputNum, outputNum, capacity;
	protected final byte fInputNum, fOutputNum;
	protected final boolean rsl, fsl;
	protected final RecipeList recipe;
	protected final String texFace, texTop;
	protected Object[] sfx;
	protected int light;
	protected ItemStack[] result;
	protected FluidStack[] fResult;
	protected EnumFaceIO[] IOs = new EnumFaceIO[5];
	public static final int SLOT_SIZE = 18;

	public MT_MachineProcess(int tier, int in, int out, int fIn, int fOut, int cap, RecipeList rec, boolean sl, boolean fs, String texf, String texu) {
		super(tier);
		inputNum = in;
		outputNum = out;
		fInputNum = (byte) fIn;
		fOutputNum = (byte) fOut;
		capacity = cap;
		recipe = rec;
		rsl = sl;
		fsl = fs;
		texFace = texf;
		texTop = texu;
		batterySlot = (inputNum + outputNum + fInputNum + fOutputNum);
		inv = NonNullList.withSize(getInvSize(), ItemStack.EMPTY);
		if (fInputNum + fOutputNum > 0 && cap > 0) {
			FluidTank[] ft = new FluidTank[fInputNum + fOutputNum];
			for (int n = 0; n < ft.length; n++) {
				boolean b = n < fInputNum;
				ft[n] = new MTETank(cap, !b, b, this, inputNum + outputNum + n);
			}
			tankHandler = new MultiTankHandler(ft);
		}
	}

	public MT_MachineProcess setSfx(Object... o) {
		sfx = o;
		return this;
	}

	public MT_MachineProcess setLux(int v) {
		light = v;
		return this;
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_MachineProcess(tier, inputNum, outputNum, fInputNum, fOutputNum, capacity, recipe, rsl, fsl, texFace, texTop).setSfx(sfx).setLux(light);
	}

	@Override
	public int getInvSize() {
		return inputNum + outputNum + fInputNum + fOutputNum + 1;
	}

	@Override
	protected boolean checkCanWork() {
		RecipeContainer rc = getRecipe();
		if (rc != null) {
			if (energyUse == 0 || mxprogress == 0) {
				ItemStack[] arr = new ItemStack[inputNum];
				for (int n = 0; n < inputNum; n++)
					arr[n] = (ItemStack) inv.get(n);
				FluidTank[] ft = tankHandler == null ? null : tankHandler.getTanks();
				FluidTank[] ft2 = null;
				if (ft != null && fInputNum > 0) {
					ft2 = new FluidTank[fInputNum];
					for (int n = 0; n < ft2.length; n++)
						ft2[n] = ft[n];
				}
				rc.process(arr, ft2, tier, rsl, fsl, true);
				result = rc.getOutputItems();
				fResult = rc.getOutputFluids();
				energyUse = rc.getEnergyPerTick();
				mxprogress = rc.getDuration();
			} else if (energyUse != rc.getEnergyPerTick() || mxprogress != rc.getDuration()) {
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
		if (tankHandler != null && fResult != null) {
			FluidTank[] t = tankHandler.getTanks();
			for (int n = 0; n < Math.min(fResult.length, fOutputNum); n++) t[(n + fOutputNum)].fillInternal(fResult[n], true);
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
		ItemStack[] il = inputNum <= 0 ? null : new ItemStack[inputNum];
		if (il != null)
			for (int n = 0; n < il.length; n++)
				il[n] = ((ItemStack) inv.get(n));
		
		int[] sl = outputNum <= 0 ? null : new int[outputNum];
		if (sl != null)
			for (int n = 0; n < sl.length; n++)
				sl[n] = (n + inputNum);
		
		FluidTank[] ft = tankHandler == null ? null : tankHandler.getTanks();
		FluidTank[] ft2 = null;
		FluidTank[] rft = null;
		if (ft != null) {
			if (fInputNum > 0) {
				ft2 = new FluidTank[fInputNum];
				for (int n = 0; n < ft2.length; n++)
					ft2[n] = ft[n];
			}
			if (fOutputNum > 0) {
				rft = new FluidTank[fOutputNum];
				for (int n = 0; n < rft.length; n++)
					rft[n] = ft[(n + fInputNum)];
			}
		}
		return checkRecipe(recipe, il, ft2, sl, rft, rsl, fsl);
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
		return (sl >= 0) && (sl < inputNum);
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		int[] combined = new int[inputNum + outputNum];
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
		return sl >= inputNum && sl < inputNum + outputNum;
	}
	
	@Override
	public boolean canSlotDrop(int num) {
		return num < inputNum + outputNum || num == batterySlot;
	}

	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		super.onPlaced(p, bs, elb, st);
		if (baseTile != null)
			baseTile.facing = JSTUtils.getClosestSide(p, elb, true);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		
		NBTTagList t = tag.getTagList("itemOut", 10);
		if (!t.hasNoTags()) {
			int sz = tag.getInteger("IOSZ");
			result = new ItemStack[sz];
	        for (int n = 0; n < t.tagCount(); n++) {
	            NBTTagCompound tc = t.getCompoundTagAt(n);
	            int m = tc.getByte("Slot");
	            if (m >= 0 && m < sz)
	            	result[m] = new ItemStack(tc);
	        }
		}
		t = tag.getTagList("fluidOut", 10);
		if (!t.hasNoTags()) {
			int sz = tag.getInteger("FOSZ");
			fResult = new FluidStack[sz];
			for (int n = 0; n < t.tagCount(); n++) {
				NBTTagCompound tc = t.getCompoundTagAt(n);
				int m = tc.getByte("Slot");
				if (m >= 0 && m < sz)
					fResult[m] = FluidStack.loadFluidStackFromNBT(tc);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (result != null && result.length > 0) {
			tag.setInteger("IOSZ", result.length);
			NBTTagList t = new NBTTagList();
			for (int n = 0; n < result.length; n++) {
				if (result[n] != null && !result[n].isEmpty()) {
					NBTTagCompound t2 = new NBTTagCompound();
	                t2.setInteger("Slot", n);
					result[n].writeToNBT(t2);
					t.appendTag(t2);
				}
			}
			tag.setTag("itemOut", t);
		}
		if (fResult != null && fResult.length > 0) {
			tag.setInteger("FOSZ", fResult.length);
			NBTTagList t = new NBTTagList();
			for (int n = 0; n < fResult.length; n++) {
				if (fResult[n] != null) {
					NBTTagCompound t2 = new NBTTagCompound();
					t2.setInteger("Slot", n);
					fResult[n].writeToNBT(t2);
					t.appendTag(t2);
				}
			}
			tag.setTag("fluidOut", t);
		}
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack st, EnumFacing f, float hitX, float hitY, float hitZ) {
		if (baseTile != null && !isClient()) {
			if (tankHandler != null && !st.isEmpty() && pl.getHeldItem(EnumHand.MAIN_HAND) == st) {
				boolean flag = true;
				FluidStack fs = FluidUtil.getFluidContained(st);
				if (fs != null && recipe != null) {
					flag = false;
					for (RecipeContainer r : recipe.list) {
						FluidStack[] inp = r.getInputFluids();
						if (inp != null)
							for (FluidStack fs2 : inp)
								if (fs.isFluidEqual(fs2)) {
									flag = true;
									break;
								}
					}
				}
				if (flag && FluidUtil.interactWithFluidHandler(pl, EnumHand.MAIN_HAND, tankHandler))
					return true;
			}
			pl.openGui(JustServerTweak.INSTANCE, 1, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
		}
		return true;
	}

	@Override
	protected void onStartWork() {
		if (sfx != null)
			try {
				if (sfx.length == 3)
					getWorld().playSound(null, getPos(), (SoundEvent)sfx[0], SoundCategory.BLOCKS, (float)sfx[1], (float)sfx[2]);
			} catch (Exception e) {}
	}

	@Override
	public int getLightValue() {
		return baseTile.isActive() ? light : 0;
	}
	
	@Override
	protected boolean toggleMachine(boolean on) {
		boolean ret = super.toggleMachine(on);
		if (light > 0 && ret) updateLight();
		return ret;
	}

	@Override
	protected void addSlot(ContainerGeneric cg, InventoryPlayer inv, TileEntityMeta te) {
		switch (inputNum) {
		case 0: break;
		case 1:
			cg.addSlot(new Slot(te, 0, 53, 31)); break;
		case 2:
			cg.addSlot(new Slot(te, 0, 44, 31)); cg.addSlot(new Slot(te, 1, 62, 31)); break;
		case 3:
			cg.addSlot(new Slot(te, 0, 35, 31)); cg.addSlot(new Slot(te, 1, 53, 31)); cg.addSlot(new Slot(te, 2, 71, 31)); break;
		case 4:
			cg.addSlot(new Slot(te, 0, 44, 22)); cg.addSlot(new Slot(te, 1, 62, 22));
			cg.addSlot(new Slot(te, 2, 44, 40)); cg.addSlot(new Slot(te, 3, 62, 40)); break;
		case 5:
			cg.addSlot(new Slot(te, 0, 35, 22)); cg.addSlot(new Slot(te, 1, 53, 22)); cg.addSlot(new Slot(te, 2, 71, 22));
			cg.addSlot(new Slot(te, 3, 44, 40)); cg.addSlot(new Slot(te, 4, 62, 40)); break;
		default:
			cg.addSlot(new Slot(te, 0, 35, 22)); cg.addSlot(new Slot(te, 1, 53, 22)); cg.addSlot(new Slot(te, 2, 71, 22));
			cg.addSlot(new Slot(te, 3, 35, 40)); cg.addSlot(new Slot(te, 4, 53, 40)); cg.addSlot(new Slot(te, 5, 71, 40));
		}
		int s = inputNum;
		switch (outputNum) {
		case 0: break;
		case 1:
			cg.addSlot(JSTSlot.out(te, s, 133, 31)); break;
		case 2:
			cg.addSlot(JSTSlot.out(te, s, 124, 31)); cg.addSlot(JSTSlot.out(te, s + 1, 142, 31)); break;
		case 3:
			cg.addSlot(JSTSlot.out(te, s, 115, 31)); cg.addSlot(JSTSlot.out(te, s + 1, 133, 31)); cg.addSlot(JSTSlot.out(te, s + 2, 151, 31)); break;
		case 4:
			cg.addSlot(JSTSlot.out(te, s, 124, 22)); cg.addSlot(JSTSlot.out(te, s + 1, 142, 22));
			cg.addSlot(JSTSlot.out(te, s + 2, 124, 40)); cg.addSlot(JSTSlot.out(te, s + 3, 142, 40)); break;
		case 5:
			cg.addSlot(JSTSlot.out(te, s, 115, 22)); cg.addSlot(JSTSlot.out(te, s + 1, 133, 22)); cg.addSlot(JSTSlot.out(te, s + 2, 151, 22));
			cg.addSlot(JSTSlot.out(te, s + 3, 124, 40)); cg.addSlot(JSTSlot.out(te, s + 4, 142, 40)); break;
		default:
			cg.addSlot(JSTSlot.out(te, s, 115, 22)); cg.addSlot(JSTSlot.out(te, s + 1, 133, 22)); cg.addSlot(JSTSlot.out(te, s + 2, 151, 22));
			cg.addSlot(JSTSlot.out(te, s + 3, 115, 40)); cg.addSlot(JSTSlot.out(te, s + 4, 133, 40)); cg.addSlot(JSTSlot.out(te, s + 5, 151, 40));
		}
		s += outputNum;
		int y = inputNum == 0 ? 31 : 62;
		switch (fInputNum) {
		case 0: break;
		case 1: cg.addSlot(JSTSlot.fl(te, s, 53, y)); break;
		case 2: cg.addSlot(JSTSlot.fl(te, s, 44, y)); cg.addSlot(JSTSlot.fl(te, s + 1, 62, y)); break;
		default: cg.addSlot(JSTSlot.fl(te, s, 35, y)); cg.addSlot(JSTSlot.fl(te, s + 1, 53, y)); cg.addSlot(JSTSlot.fl(te, s + 2, 71, y)); break;
		}
		s += fInputNum;
		y = outputNum == 0 ? 31 : 62;
		switch (fOutputNum) {
		case 0: break;
		case 1: cg.addSlot(JSTSlot.fl(te, s, 133, y)); break;
		case 2: cg.addSlot(JSTSlot.fl(te, s, 124, y)); cg.addSlot(JSTSlot.fl(te, s + 1, 142, y)); break;
		default: cg.addSlot(JSTSlot.fl(te, s, 115, y)); cg.addSlot(JSTSlot.fl(te, s + 1, 133, y)); cg.addSlot(JSTSlot.fl(te, s + 2, 151, y)); break;
		}
		s += fOutputNum;
		cg.addSlot(new BatterySlot(te, s, 8, 53, false, true));
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void addComp(GUIGeneric gg) {
		try {
			ContainerGeneric cg = (ContainerGeneric)gg.inventorySlots;
			for (int n = 0; n < inputNum + outputNum; n++) gg.addSlot(cg.getSlot(n), 0);
			int s = inputNum + outputNum;
			for (int n = 0; n < fInputNum + fOutputNum; n++) gg.addSlot(cg.getSlot(n + s), 3);
			s += fInputNum + fOutputNum;
		} catch (Throwable t) {}
		gg.addPrg(89, 31, recipe == null ? new String[0] : new String[] {JustServerTweak.MODID + "." + recipe.name});
		gg.addSlot(8, 53, 2);
		gg.addPwr(12, 31);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTieredTex(tier);
		return new TextureAtlasSprite[] {t, texTop == null ? t : getTETex(texTop), t, t, t, getTETex(texFace)};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++) {
			if (n == 1 && texTop != null)
				ret[n] = getTETex(texTop);
			else if (baseTile.facing == JSTUtils.getFacingFromNum(n))
				ret[n] = getTETex(texFace + (baseTile.isActive() ? "" : "_off"));
			else
				ret[n] = getTieredTex(tier);
		}
		return ret;
	}

	public static enum EnumRelativeFacing {
		DOWN, UP, BACK, FRONT, LEFT, RIGHT;
		
	}

	public static enum EnumFaceIO {
		ST_IN, ST_OUT, ST_IO, FL_IN, FL_OUT, FL_IO;
	}
}