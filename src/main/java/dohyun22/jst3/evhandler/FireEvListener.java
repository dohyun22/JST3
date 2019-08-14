package dohyun22.jst3.evhandler;

import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTChunkData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;

public class FireEvListener implements IWorldEventListener {
	public static final FireEvListener INSTANCE = new FireEvListener();

	@Override
	public void notifyBlockUpdate(World w, BlockPos p, IBlockState os, IBlockState ns, int flag) {
		if (JSTCfg.fireFineDust && !w.isRemote && ns != null && ns.getBlock() == Blocks.FIRE) JSTChunkData.addFineDust(w, new ChunkPos(p), 100, true);
	}

	@Override public void notifyLightSet(BlockPos p) { }
	@Override public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {}
	@Override public void playSoundToAllNearExcept(EntityPlayer pl, SoundEvent s, SoundCategory sc, double x, double y, double z, float v, float p) {}
	@Override public void playRecord(SoundEvent s, BlockPos p) {}
	@Override public void spawnParticle(int particleID, boolean ignoreRange, double x, double y, double z, double xs, double ys, double zs, int... par) {}
	@Override public void spawnParticle(int id, boolean ir, boolean b1, double x, double y, double z, double xs, double ys, double zs, int... par) {}
	@Override public void onEntityAdded(Entity e) {}
	@Override public void onEntityRemoved(Entity e) {}
	@Override public void broadcastSound(int id, BlockPos p, int d) {}
	@Override public void playEvent(EntityPlayer pl, int t, BlockPos p, int d) {}
	@Override public void sendBlockBreakProgress(int id, BlockPos p, int prg) {}
}
