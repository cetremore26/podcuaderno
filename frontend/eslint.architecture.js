// Import boundaries from docs/05 §6.2 (ADR-0004). Later config objects replace "no-restricted-imports", so each scope repeats the base patterns.

const deepFeatureImport = {
  regex: '^@/features/[^/]+/.+',
  message: 'Import other features only through their index.ts (docs/05 §6.2, rule 3).',
};

const climbingImport = {
  regex: '^\.\./\.\./',
  message: 'Do not climb more than one directory; use the @/ alias for imports across areas.',
};

const coreFramework = {
  group: [
    'react',
    'react/*',
    'react-dom',
    'react-dom/*',
    '@tanstack/*',
    'react-hook-form',
    '@hookform/*',
  ],
  message: 'core/ is framework-free (docs/05 §6.2, rule 2).',
};

const coreOutward = {
  regex: '^@/(app|features|shared)(/|$)',
  message: 'core/ depends only on core/.',
};

const sharedOutward = {
  regex: '^@/(app|features)(/|$)',
  message: 'shared/ may depend on core/ but not on app/ or features/.',
};

const featuresToApp = {
  regex: '^@/app(/|$)',
  message: 'Features must not depend on app/.',
};

const componentsToApi = {
  group: ['../api', '../api/*'],
  message: 'Components use hooks; only hooks call api/ (docs/05 §6.2, rule 1).',
};

const basePatterns = [deepFeatureImport, climbingImport];

function restrictImports(...patterns) {
  return ['error', { patterns: [...basePatterns, ...patterns] }];
}

const networkMessage = 'Only src/core/http talks to the network (docs/05 §6.2, rule 1)';

export const architectureRules = [
  {
    files: ['src/**/*.{ts,tsx}'],
    rules: {
      'no-restricted-imports': restrictImports(),
      'no-restricted-globals': [
        'error',
        { name: 'fetch', message: networkMessage },
        { name: 'XMLHttpRequest', message: networkMessage },
      ],
      'no-restricted-properties': [
        'error',
        { object: 'window', property: 'fetch', message: networkMessage },
        { object: 'globalThis', property: 'fetch', message: networkMessage },
      ],
    },
  },
  {
    files: ['src/core/http/**'],
    rules: {
      'no-restricted-globals': 'off',
      'no-restricted-properties': 'off',
    },
  },
  {
    files: ['src/core/**'],
    rules: { 'no-restricted-imports': restrictImports(coreFramework, coreOutward) },
  },
  {
    files: ['src/shared/**'],
    rules: { 'no-restricted-imports': restrictImports(sharedOutward) },
  },
  {
    files: ['src/features/**'],
    rules: { 'no-restricted-imports': restrictImports(featuresToApp) },
  },
  {
    files: ['src/features/*/components/**'],
    rules: { 'no-restricted-imports': restrictImports(featuresToApp, componentsToApi) },
  },
];
