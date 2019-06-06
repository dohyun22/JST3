package dohyun22.jst3.compat.ifg;

import com.buuz135.industrial.api.straw.StrawHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class CSH extends StrawHandler {
	private final Object[] eff;
	private final String fl;

	public CSH(String fl, Object... eff) {
		this.fl = fl;
		this.eff = eff;
	}

	@Override
	public boolean validFluid(FluidStack fs) {
		return fs.getFluid().getName().equals(fl);
	}

	@Override
	public void onDrink(World w, BlockPos p, FluidStack st, EntityPlayer pl, boolean fc) {
		for (Object o : eff)
			if (o instanceof PotionEffect) {
				PotionEffect pe = ((PotionEffect)o);
				pl.addPotionEffect(new PotionEffect(pe.getPotion(), pe.getDuration(), pe.getAmplifier()));
			} else if (o instanceof Integer) {
				switch ((Integer)o) {
				case 1: case 2: case 3: case 4: pl.setFire((Integer)o * 10);break;
				case 5: pl.extinguish(); break;
				}
			} else if (o instanceof Object[]) {
				Object[] o2 = (Object[])o;
				if (o2.length == 2 && o2[0] instanceof DamageSource && o2[1] instanceof Float)
					pl.attackEntityFrom((DamageSource)o2[0], (Float)o2[1]);
			}
	}
}