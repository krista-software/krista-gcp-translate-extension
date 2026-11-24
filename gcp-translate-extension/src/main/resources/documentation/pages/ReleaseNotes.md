# Release Notes

## Overview

This page documents the version history, new features, improvements, bug fixes, and known issues for the GCP Translate
Extension.

---

## Version 2.0.6

**Release Date**: July 2026  

- **Krista Service APIs Java**: 1.0.126

### New Features

- **Ask Agent (MCP) Support**: All three translation catalog requests — **Translate To Language**, **Detect Language**, and **Get Supported Languages** — are now exposed as tools to the Krista AI agent (`tool = true`), so the agent can call them directly during a conversation.
- **In-Platform Change Log**: These release notes are now surfaced inside the Krista platform through the extension's change-log tab.

### Resolved Bugs

- Not Available

### Dependency Updates

- Upgraded `krista-apis` and `extension-impl-anno-processors` from 1.0.118 to 1.0.126.

### Known Issues

- Not Available

### Limitations and Caveats

- Unchanged from Version 2.0.4 (see below).

---

## Version 2.0.4

**Release Date**: October 2024  
**Status**: Current Release

### Extension Information

| Property                     | Value                           |
|------------------------------|---------------------------------|
| **Version**                  | 2.0.5                           |
| **Developer**                | Vrushali Gaikwad                |
| **Krista Service APIs Java** | 1.0.118                         |
| **Global Catalog Version**   | GC.2025.11.1                    |
| **API Version**              | Google Cloud Translation API v3 |

### What's New

#### New Features

- **Enhanced Documentation**: Comprehensive documentation following Krista Extension documentation guidelines
    - Detailed catalog request pages with 15+ sections each
    - Step-by-step configuration guides with screenshots
    - Authentication flow documentation with OAuth 2.0 details
    - Troubleshooting guides for common issues

- **Improved Error Handling**: Enhanced error messages and validation
    - Clear INPUT_ERROR, LOGIC_ERROR, and SYSTEM_ERROR categorization
    - User-friendly error messages with actionable resolutions
    - Detailed validation rules for all input parameters

#### Improvements

- **Code Quality**: Refactored codebase with centralized constants
    - 68+ constants extracted into Constants class
    - Improved maintainability and consistency
    - Better telemetry and logging

- **Documentation Structure**: Organized documentation by functional categories
    - Getting Started section with configuration and authentication
    - Translation Operations section with all catalog requests
    - Cross-referenced pages for easy navigation

### Catalog Requests

This version includes the following catalog requests:

#### Translation Operations (3 requests)

1. **Translate To Language** - Translate text from one language to another
    - Supports 100+ language pairs
    - Automatic source language detection
    - Maximum 30,000 characters per request

2. **Detect Language** - Automatically detect the language of text
    - Returns language code and confidence score
    - Supports all Google Cloud Translation API languages

3. **Get Supported Languages** - Retrieve list of all supported languages
    - Returns language codes and display names
    - Optional target language for localized names

### Technical Details

#### API Integration

- **API Version**: Google Cloud Translation API v3
- **Authentication**: Service Account (JSON credentials)
- **Endpoint**: `https://translation.googleapis.com/v3`
- **Required Scopes**: `https://www.googleapis.com/auth/cloud-translation`

#### Performance

- **Maximum Text Length**: 30,000 characters per request
- **Response Time**: Typically < 2 seconds for standard requests
- **Retry Support**: Configurable retry mechanism for failed requests

### Known Issues

**None reported** - No known issues in this release.

### Limitations and Caveats

1. **Text Input Only**: This extension exclusively accepts text as the input parameter for all catalog requests
    - Does not support file uploads or binary content
    - HTML tags are preserved but not interpreted

2. **Character Limit**: Maximum 30,000 characters per translation request
    - Longer texts must be split into multiple requests
    - Character count includes whitespace and special characters

3. **Language Detection Accuracy**: Language detection confidence varies
    - Short texts (< 50 characters) may have lower confidence
    - Mixed-language texts may not be accurately detected

4. **API Quotas**: Subject to Google Cloud Translation API quotas
    - Default quota: 500,000 characters per day
    - Higher quotas available with billing enabled
    - Monitor usage to avoid quota exhaustion

5. **Service Account Only**: Only supports service account authentication
    - Does not support user account authentication
    - Requires GCP project with Translation API enabled

### Breaking Changes

**None** - This release maintains backward compatibility with previous versions.

### Deprecations

**None** - No features or APIs deprecated in this release.

### Security Updates

- **Credential Handling**: Secure storage of service account credentials
- **API Communication**: All API calls use HTTPS encryption
- **Error Messages**: Sensitive information not exposed in error messages

### Migration Guide

If upgrading from a previous version:

1. **No Migration Required**: This version is fully backward compatible
2. **Configuration**: Existing configurations will continue to work
3. **Credentials**: No changes to service account credential format
4. **API Calls**: All existing catalog requests remain unchanged

### Dependencies

#### Required Dependencies

- **Krista Service APIs Java**: 1.0.118 or higher
- **Google Cloud Translation API**: v3
- **Java**: Java 21 or higher

#### Updated Dependencies

- Updated to latest Krista Service APIs Java (1.0.118)
- Updated Google Cloud Translation API client libraries

### Documentation Updates

- **New Pages Created**: 6 comprehensive documentation pages
    - README.md - Main landing page
    - ExtensionConfiguration.md - Configuration guide
    - Authentication.md - Authentication details
    - CreatingGCPServiceAccount.md - GCP setup guide
    - TranslateToLanguage.md - Translation catalog request
    - DetectLanguage.md - Language detection catalog request
    - GetSupportedLanguages.md - Supported languages catalog request

- **Screenshots Added**: 22 screenshots for step-by-step guides
    - GCP Console navigation
    - Service account creation
    - API enablement
    - Extension configuration

### Support and Resources

#### Getting Help

- Review the [Extension Configuration](pages/ExtensionConfiguration.md) guide
- Check the [Authentication](pages/Authentication.md) documentation
- Consult individual catalog request pages for specific issues
- Review the [Creating GCP Service Account](pages/CreatingGCPServiceAccount.md) guide

#### External Resources

- [Google Cloud Translation API Documentation](https://cloud.google.com/translate/docs)
- [Supported Languages List](https://cloud.google.com/translate/docs/languages)
- [API Quotas and Limits](https://cloud.google.com/translate/quotas)
- [Pricing Information](https://cloud.google.com/translate/pricing)

### Feedback and Contributions

We welcome feedback and suggestions for improving this extension:

- Report issues through your Krista support channel
- Suggest new features or improvements
- Share use cases and best practices

---

## Previous Versions

### Version 2.0.3 and Earlier

Previous versions of this extension were released without comprehensive documentation. Version 2.0.4 represents a
significant documentation and code quality improvement while maintaining full backward compatibility.

---

## Upcoming Features

### Planned for Future Releases

- **Batch Translation**: Support for translating multiple texts in a single request
- **Document Translation**: Support for translating entire documents (PDF, DOCX, etc.)
- **Glossary Support**: Custom terminology and translation glossaries
- **Translation Memory**: Reuse of previous translations for consistency
- **Advanced Language Detection**: Multi-language detection in single text

**Note**: Planned features are subject to change based on user feedback and Google Cloud API capabilities.

---

## Version History Summary

| Version | Release Date | Key Changes                                                       |
|---------|--------------|-------------------------------------------------------------------|
| 2.0.6   | July 2026    | Ask Agent (MCP) support for all translation requests; deps → 1.0.126 |
| 2.0.4   | October 2024 | Enhanced documentation, code refactoring, improved error handling |
| 2.0.3   | Earlier      | Previous release (limited documentation)                          |

---

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure the extension
- [Authentication](pages/Authentication.md) - Authentication details
- [Translate To Language](pages/TranslateToLanguage.md) - Translation catalog request
- [Detect Language](pages/DetectLanguage.md) - Language detection
- [Get Supported Languages](pages/GetSupportedLanguages.md) - Supported languages

