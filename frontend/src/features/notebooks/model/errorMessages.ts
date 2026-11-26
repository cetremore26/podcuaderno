import { isProblem, ProblemType } from '@/core/http/problems';

export const NOTEBOOK_GONE_MESSAGE = 'Ese cuaderno ya no existe.';

export function isNotebookGone(error: unknown): boolean {
  return isProblem(error, ProblemType.notFound);
}

export function saveNotebookErrorMessage(error: unknown): string {
  if (error instanceof TypeError) {
    return 'No hay conexión. Revisa tu internet e inténtalo de nuevo.';
  }
  if (
    isProblem(error, ProblemType.businessRule, 'invalid-title') ||
    isProblem(error, ProblemType.validation)
  ) {
    return 'Usa un nombre de 1 a 150 caracteres.';
  }
  return 'No pudimos guardar el cuaderno. Inténtalo de nuevo en unos minutos.';
}

export function deleteNotebookErrorMessage(error: unknown): string {
  return error instanceof TypeError
    ? 'No hay conexión. Revisa tu internet e inténtalo de nuevo.'
    : 'No pudimos eliminar el cuaderno. Inténtalo de nuevo en unos minutos.';
}
