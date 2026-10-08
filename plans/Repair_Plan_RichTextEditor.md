# Plan naprawy RichTextEditor

Dokument opisuje zrewidowany plan naprawy błędów zachowania kontrolki w `RichTextEditor.kt` oparty o standardy architektoniczne (tzw. best practices) bibliotek takich jak `richeditor-compose`.

## 1. Asynchroniczność i State Management
**Praktyka branżowa:** Zaawansowane edytory w Compose tworzą dedykowaną klasę `RichTextState` owijającą `TextFieldValue`, by uniknąć pętli synchronizacji.
**Nasz Plan:** Nie będziemy robić gigantycznego refaktoru do `RichTextState`, ale zablokujemy błędy `LaunchedEffect`.
- `textFieldValue` wewnątrz `RichTextEditor` będzie źródłem prawdy dla wpisywania.
- Pętla `LaunchedEffect(value)` będzie aktualizować tekst *tylko*, jeśli `value.rawText` różni się od `textFieldValue.text` (np. dane załadowane z bazy danych przy starcie), a podczas przepisywania z zachowaniem instniejącej wartości `selection` (pozycji kursora).

## 2. Inicjalizacja list i problem "Hanging Indent" (Zawijanie wcięć)
**Praktyka branżowa:** Zamiast samego wcięcia (które teraz masz ustawione na `firstLine = 20.sp, restLine = 20.sp`), profesjonalne edytory ustawiają pierwsze wysunięcie na 0, a kolejne na wcięcie: `firstLine = 0.sp, restLine = 20.sp`.
**Nasz Plan:** 
1. W `RichTextMapper.kt` zmienimy konfigurację `TextIndent`, aby tekst wielolinijkowy zawijał się ładnie pod pierwszą literą, a punktor "wisiał" po lewej stronie.
2. W `applyToggle` w `RichTextEditor.kt`, gdy użytkownik kliknie ikonę listy, natychmiastowo dodamy `"• "` (lub numer) na początek aktywnej linii w `AnnotatedString` (aktualizując spany) zamiast czekać na klawisz Enter.

## 3. Duży odstęp / luka po klawiszu Enter (Ghost Paragraphs)
**Praktyka branżowa:** Drzewo składniowe (AST) przed wyrenderowaniem zawsze upewnia się, że znaki `\n` są klejem pomiędzy blokami `ParagraphStyle`, a nie "pustymi znakami" na zewnątrz.
**Nasz Plan:** W bloku gdzie wyliczasz `span.start < paraStart` usuniemy wycinanie znaku `\n` (obecnie `paraStart - 1`). `ParagraphStyle` danego paragrafu musi obejmować przyległy znak nowej linii. Dzięki temu Compose przestanie wstawiać niewidzialne, podwójne przerwy na uciętych znakach nowej linii.

## 4. Resetowanie wyrównania w nowej linii
**Nasz Plan:** Utrzymujemy dotychczasowy bardzo bezpieczny koncept wyłapywania różnicy długości tekstu (`wasEnterTyped`), ponieważ omijamy w ten sposób problem zawodności `KeyEventów` na klawiaturach ekranowych (IME) w Androidzie.
W tym bloku dodamy proste wyczyszczenie modyfikatorów `ALIGN_CENTER`, `ALIGN_RIGHT` oraz `ALIGN_LEFT` ze stanu `newToggledSpans`, co przywróci wpisywanie do domyślnej lewej krawędzi bez spana wymuszającego.

## 5. Czyszczenie stylu przy kasowaniu (Backspace)
**Praktyka branżowa:** Jeśli użytkownik usunie znak punktora `"• "` klawiszem Backspace, edytor powinien automatycznie znieść modyfikator `BULLET_LIST` z tej linii.
**Nasz Plan:** Rozbudujemy sprawdzanie modyfikacji stringa o wykrywanie, czy usunięto znak punktora na początku linii i w takiej sytuacji automatycznie wyczyścimy `SpanType` odpowiedzialny za listę.