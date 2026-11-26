import { describe, expect, it } from 'vitest';
import { ApiError } from '@/core/http/apiError';
import {
  deleteNotebookErrorMessage,
  isNotebookGone,
  saveNotebookErrorMessage,
} from './errorMessages';

const offline = new TypeError('Failed to fetch');
const serverError = new ApiError({ type: '/errors/internal', title: 'Unexpected', status: 500 });

describe('notebook error messages', () => {
  it('recognizes a notebook that no longer exists', () => {
    expect(
      isNotebookGone(new ApiError({ type: '/errors/not-found', title: 'x', status: 404 })),
    ).toBe(true);
    expect(isNotebookGone(serverError)).toBe(false);
  });

  it('explains why a notebook could not be saved or deleted', () => {
    expect(saveNotebookErrorMessage(offline)).toMatch(/No hay conexión/);
    expect(
      saveNotebookErrorMessage(
        new ApiError({ type: '/errors/validation', title: 'x', status: 400 }),
      ),
    ).toBe('Usa un nombre de 1 a 150 caracteres.');
    expect(saveNotebookErrorMessage(serverError)).toMatch(/No pudimos guardar el cuaderno/);
    expect(deleteNotebookErrorMessage(offline)).toMatch(/No hay conexión/);
    expect(deleteNotebookErrorMessage(serverError)).toMatch(/No pudimos eliminar el cuaderno/);
  });
});
