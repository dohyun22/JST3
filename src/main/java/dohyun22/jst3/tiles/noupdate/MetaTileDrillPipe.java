package dohyun22.jst3.tiles.noupdate;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.Explosion;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MetaTileDrillPipe extends MetaTileBase {
	public boolean noDrop;

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MetaTileDrillPipe();
	}
	
	@Override
	public boolean canUpdate() {
		return false;
	}

	@Override
	public List<AxisAlignedBB> getBox() {
		List<AxisAlignedBB> ret = new ArrayList(1);
		ret.add(new AxisAlignedBB(0.375, 0.0, 0.375, 0.625, 1.0, 0.625));
		return ret;
	}
	
	@Nullable
	public AxisAlignedBB getBoundingBox() {
		return new AxisAlignedBB(0.375, 0.0, 0.375, 0.625, 1.0, 0.625);
	}
	
	@Override
	public float getHardness() {
		return 1.0F;
	}
	
	@Override
	public float getResistance(@Nullable Entity ee, Explosion ex) {
		return 4.0F;
	}
	
	@Override
	public int getLightOpacity() {
		return 0;
	}
	
	@Override
	public boolean isSideSolid(EnumFacing s) {
		return false;
	}
	
	@Override
	public boolean isSideOpaque(EnumFacing s) {
		return false;
	}
	
	@Override
	@SideOnly(value = Side.CLIENT)
	public String getModelKey() {
		return "jst_dpipe";
	}

	@Override
	@SideOnly(value = Side.CLIENT)
	public List<BakedQuad> getModel(boolean isItem) {
		return JSTUtils.makeCube(MetaTileBase.getSingleTex("minecraft:blocks/anvil_base"), 0.375F, 0.0F, 0.375F, 0.625F, 1.0F, 0.625F);
	}
}
