# Migration Plan: compose-rich-editor - DONE

## 1. Setup & Dependencies
- Add dependency `com.mohamedrejeb.richeditor:richeditor-compose:<latest-stable-version>` to `app/build.gradle`.
- Check and add any necessary ProGuard/R8 rules if the library uses reflection (though Compose libraries typically don't).

## 2. Data Layer Migration (Room DB) - *Non-Destructive Approach*
- **Schema Update:** Update the Room Entity (`Step` lub `Activity`) to add a new `contentHtml: String? = null` column. **Do not delete** the old `contentJson` column yet. (Przejrzeć obecny schemat i upewnić się, jak dokładnie trzymane są formatowania)
- **Room Migration:** Write a lightweight Room `Migration` that only alters the table schema (adds the new column) without manipulating data to ensure lightning-fast app startup.
- **Data Conversion Engine:** Create a `LegacyToHtmlConverter` that accepts the old `RichTextContent` JSON and outputs standard HTML.
- **Lazy Migration Strategy:** In the Repository layer, when fetching an entity:
  - If `contentHtml` is not null, use it.
  - If `contentHtml` is null, use `LegacyToHtmlConverter` to parse `contentJson`, save the resulting HTML back to the database asynchronously, and return the HTML.

## 3. Domain & UI Model Updates
- Rename existing `RichTextContent.kt`, `RichTextSpan.kt` to `LegacyRichTextContent.kt`, etc., and mark them with `@Deprecated("Used only for local DB migration")`.
- Update the active UI/Domain models to expose the `contentHtml: String`.
- **Performance Optimization:** If the text is displayed in lists (e.g., `LazyColumn`), add a `cachedAnnotatedString: AnnotatedString?` to the UI model. Parse the HTML to `AnnotatedString` in the `ViewModel` (on `Dispatchers.Default`) to prevent UI stuttering during scrolling.

## 4. UI Layer Refactor (RichTextEditor.kt)
- Replace custom `onValueChange` and cursor calculations with `compose-rich-editor`.
- **State Management:**
  - Hoist the HTML content to the `ViewModel` and back it with `SavedStateHandle` to survive process death.
  - Initialize the `RichTextState` safely: load the initial HTML using `state.setHtml(html)` inside a `LaunchedEffect` or `ViewModel` init block to avoid blocking the main Compose thread.
- Update the toolbar to map to `state.toggleSpanStyle()` (Bold, Italic, etc.) and `state.toggleParagraphStyle()`.
- Ensure focus management and keyboard interactions behave correctly with the new library.

## 5. Testing & Validation
- **Migration Tests:** Do not delete `RichTextMapperTest.kt`. Refactor it into `LegacyToHtmlConverterTest`. Use existing test data (old span formats) and assert they generate the exact correct HTML strings.
- **UI Tests:** Add basic Compose UI tests for the new editor (e.g., clicking the "Bold" button toggles the correct state).
- **Cleanup (Future Release):** Add a note/ticket for the next major release to drop the legacy `contentJson` column and delete the `@Deprecated` legacy models once telemetry shows migration is largely complete.