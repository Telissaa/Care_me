# Szczegółowy Plan Rozwoju Projektu: Care me

Aplikacja **Care me** to nowoczesne rozwiązanie Android do zarządzania zadaniami, rutynami i planami działania. Projekt opiera się na **Jetpack Compose**, **Clean Architecture**, **MVVM** i wsparciu technologii tj. **Koin**, **Room** oraz **Retrofit**. 

Poniżej znajduje się szczegółowy, podzielony na etapy plan wdrożenia wszystkich funkcjonalności opisanych w dokumencie `README.md`.

---

## Faza 1: Architektura Bazowa i Konfiguracja (Foundation)

**Cel:** Utworzenie szkieletu aplikacji, konfiguracja bibliotek oraz architektury aplikacji.

1. **Struktura Katalogów:**
   - Zdefiniowanie pakietów zgodnie z Clean Architecture: `data`, `domain`, `presentation`, `di`, `core`.
2. **Dependency Injection (Koin):**
   - Dodanie niezbędnych zależności w `build.gradle.kts`.
   - Konfiguracja głównych modułów Koin (np. `appModule`, `networkModule`, `databaseModule`).
   - Inicjalizacja `startKoin` w klasie rozszerzającej `Application`.
3. **Konfiguracja Bazy Danych (Room):**
   - Skonfigurowanie klasy `AppDatabase`.
   - Zdefiniowanie TypeConverters (potrzebnych m.in. dla formatowania tekstu Rich-Text w obiektach JSON).
4. **Warstwa Sieciowa (Retrofit2 + Kotlinx Serialization):**
   - Konfiguracja klienta `OkHttp` (interceptor logowania).
   - Skonfigurowanie instancji `Retrofit` do komunikacji z API (dla UV Index).
5. **Nawigacja (Jetpack Navigation Compose):**
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
   - Opracowanie bezstratnej metody zapisu formatowania tekstu do bazy (serializacja stanów typu Span do JSON).

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

---

## Faza 6: Narzędzie UV Index Tracker

**Cel:** Dostarczanie danych o promieniowaniu UV w czasie rzeczywistym z zewnętrznego API.

1. **Implementacja Połączeń:**
   - Skomunikowanie przygotowanego interfejsu API z działającą usługą online.
2. **Komponent UI (Real-Time Tracker):**
   - Wydzielony ekran (bądź widget na Dashboard/szczegółach zadania) przedstawiający wizualizację poziomu UV.
   - Odpowiednie stany ładowania (Shimmer/Loader) i obsługa błędów sieci (np. brak internetu, błędy serwera) za pomocą Snackbar/M3.

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