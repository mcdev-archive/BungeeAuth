package me.loper.bungeeauth.storage;

import me.loper.bungeeauth.BungeeAuthPlugin;
import me.loper.bungeeauth.util.Throwing;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public abstract class AbstractStorage {

    private final BungeeAuthPlugin plugin;

    public AbstractStorage(BungeeAuthPlugin plugin) {
        this.plugin = plugin;
    }

    protected <T> CompletableFuture<T> makeFuture(Callable<T> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.call();
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new CompletionException(e);
            }
        }, this.plugin.getScheduler().async());
    }

    protected CompletableFuture<Void> makeFuture(Throwing.Runnable runnable) {
        return CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new CompletionException(e);
            }
        }, this.plugin.getScheduler().async());
    }

    public abstract void shutdown();

    public boolean isLoaded() {
        throw new RuntimeException(String.format("Method '%s.isLoaded()' must be implemented.", getClass().getName()));
    }
}
