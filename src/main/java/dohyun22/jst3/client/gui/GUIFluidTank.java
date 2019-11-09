package dohyun22.jst3.client.gui;

import java.io.IOException;
import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.compat.jei.JEISupport;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUIFluidTank extends GUIBase {
	private static final ResourceLocation gui = new ResourceLocation(JustServerTweak.MODID, "textures/gui/tank.png");
	private final String[] jei;
	
	public GUIFluidTank(Container con, String[] rjei) {
		super(con);
		jei = rjei;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(gui);
	    int x = (width - xSize) / 2;
	    int y = (height - ySize) / 2;
	    drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
	
	@Override
	protected void mouseClicked(int mX, int mY, int b) throws IOException {
		if (b == 0 && isPointInRegion(106, 39, 18, 8, mX, mY) && jei != null && jei.length > 0 && Loader.isModLoaded("jei")) {
			JEISupport.loadJEI(Arrays.asList(jei));
			return;
		}
		super.mouseClicked(mX, mY, b);
	}
	
	@Override
    public void drawScreen(int mX, int mY, float pt) {
		super.drawScreen(mX, mY, pt);
		if (jei != null && jei.length > 0 && isPointInRegion(106, 39, 18, 8, mX, mY) && Loader.isModLoaded("jei"))
			drawHoveringText(I18n.format("jei.tooltip.show.recipes"), mX, mY);
	}
}
