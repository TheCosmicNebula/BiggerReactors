package net.roguelogix.biggerreactors.classic.reactor.tiles;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.roguelogix.biggerreactors.classic.reactor.ReactorMultiblockController;
import net.roguelogix.phosphophyllite.multiblock.generic.IOnAssemblyTile;
import net.roguelogix.phosphophyllite.multiblock.generic.IOnDisassemblyTile;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockController;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;
import net.roguelogix.phosphophyllite.util.BlockStates;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.roguelogix.biggerreactors.classic.reactor.blocks.ReactorPowerTap.ConnectionState.*;


@RegisterTileEntity(name = "reactor_power_tap")
public class ReactorPowerTapTile extends ReactorBaseTile implements IEnergyStorage, IOnAssemblyTile, IOnDisassemblyTile {
    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;
    
    public ReactorPowerTapTile() {
        super(TYPE);
    }
    
    
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            return LazyOptional.of(() -> this).cast();
        }
        return super.getCapability(cap, side);
    }
    
    private boolean connected = false;
    Direction powerOutputDirection = null;
    
    private static final EnergyStorage ENERGY_ZERO = new EnergyStorage(0);
    
    private void setConnected(boolean newState) {
        if (newState != connected) {
            connected = newState;
            assert world != null;
            world.setBlockState(pos, getBlockState().with(CONNECTION_STATE_ENUM_PROPERTY, connected ? CONNECTED : DISCONNECTED));
        }
    }
    
    LazyOptional<IEnergyStorage> energyOutput = LazyOptional.empty();
    
    public long distributePower(long toDistribute, boolean simulate) {
        IEnergyStorage e = energyOutput.orElse(ENERGY_ZERO);
        if (e.canReceive()) {
            return e.receiveEnergy((int) toDistribute, simulate);
        }
        return 0;
    }
    
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }
    
    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }
    
    @Override
    public int getEnergyStored() {
        if (controller != null) {
            return (int) controller.simulation().battery().stored();
        }
        return 0;
    }
    
    @Override
    public int getMaxEnergyStored() {
        if (controller != null) {
            return (int) controller.simulation().battery().capacity();
        }
        return 0;
    }
    
    @Override
    public boolean canExtract() {
        return false;
    }
    
    @Override
    public boolean canReceive() {
        return false;
    }
    
    @SuppressWarnings("DuplicatedCode")
    public void neighborChanged() {
        energyOutput = LazyOptional.empty();
        if (powerOutputDirection == null) {
            setConnected(false);
            return;
        }
        assert world != null;
        TileEntity te = world.getTileEntity(pos.offset(powerOutputDirection));
        if (te == null) {
            setConnected(false);
            return;
        }
        energyOutput = te.getCapability(CapabilityEnergy.ENERGY, powerOutputDirection.getOpposite());
        setConnected(energyOutput.isPresent());
    }
    
    @Override
    public void onAssembly() {
        powerOutputDirection = getBlockState().get(BlockStates.FACING);
        neighborChanged();
    }
    
    @Override
    public void onDisassembly() {
        powerOutputDirection = null;
        neighborChanged();
    }
}
