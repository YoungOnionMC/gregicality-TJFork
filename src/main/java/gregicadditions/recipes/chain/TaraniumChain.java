package gregicadditions.recipes.chain;

import gregicadditions.GAValues;
import gregtech.api.recipes.ingredients.IntCircuitIngredient;
import gregtech.api.unification.OreDictUnifier;
import net.minecraft.item.ItemStack;

import static gregicadditions.GAMaterials.*;
import static gregicadditions.item.GAMetaItems.*;
import static gregicadditions.recipes.GARecipeMaps.*;
import static gregtech.api.recipes.RecipeMaps.*;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.dust;

public class TaraniumChain {
    public static void init(){
        MIXER_RECIPES.recipeBuilder()
                .input(dust, Stone, 24)
                .fluidInputs(HydrofluoricAcid.getFluid(6000))
                .fluidOutputs(DirtyHexafluorosilicicAcid.getFluid(3000))
                .duration(40)
                .EUt(100)
                .buildAndRegister();

        CENTRIFUGE_RECIPES.recipeBuilder()
                .fluidInputs(DirtyHexafluorosilicicAcid.getFluid(3000))
                .fluidOutputs(DiluteHexafluorosilicicAcid.getFluid(3000))
                .outputs(StoneResidueDust.getItemStack(12))
                .duration(40)
                .EUt(100)
                .buildAndRegister();

        DISTILLATION_RECIPES.recipeBuilder()
                .fluidInputs(DiluteHexafluorosilicicAcid.getFluid(3000))
                .fluidOutputs(Water.getFluid(2000))
                .fluidOutputs(FluorosilicicAcid.getFluid(1000))
                .duration(160)
                .EUt(200)
                .buildAndRegister();

        CHEMICAL_RECIPES.recipeBuilder()
                .inputs(StoneResidueDust.getItemStack(24))
                .fluidInputs(SodiumHydroxideSolution.getFluid(1000))
                .outputs(UncommonResidues.getItemStack())
                .chancedOutput(OreDictUnifier.get(dust, Magnetite, 1), 2500, 0)
                .fluidOutputs(SodiumHydroxideSolution.getFluid(925))
                .fluidOutputs(RedMud.getFluid(75))
                .duration(40)
                .EUt(100)
                .buildAndRegister();

        CHEMICAL_RECIPES.recipeBuilder()
                .fluidInputs(LiquidOxygen.getFluid(2000))
                .fluidInputs(LiquidFluorine.getFluid(2000))
                .notConsumable(MICROFOCUS_X_RAY_TUBE.getStackForm())
                .fluidOutputs(Dioxygendifluoride.getFluid(1000))
                .duration(80)
                .EUt(200)
                .buildAndRegister();

        CHEMICAL_RECIPES.recipeBuilder()
                .inputs(UncommonResidues.getItemStack())
                .fluidInputs(Dioxygendifluoride.getFluid(1000))
                .outputs(PartiallyOxidizedResidues.getItemStack())
                .duration(80)
                .EUt(100)
                .buildAndRegister();

        CENTRIFUGE_RECIPES.recipeBuilder()
                .fluidInputs(DistilledWater.getFluid(10000))
                .inputs(PartiallyOxidizedResidues.getItemStack(10))
                .fluidOutputs(OxidizedResidualSolution.getFluid(10000))
                .outputs(InertResidues.getItemStack())
                .duration(200)
                .EUt(100)
                .buildAndRegister();

        CHEMICAL_DEHYDRATOR_RECIPES.recipeBuilder()
                .fluidInputs(OxidizedResidualSolution.getFluid(2000))
                .outputs(OxidizedResidues.getItemStack())
                .outputs(HeavyOxidizedResidues.getItemStack())
                .duration(80)
                .EUt(3000)
                .buildAndRegister();

        BLAST_RECIPES.recipeBuilder()
                .inputs(OxidizedResidues.getItemStack(10))
                .fluidInputs(Hydrogen.getFluid(60000))
                .outputs(MetallicResidues.getItemStack())
                .fluidOutputs(DiluteHydrofluoricAcid.getFluid(40000))
                .duration(1600)
                .EUt(2000)
                .blastFurnaceTemp(3500)
                .buildAndRegister();

        BLAST_RECIPES.recipeBuilder()
                .inputs(HeavyOxidizedResidues.getItemStack(10))
                .fluidInputs(Hydrogen.getFluid(60000))
                .outputs(HeavyMetallicResidues.getItemStack())
                .fluidOutputs(DiluteHydrofluoricAcid.getFluid(40000))
                .duration(1600)
                .EUt(2000)
                .blastFurnaceTemp(3500)
                .buildAndRegister();

        DISTILLATION_RECIPES.recipeBuilder()
                .fluidInputs(DiluteHydrofluoricAcid.getFluid(2000))
                .fluidOutputs(Water.getFluid(1000))
                .fluidOutputs(HydrofluoricAcid.getFluid(1000))
                .duration(80)
                .EUt(200)
                .buildAndRegister();

        LARGE_CENTRIFUGE_RECIPES.recipeBuilder()
                .inputs(MetallicResidues.getItemStack(10))
                .notConsumable(SEPARATION_ELECTROMAGNET)
                .outputs(DiamagneticResidues.getItemStack(3))
                .outputs(ParamagneticResidues.getItemStack(3))
                .outputs(FerromagneticResidues.getItemStack(3))
                .outputs(UncommonResidues.getItemStack())
                .duration(80)
                .EUt(8000)
                .buildAndRegister();

        LARGE_CENTRIFUGE_RECIPES.recipeBuilder()
                .inputs(HeavyMetallicResidues.getItemStack(10))
                .notConsumable(SEPARATION_ELECTROMAGNET)
                .outputs(HeavyDiamagneticResidues.getItemStack(3))
                .outputs(HeavyParamagneticResidues.getItemStack(3))
                .outputs(HeavyFerromagneticResidues.getItemStack(3))
                .outputs(ExoticHeavyResidues.getItemStack())
                .duration(80)
                .EUt(8000)
                .buildAndRegister();

        CENTRIFUGE_RECIPES.recipeBuilder()
                .inputs(FerromagneticResidues.getItemStack(6))
                .chancedOutput(OreDictUnifier.get(dust, Iron, 1), 2500, 0)
                .chancedOutput(OreDictUnifier.get(dust, Nickel, 1), 2500, 0)
                .chancedOutput(OreDictUnifier.get(dust, Cobalt, 1), 2500, 0)

                .duration(100)
                .EUt(3000)
                .buildAndRegister();

        CENTRIFUGE_RECIPES.recipeBuilder()
                .inputs(DiamagneticResidues.getItemStack(6))
                .chancedOutput(OreDictUnifier.get(dust, Calcium, 1), 2500, 0)
                .chancedOutput(OreDictUnifier.get(dust, Zinc, 1), 2500, 0)
                .chancedOutput(OreDictUnifier.get(dust, Copper, 1), 2500, 0)
                .chancedOutput(OreDictUnifier.get(dust, Gallium, 1), 2500, 0)
                .chancedOutput(OreDictUnifier.get(dust, Beryllium, 1), 2500, 0)
                .chancedOutput(OreDictUnifier.get(dust, Tin, 1), 2500, 0)

                .duration(100)
                .EUt(3000)
                .buildAndRegister();

        CENTRIFUGE_RECIPES.recipeBuilder()
                .inputs(ParamagneticResidues.getItemStack(6))
                .chancedOutput(OreDictUnifier.get(dust, Sodium, 1), 2500, 0)
                .chancedOutput(OreDictUnifier.get(dust, Potassium, 1), 2500, 0)
                .chancedOutput(OreDictUnifier.get(dust, Magnesium, 1), 2500, 0)
                .chancedOutput(OreDictUnifier.get(dust, Titanium, 1), 2500, 0)
                .chancedOutput(OreDictUnifier.get(dust, Vanadium, 1), 2500, 0)
                .chancedOutput(OreDictUnifier.get(dust, Manganese, 1), 2500, 0)

                .duration(100)
                .EUt(3000)
                .buildAndRegister();

        CENTRIFUGE_RECIPES.recipeBuilder()
                .inputs(HeavyParamagneticResidues.getItemStack(6))
                .chancedOutput(OreDictUnifier.get(dust, ThoriumRadioactive.getMaterial(), 1), 2500, 0)
                .chancedOutput(OreDictUnifier.get(dust, UraniumRadioactive.getMaterial(), 1), 2500, 0)
                .chancedOutput(OreDictUnifier.get(dust, Tungsten, 1), 2500, 0)
                .chancedOutput(OreDictUnifier.get(dust, Hafnium, 1), 2500, 0)
                .chancedOutput(OreDictUnifier.get(dust, Tantalum, 1), 2500, 0)
                .chancedOutput(OreDictUnifier.get(dust, Thallium, 1), 2500, 0)

                .duration(120)
                .EUt(3000)
                .buildAndRegister();

        CENTRIFUGE_RECIPES.recipeBuilder()
                .inputs(HeavyDiamagneticResidues.getItemStack(6))
                .chancedOutput(OreDictUnifier.get(dust, Lead, 1), 2500, 0)
                .chancedOutput(OreDictUnifier.get(dust, Cadmium, 1), 2500, 0)
                .chancedOutput(OreDictUnifier.get(dust, Indium, 1), 2500, 0)
                .chancedOutput(OreDictUnifier.get(dust, Gold, 1), 2500, 0)
                .chancedOutput(OreDictUnifier.get(dust, Bismuth, 1), 2500, 0)
                .fluidOutputs(Mercury.getFluid(36))
                .duration(120)
                .EUt(3000)
                .buildAndRegister();

        CENTRIFUGE_RECIPES.recipeBuilder()
                .inputs(HeavyFerromagneticResidues.getItemStack(6))
                .chancedOutput(OreDictUnifier.get(dust, Dysprosium, 1), 2500, 0)

                .duration(120)
                .EUt(3000)
                .buildAndRegister();

        MIXER_RECIPES.recipeBuilder()
                .fluidInputs(DistilledWater.getFluid(2000))
                .inputs(ExoticHeavyResidues.getItemStack(16))
                .input(dust, SodiumHydroxide, 3)
                .inputs(PROTONATED_FULLERENE_SIEVING_MATRIX.getStackForm())
                .fluidOutputs(SodiumHydroxideSolution.getFluid(1000))
                .outputs(SATURATED_FULLERENE_SIEVING_MATRIX.getStackForm())
                .duration(40)
                .EUt(2000000)
                .buildAndRegister();

        LARGE_CHEMICAL_RECIPES.recipeBuilder()
                .inputs(InertResidues.getItemStack(10))
                .notConsumable(FluoroantimonicAcid.getFluid(0))
                .outputs(CleanInertResidues.getItemStack(10))
                .output(dust, NaquadricCompound)
                .duration(320)
                .EUt(200)
                .buildAndRegister();

        CHEMICAL_RECIPES.recipeBuilder()
                .fluidInputs(Tritium.getFluid(1000))
                .fluidInputs(Hydrogen.getFluid(1000))
                .fluidOutputs(TritiumHydride.getFluid(1000))
                .duration(160)
                .EUt(2000)
                .buildAndRegister();

        DISTILLATION_RECIPES.recipeBuilder()
                .fluidInputs(TritiumHydride.getFluid(10000))
                .fluidOutputs(Helium3Hydride.getFluid(100))
                .fluidOutputs(TritiumHydride.getFluid(9900))
                .duration(800)
                .EUt(200)
                .buildAndRegister();

        MIXER_RECIPES.recipeBuilder()
                .inputs(CleanInertResidues.getItemStack())
                .fluidInputs(Helium3Hydride.getFluid(1000))
                .fluidOutputs(UltraacidicResidueSolution.getFluid(1000))
                .duration(160)
                .EUt(2000)
                .buildAndRegister();

        LARGE_CHEMICAL_RECIPES.recipeBuilder()
                .fluidInputs(UltraacidicResidueSolution.getFluid(2000))
                .fluidInputs(LiquidOxygen.getFluid(4000))
                .fluidInputs(LiquidXenon.getFluid(1000))
                .fluidOutputs(XenicAcid.getFluid(1000))
                .fluidOutputs(DustyLiquidHelium3.getFluid(2000))
                .duration(120)
                .EUt(2000)
                .buildAndRegister();

        // 2 H2XeO4 -> 2 Xe + H2O + H2O2 + O3 + 2 O
        ELECTROLYZER_RECIPES.recipeBuilder()
                .fluidInputs(XenicAcid.getFluid(2000))
                .fluidOutputs(Xenon.getFluid(2000))
                .fluidOutputs(Water.getFluid(1000))
                .fluidOutputs(Ozone.getFluid(1000))
                .fluidOutputs(HydrogenPeroxide.getFluid(1000))
                .fluidOutputs(Oxygen.getFluid(2000))
                .duration(120)
                .EUt(500)
                .buildAndRegister();

        CENTRIFUGE_RECIPES.recipeBuilder()
                .fluidInputs(DustyLiquidHelium3.getFluid(1000))
                .fluidOutputs(TaraniumEnrichedLHelium3.getFluid(100))
                .fluidOutputs(TaraniumSemidepletedLHelium3.getFluid(300))
                .fluidOutputs(TaraniumDepletedLHelium3.getFluid(600))
                .duration(400)
                .EUt(3000)
                .buildAndRegister();

        FUSION_RECIPES.recipeBuilder()
                .fluidInputs(TaraniumEnrichedLHelium3.getFluid(1000))
                .fluidInputs(Helium3.getFluid(1000))
                .fluidOutputs(TaraniumRichDustyHeliumPlasma.getFluid(3000))
                .duration(160)
                .EUt(7680)
                .EUToStart(480000000)
                .buildAndRegister();


        ADV_FUSION_RECIPES.recipeBuilder().duration(100).EUt(16000000).coilTier(1).euStart(1200000000L).euReturn(75)
                .fluidInputs(TaraniumEnrichedLHelium3.getFluid(1000))
                .fluidInputs(Helium3.getFluid(1000))
                .fluidOutputs(TaraniumRichDustyHeliumPlasma.getFluid(3000))
                .buildAndRegister();

        LARGE_CENTRIFUGE_RECIPES.recipeBuilder()
                .fluidInputs(TaraniumRichDustyHeliumPlasma.getFluid(3000))
                .notConsumable(SEPARATION_ELECTROMAGNET.getStackForm())
                .fluidOutputs(TaraniumRichHelium4.getPlasma(500))
                .fluidOutputs(Hydrogen.getPlasma(2000))
                .fluidOutputs(TaraniumDepletedHeliumPlasma.getFluid(500))
                .duration(80)
                .EUt(2000)
                .buildAndRegister();

        MIXER_RECIPES.recipeBuilder()
                .fluidInputs(Helium3.getPlasma(1000))
                .fluidInputs(TaraniumDepletedLHelium3.getFluid(1000))
                .fluidOutputs(TaraniumDepletedHeliumPlasma.getFluid(2000))
                .duration(160)
                .EUt(2000)
                .buildAndRegister();

        LARGE_CENTRIFUGE_RECIPES.recipeBuilder()
                .fluidInputs(TaraniumDepletedHeliumPlasma.getFluid(10000))
                .notConsumable(SEPARATION_ELECTROMAGNET.getStackForm())
                .fluidOutputs(Helium3.getPlasma(5000))
                .outputs(CleanInertResidues.getItemStack(2))
                .duration(160)
                .EUt(2000)
                .buildAndRegister();

        CENTRIFUGE_RECIPES.recipeBuilder()
                .fluidInputs(TaraniumSemidepletedLHelium3.getFluid(1000))
                .fluidOutputs(TaraniumEnrichedLHelium3.getFluid(100))
                .fluidOutputs(TaraniumDepletedLHelium3.getFluid(900))
                .duration(400)
                .EUt(3000)
                .buildAndRegister();

        CENTRIFUGE_RECIPES.recipeBuilder()
                .fluidInputs(TaraniumRichHelium4.getFluid(400))
                .output(dust, Taranium,4)
                .fluidOutputs(TaraniumPoorLiquidHelium.getFluid(400))
                .duration(20)
                .EUt(8000)
                .buildAndRegister();

        LARGE_CENTRIFUGE_RECIPES.recipeBuilder()
                .input(dust, Stone,3)
                .chancedOutput(OreDictUnifier.get(dust,Taranium),1000,0)
                .chancedOutput(OreDictUnifier.get(dust,Taranium),1000,0)
                .chancedOutput(OreDictUnifier.get(dust,Taranium),1000,0)
                .duration(50)
                .EUt(GAValues.VA[GAValues.UXV])
                .buildAndRegister();


        MIXER_RECIPES.recipeBuilder()
                .fluidInputs(TaraniumPoorLiquidHelium.getFluid(1000))
                .fluidInputs(LiquidHelium3.getFluid(200))
                .fluidOutputs(TaraniumPoorLiquidHeliumMix.getFluid(1200))
                .duration(80)
                .EUt(8000)
                .buildAndRegister();

        CENTRIFUGE_RECIPES.recipeBuilder()
                .fluidInputs(TaraniumPoorLiquidHeliumMix.getFluid(1200))
                .fluidOutputs(LiquidHelium.getFluid(1000))
                .fluidOutputs(DustyLiquidHelium3.getFluid(200))
                .duration(80)
                .EUt(8000)
                .buildAndRegister();

        PLASMA_CONDENSER_RECIPES.recipeBuilder()
                .fluidInputs(LiquidHelium.getFluid(100))
                .fluidInputs(TaraniumRichHelium4.getPlasma(1000))
                .fluidOutputs(TaraniumRichHelium4.getFluid(1000))
                .fluidOutputs(Helium.getFluid(100))
                .notConsumable(new IntCircuitIngredient(1))
                .duration(80)
                .EUt(8000)
                .buildAndRegister();

        PLASMA_CONDENSER_RECIPES.recipeBuilder()
                .fluidInputs(LiquidHelium.getFluid(100))
                .fluidInputs(Hydrogen.getPlasma(1000))
                .fluidOutputs(Hydrogen.getFluid(1000))
                .fluidOutputs(Helium.getFluid(100))
                .notConsumable(new IntCircuitIngredient(1))
                .duration(80)
                .EUt(8000)
                .buildAndRegister();

        VACUUM_RECIPES.recipeBuilder().duration(30).EUt(480)
                .fluidInputs(Fluorine.getFluid(1000))
                .fluidOutputs(LiquidFluorine.getFluid(1000))
                .buildAndRegister();

        VACUUM_RECIPES.recipeBuilder().duration(30).EUt(480)
                .fluidInputs(Xenon.getFluid(1000))
                .fluidOutputs(LiquidXenon.getFluid(1000))
                .buildAndRegister();

        FLUID_HEATER_RECIPES.recipeBuilder()
                .fluidInputs(Helium3.getFluid(1000))
                .fluidOutputs(Helium3.getPlasma(1000))
                .circuitMeta(0)
                .duration(60)
                .EUt(8000)
                .buildAndRegister();

    }
}
