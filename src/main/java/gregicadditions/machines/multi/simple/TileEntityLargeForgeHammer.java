package gregicadditions.machines.multi.simple;

import gregicadditions.GAConfig;
import gregicadditions.GAUtility;
import gregicadditions.capabilities.GregicAdditionsCapabilities;
import gregicadditions.item.components.PistonCasing;
import gregicadditions.item.metal.MetalCasing2;
import gregicadditions.machines.multi.CasingUtils;
import gregicadditions.utils.GALog;
import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.multiblock.BlockPattern;
import gregtech.api.multiblock.FactoryBlockPattern;
import gregtech.api.multiblock.PatternMatchContext;
import gregtech.api.recipes.CountableIngredient;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.render.ICubeRenderer;
import gregtech.api.render.OrientedOverlayRenderer;
import gregtech.api.render.Textures;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.GTFluidUtils;
import gregtech.api.util.InventoryUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static gregicadditions.client.ClientHandler.IRON_CASING;
import static gregicadditions.item.GAMetaBlocks.METAL_CASING_2;
import static gregtech.api.recipes.RecipeMaps.COMPRESSOR_RECIPES;
import static gregtech.api.recipes.RecipeMaps.FORGE_HAMMER_RECIPES;

public class TileEntityLargeForgeHammer extends MultiRecipeMapMultiblockController {

    private static final MultiblockAbility<?>[] ALLOWED_ABILITIES = {
            MultiblockAbility.IMPORT_ITEMS, MultiblockAbility.EXPORT_ITEMS, MultiblockAbility.IMPORT_FLUIDS, MultiblockAbility.INPUT_ENERGY, GregicAdditionsCapabilities.MAINTENANCE_HATCH};


    public TileEntityLargeForgeHammer(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, FORGE_HAMMER_RECIPES, GAConfig.multis.largeForgeHammer.euPercentage, GAConfig.multis.largeForgeHammer.durationPercentage, GAConfig.multis.largeForgeHammer.chancedBoostPercentage, GAConfig.multis.largeForgeHammer.stack,
                new RecipeMap<?>[]{FORGE_HAMMER_RECIPES, COMPRESSOR_RECIPES});
        this.recipeMapWorkable = new LargeForgeHammerLogic(this, GAConfig.multis.largeForgeHammer.euPercentage, GAConfig.multis.largeForgeHammer.durationPercentage, GAConfig.multis.largeForgeHammer.chancedBoostPercentage, GAConfig.multis.largeForgeHammer.stack);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("SXX", "X#X", "XpX", "XXX")
                //.aisle("XXX", "X#X", "XpX", "XXX").setRepeatable(0, 4) //TODO: fix formation of multiblocks in GTCE so that this can function properly
                .setAmountAtLeast('Y', 4)
                .where('S', selfPredicate())
                .where('Y', statePredicate(getCasingState()))
                .where('X', statePredicate(getCasingState()).or(abilityPartPredicate(ALLOWED_ABILITIES)))
                .where('#', isAirPredicate())
                .where('p', pistonPredicate())
                .build();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("gregtech.multiblock.large_forge_hammer.description"));
        super.addInformation(stack, player, tooltip, advanced);
    }

    private static final IBlockState defaultCasingState = METAL_CASING_2.getState(MetalCasing2.CasingType.IRON);
    public static final IBlockState casingState = CasingUtils.getConfigCasingBlockState(GAConfig.multis.largeForgeHammer.casingMaterial, defaultCasingState);


    public IBlockState getCasingState() {
        return casingState;
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return CasingUtils.getConfigCasingTexture(GAConfig.multis.largeForgeHammer.casingMaterial, IRON_CASING);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new TileEntityLargeForgeHammer(metaTileEntityId);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        PistonCasing.CasingType piston = context.getOrDefault("Piston", PistonCasing.CasingType.PISTON_LV);
        int min = piston.getTier();
        maxVoltage = (long) (Math.pow(4, min) * 8);
    }

    @Nonnull
    @Override
    protected OrientedOverlayRenderer getFrontOverlay() {
        return (getRecipeMapIndex() == 0) ? Textures.FORGE_HAMMER_OVERLAY : Textures.COMPRESSOR_OVERLAY;
    }

    @Override
    public OrientedOverlayRenderer getRecipeMapOverlay(int recipeMapIndex) {
        return (getRecipeMapIndex() == 0) ? Textures.FORGE_HAMMER_OVERLAY : Textures.COMPRESSOR_OVERLAY;
    }

    private class LargeForgeHammerLogic extends MultiRecipeMapMultiblockRecipeLogic {

        public LargeForgeHammerLogic(RecipeMapMultiblockController tileEntity, int EUtPercentage, int durationPercentage, int chancePercentage, int stack) {
            super(tileEntity, EUtPercentage, durationPercentage, chancePercentage, stack, ((MultiRecipeMapMultiblockController) tileEntity).getRecipeMaps());
        }

        @Override
        protected Recipe createRecipe(long maxVoltage, IItemHandlerModifiable inputs, IMultipleTankHandler fluidInputs, Recipe matchingRecipe) {
            int maxItemsLimit = GAConfig.multis.largeForgeHammer.stack;
            int EUt;
            int duration;
            int currentTier = getOverclockingTier(maxVoltage);
            int tierNeeded;
            int minMultiplier = Integer.MAX_VALUE;

            tierNeeded = Math.max(1, GAUtility.getTierByVoltage(matchingRecipe.getEUt()));
            maxItemsLimit *= currentTier - tierNeeded;
            maxItemsLimit = Math.max(1, maxItemsLimit);
            if (maxItemsLimit == 1) {
                return matchingRecipe;
            }

            Set<ItemStack> countIngredients = new HashSet<>();
            if (matchingRecipe.getInputs().size() != 0) {
                this.findIngredients(countIngredients, inputs);
                minMultiplier = Math.min(maxItemsLimit, this.getMinRatioItem(countIngredients, matchingRecipe, maxItemsLimit));
            }

            Map<String, Integer> countFluid = new HashMap<>();
            if (matchingRecipe.getFluidInputs().size() != 0) {

                this.findFluid(countFluid, fluidInputs);
                minMultiplier = Math.min(minMultiplier, this.getMinRatioFluid(countFluid, matchingRecipe, maxItemsLimit));
            }

            if (minMultiplier == Integer.MAX_VALUE) {
                GALog.logger.error("Cannot calculate ratio of items for large multiblocks");
                return null;
            }

            EUt = matchingRecipe.getEUt();
            duration = matchingRecipe.getDuration();

            int tierDiff = currentTier - tierNeeded;
            for (int i = 0; i < tierDiff; i++) {
                int attemptItemsLimit = GAConfig.multis.largeForgeHammer.stack;
                attemptItemsLimit *= tierDiff - i;
                attemptItemsLimit = Math.max(1, attemptItemsLimit);
                attemptItemsLimit = Math.min(minMultiplier, attemptItemsLimit);
                List<CountableIngredient> newRecipeInputs = new ArrayList<>();
                List<FluidStack> newFluidInputs = new ArrayList<>();
                List<ItemStack> outputI = new ArrayList<>();
                List<FluidStack> outputF = new ArrayList<>();
                this.multiplyInputsAndOutputs(newRecipeInputs, newFluidInputs, outputI, outputF, matchingRecipe, attemptItemsLimit);
                newFluidInputs.add(Materials.Lubricant.getFluid(attemptItemsLimit)); // Here's the important part!

                RecipeBuilder<?> newRecipe = recipeMap.recipeBuilder();
                copyChancedItemOutputs(newRecipe, matchingRecipe, attemptItemsLimit);

                // determine if there is enough room in the output to fit all of this
                // if there isn't, we can't process this recipe.
                List<ItemStack> totalOutputs = newRecipe.getChancedOutputs().stream().map(Recipe.ChanceEntry::getItemStack).collect(Collectors.toList());
                totalOutputs.addAll(outputI);
                boolean canFitOutputs = InventoryUtils.simulateItemStackMerge(totalOutputs, this.getOutputInventory());
                canFitOutputs = canFitOutputs && GTFluidUtils.simulateFluidStackMerge(outputF, this.getOutputTank());
                if (!canFitOutputs)
                    continue;

                newRecipe.inputsIngredients(newRecipeInputs)
                        .fluidInputs(newFluidInputs)
                        .outputs(outputI)
                        .fluidOutputs(outputF)
                        .EUt(Math.max(1, EUt * this.getEUtPercentage() / 100))
                        .duration((int) Math.max(3, duration * (this.getDurationPercentage() / 100.0)));

                return newRecipe.build().getResult();
            }
            return matchingRecipe;
        }
    }
}