package dev.kaan.selfaware;

import de.maxhenkel.voicechat.api.ForgeVoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatClientApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.config.ConfigAccessor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@ForgeVoicechatPlugin
public final class SimpleVoiceChatPlugin implements VoicechatPlugin {
    private static volatile VoicechatClientApi clientApi;

    public SimpleVoiceChatPlugin() {}

    @Override
    public String getPluginId() {
        return "selfaware";
    }

    @Override
    public void initialize(VoicechatApi api) {
        clientApi = api instanceof VoicechatClientApi ? (VoicechatClientApi) api : null;
        refreshClientApi();
    }

    public static SimpleVoiceChatIcon localIcon() {
        return localIcon(null);
    }

    public static SimpleVoiceChatIcon localIcon(Object player) {
        if (!SelfawareConfig.svcIconsEnabled()) {
            return null;
        }
        VoicechatClientApi api = getClientApi();
        if (api == null) {
            return null;
        }
        if (!nameTagIconsEnabled(api)) {
            return null;
        }
        try {
            Boolean whispering = readBoolean(api, "isWhispering");
            Boolean talking = readBoolean(api, "isTalking");
            if (whispering == null && player != null) {
                whispering = readTalkCacheBoolean(player, "isWhispering");
            }
            if (talking == null && player != null) {
                talking = readTalkCacheBoolean(player, "isTalking");
            }
            return SimpleVoiceChatIcon.select(Boolean.TRUE.equals(whispering), Boolean.TRUE.equals(talking),
                    Boolean.TRUE.equals(readBoolean(api, "isDisconnected")),
                    Boolean.TRUE.equals(readBoolean(api, "isDisabled")));
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static VoicechatClientApi getClientApi() {
        VoicechatClientApi api = clientApi;
        if (api == null) {
            refreshClientApi();
            api = clientApi;
        }
        return api;
    }

    private static void refreshClientApi() {
        try {
            Class<?> managerClass = Class.forName(
                    "de.maxhenkel.voicechat.intercompatibility.ClientCompatibilityManager");
            Field instanceField = managerClass.getField("INSTANCE");
            Object manager = instanceField.get(null);
            if (manager == null) {
                return;
            }
            Method getClientApi = managerClass.getMethod("getClientApi");
            Object api = getClientApi.invoke(manager);
            if (api instanceof VoicechatClientApi) {
                clientApi = (VoicechatClientApi) api;
            }
        } catch (Throwable ignored) {
            // Simple Voice Chat is optional and its client classes are absent on servers.
        }
    }

    private static boolean nameTagIconsEnabled(VoicechatClientApi api) {
        try {
            ConfigAccessor config = api.getClientConfig();
            return !config.getBoolean("hide_icons", false)
                    && config.getBoolean("show_nametag_icons", true);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static Boolean readBoolean(VoicechatClientApi api, String methodName) {
        try {
            Method method = VoicechatClientApi.class.getMethod(methodName);
            Object value = method.invoke(api);
            return value instanceof Boolean ? (Boolean) value : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static Boolean readTalkCacheBoolean(Object player, String methodName) {
        try {
            Class<?> managerClass = Class.forName(
                    "de.maxhenkel.voicechat.voice.client.ClientManager");
            Object voicechat = managerClass.getMethod("getClient").invoke(null);
            if (voicechat == null) {
                return null;
            }
            Object talkCache = voicechat.getClass().getMethod("getTalkCache").invoke(voicechat);
            if (talkCache == null) {
                return null;
            }
            for (Method method : talkCache.getClass().getMethods()) {
                if (!method.getName().equals(methodName) || method.getParameterTypes().length != 1) {
                    continue;
                }
                Class<?> parameterType = method.getParameterTypes()[0];
                Object argument;
                try {
                    if (parameterType.isInstance(player)) {
                        argument = player;
                    } else if (parameterType == java.util.UUID.class) {
                        Method getUuid = player.getClass().getMethod("getUUID");
                        argument = getUuid.invoke(player);
                    } else {
                        continue;
                    }
                    Object value = method.invoke(talkCache, argument);
                    if (value instanceof Boolean) {
                        return (Boolean) value;
                    }
                } catch (Throwable ignored) {
                    // Try the other overload when a mapped UUID method is unavailable.
                }
            }
        } catch (Throwable ignored) {
            // Older Simple Voice Chat releases keep local talking state in TalkCache.
        }
        return null;
    }

}
