package gregicadditions.capabilities.impl;

import gregicadditions.GAValues;
import gregtech.api.capability.impl.AbstractRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.recipes.RecipeMap;

public abstract class GAAbstractRecipeLogic extends AbstractRecipeLogic {
    public GAAbstractRecipeLogic(MetaTileEntity tileEntity, RecipeMap<?> recipeMap) {
        super(tileEntity, recipeMap);
    }

    @Override
    protected int[] calculateOverclock(int EUt, long voltage, int duration) {
        if (!allowOverclocking) {
            return new int[]{EUt, duration};
        }
        boolean negativeEU = EUt < 0;
        int tier = getOverclockingTier(voltage);
        if (GAValues.V[tier] <= EUt || tier == 0)
            return new int[]{EUt, duration};
        if (negativeEU)
            EUt = -EUt;
            int resultEUt = EUt;
            double resultDuration = duration;
            //do not overclock further if duration is already too small
            while (resultDuration >= 1 && resultEUt <= GAValues.V[tier - 1]) {
                resultEUt *= 4;
                resultDuration /= 2.8;
            }
            return new int[]{negativeEU ? -resultEUt : resultEUt, (int) Math.ceil(resultDuration)};
    }
}
