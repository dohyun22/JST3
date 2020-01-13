package dohyun22.jst3.tiles.earlytech;

import java.util.ArrayList;
import java.util.List;

import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileKinetic;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.interfaces.IKineticMachine;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MetaTileGearBox extends MetaTileKinetic {
	private final boolean stone;

	public MetaTileGearBox(boolean stone) {
		this.stone = stone;
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MetaTileGearBox(stone);
	}
	
	@Override
	public boolean canUpdate() {
		return false;
	}
	
	@Override
	public boolean canAcceptKU(EnumFacing f) {
		return true;
	}

	@Override
	public boolean canProvideKU(EnumFacing f) {
		return true;
	}

	@Override
	public int injectKU(EnumFacing f, int amt, boolean sim) {
		if (baseTile == null || !canAcceptKU(f) || baseTile.errorCode != 0) return 0;
		amt = Math.min(stone ? 64 : 256, amt);
		ArrayList<BlockPos> list = new ArrayList();
		list.add(getPos());
		if (f != null) list.add(getPos().offset(f));
		try {
			return amt - transferEnergy(f, amt, 0, list, sim);
		} catch (Throwable t) {
			baseTile.errorCode = 1;
			t.printStackTrace();
		}
		return 0;
	}
	
	protected int transferEnergy(EnumFacing dir, int input, int dist, ArrayList<BlockPos> loc, boolean sim) {
		dist++;
		if (dist > (stone ? 32 : 128))
			return 0;
		byte side = JSTUtils.getNumFromFacing(dir);
		for (byte n = 0; n < 6; n++) {
			if (n != side) {
				BlockPos pos = getPos().offset(JSTUtils.getFacingFromNum(n));
				MetaTileBase te = MetaTileBase.getMTE(getWorld(), pos);
				if (te != this && te instanceof IKineticMachine && !loc.contains(pos)) {
					loc.add(pos);
					EnumFacing od = JSTUtils.getOppositeFacing(n);
					if (te instanceof MetaTileGearBox)
						input -= ((MetaTileGearBox)te).transferEnergy(od, input, dist, loc, sim);
					else
						input -= ((IKineticMachine)te).injectKU(od, input, sim);
				}
			}
		}
		return input;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		return this.getSingleTETex((stone ? 's' : 'b') + "gbox");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		ls.add("WIP");
	}
}
