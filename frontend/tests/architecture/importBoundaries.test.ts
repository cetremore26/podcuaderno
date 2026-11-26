// @vitest-environment node
import { ESLint } from 'eslint';
import { describe, expect, it } from 'vitest';

const eslint = new ESLint();

async function ruleIdsFor(code: string, filePath: string): Promise<(string | null)[]> {
  const [result] = await eslint.lintText(code, { filePath });
  return result.messages.map((message) => message.ruleId);
}

describe('import boundaries (docs/05 §6.2)', () => {
  it.each([
    ['core imports React', 'src/core/audio/x.ts', "import { useState } from 'react';"],
    [
      'core imports a feature',
      'src/core/audio/x.ts',
      "import { notebooks } from '@/features/notebooks';",
    ],
    [
      'a feature imports internals of another feature',
      'src/features/notebooks/hooks/x.ts',
      "import { Editor } from '@/features/editor/components/Editor';",
    ],
    [
      'a component imports the api layer',
      'src/features/notebooks/components/X.tsx',
      "import { notebooksApi } from '../api/notebooksApi';",
    ],
    [
      'an import climbs more than one directory',
      'src/features/notebooks/components/X.tsx',
      "import { useEditor } from '../../editor/hooks/useEditor';",
    ],
  ])('rejects: %s', async (_case, filePath, code) => {
    expect(await ruleIdsFor(`${code}\nexport const used = 1;\n`, filePath)).toContain(
      'no-restricted-imports',
    );
  });

  it.each([
    [
      'a feature imports another feature through its index',
      'src/features/notebooks/hooks/x.ts',
      "import { useEditor } from '@/features/editor';",
    ],
    [
      'the app imports a feature through its index',
      'src/app/App.tsx',
      "import { NotebooksPage } from '@/features/notebooks';",
    ],
  ])('allows: %s', async (_case, filePath, code) => {
    expect(await ruleIdsFor(`${code}\nexport const used = 1;\n`, filePath)).not.toContain(
      'no-restricted-imports',
    );
  });

  const fetchCall = "export const load = () => fetch('/api/v1/notebooks');\n";

  it('rejects fetch outside core/http', async () => {
    expect(await ruleIdsFor(fetchCall, 'src/features/notebooks/components/X.tsx')).toContain(
      'no-restricted-globals',
    );
  });

  it('allows fetch inside core/http', async () => {
    expect(await ruleIdsFor(fetchCall, 'src/core/http/x.ts')).not.toContain(
      'no-restricted-globals',
    );
  });
});
