package com.claudeusage.tracker

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/** Pairing secrets at rest, in EncryptedSharedPreferences (AES-256). */
object Prefs {
    private const val FILE = "cut_secure_prefs"

    private fun sp(ctx: Context): SharedPreferences {
        val key = MasterKey.Builder(ctx)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            ctx, FILE, key,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    fun save(ctx: Context, p: Pairing) {
        sp(ctx).edit()
            .putString("url", p.url)
            .putString("account", p.accountId)
            .putString("token", p.readToken)
            .putString("key", p.e2eeKeyB64)
            .apply()
    }

    fun load(ctx: Context): Pairing? {
        val s = sp(ctx)
        val url = s.getString("url", null) ?: return null
        val a = s.getString("account", null) ?: return null
        val t = s.getString("token", null) ?: return null
        val k = s.getString("key", null) ?: return null
        return Pairing(url, a, t, k)
    }

    fun isPaired(ctx: Context): Boolean = load(ctx) != null

    fun clear(ctx: Context) {
        sp(ctx).edit().clear().apply()
    }
}
