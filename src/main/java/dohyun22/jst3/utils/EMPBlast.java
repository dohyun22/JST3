package dohyun22.jst3.utils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import dohyun22.jst3.evhandler.EvHandler;
import dohyun22.jst3.network.JSTPacketHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityComparator;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EMPBlast {
	private World w;
	private BlockPos p;
	private boolean mob, block;
	private int r;

	public EMPBlast(World wi, BlockPos pi, int rad, boolean am, boolean ab) {
		w = wi; p = pi; r = rad; mob = am; block = ab;
	}

	public void doEMP() {
		if (!w.isRemote) {
			if (mob) {
				List<EntityLivingBase> ae = w.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(p.getX() - r, p.getY() - r, p.getZ() - r, p.getX() + r, p.getY() + r, p.getZ() + r));
				for (EntityLivingBase e : ae) {
					e.setAbsorptionAmount(0);
					if (JSTPotions.canEMP(e)) {
						e.attackEntityFrom(JSTDamageSource.EMP, 6);
						e.addPotionEffect(new PotionEffect(JSTPotions.emp, 1200));
						JSTPacketHandler.playCustomEffect(w, e.getPosition(), 1, 10);
					}
				}
			}
			if (block) {
				BlockPos p2;
				Random rn = w.rand;
				for (int x = -r; x <= r; x++)
					for (int y = -r; y <= r; y++)
						for (int z = -r; z <= r; z++) {
							p2 = new BlockPos(p.getX() + x, p.getY() + y, p.getZ() + z);
							boolean eff = false;
							if (EvHandler.makeMachineGoHaywire(w, p2)) {
								eff = true;
							} else {
								IBlockState bs = w.getBlockState(p2), bs2 = null;
								Block b = bs.getBlock();
								int m = b.getMetaFromState(bs);
								if (rn.nextBoolean()) {
									if (b == Blocks.REDSTONE_WIRE)
										bs2 = b.getStateFromMeta(m == 0 ? 15 : 0);
									else if (b == Blocks.REDSTONE_TORCH) {
										w.setBlockState(p2, Blocks.UNLIT_REDSTONE_TORCH.getStateFromMeta(m), 3);
										try {
											Method me = ReflectionUtils.getMethodObf(BlockRedstoneTorch.class, new String[] {"func_176598_a", "isBurnedOut"}, World.class, BlockPos.class, Boolean.TYPE);
											for (int n = 0; n < 8; n++)
												if ((boolean)me.invoke(Blocks.REDSTONE_TORCH, w, p2, true)) break;
											w.playSound(null, p2, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.5F, 2.6F + (w.rand.nextFloat() - w.rand.nextFloat()) * 0.8F);
										} catch (Throwable t) {
											t.printStackTrace();
										}
										if (rn.nextBoolean()) eff = true;
									} else if (b == Blocks.UNLIT_REDSTONE_TORCH)
										bs2 = Blocks.REDSTONE_TORCH.getStateFromMeta(m);
									else if (b == Blocks.POWERED_REPEATER)
										bs2 = Blocks.UNPOWERED_REPEATER.getStateFromMeta(m);
									else if (b == Blocks.UNPOWERED_REPEATER)
										bs2 = Blocks.POWERED_REPEATER.getStateFromMeta(m);
									else if (b == Blocks.POWERED_COMPARATOR || b == Blocks.UNPOWERED_COMPARATOR) {
										boolean flag = rn.nextBoolean() ? rn.nextBoolean() : (m & 8) > 0, mode = rn.nextBoolean() ? rn.nextBoolean() : (m & 4) > 0;
										m = (m & 3) | (flag ? 8 : 0) | (mode ? 4 : 0);
										w.setBlockState(p2, b.getStateFromMeta(m), 2); bs2 = null;
										TileEntity te = w.getTileEntity(p2);
										if (te instanceof TileEntityComparator)
											((TileEntityComparator)te).setOutputSignal(rn.nextInt(16));
										if (rn.nextBoolean()) eff = true;
									} else if (b == Blocks.PISTON || b == Blocks.STICKY_PISTON) {
										boolean flag = ((m >> 3) & 1) > 0, flg2 = b != Blocks.PISTON;
										EnumFacing f = JSTUtils.getFacingFromNum(m & 7);
										BlockPos o = p2.offset(f);
										if (f != null) {
											if (flag) {
												w.setBlockState(p2, b.getStateFromMeta(m ^ 8), 2);
												w.setBlockState(o, Blocks.AIR.getDefaultState(), 2);
											} else if (w.isAirBlock(o)) {
												w.setBlockState(p2, b.getStateFromMeta(m ^ 8), 2);
												w.setBlockState(o, Blocks.PISTON_HEAD.getStateFromMeta(f.getIndex() | (flg2 ? 8 : 0)), 2);
											}
											w.playSound(null, p2, flag ? SoundEvents.BLOCK_PISTON_CONTRACT : SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 1.0F, 1.0F);
										}
										if (rn.nextBoolean()) eff = true;
									}
								}
								if (bs2 != null) {
									w.setBlockState(p2, bs2, rn.nextBoolean() ? 2 : 3);
									if (rn.nextBoolean()) eff = true;
								}
							}
							if (eff) JSTPacketHandler.playCustomEffect(w, p2, 1, 10);
						}
			}
			JSTPacketHandler.playCustomEffect(w, p, 4, (int)(r * 1.5F));
			w.playSound(null, p, JSTSounds.OVERLOAD, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
	}
}
