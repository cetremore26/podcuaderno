import { z } from 'zod';

// Same limit as the backend NotebookTitleRequest and the Title value object.
export const NOTEBOOK_TITLE_MAX_LENGTH = 150;

export const notebookTitleSchema = z.object({
  title: z
    .string()
    .trim()
    .min(1, 'Escribe un nombre.')
    .max(NOTEBOOK_TITLE_MAX_LENGTH, 'Usa máximo 150 caracteres.'),
});

export type NotebookTitleValues = z.output<typeof notebookTitleSchema>;
