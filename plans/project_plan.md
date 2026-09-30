# Szczegółowy Plan Rozwoju Projektu: Care me

Aplikacja **Care me** to nowoczesne rozwiązanie Android do zarządzania zadaniami, rutynami i planami działania. Projekt opiera się na **Jetpack Compose**, **Clean Architecture**, **MVVM** i wsparciu technologii tj. **Koin**, **Room** oraz **Retrofit**. 

Poniżej znajduje się szczegółowy, podzielony na etapy plan wdrożenia wszystkich funkcjonalności opisanych w dokumencie `README.md`.

---

## Faza 1: Architektura Bazowa i Konfiguracja (Foundation)

**Cel:** Utworzenie szkieletu aplikacji, konfiguracja bibliotek oraz architektury aplikacji.

1. **Struktura Katalogów:**
   - Zdefiniowanie pakietów zgodnie z Clean Architecture: `data`, `domain`, `presentation`, `di`, `core`.
2. **Konfiguracja Gradle i Uprawnień (Manifest):**
   - Dodanie niezbędnych zależności w `build.gradle.kts`.
   - Zdefiniowanie uprawnień w `AndroidManifest.xml`: `INTERNET`, `POST_NOTIFICATIONS` (Android 13+), `SCHEDULE_EXACT_ALARM` (Android 12+) oraz potencjalnie `ACCESS_COARSE_LOCATION` (dla API UV).
3. **Design System & Theming (Material 3):**
   - Skonfigurowanie `Color.kt`, `Typography.kt`, `Shape.kt` oraz centralnego `Theme.kt` dla jasnego/ciemnego motywu.
4. **Dependency Injection (Koin):**
   - Konfiguracja głównych modułów Koin (np. `appModule`, `networkModule`, `databaseModule`).
   - Inicjalizacja `startKoin` w klasie rozszerzającej `Application`.
5. **Konfiguracja Bazy Danych (Room):**
   - Skonfigurowanie klasy `AppDatabase`.
   - Zdefiniowanie TypeConverters dla własnych struktur danych.
6. **Warstwa Sieciowa (Retrofit2 + Kotlinx Serialization):**
   - Konfiguracja klienta `OkHttp` (interceptor logowania).
   - Skonfigurowanie instancji `Retrofit` do komunikacji z API (dla UV Index).
7. **Nawigacja (Jetpack Navigation Compose):**
   - Stworzenie grafu nawigacji (Navigation Graph) bazującego na `sealed interface` dla typowanego routingu.

---

## Faza 2: Warstwa Danych i Domenowa (Database & Domain)

**Cel:** Modelowanie danych oraz przygotowanie repozytoriów dla lokalnej bazy danych i API.

1. **Modelowanie Danych (Entities):**
   - **`ActivityEntity`**: Reprezentuje pojedynczą kategorię zadań/rutyny (ikona, nazwa, opis).
   - **`ReminderEntity`**: Konfiguracja powiadomień i harmonogramu.
   - **`StepEntity` / `RichTextContent`**: Kroki w ramach Activity wraz ze zserializowanym stanem formatowania (np. pogrubienie, kolory, listy).
2. **Data Access Objects (DAO):**
   - Obsługa operacji CRUD (Create, Read, Update, Delete) zwracających struktury `Flow<T>` lub oznaczonych jako funkcje `suspend`.
3. **Repozytoria (Repository):**
   - Implementacja logiki zarządzania przepływem danych (pobieranie z DAO oraz wystawianie do warstwy UI).
   - `UVRepository` pobierające i mapujące dane o indeksie UV z API.

---

## Faza 2.5: Poprawki i Usprawnienia Architektoniczne (Refinements)

**Cel:** Wprowadzenie ważnych poprawek i standardów do zaimplementowanej już warstwy bazowej i danych.

1. **Zarządzanie Ustawieniami (Jetpack DataStore):**
   - Implementacja Preferences DataStore do bezpiecznego przechowywania ustawień (np. domyślne miasto, wybrany motyw).
2. **Bezpieczne Zarządzanie Kluczami API (API Keys Security):**
   - Ukrycie kluczy API za pomocą pliku `local.properties` i wtyczki Gradle (generowanie np. `BuildConfig.UV_API_KEY`).
3. **Separacja Modeli i Mappery (Clean Architecture):**
   - Wprowadzenie warstwy czystych modeli domenowych (Domain Models).
   - Dodanie mapperów konwertujących encje bazy danych (`Entity`) na modele używane w domenie i UI (`Entity.toDomain()`).
4. **Wstrzykiwanie Dispatcherów (Coroutines):**
   - Dodanie w Koin modułu wstrzykującego Dispatchers (np. `Dispatchers.IO`), by umożliwić swobodne testowanie (mockowanie dispatcherów).
5. **Nowoczesny Ekran Startowy (Splash Screen API):**
   - Konfiguracja `androidx.core:core-splashscreen` zapewniająca spójne i nowoczesne uruchamianie aplikacji od Androida 12+.
6. **Standaryzacja:**
   - Wykorzystanie `strings.xml` do przechowywania wszystkich tekstów w aplikacji (i18n).
   - Weryfikacja/migracja do `libs.versions.toml` w celu scentralizowanego zarządzania zależnościami Gradle.

---

## Faza 3: Interfejs Użytkownika - Dashboard (Home Dashboard)

**Cel:** Stworzenie głównego punktu wejścia do aplikacji.

1. **Logika i Stan (HomeViewModel):**
   - Pobieranie listy zapisanych Aktywności i reprezentowanie ich za pomocą strumienia stanów (`StateFlow`).
2. **Interfejs Użytkownika (Jetpack Compose):**
   - Implementacja Material 3 `Scaffold`.
   - Siatka (Grid) lub lista ikon dla Aktywności.
   - Obsługa pustych stanów (Empty State) i animacji przejść do ekranów szczegółów.

---

## Faza 4: Moduł Rich-Text Activity Views

**Cel:** Podgląd oraz edycja szczegółów zadań przy pomocy edytora tekstu formatowanego.

1. **Ekran Szczegółów Aktywności:**
   - Powiązanie UI z `ActivityDetailViewModel`.
   - Możliwość dynamicznego dodawania i sortowania kroków dla wybranej aktywności.
2. **Komponent Rich-Text Editor:**
   - Wykorzystanie Jetpack Compose `TextField` oraz `AnnotatedString` do obsługi stylów.
   - Implementacja obsługi:
     - **Pogrubienia tekstu** (Bold).
     - Kolorowania fragmentów tekstu.
     - List punktowanych i numerowanych.
   - Opracowanie bezpiecznej metody zapisu formatowania tekstu do bazy (np. parsowanie tekstu do **Markdown** w locie przed zapisem jako String do bazy Room i rekonstrukcja przy odczycie).

---

## Faza 5: System Powiadomień i Contextual Drawer

**Cel:** Implementacja zaawansowanego systemu notyfikacji (Advanced Custom Reminders) z panelami bocznymi w UI.

1. **Contextual Notification Drawer:**
   - Wdrożenie bocznego panelu (End Drawer z wykorzystaniem `ModalNavigationDrawer` lub własnej animowanej powłoki) w ekranie Aktywności.
   - Wyświetlanie listy powiadomień dedykowanych wybranej, bieżącej Aktywności.
2. **Silnik Powiadomień (Notifications Engine):**
   - Użycie **AlarmManager** do precyzyjnych (Exact) powiadomień, współpracującego ewentualnie z `WorkManager` dla powtarzalnych zadań systemowych.
   - Interfejs konfiguracji częstotliwości, reguł odnawiania i tytułów notyfikacji.
3. **Expandable Notifications:**
   - Budowa `NotificationCompat.Builder` z obsługą `BigTextStyle` do wyświetlania w powiadomieniu długich, zformatowanych opisów (Rich-Text) z poziomu systemu powiadomień Androida.
4. **Rescheduling (Boot Receiver):**
   - Implementacja `BroadcastReceiver` nasłuchującego na `ACTION_BOOT_COMPLETED`, aby po ponownym uruchomieniu telefonu wczytywać przypomnienia z bazy Room i rejestrować je ponownie w systemowym AlarmManager.
5. **Zarządzanie Uprawnieniami (Runtime Permissions):**
   - Dodanie logiki w Jetpack Compose do pytania użytkownika o uprawnienie `POST_NOTIFICATIONS` (Android 13+) przy pierwszej próbie zaplanowania przypomnienia.

---

## Faza 6: Narzędzie UV Index Tracker

**Cel:** Dostarczanie danych o promieniowaniu UV w czasie rzeczywistym z zewnętrznego API.

1. **Implementacja Połączeń:**
   - Skomunikowanie przygotowanego interfejsu API z działającą usługą online.
2. **Źródło Danych Geograficznych (Decyzja/Zdrożenie):**
   - Podjęcie decyzji w kwestii źródła lokalizacji dla API: wykorzystanie koordynat GPS z urządzenia lub manualne określenie domyślnego miasta w ustawieniach.
3. **Komponent UI (Real-Time Tracker):**
   - Wydzielony ekran (bądź widget na Dashboard/szczegółach zadania) przedstawiający wizualizację poziomu UV.
   - Odpowiednie stany ładowania (Shimmer/Loader) i obsługa błędów sieci (np. brak internetu, błędy serwera) za pomocą Snackbar/M3.
4. **Zarządzanie Uprawnieniami Lokalizacji (Runtime Permissions):**
   - Wykorzystanie Compose API (`rememberLauncherForActivityResult`) do obsługi pytań o uprawnienia `ACCESS_COARSE_LOCATION`, o ile wybrano pobieranie danych o UV na podstawie GPS.

---

## Faza 7: Stabilizacja, Testy i Ostateczny Szlif

**Cel:** Zapewnienie niezawodności oprogramowania i przygotowanie go do wdrożenia.

1. **Testy (Testing Strategy):**
   - **Unit Tests:** Przetestowanie Use Cases, ViewModeli i Repozytoriów przy użyciu biblioteki JUnit 5, MockK i narzędzi testowych Coroutines.
   - **UI Tests:** Testy kluczowych flow użytkownika (nawigacja, tworzenie Activity, testy komponentu edytora tekstu formatowanego).
2. **Zarządzanie Stanami Błędów:**
   - Dopracowanie globalnego modelu łapania błędów z wykorzystaniem mechanizmów Kotlin `Result` oraz wyświetlanie przyjaznych komunikatów.
3. **Wdrożenie Zasad Czystego Kodu (Clean Code):**
   - Refaktoryzacja klas zgodnie z zasadami SOLID.
   - Weryfikacja komentarzy (konsekwentnie w języku angielskim).
   - Ostateczna inspekcja repozytorium.
4. **Automatyzacja CI/CD (Opcjonalnie):**
   - Wdrożenie podstawowego przepływu GitHub Actions uruchamiającego detekcję błędów (linter) oraz podstawowe testy jednostkowe przy każdym Pull Requeście.