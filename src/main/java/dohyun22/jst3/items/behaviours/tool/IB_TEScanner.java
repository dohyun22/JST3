package dohyun22.jst3.items.behaviours.tool;

import java.util.List;
import java.util.Map;

import dohyun22.jst3.items.behaviours.ItemBehaviour;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.network.JSTPacketHandler;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import ic2.api.energy.EnergyNet;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class IB_TEScanner extends ItemBehaviour {

	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}

	@Override
	public EnumActionResult onUseFirst(ItemStack st, EntityPlayer pl, World w, BlockPos p, EnumFacing f, float hx, float hy, float hz, EnumHand h) {
		if (w.isRemote) return EnumActionResult.PASS;
		TileEntity te = w.getTileEntity(p);
		if (te != null) {
			String id = JSTUtils.getRegName(te);
			if (!id.isEmpty()) {
				w.playSound(null, pl.getPosition(), JSTSounds.SCAN, SoundCategory.PLAYERS, 1.0F, 1.0F);
				JSTUtils.sendSimpleMessage(pl, id.toString());
			}
		} else {
			ChunkPos cp = new ChunkPos(pl.getPosition());
			int all = 0, upd = 0, eng = 0, fl = 0;
			for (int x = -2; x <= 2; x++) {
				for (int z = -2; z <= 2; z++) {
					int cx = cp.x + x, cz = cp.z + z;
					if (w.isChunkGeneratedAt(cx, cz)) {
						Chunk c = w.getChunkFromChunkCoords(cx, cz);
						for (TileEntity te2 : c.getTileEntityMap().values()) {
							if (te2 == null) continue;
							all++;
							if (w.tickableTileEntities.contains(te2)) upd++;
							boolean flag = false;
							for (EnumFacing f2 : EnumFacing.VALUES) {
								if (te2.hasCapability(CapabilityEnergy.ENERGY, f2)) {
									eng++;
									flag = true;
									break;
								}
							}
							for (EnumFacing f2 : EnumFacing.VALUES) {
								if (te2.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f2)) {
									fl++;
									break;
								}
							}
							if (!flag && JSTCfg.ic2Loaded) {
								try {
									if (EnergyNet.instance.getSubTile(w, te2.getPos()) != null) eng++;
								} catch (Throwable t) {}
							}
						}
					}
				}
			}
			w.playSound(null, pl.getPosition(), JSTSounds.SCAN, SoundCategory.PLAYERS, 1.0F, 1.0F);
			JSTPacketHandler.playCustomEffect(w, pl.getPosition(), 3, 0);
			JSTUtils.sendMessage(pl, "jst.msg.scan.testat.1");
			JSTUtils.sendMessage(pl, "jst.msg.scan.testat.2", all, upd);
			JSTUtils.sendMessage(pl, "jst.msg.scan.testat.3", eng, fl);
		}
		return EnumActionResult.SUCCESS;
	}

	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		ls.add(I18n.format("jst.tooltip.tescan"));
	}
}
