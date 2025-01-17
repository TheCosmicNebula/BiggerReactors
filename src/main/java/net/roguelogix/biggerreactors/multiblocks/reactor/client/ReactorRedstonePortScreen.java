package net.roguelogix.biggerreactors.multiblocks.reactor.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.biggerreactors.client.*;
import net.roguelogix.biggerreactors.multiblocks.reactor.containers.ReactorRedstonePortContainer;
import net.roguelogix.biggerreactors.multiblocks.reactor.state.ReactorRedstonePortSelection;
import net.roguelogix.biggerreactors.multiblocks.reactor.state.ReactorRedstonePortState;
import net.roguelogix.biggerreactors.multiblocks.reactor.state.ReactorRedstonePortTriggers;
import net.roguelogix.phosphophyllite.client.gui.screens.PhosphophylliteScreen;
import net.roguelogix.phosphophyllite.client.gui.elements.InteractiveElement;
import net.roguelogix.phosphophyllite.client.gui.elements.RenderedElement;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class ReactorRedstonePortScreen extends PhosphophylliteScreen<ReactorRedstonePortContainer> {

    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(BiggerReactors.modid, "textures/screen/reactor_redstone_port.png");

    private static final String[] INPUT_TRANSLATIONS = new String[]{
            "screen.biggerreactors.reactor_redstone_port.input_reactor_activity",
            "screen.biggerreactors.reactor_redstone_port.input_control_rod_insertion",
            "screen.biggerreactors.reactor_redstone_port.input_eject_waste"
    };

    private static final String[] OUTPUT_TRANSLATIONS = new String[]{
            "screen.biggerreactors.reactor_redstone_port.output_fuel_temp",
            "screen.biggerreactors.reactor_redstone_port.output_casing_temp",
            "screen.biggerreactors.reactor_redstone_port.output_fuel_enrichment",
            "screen.biggerreactors.reactor_redstone_port.output_fuel_amount",
            "screen.biggerreactors.reactor_redstone_port.output_waste_amount",
            "screen.biggerreactors.reactor_redstone_port.output_output_stored"
    };

    private ReactorRedstonePortState reactorRedstonePortState;

    // Controls:
    InteractiveElement<ReactorRedstonePortContainer> applyChangesButton;
    InteractiveElement<ReactorRedstonePortContainer> revertChangesButton;
    Biselector<ReactorRedstonePortContainer> triggerTypeToggle;
    Triselector<ReactorRedstonePortContainer> triggerModeToggle;
    TextBox<ReactorRedstonePortContainer> textBufferA;
    CommonButton<ReactorRedstonePortContainer> textEnterButtonA;
    TextBox<ReactorRedstonePortContainer> textBufferB;
    CommonButton<ReactorRedstonePortContainer> textEnterButtonB;

    // Symbols:
    RenderedElement<ReactorRedstonePortContainer> selectedTabSymbol;

    public ReactorRedstonePortScreen(ReactorRedstonePortContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title, DEFAULT_TEXTURE, 200, 178);
        // Initialize access port state.
        reactorRedstonePortState = (ReactorRedstonePortState) this.getMenu().getGuiPacket();
    }

    /**
     * Initialize the screen.
     */
    @Override
    public void init() {
        super.init();

        // Set title to be drawn in the center.
        this.titleLabelX = (this.getWidth() / 2) - (this.getFont().width(this.getTitle()) / 2);

        // Initialize tooltips:

        // Initialize controls:
        this.initControls();

        // Initialize gauges:

        // Initialize symbols:
        this.initSymbols();
    }

    /**
     * Initialize controls.
     */
    public void initControls() {
        // Requst names for redstone port:
        // setSelectedTab (int)
        // setTriggerPS (bool)
        // setTriggerAB (bool)
        // setTriggerMode (int)
        // setTextBufferA (String)
        // setTextBufferB (String)
        // revertChanges (void)
        // applyChanges (void)

        // (Left) Add input tab buttons:
        for (int i = 0; i < 3; i++) {
            final int cI = i;
            final InteractiveElement<ReactorRedstonePortContainer> inputTab = new InteractiveElement<>(this, 0, (cI * 25), 25, 24, 206, (cI * 24), new TranslatableComponent(INPUT_TRANSLATIONS[cI] + ".tooltip"));
            inputTab.onMouseReleased = (mX, mY, btn) -> {
                // Click logic. Extra check necessary since this is an "in-class" button.
                if (inputTab.isMouseOver(mX, mY)) {
                    // Mouse is hovering, do the thing.
                    this.getMenu().executeRequest("setSelectedTab", cI);
                    this.reactorRedstonePortState.selectedTab = ReactorRedstonePortSelection.fromInt(cI);
                    // Play the selection sound.
                    inputTab.playSound(SoundEvents.UI_BUTTON_CLICK);
                    return true;
                } else {
                    // It ain't hovered, don't do the thing.
                    return false;
                }
            };
            inputTab.onRender = ((mS, mX, mY) -> {
                // Custom rendering.
                if (inputTab.stateEnable) {
                    // Tab is selected.
                    inputTab.blit(mS, 231, (cI * 24));
                } else {
                    // Tab is not selected.
                    inputTab.blit(mS, 206, (cI * 24));
                }
            });
            inputTab.onTick = () -> {
                // Check if this tab is selected.
                inputTab.stateEnable = (this.reactorRedstonePortState.selectedTab == ReactorRedstonePortSelection.fromInt(cI));
            };
            this.addScreenElement(inputTab);
        }

        // (Right) Add output tab buttons:
        for (int i = 0; i < 6; i++) {
            final int cI = i;
            InteractiveElement<ReactorRedstonePortContainer> outputTab = new InteractiveElement<>(this, 175, (cI * 25), 25, 24, 206, (cI * 24) + 72, new TranslatableComponent(OUTPUT_TRANSLATIONS[cI] + ".tooltip"));
            outputTab.onMouseReleased = (mX, mY, btn) -> {
                // Click logic. Extra check necessary since this is an "in-class" button.
                if (outputTab.isMouseOver(mX, mY)) {
                    // Mouse is hovering, do the thing.
                    this.getMenu().executeRequest("setSelectedTab", cI + 3);
                    this.reactorRedstonePortState.selectedTab = ReactorRedstonePortSelection.fromInt(cI + 3);
                    // Play the selection sound.
                    outputTab.playSound(SoundEvents.UI_BUTTON_CLICK);
                    return true;
                } else {
                    // It ain't hovered, don't do the thing.
                    return false;
                }
            };
            outputTab.onRender = (mS, mX, mY) -> {
                // Custom rendering.
                if (outputTab.stateEnable) {
                    // Tab is selected.
                    outputTab.blit(mS, 231, (cI * 24) + 72);
                } else {
                    // Tab is not selected.
                    outputTab.blit(mS, 206, (cI * 24) + 72);
                }
            };
            outputTab.onTick = () -> {
                // Check if this tab is selected.
                outputTab.stateEnable = (this.reactorRedstonePortState.selectedTab == ReactorRedstonePortSelection.fromInt(cI + 3));
            };
            this.addScreenElement(outputTab);
        }

        // (Bottom) Apply changes button:
        this.applyChangesButton = new InteractiveElement<>(this, 156, 156, 15, 15, 226, 216, new TranslatableComponent("screen.biggerreactors.reactor_redstone_port.apply_changes.tooltip"));
        this.applyChangesButton.onMouseReleased = (mX, mY, btn) -> {
            // Click logic. Extra check necessary since this is an "in-class" button.
            if (this.applyChangesButton.isMouseOver(mX, mY)) {
                // Mouse is hovering, do the thing.

                if (this.reactorRedstonePortState.isInput()) {
                    // This is an input, so update the PS value.
                    this.getMenu().executeRequest("setTriggerPS", this.triggerTypeToggle.getState() != 0);
                } else {
                    // This is an output, so update the AB value.
                    this.getMenu().executeRequest("setTriggerAB", this.triggerTypeToggle.getState() != 0);
                }
                this.getMenu().executeRequest("setTriggerMode", this.triggerModeToggle.getState());


                // Implicitly set the text buffers: if they've changed, the user probably wants to apply them.
                this.getMenu().executeRequest("setTextBufferA", this.textBufferA.getContents().replaceAll("[^\\d.]", ""));
                this.getMenu().executeRequest("setTextBufferB", this.textBufferB.getContents().replaceAll("[^\\d.]", ""));
                // Trigger state change.
                this.getMenu().executeRequest("applyChanges", 0);
                // Play the selection sound.
                this.applyChangesButton.playSound(SoundEvents.UI_BUTTON_CLICK);
                return true;
            } else {
                // It ain't hovered, don't do the thing.
                return false;
            }
        };
        this.applyChangesButton.onRender = (mS, mX, mY) -> {
            // Custom rendering.
            if (this.applyChangesButton.isMouseOver(mX, mY)) {
                // Mouse is hovering, highlight it.
                this.applyChangesButton.blit(mS, 241, 216);
            } else {
                // It ain't hovered, don't highlight.
                this.applyChangesButton.blit(mS, 226, 216);
            }
        };
        this.addScreenElement(this.applyChangesButton);

        // (Bottom) Revert changes button:
        this.revertChangesButton = new InteractiveElement<>(this, 138, 156, 15, 15, 226, 231, new TranslatableComponent("screen.biggerreactors.reactor_redstone_port.revert_changes.tooltip"));
        this.revertChangesButton.onMouseReleased = (mX, mY, btn) -> {
            // Click logic. Extra check necessary since this is an "in-class" button.
            if (this.revertChangesButton.isMouseOver(mX, mY)) {
                // Mouse is hovering, do the thing.
                this.getMenu().executeRequest("revertChanges", 0);
                // Play the selection sound.
                this.revertChangesButton.playSound(SoundEvents.UI_BUTTON_CLICK);
                return true;
            } else {
                // It ain't hovered, don't do the thing.
                return false;
            }
        };
        this.revertChangesButton.onRender = (mS, mX, mY) -> {
            // Custom rendering.
            if (this.revertChangesButton.isMouseOver(mX, mY)) {
                // Mouse is hovering, highlight it.
                this.revertChangesButton.blit(mS, 241, 231);
            } else {
                // It ain't hovered, don't highlight.
                this.revertChangesButton.blit(mS, 226, 231);
            }
        };
        //this.addElement(this.revertChangesButton);

        // (Left) Trigger type toggle:
        this.triggerTypeToggle = new Biselector<>(this, 29, 42, TextComponent.EMPTY,
                () -> (this.reactorRedstonePortState.triggerPS.toBool() || this.reactorRedstonePortState.triggerAB.toBool())
                        ? 1 : 0, SelectorColors.YELLOW, SelectorColors.RED);
        this.triggerTypeToggle.onMouseReleased = (mX, mY, btn) -> {
            // Click logic. Check if PS or AB.
            if (this.reactorRedstonePortState.isInput()) {
                // This is an input, so update the PS value.
                this.getMenu().executeRequest("setTriggerPS", this.triggerTypeToggle.getState() == 0);
            } else {
                // This is an output, so update the AB value.
                this.getMenu().executeRequest("setTriggerAB", this.triggerTypeToggle.getState() == 0);
            }
            return true;
        };
        this.triggerTypeToggle.onTick = () -> {
            // Check if the element should be PS or AB, and update.
            if (this.reactorRedstonePortState.isInput()) {
                // This is an input, so use the PS tooltip.
                this.triggerTypeToggle.tooltip = new TranslatableComponent("screen.biggerreactors.reactor_redstone_port.trigger_type_toggle.ps.tooltip");
            } else {
                // This is an output, so use the AB tooltip.
                this.triggerTypeToggle.tooltip = new TranslatableComponent("screen.biggerreactors.reactor_redstone_port.trigger_type_toggle.ab.tooltip");
            }
        };
        this.addScreenElement(triggerTypeToggle);

        // (Left) Trigger mode toggle:
        this.triggerModeToggle = new Triselector<>(this, 29, 58, new TranslatableComponent("screen.biggerreactors.reactor_terminal.auto_eject_toggle.tooltip"),
                () -> this.reactorRedstonePortState.triggerMode, SelectorColors.GREEN, SelectorColors.RED, SelectorColors.YELLOW);
        this.triggerModeToggle.onMouseReleased = (mX, mY, btn) -> {
            // Click logic.
            this.getMenu().executeRequest("setTriggerMode", this.triggerModeToggle.getState());
            return true;
        };
        this.addScreenElement(this.triggerModeToggle);

        // (Top) Text buffer A:
        this.textBufferA = new TextBox<>(this, this.font, 27, 91, 96, 16, this.reactorRedstonePortState.textBufferA);
        this.addScreenElement(this.textBufferA);

        // (Top) Text buffer A enter button:
        this.textEnterButtonA = new CommonButton<>(this, 135, 92, 17, 14, 61, 130, new TranslatableComponent("screen.biggerreactors.reactor_redstone_port.apply.tooltip"));
        this.textEnterButtonA.onMouseReleased = (mX, mY, btn) -> {
            // Click logic.
            this.getMenu().executeRequest("setTextBufferA", this.textBufferA.getContents().replaceAll("[^\\d.]", ""));
            return true;
        };
        this.addScreenElement(this.textEnterButtonA);

        // (Top) Text buffer B:
        this.textBufferB = new TextBox<>(this, this.font, 27, 122, 96, 16, this.reactorRedstonePortState.textBufferB);
        this.addScreenElement(textBufferB);

        // (Top) Text buffer B enter button:
        this.textEnterButtonB = new CommonButton<>(this, 135, 123, 17, 14, 61, 130, new TranslatableComponent("screen.biggerreactors.reactor_redstone_port.apply.tooltip"));
        this.textEnterButtonB.onMouseReleased = (mX, mY, btn) -> {
            // Click logic.
            this.getMenu().executeRequest("setTextBufferB", this.textBufferB.getContents().replaceAll("[^\\d.]", ""));
            return true;
        };
        this.addScreenElement(this.textEnterButtonB);
    }

    /**
     * Initialize symbols.
     */
    public void initSymbols() {
        this.selectedTabSymbol = new RenderedElement<>(this, 92, 20, 16, 16, 25, 4, TextComponent.EMPTY);
        this.selectedTabSymbol.onTick = () -> {
            // Set tooltip based on type.
            this.selectedTabSymbol.tooltip = new TranslatableComponent((this.reactorRedstonePortState.isInput())
                    ? INPUT_TRANSLATIONS[this.reactorRedstonePortState.selectedTab.toInt()]
                    : OUTPUT_TRANSLATIONS[this.reactorRedstonePortState.selectedTab.toInt() - 3]);
            // Set new uv offset.
            this.selectedTabSymbol.u = (this.reactorRedstonePortState.isInput() ? 235 : 236);
            this.selectedTabSymbol.v = ((this.reactorRedstonePortState.selectedTab.toInt() * 24) + 4);
        };
        this.addScreenElement(this.selectedTabSymbol);
    }

    /**
     * Tick/update this screen.
     */
    @Override
    public void containerTick() {
        super.containerTick();

        // Check what tab is selected.
        switch (this.reactorRedstonePortState.selectedTab) {
            // Default case INPUT_ACTIVITY:
            case INPUT_CONTROL_ROD_INSERTION: {
                this.textBufferA.actionEnable = true;
                this.textEnterButtonA.actionEnable = true;
                if (this.reactorRedstonePortState.triggerPS == ReactorRedstonePortTriggers.PULSE_OR_ABOVE) {
                    this.triggerModeToggle.actionEnable = true;
                    this.textBufferB.actionEnable = false;
                    this.textBufferB.clear();
                    this.textEnterButtonB.actionEnable = false;
                } else {
                    this.triggerModeToggle.actionEnable = false;
                    this.textBufferB.actionEnable = true;
                    this.textEnterButtonB.actionEnable = true;
                }
                break;
            }
            case OUTPUT_FUEL_TEMP:
            case OUTPUT_CASING_TEMP:
            case OUTPUT_FUEL_ENRICHMENT:
            case OUTPUT_FUEL_AMOUNT:
            case OUTPUT_WASTE_AMOUNT:
            case OUTPUT_ENERGY_AMOUNT: {
                this.triggerModeToggle.actionEnable = false;
                this.textBufferA.actionEnable = true;
                this.textEnterButtonA.actionEnable = true;
                this.textBufferB.actionEnable = false;
                this.textBufferB.clear();
                this.textEnterButtonB.actionEnable = false;
                break;
            }
            default: {
                this.triggerModeToggle.actionEnable = false;
                // Disable text buffer A.
                if (this.textBufferA.actionEnable || this.textEnterButtonA.actionEnable) {
                    this.textBufferA.clear();
                    this.textBufferA.actionEnable = false;
                    this.textEnterButtonA.actionEnable = false;
                }
                // Disable text buffer B.
                if (this.textBufferB.actionEnable || this.textEnterButtonB.actionEnable) {
                    this.textBufferB.clear();
                    this.textBufferB.actionEnable = false;
                    this.textEnterButtonB.actionEnable = false;
                }
                break;
            }
        }
    }

    /**
     * Draw the status text for this screen.
     *
     * @param poseStack    The current pose stack.
     * @param mouseX       The x position of the mouse.
     * @param mouseY       The y position of the mouse.
     * @param partialTicks Partial ticks.
     */
    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.render(poseStack, mouseX, mouseY, partialTicks);

        // Render common text.
        if (this.reactorRedstonePortState.isInput()) {
            // Check what type of trigger is used (pulse or signal):
            if (this.reactorRedstonePortState.triggerPS == ReactorRedstonePortTriggers.PULSE_OR_ABOVE) {
                // Text for on pulse:
                this.getFont().draw(poseStack, new TranslatableComponent("screen.biggerreactors.reactor_redstone_port.trigger_type_toggle.ps.on_pulse").getString(), this.getGuiLeft() + 63, this.getGuiTop() + 45, 4210752);
            } else {
                // Text for on signal:
                this.getFont().draw(poseStack, new TranslatableComponent("screen.biggerreactors.reactor_redstone_port.trigger_type_toggle.ps.on_signal").getString(), this.getGuiLeft() + 63, this.getGuiTop() + 45, 4210752);
            }
        } else {
            // Check what type of trigger is used (above or below):
            if (this.reactorRedstonePortState.triggerAB == ReactorRedstonePortTriggers.PULSE_OR_ABOVE) {
                // Text for on above:
                this.getFont().draw(poseStack, new TranslatableComponent("screen.biggerreactors.reactor_redstone_port.trigger_type_toggle.ab.while_above").getString(), this.getGuiLeft() + 63, this.getGuiTop() + 45, 4210752);
            } else {
                // Text for on below:
                this.getFont().draw(poseStack, new TranslatableComponent("screen.biggerreactors.reactor_redstone_port.trigger_type_toggle.ab.while_below").getString(), this.getGuiLeft() + 63, this.getGuiTop() + 45, 4210752);
            }
        }

        // Render tab-specific text.
        switch (this.reactorRedstonePortState.selectedTab) {
            // Default case INPUT_ACTIVITY:
            case INPUT_CONTROL_ROD_INSERTION: {
                // Check trigger type:
                if (this.reactorRedstonePortState.triggerPS == ReactorRedstonePortTriggers.PULSE_OR_ABOVE) {
                    // When set to pulse:
                    if (this.reactorRedstonePortState.triggerMode == 0) {
                        // Insert by (mode A/0):
                        this.getFont().draw(poseStack, new TranslatableComponent("screen.biggerreactors.reactor_redstone_port.trigger_mode_toggle.mode_a").getString(), this.getGuiLeft() + 80, this.getGuiTop() + 62, 4210752);
                        this.getFont().draw(poseStack, new TranslatableComponent("screen.biggerreactors.reactor_redstone_port.text_buffer_a.mode_a").getString(), this.getGuiLeft() + 29, this.getGuiTop() + 82, 4210752);
                    } else if (this.reactorRedstonePortState.triggerMode == 1) {
                        // Retract by (mode B/1):
                        this.getFont().draw(poseStack, new TranslatableComponent("screen.biggerreactors.reactor_redstone_port.trigger_mode_toggle.mode_b").getString(), this.getGuiLeft() + 80, this.getGuiTop() + 62, 4210752);
                        this.getFont().draw(poseStack, new TranslatableComponent("screen.biggerreactors.reactor_redstone_port.text_buffer_a.mode_b").getString(), this.getGuiLeft() + 29, this.getGuiTop() + 82, 4210752);
                    } else {
                        // Set to (mode C/2):
                        this.getFont().draw(poseStack, new TranslatableComponent("screen.biggerreactors.reactor_redstone_port.trigger_mode_toggle.mode_c").getString(), this.getGuiLeft() + 80, this.getGuiTop() + 62, 4210752);
                        this.getFont().draw(poseStack, new TranslatableComponent("screen.biggerreactors.reactor_redstone_port.text_buffer_a.mode_c").getString(), this.getGuiLeft() + 29, this.getGuiTop() + 82, 4210752);
                    }
                } else {
                    // When set to signal:
                    this.getFont().draw(poseStack, new TranslatableComponent("screen.biggerreactors.reactor_redstone_port.input_control_rod_insertion.while_on").getString(), this.getGuiLeft() + 29, this.getGuiTop() + 82, 4210752);
                    this.getFont().draw(poseStack, new TranslatableComponent("screen.biggerreactors.reactor_redstone_port.input_control_rod_insertion.while_off").getString(), this.getGuiLeft() + 29, this.getGuiTop() + 113, 4210752);
                }
                break;
            }
            case OUTPUT_FUEL_TEMP:
            case OUTPUT_CASING_TEMP: {
                this.getFont().draw(poseStack, new TranslatableComponent("screen.biggerreactors.reactor_redstone_port.text_buffer_a.trigger_at").getString(), this.getGuiLeft() + 29, this.getGuiTop() + 82, 4210752);
                this.getFont().draw(poseStack, "\u00B0C", this.getGuiLeft() + 155, this.getGuiTop() + 96, 4210752);
                break;
            }
            case OUTPUT_FUEL_ENRICHMENT:
            case OUTPUT_ENERGY_AMOUNT: {
                this.getFont().draw(poseStack, new TranslatableComponent("screen.biggerreactors.reactor_redstone_port.text_buffer_a.trigger_at").getString(), this.getGuiLeft() + 29, this.getGuiTop() + 82, 4210752);
                this.getFont().draw(poseStack, "%", this.getGuiLeft() + 155, this.getGuiTop() + 96, 4210752);
                break;
            }
            case OUTPUT_FUEL_AMOUNT:
            case OUTPUT_WASTE_AMOUNT: {
                this.getFont().draw(poseStack, new TranslatableComponent("screen.biggerreactors.reactor_redstone_port.text_buffer_a.trigger_at").getString(), this.getGuiLeft() + 29, this.getGuiTop() + 82, 4210752);
                this.getFont().draw(poseStack, "mB", this.getGuiLeft() + 155, this.getGuiTop() + 96, 4210752);
                break;
            }
        }
    }
}
