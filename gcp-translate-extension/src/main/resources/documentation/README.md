# GCP Translate Extension

## Overview

The GCP Translate Extension enables seamless integration with Google Cloud Translation API, providing powerful language
translation and detection capabilities within the Krista platform. This extension leverages Google's advanced Neural
Machine Translation (NMT) technology to deliver high-quality translations across 100+ languages.

## Key Features

- Text Translation: Translate text between any supported language pairs using Google's Neural Machine Translation
- Language Detection: Automatically identify the language of any text input
- Comprehensive Language Support: Access to 100+ languages supported by Google Cloud Translation API
- Real-time Translation: Fast, accurate translations powered by Google's machine learning models
- Error Handling: Robust error handling with detailed validation and user-friendly error messages
- Automatic Language Detection: Optional source language auto-detection for translation requests
- Ask Agent (MCP) Support: All translation operations are exposed as tools the Krista AI agent can call directly

## Quick Start Guide

### Prerequisites

Before using this extension, you need:

1. A Google Cloud Platform (GCP) account
2. A GCP project with Translation API enabled
3. Service account credentials (JSON file)
4. Appropriate API permissions configured

### Getting Started

1. Configure the Extension
    - Navigate to [Extension Configuration](pages/ExtensionConfiguration.md)
    - Upload your service account JSON credentials
    - Configure extension settings

2. Understand Authentication
    - Review [Authentication](pages/Authentication.md) to understand service account setup
    - Learn about required API scopes and permissions

3. Create GCP Service Account
    - Follow the guide in [Creating GCP Service Account](pages/CreatingGCPServiceAccount.md)
    - Enable Translation API for your project
    - Download service account credentials

4. Start Using Catalog Requests
    - Explore available translation operations
    - Review usage examples and best practices

## Documentation Structure

### Getting Started

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure the extension with your GCP credentials
- [Authentication](pages/Authentication.md) - Understand service account authentication and API permissions
- [Creating GCP Service Account](pages/CreatingGCPServiceAccount.md) - Step-by-step guide to set up GCP project and
  credentials

### Translation Operations

- [Translate To Language](pages/TranslateToLanguage.md) - Translate text from one language to another
- [Detect Language](pages/DetectLanguage.md) - Automatically detect the language of text
- [Get Supported Languages](pages/GetSupportedLanguages.md) - Retrieve list of all supported languages

### Additional Resources

- [Release Notes](pages/ReleaseNotes.md) - Version history, new features, and known issues

## Support & Resources

### Extension Information

- Version: 2.0.6
- API Version: Google Cloud Translation API v3
- Supported Languages: 100+ languages
- Maximum Text Length: 30,000 characters per request

### Google Cloud Resources

- [Google Cloud Translation API Documentation](https://cloud.google.com/translate/docs)
- [Supported Languages List](https://cloud.google.com/translate/docs/languages)
- [API Quotas and Limits](https://cloud.google.com/translate/quotas)
- [Pricing Information](https://cloud.google.com/translate/pricing)

### Technical Support

For technical support and questions:

- Review the troubleshooting sections in each catalog request documentation
- Check the [Authentication](pages/Authentication.md) guide for credential issues
- Consult Google Cloud Translation API documentation for API-specific questions

## Common Use Cases

### Business Translation

Translate business documents, emails, and communications across multiple languages to support global operations.

### Content Localization

Localize website content, product descriptions, and marketing materials for international audiences.

### Customer Support

Provide multilingual customer support by translating customer inquiries and support responses in real-time.

### Data Analysis

Analyze multilingual data by translating text content into a common language for processing and insights.

## Best Practices

1. Use Language Detection Wisely: For unknown source languages, use the Detect Language request first or leave source
   language empty in translation requests
2. Validate Input Length: Ensure text does not exceed 30,000 characters before translation
3. Cache Supported Languages: Retrieve supported languages once and cache the results to reduce API calls
4. Handle Errors Gracefully: Implement proper error handling for validation failures and API errors
5. Monitor API Usage: Track your API usage to stay within Google Cloud quotas and manage costs

## Understanding Error Messages

This extension provides clear, actionable error messages to help you resolve issues quickly:

### Common Error Types

**Input Validation Errors**: These occur when the data you provide doesn't meet requirements
- Empty or missing text
- Text exceeding 30,000 characters
- Invalid or unsupported language codes
- Source and target languages being the same

**Authentication Errors**: These indicate issues with your Google Cloud credentials
- Invalid or expired service account credentials
- Missing required permissions
- Translation API not enabled for your project

**Service Errors**: These are temporary issues with Google Cloud services
- Service temporarily unavailable
- API quota limits exceeded
- Network connectivity problems

Each error message includes:
- A clear explanation of what went wrong
- Specific steps to resolve the issue
- Suggestions for alternative actions

## Getting Help

If you encounter issues:

1. **Read the error message carefully** - It contains specific guidance on how to resolve the issue
2. Check the specific catalog request documentation for detailed error handling
3. Review the [Extension Configuration](pages/ExtensionConfiguration.md) to ensure proper setup
4. Verify your service account has the required permissions in [Authentication](pages/Authentication.md)
5. Consult the troubleshooting sections in individual request pages
6. For authentication issues, see the [Creating GCP Service Account](pages/CreatingGCPServiceAccount.md) guide

## Next Steps

- Configure the extension: [Extension Configuration](pages/ExtensionConfiguration.md)
- Set up authentication: [Creating GCP Service Account](pages/CreatingGCPServiceAccount.md)
- Start translating: [Translate To Language](pages/TranslateToLanguage.md)