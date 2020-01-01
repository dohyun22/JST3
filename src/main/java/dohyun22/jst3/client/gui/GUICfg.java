package dohyun22.jst3.client.gui;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.container.ContainerCfg;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.interfaces.IConfigurable;
import dohyun22.jst3.utils.EnumRelativeFacing;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ClickType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class GUICfg extends GUIBase {
	private static final ResourceLocation gui = new ResourceLocation(JustServerTweak.MODID, "textures/gui/cfg.png");

	public GUICfg(TileEntityMeta te, boolean m) {
		super(new ContainerCfg(te, m));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float pt, int mx, int my) {
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(gui);
	    int x = (width - xSize) / 2, y = (height - ySize) / 2;
	    drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	    ContainerCfg c = (ContainerCfg)inventorySlots;
	    if (c.mode) {
		    EnumFacing f = c.front;
		    if (f != null) {
		    	drawSide(c.cfg[toNum(EnumRelativeFacing.BACK, f)], 100, 75);
		    	drawTexturedModalRect(x + 80, y + 55, 192, 0, 16, 16);
		    	drawSide(c.cfg[toNum(EnumRelativeFacing.UP, f)], 80, 35);
		    	drawSide(c.cfg[toNum(EnumRelativeFacing.DOWN, f)], 80, 75);
		    	drawSide(c.cfg[toNum(EnumRelativeFacing.LEFT, f)], 60, 55);
		    	drawSide(c.cfg[toNum(EnumRelativeFacing.RIGHT, f)], 100, 55);
		    }
	    } else {
	    	for (int n = 0; n < 8; n++) {
	    		short num = c.priority[n];
	    		if (num != 0) drawTexturedModalRect(x + 26, y + 7 + n * 18, 0, 166, 142, 18);
	    	}
	    }
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
		ContainerCfg c = (ContainerCfg) inventorySlots;
		if (!c.mode) {
			for (int n = 0; n < 8; n++) {
				short num = c.priority[n];
				if (num != 0) {
					int idx = num >> 8;
					boolean e = (num & 0xFF) != 0;
					String t = "Unknown";
					try {
						String t2 = ((IConfigurable) c.te.mte).getCfgName(idx);
						if (t2 != null) t = t2;
					} catch (Exception ex) {}
					fontRenderer.drawString(I18n.format(t), 35, 13 + n * 18, e ? 0xFFFFFF : 0xAAAAAA);
					fontRenderer.drawString(I18n.format("jst.msg.com." + (e ? "on" : "off")), 150, 13 + n * 18, 0xFFFFFF);
				}
			}
		}
	}

	@Override
	protected void mouseClicked(int mX, int mY, int b) throws IOException {
		ContainerCfg c = (ContainerCfg)inventorySlots;
		if (isPointInRegion(5, 143, 18, 18, mX, mY)) {
			handleMouseClick(null, 300, b, ClickType.QUICK_CRAFT);
			return;
		}
		if (c.mode) {
			EnumRelativeFacing f = getFace(mX, mY);
			if (f != null) {
				EnumFacing f2 = f.toFacing(c.front);
				if (f2 != null) {
					handleMouseClick(null, f2.ordinal(), b, ClickType.QUICK_CRAFT);
					playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F);
				}
				return;
			}
		} else {
			try {
				for (int n = 0; n < 8; n++) {
					short s = c.priority[n];
					if (s == 0) continue;
					boolean flag = false;
					if (isPointInRegion(26, 7 + n * 18, 122, 18, mX, mY)) {
						handleMouseClick(null, 100 + n, b, ClickType.QUICK_CRAFT);
						flag = true;
					} else if (isPointInRegion(148, 7 + n * 18, 20, 18, mX, mY)) {
						handleMouseClick(null, 200 + n, b, ClickType.QUICK_CRAFT);
						flag = true;
					}
					if (flag) {
						playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F);
						return;
					}
				}
			} catch (Exception e) {}
		}
		super.mouseClicked(mX, mY, b);
	}

	@Override
    public void drawScreen(int mX, int mY, float pt) {
		super.drawScreen(mX, mY, pt);
		EnumRelativeFacing f = getFace(mX, mY);
		if (f != null) {
			ContainerCfg c = (ContainerCfg)inventorySlots;
			int n = toNum(f, c.front);
			if (n >= 0) {
				n = c.cfg[n];
				if (n >= 0) drawHoveringText(I18n.format("jst.msg.gui.c" + n), mX, mY);
			}
		}
	}

	private void drawSide(short idx, int x, int y) {
	    x += (width - xSize) / 2; y += (height - ySize) / 2;
	    drawTexturedModalRect(x, y, 176, idx * 16, 16, 16);
	}

	private boolean isFront(EnumFacing f) {
		return f != null && ((ContainerCfg)inventorySlots).front == f;
	}

	private int toNum(EnumRelativeFacing f, EnumFacing f2) {
		if (f != null) return JSTUtils.getNumFromFacing(f.toFacing(f2));
		return -1;
	}

	private EnumRelativeFacing getFace(int mX, int mY) {
		EnumRelativeFacing f = null;
		if (isPointInRegion(100, 75, 16, 16, mX, mY)) f = EnumRelativeFacing.BACK;
		else if (isPointInRegion(80, 35, 16, 16, mX, mY)) f = EnumRelativeFacing.UP;
		else if (isPointInRegion(80, 75, 16, 16, mX, mY)) f = EnumRelativeFacing.DOWN;
		else if (isPointInRegion(60, 55, 16, 16, mX, mY)) f = EnumRelativeFacing.LEFT;
		else if (isPointInRegion(100, 55, 16, 16, mX, mY)) f = EnumRelativeFacing.RIGHT;
		return f;
	}
}
