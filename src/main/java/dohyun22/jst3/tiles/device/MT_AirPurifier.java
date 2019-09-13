package dohyun22.jst3.tiles.device;

import java.util.List;

import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileEnergyInput;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTChunkData;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_AirPurifier extends MetaTileEnergyInput {
	private boolean notBlocked;

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_AirPurifier();
	}

	@Override
	public int maxEUTransfer() {
		return 32;
	}
	
	@Override
	public long getMaxEnergy() {
		return 5000;
	}
	
	@Override
	public void onPostTick() {
		super.onPostTick();
		if (isClient()) return;
		if (notBlocked && baseTile.getTimer() % 100 == 0) {
			if (baseTile.energy >= 200) {
				boolean flag = false;
				ChunkPos cp = new ChunkPos(baseTile.getPos());
				int fd = JSTChunkData.getFineDust(getWorld(), cp);
				if (fd > 0) {
					JSTChunkData.addFineDust(getWorld(), cp, -750, true);
					flag = true;
				}
				baseTile.setActive(flag);
			} else if (baseTile.isActive())
				baseTile.setActive(false);
		}
		if (baseTile.isActive()) baseTile.energy = Math.max(baseTile.energy - 8, 0);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		notBlocked = tag.getBoolean("nb");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setBoolean("nb", notBlocked);
	}

	@Override
	public void onBlockUpdate() {
		super.onBlockUpdate();
		notBlocked = false;
		for (EnumFacing f : EnumFacing.VALUES) {
			if (getWorld().isAirBlock(getPos().offset(f))) {
				notBlocked = true;
				break;
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTETex("filter"), u = getTETex("fan");
		return new TextureAtlasSprite[] {u, u, t, t, t, t};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = getDefaultTexture();
		if (!baseTile.isActive()) 
			ret[0] = ret[1] = getTETex("fan_off");
		return ret;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getInformation(ItemStack st, World w, List<String> ls, ITooltipFlag adv) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.purifier"));
	}
}