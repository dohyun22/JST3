package dohyun22.jst3.tiles;

import dohyun22.jst3.tiles.interfaces.IKineticMachine;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public abstract class MetaTileKinetic extends MetaTileBase implements IKineticMachine {

	@Override
	public boolean canAcceptKU(EnumFacing f) {
		return false;
	}

	@Override
	public boolean canProvideKU(EnumFacing f) {
		return false;
	}

	@Override
	public int injectKU(EnumFacing f, int amt, boolean sim) {
		return 0;
	}

	public int sendKU(int amt, boolean sim) {
		for (EnumFacing f : EnumFacing.VALUES) {
			if (!canProvideKU(f)) continue;
			TileEntity te = this.getWorld().getTileEntity(this.getPos());
			if (te instanceof TileEntityMeta) {
				MetaTileBase mte = ((TileEntityMeta)te).mte;
				if (mte instanceof IKineticMachine && ((IKineticMachine)mte).canAcceptKU(f.getOpposite()))
					amt -= ((IKineticMachine)mte).injectKU(f.getOpposite(), amt, sim);
			} else if (te instanceof IKineticMachine && ((IKineticMachine)te).canAcceptKU(f.getOpposite())) {
				amt -= ((IKineticMachine)te).injectKU(f.getOpposite(), amt, sim);
			}
		}
		return amt;
	}
}
