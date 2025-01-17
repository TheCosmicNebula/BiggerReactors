package net.roguelogix.biggerreactors.multiblocks.reactor.simulationold.modern;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.roguelogix.biggerreactors.registries.ReactorModeratorRegistry;
import net.roguelogix.biggerreactors.multiblocks.reactor.simulationold.IReactorCoolantTank;
import net.roguelogix.biggerreactors.util.FluidTransitionTank;

import javax.annotation.Nonnull;

public class CoolantTank extends FluidTransitionTank implements IReactorCoolantTank, ReactorModeratorRegistry.IModeratorProperties {
    public CoolantTank() {
        super(false);
        transitionUpdate();
    }
    
    @Override
    public void dumpLiquid() {
        dumpTank(IN_TANK);
    }
    
    @Override
    public void dumpVapor() {
        dumpTank(OUT_TANK);
    }
    
    private ReactorModeratorRegistry.IModeratorProperties airProperties = ReactorModeratorRegistry.ModeratorProperties.EMPTY_MODERATOR;
    private ReactorModeratorRegistry.IModeratorProperties liquidProperties = ReactorModeratorRegistry.ModeratorProperties.EMPTY_MODERATOR;
    
    @Override
    protected void transitionUpdate() {
        airProperties = ReactorModeratorRegistry.blockModeratorProperties(Blocks.AIR);
        if (airProperties == null) {
            airProperties = ReactorModeratorRegistry.ModeratorProperties.EMPTY_MODERATOR;
        }
        liquidProperties = airProperties;
        Fluid liquid = inFluid;
        if (liquid != null) {
            liquidProperties = ReactorModeratorRegistry.blockModeratorProperties(liquid.defaultFluidState().createLegacyBlock().getBlock());
            if (liquidProperties == null) {
                liquidProperties = airProperties;
            }
        }
    }
    
    public void setCoolantModerationProperties(@Nonnull ReactorModeratorRegistry.IModeratorProperties liquidProperties) {
        this.liquidProperties = liquidProperties;
    }
    
    @Override
    public double absorption() {
        if (perSideCapacity == 0) {
            return airProperties.absorption();
        }
        double absorption = 0;
        absorption += airProperties.absorption() * ((perSideCapacity) - (inAmount));
        absorption += liquidProperties.absorption() * inAmount;
        absorption /= perSideCapacity;
        return absorption;
    }
    
    @Override
    public double heatEfficiency() {
        if (perSideCapacity == 0) {
            return airProperties.heatEfficiency();
        }
        double heatEfficiency = 0;
        heatEfficiency += airProperties.heatEfficiency() * ((perSideCapacity) - (inAmount));
        heatEfficiency += liquidProperties.heatEfficiency() * inAmount;
        heatEfficiency /= perSideCapacity;
        return heatEfficiency;
    }
    
    @Override
    public double moderation() {
        if (perSideCapacity == 0) {
            return airProperties.moderation();
        }
        double moderation = 0;
        moderation += airProperties.moderation() * ((perSideCapacity) - (inAmount));
        moderation += liquidProperties.moderation() * inAmount;
        moderation /= perSideCapacity;
        return moderation;
    }
    
    @Override
    public double heatConductivity() {
        if (perSideCapacity == 0) {
            return airProperties.heatConductivity();
        }
        double heatConductivity = 0;
        heatConductivity += airProperties.heatConductivity() * ((perSideCapacity) - (inAmount));
        heatConductivity += liquidProperties.heatConductivity() * inAmount;
        heatConductivity /= perSideCapacity;
        return heatConductivity;
    }
}
