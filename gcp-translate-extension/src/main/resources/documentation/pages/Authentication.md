# Authentication

## Overview

The GCP Translate Extension uses Google Cloud service account authentication to securely access the Google Cloud
Translation API. This page explains the authentication mechanism, required permissions, and security best practices.

## Authentication Mode

### Service Account Authentication

The extension uses Service Account authentication, which is designed for server-to-server communication:

- Authentication Type: Service Account (JSON Key File)
- OAuth Flow: Not required
- User Interaction: None after initial configuration
- Token Management: Automatic (handled by Google Cloud client libraries)
- Credential Format: JSON key file

### Why Service Account?

Service accounts are the recommended authentication method for this extension because:

1. Server-to-Server: Designed for application-to-API communication without user interaction
2. Consistent Access: Same credentials work across all users and sessions
3. No Expiration: Service account keys don't expire unless manually revoked
4. Simplified Management: Single set of credentials to configure and maintain
5. Programmatic Access: Ideal for automated, background operations

## Required API Scopes and Permissions

### Cloud Translation API Permissions

The service account requires the following permissions to function properly:

| Permission                              | Description                      | Required For                    |
|-----------------------------------------|----------------------------------|---------------------------------|
| `cloudtranslate.translations.translate` | Translate text between languages | Translate To Language request   |
| `cloudtranslate.languages.list`         | List supported languages         | Get Supported Languages request |
| `cloudtranslate.detections.detect`      | Detect language of text          | Detect Language request         |

### Required IAM Role

Grant the service account the following IAM role:

- Role Name: Cloud Translation API User
- Role ID: `roles/cloudtranslate.user`
- Scope: Project-level

This role includes all necessary permissions for the extension to function.

### Alternative Roles (Not Recommended)

While these roles would work, they grant more permissions than necessary:

- Cloud Translation API Admin (`roles/cloudtranslate.admin`): Includes administrative permissions not needed
- Editor (`roles/editor`): Grants broad permissions across the entire project
- Owner (`roles/owner`): Grants full control of the project

Always follow the principle of least privilege and use only the Cloud Translation API User role.

## Service Account Key Management

### Key File Structure

The service account JSON key file contains:

```json
{
  "type": "service_account",
  "project_id": "your-project-id",
  "private_key_id": "key-id",
  "private_key": "-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n",
  "client_email": "service-account-name@project-id.iam.gserviceaccount.com",
  "client_id": "1234567890",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/..."
}
```

### Key Components

- type: Must be "service_account"
- project_id: Your GCP project identifier
- private_key: RSA private key for authentication
- client_email: Service account email address
- token_uri: OAuth 2.0 token endpoint

### Key Lifecycle

Service account keys:

- Do not expire automatically
- Remain valid until manually deleted or revoked
- Can be rotated without service interruption
- Should be rotated regularly (recommended: every 90 days)

## Authentication Flow

### Initial Authentication

1. Extension loads service account JSON key file
2. Google Cloud client library reads the private key
3. Client library creates a signed JWT (JSON Web Token)
4. JWT is exchanged for an access token at Google's token endpoint
5. Access token is used for API requests

### Token Management

The Google Cloud client library automatically handles:

- Access token generation
- Token caching
- Token refresh when expired
- Retry logic for transient failures

Access tokens:

- Lifetime: 1 hour
- Automatically refreshed by the client library
- No manual intervention required

## Security Best Practices

### Credential Protection

1. Never Commit to Version Control
    - Add `*.json` to `.gitignore`
    - Never commit service account keys to Git repositories
    - Use secret management systems for production deployments

2. Restrict File Access
    - Limit file system permissions to authorized users only
    - Store in secure, encrypted storage
    - Use environment variables or secret managers in production

3. Rotate Keys Regularly
    - Create new keys every 90 days
    - Delete old keys after rotation
    - Test new keys before deleting old ones

4. Monitor Key Usage
    - Enable audit logging in Google Cloud Console
    - Review service account activity regularly
    - Set up alerts for unusual activity

### Service Account Security

1. Principle of Least Privilege
    - Grant only the Cloud Translation API User role
    - Avoid granting Editor or Owner roles
    - Review permissions periodically

2. Dedicated Service Accounts
    - Create separate service accounts for each application
    - Don't reuse service accounts across multiple systems
    - Use descriptive names for easy identification

3. Disable Unused Keys
    - Delete service account keys that are no longer in use
    - Limit the number of active keys per service account
    - Regularly audit active keys

### Network Security

1. Use HTTPS Only
    - All API communication uses encrypted HTTPS
    - Google Cloud enforces TLS 1.2 or higher
    - Certificate validation is automatic

2. Firewall Configuration
    - Allow outbound HTTPS to `*.googleapis.com`
    - Allow outbound HTTPS to `accounts.google.com`
    - Allow outbound HTTPS to `oauth2.googleapis.com`

3. IP Restrictions (Optional)
    - Use VPC Service Controls for additional security
    - Restrict API access to specific IP ranges
    - Implement network-level access controls

## Troubleshooting Authentication

### Invalid Credentials

Error: "Invalid service account credentials. Please check your credentials file."

Causes:

- Malformed JSON file
- Incorrect file format
- Service account deleted

Resolution:

1. Validate JSON syntax using a JSON validator
2. Verify all required fields are present
3. Check service account exists in Google Cloud Console
4. Download a fresh key file if needed

### Permission Denied

Error: "Permission denied. Service account lacks required permissions."

Causes:

- Missing IAM role assignment
- Incorrect role granted
- Permissions not yet propagated

Resolution:

1. Verify service account has Cloud Translation API User role
2. Wait 1-2 minutes for permission changes to propagate
3. Check IAM policy in Google Cloud Console
4. Ensure API is enabled for the project

### API Not Enabled

Error: "Google Cloud Translation API error: API not enabled"

Causes:

- Translation API not enabled for the project
- Using credentials from wrong project

Resolution:

1. Enable Cloud Translation API in Google Cloud Console
2. Navigate to APIs & Services > Library
3. Search for "Cloud Translation API"
4. Click Enable and wait for activation

### Network Errors

Error: "Network error while connecting to Google Cloud."

Causes:

- Firewall blocking HTTPS traffic
- Network connectivity issues
- DNS resolution problems

Resolution:

1. Test internet connectivity
2. Verify firewall allows HTTPS to googleapis.com
3. Check DNS resolution for googleapis.com
4. Review proxy settings if applicable

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure service account credentials
- [Creating GCP Service Account](pages/CreatingGCPServiceAccount.md) - Create and configure service account
- [Translate To Language](pages/TranslateToLanguage.md) - Use the translation functionality

