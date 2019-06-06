package dohyun22.jst3.compat.ifg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.buuz135.industrial.api.plant.PlantRecollectable;

import ic2.api.crops.CropCard;
import ic2.api.crops.ICropTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlantIC2Crop extends PlantRecollectable {

	public PlantIC2Crop() {
		super("ic2crop");
	}

	@Override
	public boolean canBeHarvested(World w, BlockPos p, IBlockState bs) {
		TileEntity te = w.getTileEntity(p);
		try {
			if (te instanceof ICropTile) {
				CropCard cc = ((ICropTile)te).getCrop();
				if (cc != null)
					return cc.canBeHarvested((ICropTile)te) && cc.getOptimalHarvestSize((ICropTile)te) <= ((ICropTile)te).getCurrentSize();
			}
		} catch (Throwable t) {}
		return false;
	}

	@Override
	public List<ItemStack> doHarvestOperation(World w, BlockPos p, IBlockState bs) {
		List<ItemStack> ret = new ArrayList();
		TileEntity te = w.getTileEntity(p);
		try {
			if (te instanceof ICropTile) {
				List<ItemStack> ls = ((ICropTile)te).performHarvest();
				if (ls != null) ret.addAll(ls);
			}
		} catch (Throwable t) {}
		return ret;
	}

	@Override
	public boolean shouldCheckNextPlant(World w, BlockPos p, IBlockState bs) {
		return true;
	}

	@Override
	public List<String> getRecollectablesNames() {
		return Collections.singletonList("jst.compat.ifg.ic2crop");
	}
}
