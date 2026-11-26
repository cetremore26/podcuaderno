/// <reference types="vite/client" />

interface ImportMetaEnv {
  /** Contact email for data protection requests, shown on the privacy policy page. Empty until published. */
  readonly VITE_PRIVACY_CONTACT_EMAIL?: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
