package gregicadditions.machines.multi.simple;

import gregicadditions.GAMaterials;
import gregicadditions.GAUtility;
import gregicadditions.capabilities.impl.GAMultiblockRecipeLogic;
import gregicadditions.capabilities.impl.GARecipeMapMultiblockController;
import gregicadditions.item.components.*;
import gregicadditions.utils.GALog;
import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.multiblock.BlockWorldState;
import gregtech.api.recipes.CountableIngredient;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.unification.material.type.Material;
import gregtech.api.util.GTFluidUtils;
import gregtech.api.util.GTUtility;
import gregtech.api.util.InventoryUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

abstract public class LargeSimpleRecipeMapMultiblockController extends GARecipeMapMultiblockController {

    protected int EUtPercentage = 100;
    protected int durationPercentage = 100;
    protected int chancePercentage = 100;
    protected int stack = 1;
    public long maxVoltage = 0;

    DecimalFormat formatter = new DecimalFormat("#0.00");

    /**
     * @deprecated use {@link LargeSimpleRecipeMapMultiblockController#LargeSimpleRecipeMapMultiblockController(ResourceLocation, RecipeMap, int, int, int, int, boolean, boolean, boolean)
     */
    public LargeSimpleRecipeMapMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap, int EUtPercentage, int durationPercentage, int chancePercentage, int stack) {
        super(metaTileEntityId, recipeMap, false, true, true);
        this.recipeMapWorkable = new LargeSimpleMultiblockRecipeLogic(this, EUtPercentage, durationPercentage, chancePercentage, stack);

        this.EUtPercentage = EUtPercentage;
        this.durationPercentage = durationPercentage;
        this.chancePercentage = chancePercentage;
        this.stack = stack;
    }

    /**
     * Create large multiblock machine for simple machine.
     * <p>
     * Percentage : 80 => 0.8 mean lower
     * Percentage : 120 => 1.2 mean higher
     *
     * @param metaTileEntityId
     * @param recipeMap
     * @param EUtPercentage      should be between 0 ~ Integer.MAX_VALUE, Default should be 100
     * @param durationPercentage should be between 0 ~ Integer.MAX_VALUE, Default should be 100
     * @param chancePercentage   should be between 0 ~ Integer.MAX_VALUE, Default should be 100
     * @param stack              should be between 0 ~ Integer.MAX_VALUE, Default should be 1
     * @param canDistinct
     * @param hasMuffler
     * @param hasMaintenance
     */
    public LargeSimpleRecipeMapMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap, int EUtPercentage, int durationPercentage, int chancePercentage, int stack, boolean hasMuffler, boolean hasMaintenance, boolean canDistinct) {
        super(metaTileEntityId, recipeMap, hasMuffler, hasMaintenance, canDistinct);
        this.recipeMapWorkable = new LargeSimpleMultiblockRecipeLogic(this, EUtPercentage, durationPercentage, chancePercentage, stack);

        this.EUtPercentage = EUtPercentage;
        this.durationPercentage = durationPercentage;
        this.chancePercentage = chancePercentage;
        this.stack = stack;
    }

    /**
     * @deprecated use {@link LargeSimpleRecipeMapMultiblockController#LargeSimpleRecipeMapMultiblockController(ResourceLocation, RecipeMap, int, int, int, int, boolean, boolean, boolean)
     */
    public LargeSimpleRecipeMapMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap, int EUtPercentage, int durationPercentage, int chancePercentage, int stack, boolean hasMuffler) {
        this(metaTileEntityId, recipeMap, EUtPercentage, durationPercentage, chancePercentage, stack, hasMuffler, true, false);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gtadditions.multiblock.universal.tooltip.1", this.recipeMap.getLocalizedName()));
        tooltip.add(I18n.format("gtadditions.multiblock.universal.tooltip.2", formatter.format(this.EUtPercentage / 100.0)));
        tooltip.add(I18n.format("gtadditions.multiblock.universal.tooltip.3", formatter.format(this.durationPercentage / 100.0)));
        tooltip.add(I18n.format("gtadditions.multiblock.universal.tooltip.4", this.stack));
        tooltip.add(I18n.format("gtadditions.multiblock.universal.tooltip.5", this.chancePercentage));
    }

    protected static Material getCasingMaterial(Material defaultMaterial, String materialString) {
        Material mat = Material.MATERIAL_REGISTRY.getObject(materialString);
        if (mat != null && mat.hasFlag(GAMaterials.GENERATE_METAL_CASING)) {
            return mat;
        }
        return defaultMaterial;
    }

    public static Predicate<BlockWorldState> motorPredicate() {
        return (blockWorldState) -> {
            IBlockState blockState = blockWorldState.getBlockState();
            if (!(blockState.getBlock() instanceof MotorCasing)) {
                return false;
            } else {
                MotorCasing motorCasing = (MotorCasing) blockState.getBlock();
                MotorCasing.CasingType tieredCasingType = motorCasing.getState(blockState);
                MotorCasing.CasingType currentCasing = blockWorldState.getMatchContext().getOrPut("Motor", tieredCasingType);
                return currentCasing.getName().equals(tieredCasingType.getName());
            }
        };
    }

    public static Predicate<BlockWorldState> emitterPredicate() {
        return (blockWorldState) -> {
            IBlockState blockState = blockWorldState.getBlockState();
            if (!(blockState.getBlock() instanceof EmitterCasing)) {
                return false;
            } else {
                EmitterCasing motorCasing = (EmitterCasing) blockState.getBlock();
                EmitterCasing.CasingType tieredCasingType = motorCasing.getState(blockState);
                EmitterCasing.CasingType currentCasing = blockWorldState.getMatchContext().getOrPut("Emitter", tieredCasingType);
                return currentCasing.getName().equals(tieredCasingType.getName());
            }
        };
    }

    public static Predicate<BlockWorldState> conveyorPredicate() {
        return (blockWorldState) -> {
            IBlockState blockState = blockWorldState.getBlockState();
            if (!(blockState.getBlock() instanceof ConveyorCasing)) {
                return false;
            } else {
                ConveyorCasing motorCasing = (ConveyorCasing) blockState.getBlock();
                ConveyorCasing.CasingType tieredCasingType = motorCasing.getState(blockState);
                ConveyorCasing.CasingType currentCasing = blockWorldState.getMatchContext().getOrPut("Conveyor", tieredCasingType);
                return currentCasing.getName().equals(tieredCasingType.getName());
            }
        };
    }

    public static Predicate<BlockWorldState> fieldGenPredicate() {
        return (blockWorldState) -> {
            IBlockState blockState = blockWorldState.getBlockState();
            if (!(blockState.getBlock() instanceof FieldGenCasing)) {
                return false;
            } else {
                FieldGenCasing motorCasing = (FieldGenCasing) blockState.getBlock();
                FieldGenCasing.CasingType tieredCasingType = motorCasing.getState(blockState);
                FieldGenCasing.CasingType currentCasing = blockWorldState.getMatchContext().getOrPut("FieldGen", tieredCasingType);
                return currentCasing.getName().equals(tieredCasingType.getName());
            }
        };
    }

    public static Predicate<BlockWorldState> pistonPredicate() {
        return (blockWorldState) -> {
            IBlockState blockState = blockWorldState.getBlockState();
            if (!(blockState.getBlock() instanceof PistonCasing)) {
                return false;
            } else {
                PistonCasing motorCasing = (PistonCasing) blockState.getBlock();
                PistonCasing.CasingType tieredCasingType = motorCasing.getState(blockState);
                PistonCasing.CasingType currentCasing = blockWorldState.getMatchContext().getOrPut("Piston", tieredCasingType);
                return currentCasing.getName().equals(tieredCasingType.getName());
            }
        };
    }

    public static Predicate<BlockWorldState> pumpPredicate() {
        return (blockWorldState) -> {
            IBlockState blockState = blockWorldState.getBlockState();
            if (!(blockState.getBlock() instanceof PumpCasing)) {
                return false;
            } else {
                PumpCasing motorCasing = (PumpCasing) blockState.getBlock();
                PumpCasing.CasingType tieredCasingType = motorCasing.getState(blockState);
                PumpCasing.CasingType currentCasing = blockWorldState.getMatchContext().getOrPut("Pump", tieredCasingType);
                return currentCasing.getName().equals(tieredCasingType.getName());
            }
        };
    }

    public static Predicate<BlockWorldState> robotArmPredicate() {
        return (blockWorldState) -> {
            IBlockState blockState = blockWorldState.getBlockState();
            if (!(blockState.getBlock() instanceof RobotArmCasing)) {
                return false;
            } else {
                RobotArmCasing motorCasing = (RobotArmCasing) blockState.getBlock();
                RobotArmCasing.CasingType tieredCasingType = motorCasing.getState(blockState);
                RobotArmCasing.CasingType currentCasing = blockWorldState.getMatchContext().getOrPut("RobotArm", tieredCasingType);
                return currentCasing.getName().equals(tieredCasingType.getName());
            }
        };
    }

    public static Predicate<BlockWorldState> sensorPredicate() {
        return (blockWorldState) -> {
            IBlockState blockState = blockWorldState.getBlockState();
            if (!(blockState.getBlock() instanceof SensorCasing)) {
                return false;
            } else {
                SensorCasing motorCasing = (SensorCasing) blockState.getBlock();
                SensorCasing.CasingType tieredCasingType = motorCasing.getState(blockState);
                SensorCasing.CasingType currentCasing = blockWorldState.getMatchContext().getOrPut("Sensor", tieredCasingType);
                return currentCasing.getName().equals(tieredCasingType.getName());
            }
        };
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        this.maxVoltage = 0;
        ((LargeSimpleMultiblockRecipeLogic) this.recipeMapWorkable).invalidate();
    }

    @Override
    public boolean checkRecipe(Recipe recipe, boolean consumeIfSuccess) {
        return recipe.getEUt() <= maxVoltage;
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        if (isStructureFormed() && !hasProblems())
            textList.add(new TextComponentTranslation("gregtech.multiblock.universal.framework", this.maxVoltage));
    }

    public static class LargeSimpleMultiblockRecipeLogic extends GAMultiblockRecipeLogic {

        private final int EUtPercentage;
        private final int durationPercentage;
        private final int chancePercentage;
        private final int stack;
        public RecipeMap<?> recipeMap;

        public LargeSimpleMultiblockRecipeLogic(RecipeMapMultiblockController tileEntity, int EUtPercentage, int durationPercentage, int chancePercentage, int stack) {
            this(tileEntity, EUtPercentage, durationPercentage, chancePercentage, stack, 16);
        }

        public LargeSimpleMultiblockRecipeLogic(RecipeMapMultiblockController tileEntity, int EUtPercentage, int durationPercentage, int chancePercentage, int stack, int recipeCacheSize) {
            super(tileEntity, recipeCacheSize);
            this.EUtPercentage = EUtPercentage;
            this.durationPercentage = durationPercentage;
            this.chancePercentage = chancePercentage;
            this.stack = stack;
            this.recipeMap = tileEntity.recipeMap;
        }

        public int getEUtPercentage() {
            return EUtPercentage;
        }

        public int getDurationPercentage() {
            return durationPercentage;
        }

        public int getChancePercentage() {
            return chancePercentage;
        }

        public int getStack() {
            return stack;
        }

        protected List<IItemHandlerModifiable> getInputBuses() {
            RecipeMapMultiblockController controller = (RecipeMapMultiblockController) metaTileEntity;
            return controller.getAbilities(MultiblockAbility.IMPORT_ITEMS);
        }

        /**
         * Used to reset cached values after multiblock structure deforms
         */
        protected void invalidate() {
            lastRecipeIndex = 0;
        }

//        @Override
//        protected boolean trySearchNewRecipeCombined() {
//            long maxVoltage = getMaxVoltage();
//            if (metaTileEntity instanceof LargeSimpleRecipeMapMultiblockController)
//                maxVoltage = ((LargeSimpleRecipeMapMultiblockController) metaTileEntity).maxVoltage;
//            Recipe currentRecipe = null;
//            IItemHandlerModifiable importInventory = getInputInventory();
//            IMultipleTankHandler importFluids = getInputTank();
//            boolean dirty = checkRecipeInputsDirty(importInventory, importFluids);
//            //inverse of logic in normal AbstractRecipeLogic
//            //for MultiSmelter, we can reuse previous recipe if inputs didn't change
//            //otherwise, we need to recompute it for new ingredients
//            //but technically, it means we can cache multi smelter recipe, but changing inputs have more priority
//            if (dirty || forceRecipeRecheck) {
//                this.forceRecipeRecheck = false;
//                //else, try searching new recipe for given inputs
//                currentRecipe = findRecipe(maxVoltage, importInventory, importFluids);
//                if (currentRecipe != null) {
//                    this.previousRecipe.put(currentRecipe);
//                }
//            } else {
//                Recipe foundRecipe = this.previousRecipe.get(importInventory, importFluids);
//                //if previous recipe still matches inputs, try to use it
//                if (foundRecipe != null) {
//                    currentRecipe = foundRecipe;
//                }
//            }
//            if (currentRecipe != null && setupAndConsumeRecipeInputs(currentRecipe)) {
//                setupRecipe(currentRecipe);
//                return true;
//            }
//            return false;
//        }

        @Override
        protected boolean trySearchNewRecipeCombined() {
            long maxVoltage = getMaxVoltage();
            if (metaTileEntity instanceof LargeSimpleRecipeMapMultiblockController)
                maxVoltage = ((LargeSimpleRecipeMapMultiblockController) metaTileEntity).maxVoltage;
            Recipe currentRecipe = null;
            IItemHandlerModifiable importInventory = getInputInventory();
            IMultipleTankHandler importFluids = getInputTank();
            Recipe foundRecipe = this.previousRecipe.get(importInventory, importFluids);
            if (foundRecipe != null) {
                currentRecipe = foundRecipe;
            } else {
                boolean dirty = checkRecipeInputsDirty(importInventory, importFluids);
                if (dirty || this.forceRecipeRecheck) {
                    this.forceRecipeRecheck = false;
                    currentRecipe = findRecipe(maxVoltage, importInventory, importFluids, this.useOptimizedRecipeLookUp);
                    if (currentRecipe != null) {
                        this.previousRecipe.put(currentRecipe);
                        this.previousRecipe.cacheUnutilized();
                    }
                }
            }

            if (currentRecipe == null) {
                return false;
            }
            currentRecipe = createRecipe(maxVoltage, importInventory, importFluids, currentRecipe);
            if (!setupAndConsumeRecipeInputs(currentRecipe)) {
                return false;
            }
            if (foundRecipe != null) {
                this.previousRecipe.cacheUtilized();
            }
            setupRecipe(currentRecipe);
            return true;
        }

        @Override
        protected boolean trySearchNewRecipeDistinct() {
            long maxVoltage = getMaxVoltage();
            Recipe currentRecipe = null;
            List<IItemHandlerModifiable> importInventory = getInputBuses();
            IMultipleTankHandler importFluids = getInputTank();

            // Our caching implementation
            // This guarantees that if we get a recipe cache hit, our efficiency is no different from other machines
            Recipe foundRecipe = this.previousRecipe.get(importInventory.get(lastRecipeIndex), importFluids);
            HashSet<Integer> foundRecipeIndex = new HashSet<>();
            if (foundRecipe != null) {
                currentRecipe = foundRecipe;
                currentRecipe = createRecipe(maxVoltage, importInventory.get(lastRecipeIndex), importFluids, currentRecipe);
                if (setupAndConsumeRecipeInputs(currentRecipe, lastRecipeIndex)) {
                    this.previousRecipe.cacheUtilized();
                    setupRecipe(currentRecipe);
                    return true;
                }
                foundRecipeIndex.add(lastRecipeIndex);
            }

            for (int i = 0; i < importInventory.size(); i++) {
                if (i == lastRecipeIndex) {
                    continue;
                }
                foundRecipe = this.previousRecipe.get(importInventory.get(i), importFluids);
                if (foundRecipe != null) {
                    currentRecipe = foundRecipe;
                    currentRecipe = createRecipe(maxVoltage, importInventory.get(i), importFluids, currentRecipe);
                    if (setupAndConsumeRecipeInputs(currentRecipe, i)) {
                        this.previousRecipe.cacheUtilized();
                        setupRecipe(currentRecipe);
                        return true;
                    }
                    foundRecipeIndex.add(i);
                }
            }

            // On a cache miss, our efficiency is much worse, as it will check
            // each bus individually instead of the combined inventory all at once.
            for (int i = 0; i < importInventory.size(); i++) {
                if (foundRecipeIndex.contains(i)) {
                    continue;
                }
                IItemHandlerModifiable bus = importInventory.get(i);
                boolean dirty = checkRecipeInputsDirty(bus, importFluids, i);
                if (!dirty && !forceRecipeRecheck) {
                    continue;
                }
                this.forceRecipeRecheck = false;
                currentRecipe = findRecipe(maxVoltage, bus, importFluids, this.useOptimizedRecipeLookUp);
                if (currentRecipe == null) {
                    continue;
                }
                this.previousRecipe.put(currentRecipe);
                this.previousRecipe.cacheUnutilized();
                currentRecipe = createRecipe(maxVoltage, bus, importFluids, currentRecipe);
                if (!setupAndConsumeRecipeInputs(currentRecipe, i)) {
                    continue;
                }
                lastRecipeIndex = i;
                setupRecipe(currentRecipe);
                return true;
            }
            return false;
        }

        @Override
        protected Recipe findRecipe(long maxVoltage, IItemHandlerModifiable inputs, IMultipleTankHandler fluidInputs, boolean useOptimizedRecipeLookUp) {
            return super.findRecipe(maxVoltage, inputs, fluidInputs, useOptimizedRecipeLookUp);
//            if (recipe != null)
//                return createRecipe(maxVoltage, inputs, fluidInputs, recipe);
//            return null;
        }

        protected Recipe createRecipe(long maxVoltage, IItemHandlerModifiable inputs, IMultipleTankHandler fluidInputs, Recipe matchingRecipe) {
            int maxItemsLimit = this.stack;
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
                int attemptItemsLimit = this.stack;
                attemptItemsLimit *= tierDiff - i;
                attemptItemsLimit = Math.max(1, attemptItemsLimit);
                attemptItemsLimit = Math.min(minMultiplier, attemptItemsLimit);
                List<CountableIngredient> newRecipeInputs = new ArrayList<>();
                List<FluidStack> newFluidInputs = new ArrayList<>();
                List<ItemStack> outputI = new ArrayList<>();
                List<FluidStack> outputF = new ArrayList<>();
                this.multiplyInputsAndOutputs(newRecipeInputs, newFluidInputs, outputI, outputF, matchingRecipe, attemptItemsLimit);


                RecipeBuilder<?> newRecipe = recipeMap.recipeBuilder();
                copyChancedItemOutputs(newRecipe, matchingRecipe, attemptItemsLimit);

                // determine if there is enough room in the output to fit all of this
                // if there isn't, we can't process this recipe.
                List<ItemStack> totalOutputs = newRecipe.getChancedOutputs().stream().map(Recipe.ChanceEntry::getItemStack).collect(Collectors.toList());
                totalOutputs.addAll(outputI);
                boolean canFitOutputs = InventoryUtils.simulateItemStackMerge(totalOutputs, this.getOutputInventory());
                canFitOutputs = canFitOutputs && GTFluidUtils.simulateFluidStackMerge(outputF, this.getOutputTank());
                if (!canFitOutputs) {
                    continue;
                }

                newRecipe.inputsIngredients(newRecipeInputs)
                        .fluidInputs(newFluidInputs)
                        .outputs(outputI)
                        .fluidOutputs(outputF)
                        .EUt(Math.max(1, EUt * this.EUtPercentage / 100))
                        .duration((int) Math.max(3, duration * (this.durationPercentage / 100.0)));

                return newRecipe.build().getResult();
            }
            return matchingRecipe;
        }

        protected void copyChancedItemOutputs(RecipeBuilder<?> newRecipe, Recipe oldRecipe, int multiplier) {
            for (Recipe.ChanceEntry s : oldRecipe.getChancedOutputs()) {
                int chance = Math.min(10000, s.getChance() * this.chancePercentage / 100);
                int boost = s.getBoostPerTier() * this.chancePercentage / 100;
                IntStream.range(0, multiplier).forEach(value -> {
                    ItemStack itemStack = s.getItemStack().copy();
                    newRecipe.chancedOutput(itemStack, chance, boost);
                });
            }
        }


        protected void findIngredients(Set<ItemStack> countIngredients, IItemHandlerModifiable inputs) {
            for (int slot = 0; slot < inputs.getSlots(); slot++) {
                ItemStack wholeItemStack = inputs.getStackInSlot(slot);
                // skip empty slots
                String name = wholeItemStack.getItem().getUnlocalizedNameInefficiently(wholeItemStack);
                if (name.equals("tile.air"))
                    continue;
                boolean found = false;
                for (ItemStack i : countIngredients) {
                    if (i.isItemEqual(wholeItemStack)) {
                        i.setCount(i.getCount() + wholeItemStack.getCount());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    countIngredients.add(wholeItemStack.copy());
                }
            }
        }

        protected int getMinRatioItem(Set<ItemStack> countIngredients, Recipe r, int maxItemsLimit) {
            int minMultiplier = Integer.MAX_VALUE;
            for (CountableIngredient ci : r.getInputs()) {
                if (ci.getCount() == 0) {
                    continue;
                }
                for (ItemStack wholeItemStack : countIngredients) {
                    if (ci.getIngredient().apply(wholeItemStack)) {
                        int ratio = Math.min(maxItemsLimit, wholeItemStack.getCount() / ci.getCount());
                        if (ratio < minMultiplier) {
                            minMultiplier = ratio;
                        }
                        break;
                    }
                }
            }
            return minMultiplier;
        }

        protected int getMinRatioFluid(Map<String, Integer> countFluid, Recipe r, int maxItemsLimit) {
            int minMultiplier = Integer.MAX_VALUE;
            for (FluidStack fs : r.getFluidInputs()) {
                if (fs.amount != 0) { // skip notConsumable fluids
                    String name = fs.getFluid().getUnlocalizedName();
                    int ratio = Math.min(maxItemsLimit, countFluid.get(name) / fs.amount);
                    if (ratio < minMultiplier) {
                        minMultiplier = ratio;
                    }
                }
            }
            return minMultiplier;
        }

        protected void findFluid(Map<String, Integer> countFluid, IMultipleTankHandler fluidInputs) {
            for (IFluidTank tank : fluidInputs) {
                if (tank.getFluid() != null) {
                    String name = tank.getFluid().getUnlocalizedName();
                    if (countFluid.containsKey(name)) {
                        int existingValue = countFluid.get(name);
                        countFluid.put(name, existingValue + tank.getFluidAmount());
                    } else {
                        countFluid.put(name, tank.getFluidAmount());
                    }
                }
            }
        }

        protected void multiplyInputsAndOutputs(List<CountableIngredient> newRecipeInputs, List<FluidStack> newFluidInputs, List<ItemStack> outputI, List<FluidStack> outputF, Recipe r, int multiplier) {
            for (CountableIngredient ci : r.getInputs()) {
                CountableIngredient newIngredient = new CountableIngredient(ci.getIngredient(), ci.getCount() * multiplier);
                newRecipeInputs.add(newIngredient);
            }
            for (FluidStack fs : r.getFluidInputs()) {
                FluidStack newFluid = new FluidStack(fs.getFluid(), fs.amount * multiplier);
                newFluidInputs.add(newFluid);
            }
            for (ItemStack s : r.getOutputs()) {
                int num = s.getCount() * multiplier;
                ItemStack itemCopy = s.copy();
                itemCopy.setCount(num);
                outputI.add(itemCopy);
            }
            for (FluidStack f : r.getFluidOutputs()) {
                int fluidNum = f.amount * multiplier;
                FluidStack fluidCopy = f.copy();
                fluidCopy.amount = fluidNum;
                outputF.add(fluidCopy);
            }
        }

        protected void setupRecipe(Recipe recipe) {
            long maxVoltage = getMaxVoltage();
            if (metaTileEntity instanceof LargeSimpleRecipeMapMultiblockController)
                maxVoltage = ((LargeSimpleRecipeMapMultiblockController) metaTileEntity).maxVoltage;
            int[] resultOverclock = calculateOverclock(recipe.getEUt(), maxVoltage, recipe.getDuration());
            this.progressTime = 1;
            setMaxProgress(resultOverclock[1]);
            this.recipeEUt = resultOverclock[0];
            this.fluidOutputs = GTUtility.copyFluidList(recipe.getFluidOutputs());
            int tier = getMachineTierForRecipe(recipe);
            this.itemOutputs = GTUtility.copyStackList(recipe.getResultItemOutputs(Integer.MAX_VALUE, random, tier));
            if (this.wasActiveAndNeedsUpdate) {
                this.wasActiveAndNeedsUpdate = false;
            } else {
                this.setActive(true);
            }
        }

    }


}
