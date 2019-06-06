package dohyun22.jst3.tiles.interfaces;

import net.minecraft.util.EnumFacing;

public interface IKineticMachine {
	boolean canAcceptKU(EnumFacing f);

	boolean canProvideKU(EnumFacing f);

	int injectKU(EnumFacing f, int amt, boolean sim);
}
