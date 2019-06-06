package dohyun22.jst3.tiles.device;

import java.util.ArrayList;

import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MT_CircuitBlock extends MetaTileBase {
	private ArrayList<EnumFacing> coveredSide = new ArrayList();
	public int mode;
	public int timer;
	public int timerMax = 40;
	public boolean outputting = false;

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_CircuitBlock();
	}

	@Override
	public void onPostTick() {
		World w = this.getWorld();
		if (!w.isRemote) {
			boolean b = this.doRSFunc();
			if (b != this.outputting) {
				this.outputting = b;
				this.baseTile.issueUpdate();
			}
		}
	}

	private boolean doRSFunc() {
		switch (this.mode) {
		//NAND & AND
		case 0: 
		case 1:{
			int i = 0;
			for (EnumFacing d : EnumFacing.VALUES) {
				if (getRSInput(d)) {
					i++;
				}
			}
			if (i == (5 - this.coveredSide.size()))
				return this.mode != 0;
			else
				return this.mode == 0;
		}
		//NOR & OR
		case 2: 
		case 3: {
			for (EnumFacing d : EnumFacing.VALUES) {
				if (getRSInput(d)) {
					return this.mode != 2;
				}
			}
			return this.mode == 2;
		}
		//Timer
		case 4: {
			for (EnumFacing d : EnumFacing.VALUES) {
				if (getRSInput(d)) {
					if (this.timer != 0)
						this.timer = 0;
					return false;
				}
			}
			if (timerMax < 10)
				return false;
			if (this.timer > this.timerMax && !this.outputting) {
				this.timer = 0;
				return true;
			} else {
				this.timer++;
				if (this.outputting && this.timer < 4)
					return true;
			}
			return false;
		}
		//PulseFormer
		case 5: {
			boolean rs = false;
			for (EnumFacing d : EnumFacing.VALUES) {
				if (getRSInput(d)) {
					rs = true;
					break;
				}
			}
			if (this.timer <= 0 && rs) {
				this.timer = rs ? 1 : 0;
				return !this.outputting;
			} else {
				this.timer = rs ? 1 : 0;
			}
			return false;
		}
		//XNOR & XOR
		case 6: 
		case 7: {
			boolean b = false;
			for (EnumFacing d : EnumFacing.VALUES) {
				if (d != this.baseTile.facing && !this.coveredSide.contains(d)) {
					b = getRSInput(d);
				}
			}
			return b;
		}
		default:
			return false;
		}
	}
	
	@Override
	public int getWeakRSOutput(EnumFacing f) {
		if (f == this.baseTile.facing.getOpposite() && this.outputting)
			return 15;
		return 0;
	}
	
	@Override
	protected boolean getRSInput(EnumFacing d) {
		return d != this.baseTile.facing && !coveredSide.contains(d) && super.getRSInput(d);
	}
}
