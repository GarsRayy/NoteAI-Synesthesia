# 📋 IMPLEMENTASI PLAN — SPRINT 4: Polish & Testing
## Proyek: Synesthesia (NoteAI-KMP) | Pengembangan Aplikasi Mobile — ITERA 2025/2026

---

## 🔍 ANALISIS KONDISI REPO vs REQUIREMENT SPRINT 4

### Ringkasan Gap Analysis

| Komponen | Requirement Sprint 4 | Status Repo Saat Ini | Gap |
|---|---|---|---|
| **Bug Fixes** | No crashes, all features functional | Beberapa potensi bug teridentifikasi | ⚠️ PERLU FIX |
| **UI Polish** | Consistent design, proper states, edge cases | Sudah ada UI tapi belum ada loading/empty/error state yang konsisten di semua screen | ⚠️ PERLU POLISH |
| **Unit Tests** | 10+ tests, meaningful assertions | Ada ~8 test (NoteRepositoryTest + HomeViewModelTest) tapi beberapa masih placeholder/kosong | ⚠️ KURANG |
| **UI Tests** | 3+ tests, critical flows covered | **BELUM ADA SAMA SEKALI** | ❌ WAJIB BUAT |
| **Coverage** | 50%+ coverage achieved | Kover plugin belum dikonfigurasi | ❌ WAJIB SETUP |
| **README** | Updated dengan test instructions | README ada tapi belum ada bagian testing | ⚠️ PERLU UPDATE |

### Bug & Issue Yang Teridentifikasi di Repo

1. **`HomeViewModelTest.kt` — Line `category filter should filter notes`** → Test body kosong (`// ... (existing test) ...`), ini akan menyebabkan test count tidak terpenuhi.
2. **`AddNoteViewModel.kt`** → Ada `viewModelScope.launch` nested di dalam `launch` block yang sudah ada — potensi memory leak / double execution saat offline save.
3. **`InsightsScreen.kt` — `MoodCalendar`** → `dayOfWeekOffset` menggunakan `now.month` padahal harusnya `LocalDate(year, month, 1).dayOfWeek` — buggy untuk bulan selain bulan saat ini.
4. **`ConstellationCanvas.kt`** → `notePositions` pakai `remember(notes)` tapi `hubPositions` pakai `mutableStateMapOf` tanpa key, potensi stale data saat notes berubah.
5. **`HomeViewModelTest.kt`** → `testScheduler` dipakai tapi tidak di-import — akan compile error.
6. **`SonicZenScreen.kt`** → Audio loop infinite jika user tidak tap stop — tidak ada timeout guard yang benar.
7. **`AddNoteScreen.kt`** → Tidak ada validasi jika content kosong sebelum trigger AI analysis — API call yang sia-sia.
8. **Semua Screen** → Tidak ada `LaunchedEffect` untuk error snackbar di InsightsScreen, SanctuaryScreen, MemoryDetailScreen.

---

## 🎯 DELIVERABLES YANG HARUS DIPENUHI (dari slide)

Berdasarkan rubrik penilaian Sprint 4 (bobot 5%):

| Komponen | Bobot | Target |
|---|---|---|
| Bug Fixes | 25% | No crashes, all features functional |
| UI Polish | 25% | Consistent, proper states, edge cases |
| Unit Tests | 25% | **10+ tests**, meaningful assertions |
| UI Tests | 15% | **3+ tests**, critical flows |
| Coverage | 10% | **50%+** (bonus 70%+ = +10%) |
| Bonus | +10% | 70%+ coverage |

---

## 📁 STRUKTUR FILE YANG AKAN DIBUAT / DIMODIFIKASI

```
composeApp/
├── build.gradle.kts                          [MODIFIKASI — tambah Kover plugin]
├── src/
│   ├── commonMain/kotlin/.../
│   │   ├── core/util/Extensions.kt           [MODIFIKASI — tambah edge case helpers]
│   │   ├── presentation/
│   │   │   ├── components/
│   │   │   │   ├── NoteComponents.kt         [MODIFIKASI — tambah LoadingState, ErrorState, EmptyState]
│   │   │   │   └── EdgeCaseComponents.kt     [BARU — komponen reusable untuk semua state]
│   │   │   ├── screens/
│   │   │   │   ├── addnote/
│   │   │   │   │   ├── AddNoteViewModel.kt   [MODIFIKASI — fix bug nested launch + validasi]
│   │   │   │   │   └── AddNoteScreen.kt      [MODIFIKASI — tambah proper loading/error UI]
│   │   │   │   ├── home/
│   │   │   │   │   └── HomeScreen.kt         [MODIFIKASI — fix edge cases, polish UI]
│   │   │   │   ├── insights/
│   │   │   │   │   ├── InsightsScreen.kt     [MODIFIKASI — fix MoodCalendar bug]
│   │   │   │   │   └── InsightsViewModel.kt  [MODIFIKASI — tambah error state]
│   │   │   │   ├── detail/
│   │   │   │   │   └── MemoryDetailScreen.kt [MODIFIKASI — polish UI states]
│   │   │   │   └── sanctuary/
│   │   │   │       └── SanctuaryScreen.kt    [MODIFIKASI — fix timing bug]
│   ├── commonTest/kotlin/.../
│   │   ├── data/repository/
│   │   │   └── NoteRepositoryTest.kt         [MODIFIKASI — lengkapi menjadi 7+ tests]
│   │   ├── domain/usecase/
│   │   │   └── NoteUseCaseTest.kt            [BARU — 5+ tests untuk use cases]
│   │   └── presentation/
│   │       ├── HomeViewModelTest.kt          [MODIFIKASI — fix compile error + lengkapi]
│   │       └── AddNoteViewModelTest.kt       [BARU — 3+ tests]
│   └── androidTest/kotlin/.../               [BARU — folder untuk UI tests]
│       └── presentation/
│           └── UITest.kt                     [BARU — 3+ UI tests dengan Compose Test]
├── gradle/
│   └── libs.versions.toml                    [MODIFIKASI — tambah Kover + androidTest deps]
└── README.md                                 [MODIFIKASI — tambah seksi Testing]
```

---

## 🛠️ IMPLEMENTASI DETAIL — LANGKAH PER LANGKAH

---

### FASE 1: Setup Testing Infrastructure (PRIORITAS TERTINGGI)

#### 1.1 Konfigurasi Kover Plugin di `build.gradle.kts`

**File:** `composeApp/build.gradle.kts`

Tambahkan plugin dan konfigurasi Kover untuk code coverage report:

```kotlin
// Di blok plugins {}
alias(libs.plugins.kover)

// Di akhir file, tambahkan blok baru:
kover {
    reports {
        filters {
            excludes {
                classes(
                    // Exclude generated SQLDelight
                    "*.data.local.*",
                    // Exclude Compose generated
                    "*.ComposableSingletons*",
                    // Exclude DI
                    "*.di.*"
                )
            }
        }
        verify {
            rule {
                minBound(50) // Minimum 50% line coverage
            }
        }
    }
}
```

**Di `gradle/libs.versions.toml`**, tambahkan:
```toml
[versions]
kover = "0.8.3"

[plugins]
kover = { id = "org.jetbrains.kotlinx.kover", version.ref = "kover" }
```

**Di root `build.gradle.kts`**, tambahkan:
```kotlin
alias(libs.plugins.kover) apply false
```

#### 1.2 Tambahkan Dependency UI Testing

**File:** `gradle/libs.versions.toml`

```toml
[versions]
compose-test = "1.7.0"

[libraries]
# Compose UI Testing
compose-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4", version.ref = "compose-test" }
compose-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest", version.ref = "compose-test" }
```

**File:** `composeApp/build.gradle.kts` — di `androidMain.dependencies`:
```kotlin
androidTestImplementation(libs.compose.ui.test.junit4)
debugImplementation(libs.compose.ui.test.manifest)
```

---

### FASE 2: Bug Fixes (P0 — Harus Selesai Duluan)

#### 2.1 Fix Bug `AddNoteViewModel.kt` — Nested Launch & Validasi

**File:** `composeApp/src/commonMain/kotlin/.../presentation/screens/addnote/AddNoteViewModel.kt`

**Bug:** Pada offline fallback, ada `viewModelScope.launch` di dalam `viewModelScope.launch` yang sudah berjalan. Ini menyebabkan coroutine context yang salah.

**Fix — Ganti bagian `saveNote()` offline fallback:**
```kotlin
fun saveNote() {
    val state = _uiState.value
    
    // TAMBAHKAN VALIDASI AWAL
    if (state.content.isBlank()) {
        viewModelScope.launch {
            _events.emit(AddNoteEvent.Error("Tuliskan curhatanmu terlebih dahulu"))
        }
        return
    }
    
    // Minimal content length check
    if (state.content.length < 5) {
        viewModelScope.launch {
            _events.emit(AddNoteEvent.Error("Ceritamu terlalu singkat, tulis lebih dalam yuk"))
        }
        return
    }

    _uiState.update { it.copy(isAnalyzing = true) }
    
    viewModelScope.launch {
        val analysisResult = geminiService.analyzeEmotion(state.content)
        
        analysisResult.onSuccess { analysis ->
            _uiState.update { it.copy(isAnalyzing = false, isSaving = true) }
            val category = EmotionSystem.categories.find { it.id == analysis.emotionQuadrant }
            val note = Note(
                id = currentNoteId ?: 0,
                title = analysis.autoTitle,
                content = if (state.isParaphraseEnabled) analysis.paraphrasedContent else state.content,
                category = state.category,
                color = state.color,
                emotion = analysis.subEmotion,
                artToken = category?.name ?: EmotionSystem.categories.first().name,
                aiResonance = analysis.summary,
                createdAt = if (currentNoteId == null) Clock.System.now() else state.createdAt,
                updatedAt = Clock.System.now()
            )
            saveNoteUseCase(note)
                .onSuccess { _events.emit(AddNoteEvent.NoteSaved) }
                .onFailure { error ->
                    _uiState.update { it.copy(isSaving = false) }
                    _events.emit(AddNoteEvent.Error(error.message ?: "Gagal menyimpan"))
                }
        }.onFailure { error ->
            // FIX: Hapus nested launch, langsung jalankan di scope yang sama
            _uiState.update { it.copy(isAnalyzing = false, isSaving = true) }
            _events.emit(AddNoteEvent.Error("AI offline — menyimpan dengan mode lokal"))
            
            // Buat fallback note LANGSUNG (tidak nested launch)
            val fallbackNote = Note(
                id = currentNoteId ?: 0,
                title = if (state.content.length > 30) state.content.take(30) + "..." else state.content,
                content = state.content,
                category = state.category,
                color = state.color,
                emotion = state.selectedSubEmotion ?: "Peaceful",
                artToken = state.selectedMainCategory?.name ?: EmotionSystem.categories.first { it.id == "LEP" }.name,
                aiResonance = "Saved offline — AI will resonate later.",
                createdAt = if (currentNoteId == null) Clock.System.now() else state.createdAt,
                updatedAt = Clock.System.now()
            )
            
            saveNoteUseCase(fallbackNote)
                .onSuccess { _events.emit(AddNoteEvent.NoteSaved) }
                .onFailure { saveError ->
                    _uiState.update { it.copy(isSaving = false) }
                    _events.emit(AddNoteEvent.Error("Gagal menyimpan: ${saveError.message}"))
                }
        }
    }
}
```

#### 2.2 Fix Bug `InsightsScreen.kt` — MoodCalendar dayOfWeekOffset

**File:** `composeApp/src/commonMain/kotlin/.../presentation/screens/insights/InsightsScreen.kt`

**Bug di `MoodCalendar`:** `dayOfWeekOffset` menggunakan `now.month` tapi `now` adalah waktu saat ini, bukan bulan yang ditampilkan. Saat `year` atau `monthName` berbeda dari bulan saat ini, offset jadi salah.

**Fix — Update composable `MoodCalendar`:**
```kotlin
@Composable
fun MoodCalendar(
    monthName: String,
    year: Int,
    calendarData: Map<Int, String?>,
    modifier: Modifier = Modifier
) {
    // FIX: Hitung bulan dari parameter, bukan dari Clock.System.now()
    val monthNumber = Month.entries.indexOfFirst { 
        it.name.equals(monthName, ignoreCase = true) 
    } + 1 // 1-indexed
    
    val safeMonth = if (monthNumber in 1..12) monthNumber else 1
    val monthEnum = Month(safeMonth)
    
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val today = if (now.monthNumber == safeMonth && now.year == year) now.dayOfMonth else -1
    
    // FIX: Gunakan bulan dari parameter untuk hitung offset
    val firstDayOfMonth = LocalDate(year, safeMonth, 1)
    val dayOfWeekOffset = (firstDayOfMonth.dayOfWeek.isoDayNumber - 1) % 7
    
    // FIX: Hitung daysInMonth berdasarkan bulan dari parameter
    val daysInMonth = when (monthEnum) {
        Month.FEBRUARY -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
        Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
        else -> 31
    }
    
    // ... sisa composable sama, tapi gunakan `today` yang sudah di-fix
}
```

#### 2.3 Fix `HomeViewModelTest.kt` — Compile Error `testScheduler`

**File:** `composeApp/src/commonTest/kotlin/.../presentation/HomeViewModelTest.kt`

**Bug:** `testScheduler` tidak dikenal karena `StandardTestDispatcher` perlu di-cast atau menggunakan scope yang benar.

**Fix:**
```kotlin
// Ganti bagian search test:
@Test
fun `search should filter notes by query`() = runTest {
    repository.insertNote(createTestNote("Kotlin Guide"))
    repository.insertNote(createTestNote("Java Tutorial"))
    
    val vm = HomeViewModel(
        getAllNotesUseCase = getAllNotesUseCase,
        searchNotesUseCase = searchNotesUseCase,
        deleteNoteUseCase = deleteNoteUseCase,
        repository = repository,
        userPreferences = userPreferences
    )
    
    vm.uiState.test {
        skipItems(1) // Skip loading
        advanceUntilIdle()
        val initial = awaitItem()
        assertTrue(initial is HomeUiState.Success)
        
        // Act: change search query
        vm.onSearchQueryChange("Kotlin")
        
        // Advance time past debounce (300ms)
        advanceTimeBy(400)
        advanceUntilIdle()
        
        val filtered = expectMostRecentItem()
        assertTrue(filtered is HomeUiState.Success)
        assertEquals(1, (filtered as HomeUiState.Success).notes.size)
        assertEquals("Kotlin Guide", filtered.notes.first().title)
        
        cancelAndIgnoreRemainingEvents()
    }
}
```

#### 2.4 Fix `ConstellationCanvas.kt` — Stale Hub Positions

**File:** `ConstellationCanvas.kt`

**Bug:** `hubPositions` pakai `remember { mutableStateMapOf() }` tanpa key, tidak di-reset saat notes berubah.

**Fix:**
```kotlin
// Ganti baris ini:
val hubPositions = remember { mutableStateMapOf<String, Offset>() }

// Menjadi (tambahkan key agar di-reset saat notes berubah):
val hubPositions = remember(notes) { mutableStateMapOf<String, Offset>() }
```

#### 2.5 Fix Long Text Overflow — Handle Edge Cases UI

**File:** `composeApp/src/commonMain/.../presentation/screens/home/HomeScreen.kt` dan komponen terkait.

Di `ConstellationCanvas`, note dengan title sangat panjang bisa overflow. Tambahkan truncation di semua Text yang menampilkan user-generated content:

```kotlin
// Contoh fix di mana pun ada Text() dari note.title atau note.content
Text(
    text = note.title,
    maxLines = 2,
    overflow = TextOverflow.Ellipsis
)
```

---

### FASE 3: UI Polish — Komponen Reusable & State Handling

#### 3.1 Buat File `EdgeCaseComponents.kt` (BARU)

**File:** `composeApp/src/commonMain/kotlin/.../presentation/components/EdgeCaseComponents.kt`

```kotlin
package com.example.synesthesia.presentation.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Reusable full-screen loading indicator dengan teks deskriptif.
 */
@Composable
fun FullScreenLoading(
    message: String = "Loading...",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                strokeWidth = 4.dp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Reusable empty state dengan icon, title, subtitle, dan optional action button.
 */
@Composable
fun EmptyStateView(
    icon: ImageVector = Icons.Default.Inbox,
    title: String,
    subtitle: String = "",
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
            if (actionLabel != null && onAction != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onAction,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(actionLabel, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

/**
 * Reusable error state dengan retry button.
 */
@Composable
fun ErrorStateView(
    message: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
            )
            Text(
                text = "Oops! Something went wrong",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            if (onRetry != null) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onRetry,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Try Again")
                }
            }
        }
    }
}

/**
 * Skeleton loading card — tampil saat data belum tersedia.
 * Menggunakan shimmer animation.
 */
@Composable
fun SkeletonCard(
    modifier: Modifier = Modifier
) {
    val shimmerAlpha by animateFloatAsState(
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )
    // ... implementasi shimmer placeholder
}
```

#### 3.2 Polish `MemoryDetailScreen.kt` — Tambah Proper Error/Empty State

**File:** `MemoryDetailScreen.kt`

Ganti blok `is NoteDetailUiState.NotFound` yang sekarang hanya text biasa:

```kotlin
is NoteDetailUiState.NotFound -> {
    EmptyStateView(
        icon = Icons.Default.SearchOff,
        title = "Memory Not Found",
        subtitle = "This memory may have been deleted or doesn't exist.",
        actionLabel = "Go Back",
        onAction = onNavigateBack,
        modifier = Modifier.padding(padding)
    )
}
is NoteDetailUiState.Loading -> {
    FullScreenLoading(
        message = "Loading memory...",
        modifier = Modifier.padding(padding)
    )
}
```

#### 3.3 Polish `AddNoteScreen.kt` — Tambah Character Count & Better UX

**File:** `AddNoteScreen.kt`

Tambahkan character counter dan better placeholder di `JournalingStep`:

```kotlin
// Di dalam JournalingStep composable, setelah OutlinedTextField:
Row(
    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween
) {
    Text(
        text = if (content.length < 5) "Write at least 5 characters" else "✓ Ready to save",
        style = MaterialTheme.typography.labelSmall,
        color = if (content.length < 5) 
            MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
        else 
            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
    )
    Text(
        text = "${content.length} characters",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
    )
}
```

#### 3.4 Polish `HomeScreen.kt` — Spacing Consistency & Empty Search State

Di `HomeScreen`, saat search aktif dan results kosong, tampilkan empty state yang lebih informatif (sudah ada tapi polish):

```kotlin
// Di dalam EmptyState untuk query:
EmptyStateView(
    icon = Icons.Default.SearchOff,
    title = "No memories found",
    subtitle = "No results for \"${state.query}\"\nTry a different keyword.",
    modifier = Modifier.align(Alignment.Center)
)
```

---

### FASE 4: Unit Tests — Lengkapi Hingga 10+ Tests

#### 4.1 Lengkapi `NoteRepositoryTest.kt` (sudah ada, tambah/fix)

**File:** `composeApp/src/commonTest/.../data/repository/NoteRepositoryTest.kt`

Test yang sudah ada (7 tests): insertNote returns id, insertNote adds to list, getAllNotes, getNoteById, getNoteById null, searchNotes by title, searchNotes by content, deleteNote, updateNote = **9 tests**.

Tambahkan 2 test lagi:

```kotlin
@Test
fun `togglePin should change pin status`() = runTest {
    // Arrange
    val id = repository.insertNote(createTestNote(title = "Pin Me"))
    
    // Act
    repository.togglePinNote(id)
    
    // Assert
    repository.getNoteById(id).test {
        val note = awaitItem()
        assertTrue(note?.isPinned == true)
        cancelAndIgnoreRemainingEvents()
    }
}

@Test
fun `deleteNotes bulk should remove multiple notes`() = runTest {
    // Arrange
    val id1 = repository.insertNote(createTestNote(title = "Note 1"))
    val id2 = repository.insertNote(createTestNote(title = "Note 2"))
    repository.insertNote(createTestNote(title = "Note 3"))
    
    // Act
    repository.deleteNotes(listOf(id1, id2))
    
    // Assert
    repository.getAllNotes().test {
        val notes = awaitItem()
        assertEquals(1, notes.size)
        assertEquals("Note 3", notes.first().title)
        cancelAndIgnoreRemainingEvents()
    }
}

@Test
fun `getPinnedNotes should return only pinned notes`() = runTest {
    // Arrange
    val id1 = repository.insertNote(createTestNote(title = "Pinned"))
    repository.insertNote(createTestNote(title = "Not Pinned"))
    repository.togglePinNote(id1)
    
    // Assert
    repository.getPinnedNotes().test {
        val notes = awaitItem()
        assertEquals(1, notes.size)
        assertEquals("Pinned", notes.first().title)
        cancelAndIgnoreRemainingEvents()
    }
}
```

#### 4.2 Buat `NoteUseCaseTest.kt` (BARU)

**File:** `composeApp/src/commonTest/kotlin/.../domain/usecase/NoteUseCaseTest.kt`

```kotlin
package com.example.synesthesia.domain.usecase

import app.cash.turbine.test
import com.example.synesthesia.data.repository.FakeNoteRepository
import com.example.synesthesia.domain.model.Note
import com.example.synesthesia.domain.model.NoteCategory
import com.example.synesthesia.domain.model.NoteColor
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NoteUseCaseTest {
    
    private lateinit var repository: FakeNoteRepository
    private lateinit var getAllNotesUseCase: GetAllNotesUseCase
    private lateinit var saveNoteUseCase: SaveNoteUseCase
    private lateinit var deleteNoteUseCase: DeleteNoteUseCase
    private lateinit var searchNotesUseCase: SearchNotesUseCase
    
    @BeforeTest
    fun setup() {
        repository = FakeNoteRepository()
        getAllNotesUseCase = GetAllNotesUseCase(repository)
        saveNoteUseCase = SaveNoteUseCase(repository)
        deleteNoteUseCase = DeleteNoteUseCase(repository)
        searchNotesUseCase = SearchNotesUseCase(repository)
    }
    
    // ==================== SAVE NOTE USE CASE ====================
    
    @Test
    fun `saveNote should fail when title and content are both blank`() = runTest {
        // Arrange
        val emptyNote = Note(title = "", content = "", id = 0)
        
        // Act
        val result = saveNoteUseCase(emptyNote)
        
        // Assert
        assertTrue(result.isFailure)
        assertEquals("Note tidak boleh kosong", result.exceptionOrNull()?.message)
    }
    
    @Test
    fun `saveNote with content only should succeed`() = runTest {
        // Arrange
        val note = Note(title = "", content = "Just content, no title", id = 0)
        
        // Act
        val result = saveNoteUseCase(note)
        
        // Assert
        assertTrue(result.isSuccess)
    }
    
    @Test
    fun `saveNote with existing id should update, not insert`() = runTest {
        // Arrange
        val original = Note(title = "Original", content = "Content", id = 0)
        val id = saveNoteUseCase(original).getOrThrow()
        
        val updated = Note(title = "Updated Title", content = "Content", id = id)
        
        // Act
        saveNoteUseCase(updated)
        
        // Assert
        getAllNotesUseCase().test {
            val notes = awaitItem()
            assertEquals(1, notes.size) // Still 1, not 2
            assertEquals("Updated Title", notes.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    // ==================== GET ALL NOTES USE CASE ====================
    
    @Test
    fun `getAllNotes sorted TITLE_ASC should return alphabetically ordered notes`() = runTest {
        // Arrange
        repository.insertNote(createNote("Zebra Note"))
        repository.insertNote(createNote("Apple Note"))
        repository.insertNote(createNote("Mango Note"))
        
        // Act & Assert
        getAllNotesUseCase(NoteSortBy.TITLE_ASC).test {
            val notes = awaitItem()
            assertEquals("Apple Note", notes[0].title)
            assertEquals("Mango Note", notes[1].title)
            assertEquals("Zebra Note", notes[2].title)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `getAllNotes should show pinned notes first regardless of sort`() = runTest {
        // Arrange
        repository.insertNote(createNote("A Note")) // will be unpinned
        val pinnedId = repository.insertNote(createNote("Z Note")) // will be pinned
        repository.togglePinNote(pinnedId)
        
        // Act & Assert
        getAllNotesUseCase(NoteSortBy.TITLE_ASC).test {
            val notes = awaitItem()
            assertTrue(notes.first().isPinned)
            assertEquals("Z Note", notes.first().title) // Pinned comes first despite Z
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    // ==================== DELETE NOTE USE CASE ====================
    
    @Test
    fun `deleteNote should return success for existing note`() = runTest {
        // Arrange
        val id = repository.insertNote(createNote("To Delete"))
        
        // Act
        val result = deleteNoteUseCase(id)
        
        // Assert
        assertTrue(result.isSuccess)
    }
    
    // ==================== SEARCH USE CASE ====================
    
    @Test
    fun `searchNotes with category filter should return only matching category`() = runTest {
        // Arrange
        repository.insertNote(createNote("Work Item", NoteCategory.WORK))
        repository.insertNote(createNote("Personal Item", NoteCategory.PERSONAL))
        repository.insertNote(createNote("Work Personal", NoteCategory.WORK))
        
        // Act & Assert
        searchNotesUseCase("", NoteCategory.WORK).test {
            val notes = awaitItem()
            assertEquals(2, notes.size)
            assertTrue(notes.all { it.category == NoteCategory.WORK })
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    // ==================== HELPER ====================
    
    private fun createNote(title: String, category: NoteCategory = NoteCategory.GENERAL): Note {
        return Note(
            id = 0,
            title = title,
            content = "Content for $title",
            category = category,
            color = NoteColor.DEFAULT,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
    }
}
```

#### 4.3 Lengkapi `HomeViewModelTest.kt` — Fix Placeholder Test

**File:** `HomeViewModelTest.kt`

Ganti bagian `// ... (existing test) ...` dengan implementasi nyata:

```kotlin
@Test
fun `category filter should filter notes`() = runTest {
    // Arrange
    repository.insertNote(createTestNote("Work Task", NoteCategory.WORK))
    repository.insertNote(createTestNote("Personal Note", NoteCategory.PERSONAL))
    repository.insertNote(createTestNote("Another Work", NoteCategory.WORK))
    
    val vm = HomeViewModel(
        getAllNotesUseCase = getAllNotesUseCase,
        searchNotesUseCase = searchNotesUseCase,
        deleteNoteUseCase = deleteNoteUseCase,
        repository = repository,
        userPreferences = userPreferences
    )
    
    vm.uiState.test {
        skipItems(1) // Loading
        advanceUntilIdle()
        skipItems(1) // Initial success (all 3 notes)
        
        // Act
        vm.onCategorySelected(NoteCategory.WORK)
        advanceUntilIdle()
        
        // Assert
        val state = awaitItem()
        assertTrue(state is HomeUiState.Success)
        assertEquals(2, (state as HomeUiState.Success).notes.size)
        assertTrue(state.notes.all { it.category == NoteCategory.WORK })
        
        cancelAndIgnoreRemainingEvents()
    }
}
```

#### 4.4 Buat `AddNoteViewModelTest.kt` (BARU)

**File:** `composeApp/src/commonTest/kotlin/.../presentation/AddNoteViewModelTest.kt`

```kotlin
package com.example.synesthesia.presentation

import app.cash.turbine.test
import com.example.synesthesia.data.repository.FakeNoteRepository
import com.example.synesthesia.domain.usecase.SaveNoteUseCase
import com.example.synesthesia.presentation.screens.addnote.AddNoteEvent
import com.example.synesthesia.presentation.screens.addnote.AddNoteViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AddNoteViewModelTest {
    
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeNoteRepository
    private lateinit var saveNoteUseCase: SaveNoteUseCase
    private lateinit var viewModel: AddNoteViewModel
    private lateinit var fakeGeminiService: FakeGeminiService
    
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeNoteRepository()
        saveNoteUseCase = SaveNoteUseCase(repository)
        fakeGeminiService = FakeGeminiService()
        viewModel = AddNoteViewModel(
            repository = repository,
            saveNoteUseCase = saveNoteUseCase,
            geminiService = fakeGeminiService
        )
    }
    
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial uiState should have empty content`() = runTest {
        assertEquals("", viewModel.uiState.value.content)
        assertFalse(viewModel.uiState.value.isAnalyzing)
        assertFalse(viewModel.uiState.value.isSaving)
    }
    
    @Test
    fun `onContentChange should update content in state`() = runTest {
        // Act
        viewModel.onContentChange("Today I felt great!")
        
        // Assert
        assertEquals("Today I felt great!", viewModel.uiState.value.content)
    }
    
    @Test
    fun `saveNote with blank content should emit Error event`() = runTest {
        // Arrange — content is blank by default
        
        // Act & Assert
        viewModel.events.test {
            viewModel.saveNote()
            val event = awaitItem()
            assertTrue(event is AddNoteEvent.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `toggleParaphrase should toggle isParaphraseEnabled`() = runTest {
        val initialState = viewModel.uiState.value.isParaphraseEnabled
        
        viewModel.toggleParaphrase()
        
        assertEquals(!initialState, viewModel.uiState.value.isParaphraseEnabled)
    }
}

// Fake GeminiService untuk testing (hindari API call nyata)
class FakeGeminiService : com.example.synesthesia.data.remote.api.GeminiService(
    client = io.ktor.client.HttpClient() // minimal client, tidak dipakai
) {
    var shouldSucceed = true
    
    override suspend fun analyzeEmotion(journalText: String): Result<com.example.synesthesia.data.remote.dto.EmotionAnalysisResponse> {
        return if (shouldSucceed) {
            Result.success(
                com.example.synesthesia.data.remote.dto.EmotionAnalysisResponse(
                    autoTitle = "Test Title",
                    paraphrasedContent = "Paraphrased: $journalText",
                    emotionQuadrant = "LEP",
                    subEmotion = "Peaceful",
                    artColorHex = "#4CAF50",
                    summary = "A peaceful moment."
                )
            )
        } else {
            Result.failure(Exception("Network error"))
        }
    }
    
    override suspend fun generateContent(prompt: String, systemPrompt: String?): Result<String> {
        return Result.success("Fake AI response")
    }
}
```

> **CATATAN PENTING:** `GeminiService` saat ini tidak memiliki `open` keyword dan constructor-nya bergantung pada `HttpClient`. Ada dua opsi untuk membuat FakeGeminiService:
> 1. Buat interface `AIAnalysisService` yang hanya expose method yang perlu di-test, lalu inject interface-nya ke ViewModel.
> 2. Gunakan `mockk` library (tambahkan sebagai test dependency).
> **Rekomendasi:** Gunakan opsi 1 (interface extraction) karena lebih clean dan tidak butuh library tambahan.

**Refactor yang diperlukan untuk mendukung testing `AddNoteViewModel`:**

Buat interface di `commonMain`:
```kotlin
// core/ai/AIAnalysisService.kt (BARU)
interface AIAnalysisService {
    suspend fun analyzeEmotion(journalText: String): Result<EmotionAnalysisResponse>
    suspend fun generateContent(prompt: String, systemPrompt: String? = null): Result<String>
}

// GeminiService.kt — tambahkan implementasi interface:
class GeminiService(private val client: HttpClient) : AIAnalysisService {
    // existing methods, tidak perlu ubah
}

// AddNoteViewModel.kt — inject interface bukan concrete class:
class AddNoteViewModel(
    private val repository: NoteRepository,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val aiService: AIAnalysisService  // GANTI dari GeminiService ke interface
) : ViewModel()

// AppModule.kt — update binding:
single<AIAnalysisService> { get<GeminiService>() }
// atau:
singleOf(::GeminiService) bind AIAnalysisService::class
```

---

### FASE 5: UI Tests (Compose Testing)

#### 5.1 Buat Folder dan File `UITest.kt`

**File:** `composeApp/src/androidTest/kotlin/com/example/synesthesia/UITest.kt`

> **Note:** UI Test menggunakan Compose Test Rule dan hanya bisa jalan di Android target (bukan commonTest). Pastikan folder `androidTest` sudah ada.

```kotlin
package com.example.synesthesia

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.synesthesia.domain.model.Note
import com.example.synesthesia.domain.model.NoteCategory
import com.example.synesthesia.domain.model.NoteColor
import com.example.synesthesia.presentation.screens.home.HomeScreen
import com.example.synesthesia.presentation.screens.home.HomeUiState
import com.example.synesthesia.presentation.theme.NoteAITheme
import kotlinx.datetime.Clock
import org.junit.Rule
import org.junit.Test

class HomeScreenUITest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    // ==================== UI TEST 1: Empty State ====================
    
    @Test
    fun `homeScreen shows empty state when no notes`() {
        // Arrange
        composeTestRule.setContent {
            NoteAITheme {
                HomeScreen(
                    onNavigateToAddNote = {},
                    onNavigateToDetail = {},
                    onNavigateToAI = {},
                    onNavigateToSettings = {},
                    // inject fake ViewModel jika menggunakan koinViewModel override
                )
            }
        }
        
        // Assert — tampilkan empty state text
        composeTestRule
            .onNodeWithText("No memories yet", useUnmergedTree = true)
            .assertIsDisplayed()
    }
    
    // ==================== UI TEST 2: FAB Add Button ====================
    
    @Test
    fun `clicking FAB should trigger navigation to add screen`() {
        var navigated = false
        
        composeTestRule.setContent {
            NoteAITheme {
                HomeScreen(
                    onNavigateToAddNote = { navigated = true },
                    onNavigateToDetail = {},
                    onNavigateToAI = {},
                    onNavigateToSettings = {}
                )
            }
        }
        
        // Act
        composeTestRule
            .onNodeWithContentDescription("New Memory")
            .performClick()
        
        // Assert
        assert(navigated) { "Navigation to Add screen should have been triggered" }
    }
    
    // ==================== UI TEST 3: Settings Navigation ====================
    
    @Test
    fun `clicking settings icon should navigate to settings`() {
        var navigatedToSettings = false
        
        composeTestRule.setContent {
            NoteAITheme {
                HomeScreen(
                    onNavigateToAddNote = {},
                    onNavigateToDetail = {},
                    onNavigateToAI = {},
                    onNavigateToSettings = { navigatedToSettings = true }
                )
            }
        }
        
        composeTestRule
            .onNodeWithContentDescription("Settings")
            .performClick()
        
        assert(navigatedToSettings)
    }
}

class AddNoteScreenUITest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    // ==================== UI TEST 4: AddNote Shows Journal Field ====================
    
    @Test
    fun `addNoteScreen displays journal text field`() {
        composeTestRule.setContent {
            NoteAITheme {
                // Render AddNoteScreen with minimal setup
                // Ini membutuhkan koin context — gunakan pendekatan composable isolation
            }
        }
        
        composeTestRule
            .onNodeWithText("Write your soul here...", useUnmergedTree = true)
            .assertIsDisplayed()
    }
    
    // ==================== UI TEST 5: MemoryDetailScreen Not Found ====================
    
    @Test
    fun `memoryDetailScreen shows not found state`() {
        composeTestRule.setContent {
            NoteAITheme {
                // Render NotFound state langsung tanpa ViewModel
            }
        }
        
        composeTestRule
            .onNodeWithText("Memory Not Found", useUnmergedTree = true)
            .assertIsDisplayed()
    }
}
```

> **PENTING untuk UI Test:** Karena menggunakan `koinViewModel()` di dalam screen, UI test butuh setup Koin. Ada dua pendekatan:
>
> **Pendekatan A (Recommended for Sprint 4):** Test screen composable dengan state langsung (tanpa ViewModel):
> Refactor screen agar menerima `uiState` sebagai parameter langsung (stateless composable), baru ViewModel yang inject state. Ini mengikuti best practice Compose testing.
>
> **Pendekatan B (Cepat):** Setup minimal Koin di test dengan `startKoin { modules(testModules) }` di `@Before`.

---

### FASE 6: Update `README.md`

**File:** `README.md`

Tambahkan section baru setelah bagian Architecture:

```markdown
---

## 🧪 Testing

### Run All Tests
```bash
./gradlew test
```

### Run Unit Tests Only
```bash
./gradlew :composeApp:testDebugUnitTest
```

### Run Coverage Report (Kover)
```bash
./gradlew koverHtmlReport
```
Coverage report akan tersedia di: `build/reports/kover/html/index.html`

### Run UI Tests (Android)
```bash
./gradlew :composeApp:connectedAndroidTest
```

### Test Coverage Target
- Overall: **50%+** (target Sprint 4)
- Repository layer: 70%+
- ViewModel layer: 70%+
- Use Cases: 80%+

### Test Structure
```
commonTest/
├── data/repository/
│   └── NoteRepositoryTest.kt     — 10+ CRUD & search tests
├── domain/usecase/
│   └── NoteUseCaseTest.kt        — 7+ business logic tests
└── presentation/
    ├── HomeViewModelTest.kt      — 8+ state management tests
    └── AddNoteViewModelTest.kt   — 4+ ViewModel tests

androidTest/
└── presentation/
    └── UITest.kt                 — 3+ Compose UI tests
```
```

---

## 💡 SARAN PENGEMBANGAN FITUR MAKSIMAL (Bonus Sprint 4 & Sprint 5)

### Saran 1: Haptic Feedback di Key Interactions
```kotlin
// Di MainActivity.kt:
val hapticFeedback = LocalHapticFeedback.current
// Di button/FAB click:
hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
```
Menambah polish signifikan tanpa effort besar.

### Saran 2: Animated Shared Element Transition (NoteCard → Detail)
Gunakan `SharedTransitionLayout` yang sudah ada di `AppNavHost.kt` tapi belum diimplementasikan di screen. Tambahkan shared element transition antara note dot di constellation canvas ke detail screen untuk WOW effect di demo.

### Saran 3: Export Memory sebagai Image/PDF
Tambahkan tombol share di `MemoryDetailScreen` yang membuat screenshot beautiful dari memory card dan share via Android share sheet. Ini fitur yang sangat menarik untuk demo.

### Saran 4: Widget Android (Home Screen Widget)
Tambahkan simple widget yang menampilkan last emotion atau random memory. Ini adalah "advanced feature" yang bisa jadi highlight demo.

### Saran 5: Notification Reminder
`AlarmManager` atau `WorkManager` untuk reminder "Write your daily journal". Setup di Sprint 5 untuk demo day.

### Saran 6: Onboarding Screen
3-slide onboarding untuk first launch yang memperkenalkan konsep Synesthesia. Sudah ada `isOnboardingCompleted` di DataStore, tinggal buat UI-nya.

### Saran 7: Animasi di Constellation Canvas
Tambahkan particle effect saat note baru disimpan — shooting star dari center canvas ke hub yang sesuai. Menggunakan Canvas draw yang sudah ada.

---

## 📊 PRIORITAS IMPLEMENTASI

```
PRIORITAS 1 (P0 — Deadline kritis, bobot 50%):
├── Fix bug AddNoteViewModel nested launch
├── Fix bug MoodCalendar dayOfWeekOffset  
├── Fix compile error HomeViewModelTest
├── Fix hub positions key di ConstellationCanvas
└── Setup Kover plugin

PRIORITAS 2 (P1 — Unit Tests, bobot 25%):
├── Lengkapi NoteRepositoryTest → 10+ tests
├── Buat NoteUseCaseTest (baru) → 7+ tests
├── Fix HomeViewModelTest category filter test
└── Buat AddNoteViewModelTest (baru) → 4+ tests

PRIORITAS 3 (P2 — UI Polish, bobot 25%):
├── Buat EdgeCaseComponents.kt
├── Polish MemoryDetailScreen NotFound state
├── Polish AddNoteScreen character count
└── Text overflow handling di semua screen

PRIORITAS 4 (P3 — UI Tests + Coverage, bobot 25%):
├── Buat androidTest/UITest.kt → 3+ tests
├── Run Kover dan pastikan 50%+
└── Update README dengan test instructions
```

---

## ✅ DEFINITION OF DONE — CHECKLIST SPRINT 4

```
BUG FIXES:
[ ] AddNoteViewModel nested launch bug diperbaiki
[ ] MoodCalendar dayOfWeekOffset bug diperbaiki
[ ] HomeViewModelTest compile error diperbaiki
[ ] ConstellationCanvas hubPositions stale data diperbaiki
[ ] Tidak ada crash yang bisa direproduksi
[ ] Semua navigation flows berjalan normal
[ ] Empty content validation di AddNote berjalan

UI POLISH:
[ ] EdgeCaseComponents.kt dibuat dan digunakan
[ ] Loading state konsisten di semua screen
[ ] Empty state konsisten di semua screen  
[ ] Error state konsisten di semua screen
[ ] Text overflow di-handle (maxLines + Ellipsis)
[ ] Character count di AddNoteScreen
[ ] Spacing konsisten (8dp grid)

UNIT TESTS (target 15+ total):
[ ] NoteRepositoryTest: 12+ tests passing
[ ] NoteUseCaseTest: 7+ tests passing
[ ] HomeViewModelTest: 8+ tests passing (fix placeholder)
[ ] AddNoteViewModelTest: 4+ tests passing
[ ] Tidak ada test yang @Ignore atau body kosong

UI TESTS (target 3+):
[ ] HomeScreen empty state test
[ ] FAB navigation test
[ ] Settings navigation test
[ ] Minimal 3 tests passing di androidTest

COVERAGE:
[ ] Kover plugin terkonfigurasi di build.gradle.kts
[ ] Report bisa di-generate: ./gradlew koverHtmlReport
[ ] Coverage 50%+ tercapai
[ ] Screenshot coverage report ada di README

README:
[ ] Seksi "Testing" ditambahkan
[ ] Instruksi run test ada
[ ] Coverage report screenshot di-embed
[ ] Test structure dijelaskan
```

---

*Dibuat untuk Sprint 4 — Project Synesthesia | IF25-22017 Pengembangan Aplikasi Mobile ITERA 2025/2026*
