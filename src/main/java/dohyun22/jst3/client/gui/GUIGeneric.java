package dohyun22.jst3.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.compat.jei.JEISupport;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.interfaces.IGenericGUIMTE;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUIGeneric extends GUIBase {
	protected static final ResourceLocation gui = new ResourceLocation(JustServerTweak.MODID, "textures/gui/modular.png");
	protected final List<GUIComponent> components = new ArrayList();
	protected HashMap<GuiButton, int[]> btn = new HashMap();
	protected int gX, gY;

	public GUIGeneric(ContainerGeneric c, int x, int y) {
		super(c);
		if (x < 36 || y < 36) throw new IllegalArgumentException("Invalid Size");
		gX = x / 32;
		gY = y / 32;
		if (x % 32 > 0) gX++;
		if (y % 32 > 0) gY++;
		xSize = x;
		ySize = y;
	}

	public GUIGeneric(ContainerGeneric c) {
		this(c, 176, 166);
		addInv(8, 84);
	}

	public void addComp(GUIComponent gc) {
		components.add(gc);
	}

	public void addSlot(int x, int y, int type) {
		components.add(new ComponentSlot(x, y, type));
	}

	public void addSlot(Slot s, int type) {
		addSlot(s.xPos, s.yPos, type);
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

	public void addFuel(int x, int y) {
		components.add(new ComponentFuelBar(x, y));
	}

	public void addJEI(int x, int y, String... jei) {
		if (Loader.isModLoaded("jei")) components.add(new ComponentJEIButton(x, y, jei));
	}

	public void addCfg(int x, int y, boolean m) {
		components.add(new ComponentCfgButton(x, y, m));
	}

	public void addInv(int x, int y) {
		components.add(new ComponentInv(x, y));
	}

	public void addButton(int x, int y, int w, int h, int id, String t, boolean f) {
		btn.put(new GuiButton(id, x, y, w, h, f ? I18n.format(t) : t), new int[] {x, y});
	}

	public void addButton(int x, int y, int w, int h, int id, int did) {
		btn.put(new ButtonGuiData(id, x, y, w, h, did, this), new int[] {x, y});
	}

	public void addHoverText(int x, int y, int w, int h, String t) {
		components.add(new ComponentHoverText(x, y, w, h, t));
	}

	public void addText(int x, int y, int idx) {
		components.add(new ComponentText(x, y, idx));
	}

	@Override
	public void initGui() {
		super.initGui();
		for (Entry<GuiButton, int[]> e : btn.entrySet()) {
			GuiButton b = e.getKey();
			b.x = e.getValue()[0] + (width - xSize) / 2; b.y = e.getValue()[1] + (height - ySize) / 2;
			buttonList.add(b);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
		for (GUIComponent c : components)
			c.drawForeground(this);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float pt, int mx, int my) {
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(gui);
	    int x = (width - xSize) / 2;
	    int y = (height - ySize) / 2;
	    for (int a = 0; a < gX; a++) {
		    for (int b = 0; b < gY; b++) {
		    	boolean l = a == 0, u = b == 0, r = a == gX - 1, d = b == gY - 1;
		    	int px = x + a * 32, py = y + b * 32;
		    	if (r) px -= 32 - xSize % 32;
		    	if (d) py -= 32 - ySize % 32;
		    	drawTexturedModalRect(px, py, 32 + (l ? -32 : r ? 32 : 0), 32 + (u ? -32 : d ? 32 : 0), 32, 32);
		    }
	    }
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

	@Override
	protected void actionPerformed(GuiButton gb) throws IOException {
		handleMouseClick(null, 1000 + gb.id, 0, ClickType.QUICK_CRAFT);
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

	public static class ComponentInv extends GUIComponent {

		public ComponentInv(int x, int y) {
			super(x - 1, y - 1);
		}
		
		@Override
		public void draw(GUIGeneric g, int sX, int sY) {
			for (int x = 0; x < 9; x++) {
				g.drawTexturedModalRect(sX + _X + x * 18, sY + _Y, 176, 49, 18, 18);
				g.drawTexturedModalRect(sX + _X + x * 18, sY + _Y + 18, 176, 49, 18, 18);
				g.drawTexturedModalRect(sX + _X + x * 18, sY + _Y + 36, 176, 49, 18, 18);
				g.drawTexturedModalRect(sX + _X + x * 18, sY + _Y + 58, 176, 49, 18, 18);
			}
		}
	}

	public static class ComponentSlot extends GUIComponent {
		private final byte type;
		
		public ComponentSlot(int x, int y, int type) {
			super(x, y);
			this.type = (byte)type;
		}
		
		@Override
		public void draw(GUIGeneric g, int sX, int sY) {
			g.drawTexturedModalRect(sX + _X - 1, sY + _Y - 1, 176 + 18 * (type % 4), 49 + 18 * (type / 4), 18, 18);
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
			ls.add(JSTUtils.formatNum(con.energy) + " / " + JSTUtils.formatNum(con.mxenergy) + " EU");
			ls.add(JSTUtils.formatNum(con.energy * JSTCfg.RFPerEU) + " / " + JSTUtils.formatNum(con.mxenergy * JSTCfg.RFPerEU) + " RF");
			g.drawHoveringText(ls, mX, mY);
		}

		@Override
		public void draw(GUIGeneric g, int sX, int sY) {
			ContainerGeneric con = (ContainerGeneric)g.inventorySlots;
			g.drawTexturedModalRect(sX + _X, sY + _Y, 183, 34, 7, 15);
			if (con.energy <= 0 || con.mxenergy <= 0) return;
	        int s = (int)(JSTUtils.safeMultiplyLong(con.energy, 14) / con.mxenergy);
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
			ls.add(JSTUtils.formatNum(con.energy) + " / " + JSTUtils.formatNum(con.mxenergy) + " EU");
			ls.add(JSTUtils.formatNum(con.energy * JSTCfg.RFPerEU) + " / " + JSTUtils.formatNum(con.mxenergy * JSTCfg.RFPerEU) + " RF");
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

	public static class ComponentFuelBar extends GUIComponent {
		public ComponentFuelBar(int x, int y) {
			super(x, y);
		}

		@Override
		public void draw(GUIGeneric g, int sX, int sY) {
			ContainerGeneric con = (ContainerGeneric)g.inventorySlots;
			g.drawTexturedModalRect(sX + _X, sY + _Y, 204, 35, 14, 14);
			if (con.fuel <= 0 || con.mxfuel <= 0) return;
	        int s = Math.min((con.fuel * 14 / con.mxfuel), 14);
	        g.drawTexturedModalRect(sX + _X, sY + _Y + 14 - s, 190, 35 + 14 - s, 14, s);
		}
	}

	public static class ComponentJEIButton extends GUIComponent {
		private final String[] jei;

		public ComponentJEIButton(int x, int y, String... jei) {
			super(x, y);
			this.jei = Loader.isModLoaded("jei") ? jei : null;
		}

		@Override
		public void onHover(GUIGeneric g, int mX, int mY) {
			if (jei != null && jei.length > 0 && g.isPointInRegion(_X + 1, _Y + 1, 16, 16, mX, mY))
				g.drawHoveringText(I18n.format("jei.tooltip.show.recipes"), mX, mY);
		}

		@Override
		public void draw(GUIGeneric g, int sX, int sY) {
			if (jei != null && jei.length > 0) g.drawTexturedModalRect(sX + _X, sY + _Y, 0, 166, 18, 18);
		}

		@Override
		public boolean onClick(GUIGeneric g, int mX, int mY, int b) {
			if (b == 0 && g.isPointInRegion(_X + 1, _Y + 1, 16, 16, mX, mY) && jei != null && jei.length > 0) {
				JEISupport.loadJEI(Arrays.asList(jei));
				return true;
			}
			return false;
		}
	}

	public static class ComponentCfgButton extends GUIComponent {
		private final boolean mode;

		public ComponentCfgButton(int x, int y, boolean m) {
			super(x, y);
			mode = m;
		}

		@Override
		public void onHover(GUIGeneric g, int mX, int mY) {
			if (g.isPointInRegion(_X + 1, _Y + 1, 16, 16, mX, mY)) {
				String s = "jst.msg.com.cfg";
				if (!mode) s += "2";
				g.drawHoveringText(I18n.format(s), mX, mY);
			}
		}

		@Override
		public void draw(GUIGeneric g, int sX, int sY) {
			g.drawTexturedModalRect(sX + _X, sY + _Y, mode ? 18 : 36, 166, 18, 18);
		}

		@Override
		public boolean onClick(GUIGeneric g, int mX, int mY, int b) {
			if (g.isPointInRegion(_X + 1, _Y + 1, 16, 16, mX, mY)) {
				g.handleMouseClick(null, mode ? 2000 : 2001, 0, ClickType.QUICK_CRAFT);
				return true;
			}
			return false;
		}
	}

	public static class ComponentHoverText extends GUIComponent {
		private final int w, h;
		private final String s;

		public ComponentHoverText(int x, int y, int w, int h, String s) {
			super(x, y);
			this.w = w;
			this.h = h;
			this.s = I18n.format(s);
		}

		@Override
		public void onHover(GUIGeneric g, int mX, int mY) {
			if (g.isPointInRegion(_X + 1, _Y + 1, w - 1, h - 1, mX, mY))
				g.drawHoveringText(s, mX, mY);
		}
	}

	public static class ComponentText extends GUIComponent {
		private final int idx;

		public ComponentText(int x, int y, int idx) {
			super(x, y);
			this.idx = idx;
		}

		public void drawForeground(GUIGeneric g) {
			ContainerGeneric c = (ContainerGeneric)g.inventorySlots;
			if (c.te.mte instanceof IGenericGUIMTE) {
				String t = ((IGenericGUIMTE)c.te.mte).guiDataToStr(idx, c.getGuiData(idx));
				if (t != null) g.fontRenderer.drawString(t, _X, _Y, 0);
			}
		}
	}

	public static class ButtonGuiData extends GuiButton {
		private final int idx;
		private final GUIGeneric gui;
		private int debug = 5;

		public ButtonGuiData(int b, int x, int y, int w, int h, int i, GUIGeneric g) {
			super(b, x, y, w, h, "");
			idx = i;
			gui = g;
		}

		@Override
		public void drawButton(Minecraft mc, int mx, int my, float pt) {
			if (visible) {
				ContainerGeneric cg = (ContainerGeneric)gui.inventorySlots;
				if (cg.te.mte instanceof IGenericGUIMTE) {
					String t = ((IGenericGUIMTE)cg.te.mte).guiDataToStr(idx, cg.getGuiData(idx));
					displayString = t == null ? "" : t;
				}
			}
			super.drawButton(mc, mx, my, pt);
		}
	}
}
