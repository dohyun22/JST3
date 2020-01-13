package dohyun22.jst3.tiles.energy;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileEnergyInput;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_DummyLoad extends MetaTileEnergyInput {
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_DummyLoad();
	}

	@Override
	public long injectEnergy(@Nullable EnumFacing dir, long v, boolean sim) {
		return v;
	}
	
	@Override
	public int maxEUTransfer() {
		return Integer.MAX_VALUE;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.dummyload"));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite tt = getTETex("dummy");
		return new TextureAtlasSprite[] { tt, tt, tt, tt, tt, tt };
	}
}
