package dohyun22.jst3.client.model;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelBase implements IBakedModel {
	public Collection<ResourceLocation> getDependencies() {
		return Collections.emptyList();
	}

	public Collection<ResourceLocation> getTextures() {
		return Collections.emptyList();
	}

	public IBakedModel bake(IModelState state, VertexFormat format,
			Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		return this;
	}

	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}

	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		throw new UnsupportedOperationException("Don't use this class directly!");
	}

	public boolean isAmbientOcclusion() {
		return true;
	}

	public boolean isGui3d() {
		return false;
	}

	public boolean isBuiltInRenderer() {
		return false;
	}

	public TextureAtlasSprite getParticleTexture() {
		return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
	}

	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

	public ItemOverrideList getOverrides() {
		return ItemOverrideList.NONE;
	}
	
	protected static IBakedModel getMissingModel() {
		return Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager().getMissingModel();
	}
}
