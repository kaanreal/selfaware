package dev.kaan.selfaware;

import de.maxhenkel.voicechat.api.VoicechatClientApi;
import de.maxhenkel.voicechat.api.config.ConfigAccessor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleVoiceChatPluginTest {
    @AfterEach
    void clearClientApi() throws Exception {
        Field field = SimpleVoiceChatPlugin.class.getDeclaredField("clientApi");
        field.setAccessible(true);
        field.set(null, null);
    }

    @Test
    void acceptsClientApiPassedByOlderVoiceChatVersions() {
        ConfigAccessor config = (ConfigAccessor) Proxy.newProxyInstance(
                ConfigAccessor.class.getClassLoader(),
                new Class<?>[] {ConfigAccessor.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getBoolean")) {
                        return "show_nametag_icons".equals(args[0]);
                    }
                    return null;
                });
        VoicechatClientApi clientApi = (VoicechatClientApi) Proxy.newProxyInstance(
                VoicechatClientApi.class.getClassLoader(),
                new Class<?>[] {VoicechatClientApi.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getClientConfig")) {
                        return config;
                    }
                    if (method.getName().equals("isDisabled")) {
                        return true;
                    }
                    if (method.getName().equals("isTalking") || method.getName().equals("isWhispering")) {
                        throw new UnsupportedOperationException("optional method unavailable");
                    }
                    if (method.getReturnType() == boolean.class) {
                        return false;
                    }
                    return null;
                });

        new SimpleVoiceChatPlugin().initialize(clientApi);

        assertEquals(SimpleVoiceChatIcon.SPEAKER_OFF, SimpleVoiceChatPlugin.localIcon());
    }
}
