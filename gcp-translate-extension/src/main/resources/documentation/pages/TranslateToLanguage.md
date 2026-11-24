# Translate To Language

## Overview

Translates text from one language to another using Google Cloud's Neural Machine Translation (NMT) model. This request
supports translation between any pair of supported languages and can automatically detect the source language if not
specified.

## Request Details

- **MCP Tool**: ✅ Yes (exposed to the Krista AI agent)

- Area: Translation
- Type: QUERY_SYSTEM
- Retry Support: No
- Ask Agent (MCP): Enabled (callable by the Krista AI agent)

## Input Parameters

| Parameter Name  | Type      | Required | Description                                                          | Example               |
|-----------------|-----------|----------|----------------------------------------------------------------------|-----------------------|
| Text            | Paragraph | Yes      | The text to translate (maximum 30,000 characters)                    | "Hello, how are you?" |
| Source Language | Text      | No       | The language code of the input text (leave empty for auto-detection) | "en" or "English"     |
| Target Language | Text      | Yes      | The language code to translate to                                    | "es" or "Spanish"     |

### Parameter Details

#### Text

- Maximum length: 30,000 characters
- Cannot be null or empty
- Whitespace-only text is not allowed
- Supports all Unicode characters
- Line breaks and formatting are preserved in translation

#### Source Language

- Optional parameter - leave empty for automatic language detection
- Accepts language codes (e.g., "en", "es", "fr") or display names (e.g., "English", "Spanish", "French")
- Must be a language supported by Google Cloud Translation API
- Use [Get Supported Languages](pages/GetSupportedLanguages.md) to see all valid options
- If specified, must be different from Target Language

#### Target Language

- Required parameter
- Accepts language codes (e.g., "en", "es", "fr") or display names (e.g., "English", "Spanish", "French")
- Must be a language supported by Google Cloud Translation API
- Use [Get Supported Languages](pages/GetSupportedLanguages.md) to see all valid options
- Cannot be the same as Source Language

## Output Parameters

| Parameter Name  | Type      | Description                                |
|-----------------|-----------|--------------------------------------------|
| Translated Text | Paragraph | The translated text in the target language |

## Validation Rules

| Validation                               | Error Message                                                                         | Resolution                                         |
|------------------------------------------|---------------------------------------------------------------------------------------|----------------------------------------------------|
| Text is null or empty                    | "Please enter some text to translate or detect. The text field cannot be empty."                                         | Enter the text you want to translate                    |
| Text exceeds 30,000 characters           | "Your text is too long ([current] characters). Please shorten it to 30,000 characters or less, or split it into multiple requests."                                   | Reduce text length or split into smaller parts |
| Target Language is null or empty         | "Please select a target language. This field is required to proceed."                                                        | Enter a valid target language code (like 'en' or 'es')       |
| Target Language is invalid               | "We don't recognize '[language]' as a supported language. Please use the 'Get Supported Languages' request to see all available options, or try entering a standard language code like 'en' for English or 'es' for Spanish." | Use a supported language code from the list              |
| Source and Target languages are the same | "Your source and target languages are the same. Please choose a different target language to translate your text."                                     | Select a different target language      |

## Error Handling

### Input Validation Errors (INPUT_ERROR)

**What it means**: The information you provided doesn't meet the requirements for translation.

**Common scenarios**:

- You didn't enter any text to translate
- Your text is longer than 30,000 characters
- You didn't specify a target language
- The language code you entered isn't recognized or supported
- Your source and target languages are the same (nothing to translate)

**How to fix it**:

1. **Read the error message** - It tells you exactly what's wrong and how to fix it
2. Make sure you've entered text in the Text field
3. Check that your text is under 30,000 characters (you can use a character counter)
4. Verify you've entered a target language
5. Use the **'Get Supported Languages'** request to see all valid language codes
6. Make sure your source and target languages are different
7. Try using standard language codes like 'en' (English), 'es' (Spanish), 'fr' (French), etc.

### System and Service Errors (SYSTEM_ERROR)

**What it means**: There's a problem with Google Cloud services, your credentials, or network connectivity.

**Common scenarios**:

- Google Cloud Translation service is temporarily down or experiencing issues
- Your internet connection is unstable
- You've exceeded your Google Cloud API quota limits
- Your service account credentials are invalid, expired, or missing permissions
- The Translation API isn't enabled for your Google Cloud project

**How to fix it**:

1. **Check the error message** - It provides specific guidance based on the issue
2. **For service unavailability**: Wait a few minutes and try again. Check [Google Cloud Status](https://status.cloud.google.com) for known outages
3. **For quota issues**:
   - Go to Google Cloud Console → APIs & Services → Dashboard
   - Check your Translation API quota usage
   - Request a quota increase if needed, or wait for your quota to reset (usually monthly)
4. **For authentication issues**:
   - Verify your service account JSON file is correct and hasn't been modified
   - Check that the service account hasn't been deleted or disabled in Google Cloud Console
   - Ensure the Translation API is enabled for your project
   - Review the [Extension Configuration](pages/ExtensionConfiguration.md) guide
5. **For network issues**: Check your internet connection and firewall settings

### Authorization Errors

**What it means**: Your Google Cloud service account doesn't have the necessary permissions to use the Translation API.

**Common scenarios**:

- Your service account is missing the "Cloud Translation API User" role
- The Translation API isn't enabled for your Google Cloud project
- Your service account key has been revoked or has expired
- You're using the wrong Google Cloud project

**How to fix it**:

1. **Verify IAM permissions**:
   - Go to Google Cloud Console → IAM & Admin → IAM
   - Find your service account in the list
   - Ensure it has the "Cloud Translation API User" role
   - If not, click "Add" and assign the role
2. **Enable the Translation API**:
   - Go to Google Cloud Console → APIs & Services → Library
   - Search for "Cloud Translation API"
   - Click "Enable" if it's not already enabled
3. **Check your service account key**:
   - Verify the JSON file hasn't been modified
   - Check if the key is still active in Google Cloud Console → IAM & Admin → Service Accounts
   - Create a new key if the old one was revoked
4. **Review the setup guide**: See [Authentication](pages/Authentication.md) for detailed permission requirements

## Usage Examples

### Example 1: Basic Translation with Specified Source Language

Input:

```
Text: "Hello, how are you today?"
Source Language: "English"
Target Language: "Spanish"
```

Output:

```
Translated Text: "Hola, ¿cómo estás hoy?"
```

Result: The English text is successfully translated to Spanish.

### Example 2: Translation with Auto-Detected Source Language

Input:

```
Text: "Bonjour, comment allez-vous?"
Source Language: (empty)
Target Language: "English"
```

Output:

```
Translated Text: "Hello, how are you?"
```

Result: The source language (French) is automatically detected and translated to English.

### Example 3: Translation with Language Codes

Input:

```
Text: "Guten Tag, wie geht es Ihnen?"
Source Language: "de"
Target Language: "en"
```

Output:

```
Translated Text: "Good day, how are you?"
```

Result: German text is translated to English using language codes instead of display names.

### Example 4: Multi-line Text Translation

Input:

```
Text: "Line 1: Welcome to our service.
Line 2: We are happy to help you.
Line 3: Please let us know if you have questions."
Source Language: "en"
Target Language: "ja"
```

Output:

```
Translated Text: "1行目：私たちのサービスへようこそ。
2行目：お手伝いできることを嬉しく思います。
3行目：ご質問がございましたらお知らせください。"
```

Result: Multi-line text with formatting is preserved in the translation.

## Business Rules

1. Maximum text length is 30,000 characters per request
2. Source language can be omitted for automatic detection
3. Source and target languages must be different
4. Language codes and display names are both accepted
5. Text formatting (line breaks, spaces) is preserved in translation
6. Translation quality depends on language pair and text complexity
7. Automatic language detection works best with longer text samples

## Limitations

1. Maximum 30,000 characters per translation request
2. Very short text (1-2 words) may have less accurate auto-detection
3. Mixed-language text may not translate correctly
4. Specialized terminology may not translate accurately without context
5. Translation quality varies by language pair
6. API quota limits apply (check Google Cloud Console for your limits)
7. Some rare languages may have limited translation quality

## Best Practices

### 1. Use Appropriate Text Length

For best results, translate complete sentences or paragraphs rather than individual words. Longer text provides better
context for accurate translation.

### 2. Specify Source Language When Known

While auto-detection is convenient, specifying the source language improves translation speed and accuracy, especially
for short text.

### 3. Validate Language Codes

Use [Get Supported Languages](pages/GetSupportedLanguages.md) to retrieve and cache the list of supported languages.
Validate user input against this list before making translation requests.

### 4. Handle Long Text Appropriately

For text exceeding 30,000 characters, split it into smaller chunks at natural boundaries (paragraphs, sentences) and
translate each chunk separately.

### 5. Preserve Context

When splitting long text, ensure each chunk maintains sufficient context for accurate translation. Avoid splitting in
the middle of sentences.

### 6. Cache Translations

For frequently translated content, consider caching translations to reduce API calls and costs.

## Common Use Cases

### 1. Customer Support Communication

Scenario: Translate customer inquiries from various languages to English for support team processing

Action:

1. Receive customer message in unknown language
2. Use Translate To Language with empty Source Language
3. Set Target Language to "English"
4. Process translated message

Result: Support team can understand and respond to multilingual customer inquiries

### 2. Content Localization

Scenario: Translate product descriptions for international e-commerce site

Action:

1. Retrieve product description in English
2. Use Translate To Language with Source Language "English"
3. Translate to each target market language (Spanish, French, German, etc.)
4. Store translations for display

Result: Product descriptions available in multiple languages for global customers

### 3. Document Translation

Scenario: Translate business documents for international partners

Action:

1. Extract text from document
2. Verify text length is within 30,000 character limit
3. Use Translate To Language with known source and target languages
4. Format translated text into new document

Result: Business documents available in partner's preferred language

## Related Catalog Requests

- [Detect Language](pages/DetectLanguage.md) - Identify the language of text before translation
- [Get Supported Languages](pages/GetSupportedLanguages.md) - Retrieve list of all supported languages

## Technical Implementation

### Helper Class

- Class: TranslationResponseService
- Package: com.kristasoft.extensions.ai.nlp.gcp.catalog.service
- Validation: ValidationOrchestrator coordinates TextInputValidator and LanguageValidator
- Service: Delegates to GcpTranslateClient.translateText()

### Validation Flow

1. TextInputValidator checks text is not null/empty and within length limit
2. LanguageValidator verifies target language is valid and supported
3. LanguageValidator verifies source language (if provided) is valid and different from target
4. If validation fails, returns INPUT_ERROR with specific error message

### Telemetry Metrics

- gcp.translate.translateToLanguage - Total translation requests
- gcp.translate.translateToLanguage.success - Successful translations
- gcp.translate.translateToLanguage.error - Failed translations
- gcp.translate.translateToLanguage.validation_failed - Validation failures
- gcp.translate.translateToLanguage.duration - Request duration in milliseconds

## Troubleshooting

### Translation Returns Original Text

Cause: Source and target languages are the same, or translation not available for language pair

Solution:

1. Verify source and target languages are different
2. Check both languages are supported using Get Supported Languages
3. Try specifying source language explicitly instead of auto-detection

### Poor Translation Quality

Cause: Insufficient context, specialized terminology, or rare language pair

Solution:

1. Provide more context by translating complete sentences or paragraphs
2. Specify source language explicitly for better accuracy
3. Review translation and adjust source text if needed
4. Consider post-editing for specialized content

### Text Truncated in Output

Cause: Text may contain special characters causing parsing issues

Solution:

1. Check for special characters or encoding issues in source text
2. Verify text encoding is UTF-8
3. Test with simpler text to isolate the issue

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure service account credentials
- [Authentication](pages/Authentication.md) - Understand authentication and permissions
- [Detect Language](pages/DetectLanguage.md) - Detect language before translation
- [Get Supported Languages](pages/GetSupportedLanguages.md) - View all supported languages

