package gregicadditions.machines.multi.multiblockpart;

import gregicadditions.client.ClientHandler;
import gregtech.api.metatileentity.multiblock.MultiblockControllerBase;
import gregtech.api.render.ICubeRenderer;
import gregtech.api.render.Textures;
import gregtech.common.metatileentities.electric.multiblockpart.MetaTileEntityMultiblockPart;
import net.minecraft.util.ResourceLocation;

public abstract class GAMetaTileEntityMultiblockPart extends MetaTileEntityMultiblockPart {

    private ICubeRenderer hatchTexture = null;

    public GAMetaTileEntityMultiblockPart(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
    }

    @Override
    public ICubeRenderer getBaseTexture() {
        MultiblockControllerBase controller = getController();
        if (controller != null) {
            this.hatchTexture = controller.getBaseTexture(this);
        }
        if (controller == null && this.hatchTexture != null) {
            return this.hatchTexture;
        }
        if (controller == null) {
            this.setPaintingColor(DEFAULT_PAINTING_COLOR);
            return ClientHandler.VOLTAGE_CASINGS[getTier()];
        }
        this.setPaintingColor(0xFFFFFF);
        return controller.getBaseTexture(this);
    }

}
