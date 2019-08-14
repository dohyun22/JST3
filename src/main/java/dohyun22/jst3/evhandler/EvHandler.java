package dohyun22.jst3.evhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.compat.ic2.CompatIC2;
import dohyun22.jst3.container.ContainerAdvChest;
import dohyun22.jst3.entity.EntityPrimedOre;
import dohyun22.jst3.items.ItemJST1;
import dohyun22.jst3.items.ItemMetaBase;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.loader.RecipeLoader;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTChunkData;
import dohyun22.jst3.utils.JSTDamageSource;
import dohyun22.jst3.utils.JSTDamageSource.EnumHazard;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import ic2.api.crops.CropCard;
import ic2.api.crops.ICropTile;
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
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ForgeEventFactory;
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
	public static final Set<Block> NetherOreList = new HashSet();
	public static final Set<Block> EndOreList = new HashSet();
	private static final String TAG_NAME = "JST3_CD";
	private static boolean err;
	
	public static void init() {
		NetherOreList.add(JSTBlocks.blockNO);
		NetherOreList.add(Blocks.QUARTZ_ORE);
		EndOreList.add(JSTBlocks.blockEO);
		if (JSTCfg.fineDust)
			MinecraftForge.EVENT_BUS.register(new DustHandler());
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
				pl.sendMessage(new TextComponentString("\u00A7cYou can't edit blocks on the top of the nether!"));
				return;
			}
			
			if (!(pl instanceof FakePlayer) && !pl.capabilities.isCreativeMode) {
				if (JSTCfg.ECnc > 0 && NetherOreList.contains(w.getBlockState(ev.getPos()).getBlock())) {
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

								if (!NetherOreList.contains(lb) || occ)
									continue;

								int exc = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, pl.getHeldItemMainhand()) > 0 ? JSTCfg.ECncST : JSTCfg.ECnc;
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
			if (!(pl instanceof FakePlayer) && !pl.capabilities.isCreativeMode && EndOreList.contains(w.getBlockState(ev.getPos()).getBlock())) {
				int exc = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, pl.getHeldItemMainhand()) > 0 ? 800 : 10;
				if (exc > 0 && w.rand.nextInt(exc) == 0) {
					EntityEndermite em = new EntityEndermite(w);
					em.setSpawnedByPlayer(true);
					em.setLocationAndAngles(ev.getPos().getX() + 0.5F, ev.getPos().getY() + 0.2F, ev.getPos().getZ() + 0.5F, ev.getWorld().rand.nextFloat() * 360.0F, 0.0F);
					w.spawnEntity(em);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPlace(PlaceEvent ev) {
		World w = ev.getWorld();
		if (ev.isCanceled()) return;
		EntityPlayer pl = ev.getPlayer();
		BlockPos p = ev.getPos();
		if (JSTCfg.PNT && w.provider.getDimension() == -1 && ev.getPos().getY() > 127 && pl != null && !pl.capabilities.isCreativeMode) {
			ev.setCanceled(true);
			pl.sendMessage(new TextComponentString("\u00A7cYou can't edit blocks on the top of the nether!"));
			return;
		}
	}
	
	@SubscribeEvent
	public void onRightClick(PlayerInteractEvent.RightClickBlock ev) {
		World w = ev.getWorld();
		ItemStack st = ev.getItemStack();
		String name = JSTUtils.getRegName(st);
		
		if (!st.isEmpty()) {
			if (!w.isRemote && JSTCfg.DVEP && st.getItem() == Items.ENDER_EYE && w.getBlockState(ev.getPos()).getBlock() == Blocks.END_PORTAL_FRAME) {
				ev.setCanceled(true);
				return;
			}
		}
			
		if (JSTCfg.RIC2C) {
			if (ev.getHand() == EnumHand.OFF_HAND) {
				ItemStack st2 = ev.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
				if (!st2.isEmpty() && "ic2:cable".equals(name)) {
					ev.setCanceled(true);
					return;
				}
			}
			if (!st.isEmpty() && "ic2:cable".equals(name)) {
				ev.getEntityPlayer().swingArm(ev.getHand());
				ev.setCanceled(true);
				ev.setCancellationResult(EnumActionResult.SUCCESS);
				if (!w.isRemote) {
					byte ins = st.hasTagCompound() ? st.getTagCompound().getByte("insulation") : 0;
					boolean flag = ins >= getMaxIns((byte) st.getMetadata());
					int mte = getJSTMeta((byte) st.getMetadata(), flag);
					if (mte != 0) {
						BlockPos p = ev.getPos();
						Block b = w.getBlockState(p).getBlock();
						if (!b.isReplaceable(w, p))
							p = p.offset(ev.getFace());
							
						if (b.isReplaceable(w, p) && ev.getEntityPlayer().canPlayerEdit(p, ev.getFace(), st)) {
							boolean flag2 = false;
							BlockSnapshot bn = new BlockSnapshot(w, p, w.getBlockState(p));
							if (w.setBlockState(p, JSTBlocks.blockTile.getDefaultState(), 3)) {
								TileEntityMeta tem = (TileEntityMeta) w.getTileEntity(p);
								if (tem != null)
									tem.createNewMetatileEntity(mte);
								if (ForgeEventFactory.onPlayerBlockPlace(ev.getEntityPlayer(), bn, ev.getFace(), ev.getHand()).isCanceled()) {
									bn.restore(true, false);
								} else {
									w.notifyNeighborsOfStateChange(p, w.getBlockState(p).getBlock(), false);
									if (tem.hasValidMTE())
										tem.mte.onPlaced(p, w.getBlockState(p), ev.getEntityPlayer(), st);
									flag2 = true;
								}
							}
		
							if (flag2) {
								SoundType sn = w.getBlockState(p).getBlock().getSoundType(w.getBlockState(p), w, p, ev.getEntityPlayer());
								w.playSound(null, p, sn.getPlaceSound(), SoundCategory.BLOCKS, (sn.getVolume() + 1.0F) / 2.0F, sn.getPitch() * 0.8F);
								if (!ev.getEntityPlayer().capabilities.isCreativeMode) {
									Item it = JSTUtils.getModItem("ic2:crafting");
									if (!flag && ins > 0 && it != null)
										ev.getEntityPlayer().addItemStackToInventory(new ItemStack(it, ins));
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
						ev.getEntityPlayer().swingArm(ev.getHand());
						if (!ev.getEntityPlayer().capabilities.isCreativeMode)
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
			if (!ev.getEntityPlayer().capabilities.isCreativeMode && st != null && st.getUnlocalizedName().toLowerCase().contains("ichorpickgem") &&
				b.getBlockHardness(ev.getWorld(), ev.getPos()) < 0.0F)
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
	
	public static void addOre(Block b, boolean end) {
		if (b == null || b == Blocks.AIR) return;
		if (end)
			EndOreList.add(b);
		else
			NetherOreList.add(b);
	}
	
	@SubscribeEvent
	public void onChunkDataLoad(ChunkDataEvent.Load ev) {
		if (ev.getWorld().isRemote) return;
		if (ev.getData().hasKey(TAG_NAME, Constants.NBT.TAG_COMPOUND)) {
			NBTTagCompound tag = ev.getData().getCompoundTag(TAG_NAME);
			if (!tag.hasNoTags()) {
				if (tag.getInteger(JSTChunkData.DUST_TAG_NAME) > 0)
					DustHandler.addToTracker(ev.getWorld(), ev.getChunk().getPos(), true);
				JSTChunkData.setChunkData(ev.getWorld(), ev.getChunk().getPos(), tag, false);
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
				if (s.contains("electric") || s.contains("flux")) {
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
			if ((JSTCfg.NerfZombies >> 1 & 1) == 1) {
				if (e.isChild())
					return true;
			} else if ((JSTCfg.NerfZombies & 1) == 1) {
				if (e.isChild()) {
					e.setHealth(10.0F);
					e.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0F);
					e.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.115F);
					e.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0F);
				}
			}
			if ((JSTCfg.NerfZombies >> 2 & 1) == 1)
				e.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0F);
		}
		return false;
	}

	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent ev) {
		Entity e = ev.getEntity();
		World w = e.getEntityWorld();
		if (!w.isRemote && (JSTCfg.NerfZombies & 1) == 1 && w.isDaytime() && e instanceof EntityZombie && !(e instanceof EntityHusk) && ((EntityZombie)e).isChild()) {
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
			try {
				DustHandler.update(ev.world, ev.world.getTotalWorldTime());
			} catch (Exception e) {
				if (!err) {
					JSTUtils.LOG.error("Error ticking pollution");
					JSTUtils.LOG.catching(e);
					err = true;
				}
			}
		}
	}

    @SubscribeEvent
    public void onLoad(final WorldEvent.Load ev) {
        if (JSTCfg.fireFineDust) ev.getWorld().addEventListener(FireEvListener.INSTANCE);
    }

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload ev) {
		if (!ev.getWorld().isRemote && ev.getWorld().provider.getDimension() == 0 && !ev.getWorld().getMinecraftServer().isServerRunning()) {
			DustHandler.resetTracker();
			err = false;
		}
	}
}
