import { fireEvent, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { ApiError } from '@/core/http/apiError';
import { renderWithProviders } from '@/test/renderWithProviders';
import { NotebooksPage } from './NotebooksPage';

const api = vi.hoisted(() => ({
  listNotebooks: vi.fn(),
  createNotebook: vi.fn(),
  renameNotebook: vi.fn(),
  deleteNotebook: vi.fn(),
}));
vi.mock('../api/notebooksApi', () => api);

const MINUTE = 60_000;

function notebook(id: string, title: string, minutesAgo: number) {
  return { id, title, updatedAt: new Date(Date.now() - minutesAgo * MINUTE).toISOString() };
}

const databases = notebook('n-1', 'Bases de datos', 5);
const calculus = notebook('n-2', 'Cálculo integral', 24 * 60);

function notFound(): ApiError {
  return new ApiError({ type: '/errors/not-found', title: 'Resource not found', status: 404 });
}

async function renderPage(notebooks: unknown[] = [databases, calculus]) {
  api.listNotebooks.mockResolvedValue(notebooks);
  renderWithProviders(<NotebooksPage />, { route: '/cuadernos' });
  expect(screen.getByRole('status')).toHaveTextContent('Cargando tus cuadernos…');
  await waitFor(() => expect(screen.queryByText('Cargando tus cuadernos…')).toBeNull());
}

describe('NotebooksPage', () => {
  afterEach(() => {
    vi.resetAllMocks();
  });

  it('invites to create the first notebook when there are none', async () => {
    await renderPage([]);

    expect(screen.getByRole('heading', { level: 1, name: 'Mis cuadernos' })).toBeInTheDocument();
    expect(screen.getByText('Todavía no tienes cuadernos.')).toBeInTheDocument();
    expect(screen.getByText('Crea uno para cada materia.')).toBeInTheDocument();
  });

  it('lists the notebooks in the order received with their relative date', async () => {
    await renderPage();

    const items = screen.getAllByRole('listitem');
    expect(items.map((item) => within(item).getByRole('heading').textContent)).toEqual([
      'Bases de datos',
      'Cálculo integral',
    ]);
    expect(within(items[0]).getByText('Modificado hace 5 minutos')).toBeInTheDocument();
    expect(within(items[1]).getByText('Modificado ayer')).toBeInTheDocument();
    expect(
      within(items[0]).getByRole('button', { name: 'Cambiar nombre de Bases de datos' }),
    ).toBeInTheDocument();
  });

  it('offers to retry when the list cannot be loaded', async () => {
    api.listNotebooks.mockRejectedValueOnce(new TypeError('Failed to fetch'));
    api.listNotebooks.mockResolvedValueOnce([databases]);
    renderWithProviders(<NotebooksPage />);

    expect(await screen.findByRole('alert')).toHaveTextContent('No pudimos cargar tus cuadernos.');
    await userEvent.click(screen.getByRole('button', { name: 'Intentar de nuevo' }));

    expect(await screen.findByRole('heading', { name: 'Bases de datos' })).toBeInTheDocument();
  });

  it('creates a notebook after validating the name, and closes the dialog', async () => {
    await renderPage([]);
    api.createNotebook.mockResolvedValue(databases);
    api.listNotebooks.mockResolvedValue([databases]);

    await userEvent.click(screen.getByRole('button', { name: 'Nuevo cuaderno' }));
    const dialog = screen.getByRole('dialog', { name: 'Nuevo cuaderno' });
    await userEvent.click(within(dialog).getByRole('button', { name: 'Crear' }));
    expect(await within(dialog).findByText('Escribe un nombre.')).toBeInTheDocument();
    expect(api.createNotebook).not.toHaveBeenCalled();

    await userEvent.type(within(dialog).getByLabelText('Nombre del cuaderno'), '  Bases de datos ');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Crear' }));

    expect(await screen.findByRole('heading', { name: 'Bases de datos' })).toBeInTheDocument();
    expect(api.createNotebook.mock.calls[0][0]).toBe('Bases de datos');
    expect(screen.queryByRole('dialog')).toBeNull();
  });

  it('keeps the dialog open with the reason when the backend rejects the name', async () => {
    await renderPage([]);
    api.createNotebook.mockRejectedValue(
      new ApiError({
        type: '/errors/business-rule',
        title: 'Business rule violated',
        status: 422,
        code: 'invalid-title',
      }),
    );

    await userEvent.click(screen.getByRole('button', { name: 'Nuevo cuaderno' }));
    const dialog = screen.getByRole('dialog', { name: 'Nuevo cuaderno' });
    await userEvent.type(within(dialog).getByLabelText('Nombre del cuaderno'), 'Física');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Crear' }));

    expect(await within(dialog).findByRole('alert')).toHaveTextContent(
      'Usa un nombre de 1 a 150 caracteres.',
    );
  });

  it('closes the dialog with Esc or "Cancelar"', async () => {
    await renderPage([]);

    await userEvent.click(screen.getByRole('button', { name: 'Nuevo cuaderno' }));
    fireEvent(screen.getByRole('dialog'), new Event('cancel', { cancelable: true }));
    expect(screen.queryByRole('dialog')).toBeNull();

    await userEvent.click(screen.getByRole('button', { name: 'Nuevo cuaderno' }));
    await userEvent.click(screen.getByRole('button', { name: 'Cancelar' }));
    expect(screen.queryByRole('dialog')).toBeNull();
  });

  it('renames a notebook starting from its current title', async () => {
    await renderPage();
    api.renameNotebook.mockResolvedValue({ ...databases, title: 'Bases de datos II' });

    await userEvent.click(screen.getByRole('button', { name: 'Cambiar nombre de Bases de datos' }));
    const dialog = screen.getByRole('dialog', { name: 'Cambiar nombre' });
    const field = within(dialog).getByLabelText('Nombre del cuaderno');
    expect(field).toHaveValue('Bases de datos');
    await userEvent.type(field, ' II');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Guardar' }));

    await waitFor(() => expect(screen.queryByRole('dialog')).toBeNull());
    expect(api.renameNotebook.mock.calls[0][0]).toEqual({ id: 'n-1', title: 'Bases de datos II' });
  });

  it('asks for confirmation before deleting, and "Cancelar" deletes nothing', async () => {
    await renderPage();
    api.deleteNotebook.mockResolvedValue(undefined);

    await userEvent.click(screen.getByRole('button', { name: 'Eliminar Bases de datos' }));
    let dialog = screen.getByRole('dialog', { name: '¿Eliminar «Bases de datos»?' });
    expect(dialog).toHaveTextContent('Se borrarán todos sus temas y conceptos.');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Cancelar' }));
    expect(api.deleteNotebook).not.toHaveBeenCalled();

    await userEvent.click(screen.getByRole('button', { name: 'Eliminar Bases de datos' }));
    dialog = screen.getByRole('dialog', { name: '¿Eliminar «Bases de datos»?' });
    await userEvent.click(within(dialog).getByRole('button', { name: 'Eliminar' }));

    await waitFor(() => expect(screen.queryByRole('dialog')).toBeNull());
    expect(api.deleteNotebook.mock.calls[0][0]).toBe('n-1');
  });

  it('says the notebook no longer exists when it was deleted elsewhere', async () => {
    await renderPage();
    api.deleteNotebook.mockRejectedValue(notFound());

    await userEvent.click(screen.getByRole('button', { name: 'Eliminar Cálculo integral' }));
    await userEvent.click(
      within(screen.getByRole('dialog')).getByRole('button', { name: 'Eliminar' }),
    );

    expect(await screen.findByRole('alert')).toHaveTextContent('Ese cuaderno ya no existe.');
    expect(screen.queryByRole('dialog')).toBeNull();
    await waitFor(() => expect(api.listNotebooks).toHaveBeenCalledTimes(2));
  });

  it('keeps the delete dialog open with the reason on other failures', async () => {
    await renderPage();
    api.deleteNotebook.mockRejectedValue(new TypeError('Failed to fetch'));

    await userEvent.click(screen.getByRole('button', { name: 'Eliminar Bases de datos' }));
    const dialog = screen.getByRole('dialog');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Eliminar' }));

    expect(await within(dialog).findByRole('alert')).toHaveTextContent('No hay conexión.');
  });
});
