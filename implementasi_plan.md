# 🚀 IMPLEMENTASI PLAN — Sprint 3: Advanced Features
## Synesthesia Super-App (KMP)

---

## 📊 STATUS CHECK: Apa yang Sudah Ada di Repo?

### ✅ Sprint 3 Deliverables yang SUDAH Terpenuhi

| Deliverable | Status | Bukti di Repo |
|---|---|---|
| Search/Filter functionality | ✅ Done | `HomeViewModel.kt` — `_searchQuery`, `.debounce(300)`, `searchNotesUseCase` |
| API Integration (Enhanced Local) | ✅ Done | `GeminiService.kt` — Gemini 2.5 Flash API, emotion analysis |
| Additional Screen (Settings) | ✅ Done | `SettingsScreen.kt` + `SettingsViewModel.kt` |
| Offline Support | ✅ Done | SQLDelight cache-first, semua data disimpan lokal |
| Bonus Features | ✅ Done | Astronomy Mode, Constellation Canvas, AI Emotion Sensing |
| CI Passing | ✅ Done | `.github/workflows/ci.yml` |

### ❌ Sprint 3 Deliverables yang BELUM / KURANG

| Deliverable | Status | Catatan |
|---|---|---|
| **Search di UI yang visible** | ⚠️ Partial | Search logic ada di VM, tapi `HomeScreen.kt` tidak render search bar |
| **Mood Calendar View** | ❌ Missing | Disebut di `PROJECT_PLAN.md` tapi belum diimplementasi |
| **Interactive Line Charts** | ⚠️ Partial | `InsightsScreen.kt` punya `MoodChart` tapi sangat basic |
| **AI Comparative Reports** | ❌ Missing | `PROJECT_PLAN.md` mention "AI-Generated comparative reports" |
| **Spotify Integration** | ❌ Missing | `SonicZenScreen.kt` hanya dummy tracks tanpa real audio |

---

## 🎯 FITUR YANG PERLU DIKEMBANGKAN (Priority Order)

### P0 — Wajib untuk Nilai Penuh Sprint 3

1. **Search Bar UI** di HomeScreen (logic sudah ada, tinggal UI)
2. **Mood Calendar** — tampilkan warna emosi per hari di kalender bulanan
3. **Enhanced Insights Chart** — line chart interaktif dengan period filter

### P1 — Bonus Features (+10% nilai)

4. **Cognitive Insights 2.0** — Weekly AI Summary + Mood Timeline Graph
5. **Smart Sanctuary** — AI-driven activity recommendations berdasarkan mood data
6. **Animated Breathing Circle** — 4-4-4-4 box breathing dengan neon gradient

---

## 📝 IMPLEMENTATION PROMPTS

Berikut adalah prompt siap-pakai untuk mengimplementasikan setiap fitur ke dalam codebase yang sudah ada.

---

### PROMPT 1 — Search Bar UI di HomeScreen

```
Kamu adalah Kotlin Multiplatform developer yang akan menambahkan Search Bar UI 
ke HomeScreen.kt yang sudah ada di project Synesthesia.

KONTEKS KODE YANG SUDAH ADA:
- HomeViewModel.kt sudah punya: _searchQuery (MutableStateFlow), onSearchQueryChange(), clearSearch()
- HomeUiState.Success punya field: query: String
- searchNotesUseCase sudah terhubung dengan debounce 300ms
- Styling: CelestialBackground, SpaceBlack/GhostWhite theme, RoyalBlue/BrightYellow accent

TUGAS:
Modifikasi HomeScreen.kt untuk menambahkan animated search bar di bagian atas layar.

IMPLEMENTASI DETAIL:
1. Tambahkan state lokal: var isSearchActive by remember { mutableStateOf(false) }
2. Di TopAppBar actions, tambahkan IconButton dengan Icons.Default.Search
3. Saat isSearchActive = true, gunakan AnimatedVisibility(enter = expandHorizontally() + fadeIn()) 
   untuk menampilkan OutlinedTextField search bar
4. Search bar styling:
   - Shape: RoundedCornerShape(50.dp) — pill shape
   - Colors: containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.15f)
   - Border: MaterialTheme.colorScheme.primary dengan alpha 0.5f
   - leadingIcon: Icons.Default.Search dengan tint sesuai theme
   - trailingIcon: jika query tidak kosong tampilkan Icons.Default.Clear
   - placeholder: Text("Search memories...", style italic)
5. onValueChange memanggil viewModel.onSearchQueryChange(it)
6. Saat isSearchActive = false dan query tidak kosong, tampilkan badge kecil di search icon
7. Tambahkan crossfade animation saat toggle search active/inactive
8. Di dalam HomeUiState.Empty, jika query tidak blank, tampilkan pesan "No memories found for query"
   dengan Icon(Icons.Default.SearchOff)

STYLING RULES:
- Gunakan warna dari Theme.kt yang sudah ada (jangan hardcode warna baru)
- Astronomy mode: text putih, border BrightYellow
- Normal mode: text DeepIndigo, border RoyalBlue
- Animasi harus smooth, gunakan animateContentSize() pada Row yang berisi search
```

---

### PROMPT 2 — Mood Calendar View (Screen Baru di InsightsScreen)

```
Kamu adalah Kotlin Multiplatform developer yang akan menambahkan Mood Calendar View
ke InsightsScreen.kt pada project Synesthesia.

KONTEKS KODE YANG SUDAH ADA:
- InsightsViewModel.kt sudah collect semua notes dari repository.getAllNotes()
- Note.kt punya field: emotion (String?), artToken (String?), createdAt (Instant)
- EmotionSystem.kt punya: categories list dengan id (HEP/HEU/LEP/LEU) dan color (hex string)
- InsightsUiState.Success punya: emotionDistribution, weeklyTrend, totalMemories

TUGAS:
Tambahkan MoodCalendar composable ke InsightsScreen dan update InsightsViewModel
untuk menyediakan data calendar.

LANGKAH 1 — Update InsightsViewModel.kt:
Tambahkan field baru ke InsightsUiState.Success:
  val calendarData: Map<Int, String?> // key: dayOfMonth, value: dominant emotion hex color

Di dalam map operator, hitung calendarData:
  val currentMonth = now.toLocalDateTime(tz).month
  val calendarData = notes
    .filter { note -> 
      note.createdAt.toLocalDateTime(tz).month == currentMonth 
    }
    .groupBy { note -> note.createdAt.toLocalDateTime(tz).dayOfMonth }
    .mapValues { (_, dayNotes) ->
      // Ambil emosi yang paling sering muncul di hari itu
      val dominant = dayNotes.groupBy { it.emotion }.maxByOrNull { it.value.size }?.key
      EmotionSystem.categories.find { it.subEmotions.contains(dominant) }?.color
    }

Tambahkan juga:
  val currentMonthName: String = now.toLocalDateTime(tz).month.name
  val currentYear: Int = now.toLocalDateTime(tz).year

LANGKAH 2 — Buat MoodCalendar.kt di presentation/screens/insights/:

@Composable
fun MoodCalendar(
    monthName: String,
    year: Int,
    calendarData: Map<Int, String?>,
    modifier: Modifier = Modifier
) {
  // Header dengan nama bulan dan tahun
  // Grid 7 kolom (Mon-Sun) menggunakan LazyVerticalGrid(GridCells.Fixed(7))
  // Setiap cell adalah kotak dengan:
  //   - Ukuran: 40dp x 40dp
  //   - Background: jika calendarData[day] != null, warna dari hex + copy(alpha = 0.5f)
  //                 jika null/tidak ada note, MaterialTheme.colorScheme.surface.copy(alpha = 0.1f)
  //   - Border: 1dp rounded corner 8dp, warna outline.copy(alpha = 0.2f)
  //   - Text nomor tanggal: center aligned, ukuran 12sp
  //   - Jika hari ini: tambahkan ring/border accent dengan primary color
  // Animasi: setiap cell muncul dengan animateItemPlacement() dan tween(delay = index * 20)
}

STYLING:
- Card wrapping calendar: shape = RoundedCornerShape(28.dp), elevation shadow
- Header row hari (M T W T F S S) dengan typography.labelSmall dan alpha 0.5
- Warna cell mengikuti EmotionSystem: HEP=#FFC107, HEU=#FF5722, LEP=#4CAF50, LEU=#3F51B5
- Tidak ada border keras, cells minimalis dengan background saja

INTEGRASI:
Di InsightsScreen.kt, tambahkan MoodCalendar SETELAH EmotionStatRow list:
  Spacer(Modifier.height(32.dp))
  Text("MOOD CALENDAR", ...)
  Spacer(Modifier.height(16.dp))
  MoodCalendar(
    monthName = state.currentMonthName,
    year = state.currentYear,
    calendarData = state.calendarData
  )
```

---

### PROMPT 3 — Enhanced Insights: Weekly AI Summary + Mood Timeline Graph

```
Kamu adalah Kotlin Multiplatform developer yang akan mengupgrade InsightsScreen.kt
menjadi "Cognitive Insights 2.0" untuk project Synesthesia.

KONTEKS:
- InsightsViewModel sudah ada weeklyTrend: List<Float> (last 7 days note count)
- GeminiService.generateContent() sudah ada
- Existing MoodChart composable hanya plot Float points sederhana

TUGAS A — AI Weekly Summary Card:

1. Di InsightsViewModel.kt, tambahkan function:
   fun generateWeeklySummary(notes: List<Note>) {
     viewModelScope.launch {
       _isGeneratingSummary.value = true
       val last7Notes = notes.filter { 
         it.createdAt > Clock.System.now().minus(7, DateTimeUnit.DAY, tz) 
       }
       if (last7Notes.isEmpty()) { _isGeneratingSummary.value = false; return@launch }
       
       val emotionSummary = last7Notes
         .groupBy { it.emotion }
         .map { "${it.key}: ${it.value.size} kali" }
         .joinToString(", ")
       
       val prompt = """
         Berikan analisis singkat (2-3 kalimat dalam Bahasa Indonesia) tentang 
         kondisi emosi seseorang berdasarkan catatan jurnal 7 hari terakhir ini:
         $emotionSummary
         Sampaikan dengan hangat dan empatik seperti seorang teman.
       """.trimIndent()
       
       geminiService.generateContent(prompt).onSuccess { summary ->
         _weeklySummary.value = summary
       }
       _isGeneratingSummary.value = false
     }
   }
   
   Tambahkan StateFlow: _weeklySummary dan _isGeneratingSummary ke UiState

2. Di InsightsScreen.kt, SEBELUM chart section, tambahkan glassmorphic card:
   Card dengan gradient background (primary.copy(alpha=0.1f) ke secondary.copy(alpha=0.05f))
   Header: Row { Icon(AutoAwesome) + Text("ANALISIS JIWA MINGGUAN", labelLarge) }
   Content: jika isGeneratingSummary → ShimmerEffect placeholder 3 baris
            jika weeklySummary tidak null → Text(weeklySummary, italic, bodyMedium)
            jika null → Button("Generate Analysis") { viewModel.generateWeeklySummary() }

TUGAS B — Enhanced Mood Timeline Graph:

Ganti MoodChart yang ada dengan versi baru yang lebih visual:

@Composable
fun EnhancedMoodChart(
    points: List<Float>,
    emotionColors: List<Color>, // warna per titik berdasarkan emosi dominan hari itu
    modifier: Modifier = Modifier
) {
  // Gunakan Canvas
  // 1. Fill area di bawah garis dengan Brush.verticalGradient dari warna titik ke transparent
  //    Buat Path untuk area fill (moveTo start, lineTo setiap titik, lineTo bottom-right, close)
  //    drawPath dengan Brush gradient
  // 2. Draw garis utama: gunakan drawPath dengan Stroke, warna putih/primary
  // 3. Setiap titik: drawCircle radius 8dp dengan warna emotionColors[index]
  //    + drawCircle radius 12dp style Stroke untuk ring
  // 4. Animasi: gunakan animateFloatAsState untuk progress 0..1 dan clip path berdasarkan progress
  //    sehingga chart "tumbuh" dari kiri ke kanan saat pertama kali muncul
  // 5. Label hari di bawah (Sen Sel Rab Kam Jum Sab Min): Text size 10sp, alpha 0.5
}

Tambahkan period filter tabs di atas chart:
var selectedPeriod by remember { mutableStateOf(Period.WEEKLY) }
enum class Period { WEEKLY, MONTHLY }
Row dengan dua Tab: "7 Hari" dan "30 Hari"
Style: selected = filled chip dengan primary color, unselected = outlined
```

---

### PROMPT 4 — Smart Sanctuary: AI Mood-Based Recommendations

```
Kamu adalah Kotlin Multiplatform developer yang akan mengupgrade SanctuaryScreen.kt
menjadi "Smart Sanctuary" dengan AI-driven recommendations berdasarkan mood data.

KONTEKS:
- SanctuaryScreen.kt sudah ada rituals list dan RitualCard composable
- InsightsViewModel punya access ke notes repository
- EmotionSystem.kt punya categories dengan id HEP/HEU/LEP/LEU

TUGAS:

LANGKAH 1 — Buat SanctuaryViewModel.kt:

class SanctuaryViewModel(
    private val repository: NoteRepository
) : ViewModel() {
    
    // Ambil emosi paling dominan dari 3 hari terakhir
    val moodRecommendation: StateFlow<MoodRecommendation?> = repository.getAllNotes()
        .map { notes ->
            val recent = notes.filter { 
                it.createdAt > Clock.System.now().minus(3, DateTimeUnit.DAY, tz) 
            }
            if (recent.isEmpty()) return@map null
            
            val dominant = recent.groupBy { it.emotion }
                .maxByOrNull { it.value.size }?.key
            
            when {
                dominant?.let { EmotionSystem.getCategoryBySubEmotion(it)?.id } == "HEU" ->
                    MoodRecommendation(
                        title = "AI Mendeteksi Energi Tinggi",
                        message = "Emosi kamu cukup intens akhir-akhir ini. Kami rekomendasikan sesi pernapasan atau meditasi untuk menenangkan pikiran.",
                        suggestedRituals = listOf("breathing", "meditation"),
                        accentColor = Color(0xFFF97316)
                    )
                dominant?.let { EmotionSystem.getCategoryBySubEmotion(it)?.id } == "LEU" ->
                    MoodRecommendation(
                        title = "AI Mendeteksi Energi Rendah",
                        message = "Kamu mungkin butuh sedikit dorongan energi. Coba Energy Boost atau Gratitude ritual untuk mengangkat semangatmu.",
                        suggestedRituals = listOf("energy", "gratitude"),
                        accentColor = Color(0xFF60A5FA)
                    )
                else -> MoodRecommendation(
                    title = "Kondisi Stabil",
                    message = "Energimu terjaga dengan baik. Pertahankan dengan Daily Breathing untuk menjaga keseimbangan.",
                    suggestedRituals = listOf("breathing", "focus"),
                    accentColor = Color(0xFF34D399)
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}

data class MoodRecommendation(
    val title: String,
    val message: String,
    val suggestedRituals: List<String>,
    val accentColor: Color
)

LANGKAH 2 — Update SanctuaryScreen.kt:

Tambahkan di atas Featured Card yang sudah ada:
  val recommendation by viewModel.moodRecommendation.collectAsStateWithLifecycle()
  
  recommendation?.let { rec ->
    Card(
      colors = CardDefaults.cardColors(containerColor = rec.accentColor.copy(alpha = 0.12f)),
      border = BorderStroke(1.dp, rec.accentColor.copy(alpha = 0.4f)),
      shape = RoundedCornerShape(20.dp),
      modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
    ) {
      Row(modifier = Modifier.padding(16.dp)) {
        Icon(Icons.Default.AutoAwesome, tint = rec.accentColor, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Column {
          Text(rec.title, fontWeight = FontWeight.Bold, style = labelLarge, color = rec.accentColor)
          Spacer(Modifier.height(4.dp))
          Text(rec.message, style = bodySmall, color = onBackground.copy(alpha = 0.8f))
        }
      }
    }
  }

Di RitualCard, tambahkan visual "Recommended" badge jika ritual.id ada di recommendation.suggestedRituals:
  jika isRecommended: tambahkan small Surface badge di top-right corner card dengan text "AI Pick ✨"
  dan subtle glow border menggunakan BorderStroke dengan accentColor

LANGKAH 3 — Enhanced Breathing Circle:

Ganti breathing animation yang ada dengan versi premium:

@Composable
fun BreathingCircle(phase: String, breatheScale: Float) {
  Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
    // Layer 1: outer glow ring (blur effect dengan drawBehind)
    Box(
      modifier = Modifier.size(160.dp).scale(breatheScale * 1.3f)
        .drawBehind {
          drawCircle(
            brush = Brush.radialGradient(
              colors = listOf(Color(0xFF34D399).copy(alpha = 0.2f), Color.Transparent)
            )
          )
        }
    )
    // Layer 2: middle ring dengan gradient
    Box(
      modifier = Modifier.size(140.dp).scale(breatheScale)
        .background(
          brush = Brush.radialGradient(
            colors = listOf(Color(0xFF34D399).copy(alpha = 0.3f), Color(0xFF7C3AED).copy(alpha = 0.1f))
          ),
          shape = CircleShape
        )
    )
    // Layer 3: inner solid circle
    Box(
      modifier = Modifier.size(120.dp).scale(breatheScale * 0.9f)
        .background(
          brush = Brush.radialGradient(
            colors = listOf(Color(0xFF34D399).copy(alpha = 0.6f), Color(0xFF34D399).copy(alpha = 0.2f))
          ),
          shape = CircleShape
        ),
      contentAlignment = Alignment.Center
    ) {
      Text(phase, color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp, textAlign = TextAlign.Center)
    }
  }
}

Ubah animasi breathing ke 4-4-4-4 Box Breathing pattern:
  Gunakan LaunchedEffect(isRitualActive) dengan delay-based state machine:
  val phases = listOf("Breathe\nIn" to 4000L, "Hold" to 4000L, "Breathe\nOut" to 4000L, "Rest" to 4000L)
  Loop melalui phases saat active, update phaseText dan targetScale per phase
  Breathe In: targetScale = 1.0f, Hold: 1.0f, Breathe Out: 0.7f, Rest: 0.7f
  Gunakan animateFloatAsState(targetValue = targetScale, animationSpec = tween(durasi phase))
```

---

### PROMPT 5 — Register SanctuaryViewModel di Koin

```
Tambahkan SanctuaryViewModel ke AppModule.kt karena kita buat VM baru.

Di AppModule.kt pada viewModelModule:
  viewModelOf(::SanctuaryViewModel)

Di sharedModules list, pastikan viewModelModule sudah include SanctuaryViewModel.

Di SanctuaryScreen.kt, tambahkan:
  import org.koin.compose.viewmodel.koinViewModel
  val viewModel: SanctuaryViewModel = koinViewModel()

Jangan lupa import SanctuaryViewModel di AppModule.kt.
```

---

### PROMPT 6 — Pull-to-Refresh di HomeScreen (Bonus Feature)

```
Tambahkan Pull-to-Refresh ke HomeScreen.kt sebagai bonus feature Sprint 3.
Ini memenuhi requirement "offline support dengan graceful degradation".

LANGKAH 1 — Update HomeViewModel.kt:
Tambahkan:
  private val _isRefreshing = MutableStateFlow(false)
  val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
  
  fun refresh() {
    viewModelScope.launch {
      _isRefreshing.value = true
      // Data di-refresh otomatis via Flow dari SQLDelight
      // Tambahkan delay kecil untuk UX yang bagus
      kotlinx.coroutines.delay(800)
      _isRefreshing.value = false
    }
  }

LANGKAH 2 — Update HomeScreen.kt:
Tambahkan import: androidx.compose.material3.pulltorefresh.*

Di dalam Box yang wrap ConstellationCanvas:
  val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
  
  PullToRefreshBox(
    isRefreshing = isRefreshing,
    onRefresh = { viewModel.refresh() },
    modifier = Modifier.fillMaxSize()
  ) {
    // Existing ConstellationCanvas dan overlay content di sini
  }

Styling indicator: gunakan default Material3 pull-to-refresh indicator
```

---

### PROMPT 7 — Animasi Shared Element Transition untuk Memory Detail

```
Implementasikan animasi transisi saat navigate dari HomeScreen ke MemoryDetailScreen.
Ini adalah bonus feature "Shared element transitions" yang ada di PROJECT_PLAN.md.

CATATAN: Compose Multiplatform 1.7.0 sudah support SharedTransitionLayout.

LANGKAH 1 — Update AppNavHost.kt:
Wrap NavHost dengan SharedTransitionLayout:
  SharedTransitionLayout {
    NavHost(...) {
      // pass animatedVisibilityScope ke screens yang perlu
    }
  }

LANGKAH 2 — Update ConstellationCanvas.kt atau HomeScreen:
Pada setiap note star/item yang di-click, bungkus dengan:
  Modifier.sharedElement(
    state = rememberSharedContentState(key = "note-${note.id}"),
    animatedVisibilityScope = animatedVisibilityScope
  )

LANGKAH 3 — Update MemoryDetailScreen.kt:
Pada Card utama yang menampilkan note content:
  Modifier.sharedElement(
    state = rememberSharedContentState(key = "note-${noteId}"),
    animatedVisibilityScope = animatedVisibilityScope
  )

CATATAN PENTING:
- SharedTransitionLayout harus di scope yang sama (AppNavHost level)
- Pass LocalSharedTransitionScope.current ke dalam composable children
- Gunakan CompositionLocalProvider untuk pass scope jika perlu
- Fallback: jika implementasi complex, minimal tambahkan 
  EnterTransition dan ExitTransition di NavHost composable blocks:
  composable<Route.MemoryDetail>(
    enterTransition = { slideIntoContainer(SlideDirection.Up) + fadeIn() },
    exitTransition = { slideOutOfContainer(SlideDirection.Down) + fadeOut() }
  )
```

---

## 📋 RUBRIK vs IMPLEMENTASI — Mapping Nilai

| Kriteria | Bobot | Sudah Ada | Perlu Ditambah | Target |
|---|---|---|---|---|
| **Search/Filter** | 25% | Logic VM + debounce ✅ | Search bar UI (Prompt 1) | **25/25** |
| **API/Enhanced Local** | 25% | Gemini emotion analysis ✅ | AI Weekly Summary (Prompt 3) | **25/25** |
| **Offline Support** | 20% | SQLDelight cache-first ✅ | Pull-to-refresh UX (Prompt 6) | **20/20** |
| **Additional Screen** | 15% | Settings screen ✅ | MoodCalendar di Insights (Prompt 2) | **15/15** |
| **Bonus Features** | 15% | Astronomy mode, Constellation ✅ | Smart Sanctuary + Breathing (Prompt 4) | **15/15** |
| **Extra Bonus** | +10% | — | Shared transitions (Prompt 7) | **+10%** |

---

## 🗓️ Urutan Pengerjaan yang Disarankan

```
HARI 1 (2-3 jam):
  ├── Prompt 1: Search Bar UI        → Quick win, visible improvement
  └── Prompt 5: Register Koin        → Setup untuk fitur selanjutnya

HARI 2 (3-4 jam):
  ├── Prompt 2: Mood Calendar        → Visual, eye-catching untuk demo
  └── Prompt 3 Tugas A: AI Summary   → Gemini API sudah ada, tinggal UI

HARI 3 (3-4 jam):
  ├── Prompt 3 Tugas B: Enhanced Chart → Upgrade existing component
  ├── Prompt 4: Smart Sanctuary       → New VM + UI enhancement
  └── Prompt 6: Pull-to-Refresh       → Quick, adds polish

HARI 4 (optional, untuk bonus):
  └── Prompt 7: Shared Transitions    → Bonus +10%
```

---

## ⚠️ CATATAN PENTING

1. **Jangan sentuh** layer domain dan data kecuali untuk menambah field baru ke ViewModel
2. **kotlinx-datetime** sudah ada di dependencies, gunakan untuk kalkulasi tanggal di calendar
3. **Gemini API key** sudah dikonfigurasi via BuildConfig, langsung bisa dipakai
4. **Semua warna** ambil dari `Theme.kt` yang sudah ada (SpaceBlack, RoyalBlue, BrightYellow, dll)
5. **Test di kedua mode**: Normal dan Astronomy setelah setiap implementasi
6. buat branch baru untuk sprint 3 ini kemudian **Commit setelah setiap Prompt** selesai agar Git history tetap clean


---

*Generated for: NoteAI-Synesthesia — Sprint 3 Advanced Features*
*Repository: github.com/GarsRayy/NoteAI-Synesthesia*
