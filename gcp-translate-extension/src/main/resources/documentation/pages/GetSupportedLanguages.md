# Get Supported Languages

## Overview

Retrieves a comprehensive list of all languages supported by the Google Cloud Translation API. This request returns
language codes and display names for all available languages that can be used for translation and detection.

## Request Details

- **MCP Tool**: ✅ Yes (exposed to the Krista AI agent)

- Area: Translation
- Type: QUERY_SYSTEM
- Retry Support: No
- Ask Agent (MCP): Enabled (callable by the Krista AI agent)

## Input Parameters

This request requires no input parameters.

## Output Parameters

| Parameter Name      | Type         | Description                                                                                 |
|---------------------|--------------|---------------------------------------------------------------------------------------------|
| Supported Languages | List of Text | A list of supported language codes and display names (e.g., "en - English", "es - Spanish") |

### Output Format

Each entry in the list follows the format: `[language_code] - [display_name]`

Examples:

- "en - English"
- "es - Spanish"
- "fr - French"
- "de - German"
- "ja - Japanese"
- "zh - Chinese"

## Validation Rules

This request has no input validation rules as it requires no parameters.

## Error Handling

### System Errors (SYSTEM_ERROR)

Cause: Google Cloud Translation API errors or network issues

Common Scenarios:

- Google Cloud service temporarily unavailable
- Network connectivity problems
- API quota exceeded
- Invalid service account credentials

Resolution:

1. Verify internet connectivity
2. Check Google Cloud Status Dashboard for service outages
3. Verify service account credentials are valid
4. Check API quota limits in Google Cloud Console
5. Review [Extension Configuration](pages/ExtensionConfiguration.md) for credential issues

### Authorization Errors

Cause: Service account lacks required permissions

Common Scenarios:

- Service account missing "Cloud Translation API User" role
- Translation API not enabled for the project
- Service account key revoked or expired

Resolution:

1. Verify service account has correct IAM role
2. Ensure Translation API is enabled
3. Check service account key is valid
4. Review [Authentication](pages/Authentication.md) for permission requirements

## Usage Examples

### Example 1: Retrieve All Supported Languages

Input:

```
(No input parameters required)
```

Output:

```
Supported Languages: [
  "af - Afrikaans",
  "sq - Albanian",
  "am - Amharic",
  "ar - Arabic",
  "hy - Armenian",
  "az - Azerbaijani",
  "eu - Basque",
  "be - Belarusian",
  "bn - Bengali",
  "bs - Bosnian",
  "bg - Bulgarian",
  "ca - Catalan",
  "ceb - Cebuano",
  "zh - Chinese",
  "co - Corsican",
  "hr - Croatian",
  "cs - Czech",
  "da - Danish",
  "nl - Dutch",
  "en - English",
  "eo - Esperanto",
  "et - Estonian",
  "fi - Finnish",
  "fr - French",
  "fy - Frisian",
  "gl - Galician",
  "ka - Georgian",
  "de - German",
  "el - Greek",
  "gu - Gujarati",
  "ht - Haitian Creole",
  "ha - Hausa",
  "haw - Hawaiian",
  "he - Hebrew",
  "hi - Hindi",
  "hmn - Hmong",
  "hu - Hungarian",
  "is - Icelandic",
  "ig - Igbo",
  "id - Indonesian",
  "ga - Irish",
  "it - Italian",
  "ja - Japanese",
  "jv - Javanese",
  "kn - Kannada",
  "kk - Kazakh",
  "km - Khmer",
  "rw - Kinyarwanda",
  "ko - Korean",
  "ku - Kurdish",
  "ky - Kyrgyz",
  "lo - Lao",
  "la - Latin",
  "lv - Latvian",
  "lt - Lithuanian",
  "lb - Luxembourgish",
  "mk - Macedonian",
  "mg - Malagasy",
  "ms - Malay",
  "ml - Malayalam",
  "mt - Maltese",
  "mi - Maori",
  "mr - Marathi",
  "mn - Mongolian",
  "my - Myanmar (Burmese)",
  "ne - Nepali",
  "no - Norwegian",
  "ny - Nyanja (Chichewa)",
  "or - Odia (Oriya)",
  "ps - Pashto",
  "fa - Persian",
  "pl - Polish",
  "pt - Portuguese",
  "pa - Punjabi",
  "ro - Romanian",
  "ru - Russian",
  "sm - Samoan",
  "gd - Scots Gaelic",
  "sr - Serbian",
  "st - Sesotho",
  "sn - Shona",
  "sd - Sindhi",
  "si - Sinhala (Sinhalese)",
  "sk - Slovak",
  "sl - Slovenian",
  "so - Somali",
  "es - Spanish",
  "su - Sundanese",
  "sw - Swahili",
  "sv - Swedish",
  "tl - Tagalog (Filipino)",
  "tg - Tajik",
  "ta - Tamil",
  "tt - Tatar",
  "te - Telugu",
  "th - Thai",
  "tr - Turkish",
  "tk - Turkmen",
  "uk - Ukrainian",
  "ur - Urdu",
  "ug - Uyghur",
  "uz - Uzbek",
  "vi - Vietnamese",
  "cy - Welsh",
  "xh - Xhosa",
  "yi - Yiddish",
  "yo - Yoruba",
  "zu - Zulu"
]
```

Result: Complete list of 100+ supported languages with codes and display names.

### Example 2: Verify Language Support

Input:

```
(No input parameters required)
```

Action:

1. Call Get Supported Languages
2. Search the returned list for specific language
3. Verify language code exists

Result: Confirmation that a specific language is supported for translation.

### Example 3: Build Language Selection UI

Input:

```
(No input parameters required)
```

Action:

1. Call Get Supported Languages
2. Parse the returned list
3. Populate dropdown or selection UI with language options

Result: User interface populated with all available language options.

## Business Rules

1. Returns all languages supported by Google Cloud Translation API
2. Language list includes both language codes and display names
3. Language codes follow ISO 639-1 standard (2-letter codes)
4. Display names are in English
5. The list includes 100+ languages
6. Language support is determined by Google Cloud and may be updated periodically

## Limitations

1. Display names are only available in English
2. Language list is read-only and cannot be modified
3. Some regional language variants may be grouped under a single code
4. API quota limits apply (check Google Cloud Console for your limits)
5. Language support is subject to Google Cloud Translation API capabilities

## Best Practices

### 1. Cache the Results

The list of supported languages changes infrequently. Cache the results to reduce API calls and improve performance.

Recommended approach:

- Retrieve the list once during application initialization
- Store in memory or persistent cache
- Refresh periodically (e.g., daily or weekly)
- Use cached list for validation and UI population

### 2. Use for Input Validation

Validate user-provided language codes against the supported languages list before making translation or detection
requests.

Validation flow:

1. Retrieve and cache supported languages
2. When user provides language input, check against cached list
3. Reject invalid language codes with helpful error message
4. Suggest valid alternatives if possible

### 3. Build User-Friendly Interfaces

Use the display names to create user-friendly language selection interfaces rather than showing raw language codes.

UI recommendations:

- Display format: "English (en)" or "Spanish (es)"
- Sort alphabetically by display name
- Include search/filter functionality for long lists
- Show both code and name for clarity

### 4. Handle API Updates

Google may add new languages over time. Design your application to handle dynamic language lists rather than hardcoding
language options.

### 5. Test Configuration

Use this request as a configuration test to verify the extension is properly configured and can communicate with Google
Cloud.

## Common Use Cases

### 1. Configuration Verification

Scenario: Verify extension is properly configured after setup

Action:

1. Complete extension configuration
2. Call Get Supported Languages
3. Verify successful response with language list

Result: Confirmation that credentials are valid and API is accessible

### 2. Language Dropdown Population

Scenario: Create language selection dropdown in user interface

Action:

1. Call Get Supported Languages
2. Parse the returned list
3. Extract language codes and display names
4. Populate dropdown options

Result: User-friendly language selection interface

### 3. Input Validation

Scenario: Validate user-provided language codes before translation

Action:

1. Retrieve and cache supported languages list
2. User provides language code for translation
3. Validate code exists in supported languages
4. Proceed with translation or show error

Result: Prevent invalid translation requests and provide helpful feedback

### 4. Multi-Language Application Setup

Scenario: Configure application to support multiple languages

Action:

1. Call Get Supported Languages
2. Review available languages
3. Select target languages for application
4. Configure translation workflows for selected languages

Result: Application configured to support specific language set

### 5. Language Coverage Analysis

Scenario: Determine if required languages are supported

Action:

1. Define list of required languages for business needs
2. Call Get Supported Languages
3. Compare required languages against supported list
4. Identify any gaps in coverage

Result: Understanding of language support coverage for business requirements

## Related Catalog Requests

- [Translate To Language](pages/TranslateToLanguage.md) - Use supported languages for translation
- [Detect Language](pages/DetectLanguage.md) - Detect languages from the supported list

## Technical Implementation

### Helper Class

- Class: TranslationResponseService
- Package: com.kristasoft.extensions.ai.nlp.gcp.catalog.service
- Validation: No input validation required
- Service: Delegates to GcpTranslateClient.getSupportedLanguages()

### Response Processing

1. Calls Google Cloud Translation API to retrieve supported languages
2. Formats each language as "[code] - [display_name]"
3. Returns list of formatted language strings
4. No caching is performed at extension level (implement in calling application)

### Telemetry Metrics

- gcp.translate.getSupportedLanguages - Total requests
- gcp.translate.getSupportedLanguages.success - Successful retrievals
- gcp.translate.getSupportedLanguages.error - Failed retrievals
- gcp.translate.getSupportedLanguages.duration - Request duration in milliseconds

## Troubleshooting

### Empty or Null Response

Cause: API communication failure or credential issues

Solution:

1. Verify service account credentials are configured correctly
2. Check Translation API is enabled in Google Cloud Console
3. Verify network connectivity to Google Cloud
4. Review [Extension Configuration](pages/ExtensionConfiguration.md)

### Incomplete Language List

Cause: API response truncated or parsing error

Solution:

1. Check for error messages in response
2. Verify API quota has not been exceeded
3. Retry the request
4. Contact support if issue persists

### Permission Denied Error

Cause: Service account lacks required permissions

Solution:

1. Verify service account has "Cloud Translation API User" role
2. Check Translation API is enabled for the project
3. Review [Authentication](pages/Authentication.md) for permission requirements
4. Ensure service account key is valid and not revoked

### Network Timeout

Cause: Network connectivity issues or slow connection

Solution:

1. Check internet connectivity
2. Verify firewall allows HTTPS to googleapis.com
3. Retry the request
4. Check Google Cloud Status Dashboard for service issues

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure service account credentials
- [Authentication](pages/Authentication.md) - Understand authentication and permissions
- [Translate To Language](pages/TranslateToLanguage.md) - Use supported languages for translation
- [Detect Language](pages/DetectLanguage.md) - Detect languages from supported list

