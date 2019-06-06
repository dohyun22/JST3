package dohyun22.jst3.compat.rc;

import mods.railcraft.api.tracks.TrackKit;
import mods.railcraft.api.tracks.TrackType;
import mods.railcraft.common.blocks.tracks.behaivor.HighSpeedTools;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class EHSHandler extends TrackType.EventHandler {
	@Override public void onMinecartPass(World w, EntityMinecart c, BlockPos p, TrackKit tk) {try {HighSpeedTools.performHighSpeedChecks(w, p, c, tk);} catch (Throwable t) {}}
	@Override public EnumRailDirection getRailDirectionOverride(IBlockAccess w, BlockPos pos, IBlockState bs, EntityMinecart c) {return null;}
	@Override public void onEntityCollision(World w, BlockPos p, IBlockState bs, Entity e) {}
	@Override public float getMaxSpeed(World w, EntityMinecart c, BlockPos p) {return c.getEntityData().getBoolean("HighSpeed") ? 2.0F : 0.5F;}
}
