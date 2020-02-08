package dohyun22.jst3.evhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.blocks.BlockTileEntity;
import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.compat.ic2.CompatIC2;
import dohyun22.jst3.container.ContainerAdvChest;
import dohyun22.jst3.entity.EntityPrimedOre;
import dohyun22.jst3.items.ItemJST1;
import dohyun22.jst3.items.ItemMetaBase;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.loader.RecipeLoader;
import dohyun22.jst3.network.JSTPacketHandler;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.EffectBlocks;
import dohyun22.jst3.utils.JSTChunkData;
import dohyun22.jst3.utils.JSTDamageSource;
import dohyun22.jst3.utils.JSTDamageSource.EnumHazard;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import ic2.api.crops.CropCard;
import ic2.api.crops.ICropTile;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergyTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRail;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.ForgeRegistry;

public class EvHandler {
	private static final HashMap<Integer, ArrayList<BlockPos>> toBeUnloaded = new HashMap();
	private static final String TAG_NAME = "JST3_CD";
	private static boolean err;
	
	public static void init() {
		EffectBlocks.netherOres.add(JSTBlocks.blockNO);
		EffectBlocks.netherOres.add(Blocks.QUARTZ_ORE);
		EffectBlocks.endOres.add(JSTBlocks.blockEO);
		if (JSTCfg.fineDust) MinecraftForge.EVENT_BUS.register(new DustHandler());
	}
	
	@SubscribeEvent
	public void onBreak (BreakEvent ev) {
		if (ev.getWorld().isRemote)
			return;
		World w = ev.getWorld();
		EntityPlayer pl = ev.getPlayer();
		
		if (!pl.capabilities.isCreativeMode && !pl.getHeldItemMainhand().isEmpty() && pl.getHeldItemMainhand().getUnlocalizedName().toLowerCase().contains("ichorpickgem") &&
			w.getBlockState(ev.getPos()).getBlockHardness(w, ev.getPos()) < 0.0F) {
			ev.setCanceled(true);
			return;
		}
		
		if (w.provider.getDimension() == -1) {
			if (JSTCfg.PNT && ev.getPos().getY() > 127 && pl != null && !pl.capabilities.isCreativeMode) {
				ev.setCanceled(true);
				JSTUtils.sendSimpleMessage(pl, "\u00A7cYou can't edit blocks on the top of the nether!");
				return;
			}
			
			if (!(pl instanceof FakePlayer) && !pl.capabilities.isCreativeMode) {
				if (JSTCfg.eCnc > 0 && EffectBlocks.netherOres.contains(w.getBlockState(ev.getPos()).getBlock())) {
					int en = 0;
					for (int dx = -1; dx <= 1; dx++) {
						for (int dy = -1; dy <= 1; dy++) {
							for (int dz = -1; dz <= 1; dz++) {
								if ((dx | dy | dz) == 0)
									continue;

								int bx = ev.getPos().getX() + dx;
								int by = ev.getPos().getY() + dy;
								int bz = ev.getPos().getZ() + dz;
						
								Block lb = w.getBlockState(new BlockPos(bx, by, bz)).getBlock();
								if (lb == null)
								continue;

								boolean occ = false;
								List<Entity> el = w.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB((double)bx, (double)by, (double)bz, (double)(bx + 1), (double)(by + 1), (double)(bz + 1)));
								for (Entity e: el) {
									if (e instanceof EntityPrimedOre) {
										occ = true;
										break;
									}
								}

								if (!EffectBlocks.netherOres.contains(lb) || occ)
									continue;

								int exc = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, pl.getHeldItemMainhand()) > 0 ? JSTCfg.eCncST : JSTCfg.eCnc;
								if (exc > 0 && en < 2 && w.rand.nextInt(exc) == 0) {
									en++;
									EntityPrimedOre po = new EntityPrimedOre(w, bx + 0.5, by + 0.5, bz + 0.5);
									w.spawnEntity(po);
									w.playSound((EntityPlayer)null, po.posX, po.posY, po.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
								}
							}
						}
					}
				}
			}
		} else if (w.provider.getDimension() == 1) {
			if (!(pl instanceof FakePlayer) && !pl.capabilities.isCreativeMode && EffectBlocks.endOres.contains(w.getBlockState(ev.getPos()).getBlock())) {
				int exc = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, pl.getHeldItemMainhand()) > 0 ? 800 : 10;
				if (exc > 0 && w.rand.nextInt(exc) == 0) {
					EntityEndermite em = new EntityEndermite(w);
					em.setSpawnedByPlayer(true);
					em.setLocationAndAngles(ev.getPos().getX() + 0.5F, ev.getPos().getY() + 0.2F, ev.getPos().getZ() + 0.5F, ev.getWorld().rand.nextFloat() * 360.0F, 0.0F);
					w.spawnEntity(em);
				}
			}
		}
		/*if (w.getBlockState(ev.getPos().down()).getBlock() == Blocks.SPONGE) {
			ev.setCanceled(true);
			pl.sendMessage(new TextComponentString("Test"));
		}*/
	}

	@SubscribeEvent
	public void onPlace(PlaceEvent ev) {
		World w = ev.getWorld();
		if (ev.isCanceled()) return;
		Entity e = ev.getPlayer();
		BlockPos p = ev.getPos();
		if (JSTCfg.PNT && w.provider.getDimension() == -1 && ev.getPos().getY() > 127 && e != null && !(e instanceof EntityPlayer && ((EntityPlayer)e).isCreative())) {
			ev.setCanceled(true);
			JSTUtils.sendSimpleMessage(e, "\u00A7cYou can't edit blocks on the top of the nether!");
			return;
		}
		/*if (w.getBlockState(p.down()).getBlock() == Blocks.SPONGE) {
			ev.setCanceled(true);
			JSTUtils.sendSimpleMessage(e, "Test");
		}*/
	}

	@SubscribeEvent
	public void onRightClick(PlayerInteractEvent.RightClickBlock ev) {
		World w = ev.getWorld();
		ItemStack st = ev.getItemStack();
		String name = JSTUtils.getRegName(st);
		EntityPlayer pl = ev.getEntityPlayer();

		if (!st.isEmpty()) {
			if (!w.isRemote && JSTCfg.DVEP && st.getItem() == Items.ENDER_EYE && w.getBlockState(ev.getPos()).getBlock() == Blocks.END_PORTAL_FRAME) {
				ev.setCanceled(true);
				return;
			}
		}

		if (JSTCfg.rIC2C) {
			if (ev.getHand() == EnumHand.OFF_HAND) {
				ItemStack st2 = pl.getHeldItem(EnumHand.MAIN_HAND);
				if (!st2.isEmpty() && "ic2:cable".equals(name)) {
					ev.setCanceled(true);
					return;
				}
			}
			if (!st.isEmpty() && "ic2:cable".equals(name)) {
				pl.swingArm(ev.getHand());
				ev.setCanceled(true);
				ev.setCancellationResult(pl instanceof FakePlayer ? EnumActionResult.FAIL : EnumActionResult.SUCCESS);
				if (!w.isRemote && !(pl instanceof FakePlayer)) {
					byte ins = st.hasTagCompound() ? st.getTagCompound().getByte("insulation") : 0;
					boolean flag = ins >= getMaxIns((byte) st.getMetadata());
					int mte = getJSTMeta((byte) st.getMetadata(), flag);
					if (mte != 0) {
						BlockPos p = ev.getPos();
						Block b = w.getBlockState(p).getBlock();
						if (!b.isReplaceable(w, p))
							p = p.offset(ev.getFace());
							
						if (b.isReplaceable(w, p) && pl.canPlayerEdit(p, ev.getFace(), st)) {
							boolean flag2 = false;
							BlockSnapshot bn = new BlockSnapshot(w, p, w.getBlockState(p));
							if (w.setBlockState(p, JSTBlocks.blockTile.getDefaultState(), 3)) {
								TileEntityMeta tem = (TileEntityMeta) w.getTileEntity(p);
								if (tem != null) {
									tem.createNewMetatileEntity(mte);
									if (tem.hasValidMTE() && !tem.mte.isOpaque())
										BlockTileEntity.setState(w, p, false, 2);
								}
								if (ForgeEventFactory.onPlayerBlockPlace(pl, bn, ev.getFace(), ev.getHand()).isCanceled()) {
									bn.restore(true, false);
								} else {
									w.notifyNeighborsOfStateChange(p, w.getBlockState(p).getBlock(), false);
									if (tem.hasValidMTE())
										tem.mte.onPlaced(p, w.getBlockState(p), pl, st);
									flag2 = true;
								}
							}
		
							if (flag2) {
								SoundType sn = w.getBlockState(p).getBlock().getSoundType(w.getBlockState(p), w, p, pl);
								w.playSound(null, p, sn.getPlaceSound(), SoundCategory.BLOCKS, (sn.getVolume() + 1.0F) / 2.0F, sn.getPitch() * 0.8F);
								if (!pl.capabilities.isCreativeMode) {
									Item it = JSTUtils.getModItem("ic2:crafting");
									if (!flag && ins > 0 && it != null)
										pl.addItemStackToInventory(new ItemStack(it, ins));
									st.shrink(1);
								}
							}
						}
					}
				}
			}
		}
		if (!w.isRemote && JSTCfg.ic2Loaded && !st.isEmpty()) {
			TileEntity te = w.getTileEntity(ev.getPos());
			try {
				if (te instanceof ICropTile && MRecipes.isFertilizer(st)) {
					ICropTile cr = (ICropTile)te;
					CropCard cc = cr.getCrop();
					boolean flag = false;
					if (cr.getStorageNutrients() < 100) {
						cr.setStorageNutrients(cr.getStorageNutrients() + 100);
						flag  = true;
					} else if (CompatIC2.growCrop(te, 250 + w.rand.nextInt(50), true)) {
						w.playEvent(2005, ev.getPos(), 0);
						flag = true;
					}
					if (flag) {
						pl.swingArm(ev.getHand());
						if (!pl.capabilities.isCreativeMode)
							st.shrink(1);
					}
					ev.setCanceled(true);
					ev.setCancellationResult(EnumActionResult.SUCCESS);
				}
			} catch (Throwable t) {}
		}
	}

	@SubscribeEvent
	public void onLeftClick(PlayerInteractEvent.LeftClickBlock ev) {
		if (ev.isCanceled() || ev.getWorld().isRemote) return;
		ItemStack st = ev.getEntityPlayer().getHeldItemMainhand();
		IBlockState b = (ev.getEntityPlayer().world.getBlockState(ev.getPos()));
		if (st != null && st.getItem() instanceof ItemJST1)
			((ItemJST1)st.getItem()).getBehaviour(st).onHitBlock(st, ev.getPos(), ev.getEntityPlayer());
		try {
			if (!ev.getEntityPlayer().capabilities.isCreativeMode && st != null && st.getUnlocalizedName().toLowerCase().contains("ichorpickgem") && b.getBlockHardness(ev.getWorld(), ev.getPos()) < 0.0F)
				ev.setCanceled(true);
		} catch (Exception e) {}
	}

	public static int getJSTMeta(byte t, boolean ins) {
		int m = 0;
		switch (t) {
		// copper, glass, gold, iron, tin, detector, switch
		case 0:
			m = ins ? 4003 : 4004;
			break;
		case 1:
			m = 4009;
			break;
		case 2:
			m = ins ? 4005 : 4006;
			break;
		case 3:
			m = ins ? 4007 : 4008;
			break;
		case 4:
			m = ins ? 4001 : 4002;
			break;
		case 5:
			m = 4011;
			break;
		case 6:
			m = 4010;
			break;
		default:
		}
		return m;
	}

	public static byte getMaxIns(byte t) {
		switch (t) {
		// copper, glass, gold, iron, tin, detector, switch
		case 0:
			return 1;
		case 2:
			return 2;
		case 3:
			return 3;
		case 4:
			return 1;
		default:
			return 0;
		}
	}

	@SubscribeEvent
	public void onEntityJoin(EntityJoinWorldEvent ev) {
		if (nerf(ev.getEntity()))
			ev.setCanceled(true);
	}

	@SubscribeEvent
	public void onCheckSpawn(LivingSpawnEvent.CheckSpawn ev) {
		if (nerf(ev.getEntityLiving()))
			ev.setResult(Result.DENY);
	}

	@SubscribeEvent
	public void onDamage(LivingDamageEvent ev) {
		DamageSource ds = ev.getSource();
		if (ds != null) {
			String s = ds.getDamageType();
			if (s != null) {
				s = s.toLowerCase();
				if (s.contains("electric") || s.contains("flux") || s.startsWith("ierazorshock") || s.startsWith("iewireshock")) {
					if (JSTDamageSource.hasFullHazmat(EnumHazard.ELECTRIC, ev.getEntityLiving()))
						ev.setCanceled(true);
				} else if (s.startsWith("radiation") && JSTDamageSource.hasFullHazmat(EnumHazard.RADIO, ev.getEntityLiving()))
					ev.setCanceled(true);
			}
		}
	}

	private static boolean nerf(Entity i) {
		if (i instanceof EntityZombie) {
			EntityZombie e = (EntityZombie) i;
			if ((JSTCfg.nerfZombies >> 1 & 1) == 1) {
				if (e.isChild())
					return true;
			} else if ((JSTCfg.nerfZombies & 1) == 1) {
				if (e.isChild()) {
					e.setHealth(10.0F);
					e.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0F);
					e.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.115F);
					e.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0F);
				}
			}
			if ((JSTCfg.nerfZombies >> 2 & 1) == 1)
				e.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0F);
		}
		return false;
	}

	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent ev) {
		Entity e = ev.getEntity();
		World w = e.getEntityWorld();
		if (!w.isRemote && (JSTCfg.nerfZombies & 1) == 1 && w.isDaytime() && e instanceof EntityZombie && !(e instanceof EntityHusk) && ((EntityZombie)e).isChild()) {
			EntityZombie ez = (EntityZombie) e;
			float f = ez.getBrightness();
			if (f > 0.5F && w.rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && w.canSeeSky(new BlockPos(ez.posX, ez.posY + ez.getEyeHeight(), ez.posZ))) {
				boolean flag = true;
				ItemStack st = ez.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
				if (!st.isEmpty()) {
					if (st.isItemStackDamageable()) {
						st.setItemDamage(st.getItemDamage() + w.rand.nextInt(2));
						if (st.getItemDamage() >= st.getMaxDamage())
							ez.setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemStack.EMPTY);
					}
					flag = false;
				}
				if (flag) ez.setFire(8);
			}
		}
	}
	
	@SubscribeEvent
	public void onNoteBlock(NoteBlockEvent.Play ev) {
		IBlockState bs = ev.getWorld().getBlockState(ev.getPos().down());
		if (bs.getBlock() == Blocks.RAIL) {
			SoundEvent sev = null;
			switch ((BlockRailBase.EnumRailDirection)bs.getValue(BlockRail.SHAPE)) {
			case ASCENDING_EAST:
			case ASCENDING_WEST:
			case EAST_WEST:
				sev = JSTSounds.UPLINE;
				break;
			case ASCENDING_NORTH:
			case ASCENDING_SOUTH:
			case NORTH_SOUTH:
				sev = JSTSounds.DOWNLINE;
				break;
			default:
			}
			if (sev != null) {
				ev.getWorld().playSound(null, ev.getPos(), sev, SoundCategory.RECORDS, 3.0F, 1.0F);
				ev.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent ev) {
		if (JSTCfg.fineDust && ev.phase == TickEvent.Phase.END) {
			long t = ev.world.getTotalWorldTime();
			if (t % 10 == 0) {
				int d = ev.world.provider.getDimension();
				ArrayList<BlockPos> pos = toBeUnloaded.get(d);
				if (pos != null) {
					for (BlockPos p : pos) {
						TileEntity te = ev.world.getTileEntity(p);
						if (te != null)
							ev.world.tickableTileEntities.remove(te);
						else
							JSTChunkData.setBrokenMachine(ev.world, p, true);
					}
					toBeUnloaded.remove(d);
				}
			}
			try {
				DustHandler.update(ev.world, t);
			} catch (Exception e) {
				if (!err) {
					JSTUtils.LOG.error("Error ticking fine dust");
					JSTUtils.LOG.catching(e);
					err = true;
				}
			}
		}
	}

    @SubscribeEvent
    public void onLoad(WorldEvent.Load ev) {
        ev.getWorld().addEventListener(WorldEvListener.INSTANCE);
    }

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload ev) {
		if (!ev.getWorld().isRemote && ev.getWorld().provider.getDimension() == 0 && !ev.getWorld().getMinecraftServer().isServerRunning()) {
			DustHandler.clear();
			JSTChunkData.clear();
			toBeUnloaded.clear();
			err = false;
		}
	}

	@SubscribeEvent
	public void onChunkDataLoad(ChunkDataEvent.Load ev) {
		World w = ev.getWorld();
		if (w.isRemote) return;
		if (ev.getData().hasKey(TAG_NAME, Constants.NBT.TAG_COMPOUND)) {
			NBTTagCompound tag = ev.getData().getCompoundTag(TAG_NAME);
			if (!tag.hasNoTags()) {
				ChunkPos cp = ev.getChunk().getPos();
				JSTChunkData.setChunkData(w, cp, tag, false);
				if (JSTChunkData.getFineDust(w, cp) > 0)
					DustHandler.addToTracker(w, cp, true);
				List<BlockPos> ls = JSTChunkData.getBrokenMachines(w, cp);
				for (BlockPos p : ls)
					scheduleTEUnload(w, p);
			}
		}
	}

	@SubscribeEvent
	public void onChunkDataSave(ChunkDataEvent.Save ev) {
	    if (ev.getWorld().isRemote) return;
		NBTTagCompound tag = JSTChunkData.getChunkData(ev.getWorld(), ev.getChunk().getPos());
		if (tag == null || tag.hasNoTags()) {
			if (ev.getData().hasKey(TAG_NAME))
				ev.getData().removeTag(TAG_NAME);
		} else {
			ev.getData().setTag(TAG_NAME, tag);
		}
	}

	@SubscribeEvent
	public void onSoundMissing(RegistryEvent.MissingMappings<SoundEvent> ev) {
		for (RegistryEvent.MissingMappings.Mapping<SoundEvent> o : ev.getMappings())
			if (JustServerTweak.MODID.equals(o.key.getResourceDomain()))
				o.ignore();
	}

	public static boolean makeMachineGoHaywire(World w, BlockPos p) {
		if (!w.isBlockLoaded(p)) return false;
		TileEntity te = w.getTileEntity(p);
		if (te == null) return false;
		boolean flag = false;
		for (EnumFacing f : EnumFacing.VALUES) {
			IEnergyStorage c = te.getCapability(CapabilityEnergy.ENERGY, f);
			if (c != null && c.getEnergyStored() > 0) flag = true;
		}
		try {
			if (!flag && JSTCfg.ic2Loaded && EnergyNet.instance.getSubTile(w, p) != null)
				flag = true;
		} catch (Throwable t) {}
		if (flag) {
			scheduleTEUnload(w, p);
			JSTChunkData.setBrokenMachine(w, p, false);
		}
		return flag;
	}

	public static void scheduleTEUnload(World w, BlockPos p) {
		if (p == null) return;
		int d = w.provider.getDimension();
		ArrayList<BlockPos> ls = toBeUnloaded.get(d);
		if (ls == null) {
			ls = new ArrayList();
			toBeUnloaded.put(d, ls);
		}
		ls.add(p);
	}
}
