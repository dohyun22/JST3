package dohyun22.jst3.tiles.noupdate;

import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MetaTileCasing extends MetaTileBase {
	public final String name;

	public MetaTileCasing(String name) {
		this.name = name;
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MetaTileCasing(name);
	}

	@Override
	public boolean canUpdate() {
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		return this.getSingleTETex(name);
	}
	
	@Override
	@SideOnly(value=Side.CLIENT)
	public String getModelKey() {
		return name;
	}
	
	@Override
	public boolean isMultiBlockPart() {
		return true;
	}
	
	@Override
	public <T> T getCapability(Capability<T> c, EnumFacing f) {
		return null;
	}
}
