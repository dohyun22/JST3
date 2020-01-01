package dohyun22.jst3.tiles.energy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.recipe.OreDictStack;
import dohyun22.jst3.client.gui.GUIMagicGen;
import dohyun22.jst3.container.ContainerMagicGen;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;

public class MetaTileMagicGenerator extends MetaTileGenerator {
	public int fuelLeft, fuelValue;
	public boolean collectEndCrystal;
	private EntityEnderCrystal crystal;
	@Nullable
	private BlockPos crystalPos;

	public MetaTileMagicGenerator(int tier) {
		super(tier, true);
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MetaTileMagicGenerator(tier);
	}

	@Override
	protected void checkCanGenerate() {
		boolean flag = false;
		if (collectEndCrystal) {
			boolean flag2 = false;
			if (crystal == null) {
				if (crystalPos != null) {
					List<EntityEnderCrystal> ls = getWorld().getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(crystalPos));
					if (ls != null && !ls.isEmpty())
						crystal = ls.get(0);
					else
						flag2 = true;
				} else if (baseTile.getTimer() % 20 == 0) {
					BlockPos p = getPos();
					List<EntityEnderCrystal> ls = getWorld().getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(p.add(-32, -32, -32), p.add(32, 32, 32)));
					if (ls != null && !ls.isEmpty()) {
						for (EntityEnderCrystal e : ls) {
							if (e != null && e.isEntityAlive() && e.getBeamTarget() == null) {
								crystal = e;
								crystalPos = new BlockPos(e.posX, e.posY, e.posZ);
								break;
							}
						}
					}
				}
			} else if (crystal.isEntityAlive()) {
				flag = true;
				if (crystal.getBeamTarget() == null) {
					getWorld().playSound(null, getPos(), JSTSounds.MICRO, SoundCategory.BLOCKS, 1.0F, 1.0F);
					crystal.setBeamTarget(getPos().down(2));
				} else if (!getPos().equals(crystal.getBeamTarget().up(2))) {
					flag2 = true;
				}
			} else {
				flag2 = true;
			}
			if (flag2) {
				getWorld().playSound(null, getPos(), JSTSounds.INTERRUPT, SoundCategory.BLOCKS, 1.0F, 1.0F);
				crystal = null;
				crystalPos = null;
				flag = false;
			}
		} else {
			if (fuelLeft > 0.0D)
				flag = true;
			if (crystal != null) {
				getWorld().playSound(null, getPos(), JSTSounds.INTERRUPT, SoundCategory.BLOCKS, 1.0F, 1.0F);
				crystal.setBeamTarget(null);
				crystal = null;
				crystalPos = null;
			}
		}
		baseTile.setActive(flag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		fuelLeft = tag.getInteger("fuel");
		fuelValue = tag.getInteger("fuelval");
		collectEndCrystal = tag.getBoolean("CEC");
		crystalPos = NBTUtil.getPosFromTag(tag.getCompoundTag("cpos"));
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("fuel", fuelLeft);
		tag.setInteger("fuelval", fuelValue);
		tag.setBoolean("CEC", collectEndCrystal);
		if (crystalPos != null)
			tag.setTag("cpos", NBTUtil.createPosTag(crystalPos));
	}

	@Override
	protected void doGenerate() {
		if (collectEndCrystal && crystal != null) {
			baseTile.energy += Math.min(maxEUTransfer() / 2, 128);
		} else {
			int use = Math.min(fuelLeft, maxEUTransfer());
			fuelLeft -= use;
			baseTile.energy += use;
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (crystal != null)
			crystal.setBeamTarget(null);
	}

	@Override
	public int maxEUTransfer() {
		return JSTUtils.getVoltFromTier(tier);
	}

	/** 0=ItemInput 1=Output 2=Charge */
	@Override
	public int getInvSize() {
		return 3;
	}

	@Override
	public void onPreTick() {
		if (isClient())
			return;
		ItemStack in = inv.get(0);
		if (fuelLeft <= 0) {
			if (!in.isEmpty()) {
				int fv = MRecipes.getValueInMap(in, MRecipes.MagicGenFuel);
				if (fv > 0) {
					fuelLeft = fv;
					fuelValue = fv;
					in.shrink(1);
				} else {
					ItemStack out = inv.get(1);
					if (out.isEmpty()) {
						Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(in);
						if (map != null && !map.isEmpty()) {
							int val = 0;
							for (Entry<Enchantment, Integer> e : map.entrySet()) {
								if (e == null || e.getKey() == null || e.getValue() == null)
									continue;
								if (in.getItem() == Items.ENCHANTED_BOOK
										|| in.getItem().getItemEnchantability(in) > 0) {
									int lvl = e.getValue().intValue();
									int mult;
									switch (e.getKey().getRarity()) {
									case RARE:
										mult = 50000;
										break;
									case UNCOMMON:
										mult = 75000;
										break;
									case VERY_RARE:
										mult = 100000;
										break;
									default:
										mult = 25000;
									}
									val += lvl * mult;
								}
							}

							if (val > 0) {
								ItemStack ret;
								if (in.getItem() == Items.ENCHANTED_BOOK) {
									ret = new ItemStack(Items.BOOK);
								} else {
									if (in.hasTagCompound()) {
										NBTTagCompound tag = in.getTagCompound();
										tag.removeTag("ench");
										if (tag.hasNoTags())
											in.setTagCompound(null);
									}
									ret = in;
								}
								inv.set(0, ItemStack.EMPTY);
								if (inv.get(1).isEmpty())
									inv.set(1, ret);

								fuelLeft = val;
								fuelValue = val;
							}
						}
					}
				}
			} else if (JSTCfg.tcLoaded) {
				for (EnumFacing f : EnumFacing.VALUES) {
					TileEntity te = getWorld().getTileEntity(getPos().offset(f));
					if (te == null)
						continue;
					try {
						if (te instanceof IAspectContainer) {
							AspectList al = ((IAspectContainer) te).getAspects();
							for (Entry<Aspect, Integer> as : al.aspects.entrySet()) {
								int am = Math.min(maxEUTransfer() / 4, as.getValue());
								if (((IAspectContainer) te).takeFromContainer(as.getKey(), am)) {
									am *= 1000;
									fuelLeft = am;
									fuelValue = am;
								}
							}
						}
					} catch (Throwable t) {
						break;
					}
				}
			}
		}
		in = inv.get(2);
		if (!in.isEmpty() && baseTile.energy > 0L)
			baseTile.energy -= JSTUtils.chargeItem(in, Math.min(baseTile.energy, maxEUTransfer()), tier, false, false);
	}

	@Override
	public void onPostTick() {
		super.onPostTick();
		if (!isClient() || !baseTile.isActive())
			return;

		World w = getWorld();
		if (w.rand.nextInt(40) == 0)
			w.playSound(baseTile.getPos().getX() + 0.5D, baseTile.getPos().getY(), baseTile.getPos().getZ() + 0.5D, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 0.75F + w.rand.nextFloat() * 0.5F, 2.0F, false);

		if (w.rand.nextInt(8) == 0) {
			for (int i = 0; i < 3; ++i) {
				int r1 = w.rand.nextInt(2) * 2 - 1;
				int r2 = w.rand.nextInt(2) * 2 - 1;
				double dx = getPos().getX() + 0.5D + 0.25D * r1;
				double dy = getPos().getY() + w.rand.nextFloat();
				double dz = getPos().getZ() + 0.5D + 0.25D * r2;
				double sx = w.rand.nextFloat() * r1;
				double sz = w.rand.nextFloat() * r2;
				w.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, dx, dy, dz, sx, 0.1D, sz, new int[0]);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getInformation(ItemStack st, World w, List<String> ls, ITooltipFlag adv) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.magicgen"));
		if (JSTCfg.tcLoaded)
			ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.magicgen.tc"));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTETex("magicgen"), u = getTieredTex(tier);
		return new TextureAtlasSprite[] {u, u, t, t, t, t};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		String s = "magicgen";
		if (!baseTile.isActive()) s += "_off";
		TextureAtlasSprite t = getTETex(s), u = getTieredTex(tier);
		return new TextureAtlasSprite[] {u, u, t, t, t, t};
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY,
			float hitZ) {
		if (isClient())
			return true;
		pl.openGui(JustServerTweak.INSTANCE, 1, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
		return true;
	}

	@Override
	public boolean canExtractItem(int sl, ItemStack st, EnumFacing dir) {
		return sl == 1;
	}

	@Override
	public boolean isItemValidForSlot(int sl, ItemStack st) {
		if (sl == 0) {
			Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(st);
			return MRecipes.getValueInMap(st, MRecipes.MagicGenFuel) > 0 || (map != null && !map.isEmpty());
		}
		return false;
	}

	@Override
	public boolean canProvideEnergy() {
		return true;
	}

	@Override
	public boolean isEnergyOutput(EnumFacing side) {
		return true;
	}

	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		return new ContainerMagicGen(inv, te);
	}

	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		return new GUIMagicGen(new ContainerMagicGen(inv, te));
	}
}