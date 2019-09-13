package dohyun22.jst3.tiles.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import dohyun22.jst3.api.IDust;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileEnergyInput;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.energy.MetaTileCable;
import dohyun22.jst3.tiles.energy.MetaTileGenerator;
import dohyun22.jst3.tiles.energy.MetaTileSolarGen;
import dohyun22.jst3.tiles.interfaces.IScrewDriver;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;

public class TestMetaTileEntityGenerator extends MetaTileGenerator implements IScrewDriver, IDust {
	public List<BlockPos> cablePos = new ArrayList<BlockPos>();
	public List<BlockPos> connectedMachine = new ArrayList<BlockPos>();

	public TestMetaTileEntityGenerator(int tier) {
		super(tier, false);
	}

	protected void injectEnergyToSide(@Nullable EnumFacing f, long transfer) {
		if (f != null && isEnergyOutput(f)) {
			updateFaceCable();
			EnumFacing opp = f.getOpposite();
			if (connectedMachine == null)
				return;
			for (BlockPos pos : connectedMachine) {
				baseTile.energy -= JSTUtils.sendEnergy(getWorld(), pos, opp, Math.min(transfer, baseTile.energy),
						false);
			}
		}
	}

	protected void updateFaceCable() {
		World world = getWorld();
		connectedMachine.clear();
		cablePos.clear();
		
		BlockPos p = this.getPos();
		addCable(cablePos, p, world);
		System.out.println(cablePos.size() + ":" + connectedMachine.size());
	}

	/** posList is cableList **/
	protected void addCable(List<BlockPos> posList, BlockPos p, World world) {
		for (EnumFacing f : EnumFacing.values()) {
			BlockPos padd = p.offset(f);
			if (posList.contains(padd)) {
				continue;
			}
			TileEntity t = world.getTileEntity(padd);
			if (t instanceof TileEntityMeta) {
				TileEntityMeta tm = (TileEntityMeta) t;
				if (tm.mte instanceof MetaTileCable) {
					cablePos.add(p);
					addCable(posList, padd, world);
				} else if ((tm).hasValidMTE()) {
					connectedMachine.add(p);
				}
			}
		}
	}

	protected long getPower() {
		return maxEUTransfer();
	}

	@Override
	public long getMaxEnergy() {
		return this.maxEUTransfer() * 400;
	}

	@Override
	public boolean canProvideEnergy() {
		return true;
	}

	@Override
	public boolean isEnergyOutput(EnumFacing side) {
		return true;
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new TestMetaTileEntityGenerator(tier);
	}

}
