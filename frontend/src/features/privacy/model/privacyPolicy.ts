/** Version the user accepts when registering; the backend rejects any other (identity PrivacyPolicy.CURRENT_VERSION). */
export const PRIVACY_POLICY_VERSION = '1.0';

export const PRIVACY_POLICY_EFFECTIVE_DATE = '14 de diciembre de 2026';

export const DATA_CONTROLLER_NAME = 'Manuel Sebastián Cetre Ramirez';

/**
 * Contact email for data protection requests, set at build time with VITE_PRIVACY_CONTACT_EMAIL.
 * Returns null while it has not been published.
 */
export function privacyContactEmail(): string | null {
  const email = import.meta.env.VITE_PRIVACY_CONTACT_EMAIL?.trim();
  return email ? email : null;
}
