package dohyun22.jst3.tiles.test;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.multiblock.MT_Multiblock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_Dam extends MT_Multiblock {
	private int turbines, blocks;
	private boolean damNear;
	

	@Override
	protected boolean checkStructure() {
		BlockPos p = getPos();
		Biome b = getWorld().getBiome(p);
		if (!BiomeDictionary.hasType(b, BiomeDictionary.Type.RIVER)) return false;
		if (check(p, true, true)) {
			
		} else if (check(p, true, false)) {
			
		} else return false;
		searchNearby();
		return true;
	}

	private boolean check(BlockPos p, boolean cen, boolean x) {
		World w = getWorld();
		MutableBlockPos m = new MutableBlockPos(p);
		for (int n = 0; n < 24; n++) {
			if (m.getY() <= 0) break;
			if (cen && n == 0) continue;
			IBlockState bs = w.getBlockState(m);
			if (bs.getBlock() == JSTBlocks.block1 && bs.getBlock().getMetaFromState(bs) == 1) {
				continue;
			}
			m.move(EnumFacing.DOWN);
			bs = w.getBlockState(m);
			if (bs.getMaterial() == Material.WATER) return false;
		}
		return true;
	}

	private void searchNearby() {
		int cx = getPos().getX() >> 4, cz = getPos().getZ() >> 4;
		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				Chunk c = getWorld().getChunkFromChunkCoords(cx + x, cz + z);
				for (TileEntity te : c.getTileEntityMap().values())
					if (te instanceof TileEntityMeta && ((TileEntityMeta)te).mte != this && ((TileEntityMeta)te).mte instanceof MT_Dam) {
						damNear = true;
						break;
					}
			}
		}
	}

	@Override
	protected boolean checkCanWork() {
		return turbines > 0;
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_Dam();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite a = getTETex("recycler"), b = getTex(JustServerTweak.MODID + ":blocks/tileentity/plated_block");
		return new TextureAtlasSprite[] {b, a, b, b, b, b};
	}

	@Override
	@SideOnly(value=Side.CLIENT)
	public String getModelKey() {
		return "jst_dam";
	}
}
