# Detect Language

## Overview

Automatically detects the language of the provided text using Google Cloud's machine learning model. This request
analyzes the input text and returns the identified language code.

## Request Details

- **MCP Tool**: ✅ Yes (exposed to the Krista AI agent)

- Area: Translation
- Type: QUERY_SYSTEM
- Retry Support: No
- Ask Agent (MCP): Enabled (callable by the Krista AI agent)

## Input Parameters

| Parameter Name | Type      | Required | Description                                                            | Example                        |
|----------------|-----------|----------|------------------------------------------------------------------------|--------------------------------|
| Text           | Paragraph | Yes      | The text to analyze for language detection (maximum 30,000 characters) | "Bonjour, comment allez-vous?" |

### Parameter Details

#### Text

- Maximum length: 30,000 characters
- Cannot be null or empty
- Whitespace-only text is not allowed
- Supports all Unicode characters
- Longer text samples provide more accurate detection
- Minimum recommended length: 10-20 characters for reliable detection

## Output Parameters

| Parameter Name    | Type      | Description                                                                          |
|-------------------|-----------|--------------------------------------------------------------------------------------|
| Detected Language | Paragraph | The language code of the detected language (e.g., "fr" for French, "en" for English) |

## Validation Rules

| Validation                     | Error Message                                       | Resolution                                |
|--------------------------------|-----------------------------------------------------|-------------------------------------------|
| Text is null or empty          | "Please enter some text to translate or detect. The text field cannot be empty."       | Enter the text you want to analyze |
| Text exceeds 30,000 characters | "Your text is too long ([current] characters). Please shorten it to 30,000 characters or less, or split it into multiple requests." | Reduce text length or split into smaller parts    |

## Error Handling

### Input Validation Errors (INPUT_ERROR)

**What it means**: The text you provided doesn't meet the requirements for language detection.

**Common scenarios**:

- You didn't enter any text to analyze
- Your text is longer than 30,000 characters
- The text contains only whitespace (spaces, tabs, line breaks)

**How to fix it**:

1. **Read the error message** - It tells you exactly what's wrong and how to fix it
2. Make sure you've entered actual text in the Text field
3. Check that your text is under 30,000 characters (you can use a character counter)
4. Ensure the text contains real content, not just spaces or blank lines
5. For best results, use at least 10-20 characters of actual text

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

### Example 1: Detect French Language

Input:

```
Text: "Bonjour, comment allez-vous aujourd'hui?"
```

Output:

```
Detected Language: "fr"
```

Result: The text is identified as French.

### Example 2: Detect Spanish Language

Input:

```
Text: "Hola, ¿cómo estás? Me llamo María."
```

Output:

```
Detected Language: "es"
```

Result: The text is identified as Spanish.

### Example 3: Detect Japanese Language

Input:

```
Text: "こんにちは、お元気ですか？"
```

Output:

```
Detected Language: "ja"
```

Result: The text is identified as Japanese.

### Example 4: Detect Language from Longer Text

Input:

```
Text: "The quick brown fox jumps over the lazy dog. This is a sample sentence in English to demonstrate language detection with a longer text sample."
```

Output:

```
Detected Language: "en"
```

Result: The text is identified as English with high confidence due to longer sample.

### Example 5: Detect Language Before Translation

Input:

```
Text: "Guten Tag, wie geht es Ihnen?"
```

Output:

```
Detected Language: "de"
```

Result: Detected as German. This result can be used as the source language for subsequent translation requests.

## Business Rules

1. Maximum text length is 30,000 characters per request
2. Longer text samples provide more accurate language detection
3. Very short text (1-2 words) may have lower detection confidence
4. Mixed-language text will return the predominant language
5. Detection works for all languages supported by Google Cloud Translation API
6. Language codes returned are ISO 639-1 format (e.g., "en", "fr", "es")

## Limitations

1. Maximum 30,000 characters per detection request
2. Very short text (1-3 characters) may not be reliably detected
3. Mixed-language text may produce unexpected results
4. Proper nouns and numbers may affect detection accuracy
5. Text with heavy use of emojis or special characters may reduce accuracy
6. API quota limits apply (check Google Cloud Console for your limits)

## Best Practices

### 1. Use Sufficient Text Length

For reliable language detection, provide at least 10-20 characters of text. Longer samples (50+ characters) provide the
most accurate results.

### 2. Use Clean Text

Remove excessive special characters, emojis, or formatting that might interfere with language detection. Plain text
works best.

### 3. Handle Mixed-Language Content

If your text contains multiple languages, consider splitting it into segments and detecting each segment separately.

### 4. Validate Detection Results

For critical applications, consider validating the detected language against expected languages or user preferences.

### 5. Cache Detection Results

For static content, cache language detection results to reduce API calls and improve performance.

### 6. Combine with Translation

Use Detect Language before translation when the source language is unknown. This ensures accurate translation by
providing the correct source language.

## Common Use Cases

### 1. Automatic Content Routing

Scenario: Route customer support tickets to language-specific teams

Action:

1. Receive customer support ticket
2. Use Detect Language to identify ticket language
3. Route to appropriate language support team

Result: Tickets automatically routed to teams that speak the customer's language

### 2. Content Classification

Scenario: Classify and organize multilingual documents by language

Action:

1. Process document collection
2. Use Detect Language on each document
3. Tag and organize documents by detected language

Result: Documents organized by language for easier management and search

### 3. Translation Workflow

Scenario: Translate user-generated content with unknown source language

Action:

1. Receive user content in unknown language
2. Use Detect Language to identify source language
3. Use [Translate To Language](pages/TranslateToLanguage.md) with detected language as source
4. Display translated content

Result: Seamless translation of content regardless of source language

### 4. Language Analytics

Scenario: Analyze language distribution in customer communications

Action:

1. Collect customer messages over time
2. Use Detect Language on each message
3. Aggregate and analyze language distribution
4. Generate reports on language usage patterns

Result: Insights into customer language preferences and geographic distribution

### 5. Input Validation

Scenario: Verify user input is in expected language

Action:

1. User submits form in specific language context
2. Use Detect Language to verify input language
3. Prompt user if language doesn't match expected language
4. Accept or request correction

Result: Ensure data quality by validating language of user input

## Related Catalog Requests

- [Translate To Language](pages/TranslateToLanguage.md) - Translate text after detecting its language
- [Get Supported Languages](pages/GetSupportedLanguages.md) - View all languages that can be detected

## Technical Implementation

### Helper Class

- Class: TranslationResponseService
- Package: com.kristasoft.extensions.ai.nlp.gcp.catalog.service
- Validation: ValidationOrchestrator coordinates TextInputValidator
- Service: Delegates to GcpTranslateClient.detect()

### Validation Flow

1. TextInputValidator checks text is not null/empty
2. TextInputValidator verifies text length is within 30,000 character limit
3. If validation fails, returns INPUT_ERROR with specific error message
4. If validation passes, calls Google Cloud Translation API for detection

### Telemetry Metrics

- gcp.translate.detectLanguage - Total detection requests
- gcp.translate.detectLanguage.success - Successful detections
- gcp.translate.detectLanguage.error - Failed detections
- gcp.translate.detectLanguage.validation_failed - Validation failures
- gcp.translate.detectLanguage.duration - Request duration in milliseconds

## Troubleshooting

### Unexpected Language Detected

Cause: Text is too short, contains mixed languages, or has ambiguous content

Solution:

1. Provide longer text sample for more accurate detection
2. Remove special characters or formatting
3. Ensure text is in a single language
4. Verify text doesn't consist primarily of proper nouns or numbers

### Detection Returns Wrong Language for Short Text

Cause: Insufficient text for reliable detection

Solution:

1. Provide at least 10-20 characters of text
2. Use complete words or sentences rather than fragments
3. Consider using a default language for very short text
4. Validate detection result against expected languages

### Low Confidence in Detection

Cause: Ambiguous text, mixed languages, or unusual content

Solution:

1. Increase text length for better confidence
2. Use text with more distinctive language features
3. Remove non-linguistic content (URLs, email addresses, etc.)
4. Consider manual language selection for ambiguous cases

### Detection Fails for Specific Language

Cause: Language may not be supported or text may be corrupted

Solution:

1. Verify language is supported using [Get Supported Languages](pages/GetSupportedLanguages.md)
2. Check text encoding is UTF-8
3. Verify text is not corrupted or garbled
4. Test with known sample text in that language

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure service account credentials
- [Authentication](pages/Authentication.md) - Understand authentication and permissions
- [Translate To Language](pages/TranslateToLanguage.md) - Translate text after detection
- [Get Supported Languages](pages/GetSupportedLanguages.md) - View all supported languages

