package dohyun22.jst3.client.gui;

import org.lwjgl.opengl.GL11;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.container.ContainerLaser;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUILaser extends GUIBase {
	private static final ResourceLocation gui = new ResourceLocation(JustServerTweak.MODID, "textures/gui/laser.png");
	
	public GUILaser(Container con) {
		super(con);
		xSize = 176;
		ySize = 82;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2){
		ContainerLaser con = (ContainerLaser) inventorySlots;
		int ec = Math.max(MathHelper.clamp(con.range, 0, 5) * 400, 100) * (con.smelt ? 2 : 1);
		if (!con.breac) ec = (int)(ec * 0.75F);
		fontRenderer.drawString(ec + " EU", 70, 56, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float t, int mx, int my) {
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(gui);
	    int x = (width - xSize) / 2;
	    int y = (height - ySize) / 2;
	    drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	    
	    ContainerLaser con = (ContainerLaser) this.inventorySlots;
	    int r = MathHelper.clamp(con.range, 0, 5);
	    drawTexturedModalRect(x + 39 + 18 * r, y + 13, 176, 0, 8, 18);
	    if (con.breac) drawTexturedModalRect(x + 70, y + 33, 176, 18, 18, 18);
	    if (con.smelt) drawTexturedModalRect(x + 88, y + 33, 176, 36, 18, 18);
	}
	
	@Override
    public void drawScreen(int mX, int mY, float pt) {
		super.drawScreen(mX, mY, pt);
		if (isPointInRegion(71, 34, 16, 16, mX, mY))
			this.drawHoveringText(I18n.format("jst.msg.laser.mine"), mX, mY);
		if (isPointInRegion(89, 34, 16, 16, mX, mY))
			this.drawHoveringText(I18n.format("jst.msg.laser.autosmelt"), mX, mY);
	}
}
