package dohyun22.jst3.tiles.noupdate;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.api.IDCGenerator;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MetaTileDCSolar extends MetaTileBase implements IDCGenerator {
	private final byte tier;
	
	public MetaTileDCSolar(int type) {
		this.tier = (byte)type;
	}
	
	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MetaTileDCSolar(tier);
	}

	@Override
	public double getPower(World w, BlockPos p) {
		if (JSTUtils.checkSun(getWorld(), getPos()))
			return tier < 0 ? 1 : JSTUtils.getVoltFromTier(tier);
		return 0.0D;
	}
	
	@Override
	public boolean canUpdate() {
		return false;
	}
	
	@Override
	public List<AxisAlignedBB> getBox() {
		super.getBox();
		return Arrays.asList(new AxisAlignedBB[] { SLAB_BOTTOM_AABB });
	}
	
	@Override
	@Nullable
	public AxisAlignedBB getBoundingBox() {
		return SLAB_BOTTOM_AABB;
	}
	
	@Override
	public int getLightOpacity() {
		return 0;
	}
	
	@Override
	public boolean isSideSolid(EnumFacing s) {
		return s == EnumFacing.DOWN;
	}
	
	@Override
	public boolean isSideOpaque(EnumFacing s) {
		return s == EnumFacing.DOWN;
	}
	
	@Override
	@SideOnly(value = Side.CLIENT)
	public String getModelKey() {
		return "jst_dcsolar" + tier;
	}
	
	@Override
	@SideOnly(value=Side.CLIENT)
	public List<BakedQuad> getModel(boolean isItem) {
		return JSTUtils.makeCubeAABB(getDefaultTexture(), SLAB_BOTTOM_AABB);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite s = tier < 0 ? getTETex("basic_side") : getTieredTex(tier);
		return new TextureAtlasSprite[] {s , getTETex(tier < 0 ? "basic_solar" : "adv_solar"), s, s, s, s};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getInformation(ItemStack st, World w, List<String> ls, ITooltipFlag adv) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.dcgen.desc"));
	}
}
