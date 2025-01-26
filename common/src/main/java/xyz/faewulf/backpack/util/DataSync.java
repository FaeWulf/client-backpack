package xyz.faewulf.backpack.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.util.InstantTypeAdapter;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.inter.*;
import xyz.faewulf.backpack.platform.Services;
import xyz.faewulf.backpack.util.config.ModConfigs;

import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class DataSync {

    private static final String SERVER_URL = "https://faewulf.xyz/api/v1/client_backpack";
    //private static final String SERVER_URL = "http://localhost:8443/api/v1/client_backpack";
    private static final String USER_AGENT = Constants.MOD_ID + "/" + "1.0.0" + " Minecraft/" + Services.PLATFORM.getPlatformName();

    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantTypeAdapter()).create();
    private static Instant lastSync = Instant.EPOCH;
    private static final Object AUTH_LOCK = new Object();
    private static final Object SYNC_LOCK = new Object();
    private static final Executor EXECUTOR = Util.ioPool().forName(Constants.MOD_ID + "$syncData");
    private static Auth auth;

    public static Map<String, String> UPDATE_QUEUE = new HashMap<>();

    private static final Duration SYNC_COOLDOWN = Duration.ofSeconds(10);

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            //.version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(5))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public static String createServerId() {
        BigInteger intA = new BigInteger(128, new Random());
        BigInteger intB = new BigInteger(128, new Random(System.identityHashCode(new Object())));
        return intA.xor(intB).toString(16);
    }

    private static HttpRequest.Builder createRequest(URI uri) {
        Constants.LOG.info("Connecting to server: " + uri);
        return HttpRequest.newBuilder(uri)
                .header("User-Agent", USER_AGENT)
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(5));
    }

    public static boolean syncOnCooldown() {
        return lastSync.plus(SYNC_COOLDOWN).isAfter(Instant.now());
    }

    @Blocking
    private static String createAuthToken() {
        synchronized (AUTH_LOCK) {
            Minecraft client = Minecraft.getInstance();
            if (client.player == null) {
                Constants.LOG.error("[DataSync] client player is null, try later...");
            }
            if (auth == null || auth.isExpired() || auth.isInvalidForClientPlayer()) {
                Constants.LOG.info("Obtaining auth token from server...");

                String serverId = createServerId();
                User session = client.getUser();

                try {
                    client.getMinecraftSessionService().joinServer(Objects.requireNonNull(session.getProfileId()), session.getAccessToken(), serverId);
                } catch (AuthenticationException e) {
                    System.out.println("huh: " + e.toString());
                    //throw new RuntimeException(e);
                }

                String query = HttpAuthenticationService.buildQuery(Map.of("serverId", serverId, "username", session.getName(), "uuid", client.player.getUUID()));
                URI uri = URI.create(SERVER_URL + "/auth?" + query);
                HttpRequest request = createRequest(uri).GET().build();
                var response = CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString()).join();

                System.out.println(response.body());

                if (response.statusCode() >= 400) {
                    Constants.LOG.error("[DataSync] Auth failed");
                    throw new RuntimeException("Failed to authenticate with sync server: " + response.body());
                }

                auth = GSON.fromJson(response.body(), Auth.class);

                System.out.println(auth);

                if (auth.isInvalidForClientPlayer()) {
                    Constants.LOG.error("Authenticated account {} does not match the current player ({}); you likely have a misbehaving account switcher mod installed!", auth.account(), client.player.getUUID());
                }

                Constants.LOG.info("Obtained authentication token for {}, expiry {}", auth.account(), auth.expires());
            }
        }
        return auth.token();
    }

    public static @Nullable SyncUnavailable unavailableReason() {

        if (Minecraft.getInstance().getUser().getType() != User.Type.MSA) {
            return SyncUnavailable.INVALID_ACCOUNT;
        }
        var client = Minecraft.getInstance();
        var netHandler = client.getConnection();
        if (!client.isSingleplayer() && netHandler != null && !netHandler.getConnection().isEncrypted()) {
            return SyncUnavailable.OFFLINE_SERVER;
        }
        return null;
    }

    public static boolean isAvailable() {
        return unavailableReason() == null;
    }

    public static boolean isEnabled() {
        return isAvailable() && ModConfigs._enable_cloud_sync;
    }


    public static CompletableFuture<Void> sync(String uuid, @NotNull BackpackStatus status) {
        if (!isEnabled()) {
            return CompletableFuture.completedFuture(null);
        }

        synchronized (SYNC_LOCK) {
            // Force a 10s cooldown on syncing
            if (syncOnCooldown()) {
                var future = new CompletableFuture<Void>();
                future.completeExceptionally(new SyncingTooFrequentlyException());

                misc.sendSystemToast(
                        Component.translatable("backpack.system.upload.cooldown"),
                        Component.translatable("backpack.system.upload.cooldown.message")
                );
                return future;
            }
            lastSync = Instant.now();
        }

        misc.sendSystemToast(
                Component.translatable("backpack.system.upload.syncing"),
                null
        );

        return uploadData(uuid, status, false);
    }

    public static void requestUpdateData() {
        if (UPDATE_QUEUE.isEmpty()) {
            return;
        }

        Map<String, String> copy = new HashMap<>(UPDATE_QUEUE);
        System.out.println("System: size " + copy.size());

        CompletableFuture.runAsync(() -> {
            getData(copy);
        });

        UPDATE_QUEUE.clear();
    }

    // uuid, username
    public static CompletableFuture<Void> getData(Map<String, String> requests) {
        return CompletableFuture.runAsync(() -> {
            var url = URI.create(SERVER_URL + "/get");

            Constants.LOG.info("Get backpack data for {} users...", requests.size());

            Gson gson = new Gson();
            String json = gson.toJson(requests);

            System.out.println(json);

            HttpRequest request = createRequest(url)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .build();

            System.out.println(request);

            var response = CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString()).join();

            System.out.println(response.body());

            if (response.statusCode() >= 401) {
                Constants.LOG.info("Server API doesn't have any data for the list");
                return;
            }

            getDataRes dataRes = GSON.fromJson(response.body(), getDataRes.class);

            if (dataRes.success() && dataRes.data() != null) {
                dataRes.data().forEach(DataBackPack::updateForPlayer);
            }

            Constants.LOG.debug("Server responded to update: {}", response.body());
            Constants.LOG.info("Download data success");
        }, EXECUTOR);
    }

    public static CompletableFuture<Void> uploadData(String uuid, BackpackStatus config, boolean resyncing) {
        return CompletableFuture.runAsync(() -> {
            var token = createAuthToken();
            var url = URI.create(SERVER_URL + "/upload/" + uuid);
            var json = config.uploadDataJson().toString();

            Constants.LOG.info("Syncing...");

            var request = createRequest(url)
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .header("Authorization", "Bearer " + token)
                    .build();

            var response = CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString()).join();
            if (response.statusCode() == 401 && !resyncing) {
                Constants.LOG.info("Resync...");
                Constants.LOG.warn("Auth token is invalid, attempting to reauth...");
                auth = null;
                uploadData(uuid, config, true).join();
                return;
            } else if (response.statusCode() >= 400) {
                throw new RuntimeException("Server responded " + response.statusCode() + ": " + response.body());
            }

            Constants.LOG.debug("Server responded to update: {}", response.body());
            Constants.LOG.info("Uplaod data success");
        }, EXECUTOR);
    }
}

