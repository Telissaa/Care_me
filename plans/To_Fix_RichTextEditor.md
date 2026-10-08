# Lista zadań naprawczych (RichTextEditor)

- [ ] 1. Naprawa asynchroniczności i state managementu (wzorzec Single Source of Truth). Usunięcie ciągłego nadpisywania kursora przez `LaunchedEffect(value)`. Aktualizacja lokalnego `textFieldValue` tylko gdy tekst zewnętrzny faktycznie się zmienił, z zachowaniem obecnej selekcji.
- [ ] 2. Naprawa list (Punktory/Numeracja) - Część 1: Natychmiastowe wstawianie znaku `• ` / `1. ` w `applyToggle` przy aktywacji, aby nie trzeba było czekać na klawisz Enter.
- [ ] 3. Naprawa list (Punktory/Numeracja) - Część 2 (Hanging Indent). Zmiana `TextIndent` w `RichTextMapper` na `firstLine = 0.sp, restLine = 20.sp`, aby długi tekst zawijał się poprawnie pod tekstem, a nie pod punktorem (standard w edytorach tekstu).
- [ ] 4. Naprawa wielkiej luki (podwójnej linii) przy wciskaniu klawisza Enter. Usunięcie odłączania znaku nowej linii `\n` od `ParagraphStyle`. Znak `\n` musi być wewnątrz spana akapitu.
- [ ] 5. Naprawa formatowania po wciśnięciu Enter. Wyrównanie (np. `ALIGN_CENTER`) musi być odznaczane ze zbioru aktywnych spanów podczas tworzenia nowej linii.
- [ ] 6. (Opcjonalnie) Usunięcie stylu listy, gdy użytkownik usunie (Backspace) znak punktora z początku linii.