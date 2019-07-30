package dohyun22.jst3.items.behaviours;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import dohyun22.jst3.api.FineDustCapability;
import dohyun22.jst3.api.IDust;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.interfaces.IScanSupport;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import ic2.api.crops.CropCard;
import ic2.api.crops.CropProperties;
import ic2.api.crops.Crops;
import ic2.api.crops.ICropTile;
import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorChamber;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class IB_Scanner extends ItemBehaviour {

	public IB_Scanner() {
		maxEnergy = 120000;
	}

	@Override
	public EnumActionResult onUseFirst(ItemStack st, EntityPlayer pl, World w, BlockPos p, EnumFacing f, float hx, float hy, float hz, EnumHand h) {
		if (w.isRemote)
			return EnumActionResult.PASS;

		ArrayList<ITextComponent> str = new ArrayList();
		int eu = doScan(str, pl, w, p, f);
		if (eu <= this.getEnergy(st)) {
			w.playSound(null, p, JSTSounds.SCAN, SoundCategory.BLOCKS, 0.8F, 1.0F);
			for (ITextComponent s : str) {
				pl.sendMessage(s);
			}
			if (!pl.capabilities.isCreativeMode) {
				this.setEnergy(st, this.getEnergy(st) - eu);
				this.chargeFromArmor(st, pl);
			}
		} else {
			JSTUtils.sendSimpleMessage(pl, TextFormatting.RED + "Not enough energy");
			return EnumActionResult.FAIL;
		}

		return EnumActionResult.SUCCESS;
	}

	private static int doScan(ArrayList<ITextComponent> list, EntityPlayer pl, World w, BlockPos p, EnumFacing f) {
		int eu = 100;
		TileEntity te = w.getTileEntity(p);
		IBlockState bs = w.getBlockState(p);

		list.add(new TextComponentString(TextFormatting.YELLOW + "[x: " + p.getX() + " y: " + p.getY() + " z: " + p.getZ() + " w: " + w.provider.getDimension() + "]"));
		Boolean err = false;
		try {
			addTranslation(list, "jst.msg.scan.data1", Block.REGISTRY.getNameForObject(bs.getBlock()), bs.getBlock().getMetaFromState(bs));
			float h = bs.getBlockHardness(w, p);
			addTranslation(list, "jst.msg.scan.data2", h < 0 ? "\u221E" : h, bs.getBlock().getExplosionResistance(w, p, pl, new Explosion(w, pl, pl.posX, pl.posY, pl.posZ, 0.0F, false, false)) * 5);
			if (bs.getBlock().isBeaconBase(w, p, p.up())) addTranslation(list, "jst.msg.scan.beacon");
			if (te != null) addTranslation(list, "jst.msg.scan.tile" + (w.tickableTileEntities.contains(te) ? "2" : ""));
		} catch (Throwable t) {
			err = true;
			t.printStackTrace();
		}

		try {
		      boolean flag = false;
		      MetaTileBase mte = MetaTileBase.getMTE(w, p);
		      if (mte != null) {
		    	  if (mte.getMaxEnergy() > 0L)
		    		  addTranslation(list, "jst.msg.scan.energy", mte.baseTile.energy + " / " + mte.getMaxEnergy() + " EU");
		    	  if (mte instanceof IScanSupport) {
		    		  Map<String, Object[]> map = new LinkedHashMap();
		    		  ((IScanSupport)mte).getInfo(map);
		    		  for (Entry<String, Object[]> e : map.entrySet()) {
		    			  Object[] val = e.getValue();
		    			  addTranslation(list, e.getKey(), val == null ? new Object[0] : val);
		    		  }
		    	  }
		      } else if (te != null) {
		    	  IEnergyStorage es = (IEnergyStorage)te.getCapability(CapabilityEnergy.ENERGY, f);
		    	  if (es != null && es.getMaxEnergyStored() > 0)
		    		  addTranslation(list, "jst.msg.scan.energy", es.getEnergyStored() + " / " + es.getMaxEnergyStored() + " FE");
		      }
		      if (flag) eu += 400;
		} catch (Throwable t) {
			err = true;
			t.printStackTrace();
		}

		try {
			if (te != null) {
				IFluidHandler fh = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f);
				if (fh != null) {
					IFluidTankProperties[] tTanks = fh.getTankProperties();
					if (tTanks != null) {
						for (byte i = 0; i < tTanks.length; i = (byte) (i + 1)) {
							String fn = "";
							if (tTanks[i].getContents() != null)
								fn = tTanks[i].getContents().getLocalizedName();
							addTranslation(list, "jst.msg.scan.tank", i + 1, tTanks[i].getContents() == null ? 0 : tTanks[i].getContents().amount, tTanks[i].getCapacity(), fn);
						}
					}
				}
			}
		} catch (Throwable t) {
			err = true;
			t.printStackTrace();
		}

		try {
			if (te != null) {
				IDust fd = te.getCapability(FineDustCapability.FINEDUST, f);
				if (fd != null) {
					int d = fd.getDust();
					if (d > 0) addTranslation(list, "jst.tooltip.tile.com.dust", FineDustCapability.toMicrogram(d * 60));
				}
			}
		} catch (Throwable t) {
			err = true;
			t.printStackTrace();
		}

		try {
			if (JSTCfg.ic2Loaded) {
				if (te instanceof ICropTile) {
					ICropTile cr = (ICropTile) te;
					CropCard cc = cr.getCrop();
					if (cc != null && cr.getScanLevel() < 4) {
						eu += 9000;
						cr.setScanLevel(4);
					}
					
					if (cc != null) {
						eu += 1000;
						addTranslation(list, "jst.msg.scan.ic2.crop.desc");
						CropProperties pr = cc.getProperties();
						addTranslation(list, "jst.msg.scan.ic2.crop.info", new TextComponentTranslation(cc.getUnlocalizedName()), cc.getDiscoveredBy());
						addTranslation(list, "jst.msg.scan.ic2.crop.info2", pr.getTier(), cr.getCurrentSize(), cc.getMaxSize(), cr.getGrowthPoints(), cc.getGrowthDuration(cr));
						addTranslation(list, "jst.msg.scan.ic2.crop.info3", cr.getStatGrowth(), cr.getStatGain(), cr.getStatResistance());
						addTranslation(list, "jst.msg.scan.ic2.crop.info4", cr.getStorageNutrients(), cr.getStorageWater(), cr.getStorageWeedEX());
						addTranslation(list, "jst.msg.scan.ic2.crop.info5", cr.getTerrainNutrients(), cr.getTerrainHumidity(), cr.getTerrainAirQuality());
						addTranslation(list, "jst.msg.scan.ic2.crop.info6", String.join(", ", cc.getAttributes()));
						if (cc == Crops.weed)
							addTranslation(list, "jst.msg.scan.ic2.crop.weed");
					} else {
						eu += 200;
						addTranslation(list, "jst.msg.scan.ic2.crop.info4", cr.getStorageNutrients(), cr.getStorageWater(), cr.getStorageWeedEX());
					}
				}
				if (te instanceof IReactorChamber || te instanceof IReactor) {
					IReactor re = te instanceof IReactorChamber ? ((IReactorChamber)te).getReactorInstance() : ((IReactor)te);
					if (re != null) {
						addTranslation(list, "jst.msg.scan.ic2.nuclear.desc");
						int temp = re.getHeat() * 100 / re.getMaxHeat();
						TextFormatting tf = temp >= 85 ? TextFormatting.DARK_RED : temp >= 70 ? TextFormatting.RED : temp >= 50 ? TextFormatting.GOLD : temp >= 40 ? TextFormatting.YELLOW : TextFormatting.GREEN;
						addTranslation(list, "jst.msg.scan.ic2.nuclear.info", tf.toString() + re.getHeat() + " / " + re.getMaxHeat() + " (" + temp + "%)");
					}
				}
			}
		} catch (Throwable t) {
			err = true;
			t.printStackTrace();
		}
		
		if (err) addTranslation(list, "jst.msg.scan.error");
		
		return eu;
	}

	private static int doEntityScan(ArrayList<ITextComponent> list, EntityPlayer pl, EntityLivingBase elb) {
		int eu = 100;
		String s = JSTUtils.getRegName(elb);
		addTranslation(list, "jst.msg.scan.entity.data1", s == null ? "Unknown" : s, elb.getCreatureAttribute());
		addTranslation(list, "jst.msg.scan.entity.data2", (int)elb.getHealth() / 2.0F, (int)elb.getMaxHealth() / 2.0F);
		return eu;
	}

	private static void addTranslation(ArrayList<ITextComponent> list, String key, Object... obj) {
		list.add(new TextComponentTranslation(key, obj));
	}

	@Override
	public int getTier(ItemStack st) {
		return 1;
	}

	@Override
	public boolean canCharge(ItemStack st) {
		return true;
	}

	@Override
	public long transferLimit(ItemStack st) {
		return 512;
	}

	@Override
	public void getSubItems(ItemStack st, List<ItemStack> sub) {
		sub.add(st.copy());
		setEnergy(st, this.maxEnergy);
		sub.add(st);
	}

	@Override
	@SideOnly(Side.CLIENT)
	@Nullable
	public List<String> getInformation(ItemStack st, World w, ITooltipFlag adv) {
		long e = getEnergy(st);
		List<String> ret = new ArrayList();
		ret.add(I18n.format("jst.tooltip.energy.eu", e, this.maxEnergy));
		BigInteger bi = BigInteger.valueOf(JSTCfg.RFPerEU);
		ret.add(I18n.format("jst.tooltip.energy.rf", BigInteger.valueOf(e).multiply(bi), BigInteger.valueOf(this.maxEnergy).multiply(bi)));
		return ret;
	}

	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}

	@Override
	public boolean canBeStoredInToolbox(ItemStack st) {
		return true;
	}

	@Override
	public boolean interactEntity(ItemStack st, EntityPlayer p, EntityLivingBase e, EnumHand h) {
		if (p.world.isRemote)
			return true;
		
		ArrayList<ITextComponent> str = new ArrayList();
		int eu = doEntityScan(str, p, (EntityLivingBase)e);
		if (eu <= this.getEnergy(st)) {
			p.world.playSound((EntityPlayer) null, e.getPosition(), JSTSounds.SCAN, SoundCategory.BLOCKS, 0.8F, p.world.rand.nextFloat() * 0.2F + 0.9F);
			for (ITextComponent s : str)
				p.sendMessage(s);
			if (!p.capabilities.isCreativeMode) {
				this.setEnergy(st, this.getEnergy(st) - eu);
				this.chargeFromArmor(st, p);
			}
		} else {
			JSTUtils.sendSimpleMessage(p, TextFormatting.RED + "Not enough energy");
		}

		return true;
	}
}
