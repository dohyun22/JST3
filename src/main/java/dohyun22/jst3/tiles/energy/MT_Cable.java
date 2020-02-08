package dohyun22.jst3.tiles.energy;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import dohyun22.jst3.blocks.BlockTileEntity;
import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.compat.ic2.CompatIC2;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.network.JSTPacketHandler;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileEnergyInput;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTDamageSource;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import dohyun22.jst3.utils.JSTDamageSource.EnumHazard;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class MT_Cable extends MetaTileEnergyInput {
	public final String tex;
	protected final int maxv;
	protected final byte insMode;
	protected final byte r;
	protected final byte ic2rep;
	protected final int drop;
	protected byte connection;
	protected long trans;
	protected int volt;
	protected byte foam;
	protected byte blockedSide;
	protected static final DecimalFormat fmt = new DecimalFormat("#.###");

	public MT_Cable(String tex, int volt, int insMode, int r, int vd) {
		this(tex, volt, insMode, r, vd, -1);
	}

	public MT_Cable(String tex, int volt, int insMode, int r, int vd, int ic2rep) {
		this.tex = tex;
		this.maxv = volt;
		this.insMode = ((byte)insMode);
		this.r = ((byte)r);
		this.drop = vd;
		this.ic2rep = ((byte)ic2rep);
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_Cable(tex, maxv, insMode, r, drop, ic2rep);
	}

	@Override
    public long injectEnergy(EnumFacing dir, long v, boolean sim) {
        if (baseTile == null || !canAcceptEnergy() || !isEnergyInput(dir)) return 0L;
        v = getTransferrablePower(v, 0L);
        ArrayList<BlockPos> list = new ArrayList();
        list.add(getPos());
        if (dir != null) list.add(getPos().offset(dir));
        try {
            return v - transferEnergy(dir, v, 0L, list, sim);
        } catch (Throwable t) {
            if (baseTile.errorCode == 0) {
                System.err.println("[JST3] An error occurred while energy transfer. Errored Cable Pos: " + getPos());
                t.printStackTrace();
                baseTile.errorCode = 1;
            }
            if (connection != 0) {
                connection = 0;
                blockedSide = 63;
            }
            return 0L;
        }
    }

    protected long transferEnergy(EnumFacing dir, long input, long dist, ArrayList<BlockPos> loc, boolean sim) {
		dist++;
		byte side = JSTUtils.getNumFromFacing(dir);
        long gtp = getTransferrablePower(input, dist);
        if (gtp > 0L) {
            for (byte n = 0; n < 6; n++) {
                if ((connection & 1 << n) != 0 && n != side) {
                    BlockPos pos = getPos().offset(JSTUtils.getFacingFromNum(n));
                    TileEntity te = getWorld().getTileEntity(pos);
                    if (te != baseTile && !loc.contains(pos)) {
                        loc.add(pos);
                        EnumFacing od = JSTUtils.getOppositeFacing(n);
                        if (te instanceof TileEntityMeta) {
                            TileEntityMeta tem = (TileEntityMeta)te;
                            if (tem.hasValidMTE()) {
                                if (tem.mte instanceof MT_Cable) {
                                    long tp = ((MT_Cable)tem.mte).getTransferrablePower(input, dist);
                                    long cr = tp - ((MT_Cable)tem.mte).transferEnergy(od, tp, dist, loc, sim);
                                    input -= cr;
                                    if (!sim) trans += cr;
                                }
                                else {
                                    long p = tem.mte.injectEnergy(od, input, sim);
                                    input -= p;
                                    if (!sim) trans += p;
                                }
                            }
                        }
                        else if (te != null) {
                            IEnergyStorage es = (IEnergyStorage)te.getCapability(CapabilityEnergy.ENERGY, od);
                            if (es != null && es.canReceive()) {
                                long p = es.receiveEnergy(JSTUtils.convLongToInt(input * 4L), sim) / 4L;
                                input -= p;
                                if (!sim) trans += p;
                            }
                            else if (JSTCfg.ic2Loaded) {
                                IEnergyTile et = EnergyNet.instance.getSubTile(getWorld(), pos);
                                if (et instanceof IEnergySink && ((IEnergySink)et).acceptsEnergyFrom((IEnergyEmitter)null, od)) {
                                    long p2 = Math.max(0L, Math.min((long)((IEnergySink)et).getDemandedEnergy(), Math.min(JSTUtils.getVoltFromTier(((IEnergySink)et).getSinkTier(), true), input)));
                                    input -= p2;
                                    if (!sim) {
                                        ((IEnergySink)et).injectEnergy(od, (double)p2, (double)p2);
                                        trans += p2;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return input;
    }

	protected long getTransferrablePower(long v, long dist) {
		return Math.max(0L, Math.min(maxv < 0 ? Long.MAX_VALUE : maxv, v - getVDrop(dist)));
	}

	protected final long getVDrop(long dist) {
		if (dist <= 0L)
			return 0L;
		if (drop <= 0)
			return -drop;
		return dist % drop == 0L ? 1L : 0L;
	}

	@Override
	public int maxEUTransfer() {
		return maxv < 0 ? Integer.MAX_VALUE : maxv;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite ins = MetaTileBase.getTETex(insMode == 0 ? "cable" : tex); 
		return new TextureAtlasSprite[] { ins, ins, ins, ins, MetaTileBase.getTETex(tex), MetaTileBase.getTETex(tex) };
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		if (foam == -1) return getSingleTex("ic2:blocks/cf/foam");
		if (foam > 0 && foam <= 16) return getSingleTex("ic2:blocks/cf/wall_" + getCFColor(foam));
		
		if (connection == 0) return MetaTileBase.getSingleTETex(tex);
		TextureAtlasSprite ins = MetaTileBase.getTETex(insMode == 0 ? "cable" : tex); 
		TextureAtlasSprite con = MetaTileBase.getTETex(tex); 
		if (connection == 1 || connection == 2 || connection == 3) return new TextureAtlasSprite[] {con, con, ins, ins, ins, ins};
		if (connection == 4 || connection == 8 || connection == 12) return new TextureAtlasSprite[] {ins, ins, con, con, ins, ins};
		if (connection == 16 || connection == 32 || connection == 48) return new TextureAtlasSprite[] {ins, ins, ins, ins, con, con};
		
		TextureAtlasSprite[] ret =  new TextureAtlasSprite[] {ins, ins, ins, ins, ins, ins};
		for (byte i = 6; i >= 0; i--) if ((connection & 1 << i) != 0) ret[i] = MetaTileBase.getTETex(tex);
		return ret;
	}
	
	@Override
	public boolean canProvideEnergy() {
		return true;
	}

	@Override
	public boolean canAcceptEnergy() {
		return true;
	}

	@Override
	public boolean isEnergyInput(EnumFacing side) {
		if (side != null)
			return (blockedSide & 1 << side.getIndex()) == 0;
		return true;
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack st, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (st == null || st.isEmpty()) return false;
		if (foam == -1 && st.getItem() == Item.getItemFromBlock(Blocks.SAND)) {
	    	if (!pl.capabilities.isCreativeMode)
	    		st.shrink(1);
	    	setFoam((byte) 8);
	    	return true;
	    }
	    if (foam == 0) {
	    	String name = JSTUtils.getRegName(st);
			if (name.equals("ic2:foam")) {
				if (st.getItemDamage() == 0) {
		    		if (!pl.capabilities.isCreativeMode)
		    			st.shrink(1);
		    		setFoam((byte) -1);
			    	return true;
				}
	    	} else if (name.equals("ic2:foam_sprayer")) {
	    		if (st.hasTagCompound()) {
		    		NBTTagCompound nbt = st.getTagCompound();
		    		NBTTagCompound nbt2 = nbt.getCompoundTag("Fluid");
		    		if ("ic2construction_foam".equals(nbt2.getString("FluidName"))) {
		    			int amt = nbt2.getInteger("Amount");
		    			if (amt > 100) {
		    				amt -= 100;
		    	    		if (setFoam((byte) -1)) {
				    			for (int x = -1; x <= 1; x++) {
				    				for (int y = -1; y <= 1; y++) {
				    					for (int z = -1; z <= 1; z++) {
				    						if ((x | y | z) == 0) continue;
				    						if (amt < 100) break;
				    						MetaTileBase te = getMTE(getWorld(), getPos().add(x, y, z));
				    						if (te instanceof MT_Cable && ((MT_Cable)te).foam == 0) {
				    							((MT_Cable)te).setFoam((byte) -1);
				    		    				amt -= 100;
				    						}
				    					}
				    				}
				    			}
		    	    		}
		    				nbt2.setInteger("Amount", amt);
			    			nbt.setTag("Fluid", nbt2);
		    			}
		    		}
	    		}
	    		return true;
	    	} else if (st.getItem() instanceof ItemShears || name.equals("ic2:cutter") || (name.equals("immersiveengineering:tool") && st.getItemDamage() == 1)) {
	    		EnumFacing f = JSTUtils.determineWrenchingSide(side, hitX, hitY, hitZ);
	    		if (f != null) {
	    			boolean cut = false;
	    			byte bs = 0;
					for (int n = 0; n < 6; n++) {
						boolean flag = (blockedSide & 1 << n) != 0;
						if (n == f.ordinal() && !flag) {
							bs = ((byte) (bs | 1 << n));
							cut = true;
						} else if (n != f.getIndex() && flag) {
							bs = ((byte) (bs | 1 << n));
						}
					}
	    			
	    			blockedSide = bs;
	    			if (cut) st.damageItem(1, pl);
	    			World w = getWorld();
	    			BlockPos p = getPos();
	    			w.playSound(null, p, cut ? SoundEvents.ENTITY_SHEEP_SHEAR : SoundEvents.BLOCK_CLOTH_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
	    			onBlockUpdate();
	    			w.notifyNeighborsOfStateChange(p, w.getBlockState(p).getBlock(), true);
	    		}
	    	}
	    }
	    
		return false;
	}

	@Override
	public void onBlockUpdate() {
		super.onBlockUpdate();
		if (isClient()) return;
		
		byte ret = 0;
		byte i = 0;
		for (byte j = 0; i < 6; i++) {
			if ((blockedSide & 1 << i) != 0) continue;
			j = (byte) EnumFacing.VALUES[i].getOpposite().getIndex();
			TileEntity te = getWorld().getTileEntity(getPos().offset(EnumFacing.VALUES[i]));
			EnumFacing dir = JSTUtils.getFacingFromNum(j);
			if (te != null) {
				IEnergyStorage es = (IEnergyStorage)te.getCapability(CapabilityEnergy.ENERGY, dir);
				if (es != null && (es.canExtract() || es.canReceive())) {
					ret = (byte)(ret | 1 << i);
					continue;
				}
			}
			if (JSTCfg.ic2Loaded) {
				try {
					IEnergyTile et = EnergyNet.instance.getSubTile(getWorld(), getPos().offset(EnumFacing.VALUES[i]));
					if ((et instanceof IEnergyEmitter && ((IEnergyEmitter)et).emitsEnergyTo(null, dir)) || (et instanceof IEnergySink && ((IEnergySink)et).acceptsEnergyFrom(null, dir)))
						ret = (byte)(ret | 1 << i);
				} catch (Throwable t) {}
			}
		}
		if (connection != ret) {
			connection = ret;
			doBlockUpdate();
		}
	}

	@Override
	public void onPreTick() {
		if (!isClient() && foam == -1 && getWorld().rand.nextInt(2400) == 0) setFoam((byte) 8);
	}

	@Override
	public void onPostTick() {
		super.onPostTick();
		if (isClient()) return;
		volt = JSTUtils.convLongToInt(trans);
		trans = 0L;
		if (baseTile.getTimer() % 20L == 0L)
			volt = 0;
	}

	@Override
	public void readSyncableDataFromNBT(NBTTagCompound tag) {
		connection = tag.getByte("cdir");
		foam = tag.getByte("foam");
	}

	@Override
	public void writeSyncableDataToNBT(NBTTagCompound tag) {
		if (connection != 0)
			tag.setByte("cdir", connection);
		if (foam != 0)
			tag.setByte("foam", foam);
	}
	
	 @Override
	 public void readFromNBT(NBTTagCompound tag) {
		 blockedSide = tag.getByte("bs");
	 }

	 @Override
	 public void writeToNBT(NBTTagCompound tag) {
		 if (blockedSide != 0)
			 tag.setByte("bs", blockedSide);
	 }
	
	@Override
	public boolean isSideSolid (EnumFacing f) {
		if (f != null) {
			TileEntity te = getWorld().getTileEntity(getPos().offset(f));
			if (te != null && "ic2.core.block.wiring.TileEntityLuminator".equals(te.getClass().getName()))
				return true;
		}
		return isSideOpaque(f);
	}
	
	@Override
	public boolean isSideOpaque(EnumFacing f) {
		return foam > 0;
	}
	
	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		String str = maxv < 0 ? "\u221E EU/t, \u221E RF/t" : maxv + " EU/t, " + (maxv * (long)JSTCfg.RFPerEU) + " RF/t";
		ls.add(I18n.format("jst.tooltip.tile.cable.maxpower", str));
		float l = drop <= 0 ? -drop : 1.0f / drop;
		str = fmt.format(l) + " EU, " + fmt.format(l * JSTCfg.RFPerEU) + " RF";
		ls.add(I18n.format("jst.tooltip.tile.cable.loss", str));
		ls.add(I18n.format("jst.tooltip.tile.cable.tip"));
		if (maxv >= 16 && insMode == 1)
			ls.add(I18n.format("jst.tooltip.tile.cable.uninsulated"));
	}
	
	@Override
	public List<AxisAlignedBB> getBox() {
		if (foam != 0) return def;
		
		float d = r * 2 * 0.0625F;
		float s = (1.0F - d) / 2.0F;
		List<AxisAlignedBB> ret = new ArrayList(7);

		ret.add(new AxisAlignedBB(s, s, s, s + d, s + d, s + d));

		for (byte n = 0; n < 6; n++) {
			if ((connection & 1 << n) != 0) {
				float xS, yS, zS;
				xS = yS = zS = s;
				float xE, yE, zE;
				xE = yE = zE = s + d;
				switch (n) {
				case 0:
					yS = 0.0F;
					yE = s;
					break;
				case 1:
					yS = s + d;
					yE = 1.0F;
					break;
				case 2:
					zS = 0.0F;
					zE = s;
					break;
				case 3:
					zS = s + d;
					zE = 1.0F;
					break;
				case 4:
					xS = 0.0F;
					xE = s;
					break;
				case 5:
					xS = s + d;
					xE = 1.0F;
					break;
				}
				ret.add(new AxisAlignedBB(xS, yS, zS, xE, yE, zE));
			}
		}
		return ret;
	}
	
	@Nullable
	public AxisAlignedBB getBoundingBox() {
		List<AxisAlignedBB> boxes = getBox();
		if (boxes.isEmpty()) {
			return null;
		}
		if (boxes.size() == 1) {
			return (AxisAlignedBB) boxes.get(0);
		}
		double zS;
		double yS;
		double xS = yS = zS = Double.POSITIVE_INFINITY;
		double zE;
		double yE;
		double xE = yE = zE = Double.NEGATIVE_INFINITY;
		for (AxisAlignedBB ab : boxes) {
			xS = Math.min(xS, ab.minX);
			yS = Math.min(yS, ab.minY);
			zS = Math.min(zS, ab.minZ);
			xE = Math.max(xE, ab.maxX);
			yE = Math.max(yE, ab.maxY);
			zE = Math.max(zE, ab.maxZ);
		}
		return new AxisAlignedBB(xS, yS, zS, xE, yE, zE);
	}
	
	@Override
	public SoundType getSoundType(Entity e) {
		if (foam > 0)
			return SoundType.STONE;
		return SoundType.CLOTH;
	}
	
	@Override
	public void getDrops(ArrayList<ItemStack> ls) {
		if (JSTCfg.ic2Loaded && JSTCfg.rIC2C && ic2rep >= 0) {
			ItemStack st = CompatIC2.getIC2Cable(ic2rep & 0xF, ic2rep >> 4);
			if (!st.isEmpty()) ls.add(st);
		}
		super.getDrops(ls);
	}
	
	@Override
	public void getSubBlocks(int id, NonNullList<ItemStack> list) {
		if (JSTCfg.ic2Loaded && JSTCfg.rIC2C && ic2rep >= 0) {
			ArrayList ls = new ArrayList();
			getDrops(ls);
			list.addAll(ls);
		} else
			super.getSubBlocks(id, list);
	}
	
	@Override
	public boolean isOpaque() {
		return foam > 0;
	}
	
	@Override
	public void onEntityCollided(Entity e) {
		if (insMode == 1 && volt > 0 && e instanceof EntityLivingBase && e.attackEntityFrom(JSTDamageSource.ELECTRIC, JSTDamageSource.hasFullHazmat(JSTDamageSource.EnumHazard.ELECTRIC, (EntityLivingBase)e) ? 0.0F : Math.min(volt, maxv) / 64.0F)) {
			getWorld().playSound(null, getPos(), JSTSounds.SHOCK, SoundCategory.BLOCKS, 1.5F, 1.0F);
			JSTPacketHandler.playCustomEffect(getWorld(), getPos(), 1, 10);
		}
	}
	
	protected static String getCFColor(byte meta) {
		switch (meta) {
		case 1:
			return "black";
		case 2:
			return "blue";
		case 3:
			return "brown";
		case 4:
			return "cyan";
		case 5:
			return "gray";
		case 6:
			return "green";
		case 7:
			return "light_blue";
		case 8:
			return "light_gray";
		case 9:
			return "lime";
		case 10:
			return "magenta";
		case 11:
			return "orange";
		case 12:
			return "pink";
		case 13:
			return "purple";
		case 14:
			return "red";
		case 15:
			return "white";
		case 16:
			return "yellow";
		}
		return "";
	}
	
	@Override
	public boolean recolorTE(EnumFacing f, EnumDyeColor col) {
		byte n = 0;
		switch (col) {
		case BLACK:
			n = 1;
			break;
		case BLUE:
			n = 2;
			break;
		case BROWN:
			n = 3;
			break;
		case CYAN:
			n = 4;
			break;
		case GRAY:
			n = 5;
			break;
		case GREEN:
			n = 6;
			break;
		case LIGHT_BLUE:
			n = 7;
			break;
		case LIME:
			n = 9;
			break;
		case MAGENTA:
			n = 10;
			break;
		case ORANGE:
			n = 11;
			break;
		case PINK:
			n = 12;
			break;
		case PURPLE:
			n = 13;
			break;
		case RED:
			n = 14;
			break;
		case SILVER:
			n = 8;
			break;
		case WHITE:
			n = 15;
			break;
		case YELLOW:
			n = 16;
			break;
		}
		
		if (n > 0 && foam > 0 && setFoam(n))
			return true;
		
		return false;
	}
	
	@Override
	public boolean removedByPlayer(EntityPlayer pl, boolean wh) {
		if (foam != 0) {
			setFoam((byte) 0);
			return false;
		}
		return true;
	}
	
	@Override
    public float getResistance(Entity ee, Explosion ex) {
        if (foam > 0)
        	return 18.0F;
        return super.getResistance(ee, ex);
    }
	
	@Override
    public float getHardness() {
        if (foam == -1)
        	return 0.01F;
        else if (foam > 0)
        	return 3.0F;
        return super.getHardness();
    }
	
	@Override
	@Nullable
	public MapColor getMapColor() {
		return MapColor.BLACK;
	}
	
	public boolean setFoam (byte f) {
		if (foam == f || f < -1)
			return false;
		foam = f;
		World w = getWorld();
		BlockPos p = getPos();
		BlockTileEntity.setState(w, p, isOpaque(), 3);
		IBlockState bs = w.getBlockState(p);
		w.notifyBlockUpdate(getPos(), bs, bs, 3);
		return true;
	}
	
	protected byte countConnection() {
		byte ret = -1;
		for (byte n = 0; n < 6; n++) {
			if ((connection & 1 << n) != 0) {
				ret++;
			}
		}
		return ret < 0 ? 0 : ret;
	}

	@Override
	@SideOnly(value = Side.CLIENT)
	public String getModelKey() {
		return foam != 0 ? "cblf" + foam : "cble" + insMode + tex + ((int)connection | MathHelper.clamp(r, 1, 8) << 8);
	}

	@Override
	@SideOnly(value = Side.CLIENT)
	public List<BakedQuad> getModel(boolean isItem) {
		return makePipeModel(isItem ? getDefaultTexture() : getTexture(), foam != 0 ? 8 : r, connection, isItem);
	}
}