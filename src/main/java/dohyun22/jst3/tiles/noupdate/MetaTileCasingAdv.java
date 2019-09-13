package dohyun22.jst3.tiles.noupdate;

import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;

public class MetaTileCasingAdv extends MetaTileCasing {
	@Nullable
	private final ItemStack[] drop;
	private final float hardness;
	private final float resistance;
	private final boolean opaque;

	public MetaTileCasingAdv(String name, @Nullable ItemStack[] drop, float hn, float re, boolean op) {
		super(name);
		this.drop = drop;
		this.hardness = hn;
		this.resistance = re;
		this.opaque = op;
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MetaTileCasingAdv(name, drop, hardness, resistance, opaque);
	}
	
	@Nonnull
	public void getDrops(ArrayList<ItemStack> ls) {
		if (drop != null) {
			for (ItemStack st : drop) {
				if (st == null || st.isEmpty()) continue;
				ls.add(st);
			}
			return;
		}
		super.getDrops(ls);
	}
	
	@Override
	public float getHardness() {
		return hardness;
	}
	
	@Override
	public float getResistance(@Nullable Entity ee, Explosion ex) {
		return resistance;
	}
	
	@Override
	public boolean isSideOpaque(EnumFacing f) {
		return opaque;
	}
	
	@Override
	public int getLightOpacity() {
		return opaque ? 255 : 0;
	}
}
