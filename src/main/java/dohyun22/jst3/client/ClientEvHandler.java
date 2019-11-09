package dohyun22.jst3.client;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.model.ModelMTE;
import dohyun22.jst3.compat.tic.CompatTiC;
import dohyun22.jst3.evhandler.EvHandler;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.items.behaviours.ItemBehaviour;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.loader.MTELoader;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.utils.JSTFluids;
import dohyun22.jst3.utils.JSTPotions;
import dohyun22.jst3.utils.JSTUtils;
import dohyun22.jst3.utils.ReflectionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientEvHandler implements IResourceManagerReloadListener {
	
	@Override
	public void onResourceManagerReload(IResourceManager rm) {
		ModelMTE.clearCache();
	}
	
	@SubscribeEvent
	public void textureStich(TextureStitchEvent.Pre ev){
		TextureLoader.initTex(ev.getMap());
	}
	
    @SubscribeEvent
    public void onModelBake(ModelBakeEvent ev) {
        IRegistry<ModelResourceLocation, IBakedModel> reg = ev.getModelRegistry();
        String pf = JustServerTweak.MODID + ":";
        reg.putObject(new ModelResourceLocation(pf + "BlockTE#normal"), ModelMTE.INSTANCE);
        reg.putObject(new ModelResourceLocation(pf + "BlockTE#inventory"), ModelMTE.INSTANCE);
    }
    
	@SubscribeEvent
	public void onToolTip(ItemTooltipEvent ev) {
		try {
			ItemStack st = ev.getItemStack();
			if (st.isEmpty()) return;
			String name = Item.REGISTRY.getNameForObject(st.getItem()).toString();
			List<String> tl = ev.getToolTip();
			if (JSTCfg.RIC2C && "ic2:cable".equals(name)) {
				String stn = tl.isEmpty() ? null : tl.get(0);
				tl.clear();
				if (stn != null) tl.add(stn);
				int id = EvHandler.getJSTMeta((byte) st.getMetadata(), st.hasTagCompound() ? st.getTagCompound().getByte("insulation") > 0 : false);
				MetaTileBase mtb = MetaTileBase.getTE(id);
				if (mtb == null) return;
				List<String> str = new ArrayList();
				mtb.getInformation(st, ev.getEntityPlayer().world, str, ev.getFlags());
				for (String s : str) if (s != null) tl.add(s);
			} else if (st.hasTagCompound() && CompatTiC.isTiCTool(st)) {
				NBTTagCompound tag = st.getTagCompound();
				if (tag.hasKey("JST_EU_MAX")) {
					long e = tag.getLong("JST_EU"), m = tag.getLong("JST_EU_MAX");
					tl.add("\u00A7a" + e + " / " + m + " EU");
					tl.add("\u00A7a" + (e * JSTCfg.RFPerEU) + " / " + (m * JSTCfg.RFPerEU) + " RF");
					int tier = tag.getInteger("JST_EU_LVL");
					tl.add("\u00A7a" + I18n.format("jst.tooltip.energy.tier") + " " + tier + " (" + JSTUtils.getTierName(tier) + ")");
				}
			}
		} catch (Throwable e) {}
	}

	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void modFogColor(EntityViewRenderEvent.FogColors ev) {
		Entity e = ev.getEntity();
		if (e instanceof EntityLivingBase) {
			PotionEffect ef = ((EntityLivingBase)e).getActivePotionEffect(JSTPotions.finedust);
			if (ef != null) {
				int a = ef.getAmplifier();
				float r = Math.min(a * 0.15F + 0.5F, 1.0F);
				float r2 = 1.0F - r;
				int eR = (int)(ev.getRed() * 255.0F), eG = (int)(ev.getGreen() * 255.0F), eB = (int)(ev.getBlue() * 255.0F);
				if (a > 3) {
					eR = Math.max(0, eR - (a - 3) * 10);
					eG = Math.max(0, eG - (a - 3) * 10);
					eB = Math.max(0, eB - (a - 3) * 10);
				}
				ev.setRed(MathHelper.clamp((eR * r2 + 96 * r) / 255.0F, 0.0F, 1.0F));
				ev.setGreen(MathHelper.clamp((eG * r2 + 80 * r) / 255.0F, 0.0F, 1.0F));
				ev.setBlue(MathHelper.clamp((eB * r2 + 40 * r) / 255.0F, 0.0F, 1.0F));
			}
		}
	}

	@SubscribeEvent
	public void onBlockHighlight(DrawBlockHighlightEvent ev) {
		EntityPlayer pl = ev.getPlayer();
		ItemStack st = pl.getHeldItem(EnumHand.MAIN_HAND);
		if (st != null && st.getItem() == JSTItems.item1) {
			ItemBehaviour ib = JSTItems.item1.getBehaviour(st);
			if (ib.isWrench(st) && ev.getTarget().typeOfHit == RayTraceResult.Type.BLOCK) {
				BlockPos p = ev.getTarget().getBlockPos();
				GL11.glPushMatrix();
				GL11.glTranslated(-(pl.lastTickPosX + (pl.posX - pl.lastTickPosX) * ev.getPartialTicks()), -(pl.lastTickPosY + (pl.posY - pl.lastTickPosY) * ev.getPartialTicks()), -(pl.lastTickPosZ + (pl.posZ - pl.lastTickPosZ) * ev.getPartialTicks()));
				GL11.glTranslated(p.getX() + 0.5D, p.getY() + 0.5D, p.getZ() + 0.5D);
				switch (ev.getTarget().sideHit) {
				case UP: GL11.glRotated(180.0, 1.0, 0.0, 0.0); break;
				case NORTH: GL11.glRotated(90.0, 1.0, 0.0, 0.0); break;
				case SOUTH: GL11.glRotated(-90.0, 1.0, 0.0, 0.0); break;
				case EAST: GL11.glRotated(90.0, 0.0, 0.0, 1.0); break;
				case WEST: GL11.glRotated(-90.0, 0.0, 0.0, 1.0); break;
				case DOWN:}
			    GL11.glTranslated(0.0D, -0.501D, 0.0D);
			    GL11.glLineWidth(2.0F);
			    GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.5F);
			    GL11.glBegin(1);
			    GL11.glVertex3d(0.5D, 0.0D, -0.25D);
			    GL11.glVertex3d(-0.5D, 0.0D, -0.25D);
			    GL11.glVertex3d(0.5D, 0.0D, 0.25D);
			    GL11.glVertex3d(-0.5D, 0.0D, 0.25D);
			    GL11.glVertex3d(0.25D, 0.0D, -0.5D);
			    GL11.glVertex3d(0.25D, 0.0D, 0.5D);
			    GL11.glVertex3d(-0.25D, 0.0D, -0.5D);
			    GL11.glVertex3d(-0.25D, 0.0D, 0.5D);
			    GL11.glEnd();
			    GL11.glPopMatrix();
			}
		}
	}

	public static void displayString(double x, double y, double z, String txt) {
		Entity e = Minecraft.getMinecraft().getRenderViewEntity();
		if (e instanceof EntityPlayer) {
			EntityPlayer pl = (EntityPlayer) e;
			double px = pl.prevPosX + (pl.posX - pl.prevPosX);
			double py = pl.prevPosY + (pl.posY - pl.prevPosY);
			double pz = pl.prevPosZ + (pl.posZ - pl.prevPosZ);
			GL11.glPushMatrix();
			GL11.glTranslated(-px + x + 0.5D, -py + y + 0.5D, -pz + z + 0.5D);
			float xd = (float) (px - (x + 0.5D));
			float zd = (float) (pz - (z + 0.5D));
			float yaw = (float) (Math.atan2(xd, zd) * 180.0D / Math.PI);
			GL11.glRotatef(yaw + 180.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
			GL11.glScalef(0.02F, 0.02F, 0.02F);
			GL11.glEnable(3042);
			GL11.glBlendFunc(770, 771);
			Minecraft.getMinecraft().fontRenderer.drawString(txt, 1 - Minecraft.getMinecraft().fontRenderer.getStringWidth(txt) / 2, 1.0F, 16777215, true);
			GL11.glPopMatrix();
		}
	}
}
