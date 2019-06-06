package dohyun22.jst3.tiles;

import java.util.ArrayList;

import javax.annotation.Nullable;

import dohyun22.jst3.items.JSTItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class MTETank extends FluidTank {
	private final ArrayList<String> list;
	private final boolean isBlacklist;
	private final MetaTileBase mte;
	private final int slot;

	public MTETank(int cap, boolean drain, boolean fill, MetaTileBase mte, int sl) {
		this(cap, drain, fill, mte, sl, false);
	}

	public MTETank(int cap, boolean drain, boolean fill, MetaTileBase mte, int sl, boolean bl, String... fl) {
		super(null, cap);
		this.mte = mte;
		this.slot = sl;
		this.canDrain = drain;
		this.canFill = fill;
		this.isBlacklist = bl;
		if (fl != null && fl.length > 0) {
			list = new ArrayList();
			for (String s : fl)
				list.add(s);
		} else {
			list = null;
		}
	}
	
	@Override
    protected void onContentsChanged() {
		if (mte != null && slot >= 0 && slot < mte.inv.size()) {
			if (fluid == null || getFluidAmount() <= 0) {
				mte.setInventorySlotContents(slot, ItemStack.EMPTY);
				return;
			}
			ItemStack st = new ItemStack(JSTItems.item1, 1, 9999);
			st.setCount(MathHelper.clamp(getFluidAmount() / 1000, 1, 64));
			st.setTagCompound(writeToNBT(new NBTTagCompound()));
			mte.setInventorySlotContents(slot, st);
		}
    }

	@Override
	public int fillInternal(FluidStack fs, boolean fill) {
		if (fs == null || (list != null && isBlacklist == list.contains(fs.getFluid().getName()))) return 0;
        return super.fillInternal(fs, fill);
    }
}