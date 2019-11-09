package dohyun22.jst3.utils;

import java.util.Locale;

import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;

public enum EnumRelativeFacing {
	DOWN, UP, BACK, FRONT, LEFT, RIGHT;
	public static final EnumRelativeFacing[] VALUES;

	static {
		VALUES = values();
	}

	@Nullable
	public EnumFacing toFacing(@Nullable EnumFacing in) {
		if (in == null || in.getAxis() == EnumFacing.Axis.Y) return null;
		if (this == DOWN) return EnumFacing.DOWN;
		if (this == UP) return EnumFacing.UP;
		if (this == BACK) return in.getOpposite();
		if (this == FRONT) return in;
		if (this == LEFT) return in.rotateY();
		if (this == RIGHT) return in.rotateYCCW();
		return null;
	}
}
