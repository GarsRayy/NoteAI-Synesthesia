# 🚀 IMPLEMENTASI PLAN — Ekstensi Fitur & UI Polish (Sprint 3)
## Synesthesia Super-App (KMP)

---

## 📊 RENCANA EKSTENSI FITUR (FEATURE PIPELINE)

| Modul | Fitur / Solusi | Status / Prioritas | Target |
|---|---|---|---|
| **SonicZen** | Bundled Local Audio Assets (Offline-first) | 🔥 Prioritas Utama (Tier 1) | Memutar 4-6 ambient track lokal berformat `.ogg` tanpa API eksternal |
| **Sanctuary** | Emotion-to-Ritual AI Matching | 🔥 Prioritas Utama (Tier 1) | Rekomendasi ritual berbasis data 3 jurnal terakhir via Gemini |
| **Sanctuary** | Worry Vault & 5-4-3-2-1 Grounding | ⚡ Prioritas Menengah (Tier 2) | Interaksi psikologi + animasi "Vault" & validasi Gemini |
| **HomeScreen** | Time-of-Day Adaptive Sky (Light Mode) | 🔥 Prioritas Utama (Tier 1) | Gradient dinamis sesuai jam (Sunrise, Daylight, Golden Hour) |
| **HomeScreen** | Pastel Constellation & Floating Particles | ⚡ Prioritas Menengah (Tier 2) | Kontras UI lebih baik, partikel animasi ganti cloud statis |
| **HomeScreen** | Personalized Welcome Card | 🌟 Bonus | Nama user dari DataStore + Micro-animation pulse |

---

## 🛠️ DETAIL IMPLEMENTASI TEKNIS

### 1. SonicZenScreen: Pengganti Spotify API (Local Audio `.ogg`)
Karena OAuth Spotify terlalu kompleks untuk KMP dan berisiko saat demo, kita menggunakan pendekatan **Bundled Local Assets** yang sangat selaras dengan prinsip *offline-first*.

* **Tindakan:** Pastikan 4-6 file audio *royalty-free* dengan format **`.ogg`** (contoh: `rain.ogg`, `ocean.ogg`, `binaural.ogg`) sudah ditempatkan di dalam direktori resource Android Anda:
  `composeApp/src/androidMain/res/raw/`
  *(Path lokal: `C:\Users\Administrator\AndroidStudioProjects\NoteAI-Synesthesia\composeApp\src\androidMain\res\raw`)*
* **Pendekatan KMP (Expect/Actual):**
    ```kotlin
    // commonMain
    expect fun playAudio(resId: Int)
    expect fun stopAudio()
    
    // androidMain
    actual fun playAudio(resId: Int) {
        // MediaPlayer Android mendeteksi format .ogg secara otomatis
        mediaPlayer = MediaPlayer.create(context, resId).apply { start() }
    }
    
    actual fun stopAudio() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
    ```

### 2. SanctuaryScreen: Pengembangan Fitur Interaktif
Kita akan meng-upgrade Sanctuary dari sekadar kumpulan timer menjadi fitur yang terpersonalisasi oleh AI.

* **Tier 1 (Impressive for Demo):**
    * **Emotion-to-Ritual AI Matching:** Saat masuk ke Sanctuary, Gemini membaca emosi dari 3 jurnal terakhir. Prompt: *"User berada di kuadran Weary selama 2 hari. Rekomendasikan 1 ritual (Deep Ocean Breathing/Meditation/dll) beserta alasannya."*
    * **5-4-3-2-1 Grounding Exercise:** UI interaktif untuk menginput 5 hal yang dilihat, 4 dirasakan, dst. Gemini akan men-generate satu kalimat validasi/penenang di akhir sesi berdasarkan input.
    * **Worry Vault:** Input teks "kekhawatiran", dilanjutkan dengan animasi mengunci tulisan ke dalam *cosmic vault*. Gemini merespons dengan *counter-thought* positif. Teks tidak masuk ke galaksi jurnal.
* **Tier 2 (Bonus UX):**
    * **Zen Garden Canvas:** UI Canvas di mana user bisa menggambar pola di atas "pasir" menggunakan *path drawing*, diiringi auto-play suara ambient.
    * **Daily Cosmic Affirmation:** *Card* besar dengan efek *shimmer* berisi afirmasi harian dari Gemini berdasarkan tren emosi mingguan (Di-cache di DataStore agar tidak hit API berulang).

### 3. HomeScreen: Light Mode Polish & Kontras
Menyelesaikan masalah kontras pada `DaylightSky()` agar bintang putih dan titik memori tidak tenggelam di background terang.

* **Time-of-Day Adaptive Sky:** Background menyesuaikan waktu lokal device.
    ```kotlin
    val hour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
    val skyGradient = when {
        hour in 5..8 -> listOf(Color(0xFFFF8A65), Color(0xFFFFE082)) // Sunrise
        hour in 17..20 -> listOf(Color(0xFFFFB74D), Color(0xFFEF9A9A)) // Golden Hour
        else -> listOf(Color(0xFF87CEEB), Color(0xFFF0F9FF)) // Daylight
    }
    ```
* **Pastel Constellation & Shadow:** Pada Light/Normal Mode di `ConstellationCanvas.kt`, ubah warna titik bintang dari putih menjadi warna pastel (misal: emas `0xFFFFF176` atau lavender `0xFFCE93D8`). Tambahkan ketebalan shadow/glow pada memori.
* **Floating Particles:** Ganti awan statis dengan 15-20 partikel *bokeh* yang bergerak perlahan.
    ```kotlin
    // Layer terpisah di DaylightSky()
    val particles = remember { List(20) { Particle(Random.nextFloat(), Random.nextFloat()) } }
    // Animasikan posisi Y dari 1f ke 0f dengan kecepatan acak
    ```
* **Personal Welcome Card:** Ambil nama dari `UserPreferences` dan ubah teks "Welcome, Stargazer" menjadi spesifik. Tambahkan efek *pulse/heartbeat* pada badge hitungan memori saat data baru masuk.

---

## 🗓️ URUTAN PENGERJAAN YANG DISARANKAN

```text
HARI 1 (Fundamental & Quick Wins):
  ├── SonicZen Audio     → Setup expect/actual untuk MediaPlayer + integrasikan file .ogg di folder raw.
  └── Home Adaptive Sky  → Implementasi background jam & ubah warna Constellation pastel.

HARI 2 (AI Integration - Sanctuary Tier 1):
  ├── Emotion Matching   → Buat use case untuk fetch 3 note terakhir & hit Gemini API.
  └── 5-4-3-2-1 Grounding→ Setup UI interaktif dan prompt validasi Gemini.

HARI 3 (Animasi & UX Polish):
  ├── Worry Vault        → Buat animasi kunci kosmik & integrasi counter-thought AI.
  ├── Floating Particles → Masukkan animasi partikel ke background Light Mode.
  └── Welcome Card       → Hubungkan DataStore nama user ke Header Home.

HARI 4 (Bonus & Cleanup):
  ├── Zen Garden / Affirmation → Kerjakan jika ada sisa waktu.
  └── Testing Offline    → Pastikan SonicZen memutar file .ogg dengan lancar saat matikan WiFi.
## ⚠️ CATATAN PENTING

1. Jaga Prinsip Offline-First: Fitur SonicZen dan rendering HomeScreen harus 100% jalan tanpa internet. Pemanggilan file .ogg dari res lokal menjamin hal ini.
2.  Fallback Gemini: Pada fitur Emotion-to-Ritual atau Worry Vault, siapkan teks fallback lokal jika API Gemini gagal merespons atau device sedang offline (misal: "Rekomendasi hari ini: Deep Breathing").
3. DataStore Cache: Untuk afirmasi harian atau nama user, pastikan UserPreferences diakses menggunakan .collectAsState() agar UI reaktif.
4. Performa Canvas: Hati-hati saat merender partikel floating, pastikan diletakkan di luar rekomposisi yang berat (gunakan remember untuk state partikel).
5. Auto commit dengan pesan yang jelas



---

*Generated for: NoteAI-Synesthesia — Sprint 3 Advanced Features*
*Repository: github.com/GarsRayy/NoteAI-Synesthesia*
