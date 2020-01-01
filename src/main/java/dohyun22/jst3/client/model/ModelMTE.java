package dohyun22.jst3.client.model;

import java.lang.ref.WeakReference;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.blocks.BlockTileEntity;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelMTE extends ModelBase {
	public static final ModelMTE INSTANCE = new ModelMTE();
	private static Cache<String, List<BakedQuad>> modelCache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();
	private static Cache<Integer, IBakedModel> itemModelCache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();

	private ModelMTE() {}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		MetaTileBase te = null;
		if (state instanceof IExtendedBlockState) {
			IExtendedBlockState ext = (IExtendedBlockState) state;
			WeakReference<MetaTileBase> ref = (WeakReference) ext.getValue(BlockTileEntity.TE);
			if (ref != null) te = ref.get();
		}
		
		String key = te == null ? "_ERROR" : te.getModelKey();
		List<BakedQuad> ret = modelCache.getIfPresent(key);
		if (ret == null) {
			ret = te == null ? JSTUtils.makeFullCube(MetaTileBase.getErrorTex()) : te.getModel(false);
			modelCache.put(key, ret);
		}
		return ret;
	}
	
	@Override
	public TextureAtlasSprite getParticleTexture() {
		return FMLClientHandler.instance().getClient().getTextureMapBlocks().getAtlasSprite(JustServerTweak.MODID + ":blocks/tileentity/t1_side");
	}
	
	@Override
	public ItemOverrideList getOverrides() {
		return MTEOverride.INSTANCE;
	}
	
	private static class MTEOverride extends ItemOverrideList {
		public static final MTEOverride INSTANCE;

		public MTEOverride() {
			super((List) ImmutableList.of());
		}

		public IBakedModel handleItemState(IBakedModel om, ItemStack st, World w, EntityLivingBase elb) {
			int id = st.getMetadata();
			IBakedModel m = (IBakedModel) ModelMTE.itemModelCache.getIfPresent(id);
			if (m == null) {
				MetaTileBase mtb = MetaTileBase.getTE(id);
				m = new ModelSimple(mtb == null ? JSTUtils.makeFullCube(MetaTileBase.getErrorTex()) : mtb.getModel(true), ModelSimple.TRANSFORM_BLOCK);
				ModelMTE.itemModelCache.put(id, m);
			}
			return m;
		}

		static {
			INSTANCE = new MTEOverride();
		}
	}

	public static void clearCache() {
		modelCache.invalidateAll();
		itemModelCache.invalidateAll();
	}
}