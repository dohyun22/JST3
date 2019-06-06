package dohyun22.jst3.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.compat.jei.JEISupport;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.loader.JSTCfg;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUIGeneric extends GUIBase {
	protected static final ResourceLocation gui = new ResourceLocation(JustServerTweak.MODID, "textures/gui/modular.png");
	protected final List<GUIComponent> components = new ArrayList();
	
	public GUIGeneric(ContainerGeneric c) {
		super(c);
	}
	
	public void addComp(GUIComponent gc) {
		components.add(gc);
	}
	
	public void addSlot(int x, int y, int type) {
		components.add(new ComponentSlot(x, y, type));
	}
	
	public void addPrg(int x, int y, String... jei) {
		components.add(new ComponentPrgBar(x, y, jei));
	}
	
	public void addPwr(int x, int y) {
		components.add(new ComponentPwrBar(x, y));
	}
	
	public void addPwr2(int x, int y) {
		components.add(new ComponentPwrBar2(x, y));
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
		for (GUIComponent c : components)
			c.drawForeground(this);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(gui);
	    int x = (width - xSize) / 2;
	    int y = (height - ySize) / 2;
	    drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		for (GUIComponent gc : components)
			gc.draw(this, x, y);
	}
	
	@Override
    public void drawScreen(int mX, int mY, float pt) {
		super.drawScreen(mX, mY, pt);
		for (GUIComponent c : components)
			c.onHover(this, mX, mY);
	}
	
	@Override
	protected void mouseClicked(int mX, int mY, int b) throws IOException {
		for (GUIComponent c : components) if (c.onClick(this, mX, mY, b)) return;
		super.mouseClicked(mX, mY, b);
	}
	
	public static class GUIComponent {
		protected final int _X;
		protected final int _Y;
		
		public GUIComponent(int x, int y) {
			this._X = x;
			this._Y = y;
		}

		public boolean onClick(GUIGeneric g, int mX, int mY, int b) {
			return false;
		}

		public void onHover(GUIGeneric g, int mX, int mY) {}

		public void draw(GUIGeneric g, int sX, int sY) {}
		
		public void drawForeground(GUIGeneric g) {}
	}
	
	public static class ComponentSlot extends GUIComponent {
		private final byte type;
		
		public ComponentSlot(int x, int y, int type) {
			super(x, y);
			this.type = (byte)type;
		}
		
		@Override
		public void draw(GUIGeneric g, int sX, int sY) {
			g.drawTexturedModalRect(sX + _X - 1, sY + _Y - 1, 176 + 18 * (type % 2), 49 + 18 * (type / 2), 18, 18);
		}
	}
	
	public static class ComponentPrgBar extends GUIComponent {
		private final String[] jei;
		
		public ComponentPrgBar(int x, int y, String... jei) {
			super(x, y);
			this.jei = jei;
		}
		
		@Override
		public boolean onClick(GUIGeneric g, int mX, int mY, int b) {
			if (b == 0 && g.isPointInRegion(_X, _Y, 24, 17, mX, mY) && jei != null && jei.length > 0 && Loader.isModLoaded("jei")) {
				JEISupport.loadJEI(Arrays.asList(jei));
				return true;
			}
			return false;
		}

		@Override
		public void onHover(GUIGeneric g, int mX, int mY) {
			if (jei != null && jei.length > 0 && g.isPointInRegion(_X, _Y, 24, 17, mX, mY) && Loader.isModLoaded("jei"))
				g.drawHoveringText(I18n.format("jei.tooltip.show.recipes"), mX, mY);
		}

		@Override
		public void draw(GUIGeneric g, int sX, int sY) {
			ContainerGeneric con = (ContainerGeneric)g.inventorySlots;
			g.drawTexturedModalRect(sX + _X, sY + _Y, 176, 17, 24, 17);
			if (con.progress == 0 || con.mxprogress == 0) return;
	        int s = con.progress * 22 / con.mxprogress;
	        g.drawTexturedModalRect(sX + _X, sY + _Y, 176, 0, s + 1, 16);
		}
	}
	
	public static class ComponentPwrBar extends GUIComponent {
		public ComponentPwrBar(int x, int y) {
			super(x, y);
		}
		
		@Override
		public void onHover(GUIGeneric g, int mX, int mY) {
			if (!g.isPointInRegion(_X, _Y, 7, 15, mX, mY)) return;
			ContainerGeneric con = (ContainerGeneric)g.inventorySlots;
			ArrayList<String> ls = new ArrayList();
			ls.add(con.energy + " / " + con.mxenergy + " EU");
			ls.add((con.energy * JSTCfg.RFPerEU) + " / " + (con.mxenergy * JSTCfg.RFPerEU) + " RF");
			g.drawHoveringText(ls, mX, mY);
		}

		@Override
		public void draw(GUIGeneric g, int sX, int sY) {
			ContainerGeneric con = (ContainerGeneric)g.inventorySlots;
			g.drawTexturedModalRect(sX + _X, sY + _Y, 183, 34, 7, 15);
			if (con.energy <= 0 || con.mxenergy <= 0) return;
	        int s = (int)(con.energy * 14 / con.mxenergy);
	        g.drawTexturedModalRect(sX + _X, sY + _Y  + 14 - s, 176, 34 + 14 - s, 7, s);
		}
	}
	
	public static class ComponentPwrBar2 extends GUIComponent {
		public ComponentPwrBar2(int x, int y) {
			super(x, y);
		}
		
		@Override
		public void onHover(GUIGeneric g, int mX, int mY) {
			if (!g.isPointInRegion(_X, _Y, 32, 17, mX, mY)) return;
			ContainerGeneric con = (ContainerGeneric)g.inventorySlots;
			ArrayList<String> ls = new ArrayList();
			ls.add(con.energy + " / " + con.mxenergy + " EU");
			ls.add((con.energy * JSTCfg.RFPerEU) + " / " + (con.mxenergy * JSTCfg.RFPerEU) + " RF");
			g.drawHoveringText(ls, mX, mY);
		}

		@Override
		public void draw(GUIGeneric g, int sX, int sY) {
			ContainerGeneric con = (ContainerGeneric)g.inventorySlots;
			g.drawTexturedModalRect(sX + _X, sY + _Y, 200, 0, 32, 17);
			if (con.energy <= 0 || con.mxenergy <= 0) return;
	        g.drawTexturedModalRect(sX + _X + 4, sY + _Y, 200, 17, (int) (con.energy * 24 / con.mxenergy), 17);
		}
	}
}
