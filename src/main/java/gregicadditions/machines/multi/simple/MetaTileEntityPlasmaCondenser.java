package gregicadditions.machines.multi.simple;

import gregicadditions.capabilities.GregicAdditionsCapabilities;
import gregicadditions.capabilities.impl.GAMultiblockRecipeLogic;
import gregicadditions.item.components.PumpCasing;
import gregicadditions.item.metal.MetalCasing1;
import gregicadditions.recipes.GARecipeMaps;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.multiblock.BlockPattern;
import gregtech.api.multiblock.FactoryBlockPattern;
import gregtech.api.multiblock.PatternMatchContext;
import gregtech.api.recipes.Recipe;
import gregtech.api.render.ICubeRenderer;
import gregtech.api.render.OrientedOverlayRenderer;
import gregtech.api.render.Textures;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.BlockTurbineCasing;
import gregtech.common.blocks.MetaBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;
import java.util.List;

import static gregicadditions.client.ClientHandler.HASTELLOY_N_CASING;
import static gregicadditions.item.GAMetaBlocks.METAL_CASING_1;

public class MetaTileEntityPlasmaCondenser extends LargeSimpleRecipeMapMultiblockController {


    public MetaTileEntityPlasmaCondenser(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GARecipeMaps.PLASMA_CONDENSER_RECIPES,80,80,80,8,false,true,true);


        this.recipeMapWorkable = new LargeSimpleMultiblockRecipeLogic(this,80,80,80,8){
            @Override
            protected long getMaxVoltage() {
                return maxVoltage;
            }
        };
    }

    private static final MultiblockAbility<?>[] ALLOWED_ABILITIES = {
            MultiblockAbility.IMPORT_ITEMS, MultiblockAbility.IMPORT_FLUIDS,
            MultiblockAbility.EXPORT_ITEMS, MultiblockAbility.EXPORT_FLUIDS,
            MultiblockAbility.INPUT_ENERGY, GregicAdditionsCapabilities.MAINTENANCE_HATCH
    };

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("#####", "#XXX#", "#XXX#", "#XXX#", "#####")
                .aisle("#XXX#", "XGAGX", "XAPAX", "XGpGX", "#XXX#")
                .aisle("#XXX#", "XAPAX", "XPPPX", "XpPpX", "#XXX#")
                .aisle("#XXX#", "XGAGX", "XAPAX", "XGpGX", "#XXX#")
                .aisle("#####", "#XXX#", "#XSX#", "#XXX#", "#####")
                .setAmountAtLeast('L', 25)
                .where('S', selfPredicate())
                .where('X', statePredicate(getCasingState()).or(abilityPartPredicate(ALLOWED_ABILITIES)))
                .where('L', statePredicate(getCasingState()))
                .where('G', statePredicate(MetaBlocks.TURBINE_CASING.getState(BlockTurbineCasing.TurbineCasingType.STEEL_GEARBOX)))
                .where('P', statePredicate(MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.TUNGSTENSTEEL_PIPE)))
                .where('A', isAirPredicate())
                .where('#', (tile) -> true)
                .where('p', pumpPredicate())
                .build();
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        PumpCasing.CasingType pump = context.getOrDefault("Pump", PumpCasing.CasingType.PUMP_LV);
        maxVoltage = (long) (Math.pow(4, pump.getTier()) * 8);
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        this.maxVoltage = 0;
    }

    private IBlockState getCasingState() {
        return METAL_CASING_1.getState(MetalCasing1.CasingType.HASTELLOY_N);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return HASTELLOY_N_CASING;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new MetaTileEntityPlasmaCondenser(metaTileEntityId);
    }

    @Nonnull
    @Override
    protected OrientedOverlayRenderer getFrontOverlay() {
        return Textures.PLASMA_ARC_FURNACE_OVERLAY;
    }
}
