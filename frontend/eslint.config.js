import js from '@eslint/js';
import { defineConfig, globalIgnores } from 'eslint/config';
import prettier from 'eslint-config-prettier/flat';
import reactHooks from 'eslint-plugin-react-hooks';
import { reactRefresh } from 'eslint-plugin-react-refresh';
import globals from 'globals';
import tseslint from 'typescript-eslint';
import { architectureRules } from './eslint.architecture.js';

export default defineConfig([
  globalIgnores(['dist', 'coverage', 'playwright-report', 'test-results']),
  {
    files: ['**/*.{ts,tsx}'],
    extends: [
      js.configs.recommended,
      tseslint.configs.recommended,
      reactHooks.configs.flat.recommended,
      reactRefresh.configs.vite(),
    ],
    languageOptions: { ecmaVersion: 2023, globals: globals.browser },
  },
  {
    files: ['*.config.{js,ts}', 'eslint.*.js', 'e2e/**/*.ts', 'tests/**/*.ts'],
    languageOptions: { globals: globals.node },
  },
  ...architectureRules,
  prettier,
]);
