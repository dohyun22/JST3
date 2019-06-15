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
import dohyun22.jst3.tiles.machine.MT_MachineGeneric;
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
				// 일단은 개념적인 구현시 속도 측정위함
				//전압강하는 d* 알고리즘으로 최소거리 구한 후 전압.전류 강하된 만큼 줄일 예정
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
	//재귀함수로 일단 연결된 케이블 다 넣기
	//같은 시스템, 즉 전선으로 연결되있으면 재계산 안하도록 따로 리스트 저장이 필요할 듯
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
