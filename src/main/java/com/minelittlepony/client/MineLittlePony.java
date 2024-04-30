package com.minelittlepony.client;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.events.Channel;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.armour.ArmourTextureResolver;
import com.minelittlepony.client.render.MobRenderers;
import com.minelittlepony.client.render.PonyRenderDispatcher;
import com.minelittlepony.common.client.gui.VisibilityMode;
import com.minelittlepony.common.client.gui.element.Button;
import com.minelittlepony.common.client.gui.sprite.TextureSprite;
import com.minelittlepony.common.event.ScreenInitCallback;
import com.minelittlepony.common.event.SkinFilterCallback;
import com.minelittlepony.common.util.GamePaths;

import java.nio.file.Path;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

/**
 * Client Mod implementation
 */
public class MineLittlePony implements ClientModInitializer {

    private static MineLittlePony instance;

    public static final Logger logger = LogManager.getLogger("MineLittlePony");

    private PonyManagerImpl ponyManager;
    private VariatedTextureSupplier variatedTextures;

    private final KeyBinding keyBinding = new KeyBinding("key.minelittlepony.settings", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F9, "key.categories.misc");

    private final PonyRenderDispatcher renderDispatcher = new PonyRenderDispatcher();
    private boolean initialized;

    private boolean hasHdSkins;
    private boolean hasModMenu;

    public MineLittlePony() {
        instance = this;
    }

    /**
     * Gets the global MineLP instance.
     */
    public static MineLittlePony getInstance() {
        return instance;
    }

    public static Identifier id(String name) {
        return new Identifier("minelittlepony", name);
    }

    @Override
    public void onInitializeClient() {
        hasHdSkins = FabricLoader.getInstance().isModLoaded("hdskins");
        hasModMenu = FabricLoader.getInstance().isModLoaded("modmenu");

        PonyConfig config = new ClientPonyConfig(GamePaths.getConfigDirectory().resolve("minelp.json"));
        ponyManager = new PonyManagerImpl(config);
        variatedTextures = new VariatedTextureSupplier();

        KeyBindingHelper.registerKeyBinding(keyBinding);

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(ponyManager);
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(variatedTextures);

        // convert legacy pony skins
        SkinFilterCallback.EVENT.register(new LegacySkinConverter());

        // general events
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
        ScreenInitCallback.EVENT.register(this::onScreenInit);

        config.load();

        Channel.bootstrap();
        ModelType.bootstrap();

        FabricLoader.getInstance().getEntrypoints("minelittlepony", ClientModInitializer.class).forEach(ClientModInitializer::onInitializeClient);
    }

    private void onTick(MinecraftClient client) {
        if (!initialized) {
            initialized = true;
            renderDispatcher.initialise(client.getEntityRenderDispatcher(), false);
        }

        boolean inGame = client.world != null && client.player != null && client.currentScreen == null;
        boolean mainMenu = client.currentScreen instanceof TitleScreen;

        if (!inGame && mainMenu) {
            KeyBinding.updatePressedStates();
        }

        if ((mainMenu || inGame) && keyBinding.isPressed()) {
            client.setScreen(new PonySettingsScreen(client.currentScreen));
        }
    }

    private void onScreenInit(Screen screen, ScreenInitCallback.ButtonList buttons) {
        if (screen instanceof TitleScreen) {
            VisibilityMode mode = ClientPonyConfig.getInstance().horseButton.get();
            boolean show = mode == VisibilityMode.ON || (mode == VisibilityMode.AUTO
                && !(hasHdSkins || hasModMenu
            ));

            if (show) {
                int y = hasHdSkins ? 75 : 50;
                Button button = buttons.addButton(new Button(screen.width - 50, screen.height - y, 20, 20))
                    .onClick(sender -> MinecraftClient.getInstance().setScreen(new PonySettingsScreen(screen)));
                button.getStyle()
                        .setIcon(new TextureSprite()
                                .setPosition(2, 2)
                                .setTexture(new Identifier("minelittlepony", "textures/gui/pony.png"))
                                .setTextureSize(16, 16)
                                .setSize(16, 16))
                        .setTooltip("minelp.options.title", 0, 10);
                button.setY(screen.height - y); // ModMenu
            }
        }
    }

    public PonyManagerImpl getManager() {
        return ponyManager;
    }

    public VariatedTextureSupplier getVariatedTextures() {
        return variatedTextures;
    }

    /**
     * Gets the static pony render manager responsible for all entity renderers.
     */
    public PonyRenderDispatcher getRenderDispatcher() {
        return renderDispatcher;
    }

    private static final class ClientPonyConfig extends PonyConfig {
        public ClientPonyConfig(Path path) {
            super(path);
            MobRenderers.REGISTRY.values().forEach(r -> value("entities", r.name, true));
            disablePonifiedArmour.onChanged(t -> ArmourTextureResolver.INSTANCE.invalidate());
        }

        @Override
        public void save() {
            super.save();
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                player.calculateDimensions();
            }
        }
    }
}

